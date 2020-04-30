package com.jda.mobility.framework.extensions.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
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
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

@RunWith(SpringJUnit4ClassRunner.class)
public class PreAndPostProcessorServiceImplTest extends AbstractPrepareTest{
	
	@InjectMocks
	private PreAndPostProcessorServiceImpl preAndPostProcessorServiceImpl;
	
	@Mock
	private ApiRegistryRepository apiRegistryRepository;
	@Mock
	private ApiMasterRepository apiMasterRepository;
	@Mock
	private UserRoleRepository userRoleRepository;
	
	@Test
	public void testCreateRegistry() {
		
		UUID apiMasterId=UUID.randomUUID();
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
		when(apiMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(apiMaster));
		when(apiRegistryRepository.save(Mockito.any())).thenReturn(apiRegistry);
		when(apiRegistryRepository.countByNameAndApiTypeAndRoleMaster(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(0);
		when(apiMasterRepository.saveAll(Mockito.any())).thenReturn(new ArrayList<ApiMaster>());
		when(apiRegistryRepository.save(Mockito.any())).thenReturn(apiRegistry);
		MockMultipartFile preProcessorFile = new MockMultipartFile("json", "Auth.json", "application/json",
				createSwaggerFileString().getBytes());
		MockMultipartFile postProcessorFile = new MockMultipartFile("json", "Auth.json", "application/json",
				createSwaggerFileString().getBytes());
		when(userRoleRepository.findByUserId(Mockito.any())).thenReturn(Optional.of(userRole));
		BffCoreResponse response = preAndPostProcessorServiceImpl.importApiIntoNewRegistry(preProcessorFile, postProcessorFile, apiMasterId,BffAdminConstantsUtils.ApiUploadMode.CONFIRM_UPLOAD);
		assertEquals(BffResponseCode.PRE_AND_POST_PROCESSOR_UPLOAD_SUCCESS.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());

	}
	@Test
	public void testCreateRegistry_emptyFileForPreAndPostCheck() {
		
		UUID apiMasterId=UUID.randomUUID();
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
		when(apiMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(apiMaster));
		when(apiRegistryRepository.save(Mockito.any())).thenReturn(apiRegistry);
		when(apiRegistryRepository.countByNameAndApiTypeAndRoleMaster(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(0);
		when(apiMasterRepository.saveAll(Mockito.any())).thenReturn(new ArrayList<ApiMaster>());
		when(apiRegistryRepository.save(Mockito.any())).thenReturn(apiRegistry);
		when(userRoleRepository.findByUserId(Mockito.any())).thenReturn(Optional.of(userRole));
		BffCoreResponse response = preAndPostProcessorServiceImpl.importApiIntoNewRegistry(null, null, apiMasterId,BffAdminConstantsUtils.ApiUploadMode.CONFIRM_UPLOAD);
		assertEquals(BffResponseCode.ERR_PRE_AND_POST_PROCESSOR_FILE_NULL.getCode(), response.getCode());
		assertEquals(StatusCode.CONFLICT.getValue(), response.getHttpStatusCode());

	}
	
	
	@Test
	public void testCreateRegistry_checkUpload() {
		
		UUID apiMasterId=UUID.randomUUID();
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
		when(apiMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(apiMaster));
		MockMultipartFile preProcessorFile = new MockMultipartFile("json", "Auth.json", "application/json",
				createSwaggerFileString().getBytes());
		MockMultipartFile postProcessorFile = new MockMultipartFile("json", "Auth.json", "application/json",
				createSwaggerFileString().getBytes());
		when(userRoleRepository.findByUserId(Mockito.any())).thenReturn(Optional.of(userRole));
		BffCoreResponse response = preAndPostProcessorServiceImpl.importApiIntoNewRegistry(preProcessorFile, postProcessorFile, apiMasterId,BffAdminConstantsUtils.ApiUploadMode.CHECK_UPLOAD);
		assertEquals(BffResponseCode.PRE_AND_POST_PROCESSOR_UPLOAD_CONFIRM_SUCCESS.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());

	}
	@Test
	public void testCreateRegistry_checkUpload_error() {
		
		UUID apiMasterId=UUID.randomUUID();
		ApiMaster apiMaster=new ApiMaster();
		apiMaster.setUid(UUID.randomUUID());
		byte[] source = "Include Libraries  Step \"OrchExecution\" 	Priority 3 	Orchestration \"POSTMODIFICATION\" 	when  		Get Inputs 	then 		Say \"***Pre-processor   Execution  Begins Now****\" 	 end".getBytes();
		apiMaster.setPreProcessor(source);
		apiMaster.setPostProcessor(source);
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
		when(apiMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(apiMaster));
		MockMultipartFile preProcessorFile = new MockMultipartFile("json", "Auth.json", "application/json",
				createSwaggerFileString().getBytes());
		MockMultipartFile postProcessorFile = new MockMultipartFile("json", "Auth.json", "application/json",
				createSwaggerFileString().getBytes());
		when(userRoleRepository.findByUserId(Mockito.any())).thenReturn(Optional.of(userRole));
		BffCoreResponse response = preAndPostProcessorServiceImpl.importApiIntoNewRegistry(preProcessorFile, postProcessorFile, apiMasterId,BffAdminConstantsUtils.ApiUploadMode.CHECK_UPLOAD);
		assertEquals(BffResponseCode.ERR_PRE_AND_POST_PROCESSOR_EXISTING.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());

	}
	
	@Test
	public void testCreateRegistryDatabaseException() {
		UUID apiMasterId=UUID.randomUUID();
		when(apiMasterRepository.findById(Mockito.any())).thenThrow(new DataBaseException("processor file upload failed due to database error"));
		MockMultipartFile firstFile = new MockMultipartFile("json", "Auth.json", "application/json",
				createSwaggerFileString().getBytes());
		MockMultipartFile firstFileSecond = new MockMultipartFile("json", "Auth.json", "application/json",
				createSwaggerFileString().getBytes());
		BffCoreResponse response = preAndPostProcessorServiceImpl.importApiIntoNewRegistry(firstFile, firstFileSecond, apiMasterId,BffAdminConstantsUtils.ApiUploadMode.CONFIRM_UPLOAD);
		assertEquals(BffResponseCode.DB_ERR_PRE_AND_POST_PROCESSOR_UPLOAD.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateRegistryException() {
		UUID apiMasterId=UUID.randomUUID();
		when(apiMasterRepository.findById(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		MockMultipartFile firstFile = new MockMultipartFile("json", "Auth.json", "application/json",
				createSwaggerFileString().getBytes());
		MockMultipartFile firstFileSecond = new MockMultipartFile("json", "Auth.json", "application/json",
				createSwaggerFileString().getBytes());
		BffCoreResponse response = preAndPostProcessorServiceImpl.importApiIntoNewRegistry(firstFile, firstFileSecond, apiMasterId,BffAdminConstantsUtils.ApiUploadMode.CONFIRM_UPLOAD);
		assertEquals(BffResponseCode.ERR_PRE_AND_POST_PROCESSOR_UPLOAD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
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
