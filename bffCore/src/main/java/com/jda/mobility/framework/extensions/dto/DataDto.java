/**
 * 
 */
package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;

/**
 * The class DataDto.java
 * HCL Technologies Ltd.
 */
@Data
public final class DataDto implements Serializable {
	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 2230872949880881972L;

	private final UUID uid;
	private final String datalabel;
	private final String datavalue;
	private final UUID fieldId;
}
