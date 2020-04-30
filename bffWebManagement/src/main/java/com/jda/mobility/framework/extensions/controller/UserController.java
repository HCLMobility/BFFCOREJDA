package com.jda.mobility.framework.extensions.controller;

import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.config.ProductApiSettings;
import com.jda.mobility.framework.extensions.dto.UserNameDto;
import com.jda.mobility.framework.extensions.model.AccessRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.ProductRolePermission;
import com.jda.mobility.framework.extensions.repository.MasterUserRepository;
import com.jda.mobility.framework.extensions.service.UserService;
import com.jda.mobility.framework.extensions.util.RequestHelper;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ROLEID;

/**
 * The class implements to fetch, create , update , delete  - User layer, roles, privileges and permissions.
 * Also helps to map the user with layers and privileges
 * 
 * @author HCL Technologies
 */

@RestController
@RequestMapping("/api/user/v1")
public class UserController {

	@Autowired
	private UserService bffAccessService;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private BffResponse bffResponse;
	@Autowired
	private MasterUserRepository masterUserRepository;
	@Autowired
	private ProductApiSettings productApis;
	@Autowired
	private RequestHelper requestHelper;

	private static final Logger LOGGER = LogManager.getLogger(UserController.class);

	/**
	 * Method to validate user with WMS API
	 *
	 * @param userId
	 * @param httpReq
	 * @return bffCoreResponse
	 */
	@GetMapping("/{userId}/validation")
	public ResponseEntity<BffCoreResponse> validateUser(@PathVariable String userId,
			HttpServletRequest httpReq) {
		BffCoreResponse bffCoreResponse;
		ResponseEntity<BffCoreResponse> response;
		UserNameDto userNameDto = new UserNameDto();
		userNameDto.setUserId(userId);
		try {
			HttpHeaders headers = requestHelper.initHeadersFrom(httpReq);

			UriComponents uriComponents = productApis.validateUsersUrl().pathSegment(userId).build();

			HttpEntity<String> request = new HttpEntity<>(headers);

			ResponseEntity<Object> responseEntity = restTemplate.exchange(uriComponents.toString(), HttpMethod.GET,
					request, Object.class);

			if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
				userNameDto.setValid(true);
			}
			
			//Check for super user
			userNameDto.setSuperUser((masterUserRepository.countByUserId(userId) > 0));

			bffCoreResponse = bffResponse.response(userNameDto,
					BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_VALIDATE_USER,
					BffResponseCode.ACCESS_SERVICE_USER_CODE_VALIDATE_USER, StatusCode.OK);
			response = generateResponse(bffCoreResponse);

		} catch (HttpClientErrorException httpex) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, userId, httpex);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_ACCESS_SERVICE_API_VALIDATE_USER,
					BffResponseCode.ERR_ACCESS_SERVICE_USER_VALIDATE_USER), StatusCode.BADREQUEST);
			response = generateResponse(bffCoreResponse);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, userId, exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_ACCESS_SERVICE_API_VALIDATE_USER,
					BffResponseCode.ERR_ACCESS_SERVICE_USER_VALIDATE_USER), StatusCode.INTERNALSERVERERROR);
			response = generateResponse(bffCoreResponse);
		}
		return response;
	}

	/**
	 * Implementation for getting list of user layers
	 *
	 * @return BffCoreResponse
	 */
	@GetMapping("/layers")
	public ResponseEntity<BffCoreResponse> fetchUserRole() {
		BffCoreResponse responseModel = bffAccessService.getRoles();
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for associating user with layer
	 *
	 * @param accessRequest
	 * @return BffCoreResponse
	 */
	@PostMapping("/layers/map")
	public ResponseEntity<BffCoreResponse> mapUserRole(@RequestBody AccessRequest accessRequest) {
		BffCoreResponse responseModel = bffAccessService.mapUserRole(accessRequest);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for creating new use layer
	 *
	 * @param roleName
	 * @return BffCoreResponse
	 */
	@PostMapping("/layers")
	public ResponseEntity<String> createRoleMaster(@RequestParam String roleName) {
		String roleMasterId = bffAccessService.createRoleMaster(roleName);
		return new ResponseEntity<>(roleMasterId, HttpStatus.CREATED);
	}

	/**
	 * Implementation for creating a new user privilege
	 *
	 * @param privilegeName
	 * @return BffCoreResponse
	 */
	@PostMapping("/privileges")
	public ResponseEntity<String> createPrivilegeMaster(@RequestParam String privilegeName) {
		String privilegeMasterId = bffAccessService.createPrivilegeMaster(privilegeName);
		return new ResponseEntity<>(privilegeMasterId, HttpStatus.CREATED);
	}

	/**
	 * Implementation for mapping layer with privilege
	 *
	 * @param roleId
	 * @param privilegeId
	 * @return BffCoreResponse
	 */
	@PostMapping("/privileges/map")
	public ResponseEntity<String> mapRolePrivilege(@Valid @RequestParam UUID roleId,
			@Valid @RequestParam UUID privilegeId) {
		String rolePrivilegeId = bffAccessService.mapRolePrivilege(roleId, privilegeId);
		return new ResponseEntity<>(rolePrivilegeId, HttpStatus.CREATED);
	}

	/**
	 * Implementation to fetch WMS user permissions
	 *
	 * @param roleId
	 * @param httpReq
	 * @return ResponseEntity
	 */
	@GetMapping("/productperms")
	public ResponseEntity<BffCoreResponse> fetchProductUserPermissions(
			@RequestParam(required = false) String roleId,
			HttpServletRequest httpReq) {
		BffCoreResponse bffCoreResponse;

		try {
			HttpHeaders headers = requestHelper.initHeadersFrom(httpReq);

			HttpEntity<String> request = new HttpEntity<>(headers);

			UriComponentsBuilder uriBuilder = productApis.permissionsUrl().queryParam("type", "MOBILE");

			
			if (roleId != null) {
				uriBuilder.queryParam(ROLEID, roleId);
			}

			ResponseEntity<ProductRolePermission> permissions = restTemplate.exchange(uriBuilder.toUriString(),
					HttpMethod.GET, request, ProductRolePermission.class);
			LOGGER.info("Enter into fetchProductUserPermissions controller");
			bffCoreResponse = bffResponse.response(permissions.getBody(),
					BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_PRODUCT_PERMISSIOMS,
					BffResponseCode.ACCESS_SERVICE_USER_CODE_PRODUCT_PERMISSIOMS, StatusCode.OK,
					roleId, roleId);

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, roleId, exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_ACCESS_SERVICE_API_PRODUCT_PERMISSIOMS,
							BffResponseCode.ERR_ACCESS_SERVICE_USER_PRODUCT_PERMISSIOMS),
					StatusCode.BADREQUEST, roleId, roleId);
		}
		return new ResponseEntity<>(bffCoreResponse, HttpStatus.valueOf(bffCoreResponse.getHttpStatusCode()));
	}

	/**
	 * Implementation to fetch WMS user roles
	 *
	 * @param httpReq
	 * @return ResponseEntity
	 */
	@GetMapping("/productroles")
	public ResponseEntity<BffCoreResponse> fetchProductUserRoles(HttpServletRequest httpReq) {

		BffCoreResponse bffCoreResponse;
		try {
			HttpHeaders headers = requestHelper.initHeadersFrom(httpReq);

			HttpEntity<String> request = new HttpEntity<>(headers);

			UriComponents uriComponents = productApis.rolesUrl().build();
			ResponseEntity<Object> roles =
					restTemplate.exchange(uriComponents.toUriString(), HttpMethod.GET, request, Object.class);

			bffCoreResponse = bffResponse.response(roles.getBody(),
					BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_PRODUCT_USER_ROLES,
					BffResponseCode.ACCESS_SERVICE_USER_CODE_PRODUCT_USER_ROLES, StatusCode.OK);

		} catch (Exception exp) {
			LOGGER.catching(exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_ACCESS_SERVICE_API_PRODUCT_USER_ROLES,
					BffResponseCode.ERR_ACCESS_SERVICE_USER_PRODUCT_USER_ROLES), StatusCode.BADREQUEST);
		}

		return new ResponseEntity<>(bffCoreResponse, HttpStatus.valueOf(bffCoreResponse.getHttpStatusCode()));

	}

	private ResponseEntity<BffCoreResponse> generateResponse(BffCoreResponse bffCoreResponse) {
		if (bffCoreResponse != null) {
			return new ResponseEntity<>(bffCoreResponse, HttpStatus.valueOf(bffCoreResponse.getHttpStatusCode()));
		} else {
			return new ResponseEntity<>(bffCoreResponse, HttpStatus.valueOf(StatusCode.INTERNALSERVERERROR.getValue()));
		}

	}
	
	/**
	 * Implementation to fetch WMS roles for given user id
	 *
	 * @return BffCoreResponse
	 */
	@GetMapping("/layer/{userId}")
	public ResponseEntity<BffCoreResponse> fetchUserRoleByUSerId(@PathVariable String userId ) {
		BffCoreResponse responseModel = bffAccessService.getRolesForUser(userId);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}
}