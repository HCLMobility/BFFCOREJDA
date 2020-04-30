package com.jda.mobility.framework.extensions.controller;

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

import com.jda.mobility.framework.extensions.dto.TranslationRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.service.TranslationService;

/**
 * The class that implements to create, fetch resource bundle like Key and value pair
 * along with locale
 * Also it allows user to update changed locale in session
 * 
 * @author HCL Technologies
 */
@RestController
@RequestMapping("/api/core/v1")
public class TranslationController {

	@Autowired
	private TranslationService translationService;

	/**
	 * Implementation for creation of resource bundle
	 * 
	 * @param translationRequest
	 * @return ResponseEntity
	 */
	@PostMapping("/messages")
	public ResponseEntity<BffCoreResponse> createMessages(@RequestBody TranslationRequest translationRequest) {
		BffCoreResponse responseModel = translationService.createResourceBundle(translationRequest);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation to retrieve resource bundles based on locale and type
	 * 
	 * @param type
	 * @return ResponseEntity
	 */
	@GetMapping("/messages/list")
	public ResponseEntity<BffCoreResponse> getMessages(@RequestParam String type) {
		BffCoreResponse responseModel = translationService.getResourceBundles( type);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation to update locale in session
	 * 
	 * @param locale
	 * @return
	 */
	@PutMapping("/locale/{language}")
	public ResponseEntity<BffCoreResponse> updateLocale(@PathVariable("language") String locale) {
		BffCoreResponse responseModel = translationService.updateLocale(locale);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}
}
