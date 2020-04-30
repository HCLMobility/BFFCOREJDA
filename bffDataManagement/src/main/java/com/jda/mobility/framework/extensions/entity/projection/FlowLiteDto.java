package com.jda.mobility.framework.extensions.entity.projection;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;

/**
 * The class FlowLite.java
 * @author HCL Technologies Ltd.
 */
@Data
public class FlowLiteDto implements Serializable {

	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 4979234450433458186L;

	private UUID flowId;

	private String name;

	private long version;
	
	private boolean tabbedForm;	
	
	private boolean modalForm;
	
	private UUID defaultFormId;

	public FlowLiteDto(UUID uid, String name, long version,boolean tabbedForm, boolean modalForm, UUID defaultFormId) {
		super();
		this.flowId = uid;
		this.name = name;
		this.version = version;
		this.tabbedForm = tabbedForm;
		this.modalForm= modalForm;
		this.defaultFormId = defaultFormId;
	}
}