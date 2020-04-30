package com.jda.mobility.framework.extensions.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.dto.ApiMasterDto;
import com.jda.mobility.framework.extensions.dto.RegistryDto;
import com.jda.mobility.framework.extensions.entity.ApiMaster;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.entity.UserRole;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.repository.ApiMasterRepository;
import com.jda.mobility.framework.extensions.repository.ApiRegistryRepository;
import com.jda.mobility.framework.extensions.repository.UserRoleRepository;
import com.jda.mobility.framework.extensions.service.RegistryService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ApiRegistryType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.LayerMode;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;

/**
 * The class fetches imported registry and API's
 * 
 * @author HCL Technologies
 *
 */
@Service
public class RegistryServiceImpl implements RegistryService {
	private static final Logger LOGGER = LogManager.getLogger(RegistryServiceImpl.class);
	@Autowired
	private ApiRegistryRepository apiRegistryRepository;

	@Autowired
	private ApiMasterRepository apiMasterRepository;

	@Autowired
	private BffResponse bffResponse;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private SessionDetails sessionDetails;

	/**
	 * Fetch all Registry from ApiRegistry.
	 *
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchAllRegistries(LayerMode mode) {
		BffCoreResponse bffCoreResponse = null;
		try {
			List<ApiRegistry> allRegistries = apiRegistryRepository.findAllByOrderByName();
			List<RegistryDto> registryDtoList = convertToRegistryDtoList(mode, allRegistries);
			LOGGER.log(Level.DEBUG, "Total Number of registries returned : {}", registryDtoList.size());
			bffCoreResponse = bffResponse.response(registryDtoList,
					BffResponseCode.REGISTRY_SERVICE_SUCCESS_CODE_FETCH_ALL,
					BffResponseCode.REGISTRY_SERVICE_USER_CODE_FETCH_ALL, StatusCode.OK);
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_REGISTRY_SERVICE_API_FETCH_ALL_DBEXCEPTION,
							BffResponseCode.ERR_REGISTRY_SERVICE_USER_FETCH_ALL_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse
					.errResponse(
							List.of(BffResponseCode.ERR_REGISTRY_SERVICE_API_FETCH_ALL_EXCEPTION,
									BffResponseCode.ERR_REGISTRY_SERVICE_USER_FETCH_ALL_EXCEPTION),
							StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**
	 * Fetch All Registry by type from ApiRegistry.
	 *
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchRegistries(List<ApiRegistryType> type, LayerMode mode) {
		BffCoreResponse bffCoreResponse = null;
		try {

			List<String> typeList = new ArrayList<>();
			type.forEach(typ -> typeList.add(typ.getType()));

			List<ApiRegistry> allRegistries = apiRegistryRepository.findByApiTypeInOrderByName(typeList)
					.orElse(new ArrayList<>());
			List<RegistryDto> registryDtoList = convertToRegistryDtoList(mode, allRegistries);
			LOGGER.log(Level.DEBUG, "Total Number of registries returned : {}", registryDtoList.size());
			bffCoreResponse = bffResponse.response(registryDtoList,
					BffResponseCode.REGISTRY_SERVICE_SUCCESS_CODE_FETCH_ALL,
					BffResponseCode.REGISTRY_SERVICE_USER_CODE_FETCH_ALL, StatusCode.OK);
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_REGISTRY_SERVICE_API_FETCH_ALL_DBEXCEPTION,
							BffResponseCode.ERR_REGISTRY_SERVICE_USER_FETCH_ALL_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse
					.errResponse(
							List.of(BffResponseCode.ERR_REGISTRY_SERVICE_API_FETCH_ALL_EXCEPTION,
									BffResponseCode.ERR_REGISTRY_SERVICE_USER_FETCH_ALL_EXCEPTION),
							StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**
	 * Fetch the Registry based on RegistryId .
	 *
	 * @param registryId
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchRegistryById(UUID registryId) {
		BffCoreResponse bffCoreResponse = null;
		try {
			Optional<ApiRegistry> optionalRegistry = apiRegistryRepository.findById(registryId);
			bffCoreResponse = bffResponse.response(convertToRegistryDto(optionalRegistry.orElseThrow(), LayerMode.ALL),
					BffResponseCode.REGISTRY_SERVICE_SUCCESS_CODE_FETCH_ID,
					BffResponseCode.REGISTRY_SERVICE_USER_CODE_FETCH_ID, StatusCode.OK, null,
					registryId.toString());
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, registryId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_REGISTRY_SERVICE_API_FETCH_ID_DBEXCEPTION,
							BffResponseCode.ERR_REGISTRY_SERVICE_USER_FETCH_ID_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR, null, registryId.toString());
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, registryId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_REGISTRY_SERVICE_API_FETCH_ID_EXCEPTION,
							BffResponseCode.ERR_REGISTRY_SERVICE_USER_FETCH_ID_EXCEPTION),
					StatusCode.BADREQUEST, null, registryId.toString());
		}
		return bffCoreResponse;
	}

	/**
	 * Converting registry to registryDto
	 * 
	 * @param registry
	 * @return registryDto
	 */
	private RegistryDto convertToRegistryDto(ApiRegistry registry, LayerMode mode) {
		UserRole currentUserRole = userRoleRepository.findByUserId(sessionDetails.getPrincipalName()).orElseThrow();
		RegistryDto registryDto = null;

		if (registry.getRoleMaster() != null) {
			// Get only current layer
			if (mode != null && mode.equals(LayerMode.CURRENT_LAYER)) {
				if (registry.getRoleMaster().getLevel() == currentUserRole.getRoleMaster().getLevel()) {
					registryDto = setRegistryDto(registry);
					LOGGER.log(Level.DEBUG, "Registry in current layer for mode {}: {}", LayerMode.CURRENT_LAYER,
							registryDto);
				}
			}
			// Get Current and above layer
			else {
				if (!CollectionUtils.isEmpty(registry.getApiMasters())
						&& registry.getRoleMaster().getLevel() <= currentUserRole.getRoleMaster().getLevel()) {
					// Get only current layer and above layer
					registryDto = setRegistryDto(registry);

					LOGGER.log(Level.DEBUG, "Registry in current layer or below: {}", registryDto);
				}
			}
		}

		return registryDto;
	}

