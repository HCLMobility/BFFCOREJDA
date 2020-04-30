package com.jda.mobility.framework.extensions.service;

import java.util.List;
import java.util.UUID;

import org.springframework.retry.annotation.Retryable;

import com.jda.mobility.framework.extensions.model.AppConfigRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.FormData;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ActionType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DefaultType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DeleteType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.FormStatus;

/**
 * CRUD operations for Form
 * 
 * @author HCL Technologies Ltd.
 */
@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface FormService {

	/**
	 * @param formData The form request object to be created
	 * @param actionType The action type - CHECK/SAVE/CONFIRM PUBLISH
	 * @param identifier The default identifier CHECK_DEFAULT/CONFIRM_DEFAULT
	 * @param permissionIds The permissions list associated to the parent flow
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse createForm(FormData formData, ActionType actionType, DefaultType identifier,List<String> permissionIds);

	/**
	 * @param actionType The action type - CHECK/SAVE/CONFIRM PUBLISH
	 * @param formData The form request object to be modified
	 * @param identifier The default identifier CHECK_DEFAULT/CONFIRM_DEFAULT
	 * @param permissionIds The permissions list associated to the parent flow
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse modifyForm(ActionType actionType, FormData formData, DefaultType identifier, List<String> permissionIds);

	/**
	 * @param formId The ID of the form to fetch details
	 * @param permissionIds The permissions list associated to the parent flow
	 * @param menuId The menu id for the form context menu item
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse getFormById(UUID formId,List<String> permissionIds, UUID menuId);

	/**
	 * @param formId The ID of the form to fetch details
	 * @param permissionIds The permissions list associated to the parent flow
	 * @param appConfigList The application configuration variable list
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse getForm(UUID formId, List<String> permissionIds,List<AppConfigRequest> appConfigList);

	/**
	 * @param formId The ID of the form to be deleted
	 * @param identifier The delete identifier CHECK_DELETE/CONFIRM_DELETE
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse deleteFormByID(UUID formId, DeleteType identifier);

	/**
	 * @param flowId The flowid for which form list needs to be fetched
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse fetchAllForms(UUID flowId);

	/**
	 * @param flowId The flowid for which orphan form list needs to be fetched
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse fetchOrphanForms(UUID flowId);

	/**
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse fetchUnpublishForms();

	/**
	 * @param formId The default form id to be created
	 * @param identifier The default identifier CHECK_DEFAULT/CONFIRM_DEFAULT
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse createDefaultForm(UUID formId, DefaultType identifier);

	/**
	 * @param customComponentId The custom control id used in forms
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse getFormDetails(UUID customComponentId);

	/**
	 * @param formId The form id of the form to be published
	 * @param actionType The action type - CHECK/CONFIRM/NO PUBLISH
	 * @param permissionIds The permissions list associated to the parent flow
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse publishForm(UUID formId, ActionType actionType,List<String> permissionIds);

	/**
	 * @param flowId The flowid for which form list needs to be fetched
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse fetchFormBasicList(UUID flowId);

	/**
	 * @param productConfigId The product config id for which unpublished orphan forms list needs to be fetched
	 * @param identifier The identifier - UNPUBLISH, ORPHAN, ALL
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse fetchUnpublishOrphanForms(UUID productConfigId, FormStatus identifier);
}