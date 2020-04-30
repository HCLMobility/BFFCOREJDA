package com.jda.mobility.framework.extensions.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.MenuRequest;
import com.jda.mobility.framework.extensions.security.UserPrincipal;
import com.jda.mobility.framework.extensions.service.MenuService;
import com.jda.mobility.framework.extensions.util.RequestHelper;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ChannelType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The class that implements create, update , delete and fetch menus
 */

@RestController
@RequestMapping("/api/menu/v1")
public class MenuController {

	private final MenuService menuService;
	private final RequestHelper requestHelper;

	public MenuController(MenuService menuService, RequestHelper requestHelper) {
		this.menuService = menuService;
		this.requestHelper = requestHelper;
	}

	/**Create list of menus for given type
	 * 
	 * @param menuRequest
	 * @param menuType
	 * @param formId
	 * @return
	 */
	@PutMapping("/type")
	public ResponseEntity<BffCoreResponse> createMenuTreeByMenuType(@Valid @RequestBody MenuRequest menuRequest,
			@RequestParam(required = true) String menuType, @RequestParam(required = false) UUID formId) {
		BffCoreResponse responseModel = menuService.createMenuListByType(menuRequest, menuType);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**Delete the menu by MenuId
	 * @param menuId
	 * @return
	 */
	@DeleteMapping("/delete/{menuId}")
	public ResponseEntity<BffCoreResponse> deleteMenu(@Valid @PathVariable(required = true) UUID menuId) {
		BffCoreResponse responseModel = menuService.deleteMenuById(menuId);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**Fetches list of menus
	 * 
	 * @param productName
	 * @param isDefault
	 * @param httpReq
	 * @return
	 */
	@GetMapping("/ref")
	public ResponseEntity<BffCoreResponse> fetchAllMenuByRefId(@RequestParam(required = false) String productName,
			@RequestParam(required = false) boolean isDefault,
			HttpServletRequest httpReq) {
		String bearerToken = requestHelper.oidcToken(httpReq);

		BffCoreResponse responseModel = menuService.fetchMenus(bearerToken, productName, isDefault,getUserPermissions());
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	private List<String> getUserPermissions() {
		List<String> permissionIds = new ArrayList<>();
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() != null) {
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			if (ChannelType.MOBILE_RENDERER.equals(userPrincipal.getChannel())) {
				permissionIds =  userPrincipal.getPermissionIds();
			}
		}
		return permissionIds;
	}

	/**Fetches list of menus for given type and warehouse(product)
	 * 
	 * @param productName
	 * @param isDefault
	 * @param menuType
	 * @param httpReq
	 * @return
	 */
	@GetMapping("/ref/type")
	public ResponseEntity<BffCoreResponse> fetchMenusByRefIdAndMenuType(
			@RequestParam(required = false) String productName, @RequestParam(required = false) boolean isDefault,
			@RequestParam(required = true) String menuType,
			HttpServletRequest httpReq) {
		String bearerToken = requestHelper.oidcToken(httpReq);
		BffCoreResponse responseModel = menuService.fetchMenusByType(bearerToken, menuType, productName, isDefault,getUserPermissions());

		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	
	/**Fetches list of menus for given list of type and warehouse(product)
	 * 
	 * @param productName
	 * @param isDefault
	 * @param menuTypes
	 * @param httpReq
	 * @return
	 */
	@GetMapping("/ref/types")
	public ResponseEntity<BffCoreResponse> fetchMenusByRefIdAndMenuTypes(
			@RequestParam(required = false) String productName, @RequestParam(required = false) boolean isDefault,
			@RequestParam(required = true) List<String> menuTypes,
			HttpServletRequest httpReq) {
		String bearerToken = requestHelper.oidcToken(httpReq);
		BffCoreResponse responseModel = menuService.fetchMenusByTypes(bearerToken, menuTypes, productName, isDefault,getUserPermissions());

		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}


	/**Fetches list of menus for given form (Form context menus)
	 * 
	 * @param formId
	 * @return
	 */
	@GetMapping("/form/{formId}")
	public ResponseEntity<BffCoreResponse> fetchMenusByFormId(@Valid @PathVariable("formId") UUID formId) {
		BffCoreResponse responseModel = menuService.fetchMenusByFormId(formId,getUserPermissions(),false);

		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

}