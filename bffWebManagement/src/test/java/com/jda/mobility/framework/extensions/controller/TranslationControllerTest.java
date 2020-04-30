/**
 * 
 */
package com.jda.mobility.framework.extensions.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.dto.TranslationDto;
import com.jda.mobility.framework.extensions.dto.TranslationRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.service.impl.TranslationServiceImpl;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;

/**
 * JUnit Test class for ResourceBundleController
 * 
 * @author HCL Technologies
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("unittest")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser
public class TranslationControllerTest {
	
	
	private static final String transUrl = "/api/core/v1";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TranslationServiceImpl translationServiceImpl;
	
	@Test
	public void testCreateResourceBundle() throws Exception {
		
		StringBuilder url = new StringBuilder(transUrl)
				.append("/messages");
		TranslationRequest translationRequest = createTranslationRequest();
		when(translationServiceImpl.createResourceBundle(Mockito.any())).thenReturn(buildResponse(createResourceBundle()));
		mockMvc.perform( MockMvcRequestBuilders
				.post(url.toString())
				.content(asJsonString(translationRequest))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.rbvalue").value("test"));
	}

	@Test
	public void testGetResourceBundles()  throws Exception{
		StringBuilder url = new StringBuilder(transUrl)
				.append("/messages")
				.append("/list");
		List<TranslationDto> resourceBundleDtoList = new ArrayList<>();
		resourceBundleDtoList.add(createResourceBundle());
		when(translationServiceImpl.getResourceBundles("INTERNAL")).thenReturn(buildResponse(resourceBundleDtoList));
		mockMvc.perform( MockMvcRequestBuilders
				.get(url.toString())
				.param("locale", "en")
				.param("type", "INTERNAL")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].rbvalue").value("test"));
		
	}	
	@Test
	public void testUpdateLocale()  throws Exception{
		StringBuilder url = new StringBuilder(transUrl)
				.append("/locale")
				.append("/en");
		List<TranslationDto> resourceBundleDtoList = new ArrayList<>();
		resourceBundleDtoList.add(createResourceBundle());
		when(translationServiceImpl.updateLocale("en")).thenReturn(buildResponse(resourceBundleDtoList));
		mockMvc.perform( MockMvcRequestBuilders
				.put(url.toString())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].rbvalue").value("test"));
	}
	
	private TranslationRequest createTranslationRequest() {
		return TranslationRequest.builder()
				.uid(UUID.randomUUID()).locale("en").rbkey("14563").rbvalue("test").type("test")
				.build();
	}
	private TranslationDto createResourceBundle() {
		TranslationDto resourceBundle = new TranslationDto("en", "en", "test", "test", "test");
		return resourceBundle;
	}
	
	private static <T> BffCoreResponse buildResponse(T data) {
		return BffUtils.buildResponse(data, BffResponseCode.RESOURCE_BUNDLE_SERVICE_SUCCESS_CODE_CREATE.getCode(), "message",
				"detailMessage", StatusCode.OK.getValue());
	}
	private String asJsonString(final Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
	}

}
