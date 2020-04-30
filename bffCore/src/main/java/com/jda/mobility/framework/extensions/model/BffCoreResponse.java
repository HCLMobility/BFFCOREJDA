/**
 * 
 */
package com.jda.mobility.framework.extensions.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The class BffCoreResponse.java
 * @author puneet-m
 * HCL Technologies Ltd.
 */
@JsonInclude(Include.NON_NULL)
public class BffCoreResponse {
	
	/** The field responseId of type String */
	private String responseId;
	
	/** The field timestamp of type String */
	private String timestamp;
	
	/** The field code of type String */
	private int code;
	
	/** The field message of type String */
	private String message;
	
	/** The field details of type DetailResponse */
	@SuppressWarnings("rawtypes")
	private DetailResponse details;
	
	/** The field errors of type List<ErrorResponse> */
	private List<ErrorResponse> errors;
	@JsonIgnore
	/** The field httpStatus of type HttpStatus */
	private int httpStatusCode;

	/**
	 * @return the responseId of type String
	 */
	public String getResponseId() {
		return responseId;
	}

	/**
	 * @param responseId of type String
	 */
	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}

	/**
	 * @return the timestamp of type String
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp of type String
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the code of type int
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code of type int
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return the message of type String
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message of type String
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * @return the details of type DetailResponse
	 */
	@SuppressWarnings("rawtypes")
	public DetailResponse getDetails() {
		return details;
	}

	/**
	 * @param details of type DetailResponse
	 */
	@SuppressWarnings("rawtypes")
	public void setDetails(DetailResponse details) {
		this.details = details;
	}

	/**
	 * @return the errors of type List
	 */
	public List<ErrorResponse> getErrors() {
		return errors;
	}

	/**
	 * @param errors of type List
	 */
	public void setErrors(List<ErrorResponse> errors) {
		this.errors = errors;
	}

	/**
	 * @return the httpStatusCode of type int
	 */
	public int getHttpStatusCode() {
		return httpStatusCode;
	}

	/**
	 * @param httpStatusCode of type int
	 */
	public void setHttpStatusCode(int httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	
}