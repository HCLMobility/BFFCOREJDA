package com.jda.mobility.framework.extensions.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.dto.FlowDefaultFormDto;
import com.jda.mobility.framework.extensions.dto.FlowDto;
import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.entity.projection.FlowLiteDto;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.CloneRequest;
import com.jda.mobility.framework.extensions.model.FlowRequest;
import com.jda.mobility.framework.extensions.model.Layer;
import com.jda.mobility.framework.extensions.model.UserPermissionRequest;
import com.jda.mobility.framework.extensions.service.ProductPrepareService;
import com.jda.mobility.framework.extensions.service.impl.FlowServiceImpl;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ActionType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.CloneType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DeleteType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DisableType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.FlowType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;

public class FlowControllerTest extends AbstractBaseControllerTest {

	private static final String FLOW_URL = "/api/flow/v1/";

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private FlowServiceImpl flowServiceImpl;

	@InjectMocks
	private FlowController flowController;

	@Mock
	private ProductPrepareService productPrepareService;

	@Before
	public void beforeEach() {
		mockMvc = MockMvcBuilders.standaloneSetup(flowController).build();
	}

	@Test
	public void testCreateFlow() throws Exception {
		when(flowServiceImpl.createFlow(Mockito.any())).thenReturn(buildResponse(createFlow()));
		mockMvc.perform(MockMvcRequestBuilders.post(FLOW_URL).content(asJsonString(createFlowRequest()))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("pick"));
	}

	@Test
	public void testFetchCount() throws Exception {
		Map<String, Integer> countMap = new HashMap<>();
		countMap.put(BffAdminConstantsUtils.API_COUNT, 1);
		StringBuilder url = new StringBuilder(FLOW_URL).append("/count");
		when(flowServiceImpl.fetchCount()).thenReturn(buildResponse(countMap));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.API_COUNT").value(1));
	}

