package com.jda.mobility.framework.extensions.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.dto.AppConfigDto;
import com.jda.mobility.framework.extensions.entity.AppConfigDetail;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.entity.projection.AppConfigDetailDto;
import com.jda.mobility.framework.extensions.model.AppConfigRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.repository.AppConfigDetailRepository;
import com.jda.mobility.framework.extensions.repository.AppConfigMasterRepository;
import com.jda.mobility.framework.extensions.service.AppConfigService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.AppCfgRequestType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ChannelType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 * The service is intended to create/update/get/clear application configuration
 * information like GLOBAL, CONTEXT, APPLICATION and INTERNAL variables
 * 
 * @author HCL Technologies
 */
@Service
public class AppConfigServiceImpl implements AppConfigService {

	private static final Logger LOGGER = LogManager.getLogger(AppConfigServiceImpl.class);

	@Autowired
	private AppConfigMasterRepository appConfigRepository;

	@Autowired
	private AppConfigDetailRepository appConfigDetailRepository;

	@Autowired
	private BffResponse bffResponse;

	@Autowired
	public SessionDetails sessionDetails;

	/**
	 * Create Application configuration variable of type CONTEXT, GLOBAL.
	 *  - used by ADMIN UI to create config names
	 * 
	 * @param appConfigRequestList
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse createAppConfigDefinition(List<AppConfigRequest> appConfigRequestList) {

		BffCoreResponse bffCoreResponse = null;
		String configType = null;
		List<AppConfigMaster> appConfigMasterList = new ArrayList<>();
		try {
			for (AppConfigRequest appConfigRequest : appConfigRequestList) {
				
				configType = appConfigRequest.getConfigType();
				String configName = appConfigRequest.getConfigName();
				AppConfigMaster appConfigCheck = null;
				if(configName != null) {
					configName = configName.trim();
					appConfigCheck = appConfigRepository.findByConfigNameAndConfigType(configName,
							configType);
				}
				
				if (appConfigCheck != null) {
					return bffResponse.errResponse(
							List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_CREATION_CHECK,
									BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_CREATION_CHECK),
							StatusCode.INTERNALSERVERERROR,null,configType);
				} else {
					
					appConfigMasterList.add(convertAppConfigReqToAppConfigMaster(appConfigRequest));
				}

			}
			
			appConfigRepository.saveAll(appConfigMasterList);
			List<AppConfigDto> appConfigDtoList = convertToAppConfigDto(appConfigMasterList);
			
			if (AppCfgRequestType.valueOf(configType).equals(AppCfgRequestType.CONTEXT)) {
				bffCoreResponse = bffResponse.response(appConfigDtoList,
						BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_CONTEXT_VARIABLES,
						BffResponseCode.APP_CONFIG_SERVICE_USER_CONTEXT_VARIABLES, StatusCode.CREATED);
			} else if (AppCfgRequestType.valueOf(configType).equals(AppCfgRequestType.GLOBAL)) {
				
				bffCoreResponse = bffResponse.response(appConfigDtoList,
						BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_GLOBAL_VARIABLES,
						BffResponseCode.APP_CONFIG_SERVICE_USER_GLOBAL_VARIABLES, StatusCode.CREATED);
			} else if (AppCfgRequestType.valueOf(configType).equals(AppCfgRequestType.APPLICATION)) {
				bffCoreResponse = bffResponse.response(appConfigDtoList,
						BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_APPLICATION_VARIABLES,
						BffResponseCode.APP_CONFIG_SERVICE_USER_APPLICATION_VARIABLES, StatusCode.CREATED);
			} else {
				bffCoreResponse = bffResponse.response(appConfigDtoList,
						BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_INTERNAL_VARIABLES,
						BffResponseCode.APP_CONFIG_SERVICE_USER_INTERNAL_VARIABLES, StatusCode.CREATED);
			}
			
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_CREATION_DBEXCEPTION,
							BffResponseCode.ERR_APP_CONFIG_SERVICE_CREATION_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse
					.errResponse(
							List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_CREATION_EXCEPTION,
									BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_CREATION_EXCEPTION),
							StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}



	/**Converts AppConfigMaster to AppConfigDto
	 * @param appConfigMasterList
	 * @return
	 */
	private List<AppConfigDto> convertToAppConfigDto(List<AppConfigMaster> appConfigMasterList) {
		List<AppConfigDto> appConfigDtoList = new ArrayList<>();
		for (AppConfigMaster appConfigMaster : appConfigMasterList) {
			AppConfigDto appConfigDTO = convertToAppConfigDto(appConfigMaster);
			appConfigDtoList.add(appConfigDTO);

		}
		return appConfigDtoList;
	}

	
	/**Covert AppConfigDetail to AppConfigDto
	 * @param appConfigDetailList
	 * @return
	 */
	private List<AppConfigDto> buildAppConfigDto(List<AppConfigDetail> appConfigDetailList) {
		List<AppConfigDto> appConfigDtoList = new ArrayList<>();
		for (AppConfigDetail appConfigDetail : appConfigDetailList) {
			AppConfigDto appConfigDto = AppConfigDto.builder()
					.appConfigId(appConfigDetail.getAppConfigMaster().getUid())
					.configName(appConfigDetail.getAppConfigMaster().getConfigName())
					.configType(appConfigDetail.getAppConfigMaster().getConfigType())
					.rawValue(appConfigDetail.getAppConfigMaster().getRawValue())
					.configValue(appConfigDetail.getConfigValue()).flowId(appConfigDetail.getFlowId())
					.userId(appConfigDetail.getUserId()).description(appConfigDetail.getDescription()).deviceName(appConfigDetail.getDeviceName()).build();

			appConfigDtoList.add(appConfigDto);

		}
		return appConfigDtoList;
	}
	

