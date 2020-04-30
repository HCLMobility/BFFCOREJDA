package com.jda.mobility.framework.extensions.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jda.mobility.framework.extensions.model.AppConfigRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.service.AppConfigService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.AppCfgRequestType;

/**
 * The class implements for create, update ,clear and fetch context, global and applciation variables
 * details
 * 
 * @author HCL The Class AppController.java
 */

@RestController
@RequestMapping("/api/config/v1")
public class AppConfigController {

	@Autowired
	private AppConfigService appConfigService;

	/**
	 * Implementation for create a new AppConfig
	 * 
	 * @param appConfigListRequest  List&lt;AppConfigRequest&gt; List of AppConfigRequest details
	 * @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after creating appConfigration 
	 */
	@PostMapping("/")
	public ResponseEntity<BffCoreResponse> createAppConfigDefinition(@RequestBody List<AppConfigRequest> appConfigListRequest) {
		BffCoreResponse responseModel = appConfigService.createAppConfigDefinition(appConfigListRequest);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for update rawvalue or config value for given appConfigId
	 * 
	 * @param appConfigRequestList List&lt;AppConfigRequest&gt; Based on configId it will update list of rawvalues/config values
	 * @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after Updating appConfigration 
	 */
	@PutMapping("/configValue")
	public ResponseEntity<BffCoreResponse> updateAppConfigDefinition(@RequestBody List<AppConfigRequest> appConfigRequestList) {
		BffCoreResponse responseModel = appConfigService.updateAppConfigDefinition(appConfigRequestList);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}
	
	/**
	 * Implementation for fetch AppConfig details based on configType
	 * 
	 * @param configType fetch AppConfig details based on configType
	  * @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after fetch appConfigration 
	 */
	@GetMapping("/list/{configType}")
	public ResponseEntity<BffCoreResponse> getAppConfigDefinitionByType(@PathVariable("configType") AppCfgRequestType configType) {
		BffCoreResponse responseModel = appConfigService.getAppConfigDefinitionByType(configType);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}
	
	/**
	 * Implementation for fetch AppConfig details based on configType and configName
	 * 
	 * @param configType The type of appConfig
	 * @param configName The Name of appConfig
	* @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after fetch appConfigration 
	 */
	@GetMapping("/{configType}/{configName}")
	public ResponseEntity<BffCoreResponse> getAppConfig(@PathVariable("configType") AppCfgRequestType configType,
			@PathVariable("configName") String configName) {
		BffCoreResponse responseModel = appConfigService.getAppConfig(configName,configType);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	

	/**
	 * Implementation for fetch all appConfig details 
	 * 
	 * @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after fetch list of appConfig details  
	 */
	@GetMapping("/list")
	public ResponseEntity<BffCoreResponse> getAppConfigList() {
		BffCoreResponse responseModel = appConfigService.getAppConfigList();
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for create or update context and global variable for given userId and device Id (Mobile)	
	 * 
	 * @param appConfigRequestList List&lt;AppConfigRequest&gt; List of AppConfigRequest details
	 * @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after Creating/Updating list of appConfig details 
	 */
	@PutMapping("/update")
	public ResponseEntity<BffCoreResponse> updateAppConfigList(@RequestBody List<AppConfigRequest> appConfigRequestList) {
		BffCoreResponse responseModel = appConfigService.createUpdateAppConfigList(appConfigRequestList);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for clear AppConfig details based on configType , userId and deviceId
	 * 
	 * @param userId UsedId for appConfig
	 * @param flowId flowId for appConfig
	 * @param deviceName deviceName for appConfig
	 * @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after Updating list of appConfig details 
	 */
	@PutMapping("/clearAppConfig")
	public ResponseEntity<BffCoreResponse> clearAppConfig(@RequestParam(required = false) String userId, @RequestParam(required = false) UUID flowId,@RequestParam(required = false) String deviceName) {
		BffCoreResponse responseModel = appConfigService.clearAppConfig(userId,deviceName);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}
	

}
