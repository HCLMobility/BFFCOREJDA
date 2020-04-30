package com.jda.mobility.framework.extensions.controller;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.config.AppProperties;
import com.jda.mobility.framework.extensions.config.ProductApiSettings;
import com.jda.mobility.framework.extensions.entity.ApiMaster;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.ProdApiWrkMemRequest;
import com.jda.mobility.framework.extensions.model.ProductApiInvokeRequest;
import com.jda.mobility.framework.extensions.model.ProductApiRawInvokeRequest;
import com.jda.mobility.framework.extensions.model.RequestParam;
import com.jda.mobility.framework.extensions.model.SearchRequest;
import com.jda.mobility.framework.extensions.repository.ApiMasterRepository;
import com.jda.mobility.framework.extensions.repository.ApiRegistryRepository;
import com.jda.mobility.framework.extensions.service.AutoCompleteService;
import com.jda.mobility.framework.extensions.service.OrchestrationService;
import com.jda.mobility.framework.extensions.util.RequestHelper;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ApiRegistryType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;
import com.jda.mobility.framework.extensions.utils.JsonNodeModifier;
import com.jda.mobility.framework.extensions.utils.ProductAPIServiceInvoker;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RequestMapping("/api/product/v1")
@RestController
public class ProductApiInvokeController {

	private static final String DROOLS_DSL_ERROR_ERROR_PREFIX = "Drools DSL error: ";

	private static final String X_AUTH_TOKEN_HEADER_KEY = "X-Auth-Token";

	private static final String ORCHESTRATION_ERROR = "orchErrors";

	private static final String TIME_STAMP = "timestamp";

	private static final String CODE = "code";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private BffResponse bffResponse;

	@Autowired
	private ApiRegistryRepository apiRegistryRepository;

	@Autowired
	private ApiMasterRepository apiMasterRepository;

	@Autowired
	private AutoCompleteService autoCompleteService;

	@Autowired
	JsonNodeModifier jsonNodeModifier;

	@Autowired
	private ProductAPIServiceInvoker productAPIServiceInvoker;

	@Autowired
	private OrchestrationService orchestrationService;

	@Autowired
	private ProductApiSettings productApis;

	@Autowired
	private RequestHelper requestHelper;

    @Autowired
    private SessionDetails sessionDetails;

	@Autowired
	private AppProperties appProperties;

	private static final Logger LOGGER = LogManager.getLogger(ProductApiInvokeController.class);

	/**
	 * @param req
	 * @param httpReq
	 * @return ResponseEntity<BffCoreResponse>
	 */
	@PostMapping
	public ResponseEntity<BffCoreResponse> invokeProductApi(@RequestBody ProductApiInvokeRequest req,
			HttpServletRequest httpReq) {
		BffCoreResponse bffCoreResponse;
		try {
			Optional<ApiRegistry> optRegistry = apiRegistryRepository
					.findByApiTypeAndNameAndRoleMaster_level(req.getApiType(), req.getRegName(), req.getLayer());
			if (!optRegistry.isPresent()) {
				LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, req.getRegName());
				return new ResponseEntity<>(
						bffResponse.errResponse(
								List.of(BffResponseCode.ERR_APIMASTER_SERVICE_API_FETCH_REGISTRYID_DBEXCEPTION,
										BffResponseCode.ERR_APIMASTER_SERVICE_USER_FETCH_REGISTRYID_DBEXCEPTION),
								StatusCode.BADREQUEST, null, req.getRegName()),
						HttpStatus.BAD_REQUEST);
			}

			ApiRegistry registry = optRegistry.get();
			Optional<ApiMaster> optApiMaster = apiMasterRepository
					.findByNameAndRequestEndpointAndRequestMethodAndApiRegistry_uid(req.getApiName(),
							req.getRequestEndpoint(), req.getRequestMethod(), registry.getUid());
			if (!optApiMaster.isPresent()) {
				LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, req.getApiName());
				return new ResponseEntity<>(
						bffResponse.errResponse(
								List.of(BffResponseCode.ERR_APIMASTER_SERVICE_API_FETCH_REGISTRYID_DBEXCEPTION,
										BffResponseCode.ERR_APIMASTER_SERVICE_USER_FETCH_REGISTRYID_DBEXCEPTION),
								StatusCode.BADREQUEST, null , req.getApiName()),
						HttpStatus.BAD_REQUEST);
			}

