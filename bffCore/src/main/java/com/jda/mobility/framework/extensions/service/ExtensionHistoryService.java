/**
 * 
 */
package com.jda.mobility.framework.extensions.service;

import java.util.UUID;

import org.springframework.retry.annotation.Retryable;

import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ExtensionType;

/**
 * The service to retrieve extensionHistory.
 *
 */
@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface ExtensionHistoryService {

	/**
	 * fetch extension history for extendedObjectId and type of extension applied.
	 * parentObjectId may or may not be present. If parentObjectId is not present,
	 * retrieve the parentObjectId associated with extended object and then extract
	 * difference.
	 * 
	 * @param extendedObjectId
	 * @param parentObjectId
	 * @param extensionType
	 */
	BffCoreResponse fetchExtensionHistory(UUID extendedObjectId, UUID parentObjectId, ExtensionType extensionType);

}
