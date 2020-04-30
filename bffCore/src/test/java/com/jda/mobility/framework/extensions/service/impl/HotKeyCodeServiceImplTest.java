package com.jda.mobility.framework.extensions.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jda.mobility.framework.extensions.entity.HotKeyCode;
import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.exception.DataBaseException;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.repository.HotKeyCodeRepository;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 * The class HotKeyCodeServiceImplTest.java
 */

@RunWith(SpringJUnit4ClassRunner.class)
public class HotKeyCodeServiceImplTest extends AbstractPrepareTest {

	@InjectMocks
	private HotKeyCodeServiceImpl hotKeyCodeServiceImpl;

	@Mock
	private HotKeyCodeRepository hotKeyCodeRepository;

	@Test
	public void testGetHotKeyCodeList() {
		when(hotKeyCodeRepository.findAllByOrderByTypeAscSequenceAsc()).thenReturn(getHotKeyCodeListGlobal());
		BffCoreResponse response = hotKeyCodeServiceImpl.getHotKeyList();
		assertEquals(BffResponseCode.HOTKEY_SERVICE_SUCCESS_CODE_FETCH_ALL.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetHotKeyCodeListElseGlobal() {
		when(hotKeyCodeRepository.findAllByOrderByTypeAscSequenceAsc()).thenReturn(getHotKeyCodeListGlobal());
		when(sessionDetails.getChannel()).thenReturn("ADMIN_UI");
		BffCoreResponse response = hotKeyCodeServiceImpl.getHotKeyList();
		assertEquals(BffResponseCode.HOTKEY_SERVICE_SUCCESS_CODE_FETCH_ALL.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetHotKeyCodeListElseContext() {
		when(hotKeyCodeRepository.findAllByOrderByTypeAscSequenceAsc()).thenReturn(getHotKeyCodeListContext());
		when(sessionDetails.getChannel()).thenReturn("ADMIN_UI");
		BffCoreResponse response = hotKeyCodeServiceImpl.getHotKeyList();
		assertEquals(BffResponseCode.HOTKEY_SERVICE_SUCCESS_CODE_FETCH_ALL.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetHotKeyCodeList_DatabaseException() {
		when(hotKeyCodeRepository.findAllByOrderByTypeAscSequenceAsc()).thenThrow(new DataBaseException("Hot key retrieval failed"));
		BffCoreResponse response = hotKeyCodeServiceImpl.getHotKeyList();
		assertEquals(BffResponseCode.ERR_HOTKEY_SERVICE_API_FETCH_ALL_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetHotKeyCodeList_Exception() {
		when(hotKeyCodeRepository.findAllByOrderByTypeAscSequenceAsc()).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = hotKeyCodeServiceImpl.getHotKeyList();
		assertEquals(BffResponseCode.ERR_HOTKEY_SERVICE_API_FETCH_ALL_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	private List<HotKeyCode> getHotKeyCodeListGlobal() {
		List<HotKeyCode> hotKeyCodeList = new ArrayList<>();
		HotKeyCode hotKeyCode = new HotKeyCode();
		hotKeyCode.setKeyName("testKeyName");
		hotKeyCode.setKeyDisplayName("testKeyDisplayName");
		hotKeyCode.setKeyDescription("testKeyDescription");
		hotKeyCode.setAlt(true);
		hotKeyCode.setShift(true);
		hotKeyCode.setCtrl(false);
		hotKeyCode.setMetaKey(true);
		hotKeyCode.setType("GLOBAL");
		hotKeyCodeList.add(hotKeyCode);
		return hotKeyCodeList;
	}

	private List<HotKeyCode> getHotKeyCodeListContext() {
		List<HotKeyCode> hotKeyCodeList = new ArrayList<>();
		HotKeyCode hotKeyCode = new HotKeyCode();
		hotKeyCode.setKeyName("testKeyName");
		hotKeyCode.setKeyDisplayName("testKeyDisplayName");
		hotKeyCode.setKeyDescription("testKeyDescription");
		hotKeyCode.setAlt(true);
		hotKeyCode.setShift(true);
		hotKeyCode.setCtrl(false);
		hotKeyCode.setMetaKey(true);
		hotKeyCode.setType("CONTEXT");
		hotKeyCodeList.add(hotKeyCode);
		return hotKeyCodeList;
	}

}