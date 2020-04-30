package com.jda.mobility.framework.extensions.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jda.mobility.framework.extensions.config.ProductApiSettings;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.exception.DataBaseException;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.ProductRole;
import com.jda.mobility.framework.extensions.model.ProductRolePermission;
import com.jda.mobility.framework.extensions.model.SearchRequest;
import com.jda.mobility.framework.extensions.repository.AppConfigMasterRepository;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.AppCfgRequestType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * JUnit test class for AutoCompleteServiceImpl
 * 
 * @author HCL Technologies
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class AutoCompleteServiceImplTest extends AbstractPrepareTest {
	private static final String COOKIE_HEADER_NAME = "Cookie";

	@InjectMocks
	private AutoCompleteServiceImpl autoCompleteServiceImpl;

	@Mock
	private ResourceBundleRepository resourceBundleRepository;

	@Mock
	private AppConfigMasterRepository appConfigRepository;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private ResponseEntity<ProductRole> responseEntity;

	@Mock
	private ResponseEntity<ProductRolePermission> responseEntity1;

	@Mock
	private ProductApiSettings productApis;

	@Before
	public void setUp() {
		super.setUp();
		when(productApis.rolesUrl()).thenReturn(UriComponentsBuilder.fromHttpUrl("https://localhost/roles"));
		when(productApis.permissionsUrl()).thenReturn(UriComponentsBuilder.fromHttpUrl("https://localhost/permissions"));
	}

	@Test
	public void testSearch() {
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		BffCoreResponse response = autoCompleteServiceImpl.search(getSearchRequest(), authCookie, null);
		assertEquals(BffResponseCode.ERR_FLOW_API_VALIDATE_ENUM_CHECK_SEARCH.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testSearchResourceBundle() {
		String locale = "en";
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		List<ResourceBundle> rbList = new ArrayList<>();
		ResourceBundle rb = new ResourceBundle();
		rb.setLocale(BffAdminConstantsUtils.LOCALE);
		rb.setRbkey("Key");
		rb.setRbvalue("RbValue");
		rb.setUid(UUID.randomUUID());
		rb.setType(BffAdminConstantsUtils.EMPTY_SPACES);
		rbList.add(rb);
		when(resourceBundleRepository.search(getSearchRequestRB().getSearchTerm(), locale)).thenReturn(rbList);
		BffCoreResponse response = autoCompleteServiceImpl.search(getSearchRequestRB(), authCookie, null);
		assertEquals(BffResponseCode.AUTOCOMPLETE_SERVICE_SUCCESS_CODE_RESOURCE_BUNDLE.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testSearchResourceBundleDataAccessException() {
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		when(resourceBundleRepository.search(Mockito.any(), Mockito.any())).thenThrow(new DataBaseException("Resource Bundle retrieval failed"));
		BffCoreResponse response = autoCompleteServiceImpl.search(getSearchRequestRB(), authCookie, null);
		assertEquals(BffResponseCode.ERR_AUTOCOMPLETE_SERVICE_RESOURCE_BUNDLE_DBEXCEPTION.getCode(),
				response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testSearchResourceBundleException() {
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		when(resourceBundleRepository.search(Mockito.any(), Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = autoCompleteServiceImpl.search(getSearchRequestRB(), authCookie, null);
		assertEquals(BffResponseCode.ERR_AUTOCOMPLETE_SERVICE_RESOURCE_BUNDLE_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testSearchAppGlobalConfig() {
		String configType = AppCfgRequestType.GLOBAL.getType();
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		List<AppConfigMaster> appConfigList = new ArrayList<>();
		AppConfigMaster appConfig = new AppConfigMaster();
		appConfig.setUid(UUID.randomUUID());
		appConfigList.add(appConfig);
		when(appConfigRepository.search(getSearchRequestAPPConfigGlobal().getSearchTerm(), configType))
				.thenReturn(appConfigList);
		BffCoreResponse response = autoCompleteServiceImpl.search(getSearchRequestAPPConfigGlobal(), authCookie, null);
		assertEquals(BffResponseCode.APP_CONFIG_AUTOCOMPLETE_SERVICE_SUCCESS_CODE_CONFIG_TYPE.getCode(),
				response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testSearchAppConfigGlobalDataAccessException() {

		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		when(appConfigRepository.search(getSearchRequestAPPConfigGlobal().getSearchTerm(),
				AppCfgRequestType.GLOBAL.getType())).thenThrow(new DataBaseException("App configuration retrieval failed"));
		BffCoreResponse response = autoCompleteServiceImpl.search(getSearchRequestAPPConfigGlobal(), authCookie, null);
		assertEquals(BffResponseCode.APP_CONFIG_ERR_AUTOCOMPLETE_SERVICE_CONFIG_TYPE_DBEXCEPTION.getCode(),
				response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testSearchAppConfigGlobalException() {
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		when(appConfigRepository.search(getSearchRequestAPPConfigGlobal().getSearchTerm(),
				AppCfgRequestType.GLOBAL.getType())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = autoCompleteServiceImpl.search(getSearchRequestAPPConfigGlobal(), authCookie, null);
		assertEquals(BffResponseCode.APP_CONFIG_ERR_AUTOCOMPLETE_SERVICE_CONFIG_TYPE_EXCEPTION.getCode(),
				response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testSearchAppConfig() {
		String configType = AppCfgRequestType.CONTEXT.getType();
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		List<AppConfigMaster> appConfigList = new ArrayList<>();
		AppConfigMaster appConfig = new AppConfigMaster();
		appConfig.setUid(UUID.randomUUID());
		appConfigList.add(appConfig);
		when(appConfigRepository.search(getSearchRequestAPPConfig().getSearchTerm(), configType))
				.thenReturn(appConfigList);
		BffCoreResponse response = autoCompleteServiceImpl.search(getSearchRequestAPPConfig(), authCookie, null);
		assertEquals(BffResponseCode.APP_CONFIG_AUTOCOMPLETE_SERVICE_SUCCESS_CODE_CONFIG_TYPE.getCode(),
				response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testSearchAppConfigDataAccessException() {

		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		when(appConfigRepository.search(getSearchRequestAPPConfig().getSearchTerm(),
				AppCfgRequestType.CONTEXT.getType())).thenThrow(new DataBaseException("App configuration retrieval failed"));
		BffCoreResponse response = autoCompleteServiceImpl.search(getSearchRequestAPPConfig(), authCookie, null);
		assertEquals(BffResponseCode.APP_CONFIG_ERR_AUTOCOMPLETE_SERVICE_CONFIG_TYPE_DBEXCEPTION.getCode(),
				response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testSearchAppConfigException() {
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		when(appConfigRepository.search(getSearchRequestAPPConfig().getSearchTerm(),
				AppCfgRequestType.CONTEXT.getType())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = autoCompleteServiceImpl.search(getSearchRequestAPPConfig(), authCookie, null);
		assertEquals(BffResponseCode.APP_CONFIG_ERR_AUTOCOMPLETE_SERVICE_CONFIG_TYPE_EXCEPTION.getCode(),
				response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testSearchProductRole() {
		String locale = "en";
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";

		HttpHeaders headers = new HttpHeaders();
		headers.add(COOKIE_HEADER_NAME, BffUtils.buildValidHeader(authCookie));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(headers);
		when(responseEntity.getBody()).thenReturn(getPrdRoleDto());
		when(restTemplate.exchange("https://localhost/roles", HttpMethod.GET, request,
				ProductRole.class)).thenReturn(responseEntity);
		when(resourceBundleRepository.search(getSearchRequestPRole().getSearchTerm(), locale))
				.thenReturn(new ArrayList<>());
		BffCoreResponse response = autoCompleteServiceImpl.search(getSearchRequestPRole(), authCookie, null);
		assertEquals(BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_PRODUCT_USER_ROLES.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testSearchProductRoleException() {
		String locale = "en";
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		when(resourceBundleRepository.search(getSearchRequestPRole().getSearchTerm(), locale))
				.thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = autoCompleteServiceImpl.search(getSearchRequestPRole(), authCookie, null);
		assertEquals(BffResponseCode.ERR_ACCESS_SERVICE_API_PRODUCT_USER_ROLES.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testSearchProductPermission() {
		String locale = "en";
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";

		HttpHeaders headers = new HttpHeaders();
		headers.add(COOKIE_HEADER_NAME, BffUtils.buildValidHeader(authCookie));
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> request = new HttpEntity<>(headers);

		when(responseEntity1.getBody()).thenReturn(getPrdRolePermDto());
		when(restTemplate.exchange("https://localhost/permissions?type=MOBILE", HttpMethod.GET,
				request, ProductRolePermission.class)).thenReturn(responseEntity1);

		when(resourceBundleRepository.search(getSearchRequestPPerm().getSearchTerm(), locale))
				.thenReturn(new ArrayList<>());
		BffCoreResponse response = autoCompleteServiceImpl.search(getSearchRequestPPerm(), authCookie, null);
		assertEquals(BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_PRODUCT_PERMISSIOMS.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testSearchProductPermissionException() {
		String locale = "en";
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";

		when(resourceBundleRepository.search(getSearchRequestPPerm().getSearchTerm(), locale))
				.thenThrow(new RuntimeException());
		BffCoreResponse response = autoCompleteServiceImpl.search(getSearchRequestPPerm(), authCookie, null);
		assertEquals(BffResponseCode.ERR_ACCESS_SERVICE_API_PRODUCT_PERMISSIOMS.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	private SearchRequest getSearchRequest() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchTerm("id");
		searchRequest.setSearchType("TEST");
		return searchRequest;

	}

	private SearchRequest getSearchRequestRB() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchTerm("id");
		searchRequest.setSearchType("RESOURCE_BUNDLE");
		return searchRequest;

	}

	private SearchRequest getSearchRequestAPPConfigGlobal() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchTerm("id");
		searchRequest.setSearchType("APP_CONFIG_GLOBAL");
		return searchRequest;

	}

	private SearchRequest getSearchRequestAPPConfig() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchTerm("id");
		searchRequest.setSearchType("APP_CONFIG_CONTEXT");
		return searchRequest;

	}

	private SearchRequest getSearchRequestPRole() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchTerm("id");
		searchRequest.setSearchType("PRODUCT_ROLE");
		return searchRequest;

	}

	private ProductRole getPrdRoleDto() {
		ProductRole productRoleDto = new ProductRole();
		CopyOnWriteArrayList<String> roleIds = new CopyOnWriteArrayList<>();
		roleIds.add("");
		productRoleDto.setRoleIds(roleIds);
		return productRoleDto;
	}

	private SearchRequest getSearchRequestPPerm() {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.setSearchTerm("id");
		searchRequest.setSearchType("PRODUCT_PERMISSION");
		return searchRequest;

	}

	private ProductRolePermission getPrdRolePermDto() {
		ProductRolePermission productRolePermissionDto = new ProductRolePermission();
		CopyOnWriteArrayList<String> permissions = new CopyOnWriteArrayList<>();
		permissions.add("");
		productRolePermissionDto.setPermissions(permissions);
		return productRolePermissionDto;
	}

}
