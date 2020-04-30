package com.jda.mobility.framework.extensions.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.config.ProductApiSettings;
import com.jda.mobility.framework.extensions.entity.ApiMaster;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.exception.DataBaseException;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.Parameter;
import com.jda.mobility.framework.extensions.model.ProductApiInvokeRequest;
import com.jda.mobility.framework.extensions.model.RegistryMap;
import com.jda.mobility.framework.extensions.model.RequestParam;
import com.jda.mobility.framework.extensions.repository.ApiMasterRepository;
import com.jda.mobility.framework.extensions.repository.ApiRegistryRepository;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ApiRegistryType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class ProductAPIServiceInvokerTest {
	
	@InjectMocks
	private ProductAPIServiceInvoker productAPIServiceInvoker;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private BffResponse bffResponse;
	
	@Mock
	private ApiRegistryRepository apiRegistryRepository;
	
	@Mock
	private ApiMasterRepository apiMasterRepository;
	
	@Mock
	private ApiMaster apiMaster;

	@Mock
	private ApiRegistry apiRegistry;

	@Mock
	private ProductApiSettings productApi;

	private static final String COOKIE_HEADER_NAME = "Cookie";

	@Before
	public void setUp() {
		when(productApi.baseUrl())
				.thenAnswer(invocation -> UriComponentsBuilder.fromHttpUrl("http://3.13.173.174:4500"));
	}

	@Test
	public void testBuildRequest() {
		when(apiMasterRepository.findByName(any())).thenReturn(getApiMasterList());
		ProductApiInvokeRequest response = productAPIServiceInvoker.buildRequest("ORCHESTRATION");
		Assert.assertEquals(response.getApiName(), getApiMasterList().get(0).getApiRegistry().getName());
	}
	
	@Test
	public void testReplaceParam() throws JsonProcessingException {
		ProductApiInvokeRequest prodApiInvokeRequest =getPrdRequest();
		ProductApiInvokeRequest prodApiInvokeRequest1 = productAPIServiceInvoker.replaceParam(prodApiInvokeRequest, "role", "MD3");
		Assert.assertEquals("ORCHESTRATION", prodApiInvokeRequest1.getApiName());
	}	

	@Test
	public void testInvokeProductApi() throws JsonProcessingException {
		
		ProductApiInvokeRequest prdRequest = getPrdRequest();
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		String bearerToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTVVBFUiIsImlhdCI6MTU4MjgxNTEyMSwiZXhwIjoxNTgzNjc5MTIxLCJhdWQiOiJiZmYifQ.bJ3QxT3aazYzm28ha91FPGY0j02zj4PgYv1-egLgTOD8hq14zEg2nkhpkyqUyO-nk8KDmiYFsStsxx3O89P9TA";
		String authorization="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTVVBFUiIsImlhdCI6MTU4MjgxNTEyMSwiZXhwIjoxNTgzNjc5MTIxLCJhdWQiOiJiZmYifQ.bJ3QxT3aazYzm28ha91FPGY0j02zj4PgYv1-egLgTOD8hq14zEg2nkhpkyqUyO-nk8KDmiYFsStsxx3O89P9TA";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", BffUtils.buildValidHeader(authorization));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(headers);
		ApiRegistry registry= new ApiRegistry();
		registry.setBasePath("");
		registry.setApiType("INTERNAL");
		RoleMaster roleMaster= new RoleMaster();
		roleMaster.setLevel(0);
		registry.setRoleMaster(roleMaster);
		ApiMaster apiMaster = new ApiMaster();
		apiMaster.setRequestMethod("GET");
		apiMaster.setRequestEndpoint("/permissions");
		JsonNode jsonNode = new ObjectMapper().readTree("{\"TEST2\":1,\"TEST3\":\"true\",\"pk\":{\"0\":\"1\",\"id\":2},\"TEST4\":2.0,\"TEST1\":\"WMD1\"}");
		ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(jsonNode,HttpStatus.OK);
		BffCoreResponse bffCoreResponse = new BffCoreResponse();
		bffCoreResponse.setHttpStatusCode(200);
		when(bffResponse.response(any(), any(), any(), any()))
				.thenReturn(bffCoreResponse);
		when(restTemplate.exchange("http://3.13.173.174:4500/permissions?roleId=2", HttpMethod.GET, request,
				JsonNode.class)).thenReturn(responseEntity);
		when(apiMasterRepository.findById(UUID.fromString("4da0d5a3-6946-4c0e-a0e3-c209c5056925")))
				.thenReturn(Optional.of(apiMaster));
		when(apiRegistryRepository.findById(UUID.fromString("1d3114a7-e4b0-41b9-9357-02cc752b8a2d")))
				.thenReturn(Optional.of(apiRegistry));
		when(apiMasterRepository.findByApiRegistryAndName(registry, prdRequest.getApiName())).thenReturn(Optional.of(apiMaster));
		when(apiRegistryRepository.findByApiTypeAndNameAndRoleMaster_name(ApiRegistryType.INTERNAL.getType(), "Picking", "JDA Product Development")).thenReturn(Optional.of(registry));
		when(apiRegistryRepository.findByApiTypeAndNameAndRoleMaster_level(prdRequest.getApiType(), prdRequest.getRegName(), prdRequest.getLayer())).thenReturn(Optional.of(registry));
		when(apiMasterRepository.findByNameAndRequestEndpointAndRequestMethodAndApiRegistry_uid(prdRequest.getApiName(),
				prdRequest.getRequestEndpoint(), prdRequest.getRequestMethod(), UUID.randomUUID()))
		.thenReturn(Optional.of(apiMaster));
		JsonNode response = productAPIServiceInvoker.invokeApi(getPrdRequest(), "Picking", "JDA Product Development", authCookie, bearerToken, BffAdminConstantsUtils.WMS);
		assertEquals(1, response.get("TEST2").asInt());
	
	}
	@Test
	public void testInvokeProductApi_post() throws JsonProcessingException, URISyntaxException {
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		String bearerToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTVVBFUiIsImlhdCI6MTU4MjgxNTEyMSwiZXhwIjoxNTgzNjc5MTIxLCJhdWQiOiJiZmYifQ.bJ3QxT3aazYzm28ha91FPGY0j02zj4PgYv1-egLgTOD8hq14zEg2nkhpkyqUyO-nk8KDmiYFsStsxx3O89P9TA";
		String authorization = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTVVBFUiIsImlhdCI6MTU4MjgxNTEyMSwiZXhwIjoxNTgzNjc5MTIxLCJhdWQiOiJiZmYifQ.bJ3QxT3aazYzm28ha91FPGY0j02zj4PgYv1-egLgTOD8hq14zEg2nkhpkyqUyO-nk8KDmiYFsStsxx3O89P9TA";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", BffUtils.buildValidHeader(authorization));
		headers.setContentType(MediaType.APPLICATION_JSON);
		String payload = "{\"TEST2\":1,\"test\":[\"1\",\"1.0\",\"true\"],\"TEST3\":\"true\",\"pk\":{\"0\":\"1\",\"id\":2},\"TEST4\":2.0,\"TEST1\":\"WMD1\"}";
		HttpEntity<String> request = new HttpEntity<>(payload, headers);
		ApiRegistry registry= new ApiRegistry();
		registry.setBasePath("");
		RoleMaster roleMaster= new RoleMaster();
		roleMaster.setLevel(0);
		registry.setRoleMaster(roleMaster);
		ApiRegistry apiRegistry = new ApiRegistry();
		ApiMaster apiMasterObj = new ApiMaster();
		byte[] source = "Invalid Rule Content".getBytes();
		apiMasterObj.setRequestMethod("POST");
		apiMasterObj.setPreProcessor(source);
		apiMasterObj.setPostProcessor(source);
		apiMasterObj.setRequestEndpoint("/permissions");
		JsonNode jsonNode = new ObjectMapper().readTree("{\"TEST2\":1,\"TEST3\":\"true\",\"pk\":{\"0\":\"1\",\"id\":2},\"TEST4\":2.0,\"TEST1\":\"WMD1\"}");
		ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(jsonNode,HttpStatus.OK);
		BffCoreResponse bffCoreResponse = new BffCoreResponse();
		bffCoreResponse.setHttpStatusCode(200);
		when(bffResponse.response(any(), any(), any(), any()))
				.thenReturn(bffCoreResponse);
		when(restTemplate.exchange("http://3.13.173.174:4500/permissions?roleId=2", HttpMethod.POST, request,
				JsonNode.class)).thenReturn(responseEntity);
		when(apiMasterRepository.findById(UUID.fromString("4da0d5a3-6946-4c0e-a0e3-c209c5056925")))
				.thenReturn(Optional.of(apiMasterObj));
		when(apiRegistryRepository.findById(UUID.fromString("1d3114a7-e4b0-41b9-9357-02cc752b8a2d")))
				.thenReturn(Optional.of(apiRegistry));
		ProductApiInvokeRequest prdRequest = getPrdRequest();
		when(apiMasterRepository.findByApiRegistryAndName(registry, prdRequest.getApiName())).thenReturn(Optional.of(apiMasterObj));
		when(apiRegistryRepository.findByApiTypeAndNameAndRoleMaster_name(ApiRegistryType.INTERNAL.getType(), "Picking", "JDA Product Development")).thenReturn(Optional.of(registry));
		when(apiRegistryRepository.findByApiTypeAndNameAndRoleMaster_level(prdRequest.getApiType(), prdRequest.getRegName(), prdRequest.getLayer())).thenReturn(Optional.of(registry));
		when(apiMasterRepository.findByNameAndRequestEndpointAndRequestMethodAndApiRegistry_uid(prdRequest.getApiName(),
				prdRequest.getRequestEndpoint(), prdRequest.getRequestMethod(), UUID.randomUUID()))
		.thenReturn(Optional.of(apiMasterObj));
		prdRequest.setRequestMethod(HttpMethod.POST.toString());
		JsonNode response = productAPIServiceInvoker.invokeApi(prdRequest, "Picking", "JDA Product Development", authCookie, bearerToken, BffAdminConstantsUtils.WMS);
		assertEquals(1, response.get("TEST2").asInt());
	
	}
	
	@Test
	public void testInvokeProductApi_delete() throws JsonProcessingException {
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		String bearerToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTVVBFUiIsImlhdCI6MTU4MjgxNTEyMSwiZXhwIjoxNTgzNjc5MTIxLCJhdWQiOiJiZmYifQ.bJ3QxT3aazYzm28ha91FPGY0j02zj4PgYv1-egLgTOD8hq14zEg2nkhpkyqUyO-nk8KDmiYFsStsxx3O89P9TA";
		String authorization = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTVVBFUiIsImlhdCI6MTU4MjgxNTEyMSwiZXhwIjoxNTgzNjc5MTIxLCJhdWQiOiJiZmYifQ.bJ3QxT3aazYzm28ha91FPGY0j02zj4PgYv1-egLgTOD8hq14zEg2nkhpkyqUyO-nk8KDmiYFsStsxx3O89P9TA";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", BffUtils.buildValidHeader(authorization));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(headers);
		ApiRegistry apiRegistry = new ApiRegistry();
		apiRegistry.setBasePath("");
		RoleMaster roleMaster= new RoleMaster();
		roleMaster.setLevel(0);
		apiRegistry.setRoleMaster(roleMaster);
		ApiMaster apiMasterObj = new ApiMaster();
		byte[] source = "Invalid Rule Content".getBytes();
		apiMasterObj.setRequestMethod(HttpMethod.DELETE.toString());
		apiMasterObj.setPreProcessor(source);
		apiMasterObj.setPostProcessor(source);
		apiMasterObj.setRequestEndpoint("/permissions");
		JsonNode jsonNode = new ObjectMapper().readTree("{\"TEST2\":1,\"test\":[\"1\",\"1.0\",\"true\"],\"TEST3\":\"true\",\"pk\":{\"0\":\"1\",\"id\":2},\"TEST4\":2.0,\"TEST1\":\"WMD1\"}");
		ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(jsonNode, HttpStatus.OK);
		when(restTemplate.exchange("http://3.13.173.174:4500/permissions?roleId=2", HttpMethod.DELETE, request, JsonNode.class))
				.thenReturn(responseEntity);
		when(apiMasterRepository.findById(UUID.fromString("4da0d5a3-6946-4c0e-a0e3-c209c5056925")))
				.thenReturn(Optional.of(apiMasterObj));
		when(apiRegistryRepository.findById(UUID.fromString("1d3114a7-e4b0-41b9-9357-02cc752b8a2d")))
				.thenReturn(Optional.of(apiRegistry));
		ProductApiInvokeRequest prdRequest = getPrdRequest();
		when(apiMasterRepository.findByApiRegistryAndName(apiRegistry, prdRequest.getApiName())).thenReturn(Optional.of(apiMasterObj));
		when(apiRegistryRepository.findByApiTypeAndNameAndRoleMaster_name(ApiRegistryType.INTERNAL.getType(), "Picking", "JDA Product Development")).thenReturn(Optional.of(apiRegistry));
		when(apiRegistryRepository.findByApiTypeAndNameAndRoleMaster_level(prdRequest.getApiType(), prdRequest.getRegName(), prdRequest.getLayer())).thenReturn(Optional.of(apiRegistry));
		prdRequest.setRequestMethod(HttpMethod.POST.toString());
		JsonNode response = productAPIServiceInvoker.invokeApi(prdRequest, "Picking", "JDA Product Development", authCookie, bearerToken, BffAdminConstantsUtils.WMS);
		assertEquals(1, response.get("TEST2").asInt());
	
	}

	
	
	
	@Test
	public void testInvokeApi_ExceptionApiRegistry() {
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		String bearerToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTVVBFUiIsImlhdCI6MTU4MjgxNTEyMSwiZXhwIjoxNTgzNjc5MTIxLCJhdWQiOiJiZmYifQ.bJ3QxT3aazYzm28ha91FPGY0j02zj4PgYv1-egLgTOD8hq14zEg2nkhpkyqUyO-nk8KDmiYFsStsxx3O89P9TA";
		HttpHeaders headers = new HttpHeaders();
		headers.add(COOKIE_HEADER_NAME, BffUtils.buildValidHeader(authCookie));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(headers);
		ApiMaster apiMaster = new ApiMaster();
		byte[] source = "Invalid Rule Content".getBytes();
		apiMaster.setRequestMethod("POST");
		apiMaster.setRequestEndpoint("/permissions");
		apiMaster.setPreProcessor(source);
		apiMaster.setPostProcessor(source);
		ResponseEntity<Object> responseEntity = new ResponseEntity<>(
				BffResponseCode.PRODUCT_INVOKE_SERVICE_SUCCESS_CODE_001, HttpStatus.OK);
		BffCoreResponse bffCoreResponse = new BffCoreResponse();
		bffCoreResponse.setHttpStatusCode(200);
		when(apiRegistryRepository.findByApiTypeAndNameAndRoleMaster_name(any(), any(), any())).thenThrow(new DataBaseException("Product API invocation unsuccessful due to apiRegistry"));
		when(bffResponse.response(any(), any(), any(), any()))
				.thenReturn(bffCoreResponse);
		when(restTemplate.exchange("http://3.13.173.174:4500/permissions/1?roleId=2", HttpMethod.GET, request,
				Object.class)).thenReturn(responseEntity);
		JsonNode response = productAPIServiceInvoker.invokeApi(getPrdRequest(), "ORCHESTRATION","1", authCookie,  bearerToken, BffAdminConstantsUtils.WMS);
		assertEquals("Product API invocation unsuccessful due to apiRegistry",response.get("message").asText());
	
	}
	
	@Test
	public void testInvokeApi_ExceptionApiMaster() {
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		String bearerToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTVVBFUiIsImlhdCI6MTU4MjgxNTEyMSwiZXhwIjoxNTgzNjc5MTIxLCJhdWQiOiJiZmYifQ.bJ3QxT3aazYzm28ha91FPGY0j02zj4PgYv1-egLgTOD8hq14zEg2nkhpkyqUyO-nk8KDmiYFsStsxx3O89P9TA";
		HttpHeaders headers = new HttpHeaders();
		headers.add(COOKIE_HEADER_NAME, BffUtils.buildValidHeader(authCookie));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(headers);
		ApiRegistry apiRegistry = new ApiRegistry();
		RoleMaster roleMaster= new RoleMaster();
		roleMaster.setLevel(0);
		apiRegistry.setRoleMaster(roleMaster);
		ApiMaster apiMaster = new ApiMaster();
		byte[] source = "Invalid Rule Content".getBytes();
		apiMaster.setRequestMethod("POST");
		apiMaster.setRequestEndpoint("/permissions");
		apiMaster.setPreProcessor(source);
		apiMaster.setPostProcessor(source);
		when(apiRegistryRepository.findByApiTypeAndNameAndRoleMaster_name(ApiRegistryType.INTERNAL.getType(), "Picking", "JDA Product Development")).thenReturn(Optional.of(apiRegistry));
		when(apiMasterRepository.findByApiRegistryAndName(any(), any())).thenThrow(new DataBaseException("Product API invocation unsuccessful due to apiMaster"));
		ResponseEntity<Object> responseEntity = new ResponseEntity<>(
				BffResponseCode.PRODUCT_INVOKE_SERVICE_SUCCESS_CODE_001, HttpStatus.OK);
		BffCoreResponse bffCoreResponse = new BffCoreResponse();
		bffCoreResponse.setHttpStatusCode(200);
		when(bffResponse.response(any(), any(), any(), any()))
				.thenReturn(bffCoreResponse);
		when(restTemplate.exchange("http://3.13.173.174:4500/permissions/1?roleId=2", HttpMethod.GET, request,
				Object.class)).thenReturn(responseEntity);
		when(apiRegistryRepository.findByApiTypeAndNameAndRoleMaster_name(ApiRegistryType.INTERNAL.getType(),"ORCHESTRATION","1")).thenReturn(Optional.of(apiRegistry));
		JsonNode response = productAPIServiceInvoker.invokeApi(getPrdRequest(), "ORCHESTRATION","1", authCookie,  bearerToken, BffAdminConstantsUtils.WMS);
		assertEquals("Product API invocation unsuccessful due to apiMaster", response.get("message").asText());
	
	}
	
	@Test
	public void testGetJSONNodeValue() {
		Map<String,String> map = new HashMap<String,String>();
		map.put("TEST", "Value");
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.convertValue(map, JsonNode.class);
		String value = productAPIServiceInvoker.getJSONNodeValue(jsonNode,"/TEST");
		Assert.assertEquals("Value", value);
	}

	@Test
	public void testExecuteParallel() throws InterruptedException, ExecutionException {
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		String bearerToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTVVBFUiIsImlhdCI6MTU4MjgxNTEyMSwiZXhwIjoxNTgzNjc5MTIxLCJhdWQiOiJiZmYifQ.bJ3QxT3aazYzm28ha91FPGY0j02zj4PgYv1-egLgTOD8hq14zEg2nkhpkyqUyO-nk8KDmiYFsStsxx3O89P9TA";
		Map<String,RegistryMap> registryLayerMap = new HashMap<>();
		RegistryMap registryMap = new RegistryMap();
		registryMap.setLayer("JDA PRODUCTDEVELOPMENT");
		registryMap.setRegistryName("Picking");
		registryLayerMap.put("1", registryMap);
		ProductApiInvokeRequest prdRequest = new ProductApiInvokeRequest();
		prdRequest.setRegistry(UUID.fromString("1d3114a7-e4b0-41b9-9357-02cc752b8a2d"));
		prdRequest.setRegistryApi(UUID.fromString("4da0d5a3-6946-4c0e-a0e3-c209c5056925"));
		Parameter parameter = new Parameter();
		List<RequestParam> requestParam = new ArrayList<>();
		parameter.setPropertyName("TEST1");
		parameter.setPropertyType("string");
		RequestParam reqParam = new RequestParam();
		reqParam.setValue("WMD1");
		reqParam.setType("REQUEST BODY");
		reqParam.setParameter(parameter);
		requestParam.add(reqParam);
		prdRequest.setRequestParam(requestParam);
		Map<String, ProductApiInvokeRequest> apiInput= new HashMap<>();
		apiInput.put("1", prdRequest);
		when(apiMaster.getRequestEndpoint()).thenReturn("/permissions");
		when(apiRegistry.getBasePath()).thenReturn("");
		BffCoreResponse bffCoreResponse = new BffCoreResponse();
		bffCoreResponse.setHttpStatusCode(200);
		when(bffResponse.response(any(), any(), any(), any()))
				.thenReturn(bffCoreResponse);
		prdRequest.setRequestMethod(HttpMethod.POST.toString());
		JsonNode response = productAPIServiceInvoker.executeParallel(apiInput, authCookie, bearerToken, BffAdminConstantsUtils.WMS, registryLayerMap);
		assertEquals("Registry does not exist!", response.get("1").get("message").asText());
	
	}

	
	@Test
	public void testInvokeApiByUri() throws URISyntaxException {
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		HttpHeaders headers = new HttpHeaders();
		headers.add(COOKIE_HEADER_NAME, BffUtils.buildValidHeader(authCookie));
		headers.setContentType(MediaType.APPLICATION_JSON);

		String requestBody = "{\"TEST2\":1,\"TEST3\":\"true\",\"pk\":{\"0\":\"1\",\"id\":2},\"TEST4\":2.0,\"TEST1\":\"WMD1\"}";

		when(restTemplate.exchange(eq(new URI("https://localhost/test-api")), eq(HttpMethod.POST), any(HttpEntity.class), eq(JsonNode.class)))
				.thenAnswer(invocation -> {
					HttpEntity entity = invocation.getArgument(2, HttpEntity.class);
					Object body = entity.getBody();
					assertNotNull(body);
					return new ResponseEntity<>(new ObjectMapper().readTree(body.toString()), HttpStatus.OK);
				});

		JsonNode response = productAPIServiceInvoker.invokeApi(
				UriComponentsBuilder.fromHttpUrl("https://localhost/test-api").build().toUri(),
				HttpMethod.POST, requestBody, null, authCookie);

		assertEquals(1, response.path("TEST2").asInt());
		assertTrue(response.path("TEST3").asBoolean());
		assertEquals("WMD1", response.path("TEST1").asText());
	}

	@Test
	public void testAddParametersToInput() throws JsonProcessingException {
		ProductApiInvokeRequest req=getPrdRequest();
		String propertyName="boolean";
		String value="2";
		String type="REQUEST BODY";
		String propertyType="string";
		ProductApiInvokeRequest response = productAPIServiceInvoker.addParametersToInput(req, propertyName,value,type,propertyType);
		assertEquals("ORCHESTRATION", response.getApiName());
	
	}
	
	private List<ApiMaster> getApiMasterList() {
		List<ApiMaster> apiMasterList= new ArrayList<>();
		ApiMaster apiMaster= new ApiMaster();
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setLevel(1);
		ApiRegistry apiRegistry= new ApiRegistry();
		apiRegistry.setUid(UUID.randomUUID());
		apiRegistry.setRoleMaster(roleMaster);
		apiRegistry.setName("ORCHESTRATION");
		apiMaster.setApiRegistry(apiRegistry);
		apiMaster.setRequestEndpoint("/endpoint");
		apiMaster.setRequestMethod("getList()");
		apiMasterList.add(apiMaster);
		return apiMasterList;
	}

	private ProductApiInvokeRequest getPrdRequest() {
		ProductApiInvokeRequest prdRequest = new ProductApiInvokeRequest();
		prdRequest.setRegName("ORCHESTRATION");
		prdRequest.setApiName("ORCHESTRATION");
		prdRequest.setRegistry(UUID.fromString("1d3114a7-e4b0-41b9-9357-02cc752b8a2d"));
		prdRequest.setRegistryApi(UUID.fromString("4da0d5a3-6946-4c0e-a0e3-c209c5056925"));
		List<RequestParam> requestParam = new ArrayList<>();
		RequestParam reqParam = new RequestParam();
		reqParam.setValue("WMD1");
		reqParam.setType("REQUEST BODY");
		RequestParam reqParam1 = new RequestParam();
		reqParam1.setValue("1");
		reqParam1.setType("REQUEST BODY");
		RequestParam reqParam2 = new RequestParam();
		reqParam2.setValue("true");
		reqParam2.setType("REQUEST BODY");
		RequestParam reqParam3 = new RequestParam();
		reqParam3.setValue("2");
		reqParam3.setType("REQUEST BODY");
		RequestParam reqParam4 = new RequestParam();
		reqParam4.setValue("2");
		reqParam4.setType("REQUEST BODY");
		RequestParam reqParam5 = new RequestParam();
		reqParam5.setValue("2");
		reqParam5.setType("QUERY");
		RequestParam reqParam6 = new RequestParam();
		reqParam6.setValue("1");
		reqParam6.setType("PATH");
		RequestParam reqParam7 = new RequestParam();
		reqParam7.setValue("1");
		reqParam7.setType("REQUEST BODY");
		RequestParam reqParam8 = new RequestParam();
		reqParam8.setValue("1");
		reqParam8.setType("REQUEST BODY");
		RequestParam reqParam9 = new RequestParam();
		reqParam9.setValue("1");
		reqParam9.setType("REQUEST BODY");
		RequestParam reqParam10 = new RequestParam();
		reqParam10.setValue("1");
		reqParam10.setType("REQUEST BODY");
		RequestParam reqParam11 = new RequestParam();
		reqParam11.setValue("true");
		reqParam11.setType("REQUEST BODY");
		Parameter parameter = new Parameter();
		parameter.setPropertyName("TEST1");
		parameter.setPropertyType("string");
		Parameter parameter1 = new Parameter();
		parameter1.setPropertyName("TEST2");
		parameter1.setPropertyType("integer");
		Parameter parameter2 = new Parameter();
		parameter2.setPropertyName("TEST3");
		parameter2.setPropertyType("boolean");
		Parameter parameter3 = new Parameter();
		parameter3.setPropertyName("TEST4");
		parameter3.setPropertyType("number");
		Parameter parameter4 = new Parameter();
		parameter4.setPropertyName("pk.id");
		parameter4.setPropertyType("integer");
		Parameter parameter5 = new Parameter();
		parameter5.setPropertyName("roleId");
		parameter5.setPropertyType("integer");
		Parameter parameter6 = new Parameter();
		parameter6.setPropertyName("id");
		parameter6.setPropertyType("integer");
		Parameter parameter7 = new Parameter();
		parameter7.setPropertyName("pk[0]");
		parameter7.setPropertyType("integer");
		Parameter parameter8 = new Parameter();
		parameter8.setPropertyName("pk[1]");
		parameter8.setPropertyType("object");
		Parameter parameter9 = new Parameter();
		parameter9.setPropertyName("test[0]");
		parameter9.setPropertyType("integer");
		Parameter parameter10 = new Parameter();
		parameter10.setPropertyName("test[1]");
		parameter10.setPropertyType("number");
		Parameter parameter11 = new Parameter();
		parameter11.setPropertyName("test[2]");
		parameter11.setPropertyType("boolean");
		reqParam.setParameter(parameter);
		reqParam1.setParameter(parameter1);
		reqParam2.setParameter(parameter2);
		reqParam3.setParameter(parameter3);
		reqParam4.setParameter(parameter4);
		reqParam5.setParameter(parameter5);
		reqParam6.setParameter(parameter6);
		reqParam7.setParameter(parameter7);
		reqParam8.setParameter(parameter8);
		reqParam9.setParameter(parameter9);
		reqParam10.setParameter(parameter10);
		reqParam11.setParameter(parameter11);
		requestParam.add(reqParam);
		requestParam.add(reqParam1);
		requestParam.add(reqParam2);
		requestParam.add(reqParam3);
		requestParam.add(reqParam4);
		requestParam.add(reqParam5);
		requestParam.add(reqParam6);
		requestParam.add(reqParam7);
		requestParam.add(reqParam8);
		requestParam.add(reqParam9);
		requestParam.add(reqParam10);
		requestParam.add(reqParam11);
		prdRequest.setRequestParam(requestParam);
		prdRequest.setRequestMethod(HttpMethod.GET.toString());
		return prdRequest;
	}
}