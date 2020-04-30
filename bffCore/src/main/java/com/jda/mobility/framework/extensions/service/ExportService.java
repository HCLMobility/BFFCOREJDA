package com.jda.mobility.framework.extensions.service;

import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.retry.annotation.Retryable;

import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ExportType;

@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface ExportService {

    Map<String, JsonNode> exportData(UUID id, ExportType[] actionType, String userId);
}
