package com.jda.mobility.framework.extensions.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.service.ImportService;
import com.jda.mobility.framework.extensions.service.impl.migration.importer.FlowConflictResolutionStrategy;
import com.jda.mobility.framework.extensions.service.impl.migration.importer.ImportContext;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ImportAction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.jda.mobility.framework.extensions.service.impl.migration.importer.FlowConflictResolutionStrategy.RENAME_NEW;
import static com.jda.mobility.framework.extensions.util.ZipHelper.zipToStream;

/**
 * Exposes EndPoint to import FormFlow and other components.
 */
@RestController
@RequestMapping("/api/import/v1")
public class ImportController {

    private final ImportService importService;
    private final ObjectMapper mapper;

    public ImportController(ImportService importService) {
        this.importService = importService;
        this.mapper = new ObjectMapper()
                .configure(Feature.AUTO_CLOSE_TARGET, false);
    }

    @PostMapping(value = "/data/{importAction}")
    public void importFile(@RequestParam("files") MultipartFile file,
                           @PathVariable("importAction") ImportAction importAction,
                           @RequestParam(value = "flowConflictResolutionStrategy", required = false)
                           FlowConflictResolutionStrategy conflictStrategy,
                           HttpServletResponse response) throws IOException {
        ImportContext context = new ImportContext(
                ImportAction.VALIDATE.equals(importAction),
                StringUtils.defaultIfBlank(file.getOriginalFilename(), file.getName()),
                conflictStrategy == null ? RENAME_NEW : conflictStrategy);

        Map<String, JsonNode> output = importService.importFile(file, context);

        HttpStatus status = context.hasErrors() ? HttpStatus.CONFLICT : HttpStatus.OK;

        Map<String, JsonNode> results = new HashMap<>(output);

        results.put("import-results.json", mapper.valueToTree(context));

        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setStatus(status.value());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=import-results.zip");
        zipToStream(results, mapper, response.getOutputStream());
    }
}
