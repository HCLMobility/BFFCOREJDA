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
import com.jda.mobility.framework.extensions.dto.LocaleDto;
import com.jda.mobility.framework.extensions.entity.Locale;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.repository.LocaleRepository;
import com.jda.mobility.framework.extensions.service.LocaleService;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;


/**
 *The class that retrieve list of locale supported by the product
 */
@Service
public class LocaleServiceImpl implements LocaleService {

	private static final Logger LOGGER = LogManager.getLogger(LocaleServiceImpl.class);

	@Autowired
	private LocaleRepository localeRepository;

	@Autowired
	private BffResponse bffResponse; 

	/**Retrieves the list the locale 
	 *
	 */
	@Override 
	public BffCoreResponse getLocaleList() {
		BffCoreResponse bffCoreResponse = null;  
		try {
			List<Locale> allLocaleCodes = localeRepository.findAll();
			List<LocaleDto> localeCodeDtoList = new ArrayList<>();

			for (Locale localeCode : allLocaleCodes) {
				LocaleDto localeCodeDto = convertToLocaleDto(localeCode);
				localeCodeDtoList.add(localeCodeDto);
			}
			LOGGER.log(Level.DEBUG, "Total Number of Mapped HotKeys are returned : {}", localeCodeDtoList.size());
			bffCoreResponse = bffResponse.response(localeCodeDtoList,
					BffResponseCode.USER_LANGUAGE_CODE_SERVICE_SUCCESS_CODE_FETCH_ALL,
					BffResponseCode.USER_LANGUAGE_CODE_SERVICE_USER_CODE_FETCH_ALL, StatusCode.OK);
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_USER_LANGUAGE_CODE_SERVICE_API_FETCH_ALL_DBEXCEPTION,
							BffResponseCode.ERR_USER_LANGUAGE_CODE_SERVICE_USER_FETCH_ALL_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse
					.errResponse(
							List.of(BffResponseCode.ERR_USER_LANGUAGE_CODE_SERVICE_API_FETCH_ALL_EXCEPTION,
									BffResponseCode.ERR_USER_LANGUAGE_CODE_SERVICE_USER_FETCH_ALL_EXCEPTION),
							StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	private LocaleDto convertToLocaleDto(Locale locale) {
		LocaleDto localeDto = LocaleDto.builder().localeCode(locale.getLocaleCode()).localeId(locale.getUid().toString()).build();
		return localeDto;
	}

}
