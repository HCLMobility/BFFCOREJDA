/**
 * 
 */
package com.jda.mobility.framework.extensions.model;

import java.util.List;
import java.util.UUID;

/**
 * The class FlowPermissionRequest.java
 * 
 * @author HCL Technologies Ltd.
 */
public class UserPermissionRequest {

	private UUID flowId;
	private UUID formId;
	private List<String> userPermissions;

	/**
	 * @return the flowId of type UUID
	 */
	public UUID getFlowId() {
		return flowId;
	}

	/**
	 * @param flowId of type UUID
	 */
	public void setFlowId(UUID flowId) {
		this.flowId = flowId;
	}

	/**
	 * @return the formId of type UUID
	 */
	public UUID getFormId() {
		return formId;
	}

	/**
	 * @param formId of type UUID
	 */
	public void setFormId(UUID formId) {
		this.formId = formId;
	}

	/**
	 * @return the flowPermissions of type List
	 */
	public List<String> getUserPermissions() {
		return userPermissions;
	}

	/**
	 * @param flowPermissions of type List
	 */
	public void setUserPermissions(List<String> flowPermissions) {
		this.userPermissions = flowPermissions;
	}

}
