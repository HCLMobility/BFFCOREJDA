package com.jda.mobility.framework.extensions.dto;

import java.util.UUID;

import lombok.Data;

/**
 * The class FormDependantDto.java
 * HCL Technologies Ltd.
 */
@Data
public class FormDependantDto {
	
	/** The field uid of type UUID */
	private UUID uid;
	
	/** The field form of type FormDto */
	private UUID formUid;
}
