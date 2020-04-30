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
import com.jda.mobility.framework.extensions.dto.TranslationDto;
import com.jda.mobility.framework.extensions.dto.TranslationRequest;
import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;
import com.jda.mobility.framework.extensions.service.TranslationService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 * The class that implements to create resource bundle as Key and Value pair 
 * along with locale , to fetch list of bundles by type and locale
 * 
 * 
 * @author HCL Technologies
 */
@Service
public class TranslationServiceImpl implements TranslationService {

	private static final Logger LOGGER = LogManager.getLogger(TranslationServiceImpl.class);

	@Autowired
	private ResourceBundleRepository resourceBundleRepo;

	@Autowired
	private BffResponse bffResponse;

	@Autowired
	private SessionDetails sessionDetails;

	/**
	 * Method to create/update new resource bundle.
	 * 
	 * @param translationRequest
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse createResourceBundle(TranslationRequest translationRequest) {
		BffCoreResponse bffCoreResponse = null;
		String rbkey = translationRequest.getRbkey();
		
		String locale = sessionDetails.getLocale() != null ? sessionDetails.getLocale() : BffAdminConstantsUtils.LOCALE;
		try {
			if (rbkey != null && !rbkey.isEmpty()
						&& translationRequest.getRbvalue()!= null && !translationRequest.getRbvalue().isEmpty()
						&& translationRequest.getLocale()!=null && !translationRequest.getLocale().isEmpty()) {
				ResourceBundle resourceBundleChk = resourceBundleRepo.findByRbkeyAndLocale(rbkey, locale);
				if (resourceBundleChk != null) {
					bffCoreResponse = bffResponse.errResponse(
							List.of(BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_API_CREATE_CHECK,
									BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_USER_CREATE_CHECK),
							StatusCode.INTERNALSERVERERROR);
				} else {
					ResourceBundle resourceBundle = resourceBundleRepo
							.save(convertToResourceBundleEntity(translationRequest, locale));
					TranslationDto translationDto = buildTranslationDto(resourceBundle);
					bffCoreResponse = bffResponse.response(translationDto,
							BffResponseCode.RESOURCE_BUNDLE_SERVICE_SUCCESS_CODE_CREATE,
							BffResponseCode.RESOURCE_BUNDLE_SERVICE_USER_CODE_CREATE, StatusCode.CREATED, null,
							resourceBundle.getUid().toString());
				}
			} else {
				bffCoreResponse = bffResponse.errResponse(
						List.of(BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_API_CREATE_CHECK_RBKEY_EMPTY,
								BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_USER_CREATE_CHECK_RBKEY_EMPTY),
						StatusCode.INTERNALSERVERERROR);
			}
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, translationRequest.getRbkey());
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_API_CREATE_DBEXCEPTION,
							BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_USER_CREATE_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, translationRequest.getRbkey());
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse
					.errResponse(
							List.of(BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_API_CREATE_EXCEPTION,
									BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_USER_CREATE_EXCEPTION),
							StatusCode.BADREQUEST);

		}
		return bffCoreResponse;
	}

	/**
	 * Retrieve list of resource bundle by type and locale
	 * 
	 * @param type
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse getResourceBundles(String type) {
		BffCoreResponse bffCoreResponse = null;
		List<TranslationDto> resourceBundleDtoList  = new ArrayList<>();
		
		String locale = sessionDetails.getLocale() != null ? sessionDetails.getLocale() : BffAdminConstantsUtils.LOCALE;
		
		try {
			List<ResourceBundle> resourceBundleList = resourceBundleRepo.findByLocaleAndTypeOrderByRbkeyAscRbvalueAsc(locale,type);
			
			//If bundle found for given locale and type , then prepare the list
			if(resourceBundleList!=null && !resourceBundleList.isEmpty())
			{
				prepareResBundleList(resourceBundleDtoList, resourceBundleList);
			}
			LOGGER.log(Level.DEBUG, "Total Number of registries returned : {}", resourceBundleDtoList.size());
			bffCoreResponse = bffResponse.response(resourceBundleDtoList,
					BffResponseCode.RESOURCE_BUNDLE_SERVICE_SUCCESS_CODE_FETCH,
					BffResponseCode.RESOURCE_BUNDLE_SERVICE_USER_CODE_FETCH, StatusCode.OK);

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, locale);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse
					.errResponse(
							List.of(BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_API_FETCH_DBEXCEPTION,
									BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_USER_FETCH_DBEXCEPTION),
							StatusCode.BADREQUEST);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, locale);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse
					.errResponse(
							List.of(BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_API_FETCH_EXCEPTION,
									BffResponseCode.ERR_RESOURCE_BUNDLE_SERVICE_USER_FETCH_EXCEPTION),
							StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**
	 * Build Translation DTO.
	 * 
	 * @param translation
	 * @return TranslationDto
	 */
	private TranslationDto buildTranslationDto(ResourceBundle translation) {
		return new TranslationDto(translation.getUid().toString(), translation.getLocale(),
				translation.getRbkey(), translation.getRbvalue(), translation.getType());

	}

