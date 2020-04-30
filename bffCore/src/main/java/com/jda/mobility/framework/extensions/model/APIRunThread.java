package com.jda.mobility.framework.extensions.model;

import java.util.concurrent.Callable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.jda.mobility.framework.extensions.utils.ProductAPIServiceInvoker;

public class APIRunThread implements Callable<ResponseEntity<JsonNode>> {

	private ProductApiInvokeRequest rq;
	private String bearerToken;
	private String authCookie;
	private String layer;
	private String registryName;
	ProductAPIServiceInvoker invoker;
	private String productName;
	

	public APIRunThread(ProductApiInvokeRequest rq, String authCookie, ProductAPIServiceInvoker invoker,
			String bearerToken,String layer,String registryName, String productName) {
		this.rq = rq;
		this.authCookie = authCookie;
		this.bearerToken = bearerToken;
		this.invoker = invoker;
		this.layer=layer;
		this.registryName=registryName;
		this.productName = productName;
		
		
	}

	@Override
	public ResponseEntity<JsonNode> call() throws Exception {
		JsonNode jsonNode = invoker.invokeApi(rq, registryName, layer, authCookie, bearerToken, productName);
		ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(jsonNode, HttpStatus.OK);
		return responseEntity;
	}
}
