
/**
 * 
 */

package com.jda.mobility.framework.extensions.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.dto.TranslationRequest;
import com.jda.mobility.framework.extensions.entity.AppConfigDetail;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.entity.Events;
import com.jda.mobility.framework.extensions.entity.ExtendedFlowBase;
import com.jda.mobility.framework.extensions.entity.Field;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.FlowPermission;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.entity.ProductProperty;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.entity.UserRole;
import com.jda.mobility.framework.extensions.entity.projection.FlowLiteDto;
import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.exception.DataBaseException;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.CloneRequest;
import com.jda.mobility.framework.extensions.model.FlowRequest;
import com.jda.mobility.framework.extensions.model.FormData;
import com.jda.mobility.framework.extensions.model.FormProperties;
import com.jda.mobility.framework.extensions.model.MenuAction;
import com.jda.mobility.framework.extensions.model.MenuListRequest;
import com.jda.mobility.framework.extensions.model.MenuRequest;
import com.jda.mobility.framework.extensions.model.UserPermissionRequest;
import com.jda.mobility.framework.extensions.repository.ApiMasterRepository;
import com.jda.mobility.framework.extensions.repository.ApiRegistryRepository;
import com.jda.mobility.framework.extensions.repository.AppConfigMasterRepository;
import com.jda.mobility.framework.extensions.repository.CustomComponentMasterRepository;
import com.jda.mobility.framework.extensions.repository.ExtendedFlowBaseRepository;
import com.jda.mobility.framework.extensions.repository.FlowPermissionRepository;
import com.jda.mobility.framework.extensions.repository.FlowRepository;
import com.jda.mobility.framework.extensions.repository.FormRepository;
import com.jda.mobility.framework.extensions.repository.ProductConfigRepository;
import com.jda.mobility.framework.extensions.repository.ProductMasterRepository;
import com.jda.mobility.framework.extensions.repository.ProductPropertyRepository;
import com.jda.mobility.framework.extensions.repository.UserRoleRepository;
import com.jda.mobility.framework.extensions.service.ProductPrepareService;
import com.jda.mobility.framework.extensions.transformation.FormTransformation;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ActionType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.AppCfgRequestType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.CloneType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DeleteType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DisableType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.FlowType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.TriggerAction;
import com.jda.mobility.framework.extensions.utils.BffCommonUtil;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.FormDependencyUtil;

/**
 * The class FlowServiceImplTest.java
 * 
 * @author HCL Technologies Ltd.
 */

@RunWith(SpringJUnit4ClassRunner.class)
public class FlowServiceImplTest extends AbstractPrepareTest {

	/** The field flowserviceImpl of type FlowServiceImpl */

	@InjectMocks
	private FlowServiceImpl flowserviceImpl;

	/** The field flowRepository of type FlowRepository */

	@Mock
	private FlowRepository flowRepository;

	/** The field productConfigRepository of type ProductConfigRepository */

	@Mock
	private ProductConfigRepository productConfigRepository;

	@Mock
	private ProductPrepareService productPrepareService;

	@Mock
	private ApiMasterRepository apiMasterRepository;

	@Mock
	private UserRoleRepository userRoleRepository;

	@Mock
	private ProductPropertyRepository productPropertyRepo;

	@Mock
	private ProductMasterRepository productMasterRepo;

	@Mock
	private ApiRegistryRepository apiRegistryRepository;

	@Mock
	private CustomComponentMasterRepository customComponentMasterRepository;

	@Mock
	BffCommonUtil commonUtil;

	@Mock
	private FormTransformation formTransformation;

	@Mock
	private FormRepository formRepo;

	@Mock
	private ExtendedFlowBaseRepository extendedFlowBaseRepo;

	@Mock
	private AppConfigMasterRepository appConfigRepository;

	@Mock
	private FormServiceImpl formServiceImpl;

	@Mock
	private BffCommonUtil bffCommonUtil;

	@Mock
	private FlowPermissionRepository flowPermissionRepository;
	@Mock
	private FormDependencyUtil dependencyUtil;

	/**
	 * Test method for createFlow
	 */

