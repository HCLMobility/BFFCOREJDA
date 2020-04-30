package com.jda.mobility.framework.extensions.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.config.ProductApiSettings;
import com.jda.mobility.framework.extensions.dto.AppConfigDto;
import com.jda.mobility.framework.extensions.dto.TranslationDto;
import com.jda.mobility.framework.extensions.entity.AppConfigDetail;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.ProductRole;
import com.jda.mobility.framework.extensions.model.ProductRolePermission;
import com.jda.mobility.framework.extensions.model.SearchRequest;
import com.jda.mobility.framework.extensions.repository.AppConfigMasterRepository;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;
import com.jda.mobility.framework.extensions.service.AutoCompleteService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.AppCfgRequestType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.SearchType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

/**
 * Implements Autocomplete feature at start , middle or end
 */
@Service
public class AutoCompleteServiceImpl implements AutoCompleteService {

	private static final Logger LOGGER = LogManager.getLogger(AutoCompleteServiceImpl.class);

	private static final String COOKIE_HEADER_NAME = "Cookie";

	@Autowired
	private BffResponse bffResponse;

	@Autowired
	private ResourceBundleRepository resourceBundleRepository;

	@Autowired
	private AppConfigMasterRepository appConfigRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SessionDetails sessionDetails;

	@Autowired
	private ProductApiSettings productApis;

