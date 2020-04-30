package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.databind.node.ObjectNode;



public class MenuAction implements Serializable{

	private static final long serialVersionUID = 2249566531255649892L;
	private String actionType;
	
	private transient ObjectNode properties;


	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public ObjectNode getProperties() {
		return properties;
	}

	public void setProperties(ObjectNode properties) {
		this.properties = properties;
	}

	@Override
	public int hashCode() {
		return Objects.hash(actionType,properties);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MenuAction other = (MenuAction) obj;
		return actionType == other.actionType && Objects.equals(properties, other.properties);
	}
	
}
