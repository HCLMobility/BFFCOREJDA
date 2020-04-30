package com.jda.mobility.framework.extensions.service;

import org.springframework.retry.annotation.Retryable;

import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.SearchRequest;

/**
 * @author V.Rama This Class will provide the functionality of wild card search
 *         for resource bundle
 * 
 */
@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface AutoCompleteService {

	BffCoreResponse search(SearchRequest searchRequest, String authCookie, String bearerToken);

}
