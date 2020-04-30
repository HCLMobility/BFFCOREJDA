package com.jda.mobility.framework.extensions.model;

import java.util.UUID;

import javax.validation.constraints.NotNull;

public class CloneRequest {
	@NotNull
	private UUID id;
	private UUID flowIdForClonedForm;
	@NotNull
	private String name;
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}	
	public UUID getFlowIdForClonedForm() {
		return flowIdForClonedForm;
	}
	public void setFlowIdForClonedForm(UUID flowIdForClonedForm) {
		this.flowIdForClonedForm = flowIdForClonedForm;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}