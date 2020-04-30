package com.jda.mobility.framework.extensions.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.dto.ApiMasterDto;
import com.jda.mobility.framework.extensions.dto.RegistryDto;
import com.jda.mobility.framework.extensions.model.ApiMasterRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.service.impl.ApiImportServiceImpl;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ApiRegistryType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;


public class ApiImportControllerTest extends AbstractBaseControllerTest{
	
	private static final String API_IMPORT_URL = "/api/import/v1";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Mock
	private ApiImportServiceImpl apiImportServiceImpl;
	
	
	@InjectMocks
	private ApiImportController apiImportController;

	@Before
	public void beforeEach() {
		mockMvc = MockMvcBuilders.standaloneSetup(apiImportController).build();
	} 
	
	@Test
	public void testImportApiIntoNewRegistry() throws Exception {
		StringBuilder url = new StringBuilder(API_IMPORT_URL)
				.append("/registry");
		MockMultipartFile file = new MockMultipartFile("file", "", "application/json", "{\"json\": \"someValue\"}".getBytes());
		when(apiImportServiceImpl.importApiIntoNewRegistry(file, "item", ApiRegistryType.INTERNAL, null)).thenReturn(buildResponse(createRegistryDto()));
		mockMvc.perform(MockMvcRequestBuilders
				.multipart(url.toString())
				.file(file)
				.param("newRegistryApiType", ApiRegistryType.INTERNAL.toString())
				.param("newRegistryName", "item")
				.contentType(MediaType.MULTIPART_FORM_DATA))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.name").value("item"))
		.andDo(print());
	}
	
	
	@Test
	public void testImportApiIntoExistingRegistry() throws Exception {
		UUID registryId = UUID.randomUUID();
		MockMultipartFile file = new MockMultipartFile("file", "", "application/json", "{\"name\": \"item\"}".getBytes());
		List<UUID> uidList = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
		when(apiImportServiceImpl.importApiIntoExistingRegistry(Mockito.any(), Mockito.anyBoolean(), Mockito.any(), Mockito.any())).thenReturn(buildResponse(uidList));
		StringBuilder url = new StringBuilder(API_IMPORT_URL)
				.append(BffAdminConstantsUtils.FORWARD_SLASH)
				.append(registryId)
				.append(BffAdminConstantsUtils.FORWARD_SLASH)
				.append(true);
		MockMultipartHttpServletRequestBuilder multipart = (MockMultipartHttpServletRequestBuilder) MockMvcRequestBuilders
				.multipart(url.toString())
				.file(file)
				.with(request -> {
					request.setMethod(HttpMethod.PUT.toString());
					return request;
				});
		mockMvc.perform(multipart)
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data").isNotEmpty())
		.andDo(print());
	}
	
	@Test
	public void testOverrideApiData() throws Exception {
		UUID registryid = UUID.randomUUID();
		List<ApiMasterRequest> apiMasterRequests = Arrays.asList(createApiMasterRequest());
		List<ApiMasterDto> apiMasterDtos = Arrays.asList(createApiMasterDto());
		when(apiImportServiceImpl.overrideExistingApis(apiMasterRequests, registryid)).thenReturn(buildResponse(apiMasterDtos));
		StringBuilder url = new StringBuilder(API_IMPORT_URL)
				.append("/registry/")
				.append(registryid);
		mockMvc.perform(MockMvcRequestBuilders
				.put(url.toString())
				.content(asJsonString(apiMasterRequests))
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].name").value("item"));
	}
	
	private RegistryDto createRegistryDto() {
		RegistryDto registryDto = new RegistryDto.RegistryBuilder()
				.setRegId(UUID.randomUUID().toString())
				.setName("item")
				.setApiVersion("1.0")
				.setHelperClass("helperClass")
				.setBasePath(BffAdminConstantsUtils.FORWARD_SLASH)
				.setPort("8080")
				.setContextPath(BffAdminConstantsUtils.FORWARD_SLASH)
				.setApiType(ApiRegistryType.INTERNAL.toString())
				.setVersionId(1)
				.setCreatedBy(BffAdminConstantsUtils.SUPER)
				.setCreatedOn(new Date())
				.setModifiedBy(BffAdminConstantsUtils.SUPER)
				.setModifiedOn(new Date())
				.setLayer(1)
				.build();
		return registryDto;
	}
	
	private ApiMasterRequest createApiMasterRequest() {
		ApiMasterRequest apiMasterRequest = new ApiMasterRequest();
		apiMasterRequest.setUid(UUID.randomUUID());
		apiMasterRequest.setName("item");
		apiMasterRequest.setRequestBody("{}");
		apiMasterRequest.setRequestEndpoint(BffAdminConstantsUtils.FORWARD_SLASH);
		apiMasterRequest.setRequestMethod(HttpMethod.POST.toString());
		apiMasterRequest.setRequestPathparams(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRequest.setRequestPreproc(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRequest.setRequestQuery(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRequest.setResponsePostproc(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRequest.setResponseSchema(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRequest.setVersion("1");
		return apiMasterRequest;
	}
	
	private ApiMasterDto createApiMasterDto() {
		ApiMasterDto apiMasterDto = ApiMasterDto.builder()
				.uid(UUID.randomUUID())
				.name("item")
				.requestBody("{}")
				.requestEndpoint(BffAdminConstantsUtils.FORWARD_SLASH)
				.requestMethod(HttpMethod.POST.toString())
				.requestPathparams(BffAdminConstantsUtils.EMPTY_SPACES)
				.requestQuery(BffAdminConstantsUtils.EMPTY_SPACES)
				.requestQuery(BffAdminConstantsUtils.EMPTY_SPACES)
				.responsePostproc(BffAdminConstantsUtils.EMPTY_SPACES)
				.responseSchema(BffAdminConstantsUtils.EMPTY_SPACES)
				.requestPreproc(BffAdminConstantsUtils.EMPTY_SPACES)
				.version("1")
				.regName(BffAdminConstantsUtils.EMPTY_SPACES)
				.build();
		return apiMasterDto;
		
	}
	
	private String asJsonString(final Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
	}
	
	private <T> BffCoreResponse buildResponse(T data) {
		return BffUtils.buildResponse(data, BffResponseCode.API_IMPORT_SERVICE_SUCCESS_CODE_NEW_REGISTRY.getCode(), "message",
				"detailMessage", StatusCode.OK.getValue());
	}
}
