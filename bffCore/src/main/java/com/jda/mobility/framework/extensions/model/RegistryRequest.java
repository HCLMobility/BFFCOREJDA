package com.jda.mobility.framework.extensions.model;

import java.util.Date;

/**
 * @author V.Rama
 *
 */
public class RegistryRequest {

	/** The field id of type UUID */
	private String regId;
	/** The field name of type String */
	private String name;
	/** The field apiVersion of type String */
	private String apiVersion;
	/** The field helperClass of type String */
	private String helperClass;
	/** The field basePath of type String */
	private String basePath;
	/** The field port of type String */
	private String port;
	/** The field contextPath of type String */
	private String contextPath;
	/** The field apiType of type String */
	private String apiType;
	/** The field versionId of type String */
	private int versionId;
	/** The field createdBy of type String */
	private String createdBy;
	/** The field createdOn of type Date */
	private Date createdOn;
	/** The field modifiedBy of type String */
	private String modifiedBy;
	/** The field modifiedOn of type Date */
	private Date modifiedOn;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the regId of type String
	 */
	public String getRegId() {
		return regId;
	}

	/**
	 * @param regId of type String
	 */
	public void setRegId(String regId) {
		this.regId = regId;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the apiVersion
	 */
	public String getApiVersion() {
		return apiVersion;
	}

	/**
	 * @param apiVersion the apiVersion to set
	 */
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	/**
	 * @return the helperClass
	 */
	public String getHelperClass() {
		return helperClass;
	}

	/**
	 * @param helperClass the helperClass to set
	 */
	public void setHelperClass(String helperClass) {
		this.helperClass = helperClass;
	}

	/**
	 * @return the basePath
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * @param basePath the basePath to set
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @return the contextPath
	 */
	public String getContextPath() {
		return contextPath;
	}

	/**
	 * @param contextPath the contextPath to set
	 */
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	/**
	 * @return the apiType
	 */
	public String getApiType() {
		return apiType;
	}

	/**
	 * @param apiType the apiType to set
	 */
	public void setApiType(String apiType) {
		this.apiType = apiType;
	}

	/**
	 * @return the versionId
	 */
	public int getVersionId() {
		return versionId;
	}

	/**
	 * @param versionId the versionId to set
	 */
	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdOn
	 */
	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdOn the createdOn to set
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the modifiedOn
	 */
	public Date getModifiedOn() {
		return modifiedOn;
	}

	/**
	 * @param modifiedOn the modifiedOn to set
	 */
	public void setModifiedOn(Date modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

}