			LOGGER.log(Level.INFO, registry.getApiType());

			HttpHeaders headers = buildHeaders(httpReq, registry);
			String authCookie = requestHelper.cookieValue(httpReq);
			String bearerToken = requestHelper.oidcToken(httpReq);
			
			ApiMaster apiMaster = optApiMaster.get();
			if (ApiRegistryType.ORCHESTRATION.getType().equals(registry.getApiType())) {
				return invokeOrchestrationApi(req, registry, authCookie, apiMaster, bearerToken);
			}

			Map<String, String> numericKeys = new HashMap<>();
			if (req.getRequestParam() == null) {
				req.setRequestParam(new ArrayList<>());
			}
			for (RequestParam param : req.getRequestParam()) {
				if (param.getType().equalsIgnoreCase(BffAdminConstantsUtils.BODY)) {
					createReqBodyDataTypeMap(numericKeys, param);
				}
			}
			
			// TEMPORARY CODE - TO BE REMOVED AFTER TESTING IS DONE
			if (ApiRegistryType.LOCAL.getType().equals(registry.getApiType())) {
				setLocalApiSettings(httpReq, headers, authCookie);
			}
			// TEMPORARY CODE - TO BE REMOVED AFTER TESTING IS DONE

			UriComponentsBuilder uriBuilder = determineApiUrl(apiMaster, registry);

			Map<String, String> bodyParameterMap = new HashMap<>();
			URI uri = processRequestParams(req, bodyParameterMap, uriBuilder);

			//Building Request body
			String payload = null;
			if (!bodyParameterMap.isEmpty()) {
				payload = constructRequestBody(bodyParameterMap, numericKeys).toString();
			}

