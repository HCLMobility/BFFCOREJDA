package com.jda.mobility.framework.extensions.utils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.jda.mobility.framework.extensions.config.ProductApiSettings;
import com.jda.mobility.framework.extensions.entity.ApiMaster;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.model.APIRunThread;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.Parameter;
import com.jda.mobility.framework.extensions.model.ProductApiInvokeRequest;
import com.jda.mobility.framework.extensions.model.RegistryMap;
import com.jda.mobility.framework.extensions.model.RequestParam;
import com.jda.mobility.framework.extensions.repository.ApiMasterRepository;
import com.jda.mobility.framework.extensions.repository.ApiRegistryRepository;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ApiRegistryType;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/** Class help to invoke parallel and serial Orchestration
 * @author HCL
 *
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ProductAPIServiceInvoker {

	private static final Logger LOGGER = LogManager.getLogger(ProductAPIServiceInvoker.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	JsonNodeModifier jsonNodeModifier;

	@Autowired
	private ApiRegistryRepository apiRegistryRepository;

	@Autowired
	private ApiMasterRepository apiMasterRepository;

	@Autowired
	private ProductApiSettings productApis;
	
	private static final String COOKIE_HEADER_NAME = "Cookie";

	/**Prepare ProductApiInvokeRequest object
	 * @param apiName API name to be invoked
	 * @return ProductApiInvokeRequest Request object for invoking orcherstation
	 */
	public ProductApiInvokeRequest buildRequest(String apiName) {
		ProductApiInvokeRequest productApiInvokeRequest = new ProductApiInvokeRequest();
		productApiInvokeRequest.setApiName(apiName);
		productApiInvokeRequest.setRequestParam(new ArrayList<RequestParam>());
		return productApiInvokeRequest;
	}

	public JsonNode invokeApi(URI uri, HttpMethod method,
							  Object body, String bearerToken, String authCookie) {
		HttpHeaders headers = new HttpHeaders();
		if (bearerToken != null) {
			headers.add(HttpHeaders.AUTHORIZATION, BffUtils.buildValidHeader(bearerToken));
		} else {
			headers.add(COOKIE_HEADER_NAME, BffUtils.buildValidHeader(authCookie));
		}
		headers.setContentType(MediaType.APPLICATION_JSON);

		return restTemplate.exchange(uri, method, new HttpEntity<>(body, headers), JsonNode.class).getBody();
	}

	/**Add query params , Path variables , request body to the request object
	 *
	 * @param req - Request object
	 * @param propertyName - Name of the property
	 * @param value - Value of the property
	 * @param type - Type of the property
	 * @param propertyType - Format of the property
	 * @return ProductApiInvokeRequest Request Object for invoking API
	 */
	public ProductApiInvokeRequest addParametersToInput(ProductApiInvokeRequest req, String propertyName, String value,
			String type, String propertyType)

	{
		List<RequestParam> requestParamList = req.getRequestParam();
		for (RequestParam reqParam : requestParamList)

		{
			if (reqParam.getParameter().getPropertyName().equals(propertyName)
					&& reqParam.getType().equalsIgnoreCase(type)) {
				reqParam.setValue(value);
				req.setRequestParam(requestParamList);
				return req;
			}
		}
		RequestParam requestParam = new RequestParam();
		requestParam.setValue(value);
		requestParam.setType(type);
		Parameter parameter = new Parameter();
		parameter.setPropertyType(propertyType);
		parameter.setPropertyName(propertyName);
		requestParam.setParameter(parameter);
		req.getRequestParam().add(requestParam);

		return req;
	}

	/**Method helps to invoke API with registry name and layer
	 *
	 * @param req -Request object
	 * @param regName - Name of the registry
	 * @param layer - Name of the layer
	 * @param authCookie - Token for authentication
	 * @param bearerToken - Token for authorization
	 * @param productName - Name of the product
	 * @return
	 */
	public JsonNode invokeApi(ProductApiInvokeRequest req, String regName, String layer, String authCookie,
			String bearerToken, String productName) {
		JsonNode jsonNode = null;
		Properties bodyParameterList = new Properties();
		BffCoreResponse bffCoreResponse = null;
		List<BffResponseCode> userCodeList = null;
		String payload = null;
		ApiMaster apiMaster;
		try {
			Optional<ApiRegistry> optRegistry = apiRegistryRepository
					.findByApiTypeAndNameAndRoleMaster_name(ApiRegistryType.INTERNAL.getType(), regName, layer);
			if (!optRegistry.isPresent()) {
				LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, req.getRegName());
				userCodeList = new ArrayList<>();
				userCodeList.add(BffResponseCode.ERR_APIMASTER_SERVICE_API_FETCH_REGISTRYID_DBEXCEPTION);
				userCodeList.add(BffResponseCode.ERR_APIMASTER_SERVICE_USER_FETCH_REGISTRYID_DBEXCEPTION);
				bffCoreResponse = new BffCoreResponse();
				bffCoreResponse.setMessage("Registry does not exist!");
				bffCoreResponse.setTimestamp(new Date().toString());
				bffCoreResponse.setHttpStatusCode(HttpStatus.BAD_REQUEST.value());
				return new ObjectMapper().valueToTree(bffCoreResponse);
			}

			ApiRegistry registry = optRegistry.get();
			
			if (registry.getRoleMaster().getLevel() > req.getLayer()) {
				LOGGER.log(Level.ERROR, BffAdminConstantsUtils.EXP_MSG, registry.getName());
				userCodeList = new ArrayList<>();
				userCodeList.add(BffResponseCode.ERR_ORCHESTRATION_API_BADLAYER_EXCEPTION);
				userCodeList.add(BffResponseCode.ERR_ORCHESTRATION_USER_API_BADLAYER_EXCEPTION);
				bffCoreResponse = new BffCoreResponse();
				bffCoreResponse.setMessage("Invalid layer for API execution. It belongs to a layer which is higher than the current orchestration's layer!");
				bffCoreResponse.setTimestamp(new Date().toString());
				bffCoreResponse.setHttpStatusCode(HttpStatus.BAD_REQUEST.value());
				return new ObjectMapper().valueToTree(bffCoreResponse);
			}
			
			Optional<ApiMaster> optApiMaster = apiMasterRepository.findByApiRegistryAndName(registry, req.getApiName());
			if (!optApiMaster.isPresent()) {
				LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, req.getApiName());
				userCodeList = new ArrayList<>();
				userCodeList.add(BffResponseCode.ERR_APIMASTER_SERVICE_API_FETCH_REGISTRYID_DBEXCEPTION);
				userCodeList.add(BffResponseCode.ERR_APIMASTER_SERVICE_USER_FETCH_REGISTRYID_DBEXCEPTION);
				bffCoreResponse = new BffCoreResponse();
				bffCoreResponse.setMessage("API does not exist!");
				bffCoreResponse.setTimestamp(new Date().toString());
				bffCoreResponse.setHttpStatusCode(HttpStatus.BAD_REQUEST.value());
				return new ObjectMapper().valueToTree(bffCoreResponse);
			}
			apiMaster = optApiMaster.get();
			Map<String, String> numericKeys = new HashMap<>();
			for (RequestParam param : req.getRequestParam()) {
				if (param.getType().equalsIgnoreCase(BffAdminConstantsUtils.BODY)
						&& !param.getParameter().getPropertyType().equals(BffAdminConstantsUtils.STRING_TYPE)) {
					String propertyName = param.getParameter().getPropertyName().substring(
							param.getParameter().getPropertyName().lastIndexOf(BffAdminConstantsUtils.PERIOD) + 1);
					if (propertyName.contains(BffAdminConstantsUtils.OPEN_SQUARE_BRACES)) {
						propertyName = propertyName.substring(0,
								propertyName.indexOf(BffAdminConstantsUtils.OPEN_SQUARE_BRACES));
					}
					numericKeys.put(propertyName, param.getParameter().getPropertyType());
				}
			}

			UriComponentsBuilder uriBuilder = productApis.baseUrl()
					.path(registry.getBasePath())
					.path(apiMaster.getRequestEndpoint());

			processRequestParams(req, bodyParameterList, uriBuilder);
			if (!bodyParameterList.isEmpty()) {
				payload = constructRequestBody(bodyParameterList, numericKeys).toString();
			}

			HttpHeaders headers = new HttpHeaders();
			if (bearerToken != null) {
				headers.add(HttpHeaders.AUTHORIZATION, BffUtils.buildValidHeader(bearerToken));
			} else {
				headers.add(COOKIE_HEADER_NAME, BffUtils.buildValidHeader(authCookie));
			}
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<String> request = new HttpEntity<>(headers);

			UriComponents uriComponents = uriBuilder.build();

			String requestMethod = apiMaster.getRequestMethod();
			if (requestMethod.equalsIgnoreCase(HttpMethod.GET.toString())
					|| requestMethod.equalsIgnoreCase(HttpMethod.DELETE.toString())) {
				jsonNode = getOrDelete(requestMethod, uriComponents, request);

			} else if (requestMethod.equalsIgnoreCase(HttpMethod.POST.toString())
					|| requestMethod.equalsIgnoreCase(HttpMethod.PUT.toString())) {
				jsonNode = postOrPut(requestMethod, payload, headers, uriComponents);

			}

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG,
					BffAdminConstantsUtils.FORM_ID + BffAdminConstantsUtils.COLON + req.getFormId()
							+ BffAdminConstantsUtils.EMPTY_SPACES + BffAdminConstantsUtils.FORM_NAME
							+ BffAdminConstantsUtils.COLON + req.getFormName(),
					exp);
			userCodeList = new ArrayList<>();
			userCodeList.add(BffResponseCode.ERR_PRODUCT_INVOKE_SERVICE_USER_002);
			bffCoreResponse = new BffCoreResponse();
			bffCoreResponse.setMessage(exp.getMessage());
			bffCoreResponse.setTimestamp(new Date().toString());
			bffCoreResponse.setHttpStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return new ObjectMapper().valueToTree(bffCoreResponse);
		}

		return jsonNode;
	}

	/**Method help to POST or PUT data
	 * @param requestMethod Request Object
	 * @param payload Request Body
	 * @param headers Http headers
	 * @param uriComponents URL to be invoked
	 * @return JsonNode Returns the output
	 */
	private JsonNode postOrPut(String requestMethod, String payload, HttpHeaders headers, UriComponents uriComponents) {
		ObjectMapper objectMapper = new ObjectMapper();
		ResponseEntity<JsonNode> response;
		HttpMethod httpMethod = requestMethod.equals(HttpMethod.POST.name()) ? HttpMethod.POST : HttpMethod.PUT;
		HttpEntity<String> httpRequest = new HttpEntity<>(payload, headers);
		response = restTemplate.exchange(uriComponents.toUriString(), httpMethod, httpRequest,
				JsonNode.class);
		if (response.getStatusCodeValue() > 210) {
			return objectMapper.convertValue(response, JsonNode.class);
		} else {
			return response.getBody();
		}
	}

	/**Method help to call get or delete API
	 * @param requestMethod Request Object
	 * @param uriComponents URL to be invoked
	 * @param request HTTP method
	 * @return JsonNode Returns the output
	 */
	private JsonNode getOrDelete(String requestMethod, UriComponents uriComponents, HttpEntity<String> request) {

		HttpMethod httpMethod = requestMethod.equals(HttpMethod.GET.name()) ? HttpMethod.GET : HttpMethod.DELETE;
		ResponseEntity<JsonNode> response = restTemplate.exchange(uriComponents.toString(), httpMethod, request, JsonNode.class);
		ObjectMapper objectMapper = new ObjectMapper();

		if (response.getStatusCodeValue() > 210) {
			return objectMapper.convertValue(response, JsonNode.class);
		} else {
			return response.getBody();
		}
	}

	/**Method to build up request body
	 *
	 * @param bodyParameterList List of parameters
	 * @param numericKeys Map of numeric Keys
	 * @throws IOException
	 */
	private ObjectNode constructRequestBody(Properties bodyParameterList, Map<String, String> numericKeys)
			throws IOException {
		JavaPropsMapper javaPropsMapper = new JavaPropsMapper();
		JsonNode json = javaPropsMapper.readPropertiesAs(bodyParameterList, JsonNode.class);

		Iterator<String> m = json.fieldNames();
		List<String> propertyList = new ArrayList<>();
		while (m.hasNext()) {
			propertyList.add(m.next());
		}
		if (json.get(propertyList.get(0)).isObject() && propertyList.size() == 1) {

			json = json.get(propertyList.get(0));
		}

		ObjectNode obj = json.deepCopy();

		Iterator<Map.Entry<String, JsonNode>> it = obj.fields();

		while (it.hasNext()) {
			Map.Entry<String, JsonNode> l = it.next();

			if (l.getValue().getNodeType() == JsonNodeType.OBJECT) {
				ObjectNode obj1 = replaceField(l.getValue(), numericKeys);
				obj.replace(l.getKey(), obj1);

			} else if (l.getValue().getNodeType() == JsonNodeType.ARRAY) {
				ifTypeArray(numericKeys, obj, l);

			} else if (numericKeys.containsKey(l.getKey())) {
				if (numericKeys.get(l.getKey()).equals(BffAdminConstantsUtils.NUMBER_STRING)) {
					NumericNode nod = JsonNodeFactory.instance.numberNode(l.getValue().asDouble());
					JsonNode n = nod;
					obj.replace(l.getKey(), n);
				} else if (numericKeys.get(l.getKey()).equals(BffAdminConstantsUtils.INTEGER_STRING)) {
					NumericNode nod = JsonNodeFactory.instance.numberNode(l.getValue().asInt());
					JsonNode n = nod;
					obj.replace(l.getKey(), n);
				}
			}

		}
		return obj;
	}

	/**Prepare the Array Object
	 * @param numericKeys Map of numeric keys
	 * @param obj Object Node
	 * @param l List of keys to be looked for
	 */
	private void ifTypeArray(Map<String, String> numericKeys, ObjectNode obj, Map.Entry<String, JsonNode> l) {
		List<JsonNode> objlist = new ArrayList<>();
		l.getValue().forEach(objlist::add);
		List<JsonNode> objlist1 = new ArrayList<>();
		for (JsonNode currentNode : objlist) {
			if (currentNode.getNodeType().equals(JsonNodeType.OBJECT)) {
				ObjectNode obj1 = replaceField(currentNode, numericKeys);
				objlist1.add(obj1);
			} else {
				if (BffAdminConstantsUtils.INTEGER_STRING.equals(numericKeys.get(l.getKey()))) {
					objlist1.add(JsonNodeFactory.instance.numberNode(currentNode.asInt()));
				} else if (BffAdminConstantsUtils.NUMBER_STRING.equals(numericKeys.get(l.getKey()))) {
					objlist1.add(JsonNodeFactory.instance.numberNode(currentNode.asDouble()));
				}
			}
		}
		if (!objlist1.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode array = mapper.valueToTree(objlist1);
			obj.replace(l.getKey(), array);
		}
	}

	/**Replace a given node in the destination node
	 * @param value Value to be replaced
	 * @param numericKeys Map of numeric keys
	 * @return ObjectNode Return a node
	 */
	private ObjectNode replaceField(JsonNode value, Map<String, String> numericKeys) {

		ObjectNode obj = value.deepCopy();
		Iterator<Map.Entry<String, JsonNode>> it = value.fields();

		while (it.hasNext()) {
			Map.Entry<String, JsonNode> l = it.next();

			if (l.getValue().getNodeType() == JsonNodeType.OBJECT) {
				ObjectNode obj1 = replaceField(l.getValue(), numericKeys);
				obj.replace(l.getKey(), obj1);
			} else if (l.getValue().getNodeType() == JsonNodeType.ARRAY) {
				List<JsonNode> objlist = new ArrayList<>();
				l.getValue().forEach(objlist::add);
				List<JsonNode> objlist1 = new ArrayList<>();
				for (JsonNode currentNode : objlist) {
					ObjectNode obj1 = replaceField(currentNode, numericKeys);
					objlist1.add(obj1);
				}
				ObjectMapper mapper = new ObjectMapper();
				ArrayNode array = mapper.valueToTree(objlist1);
				obj.replace(l.getKey(), array);
			} else if (numericKeys.containsKey(l.getKey())) {
				if (numericKeys.get(l.getKey()).equals(BffAdminConstantsUtils.NUMBER_STRING)) {
					NumericNode numericNode = JsonNodeFactory.instance.numberNode(l.getValue().asDouble());
					JsonNode jsonNode = numericNode;
					obj.replace(l.getKey(), jsonNode);
				} else if (numericKeys.get(l.getKey()).equals(BffAdminConstantsUtils.INTEGER_STRING)) {
					NumericNode numericNode = JsonNodeFactory.instance.numberNode(l.getValue().asInt());
					JsonNode jsonNode = numericNode;
					obj.replace(l.getKey(), jsonNode);
				}
			}

		}
		return obj;
	}

	/**
	 * @param productApiInvokeRequest
	 * @param property
	 * @param value
	 * @return
	 * @throws JsonProcessingException
	 */
	public ProductApiInvokeRequest replaceParam(ProductApiInvokeRequest productApiInvokeRequest, String property,
			String value) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.convertValue(productApiInvokeRequest, JsonNode.class);
		JsonNode requestNode = jsonNode.at("/requestParam");
		if (requestNode.isArray()) {
			ArrayNode arrayNode = (ArrayNode) requestNode;
			for (JsonNode jnode : arrayNode) {
				if (jnode.isObject()) {
					ObjectNode obj = (ObjectNode) jnode;
					String[] s = obj.at("/parameter/propertyName").asText().split("\\.");
					String propertyToBeChanged = s[s.length - 1];
					if (propertyToBeChanged.equals(property)) {
						obj.set("value", JsonNodeFactory.instance.textNode(value));
						jnode = obj;
					}
				}
			}
		}
		return objectMapper.treeToValue(jsonNode, ProductApiInvokeRequest.class);
	}

	/**
	/**Prepare the Path and query parameters
	 * @param req Request Object
	 * @param bodyParameterList - List of parameters
	 * @param builder URL to be invoked
	 * @return String Return the build up URL
	 */
	private void processRequestParams(ProductApiInvokeRequest req, Properties bodyParameterList, UriComponentsBuilder builder) {
		ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
		HashMap<String, Object> pathVars = new HashMap<>();

		for (RequestParam param : req.getRequestParam()) {
			String paramType = param.getType();
			String propertyName = param.getParameter().getPropertyName();
			if (paramType.equalsIgnoreCase(BffAdminConstantsUtils.QUERY)) {
				builder.queryParam(propertyName, param.getValue());
			}
			else if (paramType.equalsIgnoreCase(BffAdminConstantsUtils.PATH)) {
				pathVars.put(propertyName, param.getValue());
			}
			else if (paramType.equalsIgnoreCase((BffAdminConstantsUtils.BODY))) {
				requestBody(bodyParameterList, rootNode, param);
			}
		}

		builder.uriVariables(pathVars);
	}

	/**Prepare the request body
	 * @param bodyParameterList - List of parameters
	 * @param rootNode - Root of the node where request need to built
	 * @param param - Request parameter
	 */
	private void requestBody(Properties bodyParameterList, ObjectNode rootNode, RequestParam param) {
		String propertyType = param.getParameter().getPropertyType();
		String propertyName = param.getParameter().getPropertyName();
		String paramValue = param.getValue();

		switch (propertyType) {
			case BffAdminConstantsUtils.STRING_TYPE:
				rootNode.put(propertyName, paramValue);
				bodyParameterList.put(propertyName, paramValue);
				break;
			case BffAdminConstantsUtils.BOOLEAN_STRING:
				rootNode.put(propertyName, Boolean.valueOf(paramValue));
				bodyParameterList.put(propertyName, Boolean.valueOf(paramValue));
				break;
			case BffAdminConstantsUtils.INTEGER_STRING:
				rootNode.put(propertyName, Integer.valueOf(paramValue));
				bodyParameterList.put(propertyName, Integer.valueOf(paramValue));
				break;
			case BffAdminConstantsUtils.NUMBER_STRING:
				rootNode.put(propertyName, Double.valueOf(paramValue));
				bodyParameterList.put(propertyName, Double.valueOf(paramValue));
				break;
			case BffAdminConstantsUtils.OBJECT_TYPE:
				if (propertyName.contains(BffAdminConstantsUtils.OPEN_SQUARE_BRACES)) {
					ArrayNode arrayNode;
					String key = propertyName.substring(propertyName.indexOf(BffAdminConstantsUtils.PERIOD) + 1,
							propertyName.indexOf(BffAdminConstantsUtils.OPEN_SQUARE_BRACES));
					if (rootNode.has(key)) {
						arrayNode = (ArrayNode) rootNode.get(key);
					}
					else {
						arrayNode = rootNode.arrayNode();
					}
					arrayNode.add(paramValue);
				}
				rootNode.put(propertyName, Double.valueOf(paramValue));
				break;
		}
	}

	/**Get the value at a JSON node
	 * @param jsonNode Node where we need to get value
	 * @param path Path of the node
	 * @return String value of the node
	 */
	public String getJSONNodeValue(JsonNode jsonNode, String path) {
		if (jsonNode == null) {
			return BffAdminConstantsUtils.WMS;
		}

		return jsonNode.at(path).asText();
	}

	/**Converts to JSON node
	 * @param apiResponseMap Map of Attributes
	 * @return JsonNode Return a node
	 */
	public JsonNode jsonNodeConverter(Map<String, JsonNode> apiResponseMap) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.convertValue(apiResponseMap, JsonNode.class);
		return jsonNode;
	}

	/**To Execute the orcherstration in parallel
	 * @param apiInputMap Request Object
	 * @param authCookie Token for authentication
	 * @param bearerToken Token for authorization
	 * @param productName Name of the product
	 * @param registryLayerMap Map with registry and layers
	 * @return Returns a node
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public JsonNode executeParallel(Map<String, ProductApiInvokeRequest> apiInputMap, String authCookie,
			String bearerToken, String productName, Map<String, RegistryMap> registryLayerMap)
			throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newFixedThreadPool(apiInputMap.size());
		Map<String, Future<ResponseEntity<JsonNode>>> futureCallMap = new HashMap<>();
		String apiName = null;
		for (Map.Entry<String, ProductApiInvokeRequest> entry : apiInputMap.entrySet()) {
			apiName = entry.getKey();
			Future<ResponseEntity<JsonNode>> future = executor.submit(new APIRunThread(apiInputMap.get(apiName),
					authCookie, this, bearerToken, registryLayerMap.get(apiName).getLayer(),
					registryLayerMap.get(apiName).getRegistryName(),productName));

			futureCallMap.put(entry.getKey(), future);
		}

		Map<String, JsonNode> resultMap = new HashMap<>();
		for (Map.Entry<String, ProductApiInvokeRequest> entry : apiInputMap.entrySet()) {
			resultMap.put(entry.getKey(), futureCallMap.get(entry.getKey()).get().getBody());
		}

		executor.shutdown();
		while (!executor.isTerminated()) {
			executor.shutdown();
			LOGGER.log(Level.DEBUG, "now its been shutdown");
		}
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.convertValue(resultMap, JsonNode.class);
		return jsonNode;
	}
}
