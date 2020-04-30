package com.jda.mobility.framework.extensions.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.service.PreAndPostProcessorService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;

public class PreAndPostProcessorControllerTest extends AbstractBaseControllerTest{

	
private static final String API_IMPORT_URL = "/api/preandpostprocessor/v1";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Mock
	private PreAndPostProcessorService preAndPostProcessorService;
	
	
	@InjectMocks
	private PreAndPostProcessorController preAndPostProcessorController;

	@Before
	public void beforeEach() {
		mockMvc = MockMvcBuilders.standaloneSetup(preAndPostProcessorController).build();
	} 
	
	
	@Test
	public void testImportApiIntoNewRegistry() throws Exception {
		UUID apiMasterId=UUID.randomUUID();
		StringBuilder url = new StringBuilder(API_IMPORT_URL)
				.append("/upload");
		MockMultipartFile preProcessorFile = new MockMultipartFile("preProcessorFile", "", "application/json", "{\"json\": \"someValue\"}".getBytes());
		MockMultipartFile postProcessorFile = new MockMultipartFile("postProcessorFile", "", "application/json", "{\"json\": \"someValue\"}".getBytes());
		when(preAndPostProcessorService.importApiIntoNewRegistry(preProcessorFile, postProcessorFile, apiMasterId,BffAdminConstantsUtils.ApiUploadMode.CONFIRM_UPLOAD))
		.thenReturn(buildResponse(0));
		mockMvc.perform(MockMvcRequestBuilders
				.multipart(url.toString())
				.file(preProcessorFile)
				.file(postProcessorFile)
				.param("apiMasterId", apiMasterId.toString())
				.param("identifier", BffAdminConstantsUtils.ApiUploadMode.CONFIRM_UPLOAD.toString())
				.contentType(MediaType.MULTIPART_FORM_DATA))
		.andExpect(status().isOk())
		.andDo(print());
	}

	private <T> BffCoreResponse buildResponse(T data) {
		return BffUtils.buildResponse(data, BffResponseCode.API_IMPORT_SERVICE_SUCCESS_CODE_NEW_REGISTRY.getCode(), "message",
				"detailMessage", StatusCode.OK.getValue());
	}
	
}
