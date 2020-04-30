package com.jda.mobility.framework.extensions.entity.projection;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;

@Data
public class AppConfigDetailDto implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 6384360371096410380L;
	private  UUID appConfigId;	
	private  String configName;	
	private  String configType;
	private  String rawValue;
	private  String configValue;
	private  UUID flowId;
	private  String userId;
	private  String description;
	private  String deviceName;
	
	
	public AppConfigDetailDto(UUID appConfigId)
	{
		super();
		this.appConfigId = appConfigId;
	}
	
	public AppConfigDetailDto(UUID appConfigId, String configName, String configType, String rawValue , String configValue, UUID flowId, String userId, String description , String deviceName)
	{
		super();
		this.appConfigId = appConfigId;
		this.configName = configName;
		this.configType = configType;
		this.rawValue = rawValue;
		this.configValue = configValue;
		this.flowId = flowId;
		this.userId = userId;
		this.description = description;
		this.deviceName = deviceName;
	}

}
