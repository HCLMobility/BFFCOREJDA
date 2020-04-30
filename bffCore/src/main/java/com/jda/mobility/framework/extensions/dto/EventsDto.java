/**
 * 
 */
package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

/**
 * The class EventsDto.java
 * HCL Technologies Ltd.
 */
@Data @Builder(toBuilder = true)
public final class EventsDto implements Serializable {
	
	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = -9219839927730960612L;
	private UUID uid;	
	private String event;	
	private String action;	
	private UUID fieldId;	
	private UUID formId;
	
	
}