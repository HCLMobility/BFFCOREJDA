/**
 * 
 */
package com.jda.mobility.framework.extensions.entity.projection;

import java.util.UUID;

import lombok.Data;

/**
 * @author HCL Technologies
 *
 */
@Data
public class CustomFormLiteDto {

	private UUID customComponentId;
	private String name;
	private String description;	
	private boolean visibility;
	private boolean disabled;
	/**
	 * @param customComponentId
	 * @param name
	 * @param description
	 * @param visibility
	 * @param disabled
	 */
	public CustomFormLiteDto(UUID customComponentId, String name, String description, boolean visibility,
			boolean disabled) {
		super();
		this.customComponentId = customComponentId;
		this.name = name;
		this.description = description;
		this.visibility = visibility;
		this.disabled = disabled;
	}
	
}
