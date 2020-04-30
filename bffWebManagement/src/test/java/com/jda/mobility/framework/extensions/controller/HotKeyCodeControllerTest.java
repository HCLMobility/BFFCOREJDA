package com.jda.mobility.framework.extensions.controller;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.jda.mobility.framework.extensions.dto.HotKeyCodeDto;
import com.jda.mobility.framework.extensions.dto.KeyEventDto;
import com.jda.mobility.framework.extensions.entity.HotKeyCode;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.service.impl.HotKeyCodeServiceImpl;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;


public class HotKeyCodeControllerTest extends AbstractBaseControllerTest {
	private static final String HOTKEY_CODE_URL = "/api/hotkey/v1/";

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private HotKeyCodeServiceImpl hotKeyCodeServiceImpl;
	
	@InjectMocks
	private HotKeyCodeController hotKeyCodeController;
	
	@Before
	public void beforeEach() {
		mockMvc = MockMvcBuilders.standaloneSetup(hotKeyCodeController).build();
	}


	@Test
	public void testgetHotKeyCodeList() throws Exception {
		StringBuilder url = new StringBuilder(HOTKEY_CODE_URL).append("list");
		List<HotKeyCodeDto> hotKeyCodeDtoList = Arrays.asList(createHotKeyCodeDto());
		when(hotKeyCodeServiceImpl.getHotKeyList()).thenReturn(buildResponse(hotKeyCodeDtoList));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()))
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].keyDescription").value("testKeyDescription"));
	}

	private HotKeyCode createHotKeyCode() {
		HotKeyCode hotKeyCode = new HotKeyCode();

		hotKeyCode.setKeyName("testKeyName");
		hotKeyCode.setKeyDisplayName("testKeyDisplayName");
		hotKeyCode.setKeyDescription("testKeyDescription");
		hotKeyCode.setCode("testCode");
		hotKeyCode.setAlt(false);
		hotKeyCode.setCtrl(false);
		hotKeyCode.setShift(false);
		hotKeyCode.setMetaKey(false);
	
		return hotKeyCode;
	}

	private HotKeyCodeDto createHotKeyCodeDto() {
		KeyEventDto keyEventDto = KeyEventDto.builder().code(createHotKeyCode().getCode())
				.ctrl(createHotKeyCode().isCtrl()).shift(createHotKeyCode().isShift()).alt(createHotKeyCode().isAlt())
				.metaKey(createHotKeyCode().isMetaKey()).build();

		HotKeyCodeDto hotKeyCodeDto = HotKeyCodeDto.builder().keyName(createHotKeyCode().getKeyName())
				.keyDisplayName(createHotKeyCode().getKeyDisplayName())
				.keyDescription(createHotKeyCode().getKeyDescription()).build();

		hotKeyCodeDto.setKeyEvent(keyEventDto);

		return hotKeyCodeDto;
	}

	private <T> BffCoreResponse buildResponse(T data) {
		return BffUtils.buildResponse(data, BffResponseCode.HOTKEY_SERVICE_SUCCESS_CODE_FETCH_ALL.getCode(),
				"message", "detailMessage", StatusCode.OK.getValue());
	}

	
	
	 
}
