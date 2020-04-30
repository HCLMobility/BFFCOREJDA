package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import com.jda.mobility.framework.extensions.dto.TranslationRequest;

public class Tab implements Serializable{
	
	
	private static final long serialVersionUID = -8915494719366325534L;

	private UUID linkedFormId;
	
	private String linkedFormName;
	
	private UUID tabId;
	
	private TranslationRequest tabName;
	
	private boolean defaultForm;
	

	public UUID getLinkedFormId() {
		return linkedFormId;
	}

	public void setLinkedFormId(UUID linkedFormId) {
		this.linkedFormId = linkedFormId;
	}

	public String getLinkedFormName() {
		return linkedFormName;
	}

	public void setLinkedFormName(String linkedFormName) {
		this.linkedFormName = linkedFormName;
	}

	public UUID getTabId() {
		return tabId;
	}

	public void setTabId(UUID tabId) {
		this.tabId = tabId;
	}

	public TranslationRequest getTabName() {
		return tabName;
	}

	public void setTabName(TranslationRequest tabName) {
		this.tabName = tabName;
	}

	

	public boolean isDefaultForm() {
		return defaultForm;
	}

	public void setDefaultForm(boolean defaultForm) {
		this.defaultForm = defaultForm;
	}

	@Override
	public int hashCode() {
		return Objects.hash(linkedFormId,linkedFormName,tabId,tabName,defaultForm);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tab other = (Tab) obj;
		return linkedFormId == other.linkedFormId && Objects.equals(linkedFormName, other.linkedFormName)
				&& Objects.equals(tabId, other.tabId) && Objects.equals(tabName, other.tabName)
				&& Objects.equals(defaultForm, other.defaultForm);
	}

}
