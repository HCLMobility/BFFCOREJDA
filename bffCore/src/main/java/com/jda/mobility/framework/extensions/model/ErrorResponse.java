/**
 * 
 */
package com.jda.mobility.framework.extensions.model;

/**
 * The class ErrorResponse.java
 * @author puneet-m
 * HCL Technologies Ltd.
 */
public class ErrorResponse {
	
	/** The field errorCode of type String */
	private int errorCode;
	/** The field userMessage of type String */
	private String userMessage;	
	/**
	 * @return the errorCode of type int
	 */
	public int getErrorCode() {
		return errorCode;
	}
	/**
	 * @param errorCode of type int
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	/**
	 * @return the userMessage of type String
	 */
	public String getUserMessage() {
		return userMessage;
	}
	/**
	 * @param userMessage of type String
	 */
	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

}