	/**
	 * Implementation for - (ADMIN UI)
	 * 	-  Create  App Config if not already present (APPLICATION)
	 *  -  Update App Config value - config or raw value if already present
	 * 
	 * @param appConfigRequestList
	 * @return BffCoreResponse
	 */

	@Override
	public BffCoreResponse updateAppConfigDefinition(List<AppConfigRequest> appConfigRequestList) {
		BffCoreResponse bffCoreResponse = null;
		
		try {
			for (AppConfigRequest appConfigRequest : appConfigRequestList) {
				
				String configType = appConfigRequest.getConfigType();
				String configName = appConfigRequest.getConfigName();
				
				//Calls for update
				if(appConfigRequest.getAppConfigId()!=null && !appConfigRequest.getAppConfigId().toString().isEmpty())
				{
					updateAppConfig(appConfigRequest, configType);
				}
				//Calls for Insert
				else
				{
					AppConfigMaster appConfigCheck = null;
					if(configName != null) {
						appConfigCheck = appConfigRepository.findByConfigNameAndConfigType(configName.trim(),
								configType);
					}
					
					if (appConfigCheck != null) {
						return bffResponse.errResponse(
								List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_CREATION_CHECK,
										BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_CREATION_CHECK),
								StatusCode.INTERNALSERVERERROR,null,configType);
					} else {
						appConfigRepository.save(convertAppConfigReqToAppConfigMaster(appConfigRequest));
					}
				}
			}
			bffCoreResponse = bffResponse.response(appConfigRequestList,
					BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_UPDATE_VALUE,
					BffResponseCode.APP_CONFIG_SERVICE_USER_CODE_UPDATE_VALUE, StatusCode.OK);

		}

		catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_UPDATE_VALUE_DBEXCEPTION,
							BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_UPDATE_VALUE_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse
					.errResponse(
							List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_UPDATE_VALUE_EXCEPTION,
									BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_UPDATE_VALUE_EXCEPTION),
							StatusCode.BADREQUEST);
		}

		return bffCoreResponse;
	}



	/**Update already existing app config variables
	 *  - Context/Global - update raw value
	 *  - Application/Internal - update config value
	 *  
	 * @param appConfigRequest
	 * @param configType
	 */
	private void updateAppConfig(AppConfigRequest appConfigRequest, String configType) {
		
		UUID uid = appConfigRequest.getAppConfigId();
		
		if (AppCfgRequestType.valueOf(configType).equals(AppCfgRequestType.CONTEXT)
				|| AppCfgRequestType.valueOf(configType).equals(AppCfgRequestType.GLOBAL)) {
			appConfigRepository.updateAppConfigRawValue(uid, appConfigRequest.getRawValue());
		} else if (AppCfgRequestType.valueOf(configType).equals(AppCfgRequestType.APPLICATION)
				|| AppCfgRequestType.valueOf(configType).equals(AppCfgRequestType.INTERNAL)) {
			updateConfigValue(appConfigRequest, uid);
		}
	}



	/** Update config value for given appConfigId
	 * 
	 * @param appConfigRequest
	 * @param uid
	 */
	private void updateConfigValue(AppConfigRequest appConfigRequest, UUID uid) {
		AppConfigMaster appConfigMaster = appConfigRepository.findById(uid).orElseThrow();
		Optional<AppConfigDetail> appConfigDetail = appConfigDetailRepository
				.findByAppConfigMaster(appConfigMaster);
		if (appConfigDetail.isPresent()) {
			appConfigDetail.get().setConfigValue(appConfigRequest.getConfigValue());
			appConfigDetailRepository.save(appConfigDetail.get());
		}
	}

	/**
	 * Implementation for fetch AppConfig details based on configType
	 * 
	 * @param configType
	 * @return BffCoreResponse
	 */
	public BffCoreResponse getAppConfigDefinitionByType(AppCfgRequestType configType) {
		BffCoreResponse bffCoreResponse = null;
		try {
			List<AppConfigMaster> apiConfigList = appConfigRepository
					.findByConfigTypeOrderByConfigName(configType.getType());

			List<AppConfigDto> appConfigDtoList = new ArrayList<>();
			for (AppConfigMaster appConfig : apiConfigList) {
				appConfigDtoList.add(convertToAppConfigDto(appConfig));
			}
			LOGGER.log(Level.DEBUG, "App config list returned with size : {}", appConfigDtoList.size());
			bffCoreResponse = bffResponse.response(appConfigDtoList,
					BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_CODE_FETCHALL,
					BffResponseCode.APP_CONFIG_SERVICE_USER_CODE_FETCHALL, StatusCode.CREATED, null,
					configType.getType());
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCHALL_DBEXCEPTION,
							BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_FETCHALL_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR, null, configType.getType());
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCHALL_EXCEPTION,
							BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_FETCHALL_EXCEPTION),
					StatusCode.BADREQUEST, null,configType.getType());
		}
		return bffCoreResponse;
	}

	/**
	 * Implementation to fetch context and global variables
	 * 
	 * @return BffCoreResponse
	 */
	public BffCoreResponse getAppConfigList() {
		BffCoreResponse bffCoreResponse = null;
		try {
			Iterable<AppConfigMaster> apiConfigList = appConfigRepository.findByConfigTypeIn(
								List.of(AppCfgRequestType.CONTEXT.getType(),AppCfgRequestType.GLOBAL.getType()));
			
			List<AppConfigDto> appConfigDtoList = new ArrayList<>();
			for (AppConfigMaster appConfig : apiConfigList) {
					appConfigDtoList.add(convertToAppConfigDto(appConfig));
			}
			LOGGER.log(Level.DEBUG, "App Config List : {}", appConfigDtoList.size());
			bffCoreResponse = bffResponse.response(appConfigDtoList,
					BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_CODE_FETCHALL,
					BffResponseCode.APP_CONFIG_SERVICE_USER_CODE_FETCHALL, StatusCode.CREATED);
		} catch (DataAccessException exp) {			
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCHALL_DBEXCEPTION,
							BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_FETCHALL_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);

		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);

			bffCoreResponse = bffResponse
					.errResponse(
							List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCHALL_EXCEPTION,
									BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_FETCHALL_EXCEPTION),
							StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**
	 * Implementation to update List of AppConfigValue(Context/Global) for Mobile Renderer
	 * - To help in session recovery
	 * - Context and Global are maintained in AppConfigDetail for given userId and deviceId
	 * 
	 * @param appConfigRequestList
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse createUpdateAppConfigList(List<AppConfigRequest> appConfigRequestList) {
		BffCoreResponse bffCoreResponse = null;
		Optional<AppConfigDetail> appConfigDetail = null;
		List<AppConfigDetail> detailsList = new ArrayList<>();
		try {
			for (AppConfigRequest appConfigRequest : appConfigRequestList) {
				String configType = appConfigRequest.getConfigType();
				UUID uid = appConfigRequest.getAppConfigId();
				String userId = sessionDetails.getPrincipalName(); 
				String deviceName = sessionDetails.getDeviceName();
				UUID flowid = appConfigRequest.getFlowId();
				Optional<AppConfigMaster> appConfigMaster = appConfigRepository.findById(uid);
				
				// Check ConfigName and type is present
				if (appConfigMaster.isPresent()) {
					if (AppCfgRequestType.valueOf(configType).equals(AppCfgRequestType.CONTEXT)) {
						appConfigDetail = appConfigDetailRepository
								.findByAppConfigMasterAndUserIdAndFlowIdAndDeviceName(appConfigMaster.get(), userId, flowid,deviceName);

					} else if (AppCfgRequestType.valueOf(configType).equals(AppCfgRequestType.GLOBAL)) {
						appConfigDetail = appConfigDetailRepository
								.findByAppConfigMasterAndUserIdAndDeviceName(appConfigMaster.get(), userId,deviceName);
					} else {
						appConfigDetail = appConfigDetailRepository.findByAppConfigMaster(appConfigMaster.get());
					}

					//Config Name , User Id , device Id and FlowId - is present already , then update
					if (appConfigDetail.isPresent()) {
						appConfigDetail.get().setConfigValue(appConfigRequest.getConfigValue());
						detailsList.add(appConfigDetail.get());
						
					} else {
						AppConfigDetail appConfig = new AppConfigDetail();
						appConfig.setConfigValue(appConfigRequest.getConfigValue());
						appConfig.setAppConfigMaster(appConfigMaster.get());
						appConfig.setUserId(userId);
						appConfig.setFlowId(flowid);
						appConfig.setDeviceName(deviceName);
						detailsList.add(appConfig);
					}

				} else {
					return bffResponse.errResponse(
							List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_UPDATE_CONFIGTYPE,
									BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_UPDATE_CONFIGTYPE),
							StatusCode.CONFLICT, null,appConfigRequest.getConfigType());
				}
			}
			appConfigDetailRepository.saveAll(detailsList);
			List<AppConfigDto> appConfigDtoList = buildAppConfigDto(detailsList);
			
			
			bffCoreResponse = bffResponse.response(appConfigDtoList,
					BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_UPDATE_VALUE,
					BffResponseCode.APP_CONFIG_SERVICE_USER_CODE_UPDATE_VALUE, StatusCode.OK, null, null);
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);

			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_UPDATE_VALUE_DBEXCEPTION,
							BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_UPDATE_VALUE_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse
					.errResponse(
							List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_UPDATE_VALUE_EXCEPTION,
									BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_UPDATE_VALUE_EXCEPTION),
							StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**
	 * Clear context and global variables for given user id and device Id
	 * 
	 * @param userId
	 * @param deviceName
	 * @return BffCoreResponse
	 */

	@Override
	@Transactional
	public BffCoreResponse clearAppConfig(String userId,String deviceName) {
		BffCoreResponse bffCoreResponse = null;
		try {
			List<UUID> appConfigMasterList = appConfigRepository.fetchUserAndSpecificVariables(
					List.of(AppCfgRequestType.CONTEXT.getType(),AppCfgRequestType.GLOBAL.getType()),userId, deviceName);
			
			if (!CollectionUtils.isEmpty(appConfigMasterList)) {
				appConfigDetailRepository.deleteByIdInList(appConfigMasterList);
			}

			bffCoreResponse = bffResponse.response(appConfigMasterList,
					BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_CLEAR_VALUE,
					BffResponseCode.APP_CONFIG_SERVICE_USER_CLEAR_VALUE, StatusCode.CREATED);
			LOGGER.log(Level.DEBUG, "Clear app config data .");
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_CLEAR_VALUE_DBEXCEPTION,
							BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_CLEAR_VALUE_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse
					.errResponse(
							List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_CLEAR_VALUE_EXCEPTION,
									BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_CLEAR_VALUE_EXCEPTION),
							StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**
	 * Implementation to fetch AppConfig details based on configType and configName
	 * 
	 * @param configType
	 * @param configName
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse getAppConfig(String configName, AppCfgRequestType configType) {
		BffCoreResponse bffCoreResponse = null;
		try {
			AppConfigMaster appConfigTypeAndNameAndUser = appConfigRepository.findByConfigNameAndConfigType(configName,
					configType.getType());

			bffCoreResponse = bffResponse.response(appConfigTypeAndNameAndUser,
					BffResponseCode.APP_CONFIG_SERVICE_SUCCESS_CODE_FETCH,
					BffResponseCode.APP_CONFIG_SERVICE_USER_CODE_FETCH, StatusCode.CREATED,
					configType.getType(), configName);

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, configName);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCH_DBEXCEPTION,
							BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_FETCH_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR, configType.getType(),
					configName);

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, configName);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_APP_CONFIG_SERVICE_API_FETCH_EXCEPTION,
							BffResponseCode.ERR_APP_CONFIG_SERVICE_USER_FETCH_EXCEPTION),
					StatusCode.BADREQUEST, configType.getType(), configName);
		}

		return bffCoreResponse;
	}

	private AppConfigMaster convertAppConfigReqToAppConfigMaster(AppConfigRequest appConfigRequest) {

		AppConfigMaster appConfigMaster = new AppConfigMaster();
		appConfigMaster.setConfigName(appConfigRequest.getConfigName());
		appConfigMaster.setConfigType(appConfigRequest.getConfigType());
		appConfigMaster.setRawValue(appConfigRequest.getRawValue());

		if ((sessionDetails.getChannel().equals(ChannelType.MOBILE_RENDERER.getType()))
				|| (sessionDetails.getChannel().equals(ChannelType.ADMIN_UI.getType())
						&& (appConfigRequest.getConfigType().equals(AppCfgRequestType.APPLICATION.getType())
								|| appConfigRequest.getConfigType().equals(AppCfgRequestType.INTERNAL.getType())))) {
			List<AppConfigDetail> appConfigDetails = new ArrayList<>();

			AppConfigDetail appConfigDetail = new AppConfigDetail();
			appConfigDetail.setConfigValue(appConfigRequest.getConfigValue());
			appConfigDetail.setDescription(appConfigRequest.getDescription());
			appConfigDetail.setFlowId(appConfigRequest.getFlowId());
			appConfigDetail.setAppConfigMaster(appConfigMaster);
			appConfigDetails.add(appConfigDetail);

			appConfigMaster.setAppConfigDetails(appConfigDetails);
		}
		return appConfigMaster;
	}

	private AppConfigDto convertToAppConfigDto(AppConfigMaster appConfig) {
		AppConfigDetail appConfigDetail = null;
		if (appConfig.getAppConfigDetails() != null && !appConfig.getAppConfigDetails().isEmpty()) {
			appConfigDetail = appConfig.getAppConfigDetails().get(0);
		} else {
			appConfigDetail = new AppConfigDetail();
		}

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
	
	/**
	 * Implementation to fetch AppConfig details based on configType, userId and deviceId - Mobile
	 * - To launch the mobile application these are set of variables required
	 * - Fetch Application , Context(for user and flow) and Global (for user)
	 * 
	 * @param deviceName
	 * @return List&lt;AppConfigDto&gt;
	 */
	public List<AppConfigDto> getAppSettingsList(String deviceName) {
		String userId = BffAdminConstantsUtils.EMPTY_SPACES;

		if (null != sessionDetails && null != sessionDetails.getPrincipalName()) {
			userId = sessionDetails.getPrincipalName();
		}
		Iterable<com.jda.mobility.framework.extensions.entity.projection.AppConfigDetailDto> appConfigList = appConfigRepository
				.fetchAllAndUserAndDeviceSpecificVariables(List.of(AppCfgRequestType.CONTEXT.getType(),
						AppCfgRequestType.GLOBAL.getType(), AppCfgRequestType.APPLICATION.getType()), userId, deviceName);

		List<AppConfigDto> appConfigDtoList = new ArrayList<>();

		for (AppConfigDetailDto appConfigDetail : appConfigList) {
			AppConfigDto appConfigDto = AppConfigDto.builder()
					.appConfigId(appConfigDetail.getAppConfigId())
					.configName(appConfigDetail.getConfigName())
					.configType(appConfigDetail.getConfigType())
					.rawValue(appConfigDetail.getRawValue())
					.configValue(appConfigDetail.getConfigValue())
					.flowId(appConfigDetail.getFlowId())
					.userId(appConfigDetail.getUserId())
					.description(appConfigDetail.getDescription())
					.deviceName(appConfigDetail.getDeviceName())
					.build();

			appConfigDtoList.add(appConfigDto);
		}

		LOGGER.log(Level.DEBUG, "App Config List : {}", appConfigDtoList.size());
		return appConfigDtoList;
	}
	
}
