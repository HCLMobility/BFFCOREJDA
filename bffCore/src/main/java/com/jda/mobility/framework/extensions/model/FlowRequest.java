/**
 * 
 */
package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class FlowRequest implements Serializable{

	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 4043173809525238011L;
	/** The field flowId of type UUID */
	private UUID flowId;
	/** The field name of type String */
	private String name;
	/** The field description of type String */
	private String description;
	/** The field defaultFormId of type UUID */
	private UUID defaultFormId;
	/** The field disabled of type boolean */
	private boolean disabled;
	/** The field extDisabled of type boolean */
	private boolean extDisabled;
	
	private List<String> permissions;
	
	private long version;
	
	public List<String> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}
	/** The field tag of type String */
	private String tag;
	
	/**
	 * @return the flowId of type UUID
	 */
	public UUID getFlowId() {
		return flowId;
	}
	/**
	 * @return the name of type String
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the description of type String
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @return the defaultFormId of type UUID
	 */
	public UUID getDefaultFormId() {
		return defaultFormId;
	}
	/**
	 * @return the disabled of type boolean
	 */
	public boolean isDisabled() {
		return disabled;
	}
	/**
	 * @return the extDisabled of type boolean
	 */
	public boolean isExtDisabled() {
		return extDisabled;
	}
	
	/**
	 * @return the tag of type String
	 */
	public String getTag() {
		return tag;
	}
	/**
	 * @param flowId of type UUID
	 */
	public void setFlowId(UUID flowId) {
		this.flowId = flowId;
	}
	/**
	 * @param name of type String
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param description of type String
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @param defaultFormId of type UUID
	 */
	public void setDefaultFormId(UUID defaultFormId) {
		this.defaultFormId = defaultFormId;
	}
	/**
	 * @param disabled of type boolean
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	/**
	 * @param extDisabled of type boolean
	 */
	public void setExtDisabled(boolean extDisabled) {
		this.extDisabled = extDisabled;
	}
	
	/**
	 * @param tag of type String
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}
		
	public long getVersion() {
		return version;
	}
	public void setVersion(long version) {
		this.version = version;
	}
	@Override
	public int hashCode() {
		return Objects.hash(defaultFormId, description, disabled, extDisabled, flowId, name,
				tag,version);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FlowRequest other = (FlowRequest) obj;
		return Objects.equals(defaultFormId, other.defaultFormId)
				&& Objects.equals(description, other.description) && disabled == other.disabled
				&& extDisabled == other.extDisabled && Objects.equals(flowId, other.flowId)
				&& Objects.equals(name, other.name) 
				&& Objects.equals(tag, other.tag) && Objects.equals(version, other.version);
	}
	
}