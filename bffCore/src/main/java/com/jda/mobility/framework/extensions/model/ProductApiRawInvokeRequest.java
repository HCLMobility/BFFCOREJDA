package com.jda.mobility.framework.extensions.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductApiRawInvokeRequest {
	
	private String apiUri;
	private String apiPayload;
	
	public String getApiUri() {
		return apiUri;
	}
	public void setApiUri(String apiUri) {
		this.apiUri = apiUri;
	}
	public String getApiPayload() {
		return apiPayload;
	}
	public void setApiPayload(String apiPayload) {
		this.apiPayload = apiPayload;
	}

	
}
