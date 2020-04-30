package com.jda.mobility.framework.extensions.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.jda.mobility.framework.extensions.config.ProductApiSettings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.dto.TranslationRequest;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.MenuMaster;
import com.jda.mobility.framework.extensions.entity.MenuPermission;
import com.jda.mobility.framework.extensions.entity.MenuType;
import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.entity.ProductMaster;
import com.jda.mobility.framework.extensions.entity.ProductProperty;
import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.exception.DataBaseException;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.MenuAction;
import com.jda.mobility.framework.extensions.model.MenuListRequest;
import com.jda.mobility.framework.extensions.model.MenuRequest;
import com.jda.mobility.framework.extensions.repository.FlowRepository;
import com.jda.mobility.framework.extensions.repository.MenuMasterRepository;
import com.jda.mobility.framework.extensions.repository.MenuTypeRepository;
import com.jda.mobility.framework.extensions.repository.ProductConfigRepository;
import com.jda.mobility.framework.extensions.repository.ProductMasterRepository;
import com.jda.mobility.framework.extensions.repository.ProductPropertyRepository;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.TriggerAction;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffCommonUtil;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.ProductAPIServiceInvoker;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The class MenuServiceImplTest.java
 * 
 * @author HCL Technologies Ltd.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class MenuServiceImplTest extends AbstractPrepareTest {

	@InjectMocks
	private MenuServiceImpl menuserviceImpl;
	@Mock
	private MenuMasterRepository menuMasterRepository;
	@Mock
	private ResourceBundleRepository resourceBundleRepository;
	@Mock
	MenuTypeRepository menuTypeRepository;
	@Mock
	private ProductConfigRepository productConfigRepo;
	@Mock
	private ProductPropertyRepository productPropertyRepository;
	@Mock
	private FlowRepository flowRepository;
	@Mock
	private ProductMasterRepository productMasterRepo;
	@Mock
	private ProductApiSettings productApis;

	@Spy
	private BffCommonUtil commonUtil = new BffCommonUtil();
	
	@Mock
	private ProductAPIServiceInvoker serviceInvoker;
	

	@Before
	public void setUpMenuService() {
		List<ResourceBundle> resourceBundleList = new ArrayList<>();
		ResourceBundle resourceBundle = new ResourceBundle();
		resourceBundle.setLocale(BffAdminConstantsUtils.LOCALE);
		resourceBundle.setRbkey(BffAdminConstantsUtils.RB_TEST_KEY);
		resourceBundle.setRbvalue(BffAdminConstantsUtils.RB_TEST_VAL);
		resourceBundle.setCreatedBy(BffAdminConstantsUtils.SUPER);
		resourceBundle.setCreationDate(new Date());
		resourceBundle.setType("INTERNAL");
		resourceBundleList.add(resourceBundle);

		ResourceBundleRepository resourceBundleRepo = mock(ResourceBundleRepository.class);
		when(resourceBundleRepo.findByLocaleAndRbkey(Mockito.any(), Mockito.any())).thenReturn(resourceBundleList);
		ReflectionTestUtils.setField(commonUtil, "resBundleRepo", resourceBundleRepo);
		ReflectionTestUtils.setField(commonUtil, "productPropertyRepository", productPropertyRepository);
		ReflectionTestUtils.setField(commonUtil, "productMasterRepo", productMasterRepo);

		sessionDetails.setSessionId("df44ec7f-2dcc-4d76-8906-c615182fc851");
		sessionDetails.setLocale(BffAdminConstantsUtils.LOCALE);
		sessionDetails.setPrincipalName(BffAdminConstantsUtils.SUPER);
		sessionDetails.setVersion("1");
		sessionDetails.setChannel("MOBILE_RENDERER");
		sessionDetails.setTenant("SOURCE_A");
		sessionDetails.setPrdAuthCookie("COOKIE");
		ReflectionTestUtils.setField(commonUtil, "sessionDetails", sessionDetails);

		when(productApis.warehousesUrl()).thenReturn(UriComponentsBuilder.fromHttpUrl("https://localhost/{userId}/warehouses"));

	}

	@Test
	public void testCreateMenuByType() {
		List<ProductProperty> productPropertyList = new ArrayList<>();
		ProductProperty prodProp = new ProductProperty();
		prodProp.setUid(UUID.randomUUID());
		prodProp.setName("WMD");
		prodProp.setPropValue("Test");
		prodProp.setSecondaryRef(true);
		productPropertyList.add(prodProp);
		/*
		 * when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(
		 * Mockito.anyString(), Mockito.anyString())).thenReturn(productPropertyList);
		 */
		when(menuTypeRepository.findByType(Mockito.any())).thenReturn(Optional.empty());
		MenuRequest menuRequest = getMenuListRequest();
		MenuType menuType= new MenuType();
		menuType.setType("WMD");
		menuType.setUid(UUID.randomUUID());
		when(productPropertyRepository.save(Mockito.any())).thenReturn(prodProp);
		when(commonUtil.createdOrGetSecondaryRefId(Mockito.any())).thenReturn(prodProp);
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(BffAdminConstantsUtils.PRODUCT_MASTER_CODE, null)).thenReturn(productPropertyList);
		BffCoreResponse response4 = menuserviceImpl.createMenuListByType(menuRequest, "MAIN");
		assertEquals(BffResponseCode.MENU_INVALID_INPUT.getCode(), response4.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response4.getHttpStatusCode());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(new Flow()));
		when(menuMasterRepository.save(Mockito.any())).thenReturn(getMenuMaster());
		when(resourceBundleRepository.findById(Mockito.any())).thenReturn(Optional.of(new ResourceBundle()));
		when(menuTypeRepository.findByType(Mockito.any())).thenReturn(Optional.of(new MenuType()));
		BffCoreResponse response = menuserviceImpl.createMenuListByType(getMenuListRequest(), "MAIN");
		assertEquals(BffResponseCode.MENU_CREATE_SUCCESS_CD.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
		menuRequest = getMenuListRequest();
		menuRequest.setMenus(null);
		List<MenuMaster> menuList = new ArrayList<>();
		menuList.add(getMenuMaster());
		when(menuMasterRepository.findByProductPropertyAndMenuType(Mockito.any(), Mockito.any())).thenReturn(menuList);
		BffCoreResponse response2 = menuserviceImpl.createMenuListByType(menuRequest, "MAIN");
		assertEquals(BffResponseCode.MENU_NO_INPUT_SUCCESS_CD.getCode(), response2.getCode());
		assertEquals(StatusCode.OK.getValue(), response2.getHttpStatusCode());
		menuRequest.setFormId(UUID.randomUUID());
		MenuType type= new MenuType();
		type.setType("FORM_CONTEXT");
		when(menuTypeRepository.findByType(Mockito.any())).thenReturn(Optional.of(type));
		BffCoreResponse response3 = menuserviceImpl.createMenuListByType(menuRequest, "MAIN");
		assertEquals(BffResponseCode.MENU_NO_INPUT_SUCCESS_CD.getCode(), response3.getCode());
		assertEquals(StatusCode.OK.getValue(), response3.getHttpStatusCode());
	}
	
	@Test
	public void testCreateMenuByType_Exception() {
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(Mockito.anyString(),
				Mockito.anyString())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		when(productConfigRepo.findById(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = menuserviceImpl.createMenuListByType(getMenuListRequest(), "MAIN");
		assertEquals(BffResponseCode.MENU_CREATE_SYS_ERR_CD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateMenuByType_DataBaseException() {
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(Mockito.anyString(),
				Mockito.anyString())).thenThrow(new DataBaseException("Menu creation failed"));
		when(productConfigRepo.findById(Mockito.any())).thenThrow(new DataBaseException("Menu creation failed"));
		BffCoreResponse response = menuserviceImpl.createMenuListByType(getMenuListRequest(), "MAIN");
		assertEquals(BffResponseCode.MENU_CREATE_DB_ERR_CD.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchMenus() {
		JsonNode node = mock(JsonNode.class);
		JsonNodeFactory jnf = new JsonNodeFactory(true);
		ArrayNode warehouseList = new ArrayNode(jnf);
		warehouseList.add("PRODUCT_NAME");
		when(serviceInvoker.invokeApi(
				UriComponentsBuilder.fromHttpUrl("https://localhost/SUPER/warehouses").build().toUri(),
				HttpMethod.GET, null, null, "COOKIE")
		).thenReturn(node);
		when(node.get(Mockito.any())).thenReturn(warehouseList);
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(Mockito.anyString(),
				Mockito.anyString())).thenReturn(new ArrayList<>());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(new Flow()));
		List<MenuMaster> menuList = new ArrayList<MenuMaster>();
		menuList.add(getMenuMaster());
		ProductConfig config = new ProductConfig();
		config.setSecondaryRefId(UUID.randomUUID());
		when(productConfigRepo.findById(Mockito.any())).thenReturn(Optional.of(config));

		when(menuMasterRepository.findByProductPropertyAndParentMenuIdIsNullOrderByMenuTypeAscSequenceAsc(Mockito.any()))
				.thenReturn(menuList);
		when(menuMasterRepository.findByParentMenuIdOrderBySequence(Mockito.any())).thenReturn(menuList);
		List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
		ResourceBundle bundle = new ResourceBundle();
		bundles.add(bundle);
		when(resourceBundleRepository.findByLocaleAndRbkey(Mockito.any(), Mockito.any())).thenReturn(bundles);
		when(menuTypeRepository.findById(Mockito.any())).thenReturn(Optional.of(new MenuType()));
		List<String> userPermissions= new ArrayList<>();	
		String userPermission= "Test";
		userPermissions.add(userPermission);
		BffCoreResponse response = menuserviceImpl.fetchMenus(null,"PRODUCT_NAME", false,userPermissions);
		assertEquals(BffResponseCode.MENU_INVALID_PRODUCT_CD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
		List<ProductProperty> productPropertyList = new ArrayList<>();
		ProductProperty prodProp = new ProductProperty();
		prodProp.setUid(UUID.randomUUID());
		productPropertyList.add(prodProp);
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(
				BffAdminConstantsUtils.PRODUCT_MASTER_CODE, "PRODUCT_NAME")).thenReturn(productPropertyList);
		BffCoreResponse response1 = menuserviceImpl.fetchMenus(null,"PRODUCT_NAME", false,userPermissions);
		assertEquals(BffResponseCode.MENU_FETCH_SUCCESS_CD.getCode(), response1.getCode());
		assertEquals(StatusCode.OK.getValue(), response1.getHttpStatusCode());
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(
				BffAdminConstantsUtils.PRODUCT_MASTER_CODE, "PRODUCT_NAME")).thenReturn(null);
		BffCoreResponse response3 = menuserviceImpl.fetchMenus(null,"PRODUCT_NAME", false,userPermissions);
		assertEquals(BffResponseCode.MENU_INVALID_PRODUCT_CD.getCode(), response3.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response3.getHttpStatusCode());
		BffCoreResponse response4 = menuserviceImpl.fetchMenus(null,"", false,userPermissions);
		assertEquals(BffResponseCode.MENU_INVALID_PRODUCT_CD.getCode(), response4.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response4.getHttpStatusCode());

	}

	@Test
	public void testFetchMenus_Exception() {
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(Mockito.anyString(),
				Mockito.anyString())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		when(productConfigRepo.findById(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = menuserviceImpl.fetchMenus(null, "pdr1", false,null);
		assertEquals(BffResponseCode.MENU_FETCH_SYS_ERR_CD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchMenus_DBException() {
		List<String> userPermissions= new ArrayList<>();	
		String userPermission= "Test";
		userPermissions.add(userPermission);
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(Mockito.anyString(),
				Mockito.anyString())).thenThrow(new DataBaseException("Menu retrieval failed"));
		when(productConfigRepo.findById(Mockito.any())).thenThrow(new DataBaseException("Menu retrieval failed"));
		BffCoreResponse response = menuserviceImpl.fetchMenus(null, "prd2", false,userPermissions);
		assertEquals(BffResponseCode.MENU_FETCH_DB_ERR_CD.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchMenusByType() {
		JsonNode node = mock(JsonNode.class);
		JsonNodeFactory jnf = new JsonNodeFactory(true);
		ArrayNode warehouseList = new ArrayNode(jnf);
		warehouseList.add("PRODUCT_NAME");
		List<MenuMaster> menuList = new ArrayList<MenuMaster>();
		menuList.add(getMenuMaster());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(new Flow()));
		ProductConfig config = new ProductConfig();
		config.setSecondaryRefId(UUID.randomUUID());
		when(serviceInvoker.invokeApi(
				UriComponentsBuilder.fromHttpUrl("https://localhost/SUPER/warehouses").build().toUri(),
				HttpMethod.GET, null, null, "COOKIE")
		).thenReturn(node);
		when(node.get(Mockito.any())).thenReturn(warehouseList);
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(Mockito.anyString(),
				Mockito.anyString())).thenReturn(new ArrayList<>());
		when(productConfigRepo.findById(Mockito.any())).thenReturn(Optional.of(config));
		when(menuMasterRepository.findByProductPropertyAndMenuTypeAndParentMenuIdIsNullOrderBySequence(Mockito.any(),
				Mockito.any())).thenReturn(menuList);
		when(menuMasterRepository.findByParentMenuIdOrderBySequence(Mockito.any())).thenReturn(menuList);
		List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
		ResourceBundle bundle = new ResourceBundle();
		bundles.add(bundle);
		when(resourceBundleRepository.findByLocaleAndRbkey(Mockito.any(), Mockito.any())).thenReturn(bundles);
		when(menuTypeRepository.findByType(Mockito.any())).thenReturn(Optional.of(new MenuType()));
		when(menuTypeRepository.findById(Mockito.any())).thenReturn(Optional.of(new MenuType()));
		BffCoreResponse response = menuserviceImpl.fetchMenusByType(null, "MAIN", "PRODUCT_NAME", false,Arrays.asList("Permssion"));
		assertEquals(BffResponseCode.MENU_INVALID_PRODUCT_CD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());

		List<ProductProperty> productPropertyList = new ArrayList<>();
		ProductProperty prodProp = new ProductProperty();
		prodProp.setUid(UUID.randomUUID());
		productPropertyList.add(prodProp);
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(
				BffAdminConstantsUtils.PRODUCT_MASTER_CODE, "PRODUCT_NAME")).thenReturn(productPropertyList);
		BffCoreResponse response1 = menuserviceImpl.fetchMenusByType(null, "MAIN", "PRODUCT_NAME", false,Arrays.asList("Permssion"));
		assertEquals(BffResponseCode.MENU_FETCH_SUCCESS_CD.getCode(), response1.getCode());
		assertEquals(StatusCode.OK.getValue(), response1.getHttpStatusCode());

		when(menuMasterRepository.findByProductPropertyAndMenuTypeAndParentMenuIdIsNullOrderBySequence(Mockito.any(),
				Mockito.any())).thenReturn(new ArrayList<>());

		BffCoreResponse response2 = menuserviceImpl.fetchMenusByType(null, "MAIN", "PRODUCT_NAME", false,Arrays.asList("Permssion"));
		assertEquals(BffResponseCode.MENU_NODATA_SUCCESS_CD.getCode(), response2.getCode());
		assertEquals(StatusCode.OK.getValue(), response2.getHttpStatusCode());

		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(
				BffAdminConstantsUtils.PRODUCT_MASTER_CODE, "PRODUCT_NAME")).thenReturn(null);
		BffCoreResponse response3 = menuserviceImpl.fetchMenusByType(null, "MAIN", "PRODUCT_NAME", false,Arrays.asList("Permssion"));
		assertEquals(BffResponseCode.MENU_INVALID_PRODUCT_CD.getCode(), response3.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response3.getHttpStatusCode());
		BffCoreResponse response4 = menuserviceImpl.fetchMenusByType(null, "MAIN", "", false,Arrays.asList("Permssion"));
		assertEquals(BffResponseCode.MENU_INVALID_PRODUCT_CD.getCode(), response4.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response4.getHttpStatusCode());
	}

	@Test
	public void testFetchMenusByType_Exception() {
		ProductConfig config = new ProductConfig();
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(Mockito.anyString(),
				Mockito.anyString())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		config.setSecondaryRefId(UUID.randomUUID());
		when(productConfigRepo.findById(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = menuserviceImpl.fetchMenusByType(null, "MAIN", "PRODUCT_NAME", false,Arrays.asList("Permssion"));
		assertEquals(BffResponseCode.MENU_FETCH_SYS_ERR_CD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchMenusByType_DBException() {
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(Mockito.anyString(),
				Mockito.anyString())).thenThrow(new DataBaseException("Menu retrieval failed"));
		when(productConfigRepo.findById(Mockito.any())).thenThrow(new DataBaseException("Menu retrieval failed"));
		BffCoreResponse response = menuserviceImpl.fetchMenusByType(null, "MAIN", "PRODUCT_NAME", false,Arrays.asList("Permssion"));
		assertEquals(BffResponseCode.MENU_FETCH_DB_ERR_CD.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchMenusByTypes() {
		JsonNode node = mock(JsonNode.class);
		JsonNodeFactory jnf = new JsonNodeFactory(true);
		ArrayNode warehouseList = new ArrayNode(jnf);
		warehouseList.add("PRODUCT_NAME");
		List<MenuMaster> menuList = new ArrayList<MenuMaster>();
		menuList.add(getMenuMaster());
		when(serviceInvoker.invokeApi(
				UriComponentsBuilder.fromHttpUrl("https://localhost/SUPER/warehouses").build().toUri(),
				HttpMethod.GET, null, null, "COOKIE")
		).thenReturn(node);
		when(node.get(Mockito.any())).thenReturn(warehouseList);
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(Mockito.anyString(),
				Mockito.anyString())).thenReturn(new ArrayList<>());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(new Flow()));
		ProductConfig config = new ProductConfig();
		config.setSecondaryRefId(UUID.randomUUID());
		when(productConfigRepo.findById(Mockito.any())).thenReturn(Optional.of(config));
		when(menuMasterRepository.findByProductPropertyAndMenuTypeInAndParentMenuIdIsNullOrderByMenuTypeAscSequenceAsc(
				Mockito.any(), Mockito.any())).thenReturn(menuList);
		when(menuMasterRepository.findByParentMenuIdOrderBySequence(Mockito.any())).thenReturn(menuList);
		List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
		ResourceBundle bundle = new ResourceBundle();
		bundles.add(bundle);
		when(resourceBundleRepository.findByLocaleAndRbkey(Mockito.any(), Mockito.any())).thenReturn(bundles);
		when(menuTypeRepository.findById(Mockito.any())).thenReturn(Optional.of(new MenuType()));
		List<String> list = new ArrayList<String>();
		list.add("MAIN");
		list.add("GLOBAL_CONTEXT");
		BffCoreResponse response = menuserviceImpl.fetchMenusByTypes(null, list, "PRODUCT_NAME", false,Arrays.asList("Permssion"));
		assertEquals(BffResponseCode.MENU_INVALID_PRODUCT_CD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());

		List<ProductProperty> productPropertyList = new ArrayList<>();
		ProductProperty prodProp = new ProductProperty();
		prodProp.setUid(UUID.randomUUID());
		productPropertyList.add(prodProp);
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(
				BffAdminConstantsUtils.PRODUCT_MASTER_CODE, "PRODUCT_NAME")).thenReturn(productPropertyList);
		BffCoreResponse response1 = menuserviceImpl.fetchMenusByTypes(null, list, "PRODUCT_NAME", false,Arrays.asList("Permssion"));
		assertEquals(BffResponseCode.MENU_INVALID_INPUT.getCode(), response1.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response1.getHttpStatusCode());
		when(menuMasterRepository.findByProductPropertyAndMenuTypeInAndParentMenuIdIsNullOrderByMenuTypeAscSequenceAsc(
				Mockito.any(), Mockito.any())).thenReturn(null);

		BffCoreResponse response2 = menuserviceImpl.fetchMenusByTypes(null, list, "PRODUCT_NAME", false,Arrays.asList("Permssion"));
		assertEquals(BffResponseCode.MENU_INVALID_INPUT.getCode(), response2.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response2.getHttpStatusCode());
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(
				BffAdminConstantsUtils.PRODUCT_MASTER_CODE, "PRODUCT_NAME")).thenReturn(null);
		BffCoreResponse response3 = menuserviceImpl.fetchMenusByTypes(null, list, "PRODUCT_NAME", false,Arrays.asList("Permssion"));
		assertEquals(BffResponseCode.MENU_INVALID_PRODUCT_CD.getCode(), response3.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response3.getHttpStatusCode());
		BffCoreResponse response4 = menuserviceImpl.fetchMenusByTypes(null, list, "", false,Arrays.asList("Permssion"));
		assertEquals(BffResponseCode.MENU_INVALID_PRODUCT_CD.getCode(), response4.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response4.getHttpStatusCode());
	}
	
	
	@Test
	public void testFetchMenusByTypes_check() {
		
		List<ProductProperty>  propertylist= new ArrayList<>();
		ProductProperty productProperty= new ProductProperty();
		ProductMaster productMaster= new ProductMaster();
		productProperty.setName("test");
		productProperty.setUid(UUID.randomUUID());
		productProperty.setProductMaster(productMaster);
		propertylist.add(productProperty);
		JsonNode node = mock(JsonNode.class);
		JsonNodeFactory jnf = new JsonNodeFactory(true);
		ArrayNode warehouseList = new ArrayNode(jnf);
		warehouseList.add("PRODUCT_NAME");
		List<MenuMaster> menuList = new ArrayList<MenuMaster>();
		menuList.add(getMenuMaster());
		when(serviceInvoker.invokeApi(
				UriComponentsBuilder.fromHttpUrl("https://localhost/SUPER/warehouses").build().toUri(),
				HttpMethod.GET, null, null, "COOKIE")
		).thenReturn(node);
		when(node.get(Mockito.any())).thenReturn(warehouseList);
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(Mockito.anyString(),
				Mockito.anyString())).thenReturn(propertylist);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(new Flow()));
		ProductConfig config = new ProductConfig();
		config.setSecondaryRefId(UUID.randomUUID());
		when(productConfigRepo.findById(Mockito.any())).thenReturn(Optional.of(config));
		when(menuMasterRepository.findByProductPropertyAndMenuTypeInAndParentMenuIdIsNullOrderByMenuTypeAscSequenceAsc(
				Mockito.any(), Mockito.any())).thenReturn(menuList);
		when(menuMasterRepository.findByParentMenuIdOrderBySequence(Mockito.any())).thenReturn(menuList);
		List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
		ResourceBundle bundle = new ResourceBundle();
		bundles.add(bundle);
		when(resourceBundleRepository.findByLocaleAndRbkey(Mockito.any(), Mockito.any())).thenReturn(bundles);
		when(menuTypeRepository.findById(Mockito.any())).thenReturn(Optional.of(new MenuType()));
		List<String> list = new ArrayList<String>();
		list.add("MAIN");
		list.add("GLOBAL_CONTEXT");
		BffCoreResponse response = menuserviceImpl.fetchMenusByTypes(null, list, "PRODUCT_NAME", false,Arrays.asList("Permssion"));
		assertEquals(BffResponseCode.MENU_INVALID_INPUT.getCode(), response.getCode());

		List<ProductProperty> productPropertyList = new ArrayList<>();
		ProductProperty prodProp = new ProductProperty();
		prodProp.setUid(UUID.randomUUID());
		productPropertyList.add(prodProp);
		List<MenuType> menuTypList =new ArrayList<>();
		MenuType menuType= new MenuType();
		menuType.setUid(UUID.randomUUID());
		menuType.setType("WMD");
		menuTypList.add(menuType);
		when(menuTypeRepository.findByTypeIn(Mockito.any())).thenReturn(menuTypList);
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(
				BffAdminConstantsUtils.PRODUCT_MASTER_CODE, "PRODUCT_NAME")).thenReturn(productPropertyList);
		BffCoreResponse response1 = menuserviceImpl.fetchMenusByTypes(null, list, "PRODUCT_NAME", false,Arrays.asList("Permssion"));
		assertEquals(BffResponseCode.MENU_FETCH_SUCCESS_CD.getCode(), response1.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());

	}

	@Test
	public void testFetchMenusByTypes_Exception() {
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(Mockito.anyString(),
				Mockito.anyString())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		when(productConfigRepo.findById(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		List<String> list = new ArrayList<String>();
		list.add("MAIN");
		list.add("GLOBAL_CONTEXT");
		BffCoreResponse response = menuserviceImpl.fetchMenusByTypes(null, list, "PRODUCT_NAME", false,Arrays.asList("Permssion")	);
		assertEquals(BffResponseCode.MENU_FETCH_SYS_ERR_CD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchMenusByTypes_DBException() {
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(Mockito.anyString(),
				Mockito.anyString())).thenThrow(new DataBaseException("Menu retrieval failed"));
		when(productConfigRepo.findById(Mockito.any())).thenThrow(new DataBaseException("Menu retrieval failed"));
		List<String> list = new ArrayList<String>();
		list.add("MAIN");
		list.add("GLOBAL_CONTEXT");
		BffCoreResponse response = menuserviceImpl.fetchMenusByTypes(null, list, "PRODUCT_NAME", false,Arrays.asList("Permssion"));
		assertEquals(BffResponseCode.MENU_FETCH_DB_ERR_CD.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	private MenuMaster getMenuMaster() {
		MenuMaster menuMaster = new MenuMaster();
		menuMaster.setUid(UUID.randomUUID());
		menuMaster.setParentMenuId(UUID.randomUUID());
		menuMaster.setIconAlignment("LEFT");
		menuMaster.setIconName("ERROR");
		MenuPermission menuPer = new MenuPermission();
		menuPer.setPermission("SchAgtOpr");
		menuPer.setUid(UUID.randomUUID());
		List<MenuPermission> permList = new ArrayList<MenuPermission>();
		permList.add(menuPer);
		menuMaster.setMenupermission(permList);
		MenuType type = new MenuType();
		type.setUid(UUID.randomUUID());
		type.setType("MAIN");
		menuMaster.setMenuType(type);
		menuMaster.setMenuName("5007");
		menuMaster.setCreatedBy("Admin");
		ProductProperty prop = new ProductProperty();
		prop.setUid(UUID.randomUUID());
		menuMaster.setProductProperty(prop);
		menuMaster.addPermissions(menuPer);
		menuMaster.setAction(TriggerAction.NAVIGATE_TO_WORKFLOW.toString());
		menuMaster.setProperties("{\"workflow\" :{\"defaultFormId\" : \"73e70e85-c547-45f1-8ac5-54a5d5aac3d0\",\"flowId\" : \"73e70e85-c547-45f1-8ac5-54a5d5aac3d0\"}}");
		menuMaster.setHotKey("{\"Key\" : \"F7\",\"value\" : \"Enter\"}");
		return menuMaster;
	}

	private MenuRequest getMenuListRequest() {
		MenuRequest request = new MenuRequest();
		List<String> perm = new ArrayList<String>();
		perm.add("Read");
		List<MenuListRequest> menuListRequestList = new ArrayList<MenuListRequest>();
		MenuListRequest menuListRequest = new MenuListRequest();
		TranslationRequest translationRequest = TranslationRequest.builder().locale(BffAdminConstantsUtils.LOCALE)
				.rbkey("1001").rbvalue(BffAdminConstantsUtils.EMPTY_SPACES).type(BffAdminConstantsUtils.EMPTY_SPACES)
				.uid(UUID.randomUUID()).build();
		MenuAction menuAction = new MenuAction();
		menuListRequest.setIconName("ERROR");
		menuListRequest.setIconAlignment("left");
		menuListRequest.setMenuType("MAIN");
		menuListRequest.setPermissions(perm);
		menuListRequest.setMenuName(translationRequest);
		menuListRequest.setShowInToolBar(false);
		menuAction.setActionType(TriggerAction.NAVIGATE_TO_WORKFLOW.toString());
		menuListRequest.setMenuAction(menuAction);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode root = mapper.createObjectNode();
		menuAction.setProperties(root);
		menuListRequest.setMenuAction(menuAction);
		List<MenuListRequest> menuList = new ArrayList<MenuListRequest>();
		MenuListRequest subMenu = new MenuListRequest();
		subMenu.setIconName("IMAGe");
		subMenu.setIconAlignment("left");
		subMenu.setMenuType("MAIN");
		subMenu.setPermissions(perm);
		subMenu.setMenuName(translationRequest);
		subMenu.setShowInToolBar(false);
		subMenu.setMenuAction(menuAction);
		menuList.add(subMenu);
		menuListRequest.setSubMenus(menuList);
		menuListRequestList.add(menuListRequest);
		request.setMenus(menuListRequestList);
		request.setWarehouseName("PRODUCT_NAME");
		menuListRequest.setHotKey(mapper.createObjectNode());
		return request;
	}

	@Test
	public void testFetchMenusByFormId() {
		List<MenuMaster> menuList = new ArrayList<MenuMaster>();
		menuList.add(getMenuMaster());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(new Flow()));
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(Mockito.anyString(),
				Mockito.anyString())).thenReturn(new ArrayList<>());
		when(menuMasterRepository.findByLinkedFormId(Mockito.any())).thenReturn(menuList);
		when(menuMasterRepository.findByParentMenuIdOrderBySequence(Mockito.any())).thenReturn(menuList);
		List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
		ResourceBundle bundle = new ResourceBundle();
		bundles.add(bundle);
		when(resourceBundleRepository.findByLocaleAndRbkey(Mockito.any(), Mockito.any())).thenReturn(bundles);
		when(menuTypeRepository.findById(Mockito.any())).thenReturn(Optional.of(new MenuType()));
		BffCoreResponse response = menuserviceImpl.fetchMenusByFormId(UUID.randomUUID(),Arrays.asList("SchAgtOpr"),false);
		assertEquals(BffResponseCode.MENU_FETCH_SUCCESS_CD.getCode(), response.getCode());

		when(menuMasterRepository.findByLinkedFormId(Mockito.any())).thenReturn(null);
		BffCoreResponse response1 = menuserviceImpl.fetchMenusByFormId(UUID.randomUUID(),Arrays.asList("SchAgtOpr"),false);
		assertEquals(BffResponseCode.MENU_NODATA_SUCCESS_CD.getCode(), response1.getCode());
	}
	
	
	@Test
	public void testFetchMenusByFormId_permission() {
		List<MenuMaster> menuList = new ArrayList<MenuMaster>();
		menuList.add(getMenuMaster());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(new Flow()));
		when(productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(Mockito.anyString(),
				Mockito.anyString())).thenReturn(new ArrayList<>());
		when(menuMasterRepository.findByLinkedFormId(Mockito.any())).thenReturn(menuList);
		when(menuMasterRepository.findByParentMenuIdOrderBySequence(Mockito.any())).thenReturn(menuList);
		List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
		ResourceBundle bundle = new ResourceBundle();
		bundles.add(bundle);
		when(resourceBundleRepository.findByLocaleAndRbkey(Mockito.any(), Mockito.any())).thenReturn(bundles);
		when(menuTypeRepository.findById(Mockito.any())).thenReturn(Optional.of(new MenuType()));
		
		BffCoreResponse response = menuserviceImpl.fetchMenusByFormId(UUID.randomUUID(),Arrays.asList("Permssion"),false);
		assertEquals(BffResponseCode.MENU_FETCH_SUCCESS_CD.getCode(), response.getCode());

		when(menuMasterRepository.findByLinkedFormId(Mockito.any())).thenReturn(null);
		BffCoreResponse response1 = menuserviceImpl.fetchMenusByFormId(UUID.randomUUID(),Arrays.asList("Permssion"),false);
		assertEquals(BffResponseCode.MENU_NODATA_SUCCESS_CD.getCode(), response1.getCode());
	}

	@Test
	public void testFetchMenusByFormId_Exp() {
		when(menuMasterRepository.findByLinkedFormId(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = menuserviceImpl.fetchMenusByFormId(UUID.randomUUID(),Arrays.asList("Permssion"),false);
		assertEquals(BffResponseCode.MENU_FETCH_SYS_ERR_CD.getCode(), response.getCode());
	}

	@Test
	public void testFetchMenusByFormId_DBExp() {
		when(menuMasterRepository.findByLinkedFormId(Mockito.any())).thenThrow(new DataBaseException("Menu retrieval failed"));
		BffCoreResponse response = menuserviceImpl.fetchMenusByFormId(UUID.randomUUID(),Arrays.asList("Permssion"),false);
		assertEquals(BffResponseCode.MENU_FETCH_DB_ERR_CD.getCode(), response.getCode());
	}


	@Test
	public void testDeleteMenuById() {
		UUID menuId=UUID.randomUUID();
		MenuMaster menuMaster= new MenuMaster();
		menuMaster.setUid(menuId);
		List<MenuMaster> menuList = new ArrayList<MenuMaster>();
		menuList.add(getMenuMaster());
		ProductConfig config = new ProductConfig();
		config.setSecondaryRefId(UUID.randomUUID());
		when(menuMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(menuMaster));
		when(menuMasterRepository.findByParentMenuIdOrderBySequence(Mockito.any())).thenReturn(menuList);
		List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();
		ResourceBundle bundle = new ResourceBundle();
		bundles.add(bundle);
		when(resourceBundleRepository.findByLocaleAndRbkey(Mockito.any(), Mockito.any())).thenReturn(bundles);
		when(menuTypeRepository.findById(Mockito.any())).thenReturn(Optional.of(new MenuType()));
		BffCoreResponse response = menuserviceImpl.deleteMenuById(menuId);
		assertEquals(BffResponseCode.MENU_DELETE_SUCCESS_CD.getCode(), response.getCode());
		
	}

	@Test
	public void testDeleteMenuById_Exception() {
		UUID menuId=UUID.randomUUID();
		when(menuMasterRepository.findById(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		when(productConfigRepo.findById(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = menuserviceImpl.deleteMenuById(menuId);
		assertEquals(BffResponseCode.MENU_DELETE_SYS_ERR_CD.getCode(), response.getCode());
	}

	@Test
	public void testDeleteMenuById_DBException() {
		UUID menuId=UUID.randomUUID();
		when(menuMasterRepository.findById(Mockito.any())).thenThrow(new DataBaseException("Menu deletion failed"));
		when(productConfigRepo.findById(Mockito.any())).thenThrow(new DataBaseException("Menu deletion failed"));
		BffCoreResponse response = menuserviceImpl.deleteMenuById(menuId);
		assertEquals(BffResponseCode.MENU_DELETE_DB_ERR_CD.getCode(), response.getCode());
	}
}
