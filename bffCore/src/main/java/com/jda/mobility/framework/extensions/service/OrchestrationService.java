package com.jda.mobility.framework.extensions.service;

import org.springframework.retry.annotation.Retryable;

import com.fasterxml.jackson.databind.JsonNode;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.model.ProdApiWrkMemRequest;
@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface OrchestrationService {
	
	byte[] getRuleContent(ApiRegistry registry, String orchestrationName);
	
	ProdApiWrkMemRequest buildOrchestrationPreProcessor(JsonNode jsonNode, int currentLayer);
}
