/**
 * 
 */
package com.jda.mobility.framework.extensions.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.dto.AppConfigDto;
import com.jda.mobility.framework.extensions.dto.AppSettingsDto;
import com.jda.mobility.framework.extensions.dto.DefaultHomeFlowDto;
import com.jda.mobility.framework.extensions.dto.FlowDto;
import com.jda.mobility.framework.extensions.entity.ProductMaster;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.FlowRequest;
import com.jda.mobility.framework.extensions.model.Layer;
import com.jda.mobility.framework.extensions.model.PrepRequest;
import com.jda.mobility.framework.extensions.repository.ProductMasterRepository;
import com.jda.mobility.framework.extensions.service.impl.ProductPrepareServiceImpl;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The class ProductPrepControllerTest.java
 * 
 * @author V.Rama HCL Technologies Ltd.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("unittest")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser
public class ProductPrepControllerTest {

	private static final String PRODUCT_PREPARE_URL = "/api/product/v1";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductPrepareServiceImpl productPrepareService;
	
	@MockBean
	private ProductMasterRepository productMasterRepo;

	@MockBean
	private RestTemplate restTemplate;

	/*
	 * @Spy public BffResponse bffResponse = new BffResponse();
	 */
	@MockBean
	public SessionDetails sessionDetails;

