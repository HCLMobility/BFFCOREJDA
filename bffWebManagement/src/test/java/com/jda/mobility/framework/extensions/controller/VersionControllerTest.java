package com.jda.mobility.framework.extensions.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
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
import com.jda.mobility.framework.extensions.dto.FlowDto;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.CloneRequest;
import com.jda.mobility.framework.extensions.model.Layer;
import com.jda.mobility.framework.extensions.service.impl.FlowServiceImpl;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.CloneType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;

public class VersionControllerTest  extends AbstractBaseControllerTest{

	
	private static final String versionUrl = "/api/version/v1/";
	
	@Autowired
	private MockMvc mockMvc;

	@Mock
	private FlowServiceImpl flowServiceImpl;
	
	@InjectMocks
	private VersionController versionController;
	
	@Before
	public void beforeEach() {
		mockMvc = MockMvcBuilders.standaloneSetup(versionController).build();
	}
	
	@Test
	public void testcreateNewVersionforFlow() throws Exception {
		StringBuilder url = new StringBuilder(versionUrl)
				.append(CloneType.FLOW);
		CloneRequest cloneRequest = createCloneRequest();
		when(flowServiceImpl.cloneComponent(Mockito.any(), Mockito.eq(CloneType.FLOW), Mockito.eq(BffAdminConstantsUtils.VERSIONING))).thenReturn(buildResponse(createFlow()));
		mockMvc.perform( MockMvcRequestBuilders
				.post(url.toString())
				.content(asJsonString(cloneRequest))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("pick"));
	}

	
	private static <T> BffCoreResponse buildResponse(T data) {
		return BffUtils.buildResponse(data, BffResponseCode.FLOW_SUCCESS_CODE_CREATE_FLOW.getCode(), "message",
				"detailMessage", StatusCode.OK.getValue());
	}
	private FlowDto createFlow() {
		List<String> permissions = new ArrayList<>();
		permissions.add("pick flow");
		Layer layer= new Layer();
		layer.setLevel(1);
		layer.setName("service");
		FlowDto flowDto = new FlowDto.FlowBuilder("pick")
				.setFlowId(UUID.randomUUID())
				.setDefaultFormId(UUID.randomUUID())
				.setDescription("description")
				.setDisabled(false)
				.setExtDisabled(false)
				.setPublished(true)
				.setTag("flow tag")
				.setTabbedForm(true)
				.setExtendedFromFlowId(UUID.randomUUID())
				.setExtendedFromFlowName("extended form name")
				.setLayer(layer)
				.setVersion(1L)
				.setPermissions(permissions)
				.build();
		return flowDto;
	}
	
	private CloneRequest createCloneRequest() {
		CloneRequest cloneRequest = new CloneRequest();
		cloneRequest.setId(UUID.randomUUID());
		cloneRequest.setName("pick");
		return cloneRequest;
	}
	
	private String asJsonString(final Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
	}

}

