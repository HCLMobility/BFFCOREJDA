package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * The class ResourceValDto.java
 * @author  HCL Technologies Ltd.
 */
@Data
public final class TranslationDto implements Serializable{
	
	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 1175744115979363736L;

	private final String uId;
	private final String locale;
	private final String rbkey;
	private final String rbvalue;
	private final String type;
}