	/**
	 * Test method for createDefaultFlow
	 */
	@Test
	public void testCreateDefaultFlow() throws Exception {
		String configName = "CONTEXT";
		List<AppConfigDto> appConfigDtoList = new ArrayList<>();
		appConfigDtoList.add(createAppConfig());
		StringBuilder url = new StringBuilder(PRODUCT_PREPARE_URL).append("/defaultflow/").append(configName);
		when(productPrepareService.fetchDefaultFlowConfig(configName)).thenReturn(buildResponse(appConfigDtoList));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].configName").value("CONTEXT"));
	}

	/**
	 * Test method for fetchDashboardFlows
	 */
	@Test
	public void testFetchDashboardFlows() throws Exception {
		List<FlowDto> flowDtoList = new ArrayList<>();
		flowDtoList.add(createFlow());
		StringBuilder url = new StringBuilder(PRODUCT_PREPARE_URL).append("/dashboardflows");
		when(productPrepareService.fetchDashboardFlows(Mockito.any())).thenReturn(buildResponse(flowDtoList));
		mockMvc.perform(MockMvcRequestBuilders.post(url.toString()).content(asJsonString(createPrepRequest()))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].name").value("pick"));
	}

	/**
	 * Test method for fetchProductConfigId
	 */
	@Test
	public void testFetchProductConfigId() throws Exception {
		StringBuilder url = new StringBuilder(PRODUCT_PREPARE_URL).append("/productconfigid");
		MockHttpSession session = new MockHttpSession(null, "test-session-id");
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setSession(session);
	    request.setRemoteAddr("1.2.3.4");
		//when(request.getSession(false)).thenReturn(new MockHttpSession(null, "test-session-id"));
		when(productPrepareService.fetchProductConfigId(Mockito.any())).thenReturn(buildResponse(createFlowRequest()));
		mockMvc.perform(MockMvcRequestBuilders.post(url.toString())
				.content(asJsonString(createPrepRequest()))
				.contentType(MediaType.APPLICATION_JSON)
				.with(request1 -> {
				   request1.setRemoteAddr("12345");
				   request1.setSession(session);
				   return request1;
				})
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("pick"));
	}

	@Test
	public void testGetDefaultHomeFlow() throws Exception {
		StringBuilder url = new StringBuilder(PRODUCT_PREPARE_URL).append("/defhomeflow");
		when(productPrepareService.getDefaultHomeFlow()).thenReturn(buildResponse(getDefaultHomeFlowDto()));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.defaultFlowName").value("testFlowName"));
	}

	@Test
	public void testGetWarehouseList() throws Exception {
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		StringBuilder url = new StringBuilder(PRODUCT_PREPARE_URL).append("/warehouse");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", BffUtils.buildValidHeader(authCookie));
		headers.setContentType(MediaType.APPLICATION_JSON);
		when(productMasterRepo.findByName(Mockito.any())).thenReturn(getProductMaster());
		when(sessionDetails.getPrincipalName()).thenReturn("SUPER");
		HttpEntity<String> request = new HttpEntity<>(headers);
		when(restTemplate.exchange("http://3.13.173.174:4500/api/user/v1beta/users/SUPER/warehouses", HttpMethod.GET,
				request, Object.class))
						.thenReturn(new ResponseEntity<>("SOURCE_A", HttpStatus.OK));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).header("SET_COOKIE", authCookie))
				.andExpect(status().isOk());
	}

	/**
	 * Test method for fetchDashboardFlows
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetWarehouseListException() throws Exception {
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		StringBuilder url = new StringBuilder(PRODUCT_PREPARE_URL).append("/warehouse");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", BffUtils.buildValidHeader(authCookie));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(headers);
		when(sessionDetails.getPrincipalName()).thenReturn("SUPER");
		when(restTemplate.exchange("http://3.13.173.174:4500/api/user/v1beta/users/SUPER/warehouses", HttpMethod.GET,
				request, Object.class)).thenThrow(new RuntimeException());
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).header("SET_COOKIE", authCookie))
				.andExpect(status().is5xxServerError());
	}

	@Test
	public void testGetWarehouseListRestClientException() throws Exception {
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		StringBuilder url = new StringBuilder(PRODUCT_PREPARE_URL).append("/warehouse");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cookie", BffUtils.buildValidHeader(authCookie));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(headers);
		when(productMasterRepo.findByName(Mockito.any())).thenReturn(getProductMaster());
		when(sessionDetails.getPrincipalName()).thenReturn("SUPER");
		when(restTemplate.exchange("http://3.13.173.174:4500/api/user/v1beta/users/SUPER/warehouses", HttpMethod.GET,
				request, Object.class)).thenThrow(new RestClientException(""));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).header("SET_COOKIE", authCookie))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void testGetAppSettings() throws Exception {
		AppSettingsDto appSettingsDto = new AppSettingsDto();
		appSettingsDto.setAppConfigs(new ArrayList<>());
		appSettingsDto.setDefaultHomeFlow(getDefaultHomeFlowDto());
		StringBuilder url = new StringBuilder(PRODUCT_PREPARE_URL).append("/appsettings");
		when(productPrepareService.getAppSettings("recoveredDeviceId")).thenReturn(buildResponse(appSettingsDto));
		mockMvc.perform(MockMvcRequestBuilders
				.get(url.toString())
				.param("recoveredDeviceId", "recoveredDeviceId")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}

	private <T> BffCoreResponse buildResponse(T data) {
		return BffUtils.buildResponse(data, BffResponseCode.FLOW_SUCCESS_CODE_CREATE_FLOW.getCode(), "message",
				"detailMessage", StatusCode.OK.getValue());
	}

	private PrepRequest createPrepRequest() {
		PrepRequest prepRequest = new PrepRequest();
		prepRequest.setFlowRo(createFlowRequest());
		prepRequest.setIsDefaultWarehouse(true);
		prepRequest.setName("testName");
		prepRequest.setPropValue("testPropValue");
		prepRequest.setVersion(0);

		return prepRequest;
	}

	private FlowRequest createFlowRequest() {
		FlowRequest flowRequest = new FlowRequest();
		flowRequest.setName("pick");
		flowRequest.setDefaultFormId(UUID.randomUUID());
		flowRequest.setDisabled(true);
		return flowRequest;
	}

	private String asJsonString(final Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
	}

	private FlowDto createFlow() {
		List<String> permissions = new ArrayList<>();
		permissions.add("pick flow");
		Layer layer = new Layer();
		layer.setLevel(1);
		layer.setName("service");
		FlowDto flowDto = new FlowDto.FlowBuilder("pick").setFlowId(UUID.randomUUID())
				.setDefaultFormId(UUID.randomUUID()).setDescription("description").setDisabled(false)
				.setExtDisabled(false).setPublished(true).setTag("flow tag").setTabbedForm(true)
				.setExtendedFromFlowId(UUID.randomUUID()).setExtendedFromFlowName("extended form name").setLayer(layer)
				.setVersion(1L).setPermissions(permissions).build();
		return flowDto;
	}

	private AppConfigDto createAppConfig() {

		return AppConfigDto.builder().configName("CONTEXT").configValue("test").appConfigId(UUID.randomUUID())
				.configType("CONTEXT").description("DESC").build();
	}

	private DefaultHomeFlowDto getDefaultHomeFlowDto() {
		DefaultHomeFlowDto defaultHomeFlowDto = new DefaultHomeFlowDto();
		defaultHomeFlowDto.setDefaultFlowdefFormId(UUID.randomUUID().toString());
		defaultHomeFlowDto.setDefaultFlowId(UUID.randomUUID());
		defaultHomeFlowDto.setDefaultFlowName("testFlowName");
		defaultHomeFlowDto.setDefaultFormTabbed(true);
		defaultHomeFlowDto.setHomeFlowDefFormId(UUID.randomUUID().toString());
		defaultHomeFlowDto.setHomeFlowId(UUID.randomUUID());
		defaultHomeFlowDto.setHomeFlowName("testHomeFlowName");
		defaultHomeFlowDto.setHomeFormTabbed(true);
		return defaultHomeFlowDto;
	}
	private ProductMaster getProductMaster(){
		ProductMaster productMaster = new ProductMaster();
		productMaster.setScheme("http");
		productMaster.setContextPath("3.13.173.174");
		productMaster.setPort("4500");
		return productMaster;
	}
}
