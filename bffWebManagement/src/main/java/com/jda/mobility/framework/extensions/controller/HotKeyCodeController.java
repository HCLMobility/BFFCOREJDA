package com.jda.mobility.framework.extensions.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.service.HotKeyCodeService;

/**
 *The class that retrieve list of hot keys supported by the product
 */
@RestController
@RequestMapping("/api/hotkey/v1")
public class HotKeyCodeController {
	
	@Autowired
	private HotKeyCodeService hotKeyMapService;


	/**Retrieves the list the hot keys 
	 *  
	 * @return
	 */
	@GetMapping("/list")
	public ResponseEntity<BffCoreResponse> getHotKeyList() {
		BffCoreResponse responseModel = hotKeyMapService.getHotKeyList();
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}
}

