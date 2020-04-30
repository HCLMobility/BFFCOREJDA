package com.jda.mobility.framework.extensions.model;

import com.fasterxml.jackson.databind.JsonNode;

public class ParserObject {
	
	private String fieldName;
	
	private String type;
	
	private JsonNode value;
	
	private String parent;

	private String path;
	

	/**
	 * @param fieldName of type String
	 * @param value of type JsonNode
	 * @param type of type String
	 * @param parent of type String
	 * @param path of type String
	 */
	public ParserObject(String fieldName, JsonNode value,String type,String parent, String path) {
		super();
		this.fieldName = fieldName;
		this.value = value;
		this.type=type;
		this.parent=parent;
		this.setPath(path);
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public JsonNode getValue() {
		return value;
	}

	public void setValue(JsonNode value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

}
