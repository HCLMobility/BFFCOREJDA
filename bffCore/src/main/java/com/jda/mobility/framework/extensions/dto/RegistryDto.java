package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * The class RegistryDto.java
 * @author HCL Technologies Ltd.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistryDto implements Serializable{

	private static final long serialVersionUID = -8129389360119885625L;
	
	private String regId;
	private String name;
	private String apiVersion;
	private String helperClass;
	private String basePath;
	private String port;
	private String contextPath;
	private String apiType;
	private int versionId;
	private String createdBy;
	private Date createdOn;
	private String modifiedBy;
	private Date modifiedOn;
	private int layer;
	private String layerName;


	/**
	 * @param builder
	 */
	private RegistryDto(RegistryBuilder builder) {
		super();
		this.regId = builder.regId;
		this.name = builder.name;
		this.apiVersion = builder.apiVersion;
		this.helperClass = builder.helperClass;
		this.basePath = builder.basePath;
		this.port = builder.port;
		this.contextPath = builder.contextPath;
		this.apiType = builder.apiType;
		this.versionId = builder.versionId;
		this.createdBy = builder.createdBy;
		this.createdOn = builder.createdOn;
		this.modifiedBy = builder.modifiedBy;
		this.modifiedOn = builder.modifiedOn;
		this.layer = builder.layer;
		this.layerName = builder.layerName;
	}
	public static class RegistryBuilder{
		private String regId;
		private String name;
		private String apiVersion;
		private String helperClass;
		private String basePath;
		private String port;
		private String contextPath;
		private String apiType;
		private int versionId;
		private String createdBy;
		private Date createdOn;
		private String modifiedBy;
		private Date modifiedOn;
		private int layer;
		private String layerName;
		
		public RegistryBuilder() {
			super();
		}
		public RegistryBuilder setRegId(String regId) {
			this.regId = regId;
			return this;
		}
		public RegistryBuilder setName(String name) {
			this.name = name;
			return this;
		}
		public RegistryBuilder setApiVersion(String apiVersion) {
			this.apiVersion = apiVersion;
			return this;
		}
		public RegistryBuilder setHelperClass(String helperClass) {
			this.helperClass = helperClass;
			return this;
		}
		public RegistryBuilder setBasePath(String basePath) {
			this.basePath = basePath;
			return this;
		}
		public RegistryBuilder setPort(String port) {
			this.port = port;
			return this;
		}
		public RegistryBuilder setContextPath(String contextPath) {
			this.contextPath = contextPath;
			return this;
		}
		public RegistryBuilder setApiType(String apiType) {
			this.apiType = apiType;
			return this;
		}
		public RegistryBuilder setVersionId(int versionId) {
			this.versionId = versionId;
			return this;
		}
		public RegistryBuilder setCreatedBy(String createdBy) {
			this.createdBy = createdBy;
			return this;
		}
		public RegistryBuilder setCreatedOn(Date createdOn) {
			this.createdOn = createdOn;
			return this;
		}
		public RegistryBuilder setModifiedBy(String modifiedBy) {
			this.modifiedBy = modifiedBy;
			return this;
		}
		public RegistryBuilder setModifiedOn(Date modifiedOn) {
			this.modifiedOn = modifiedOn;
			return this;
		}
		public RegistryBuilder setLayer(int layer) {
			this.layer = layer;
			return this;
		}
		
		public RegistryBuilder setLayerName(String layerName) {
			this.layerName = layerName;
			return this;
		}
		public RegistryDto build() {
			return new RegistryDto(this);
		}
	}
}