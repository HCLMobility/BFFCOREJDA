package com.jda.mobility.framework.extensions.controller;

import static org.mockito.Mockito.when;

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

import com.jda.mobility.framework.extensions.dto.LocaleDto;
import com.jda.mobility.framework.extensions.entity.Locale;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.service.impl.LocaleServiceImpl;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;


public class LocaleControllerTest extends AbstractBaseControllerTest{
	private static final String USER_LANGUAGE_CODE_URL = "/api/locale/v1/";

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private LocaleServiceImpl localeServiceImpl;
	
	@InjectMocks
	private LocaleController localeController;
	
	@Before
	public void beforeEach() {
		mockMvc = MockMvcBuilders.standaloneSetup(localeController).build();
	}

	@Test
	public void testgetLocaleCodeList() throws Exception {
		StringBuilder url = new StringBuilder(USER_LANGUAGE_CODE_URL).append("list");
		List<LocaleDto> localeCodeDtoList = Arrays.asList(createLocaleCodeDto());
		when(localeServiceImpl.getLocaleList()).thenReturn(buildResponse(localeCodeDtoList));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].localeCode").value("EN"));
	}

	private Locale createLocaleCode() {
		Locale localeCode = new Locale();
		localeCode.setLocaleCode("EN");			
		return localeCode;
	}

	private LocaleDto createLocaleCodeDto() {		
		LocaleDto localeCodeDto = LocaleDto.builder().localeCode(createLocaleCode().getLocaleCode()).localeId(UUID.randomUUID().toString()).build();
		return localeCodeDto;
	}

	private <T> BffCoreResponse buildResponse(T data) {
		return BffUtils.buildResponse(data, BffResponseCode.USER_LANGUAGE_CODE_SERVICE_SUCCESS_CODE_FETCH_ALL.getCode(),
				"message", "detailMessage", StatusCode.OK.getValue());
	}

}