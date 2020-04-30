package com.jda.mobility.framework.extensions.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.entity.ApiMaster;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.model.ProdApiWrkMemRequest;
import com.jda.mobility.framework.extensions.model.ProductApiInvokeRequest;
import com.jda.mobility.framework.extensions.model.RequestParam;
import com.jda.mobility.framework.extensions.repository.ApiMasterRepository;
import com.jda.mobility.framework.extensions.service.OrchestrationService;

@Service
public class OrchestrationServiceImpl implements OrchestrationService {
	@Autowired
	public SessionDetails sessionDetails;
	@Autowired
	private ApiMasterRepository apiMasterRepository;
	private static final String REQUEST_PARAM = "/requestParam";
	private static final Logger LOGGER = LogManager.getLogger(OrchestrationServiceImpl.class);

	public ProdApiWrkMemRequest buildOrchestrationPreProcessor(JsonNode jsonNode, int currentLayer) {
		Map<String, ProductApiInvokeRequest> apiInput = null;
		ProdApiWrkMemRequest prodApiWrkMemRequest = null;
		
		try {
			apiInput = processInput(jsonNode, currentLayer);
			prodApiWrkMemRequest = new ProdApiWrkMemRequest();
			prodApiWrkMemRequest.setApiInput(apiInput);
		} catch (JsonProcessingException e) {
			LOGGER.log(Level.ERROR, "Json could not be mapped!");
		}

		return prodApiWrkMemRequest;
	}

	public byte[] getRuleContent(ApiRegistry registry, String orchestrationName) {
		ApiMaster apiOrchestrationMaster = apiMasterRepository
				.findByApiRegistryAndOrchestrationName(registry, orchestrationName).orElseThrow();
		byte[] source = null;
		if (apiOrchestrationMaster != null) {
			source = apiOrchestrationMaster.getRuleContent();
		}
		return source;
	}

	private Map<String, ProductApiInvokeRequest> processInput(JsonNode jsonNode, int currentLayer) throws JsonProcessingException {
		Map<String, ProductApiInvokeRequest> apiRequest = new HashMap<>();
		Map<String, List<RequestParam>> map = new HashMap<>();

		JsonNode bodyNode = jsonNode.at(REQUEST_PARAM);

		ObjectMapper objectMapper = new ObjectMapper();
		String requestParam = objectMapper.writeValueAsString(bodyNode);
		List<RequestParam> requestParamList = Arrays.asList(objectMapper.readValue(requestParam, RequestParam[].class));

		if (bodyNode.isArray()) {
			buildApiRequestMap(apiRequest, map, requestParamList, currentLayer);
		}
		return apiRequest;
	}

	/**
	 * @param apiRequest
	 * @param map
	 * @param jsonUtils
	 * @param bodyNode
	 */
	private void buildApiRequestMap(Map<String, ProductApiInvokeRequest> apiRequest, Map<String, List<RequestParam>> map,
			List<RequestParam> requestParamList, int currentLayer) {

		for (RequestParam requestParam : requestParamList) {

			if (requestParam.getType().equals("QUERY") || requestParam.getType().equals("PATH")) {
				buildQueryPathParams(map, requestParam);
			}

			else {
				String[] propertyNameSplit = requestParam.getParameter().getPropertyName().split("\\.");
				String apiName = propertyNameSplit[0];
				if (map.containsKey(apiName)) {
					map.get(apiName).add(requestParam);
				} else {
					List<RequestParam> apiParamList = new ArrayList<>();
					apiParamList.add(requestParam);

					map.put(apiName, apiParamList);
				}
			}
		}

		Iterator<String> it = map.keySet().iterator();

		while (it.hasNext()) {
			String api = it.next();
			apiRequest.put(api, createProductApiInvokeRequest(map, api, currentLayer));
		}
	}

	private void buildQueryPathParams(Map<String, List<RequestParam>> map, RequestParam requestParam) {
		String[] typeSplit = requestParam.getParameter().getPropertyName().split("~");
		String propertyName = typeSplit[1];

		if (map.containsKey(typeSplit[0])) {
			// modifying property name-removing the apiName
			requestParam.getParameter().setPropertyName(propertyName);
			map.get(typeSplit[0]).add(requestParam);
		} else {

			requestParam.getParameter().setPropertyName(propertyName);
			List<RequestParam> apiParamList = new ArrayList<>();
			apiParamList.add(requestParam);

			map.put(typeSplit[0], apiParamList);
		}
	}

	private ProductApiInvokeRequest createProductApiInvokeRequest(Map<String, List<RequestParam>> map, String apiName, int currentLayer) {
		ProductApiInvokeRequest productApiInvokeRequest = new ProductApiInvokeRequest();
		productApiInvokeRequest.setApiName(apiName);
		productApiInvokeRequest.setRequestParam(map.get(apiName));
		productApiInvokeRequest.setLayer(currentLayer);
		return productApiInvokeRequest;

	}

}
