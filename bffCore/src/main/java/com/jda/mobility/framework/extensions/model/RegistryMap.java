package com.jda.mobility.framework.extensions.model;

public class RegistryMap {

	private String registryName;
	
	private String layer;
	
	public RegistryMap() {
		super();
	}
	
	public RegistryMap(String registryName, String layer) {
		super();
		this.registryName = registryName;
		this.layer = layer;
	}	
	
	public String getRegistryName() {
		return registryName;
	}

	public void setRegistryName(String registryName) {
		this.registryName = registryName;
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}
	
	
}
