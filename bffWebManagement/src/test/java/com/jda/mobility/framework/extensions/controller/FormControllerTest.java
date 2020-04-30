/**
 * 
 */

package com.jda.mobility.framework.extensions.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.dto.FieldObjDto;
import com.jda.mobility.framework.extensions.dto.FlowDefaultFormDto;
import com.jda.mobility.framework.extensions.dto.FormCustomDto;
import com.jda.mobility.framework.extensions.dto.FormObjDto;
import com.jda.mobility.framework.extensions.entity.Field;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.entity.projection.FormLiteDto;
import com.jda.mobility.framework.extensions.model.AppConfigRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.FormData;
import com.jda.mobility.framework.extensions.security.UserPrincipal;
import com.jda.mobility.framework.extensions.service.FormService;
import com.jda.mobility.framework.extensions.service.ProductPrepareService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ActionType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DefaultType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DeleteType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.FormStatus;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;

/**
 * The class FormControllerTest.java
 * 
 * @author ChittipalliN HCL Technologies Ltd.
 */

public class FormControllerTest extends AbstractBaseControllerTest {
	private static final String formUrl = "/api/form/v1/";

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private FormService formServiceImpl;

	@Mock
	private ProductPrepareService productPrepareService;

	@InjectMocks
	private FormController formController;
	
	@Mock
	private SessionDetails sessionDetails;
	
	@Before
	public void beforeEach() {
		mockMvc = MockMvcBuilders.standaloneSetup(formController).build();
	}

	
	@Test
	public void testCreateForm() throws Exception {
		StringBuilder url = new StringBuilder(formUrl).append(ActionType.CHECK_PUBLISH).append("/");

		when(formServiceImpl.createForm(Mockito.any(), Mockito.eq(ActionType.CHECK_PUBLISH),
				Mockito.eq(DefaultType.CHECK_DEFAULT),Mockito.any())).thenReturn(buildResponse(formDataRespone()));
		mockMvc.perform(MockMvcRequestBuilders.post(url.toString())
				.queryParam("identifier", DefaultType.CHECK_DEFAULT.toString()).content(asJsonString(formDataRequest()))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("Form1"));
	}

	@Test
	public void testModifyForm() throws Exception {
		StringBuilder url = new StringBuilder(formUrl).append(UUID.randomUUID()).append("/")
				.append(ActionType.CHECK_PUBLISH).append("/");
		when(formServiceImpl.modifyForm(Mockito.eq(ActionType.CHECK_PUBLISH), Mockito.any(),
				Mockito.eq(DefaultType.CHECK_DEFAULT),Mockito.any())).thenReturn(buildResponse(formDataRespone()));
		mockMvc.perform(MockMvcRequestBuilders.put(url.toString())
				.queryParam("identifier", DefaultType.CHECK_DEFAULT.toString()).content(asJsonString(formDataRequest()))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("Form1"));

	}
	
