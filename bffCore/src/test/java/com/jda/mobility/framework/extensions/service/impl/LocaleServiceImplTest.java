package com.jda.mobility.framework.extensions.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jda.mobility.framework.extensions.entity.Locale;
import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.exception.DataBaseException;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.repository.LocaleRepository;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 * The class LocaleServiceImplTest.java
 */

@RunWith(SpringJUnit4ClassRunner.class)
public class LocaleServiceImplTest extends AbstractPrepareTest {

	@InjectMocks
	private LocaleServiceImpl localeServiceImpl;

	@Mock
	private LocaleRepository localeCodeRepository;

	@Test
	public void testGetUserLanguageCodeList() {
		when(localeCodeRepository.findAll()).thenReturn(getUserLanguageCodeList());
		BffCoreResponse response = localeServiceImpl.getLocaleList();
		assertEquals(BffResponseCode.USER_LANGUAGE_CODE_SERVICE_SUCCESS_CODE_FETCH_ALL.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetUserLanguageCodeList_DatabaseException() {
		when(localeCodeRepository.findAll()).thenThrow(new DataBaseException("Locale retrieval failed"));
		BffCoreResponse response = localeServiceImpl.getLocaleList();
		assertEquals(BffResponseCode.ERR_USER_LANGUAGE_CODE_SERVICE_API_FETCH_ALL_DBEXCEPTION.getCode(),
				response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetUserLanguageCodeList_Exception() {
		when(localeCodeRepository.findAll()).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = localeServiceImpl.getLocaleList();
		assertEquals(BffResponseCode.ERR_USER_LANGUAGE_CODE_SERVICE_API_FETCH_ALL_EXCEPTION.getCode(),
				response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	private List<Locale> getUserLanguageCodeList() {
		List<Locale> userLanguageCodeList = new ArrayList<>();
		Locale localeCode = new Locale();
		localeCode.setLocaleCode("EN");
		localeCode.setUid(UUID.randomUUID());

		userLanguageCodeList.add(localeCode);
		return userLanguageCodeList;
	}

}