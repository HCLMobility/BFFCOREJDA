package com.jda.mobility.framework.extensions.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.CloneRequest;
import com.jda.mobility.framework.extensions.service.FlowService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.CloneType;

@RestController
@RequestMapping("/api/version/v1")
public class VersionController {

	@Autowired
	private FlowService flowService;

	@PostMapping("/{actionType}")
	public ResponseEntity<BffCoreResponse> createNewVersionforFlow(@Valid @RequestBody CloneRequest cloneRequest,
			@PathVariable CloneType actionType) {
		BffCoreResponse responseModel = flowService.cloneComponent(cloneRequest, actionType,
				BffAdminConstantsUtils.VERSIONING);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

}
