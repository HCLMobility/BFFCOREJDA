/**
 * 
 */
package com.jda.mobility.framework.extensions.model;

import javax.validation.constraints.NotNull;

/**
 * @author HCL Technologies Limited
 * Model object for api orchestration controller
 *
 */
public class OrchRequest {

	@NotNull
	private String status;
	
	private String code;
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

}
