package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ApiMasterRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	/** The field uid of type UUID */
	@JsonInclude(Include.NON_NULL)
	private UUID uid;

	/** The field name of type String */
	private String name;

	/** The field requestBody of type String */
	private String requestBody;

	/** The field requestEndpoint of type String */
	private String requestEndpoint;

	/** The field requestMethod of type String */
	private String requestMethod;

	/** The field requestPathparams of type String */
	private String requestPathparams;

	/** The field requestPreproc of type String */
	private String requestPreproc;

	/** The field requestQuery of type String */
	private String requestQuery;

	/** The field responsePosrproc of type String */
	private String responsePostproc;

	/** The field responseSchema of type String */
	private String responseSchema;

	/** The field version of type String */
	private String version;


	/**
	 * @return the uid of type UUID
	 */
	public UUID getUid() {
		return uid;
	}

	/**
	 * @param uid of type UUID
	 */
	public void setUid(UUID uid) {
		this.uid = uid;
	}

	/**
	 * @return the name of type String
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name of type String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the requestBody of type String
	 */
	public String getRequestBody() {
		return requestBody;
	}

	/**
	 * @param requestBody of type String
	 */
	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	/**
	 * @return the requestEndpoint of type String
	 */
	public String getRequestEndpoint() {
		return requestEndpoint;
	}

	/**
	 * @param requestEndpoint of type String
	 */
	public void setRequestEndpoint(String requestEndpoint) {
		this.requestEndpoint = requestEndpoint;
	}

	/**
	 * @return the requestMethod of type String
	 */
	public String getRequestMethod() {
		return requestMethod;
	}

	/**
	 * @param requestMethod of type String
	 */
	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	/**
	 * @return the requestPathparams of type String
	 */
	public String getRequestPathparams() {
		return requestPathparams;
	}

	/**
	 * @param requestPathparams of type String
	 */
	public void setRequestPathparams(String requestPathparams) {
		this.requestPathparams = requestPathparams;
	}

	/**
	 * @return the requestPreproc of type String
	 */
	public String getRequestPreproc() {
		return requestPreproc;
	}

	/**
	 * @param requestPreproc of type String
	 */
	public void setRequestPreproc(String requestPreproc) {
		this.requestPreproc = requestPreproc;
	}

	/**
	 * @return the requestQuery of type String
	 */
	public String getRequestQuery() {
		return requestQuery;
	}

	/**
	 * @param requestQuery of type String
	 */
	public void setRequestQuery(String requestQuery) {
		this.requestQuery = requestQuery;
	}

	/**
	 * @return the responsePostproc of type String
	 */
	public String getResponsePostproc() {
		return responsePostproc;
	}

	/**
	 * @param responsePostproc of type String
	 */
	public void setResponsePostproc(String responsePostproc) {
		this.responsePostproc = responsePostproc;
	}

	/**
	 * @return the responseSchema of type String
	 */
	public String getResponseSchema() {
		return responseSchema;
	}

	/**
	 * @param responseSchema of type String
	 */
	public void setResponseSchema(String responseSchema) {
		this.responseSchema = responseSchema;
	}

	/**
	 * @return the version of type String
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version of type String
	 */
	public void setVersion(String version) {
		this.version = version;
	}


	@Override
	public int hashCode() {
		return Objects.hash( name, requestBody, requestEndpoint, requestMethod, requestPathparams,
				requestPreproc, requestQuery, responsePostproc, responseSchema, uid, version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApiMasterRequest other = (ApiMasterRequest) obj;
		return  Objects.equals(name, other.name)
				&& Objects.equals(requestBody, other.requestBody)
				&& Objects.equals(requestEndpoint, other.requestEndpoint)
				&& Objects.equals(requestMethod, other.requestMethod)
				&& Objects.equals(requestPathparams, other.requestPathparams)
				&& Objects.equals(requestPreproc, other.requestPreproc)
				&& Objects.equals(requestQuery, other.requestQuery)
				&& Objects.equals(responsePostproc, other.responsePostproc)
				&& Objects.equals(responseSchema, other.responseSchema) && Objects.equals(uid, other.uid)
				&& Objects.equals(version, other.version);
	}

}