	@Test
	public void testGetFormById() throws Exception {
		UserPrincipal principal = (UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UUID formId = UUID.fromString("043a765c-768b-486c-8632-5ae0efe505e4");
		UUID menuId = UUID.fromString("043a765c-768b-486c-8632-5ae0efe505e4");
		StringBuilder url = new StringBuilder(formUrl).append(formId);
		when(formServiceImpl.getFormById(formId, principal.getPermissionIds(), menuId)).thenReturn(buildResponse(formDataRespone()));
		mockMvc.perform(MockMvcRequestBuilders
				.get(url.toString())
				.queryParam("menuId", menuId.toString())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("Form1"));
	}

	@Test
	public void testForm() throws Exception {
		List<AppConfigRequest> appConfigList =new ArrayList<>();
		UUID formId = UUID.fromString("043a765c-768b-486c-8632-5ae0efe505e4");
		UUID menuId = UUID.fromString("043a765c-768b-486c-8632-5ae0efe505e4");
		StringBuilder url = new StringBuilder(formUrl).append("formdata");
		when(formServiceImpl.getForm(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(buildResponse(getFlowDefaultFormDto()));
		mockMvc.perform(MockMvcRequestBuilders
				.put(url.toString())
				.queryParam("formId", formId.toString())
				.queryParam("menuId", menuId.toString()).content(asJsonString(appConfigList))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("Flow_testflow1"));
	}

	@Test
	public void testDeleteForm() throws Exception {
		UUID formId = UUID.fromString("043a765c-768b-486c-8632-5ae0efe505e4");
		StringBuilder url = new StringBuilder(formUrl).append(formId).append("/").append(DeleteType.CHECK_DELETE)
				.append("/");
		when(formServiceImpl.deleteFormByID(formId, DeleteType.CHECK_DELETE))
				.thenReturn(buildResponse("deleted successfully"));
		mockMvc.perform(MockMvcRequestBuilders.delete(url.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data").value("deleted successfully"));
	}

	@Test
	public void testFetchAllForms() throws Exception {
		UUID flowId = UUID.fromString("043a765c-768b-486c-8632-5ae0efe505e4");
		List<FormData> formDtoList = new ArrayList<>();
		formDtoList.add(formDataRespone());
		StringBuilder url = new StringBuilder(formUrl).append("list").append("/").append(flowId).append("/");
		when(formServiceImpl.fetchAllForms(flowId)).thenReturn(buildResponse(formDtoList));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].name").value("Form1"));
	}

	@Test
	public void testFetchOrphanForms() throws Exception {
		UUID flowId = UUID.fromString("043a765c-768b-486c-8632-5ae0efe505e4");
		List<FormData> formDtoList = new ArrayList<>();
		formDtoList.add(formDataRespone());
		StringBuilder url = new StringBuilder(formUrl).append("orphanforms").append("/").append(flowId).append("/");
		when(formServiceImpl.fetchOrphanForms(flowId)).thenReturn(buildResponse(formDtoList));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].name").value("Form1"));
	}

	@Test
	public void testFetchUnpublishForms() throws Exception {
		List<FormData> formDtoList = new ArrayList<>();
		formDtoList.add(formDataRequest());
		StringBuilder url = new StringBuilder(formUrl).append("unpublishforms");
		when(formServiceImpl.fetchUnpublishForms()).thenReturn(buildResponse(formDtoList));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].name").value("Form1"));
	}

	@Test
	public void testCreateDefaultForm() throws Exception {
		UUID formId = UUID.fromString("043a765c-768b-486c-8632-5ae0efe505e4");
		StringBuilder url = new StringBuilder(formUrl).append("defaultform").append("/").append(formId).append("/");
		when(formServiceImpl.createDefaultForm(formId, DefaultType.CHECK_DEFAULT)).thenReturn(buildResponse(formId));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString())
				.queryParam("identifier", DefaultType.CHECK_DEFAULT.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data").value(formId.toString()));
	}

	@Test
	public void testGetFormComponent() throws Exception {
		UUID customComponentId = UUID.fromString("043a765c-768b-486c-8632-5ae0efe505e4");
		StringBuilder url = new StringBuilder(formUrl).append("formlist").append("/").append(customComponentId);
		when(formServiceImpl.getFormDetails(customComponentId)).thenReturn(buildResponse(getFormCustom()));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.customComponentName").value("CustomForm"));
	}

	@Test
	public void testPublishForm() throws Exception {
		UUID formId = UUID.fromString("043a765c-768b-486c-8632-5ae0efe505e4");
		StringBuilder url = new StringBuilder(formUrl).append("publish").append("/").append(formId).append("/")
				.append(ActionType.CHECK_PUBLISH);
		when(formServiceImpl.publishForm(Mockito.eq(formId), Mockito.eq(ActionType.CHECK_PUBLISH), Mockito.any()))
				.thenReturn(buildResponse(formDataRespone()));
		mockMvc.perform(MockMvcRequestBuilders.put(url.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("Form1"));
	}

	@Test
	public void testFetchFormBasicList() throws Exception {
		UUID flowId = UUID.fromString("043a765c-768b-486c-8632-5ae0efe505e4");
		List<FormLiteDto> formDtoList = new ArrayList<>();
		formDtoList.add(getFormNameList());
		StringBuilder url = new StringBuilder(formUrl).append("list").append("/basic/").append(flowId);
		when(formServiceImpl.fetchFormBasicList(flowId)).thenReturn(buildResponse(formDtoList));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].formName").value("Form1"));
	}

	@Test
	public void testFetchForms() throws Exception {
		UUID productConfigId = UUID.fromString("043a765c-768b-486c-8632-5ae0efe505e4");
		List<FormObjDto> formObjList = new ArrayList<>();
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(productConfigId);
		formObjList.add(getFormObjDto());
		StringBuilder url = new StringBuilder(formUrl).append("forms");
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(productConfig);
		when(formServiceImpl.fetchUnpublishOrphanForms(productConfigId, FormStatus.ORPHAN))
				.thenReturn(buildResponse(formObjList));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString())
				.queryParam("identifier", FormStatus.ORPHAN.toString()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andDo(print())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].name").value("Form1"));
	}

	@Test
	public void testTestLog() throws Exception {
		StringBuilder url = new StringBuilder(formUrl).append("log").append("/");
		mockMvc.perform(MockMvcRequestBuilders.post(url.toString()).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}

	private FormData formDataRequest() {
		FormData formData = new FormData();
		formData.setName("Form1");
		formData.setFlowId(UUID.fromString("043a765c-768b-486c-8632-5ae0efe505e4"));
		formData.setOrphanForm(false);
		formData.setClonableForm(true);
		formData.setApplyToAllClones(false);
		formData.setDefaultForm(true);
		return formData;
	}

	private FormData formDataRespone() {
		FormData formData = new FormData();
		formData.setName("Form1");
		return formData;
	}

	private static <T> BffCoreResponse buildResponse(T data) {
		return BffUtils.buildResponse(data, BffResponseCode.FLOW_SUCCESS_CODE_CREATE_FLOW.getCode(), "message",
				"detailMessage", StatusCode.OK.getValue());
	}

	private String asJsonString(final Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
	}

	private FormCustomDto getFormCustom() {
		FormCustomDto formCustomDto = new FormCustomDto("CustomForm", null, null);
		return formCustomDto;
	}

	private FormLiteDto getFormNameList() {
		return new FormLiteDto(null, "Form1", false, false);
	}

	private FormObjDto getFormObjDto() throws IOException {
		Form form = new Form();
		Flow flow = new Flow();
		flow.setDefaultFormId(UUID.randomUUID());
		form.setName("Form1");
		form.setUid(UUID.fromString("043a765c-768b-486c-8632-5ae0efe505e4"));
		form.setOrphan(false);
		form.setCloneable(true);
		form.setApplyToAllClones(false);
		flow.addForm(form);
		Field field = new Field();
		FieldObjDto fieldObjDto = new FieldObjDto(field, new ArrayList<FieldObjDto>());
		form.setName("Form1");
		form.setUid(UUID.fromString("043a765c-768b-486c-8632-5ae0efe505e4"));
		form.setOrphan(false);
		form.setCloneable(true);
		form.setApplyToAllClones(false);
		List<FieldObjDto> fieldObjDtos = new ArrayList<>();
		fieldObjDtos.add(fieldObjDto);
		FormObjDto formObjDto = new FormObjDto(form, fieldObjDtos);
		return formObjDto;
	}

	private FlowDefaultFormDto getFlowDefaultFormDto() {
		FlowDefaultFormDto flowDefaultFormDto = new FlowDefaultFormDto();
		flowDefaultFormDto.setName("Flow_testflow1");
		flowDefaultFormDto.setModalForm(true);
		flowDefaultFormDto.setModalForm(true);
		return flowDefaultFormDto;
	}
}
