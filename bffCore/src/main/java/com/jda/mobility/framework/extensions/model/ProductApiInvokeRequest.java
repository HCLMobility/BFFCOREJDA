
package com.jda.mobility.framework.extensions.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProductApiInvokeRequest {

	private UUID registry;
	private UUID registryApi;
	private String selectedItem;
	private String selectedParamType;
	private String selectedParam;
	private String selectedParamValue;
	private String selectedResponse;
	private String selectedResponseValue;
	private List<ResponseParamAndValue> responseParamAndValue = null;
	private String rawValue;
	private OnSuccess onSuccess;
	private OnFailure onFailure;
	private String regName;
	private String version;
	private String requestEndpoint;
	private String requestMethod;
	private List<RequestParam> requestParam = null;
	private String apiType;
	private String basePath;
	private int layer;
	private String apiName;
	private UUID formId;
	private String formName;
	
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<>();

	public UUID getRegistry() {
		return registry;
	}

	public void setRegistry(UUID registry) {
		this.registry = registry;
	}

	public UUID getRegistryApi() {
		return registryApi;
	}

	public void setRegistryApi(UUID registryApi) {
		this.registryApi = registryApi;
	}

	public String getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(String selectedItem) {
		this.selectedItem = selectedItem;
	}

	public String getSelectedParamType() {
		return selectedParamType;
	}

	public void setSelectedParamType(String selectedParamType) {
		this.selectedParamType = selectedParamType;
	}

	public String getSelectedParam() {
		return selectedParam;
	}

	public void setSelectedParam(String selectedParam) {
		this.selectedParam = selectedParam;
	}

	public String getSelectedParamValue() {
		return selectedParamValue;
	}

	public void setSelectedParamValue(String selectedParamValue) {
		this.selectedParamValue = selectedParamValue;
	}

	public String getSelectedResponse() {
		return selectedResponse;
	}

	public void setSelectedResponse(String selectedResponse) {
		this.selectedResponse = selectedResponse;
	}

	public String getSelectedResponseValue() {
		return selectedResponseValue;
	}

	public void setSelectedResponseValue(String selectedResponseValue) {
		this.selectedResponseValue = selectedResponseValue;
	}

	public List<ResponseParamAndValue> getResponseParamAndValue() {
		return responseParamAndValue;
	}

	public void setResponseParamAndValue(List<ResponseParamAndValue> responseParamAndValue) {
		this.responseParamAndValue = responseParamAndValue;
	}

	public String getRawValue() {
		return rawValue;
	}

	public void setRawValue(String rawValue) {
		this.rawValue = rawValue;
	}

	public OnSuccess getOnSuccess() {
		return onSuccess;
	}

	public void setOnSuccess(OnSuccess onSuccess) {
		this.onSuccess = onSuccess;
	}

	public OnFailure getOnFailure() {
		return onFailure;
	}

	public void setOnFailure(OnFailure onFailure) {
		this.onFailure = onFailure;
	}

	public String getRegName() {
		return regName;
	}

	public void setRegName(String regName) {
		this.regName = regName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRequestEndpoint() {
		return requestEndpoint;
	}

	public void setRequestEndpoint(String requestEndpoint) {
		this.requestEndpoint = requestEndpoint;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public List<RequestParam> getRequestParam() {
		return requestParam;
	}

	public void setRequestParam(List<RequestParam> requestParam) {
		this.requestParam = requestParam;
	}

	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public UUID getFormId() {
		return formId;
	}

	public void setFormId(UUID formId) {
		this.formId = formId;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}
}
