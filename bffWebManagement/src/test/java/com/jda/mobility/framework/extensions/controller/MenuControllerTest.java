package com.jda.mobility.framework.extensions.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.config.AppProperties;
import com.jda.mobility.framework.extensions.dto.MenuDto;
import com.jda.mobility.framework.extensions.dto.MenuListDto;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.MenuAction;
import com.jda.mobility.framework.extensions.model.MenuListRequest;
import com.jda.mobility.framework.extensions.model.MenuRequest;
import com.jda.mobility.framework.extensions.security.UserPrincipal;
import com.jda.mobility.framework.extensions.service.MenuService;
import com.jda.mobility.framework.extensions.util.RequestHelper;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.TriggerAction;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class MenuControllerTest extends AbstractBaseControllerTest {

	private static final String MENU_URL = "/api/menu/v1/";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private RequestHelper requestHelper;
	
	@Mock
	private MenuService menuService;

	@MockBean
	private AppProperties appProperties;

	@Before
	public void beforeEach() {
		MenuController controller = new MenuController(menuService, requestHelper);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		when(appProperties.isBasicAuthEnabled()).thenReturn(true);
		when(appProperties.isOidcEnabled()).thenReturn(false);
	}

	@Test
	public void testCreateMenuTreeByMenuType() throws Exception {
		UUID formId = UUID.randomUUID();

		when(menuService.createMenuListByType( Mockito.any(),Mockito.eq("MAIN"))).thenReturn(buildResponse(createMenuDto()));

		mockMvc.perform( MockMvcRequestBuilders
				.put(MENU_URL + "type")
				.param("menuType", "MAIN")
				.param("formId", formId.toString())
				.content(asJsonString(getMenuListRequest()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.menuType").value("MAIN"));
	}

	@Test
	public void testDeleteMenu() throws Exception {
		UUID menuId = UUID.randomUUID();

		when(menuService.deleteMenuById(menuId)).thenReturn(buildResponse(createMenuDto()));

		mockMvc.perform(MockMvcRequestBuilders
				.delete(MENU_URL + "delete/" + menuId)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.menuType").value("MAIN"));
	}

	
	@Test
	public void testFetchAllMenuByRefId() throws Exception {
		UserPrincipal principal = (UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		when(menuService.fetchMenus(null,"WMD1",false,principal.getPermissionIds())).thenReturn(buildResponse(createMenuDto()));

		mockMvc.perform(MockMvcRequestBuilders
				.get(MENU_URL + "ref")
				.param("productName", "WMD1")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.menuType").value("MAIN"));
	}

	@Test
	public void testFetchMenusByRefIdAndMenuType() throws Exception {
		UserPrincipal principal = (UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		when(menuService.fetchMenusByType(null,"MAIN", "WMD1",false,principal.getPermissionIds())).thenReturn(buildResponse(createMenuDto()));

		mockMvc.perform(MockMvcRequestBuilders
				.get(MENU_URL + "ref/type")
				.param("productName", "WMD1")
				.param("menuType", "MAIN")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.menuType").value("MAIN"));
	}

	@Test
	public void testFetchMenusByRefIdAndMenuTypes() throws Exception {
		UserPrincipal principal = (UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		when(menuService.fetchMenusByTypes(null,Arrays.asList("MAIN"), "WMD1",false,principal.getPermissionIds())).thenReturn(buildResponse(createMenuDto()));

		mockMvc.perform(MockMvcRequestBuilders
				.get(MENU_URL + "ref/types")
				.param("productName", "WMD1")
				.param("menuTypes", "MAIN")
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.menuType").value("MAIN"));
	}

	@Test
	public void testFetchMenusByFormId() throws Exception {
		UserPrincipal principal = (UserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UUID formId = UUID.randomUUID();

		when(menuService.fetchMenusByFormId(formId,principal.getPermissionIds(),false)).thenReturn(buildResponse(createMenuDto()));

		mockMvc.perform(MockMvcRequestBuilders
				.get(MENU_URL + "form/" + formId)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.menuType").value("MAIN"));
	}

	

	private MenuRequest getMenuListRequest() {
		MenuRequest request = new MenuRequest();
		List<String> perm = new ArrayList<String>();
		perm.add("Read");
		List<MenuListRequest> menuListRequestList = new ArrayList<MenuListRequest>();
		MenuListRequest menuListRequest = new MenuListRequest();
		MenuAction menuAction = new MenuAction();
		menuListRequest.setIconName("ERROR");
		menuListRequest.setIconAlignment("left");
		menuListRequest.setMenuType("MAIN");
		menuListRequest.setPermissions(perm);
		menuListRequest.setShowInToolBar(false);
		menuAction.setActionType(TriggerAction.NAVIGATE_TO_WORKFLOW.toString());
		menuListRequest.setMenuAction(menuAction);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode root = mapper.createObjectNode();
		menuAction.setProperties(root);
		menuListRequest.setMenuAction(menuAction);
		List<MenuListRequest> menuList = new ArrayList<MenuListRequest>();
		MenuListRequest subMenu = new MenuListRequest();
		subMenu.setIconName("Image");
		subMenu.setIconAlignment("left");
		subMenu.setMenuType("MAIN");
		subMenu.setPermissions(perm);
		subMenu.setShowInToolBar(false);
		subMenu.setMenuAction(menuAction);
		menuList.add(subMenu);
		menuListRequest.setSubMenus(menuList);
		menuListRequestList.add(menuListRequest);
		request.setMenus(menuListRequestList);
		menuListRequest.setHotKey(mapper.createObjectNode());
		return request;
	}

	private MenuDto createMenuDto() {
		MenuDto menuDto = new MenuDto();
		menuDto.setMenuType("MAIN");
		MenuListDto menuListDto = new MenuListDto();
		List<MenuListDto> menuListDtos = Arrays.asList(menuListDto);
		menuDto.setMenus(menuListDtos);
		return menuDto;
	}

	private String asJsonString(final Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
	}

	private <T> BffCoreResponse buildResponse(T data) {
		return BffUtils.buildResponse(data, BffResponseCode.MENU_CREATE_SUCCESS_CD.getCode(), "message",
				"detailMessage", StatusCode.OK.getValue());
	}
}

