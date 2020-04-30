package com.jda.mobility.framework.extensions.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import com.jda.mobility.framework.extensions.entity.ApiMaster;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.entity.UserRole;
import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.exception.DataBaseException;
import com.jda.mobility.framework.extensions.model.ApiMasterRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.repository.ApiMasterRepository;
import com.jda.mobility.framework.extensions.repository.ApiRegistryRepository;
import com.jda.mobility.framework.extensions.repository.UserRoleRepository;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 * The class ApiImportServiceImplTest.java
 * 
 * @author HCL Technologies Ltd.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ApiImportServiceImplTest extends AbstractPrepareTest{

	@InjectMocks
	private ApiImportServiceImpl apiImportServiceImpl;
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
	public void testCreateRegistry() {
		ApiMaster apiMaster=new ApiMaster();
		apiMaster.setUid(UUID.randomUUID());
		ApiRegistry apiRegistry = new ApiRegistry();
		apiRegistry.setApiType("ORCHESTRATION");
		apiRegistry.setUid(UUID.randomUUID());
		apiMaster.setApiRegistry(apiRegistry);
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setName("Orcch");
		roleMaster.setLevel(1);
		UserRole userRole = new UserRole();
		userRole.setUid(UUID.randomUUID());
		userRole.setUserId("SUPER");
		userRole.setRoleMaster(roleMaster);
		apiRegistry.setRoleMaster(roleMaster);
		when(apiMasterRepository.findByApiRegistryAndOrchestrationName(Mockito.any(),Mockito.any())).thenReturn(Optional.of(apiMaster));
		when(apiRegistryRepository.save(Mockito.any())).thenReturn(apiRegistry);
		when(apiRegistryRepository.countByNameAndApiTypeAndRoleMaster(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(0);
		when(apiMasterRepository.saveAll(Mockito.any())).thenReturn(new ArrayList<ApiMaster>());
		when(apiRegistryRepository.save(Mockito.any())).thenReturn(apiRegistry);
		MockMultipartFile firstFile = new MockMultipartFile("json", "Auth.json", "application/json",
				createSwaggerFileString().getBytes());
	MockMultipartFile ruleFile = new MockMultipartFile("rule","ORCHEXECUTION.DSLR","application/txt",createRuleFileString().getBytes());
		when(userRoleRepository.findByUserId(Mockito.any())).thenReturn(Optional.of(userRole));
		BffCoreResponse response = apiImportServiceImpl.importApiIntoNewRegistry(firstFile, "sample reg", BffAdminConstantsUtils.ApiRegistryType.ORCHESTRATION,ruleFile);
		assertEquals(BffResponseCode.API_IMPORT_SERVICE_SUCCESS_CODE_NEW_REGISTRY.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateRegistryNonUniqueRegistry() {
		UserRole userRole = new UserRole();
		when(userRoleRepository.findByUserId(Mockito.any())).thenReturn(Optional.of(userRole));
		when(apiRegistryRepository.countByNameAndApiTypeAndRoleMaster(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);
		BffCoreResponse bffResponse1 = new BffCoreResponse();
		bffResponse1.setHttpStatusCode(StatusCode.BADREQUEST.getValue());
		MockMultipartFile firstFile = new MockMultipartFile("json", "Auth.json", "application/json",
				createSwaggerFileString().getBytes());
		MockMultipartFile ruleFile = new MockMultipartFile("rule","ORCHEXECUTION.DSLR","application/txt",createRuleFileString().getBytes());
		BffCoreResponse response = apiImportServiceImpl.importApiIntoNewRegistry(firstFile, "sample reg", BffAdminConstantsUtils.ApiRegistryType.INTERNAL,ruleFile);
		assertEquals(BffResponseCode.API_IMPORT_SERVICE_SUCCESS_CODE_NOT_UNIQUE_REGISTRY.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testCreateRegistryNonUniqueRegistry_Swagger_Validation_Exception() {
		byte[] source = "Invalid Rule Content".getBytes();
		UserRole userRole = new UserRole();
		when(userRoleRepository.findByUserId(Mockito.any())).thenReturn(Optional.of(userRole));
		when(apiRegistryRepository.countByNameAndApiTypeAndRoleMaster(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(0);
		BffCoreResponse bffResponse1 = new BffCoreResponse();
		bffResponse1.setHttpStatusCode(StatusCode.OK.getValue());
		MockMultipartFile firstFile = new MockMultipartFile("json", "Auth.json", "application/json",source);
		MockMultipartFile ruleFile = new MockMultipartFile("rule","ORCHEXECUTION.DSLR","application/txt",source);
		BffCoreResponse response = apiImportServiceImpl.importApiIntoNewRegistry(firstFile, "sample reg", BffAdminConstantsUtils.ApiRegistryType.INTERNAL,ruleFile);
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
		assertEquals(BffResponseCode.VALIDATIION_IMPORT_SWAGGER_FILE.getCode(), response.getCode());
	}
	
	
	@Test
	public void testCreateRegistryNonUniqueRegistry_Exception() {
		ApiMaster apiMaster=new ApiMaster();
		apiMaster.setUid(UUID.randomUUID());
		ApiRegistry apiRegistry = new ApiRegistry();
		apiRegistry.setApiType("INTERNAL");
		apiRegistry.setUid(UUID.randomUUID());
		apiMaster.setApiRegistry(apiRegistry);
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setName("Orcch");
		roleMaster.setLevel(1);
		UserRole userRole = new UserRole();
		userRole.setUid(UUID.randomUUID());
		userRole.setUserId("SUPER");
		userRole.setRoleMaster(roleMaster);
		apiRegistry.setRoleMaster(roleMaster);
		when(userRoleRepository.findByUserId(Mockito.any())).thenReturn(Optional.of(userRole));
		when(apiRegistryRepository.countByNameAndApiTypeAndRoleMaster(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		MultipartFile firstFile = new MockMultipartFile("json", "Auth.json", "application/json",
				createSwaggerFileString().getBytes());
		MultipartFile ruleFile = new MockMultipartFile("rule","ORCHEXECUTION.DSLR","application/txt",createRuleFileString().getBytes());
		BffCoreResponse response = apiImportServiceImpl.importApiIntoNewRegistry(firstFile, "sample reg", BffAdminConstantsUtils.ApiRegistryType.INTERNAL,ruleFile);
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
		assertEquals(BffResponseCode.ERR_APIIMPORT_API_UNIQUE_REGISTRY_EXCEPTION.getCode(), response.getCode());
	}

	@Test
	public void testCreateRegistryDatabaseException() {
		UserRole userRole = new UserRole();
		when(userRoleRepository.findByUserId(Mockito.any())).thenReturn(Optional.of(userRole));
		when(apiRegistryRepository.save(Mockito.any())).thenThrow(new DataBaseException("API Registry save failed"));
		MockMultipartFile ruleFile = new MockMultipartFile("rule","ORCHEXECUTION.DSLR","application/txt",createRuleFileString().getBytes());
		MockMultipartFile firstFile = new MockMultipartFile("json", "Auth.json", "application/json",
				createSwaggerFileString().getBytes());
		BffCoreResponse response = apiImportServiceImpl.importApiIntoNewRegistry(firstFile, "sample reg", BffAdminConstantsUtils.ApiRegistryType.INTERNAL,ruleFile);
		assertEquals(BffResponseCode.ERR_APIIMPORT_API_NEW_REGISTRY_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateRegistryException() {
		MultipartFile firstFile = new MockMultipartFile("json", "Auth.json", "application/json",
				createSwaggerFileString().getBytes());
		MultipartFile ruleFile = new MockMultipartFile("rule","ORCHEXECUTION.DSLR","application/txt",createRuleFileString().getBytes());
		BffCoreResponse response = apiImportServiceImpl.importApiIntoNewRegistry(firstFile, "sample reg", null,ruleFile);
		assertEquals(BffResponseCode.ERR_APIIMPORT_API_NEW_REGISTRY_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
		
		BffCoreResponse response1 = apiImportServiceImpl.importApiIntoNewRegistry(firstFile, "sample reg", null,ruleFile);
		assertEquals(BffResponseCode.ERR_APIIMPORT_API_NEW_REGISTRY_EXCEPTION.getCode(), response1.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testModifyRegistryAppend() {
		ApiRegistry apiRegistry = new ApiRegistry();
		apiRegistry.setUid(UUID.randomUUID());
		when(apiRegistryRepository.findById(Mockito.any())).thenReturn(Optional.of(createApiRegistrySample()));
		when(apiMasterRepository.saveAll(Mockito.any())).thenReturn(new ArrayList<ApiMaster>());
		MockMultipartFile ruleFile = new MockMultipartFile("rule","ORCHEXECUTION.DSLR","application/txt",createRuleFileString().getBytes());
		BffCoreResponse response = apiImportServiceImpl
				.importApiIntoExistingRegistry(createSwaggerFileString().getBytes(), false, UUID.randomUUID(),ruleFile);
		assertEquals(BffResponseCode.API_IMPORT_SERVICE_SUCCESS_CODE_MODIFY_REGISTRY.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testModifyRegistryOverride() {
		ApiRegistry apiRegistry = new ApiRegistry();
		apiRegistry.setUid(UUID.randomUUID());
		when(apiRegistryRepository.findById(Mockito.any())).thenReturn(Optional.of(createApiRegistrySample()));
		when(apiMasterRepository.saveAll(Mockito.any())).thenReturn(new ArrayList<ApiMaster>());
		MockMultipartFile ruleFile = new MockMultipartFile("rule","ORCHEXECUTION.DSLR","application/txt",createRuleFileString().getBytes());
		BffCoreResponse response = apiImportServiceImpl
				.importApiIntoExistingRegistry(createSwaggerFileString().getBytes(), true, UUID.randomUUID(),ruleFile);
		assertEquals(BffResponseCode.API_IMPORT_SERVICE_SUCCESS_CODE_MODIFY_REGISTRY.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testModifyRegistryDatabaseException() {
		when(apiRegistryRepository.findById(Mockito.any())).thenThrow(new DataBaseException("API Registry retrieval failed"));
		MockMultipartFile ruleFile = new MockMultipartFile("rule","ORCHEXECUTION.DSLR","application/txt",createRuleFileString().getBytes());
		BffCoreResponse response = apiImportServiceImpl
				.importApiIntoExistingRegistry(createSwaggerFileString().getBytes(), true, UUID.randomUUID(),ruleFile);
		assertEquals(BffResponseCode.ERR_APIIMPORT_API_MODIFY_REGISTRY_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testModifyRegistryException() {
		MockMultipartFile ruleFile = new MockMultipartFile("rule","ORCHEXECUTION.DSLR","application/txt",createRuleFileString().getBytes());
		BffCoreResponse response = apiImportServiceImpl
				.importApiIntoExistingRegistry(null, true, UUID.randomUUID(),ruleFile);
		assertEquals(BffResponseCode.ERR_APIIMPORT_API_MODIFY_REGISTRY_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testOverrideExistingApis() {
		ApiRegistry apiRegistry = new ApiRegistry();
		apiRegistry.setUid(UUID.randomUUID());
		when(apiRegistryRepository.findById(Mockito.any())).thenReturn(Optional.of(createApiRegistrySample()));
		when(apiMasterRepository.saveAll(Mockito.any())).thenReturn(new ArrayList<ApiMaster>());
		BffCoreResponse response = apiImportServiceImpl.overrideExistingApis(createApiMastersRequestSample(),
				UUID.randomUUID());
		assertEquals(BffResponseCode.API_IMPORT_SERVICE_SUCCESS_CODE_OVERRIDE_APIS.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testOverrideExistingApisDatabaseException() {
		when(apiRegistryRepository.findById(Mockito.any())).thenThrow(new DataBaseException("API Registry retrieval failed"));
		BffCoreResponse response = apiImportServiceImpl.overrideExistingApis(createApiMastersRequestSample(),
				UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_APIIMPORT_API_OVERRIDE_APIS_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testOverrideExistingApisException() {
		BffCoreResponse response = apiImportServiceImpl.overrideExistingApis(createApiMastersRequestSample(),
				UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_APIIMPORT_API_OVERRIDE_APIS_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	private ApiRegistry createApiRegistrySample() {
		ApiRegistry apiRegistry = new ApiRegistry();
		apiRegistry.setApiType("ORCHESTRATION");
		List<ApiMaster> apiMasters = createApiMastersSample();
		apiRegistry.setApiMasters(apiMasters);
		return apiRegistry;
	}

	private List<ApiMaster> createApiMastersSample() {
		List<ApiMaster> apiMasters = new ArrayList<ApiMaster>();
		ApiMaster apiMaster = new ApiMaster();
		apiMaster.setName("login");
		apiMaster.setRequestMethod("GET");
		apiMaster.setRequestEndpoint("/login");
		apiMasters.add(apiMaster);
		return apiMasters;
	}

	private List<ApiMasterRequest> createApiMastersRequestSample() {
		List<ApiMasterRequest> apiMasters = new ArrayList<ApiMasterRequest>();
		ApiMasterRequest apiMaster = new ApiMasterRequest();
		apiMaster.setName("login");
		apiMaster.setRequestMethod("GET");
		apiMaster.setRequestEndpoint("/login");
		apiMasters.add(apiMaster);
		return apiMasters;
	}
	
	private String createRuleFileString() {
		
		StringBuilder ruleBuilder = new StringBuilder();
		ruleBuilder.append("Include Libraries\r\n" + 
				"\r\n" + 
				"Step \"OrchExecution\"\r\n" + 
				"	Priority 3");
		ruleBuilder.append("Orchestration \"ORCHDEMOEXECUTION\"\r\n" + 
				"	when \r\n" + 
				"		Get Inputs");
		ruleBuilder.append("then\r\n" + 
				"		Say \"***Orchestration  Execution of WorkArea the api****\" ");
		ruleBuilder.append("end");
		
		return ruleBuilder.toString();
		
		
		
		
	}

	private String createSwaggerFileString() {
		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("{  ");
		jsonBuilder.append("   \"swagger\":\"2.0\",");
		jsonBuilder.append("   \"info\":{  ");
		jsonBuilder.append(
				"      \"description\":\"JDA's private web service API documentation. This information is subject to change at any time.\",");
		jsonBuilder.append("      \"version\":\"1.0\",");
		jsonBuilder.append("      \"title\":\"JDA Private API Documentation\"");
		jsonBuilder.append("   },");
		jsonBuilder.append("   \"host\":\"localhost:4500\",");
		jsonBuilder.append("   \"basePath\":\"/ws/auth\",");
		jsonBuilder.append("   \"tags\":[  ");
		jsonBuilder.append("      {  ");
		jsonBuilder.append("         \"name\":\"loginservice\",");
		jsonBuilder.append("         \"description\":\"The operations to login to the server.\"");
		jsonBuilder.append("      },");
		jsonBuilder.append("      {  ");
		jsonBuilder.append("         \"name\":\"user-authorizations-controller\",");
		jsonBuilder.append("         \"description\":\"User Authorizations Controller\"");
		jsonBuilder.append("      }");
		jsonBuilder.append("   ],");
		jsonBuilder.append("   \"paths\":{  ");
		jsonBuilder.append("      \"/login\":{  ");
		jsonBuilder.append("         \"get\":{  ");
		jsonBuilder.append("            \"tags\":[  ");
		jsonBuilder.append("               \"loginservice\"");
		jsonBuilder.append("            ],");
		jsonBuilder.append("            \"summary\":\"A GET endpoint used for logging in.\",");
		jsonBuilder.append("            \"operationId\":\"login\",");
		jsonBuilder.append("            \"produces\":[  ");
		jsonBuilder.append("               \"*/*\"");
		jsonBuilder.append("            ],");
		jsonBuilder.append("            \"parameters\":[  ");
		jsonBuilder.append("               {  ");
		jsonBuilder.append("                  \"name\":\"password\",");
		jsonBuilder.append("                  \"in\":\"query\",");
		jsonBuilder.append("                  \"description\":\"The user's password\",");
		jsonBuilder.append("                  \"required\":true,");
		jsonBuilder.append("                  \"type\":\"string\",");
		jsonBuilder.append("                  \"allowEmptyValue\":false");
		jsonBuilder.append("               },");
		jsonBuilder.append("               {  ");
		jsonBuilder.append("                  \"name\":\"usr_id\",");
		jsonBuilder.append("                  \"in\":\"query\",");
		jsonBuilder.append("                  \"description\":\"The user's userId\",");
		jsonBuilder.append("                  \"required\":true,");
		jsonBuilder.append("                  \"type\":\"string\",");
		jsonBuilder.append("                  \"allowEmptyValue\":false");
		jsonBuilder.append("               }");
		jsonBuilder.append("            ],");
		jsonBuilder.append("            \"responses\":{  ");
		jsonBuilder.append("               \"200\":{  ");
		jsonBuilder.append("                  \"description\":\"OK\"");
		jsonBuilder.append("               },");
		jsonBuilder.append("               \"401\":{  ");
		jsonBuilder.append("                  \"description\":\"Unauthorized\"");
		jsonBuilder.append("               },");
		jsonBuilder.append("               \"403\":{  ");
		jsonBuilder.append("                  \"description\":\"Forbidden\"");
		jsonBuilder.append("               },");
		jsonBuilder.append("               \"404\":{  ");
		jsonBuilder.append("                  \"description\":\"Not Found\"");
		jsonBuilder.append("               }");
		jsonBuilder.append("            }");
		jsonBuilder.append("         },");
		jsonBuilder.append("         \"post\":{  ");
		jsonBuilder.append("            \"tags\":[  ");
		jsonBuilder.append("               \"loginservice\"");
		jsonBuilder.append("            ],");
		jsonBuilder.append("            \"summary\":\"A POST endpoint used for logging in.\",");
		jsonBuilder.append("            \"operationId\":\"login\",");
		jsonBuilder.append("            \"consumes\":[  ");
		jsonBuilder.append("               \"application/json\"");
		jsonBuilder.append("            ],");
		jsonBuilder.append("            \"produces\":[  ");
		jsonBuilder.append("               \"*/*\"");
		jsonBuilder.append("            ],");
		jsonBuilder.append("            \"parameters\":[  ");
		jsonBuilder.append("               {  ");
		jsonBuilder.append("                  \"in\":\"body\",");
		jsonBuilder.append("                  \"name\":\"body\",");
		jsonBuilder.append("                  \"description\":\"A json map of the user's login credentials\",");
		jsonBuilder.append("                  \"required\":true,");
		jsonBuilder.append("                  \"schema\":{  ");
		jsonBuilder.append("                     \"type\":\"object\",");
		jsonBuilder.append("                     \"additionalProperties\":{  ");
		jsonBuilder.append("                        \"type\":\"string\"");
		jsonBuilder.append("                     }");
		jsonBuilder.append("                  }");
		jsonBuilder.append("               }");
		jsonBuilder.append("            ],");
		jsonBuilder.append("            \"responses\":{  ");
		jsonBuilder.append("               \"200\":{  ");
		jsonBuilder.append("                  \"description\":\"OK\"");
		jsonBuilder.append("               },");
		jsonBuilder.append("               \"201\":{  ");
		jsonBuilder.append("                  \"description\":\"Created\"");
		jsonBuilder.append("               },");
		jsonBuilder.append("               \"401\":{  ");
		jsonBuilder.append("                  \"description\":\"Unauthorized\"");
		jsonBuilder.append("               },");
		jsonBuilder.append("               \"403\":{  ");
		jsonBuilder.append("                  \"description\":\"Forbidden\"");
		jsonBuilder.append("               },");
		jsonBuilder.append("               \"404\":{  ");
		jsonBuilder.append("                  \"description\":\"Not Found\"");
		jsonBuilder.append("               }");
		jsonBuilder.append("            }");
		jsonBuilder.append("         }");
		jsonBuilder.append("      },");
		jsonBuilder.append("      \"/userAuthorizations\":{  ");
		jsonBuilder.append("         \"get\":{  ");
		jsonBuilder.append("            \"tags\":[  ");
		jsonBuilder.append("               \"user-authorizations-controller\"");
		jsonBuilder.append("            ],");
		jsonBuilder.append("            \"summary\":\"get\",");
		jsonBuilder.append(
				"            \"description\":\"Returns authorization information for the user access token used to make the request\",");
		jsonBuilder.append("            \"operationId\":\"get\",");
		jsonBuilder.append("            \"produces\":[  ");
		jsonBuilder.append("               \"application/json\"");
		jsonBuilder.append("            ],");
		jsonBuilder.append("            \"responses\":{  ");
		jsonBuilder.append("               \"200\":{  ");
		jsonBuilder.append("                  \"description\":\"OK\",");
		jsonBuilder.append("                  \"schema\":{  ");
		jsonBuilder.append("                     \"$ref\":\"#/definitions/UserAuthorization\"");
		jsonBuilder.append("                  }");
		jsonBuilder.append("               },");
		jsonBuilder.append("               \"401\":{  ");
		jsonBuilder.append("                  \"description\":\"Unauthorized\"");
		jsonBuilder.append("               },");
		jsonBuilder.append("               \"403\":{  ");
		jsonBuilder.append("                  \"description\":\"Forbidden\"");
		jsonBuilder.append("               },");
		jsonBuilder.append("               \"404\":{  ");
		jsonBuilder.append("                  \"description\":\"Not Found\"");
		jsonBuilder.append("               }");
		jsonBuilder.append("            }");
		jsonBuilder.append("         }");
		jsonBuilder.append("      }");
		jsonBuilder.append("   },");
		jsonBuilder.append("   \"definitions\":{  ");
		jsonBuilder.append("      \"RoleOption\":{  ");
		jsonBuilder.append("         \"type\":\"object\",");
		jsonBuilder.append("         \"properties\":{  ");
		jsonBuilder.append("            \"name\":{  ");
		jsonBuilder.append("               \"type\":\"string\"");
		jsonBuilder.append("            },");
		jsonBuilder.append("            \"type\":{  ");
		jsonBuilder.append("               \"type\":\"string\"");
		jsonBuilder.append("            }");
		jsonBuilder.append("         },");
		jsonBuilder.append("         \"title\":\"RoleOption\"");
		jsonBuilder.append("      },");
		jsonBuilder.append("      \"UserAuthorization\":{  ");
		jsonBuilder.append("         \"type\":\"object\",");
		jsonBuilder.append("         \"properties\":{  ");
		jsonBuilder.append("            \"roleOptions\":{  ");
		jsonBuilder.append("               \"type\":\"array\",");
		jsonBuilder.append("               \"items\":{  ");
		jsonBuilder.append("                  \"$ref\":\"#/definitions/RoleOption\"");
		jsonBuilder.append("               }");
		jsonBuilder.append("            },");
		jsonBuilder.append("            \"userId\":{  ");
		jsonBuilder.append("               \"type\":\"string\"");
		jsonBuilder.append("            }");
		jsonBuilder.append("         },");
		jsonBuilder.append("         \"title\":\"UserAuthorization\"");
		jsonBuilder.append("      }");
		jsonBuilder.append("   }");
		jsonBuilder.append("}");
		return jsonBuilder.toString();
	}

}
