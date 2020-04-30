package com.jda.mobility.framework.extensions.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.ApiMaster;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.Parameter;
import com.jda.mobility.framework.extensions.model.ProdApiWrkMemRequest;
import com.jda.mobility.framework.extensions.model.ProductApiInvokeRequest;
import com.jda.mobility.framework.extensions.model.ProductApiRawInvokeRequest;
import com.jda.mobility.framework.extensions.model.RequestParam;
import com.jda.mobility.framework.extensions.repository.ApiMasterRepository;
import com.jda.mobility.framework.extensions.repository.ApiRegistryRepository;
import com.jda.mobility.framework.extensions.service.OrchestrationService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ApiRegistryType;
import com.jda.mobility.framework.extensions.utils.BffUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("unittest")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser
public class ProductApiInvokeControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ApiRegistryRepository apiRegistryRepository;
	@MockBean
	private ApiMasterRepository apiMasterRepository;
	@MockBean
	private RestTemplate restTemplate;
	@MockBean
	private OrchestrationService orchestrationService;

	@Test
	public void testInvokeProductApi() throws Exception {
		String url="http://3.13.173.174:4500/api/user/v1beta/permissions?roleId=2";	
		URI myURI = new URI(url);
		ProductApiInvokeRequest prdRequest = getPrdRequest();
		ApiRegistry apiRegistry = new ApiRegistry();
		ApiMaster apiMaster = new ApiMaster();
		byte[] source = "Include Libraries  Step \"OrchExecution\" 	Priority 3 	Orchestration \"POSTMODIFICATION\" 	when  		Get Inputs 	then 		Say \"***Pre-processor   Execution  Begins Now****\" 	 end".getBytes();
		apiMaster.setRequestMethod("POST");
		apiMaster.setRequestEndpoint("/permissions");
		apiMaster.setPreProcessor(source);
		apiMaster.setPostProcessor(source);
		apiRegistry.setUid(getPrdRequest().getRegistryApi());
		apiRegistry.setApiType("WMS");
		apiRegistry.setBasePath("/api/user/v1beta");
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", BffUtils.buildValidHeader(authCookie));
		headers.setContentType(MediaType.APPLICATION_JSON);
		when(apiRegistryRepository.findByApiTypeAndNameAndRoleMaster_level(prdRequest.getApiType(), prdRequest.getRegName(), prdRequest.getLayer())).thenReturn(Optional.of(apiRegistry));
		when(apiMasterRepository.findByNameAndRequestEndpointAndRequestMethodAndApiRegistry_uid(prdRequest.getApiName(),
				prdRequest.getRequestEndpoint(), prdRequest.getRequestMethod(), apiRegistry.getUid())).thenReturn(Optional.of(apiMaster));
		when(apiRegistryRepository.findById(Mockito.any())).thenReturn(Optional.of(apiRegistry));
		when(apiMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(apiMaster));
		JsonNode jsonNode = new ObjectMapper().readTree("{\"TEST2\":1,\"test\":[false,false,true],\"TEST3\":true,\"pk\":{\"0\":\"1\",\"id\":2},\"TEST4\":2.0,\"TEST1\":\"WMD1\"}");
		String payload = "{\"TEST2\":1,\"pk\":{\"0\":\"1\",\"1\":\"1\",\"id\":2},\"test\":[false,false,true],\"TEST3\":true,\"TEST4\":2.0,\"TEST1\":\"WMD1\"}";
		ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(jsonNode,HttpStatus.OK);
		when(restTemplate.exchange(myURI, HttpMethod.POST, new HttpEntity<>(payload,headers),
				JsonNode.class)).thenReturn(responseEntity);
		when(orchestrationService.getRuleContent(Mockito.any(), Mockito.any())).thenReturn(source);
		when(orchestrationService.buildOrchestrationPreProcessor(Mockito.any(), Mockito.anyInt())).thenReturn(new ProdApiWrkMemRequest());
		prdRequest.setRequestMethod(HttpMethod.POST.toString());
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/product/v1")
				.content(asJsonString(getPrdRequest()))
				.header("SET_COOKIE", "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3")
				.contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isOk());
	}
	
	@Test
	public void testInvokeProductApi_EXTERNAL() throws Exception {
		String url="http://3.13.173.174:4500/api/user/v1beta/permissions?roleId=2";	
		URI myURI = new URI(url);
		ProductApiInvokeRequest prdRequest = getPrdRequest();
		ApiRegistry apiRegistry = new ApiRegistry();
		ApiMaster apiMaster = new ApiMaster();
		byte[] source = "Include Libraries  Step \"OrchExecution\" 	Priority 3 	Orchestration \"POSTMODIFICATION\" 	when  		Get Inputs 	then 		Say \"***Pre-processor   Execution  Begins Now****\" 	 end".getBytes();
		apiMaster.setRequestMethod("POST");
		apiMaster.setRequestEndpoint("/permissions");
		apiMaster.setPreProcessor(source);
		apiMaster.setPostProcessor(source);
		apiRegistry.setSchemeList(" http,https");
		apiRegistry.setUid(getPrdRequest().getRegistryApi());
		apiRegistry.setApiType("EXTERNAL");
		apiRegistry.setBasePath("/api/user/v1beta");
		apiRegistry.setPort("4500");
		apiRegistry.setContextPath("3.13.173.174");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		when(apiRegistryRepository.findByApiTypeAndNameAndRoleMaster_level(prdRequest.getApiType(), prdRequest.getRegName(), prdRequest.getLayer())).thenReturn(Optional.of(apiRegistry));
		when(apiMasterRepository.findByNameAndRequestEndpointAndRequestMethodAndApiRegistry_uid(prdRequest.getApiName(),
				prdRequest.getRequestEndpoint(), prdRequest.getRequestMethod(), apiRegistry.getUid())).thenReturn(Optional.of(apiMaster));
		when(apiRegistryRepository.findById(Mockito.any())).thenReturn(Optional.of(apiRegistry));
		when(apiMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(apiMaster));
		JsonNode jsonNode = new ObjectMapper().readTree("{\"TEST2\":1,\"pk\":{\"0\":\"1\",\"1\":\"1\",\"id\":2},\"test\":[false,false,true],\"TEST3\":true,\"TEST4\":2.0,\"TEST1\":\"WMD1\"}");
		String payload = "{\"TEST2\":1,\"pk\":{\"0\":\"1\",\"1\":\"1\",\"id\":2},\"test\":[false,false,true],\"TEST3\":true,\"TEST4\":2.0,\"TEST1\":\"WMD1\"}";
		ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(jsonNode,HttpStatus.OK);
		when(restTemplate.exchange(myURI, HttpMethod.POST, new HttpEntity<>(payload,headers),
				JsonNode.class)).thenReturn(responseEntity);
		ProdApiWrkMemRequest prodApiWrkMemRequest= new ProdApiWrkMemRequest();
		prodApiWrkMemRequest.setApiResponse(jsonNode);
		when(orchestrationService.getRuleContent(Mockito.any(), Mockito.any())).thenReturn(source);
		when(orchestrationService.buildOrchestrationPreProcessor(Mockito.any(), Mockito.anyInt())).thenReturn(prodApiWrkMemRequest);
		prdRequest.setRequestMethod(HttpMethod.POST.toString());
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/product/v1")
				.content(asJsonString(getPrdRequest()))
				.header("SET_COOKIE", "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3")
				.contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isOk());
	}
	
	
	@Test
	public void testInvokeOrchestrationApi() throws Exception {
		
		ProductApiInvokeRequest prdRequest = getPrdRequest();
		ApiRegistry apiRegistry = new ApiRegistry();
		RoleMaster roleMaster= new RoleMaster();
		roleMaster.setName("ORCHESTRATION");
		ApiMaster apiMaster = new ApiMaster();
		byte[] source = "Include Libraries  Step \"OrchExecution\" 	Priority 3 	Orchestration \"POSTMODIFICATION\" 	when  		Get Inputs 	then 		Say \"***Pre-processor   Execution  Begins Now****\" 	 end".getBytes();
		apiMaster.setRequestMethod("POST");
		apiMaster.setRequestEndpoint("/permissions");
		apiMaster.setPreProcessor(source);
		apiMaster.setPostProcessor(source);
		apiMaster.setOrchestrationName("ORCHESTRATION");
		apiRegistry.setUid(getPrdRequest().getRegistryApi());
		apiRegistry.setApiType("ORCHESTRATION");
		apiRegistry.setBasePath("/api/user/v1beta");
		apiRegistry.setRoleMaster(roleMaster);
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", BffUtils.buildValidHeader(authCookie));
		headers.setContentType(MediaType.APPLICATION_JSON);
		when(apiRegistryRepository.findByApiTypeAndNameAndRoleMaster_level(prdRequest.getApiType(), prdRequest.getRegName(), prdRequest.getLayer())).thenReturn(Optional.of(apiRegistry));
		when(apiMasterRepository.findByNameAndRequestEndpointAndRequestMethodAndApiRegistry_uid(prdRequest.getApiName(),
				prdRequest.getRequestEndpoint(), prdRequest.getRequestMethod(), apiRegistry.getUid())).thenReturn(Optional.of(apiMaster));
		when(apiRegistryRepository.findById(Mockito.any())).thenReturn(Optional.of(apiRegistry));
		when(apiMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(apiMaster));
		JsonNode jsonNode = new ObjectMapper().readTree("{\"TEST2\":1,\"test\":[false,false,true],\"TEST3\":true,\"pk\":{\"0\":\"1\",\"id\":2},\"TEST4\":2.0,\"TEST1\":\"WMD1\"}");
		String payload = "{\"TEST2\":1,\"test\":[false,false,true],\"TEST3\":true,\"pk\":{\"0\":\"1\",\"id\":2},\"TEST4\":2.0,\"TEST1\":\"WMD1\"}";
		ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(jsonNode,HttpStatus.OK);
		when(restTemplate.exchange("http://3.13.173.174:4500/api/user/v1beta/permissions?roleId=2", HttpMethod.POST, new HttpEntity<>(payload,headers),
				JsonNode.class)).thenReturn(responseEntity);
		when(orchestrationService.getRuleContent(Mockito.any(), Mockito.any())).thenReturn(source);
		ProdApiWrkMemRequest prodApiWrkMemRequest= new ProdApiWrkMemRequest();
		prodApiWrkMemRequest.setApiResponse(jsonNode);
		when(orchestrationService.buildOrchestrationPreProcessor(Mockito.any(), Mockito.anyInt())).thenReturn(prodApiWrkMemRequest);
		prdRequest.setRequestMethod(HttpMethod.POST.toString());
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/product/v1")
				.content(asJsonString(getPrdRequest()))
				.header("SET_COOKIE", "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3")
				.contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	public void testInvokeProductApi_exception() throws Exception {
		
		ProductApiInvokeRequest prdRequest = getPrdRequest();
		ApiRegistry apiRegistry = new ApiRegistry();
		ApiMaster apiMaster = new ApiMaster();
		byte[] source = "Invalid Rule Content".getBytes();
		apiMaster.setName("getPermissions");
		apiMaster.setRequestMethod("POST");
		apiMaster.setRequestEndpoint("/permissions");
		apiMaster.setPreProcessor(source);
		apiMaster.setPostProcessor(source);
		apiRegistry.setName("User");
		apiRegistry.setApiType(BffAdminConstantsUtils.WMS);
		apiRegistry.setBasePath("/api/user/v1beta");
		when(apiRegistryRepository.findByApiTypeAndNameAndRoleMaster_level(prdRequest.getApiType(), prdRequest.getRegName(), prdRequest.getLayer())).thenReturn(Optional.of(apiRegistry));
		prdRequest.setRequestMethod(HttpMethod.POST.toString());
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/product/v1")
				.content(asJsonString(getPrdRequest()))
				.header("SET_COOKIE", "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3")
				.contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isBadRequest());
	}
	
	
	@Test
	public void testInvokeProductApi_internalException() throws Exception {
		
		ProductApiInvokeRequest prdRequest = getPrdRequest();
		ApiRegistry apiRegistry = new ApiRegistry();
		ApiMaster apiMaster = new ApiMaster();
		byte[] source = "Invalid Rule Content".getBytes();
		apiMaster.setRequestMethod("POST");
		apiMaster.setRequestEndpoint("/permissions");
		apiMaster.setPreProcessor(source);
		apiMaster.setPostProcessor(source);
		apiRegistry.setUid(getPrdRequest().getRegistryApi());
		apiRegistry.setApiType(BffAdminConstantsUtils.WMS);
		apiRegistry.setBasePath("/api/user/v1beta");
		when(apiRegistryRepository.findByApiTypeAndNameAndRoleMaster_level(prdRequest.getApiType(), prdRequest.getRegName(), prdRequest.getLayer())).thenReturn(Optional.of(apiRegistry));
		when(apiMasterRepository.findByNameAndRequestEndpointAndRequestMethodAndApiRegistry_uid(prdRequest.getApiName(),
				prdRequest.getRequestEndpoint(), prdRequest.getRequestMethod(), apiRegistry.getUid())).thenThrow(new NullPointerException(BffAdminConstantsUtils.EMPTY_SPACES));
		when(apiRegistryRepository.findById(Mockito.any())).thenReturn(Optional.of(apiRegistry));
		when(apiMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(apiMaster));
		when(orchestrationService.getRuleContent(Mockito.any(), Mockito.any())).thenReturn(source);
		when(orchestrationService.buildOrchestrationPreProcessor(Mockito.any(), Mockito.anyInt())).thenReturn(new ProdApiWrkMemRequest());
		prdRequest.setRequestMethod(HttpMethod.POST.toString());
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/product/v1")
				.content(asJsonString(getPrdRequest()))
				.header("SET_COOKIE", "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3")
				.contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isInternalServerError());
	}
	
	@Test
	public void testInvokeProductApiLocalDefault() throws Exception {
		
		ProductApiInvokeRequest prdRequest = getPrdRequest();
		ApiRegistry apiRegistry = new ApiRegistry();
		ApiMaster apiMaster = new ApiMaster();
		apiRegistry.setUid(getPrdRequest().getRegistryApi());
		apiMaster.setName("other");
		apiMaster.setRequestMethod("POST");
		apiMaster.setRequestEndpoint("/permissions");
		apiRegistry.setName("MobileTesting");
		apiRegistry.setApiType("LOCAL");
		apiRegistry.setBasePath("/api/user/v1beta");
		when(apiRegistryRepository.findByApiTypeAndNameAndRoleMaster_level(prdRequest.getApiType(), prdRequest.getRegName(), prdRequest.getLayer())).thenReturn(Optional.of(apiRegistry));
		when(apiMasterRepository.findByNameAndRequestEndpointAndRequestMethodAndApiRegistry_uid(prdRequest.getApiName(),
				prdRequest.getRequestEndpoint(), prdRequest.getRequestMethod(), apiRegistry.getUid())).thenReturn(Optional.of(apiMaster));
		when(apiRegistryRepository.findById(Mockito.any())).thenReturn(Optional.of(apiRegistry));
		when(apiMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(apiMaster));
		prdRequest.setRequestMethod(HttpMethod.POST.toString());
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/product/v1")
				.content(asJsonString(getPrdRequest()))
				.header("SET_COOKIE", "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
	@Test
	public void testInvokeProductApiLocal_Else() throws Exception {
		String url="http://3.13.173.174:4500/api/user/v1beta/permissions?roleId=2";	
		URI myURI = new URI(url);
		ProductApiInvokeRequest prdRequest = getPrdRequest();
		ApiRegistry apiRegistry = new ApiRegistry();
		ApiMaster apiMaster = new ApiMaster();
		apiRegistry.setUid(getPrdRequest().getRegistryApi());
		apiMaster.setName("other");
		apiMaster.setRequestMethod("POST");
		apiMaster.setRequestEndpoint("/permissions");
		apiRegistry.setName("Error");
		apiRegistry.setApiType("LOCAL");
		apiRegistry.setBasePath("/api/user/v1beta");
		String set_cookie="MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", BffUtils.buildValidHeader(authCookie));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("SET_COOKIE", BffUtils.buildValidHeader(set_cookie));
		headers.add("Authorization", BffUtils.buildValidHeader(null));
		headers.add("X-Auth-Token", BffUtils.buildValidHeader(null));
		when(apiRegistryRepository.findByApiTypeAndNameAndRoleMaster_level(prdRequest.getApiType(), prdRequest.getRegName(), prdRequest.getLayer())).thenReturn(Optional.of(apiRegistry));
		when(apiMasterRepository.findByNameAndRequestEndpointAndRequestMethodAndApiRegistry_uid(prdRequest.getApiName(),
				prdRequest.getRequestEndpoint(), prdRequest.getRequestMethod(), apiRegistry.getUid())).thenReturn(Optional.of(apiMaster));
		when(apiRegistryRepository.findById(Mockito.any())).thenReturn(Optional.of(apiRegistry));
		when(apiMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(apiMaster));
		prdRequest.setRequestMethod(HttpMethod.POST.toString());
		JsonNode jsonNode = new ObjectMapper().readTree("{\"TEST2\":1,\"test\":[false,false,true],\"TEST3\":true,\"pk\":{\"0\":\"1\",\"id\":2},\"TEST4\":2.0,\"TEST1\":\"WMD1\"}");
		String payload = "{\"TEST2\":1,\"pk\":{\"0\":\"1\",\"1\":\"1\",\"id\":2},\"test\":[false,false,true],\"TEST3\":true,\"TEST4\":2.0,\"TEST1\":\"WMD1\"}";
		ResponseEntity<Object> responseEntity = new ResponseEntity<>(jsonNode,HttpStatus.OK);
		when(restTemplate.exchange(myURI, HttpMethod.POST, new HttpEntity<>(payload,headers),
				Object.class)).thenReturn(responseEntity);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/product/v1")
				.content(asJsonString(getPrdRequest()))
				.header("SET_COOKIE", "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
	
	@Test
	public void testInvokeProductApiGet() throws Exception {
		ProductApiInvokeRequest prdRequest = getPrdRequestPost();
		prdRequest.setRequestMethod(HttpMethod.GET.toString());
		prdRequest.setApiName("ORCHESTRATION");
		ApiRegistry apiRegistry = new ApiRegistry();
		apiRegistry.setBasePath("/api/user/v1beta");
		apiRegistry.setUid(getPrdRequestPost().getRegistryApi());
		apiRegistry.setApiType(ApiRegistryType.ORCHESTRATION.toString());
		RoleMaster roleMaster= new RoleMaster();
		roleMaster.setLevel(0);
		apiRegistry.setRoleMaster(roleMaster);
		ApiMaster apiMaster = new ApiMaster();
		apiMaster.setName("ORCHESTRATION");
		apiMaster.setOrchestrationName("ORCHESTRATION");
		apiMaster.setRequestMethod(HttpMethod.GET.toString());
		apiMaster.setRequestEndpoint("/permissions");
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", BffUtils.buildValidHeader(authCookie));
		headers.setContentType(MediaType.APPLICATION_JSON);
		JsonNode jsonNode = new ObjectMapper().readTree("{\"TEST2\":1,\"TEST3\":\"true\",\"pk\":{\"0\":\"1\",\"id\":2},\"TEST4\":2.0,\"TEST1\":\"WMD1\"}");
		ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(jsonNode, HttpStatus.OK);
		BffCoreResponse bffCoreResponse = new BffCoreResponse();
		bffCoreResponse.setHttpStatusCode(200);
		when(apiRegistryRepository.findByApiTypeAndNameAndRoleMaster_level(prdRequest.getApiType(), prdRequest.getRegName(), prdRequest.getLayer())).thenReturn(Optional.of(apiRegistry));
		when(apiMasterRepository.findByNameAndRequestEndpointAndRequestMethodAndApiRegistry_uid(prdRequest.getApiName(),
				prdRequest.getRequestEndpoint(), prdRequest.getRequestMethod(), apiRegistry.getUid())).thenReturn(Optional.of(apiMaster));
		when(restTemplate.exchange("http://3.13.173.174:4500/api/user/v1beta/permissions", HttpMethod.GET, new HttpEntity<>(null,headers),
				JsonNode.class)).thenReturn(responseEntity);
		when(apiMasterRepository.findById(UUID.fromString("4da0d5a3-6946-4c0e-a0e3-c209c5056925")))
				.thenReturn(Optional.of(apiMaster));
		when(apiRegistryRepository.findById(UUID.fromString("1d3114a7-e4b0-41b9-9357-02cc752b8a2d")))
				.thenReturn(Optional.of(apiRegistry));
		when(orchestrationService.getRuleContent(Mockito.any(), Mockito.any())).thenReturn("Invalid Rule Content".getBytes());
		ProdApiWrkMemRequest prodApiWrkMemRequest = new ProdApiWrkMemRequest();
		prodApiWrkMemRequest.setApiResponse(jsonNode);
		when(orchestrationService.buildOrchestrationPreProcessor(Mockito.any(), Mockito.anyInt())).thenReturn(prodApiWrkMemRequest);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/product/v1")
				.content(asJsonString(getPrdRequestPost()))
				.header("SET_COOKIE", authCookie)
				.contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isOk());
	}
	
	
	@Test
	public void testInvokeProductApiRaw() throws Exception {
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", BffUtils.buildValidHeader(authCookie));
		headers.setContentType(MediaType.APPLICATION_JSON);
		JsonNode jsonNode = new ObjectMapper().readTree("{\"TEST2\":1,\"TEST3\":\"true\",\"pk\":{\"0\":\"1\",\"id\":2},\"TEST4\":2.0,\"TEST1\":\"WMD1\"}");
		String apiPayload= "{\"TEST2\":1,\"TEST3\":\"true\",\"pk\":{\"0\":\"1\",\"id\":2},\"TEST4\":2.0,\"TEST1\":\"WMD1\"}";
		ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(jsonNode, HttpStatus.OK);
		BffCoreResponse bffCoreResponse = new BffCoreResponse();
		bffCoreResponse.setHttpStatusCode(200);
		when(restTemplate.exchange("http://3.13.173.174:4500", HttpMethod.POST, new HttpEntity<>(apiPayload,headers),
				JsonNode.class)).thenReturn(responseEntity);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/product/v1/raw")
				.content(asJsonString(postRawInvokeRequest()))
				.header("SET_COOKIE", authCookie)
				.contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isOk());
	}
	
	@Test
	public void testInvokeProductApiRaw_Exception() throws Exception {
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", BffUtils.buildValidHeader(authCookie));
		headers.setContentType(MediaType.APPLICATION_JSON);
		JsonNode jsonNode = new ObjectMapper().readTree("{\"TEST2\":1,\"TEST3\":\"true\",\"pk\":{\"0\":\"1\",\"id\":2},\"TEST4\":2.0,\"TEST1\":\"WMD1\"}");
		String apiPayload= "{\"TEST2\":1,\"TEST3\":\"true\",\"pk\":{\"0\":\"1\",\"id\":2},\"TEST4\":2.0,\"TEST1\":\"WMD1\"}";
		ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(jsonNode, HttpStatus.OK);
		BffCoreResponse bffCoreResponse = new BffCoreResponse();
		bffCoreResponse.setHttpStatusCode(200);
		when(restTemplate.exchange("http://3.13.173.174:4500", HttpMethod.POST, new HttpEntity<>(apiPayload,headers),
				JsonNode.class)).thenReturn(responseEntity);
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/product/v1/raw")
				.content(asJsonString(rawInvokeRequest()))
				.header("SET_COOKIE", authCookie)
				.contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isInternalServerError());
	}
	
	
	@Test
	public void testInvokeProductApiException() throws Exception {
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		when(apiRegistryRepository.findById(Mockito.any()))
				.thenThrow(new RuntimeException());
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/product/v1")
				.content(asJsonString(getPrdRequestPost()))
				.header("SET_COOKIE", authCookie)
				.contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isBadRequest());
	}

	private ProductApiInvokeRequest getPrdRequestPost() {
		ProductApiInvokeRequest prdRequest = new ProductApiInvokeRequest();
		prdRequest.setRegistry(UUID.fromString("1d3114a7-e4b0-41b9-9357-02cc752b8a2d"));
		prdRequest.setRegistryApi(UUID.fromString("4da0d5a3-6946-4c0e-a0e3-c209c5056925"));
		prdRequest.setRegName("ORCHESTRATION");
		prdRequest.setApiType("ORCHESTRATION");
		prdRequest.setBasePath("/api/user/v1beta");
		prdRequest.setApiName("ORCHESTRATION");
		prdRequest.setRequestEndpoint("/permissions");
		List<RequestParam> requestParam = new ArrayList<>();
		RequestParam reqParam = new RequestParam();
		reqParam.setType("REQUEST BODY");
		Parameter parameter = new Parameter();
		parameter.setPropertyName("TEST");
		parameter.setPropertyType("TEST");
		reqParam.setParameter(parameter);
		requestParam.add(reqParam);
		prdRequest.setRequestParam(requestParam);
		prdRequest.setRequestMethod(HttpMethod.GET.toString());
		return prdRequest;

	}
	
	private ProductApiRawInvokeRequest postRawInvokeRequest() {
		ProductApiRawInvokeRequest prdRequest = new ProductApiRawInvokeRequest();
		prdRequest.setApiPayload("{\"TEST2\":1,\"TEST3\":\"true\",\"pk\":{\"0\":\"1\",\"id\":2},\"TEST4\":2.0,\"TEST1\":\"WMD1\"}");
		
		return prdRequest;

	}
	
	private ProductApiRawInvokeRequest rawInvokeRequest() {
		return new ProductApiRawInvokeRequest();
	}

	/**
	 * @return ProductApiInvokeRequest
	 */
	private ProductApiInvokeRequest getPrdRequest() {
		ProductApiInvokeRequest prdRequest = new ProductApiInvokeRequest();
		prdRequest.setRegistry(UUID.fromString("1d3114a7-e4b0-41b9-9357-02cc752b8a2d"));
		prdRequest.setRegistryApi(UUID.fromString("4da0d5a3-6946-4c0e-a0e3-c209c5056925"));
		prdRequest.setRegName("User");
		prdRequest.setApiName("getPermissions");
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
		prdRequest.setRequestMethod(HttpMethod.POST.toString());
		return prdRequest;

	}
	
	
	private String asJsonString(final Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
	}
	
}
