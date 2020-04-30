package com.jda.mobility.framework.extensions.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.dto.HotKeyCodeDto;
import com.jda.mobility.framework.extensions.dto.HotKeyCodeListDto;
import com.jda.mobility.framework.extensions.dto.KeyEventDto;
import com.jda.mobility.framework.extensions.entity.HotKeyCode;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.repository.HotKeyCodeRepository;
import com.jda.mobility.framework.extensions.service.HotKeyCodeService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.AppCfgRequestType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 *The class that retrieve list of hot keys supported by the product
 */
@Service
public class HotKeyCodeServiceImpl implements HotKeyCodeService {

	private static final Logger LOGGER = LogManager.getLogger(HotKeyCodeServiceImpl.class);

	@Autowired
	private HotKeyCodeRepository hotKeyCodeRepository;

	@Autowired
	private BffResponse bffResponse;

	@Autowired
	private SessionDetails sessionDetails;

	/**Retrieves list of hot keys order by sequence
	 *
	 */
	@Override
	public BffCoreResponse getHotKeyList() {
		BffCoreResponse bffCoreResponse = null;
		try {
			List<HotKeyCode> allHotKeyCodes = hotKeyCodeRepository.findAllByOrderByTypeAscSequenceAsc();
			Object response = null;

			if (sessionDetails != null && sessionDetails.getChannel()
					.equals(BffAdminConstantsUtils.ChannelType.MOBILE_RENDERER.getType())) {
				List<HotKeyCodeDto> hotKeyCodeDtoList = new ArrayList<>();
				for (HotKeyCode hotKeyCode : allHotKeyCodes) {
					HotKeyCodeDto hotKeyCodeDto = convertToHotKeyMapDto(hotKeyCode, true);
					hotKeyCodeDtoList.add(hotKeyCodeDto);
				}

				LOGGER.log(Level.DEBUG, "Total Number of Mapped HotKeys are returned : {}", hotKeyCodeDtoList.size());
				response = hotKeyCodeDtoList;
			} else {
				List<HotKeyCodeDto> globalCodeDtoList = new ArrayList<>();
				List<HotKeyCodeDto> contextCodeDtoList = new ArrayList<>();

				for (HotKeyCode hotKeyCode : allHotKeyCodes) {
					HotKeyCodeDto hotKeyCodeDto = convertToHotKeyMapDto(hotKeyCode, false);

					if (hotKeyCode.getType().equals(AppCfgRequestType.GLOBAL.getType())) {
						globalCodeDtoList.add(hotKeyCodeDto);
					} else {
						contextCodeDtoList.add(hotKeyCodeDto);
					}
				}
				HotKeyCodeListDto hotKeyCodeList = HotKeyCodeListDto.builder()
						.contextHotKeyList(contextCodeDtoList)
						.globalHotKeyList(globalCodeDtoList)
						.build();
				response = hotKeyCodeList;
			}

			bffCoreResponse = bffResponse.response(response, BffResponseCode.HOTKEY_SERVICE_SUCCESS_CODE_FETCH_ALL,
					BffResponseCode.HOTKEY_SERVICE_USER_CODE_FETCH_ALL, StatusCode.OK);
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_HOTKEY_SERVICE_API_FETCH_ALL_DBEXCEPTION,
							BffResponseCode.ERR_HOTKEY_SERVICE_USER_FETCH_ALL_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse
					.errResponse(
							List.of(BffResponseCode.ERR_HOTKEY_SERVICE_API_FETCH_ALL_EXCEPTION,
									BffResponseCode.ERR_HOTKEY_SERVICE_USER_FETCH_ALL_EXCEPTION),
							StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	private HotKeyCodeDto convertToHotKeyMapDto(HotKeyCode hotKeyCode, boolean required) {

		KeyEventDto keyEventDto = null;
		if (required) {
			keyEventDto = KeyEventDto.builder()
					.code(hotKeyCode.getCode())
					.ctrl(hotKeyCode.isCtrl())
					.shift(hotKeyCode.isShift())
					.alt(hotKeyCode.isAlt())
					.metaKey(hotKeyCode.isMetaKey())
					.build();
		}

		HotKeyCodeDto hotKeyCodeDto = HotKeyCodeDto.builder()
				.keyName(hotKeyCode.getKeyName())
				.keyDisplayName(hotKeyCode.getKeyDisplayName())
				.keyDescription(hotKeyCode.getKeyDescription())
				.build();

		hotKeyCodeDto.setKeyEvent(keyEventDto);

		return hotKeyCodeDto;

	}

	/**Retrieves list of hot keys for Mobile
	 *
	 */
	public List<HotKeyCodeDto> getHotKeyCodeDtoList() {
		List<HotKeyCode> allHotKeyCodes = hotKeyCodeRepository.findAll();
		List<HotKeyCodeDto> hotKeyCodeDtoList = new ArrayList<>();
		for (HotKeyCode hotKeyCode : allHotKeyCodes) {
			HotKeyCodeDto hotKeyCodeDto = convertToHotKeyMapDto(hotKeyCode, true);
			hotKeyCodeDtoList.add(hotKeyCodeDto);
		}
		return hotKeyCodeDtoList;
	}
}