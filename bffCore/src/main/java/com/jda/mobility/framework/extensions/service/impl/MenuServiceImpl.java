package com.jda.mobility.framework.extensions.service.impl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.jda.mobility.framework.extensions.config.ProductApiSettings;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.dto.MenuDto;
import com.jda.mobility.framework.extensions.dto.MenuListDto;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.MenuMaster;
import com.jda.mobility.framework.extensions.entity.MenuPermission;
import com.jda.mobility.framework.extensions.entity.MenuType;
import com.jda.mobility.framework.extensions.entity.ProductProperty;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.MenuAction;
import com.jda.mobility.framework.extensions.model.MenuListRequest;
import com.jda.mobility.framework.extensions.model.MenuRequest;
import com.jda.mobility.framework.extensions.repository.FlowRepository;
import com.jda.mobility.framework.extensions.repository.FormRepository;
import com.jda.mobility.framework.extensions.repository.MenuMasterRepository;
import com.jda.mobility.framework.extensions.repository.MenuPermissionRepository;
import com.jda.mobility.framework.extensions.repository.MenuTypeRepository;
import com.jda.mobility.framework.extensions.repository.ProductPropertyRepository;
import com.jda.mobility.framework.extensions.service.MenuService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ChannelType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.TriggerAction;
import com.jda.mobility.framework.extensions.utils.BffCommonUtil;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.ProductAPIServiceInvoker;

/**
 * The class that implements create, update , delete and fetch menus
 */
@Service
public class MenuServiceImpl implements MenuService {

	private static final String RETREIVED_MENUS_MESSAGE = "Retrieved Menus :{}";
	private static final String MENU_FETCH_SUCCESS_MESSAGE = "All menus are fetched succesfully";
	private static final String DEFAULT_WAREHOUSE_DEBUG_MSG = "Menu size for default warehouse: {}";

