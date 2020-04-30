/**
 * 
 */
package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.UUID;

import com.jda.mobility.framework.extensions.model.FormData;

import lombok.Data;

/**
 * The class FlowDefaultFormDto.java
 * @author HCL Technologies Ltd.
 */
@Data
public class FlowDefaultFormDto implements Serializable {
	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 7910509755744477427L;

	private UUID uid;

	private String name;

	private long version;
	
	private boolean tabbedForm;	
	
	private boolean modalForm;
	
	private FormData formData;
}
