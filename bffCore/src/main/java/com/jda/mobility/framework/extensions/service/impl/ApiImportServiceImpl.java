package com.jda.mobility.framework.extensions.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.dto.ApiMasterDto;
import com.jda.mobility.framework.extensions.dto.RegistryDto;
import com.jda.mobility.framework.extensions.entity.ApiMaster;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.entity.UserRole;
import com.jda.mobility.framework.extensions.model.ApiMasterRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.repository.ApiMasterRepository;
import com.jda.mobility.framework.extensions.repository.ApiRegistryRepository;
import com.jda.mobility.framework.extensions.repository.UserRoleRepository;
import com.jda.mobility.framework.extensions.service.ApiImportService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ApiRegistryType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;

import io.swagger.models.ArrayModel;
import io.swagger.models.HttpMethod;
import io.swagger.models.Model;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.RefModel;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerParser;
import io.swagger.util.Json;

/**
 * The class implements create , update and override existing registry and API's
 * 
 * @author HCL Technologies
 */

@Service
public class ApiImportServiceImpl implements ApiImportService {
	private static final Logger LOGGER = LogManager.getLogger(ApiImportServiceImpl.class);
	@Autowired
	private ApiRegistryRepository apiRegistryRepository;

	@Autowired
	private ApiMasterRepository apiMasterRepository;

	@Autowired
	private BffResponse bffResponse;
	@Autowired
	private SessionDetails sessionDetails;
	@Autowired
	private UserRoleRepository userRoleRepository;

	/**
	 * Check for unique registry and if its unique parse the give file set to Entity class
	 *  - Insert Registry and API's
	 * 
	 * @param file
	 * @param registryName
	 * @param apiType
	 * @param ruleFile
	 * @return BffCoreResponse
	 */

