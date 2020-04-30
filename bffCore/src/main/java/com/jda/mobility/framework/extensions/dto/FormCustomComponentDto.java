/**
 * 
 */
package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;

/**
 * The class FormCustomComponentDto.java
 * HCL Technologies Ltd.
 */
@Data
public final class FormCustomComponentDto implements Serializable {

	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 1105019055085578826L;
	/** The field formCusId of type String */
	private final UUID formCusId;
	/** The field formId of type String */
	private final UUID formId;
	/** The field customFormId of type String */
	private final UUID customFormId;
	
}