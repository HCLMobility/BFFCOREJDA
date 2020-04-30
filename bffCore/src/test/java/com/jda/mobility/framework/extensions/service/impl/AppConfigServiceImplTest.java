
package com.jda.mobility.framework.extensions.service.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.jda.mobility.framework.extensions.dto.AppConfigDto;
import com.jda.mobility.framework.extensions.entity.AppConfigDetail;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.entity.projection.AppConfigDetailDto;
import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.exception.DataBaseException;
import com.jda.mobility.framework.extensions.model.AppConfigRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.repository.AppConfigDetailRepository;
import com.jda.mobility.framework.extensions.repository.AppConfigMasterRepository;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.AppCfgRequestType;
import com.jda.mobility.framework.extensions.utils.BffCommonUtil;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 * The class AppConfigServiceImplTest.java
 */

@RunWith(SpringJUnit4ClassRunner.class)
public class AppConfigServiceImplTest extends AbstractPrepareTest {

	@InjectMocks
	private AppConfigServiceImpl appConfigServiceImpl;

	@Mock
	private AppConfigMasterRepository appConfigRepository;

	@Mock
	private AppConfigDetailRepository appConfigDetailRepository;

	@Spy
	private BffCommonUtil commonUtil = new BffCommonUtil();

	@Before
	public void setUpAppConfigService() {
		sessionDetails.setSessionId("df44ec7f-2dcc-4d76-8906-c615182fc851");
		sessionDetails.setLocale(BffAdminConstantsUtils.LOCALE);
		sessionDetails.setPrincipalName(BffAdminConstantsUtils.SUPER);
		sessionDetails.setVersion("1");
		sessionDetails.setChannel("MOBILE_RENDERER");
		sessionDetails.setTenant("SOURCE_A");
		sessionDetails.setPrdAuthCookie("COOKIE");
		ReflectionTestUtils.setField(commonUtil, "sessionDetails", sessionDetails);
	}

