package com.jda.mobility.framework.extensions.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.service.PreAndPostProcessorService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ApiUploadMode;

@RestController
@RequestMapping("/api/preandpostprocessor/v1")
public class PreAndPostProcessorController {

	@Autowired
	private PreAndPostProcessorService preAndPostProcessorService;
	
	/**
	 * @param preProcessorFile
	 * @param postProcessorFile
	 * @param apiMasterId
	 * @param checkUpload
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@PostMapping("/upload")
	public ResponseEntity<BffCoreResponse> importPreAndPostProcessor(
			@RequestParam(name = "preProcessorFile", required = false) MultipartFile preProcessorFile,
			@RequestParam(name = "postProcessorFile", required = false) MultipartFile postProcessorFile,
			@RequestParam(name = "apiMasterId") UUID apiMasterId,
			@RequestParam(name = "identifier") ApiUploadMode checkUpload){
		BffCoreResponse responseModel = preAndPostProcessorService.importApiIntoNewRegistry(preProcessorFile, postProcessorFile, apiMasterId, checkUpload);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}
	
}