	private static final Logger LOGGER = LogManager.getLogger(MenuServiceImpl.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private MenuMasterRepository menuMasterRepository;
	@Autowired
	MenuTypeRepository menuTypeRepository;
	@Autowired
	private BffResponse bffResponse;
	@Autowired
	private SessionDetails sessionDetails;
	@Autowired
	FlowRepository flowRepository;
	@Autowired
	ProductPropertyRepository productPropertyRepository;
	@Autowired
	BffCommonUtil commonUtil;
	@Autowired
	private ProductAPIServiceInvoker serviceInvoker;
	@Autowired
	FormRepository formRepo;
	@Autowired
	MenuPermissionRepository menuPermRepo;
	@Autowired
	private ProductApiSettings productApis;

	private int counter = 0;

	private int incrementCounter() {
		counter = counter + 1;
		return counter;
	}

	private void resetCounter() {
		counter = 0;
	}

	/**
	 * Create the menu and sub menu for given menu type and product
	 * 
	 * @param menuRequest
	 * @param menuType
	 * @return BffCoreResponse
	 */
	public BffCoreResponse createMenuListByType(MenuRequest menuRequest, String menuType) {
		BffCoreResponse bffCoreResponse = null;
		MenuDto menu = new MenuDto();
		String responseVal = BffAdminConstantsUtils.EMPTY_SPACES;
		try {
			ProductProperty productProperty = fetchOrCreateSecRefId(menuRequest);

			responseVal = menuRequest.getWarehouseName() != null ? menuRequest.getWarehouseName()
					: menuRequest.getFormId().toString();

			// Get the Menu Type
			Optional<MenuType> menuTyp = menuTypeRepository.findByType(menuType);
			menu.setMenuType(menuType);

			// If no valid menu type, throw error
			if (!menuTyp.isPresent()) {
				return bffResponse.errResponse(
						List.of(BffResponseCode.MENU_INVALID_INPUT, BffResponseCode.MENU_INVALID_MENU_TYPE_USER_CD),
						StatusCode.BADREQUEST);
			}
			
			// Get Existing menus
			Map<UUID, MenuMaster> existingMenuMap = fetchExistingMenus(menuRequest.getFormId(), productProperty,
					menuTyp.get());

			// Save the menus
			if (!CollectionUtils.isEmpty(menuRequest.getMenus())) {
				saveMenuByType(menuRequest.getMenus(), productProperty, menuTyp.get(), menuRequest.getFormId(),
						existingMenuMap);
			}

			// Prepare the response object
			menu.setSecondaryRefId(productProperty.getUid());
			menu.setMenus(new ArrayList<MenuListDto>());
			if (CollectionUtils.isEmpty(menuRequest.getMenus())) {
				bffCoreResponse = bffResponse.response(menu, BffResponseCode.MENU_NO_INPUT_SUCCESS_CD,
						BffResponseCode.MENU_NO_INPUT_SUCCESS_USER_CD, StatusCode.OK, null,
						responseVal);
			} else {
				if (!ObjectUtils.isEmpty(menuRequest.getMenus().get(0).getUid())) {
					bffCoreResponse = bffResponse.response(menu, BffResponseCode.MENU_UPDATE_SUCCESS_CD,
							BffResponseCode.MENU_UPDATE_SUCCESS_USER_CD, StatusCode.OK, null,
							responseVal);
				} else {
					bffCoreResponse = bffResponse.response(menu, BffResponseCode.MENU_CREATE_SUCCESS_CD,
							BffResponseCode.MENU_CREATE_SUCCESS_USER_CD, StatusCode.OK, null,
							responseVal);
				}
			}
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, responseVal);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.MENU_CREATE_DB_ERR_CD, BffResponseCode.MENU_CREATE_DB_USER_ERR_CD),
					StatusCode.INTERNALSERVERERROR, null, responseVal);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, responseVal);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.MENU_CREATE_SYS_ERR_CD, BffResponseCode.MENU_CREATE_SYS_USER_ERR_CD),
					StatusCode.BADREQUEST, null, responseVal);
		}
		resetCounter();
		return bffCoreResponse;
	}

	/**
	 *Add , update or remove menu permission for a given menu
	 * 
	 * @param menuListRequest
	 * @param parentMenuId
	 * @param menuTyp
	 * @param secondaryRefId
	 * @return
	 * @throws IOException
	 */
	private void addOrUpdateOrRemoveMenuPermissions(MenuListRequest menuListRequest, MenuMaster menuMaster,
			MenuType menuTyp) {
		menuMaster.setMenuType(menuTyp);
		// Prepare existing permission as Map
		Map<String, MenuPermission> permissionMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(menuMaster.getMenupermission())) {
			for (MenuPermission permission : menuMaster.getMenupermission()) {
				permissionMap.put(permission.getPermission(), permission);
			}
		}

		// Set the permissions
		if (null != menuListRequest.getPermissions() && !menuListRequest.getPermissions().isEmpty()) {
			for (String existingPermission : menuListRequest.getPermissions()) {
				if (!permissionMap.isEmpty() && permissionMap.get(existingPermission) != null) {
					permissionMap.get(existingPermission).setExists(true);
				} else {
					MenuPermission menuPermission = new MenuPermission();
					menuPermission.setPermission(existingPermission);
					menuPermission.setExists(true);
					menuMaster.addPermissions(menuPermission);
				}
			}

		}

		if (!CollectionUtils.isEmpty(menuMaster.getMenupermission())) {
			// Delete the removed permission entries
			for (MenuPermission removePermission : menuMaster.getMenupermission()) {
				if (!removePermission.isExists()) {
					menuMaster.removePermissions(removePermission);
				}
			}
		}

	}

	/**
	 * Validate and fetch All menu and sub menu for given product
	 * 
	 * @param bearerToken
	 * @param productName
	 * @param isDefault
	 * @param userPermission
	 * @return BffCoreResponse
	 */

	@Override
	public BffCoreResponse fetchMenus(String bearerToken, String productName, boolean isDefault,
			List<String> userPermission) {
		try {

			// If Product name is empty or null for mobile - send default warehouse menu
			if ((productName == null || productName.isEmpty())
					&& sessionDetails.getChannel().contains(ChannelType.MOBILE_RENDERER.getType())) {
				isDefault = true;
			}

			productName = setDefaultWarehouseName(productName, isDefault);
			// Get the secondaryRefId using Product name
			List<ProductProperty> productProperty = productPropertyRepository
					.findByNameAndPropValueAndIsSecondaryRefTrue(BffAdminConstantsUtils.PRODUCT_MASTER_CODE,
							productName);

			if (productProperty != null && !productProperty.isEmpty()) {

				// Get Menu(Not sub menu) for given product - Ordered by Menu type and sequence
				List<MenuMaster> menuMasterList = menuMasterRepository
						.findByProductPropertyAndParentMenuIdIsNullOrderByMenuTypeAscSequenceAsc(
								productProperty.get(0));

				BffCoreResponse response = retrieveMenus(productName, menuMasterList,
						BffAdminConstantsUtils.EMPTY_SPACES, userPermission);
				if (response != null) {
					return response;
				}
			} else {
				BffCoreResponse coreResponse = validateWarehouse(bearerToken, productName, userPermission);
				if (null != coreResponse) {
					return coreResponse;
				}
			}
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, productName);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			return bffResponse.errResponse(
					List.of(BffResponseCode.MENU_FETCH_DB_ERR_CD, BffResponseCode.MENU_FETCH_DB_UDER_ERR_CD),
					StatusCode.INTERNALSERVERERROR, null, productName);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, productName);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			return bffResponse.errResponse(
					List.of(BffResponseCode.MENU_FETCH_SYS_ERR_CD, BffResponseCode.MENU_FETCH_SYS_USER_ERR_CD),
					StatusCode.BADREQUEST, null, productName);
		}
		return getNoDataResponse();
	}

	/**Retrieve menus for given product and given type
	 * 
	 * @param productName
	 * @param menuMasterList
	 * @param menuType
	 * @param userPermission
	 * @return BffCoreResponse
	 * @throws IOException
	 */
	private BffCoreResponse retrieveMenus(String productName, List<MenuMaster> menuMasterList, String menuType,
			List<String> userPermission) throws IOException {
		// Return specified warehouse menus
		if (menuMasterList != null && !menuMasterList.isEmpty()) {
			LOGGER.log(Level.DEBUG, RETREIVED_MENUS_MESSAGE, menuMasterList.size());
			MenuDto menuDto = getSubMenusAndPopulateDto(menuMasterList, menuType, userPermission,false);
			return bffResponse.response(menuDto, BffResponseCode.MENU_FETCH_SUCCESS_CD,
					BffResponseCode.MENU_FETCH_SUCCESS_USER_CD, StatusCode.OK, null, productName);
		}
		// Mobile - If no menus from specific warehouse then fetch for default warehouse
		else if (sessionDetails.getChannel().equals(ChannelType.MOBILE_RENDERER.getType())) {
			MenuDto menuDto = fetchDefaultWarehouseMenus(userPermission);

			// If default warehouse menu are available , then prepare them
			if (menuDto != null) {
				return bffResponse.response(menuDto, BffResponseCode.MENU_FETCH_SUCCESS_CD,
						BffResponseCode.MENU_FETCH_SUCCESS_USER_CD, StatusCode.OK, null,
						productName);
			}
		}
		return null;
	}

	/**Fetch menus for default warehouse
	 * 
	 * @param userPermission
	 * @return MenuDto
	 * @throws IOException
	 */
	private MenuDto fetchDefaultWarehouseMenus(List<String> userPermission) throws IOException {
		MenuDto menuDto = null;
		// Get the secondaryRefId for default warehouse
		List<ProductProperty> productProperty = productPropertyRepository.findByNameAndPropValueAndIsSecondaryRefTrue(
				BffAdminConstantsUtils.PRODUCT_MASTER_CODE, BffAdminConstantsUtils.WAREHOUSE_DEFAULT);

		if (productProperty != null && !productProperty.isEmpty()) {
			// Get Menu(Not sub menu) for default warehouse - Ordered by Menu type and
			// sequence
			List<MenuMaster> defaultMenuList = menuMasterRepository
					.findByProductPropertyAndParentMenuIdIsNullOrderByMenuTypeAscSequenceAsc(productProperty.get(0));

			if (defaultMenuList != null && !defaultMenuList.isEmpty()) {
				LOGGER.log(Level.DEBUG, DEFAULT_WAREHOUSE_DEBUG_MSG, defaultMenuList.size());
				menuDto = getSubMenusAndPopulateDto(defaultMenuList, BffAdminConstantsUtils.EMPTY_SPACES,
						userPermission,false);
			}
		}
		return menuDto;
	}

	/**Method to prepare Response with no data
	 * 
	 * @return BffCoreResponse
	 */
	private BffCoreResponse getNoDataResponse() {
		LOGGER.log(Level.DEBUG, "No Menus for specified Product.");
		MenuDto menuDto = new MenuDto();
		List<MenuListDto> menuListDtos = new ArrayList<>();
		menuDto.setMenus(menuListDtos);
		return bffResponse.response(menuDto, BffResponseCode.MENU_NODATA_SUCCESS_CD,
				BffResponseCode.MENU_NODATA_SUCCESS_USER_CD, StatusCode.OK);
	}

	/**
	 * Fetches the submenu and populate MenuDto and MenuListDto
	 * 
	 * @param menuMasterList
	 * @param menuType
	 * @param userPermission
	 * @param isPublished
	 * @return MenuDto
	 * @throws IOException
	 */
	private MenuDto getSubMenusAndPopulateDto(List<MenuMaster> menuMasterList, String menuType,
			List<String> userPermission ,boolean isPublished) throws IOException {
		MenuDto menuDto = new MenuDto();
		List<MenuListDto> menuListDtos = new ArrayList<>();

		if (menuMasterList != null && !menuMasterList.isEmpty()) {
			for (MenuMaster rtrvMenu : menuMasterList) {
				MenuListDto menuListDto = convertToMenuListDto(rtrvMenu,isPublished);
				LOGGER.log(Level.DEBUG, "Menu Name {}", menuListDto.getMenuName());
				// Get the sub menus
				List<MenuMaster> menuMasterSubMenus = menuMasterRepository
						.findByParentMenuIdOrderBySequence(rtrvMenu.getUid());

				// By Default Menu permission are true
				boolean userPermissionForMenu = true;

				// If Mobile renderer - Check User Permission and Menu Permission are matching
				if (null != sessionDetails.getChannel()
						&& ChannelType.MOBILE_RENDERER.getType().equals(sessionDetails.getChannel())) {
					List<MenuPermission> menuPermissionList = rtrvMenu.getMenupermission();
					if (menuPermissionList != null && !menuPermissionList.isEmpty()) {
						userPermissionForMenu = commonUtil.checkMenuHasPermissionForMenu(menuPermissionList,
								userPermission);
					}
				}
				// If parent menu has permission
				if (userPermissionForMenu) {
					List<MenuListDto> subMenus = new ArrayList<>();
					// Iterate and set the sub menu to its menu
					if (menuMasterSubMenus != null && !menuMasterSubMenus.isEmpty()) {
						LOGGER.log(Level.DEBUG, "Retrived Sub menu size:{}", menuMasterSubMenus.size());
						for (MenuMaster subMenuMaster : menuMasterSubMenus) {
							boolean userPermissionForSubMenu = true;
							// If Mobile renderer - Check User Permission and Menu Permission are matching
							if (subMenuMaster != null && null != sessionDetails.getChannel()
									&& ChannelType.MOBILE_RENDERER.getType().equals(sessionDetails.getChannel())) {
								userPermissionForSubMenu = commonUtil.checkMenuHasPermissionForMenu(
										subMenuMaster.getMenupermission(), userPermission);
							}
							// If Sub menu has permissions
							if (userPermissionForSubMenu) {
								MenuListDto subMenuDto = convertToMenuListDto(subMenuMaster,isPublished);
								// Set empty submenu list for submenus
								subMenuDto.setSubMenus(new ArrayList<MenuListDto>());
								subMenus.add(subMenuDto);
							}

						}

						if (!subMenus.isEmpty()) {
							menuListDto.setSubMenus(subMenus);
							menuListDtos.add(menuListDto);
						}
					} else {

						menuListDto.setSubMenus(subMenus);
						// Set empty submenu list for submenus
						menuListDtos.add(menuListDto);
					}

				}
			}

			if (null != menuType) {
				menuDto.setMenuType(menuType);
			}

			// Set MenuDto
			menuDto.setMenus(menuListDtos);
		}
		return menuDto;
	}

	/**
	 * Convert to DTO - MenuListDto
	 * 
	 * @param rtrvMenu
	 * @param menuType
	 * @return MenuListDto
	 * @throws IOException
	 */
	private MenuListDto convertToMenuListDto(MenuMaster rtrvMenu ,boolean isPublished) throws IOException {
		MenuListDto menuListDto = new MenuListDto();

		menuListDto.setMenuType(rtrvMenu.getMenuType().getType());

		// Set Menu Name
		menuListDto.setMenuName(commonUtil.getResourceBundle(rtrvMenu.getMenuName()));

		// Get the Menu - Trigger Action using UUID
		MenuAction action = new MenuAction();
		action.setActionType(rtrvMenu.getAction());

		if (rtrvMenu.getProperties() != null
				&& !BffAdminConstantsUtils.EMPTY_SPACES.equalsIgnoreCase(rtrvMenu.getProperties())) {
			ObjectNode properties = (ObjectNode) mapper.readTree(rtrvMenu.getProperties());
			action.setProperties(properties);
		}

		// Mobile - Extract Flow Id from JSON node
		getFlowId(rtrvMenu, menuListDto, mapper,isPublished);

		menuListDto.setMenuAction(action);

		menuListDto.setIconAlignment(rtrvMenu.getIconAlignment());
		menuListDto.setIconName(rtrvMenu.getIconName());
		menuListDto.setUid(rtrvMenu.getUid());
		if (rtrvMenu.getParentMenuId() != null) {
			menuListDto.setParentMenuId(rtrvMenu.getParentMenuId());
		}
		// Set permissions
		List<String> permissionList = new ArrayList<>();
		if (null != rtrvMenu.getMenupermission()) {
			for (MenuPermission menuPerm : rtrvMenu.getMenupermission()) {
				permissionList.add(menuPerm.getPermission());
			}
		}
		menuListDto.setPermissions(permissionList);
		menuListDto.setSequence(rtrvMenu.getSequence());
		menuListDto.setUid(rtrvMenu.getUid());
		menuListDto.setShowInToolBar(rtrvMenu.isShowInToolBar());

		if (null != rtrvMenu.getHotKey()) {
			ObjectNode hotKey = (ObjectNode) mapper.readTree(rtrvMenu.getHotKey());
			menuListDto.setHotKey(hotKey);
		}

		menuListDto.setHotKeyName(rtrvMenu.getHotKeyName());
		return menuListDto;
	}

	/**Parse and Extract the flow information associated with menu 
	 * 
	 * @param rtrvMenu
	 * @param menuListDto
	 * @param mapper
	 * @param isPublished
	 * @throws JsonProcessingException
	 */
	private void getFlowId(MenuMaster rtrvMenu, MenuListDto menuListDto, ObjectMapper mapper,boolean isPublished)
			throws JsonProcessingException {
		// Mobile - Set flow Id ,Tabbed form and default form details
		if ((isPublished || (sessionDetails != null
				&& sessionDetails.getChannel().equals(BffAdminConstantsUtils.ChannelType.MOBILE_RENDERER.getType())))
				&& rtrvMenu.getAction() != null
				&& rtrvMenu.getAction().equals(TriggerAction.NAVIGATE_TO_WORKFLOW.toString())) {
			JsonNode rootNode = mapper.readTree(rtrvMenu.getProperties()); // Read flowId
			JsonNode workflow = rootNode.path(BffAdminConstantsUtils.WORKFLOW);
			JsonNode flowId = workflow.path(BffAdminConstantsUtils.FLOWID);

			if (!flowId.isMissingNode()) {
				String id = flowId.toString().replace(BffAdminConstantsUtils.DOUBLE_QUOTE,
						BffAdminConstantsUtils.EMPTY_SPACES);
				Optional<Flow> flow = flowRepository.findById(UUID.fromString(id));
				if (flow.isPresent()) {
					menuListDto.setFlowId(flow.get().getUid());
					menuListDto.setDefaultFormId(flow.get().getPublishedDefaultFormId());

					menuListDto.setTabbedForm(flow.get().isDefaultFormTabbed());
					menuListDto.setModalForm(flow.get().isDefaultModalForm());
				}
			}
		}
	}

	/**
	 * Convert to Entity MenuMaster
	 * 
	 * @param menuRequest
	 * @param productProperty
	 * @param formId
	 * @param menuMaster
	 * @return MenuMaster
	 */
	private MenuMaster convertToMenuMasterEntity(MenuListRequest menuListRequest, ProductProperty productProperty,
			UUID formId, MenuMaster menuMaster) {

		if (null != productProperty) {
			menuMaster.setProductProperty(productProperty);
		}
		menuMaster.setIconName(menuListRequest.getIconName());
		menuMaster.setIconAlignment(menuListRequest.getIconAlignment());
		menuMaster.setShowInToolBar(menuListRequest.isShowInToolBar());
		if (menuListRequest.getMenuName() != null) {
			menuMaster.setMenuName(menuListRequest.getMenuName().getRbkey());
		}
		menuMaster.setLinkedFormId(formId);
		if (menuListRequest.getHotKey() != null) {
			menuMaster.setHotKey(menuListRequest.getHotKey().toString());
		}

		if (menuListRequest.getMenuAction() != null) {
			menuMaster.setAction(menuListRequest.getMenuAction().getActionType());
			if (menuListRequest.getMenuAction().getProperties() != null) {
				menuMaster.setProperties(menuListRequest.getMenuAction().getProperties().toString());
			}
		}
		menuMaster.setSequence(incrementCounter());
		menuMaster.setHotKeyName(menuListRequest.getHotKeyName());

		return menuMaster;
	}

	/**
	 * Validate and Fetches the Menu list by its type and product
	 * @param bearerToken
	 * @param menuType
	 * @param productName
	 * @param isDefault
	 * @param userPermission
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchMenusByType(String bearerToken, String menuType, String productName, boolean isDefault,
			List<String> userPermission) {
		try {

			// If Product name is empty or null for mobile - send default warehouse menu
			if ((productName == null || productName.isEmpty())
					&& sessionDetails.getChannel().contains(ChannelType.MOBILE_RENDERER.getType())) {
				isDefault = true;
			}

			productName = setDefaultWarehouseName(productName, isDefault);

			// Get the secondaryRefId using Product name
			List<ProductProperty> productProperty = productPropertyRepository
					.findByNameAndPropValueAndIsSecondaryRefTrue(BffAdminConstantsUtils.PRODUCT_MASTER_CODE,
							productName);

			if (productProperty != null && !productProperty.isEmpty()) {
				// Get the Menu Type
				Optional<MenuType> menuTyp = menuTypeRepository.findByType(menuType);
				// If no valid menu type, throw error
				if (!menuTyp.isPresent()) {
					return bffResponse.errResponse(
							List.of(BffResponseCode.MENU_INVALID_INPUT, BffResponseCode.MENU_INVALID_MENU_TYPE_USER_CD),
							StatusCode.BADREQUEST);
				}

				// Get Menu(Not sub menu) for given product and MenuType - Ordered by sequence
				List<MenuMaster> menuMasterList = menuMasterRepository
						.findByProductPropertyAndMenuTypeAndParentMenuIdIsNullOrderBySequence(productProperty.get(0),
								menuTyp.get());
				LOGGER.log(Level.DEBUG, RETREIVED_MENUS_MESSAGE, menuMasterList.size());

				BffCoreResponse response = retrieveMenus(productName, menuMasterList, menuType, userPermission);
				if (response != null) {
					return response;
				}
			} else if (menuType != null && !menuType.equals(BffAdminConstantsUtils.MenuType.FORM_CONTEXT.getType())) {
				BffCoreResponse coreResponse = validateWarehouse(bearerToken, productName, userPermission);
				if (null != coreResponse) {
					return coreResponse;
				}
			}
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, productName);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			return bffResponse.errResponse(
					List.of(BffResponseCode.MENU_FETCH_DB_ERR_CD, BffResponseCode.MENU_FETCH_DB_UDER_ERR_CD),
					StatusCode.INTERNALSERVERERROR, null, productName);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, productName);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			return bffResponse.errResponse(
					List.of(BffResponseCode.MENU_FETCH_SYS_ERR_CD, BffResponseCode.MENU_FETCH_SYS_USER_ERR_CD),
					StatusCode.BADREQUEST, null, productName);
		}
		return getNoDataResponse();

	}

	/**Validate the given warehouse is valid or not
	 * - If not return error message 
	 * - Otherwise return warehouse menu list
	 * 
	 * @param bearerToken
	 * @param productName
	 * @param userPermission
	 * @return BffCoreResponse
	 * @throws IOException
	 */
	private BffCoreResponse validateWarehouse(String bearerToken, String productName, List<String> userPermission)
			throws IOException {
		if (!checkWarehouseList(bearerToken, productName)) {
			return bffResponse.errResponse(
					List.of(BffResponseCode.MENU_INVALID_PRODUCT_CD, BffResponseCode.MENU_INVALID_PRODUCT_USER_CD),
					StatusCode.BADREQUEST, null, productName);
		}
		// If valid warehouse and no menus then return default warehouse for mobile
		else if (sessionDetails.getChannel().contains(ChannelType.MOBILE_RENDERER.getType())) {
			MenuDto menuDto = fetchDefaultWarehouseMenus(userPermission);

			// If default warehouse menu are available , then prepare them
			if (menuDto != null) {
				return bffResponse.response(menuDto, BffResponseCode.MENU_FETCH_SUCCESS_CD,
						BffResponseCode.MENU_FETCH_SUCCESS_USER_CD, StatusCode.OK, null,
						productName);
			}
		}
		return null;
	}

	/**Fetch the list of valid warehouse from WMS API and return valid warehouse or not
	 * 
	 * @param bearerToken
	 * @param productName
	 * @return boolean
	 */
	private boolean checkWarehouseList(String bearerToken, String productName) {
		URI uri = productApis.warehousesUrl()
				.buildAndExpand(Map.of("userId", sessionDetails.getPrincipalName()))
				.toUri();
		JsonNode node = serviceInvoker.invokeApi(uri, HttpMethod.GET, null, bearerToken, sessionDetails.getPrdAuthCookie());

		ArrayNode warehouseList = (ArrayNode) node.get(BffAdminConstantsUtils.WAREHOUSES);

		List<String> warehouseIdList = new ArrayList<>();

		Iterator<JsonNode> warehouseIterator = warehouseList.iterator();
		while (warehouseIterator.hasNext()) {
			JsonNode warehouseNode = warehouseIterator.next();
			JsonNode warehouseId = warehouseNode.path(BffAdminConstantsUtils.WAREHOUSE_ID);
			warehouseIdList.add(warehouseId.toString().replace(BffAdminConstantsUtils.DOUBLE_QUOTE,
					BffAdminConstantsUtils.EMPTY_SPACES));

			if (!warehouseIdList.isEmpty() && warehouseIdList.contains(productName)) {
				return true;
			}
		}
		return false;
	}

	/**Set default warehouse configuration
	 * 
	 * @param productName
	 * @param isDefault
	 * @return String
	 */
	private String setDefaultWarehouseName(String productName, boolean isDefault) {
		// If default warehouse , set the productName
		if (isDefault) {
			productName = BffAdminConstantsUtils.WAREHOUSE_DEFAULT;
		}
		return productName;
	}

	/**
	 * Fetch the menu list by its list of types and product
	 * @param bearerToken
	 * @param menuType
	 * @param productName
	 * @param isDefault
	 * @param userPermission
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchMenusByTypes(String bearerToken, List<String> menuType, String productName,
			boolean isDefault, List<String> userPermission) {
		try {
			// If Product name is empty or null for mobile - send default warehouse menu
			if ((productName == null || productName.isEmpty())
					&& sessionDetails.getChannel().contains(ChannelType.MOBILE_RENDERER.getType())) {
				isDefault = true;
			}

			productName = setDefaultWarehouseName(productName, isDefault);

			// Get the secondaryRefId using Product name
			List<ProductProperty> productProperty = productPropertyRepository
					.findByNameAndPropValueAndIsSecondaryRefTrue(BffAdminConstantsUtils.PRODUCT_MASTER_CODE,
							productName);

			if (productProperty != null && !productProperty.isEmpty()) {

				// Get the Menu Type
				List<MenuType> menuTypList = menuTypeRepository.findByTypeIn(menuType);
				// If no valid menu type, throw error
				if (menuTypList.isEmpty()) {
					return bffResponse.errResponse(
							List.of(BffResponseCode.MENU_INVALID_INPUT, BffResponseCode.MENU_INVALID_MENU_TYPE_USER_CD),
							StatusCode.BADREQUEST);
				}

				// Get Menu(Not sub menu) for given product and MenuType - Ordered by sequence
				List<MenuMaster> menuMasterList = menuMasterRepository
						.findByProductPropertyAndMenuTypeInAndParentMenuIdIsNullOrderByMenuTypeAscSequenceAsc(
								productProperty.get(0), menuTypList);

				BffCoreResponse response = retrieveMenus(productName, menuMasterList, BffAdminConstantsUtils.ALL,
						userPermission);
				if (response != null) {
					return response;
				}
			} else {
				BffCoreResponse coreResponse = validateWarehouse(bearerToken, productName, userPermission);
				if (null != coreResponse) {
					return coreResponse;
				}
			}
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, productName);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			return bffResponse.errResponse(
					List.of(BffResponseCode.MENU_FETCH_DB_ERR_CD, BffResponseCode.MENU_FETCH_DB_UDER_ERR_CD),
					StatusCode.INTERNALSERVERERROR, null, productName);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, productName);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			return bffResponse.errResponse(
					List.of(BffResponseCode.MENU_FETCH_SYS_ERR_CD, BffResponseCode.MENU_FETCH_SYS_USER_ERR_CD),
					StatusCode.BADREQUEST, null, productName);
		}
		return getNoDataResponse();
	}

	/**
	 *Fetch the list of menu for given form (Form context menus)
	 * @param formId
	 * @param userPermission
	 * @param isPublished
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchMenusByFormId(UUID formId, List<String> userPermission,boolean isPublished) {
		BffCoreResponse bffCoreResponse = null;
		try {
			List<MenuMaster> menuMasterList = menuMasterRepository.findByLinkedFormId(formId);

			if (menuMasterList != null && !menuMasterList.isEmpty()) {
				LOGGER.log(Level.DEBUG, RETREIVED_MENUS_MESSAGE, menuMasterList.size());
				MenuDto menuDto = getSubMenusAndPopulateDto(menuMasterList, BffAdminConstantsUtils.EMPTY_SPACES,
						userPermission,isPublished);
				LOGGER.log(Level.DEBUG, MENU_FETCH_SUCCESS_MESSAGE);
				bffCoreResponse = bffResponse.response(menuDto, BffResponseCode.MENU_FETCH_SUCCESS_CD,
						BffResponseCode.MENU_FETCH_SUCCESS_USER_CD, StatusCode.OK, null,
						formId.toString());
			} else {
				bffCoreResponse = getNoDataResponse();
			}
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, formId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.MENU_FETCH_DB_ERR_CD, BffResponseCode.MENU_FETCH_DB_UDER_ERR_CD),
					StatusCode.INTERNALSERVERERROR, null, formId.toString());
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, formId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.MENU_FETCH_SYS_ERR_CD, BffResponseCode.MENU_FETCH_SYS_USER_ERR_CD),
					StatusCode.BADREQUEST, null, formId.toString());
		}
		return bffCoreResponse;
	}

	/**Fetch already existing menu for given warehouse and prepare them as Map
	 * - To decide it going for update or delete
	 * @param formId
	 * @param productProperty
	 * @param menuTyp
	 * @return Map&lt;UUID, MenuMaster&gt;
	 */
	private Map<UUID, MenuMaster> fetchExistingMenus(UUID formId, ProductProperty productProperty, MenuType menuTyp) {
		List<MenuMaster> menuMasterExists = null;

		// Fetch By Menu type and Form id
		if (null != menuTyp.getType() && menuTyp.getType().equals( BffAdminConstantsUtils.MenuType.FORM_CONTEXT.getType())
				&& formId != null) {
			// Get already exists records for given form and Menu type
			menuMasterExists = menuMasterRepository.findByMenuTypeAndLinkedFormId(menuTyp, formId);

		}
		// Fetch By Menu type , warehouse name
		else {
			menuMasterExists = menuMasterRepository.findByProductPropertyAndMenuType(productProperty, menuTyp);
		}

		Map<UUID, MenuMaster> menuMap = new HashMap<>();
		for (MenuMaster menu : menuMasterExists) {
			menuMap.put(menu.getUid(), menu);
		}

		return menuMap;

	}

	/**Create or Fetch secondayRefId
	 * 
	 * @param menuRequest
	 * @return ProductProperty
	 */
	private ProductProperty fetchOrCreateSecRefId(MenuRequest menuRequest) {

		// If default warehouse , then set the warehouse name accordingly
		if (menuRequest.isDefaultWarehouse()) {
			menuRequest.setWarehouseName(BffAdminConstantsUtils.WAREHOUSE_DEFAULT);
		}
		return commonUtil.createdOrGetSecondaryRefId(menuRequest.getWarehouseName());
	}

	/**Save the Menu details by its type
	 * 
	 * @param menuList
	 * @param productProperty
	 * @param menuTyp
	 * @param formId
	 * @param existingMenuMap
	 */
	private void saveMenuByType(List<MenuListRequest> menuList, ProductProperty productProperty, MenuType menuTyp,
			UUID formId, Map<UUID, MenuMaster> existingMenuMap) {
		for (MenuListRequest menuListRequest : menuList) {

			MenuMaster menuMaster = null;
			if (menuListRequest.getUid() != null && !existingMenuMap.isEmpty()) {
				menuMaster = existingMenuMap.get(menuListRequest.getUid());
			} else {
				menuMaster = new MenuMaster();
			}

			// Convert to Entity Object
			convertToMenuMasterEntity(menuListRequest, productProperty, formId, menuMaster);

			addOrUpdateOrRemoveMenuPermissions(menuListRequest, menuMaster, menuTyp);

			menuMaster = menuMasterRepository.save(menuMaster);
			LOGGER.log(Level.DEBUG, "Menu inserted id: {} ", menuMaster.getUid());
			saveSubMenuByType(productProperty, menuTyp, menuListRequest.getSubMenus(), menuMaster.getUid(), formId,
					existingMenuMap);

		}
	}

	/**Save the sub menu details by its type
	 * 
	 * @param productProperty
	 * @param menuTyp
	 * @param subMenuList
	 * @param parentMenuId
	 * @param formId
	 * @param existingMenuMap
	 */
	private void saveSubMenuByType(ProductProperty productProperty, MenuType menuTyp, List<MenuListRequest> subMenuList,
			UUID parentMenuId, UUID formId, Map<UUID, MenuMaster> existingMenuMap) {
		// Save the submenu
		if (null != subMenuList && !subMenuList.isEmpty()) {
			for (MenuListRequest subMenu : subMenuList) {

				MenuMaster subMenuMaster = null;
				if (subMenu.getUid() != null) {
					subMenuMaster = existingMenuMap.get(subMenu.getUid());
				} else {
					subMenuMaster = new MenuMaster();
				}

				// Convert to Entity Object
				convertToMenuMasterEntity(subMenu, productProperty, formId, subMenuMaster);
				subMenuMaster.setParentMenuId(parentMenuId);
				addOrUpdateOrRemoveMenuPermissions(subMenu, subMenuMaster, menuTyp);

				subMenuMaster = menuMasterRepository.save(subMenuMaster);
				LOGGER.log(Level.DEBUG, "SubMenu inserted id: {} ", subMenuMaster.getUid());
			}
		}
	}

	/**
	 * Delete the menu by its menuId
	 * @param menuId
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse deleteMenuById(UUID menuId) {
		BffCoreResponse bffCoreResponse = null;
		MenuDto menu = new MenuDto();
		try {

			// Check for parent menu
			Optional<MenuMaster> menuMaster = menuMasterRepository.findById(menuId);

			if (menuMaster.isPresent()) {
				// Delete the parent menu
				menuMasterRepository.deleteById(menuId);

				// Check for sub menu
				List<MenuMaster> subMenus = menuMasterRepository.findByParentMenuIdOrderBySequence(menuId);

				// Delete the sub menus
				if (subMenus != null && !subMenus.isEmpty()) {
					menuMasterRepository.deleteAll(subMenus);
				}

				menu.setMenus(new ArrayList<>());
				bffCoreResponse = bffResponse.response(menu, BffResponseCode.MENU_DELETE_SUCCESS_CD,
						BffResponseCode.MENU_DELETE_SUCCESS_USER_CD, StatusCode.OK, null,
						menuId.toString());
			}

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, menuId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.MENU_DELETE_DB_ERR_CD, BffResponseCode.MENU_DELETE_DB_USER_ERR_CD),
					StatusCode.INTERNALSERVERERROR, null, menuId.toString());
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, menuId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.MENU_DELETE_SYS_ERR_CD, BffResponseCode.MENU_DELETE_SYS_USER_ERR_CD),
					StatusCode.BADREQUEST, null, menuId.toString());
		}
		return bffCoreResponse;
	}

}