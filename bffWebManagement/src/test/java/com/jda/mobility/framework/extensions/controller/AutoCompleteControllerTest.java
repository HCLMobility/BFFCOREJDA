package com.jda.mobility.framework.extensions.controller;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.config.AppProperties;
import com.jda.mobility.framework.extensions.dto.TranslationDto;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.SearchRequest;
import com.jda.mobility.framework.extensions.service.impl.AutoCompleteServiceImpl;
import com.jda.mobility.framework.extensions.util.RequestHelper;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.SearchType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AutoCompleteControllerTest extends AbstractBaseControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private AutoCompleteServiceImpl autoCompleteService;

	@MockBean
	private AppProperties appProperties;

	@Autowired
	private RequestHelper requestHelper;

	@Before
	public void beforeEach() {
		AutoCompleteController controller = new AutoCompleteController(autoCompleteService, requestHelper);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	} 
	@Test
	public void testSearch() throws Exception {
		TranslationDto translationDto = new TranslationDto(UUID.randomUUID().toString(), "en-US", "key1", "First Name", SearchType.RESOURCE_BUNDLE.getType());
		List<TranslationDto> translationDtos = Arrays.asList(translationDto);
		when(appProperties.isBasicAuthEnabled()).thenReturn(true);
		SearchRequest searchRequest = createSearchRequest();
		when(autoCompleteService.search(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(buildResponse(translationDtos));
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/autocomplete/v1/search")
				.content(asJsonString(searchRequest))
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].type").value(SearchType.RESOURCE_BUNDLE.getType()));
	}

	private SearchRequest createSearchRequest() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchTerm(SearchType.RESOURCE_BUNDLE.getType());
		searchRequest.setSearchType("rbkey");
		return searchRequest;
	}

	private String asJsonString(final Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
	}

	private <T> BffCoreResponse buildResponse(T data) {
		return BffUtils.buildResponse(data, BffResponseCode.API_IMPORT_SERVICE_SUCCESS_CODE_NEW_REGISTRY.getCode(), "message",
				"detailMessage", StatusCode.OK.getValue());
	}
}
