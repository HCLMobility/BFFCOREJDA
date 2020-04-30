package com.jda.mobility.framework.extensions.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.service.LocaleService;


/**
 *The class that retrieve list of locale supported by the product
 */
@RestController
@RequestMapping("/api/locale/v1")
public class LocaleController {
	
	@Autowired
	private LocaleService localeServices;


	/**Retrieves the list the locale 
	 * @return
	 */
	@GetMapping("/list")
	public ResponseEntity<BffCoreResponse> getLocaleList() {
		BffCoreResponse responseModel = localeServices.getLocaleList();
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}
}
