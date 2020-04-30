package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * The class AppConfigDto.java
 * HCL Technologies Ltd.
 */
public final class AppConfigRequest implements Serializable {
	
	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 4377306606820775778L;
	
	/** The field id of type String */
	private  UUID appConfigId;	
	/** The field name of type String */
	private  String configName;	
	/** The field name of type String */
	private  String configType;
	/** The field name of type String */
	private  String configValue;
	/** The field name of type String */
	private  String rawValue;
	/** The field name of type String */
	private  UUID flowId;
	/** The field name of type String */
	private  String description;
	/** The field id of type String */
	private  UUID appDetailConfigId;
	/** The field id of type String */
	private  UUID appConfigMasterId;
	
	
	public UUID getAppConfigId() {
		return appConfigId;
	}
	public void setAppConfigId(UUID appConfigId) {
		this.appConfigId = appConfigId;
	}
	public String getConfigName() {
		return configName;
	}
	public void setConfigName(String configName) {
		this.configName = configName;
	}
	public String getConfigType() {
		return configType;
	}
	public void setConfigType(String configType) {
		this.configType = configType;
	}
	public String getConfigValue() {
		return configValue;
	}
	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}
	public String getRawValue() {
		return rawValue;
	}
	public void setRawValue(String rawValue) {
		this.rawValue = rawValue;
	}
	public UUID getFlowId() {
		return flowId;
	}
	public void setFlowId(UUID flowId) {
		this.flowId = flowId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public UUID getAppDetailConfigId() {
		return appDetailConfigId;
	}
	public void setAppDetailConfigId(UUID appDetailConfigId) {
		this.appDetailConfigId = appDetailConfigId;
	}
	public UUID getAppConfigMasterId() {
		return appConfigMasterId;
	}
	public void setAppConfigMasterId(UUID appConfigMasterId) {
		this.appConfigMasterId = appConfigMasterId;
	}
	@Override
	public int hashCode() {
		return Objects.hash(appConfigId, appConfigMasterId, appDetailConfigId, configName, configType, configValue,
				description, flowId, rawValue);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AppConfigRequest other = (AppConfigRequest) obj;
		return Objects.equals(appConfigId, other.appConfigId)
				&& Objects.equals(appConfigMasterId, other.appConfigMasterId)
				&& Objects.equals(appDetailConfigId, other.appDetailConfigId)
				&& Objects.equals(configName, other.configName) && Objects.equals(configType, other.configType)
				&& Objects.equals(configValue, other.configValue) && Objects.equals(description, other.description)
				&& Objects.equals(flowId, other.flowId) && Objects.equals(rawValue, other.rawValue);
	}
	
}

