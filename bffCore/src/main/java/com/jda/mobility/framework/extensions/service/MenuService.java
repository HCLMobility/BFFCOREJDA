package com.jda.mobility.framework.extensions.service;

import java.util.List;
import java.util.UUID;

import org.springframework.retry.annotation.Retryable;

import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.MenuRequest;

/**
 * CRUD operation for Menus
 * HCL Technologies Ltd.
 */
@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface MenuService {

	
	/**Fetch menu list for a product of all menu type
	 * @param bearerToken The ID token from OAuth
	 * @param productName The product name
	 * @param isDefault Whether default or specific warehouse
	 * @param userPermission The user permission list associated to the menu
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse fetchMenus(String bearerToken, String productName,boolean isDefault,List<String> userPermission);

	/**Fetch menu list for a product of given menu type
	 * @param bearerToken The ID token from OAuth
	 * @param menuType The menu type - GLOBAL, CONTEXT or MAIN
	 * @param productName The product name
	 * @param isDefault Whether default or specific warehouse
	 * @param userPermission The user permission list associated to the menu
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse fetchMenusByType(String bearerToken, String menuType,String productName,boolean isDefault,List<String> userPermission);
	
	
	/**Fetch menu list for a product of given Menu types
	 * @param bearerToken The ID token from OAuth
	 * @param menuType The list of menu types - GLOBAL, CONTEXT and MAIN
	 * @param productName The product name
	 * @param isDefault Whether default or specific warehouse
	 * @param userPermission The user permission list associated to the menu
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse fetchMenusByTypes(String bearerToken, List<String> menuType,String productName,boolean isDefault,List<String> userPermission);

	
	/**Fetch menu list for a product of given Menu types
	 * @param menuRequest The Menu request with all the details to create a menu
	 * @param menuType The menu type - GLOBAL, CONTEXT or MAIN 
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse createMenuListByType(MenuRequest menuRequest, String menuType);

	/**Fetch the Menu by form id
	 * @param formId The form id for form context menu
	 * @param userPermission The user permission list associated to the menu
	 * @param isPublished Whether form published or not
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse fetchMenusByFormId(UUID formId,List<String> userPermission,boolean isPublished);

	/**Fetch the Menu by form id
	 * @param menuId The ID of the menu that needs to be deleted
	 * @return BffCoreResponse The success/error response object
	 */	
	BffCoreResponse deleteMenuById(UUID menuId);


}