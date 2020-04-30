/**
 * 
 */
package com.jda.mobility.framework.extensions.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.service.ExtensionHistoryService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ExtensionType;

/**
 * The EndPoint implementations to extract extension history related
 * information.
 * 
 * @author HCL Technologies
 */
@RestController
@RequestMapping("/api/extension/v1")
public class ExtensionHistoryController {

	@Autowired
	private ExtensionHistoryService extHistoryService;

	/**
	 * Fetch difference capability for extended component with parent within
	 * different layers and components within same layer.
	 *
	 * @param extendedObjectId
	 * @param parentObjectId   - Optional
	 * @param extensionType
	 * @return BffCoreResponse
	 */
	@GetMapping("/variance/{extendedObjectId}")
	public ResponseEntity<BffCoreResponse> fetchExtensionHistory(
			@Valid @PathVariable("extendedObjectId") UUID extendedObjectId,
			@RequestParam(required = false) UUID parentObjectId,
			@RequestParam(required = true) ExtensionType extensionType) {
		BffCoreResponse responseModel = extHistoryService.fetchExtensionHistory(extendedObjectId, parentObjectId,
				extensionType);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

}
