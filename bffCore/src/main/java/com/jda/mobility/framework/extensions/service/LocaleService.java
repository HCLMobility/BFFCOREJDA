package com.jda.mobility.framework.extensions.service;

import org.springframework.retry.annotation.Retryable;

import com.jda.mobility.framework.extensions.model.BffCoreResponse;

@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface LocaleService {
	

	BffCoreResponse getLocaleList();

}
