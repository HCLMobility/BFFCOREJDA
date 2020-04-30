/**
 * 
 */
package com.jda.mobility.framework.extensions.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.jda.mobility.framework.extensions.entity.AppConfigDetail;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.entity.ProductMaster;
import com.jda.mobility.framework.extensions.entity.ProductProperty;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.entity.UserRole;
import com.jda.mobility.framework.extensions.entity.projection.AppConfigDetailDto;
import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.exception.DataBaseException;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.PrepRequest;
import com.jda.mobility.framework.extensions.repository.AppConfigMasterRepository;
import com.jda.mobility.framework.extensions.repository.FlowRepository;
import com.jda.mobility.framework.extensions.repository.FormRepository;
import com.jda.mobility.framework.extensions.repository.ProductConfigRepository;
import com.jda.mobility.framework.extensions.repository.ProductMasterRepository;
import com.jda.mobility.framework.extensions.repository.ProductPropertyRepository;
import com.jda.mobility.framework.extensions.repository.RoleMasterRepository;
import com.jda.mobility.framework.extensions.repository.UserRoleRepository;
import com.jda.mobility.framework.extensions.service.AppConfigService;
import com.jda.mobility.framework.extensions.service.HotKeyCodeService;
import com.jda.mobility.framework.extensions.service.TranslationService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.AppCfgRequestType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 * The class ProductPrepareServiceImplTest.java
 * 
 * @author HCL Technologies Ltd.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ProductPrepareServiceImplTest extends AbstractPrepareTest {
	@InjectMocks
	private ProductPrepareServiceImpl productPrepareServiceImpl;
	@Mock
	private AppConfigMasterRepository appConfigRepository;
	@Mock
	private FlowRepository flowRepository;
	@Mock
	private ProductMasterRepository productMasterRepository;
	@Mock
	private ProductPropertyRepository productPropertyRepository;
	@Mock
	private RoleMasterRepository roleMasterRepository;
	@Mock
	private ProductConfigRepository productConfigRepository;
	@Mock
	private UserRoleRepository userRoleRepository;
	@Mock
	private FormRepository formRepository;
	@Mock
	private ProductMasterRepository productMasterRepo;
	@Mock
	private AppConfigService appConfigService;
	@Mock
	private AppConfigServiceImpl appConfigServiceImpl;
	@Mock
	private HotKeyCodeService hotKeyCodeService;
	@Mock
	private TranslationService translationService;

	/**
	 * 
	 */
	@Test
	public void testCreateDetaultFlow() {
		List<AppConfigMaster> appconfiglist = new ArrayList<>();
		AppConfigMaster appconfig = new AppConfigMaster();
		appconfiglist.add(appconfig);
		when(appConfigRepository.findByConfigName(Mockito.anyString())).thenReturn(appconfiglist);
		BffCoreResponse response = productPrepareServiceImpl.fetchDefaultFlowConfig("test");
		assertEquals(BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_DEFAULT_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	/**
	 * 
	 */
	@Test
	public void testCreateDetaultFlowException() {
		when(appConfigRepository.findByConfigName(Mockito.anyString()))
				.thenThrow(new BffException(new BffException(BffAdminConstantsUtils.EXP_MSG)));
		BffCoreResponse response = productPrepareServiceImpl.fetchDefaultFlowConfig("test");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * 
	 */
	@Test
	public void testCreateDetaultFlowDataBaseException() {
		when(appConfigRepository.findByConfigName(Mockito.anyString()))
				.thenThrow(new DataBaseException("Application Config retrieval failed"));
		BffCoreResponse response = productPrepareServiceImpl.fetchDefaultFlowConfig("test");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * 
	 */
	@Test
	public void testFetchDashboardFlows() {
		ProductMaster productMaster = new ProductMaster();
		ProductProperty productProperty = new ProductProperty();
		productProperty.setUid(UUID.randomUUID());
		PrepRequest prepRequest = getPrepRequest();
		prepRequest.setPropValue("name");
		List<ProductProperty> plist = new ArrayList<>();
		plist.add(productProperty);
		List<ProductProperty> plis1t1 = new ArrayList<>();
		plis1t1.add(productProperty);
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());

		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setLevel(1);
		productConfig.setRoleMaster(roleMaster);
		List<ProductConfig> productconfiglist = new ArrayList<>();
		productconfiglist.add(productConfig);
		when(productConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(productConfig));
		when(productConfigRepository.findBySecondaryRefId(productProperty.getUid())).thenReturn(productconfiglist);
		when(productPropertyRepository.findByNameAndPropValueAndProductMasterAndIsSecondaryRefTrue(Mockito.anyString(),
				Mockito.anyString(), Mockito.any())).thenReturn(plis1t1);
		when(roleMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(new RoleMaster()));
		when(productMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(productMaster));
		when(userRoleRepository.findByUserId(Mockito.any())).thenReturn(Optional.of(getUserRole()));
		List<Flow> flowList = new ArrayList<>();
		Flow flow = new Flow();
		flow.setDefaultFormId(UUID.randomUUID());
		flow.setUid(UUID.randomUUID());
		flow.setCreatedBy("SUPER");

		flow.setDescription("DESC");
		flow.setDisabled(false);
		flow.setExtDisabled(false);
		flow.setPublished(false);
		flow.setProductConfig(productConfig);
		flow.setTag("TAG1");
		flow.setVersion(1);
		flow.setExtendedFromFlowId(UUID.randomUUID());
		flowList.add(flow);
		when(flowRepository.findByProductConfigInOrderByLastModifiedDateDesc(Mockito.any())).thenReturn(flowList);
		BffCoreResponse response = productPrepareServiceImpl.fetchDashboardFlows(prepRequest);
		assertEquals(BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_DASHBOARD_FLOWS.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchDashboardFlowsDatabaseException() {
		PrepRequest prepRequest = getPrepRequest();
		prepRequest.setPropValue("name");
		when(userRoleRepository.findByUserId(Mockito.any()))
				.thenThrow(new DataBaseException("Dashboard flows retrieval failed"));
		BffCoreResponse response = productPrepareServiceImpl.fetchDashboardFlows(prepRequest);
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DASHBOARD_FLOWS_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchDashboardFlowsException() {
		PrepRequest prepRequest = getPrepRequest();
		prepRequest.setPropValue("name");
		when(userRoleRepository.findByUserId(Mockito.anyString()))
				.thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		when(flowRepository.findByProductConfigInOrderByLastModifiedDateDesc(Mockito.any()))
				.thenReturn(new ArrayList<>());
		BffCoreResponse response = productPrepareServiceImpl.fetchDashboardFlows(prepRequest);
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DASHBOARD_FLOWS_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchDashboardFlowsDataBaseException() {
		when(userRoleRepository.findByUserId(Mockito.any()))
				.thenThrow(new DataBaseException("Dashboard flows retrieval failed"));
		BffCoreResponse response = productPrepareServiceImpl.fetchDashboardFlows(getPrepRequest());
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DASHBOARD_FLOWS_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * 
	 */
	@Test
	public void testFetchProductConfigId() {
		PrepRequest prepRequest = getPrepRequest();
		prepRequest.setIsDefaultWarehouse(true);
		prepRequest.setPropValue("name");
		ProductProperty productProperty = new ProductProperty();
		productProperty.setUid(UUID.randomUUID());
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setLevel(1);
		UserRole userRole = new UserRole();
		userRole.setRoleMaster(roleMaster);
		when(productConfigRepository.save(Mockito.any())).thenReturn(productConfig);
		when(productPropertyRepository.save(Mockito.any())).thenReturn(productProperty);
		when(roleMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(roleMaster));
		when(productMasterRepository.findByName(Mockito.anyString())).thenReturn(new ProductMaster());
		when(userRoleRepository.findByUserId(Mockito.anyString())).thenReturn(Optional.of(userRole));
		BffCoreResponse response = productPrepareServiceImpl.fetchProductConfigId(prepRequest);
		assertEquals(BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_FETCH_CONFIGID.getCode(), response.getCode());
		prepRequest.setPropValue("name_DEFAULT");
		BffCoreResponse response1 = productPrepareServiceImpl.fetchProductConfigId(prepRequest);
		assertEquals(BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_FETCH_CONFIGID.getCode(), response1.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchProductConfigIdElse() {
		PrepRequest prepRequest = getPrepRequest();
		prepRequest.setPropValue("name");
		ProductProperty productProperty = new ProductProperty();
		productProperty.setUid(UUID.randomUUID());
		List<ProductProperty> ppList = new ArrayList<>();
		ppList.add(productProperty);
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setLevel(1);
		UserRole userRole = new UserRole();
		userRole.setRoleMaster(roleMaster);
		ProductMaster prodMaster = new ProductMaster();
		when(userRoleRepository.findByUserId(Mockito.anyString())).thenReturn(Optional.of(userRole));
		when(productPropertyRepository.findByNameAndPropValueAndProductMasterAndIsSecondaryRefTrue(
				prepRequest.getName(), prepRequest.getPropValue(), prodMaster)).thenReturn(ppList);
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		when(productConfigRepository.findBySecondaryRefIdAndRoleMaster(productProperty.getUid(), roleMaster))
				.thenReturn(productConfig);
		when(productConfigRepository.save(Mockito.any())).thenReturn(productConfig);
		when(productPropertyRepository.save(Mockito.any())).thenReturn(productProperty);
		when(roleMasterRepository.findByName(Mockito.any())).thenReturn(roleMaster);
		when(productMasterRepository.findByName(Mockito.any())).thenReturn(prodMaster);
		BffCoreResponse response = productPrepareServiceImpl.fetchProductConfigId(prepRequest);
		assertEquals(BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_FETCH_CONFIGID.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchProductConfigIdNotFound() {
		PrepRequest prepRequest = getPrepRequest();
		prepRequest.setPropValue("name_DEFAULT");
		ProductProperty productProperty = new ProductProperty();
		productProperty.setUid(UUID.randomUUID());
		List<ProductProperty> ppList = new ArrayList<>();
		ppList.add(productProperty);
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setLevel(1);
		UserRole userRole = new UserRole();
		userRole.setRoleMaster(roleMaster);
		when(userRoleRepository.findByUserId(Mockito.anyString())).thenReturn(Optional.of(userRole));
		ProductMaster prodMaster = new ProductMaster();
		when(productMasterRepo.findByName(BffAdminConstantsUtils.PRODUCT_MASTER_CODE)).thenReturn(prodMaster);
		when(productPropertyRepository.findByNameAndPropValueAndProductMasterAndIsSecondaryRefTrue(
				prepRequest.getName(), prepRequest.getPropValue(), prodMaster)).thenReturn(ppList);
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		when(productConfigRepository.findBySecondaryRefIdAndRoleMaster(productProperty.getUid(), roleMaster))
				.thenReturn(productConfig);
		when(productConfigRepository.save(Mockito.any())).thenReturn(productConfig);
		when(productPropertyRepository.save(Mockito.any())).thenReturn(productProperty);
		when(roleMasterRepository.findByName(Mockito.any())).thenReturn(roleMaster);
		when(productMasterRepository.findByName(Mockito.any())).thenReturn(new ProductMaster());
		BffCoreResponse response = productPrepareServiceImpl.fetchProductConfigId(prepRequest);
		assertEquals(BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_FETCH_CONFIGID.getCode(), response.getCode());
		when(productConfigRepository.findBySecondaryRefIdAndRoleMaster(productProperty.getUid(), roleMaster))
				.thenReturn(null);
		BffCoreResponse response1 = productPrepareServiceImpl.fetchProductConfigId(prepRequest);
		assertEquals(BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_FETCH_CONFIGID.getCode(), response1.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchProductConfigIdDataBaseException() {
		PrepRequest prepRequest = getPrepRequest();
		prepRequest.setPropValue("name_DEFAULT");
		when(userRoleRepository.findByUserId(Mockito.any()))
				.thenThrow(new DataBaseException("Product Configuration retrieval failed for given Id"));
		BffCoreResponse response = productPrepareServiceImpl.fetchProductConfigId(prepRequest);
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_FETCH_CONFIGID_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchProductConfigIdException() {
		PrepRequest prepRequest = getPrepRequest();
		prepRequest.setPropValue("name_DEFAULT");
		when(productMasterRepository.findByName(Mockito.any()))
				.thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = productPrepareServiceImpl.fetchProductConfigId(prepRequest);
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_FETCH_CONFIGID_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testLayeredProductConfigList() {
		when(userRoleRepository.findByUserId(Mockito.anyString())).thenReturn(Optional.of(getUserRole()));
		ProductMaster prodMaster = new ProductMaster();
		when(productMasterRepo.findByName(Mockito.any())).thenReturn(prodMaster);
		ProductProperty productProperty = new ProductProperty();
		productProperty.setUid(UUID.randomUUID());
		List<ProductProperty> ppList = new ArrayList<>();
		ppList.add(productProperty);
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setLevel(1);
		when(productPropertyRepository.findByNameAndPropValueAndProductMasterAndIsSecondaryRefTrue(
				BffAdminConstantsUtils.PRODUCT_MASTER_CODE, BffAdminConstantsUtils.WAREHOUSE_DEFAULT, prodMaster))
						.thenReturn(ppList);
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		productConfig.setRoleMaster(roleMaster);
		List<ProductConfig> prodConfigList = new ArrayList<>();
		prodConfigList.add(productConfig);
		when(productConfigRepository.findBySecondaryRefId(productProperty.getUid())).thenReturn(prodConfigList);
		List<ProductConfig> prodList = productPrepareServiceImpl.getLayeredProductConfigList();
		Assert.notEmpty(prodList, "ProductConfigList must not be null or empty");
		assertEquals(getUserRole().getRoleMaster().getLevel(), prodList.get(0).getRoleMaster().getLevel());
	}

	@Test
	public void testCurrentLayerProdConfigId() {
		when(userRoleRepository.findByUserId(Mockito.anyString())).thenReturn(Optional.of(getUserRole()));
		ProductMaster prodMaster = new ProductMaster();
		when(productMasterRepo.findByName(Mockito.any())).thenReturn(prodMaster);
		ProductProperty productProperty = new ProductProperty();
		productProperty.setUid(UUID.randomUUID());
		List<ProductProperty> ppList = new ArrayList<>();
		ppList.add(productProperty);
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setLevel(1);
		when(productPropertyRepository.findByNameAndPropValueAndProductMasterAndIsSecondaryRefTrue(
				BffAdminConstantsUtils.PRODUCT_MASTER_CODE, BffAdminConstantsUtils.WAREHOUSE_DEFAULT, prodMaster))
						.thenReturn(ppList);
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		productConfig.setRoleMaster(roleMaster);
		List<ProductConfig> prodConfigList = new ArrayList<>();
		prodConfigList.add(productConfig);
		when(productConfigRepository.findBySecondaryRefId(productProperty.getUid())).thenReturn(prodConfigList);
		ProductConfig prodConfig = productPrepareServiceImpl.getCurrentLayerProdConfigId();
		assertEquals(getUserRole().getRoleMaster().getLevel(), prodConfig.getRoleMaster().getLevel());
	}
	
	@Test
	public void testCurrentLayerProdConfigId1() {
		when(userRoleRepository.findByUserId(Mockito.anyString())).thenReturn(Optional.of(getUserRole()));
		ProductMaster prodMaster = new ProductMaster();
		when(productMasterRepo.findByName(Mockito.any())).thenReturn(prodMaster);
		ProductProperty productProperty = new ProductProperty();
		productProperty.setUid(UUID.randomUUID());
		List<ProductProperty> ppList = new ArrayList<>();
		ppList.add(productProperty);
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setLevel(1);
		when(productPropertyRepository.findByNameAndPropValueAndProductMasterAndIsSecondaryRefTrue(
				BffAdminConstantsUtils.PRODUCT_MASTER_CODE, BffAdminConstantsUtils.WAREHOUSE_DEFAULT, prodMaster))
						.thenReturn(ppList);
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		productConfig.setRoleMaster(roleMaster);
		List<ProductConfig> prodConfigList = new ArrayList<>();
		prodConfigList.add(productConfig);
		when(productConfigRepository.findBySecondaryRefId(productProperty.getUid())).thenReturn(prodConfigList);
		ProductConfig prodConfig = productPrepareServiceImpl.getCurrentLayerProdConfigId();
		assertEquals(getUserRole().getRoleMaster().getLevel(), prodConfig.getRoleMaster().getLevel());
	}

	private UserRole getUserRole() {
		UserRole userRole = new UserRole();
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setLevel(1);
		userRole.setRoleMaster(roleMaster);
		return userRole;
	}

	private PrepRequest getPrepRequest() {
		PrepRequest prepRequest = new PrepRequest();
		prepRequest.setName("WMS");
		ProductProperty productProperty = new ProductProperty();
		productProperty.setUid(UUID.randomUUID());
		List<ProductProperty> productPropertyList = new ArrayList<ProductProperty>();
		productPropertyList.add(productProperty);
		List<ProductConfig> productConfigList = new ArrayList<>();
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		productConfigList.add(productConfig);
		productProperty.setName(BffAdminConstantsUtils.EMPTY_SPACES);
		productProperty.setPropValue(BffAdminConstantsUtils.EMPTY_SPACES);
		productProperty.setProductMaster(new ProductMaster());
		productPropertyList.add(productProperty);
		return prepRequest;
	}

	@Test
	public void testGetDefaultHomeFlow() {
		List<Flow> flowList = new ArrayList<>();
		Flow flow = new Flow();
		flow.setDefaultFormId(UUID.fromString("d8fb97a9-28b7-4378-84ec-07e3be044d1e"));
		flow.setUid(UUID.randomUUID());
		flow.setPublished(true);
		flow.setCreatedBy("SUPER");
		flowList.add(flow);
		Form defaultForm = new Form();
		defaultForm.setUid(UUID.fromString("d8fb97a9-28b7-4378-84ec-07e3be044d1e"));
		defaultForm.setPublished(true);
		when(appConfigRepository.findByConfigNameAndConfigType(BffAdminConstantsUtils.DEFAULT_FLOW_KEY,
				AppCfgRequestType.APPLICATION.getType())).thenReturn(getDefFlowAppConfig());
		when(appConfigRepository.findByConfigNameAndConfigType(BffAdminConstantsUtils.HOME_FLOW_KEY,
				AppCfgRequestType.APPLICATION.getType())).thenReturn(getHomeFlowAppConfig());
		when(flowRepository.findByNameAndVersion(Mockito.any(), Mockito.anyLong())).thenReturn(flowList);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(flow));
		when(formRepository.findById(UUID.fromString("d8fb97a9-28b7-4378-84ec-07e3be044d1e")))
				.thenReturn(Optional.of(defaultForm));
		BffCoreResponse response = productPrepareServiceImpl.getDefaultHomeFlow();
		assertEquals(BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_DEFAULT_HOME_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetDefHomeFlowWithNoDefOrHomeFlow() {
		when(appConfigRepository.findByConfigNameAndConfigType(BffAdminConstantsUtils.DEFAULT_FLOW_KEY,
				AppCfgRequestType.APPLICATION.getType())).thenReturn(null);
		when(appConfigRepository.findByConfigNameAndConfigType(BffAdminConstantsUtils.HOME_FLOW_KEY,
				AppCfgRequestType.APPLICATION.getType())).thenReturn(null);
		BffCoreResponse response = productPrepareServiceImpl.getDefaultHomeFlow();
		assertEquals(BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_DEFAULT_HOME_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetDefaultHomeFlowDataException() {
		when(appConfigRepository.findByConfigNameAndConfigType(Mockito.any(), Mockito.any()))
				.thenThrow(new DataBaseException("Defaultflow and Homeflow retrieval failed due to database error"));
		BffCoreResponse response = productPrepareServiceImpl.getDefaultHomeFlow();
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_HOME_FLOW_DBEXCEPTION.getCode(),
				response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetDefaultHomeFlowException() {
		when(appConfigRepository.findByConfigNameAndConfigType(Mockito.any(), Mockito.any()))
				.thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = productPrepareServiceImpl.getDefaultHomeFlow();
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_HOME_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettings() {
		UUID defFlowDefFormId = UUID.fromString("0d4fd0a0-cee4-4672-9acd-47cd9b5205f1");
		UUID homeFlowDefFormId = UUID.fromString("0d4fd0a0-cee4-4672-9acd-47cd9b5205f1");
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setPublishedDefaultFormId(UUID.randomUUID());
		defaultFlow.setPublishedFlow(true);
		Flow homeFlow = new Flow();
		homeFlow.setPublished(true);
		homeFlow.setPublishedDefaultFormId(UUID.randomUUID());
		homeFlow.setPublishedFlow(true);
		Form defaultFormOfDefaultFlow = new Form();
		defaultFormOfDefaultFlow.setPublishedForm(new byte[1]);
		Form defaultFormOfHomeFlow = new Form();
		defaultFormOfHomeFlow.setPublishedForm(new byte[1]);
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(defaultFormOfHomeFlow));
		homeFlow.setDefaultFormId(homeFlowDefFormId);
		when(formRepository.getModalAndTabbedDetails(Mockito.any()))
				.thenReturn(Optional.of(defaultFormOfDefaultFlow));
		defaultFlow.setDefaultFormId(defFlowDefFormId);
		when(flowRepository.findById(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209")))
				.thenReturn(Optional.of(defaultFlow));
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(homeFlow));

		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));

		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_DEFAULT_HOME_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testGetAppSettingsNoDefHomeFlow() {
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(null);
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.PRODUCT_PREPARE_SUCCESS_CODE_DEFAULT_HOME_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsEmptyConfigValue() {
		AppConfigMaster homeFlowAppConfig = new AppConfigMaster();
		homeFlowAppConfig.setConfigName("HOME_FLOW_ID");
		List<AppConfigDetail> homeFlowAppConfigDetails = new ArrayList<AppConfigDetail>();
		AppConfigDetail homeFlowAppConfigDetail = new AppConfigDetail();
		homeFlowAppConfigDetails.add(homeFlowAppConfigDetail);
		homeFlowAppConfig.setAppConfigDetails(homeFlowAppConfigDetails);
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), homeFlowAppConfig));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_NOT_FOUND.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsDefFlowNotPresent() {
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.empty());
		when(appConfigRepository.findByConfigNameAndConfigType(BffAdminConstantsUtils.DEFAULT_FLOW_KEY,
				AppCfgRequestType.APPLICATION.getType())).thenReturn(getDefFlowAppConfig());
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_NOT_FOUND.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsDefFlowNotPublsh() {
		Flow defaultFlow = new Flow();
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(defaultFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_NOT_PUBLISHED.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsDefFlowDisabled() {
		Flow defaultFlow = new Flow();
		defaultFlow.setDisabled(true);
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(defaultFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DISABLED.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsDefFlowDefFormNull() {
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setPublishedFlow(true);
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(defaultFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_NOT_FOUND.getCode(),
				response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	
	@Test
	public void testGetAppSettingsDefFlowDefFormNull1() {
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setName("def");
		defaultFlow.setDefaultFormId(UUID.randomUUID());
		defaultFlow.setPublishedFlow(true);
		Flow homeFlow = new Flow();
		homeFlow.setPublished(true);
		homeFlow.setDefaultFormId(UUID.randomUUID());
		homeFlow.setPublishedFlow(true);
		homeFlow.setName("home");
		Form defaultFormOfDefaultFlow = new Form();
		defaultFormOfDefaultFlow.setPublishedForm(new byte[1]);
		Form defaultFormOfHomeFlow = new Form();
		defaultFormOfHomeFlow.setPublished(true);
		defaultFormOfHomeFlow.setPublishedForm(new byte[1]);
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(defaultFormOfHomeFlow));
		defaultFlow.setPublishedDefaultFormId(UUID.randomUUID());
		defaultFlow.setDefaultFormId(UUID.fromString("c838e245-31fe-4fbb-85fb-f19fdb5d5594"));
		when(flowRepository.findById(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209")))
				.thenReturn(Optional.of(defaultFlow));
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(homeFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_NOT_PUBLISHED.getCode(),
				response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testGetAppSettingsDefFlowDefFormNull11() {
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setName("def");
		defaultFlow.setDefaultFormId(UUID.randomUUID());
		defaultFlow.setPublishedFlow(true);
		Flow homeFlow = new Flow();
		homeFlow.setPublished(true);
		homeFlow.setDefaultFormId(UUID.randomUUID());
		homeFlow.setPublishedFlow(true);
		homeFlow.setName("home");
		Form defaultFormOfDefaultFlow = new Form();
		defaultFormOfDefaultFlow.setPublishedForm(new byte[1]);
		Form defaultFormOfHomeFlow = new Form();
		defaultFormOfHomeFlow.setPublished(true);
		defaultFormOfHomeFlow.setPublishedForm(new byte[1]);
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.empty());
		defaultFlow.setPublishedDefaultFormId(UUID.randomUUID());
		defaultFlow.setDefaultFormId(UUID.fromString("c838e245-31fe-4fbb-85fb-f19fdb5d5594"));
		when(flowRepository.findById(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209")))
				.thenReturn(Optional.of(defaultFlow));
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(homeFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_NOT_PUBLISHED.getCode(),
				response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsDefFlowDefFormDisabled() {
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setPublishedDefaultFormId(UUID.randomUUID());
		defaultFlow.setPublishedFlow(true);
		Form defaultFormOfDefaultFlow = new Form();
		defaultFormOfDefaultFlow.setDisabled(true);
		when(formRepository.getModalAndTabbedDetails(Mockito.any())).thenReturn(Optional.of(defaultFormOfDefaultFlow));
		defaultFlow.setDefaultFormId(UUID.fromString("c838e245-31fe-4fbb-85fb-f19fdb5d5594"));
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(defaultFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_DISABLED.getCode(),
				response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsDefFlowDefFormUnpublishd() {
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setPublishedDefaultFormId(UUID.randomUUID());
		defaultFlow.setPublishedFlow(true);
		defaultFlow.setPublishedFlow(true);
		Form defaultFormOfDefaultFlow = new Form();
		when(formRepository.getModalAndTabbedDetails(Mockito.any())).thenReturn(Optional.of(defaultFormOfDefaultFlow));
		defaultFlow.setDefaultFormId(UUID.fromString("c838e245-31fe-4fbb-85fb-f19fdb5d5594"));
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(defaultFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_NOT_PUBLISHED.getCode(),
				response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsHomeFlowEmpty() {
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setPublishedDefaultFormId(UUID.randomUUID());
		defaultFlow.setPublishedFlow(true);
		Form defaultFormOfDefaultFlow = new Form();
		defaultFormOfDefaultFlow.setPublishedForm(new byte[1]);
		when(appConfigService.getAppConfigList()).thenReturn(new BffCoreResponse());
		when(formRepository.getModalAndTabbedDetails(Mockito.any())).thenReturn(Optional.of(defaultFormOfDefaultFlow));
		defaultFlow.setDefaultFormId(UUID.fromString("c838e245-31fe-4fbb-85fb-f19fdb5d5594"));
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(defaultFlow));
		when(flowRepository.findById(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209")))
				.thenReturn(Optional.empty());
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_NOT_FOUND.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsHomeFlowDisblled() {
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setPublishedDefaultFormId(UUID.randomUUID());
		defaultFlow.setDefaultFormId(UUID.randomUUID());
		defaultFlow.setPublishedFlow(true);
		Flow homeFlow = new Flow();
		homeFlow.setDisabled(true);
		homeFlow.setPublishedDefaultFormId(UUID.randomUUID());
		homeFlow.setDefaultFormId(UUID.randomUUID());
		homeFlow.setPublishedFlow(true);
		Form defaultFormOfDefaultFlow = new Form();
		defaultFormOfDefaultFlow.setPublishedForm(new byte[1]);
		when(appConfigService.getAppConfigList()).thenReturn(new BffCoreResponse());
		when(formRepository.getModalAndTabbedDetails(Mockito.any())).thenReturn(Optional.of(defaultFormOfDefaultFlow));
		defaultFlow.setDefaultFormId(UUID.fromString("c838e245-31fe-4fbb-85fb-f19fdb5d5594"));
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(defaultFlow));
		when(flowRepository.findById(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209")))
				.thenReturn(Optional.of(homeFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DISABLED.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsHomeFlowUnpublished() {
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setPublishedDefaultFormId(UUID.randomUUID());
		defaultFlow.setPublishedFlow(true);
		Flow homeFlow = new Flow();
		Form defaultFormOfDefaultFlow = new Form();
		defaultFormOfDefaultFlow.setPublishedForm(new byte[1]);
		when(appConfigService.getAppConfigList()).thenReturn(new BffCoreResponse());
		when(formRepository.getModalAndTabbedDetails(Mockito.any())).thenReturn(Optional.of(defaultFormOfDefaultFlow));
		defaultFlow.setDefaultFormId(UUID.fromString("c838e245-31fe-4fbb-85fb-f19fdb5d5594"));
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(defaultFlow));
		when(flowRepository.findById(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209")))
				.thenReturn(Optional.of(homeFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_NOT_PUBLISHED.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsHomeFlowNoDefForm() {
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setPublishedDefaultFormId(UUID.randomUUID());
		defaultFlow.setPublishedFlow(true);
		Flow homeFlow = new Flow();
		homeFlow.setPublished(true);
		homeFlow.setPublishedFlow(true);
		Form defaultFormOfDefaultFlow = new Form();
		defaultFormOfDefaultFlow.setPublishedForm(new byte[1]);
		when(appConfigService.getAppConfigList()).thenReturn(new BffCoreResponse());
		
		when(formRepository.getModalAndTabbedDetails(Mockito.any())).thenReturn(Optional.of(defaultFormOfDefaultFlow));
		defaultFlow.setDefaultFormId(UUID.fromString("c838e245-31fe-4fbb-85fb-f19fdb5d5594"));
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(defaultFlow));
		when(flowRepository.findById(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209")))
				.thenReturn(Optional.of(homeFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType())).thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		when(appConfigRepository.fetchAllAndUserAndDeviceSpecificVariables(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(getListAppConfigDto());
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_NOT_FOUND.getCode(),
				response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testGetAppSettingsHomeFlowNoDefForm1() {
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setPublishedDefaultFormId(UUID.randomUUID());
		defaultFlow.setPublishedFlow(true);
		Flow homeFlow = new Flow();
		homeFlow.setPublished(true);
		homeFlow.setPublishedFlow(true);
		homeFlow.setDefaultFormId(UUID.randomUUID());
		Form defaultFormOfDefaultFlow = new Form();
		defaultFormOfDefaultFlow.setPublishedForm(new byte[1]);
		when(appConfigService.getAppConfigList()).thenReturn(new BffCoreResponse());
		
		when(formRepository.getModalAndTabbedDetails(Mockito.any())).thenReturn(Optional.of(defaultFormOfDefaultFlow));
		defaultFlow.setDefaultFormId(UUID.fromString("c838e245-31fe-4fbb-85fb-f19fdb5d5594"));
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(defaultFlow));
		when(flowRepository.findById(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209")))
				.thenReturn(Optional.of(homeFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType())).thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		when(appConfigRepository.fetchAllAndUserAndDeviceSpecificVariables(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(getListAppConfigDto());
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_NOT_PUBLISHED.getCode(),
				response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testGetAppSettingsHomeFlowNoDefForm11() {
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setPublishedDefaultFormId(UUID.randomUUID());
		defaultFlow.setPublishedFlow(true);
		Flow homeFlow = new Flow();
		homeFlow.setPublished(true);
		homeFlow.setPublishedFlow(true);
		homeFlow.setDefaultFormId(UUID.randomUUID());
		Form defaultFormOfDefaultFlow = new Form();
		defaultFormOfDefaultFlow.setPublishedForm(new byte[1]);
		when(appConfigService.getAppConfigList()).thenReturn(new BffCoreResponse());
		
		when(formRepository.getModalAndTabbedDetails(Mockito.any())).thenReturn(Optional.empty());
		defaultFlow.setDefaultFormId(UUID.fromString("c838e245-31fe-4fbb-85fb-f19fdb5d5594"));
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(defaultFlow));
		when(flowRepository.findById(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209")))
				.thenReturn(Optional.of(homeFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType())).thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		when(appConfigRepository.fetchAllAndUserAndDeviceSpecificVariables(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(getListAppConfigDto());
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_DELETED.getCode(),
				response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}
	
	

	@Test
	public void testGetAppSettingsHomeFlowDefFormEmpty() {
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setPublishedDefaultFormId(UUID.randomUUID());
		defaultFlow.setPublishedFlow(true);
		Flow homeFlow = new Flow();
		homeFlow.setPublished(true);
		homeFlow.setPublishedFlow(true);
		Form defaultFormOfDefaultFlow = new Form();
		defaultFormOfDefaultFlow.setPublishedForm(new byte[1]);
		when(formRepository.getModalAndTabbedDetails(UUID.fromString("0d4fd0a0-cee4-4672-9acd-47cd9b5205f1")))
				.thenReturn(Optional.empty());
		when(appConfigService.getAppConfigList()).thenReturn(new BffCoreResponse());
		when(formRepository.getModalAndTabbedDetails(Mockito.any())).thenReturn(Optional.of(defaultFormOfDefaultFlow));
		defaultFlow.setDefaultFormId(UUID.fromString("c838e245-31fe-4fbb-85fb-f19fdb5d5594"));
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(defaultFlow));
		when(flowRepository.findById(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209")))
				.thenReturn(Optional.of(homeFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_NOT_FOUND.getCode(),
				response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsHomeFlowDefFormDisabld() {
		UUID defFlowDefFormId = UUID.fromString("c838e245-31fe-4fbb-85fb-f19fdb5d5594");
		UUID homeFlowDefFormId = UUID.fromString("0d4fd0a0-cee4-4672-9acd-47cd9b5205f1");
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setPublishedDefaultFormId(UUID.randomUUID());
		defaultFlow.setPublishedFlow(true);
		Flow homeFlow = new Flow();
		homeFlow.setPublished(true);
		homeFlow.setPublishedDefaultFormId(UUID.randomUUID());
		homeFlow.setPublishedFlow(true);
		Form defaultFormOfDefaultFlow = new Form();
		defaultFormOfDefaultFlow.setPublishedForm(new byte[1]);
		Form defaultFormOfHomeFlow = new Form();
		defaultFormOfHomeFlow.setDisabled(true);
		defaultFormOfHomeFlow.setPublishedForm(new byte[1]);
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(defaultFormOfHomeFlow));
		homeFlow.setDefaultFormId(homeFlowDefFormId);
		when(formRepository.getModalAndTabbedDetails(Mockito.any()))
				.thenReturn(Optional.of(defaultFormOfDefaultFlow));
		when(formRepository.findById(homeFlowDefFormId)).thenReturn(Optional.of(defaultFormOfHomeFlow));
		defaultFlow.setDefaultFormId(defFlowDefFormId);
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(defaultFlow));
		when(flowRepository.findById(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209")))
				.thenReturn(Optional.of(homeFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_DISABLED.getCode(),
				response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsHomeFlowDefFormUnpublishd() {
		UUID defFlowDefFormId = UUID.fromString("c838e245-31fe-4fbb-85fb-f19fdb5d5594");
		UUID homeFlowDefFormId = UUID.fromString("0d4fd0a0-cee4-4672-9acd-47cd9b5205f1");
		UUID publishedDefaultFormId = UUID.fromString("0d4fd0a0-cee4-4672-9acd-47cd9b5205f1");
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setPublishedFlow(true);
		Flow homeFlow = new Flow();
		homeFlow.setPublished(true);
		homeFlow.setPublishedDefaultFormId(publishedDefaultFormId);
		homeFlow.setPublishedFlow(true);
		Form defaultFormOfDefaultFlow = new Form();
		defaultFormOfDefaultFlow.setPublishedForm(new byte[1]);
		defaultFormOfDefaultFlow.setFlow(homeFlow);
		Form defaultFormOfHomeFlow = new Form();
		when(formRepository.findById(homeFlowDefFormId)).thenReturn(Optional.of(defaultFormOfHomeFlow));
		homeFlow.setDefaultFormId(homeFlowDefFormId);
		when(formRepository.getModalAndTabbedDetails(Mockito.any()))
				.thenReturn(Optional.of(defaultFormOfDefaultFlow));
		defaultFlow.setDefaultFormId(defFlowDefFormId);
		defaultFlow.setPublishedDefaultFormId(publishedDefaultFormId);
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(defaultFlow));
		when(flowRepository.findById(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209")))
				.thenReturn(Optional.of(homeFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_NOT_PUBLISHED.getCode(),
				response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsHomeFlowDefFormempty() {
		UUID defFlowDefFormId = UUID.fromString("c838e245-31fe-4fbb-85fb-f19fdb5d5594");
		UUID homeFlowDefFormId = UUID.fromString("0d4fd0a0-cee4-4672-9acd-47cd9b5205f1");
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setPublishedDefaultFormId(UUID.randomUUID());
		defaultFlow.setPublishedFlow(true);
		Flow homeFlow = new Flow();
		homeFlow.setPublished(true);
		homeFlow.setPublishedFlow(true);
		Form defaultFormOfDefaultFlow = new Form();
		defaultFormOfDefaultFlow.setPublishedForm(new byte[1]);
		when(formRepository.findById(homeFlowDefFormId)).thenReturn(Optional.empty());
		when(formRepository.getModalAndTabbedDetails(Mockito.any()))
				.thenReturn(Optional.of(defaultFormOfDefaultFlow));
		defaultFlow.setDefaultFormId(defFlowDefFormId);
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(defaultFlow));
		when(flowRepository.findById(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209")))
				.thenReturn(Optional.of(homeFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_NOT_FOUND.getCode(),
				response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsHomeFlowDefFormempty1() {
		UUID defFlowDefFormId = UUID.fromString("c838e245-31fe-4fbb-85fb-f19fdb5d5594");
		UUID homeFlowDefFormId = UUID.fromString("0d4fd0a0-cee4-4672-9acd-47cd9b5205f1");
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setPublishedDefaultFormId(UUID.randomUUID());
		defaultFlow.setPublishedFlow(true);
		Flow homeFlow = new Flow();
		homeFlow.setPublished(true);
		homeFlow.setPublishedFlow(true);
		homeFlow.setPublishedDefaultFormId(UUID.randomUUID());
		Form defaultFormOfDefaultFlow = new Form();
		defaultFormOfDefaultFlow.setPublishedForm(new byte[1]);
		when(formRepository.findById(homeFlowDefFormId)).thenReturn(Optional.empty());
		when(formRepository.getModalAndTabbedDetails(Mockito.any()))
				.thenReturn(Optional.of(defaultFormOfDefaultFlow));
		defaultFlow.setDefaultFormId(defFlowDefFormId);
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(defaultFlow));
		when(flowRepository.findById(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209")))
				.thenReturn(Optional.of(homeFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_HOME_FORM_DELETED.getCode(),
				response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsDbException() {
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenThrow(new DataBaseException("Defaultflow and Homeflow retrieval failed due to database error"));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_HOME_FLOW_DBEXCEPTION.getCode(),
				response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsException() {
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_PRODUCT_PREPARE_API_DEFAULT_HOME_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsFetchAllDbException() {
		UUID defFlowDefFormId = UUID.fromString("c838e245-31fe-4fbb-85fb-f19fdb5d5594");
		UUID homeFlowDefFormId = UUID.fromString("0d4fd0a0-cee4-4672-9acd-47cd9b5205f1");
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setPublishedDefaultFormId(UUID.randomUUID());
		defaultFlow.setPublishedFlow(true);
		Flow homeFlow = new Flow();
		homeFlow.setPublished(true);
		homeFlow.setPublishedDefaultFormId(UUID.randomUUID());
		homeFlow.setPublishedFlow(true);
		Form defaultFormOfDefaultFlow = new Form();
		defaultFormOfDefaultFlow.setPublishedForm(new byte[1]);
		Form defaultFormOfHomeFlow = new Form();
		defaultFormOfHomeFlow.setPublishedForm(new byte[1]);
		when(appConfigService.getAppSettingsList("Device1"))
				.thenThrow(new DataBaseException("Application configuration list retrieval failed for Type "));
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(defaultFormOfHomeFlow));
		homeFlow.setDefaultFormId(homeFlowDefFormId);
		when(formRepository.getModalAndTabbedDetails(Mockito.any()))
				.thenReturn(Optional.of(defaultFormOfDefaultFlow));
		defaultFlow.setDefaultFormId(defFlowDefFormId);
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(defaultFlow));
		when(flowRepository.findById(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209")))
				.thenReturn(Optional.of(homeFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCHALL_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetAppSettingsFetchAllException() {
		UUID defFlowDefFormId = UUID.fromString("c838e245-31fe-4fbb-85fb-f19fdb5d5594");
		Flow defaultFlow = new Flow();
		defaultFlow.setPublished(true);
		defaultFlow.setPublishedDefaultFormId(UUID.randomUUID());
		defaultFlow.setPublishedFlow(true);
		Flow homeFlow = new Flow();
		homeFlow.setPublished(true);
		homeFlow.setPublishedDefaultFormId(UUID.randomUUID());
		homeFlow.setPublishedFlow(true);
		Form defaultFormOfDefaultFlow = new Form();
		defaultFormOfDefaultFlow.setPublishedForm(new byte[1]);
		Form defaultFormOfHomeFlow = new Form();
		defaultFormOfHomeFlow.setPublished(true);
		defaultFormOfHomeFlow.setPublishedForm(new byte[1]);
		
		when(appConfigService.getAppSettingsList("Device1")).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(defaultFormOfHomeFlow));
		when(formRepository.getModalAndTabbedDetails(Mockito.any()))
				.thenReturn(Optional.of(defaultFormOfDefaultFlow));
		defaultFlow.setDefaultFormId(defFlowDefFormId);
		when(flowRepository.findById(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0")))
				.thenReturn(Optional.of(defaultFlow));
		when(flowRepository.findById(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209")))
				.thenReturn(Optional.of(homeFlow));
		when(appConfigRepository.findByConfigNameInAndConfigType(
				Arrays.asList(BffAdminConstantsUtils.HOME_FLOW_KEY, BffAdminConstantsUtils.DEFAULT_FLOW_KEY),
				AppCfgRequestType.APPLICATION.getType()))
						.thenReturn(Arrays.asList(getDefFlowAppConfig(), getHomeFlowAppConfig()));
		BffCoreResponse response = productPrepareServiceImpl.getAppSettings("Device1");
		assertEquals(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCHALL_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	private AppConfigMaster getHomeFlowAppConfig() {
		AppConfigMaster homeFlowAppConfig = new AppConfigMaster();
		List<AppConfigDetail> homeFlowAppConfigDetails = new ArrayList<AppConfigDetail>();
		AppConfigDetail homeFlowAppConfigDetail = new AppConfigDetail();
		homeFlowAppConfigDetail.setConfigValue("cb527cc6-0c75-4dc2-b789-39a9cdc167a0");
		homeFlowAppConfigDetails.add(homeFlowAppConfigDetail);
		homeFlowAppConfig.setAppConfigDetails(homeFlowAppConfigDetails);
		homeFlowAppConfig.setConfigName("DEFAULT_FLOW_ID");
		return homeFlowAppConfig;
	}

	private AppConfigMaster getDefFlowAppConfig() {
		AppConfigMaster defFlowAppConfig = new AppConfigMaster();
		List<AppConfigDetail> defFlowAppConfigDetails = new ArrayList<AppConfigDetail>();
		AppConfigDetail defFlowAppConfigDetail = new AppConfigDetail();
		defFlowAppConfigDetail.setConfigValue("37c05061-2620-433f-9c8f-54bb990c8209");
		defFlowAppConfigDetails.add(defFlowAppConfigDetail);
		defFlowAppConfig.setAppConfigDetails(defFlowAppConfigDetails);
		defFlowAppConfig.setConfigName("HOME_FLOW_ID");
		return defFlowAppConfig;
	}

	private List<AppConfigDetailDto> getListAppConfigDto() {
		List<AppConfigDetailDto> appConfigList = new ArrayList<>();
		AppConfigDetailDto dto  = new AppConfigDetailDto(UUID.randomUUID());
		dto.setConfigName("Warehouseid1");
		dto.setConfigType(AppCfgRequestType.APPLICATION.getType());
		dto.setConfigValue("1637");
		dto.setDescription("Test1");
		dto.setFlowId(UUID.fromString("128c5740-d32d-47c7-97d8-a1819826e94e"));
		dto.setUserId("SUPER");
		appConfigList.add(dto);
		return appConfigList;
	}
}