			if (ApiRegistryType.LOCAL.getType().equals(registry.getApiType())) {
				bffCoreResponse = invokeLocalApi(authCookie, httpReq.getHeader(HttpHeaders.AUTHORIZATION), payload,
						registry, apiMaster, headers, uri);
			} else {
				bffCoreResponse = invokeExternalApi(payload, apiMaster, headers, uri);
			}
		} catch (HttpStatusCodeException hexp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG,
					BffAdminConstantsUtils.FORM_ID + BffAdminConstantsUtils.COLON + req.getFormId()
							+ BffAdminConstantsUtils.EMPTY_SPACES + BffAdminConstantsUtils.FORM_NAME
							+ BffAdminConstantsUtils.COLON + req.getFormName());
			LOGGER.log(Level.ERROR, hexp.getLocalizedMessage(), hexp);
			String cause = (hexp.getLocalizedMessage() != null) ? hexp.getLocalizedMessage()
					: BffAdminConstantsUtils.EMPTY_SPACES;
			bffCoreResponse = bffResponse.errResponseIgnoreDelim(
					List.of(BffResponseCode.ERR_PRODUCT_INVOKE_SERVICE_API_001,
							BffResponseCode.ERR_PRODUCT_INVOKE_SERVICE_USER_002),
					hexp.getRawStatusCode(), null, cause);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG,
					BffAdminConstantsUtils.FORM_ID + BffAdminConstantsUtils.COLON + req.getFormId()
							+ BffAdminConstantsUtils.EMPTY_SPACES + BffAdminConstantsUtils.FORM_NAME
							+ BffAdminConstantsUtils.COLON + req.getFormName());
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponseIgnoreDelim(
					List.of(BffResponseCode.ERR_PRODUCT_INVOKE_SERVICE_API_001,
							BffResponseCode.ERR_PRODUCT_INVOKE_SERVICE_USER_002),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null, exp.getMessage());
		}
		return new ResponseEntity<>(bffCoreResponse, HttpStatus.valueOf(bffCoreResponse.getHttpStatusCode()));
	}

	private String getScheme(ApiRegistry apiRegistry) {
		String scheme = BffAdminConstantsUtils.HTTP;
		//Default value
		
		//Check whether scheme is present , then override
		if (StringUtils.isNotEmpty(apiRegistry.getSchemeList())) {
			//To handle List of Protocols 
			if (apiRegistry.getSchemeList().contains(BffAdminConstantsUtils.COMMA)) {
				scheme = apiRegistry.getSchemeList().substring(1, apiRegistry.getSchemeList().indexOf(BffAdminConstantsUtils.COMMA));
			}
			//To support single protocol
			else
			{
				scheme = apiRegistry.getSchemeList().substring(1, apiRegistry.getSchemeList().length()-1);
			}
		}
		return scheme;
	}

	private HttpHeaders buildHeaders(HttpServletRequest httpReq, ApiRegistry registry) {
		HttpHeaders headers = new HttpHeaders();
		if (!registry.getApiType().equals(ApiRegistryType.EXTERNAL.getType())) {
			requestHelper.initHeadersFrom(headers, httpReq);
		}
		else {
			headers.setContentType(MediaType.APPLICATION_JSON);
		}
		return headers;
	}

	/**
	 * @param req
	 * @param httpReq
	 * @return ResponseEntity<BffCoreResponse>
	 */
	@PostMapping("/raw")
	public ResponseEntity<?> invokeProductApiRaw(@RequestBody ProductApiRawInvokeRequest req,
												 HttpServletRequest httpReq) {
		try {

			HttpHeaders headers = requestHelper.initHeadersFrom(httpReq);
			UriComponents uriComponents = productApis.baseUrl()
					.path(req.getApiUri())
					.build();
			LOGGER.log(Level.INFO, req.getApiPayload());
			ResponseEntity<JsonNode> response = restTemplate.exchange(uriComponents.toString(),
					HttpMethod.POST, new HttpEntity<>(req.getApiPayload(), headers), JsonNode.class);
			JsonNode jsonNode = response.getBody();
			return new ResponseEntity<>(jsonNode, response.getStatusCode());

		} catch (HttpStatusCodeException hexp) {
			LOGGER.catching(hexp);
			return new ResponseEntity<>(hexp.getResponseBodyAsString(), HttpStatus.valueOf(hexp.getRawStatusCode()));
		} catch (Exception exp) {
			ObjectNode prodApiRespErrRootNode = generateErrRespPayload(exp);
			LOGGER.catching(exp);
			return new ResponseEntity<>(prodApiRespErrRootNode, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ObjectNode generateErrRespPayload(Exception exp) {
		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
		ObjectNode prodApiRespErrRootNode = jsonNodeFactory.objectNode();
		prodApiRespErrRootNode.put("responseId", UUID.randomUUID().toString());
		prodApiRespErrRootNode.put(TIME_STAMP, Calendar.getInstance().getTime().toString());
		ArrayNode errorsNode = prodApiRespErrRootNode.arrayNode();
		ObjectNode eachErrorNode = jsonNodeFactory.objectNode();
		eachErrorNode.put("errorCode", "9001");
		eachErrorNode.put("userMessage", "Server is down or unternal server error occurred due to: " + exp.getCause());
		errorsNode.add(eachErrorNode);
		prodApiRespErrRootNode.set("errors", errorsNode);
		return prodApiRespErrRootNode;
	}

	private ResponseEntity<BffCoreResponse> invokeOrchestrationApi(ProductApiInvokeRequest req, ApiRegistry registry,
			String authCookie, ApiMaster apiMaster, String bearerToken) throws IOException {
		BffCoreResponse bffCoreResponse;
		ObjectMapper objectMapper = new ObjectMapper();
		String orchestrationName = apiMaster.getOrchestrationName();
		JsonNode jsonNode = objectMapper.valueToTree(req);
		jsonNode = invokeOrchestrationDynamic(authCookie, jsonNode, registry, orchestrationName, bearerToken);
		JsonNode errorNode = jsonNode.path(ORCHESTRATION_ERROR);
		if (!errorNode.isEmpty()) {
			bffCoreResponse = bffResponse.response(errorNode,
					BffResponseCode.ERR_PRODUCT_INVOKE_SERVICE_API_001,
					BffResponseCode.ERR_PRODUCT_INVOKE_SERVICE_USER_002,
					StatusCode.INTERNALSERVERERROR, null, "execution error");
			return new ResponseEntity<>(bffCoreResponse, HttpStatus.valueOf(bffCoreResponse.getHttpStatusCode()));
		}
		bffCoreResponse = bffResponse.response(jsonNode, BffResponseCode.PRODUCT_INVOKE_SERVICE_SUCCESS_CODE_001,
				BffResponseCode.PRODUCT_INVOKE_SERVICE_USER_CODE_001, StatusCode.OK);
		return new ResponseEntity<>(bffCoreResponse, HttpStatus.valueOf(bffCoreResponse.getHttpStatusCode()));
	}

	private JsonNode invokeOrchestrationDynamic(String authCookie, JsonNode jsonNode, ApiRegistry registry,
			String orchestrationName, String bearerToken) throws IOException {

		byte[] source = orchestrationService.getRuleContent(registry, orchestrationName);

		ProdApiWrkMemRequest prodApiWrkMemRequest = orchestrationService.buildOrchestrationPreProcessor(jsonNode, registry.getRoleMaster().getLevel());

		Map<String, JsonNode> apiResponseMap = new HashMap<>();
		try {
			prodApiWrkMemRequest.setProdAuthCookie(authCookie);
			prodApiWrkMemRequest.setBearerToken(bearerToken);
			prodApiWrkMemRequest.setTenant(sessionDetails.getTenant());
			prodApiWrkMemRequest.setLayer(registry.getRoleMaster().getName());
			prodApiWrkMemRequest.setProductAPIServiceInvoker(productAPIServiceInvoker);

			prodApiWrkMemRequest.setJsonNodeModifier(jsonNodeModifier);
			prodApiWrkMemRequest.setApiResponseMap(apiResponseMap);

			byte[] grammarbytes = new ClassPathResource(BffAdminConstantsUtils.API_ORCHESTRATION_GRAMMAR_FILE)
					.getInputStream().readAllBytes();

			KieSession kSession = null;
			KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
			kb.add(ResourceFactory.newByteArrayResource(grammarbytes), ResourceType.DSL);
			kb.add(ResourceFactory.newByteArrayResource(source), ResourceType.DSLR);

			KnowledgeBuilderErrors errors = kb.getErrors();
			StringBuilder errorBuilder = new StringBuilder();
			for (KnowledgeBuilderError error : errors) {
				errorBuilder.append(error.getMessage()).append(BffAdminConstantsUtils.COLON);
			}
			if (errorBuilder.length() > 0) {
				errorBuilder.insert(0, DROOLS_DSL_ERROR_ERROR_PREFIX);
				throw new BffException(errorBuilder.toString());
			}
			InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
			kBase.addPackages(kb.getKnowledgePackages());

			kSession = kBase.newKieSession();

			kSession.insert(prodApiWrkMemRequest);
			kSession.getAgenda().getAgendaGroup(orchestrationName).setFocus();
			kSession.fireAllRules();
			boolean errorsAvailable = false;
			ObjectNode errorNode = JsonNodeFactory.instance.objectNode();
			Iterator<Entry<String, JsonNode>> errorSet = prodApiWrkMemRequest.getApiResponseMap().entrySet().iterator();

			while (errorSet.hasNext()) {
				Entry<String, JsonNode> nodeWithError = errorSet.next();
				if (!nodeWithError.getValue().findPath(TIME_STAMP).isMissingNode()
						|| !nodeWithError.getValue().findPath(CODE).isMissingNode()) {
					errorNode.set(nodeWithError.getKey(), nodeWithError.getValue());
					errorsAvailable = true;
				}
			}
			if (errorsAvailable) {

				ObjectNode resultNode = (ObjectNode) prodApiWrkMemRequest.getApiResponse();
				if (resultNode != null) {
					resultNode.set(ORCHESTRATION_ERROR, errorNode);
				} else {
					resultNode = JsonNodeFactory.instance.objectNode();
					resultNode.set(ORCHESTRATION_ERROR, errorNode);
				}

				return resultNode;
			}

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
		}

		return prodApiWrkMemRequest.getApiResponse();

	}

	private BffCoreResponse invokeExternalApi(String payload, ApiMaster apiMaster, HttpHeaders headers,
			URI url) throws JsonProcessingException {
		LOGGER.log(Level.INFO, payload);
		if (apiMaster.getPreProcessor() != null) {
			payload = preProcess(payload, apiMaster.getPreProcessor());
		}

		LOGGER.log(Level.INFO, payload);
		ResponseEntity<JsonNode> response = restTemplate.exchange(url,
				HttpMethod.valueOf(apiMaster.getRequestMethod()), new HttpEntity<>(payload, headers), JsonNode.class);

		JsonNode jsonNode = response.getBody();
		if (apiMaster.getPostProcessor() != null) {
			jsonNode = postProcess(jsonNode, apiMaster.getPostProcessor());
		}
		return bffResponse.response(jsonNode, BffResponseCode.PRODUCT_INVOKE_SERVICE_SUCCESS_CODE_001,
				BffResponseCode.PRODUCT_INVOKE_SERVICE_USER_CODE_001, StatusCode.OK);
	}

	private BffCoreResponse invokeLocalApi(String authCookie, String bearerToken, String payload, ApiRegistry registry,
			ApiMaster apiMaster, HttpHeaders headers, URI url) throws JsonProcessingException {
		BffCoreResponse bffCoreResponse = null;
		if ("MobileTesting".equals(registry.getName())) {
			bffCoreResponse = invokeMobileTestingApi(authCookie, bearerToken, payload, apiMaster);
		} else {
			ResponseEntity<Object> response = restTemplate.exchange(url,
					HttpMethod.valueOf(apiMaster.getRequestMethod()), new HttpEntity<>(payload, headers), Object.class);
			bffCoreResponse = bffResponse.response(response.getBody(),
					BffResponseCode.PRODUCT_INVOKE_SERVICE_SUCCESS_CODE_001,
					BffResponseCode.PRODUCT_INVOKE_SERVICE_USER_CODE_001, StatusCode.OK);
		}
		return bffCoreResponse;
	}

	private BffCoreResponse invokeMobileTestingApi(String authCookie,
												   String bearerToken,
												   String payload,
												   ApiMaster apiMaster) throws JsonProcessingException {
		BffCoreResponse bffCoreResponse;
		if ("search".equals(apiMaster.getName())) {
			JsonNode searchReqNode = new ObjectMapper().readTree(payload);

			SearchRequest searchRequest = new SearchRequest();
			searchRequest.setSearchTerm(searchReqNode.at("/searchTerm").asText());
			searchRequest.setSearchType(searchReqNode.at("/searchType").asText());

			if (appProperties.isOidcEnabled()) {
				bffCoreResponse = autoCompleteService.search(searchRequest, null, bearerToken);
			}
			else {
				bffCoreResponse = autoCompleteService.search(searchRequest, authCookie, null);
			}
		}
		else {
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_PRODUCT_INVOKE_SERVICE_API_001,
					BffResponseCode.ERR_PRODUCT_INVOKE_SERVICE_API_001), StatusCode.OK);
		}
		return bffCoreResponse;
	}

	private void setLocalApiSettings(HttpServletRequest httpReq, HttpHeaders headers, String authCookie) {
		String cookieValue = requestHelper.cookieValue(httpReq);
		if (StringUtils.isNotBlank(cookieValue)) {
			headers.add("SET_COOKIE", BffUtils.buildValidHeader(cookieValue));
		}
		headers.add(HttpHeaders.AUTHORIZATION, BffUtils.buildValidHeader(httpReq.getHeader(HttpHeaders.AUTHORIZATION)));
		headers.add(X_AUTH_TOKEN_HEADER_KEY, BffUtils.buildValidHeader(httpReq.getHeader(X_AUTH_TOKEN_HEADER_KEY)));
	}

	private JsonNode postProcess(JsonNode jsonNode, byte[] postProcessor) {
		ProdApiWrkMemRequest prodApiWrkMemRequest = new ProdApiWrkMemRequest();
		prodApiWrkMemRequest.setJsonNodeModifier(jsonNodeModifier);
		prodApiWrkMemRequest.setApiResponse(jsonNode);
		executeRules(postProcessor, prodApiWrkMemRequest, "POSTMODIFICATION");
		return prodApiWrkMemRequest.getApiResponse();
	}

	private void executeRules(byte[] ruleBytes, ProdApiWrkMemRequest prodApiWrkMemRequest, String agendaGroup) {
		try {
			KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
			kb.add(ResourceFactory
					.newByteArrayResource(new ClassPathResource(BffAdminConstantsUtils.PREPOSTPROC_GRAMMAR_FILE)
							.getInputStream().readAllBytes()),
					ResourceType.DSL);
			kb.add(ResourceFactory.newByteArrayResource(ruleBytes), ResourceType.DSLR);

			KnowledgeBuilderErrors errors = kb.getErrors();
			StringBuilder errorBuilder = new StringBuilder();
			for (KnowledgeBuilderError error : errors) {
				errorBuilder.append(error.getMessage()).append(BffAdminConstantsUtils.COLON);
			}
			if (errorBuilder.length() > 0) {
				errorBuilder.insert(0, DROOLS_DSL_ERROR_ERROR_PREFIX);
				throw new BffException(errorBuilder.toString());
			}
			InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
			kBase.addPackages(kb.getKnowledgePackages());

			KieSession kSession = kBase.newKieSession();
			kSession.insert(prodApiWrkMemRequest);
			kSession.getAgenda().getAgendaGroup(agendaGroup).setFocus();

			kSession.fireAllRules();
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "***Exception while executing sequence request calls: {}", e.getLocalizedMessage());
			throw new BffException(e.getLocalizedMessage());
		}
	}

	private String preProcess(String payload, byte[] preProcessor) throws JsonProcessingException{
		ProdApiWrkMemRequest prodApiWrkMemRequest = new ProdApiWrkMemRequest();
		prodApiWrkMemRequest.setJsonNodeModifier(jsonNodeModifier);
		prodApiWrkMemRequest.setApiRequest(payload!=null?new ObjectMapper().readTree(payload):null);
		executeRules(preProcessor, prodApiWrkMemRequest, "PREMODIFICATION");
		return prodApiWrkMemRequest.getApiRequest().toString();
	}

	/**
	 * @param numericKeys
	 * @param param
	 */
	private void createReqBodyDataTypeMap(Map<String, String> numericKeys, RequestParam param) {
		String propertyName = param.getParameter().getPropertyName()
				.substring(param.getParameter().getPropertyName().lastIndexOf(BffAdminConstantsUtils.PERIOD) + 1);
		if (propertyName.contains(BffAdminConstantsUtils.OPEN_SQUARE_BRACES)) {
			propertyName = propertyName.substring(0, propertyName.indexOf(BffAdminConstantsUtils.OPEN_SQUARE_BRACES));
		}
		if (param.getValue() == null) {
			numericKeys.put(propertyName, BffAdminConstantsUtils.JSON_NULL_STR);
		}
		else {
			numericKeys.put(propertyName, param.getParameter().getPropertyType());
		}
	}

	/**
	 * @param bodyParameterMap
	 * @param numericKeys
	 * @return ObjectNode
	 * @throws IOException
	 */
	private JsonNode constructRequestBody(Map<String, String> bodyParameterMap, Map<String, String> numericKeys)
			throws IOException {
		JavaPropsMapper javaPropsMapper = new JavaPropsMapper();
		JsonNode json = javaPropsMapper.readMapAs(bodyParameterMap, JsonNode.class);
		JsonNode rawObj = json.deepCopy();
		if (rawObj.isObject()) {
			setValuePerDataType(numericKeys, (ObjectNode) rawObj);
		} else if (rawObj.isArray()) {
			for (final JsonNode rawNode : (ArrayNode) rawObj) {
				setValuePerDataType(numericKeys, (ObjectNode) rawNode);
			}
		}

		return rawObj;
	}

	private void setValuePerDataType(Map<String, String> numericKeys, ObjectNode obj) {
		Iterator<Map.Entry<String, JsonNode>> it = obj.fields();
		while (it.hasNext()) {
			Map.Entry<String, JsonNode> currNode = it.next();
			if (currNode.getValue().getNodeType() == JsonNodeType.OBJECT) {
				ObjectNode obj1 = replaceField(currNode.getValue(), numericKeys);
				obj.replace(currNode.getKey(), obj1);
			} else if (currNode.getValue().getNodeType() == JsonNodeType.ARRAY) {
				setNumericFieldsInArray(numericKeys, obj, currNode);
			} else if (numericKeys.containsKey(currNode.getKey())) {
				if (numericKeys.get(currNode.getKey()).equals(BffAdminConstantsUtils.NUMBER_STRING)) {
					NumericNode nod = JsonNodeFactory.instance.numberNode(currNode.getValue().asDouble());
					JsonNode n = nod;
					obj.replace(currNode.getKey(), n);
				} else if (numericKeys.get(currNode.getKey()).equals(BffAdminConstantsUtils.INTEGER_STRING)) {
					NumericNode nod = JsonNodeFactory.instance.numberNode(currNode.getValue().asLong());
					JsonNode n = nod;
					obj.replace(currNode.getKey(), n);
				} else if (numericKeys.get(currNode.getKey()).equals(BffAdminConstantsUtils.BOOLEAN_STRING)) {
					BooleanNode nod = JsonNodeFactory.instance.booleanNode(currNode.getValue().asBoolean());
					JsonNode n = nod;
					obj.replace(currNode.getKey(), n);
				} else if (numericKeys.get(currNode.getKey()).equals(BffAdminConstantsUtils.JSON_NULL_STR)) {
					obj.replace(currNode.getKey(), null);
				}
			}
		}
	}

	/**
	 * @param numericKeys
	 * @param obj
	 * @param currNode
	 */
	private void setNumericFieldsInArray(Map<String, String> numericKeys, ObjectNode obj,
			Map.Entry<String, JsonNode> currNode) {
		List<JsonNode> objlist = new ArrayList<>();
		currNode.getValue().forEach(objlist::add);
		List<JsonNode> objlist1 = new ArrayList<>();
		for (JsonNode currentNode : objlist) {
			if (currentNode.getNodeType().equals(JsonNodeType.OBJECT)) {
				ObjectNode obj1 = replaceField(currentNode, numericKeys);
				objlist1.add(obj1);
			}
			else {
				if (BffAdminConstantsUtils.INTEGER_STRING.equals(numericKeys.get(currNode.getKey()))) {
					objlist1.add(JsonNodeFactory.instance.numberNode(currentNode.asLong()));
				} else if (BffAdminConstantsUtils.NUMBER_STRING.equals(numericKeys.get(currNode.getKey()))) {
					objlist1.add(JsonNodeFactory.instance.numberNode(currentNode.asDouble()));
				} else if (BffAdminConstantsUtils.BOOLEAN_STRING.equals(numericKeys.get(currNode.getKey()))) {
					objlist1.add(JsonNodeFactory.instance.booleanNode(currentNode.asBoolean()));
				} else if (BffAdminConstantsUtils.JSON_NULL_STR.equals(numericKeys.get(currNode.getKey()))) {
					objlist1.add(JsonNodeFactory.instance.nullNode());
				}

			}
		}
		if (!objlist1.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode array = mapper.valueToTree(objlist1);
			obj.replace(currNode.getKey(), array);
		}
	}

	/**
	 * @param value
	 * @param numericKeys
	 * @return ObjectNode
	 */
	private ObjectNode replaceField(JsonNode value, Map<String, String> numericKeys) {
		ObjectNode obj = null;
		if (value.isObject()) {
			obj = value.deepCopy();
			Iterator<Map.Entry<String, JsonNode>> it = value.fields();
			while (it.hasNext()) {
				Map.Entry<String, JsonNode> currNode = it.next();
				if (numericKeys.containsKey(currNode.getKey())) {
					if (numericKeys.get(currNode.getKey()).equals(BffAdminConstantsUtils.NUMBER_STRING)) {
						obj.replace(currNode.getKey(),
								JsonNodeFactory.instance.numberNode(currNode.getValue().asDouble()));
					} else if (numericKeys.get(currNode.getKey()).equals(BffAdminConstantsUtils.INTEGER_STRING)) {
						obj.replace(currNode.getKey(),
								JsonNodeFactory.instance.numberNode(currNode.getValue().asInt()));
					} else if (numericKeys.get(currNode.getKey()).equals(BffAdminConstantsUtils.BOOLEAN_STRING)) {
						obj.replace(currNode.getKey(),
								JsonNodeFactory.instance.booleanNode(currNode.getValue().asBoolean()));
					}
					else if (BffAdminConstantsUtils.JSON_NULL_STR.equals(numericKeys.get(currNode.getKey()))) {
						obj.replace(currNode.getKey(), null);
					}
				} else {
					if (currNode.getValue().isObject()) {
						obj.replace(currNode.getKey(), replaceField(currNode.getValue(), numericKeys));
					}
					else if (currNode.getValue().isArray()) {
						fixArrayNodes(numericKeys, obj, currNode);
					}
				}
			}
		}
		return obj;
	}

	private void fixArrayNodes(Map<String, String> numericKeys, ObjectNode obj, Map.Entry<String, JsonNode> currNode) {
		List<JsonNode> objlist = new ArrayList<>();
		currNode.getValue().forEach(objlist::add);
		List<JsonNode> objlist1 = new ArrayList<>();
		for (JsonNode currentNode : objlist) {
			if (currentNode.isValueNode()) {
				objlist1.add(new TextNode(currentNode.asText()));
			} else {
				objlist1.add(replaceField(currentNode, numericKeys));
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode array = mapper.valueToTree(objlist1);
		obj.replace(currNode.getKey(), array);
	}

	/**
	 * @param req
	 * @param bodyParameterMap
	 * @param builder
	 * @return String
	 */
	private URI processRequestParams(ProductApiInvokeRequest req, Map<String, String> bodyParameterMap, UriComponentsBuilder builder) {
		Map<String, String> pathParams = new HashMap<>();

		for (RequestParam param : req.getRequestParam()) {
			//Building Query parameters as collection format = "multi"
			if (param.getType().equalsIgnoreCase(BffAdminConstantsUtils.QUERY)) {
				String key ;
				if (param.getParameter().getPropertyName().contains(BffAdminConstantsUtils.OPEN_SQUARE_BRACES)
							&& param.getParameter().getPropertyName().contains(BffAdminConstantsUtils.CLOSE_SQUARE_BRACES)) {
					key = param.getParameter().getPropertyName()
							.substring(0, param.getParameter().getPropertyName()
							.indexOf(BffAdminConstantsUtils.OPEN_SQUARE_BRACES));
				}
				else {
					key = param.getParameter().getPropertyName();
				}
				builder.queryParam(key, param.getValue());
			} 
			//Prepare map for Path or URI variables
			else if (param.getType().equalsIgnoreCase(BffAdminConstantsUtils.PATH)) {
				pathParams.put(param.getParameter().getPropertyName(), param.getValue());
			}
			//Prepare map for Request body
			else if (param.getType().equalsIgnoreCase(BffAdminConstantsUtils.BODY)) {
				bodyParameterMap.put(param.getParameter().getPropertyName(), param.getValue());
			}
		}
		//Building the path/URI variables (Replace the path variable with actual values)
		UriComponents uriComponent = builder.buildAndExpand(pathParams);

		//toUri() to encode the path and query params
		return uriComponent.toUri();
	}

	private UriComponentsBuilder determineApiUrl(ApiMaster api, ApiRegistry registry) {
		UriComponentsBuilder builder;
		if (registry.getApiType().equals(ApiRegistryType.EXTERNAL.getType())) {
			builder = UriComponentsBuilder.newInstance()
					.scheme(getScheme(registry))
					.host(registry.getContextPath())
					.port(registry.getPort());
		}
		else {
			builder = productApis.baseUrl();
		}

		return builder.path(registry.getBasePath())
				.path(api.getRequestEndpoint());
	}
}