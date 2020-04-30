package com.jda.mobility.framework.extensions.controller;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.security.UserPrincipal;
import com.jda.mobility.framework.extensions.service.ExportService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ExportType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.jda.mobility.framework.extensions.util.ZipHelper.zipToStream;

/**
 * Exposes EndPoint to export FormFlow and system files components.
 *
 */
@RestController
@RequestMapping("/api/export/v1")
public class ExportController {

    private final ExportService exportService;
    private final ObjectMapper mapper;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
        this.mapper = new ObjectMapper()
                .configure(Feature.AUTO_CLOSE_TARGET, false);
    }

    /**Export the form flow or selected system files as ZIP.
     * @param exportType
     * @param id
     * @return
     */
    @GetMapping(value = "/data", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void exportData(@RequestParam ExportType[] exportType,
                           @Valid @RequestParam(required = false) UUID id,
                           @Value("#{request.userPrincipal.principal}") UserPrincipal user,
                           HttpServletResponse response) throws IOException {
        Map<String, JsonNode> exports = exportService.exportData(id, exportType, user.getUserId());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=mab-export.zip");
        response.setStatus(HttpStatus.OK.value());
        zipToStream(exports, mapper, response.getOutputStream());
    }

}
