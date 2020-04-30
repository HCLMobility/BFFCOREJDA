/**
* 
*/
package com.jda.mobility.framework.extensions.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.service.RegistryService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ApiRegistryType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.LayerMode;

/**
 * The class to fetch imported registry and API's
 * 
 * @author HCL Technologies
 *
 */

@RestController
@RequestMapping("/api/registry/v1")
public class RegistryController {

	@Autowired
	private RegistryService registryService;

	private static final Logger LOGGER = LogManager.getLogger(RegistryController.class);

	/**
	 * Implementation to fetch all Registry from ApiRegistry.
	 *
	 * @return BffCoreResponse
	 */
	@GetMapping("/list")
	public ResponseEntity<BffCoreResponse> fetchAllRegistries(@RequestParam(required = false) LayerMode mode) {
		BffCoreResponse responseModel = registryService.fetchAllRegistries(mode);
		LOGGER.debug("Existing from fetchAllRegistries in Controller");
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}
	
	/**
	 * Implementation to fetch all Registry in the given type from ApiRegistry.
	 *
	 * @return BffCoreResponse
	 */
	@GetMapping("/list/{type}")
	public ResponseEntity<BffCoreResponse> fetchRegistries(@PathVariable("type") List<ApiRegistryType> type, @RequestParam(required = false) LayerMode mode) {
		BffCoreResponse responseModel = registryService.fetchRegistries(type, mode);
		LOGGER.debug("Existing from fetchAllRegistries in Controller");
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}	

	/**
	 * Implementation to Fetch the Registry based on Registry id .
	 *
	 * @param registryId
	 * @return BffCoreResponse
	 */
	@GetMapping("/{registryId}")
	public ResponseEntity<BffCoreResponse> fetchRegistryById(@Valid @PathVariable("registryId") UUID registryId) {
		BffCoreResponse responseModel = registryService.fetchRegistryById(registryId);
		LOGGER.debug("Existing from fetch based on RegistryById method in Controller");
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation to fetch API based on api id
	 *
	 * @param id
	 * @return BffCoreResponse
	 */
	@GetMapping("master/{id}")
	public ResponseEntity<BffCoreResponse> fetchApiById(@Valid @PathVariable("id") UUID id) {
		BffCoreResponse responseModel = null;
		ResponseEntity<BffCoreResponse> responseEntity = null;
		responseModel = registryService.fetchApiById(id);
		LOGGER.debug("Exiting from fetchApiMaster byID in Controller");
		responseEntity = new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
		return responseEntity;
	}

	/**
	 * Implementation to fetch all API's based on  Registry Id
	 * @param registryId
	 * @return BffCoreResponse
	 */
	@GetMapping("/master/list")
	public ResponseEntity<BffCoreResponse> fetchByRegistryIdOrApiList(@RequestParam(required = false) UUID registryId) {
		BffCoreResponse responseModel = null;
		ResponseEntity<BffCoreResponse> responseEntity = null;
		if (Optional.ofNullable(registryId).isPresent()) {
			responseModel = registryService.fetchApiByRegistryId(registryId);
			LOGGER.debug("Exiting from fetchApiByRegistryId ");
			responseEntity = new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
		} else {
			LOGGER.debug("Feathing all AllapiMaster data ");
			responseModel = registryService.fetchAllApis();
			LOGGER.debug("Exiting from AllapiMaster data");
			responseEntity = new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
		}
		return responseEntity;
	}
}
