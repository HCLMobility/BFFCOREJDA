/**
 * 
 */
package com.jda.mobility.framework.extensions.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.CustomFormData;
import com.jda.mobility.framework.extensions.service.CustomFormService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.CustomFormFilterMode;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DeleteType;

/**
 * The implementation to create new CustomComponent and update/delete the
 * existing CustomComponent.
 * 
 * @author HCL Technologies
 */

@RestController
@RequestMapping("/api/customcomponent/v1")
public class CustomFormController {

	@Autowired
	private CustomFormService customFormService;

	/**
	 * Create new CustomComponent form.
	 *
	 * @param customFormData
	 * @return BffCoreResponse
	 */
	@PostMapping("/")
	public ResponseEntity<BffCoreResponse> createCustomComponent(@Valid @RequestBody CustomFormData customFormData) {
		BffCoreResponse responseModel = customFormService.createCustomComponent(customFormData);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Retrieve CustomComponent form for customComponentId
	 *
	 * @param customComponentId
	 * @return BffCoreResponse
	 */
	@GetMapping("/{customComponentId}")
	public ResponseEntity<BffCoreResponse> getCustomComponentById(
			@Valid @PathVariable("customComponentId") UUID customComponentId) {
		BffCoreResponse responseModel = customFormService.getCustomComponentById(customComponentId);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Modify CustomComponentFormData based on customComponentId
	 *
	 * @param customFormData
	 * @param customComponentId
	 * @return BffCoreResponse
	 */
	@PutMapping("/{customComponentId}")
	public ResponseEntity<BffCoreResponse> modifyCustomComponent(@Valid @RequestBody CustomFormData customFormData,
			@Valid @PathVariable("customComponentId") UUID customComponentId) {
		customFormData.setCustomComponentId(customComponentId);
		BffCoreResponse responseModel = customFormService.modifyCustomComponent(customFormData);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Delete CustomComponent based on customComponentId
	 *
	 * @param customComponentId
	 * @param identifier
	 * @return BffCoreResponse
	 */
	@DeleteMapping("/{customComponentId}/{identifier}")
	public ResponseEntity<BffCoreResponse> deleteCustomComponent(
			@PathVariable("customComponentId") UUID customComponentId,
			@Valid @PathVariable("identifier") DeleteType identifier) {
		BffCoreResponse responseModel = customFormService.deleteCustomComponentById(customComponentId, identifier);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Retrieve list of All custom components.
	 *
	 * @return BffCoreResponse
	 */
	@GetMapping("/list")
	public ResponseEntity<BffCoreResponse> fetchCustomComponentList(
			@RequestParam(required = false) CustomFormFilterMode identifier, @RequestParam(required = false) Integer pageNo,
			@RequestParam(required = false) Integer pageSize) {
		BffCoreResponse responseModel = customFormService.fetchCustomCompList(identifier, pageNo, pageSize);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}
}