	@Override
	public BffCoreResponse importApiIntoNewRegistry(MultipartFile file, String registryName, ApiRegistryType apiType,
			MultipartFile ruleFile) {
		BffCoreResponse bffCoreResponse = null;
		Swagger swagger = null;
		try {
			BffCoreResponse bffCountResponse = null;

			UserRole userRole = userRoleRepository.findByUserId(sessionDetails.getPrincipalName()).orElseThrow();
			RoleMaster roleMaster = userRole.getRoleMaster();

			// Check if registry name is unique or not
			bffCountResponse = uniqueRegistry(registryName, apiType.getType(), roleMaster);

			// If registry name is not unique throw exception
			if (bffCountResponse != null && bffCountResponse.getHttpStatusCode() != StatusCode.OK.getValue()) {
				LOGGER.log(Level.DEBUG, "Registry could not be created for name: {}", registryName);
				return bffCountResponse;
			}
			// If registry name is unique
			else {
				// Parse the given file and set to ApiRegistry and ApiMaster
				swagger = new SwaggerParser().parse(new String(file.getBytes()));
				if (swagger != null) {
					if (apiType.equals(BffAdminConstantsUtils.ApiRegistryType.EXTERNAL) &&
							StringUtils.isEmpty(swagger.getHost())) {
						return bffResponse.errResponse(
								List.of(BffResponseCode.VALIDATIION_IMPORT_SWAGGER_FILE,
										BffResponseCode.VALIDATIION_EXTERNAL_SWAGGER_FILE_USER_CD),
								StatusCode.BADREQUEST, null, registryName);
					}
					String basePath = StringUtils.defaultIfEmpty(swagger.getBasePath(), BffAdminConstantsUtils.EMPTY);

					ApiRegistry apiregistry = createApiRegistryEntity(registryName, apiType, swagger, userRole, roleMaster,
							basePath);
					LOGGER.log(Level.DEBUG, "File is parsed succesfully");
	
					apiregistry = createApiMasters(swagger, apiregistry);
					LOGGER.debug("ApiMaster inserted succesfully");
					// Convert the Entity to DTO
					RegistryDto registryDto = convertToRegistryDto(apiregistry);
	
					// Send success response
					bffCoreResponse = bffResponse.response(registryDto,
							BffResponseCode.API_IMPORT_SERVICE_SUCCESS_CODE_NEW_REGISTRY,
							BffResponseCode.API_IMPORT_SERVICE_USER_CODE_NEW_REGISTRY, StatusCode.OK, null,
							apiregistry.getUid().toString());
				}
				else
				{
					bffCoreResponse = bffResponse.errResponse(
							List.of(BffResponseCode.VALIDATIION_IMPORT_SWAGGER_FILE,
									BffResponseCode.VALIDATIION_IMPORT_SWAGGER_FILE_USER_CD),
							StatusCode.BADREQUEST, null, registryName);
				}
			}
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, registryName);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APIIMPORT_API_NEW_REGISTRY_DBEXCEPTION,
							BffResponseCode.ERR_API_IMPORT_SERVICE_USER_NEW_REGISTRY_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR, null, registryName);
		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, registryName);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APIIMPORT_API_NEW_REGISTRY_EXCEPTION,
							BffResponseCode.ERR_API_IMPORT_SERVICE_USER_NEW_REGISTRY_EXCEPTION),
					StatusCode.BADREQUEST, null,registryName);
		}
		return bffCoreResponse;
	}

	/**Prepare list of API's and set to Registry
	 *
	 * @param swagger
	 * @param apiregistry
	 * @return
	 */
	private ApiRegistry createApiMasters(Swagger swagger, ApiRegistry apiregistry) {
		List<ApiMaster> apiMasterList = getApiList(swagger, apiregistry);
		// Save each API into ApiMaster
		for (ApiMaster apiMaster : apiMasterList) {
			apiMaster.setApiRegistry(apiregistry);
		}
		apiregistry.setApiMasters(apiMasterList);

		// Save ApiRegistry and ApiMaster
		apiregistry = apiRegistryRepository.save(apiregistry);
		return apiregistry;
	}

	/**Prepare attributes for inserting into API_REGISTRY table
	 *
	 * @param registryName
	 * @param apiType
	 * @param swagger
	 * @param userRole
	 * @param roleMaster
	 * @param basePath
	 * @return
	 */
	private ApiRegistry createApiRegistryEntity(String registryName, ApiRegistryType apiType, Swagger swagger,
			UserRole userRole, RoleMaster roleMaster, String basePath) {
		ApiRegistry apiregistry = new ApiRegistry();
		String swaggerHost = swagger.getHost();
		if(swaggerHost.contains(BffAdminConstantsUtils.SINGLE_COLON))
		{
			apiregistry.setContextPath(swaggerHost.substring(0, swaggerHost.indexOf(BffAdminConstantsUtils.SINGLE_COLON)));
			apiregistry.setPort(swaggerHost.substring(swaggerHost.indexOf(BffAdminConstantsUtils.SINGLE_COLON) + 1,
					swaggerHost.length()));
		}
		else
		{
			apiregistry.setContextPath(swaggerHost);
		}
		apiregistry.setBasePath(basePath);
		apiregistry.setName(registryName);
		apiregistry.setApiVersion(swagger.getSwagger());
		apiregistry.setApiType(apiType.getType());

		if (roleMaster != null) {
			apiregistry.setRoleMaster(userRole.getRoleMaster());
		}

		if (CollectionUtils.isNotEmpty(swagger.getSchemes())) {
			apiregistry.setSchemeList(swagger.getSchemes().toString());
		}

		return apiregistry;
	}

	/**
	 * Implementation for modify Registry based on RegistryId
	 * - If override is true - then old registries and API's will be replaced with new sets
	 * - If override is false - then compare and append/replace/update API's where ever required
	 *
	 * @param fileAsBytes
	 * @param override
	 * @param registryId
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse importApiIntoExistingRegistry(byte[] fileAsBytes, boolean override, UUID registryId,
			MultipartFile ruleFile) {
		BffCoreResponse bffCoreResponse = null;
		List<ApiMaster> overriddenMasterList = new ArrayList<>();
		List<ApiMasterDto> apiMasterDtoList = new ArrayList<>();
		try {
			ApiRegistry apiregistry = apiRegistryRepository.findById(registryId).orElseThrow();
			LOGGER.log(Level.DEBUG, "Fetching the registry for id : {}", apiregistry.getUid());
			// Parse the given file
			Swagger swagger = new SwaggerParser().parse(new String(fileAsBytes));
			List<String> existingApisNames = apiregistry.getApiMasters().stream().map(ApiMaster::getName)
					.collect(Collectors.toList());
			List<String> existingApisRequestMethod = apiregistry.getApiMasters().stream()
					.map(ApiMaster::getRequestMethod).collect(Collectors.toList());
			List<String> existingApisEndpoints = apiregistry.getApiMasters().stream().map(ApiMaster::getRequestEndpoint)
					.collect(Collectors.toList());
			List<ApiMaster> apiMasterList = getApiList(swagger, apiregistry);
			List<ApiMaster> overlappedMasterList = new ArrayList<>();

			List<ApiMaster> apiMasterUpdated = new ArrayList<>();
			// Check for override flag, if its false then check conflicted API and return
			if (!override) {
				LOGGER.log(Level.DEBUG, "modifyRegistry :: Override flag - false");
				for (ApiMaster apiMaster : apiMasterList) {
					if (existingApisNames.contains(apiMaster.getName())
							&& existingApisRequestMethod.contains(apiMaster.getRequestMethod())
							&& existingApisEndpoints.contains(apiMaster.getRequestEndpoint())) {
						apiMaster.setApiRegistry(apiregistry);
						overlappedMasterList.add(apiMaster);
					} else {
						apiMasterUpdated.add(apiMaster);
					}
				}
				LOGGER.log(Level.DEBUG, "No of Conflicted Apis : {}", overlappedMasterList.size());

				updateRegistry(apiregistry,swagger);

				if (!apiMasterUpdated.isEmpty()) {
					for(ApiMaster master : apiMasterUpdated) {
						apiregistry.addApiMaster(master);
					}
					LOGGER.log(Level.DEBUG, "ApiMaster is updated successfully for Id : {}", registryId);
				}
				apiRegistryRepository.save(apiregistry);
			}
			// If override flag is true, then all existing API are cleared and saved with
			// updated data		
			else {
				LOGGER.log(Level.DEBUG, "modifyRegistry :: Override flag - true");
				updateRegistry(apiregistry,swagger);
				if (ApiRegistryType.ORCHESTRATION.getType().equals(apiregistry.getApiType())) {
					String orchName = FilenameUtils.getBaseName(ruleFile.getOriginalFilename());
					Optional<ApiMaster> apiMaster = apiMasterRepository.findByApiRegistryAndOrchestrationName(apiregistry, orchName);					
					if (apiMaster.isPresent()) {
						apiMasterList.get(0).setUid(apiMaster.get().getUid());
					}
					apiMasterList.get(0).setOrchestrationName(orchName);
					apiMasterList.get(0).setRuleContent(ruleFile.getBytes());
					apiMasterRepository.save(apiMasterList.get(0));
				}
				else {
					apiregistry.getApiMasters().clear();
					for (ApiMaster master : apiMasterList) {
						apiregistry.addApiMaster(master);
					}
				}
				LOGGER.log(Level.DEBUG, "ApiMaster is updated successfully for Id : {}", registryId);
				apiRegistryRepository.save(apiregistry);
			}

			// Convert Entity to DTO
			for (ApiMaster apimaster : apiMasterList) {
				apiMasterDtoList.add(convertToMasterDto(apimaster));
			}

			// Prepare success response
			bffCoreResponse = bffResponse.response(
					!apiMasterDtoList.isEmpty() ? apiMasterDtoList
							: overriddenMasterList.stream().map(ApiMaster::getUid).collect(Collectors.toList()),
					BffResponseCode.API_IMPORT_SERVICE_SUCCESS_CODE_MODIFY_REGISTRY,
					BffResponseCode.API_IMPORT_SERVICE_USER_CODE_MODIFY_REGISTRY, StatusCode.OK, null,
					registryId.toString());
		} catch (DataAccessException exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, registryId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APIIMPORT_API_MODIFY_REGISTRY_DBEXCEPTION,
							BffResponseCode.ERR_API_IMPORT_SERVICE_USER_MODIFY_REGISTRY_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, registryId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APIIMPORT_API_MODIFY_REGISTRY_EXCEPTION,
							BffResponseCode.ERR_API_IMPORT_SERVICE_USER_MODIFY_REGISTRY_EXCEPTION),
					StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**Prepare attributes to be updated in API_REGISTRY table
	 * @param apiregistry
	 * @param swagger
	 */
	private void updateRegistry(ApiRegistry apiregistry, Swagger swagger) {
		String swaggerHost = swagger.getHost();
		if (swaggerHost.contains(BffAdminConstantsUtils.SINGLE_COLON)) {
			apiregistry.setContextPath(swaggerHost.substring(0, swaggerHost.indexOf(BffAdminConstantsUtils.SINGLE_COLON)));
			apiregistry.setPort(swaggerHost.substring(swaggerHost.indexOf(BffAdminConstantsUtils.SINGLE_COLON) + 1,
					swaggerHost.length()));
		}
		else {
			apiregistry.setContextPath(swaggerHost);
		}

		String basePath =  (null!= swagger.getBasePath()) ? swagger.getBasePath() : BffAdminConstantsUtils.EMPTY;
		apiregistry.setBasePath(basePath);

		if (CollectionUtils.isNotEmpty(swagger.getSchemes())) {
			apiregistry.setSchemeList(swagger.getSchemes().toString());
		}
		apiregistry.setApiVersion(swagger.getSwagger());
	}

	/**
	 * Performs force update to ApiRegistry and ApiMaster tables for given registry
	 * id
	 * 
	 * @param apisToOverride
	 * @param registryId
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse overrideExistingApis(List<ApiMasterRequest> apisToOverride, UUID registryId) {
		BffCoreResponse bffCoreResponse = null;
		try {
			ApiRegistry apiregistry = apiRegistryRepository.findById(registryId).orElseThrow();
			LOGGER.log(Level.DEBUG, "ApiRegistry with the registry id : {}", apiregistry.getUid());
			List<ApiMasterDto> updtApiMasterDto = new ArrayList<>();
			List<ApiMaster> updtApiMasters = new ArrayList<>();

			for (ApiMasterRequest apiMasterRequest : apisToOverride) {
				ApiMaster apimasterEntity = convertToApiMasterRequesToApiMaster(apiMasterRequest);
				apimasterEntity.setApiRegistry(apiregistry);
				Optional<ApiMaster> apiMasterSearchResult = apiregistry.getApiMasters().stream()
						.filter(apiMaster -> apiMaster.getName().equals(apiMasterRequest.getName())
								&& apiMaster.getRequestMethod().equals(apiMasterRequest.getRequestMethod()))
						.findFirst();
				if (apiMasterSearchResult.isPresent()) {
					apimasterEntity.setUid(apiMasterSearchResult.get().getUid());
				}
				updtApiMasters.add(apimasterEntity);
			}

			// Perform update to ApiMaster
			updtApiMasters = (List<ApiMaster>) apiMasterRepository.saveAll(updtApiMasters);
			LOGGER.log(Level.DEBUG, "ApiRegistry updated with the registry id : {}", apiregistry.getUid());
			// Convert Entity to DTO
			for (ApiMaster apiMaster : updtApiMasters) {
				updtApiMasterDto.add(convertToMasterDto(apiMaster));
			}

			// Prepare Success response
			bffCoreResponse = bffResponse.response(updtApiMasterDto,
					BffResponseCode.API_IMPORT_SERVICE_SUCCESS_CODE_OVERRIDE_APIS,
					BffResponseCode.API_IMPORT_SERVICE_USER_CODE_OVERRIDE_APIS, StatusCode.OK);

		} catch (DataAccessException exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, registryId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APIIMPORT_API_OVERRIDE_APIS_DBEXCEPTION,
							BffResponseCode.ERR_API_IMPORT_SERVICE_USER_OVERRIDE_APIS_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, registryId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APIIMPORT_API_OVERRIDE_APIS_EXCEPTION,
							BffResponseCode.ERR_API_IMPORT_SERVICE_USER_OVERRIDE_APIS_DBEXCEPTION),
					StatusCode.BADREQUEST);
		}
		return bffCoreResponse;

	}

	/**
	 * Retrieves the API List
	 * 
	 * @param allApis
	 * @param swagger
	 * @param apiregistry
	 * @return
	 */
	private List<ApiMaster> getApiList(Swagger swagger, ApiRegistry apiregistry) {
		List<ApiMaster> apiMasterList = new ArrayList<>();
		Map<String, Path> paths = swagger.getPaths();
		LOGGER.debug("Total No of paths in give file" + swagger.getPaths().size());
		Map<String, Model> vanillaMap = new LinkedHashMap<>();
		Map<String, Model> definitions = swagger.getDefinitions();
		if (MapUtils.isNotEmpty(definitions)) {
			Map<String, Model> refMap = new LinkedHashMap<>();
			for (Entry<String, Model> definition : definitions.entrySet()) {
				String json = Json.pretty(definition.getValue());
				if (json.contains(BffAdminConstantsUtils.REFERENCE)) {
					refMap.put(definition.getKey(), definition.getValue());
				} else {
					vanillaMap.put(definition.getKey(), definition.getValue());
				}
			}
			LOGGER.log(Level.DEBUG, "vanilla map size: {}", vanillaMap.size());
			LOGGER.log(Level.DEBUG, "ref map size: {}", refMap.size());
			LOGGER.log(Level.DEBUG, "definitionSize : {}", definitions.size());
	
			while (!refMap.isEmpty()) {
				LOGGER.log(Level.DEBUG, "Invoking into parseReferenceDefSchema method");
				List<String> oldKeyList = parseReferenceDefSchema(vanillaMap, refMap);
				for (String id : oldKeyList) {
					refMap.remove(id);
				}
	
			}
		}
		createApiListForImport(swagger, apiregistry, apiMasterList, paths, vanillaMap);
		return apiMasterList;

	}

	/**Extract and prepare the API's with operation details , Path ,scheme etc
	 *
	 * @param swagger
	 * @param apiregistry
	 * @param apiMasterList
	 * @param paths
	 * @param vanilla
	 */
	private void createApiListForImport(Swagger swagger, ApiRegistry apiregistry, List<ApiMaster> apiMasterList,
			Map<String, Path> paths, Map<String, Model> vanilla) {

		for (Map.Entry<String, Path> pathkey : paths.entrySet()) {

			Path path = pathkey.getValue();
			Map<HttpMethod, Operation> operations = path.getOperationMap();
			for (Entry<HttpMethod, Operation> operation : operations.entrySet()) {
				ApiMaster apiMaster = new ApiMaster();
				LOGGER.log(Level.DEBUG, "PATH: {}", pathkey.getKey());
				apiMaster.setVersion(swagger.getInfo().getVersion() != null ? swagger.getInfo().getVersion()
						: BffAdminConstantsUtils.VERSION_SWAGGER);
				apiMaster.setRequestEndpoint(pathkey.getKey());
				if (operation != null && operation.getKey() != null) {
					LOGGER.log(Level.DEBUG, "Http method: {}", operation.getKey());
					apiMaster.setRequestMethod((operation.getKey().toString()));
				}
				if (operation != null && operation.getValue() != null) {
					LOGGER.log(Level.DEBUG, "OperationId: {}", operation.getValue().getOperationId());
					if(!StringUtils.isEmpty(operation.getValue().getOperationId()))
					{
						apiMaster.setName(operation.getValue().getOperationId());
					}
					else 
					{
						apiMaster.setName(pathkey.getKey());
					}
					setApiRequestParams(vanilla, operation, apiMaster);
				}
				apiMaster.setApiRegistry(apiregistry);

				apiMasterList.add(apiMaster);
			}
		}
	}

	/**Extract and prepare the request parameters
	 *
	 * @param vanilla
	 * @param operation
	 * @param data
	 */
	private void setApiRequestParams(Map<String, Model> vanilla, Entry<HttpMethod, Operation> operation,
			ApiMaster data) {
		List<String> pathParams = new ArrayList<>();
		List<String> reqParams = new ArrayList<>();

		for (Parameter parameter : operation.getValue().getParameters()) {
			LOGGER.log(Level.DEBUG, "Parameter name: {} and Type : {}", parameter.getName(), parameter.getIn());
			if (parameter instanceof PathParameter) {
				pathParams.add(Json.pretty(parameter));
			}
			if (parameter instanceof QueryParameter) {
				reqParams.add(Json.pretty(parameter));
			}
			if (parameter instanceof BodyParameter) {
				LOGGER.log(Level.DEBUG, "Entering into getRequestBody method");
				String json = getRequestBody(parameter, vanilla);
				data.setRequestBody(json);
			}
		}
		data.setRequestPathparams(pathParams.toString());
		LOGGER.log(Level.DEBUG, "Path params JSON {}", pathParams.toString());

		if (!reqParams.isEmpty()) {
			data.setRequestQuery(reqParams.toString());
		}
		Map<String, Response> responses = operation.getValue().getResponses();
		String responseBody = getResponseBody(responses, vanilla);

		data.setResponseSchema(responseBody);
	}

	/**Get Request body object as string
	 *
	 * @param parameter
	 * @param vanillaMap
	 * @return requestBody as String
	 */
	private String getRequestBody(Parameter parameter, Map<String, Model> vanillaMap) {
		String json = null;
		BodyParameter bodyParameter = (BodyParameter) parameter;

		Model model = bodyParameter.getSchema();
		String modelAsString = Json.pretty(model);
		if (!modelAsString.contains(BffAdminConstantsUtils.REFERENCE)) {
			return modelAsString;
		}

		if (model != null && model.getReference() != null) {
			LOGGER.log(Level.DEBUG, "Reference value : {}", model.getReference());
			return Json.pretty(vanillaMap.get(model.getReference().replace(BffAdminConstantsUtils.DEFINITIONS,
					BffAdminConstantsUtils.EMPTY_SPACES)));
		}
		return json;
	}

	/**Parse the reference attribute fromm swagger file
	 *
	 * @param vanillaMap
	 * @param refMap
	 * @return list of keys to delete from refMap
	 */
	private List<String> parseReferenceDefSchema(Map<String, Model> vanillaMap, Map<String, Model> refMap) {
		List<String> oldKeyList = new ArrayList<>();
		for (Entry<String, Model> b : refMap.entrySet()) {
			Map<String, Property> prop = b.getValue().getProperties();
			boolean noSaveCheck = false;
			if (prop != null) {
				prop.remove(BffAdminConstantsUtils.PARENT);
				for (Entry<String, Property> property : prop.entrySet()) {
					if (property.getValue() instanceof RefProperty) {
						noSaveCheck = processRefProperty(vanillaMap, prop, noSaveCheck, property);
					} else if (property.getValue() instanceof ArrayProperty) {
						noSaveCheck = processArrayProperty(vanillaMap, noSaveCheck, property);
					}
				}
			}
			if (!noSaveCheck) {
				vanillaMap.put(b.getKey(), b.getValue());
				oldKeyList.add(b.getKey());
			}
		}
		return oldKeyList;
	}

	/**Process the Array property of referenceObject from Swagger
	 *
	 * @param vanillaMap
	 * @param noSaveCheck
	 * @param property
	 * @return
	 */
	private boolean processArrayProperty(Map<String, Model> vanillaMap, boolean noSaveCheck,
			Entry<String, Property> property) {
		ArrayProperty arrProp = (ArrayProperty) property.getValue();
		if (arrProp.getItems() instanceof RefProperty) {
			Model model = vanillaMap.get(((RefProperty) arrProp.getItems()).getSimpleRef());
			if (model != null) {
				Property propLocal = new ObjectProperty(model.getProperties());
				arrProp.setItems(propLocal);
			} else {
				noSaveCheck = true;
			}
		}
		return noSaveCheck;
	}

	/**Process refProperty in referenceObject from swagger file
	 * @param vanillaMap
	 * @param prop
	 * @param noSaveCheck
	 * @param property
	 * @return
	 */
	private boolean processRefProperty(Map<String, Model> vanillaMap, Map<String, Property> prop, boolean noSaveCheck,
			Entry<String, Property> property) {
		Model model = vanillaMap.get(((RefProperty) property.getValue()).getSimpleRef());
		if (model != null) {
			Property propLocal = new ObjectProperty(model.getProperties());

			prop.put(property.getKey(), propLocal);
		} else {
			noSaveCheck = true;
		}
		return noSaveCheck;
	}

	/**Get the response body as string from swagger file
	 *
	 * @param responses
	 * @param vanilla
	 * @return Response Body as String
	 */
	private String getResponseBody(Map<String, Response> responses, Map<String, Model> vanillaMap) {
		if (!Json.pretty(responses).contains(BffAdminConstantsUtils.REFERENCE)) {
			return Json.pretty(responses);
		}
		for (Map.Entry<String, Response> res : responses.entrySet()) {
			if (res.getValue().getResponseSchema() == null) {
				continue;
			}

			if (res.getValue().getResponseSchema() instanceof ArrayModel) {
				ArrayModel arrProp = (ArrayModel) res.getValue().getResponseSchema();
				if (arrProp.getItems() instanceof RefProperty) {
					String reference = ((RefProperty) arrProp.getItems()).getSimpleRef();
					Model model = vanillaMap.get(reference);
					if (model != null) {
						Property propLocal = new ObjectProperty(model.getProperties());
						arrProp.setItems(propLocal);
					}
				}
			}
			if (res.getValue().getResponseSchema() instanceof RefModel) {
				String reference = res.getValue().getResponseSchema().getReference()
						.replace(BffAdminConstantsUtils.DEFINITIONS, BffAdminConstantsUtils.EMPTY_SPACES);
				res.getValue().setResponseSchema(vanillaMap.get(reference));
			}

		}
		return Json.pretty(responses);
	}

	/**
	 * Check whether given registry already exists in DB
	 * - If present return error message
	 *
	 * @param registryName
	 * @param apiType
	 * @param roleMaster
	 * @return BffCoreResponse
	 */
	private BffCoreResponse uniqueRegistry(String registryName, String apiType, RoleMaster roleMaster) {
		BffCoreResponse bffCoreResponse = null;
		try {
			int count = apiRegistryRepository.countByNameAndApiTypeAndRoleMaster(registryName, apiType, roleMaster);
			if (count == 0) {
				LOGGER.log(Level.DEBUG, "Registry name is unique: {}", registryName);
				bffCoreResponse = bffResponse.response(registryName,
						BffResponseCode.API_IMPORT_SERVICE_SUCCESS_CODE_UNIQUE_REGISTRY,
						BffResponseCode.API_IMPORT_SERVICE_USER_CODE_UNIQUE_REGISTRY, StatusCode.OK,
						registryName, registryName);
			} else {
				LOGGER.log(Level.DEBUG, "Registry name is not unique: {}", registryName);
				bffCoreResponse = bffResponse.errResponse(
						List.of(BffResponseCode.API_IMPORT_SERVICE_SUCCESS_CODE_NOT_UNIQUE_REGISTRY,
								BffResponseCode.API_IMPORT_SERVICE_USER_CODE_NOT_UNIQUE_REGISTRY),
						StatusCode.BADREQUEST, null, registryName);
			}
		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, registryName);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APIIMPORT_API_UNIQUE_REGISTRY_EXCEPTION,
							BffResponseCode.ERR_API_IMPORT_SERVICE_USER_UNIQUE_REGISTRY_EXCEPTION),
					StatusCode.INTERNALSERVERERROR, null, registryName);
		}
		return bffCoreResponse;
	}

	/**
	 * Convert ApiRegistry to RegistryDto
	 * 
	 * @param registry
	 * @return registryDto
	 */
	private RegistryDto convertToRegistryDto(ApiRegistry registry) {
		return new RegistryDto.RegistryBuilder().setRegId(registry.getUid().toString()).setName(registry.getName())
				.setApiVersion(registry.getApiVersion()).setHelperClass(registry.getHelperClass())
				.setBasePath(registry.getBasePath()).setPort(registry.getPort())
				.setContextPath(registry.getContextPath()).setApiType(registry.getApiType())
				.setVersionId(registry.getVersionId()).setLayer(registry.getRoleMaster().getLevel())
				.setLayerName(registry.getRoleMaster().getName()).build();
	}

	/**
	 * Convert ApiMaster to ApiMasterDto
	 * 
	 * @param apiMaster
	 * @return ApiMasterDto
	 */
	private ApiMasterDto convertToMasterDto(ApiMaster apiMaster) {
		return ApiMasterDto.builder().name(apiMaster.getName()).uid(apiMaster.getUid())
				.regName(BffUtils.getNullable(apiMaster.getApiRegistry(), ApiRegistry::getName))
				.requestBody(apiMaster.getRequestBody()).requestEndpoint(apiMaster.getRequestEndpoint())
				.requestMethod(apiMaster.getRequestMethod()).requestPathparams(apiMaster.getRequestPathparams())
				.requestPreproc(apiMaster.getRequestPreproc()).requestQuery(apiMaster.getRequestQuery())
				.responsePostproc(apiMaster.getResponsePostproc()).responseSchema(apiMaster.getResponseSchema())
				.version(apiMaster.getVersion()).build();
	}

	/**
	 * Convert ApiMasterRequest to ApiMaster
	 * 
	 * @param apiMasterRequest
	 * @return
	 */
	private ApiMaster convertToApiMasterRequesToApiMaster(ApiMasterRequest apiMasterRequest) {
		ApiMaster apiMaster = new ApiMaster();
		apiMaster.setName(apiMasterRequest.getName());
		apiMaster.setRequestBody(apiMasterRequest.getRequestBody());
		apiMaster.setRequestEndpoint(apiMasterRequest.getRequestEndpoint());
		apiMaster.setRequestMethod(apiMasterRequest.getRequestMethod());
		apiMaster.setRequestPathparams(apiMasterRequest.getRequestPathparams());
		apiMaster.setRequestPreproc(apiMasterRequest.getRequestPreproc());
		apiMaster.setRequestQuery(apiMasterRequest.getRequestQuery());
		apiMaster.setResponsePostproc(apiMasterRequest.getResponsePostproc());
		apiMaster.setResponseSchema(apiMasterRequest.getResponseSchema());
		apiMaster.setVersion(apiMasterRequest.getVersion());
		return apiMaster;
	}

}