	@Test
	public void testCreateFlow() {
		when(flowRepository.findByNameAndVersion(Mockito.any(), Mockito.anyLong())).thenReturn(new ArrayList<Flow>());
		when(flowRepository.save(Mockito.any())).thenReturn(getFlow());
		when(productConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(new ProductConfig()));
		BffCoreResponse response = flowserviceImpl.createFlow(getFlowRo());
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_CREATE_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for createFlow
	 */

	@Test
	public void testCreateFlow_ExistingFlow() {
		List<Flow> flowList = new ArrayList<Flow>();
		flowList.add(new Flow());
		when(flowRepository.findByNameAndVersion(Mockito.any(), Mockito.anyLong())).thenReturn(flowList);
		when(flowRepository.save(Mockito.any())).thenReturn(getFlow());
		when(productConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(new ProductConfig()));
		BffCoreResponse response = flowserviceImpl.createFlow(getFlowRo());
		assertEquals(BffResponseCode.ERR_FLOW_API_CREATE_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.CONFLICT.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for createFlow for BffCoreException
	 */

	@Test
	public void testCreateFlow_Exception() {
		when(productConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(new ProductConfig()));
		getFlowRo().setFlowId(null);
		BffCoreResponse response = flowserviceImpl.createFlow(getFlowRo());
		assertEquals(BffResponseCode.ERR_FLOW_API_CREATE_FLOW_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for createFlow for DataBaseException
	 */

	@Test
	public void testCreateFlow_DataBaseException() {
		when(flowRepository.save(Mockito.any())).thenThrow(new DataBaseException("Flow save failed"));
		when(productConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(new ProductConfig()));
		BffCoreResponse response = flowserviceImpl.createFlow(getFlowRo());
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_CREATE_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for modifyFlow
	 * 
	 * @throws IOException
	 */

	@Test
	public void testModifyFlow() throws IOException {
		FormData formdata = new FormData();
		FormProperties formProperties = new FormProperties();
		formProperties.setMenus(getMenuListRequest());
		formdata.setFormProperties(formProperties);
		DisableType identifier = DisableType.CONFIRM_DISABLE;
		List<Flow> flowList = new ArrayList<>();
		Flow nonUniqueFlow = new Flow();
		nonUniqueFlow.setDisabled(true);
		nonUniqueFlow.setName(getFlowRo().getName());
		flowList.add(nonUniqueFlow);
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		RoleMaster rm = new RoleMaster();
		rm.setLevel(0);
		productConfig.setRoleMaster(rm);
		when(formTransformation.createFormData(Mockito.any())).thenReturn(formdata);
		when(formRepo.save(Mockito.any())).thenReturn(getForm());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(getFlow()));
		when(productConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(productConfig));
		when(flowRepository.findByNameAndVersion(Mockito.anyString(), Mockito.anyLong())).thenReturn(flowList);
		BffCoreResponse response1 = flowserviceImpl.modifyFlow(getFlowRo(), ActionType.CONFIRM_PUBLISH, identifier,
				Arrays.asList("foot"));
		assertEquals(BffResponseCode.ERR_FLOW_API_MODIFY_FLOW.getCode(), response1.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response1.getHttpStatusCode());
		when(flowRepository.findByNameAndVersion(Mockito.anyString(), Mockito.anyLong())).thenReturn(null);
		AppConfigMaster appConfig = new AppConfigMaster();
		when(appConfigRepository.findByConfigNameAndConfigType(Mockito.any(), Mockito.any())).thenReturn(appConfig);
		when(appConfigRepository.findByConfigNameAndConfigType(Mockito.any(), Mockito.any())).thenReturn(appConfig);
		BffCoreResponse response2 = flowserviceImpl.modifyFlow(getFlowRo(), ActionType.CONFIRM_PUBLISH,
				DisableType.CHECK_DISABLE, Arrays.asList("foot"));
		assertEquals(BffResponseCode.ERR_FLOW_API_MODIFY_FLOW_EXCEPTION.getCode(), response2.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response2.getHttpStatusCode());
		when(flowRepository.save(Mockito.any())).thenReturn(getFlow());
		BffCoreResponse response3 = flowserviceImpl.modifyFlow(getFlowRo(), ActionType.CONFIRM_PUBLISH, identifier,
				Arrays.asList("foot"));
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_FLOW_PUBLISH.getCode(), response3.getCode());
		assertEquals(StatusCode.OK.getValue(), response3.getHttpStatusCode());
		when(flowRepository.save(Mockito.any())).thenReturn(getFlow());
		flowserviceImpl.modifyFlow(getFlowRo(), ActionType.CHECK_PUBLISH, identifier, Arrays.asList("foot"));
		flowserviceImpl.modifyFlow(getFlowRo(), ActionType.SAVE, identifier, Arrays.asList("foot"));

	}

	@Test
	public void testModifyFlowNotPresent() {
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.empty());
		BffCoreResponse response = flowserviceImpl.modifyFlow(getFlowRo(), ActionType.SAVE, DisableType.CHECK_DISABLE,
				Arrays.asList("foot"));
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testModifyFlowDisableDefaultFlow() {
		FlowRequest flowro = new FlowRequest();
		flowro.setFlowId(UUID.fromString("0fb377f1-e357-f24a-a85e-e31bc232cbef"));
		flowro.setName("pick");
		flowro.setDisabled(true);
		Flow flow = new Flow();
		flow.setUid(UUID.fromString("0fb377f1-e357-f24a-a85e-e31bc232cbef"));
		flow.setName("pick");
		when(appConfigRepository.findByConfigNameAndConfigType(BffAdminConstantsUtils.DEFAULT_FLOW_KEY,
				AppCfgRequestType.APPLICATION.getType())).thenReturn(getDefFlowAppConfig());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(flow));
		BffCoreResponse response = flowserviceImpl.modifyFlow(flowro, ActionType.CHECK_PUBLISH,
				DisableType.CHECK_DISABLE, Arrays.asList("foot"));
		assertEquals(BffResponseCode.ERR_FLOW_API_CHECK_DEFAULT_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testModifyFlowDisableHomeFlow() {
		FlowRequest flowro = new FlowRequest();
		flowro.setFlowId(UUID.fromString("0fb377f1-e357-f24a-a85e-e31bc232cbef"));
		flowro.setName("pick");
		flowro.setDisabled(true);
		Flow flow = new Flow();
		flow.setUid(UUID.fromString("0fb377f1-e357-f24a-a85e-e31bc232cbef"));
		flow.setName("pick");
		when(appConfigRepository.findByConfigNameAndConfigType(BffAdminConstantsUtils.HOME_FLOW_KEY,
				AppCfgRequestType.APPLICATION.getType())).thenReturn(getHomeFlowAppConfig());
		when(appConfigRepository.findByConfigNameAndConfigType(BffAdminConstantsUtils.DEFAULT_FLOW_KEY,
				AppCfgRequestType.APPLICATION.getType())).thenReturn(new AppConfigMaster());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(flow));
		BffCoreResponse response = flowserviceImpl.modifyFlow(flowro, ActionType.CHECK_PUBLISH,
				DisableType.CHECK_DISABLE, Arrays.asList("foot"));
		assertEquals(BffResponseCode.ERR_FLOW_API_CHECK_CURRENT_FLOW_HOME.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testModifyFlowNOtDsbled() throws IOException {
		FormData formdata = new FormData();
		FormProperties formProperties = new FormProperties();
		formProperties.setMenus(getMenuListRequest());
		formdata.setFormProperties(formProperties);
		when(formTransformation.createFormData(Mockito.any())).thenReturn(formdata);
		when(formRepo.save(Mockito.any())).thenReturn(getForm());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(getFlow()));
		when(flowRepository.save(Mockito.any())).thenReturn(getFlow());
		BffCoreResponse response = flowserviceImpl.modifyFlow(getFlowRo1(), ActionType.CONFIRM_PUBLISH,
				DisableType.CHECK_DISABLE, Arrays.asList("foot"));
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_FLOW_PUBLISH.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for modifyFlow for BffCoreException
	 */

	@Test
	public void testModifyFlowException() {
		when(flowRepository.findById(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		getFlowRo().setFlowId(null);
		BffCoreResponse response = flowserviceImpl.modifyFlow(getFlowRo(), ActionType.SAVE, DisableType.CHECK_DISABLE,
				Arrays.asList("foot"));
		assertEquals(BffResponseCode.ERR_FLOW_API_MODIFY_FLOW_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for modifyFlow for DataBaseException
	 */

	@Test
	public void testModifyFlowDataBaseException() {
		when(flowRepository.findById(Mockito.any())).thenThrow(new DataBaseException("Flow retrieval failed"));
		BffCoreResponse response = flowserviceImpl.modifyFlow(getFlowRo(), ActionType.SAVE, DisableType.CHECK_DISABLE,
				Arrays.asList("foot"));
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_MODIFY_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());

	}

	/**
	 * Test method for getFlowById
	 */

	@Test
	public void testGetFlowById() {
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		RoleMaster rm = new RoleMaster();
		rm.setLevel(0);
		productConfig.setRoleMaster(rm);
		when(sessionDetails.getChannel()).thenReturn("ADMIN_UI");
		when(productConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(productConfig));
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(getFlow()));
		BffCoreResponse response = flowserviceImpl.getFlowById(UUID.randomUUID());
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_FETCH_FLOW_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetFlowByIdDefFormNotPresent() {
		String flowId = "0fb377f1-e357-f24a-a85e-e31bc232cbef";
		Flow flow = new Flow();
		flow.setUid(UUID.fromString(flowId));
		flow.setPublished(true);
		flow.setDefaultFormId(UUID.randomUUID());
		flow.setPublishedFlow(true);
		when(formRepo.findById(Mockito.any())).thenReturn(Optional.empty());
		when(sessionDetails.getChannel()).thenReturn("MOBILE_RENDERER");
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(flow));
		BffCoreResponse response = flowserviceImpl.getFlowById(UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FORM_NOT_FOUND.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetFlowByIdFlowNotPresent() {
		when(flowRepository.findById(UUID.randomUUID())).thenReturn(Optional.empty());
		BffCoreResponse response = flowserviceImpl.getFlowById(UUID.randomUUID());
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetFlowByIdPublish() {
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		RoleMaster rm = new RoleMaster();
		rm.setLevel(0);
		productConfig.setRoleMaster(rm);
		when(productConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(productConfig));
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(getFlow()));
		BffCoreResponse response = flowserviceImpl.getFlowById(UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FLOW_UPUBLISH_CD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getFlowById
	 */

	@Test
	public void testGetFlowByIdIsDisabled() {
		Flow flow = new Flow();
		flow.setPublished(true);
		flow.setUid(UUID.randomUUID());
		flow.setDisabled(true);
		flow.setPublishedFlow(true);
		flow.setDefaultFormId(UUID.randomUUID());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(flow));
		BffCoreResponse response = flowserviceImpl.getFlowById(UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FLOW_DISABLE_CD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getFlowById
	 */

	@Test
	public void testGetFlowByIdDefaultFormIdNull() {
		Flow flow = new Flow();
		flow.setPublished(true);
		flow.setUid(UUID.randomUUID());
		flow.setDisabled(false);
		flow.setPublishedFlow(true);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(flow));
		BffCoreResponse response = flowserviceImpl.getFlowById(UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FORM_DEFAULT_FORM_NOT_FOUND.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getFlowById
	 */

	@Test
	public void testGetFlowByIdDefaultFormId() {
		Flow flow = new Flow();
		flow.setPublished(true);
		flow.setUid(UUID.randomUUID());
		flow.setDisabled(false);
		flow.setPublishedFlow(true);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(flow));
		BffCoreResponse response = flowserviceImpl.getFlowById(UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FORM_DEFAULT_FORM_NOT_FOUND.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getFlowById
	 */

	@Test
	public void testGetFlowByIdDefaultFormIdElse() {
		Flow flow = new Flow();
		flow.setPublished(true);
		flow.setUid(UUID.randomUUID());
		flow.setDefaultFormId(UUID.randomUUID());
		flow.setDisabled(false);
		Form form = new Form();
		form.setFlow(flow);
		flow.setPublishedFlow(true);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(flow));
		when(formRepo.findById(Mockito.any())).thenReturn(Optional.of(form));
		BffCoreResponse response = flowserviceImpl.getFlowById(UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FLOW_DEFAULT_FORM_NOT_PUBLISHED.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getFlowById
	 */

	@Test
	public void testGetFlowByIdDefaultFormIdElseDisabled() {
		Flow flow = new Flow();
		flow.setPublished(true);
		flow.setUid(UUID.randomUUID());
		flow.setDefaultFormId(UUID.randomUUID());
		flow.setDisabled(false);
		Form form = new Form();
		form.setFlow(flow);
		form.setDisabled(true);
		flow.setPublishedFlow(true);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(flow));
		when(formRepo.findById(Mockito.any())).thenReturn(Optional.of(form));
		BffCoreResponse response = flowserviceImpl.getFlowById(UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FLOW_DEFAULT_FORM_DISABLED.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getFlowById for Exception
	 */

	@Test
	public void testGetFlowByIdException() {
		when(flowRepository.findById(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = flowserviceImpl.getFlowById(UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FLOW_API_FETCH_FLOW_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getFlowById for DataBaseException
	 */

	@Test
	public void testGetFlowByIdDataBaseException() {
		when(flowRepository.findById(Mockito.any())).thenThrow(new DataBaseException("Flow retrieval failed"));
		BffCoreResponse response = flowserviceImpl.getFlowById(UUID.randomUUID());
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetDefaultFormForFlowId() {
		String flowId = "0fb377f1-e357-f24a-a85e-e31bc232cbef";
		String formId = "432d3eb6-a6c7-475c-b1a4-bd7b9dfbc6e4";
		List<String> permissions = new ArrayList<>();
		permissions.add("footprint_add");
		UserPermissionRequest userPermissions = new UserPermissionRequest();
		userPermissions.setFlowId(UUID.fromString(flowId));
		userPermissions.setUserPermissions(permissions);
		when(formRepo.findById(UUID.fromString(formId))).thenReturn(Optional.of(getDefForm()));
		when(bffCommonUtil.checkUserHasPermissionForFlow(Mockito.any(), Mockito.any())).thenReturn(true);
		when(flowRepository.findById(UUID.fromString(flowId))).thenReturn(Optional.of(getFlowWithPermissions()));
		BffCoreResponse response = flowserviceImpl.getDefaultFormForFlowId(userPermissions);
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_FETCH_FLOW_DEF_FORM.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetDefaultFormForFlowDefFormNotPrsnt() {
		String flowId = "0fb377f1-e357-f24a-a85e-e31bc232cbef";
		String formId = "432d3eb6-a6c7-475c-b1a4-bd7b9dfbc6e4";
		List<String> permissions = new ArrayList<>();
		permissions.add("footprint_add");
		UserPermissionRequest userPermissions = new UserPermissionRequest();
		when(formRepo.findById(UUID.fromString(formId))).thenReturn(Optional.empty());
		userPermissions.setFlowId(UUID.fromString(flowId));
		userPermissions.setUserPermissions(permissions);
		when(bffCommonUtil.checkUserHasPermissionForFlow(Mockito.any(), Mockito.any())).thenReturn(true);
		when(flowRepository.findById(UUID.fromString(flowId))).thenReturn(Optional.of(getFlowWithPermissions()));
		BffCoreResponse response = flowserviceImpl.getDefaultFormForFlowId(userPermissions);
		assertEquals(BffResponseCode.ERR_NO_FORM_FOUND.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetDefaultFormForFlowDefFormDisabld() {
		String flowId = "0fb377f1-e357-f24a-a85e-e31bc232cbef";
		String formId = "432d3eb6-a6c7-475c-b1a4-bd7b9dfbc6e4";
		Form form = new Form();
		form.setDisabled(true);
		List<String> permissions = new ArrayList<>();
		permissions.add("footprint_add");
		UserPermissionRequest userPermissions = new UserPermissionRequest();
		when(formRepo.findById(UUID.fromString(formId))).thenReturn(Optional.of(form));
		userPermissions.setFlowId(UUID.fromString(flowId));
		userPermissions.setUserPermissions(permissions);
		when(bffCommonUtil.checkUserHasPermissionForFlow(Mockito.any(), Mockito.any())).thenReturn(true);
		when(flowRepository.findById(UUID.fromString(flowId))).thenReturn(Optional.of(getFlowWithPermissions()));
		BffCoreResponse response = flowserviceImpl.getDefaultFormForFlowId(userPermissions);
		assertEquals(BffResponseCode.ERR_FORM_DISABLE_CD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetDefaultFormForFlowDefFormUnpublish() {
		String flowId = "0fb377f1-e357-f24a-a85e-e31bc232cbef";
		String formId = "432d3eb6-a6c7-475c-b1a4-bd7b9dfbc6e4";
		List<String> permissions = new ArrayList<>();
		permissions.add("footprint_add");
		UserPermissionRequest userPermissions = new UserPermissionRequest();
		when(formRepo.findById(UUID.fromString(formId))).thenReturn(Optional.of(new Form()));
		userPermissions.setFlowId(UUID.fromString(flowId));
		userPermissions.setUserPermissions(permissions);
		when(bffCommonUtil.checkUserHasPermissionForFlow(Mockito.any(), Mockito.any())).thenReturn(true);
		when(flowRepository.findById(UUID.fromString(flowId))).thenReturn(Optional.of(getFlowWithPermissions()));
		BffCoreResponse response = flowserviceImpl.getDefaultFormForFlowId(userPermissions);
		assertEquals(BffResponseCode.ERR_FORM_UPUBLISH_CD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetDefaultFormForFlowIdNotPublishd() {
		String flowId = "0fb377f1-e357-f24a-a85e-e31bc232cbef";
		List<String> permissions = new ArrayList<>();
		permissions.add("footprint_add");
		UserPermissionRequest userPermissions = new UserPermissionRequest();
		userPermissions.setFlowId(UUID.fromString(flowId));
		userPermissions.setUserPermissions(permissions);
		when(flowRepository.findById(UUID.fromString(flowId))).thenReturn(Optional.of(getFlow()));
		BffCoreResponse response = flowserviceImpl.getDefaultFormForFlowId(userPermissions);
		assertEquals(BffResponseCode.ERR_FLOW_UPUBLISH_CD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetDefaultFormForFlowNotPresent() {
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.empty());
		BffCoreResponse response = flowserviceImpl.getDefaultFormForFlowId(new UserPermissionRequest());
		assertEquals(BffResponseCode.ERR_FLOW_API_FETCH_FLOW_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetDefaultFormForFlowDisabled() {
		Flow flow = new Flow();
		flow.setDisabled(true);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(flow));
		BffCoreResponse response = flowserviceImpl.getDefaultFormForFlowId(new UserPermissionRequest());
		assertEquals(BffResponseCode.ERR_FLOW_DISABLE_CD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetDefaultFormForFlowEmptyDefForm() {
		Flow flow = new Flow();
		flow.setPublished(true);
		flow.setDefaultFormId(null);
		flow.setPublishedFlow(true);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(flow));
		BffCoreResponse response = flowserviceImpl.getDefaultFormForFlowId(new UserPermissionRequest());
		assertEquals(BffResponseCode.ERR_FLOW_NO_DEFAULT_FORM.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetDefaultFormForFlowEmptyFlowPermssions() {
		Flow flow = new Flow();
		flow.setDefaultFormId(UUID.randomUUID());
		flow.setPublished(true);
		flow.setPublishedFlow(true);
		List<String> permissions = new ArrayList<>();
		permissions.add("footprint_add");
		UserPermissionRequest userPermissions = new UserPermissionRequest();
		userPermissions.setUserPermissions(permissions);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(flow));
		BffCoreResponse response = flowserviceImpl.getDefaultFormForFlowId(userPermissions);
		assertEquals(BffResponseCode.ERR_FLOW_API_FLOW_PERMISSIONS_EMPTY.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetDefaultFormForFlowInvalidPermssions() {
		UserPermissionRequest userPermissions = new UserPermissionRequest();
		userPermissions.setUserPermissions(new ArrayList<>());
		when(bffCommonUtil.checkUserHasPermissionForFlow(Mockito.any(), Mockito.any())).thenReturn(false);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(getFlowWithPermissions()));
		BffCoreResponse response = flowserviceImpl.getDefaultFormForFlowId(userPermissions);
		assertEquals(BffResponseCode.ERR_FLOW_API_INVALID_FLOW_PERMISSION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetDefaultFormForFlowDbException() {
		UserPermissionRequest userPermissions = new UserPermissionRequest();
		userPermissions.setFlowId(UUID.randomUUID());
		when(flowRepository.findById(Mockito.any())).thenThrow(new DataBaseException("Flow retrieval failed"));
		BffCoreResponse response = flowserviceImpl.getDefaultFormForFlowId(userPermissions);
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_DEF_FORM.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetDefaultFormForFlowException() {
		UserPermissionRequest userPermissions = new UserPermissionRequest();
		userPermissions.setFlowId(UUID.randomUUID());
		when(flowRepository.findById(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = flowserviceImpl.getDefaultFormForFlowId(userPermissions);
		assertEquals(BffResponseCode.ERR_FLOW_API_FETCH_FLOW_DEF_FORM.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for fetchAllFlows
	 */

	@Test
	public void testFetchAllFlows() {
		List<Flow> flowList = new ArrayList<>();
		List<ProductConfig> prodConfigIdList = new ArrayList<>();
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		prodConfigIdList.add(productConfig);
		flowList.add(getFlow());
		UserRole userRole = new UserRole();
		productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		RoleMaster rm = new RoleMaster();
		rm.setLevel(0);
		productConfig.setRoleMaster(rm);
		userRole.setRoleMaster(rm);
		List<ProductConfig> prodConfigList = new ArrayList<>();
		prodConfigList.add(productConfig);
		List<ProductProperty> prodPropList = new ArrayList<>();
		when(productConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(productConfig));
		when(productConfigRepository.findBySecondaryRefId(Mockito.any())).thenReturn(prodConfigList);
		when(productPropertyRepo.findByNameAndPropValueAndProductMasterAndIsSecondaryRefTrue(Mockito.any(),
				Mockito.any(), Mockito.any())).thenReturn(prodPropList);
		when(userRoleRepository.findByUserId(Mockito.any())).thenReturn(Optional.of(userRole));
		when(productPrepareService.getLayeredProductConfigList()).thenReturn(prodConfigIdList);

		when(flowRepository.findByIsPublishedTrueAndProductConfigInOrderByLastModifiedDateDesc(Mockito.any()))
				.thenReturn(flowList);
		BffCoreResponse response = flowserviceImpl.fetchFlows(BffAdminConstantsUtils.FlowType.PUBLISHED);
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_FETCH_ALL_FLOWS.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
		when(flowRepository.findByIsPublishedFalseAndProductConfigInOrderByLastModifiedDateDesc(Mockito.any()))
				.thenReturn(flowList);
		BffCoreResponse response2 = flowserviceImpl.fetchFlows(BffAdminConstantsUtils.FlowType.UNPUBLISHED);
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_FETCH_ALL_FLOWS.getCode(), response2.getCode());
		assertEquals(StatusCode.OK.getValue(), response2.getHttpStatusCode());
		when(flowRepository.findByProductConfigInOrderByLastModifiedDateDesc(Mockito.any())).thenReturn(flowList);
		BffCoreResponse response3 = flowserviceImpl.fetchFlows(BffAdminConstantsUtils.FlowType.ALL);
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_FETCH_ALL_FLOWS.getCode(), response3.getCode());
		assertEquals(StatusCode.OK.getValue(), response3.getHttpStatusCode());

	}

	/**
	 * Test method for fetchAllFlows for Exception
	 */

	@Test
	public void testFetchAllFlows_Exception() {
		when(productPrepareService.getLayeredProductConfigList()).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = flowserviceImpl.fetchFlows(FlowType.ALL);
		assertEquals(BffResponseCode.ERR_FLOW_API_FETCH_ALL_FLOWS.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for fetchAllFlows for DataBaseException
	 */

	@Test
	public void testFetchAllFlows_DataBaseException() {
		when(productPrepareService.getLayeredProductConfigList()).thenThrow(new DataBaseException("Product config details retieval failed"));
		BffCoreResponse response = flowserviceImpl.fetchFlows(FlowType.ALL);
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_FETCH_ALL_FLOWS.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for deleteFlowById
	 */

	@Test
	public void testDeleteFlowById() {
		AppConfigMaster appConfig = new AppConfigMaster();
		when(appConfigRepository.findByConfigNameAndConfigType(Mockito.any(), Mockito.any())).thenReturn(appConfig);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(new Flow()));
		doNothing().when(flowRepository).deleteById(Mockito.any());
		BffCoreResponse response = flowserviceImpl.deleteFlowById(UUID.randomUUID(), DeleteType.CONFIRM_DELETE);
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_DELETE_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testDeleteFlowByIdChkDelete() {
		AppConfigMaster appConfig = new AppConfigMaster();
		when(appConfigRepository.findByConfigNameAndConfigType(Mockito.any(), Mockito.any())).thenReturn(appConfig);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(new Flow()));
		BffCoreResponse response = flowserviceImpl.deleteFlowById(UUID.randomUUID(), DeleteType.CHECK_DELETE);
		assertEquals(BffResponseCode.ERR_FLOW_API_DELETE_FLOW_UNSUCCESS.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testDeleteFlowByIdDefFlow() {
		Flow flow = new Flow();
		flow.setUid(UUID.fromString("0fb377f1-e357-f24a-a85e-e31bc232cbef"));
		when(appConfigRepository.findByConfigNameAndConfigType(BffAdminConstantsUtils.DEFAULT_FLOW_KEY,
				AppCfgRequestType.APPLICATION.getType())).thenReturn(getDefFlowAppConfig());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(flow));
		doNothing().when(flowRepository).deleteById(Mockito.any());
		BffCoreResponse response = flowserviceImpl
				.deleteFlowById(UUID.fromString("0fb377f1-e357-f24a-a85e-e31bc232cbef"), DeleteType.CONFIRM_DELETE);
		assertEquals(BffResponseCode.ERR_FLOW_API_DELETE_FLOW_UNSUCCESS.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testDeleteFlowByIdHomeFlow() {
		Flow flow = new Flow();
		flow.setUid(UUID.fromString("0fb377f1-e357-f24a-a85e-e31bc232cbef"));
		when(appConfigRepository.findByConfigNameAndConfigType(BffAdminConstantsUtils.HOME_FLOW_KEY,
				AppCfgRequestType.APPLICATION.getType())).thenReturn(getHomeFlowAppConfig());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(flow));
		doNothing().when(flowRepository).deleteById(Mockito.any());
		BffCoreResponse response = flowserviceImpl
				.deleteFlowById(UUID.fromString("0fb377f1-e357-f24a-a85e-e31bc232cbef"), DeleteType.CONFIRM_DELETE);
		assertEquals(BffResponseCode.ERR_FLOW_API_DELETE_FLOW_UNSUCCESS.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testDeleteFlowByIdFlowNotPresent() {
		when(flowRepository.findById(UUID.randomUUID())).thenReturn(Optional.empty());
		BffCoreResponse response = flowserviceImpl.deleteFlowById(UUID.randomUUID(), DeleteType.CONFIRM_DELETE);
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for deleteFlowById for for Exception
	 */

	@Test
	public void testDeleteFlowByIdException() {
		BffCoreResponse response = flowserviceImpl.deleteFlowById(null, Mockito.any());
		assertEquals(BffResponseCode.ERR_FLOW_API_DELETE_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for deleteFlowById for DataBaseException
	 */

	@Test
	public void testDeleteFlowByIdDataBaseException() {
		when(flowRepository.findById(Mockito.any())).thenThrow(new DataBaseException("Flow retrieval failed"));
		BffCoreResponse response = flowserviceImpl.deleteFlowById(UUID.randomUUID(), Mockito.any());
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_DELETE_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testUniqueFlow() {
		when(flowRepository.findByNameAndVersion(Mockito.anyString(), Mockito.anyLong()))
				.thenReturn(new ArrayList<Flow>());
		BffCoreResponse response = flowserviceImpl.uniqueFlow("picking", 1);
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_UNIQUE_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testUniqueFlow_NonUnique() {
		List<Flow> flowList = new ArrayList<Flow>();
		flowList.add(new Flow());
		when(flowRepository.findByNameAndVersion(Mockito.anyString(), Mockito.anyLong())).thenReturn(flowList);
		BffCoreResponse response = flowserviceImpl.uniqueFlow("picking", 1);
		assertEquals(BffResponseCode.ERR_FLOW_API_CREATE_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testUniqueFlow_DataBaseException() {

		when(flowRepository.findByNameAndVersion(Mockito.any(), Mockito.anyLong()))
				.thenThrow(new DataBaseException("Flow retreival failed"));
		BffCoreResponse response = flowserviceImpl.uniqueFlow("picking", 1);
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_UNIQUE_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchCount() {
		List<ProductConfig> prodConfigIdList = new ArrayList<>();
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		prodConfigIdList.add(productConfig);
		when(productPrepareService.getLayeredProductConfigList()).thenReturn(prodConfigIdList);
		when(apiMasterRepository.countAllApis()).thenReturn(1);
		when(customComponentMasterRepository.customComponentCount()).thenReturn(1);
		when(apiMasterRepository.countAllApis()).thenReturn(1);
		when(apiRegistryRepository.countAllRegistry()).thenReturn(1);
		when(flowRepository.findByIsPublishedTrueAndProductConfigInOrderByLastModifiedDateDesc(prodConfigIdList))
				.thenReturn(Arrays.asList(getFlow()));
		BffCoreResponse response = flowserviceImpl.fetchCount();
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_FLOW_COUNT.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchCountDataAccessException() {
		when(productPrepareService.getLayeredProductConfigList()).thenThrow(new DataBaseException("Product config details retrieval failed"));
		BffCoreResponse response = flowserviceImpl.fetchCount();
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_FLOW_COUNT.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchCountException() {
		when(productPrepareService.getLayeredProductConfigList()).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = flowserviceImpl.fetchCount();
		assertEquals(BffResponseCode.ERR_FLOW_API_FLOW_COUNT.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testDisableFlow() {
		String flowId = "0fb377f1-e357-f24a-a85e-e31bc232cbef";
		AppConfigMaster appConfig = new AppConfigMaster();
		List<AppConfigDetail> appConfigDetails = new ArrayList<>();
		AppConfigDetail appConfigDetail = new AppConfigDetail();
		appConfigDetails.add(appConfigDetail);
		appConfig.setAppConfigDetails(appConfigDetails);
		when(appConfigRepository.findByConfigNameAndConfigType(Mockito.any(), Mockito.any())).thenReturn(appConfig);
		when(flowRepository.findById(UUID.fromString(flowId))).thenReturn(Optional.of(getFlow()));
		BffCoreResponse response = flowserviceImpl.disableFlow(UUID.fromString(flowId), DisableType.CONFIRM_DISABLE);
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_DISABLE_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testDisableFlowFlowNotPresent() {
		when(flowRepository.findById(UUID.randomUUID())).thenReturn(Optional.empty());
		BffCoreResponse response = flowserviceImpl.disableFlow(UUID.randomUUID(), DisableType.CONFIRM_DISABLE);
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testDisableFlowDefaultFlow() {
		String flowId = "0fb377f1-e357-f24a-a85e-e31bc232cbef";
		when(appConfigRepository.findByConfigNameAndConfigType(BffAdminConstantsUtils.DEFAULT_FLOW_KEY,
				AppCfgRequestType.APPLICATION.getType())).thenReturn(getDefFlowAppConfig());
		when(flowRepository.findById(UUID.fromString(flowId))).thenReturn(Optional.of(getFlow()));
		BffCoreResponse response = flowserviceImpl.disableFlow(UUID.fromString(flowId), DisableType.CONFIRM_DISABLE);
		assertEquals(BffResponseCode.ERR_FLOW_API_CHECK_DEFAULT_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testDisableFlowHomeFlow() {
		String flowId = "0fb377f1-e357-f24a-a85e-e31bc232cbef";
		when(appConfigRepository.findByConfigNameAndConfigType(BffAdminConstantsUtils.HOME_FLOW_KEY,
				AppCfgRequestType.APPLICATION.getType()))
				.thenReturn(getAppConfigMaster());
		when(appConfigRepository.findByConfigNameAndConfigType(BffAdminConstantsUtils.DEFAULT_FLOW_KEY,
				AppCfgRequestType.APPLICATION.getType())).thenReturn(new AppConfigMaster());
		when(flowRepository.findById(UUID.fromString(flowId))).thenReturn(Optional.of(getFlowHome()));
		BffCoreResponse response = flowserviceImpl.disableFlow(UUID.fromString(flowId), DisableType.CONFIRM_DISABLE);
		assertEquals(BffResponseCode.ERR_FLOW_API_CHECK_CURRENT_FLOW_HOME.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testDisableFlowCheckDisable() {
		String flowId = "0fb377f1-e357-f24a-a85e-e31bc232cbef";
		AppConfigMaster appConfig = new AppConfigMaster();
		when(appConfigRepository.findByConfigNameAndConfigType(Mockito.any(), Mockito.any())).thenReturn(appConfig);
		when(flowRepository.findById(UUID.fromString(flowId))).thenReturn(Optional.of(getFlow()));
		BffCoreResponse response = flowserviceImpl.disableFlow(UUID.fromString(flowId), DisableType.CHECK_DISABLE);
		assertEquals(BffResponseCode.ERR_FLOW_API_CHECK_DISABLE_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testDisableFlow_Exception() {
		String flowId = "0fb377f1-e357-f24a-a85e-e31bc232cbef";
		when(flowRepository.findById(Mockito.any())).thenReturn(null);
		BffCoreResponse response = flowserviceImpl.disableFlow(UUID.fromString(flowId), DisableType.CHECK_DISABLE);
		assertEquals(BffResponseCode.ERR_FLOW_API_DISABLE_FLOW_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());

	}

	/**
	 * Test method for testDisableFlow for DataBaseException
	 */

	@Test
	public void testDisableFlow_DataBaseException() {
		String flowId = "0fb377f1-e357-f24a-a85e-e31bc232cbef";
		when(flowRepository.findById(Mockito.any())).thenThrow(new DataBaseException("Flow retrieval failed"));
		BffCoreResponse response = flowserviceImpl.disableFlow(UUID.fromString(flowId), DisableType.CHECK_DISABLE);
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_DISABLE_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	private FlowRequest getFlowRo() {
		FlowRequest flowro = new FlowRequest();
		flowro.setFlowId(UUID.randomUUID());
		flowro.setName("pick");
		flowro.setDefaultFormId(UUID.randomUUID());
		flowro.setDisabled(true);
		List<String> permissions = new ArrayList<String>();
		permissions.add("");
		flowro.setPermissions(permissions);
		return flowro;
	}

	private FlowRequest getFlowRo1() {
		FlowRequest flowro = new FlowRequest();
		flowro.setFlowId(UUID.randomUUID());
		flowro.setName("pick");
		flowro.setDefaultFormId(UUID.randomUUID());
		List<String> permissions = new ArrayList<String>();
		permissions.add("");
		flowro.setPermissions(permissions);
		return flowro;
	}

	private CloneRequest getCloneFlowRo() {
		CloneRequest flowro = new CloneRequest();
		flowro.setId(UUID.randomUUID());
		flowro.setName("pick");
		flowro.setFlowIdForClonedForm(UUID.randomUUID());
		return flowro;
	}

	private CloneRequest getCloneForm() {
		CloneRequest cloneRequest = new CloneRequest();
		cloneRequest.setId(UUID.fromString("1a6503f0-2c97-436c-bbf6-357609eca276"));
		cloneRequest.setName("cloneform");
		return cloneRequest;
	}

	private CloneRequest getCloneForm1() {
		CloneRequest cloneRequest = new CloneRequest();
		cloneRequest.setId(UUID.fromString("1a6503f0-2c97-436c-bbf6-357609eca276"));
		cloneRequest.setFlowIdForClonedForm(UUID.fromString("44a07fe0-697e-474b-95b0-ffdeec3ecf45"));
		cloneRequest.setName("cloneform");
		return cloneRequest;
	}

	private Flow getFlow() {
		String flowId = "0fb377f1-e357-f24a-a85e-e31bc232cbef";
		Flow flow = new Flow();
		flow.setUid(UUID.fromString(flowId));
		flow.setDefaultFormId(UUID.randomUUID());
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setName("SUPER");
		productConfig.setRoleMaster(roleMaster);
		flow.setProductConfig(productConfig);
		List<Form> formlist = new ArrayList<>();
		Form form = new Form();
		form.setUid(UUID.fromString("1a6503f0-2c97-436c-bbf6-357609eca276"));
		form.setName("login_flow");
		form.setTabbedForm(true);
		Form form1 = new Form();
		form1.setUid(UUID.fromString("0fb377f1-e357-f24a-a85e-e31bc232cbef"));
		form1.setName("login_flow");
		formlist.add(form);
		formlist.add(form1);
		flow.setForms(formlist);
		flow.setName("DEFAULT_FLOW");
		flow.setPublished(false);
		flow.setDisabled(false);
		List<FlowPermission> flowPermissions = new ArrayList<>();
		FlowPermission flowPermission = new FlowPermission();
		flowPermissions.add(flowPermission);
		flow.setFlowPermission(flowPermissions);
		List<Field> fields = new ArrayList<>();
		Field Field = new Field();
		Field.setAddAnother("");
		fields.add(Field);
		form.setFields(fields);
		form.setFlow(flow);
		Events events = new Events();
		events.setUid(UUID.randomUUID());
		events.setEvent("on click");
		events.setAction("0fb377f1-e357-f24a-a85e-e31bc232cbef");
		List<Events> eventList = new ArrayList<>();
		eventList.add(events);
		form.setEvents(eventList);
		return flow;
	}

	private Form getForm() {
		Form form = new Form();
		Flow flow = new Flow();
		flow.setUid(UUID.fromString("0fb377f1-e357-f24a-a85e-e31bc232cbef"));
		form.setUid(UUID.fromString("1a6503f0-2c97-436c-bbf6-357609eca276"));
		form.setName("login_" + new Random().nextInt(10000));
		List<Form> formlist = new ArrayList<>();
		formlist.add(form);
		List<Field> fields = new ArrayList<>();
		Field Field = new Field();
		Field.setAddAnother("");
		fields.add(Field);
		form.setFlow(flow);
		form.setFields(fields);

		return form;
	}

	private Flow getFlowHome() {
		String flowId = "0fb377f1-e357-f24a-a85e-e31bc232cbef";
		Flow flow = new Flow();
		flow.setUid(UUID.fromString(flowId));
		flow.setDefaultFormId(UUID.randomUUID());
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		flow.setProductConfig(productConfig);
		List<Form> formlist = new ArrayList<>();
		Form form = new Form();
		form.setName("login_" + new Random().nextInt(10000));
		formlist.add(form);
		flow.setForms(formlist);
		flow.setName("HOME_FLOW");
		flow.setDefaultFormId(UUID.randomUUID());
		return flow;
	}

	@Test
	public void testPublishFlow() throws IOException {
		FormData formdata = new FormData();
		FormProperties formProperties = new FormProperties();
		formProperties.setMenus(getMenuListRequest());
		formdata.setFormProperties(formProperties);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(getFlow()));
		BffCoreResponse response = flowserviceImpl.publishFlow(UUID.randomUUID(), ActionType.CHECK_PUBLISH,
				Arrays.asList("foot"));
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_MODIFY_FLOW_CHECK_PUBLISH.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
		flowserviceImpl.publishFlow(UUID.randomUUID(), ActionType.CHECK_PUBLISH, Arrays.asList("foot"));
		when(formRepo.save(Mockito.any())).thenReturn(getFlow().getForms().get(0));
		when(formTransformation.createFormData(Mockito.any())).thenReturn(formdata);
		when(flowRepository.save(Mockito.any())).thenReturn(getFlow());
		when(formServiceImpl.getFormMenus(Mockito.any(), Mockito.any(),Mockito.anyBoolean())).thenReturn((getMenuListRequest()));
		BffCoreResponse response2 = flowserviceImpl.publishFlow(UUID.randomUUID(), ActionType.CONFIRM_PUBLISH,
				Arrays.asList("foot"));
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_CONFIRM_PUBLISH.getCode(), response2.getCode());
		assertEquals(StatusCode.OK.getValue(), response2.getHttpStatusCode());
	}

	@Test
	public void testPublishFlowFlowNotPresent() {
		when(flowRepository.findById(UUID.randomUUID())).thenReturn(Optional.empty());
		BffCoreResponse response = flowserviceImpl.publishFlow(UUID.randomUUID(), ActionType.CONFIRM_PUBLISH,
				Arrays.asList("foot"));
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testPublishFlow_DBException() {
		when(flowRepository.findById(Mockito.any())).thenThrow(new DataBaseException("Flow retrieval failed"));
		BffCoreResponse response = flowserviceImpl.publishFlow(UUID.randomUUID(), ActionType.CONFIRM_PUBLISH,
				Arrays.asList("foot"));
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_PUBLISH_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testPublishFlow_Exception() {
		when(flowRepository.findById(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = flowserviceImpl.publishFlow(UUID.randomUUID(), ActionType.CONFIRM_PUBLISH,
				Arrays.asList("foot"));
		assertEquals(BffResponseCode.ERR_FLOW_API_PUBLISH_FLOW_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCloneFlow() {
		UUID uid = UUID.randomUUID();
		Flow flow = new Flow();
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.fromString("1a6503f0-2c97-436c-bbf6-357609eca276"));
		flow.setProductConfig(productConfig);
		flow.setDefaultFormId(uid);
		ExtendedFlowBase extendedFlowBase = new ExtendedFlowBase();
		List<ProductConfig> prodConfigList = new ArrayList<>();
		prodConfigList.add(productConfig);
		List<Form> formList = new ArrayList<>();
		Form form = new Form();
		form.setUid(UUID.fromString("1a6503f0-2c97-436c-bbf6-357609eca276"));
		form.setTabbedForm(true);
		form.setName("login_flow");
		formList.add(form);
		form.setFlow(flow);
		when(formRepo.findById(Mockito.any())).thenReturn(Optional.of(form));
		when(formRepo.findByExtendedFromFormId(UUID.randomUUID())).thenReturn(formList);
		when(flowRepository.getFlowLatestVersion("pick")).thenReturn(BffAdminConstantsUtils.FLOW_INITIAL_VERSION);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(getFlow()));
		when(flowRepository.save(Mockito.any())).thenReturn(getFlow());
		when(extendedFlowBaseRepo.save(Mockito.any())).thenReturn(extendedFlowBase);
		when(productConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(new ProductConfig()));
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(productConfig);
		when(formRepo.save(Mockito.any())).thenReturn(form);
		List<Form> forms = new ArrayList<>();
		forms.add(getFlow().getForms().get(0));
		when(formRepo.findByExtendedFromFormId(Mockito.any())).thenReturn(forms);
		BffCoreResponse response = flowserviceImpl.cloneComponent(getCloneFlowRo(), CloneType.FLOW,
				BffAdminConstantsUtils.VERSIONING);
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_VERSION_COMPONENT.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
		BffCoreResponse response1 = flowserviceImpl.cloneComponent(getCloneFlowRo(), CloneType.FLOW,
				BffAdminConstantsUtils.EXTENDED);
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_EXTENDED_COMPONENT.getCode(), response1.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response1.getHttpStatusCode());
	}

	@Test
	public void testCloneFlowFlowNameNotUnique() {
		List<Flow> flowList = new ArrayList<Flow>();
		flowList.add(new Flow());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(getFlow()));
		when(flowRepository.findByNameAndVersion(Mockito.any(), Mockito.anyLong())).thenReturn(flowList);
		when(flowRepository.save(Mockito.any())).thenReturn(getFlow());
		when(productConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(new ProductConfig()));
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(productConfig);
		BffCoreResponse response = flowserviceImpl.cloneComponent(getCloneFlowRo(), CloneType.FLOW,
				BffAdminConstantsUtils.EXTENDED);
		assertEquals(BffResponseCode.ERR_CLONE_API_FLOW_NAME_UNIQUE_CHECK.getCode(), response.getCode());
		assertEquals(StatusCode.CONFLICT.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCloneFlowFlowNameUniqueChkVersioning() {
		List<Flow> flowList = new ArrayList<Flow>();
		flowList.add(new Flow());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(getFlow()));
		when(flowRepository.findByNameAndVersion(Mockito.any(), Mockito.anyLong())).thenReturn(flowList);
		when(flowRepository.save(Mockito.any())).thenReturn(getFlow());
		when(productConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(new ProductConfig()));
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(productConfig);
		BffCoreResponse response = flowserviceImpl.cloneComponent(getCloneFlowRo(), CloneType.FLOW,
				BffAdminConstantsUtils.VERSIONING);
		assertEquals(BffResponseCode.ERR_VERSION_API_FLOW_NAME_UNIQUE_CHECK.getCode(), response.getCode());
		assertEquals(StatusCode.CONFLICT.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCloneFlowFlowNotPrsnt() {
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.empty());
		when(flowRepository.findByNameAndVersion(Mockito.any(), Mockito.anyLong())).thenReturn(new ArrayList<Flow>());
		when(flowRepository.save(Mockito.any())).thenReturn(getFlow());
		when(productConfigRepository.findById(Mockito.any())).thenReturn(Optional.of(new ProductConfig()));
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(productConfig);
		BffCoreResponse response = flowserviceImpl.cloneComponent(getCloneFlowRo(), CloneType.FLOW,
				BffAdminConstantsUtils.VERSIONING);
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCloneFormInSameFlow() {
		ProductConfig config = new ProductConfig();
		config.setUid(UUID.randomUUID());
		Form clonedForm = new Form(new Form(), false, config.getUid(), true);
		clonedForm.setUid(UUID.fromString("1a6503f0-2c97-436c-bbf6-357609eca276"));
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		Flow flow = new Flow();
		clonedForm.setFlow(flow);
		flow.setProductConfig(productConfig);
		flow.setDefaultFormId(UUID.randomUUID());
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(productConfig);
		when(formRepo.save(Mockito.any())).thenReturn(clonedForm);
		when(formRepo.findById(UUID.fromString("1a6503f0-2c97-436c-bbf6-357609eca276")))
				.thenReturn(Optional.of(new Form()));
		when(formRepo.save(Mockito.any())).thenReturn(clonedForm);
		BffCoreResponse response = flowserviceImpl.cloneComponent(getCloneForm(), CloneType.FORM_IN_SAME_FLOW,
				BffAdminConstantsUtils.EXTENDED);
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_CLONE_FORM_IN_SAME_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCloneFormInSameFlowNameNotUnique() {
		List<Form> formList = new ArrayList<Form>();
		Form form = new Form();
		formList.add(form);
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(productConfig);
		when(formRepo.findByNameAndFlow(Mockito.any(), Mockito.any())).thenReturn(formList);
		when(formRepo.findById(UUID.fromString("1a6503f0-2c97-436c-bbf6-357609eca276")))
				.thenReturn(Optional.of(new Form()));
		BffCoreResponse response = flowserviceImpl.cloneComponent(getCloneForm(), CloneType.FORM_IN_SAME_FLOW,
				BffAdminConstantsUtils.EXTENDED);
		assertEquals(BffResponseCode.ERR_API_FORM_NAME_UNIQUE_CHECK.getCode(), response.getCode());
		assertEquals(StatusCode.CONFLICT.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCloneFormInSameFlowFormNotPrsnt() {
		when(formRepo.findById(UUID.fromString("1a6503f0-2c97-436c-bbf6-357609eca276"))).thenReturn(Optional.empty());
		BffCoreResponse response = flowserviceImpl.cloneComponent(getCloneForm(), CloneType.FORM_IN_SAME_FLOW,
				BffAdminConstantsUtils.EXTENDED);
		assertEquals(BffResponseCode.ERR_NO_FORM_FOUND.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCloneFormInDiffFlow() {
		UUID uid = UUID.randomUUID();
		Flow flow = new Flow();
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.fromString("1a6503f0-2c97-436c-bbf6-357609eca276"));
		flow.setProductConfig(productConfig);
		flow.setDefaultFormId(uid);
		Form clonedForm = new Form(new Form(), false, productConfig.getUid(), true);
		clonedForm.setUid(UUID.fromString("1a6503f0-2c97-436c-bbf6-357609eca276"));
		clonedForm.setFlow(flow);
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(productConfig);
		when(flowRepository.findById(getCloneForm1().getFlowIdForClonedForm())).thenReturn(Optional.of(flow));
		when(formRepo.save(Mockito.any())).thenReturn(clonedForm);
		when(formRepo.findById(UUID.fromString("1a6503f0-2c97-436c-bbf6-357609eca276")))
				.thenReturn(Optional.of(new Form()));
		BffCoreResponse response = flowserviceImpl.cloneComponent(getCloneForm1(), CloneType.FORM_IN_DIFF_FLOW,
				BffAdminConstantsUtils.EXTENDED);
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_CLONE_FORM_IN_DIFF_FLOW.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCloneFormInDiffFlowNameNotUnique() {
		List<Form> formList = new ArrayList<Form>();
		Form form = new Form();
		Flow flow = new Flow();
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.fromString("1a6503f0-2c97-436c-bbf6-357609eca276"));
		flow.setProductConfig(productConfig);
		formList.add(form);
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(productConfig);
		when(flowRepository.findById(getCloneForm1().getFlowIdForClonedForm())).thenReturn(Optional.of(flow));
		when(formRepo.findByNameAndFlow(Mockito.any(), Mockito.any())).thenReturn(formList);
		when(formRepo.findById(UUID.fromString("1a6503f0-2c97-436c-bbf6-357609eca276")))
				.thenReturn(Optional.of(new Form()));
		BffCoreResponse response = flowserviceImpl.cloneComponent(getCloneForm1(), CloneType.FORM_IN_DIFF_FLOW,
				BffAdminConstantsUtils.EXTENDED);
		assertEquals(BffResponseCode.ERR_API_FORM_NAME_UNIQUE_CHECK.getCode(), response.getCode());
		assertEquals(StatusCode.CONFLICT.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCloneFormInDiffFlowFormNotPrsnt() {
		when(formRepo.findById(UUID.fromString("1a6503f0-2c97-436c-bbf6-357609eca276"))).thenReturn(Optional.empty());
		BffCoreResponse response = flowserviceImpl.cloneComponent(getCloneForm1(), CloneType.FORM_IN_DIFF_FLOW,
				BffAdminConstantsUtils.EXTENDED);
		assertEquals(BffResponseCode.ERR_NO_FORM_FOUND.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCloneFormInDiffFlowFlowNotPrsnt() {
		when(flowRepository.findById(UUID.randomUUID())).thenReturn(Optional.empty());
		when(formRepo.findById(UUID.fromString("1a6503f0-2c97-436c-bbf6-357609eca276")))
				.thenReturn(Optional.of(new Form()));
		BffCoreResponse response = flowserviceImpl.cloneComponent(getCloneForm1(), CloneType.FORM_IN_DIFF_FLOW,
				BffAdminConstantsUtils.EXTENDED);
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for CloneFlow for BffCoreException
	 */

	@Test
	public void testCloneFlowSameLayerException() {
		when(productPrepareService.getCurrentLayerProdConfigId()).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		getFlowRo().setFlowId(null);
		BffCoreResponse response = flowserviceImpl.cloneComponent(getCloneFlowRo(), CloneType.FLOW,
				BffAdminConstantsUtils.EXTENDED);
		assertEquals(BffResponseCode.ERR_FLOW_API_CREATE_FLOW_EXCEPTION_CLONED.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCloneFlowVersioningException() {
		when(productPrepareService.getCurrentLayerProdConfigId()).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		getFlowRo().setFlowId(null);
		BffCoreResponse response = flowserviceImpl.cloneComponent(getCloneFlowRo(), CloneType.FLOW,
				BffAdminConstantsUtils.VERSIONING);
		assertEquals(BffResponseCode.ERR_FLOW_API_CREATE_FLOW_EXCEPTION_VERSIONED.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for CloneFlow for DataBaseException
	 */

	@Test
	public void testCloneFlowDataBaseException() {
		when(flowRepository.findById(Mockito.any())).thenThrow(new DataBaseException("Flow retrieval failed"));
		when(productPrepareService.getCurrentLayerProdConfigId()).thenThrow(new DataBaseException("Product config details retrieval failed"));
		BffCoreResponse response = flowserviceImpl.cloneComponent(getCloneFlowRo(), CloneType.FLOW,
				BffAdminConstantsUtils.EXTENDED);
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_CREATE_FLOW_CLONED.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCloneFlowVersioningDataBaseException() {
		when(flowRepository.findById(Mockito.any())).thenThrow(new DataBaseException("Flow retrieval failed"));
		when(productPrepareService.getCurrentLayerProdConfigId()).thenThrow(new DataBaseException("Product config details retrieval failed"));
		BffCoreResponse response = flowserviceImpl.cloneComponent(getCloneFlowRo(), CloneType.FLOW,
				BffAdminConstantsUtils.VERSIONING);
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_CREATE_FLOW_VERSIONED.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void fetchFlowBasicList() {
		List<ProductConfig> prodConfigList = new ArrayList<>();
		ProductConfig productConfig = new ProductConfig();
		prodConfigList.add(productConfig);
		List<FlowLiteDto> flowLiteList = new ArrayList<>();
		FlowLiteDto flowLite = new FlowLiteDto(UUID.fromString("0fb377f1-e357-f24a-a85e-e31bc232cbef"), "", 1, false,
				false, UUID.randomUUID());
		flowLiteList.add(flowLite);
		when(flowRepository.getFlowBasicList(prodConfigList)).thenReturn(flowLiteList);
		when(productPrepareService.getLayeredProductConfigList()).thenReturn(prodConfigList);
		BffCoreResponse response = flowserviceImpl.fetchFlowBasicList();
		assertEquals(BffResponseCode.FLOW_SUCCESS_CODE_FLOW_BASIC_LIST_FETCH.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void fetchFlowBasicListException() {
		when(flowRepository.getFlowBasicList(new ArrayList<>())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = flowserviceImpl.fetchFlowBasicList();
		assertEquals(BffResponseCode.ERR_FLOW_API_FLOW_BASIC_LIST_FETCH.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void fetchFlowBasicListDataAccessException() {
		when(flowRepository.getFlowBasicList(new ArrayList<>())).thenThrow(new DataBaseException("Flow retrieval failed"));
		BffCoreResponse response = flowserviceImpl.fetchFlowBasicList();
		assertEquals(BffResponseCode.DB_ERR_FLOW_API_FLOW_BASIC_LIST_FETCH.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	private Flow getFlowWithPermissions() {
		String flowId = "0fb377f1-e357-f24a-a85e-e31bc232cbef";
		String formId = "432d3eb6-a6c7-475c-b1a4-bd7b9dfbc6e4";
		List<FlowPermission> flowPermissions = new ArrayList<>();
		FlowPermission flowPermission = new FlowPermission();
		flowPermission.setPermission("footprint_add");
		flowPermission.setPermission("footprint_delete");
		flowPermissions.add(flowPermission);
		Flow flow = new Flow();
		flow.setUid(UUID.fromString(flowId));
		flow.setDefaultFormId(UUID.randomUUID());
		flow.setFlowPermission(flowPermissions);
		flow.setPublished(true);
		flow.setDefaultFormId(UUID.fromString(formId));
		flow.setVersion(1);
		flow.setName("flow1");
		flow.setPublishedFlow(true);
		return flow;
	}

	private AppConfigMaster getAppConfigMaster() {
		String uid = "0fb377f1-e357-f24a-a85e-e31bc232cbef";
		List<AppConfigDetail> appConfigDetails = new ArrayList<>();
		AppConfigDetail appConfigDetail = new AppConfigDetail();
		appConfigDetail.setConfigValue(uid.toString());
		appConfigDetails.add(appConfigDetail);
		AppConfigMaster appConfig = new AppConfigMaster();
		appConfig.setAppConfigDetails(appConfigDetails);
		return appConfig;
	}

	private Form getDefForm() {
		byte[] publishdForm = "{}".getBytes();
		Form form = new Form();
		form.setModalForm(true);
		form.setTabbedForm(true);
		form.setPublishedForm(publishdForm);
		return form;
	}

	private AppConfigMaster getHomeFlowAppConfig() {
		AppConfigMaster homeFlowAppConfig = new AppConfigMaster();
		List<AppConfigDetail> homeFlowAppConfigDetails = new ArrayList<AppConfigDetail>();
		AppConfigDetail homeFlowAppConfigDetail = new AppConfigDetail();
		homeFlowAppConfigDetail.setConfigValue("0fb377f1-e357-f24a-a85e-e31bc232cbef");
		homeFlowAppConfigDetails.add(homeFlowAppConfigDetail);
		homeFlowAppConfig.setAppConfigDetails(homeFlowAppConfigDetails);
		return homeFlowAppConfig;
	}

	private AppConfigMaster getDefFlowAppConfig() {
		AppConfigMaster defFlowAppConfig = new AppConfigMaster();
		List<AppConfigDetail> defFlowAppConfigDetails = new ArrayList<AppConfigDetail>();
		AppConfigDetail defFlowAppConfigDetail = new AppConfigDetail();
		defFlowAppConfigDetail.setConfigValue("0fb377f1-e357-f24a-a85e-e31bc232cbef");
		defFlowAppConfigDetails.add(defFlowAppConfigDetail);
		defFlowAppConfig.setAppConfigDetails(defFlowAppConfigDetails);
		return defFlowAppConfig;
	}

	private List<MenuListRequest> getMenuListRequest() {
		MenuRequest request = new MenuRequest();
		List<String> perm = new ArrayList<String>();
		perm.add("Read");
		List<MenuListRequest> menuListRequestList = new ArrayList<MenuListRequest>();
		MenuListRequest menuListRequest = new MenuListRequest();
		TranslationRequest translationRequest = TranslationRequest.builder().locale(BffAdminConstantsUtils.LOCALE)
				.rbkey("1001").rbvalue(BffAdminConstantsUtils.EMPTY_SPACES).type(BffAdminConstantsUtils.EMPTY_SPACES)
				.uid(UUID.randomUUID()).build();
		MenuAction menuAction = new MenuAction();
		menuListRequest.setIconName("ERROR");
		menuListRequest.setIconAlignment("left");
		menuListRequest.setMenuType("MAIN");
		menuListRequest.setPermissions(perm);
		menuListRequest.setMenuName(translationRequest);
		menuListRequest.setShowInToolBar(false);
		menuAction.setActionType(TriggerAction.NAVIGATE_TO_WORKFLOW.toString());
		menuListRequest.setMenuAction(menuAction);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode root = mapper.createObjectNode();
		menuAction.setProperties(root);
		menuListRequest.setMenuAction(menuAction);
		List<MenuListRequest> menuList = new ArrayList<MenuListRequest>();
		MenuListRequest subMenu = new MenuListRequest();
		subMenu.setIconName("IMAGe");
		subMenu.setIconAlignment("left");
		subMenu.setMenuType("MAIN");
		subMenu.setPermissions(perm);
		subMenu.setMenuName(translationRequest);
		subMenu.setShowInToolBar(false);
		subMenu.setMenuAction(menuAction);
		menuList.add(subMenu);
		menuListRequest.setSubMenus(menuList);
		menuListRequestList.add(menuListRequest);
		request.setMenus(menuListRequestList);
		request.setWarehouseName("PRODUCT_NAME");
		menuListRequest.setHotKey(mapper.createObjectNode());
		return menuListRequestList;
	}
}
