/**
 * 
 */
package com.jda.mobility.framework.extensions.service;

import java.util.List;

import org.springframework.retry.annotation.Retryable;

import com.jda.mobility.framework.extensions.dto.AppConfigDto;
import com.jda.mobility.framework.extensions.model.AppConfigRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.AppCfgRequestType;

/**
 * @author HCL
 *
 */
@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface AppConfigService {


	/**
	 * @param appConfigListRo The list of application config requests
	 * @return BffCoreResponse The success/error message Object
	 */
	BffCoreResponse createAppConfigDefinition(List<AppConfigRequest> appConfigListRo);
	
	
	/**
	 * @param configType The application config type - GLOBAL/CONTEXT/APPLICATION
	 * @param configName The application config variable name
	 * @return BffCoreResponse The success/error message Object
	 */
	BffCoreResponse getAppConfig(String configName, AppCfgRequestType configType);
	
	/**
	 * @param configType The application config type - GLOBAL/CONTEXT/APPLICATION
	 * @return BffCoreResponse The success/error message Object
	 */
	BffCoreResponse getAppConfigDefinitionByType(AppCfgRequestType configType);
	
	/**
	 * @param userId The user Id to clear the application config variables - GLOBAL/CONTEXT
	 * @param deviceName The device name to clear the application config variables - GLOBAL/CONTEXT
	 * @return BffCoreResponse The success/error message Object
	 */
	BffCoreResponse clearAppConfig(String userId,String deviceName);
	
	/**
	 * @param appConfigRequestList The list of application config requests 
	 * @return BffCoreResponse The success/error message Object
	 */
	BffCoreResponse updateAppConfigDefinition(List<AppConfigRequest> appConfigRequestList);


	/**
	 * @return BffCoreResponse The success/error message Object
	 */
	BffCoreResponse getAppConfigList();
	
	/**
	 * @param deviceName The device name to get application settings
	 * @return List&lt;AppConfigDto&gt; The list of application config responses 
	 */	
	List<AppConfigDto> getAppSettingsList(String deviceName);
	
	/**
	 * @param appConfigRequestList The list of application config requests 
	 * @return BffCoreResponse The success/error message Object
	 */
	BffCoreResponse createUpdateAppConfigList(List<AppConfigRequest> appConfigRequestList);


}
