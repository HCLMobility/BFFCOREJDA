package com.jda.mobility.framework.extensions.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jda.mobility.framework.extensions.model.ApiMasterRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.service.ApiImportService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ApiRegistryType;

/**
 * The class implements create , update and override existing registry and API's
 * 
 * @author HCL Technologies
 */


@RestController

@RequestMapping("/api/import/v1")
public class ApiImportController {

	@Autowired
	private ApiImportService apiImportService;

	/**
	 * Implementation for create a new registry and its API's
	 * 
	 * /**
	 * 
	 * @param file The swagger file content as bytes
	 * @param newRegistryApiType   create a new registry type for ApiType
	 * @param newRegistryName Create a registry name for ApiType
	 * @param ruleFile    The orchestration rule file content as bytes to be updated
	 * @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after importing into new registry
	 */

	@PostMapping("/registry")
	public ResponseEntity<BffCoreResponse> importApiIntoNewRegistry(@RequestPart("file") MultipartFile file,
			@RequestParam("newRegistryApiType") ApiRegistryType newRegistryApiType,
			@RequestParam("newRegistryName") String newRegistryName,
			@RequestParam(name = "OrchFile", required = false) MultipartFile ruleFile) {
		BffCoreResponse responseModel = apiImportService.importApiIntoNewRegistry(file, newRegistryName,
				newRegistryApiType, ruleFile);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for modify existing registry and API's
	 * 
	 * @param file The swagger file content as bytes
	 * @param override    Flag to decide whether to overwrite or append APIs to existing registry
	 * @param registryId  The registry id of the registry to be modified
	 * @param ruleFile    The orchestration rule file content as bytes to be updated
	 * @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after importing into existing registry
	 */
	@PutMapping("/{registryid}/{override}")
	public ResponseEntity<BffCoreResponse> importApiIntoExistingRegistry(@RequestPart(name = "file", required = false) MultipartFile file,
			@PathVariable("registryid") UUID registryId, @PathVariable("override") boolean override, @RequestParam(name = "OrchFile", required = false) MultipartFile ruleFile)
			throws IOException {
		BffCoreResponse responseModel = apiImportService.importApiIntoExistingRegistry(file.getBytes(), override,
				registryId,ruleFile);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for overriding existing registry and API's 
	 *
	 * @param apiMasterList List&lt;ApiMasterRequest&gt; List of ApiMaster details
	 * @param registryId  The registry id of the registry to be modified
	 * @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after overwrite into existing registry
	 */
	@PutMapping("/registry/{registryid}")
	public ResponseEntity<BffCoreResponse> overrideApiData(@Valid @RequestBody List<ApiMasterRequest> apiMasterList,
			@PathVariable("registryid") UUID registryId) {
		BffCoreResponse responseModel = apiImportService.overrideExistingApis(apiMasterList, registryId);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}
}