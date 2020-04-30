/**
 * 
 */
package com.jda.mobility.framework.extensions.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.retry.annotation.Retryable;

import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.CloneRequest;
import com.jda.mobility.framework.extensions.model.FlowRequest;
import com.jda.mobility.framework.extensions.model.UserPermissionRequest;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ActionType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.CloneType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DeleteType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DisableType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.FlowType;

/**
 * CRUD operation for flow
 */

@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface FlowService {

	/**
	 * @param model The Flow request object to create a new flow
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse createFlow(FlowRequest model);

	/**
	 * @param flowRo The flow request object to be modified
	 * @param actionType The action type - CHECK/SAVE/CONFIRM PUBLISH 
	 * @param identifier The disable identifier - CHECK/CONFIRM DISABLE
	 * @param permissionIds The permissions list associated to the flow
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse modifyFlow(FlowRequest flowRo, ActionType actionType, DisableType identifier,List<String> permissionIds);

	/**
	 * @param flowId The ID of the flow to fetch details
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse getFlowById(UUID flowId);

	/**
	 * @param userPermissionRequest The user permissions request to check
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse getDefaultFormForFlowId(UserPermissionRequest userPermissionRequest);

	/**
	 * @param identifier The identifier / filter - PUBLISHED, UNPUBLISHED, ALL
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse fetchFlows( FlowType identifier);

	/**
	 * @param flowId The ID of the flow to be deleted
	 * @param identifier The identifier - CHECK/CONFIRM DELETE
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse deleteFlowById(UUID flowId, DeleteType identifier);

	/**
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse fetchCount();

	/**
	 * @param flowName The name of the flow
	 * @param version The version of the flow
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse uniqueFlow(String flowName, long version);

	/**
	 * @param flowId The ID of the flow to be disabled
	 * @param identifier The identifier - CHECK/CONFIRM DISABLE
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse disableFlow(UUID flowId, DisableType identifier);

	/**
	 * @param flowId The ID of the flow to be published
	 * @param actionType The action type - CHECK/SAVE/CONFIRM PUBLISH
	 * @param permissionIds The permissions list associated to the flow
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse publishFlow(UUID flowId, ActionType actionType,List<String> permissionIds);
	/**
	 * @param flow The flow details to be published
	 * @param permissionIds The permissions list associated to the flow
	 * @throws IOException Throw IO exception if published JSON could not be generated
	 */
	void publishFormFlow(Flow flow,List<String> permissionIds) throws IOException;
	/**
	 * @param cloneRequest The clone request that contains the details of the flow to be cloned
	 * @param actionType The clone type - FLOW, FORM_IN_SAME_FLOW, FORM_IN_DIFF_FLOW
	 * @param identifier - The identifier to check version
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse cloneComponent(CloneRequest cloneRequest, CloneType actionType, String identifier);

	/**
	 * 
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse fetchFlowBasicList();
}