	/**
	 *Auto Complete feature implementation for 
	 * - App Config Context , Global
	 * - Resource Bundles
	 * - Product Roles and Permissions(WMS)
	 */
	public BffCoreResponse search(SearchRequest searchRequest, String authCookie, String bearerToken) {
		BffCoreResponse bffCoreResponse = null;
		String searchType = searchRequest.getSearchType();
		if (!EnumUtils.isValidEnum(SearchType.class, searchType)) {
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FLOW_API_VALIDATE_ENUM_CHECK_SEARCH,
							BffResponseCode.ERR_FLOW_USER_VALIDATE_ENUM_CHECK_SEARCH),
					StatusCode.BADREQUEST, searchRequest.getSearchType(),
					searchRequest.getSearchType());
			return bffCoreResponse;
		}
		switch (SearchType.valueOf(searchType)) {
		case RESOURCE_BUNDLE:
			bffCoreResponse = searchResourceBundle(searchRequest.getSearchTerm());
			break;
		case APP_CONFIG_GLOBAL:
			bffCoreResponse = searchAppConfig(searchRequest.getSearchTerm(), AppCfgRequestType.GLOBAL.getType());
			break;
		case APP_CONFIG_CONTEXT:
			bffCoreResponse = searchAppConfig(searchRequest.getSearchTerm(), AppCfgRequestType.CONTEXT.getType());
			break;
		case PRODUCT_ROLE:
			bffCoreResponse = searchProductRole(authCookie, bearerToken, searchRequest.getSearchTerm());
			break;
		case PRODUCT_PERMISSION:
			bffCoreResponse = searchProductPermission(authCookie, bearerToken, searchRequest.getSearchTerm());
			break;
		}

		return bffCoreResponse;
	}

	/**Method for implementing auto complete feature in Resource_bundle table 
	 *  - Search characters at any place
	 *   
	 * @param searchTerm
	 * @return BffCoreResponse
	 */
	private BffCoreResponse searchResourceBundle(String searchTerm) {
		BffCoreResponse bffCoreResponse;
		try {
			List<ResourceBundle> resourceBundleList = resourceBundleRepository.search(searchTerm,
					sessionDetails.getLocale());
			List<TranslationDto> resourceBundleDtoList = new ArrayList<>();

			for (ResourceBundle translation : resourceBundleList) {
				resourceBundleDtoList.add(convertToResourceBundleDto(translation));
			}
			LOGGER.log(Level.DEBUG, "Total Number of records found : {}", resourceBundleDtoList.size());
			bffCoreResponse = bffResponse.response(resourceBundleDtoList,
					BffResponseCode.AUTOCOMPLETE_SERVICE_SUCCESS_CODE_RESOURCE_BUNDLE,
					BffResponseCode.AUTOCOMPLETE_SERVICE_USER_CODE_RESOURCE_BUNDLE, StatusCode.OK);
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_AUTOCOMPLETE_SERVICE_RESOURCE_BUNDLE_DBEXCEPTION,
							BffResponseCode.ERR_AUTOCOMPLETE_SERVICE_USER_RESOURCE_BUNDLE_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_AUTOCOMPLETE_SERVICE_RESOURCE_BUNDLE_EXCEPTION,
							BffResponseCode.ERR_AUTOCOMPLETE_SERVICE_USER_RESOURCE_BUNDLE_EXCEPTION),
					StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**Method for implementing auto complete feature in App_config_master table 
	 *  -  Search character at any places
	 *  
	 * @param searchTerm
	 * @param configType
	 * @return
	 */
	private BffCoreResponse searchAppConfig(String searchTerm, String configType) {
		BffCoreResponse bffCoreResponse;
		try {
			List<AppConfigMaster> appConfigList = appConfigRepository.search(searchTerm, configType);
			List<AppConfigDto> appConfigDtoList = new ArrayList<>();

			for (AppConfigMaster appConfigdata : appConfigList) {
				appConfigDtoList.add(convertToAppConfigDto(appConfigdata));
			}
			LOGGER.log(Level.DEBUG, "Total Number of config found : {}", appConfigDtoList.size());
			bffCoreResponse = bffResponse.response(appConfigDtoList,
					BffResponseCode.APP_CONFIG_AUTOCOMPLETE_SERVICE_SUCCESS_CODE_CONFIG_TYPE,
					BffResponseCode.APP_CONFIG_AUTOCOMPLETE_SERVICE_USER_CODE_CONFIG_TYPE, StatusCode.OK);
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.APP_CONFIG_ERR_AUTOCOMPLETE_SERVICE_CONFIG_TYPE_DBEXCEPTION,
							BffResponseCode.APP_CONFIG_ERR_AUTOCOMPLETE_SERVICE_USER_CONFIG_TYPE_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.APP_CONFIG_ERR_AUTOCOMPLETE_SERVICE_CONFIG_TYPE_EXCEPTION,
							BffResponseCode.APP_CONFIG_ERR_AUTOCOMPLETE_SERVICE_USER_CONFIG_TYPE_EXCEPTION),
					StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**Method to implement auto complete feature for WMS product roles
	 *  - Search characters at any place
	 *  
	 * @param authCookie
	 * @param searchTerm
	 * @return BffCoreResponse
	 */
	private BffCoreResponse searchProductRole(String authCookie, String bearerToken, String searchTerm) {
		BffCoreResponse bffCoreResponse;
		try {
			HttpHeaders headers = createHttpHeaders(authCookie, bearerToken);

			UriComponents uriComponents = productApis.rolesUrl().build();

			HttpEntity<String> request = new HttpEntity<>(headers);

			ResponseEntity<ProductRole> productRoleRes = restTemplate.exchange(uriComponents.toString(), HttpMethod.GET,
					request, ProductRole.class);

			ProductRole prdRole = new ProductRole();
			prdRole.setRoleIds(productRoleRes.getBody().getRoleIds().stream()
					.filter(currRole -> currRole.contains(searchTerm!=null?searchTerm:BffAdminConstantsUtils.EMPTY_SPACES))
					.collect(Collectors.toList()));

			bffCoreResponse = bffResponse.response(prdRole,
					BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_PRODUCT_USER_ROLES,
					BffResponseCode.ACCESS_SERVICE_USER_CODE_PRODUCT_USER_ROLES, StatusCode.OK);

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, searchTerm);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_ACCESS_SERVICE_API_PRODUCT_USER_ROLES,
					BffResponseCode.ERR_ACCESS_SERVICE_USER_PRODUCT_USER_ROLES), StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**Method to implement auto complete feature for WMS product permission
	 *  - Search characters at any place
	 *  
	 * @param authCookie
	 * @param searchTerm
	 * @return BffCoreResponse
	 */
	private BffCoreResponse searchProductPermission(String authCookie, String bearerToken, String searchTerm) {
		BffCoreResponse bffCoreResponse;
		try {
			HttpHeaders headers = createHttpHeaders(authCookie, bearerToken);

			HttpEntity<String> request = new HttpEntity<>(headers);
			
			UriComponents uriComponents = productApis.permissionsUrl()
					.queryParam("type", "MOBILE")
					.build();

			ResponseEntity<ProductRolePermission> responseEntity = restTemplate.exchange(uriComponents.toUriString(),
					HttpMethod.GET, request, ProductRolePermission.class);

			ProductRolePermission prdRolePerm = new ProductRolePermission();
			prdRolePerm.setPermissions(responseEntity.getBody().getPermissions().stream()
					.filter(currPerm -> currPerm.contains(searchTerm!=null?searchTerm:BffAdminConstantsUtils.EMPTY_SPACES))
					.collect(Collectors.toList()));

			bffCoreResponse = bffResponse.response(prdRolePerm,
					BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_PRODUCT_PERMISSIOMS,
					BffResponseCode.ACCESS_SERVICE_USER_CODE_PRODUCT_PERMISSIOMS, StatusCode.OK);

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, searchTerm);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse
					.errResponse(
							List.of(BffResponseCode.ERR_ACCESS_SERVICE_API_PRODUCT_PERMISSIOMS,
									BffResponseCode.ERR_ACCESS_SERVICE_USER_PRODUCT_PERMISSIOMS),
							StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	private HttpHeaders createHttpHeaders(String authCookie, String bearerToken) {
		HttpHeaders headers = new HttpHeaders();
		if (bearerToken != null) {
			headers.add(HttpHeaders.AUTHORIZATION, BffUtils.buildValidHeader(bearerToken));
		}
		else {
			headers.add(COOKIE_HEADER_NAME, BffUtils.buildValidHeader(authCookie));
		}
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	/**
	 * @param translation
	 * @return TranslationDto
	 */
	private TranslationDto convertToResourceBundleDto(ResourceBundle translation) {
		return new TranslationDto(translation.getUid().toString(), translation.getLocale(),
				translation.getRbkey(), translation.getRbvalue(), translation.getType());
	}

	/**
	 * @param appConfig
	 * @return AppConfigDto
	 */
	private AppConfigDto convertToAppConfigDto(AppConfigMaster appConfig) {
		AppConfigDetail appConfigDetail = new AppConfigDetail();
		return AppConfigDto.builder()
				.appConfigId(appConfig.getUid())
				.configName(appConfig.getConfigName())
				.configType(appConfig.getConfigType())
				.rawValue(appConfig.getRawValue())
				.configValue(appConfigDetail.getConfigValue())
				.flowId(appConfigDetail.getFlowId())
				.userId(appConfigDetail.getUserId())
				.description(appConfigDetail.getDescription())
				.build();
	}
}