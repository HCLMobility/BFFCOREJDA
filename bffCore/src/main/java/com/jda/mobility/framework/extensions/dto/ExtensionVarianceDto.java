/**
 * 
 */
package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;
/**
 * The class ExtensionVarianceDto.java
 * HCL Technologies Ltd.
 */
@Data
public class ExtensionVarianceDto implements Serializable {

	private static final long serialVersionUID = 6379994082341627735L;
	
	private final String key;
	private final transient JsonNode leftValue;
	private final transient JsonNode rightValue;
	
}