	/**
	 * create ResourceBundle Entity Object.
	 * 
	 * @param translationRequest
	 * @return ResourceBundle
	 */
	private ResourceBundle convertToResourceBundleEntity(TranslationRequest translationRequest, String locale) {
		return ResourceBundle.builder()
				.locale(locale)
				.rbkey(translationRequest.getRbkey())
				.rbvalue(translationRequest.getRbvalue())
				.type(translationRequest.getType())
				.uid(translationRequest.getUid())
				.build();
	}

	/**
	 * Method to update locale in session
	 * 
	 * @param locale
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse updateLocale(String locale) {
		BffCoreResponse bffCoreResponse = null;
		try {
			sessionDetails.setLocale(locale);
			bffCoreResponse = bffResponse.response(locale, BffResponseCode.LOCALE_SUCCESS_CODE_UPDATE,
					BffResponseCode.LOCALE_SUCCESS_USER_CODE_UPDATE, StatusCode.OK);
			
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, locale);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.LOCALE_ERR_UPDATE_LOCALE,
					BffResponseCode.LOCALE_ERR_USER_CODE_UPDATE_LOCALE), StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**Method to fetch Mobile specific localization strings(Type : MOBILE)
	 *
	 */
	@Override
	public List<TranslationDto> getlocalizedResBundleEntries(String type) {

		List<TranslationDto> resourceBundleDtoList  = new ArrayList<>();
		
		String locale = sessionDetails.getLocale() != null ? sessionDetails.getLocale() : BffAdminConstantsUtils.LOCALE;
		
		//Fetch for specified locale
		List<ResourceBundle> resourceBundleList = resourceBundleRepo.findByLocaleAndTypeOrderByRbkeyAscRbvalueAsc(locale,type);
		
		//If bundle found for given locale and type , then prepare the list
		if(resourceBundleList!=null && !resourceBundleList.isEmpty())
		{
			prepareResBundleList(resourceBundleDtoList, resourceBundleList);
		}
		else
		{
			//If bundle are not found , then fetch for "en-US" considering as default
			List<ResourceBundle> resourceBundleListEnUs = resourceBundleRepo.findByLocaleAndTypeOrderByRbkeyAscRbvalueAsc(BffAdminConstantsUtils.LOCALE,type);
			prepareResBundleList(resourceBundleDtoList, resourceBundleListEnUs);
		}
		LOGGER.log(Level.DEBUG, "Total Number of variables returned : {}", resourceBundleDtoList.size());
		return resourceBundleDtoList;
	}

	private void prepareResBundleList(List<TranslationDto> resourceBundleDtoList,
			List<ResourceBundle> resourceBundleList) {
		for (ResourceBundle resourceBundle : resourceBundleList) {
			TranslationDto translationDto = buildTranslationDto(resourceBundle);
			resourceBundleDtoList.add(translationDto);
		}
	}

}