	@Test
	public void testCreateAppConfig() {
		BffCoreResponse response2 = appConfigServiceImpl.createAppConfigDefinition(getAppConfigRequest_invalidList());
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_CONTEXT_VARIABLES.getCode(), response2.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response2.getHttpStatusCode());
		when(appConfigRepository.save(Mockito.any())).thenReturn(getAppConfig());
		BffCoreResponse response = appConfigServiceImpl.createAppConfigDefinition(getAppConfigRequestList());
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_CONTEXT_VARIABLES.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
		when(appConfigRepository.save(Mockito.any())).thenReturn(getAppConfigGlobal());
		BffCoreResponse response1 = appConfigServiceImpl.createAppConfigDefinition(getAppConfigRequestGlobalList());
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_GLOBAL_VARIABLES.getCode(), response1.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
		when(appConfigRepository.save(Mockito.any())).thenReturn(getAppConfigInternal());
		BffCoreResponse response3 = appConfigServiceImpl.createAppConfigDefinition(getAppConfigRequestInternalList());
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_INTERNAL_VARIABLES.getCode(), response3.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
		when(appConfigRepository.save(Mockito.any())).thenReturn(getAppConfigApplication());
		BffCoreResponse response4 = appConfigServiceImpl
				.createAppConfigDefinition(getAppConfigRequestApplicationList());
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_APPLICATION_VARIABLES.getCode(), response4.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateAppConfigCheckGlobal() {
		when(appConfigRepository.findByConfigNameAndConfigType("Warehouseid1", "GLOBAL"))
				.thenReturn(getAppConfigGlobal());
		BffCoreResponse response = appConfigServiceImpl.createAppConfigDefinition(getAppConfigRequestGlobalList());
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_CREATION_CHECK.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateAppConfigCheckApplication() {
		when(appConfigRepository.findByConfigNameAndConfigType("Warehouseid1", "APPLICATION"))
				.thenReturn(getAppConfigApplication());
		BffCoreResponse response = appConfigServiceImpl.createAppConfigDefinition(getAppConfigRequestApplicationList());
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_CREATION_CHECK.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateAppConfigCheckInternal() {
		when(appConfigRepository.findByConfigNameAndConfigType("Warehouseid1", "INTERNAL"))
				.thenReturn(getAppConfigInternal());
		BffCoreResponse response = appConfigServiceImpl.createAppConfigDefinition(getAppConfigRequestInternalList());
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_CREATION_CHECK.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateAppConfigCheck() {
		when(appConfigRepository.findByConfigNameAndConfigType("Warehouseid1", "GLOBAL"))
				.thenReturn(getAppConfigGlobal());
		BffCoreResponse response1 = appConfigServiceImpl.createAppConfigDefinition(getAppConfigRequestGlobalList());
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_CREATION_CHECK.getCode(), response1.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response1.getHttpStatusCode());
	}

	@Test
	public void testCreateAppConfigDatabaseException() {
		when(appConfigRepository.findByConfigNameAndConfigType(Mockito.any(), Mockito.any()))
				.thenThrow(new DataBaseException("App configuration retrieval failed"));
		BffCoreResponse response = appConfigServiceImpl.createAppConfigDefinition(getAppConfigRequestList());
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_CREATION_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateAppConfigException() {
		List<AppConfigRequest> appConfigList = getAppConfigRequestList();
		appConfigList.get(0).setConfigType(null);
		BffCoreResponse response = appConfigServiceImpl.createAppConfigDefinition(appConfigList);
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_CREATION_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppConfigDefinitionByType() {
		when(appConfigRepository.findByConfigTypeOrderByConfigName(AppCfgRequestType.CONTEXT.getType()))
				.thenReturn(getListAppConfig());
		BffCoreResponse response = appConfigServiceImpl.getAppConfigDefinitionByType(AppCfgRequestType.CONTEXT);
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_CODE_FETCHALL.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppConfigDefinitionByType_DatabaseException() {
		when(appConfigRepository.findByConfigTypeOrderByConfigName(Mockito.any())).thenThrow(new DataBaseException("App configuration retrieval failed"));
		BffCoreResponse response = appConfigServiceImpl.getAppConfigDefinitionByType(AppCfgRequestType.CONTEXT);
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCHALL_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppConfigDefinitionByType_Exception() {
		when(appConfigRepository.findByConfigTypeOrderByConfigName(Mockito.any()))
				.thenThrow(new BffException("App configuration retrieval failed"));
		BffCoreResponse response = appConfigServiceImpl.getAppConfigDefinitionByType(AppCfgRequestType.CONTEXT);
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCHALL_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Implementation for fetch AppConfig details based on configType and configName
	 */
	@Test
	public void testGetAppConfig() {
		String configName = "test";
		when(appConfigRepository.findByConfigNameAndConfigType(configName, AppCfgRequestType.CONTEXT.getType()))
				.thenReturn(getAppConfig());
		BffCoreResponse response = appConfigServiceImpl.getAppConfig(configName, AppCfgRequestType.CONTEXT);
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_CODE_FETCH.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppConfigDatabaseException() {
		String configName = "test";
		when(appConfigRepository.findByConfigNameAndConfigType(Mockito.any(), Mockito.any()))
				.thenThrow(new DataBaseException("App configuration retrieval failed"));
		BffCoreResponse response = appConfigServiceImpl.getAppConfig(configName, AppCfgRequestType.CONTEXT);
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCH_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppConfigException() {
		String configName = "test";
		when(appConfigRepository.findByConfigNameAndConfigType(Mockito.any(), Mockito.any()))
				.thenThrow(new BffException("App configuration retrieval failed"));

		BffCoreResponse response = appConfigServiceImpl.getAppConfig(configName, AppCfgRequestType.CONTEXT);
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCH_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testUpdateAppConfig() {
		String rawValue = "testing";
		UUID uid = UUID.randomUUID();
		int count = 20;
		List<AppConfigRequest> appConfigRequestList = new ArrayList<>();
		AppConfigRequest appConfigRequest = new AppConfigRequest();
		appConfigRequestList.add(appConfigRequest);
		when(appConfigRepository.findByConfigNameAndConfigType(Mockito.any(), Mockito.any())).thenReturn(null);
		BffCoreResponse response2 = appConfigServiceImpl.updateAppConfigDefinition(appConfigRequestList);
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_UPDATE_VALUE.getCode(), response2.getCode());
		assertEquals(StatusCode.OK.getValue(), response2.getHttpStatusCode());
		when(appConfigRepository.updateAppConfigRawValue(uid, rawValue)).thenReturn(count);
		BffCoreResponse response = appConfigServiceImpl.updateAppConfigDefinition(getAppConfigRequestList());
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_UPDATE_VALUE.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
		when(appConfigRepository.updateAppConfigRawValue(Mockito.any(), Mockito.any())).thenReturn(count);
		BffCoreResponse response1 = appConfigServiceImpl.updateAppConfigDefinition(getAppConfigRequestGlobalList());
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_UPDATE_VALUE.getCode(), response1.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
		when(appConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(getAppConfigApplication()));
		
	}

	@Test
	public void testUpdateAppConfigINTERNAL() {
		String rawValue = "testing";
		UUID uid = UUID.randomUUID();
		int count = 20;
		when(appConfigRepository.updateAppConfigRawValue(uid, rawValue)).thenReturn(count);
		when(appConfigRepository.findByConfigNameAndConfigType(Mockito.any(), Mockito.any()))
				.thenReturn(getAppConfigInternal());
		BffCoreResponse response = appConfigServiceImpl.updateAppConfigDefinition(getAppConfigRequestListCheck());
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_CREATION_CHECK.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testUpdateAppConfigINTERNAL1() {
		String rawValue = "testing";
		UUID uid = UUID.randomUUID();
		int count = 20;
		AppConfigMaster appConfig = new AppConfigMaster();
		appConfig.setConfigName("Warehouseid1");
		appConfig.setConfigType("GLOBAL");
		appConfig.setRawValue("20");
		when(appConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(appConfig));
		when(appConfigDetailRepository.findByAppConfigMaster(Mockito.any()))
				.thenReturn(Optional.of(getAppConfigDetail()));
		when(appConfigRepository.updateAppConfigRawValue(uid, rawValue)).thenReturn(count);
		when(appConfigRepository.findByConfigNameAndConfigType(Mockito.any(), Mockito.any()))
				.thenReturn(getAppConfigInternal());
		BffCoreResponse response = appConfigServiceImpl.updateAppConfigDefinition(getAppConfigRequestInternalList());
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_UPDATE_VALUE.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testUpdateAppConfigException() {
		BffCoreResponse response = appConfigServiceImpl.updateAppConfigDefinition(null);
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_UPDATE_VALUE_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testUpdateAppConfigDBException() {
		when(appConfigRepository.updateAppConfigRawValue(Mockito.any(), Mockito.any()))
				.thenThrow(new DataBaseException("App configuration update failed"));
		BffCoreResponse response = appConfigServiceImpl.updateAppConfigDefinition(getAppConfigRequestList());
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_UPDATE_VALUE_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateUpdateAppConfigListGlobal() {
		AppConfigMaster appConfig = new AppConfigMaster();
		appConfig.setConfigName("Warehouseid1");
		appConfig.setConfigType("GLOBAL");
		appConfig.setRawValue("20");
		when(appConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(appConfig));
		when(appConfigDetailRepository.findByAppConfigMasterAndUserIdAndDeviceName(Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(Optional.of(getAppConfigDetail()));
		BffCoreResponse response = appConfigServiceImpl.createUpdateAppConfigList(getAppConfigRequestGlobalList());
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_UPDATE_VALUE.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateUpdateAppConfigListContext() {
		AppConfigMaster appConfig = new AppConfigMaster();
		appConfig.setConfigType("CONTEXT");
		when(appConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(appConfig));
		when(appConfigDetailRepository.findByAppConfigMasterAndUserIdAndDeviceName(Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(Optional.of(getAppConfigDetail()));
		BffCoreResponse response = appConfigServiceImpl.createUpdateAppConfigList(getAppConfigRequestContextList());
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_UPDATE_VALUE.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateUpdateAppConfigListInternal() {
		AppConfigMaster appConfig = new AppConfigMaster();
		appConfig.setConfigType("INTERNAL");
		when(appConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(appConfig));
		when(appConfigDetailRepository.findByAppConfigMasterAndUserIdAndDeviceName(Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(Optional.of(getAppConfigDetail()));
		BffCoreResponse response = appConfigServiceImpl.createUpdateAppConfigList(getAppConfigRequestInternalList());
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_UPDATE_VALUE.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateUpdateAppConfigList() {
		AppConfigMaster appConfig = new AppConfigMaster();
		appConfig.setConfigType("INTERNAL");
		when(appConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(appConfig));
		when(appConfigDetailRepository.findByAppConfigMasterAndUserIdAndDeviceName(Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(Optional.of(getAppConfigDetail()));
		BffCoreResponse response = appConfigServiceImpl.createUpdateAppConfigList(getAppConfigRequestInternalList());
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_UPDATE_VALUE.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateUpdateAppConfigListEmptyAppConfig() {
		when(appConfigRepository.findById(Mockito.any())).thenReturn(Optional.empty());
		when(appConfigDetailRepository.findByAppConfigMasterAndUserIdAndDeviceName(Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(Optional.of(getAppConfigDetail()));
		BffCoreResponse response = appConfigServiceImpl.createUpdateAppConfigList(getAppConfigRequestGlobalList());
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_UPDATE_CONFIGTYPE.getCode(), response.getCode());
		assertEquals(StatusCode.CONFLICT.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateUpdateAppConfigListEmptyAppConfigDetail() {
		AppConfigMaster appConfig = new AppConfigMaster();
		appConfig.setConfigType("INTERNAL");
		when(appConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(appConfig));
		when(appConfigDetailRepository.findByAppConfigMasterAndUserIdAndDeviceName(Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(Optional.empty());
		BffCoreResponse response = appConfigServiceImpl.createUpdateAppConfigList(getAppConfigRequestInternalList());
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_UPDATE_VALUE.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateUpdateAppConfigListException() {
		when(appConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(getAppConfigGlobal()));
		when(appConfigDetailRepository.findByAppConfigMasterAndUserIdAndDeviceName(Mockito.any(), Mockito.any(),
				Mockito.any())).thenThrow(new BffException("App configuration retrieval failed"));
		BffCoreResponse response = appConfigServiceImpl.createUpdateAppConfigList(getAppConfigRequestGlobalList());
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_UPDATE_VALUE_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateUpdateAppConfigListDBException() {
		when(appConfigRepository.findById(Mockito.any())).thenThrow(new DataBaseException("App configuration retrieval failed"));
		BffCoreResponse response = appConfigServiceImpl.createUpdateAppConfigList(getAppConfigRequestGlobalList());
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_UPDATE_VALUE_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testClearAppConfig() {
		String userId = "SUPER";
		when(appConfigRepository.findByConfigType(Mockito.any())).thenReturn(getListAppConfig());
		when(appConfigRepository.save(Mockito.any())).thenReturn(getUpdateAppConfig());
		BffCoreResponse response = appConfigServiceImpl.clearAppConfig(userId, "Device1");
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_CLEAR_VALUE.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testClearAppConfigDatabaseException() {
		String userId = "SUPER";
		when(appConfigRepository.fetchUserAndSpecificVariables(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new DataBaseException("App configuration retrieval failed"));
		BffCoreResponse response = appConfigServiceImpl.clearAppConfig(userId, "Device1");
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_CLEAR_VALUE_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testClearAppConfigException() {
		String userId = "SUPER";
		when(appConfigRepository.fetchUserAndSpecificVariables(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new BffException("App configuration retrieval failed"));
		BffCoreResponse response = appConfigServiceImpl.clearAppConfig(userId, "Device1");
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_CLEAR_VALUE_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppConfigAll() {
		when(appConfigRepository.findByConfigType("APPLICATION")).thenReturn(getListAppConfig());
		BffCoreResponse response = appConfigServiceImpl.getAppConfigList();
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_CODE_FETCHALL.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppConfigAllInternal() {
		when(appConfigRepository.findByConfigType("INTERNAL")).thenReturn(getListAppConfig_Internal());
		BffCoreResponse response = appConfigServiceImpl.getAppConfigList();
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_CODE_FETCHALL.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppConfigAllGlobal() {
		when(appConfigRepository.findByConfigTypeIn(Mockito.any())).thenReturn(getListAppConfig());
		BffCoreResponse response = appConfigServiceImpl.getAppConfigList();
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_CODE_FETCHALL.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppConfigAllContext() {
		when(appConfigRepository.findByConfigType("CONTEXT")).thenReturn(getAppConfigApplicationList());
		BffCoreResponse response = appConfigServiceImpl.getAppConfigList();
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_CODE_FETCHALL.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppConfigAllDatabaseException() {
		when(appConfigRepository.findByConfigTypeIn(Mockito.any())).thenThrow(new DataBaseException("App configuration retrieval failed"));
		BffCoreResponse response = appConfigServiceImpl.getAppConfigList();
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCHALL_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppConfigAllException() {
		when(appConfigRepository.findByConfigTypeIn(Mockito.any())).thenThrow(new BffException("App configuration retrieval failed"));
		BffCoreResponse response = appConfigServiceImpl.getAppConfigList();
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCHALL_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppConfigMobile() {
		when(appConfigRepository.findAll()).thenReturn(getAppConfigApplicationList());
		BffCoreResponse response = appConfigServiceImpl.getAppConfigList();
		assertEquals(BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_CODE_FETCHALL.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppConfigMobileDatabaseException() {
		when(appConfigRepository.findByConfigTypeIn(Mockito.any())).thenThrow(new DataBaseException("App configuration retrieval failed"));
		BffCoreResponse response = appConfigServiceImpl.getAppConfigList();
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCHALL_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppConfigMobileException() {
		when(appConfigRepository.findByConfigTypeIn(Mockito.any())).thenThrow(new BffException("App configuration retrieval failed"));
		BffCoreResponse response = appConfigServiceImpl.getAppConfigList();
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCHALL_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsList() {
		when(appConfigRepository.fetchAllAndUserAndDeviceSpecificVariables(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(getListAppConfigDto());
		List<AppConfigDto> response = appConfigServiceImpl.getAppSettingsList("Device1");
		assertTrue(response.size()>0);
	}

	private AppConfigMaster getAppConfig() {
		AppConfigMaster appConfig = new AppConfigMaster();
		appConfig.setConfigName("Warehouseid1");
		appConfig.setConfigType("CONTEXT");
		appConfig.setRawValue("20");
		return appConfig;
	}

	private List<AppConfigDetailDto> getListAppConfigDto() {
		List<AppConfigDetailDto> appConfigList = new ArrayList<>();
		AppConfigDetailDto dto = new AppConfigDetailDto(UUID.randomUUID());
		dto.setConfigName("Warehouseid1");
		dto.setConfigType(AppCfgRequestType.APPLICATION.getType());
		dto.setConfigValue("1637");
		dto.setDescription("Test1");
		dto.setFlowId(UUID.fromString("128c5740-d32d-47c7-97d8-a1819826e94e"));
		dto.setUserId("SUPER");
		appConfigList.add(dto);
		return appConfigList;
	}

	private List<AppConfigMaster> getListAppConfig() {
		List<AppConfigMaster> appConfigList = new ArrayList<>();
		AppConfigMaster appConfig = new AppConfigMaster();
		appConfig.setConfigName("Warehouseid1");
		appConfig.setConfigType(AppCfgRequestType.APPLICATION.getType());
		List<AppConfigDetail> appConfigDetails = new ArrayList<>();
		AppConfigDetail appConfigDetail = new AppConfigDetail();
		appConfigDetail.setConfigValue("1637");
		appConfigDetail.setDescription("Test1");
		appConfigDetail.setFlowId(UUID.fromString("128c5740-d32d-47c7-97d8-a1819826e94e"));
		appConfigDetail.setUserId("SUPER");
		appConfig.setAppConfigDetails(appConfigDetails);
		appConfigList.add(appConfig);
		return appConfigList;
	}

	private List<AppConfigMaster> getListAppConfig_Internal() {
		List<AppConfigMaster> appConfigList = new ArrayList<>();
		AppConfigMaster appConfig = new AppConfigMaster();
		appConfig.setConfigName("Warehouseid1");
		appConfig.setConfigType(AppCfgRequestType.INTERNAL.getType());
		List<AppConfigDetail> appConfigDetails = new ArrayList<>();
		AppConfigDetail appConfigDetail = new AppConfigDetail();
		appConfigDetail.setConfigValue("1637");
		appConfigDetail.setDescription("Test1");
		appConfigDetail.setFlowId(UUID.fromString("128c5740-d32d-47c7-97d8-a1819826e94e"));
		appConfigDetail.setUserId("SUPER");
		appConfig.setAppConfigDetails(appConfigDetails);
		appConfigList.add(appConfig);
		return appConfigList;
	}

	private AppConfigDetail getUpdateAppConfig() {
		AppConfigDetail appConfig = new AppConfigDetail();
		appConfig.setConfigValue("123");
		return appConfig;
	}

	private List<AppConfigRequest> getAppConfigRequest_invalidList() {
		List<AppConfigRequest> appConfigRequestList = new ArrayList<>();
		AppConfigRequest appConfigRequest = new AppConfigRequest();
		appConfigRequest.setAppConfigId(UUID.fromString("0953bf8d-8906-48bc-99c8-c492b01e8be6"));
		appConfigRequest.setConfigName("button");
		appConfigRequest.setConfigType(AppCfgRequestType.CONTEXT.getType());
		appConfigRequest.setConfigValue("1637");
		appConfigRequest.setRawValue("test");
		appConfigRequest.setDescription("Test1");
		appConfigRequest.setAppConfigMasterId(UUID.fromString("0953bf8d-8906-48bc-99c8-c492b01e8be6"));
		appConfigRequest.setFlowId(UUID.fromString("128c5740-d32d-47c7-97d8-a1819826e94e"));
		appConfigRequestList.add(appConfigRequest);
		return appConfigRequestList;
	}

	private List<AppConfigRequest> getAppConfigRequestGlobalList() {
		List<AppConfigRequest> appConfigRequestList = new ArrayList<>();
		AppConfigRequest appConfigRequest = new AppConfigRequest();
		appConfigRequest.setAppConfigId(UUID.fromString("0953bf8d-8906-48bc-99c8-c492b01e8be6"));
		appConfigRequest.setConfigName("Warehouseid1");
		appConfigRequest.setConfigType(AppCfgRequestType.GLOBAL.getType());
		appConfigRequest.setConfigValue("1637");
		appConfigRequest.setRawValue("test");
		appConfigRequest.setDescription("Test1");
		appConfigRequest.setAppConfigMasterId(UUID.fromString("0953bf8d-8906-48bc-99c8-c492b01e8be6"));
		appConfigRequest.setFlowId(UUID.fromString("128c5740-d32d-47c7-97d8-a1819826e94e"));
		appConfigRequestList.add(appConfigRequest);
		return appConfigRequestList;
	}

	private List<AppConfigRequest> getAppConfigRequestContextList() {
		List<AppConfigRequest> appConfigRequestList = new ArrayList<>();
		AppConfigRequest appConfigRequest = new AppConfigRequest();
		appConfigRequest.setAppConfigId(UUID.fromString("0953bf8d-8906-48bc-99c8-c492b01e8be6"));
		appConfigRequest.setConfigName("Warehouseid1");
		appConfigRequest.setConfigType(AppCfgRequestType.CONTEXT.getType());
		appConfigRequest.setConfigValue("1637");
		appConfigRequest.setRawValue("test");
		appConfigRequest.setDescription("Test1");
		appConfigRequest.setAppConfigMasterId(UUID.fromString("0953bf8d-8906-48bc-99c8-c492b01e8be6"));
		appConfigRequest.setFlowId(UUID.fromString("128c5740-d32d-47c7-97d8-a1819826e94e"));
		appConfigRequestList.add(appConfigRequest);
		return appConfigRequestList;
	}

	private List<AppConfigRequest> getAppConfigRequestListCheck() {
		List<AppConfigRequest> appConfigRequestList = new ArrayList<>();
		AppConfigRequest appConfigRequest = new AppConfigRequest();
		appConfigRequest.setConfigType(AppCfgRequestType.CONTEXT.getType());
		appConfigRequest.setConfigName("button");
		appConfigRequest.setConfigValue("1637");
		appConfigRequest.setRawValue("test");
		appConfigRequest.setDescription("Test1");
		appConfigRequest.setAppConfigMasterId(UUID.fromString("0953bf8d-8906-48bc-99c8-c492b01e8be6"));
		appConfigRequest.setFlowId(UUID.fromString("128c5740-d32d-47c7-97d8-a1819826e94e"));
		appConfigRequestList.add(appConfigRequest);
		return appConfigRequestList;
	}

	private List<AppConfigRequest> getAppConfigRequestList() {
		List<AppConfigRequest> appConfigRequestList = new ArrayList<>();
		AppConfigRequest appConfigRequest = new AppConfigRequest();
		appConfigRequest.setAppConfigId(UUID.fromString("0953bf8d-8906-48bc-99c8-c492b01e8be6"));
		appConfigRequest.setConfigName("button");
		appConfigRequest.setConfigType(AppCfgRequestType.CONTEXT.getType());
		appConfigRequest.setConfigValue("1637");
		appConfigRequest.setRawValue("test");
		appConfigRequest.setDescription("Test1");
		appConfigRequest.setAppConfigMasterId(UUID.fromString("0953bf8d-8906-48bc-99c8-c492b01e8be6"));
		appConfigRequest.setFlowId(UUID.fromString("128c5740-d32d-47c7-97d8-a1819826e94e"));
		appConfigRequestList.add(appConfigRequest);
		return appConfigRequestList;
	}

	private AppConfigMaster getAppConfigGlobal() {
		AppConfigMaster appConfig = new AppConfigMaster();
		appConfig.setConfigName("Warehouseid1");
		appConfig.setConfigType("GLOBAL");
		appConfig.setRawValue("20");
		return appConfig;
	}

	private List<AppConfigRequest> getAppConfigRequestInternalList() {
		List<AppConfigRequest> appConfigRequestList = new ArrayList<>();
		AppConfigRequest appConfigRequest = new AppConfigRequest();
		appConfigRequest.setAppConfigId(UUID.fromString("0953bf8d-8906-48bc-99c8-c492b01e8be6"));
		appConfigRequest.setConfigName("Warehouseid1");
		appConfigRequest.setConfigType(AppCfgRequestType.INTERNAL.getType());
		appConfigRequest.setConfigValue("1637");
		appConfigRequest.setRawValue("test");
		appConfigRequest.setDescription("Test1");
		appConfigRequest.setAppConfigMasterId(UUID.fromString("0953bf8d-8906-48bc-99c8-c492b01e8be6"));
		appConfigRequest.setFlowId(UUID.fromString("128c5740-d32d-47c7-97d8-a1819826e94e"));
		appConfigRequestList.add(appConfigRequest);
		return appConfigRequestList;
	}

	private AppConfigMaster getAppConfigInternal() {
		AppConfigMaster appConfig = new AppConfigMaster();
		appConfig.setConfigName("Warehouseid1");
		appConfig.setConfigType("INTERNAL");
		appConfig.setRawValue("10");
		return appConfig;
	}

	private AppConfigDetail getAppConfigDetail() {
		AppConfigDetail appConfig = new AppConfigDetail();
		AppConfigMaster appConfigMaster = new AppConfigMaster();
		appConfig.setConfigValue("test");
		appConfig.setAppConfigMaster(appConfigMaster);

		return appConfig;
	}

	private List<AppConfigRequest> getAppConfigRequestApplicationList() {
		List<AppConfigRequest> appConfigRequestList = new ArrayList<>();
		AppConfigRequest appConfigRequest = new AppConfigRequest();
		appConfigRequest.setAppConfigId(UUID.fromString("0953bf8d-8906-48bc-99c8-c492b01e8be6"));
		appConfigRequest.setConfigName("Warehouseid1");
		appConfigRequest.setConfigType(AppCfgRequestType.APPLICATION.getType());
		appConfigRequest.setConfigValue("1637");
		appConfigRequest.setRawValue("test");
		appConfigRequest.setDescription("Test1");
		appConfigRequest.setAppConfigMasterId(UUID.fromString("0953bf8d-8906-48bc-99c8-c492b01e8be6"));
		appConfigRequest.setFlowId(UUID.fromString("128c5740-d32d-47c7-97d8-a1819826e94e"));
		appConfigRequestList.add(appConfigRequest);
		return appConfigRequestList;
	}

	private AppConfigMaster getAppConfigApplication() {
		AppConfigMaster appConfig = new AppConfigMaster();
		appConfig.setConfigName("Warehouseid1");
		appConfig.setConfigType("APPLICATION");
		appConfig.setRawValue("10");
		return appConfig;
	}

	private List<AppConfigMaster> getAppConfigApplicationList() {
		List<AppConfigMaster> appConfigRequestList = new ArrayList<>();
		AppConfigMaster appConfigMaster = new AppConfigMaster();
		appConfigMaster.setConfigName("Warehouseid1");
		appConfigMaster.setConfigType("APPLICATION");
		appConfigMaster.setRawValue("10");
		appConfigRequestList.add(appConfigMaster);
		return appConfigRequestList;
	}
}