	@Test
	public void testUpdateFlow() throws Exception {
		StringBuilder url = new StringBuilder(FLOW_URL).append(UUID.randomUUID()).append("/").append(ActionType.SAVE);
		FlowRequest flowRequest = createFlowRequest();
		when(flowServiceImpl.modifyFlow(Mockito.any(), Mockito.eq(ActionType.SAVE),
				Mockito.eq(DisableType.CHECK_DISABLE),Mockito.any())).thenReturn(buildResponse(createFlow()));
		mockMvc.perform(MockMvcRequestBuilders.put(url.toString())
				.queryParam("identifier", DisableType.CHECK_DISABLE.toString()).content(asJsonString(flowRequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("pick"));
	}

	@Test
	public void testUniqueFlow() throws Exception {
		StringBuilder url = new StringBuilder(FLOW_URL).append("/unique/").append("pick").append("/").append(1);
		when(flowServiceImpl.uniqueFlow("pick", 1)).thenReturn(buildResponse("pick"));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.details.data").value("pick"));
	}

	@Test
	public void testGetFlowById() throws Exception {
		MockHttpSession mockHttpSession = new MockHttpSession();
		UUID flowId = UUID.randomUUID();
		StringBuilder url = new StringBuilder(FLOW_URL).append(flowId);
		when(flowServiceImpl.getFlowById(flowId)).thenReturn(buildResponse(createFlow()));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).accept(MediaType.APPLICATION_JSON)
				.session(mockHttpSession))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("pick"));
	}

	@Test
	public void testGetDefaultFormForFlowId() throws Exception {
		StringBuilder url = new StringBuilder(FLOW_URL).append("defaultform");
		when(flowServiceImpl.getDefaultFormForFlowId(Mockito.any())).thenReturn(buildResponse(getFlowDefaultFormDto()));
		mockMvc.perform(MockMvcRequestBuilders
				.post(url.toString()).content(asJsonString(getUserPermissionRequest()))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("Flow_testflow1"));
	}

	@Test
	public void testFetchFlows() throws Exception {
		UUID productConfigId = UUID.randomUUID();
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(productConfigId);
		List<FlowDto> flowDtoList = new ArrayList<>();
		flowDtoList.add(createFlow());
		StringBuilder url = new StringBuilder(FLOW_URL).append("/list/").append(FlowType.PUBLISHED);
		when(flowServiceImpl.fetchFlows( FlowType.PUBLISHED)).thenReturn(buildResponse(flowDtoList));
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(productConfig);
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteFlowById() throws Exception {
		UUID flowId = UUID.randomUUID();
		StringBuilder url = new StringBuilder(FLOW_URL).append(flowId).append("/").append(DeleteType.CONFIRM_DELETE);
		when(flowServiceImpl.deleteFlowById(flowId, DeleteType.CONFIRM_DELETE))
				.thenReturn(buildResponse(flowId.toString()));
		mockMvc.perform(MockMvcRequestBuilders.delete(url.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data").value(flowId.toString()));
	}

	@Test
	public void testDisableFlow() throws Exception {
		UUID flowId = UUID.randomUUID();
		StringBuilder url = new StringBuilder(FLOW_URL).append("/disableflow/").append(flowId).append("/")
				.append(DisableType.CONFIRM_DISABLE);
		when(flowServiceImpl.disableFlow(flowId, DisableType.CONFIRM_DISABLE)).thenReturn(buildResponse(flowId));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data").value(flowId.toString()));
	}

	@Test
	public void testPublishFlow() throws Exception {
		UUID flowId = UUID.randomUUID();
		StringBuilder url = new StringBuilder(FLOW_URL).append("/publish/").append(flowId).append("/")
				.append(ActionType.CONFIRM_PUBLISH);
		when(flowServiceImpl.publishFlow(Mockito.eq(flowId), Mockito.eq(ActionType.CONFIRM_PUBLISH), Mockito.any())).thenReturn(buildResponse(createFlow()));
		mockMvc.perform(MockMvcRequestBuilders.put(url.toString()).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("pick"));
	}

	@Test
	public void testCloneComponent() throws Exception {
		StringBuilder url = new StringBuilder(FLOW_URL).append("clone/").append(CloneType.FLOW);
		CloneRequest cloneRequest = createCloneRequest();
		when(flowServiceImpl.cloneComponent(Mockito.any(), Mockito.eq(CloneType.FLOW),
				Mockito.eq(BffAdminConstantsUtils.EXTENDED))).thenReturn(buildResponse(createFlow()));
		mockMvc.perform(MockMvcRequestBuilders.post(url.toString()).content(asJsonString(cloneRequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("pick"));
	}

	@Test
	public void testFetchFlowNameList() throws Exception {
		List<FlowLiteDto> flowDetailsList = new ArrayList<>();
		FlowLiteDto flowLiteDto = new FlowLiteDto(UUID.randomUUID(), "pick", 0, false, false,UUID.randomUUID());
		flowLiteDto.setFlowId(UUID.randomUUID());
		flowLiteDto.setName("pick");
		flowLiteDto.setVersion(1);
		flowDetailsList.add(flowLiteDto);
		StringBuilder url = new StringBuilder(FLOW_URL).append("list/basic");
		when(flowServiceImpl.fetchFlowBasicList()).thenReturn(buildResponse(flowDetailsList));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].name").value("pick"));
	}

	private <T> BffCoreResponse buildResponse(T data) {
		return BffUtils.buildResponse(data, BffResponseCode.FLOW_SUCCESS_CODE_CREATE_FLOW.getCode(), "message",
				"detailMessage", StatusCode.OK.getValue());
	}

	private String asJsonString(final Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
	}

	private CloneRequest createCloneRequest() {
		CloneRequest cloneRequest = new CloneRequest();
		cloneRequest.setId(UUID.randomUUID());
		cloneRequest.setName("pick");
		return cloneRequest;
	}

	private FlowDto createFlow() {
		List<String> permissions = new ArrayList<>();
		permissions.add("pick flow");
		Layer layer = new Layer();
		layer.setLevel(1);
		layer.setName("service");
		FlowDto flowDto = new FlowDto.FlowBuilder("pick").setFlowId(UUID.randomUUID())
				.setDefaultFormId(UUID.randomUUID()).setDescription("description").setDisabled(false)
				.setExtDisabled(false).setPublished(true).setTabbedForm(true).setExtendedFromFlowId(UUID.randomUUID())
				.setExtendedFromFlowName("extended form name").setLayer(layer).setVersion(1L)
				.setPermissions(permissions).build();
		return flowDto;
	}

	private FlowRequest createFlowRequest() {
		FlowRequest flowRequest = new FlowRequest();
		flowRequest.setName("pick");
		flowRequest.setDefaultFormId(UUID.randomUUID());
		flowRequest.setDisabled(true);
		return flowRequest;
	}

	private UserPermissionRequest getUserPermissionRequest() {
		UserPermissionRequest userPermissionRequest = new UserPermissionRequest();
		userPermissionRequest.setFlowId(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209"));
		userPermissionRequest.setFormId(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209"));
		userPermissionRequest.setUserPermissions(new ArrayList<>());
		return userPermissionRequest;
	}

	private FlowDefaultFormDto getFlowDefaultFormDto() {
		FlowDefaultFormDto flowDefaultFormDto = new FlowDefaultFormDto();
		flowDefaultFormDto.setName("Flow_testflow1");
		flowDefaultFormDto.setModalForm(true);
		flowDefaultFormDto.setModalForm(true);
		return flowDefaultFormDto;
	}

}
