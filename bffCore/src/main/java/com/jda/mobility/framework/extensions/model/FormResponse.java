package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.UUID;

public class FormResponse implements Serializable {

	private static final long serialVersionUID = 3749764122261403799L;
	private UUID formId;
	private String name;
	
	public UUID getFormId() {
		return formId;
	}
	public void setFormId(UUID formId) {
		this.formId = formId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
