package com.jda.mobility.framework.extensions.service;

import java.util.List;

import org.springframework.retry.annotation.Retryable;

import com.jda.mobility.framework.extensions.dto.TranslationDto;
import com.jda.mobility.framework.extensions.dto.TranslationRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;


/**
 * Reference class for TranslationServiceImpl class
 *  
 * @author HCL Technologies
 */
@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface TranslationService {
	
	
	/**
	 * @param type
	 * @return BffCoreResponse
	 */
	BffCoreResponse getResourceBundles(String type);
	
	
	/**
	 * @param translationRequest
	 * @return BffCoreResponse
	 */
	BffCoreResponse createResourceBundle(TranslationRequest translationRequest);
	
	/**
	 * @param locale
	 * @return BffCoreResponse
	 */
	BffCoreResponse updateLocale(String locale);

	/**
	 * @param type
	 * @return List&lt;TranslationDto&gt;
	 */
	List<TranslationDto> getlocalizedResBundleEntries(String type);
}
