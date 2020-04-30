package com.jda.mobility.framework.extensions.service;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.jda.mobility.framework.extensions.service.impl.migration.importer.ImportContext;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.multipart.MultipartFile;

@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface ImportService {

    Map<String, JsonNode> importFile(MultipartFile file,
                                     ImportContext context);
}
