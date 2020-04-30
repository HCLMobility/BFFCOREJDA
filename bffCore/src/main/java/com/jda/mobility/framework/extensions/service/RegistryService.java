/**
 * 
 */
package com.jda.mobility.framework.extensions.service;

import java.util.List;
import java.util.UUID;

import org.springframework.retry.annotation.Retryable;

import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ApiRegistryType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.LayerMode;

/**
 * CRUD operations for Registry
 *
 */
@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface RegistryService {

	/**
	 * @return BffCoreResponse
	 */
	BffCoreResponse fetchAllRegistries(LayerMode mode);
	
	/**
	 * @return BffCoreResponse
	 */
	BffCoreResponse fetchRegistries(List<ApiRegistryType> type, LayerMode mode);	

	/**
	 * @param registryId
	 * @return BffCoreResponse
	 */
	BffCoreResponse fetchRegistryById(UUID registryId);

	/**
	 * @param registeryId
	 * @return BffCoreResponse
	 */
	BffCoreResponse fetchApiByRegistryId(UUID registeryId);

	/**
	 * @return BffCoreResponse
	 */
	
	BffCoreResponse fetchAllApis();

	/**
	 * @return BffCoreResponse
	 */
	BffCoreResponse fetchApiById(UUID registeryId);
}
