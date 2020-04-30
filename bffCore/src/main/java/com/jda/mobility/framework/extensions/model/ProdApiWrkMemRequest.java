package com.jda.mobility.framework.extensions.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.jda.mobility.framework.extensions.utils.JsonNodeModifier;
import com.jda.mobility.framework.extensions.utils.ProductAPIServiceInvoker;

public class ProdApiWrkMemRequest {

	private String prodAuthCookie;
	private String bearerToken;
	private String tenant;
	private Map<String, JsonNode> apiInputBody;
	private JsonNode apiRequest;
	private JsonNode apiResponse;
	private boolean responseError = false;
	private Map<String,RegistryMap> registryLayerMap = new HashMap<>();
	private JsonNodeModifier jsonNodeModifier;
	private List<String> apiList;

	private String requestMethod;
	
	private Map<String, ProductApiInvokeRequest> apiInput ;
	
	private Map<String, JsonNode> apiResponseMap ;

	private List<ResponseEntity<JsonNode>> apiResponseList;

	private String userId;

	private ProductAPIServiceInvoker productAPIServiceInvoker;
	
	private String layer;

	private String outputParam1;

	private String outputParam2;

	private String outputParam3;

	private double outputDoubleParam1;
	private double outputDoubleParam2;
	private double outputDoubleParam3;
	private double outputDoubleParam4;

	private int outputIntParam1;
	private int outputIntParam2;
	private int outputIntParam3;
	private int outputIntParam4;

	public String getProdAuthCookie() {
		return prodAuthCookie;
	}

	public void setProdAuthCookie(String prodAuthCookie) {
		this.prodAuthCookie = prodAuthCookie;
	}

	public String getBearerToken() {
		return bearerToken;
	}

	public void setBearerToken(String bearerToken) {
		this.bearerToken = bearerToken;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	public JsonNode getApiResponse() {
		return apiResponse;
	}

	public void setApiResponse(JsonNode apiResponse) {
		this.apiResponse = apiResponse;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOutputParam1() {
		return outputParam1;
	}

	public void setOutputParam1(String outputParam1) {
		this.outputParam1 = outputParam1;
	}

	public String getOutputParam2() {
		return outputParam2;
	}

	public void setOutputParam2(String outputParam2) {
		this.outputParam2 = outputParam2;
	}

	public String getOutputParam3() {
		return outputParam3;
	}

	public void setOutputParam3(String outputParam3) {
		this.outputParam3 = outputParam3;
	}

	public ProductAPIServiceInvoker getProductAPIServiceInvoker() {
		return productAPIServiceInvoker;
	}

	public void setProductAPIServiceInvoker(ProductAPIServiceInvoker productAPIServiceInvoker) {
		this.productAPIServiceInvoker = productAPIServiceInvoker;
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public double getOutputDoubleParam1() {
		return outputDoubleParam1;
	}

	public void setOutputDoubleParam1(double outputDoubleParam1) {
		this.outputDoubleParam1 = outputDoubleParam1;
	}

	public double getOutputDoubleParam2() {
		return outputDoubleParam2;
	}

	public void setOutputDoubleParam2(double outputDoubleParam2) {
		this.outputDoubleParam2 = outputDoubleParam2;
	}

	public double getOutputDoubleParam3() {
		return outputDoubleParam3;
	}

	public void setOutputDoubleParam3(double outputDoubleParam3) {
		this.outputDoubleParam3 = outputDoubleParam3;
	}

	public double getOutputDoubleParam4() {
		return outputDoubleParam4;
	}

	public void setOutputDoubleParam4(double outputDoubleParam4) {
		this.outputDoubleParam4 = outputDoubleParam4;
	}

	public int getOutputIntParam1() {
		return outputIntParam1;
	}

	public void setOutputIntParam1(int outputIntParam1) {
		this.outputIntParam1 = outputIntParam1;
	}

	public int getOutputIntParam2() {
		return outputIntParam2;
	}

	public void setOutputIntParam2(int outputIntParam2) {
		this.outputIntParam2 = outputIntParam2;
	}

	public int getOutputIntParam3() {
		return outputIntParam3;
	}

	public void setOutputIntParam3(int outputIntParam3) {
		this.outputIntParam3 = outputIntParam3;
	}

	public int getOutputIntParam4() {
		return outputIntParam4;
	}

	public void setOutputIntParam4(int outputIntParam4) {
		this.outputIntParam4 = outputIntParam4;
	}

	public List<ResponseEntity<JsonNode>> getApiResponseList() {
		return apiResponseList;
	}

	public void setApiResponseList(List<ResponseEntity<JsonNode>> apiResponseList) {
		this.apiResponseList = apiResponseList;
	}

	public Map<String, JsonNode> getApiInputBody() {
		return apiInputBody;
	}

	public void setApiInputBody(Map<String, JsonNode> apiInputBody) {
		
		this.apiInputBody = apiInputBody;
	}

	public Map<String, ProductApiInvokeRequest> getApiInput() {
		return apiInput;
	}

	public void setApiInput(Map<String, ProductApiInvokeRequest> apiInput) {
		this.apiInput = apiInput;
		
	}

	/**
	 * @return the jsonNodeModifier
	 */
	public JsonNodeModifier getJsonNodeModifier() {
		return jsonNodeModifier;
	}

	/**
	 * @param jsonNodeModifier the jsonNodeModifier to set
	 */
	public void setJsonNodeModifier(JsonNodeModifier jsonNodeModifier) {
		this.jsonNodeModifier = jsonNodeModifier;
	}

	/**
	 * @return the apiRequest
	 */
	public JsonNode getApiRequest() {
		return apiRequest;
	}

	/**
	 * @param apiRequest the apiRequest to set
	 */
	public void setApiRequest(JsonNode apiRequest) {
		this.apiRequest = apiRequest;
	}

	public Map<String, JsonNode> getApiResponseMap() {
		return apiResponseMap;
	}

	public void setApiResponseMap(Map<String, JsonNode> apiResponseMap) {
		this.apiResponseMap = apiResponseMap;
	}

	public boolean isResponseError() {
		return responseError;
	}

	public void setResponseError(boolean responseError) {
		this.responseError = responseError;
	}
	
	
	public Map<String, RegistryMap> getRegistryLayerMap() {
		return registryLayerMap;
	}

	public void setRegistryLayerMap(Map<String, RegistryMap> registryLayerMap) {
		this.registryLayerMap = registryLayerMap;
	}

	public List<String> getApiList() {
		return apiList;
	}

	public void setApiList(List<String> apiList) {
		this.apiList = apiList;
	}
}
