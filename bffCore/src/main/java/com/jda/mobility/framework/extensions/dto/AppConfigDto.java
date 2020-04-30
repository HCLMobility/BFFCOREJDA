/**
 * 
 */
package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

/**
 * The class AppConfigDto.java
 * HCL Technologies Ltd.
 */
@Data @Builder(toBuilder = true)
public final class AppConfigDto implements Serializable {
	
	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 4377306606820775778L;
	
	/** The field id of type String */
	private final UUID appConfigId;	
	/** The field name of type String */
	private final String configName;	
	/** The field name of type String */
	private final String configType;
	/** The field name of type String */
	private final String rawValue;
	/** The field name of type String */
	private final String configValue;
	/** The field name of type String */
	private final UUID flowId;
	/** The field name of type String */
	private final String userId;
	/** The field name of type String */
	private final String description;
	/** The field id of type String */
	private final String deviceName;
	
}
