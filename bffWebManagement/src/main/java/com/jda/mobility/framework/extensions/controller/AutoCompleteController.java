package com.jda.mobility.framework.extensions.controller;

import javax.servlet.http.HttpServletRequest;

import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.SearchRequest;
import com.jda.mobility.framework.extensions.service.AutoCompleteService;
import com.jda.mobility.framework.extensions.util.RequestHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class implements auto complete feature for various components
 */
@RestController
@RequestMapping("/api/autocomplete/v1")
public class AutoCompleteController {

	private final AutoCompleteService autoCompleteService;
	private final RequestHelper requestHelper;

	public AutoCompleteController(AutoCompleteService autoCompleteService, RequestHelper requestHelper) {
		this.autoCompleteService = autoCompleteService;
		this.requestHelper = requestHelper;
	}

	/**Method that implements Auto complete feature 
	 * @param searchRequest
	 * @param httpReq
	 * @return
	 */
	@PostMapping("/search")
	public ResponseEntity<BffCoreResponse> search(@RequestBody SearchRequest searchRequest,
			HttpServletRequest httpReq) {
		String bearerToken = requestHelper.oidcToken(httpReq);
		String authCookie = requestHelper.cookieValue(httpReq);
		BffCoreResponse responseModel = autoCompleteService.search(searchRequest, authCookie, bearerToken);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));

	}
}
