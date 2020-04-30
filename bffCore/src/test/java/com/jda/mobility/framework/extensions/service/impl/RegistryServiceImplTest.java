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

import com.jda.mobility.framework.extensions.entity.ApiMaster;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.entity.UserRole;
import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.exception.DataBaseException;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.repository.ApiMasterRepository;
import com.jda.mobility.framework.extensions.repository.ApiRegistryRepository;
import com.jda.mobility.framework.extensions.repository.UserRoleRepository;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ApiRegistryType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.LayerMode;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 * The class FormServiceImplTest.java
 * 
 * @author HCL Technologies Ltd.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class RegistryServiceImplTest extends AbstractPrepareTest{

	@InjectMocks
	private RegistryServiceImpl registryServiceImpl;
	@Mock
	private ApiRegistryRepository apiRegistryRepository;
	@Mock
	private ApiMasterRepository apiMasterRepository;
	@Mock
	private UserRoleRepository userRoleRepository;
	/**
	 * Test method for registryId
	 */

	@Test
	public void testfetchAllRegistries() {
		when(apiRegistryRepository.findAllByOrderByName()).thenReturn(getAllRegistry());
		when(userRoleRepository.findByUserId(Mockito.any())).thenReturn(Optional.of(getUserRole()));
	
		BffCoreResponse response = registryServiceImpl.fetchAllRegistries(LayerMode.CURRENT_LAYER);
		assertEquals(BffResponseCode.REGISTRY_SERVICE_SUCCESS_CODE_FETCH_ALL.getCode(), response.getCode());

		BffCoreResponse response1 = registryServiceImpl.fetchAllRegistries(LayerMode.ALL);
		assertEquals(BffResponseCode.REGISTRY_SERVICE_SUCCESS_CODE_FETCH_ALL.getCode(), response1.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testfetchAllRegistriesDBExcption() {
		when(apiRegistryRepository.findAllByOrderByName())
				.thenThrow(new DataBaseException("Registry list retrieval failed"));
		BffCoreResponse response = registryServiceImpl.fetchAllRegistries(LayerMode.CURRENT_LAYER);
		assertEquals(BffResponseCode.ERR_REGISTRY_SERVICE_API_FETCH_ALL_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testfetchAllRegistriesExcption() {
		when(apiRegistryRepository.findAllByOrderByName()).thenReturn(registry());
		BffCoreResponse response = registryServiceImpl.fetchAllRegistries(LayerMode.CURRENT_LAYER);
		assertEquals(BffResponseCode.ERR_REGISTRY_SERVICE_API_FETCH_ALL_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());

	}
	
	
	@Test
	public void testFetchRegistries() {
		List<ApiRegistry> allRegistries = new ArrayList<>();
		ApiRegistry apiRegistry= new ApiRegistry();
		apiRegistry.setApiType("ORCHESTRATION");
		ApiRegistry apiRegistry1= new ApiRegistry();
		apiRegistry.setApiType("INTERNAL");
		allRegistries.add(apiRegistry1);
		allRegistries.add(apiRegistry);
		when(apiRegistryRepository.findByApiTypeInOrderByName(Mockito.any())).thenReturn(Optional.of(allRegistries));
		when(userRoleRepository.findByUserId(Mockito.any())).thenReturn(Optional.of(getUserRole()));
		BffCoreResponse response = registryServiceImpl.fetchRegistries(Arrays.asList(ApiRegistryType.ORCHESTRATION, ApiRegistryType.INTERNAL),LayerMode.CURRENT_LAYER);
		assertEquals(BffResponseCode.REGISTRY_SERVICE_SUCCESS_CODE_FETCH_ALL.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
		BffCoreResponse response1 = registryServiceImpl.fetchRegistries(Arrays.asList(ApiRegistryType.ORCHESTRATION, ApiRegistryType.INTERNAL),LayerMode.ALL);
		assertEquals(BffResponseCode.REGISTRY_SERVICE_SUCCESS_CODE_FETCH_ALL.getCode(), response1.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchRegistriesDBExcption() {
		when(apiRegistryRepository.findByApiTypeInOrderByName(Mockito.any()))
				.thenThrow(new DataBaseException("Registry list retrieval failed"));
		BffCoreResponse response = registryServiceImpl.fetchRegistries(Arrays.asList(ApiRegistryType.ORCHESTRATION, ApiRegistryType.INTERNAL),LayerMode.CURRENT_LAYER);
		assertEquals(BffResponseCode.ERR_REGISTRY_SERVICE_API_FETCH_ALL_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchRegistriesExcption() {
		when(apiRegistryRepository.findByApiTypeInOrderByName(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = registryServiceImpl.fetchRegistries(Arrays.asList(ApiRegistryType.ORCHESTRATION, ApiRegistryType.INTERNAL),LayerMode.CURRENT_LAYER);
		assertEquals(BffResponseCode.ERR_REGISTRY_SERVICE_API_FETCH_ALL_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());

	}

	@Test
	public void testfetchRegistryById() {
		UserRole userRole = new UserRole();
		String registryId = "0f964ee1-4b41-4783-986f-f1ec96f48918";
		when(apiRegistryRepository.findById(UUID.fromString(registryId)))
				.thenReturn(Optional.of(getFindbyRegistryId()));
		when(userRoleRepository.findByUserId(Mockito.any())).thenReturn(Optional.of(userRole));
		BffCoreResponse response = registryServiceImpl.fetchRegistryById(UUID.fromString(registryId));
		assertEquals(BffResponseCode.REGISTRY_SERVICE_SUCCESS_CODE_FETCH_ID.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testfetchRegistryByIdDBExcption() {
		String registryId = "0f964ee1-4b41-4783-986f-f1ec96f48918";
		when(apiRegistryRepository.findById(UUID.fromString(registryId)))
				.thenThrow(new DataBaseException("Registry retrieval failed for Given Id"));
		BffCoreResponse response = registryServiceImpl.fetchRegistryById(UUID.fromString(registryId));
		assertEquals(BffResponseCode.ERR_REGISTRY_SERVICE_API_FETCH_ID_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testfetchRegistryByIdExcption() {
		String registryId = "0f964ee1-4b41-4783-986f-f1ec96f48918";
		BffCoreResponse response = registryServiceImpl.fetchRegistryById(UUID.fromString(registryId));
		assertEquals(BffResponseCode.ERR_REGISTRY_SERVICE_API_FETCH_ID_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testfetchAllApis() {
		when(apiMasterRepository.findAll()).thenReturn(getAllApimaster());		
		BffCoreResponse response = registryServiceImpl.fetchAllApis();
		assertEquals(BffResponseCode.APIMASTER_SERVICE_SUCCESS_CODE_FETCH_ALL_APIS.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testfetchAllApisDBExcption() {
		when(apiMasterRepository.findAll()).thenThrow(new DataBaseException("Master Registry list retrieval failed"));
		BffCoreResponse response = registryServiceImpl.fetchAllApis();
		assertEquals(BffResponseCode.ERR_APIMASTER_SERVICE_API_FETCH_ALL_APIS_DBEXCEPTION.getCode(),
				response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testfetchAllApisExcption() {
		when(apiMasterRepository.findAll()).thenReturn(allApimaster());
		when(apiMasterRepository.findAll()).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = registryServiceImpl.fetchAllApis();
		assertEquals(BffResponseCode.ERR_APIMASTER_SERVICE_API_FETCH_ALL_APIS_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testfetchApiByRegistryId() {
		String registryId = "0f964ee1-4b41-4783-986f-f1ec96f48918";
		ApiRegistry ap = new ApiRegistry();
		ap.setApiMasters((List<ApiMaster>) getAllApimaster());
		when(apiRegistryRepository.findById(UUID.fromString(registryId))).thenReturn(Optional.of(ap));
		BffCoreResponse response = registryServiceImpl.fetchApiByRegistryId(UUID.fromString(registryId));
		assertEquals(BffResponseCode.APIMASTER_SERVICE_SUCCESS_CODE_FETCH_REGISTRYID.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testfetchApiByRegistryIdDBExcption() {
		String registryId = "0f964ee1-4b41-4783-986f-f1ec96f48918";
		when(apiRegistryRepository.findById(UUID.fromString(registryId)))
				.thenThrow(new DataBaseException("Registry retrieval failed for given registry Id"));
		BffCoreResponse response = registryServiceImpl.fetchApiByRegistryId(UUID.fromString(registryId));
		assertEquals(BffResponseCode.ERR_APIMASTER_SERVICE_API_FETCH_REGISTRYID_DBEXCEPTION.getCode(),
				response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testfetchApiByRegistryIdExcption() {
		String registryId = "0f964ee1-4b41-4783-986f-f1ec96f48918";
		ApiRegistry ap = new ApiRegistry();
		ap.setApiMasters((List<ApiMaster>) allApimaster());
		when(apiRegistryRepository.findById(UUID.fromString(registryId))).thenReturn(Optional.of(ap));
		when(apiRegistryRepository.findById(UUID.fromString(registryId))).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = registryServiceImpl.fetchApiByRegistryId(UUID.fromString(registryId));
		assertEquals(BffResponseCode.ERR_APIMASTER_SERVICE_API_FETCH_REGISTRYID_EXCEPTION.getCode(),
				response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testfetchApiById() {
		String registryId = "1337ce65-f95b-4fa3-8f84-7f1ba73f580e";
		List<ApiMaster> apiRegistryList = new ArrayList<>();
		ApiRegistry ap = new ApiRegistry();
		ap.setName("WMS");
		ApiMaster apiMasterRegistry = new ApiMaster();
		apiMasterRegistry.setApiRegistry(ap);
		apiRegistryList.add(apiMasterRegistry);
		when(apiMasterRepository.findById(UUID.fromString(registryId))).thenReturn(Optional.of(getFindApimasterbyId()));
		BffCoreResponse response = registryServiceImpl.fetchApiById(UUID.fromString(registryId));
		assertEquals(BffResponseCode.APIMASTER_SERVICE_SUCCESS_CODE_FETCHBYID.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testfetchApiByIdDBExcption() {
		String registryId = "0c24d225-e89d-4c3d-95c3-035606262ce6";
		when(apiMasterRepository.findById(UUID.fromString(registryId)))
				.thenThrow(new DataBaseException("APIs retrieval failed for given Id"));
		BffCoreResponse response = registryServiceImpl.fetchApiById(UUID.fromString(registryId));
		assertEquals(BffResponseCode.ERR_APIMASTER_SERVICE_API_FETCHBYID_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testfetchApiByIdExcption() {
		String registryId = "0c24d225-e89d-4c3d-95c3-035606262ce6";
		BffCoreResponse response = registryServiceImpl.fetchApiById(UUID.fromString(registryId));
		assertEquals(BffResponseCode.ERR_APIMASTER_SERVICE_API_FETCHBYID_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	private List<ApiRegistry> getAllRegistry() {

		List<ApiRegistry> registry = null;
		List<ApiRegistry> apiRegistryList = new ArrayList<>();
		ApiRegistry apiRegistry = new ApiRegistry();
		apiRegistry.setUid(UUID.fromString("776387dc-57af-488e-88e5-d05eae572d1c"));
		apiRegistry.setApiType("internal");
		apiRegistry.setApiVersion("2.0");
		apiRegistry.setBasePath("/ws/admin");
		apiRegistry.setContextPath("localhost");
		apiRegistry.setCreatedBy("SUPER");
		apiRegistry.setCreationDate(null);
		apiRegistry.setHelperClass(null);
		apiRegistry.setLastModifiedBy(null);
		apiRegistry.setLastModifiedDate(null);
		apiRegistry.setName("Item");
		apiRegistry.setPort("4500");
		apiRegistry.setVersionId(0);
		apiRegistry.setRoleMaster(getUserRole().getRoleMaster());
		apiRegistryList.add(apiRegistry);
		registry = apiRegistryList;
		return registry;

	}

	private List<ApiRegistry> registry() {

		List<ApiRegistry> registry = null;
		List<ApiRegistry> apiRegistryList = new ArrayList<>();
		ApiRegistry apiRegistry = new ApiRegistry();
		apiRegistry.setApiType("internal");
		apiRegistry.setApiVersion("2.0");
		apiRegistry.setBasePath("/ws/admin");
		apiRegistry.setContextPath("localhost");
		apiRegistry.setCreatedBy("SUPER");
		apiRegistry.setCreationDate(null);
		apiRegistry.setHelperClass(null);
		apiRegistry.setLastModifiedBy(null);
		apiRegistry.setLastModifiedDate(null);
		apiRegistry.setName("Item");
		apiRegistry.setPort("4500");
		apiRegistry.setVersionId(0);
		apiRegistryList.add(apiRegistry);
		registry = apiRegistryList;
		return registry;

	}

	private List<ApiMaster> getAllApimaster() {

		List<ApiMaster> registry = null;
		List<ApiMaster> apiRegistryList = new ArrayList<>();
		ApiRegistry ap = new ApiRegistry();
		ap.setName("WMS");
		ApiMaster apiMasterRegistry = new ApiMaster();
		apiMasterRegistry.setUid(UUID.fromString("776387dc-57af-488e-88e5-d05eae572d1c"));
		apiMasterRegistry.setName(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setRequestBody(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setRequestEndpoint(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setRequestMethod(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setRequestPathparams(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setRequestPreproc(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setRequestQuery(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setResponsePostproc(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setResponseSchema(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setVersion(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setApiRegistry(ap);
		apiRegistryList.add(apiMasterRegistry);
		registry = apiRegistryList;
		return registry;

	}

	private List<ApiMaster> allApimaster() {

		List<ApiMaster> registry = null;
		List<ApiMaster> apiRegistryList = new ArrayList<>();
		ApiMaster apiMasterRegistry = new ApiMaster();
		apiMasterRegistry.setUid(UUID.fromString("776387dc-57af-488e-88e5-d05eae572d1c"));
		apiMasterRegistry.setName(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setRequestBody(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setRequestEndpoint(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setRequestMethod(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setRequestPathparams(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setRequestPreproc(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setRequestQuery(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setResponsePostproc(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setResponseSchema(BffAdminConstantsUtils.EMPTY_SPACES);
		apiMasterRegistry.setVersion(BffAdminConstantsUtils.EMPTY_SPACES);
		apiRegistryList.add(apiMasterRegistry);
		registry = apiRegistryList;
		return registry;

	}
	private ApiRegistry getFindbyRegistryId() {
		ApiRegistry apiRegistry = new ApiRegistry();
		apiRegistry.setUid(UUID.fromString("0f964ee1-4b41-4783-986f-f1ec96f48918"));
		return apiRegistry;
	}
	private ApiMaster getFindApimasterbyId() {
		
		
		List<ApiMaster> apiRegistryList = new ArrayList<>();
		ApiRegistry ap = new ApiRegistry();
		ap.setName("WMS");
		ApiMaster apiMasterRegistry = new ApiMaster();
		apiMasterRegistry.setUid(UUID.fromString("1337ce65-f95b-4fa3-8f84-7f1ba73f580e"));
		apiMasterRegistry.setApiRegistry(ap);
		apiRegistryList.add(apiMasterRegistry);
		return apiMasterRegistry;
	}
	/**
	 * @return UserRole
	 */
	private UserRole getUserRole() {
		UserRole userRole = new UserRole();
		userRole.setUid(UUID.fromString("e8fcb1a7-d453-4ee3-8aaa-b0bd230e27f3"));
		userRole.setUserId("TEST");
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setUid(UUID.fromString("1337ce65-f95b-4fa3-8f84-7f1ba73f580e"));
		roleMaster.setLevel(0);
		userRole.setRoleMaster(roleMaster);
		return userRole;
	}

	
}
