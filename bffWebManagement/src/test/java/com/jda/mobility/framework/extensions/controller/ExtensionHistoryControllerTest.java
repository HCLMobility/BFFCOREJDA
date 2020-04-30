package com.jda.mobility.framework.extensions.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.dto.ExtensionVarianceDto;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.service.impl.ExtensionHistoryServiceImpl;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ExtensionType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;


public class ExtensionHistoryControllerTest extends AbstractBaseControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@InjectMocks
	private ExtensionHistoryController extensionHistoryController;
	
	@Mock
	private ExtensionHistoryServiceImpl extHistoryServiceImpl;
	
	@Before
	public void beforeEach() {
		mockMvc = MockMvcBuilders.standaloneSetup(extensionHistoryController).build();
	}
	
	
	@Test
	public void testFetchExtensionHistory() throws Exception{
		UUID extendedObjectId = UUID.randomUUID();
		StringBuilder url = new StringBuilder("/api/extension/v1/variance/")
				.append(extendedObjectId);
		List<ExtensionVarianceDto> extensionVarianceDtos = Arrays.asList(createExtensionVarianceDto());
		when(extHistoryServiceImpl.fetchExtensionHistory(extendedObjectId, null, ExtensionType.FLOW)).thenReturn(buildResponse(extensionVarianceDtos));
		mockMvc.perform(MockMvcRequestBuilders
				.get(url.toString())
				.param("extensionType", ExtensionType.FLOW.toString()))
		.andDo(print())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].key").value("name"));
		
	}

	private ExtensionVarianceDto createExtensionVarianceDto() throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode leftValue =  mapper.readTree("{\"name\":\"test\"}");
		JsonNode rightValue =  mapper.readTree("{\"name\":\"test1\"}");
		ExtensionVarianceDto extensionVariance = new ExtensionVarianceDto("name", leftValue, rightValue);
		return extensionVariance;
	}
	private <T> BffCoreResponse buildResponse(T data) {
		return BffUtils.buildResponse(data, BffResponseCode.FLOW_SUCCESS_CODE_FLOW_EXTENDED_FETCH.getCode(), "message",
				"detailMessage", StatusCode.OK.getValue());
	}
}
