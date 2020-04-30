
package com.jda.mobility.framework.extensions.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.dto.AppConfigDto;
import com.jda.mobility.framework.extensions.entity.AppConfigDetail;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.model.AppConfigRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.service.impl.AppConfigServiceImpl;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.AppCfgRequestType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;


public class AppConfigControllerTest extends AbstractBaseControllerTest {
	private static final String APP_CONFIG_URL = "/api/config/v1/";

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private AppConfigServiceImpl appConfigServiceImpl;
	
	@InjectMocks
	private AppConfigController appConfigController;

	@Before
	public void beforeEach() {
		mockMvc = MockMvcBuilders.standaloneSetup(appConfigController).build();
	} 

	@Test
	public void testCreateAppConfig() throws Exception {
		List<AppConfigRequest> appConfigRequests = Arrays.asList(createAppConfigRequest());
		List<AppConfigDto> appConfigDtos = Arrays.asList(createAppConfigDto());
		when(appConfigServiceImpl.createAppConfigDefinition(appConfigRequests))
				.thenReturn(buildResponse(appConfigDtos));
		mockMvc.perform(MockMvcRequestBuilders.post(APP_CONFIG_URL).content(asJsonString(appConfigRequests))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].configName").value("button"));
	}
	
	@Test
	public void updateAppConfigDefinition() throws Exception {
		StringBuilder url = new StringBuilder(APP_CONFIG_URL).append("configValue");
		List<AppConfigRequest> appConfigRequests = Arrays.asList(createAppConfigRequest());
		when(appConfigServiceImpl.updateAppConfigDefinition(appConfigRequests)).thenReturn(buildResponse(appConfigRequests));
		mockMvc.perform(MockMvcRequestBuilders.put(url.toString()).content(asJsonString(appConfigRequests))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].configName").value("button"));
	}

	@Test
	public void testGetAppConfig() throws Exception {
		StringBuilder url = new StringBuilder(APP_CONFIG_URL).append(AppCfgRequestType.CONTEXT.toString())
				.append(BffAdminConstantsUtils.FORWARD_SLASH).append("button");
		when(appConfigServiceImpl.getAppConfig("button", AppCfgRequestType.CONTEXT))
				.thenReturn(buildResponse(createAppConfig()));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString())).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.configName").value("button"));

	}

	@Test
	public void testGetAllAppConfig() throws Exception {
		StringBuilder url = new StringBuilder(APP_CONFIG_URL).append("list/").append(AppCfgRequestType.CONTEXT);
		List<AppConfigDto> appConfigDtoList = Arrays.asList(createAppConfigDto());
		when(appConfigServiceImpl.getAppConfigDefinitionByType(AppCfgRequestType.CONTEXT))
				.thenReturn(buildResponse(appConfigDtoList));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString())).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].configName").value("button"));
	}

	@Test
	public void testgetAppConfigList() throws Exception {
		StringBuilder url = new StringBuilder(APP_CONFIG_URL).append("list");
		List<AppConfigDto> appConfigDtoList = Arrays.asList(createAppConfigDto());
		when(appConfigServiceImpl.getAppConfigList()).thenReturn(buildResponse(appConfigDtoList));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].configName").value("button"));
	}

	@Test
	public void testUpdateAppConfig() throws Exception {
		StringBuilder url = new StringBuilder(APP_CONFIG_URL).append("update");
		List<AppConfigRequest> appConfigRequests = Arrays.asList(createAppConfigRequest());
		when(appConfigServiceImpl.createUpdateAppConfigList(appConfigRequests)).thenReturn(buildResponse(appConfigRequests));
		mockMvc.perform(MockMvcRequestBuilders.put(url.toString()).content(asJsonString(appConfigRequests))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].configName").value("button"));
	}

	@Test
	public void testClearAppConfig() throws Exception {
		UUID flowId=UUID.randomUUID();
		StringBuilder url = new StringBuilder(APP_CONFIG_URL).append("clearAppConfig/");
		List<AppConfigMaster> appConfigs =getListAppConfig();
		when(appConfigServiceImpl.clearAppConfig("SUPER","Device1"))
				.thenReturn(buildResponse(appConfigs));
		mockMvc.perform(MockMvcRequestBuilders
				.put(url.toString())
				.queryParam("userId", "SUPER")
				.queryParam("flowId", flowId.toString())
				.queryParam("deviceName", "Device1"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].configType").value(AppCfgRequestType.CONTEXT.toString()));
	}

	private AppConfigRequest createAppConfigRequest() {
		
		AppConfigRequest appConfigRequest = new AppConfigRequest();
		appConfigRequest.setAppConfigId(UUID.fromString("0953bf8d-8906-48bc-99c8-c492b01e8be6"));
		appConfigRequest.setConfigName("button");
		appConfigRequest.setConfigType(AppCfgRequestType.CONTEXT.getType());
		appConfigRequest.setConfigValue("1637");
		appConfigRequest.setRawValue("test");
		appConfigRequest.setDescription("Test1");
		appConfigRequest.setAppConfigMasterId(UUID.fromString("0953bf8d-8906-48bc-99c8-c492b01e8be6"));
		appConfigRequest.setFlowId(UUID.fromString("128c5740-d32d-47c7-97d8-a1819826e94e"));
		return appConfigRequest;

	}

	private AppConfigMaster createAppConfig() {
		AppConfigMaster appConfig = new AppConfigMaster();
		appConfig.setConfigName("button");
		appConfig.setUid(UUID.randomUUID());
		appConfig.setConfigType(AppCfgRequestType.CONTEXT.toString());
		return appConfig;
	}
	
	private List<AppConfigMaster> getListAppConfig() {
		List<AppConfigMaster> appConfigList = new ArrayList<>();
		AppConfigMaster appConfig = new AppConfigMaster();
		appConfig.setConfigName("Warehouseid1");
		appConfig.setConfigType(AppCfgRequestType.CONTEXT.getType());
		List<AppConfigDetail> appConfigDetails = new ArrayList<>();
		AppConfigDetail appConfigDetail = new AppConfigDetail();
		appConfigDetail.setConfigValue("1637");
		appConfigDetail.setDescription("Test1");
		appConfigDetail.setFlowId(UUID.fromString("128c5740-d32d-47c7-97d8-a1819826e94e"));
		appConfigDetail.setUserId("SUPER");
		appConfig.setAppConfigDetails(appConfigDetails);
		appConfigList.add(appConfig);
		return appConfigList;
	}



	private AppConfigDto createAppConfigDto() {
		AppConfigDto appConfigDto = AppConfigDto.builder().configName("button").build();
		return appConfigDto;
	}

	private <T> BffCoreResponse buildResponse(T data) {
		return BffUtils.buildResponse(data, BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_CONTEXT_VARIABLES.getCode(),
				"message", "detailMessage", StatusCode.OK.getValue());
	}

	private String asJsonString(final Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
	}
}