	private RegistryDto setRegistryDto(ApiRegistry registry) {
		return new RegistryDto.RegistryBuilder().setRegId(registry.getUid().toString()).setName(registry.getName())
				.setApiVersion(registry.getApiVersion()).setHelperClass(registry.getHelperClass())
				.setBasePath(registry.getBasePath()).setPort(registry.getPort())
				.setContextPath(registry.getContextPath()).setApiType(registry.getApiType())
				.setVersionId(registry.getVersionId()).setLayer(registry.getRoleMaster().getLevel())
				.setLayerName(registry.getRoleMaster().getName()).build();
	}

	/**
	 * Fetch All API's in all registry
	 * 
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchAllApis() {
		BffCoreResponse bffCoreResponse = null;
		try {
			List<ApiMaster> allApis = apiMasterRepository.findAll();
			List<ApiMasterDto> registryDtoList = new ArrayList<>();
			for (ApiMaster api : allApis) {
				registryDtoList.add(convertToMasterDto(api));
			}
			LOGGER.log(Level.DEBUG, "Total number of APIs returned : {}", registryDtoList.size());
			bffCoreResponse = bffResponse.response(registryDtoList,
					BffResponseCode.APIMASTER_SERVICE_SUCCESS_CODE_FETCH_ALL_APIS,
					BffResponseCode.APIMASTER_SERVICE_USER_CODE_FETCH_ALL_APIS, StatusCode.OK);
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APIMASTER_SERVICE_API_FETCH_ALL_APIS_DBEXCEPTION,
							BffResponseCode.ERR_APIMASTER_SERVICE_USER_FETCH_ALL_APIS_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APIMASTER_SERVICE_API_FETCH_ALL_APIS_EXCEPTION,
							BffResponseCode.ERR_APIMASTER_SERVICE_USER_FETCH_ALL_APIS_EXCEPTION),
					StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**
	 * Fetch all API's based on registryId .
	 * 
	 * @param registryId
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchApiByRegistryId(UUID registryId) {
		BffCoreResponse bffCoreResponse = null;
		List<ApiMasterDto> apiMasterDtoList = new ArrayList<>();
		try {
			Optional<ApiRegistry> apiMasterList = apiRegistryRepository.findById(registryId);

			if (apiMasterList.isPresent()) {
				for (ApiMaster apiMaster : apiMasterList.get().getApiMasters()) {
					apiMasterDtoList.add(convertToMasterDto(apiMaster));
				}
				bffCoreResponse = bffResponse.response(apiMasterDtoList,
						BffResponseCode.APIMASTER_SERVICE_SUCCESS_CODE_FETCH_REGISTRYID,
						BffResponseCode.APIMASTER_SERVICE_USER_CODE_FETCH_REGISTRYID, StatusCode.OK, null,
						registryId.toString());
			}
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, registryId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APIMASTER_SERVICE_API_FETCH_REGISTRYID_DBEXCEPTION,
							BffResponseCode.ERR_APIMASTER_SERVICE_USER_FETCH_REGISTRYID_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR, null, registryId.toString());
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, registryId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APIMASTER_SERVICE_API_FETCH_REGISTRYID_EXCEPTION,
							BffResponseCode.ERR_APIMASTER_SERVICE_USER_FETCH_REGISTRYID_EXCEPTION),
					StatusCode.BADREQUEST, null, registryId.toString());
		}
		return bffCoreResponse;
	}

	/**
	 * Fetch API based on  api id
	 *
	 * @param registryId
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchApiById(UUID registryId) {
		BffCoreResponse bffCoreResponse = null;
		try {
			Optional<ApiMaster> optionalRegistry = apiMasterRepository.findById(registryId);

			bffCoreResponse = bffResponse.response(convertToMasterDto(optionalRegistry.orElseThrow()),
					BffResponseCode.APIMASTER_SERVICE_SUCCESS_CODE_FETCHBYID,
					BffResponseCode.APIMASTER_SERVICE_USER_CODE_FETCHBYID, StatusCode.OK, null,
					registryId.toString());
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, registryId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APIMASTER_SERVICE_API_FETCHBYID_DBEXCEPTION,
							BffResponseCode.ERR_APIMASTER_SERVICE_USER_FETCHBYID_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR, null, registryId.toString());
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, registryId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APIMASTER_SERVICE_API_FETCHBYID_EXCEPTION,
							BffResponseCode.ERR_APIMASTER_SERVICE_USER_FETCHBYID_EXCEPTION),
					StatusCode.BADREQUEST, null, registryId.toString());
		}
		return bffCoreResponse;
	}

	private List<RegistryDto> convertToRegistryDtoList(LayerMode mode, List<ApiRegistry> allRegistries) {
		List<RegistryDto> registryDtoList = new ArrayList<>();

		for (ApiRegistry registry : allRegistries) {
			RegistryDto registryDto = convertToRegistryDto(registry, mode);
			if (registryDto != null) {
				registryDtoList.add(registryDto);
			}
		}
		return registryDtoList;
	}

	/**
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
}