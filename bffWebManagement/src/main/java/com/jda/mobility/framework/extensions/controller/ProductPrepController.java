package com.jda.mobility.framework.extensions.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.config.ProductApiSettings;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.PrepRequest;
import com.jda.mobility.framework.extensions.service.ProductPrepareService;
import com.jda.mobility.framework.extensions.util.RequestHelper;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.SessionAttribute;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

/**
 *The controller that does functionalities related to product configuration.
 * Ex: Fetch/Create secondary reference Id , Product Config Id , Mobile App Launch configuration etc.
 * 
 * @author HCL Technologies
 */

@RestController
@RequestMapping("/api/product/v1")
public class ProductPrepController {

	private static final Logger LOGGER = LogManager.getLogger(ProductPrepController.class);

	@Autowired
	private ProductPrepareService productPrepareService;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private BffResponse bffResponse;

	@Autowired
	private SessionDetails session;

	@Autowired
	private ProductApiSettings productApis;

	@Autowired
	private RequestHelper requestHelper;

	/**
	 * 
	 * Fetch Default flow information for the application/product
	 *
	 * @param configName
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/defaultflow/{configName}")
	public ResponseEntity<BffCoreResponse> fetchDefaultFlowConfig(
			@Valid @PathVariable("configName") String configName) {
		BffCoreResponse responseModel = productPrepareService.fetchDefaultFlowConfig(configName);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 *  Retrieves flow list based on layering concept for given warehouse and default warehouse
	 *
	 * @param prepRequest
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@PostMapping("/dashboardflows")
	public ResponseEntity<BffCoreResponse> fetchDashboardFlows(@Valid @RequestBody PrepRequest prepRequest) {
		BffCoreResponse responseModel = productPrepareService.fetchDashboardFlows(prepRequest);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Retrieves the product config Id for given warehouse and layer
	 *
	 * @param prepRequest
	 * @param request
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@PostMapping("/productconfigid")
	public ResponseEntity<BffCoreResponse> fetchProductConfigId(@Valid @RequestBody PrepRequest prepRequest,
			HttpServletRequest request) {
		if (request.getSession(false) != null) {
			request.getSession(false).setAttribute(SessionAttribute.WAREHOUSE_ID.name(), prepRequest.getName());
			request.getSession(false).setAttribute("WAREHOUSE_ID_VALUE", prepRequest.getPropValue());
		}
		BffCoreResponse responseModel = productPrepareService.fetchProductConfigId(prepRequest);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Fetch the Default flow and Home flow information
	 * 
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/defhomeflow")
	public ResponseEntity<BffCoreResponse> getDefaultHomeFlow() {
		BffCoreResponse responseModel = productPrepareService.getDefaultHomeFlow();
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}
	
	/**
	 * Fetch the Default , Home flow , hot keys ,context , global and application variables - Mobile
	 * 
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/appsettings")
	public ResponseEntity<BffCoreResponse> getAppSettings(@RequestParam(required=false)	 String recoveredDeviceId ) {
		BffCoreResponse responseModel = productPrepareService.getAppSettings(recoveredDeviceId);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Retrieves the product config Id for given warehouse and layer
	 *
	 * @param httpReq
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/warehouse")
	public ResponseEntity<BffCoreResponse> getWarehouseList(HttpServletRequest httpReq) {
		String userId = session.getPrincipalName();

		BffCoreResponse bffCoreResponse;
		try {
			
			HttpHeaders headers = requestHelper.initHeadersFrom(httpReq);

			UriComponents uri = productApis.warehousesUrl().buildAndExpand(Map.of("userId", userId));

			ResponseEntity<Object> responseEntity = restTemplate.exchange(uri.toUriString(), HttpMethod.GET,
					new HttpEntity<String>(headers), Object.class);

			bffCoreResponse = bffResponse.response(responseEntity.getBody(), BffResponseCode.WAREHOUSE_SUCCESS_CODE,
					BffResponseCode.WAREHOUSE_SUCC_USER_CODE, StatusCode.OK);
		} catch (RestClientException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, userId, exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.WAREHOUSE_EXP_CODE, BffResponseCode.WAREHOUSE_EXP_USER_CODE),
					StatusCode.BADREQUEST, userId, userId);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, userId, exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.WAREHOUSE_EXP_CODE, BffResponseCode.WAREHOUSE_EXP_USER_CODE),
					StatusCode.INTERNALSERVERERROR, userId, userId);
		}
		return new ResponseEntity<>(bffCoreResponse, HttpStatus.valueOf(bffCoreResponse.getHttpStatusCode()));
	}

}
