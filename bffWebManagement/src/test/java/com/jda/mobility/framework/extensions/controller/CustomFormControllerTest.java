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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.CustomFormData;
import com.jda.mobility.framework.extensions.service.impl.CustomFormServiceImpl;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DeleteType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;

public class CustomFormControllerTest extends AbstractBaseControllerTest {

	private static final String CUSTOM_FORM_URL = "/api/customcomponent/v1/";

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private CustomFormServiceImpl customFormServiceImpl;
	
	@InjectMocks
	private CustomFormController customFormController;
	
	@Before
	public void beforeEach() {
		mockMvc = MockMvcBuilders.standaloneSetup(customFormController).build();
	}

	@Test
	public void testCreateCustomComponent() throws Exception {
		CustomFormData customFormData = createCustomFormData();
		when(customFormServiceImpl.createCustomComponent(Mockito.any())).thenReturn(buildResponse(customFormData));
		mockMvc.perform(MockMvcRequestBuilders.post(CUSTOM_FORM_URL).content(asJsonString(customFormData))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("Address Control"));
	}

	@Test
	public void testGetCustomComponentById() throws Exception {
		UUID customComponentId = UUID.randomUUID();
		StringBuilder url = new StringBuilder(CUSTOM_FORM_URL).append(customComponentId);
		when(customFormServiceImpl.getCustomComponentById(customComponentId))
				.thenReturn(buildResponse(createCustomFormData()));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString())).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("Address Control"));
	}

	@Test
	public void testModifyCustomComponent() throws Exception {
		UUID customComponentId = UUID.randomUUID();
		CustomFormData customFormData = createCustomFormData();
		customFormData.setName("Current Address");
		StringBuilder url = new StringBuilder(CUSTOM_FORM_URL).append(customComponentId);
		when(customFormServiceImpl.modifyCustomComponent(Mockito.any())).thenReturn(buildResponse(customFormData));
		mockMvc.perform(MockMvcRequestBuilders.put(url.toString()).content(asJsonString(customFormData))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("Current Address"));
	}

	@Test
	public void testDeleteCustomComponent() throws Exception {
		UUID customComponentId = UUID.randomUUID();
		StringBuilder url = new StringBuilder(CUSTOM_FORM_URL).append(customComponentId)
				.append(BffAdminConstantsUtils.FORWARD_SLASH).append(DeleteType.CONFIRM_DELETE);
		when(customFormServiceImpl.deleteCustomComponentById(customComponentId, DeleteType.CONFIRM_DELETE))
				.thenReturn(buildResponse(customComponentId));
		mockMvc.perform(MockMvcRequestBuilders.delete(url.toString()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data").value(customComponentId.toString()));
	}

	@Test
	public void testGetCustomComponentList() throws Exception {
		StringBuilder url = new StringBuilder(CUSTOM_FORM_URL).append("list");
		List<CustomFormData> customFormDatas = Arrays.asList(createCustomFormData());
		when(customFormServiceImpl.fetchCustomCompList(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(buildResponse(customFormDatas));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString())).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].name").value("Address Control"));

	}

	private CustomFormData createCustomFormData() {
		CustomFormData customFormData = new CustomFormData();
		customFormData.setName("Address Control");
		customFormData.setCustomComponentId(UUID.randomUUID());
		customFormData.setComponents(new ArrayList<>());
		customFormData.setDescription("Address control description");
		customFormData.setVisibility(true);
		customFormData.setDisabled(false);
		return customFormData;
	}

	private <T> BffCoreResponse buildResponse(T data) {
		return BffUtils.buildResponse(data, BffResponseCode.FLOW_SUCCESS_CODE_CREATE_FLOW.getCode(), "message",
				"detailMessage", StatusCode.OK.getValue());
	}

	private String asJsonString(final Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
	}
}
