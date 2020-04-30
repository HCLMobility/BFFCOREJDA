/**
 * 
 */
package com.jda.mobility.framework.extensions.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The class DetailResponse.java
 * @author puneet-m
 * HCL Technologies Ltd.
 */
@JsonInclude(Include.NON_NULL)
public class DetailResponse<T> {
	
	/** The field detailMessage of type String */
	private String detailMessage;
	
	/** The field data of type Collection<T> */
	private T data;

	/**
	 * @return the detailMessage of type String
	 */
	public String getDetailMessage() {
		return detailMessage;
	}

	/**
	 * @param detailMessage of type String
	 */
	public void setDetailMessage(String detailMessage) {
		this.detailMessage = detailMessage;
	}

	/**
	 * @return the data of type T
	 */
	public T getData() {
		return data;
	}

	/**
	 * @param data of type T
	 */
	public void setData(T data) {
		this.data = data;
	}

}