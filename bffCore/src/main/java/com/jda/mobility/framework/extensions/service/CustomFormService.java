/**
 * 
 */
package com.jda.mobility.framework.extensions.service;

import java.util.UUID;

import org.springframework.retry.annotation.Retryable;

import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.CustomFormData;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.CustomFormFilterMode;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DeleteType;

/**
 * Perform CRUD operation in CustomFormComponentMaster table
 */
@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface CustomFormService {

	/**
	 * @param customFormData The input object to create a new custom control
	 * @return BffCoreResponse The success/failure response Object
	 */
	BffCoreResponse createCustomComponent(CustomFormData customFormData);

	/**
	 * @param customFormData The input object to modify an existing custom control
	 * @return BffCoreResponse The success/failure response Object
	 */
	BffCoreResponse modifyCustomComponent(CustomFormData customFormData);

	/**
	 * @param customComponentId The ID of the custom control to fetch the details
	 * @return BffCoreResponse The success/failure response Object
	 */
	BffCoreResponse getCustomComponentById(UUID customComponentId);

	/**
	 * @param identifier The custom control filter mode - BASIC/ALL
	 * @param pageNo The start page no of the paginated results
	 * @param pageSize The page size of the paginated results
	 * @return BffCoreResponse The success/failure response Object
	 */
	BffCoreResponse fetchCustomCompList(CustomFormFilterMode identifier, Integer pageNo, Integer pageSize);

	/**
	 * @param customComponentId The ID of the custom control to be deleted
	 * @param identifier The custom control delete mode - CHECK_DELETE/CONFIRM_DELETE
	 * @return BffCoreResponse The success/failure response Object
	 */
	BffCoreResponse deleteCustomComponentById(UUID customComponentId, DeleteType identifier);
}
