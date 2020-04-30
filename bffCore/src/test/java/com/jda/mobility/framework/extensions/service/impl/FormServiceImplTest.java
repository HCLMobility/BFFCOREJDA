
package com.jda.mobility.framework.extensions.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jda.mobility.framework.extensions.dto.FieldObjDto;
import com.jda.mobility.framework.extensions.dto.FormObjDto;
import com.jda.mobility.framework.extensions.dto.MenuDto;
import com.jda.mobility.framework.extensions.dto.MenuListDto;
import com.jda.mobility.framework.extensions.dto.TabDto;
import com.jda.mobility.framework.extensions.dto.TranslationRequest;
import com.jda.mobility.framework.extensions.entity.CustomComponentMaster;
import com.jda.mobility.framework.extensions.entity.Events;
import com.jda.mobility.framework.extensions.entity.Field;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.FlowPermission;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.FormCustomComponent;
import com.jda.mobility.framework.extensions.entity.FormDependency;
import com.jda.mobility.framework.extensions.entity.FormDependent;
import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.entity.Tabs;
import com.jda.mobility.framework.extensions.entity.projection.FormLiteDto;
import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.exception.DataBaseException;
import com.jda.mobility.framework.extensions.model.AppConfigRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.DatePicker;
import com.jda.mobility.framework.extensions.model.DetailResponse;
import com.jda.mobility.framework.extensions.model.Event;
import com.jda.mobility.framework.extensions.model.FieldComponent;
import com.jda.mobility.framework.extensions.model.FieldDependency;
import com.jda.mobility.framework.extensions.model.FormAttributes;
import com.jda.mobility.framework.extensions.model.FormData;
import com.jda.mobility.framework.extensions.model.FormProperties;
import com.jda.mobility.framework.extensions.model.IconInfo;
import com.jda.mobility.framework.extensions.model.LabelDetails;
import com.jda.mobility.framework.extensions.model.MenuListRequest;
import com.jda.mobility.framework.extensions.model.Style;
import com.jda.mobility.framework.extensions.model.Validate;
import com.jda.mobility.framework.extensions.repository.CustomComponentMasterRepository;
import com.jda.mobility.framework.extensions.repository.DataRepository;
import com.jda.mobility.framework.extensions.repository.EventsRepository;
import com.jda.mobility.framework.extensions.repository.FieldRepository;
import com.jda.mobility.framework.extensions.repository.FieldValuesRepository;
import com.jda.mobility.framework.extensions.repository.FlowRepository;
import com.jda.mobility.framework.extensions.repository.FormCustomComponentRepository;
import com.jda.mobility.framework.extensions.repository.FormDependencyRepository;
import com.jda.mobility.framework.extensions.repository.FormRepository;
import com.jda.mobility.framework.extensions.repository.MenuMasterRepository;
import com.jda.mobility.framework.extensions.repository.ProductConfigRepository;
import com.jda.mobility.framework.extensions.repository.ProductMasterRepository;
import com.jda.mobility.framework.extensions.repository.ProductPropertyRepository;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;
import com.jda.mobility.framework.extensions.repository.TabRepository;
import com.jda.mobility.framework.extensions.repository.UserRoleRepository;
import com.jda.mobility.framework.extensions.service.ProductPrepareService;
import com.jda.mobility.framework.extensions.transformation.FormTransformation;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ActionType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DefaultType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.FormStatus;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffCommonUtil;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.FormDependencyUtil;

/**
 * The class FormServiceImplTest.java
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class FormServiceImplTest extends AbstractPrepareTest {

	@InjectMocks
	private FormServiceImpl formServiceImpl;

	@Mock
	private FormRepository formRepository;

	@Mock
	private CustomComponentMasterRepository customComponentMasterRepository;

	@Mock
	private ResourceBundleRepository resourceBundleRepo;

	@Mock
	private FlowRepository flowRepository;

	@Mock
	private FieldRepository fieldRepo;

	@Mock
	private ProductMasterRepository productMasterRepo;

	@Mock
	private ProductPropertyRepository productPropertyRepo;

	@Mock
	private ProductConfigRepository productConfigRepo;

	@Mock
	private FormCustomComponentRepository formCustomComponentRepo;

	@Mock
	private FormTransformation formTransformation;

	@Mock
	private EventsRepository eventRepo;

	@Mock
	private DataRepository dataRepo;

	@Mock
	private FieldValuesRepository fieldValRepo;

	@Mock
	private MenuMasterRepository menuMasterRepository;

	@Mock
	private MenuServiceImpl menuServiceImpl;

	@Mock
	private UserRoleRepository userRoleRepository;
	@Mock
	private TabRepository tabRepo;
	@Mock
	private ProductPrepareService productPrepareService;
	@Mock
	private FormDependencyRepository formDependencyRepository;
	@Mock
	private BffCommonUtil bffCommonUtil;
	@Mock
	private FormDependencyUtil formDependencyUtil;
	@Mock
	private AppConfigServiceImpl appConfigServiceImpl;
	
	
	

	/**
	 * Test method for createForm
	 * 
	 * @throws IOException
	 */
	@Test
	public void testCreateForm() throws IOException {
		FormObjDto formObjDto = new FormObjDto(getFormData(), new ArrayList<>(), UUID.randomUUID());
		formObjDto.setDefaultForm(true);
		List<FieldObjDto> fields = new ArrayList<>();
		FieldObjDto fieldObjDto = createChildFieldObjDto();
		fields.add(fieldObjDto);
		formObjDto.setFields(fields);
		Form form = getForm();
		FormData formData = getFormData();
		ProductConfig productConfig = new ProductConfig();
		when(formTransformation.checkUniqueFormName(Mockito.any(), Mockito.any())).thenReturn(true);
		when(formTransformation.convertToFormObjDto(Mockito.any())).thenReturn(formObjDto);
		when(formTransformation.convertToFormEntity(Mockito.any(), Mockito.any())).thenReturn(form);
		when(formTransformation.convertToFieldEntity(Mockito.any(), Mockito.any())).thenReturn(new Field());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(getFlow()));
		when(formRepository.save(form)).thenReturn(form);
		when(formTransformation.createFormData(Mockito.any())).thenReturn(formData);
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(productConfig);
		BffCoreResponse successResponse1 = formServiceImpl.createForm(formData, ActionType.CONFIRM_PUBLISH,
				DefaultType.CONFIRM_DEFAULT,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_CREATE_FORM.getCode(), successResponse1.getCode());
		assertEquals(StatusCode.CREATED.getValue(), successResponse1.getHttpStatusCode());
	}

	@Test
	public void testCreateFormChkUniqueName() {
		FormData formData = getFormData();
		when(formTransformation.checkUniqueFormName(Mockito.any(), Mockito.any())).thenReturn(false);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(new Flow()));
		BffCoreResponse successResponse1 = formServiceImpl.createForm(formData, ActionType.CONFIRM_PUBLISH,
				DefaultType.CONFIRM_DEFAULT,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.ERR_FORM_API_FLOW_GIVEN_FORM.getCode(), successResponse1.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), successResponse1.getHttpStatusCode());
	}

	@Test
	public void testCreateFormDefFormForFlowChk() {
		FormData formData = new FormData();
		formData.setDefaultForm(true);
		when(formTransformation.checkUniqueFormName(Mockito.any(), Mockito.any())).thenReturn(true);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(getFlow()));
		BffCoreResponse successResponse1 = formServiceImpl.createForm(formData, ActionType.CONFIRM_PUBLISH,
				DefaultType.CHECK_DEFAULT,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.ERR_FORM_API_CHECK_DEFAULT.getCode(), successResponse1.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), successResponse1.getHttpStatusCode());
	}

	@Test
	public void testCreateFormFormNotPresent() {
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.empty());
		BffCoreResponse response = formServiceImpl.createForm(new FormData(), ActionType.CONFIRM_PUBLISH,
				DefaultType.CHECK_DEFAULT,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.ERR_FORM_FLOW_NOT_FOUND.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testCreateFormFormDataIsDef() {
		FormObjDto formObjDto = new FormObjDto(getFormData(), new ArrayList<>(), UUID.randomUUID());
		Form form = getForm();		
		when(formRepository.save(form)).thenReturn(form);
		when(formTransformation.convertToFormEntity(Mockito.any(), Mockito.any())).thenReturn(form);
		when(formTransformation.convertToFormObjDto(Mockito.any())).thenReturn(formObjDto);
		when(formTransformation.checkUniqueFormName(Mockito.any(), Mockito.any())).thenReturn(true);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(getFlow()));
		BffCoreResponse response = formServiceImpl.createForm(getFormData(), ActionType.CHECK_PUBLISH,
				DefaultType.CHECK_DEFAULT, Arrays.asList("footprint"));
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_CREATE_FORM.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testCreateFormDefFormNull() {
		FormObjDto formObjDto = new FormObjDto(getFormData(), new ArrayList<>(), UUID.randomUUID());
		Form form = getForm();		
		when(formRepository.save(form)).thenReturn(form);
		when(formTransformation.convertToFormEntity(Mockito.any(), Mockito.any())).thenReturn(form);
		when(formTransformation.convertToFormObjDto(Mockito.any())).thenReturn(formObjDto);
		when(formTransformation.checkUniqueFormName(Mockito.any(), Mockito.any())).thenReturn(true);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(new Flow()));
		BffCoreResponse response = formServiceImpl.createForm(getFormData1(), ActionType.CHECK_PUBLISH,
				DefaultType.CHECK_DEFAULT, Arrays.asList("footprint"));
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_CREATE_FORM.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testCreateFormCnfrmDefault() {
		FormObjDto formObjDto = new FormObjDto(getFormData(), new ArrayList<>(), UUID.randomUUID());
		Form form = getForm();		
		when(formRepository.save(form)).thenReturn(form);
		when(formTransformation.convertToFormEntity(Mockito.any(), Mockito.any())).thenReturn(form);
		when(formTransformation.convertToFormObjDto(Mockito.any())).thenReturn(formObjDto);
		when(formTransformation.checkUniqueFormName(Mockito.any(), Mockito.any())).thenReturn(true);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(new Flow()));
		BffCoreResponse response = formServiceImpl.createForm(getFormData1(), ActionType.CHECK_PUBLISH,
				DefaultType.CONFIRM_DEFAULT, Arrays.asList("footprint"));
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_CREATE_FORM.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testCreateFormCnfrmDefault1() {
		FormObjDto formObjDto = new FormObjDto(getFormData(), new ArrayList<>(), UUID.randomUUID());
		Form form = getForm();		
		when(formRepository.save(form)).thenReturn(form);
		when(formTransformation.convertToFormEntity(Mockito.any(), Mockito.any())).thenReturn(form);
		when(formTransformation.convertToFormObjDto(Mockito.any())).thenReturn(formObjDto);
		when(formTransformation.checkUniqueFormName(Mockito.any(), Mockito.any())).thenReturn(true);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(new Flow()));
		BffCoreResponse response = formServiceImpl.createForm(getFormData(), ActionType.CHECK_PUBLISH,
				DefaultType.CONFIRM_DEFAULT, Arrays.asList("footprint"));
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_CREATE_FORM.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for createForm for Exception
	 */
	@Test
	public void testCreateFormException() {
		FormData formData = getFormData();
		when(flowRepository.findById(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = formServiceImpl.createForm(formData, ActionType.CHECK_PUBLISH,
				DefaultType.CHECK_DEFAULT,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.ERR_FORM_API_CREATE_FORM_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for createForm for DataBaseException
	 */
	@Test
	public void testCreateFormDataBaseException() {
		FormData formData = getFormData();
		when(productPrepareService.getCurrentLayerProdConfigId()).thenThrow(new DataBaseException("Product config details retrieval failed"));
		when(flowRepository.findById(Mockito.any())).thenThrow(new DataBaseException("Product config details retrieval failed"));
		BffCoreResponse response = formServiceImpl.createForm(formData, ActionType.CHECK_PUBLISH,
				DefaultType.CHECK_DEFAULT,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.DB_ERR_FORM_API_CREATE_FORM.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for modifyForm
	 * 
	 * @throws IOException
	 */
	@Test
	public void testModifyForm() throws IOException {
		FormObjDto formObjDto = new FormObjDto(getFormData(), new ArrayList<>(), UUID.randomUUID());
		List<TabDto> tabs = new ArrayList<>();
		TabDto tabDto = TabDto.builder().linkedFormId(UUID.randomUUID())
				.linkedFormName(BffAdminConstantsUtils.EMPTY_SPACES).tabId(UUID.randomUUID())
				.tabName(BffAdminConstantsUtils.EMPTY_SPACES).sequence(0).defaultForm(false).build();
		tabs.add(tabDto);
		formObjDto.setDefaultForm(true);
		formObjDto.setTabs(tabs);
		List<UUID> uuidList = new ArrayList<>();
		uuidList.add(UUID.randomUUID());
		formObjDto.setDeleteTabs(uuidList);
		Flow flow = getFlow();
		Form form = getForm();
		flow.setDefaultFormId(form.getUid());
		formObjDto.setFlowId(form.getFlow().getUid());
		FormData formData = getFormData();
		when(tabRepo.findById(Mockito.any())).thenReturn(Optional.of(new Tabs()));
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		Field field = new Field();
		field.setParentField(new Field());
		field.setLinkedComponentId(UUID.randomUUID());
		field.setType("customContainer");
		when(formTransformation.convertToFormObjDto(formData)).thenReturn(formObjDto);
		when(formTransformation.convertToFormEntity(Mockito.any(), Mockito.any())).thenReturn(form);
		when(fieldRepo.findById(Mockito.any())).thenReturn(Optional.of(field));
		when(formTransformation.convertToFieldEntity(Mockito.any(), Mockito.any())).thenReturn(new Field());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(flow));
		when(formRepository.save(form)).thenReturn(form);
		when(formTransformation.createFormData(Mockito.any())).thenReturn(formData);
		when(formTransformation.checkUniqueFormName(Mockito.any(), Mockito.any())).thenReturn(true);
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(productConfig);
		when(customComponentMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(getSampleCustomComponentMaster()));
		when(formCustomComponentRepo.findByFormAndCustomComponentMaster(Mockito.any(),Mockito.any())).thenReturn(getFormCustomComponent());
		BffCoreResponse successResponse = formServiceImpl.modifyForm(ActionType.CONFIRM_PUBLISH, formData,
				DefaultType.CONFIRM_DEFAULT,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_MODIFY_FORM.getCode(), successResponse.getCode());
		assertEquals(StatusCode.OK.getValue(), successResponse.getHttpStatusCode());
		BffCoreResponse successResponseForCheckDefault = formServiceImpl.modifyForm(ActionType.CONFIRM_PUBLISH,
				formData, DefaultType.CHECK_DEFAULT,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.ERR_FORM_API_CHECK_DEFAULT.getCode(), successResponseForCheckDefault.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), successResponseForCheckDefault.getHttpStatusCode());
		formObjDto.setDisabledForm(true);
		when(formTransformation.convertToFormObjDto(formData)).thenReturn(formObjDto);
		when(flowRepository.findById(formObjDto.getFlowId())).thenReturn(Optional.of(form.getFlow()));
		formObjDto.setDefaultForm(false);
		formObjDto.setDisabledForm(false);
		List<FieldObjDto> fields = new ArrayList<>();
		FieldObjDto fieldObjDto = createChildFieldObjDto();
		fields.add(fieldObjDto);
		formObjDto.setFields(fields);
		when(formTransformation.convertToFormObjDto(formData)).thenReturn(formObjDto);
		BffCoreResponse response3 = formServiceImpl.modifyForm(ActionType.CHECK_PUBLISH, formData,
				DefaultType.CHECK_DEFAULT,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.ERR_FORM_API_CHECK_PUBLISH.getCode(), response3.getCode());
		assertEquals(StatusCode.OK.getValue(), response3.getHttpStatusCode());
		BffCoreResponse response4 = formServiceImpl.modifyForm(ActionType.CONFIRM_PUBLISH, formData,
				DefaultType.CHECK_DEFAULT,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_MODIFY_FORM.getCode(), response4.getCode());
		assertEquals(StatusCode.OK.getValue(), response4.getHttpStatusCode());
	}

	@Test
	public void testModifyFormFlowNameNotUnq() {
		FormData formData = getFormData();
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(new Flow()));
		when(formTransformation.checkUniqueFormName(Mockito.any(), Mockito.any())).thenReturn(false);
		BffCoreResponse response = formServiceImpl.modifyForm(ActionType.CONFIRM_PUBLISH, formData,
				DefaultType.CHECK_DEFAULT,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.ERR_FORM_API_FLOW_GIVEN_FORM.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testModifyFormDisableForm() {
		FormObjDto formObjDto = new FormObjDto(getForm2(), null);
		formObjDto.setDisabledForm(true);
		formObjDto.setDefaultForm(true);
		when(formTransformation.convertToFormEntity(Mockito.any(), Mockito.any())).thenReturn(getForm());
		when(formTransformation.convertToFormObjDto(Mockito.any())).thenReturn(formObjDto);
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(new ProductConfig());
		BffCoreResponse response4 = formServiceImpl.modifyForm(ActionType.CONFIRM_PUBLISH, new FormData(),
				DefaultType.CONFIRM_DEFAULT,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.ERR_FORM_API_IS_DISABLED.getCode(), response4.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response4.getHttpStatusCode());
	}

	@Test
	public void testModifyFormFormNullDefForm() throws IOException {
		FormObjDto formObjDto = new FormObjDto(getForm2(), null);
		formObjDto.setDisabledForm(true);
		formObjDto.setDefaultForm(true);
		List<UUID> list = new ArrayList<>();
		list.add(UUID.randomUUID());
		formObjDto.setDeleteEvents(list);
		formObjDto.setDeleteValues(list);
		formObjDto.setDeleteDataValues(list);
		formObjDto.setDeleteFields(list);
		Form form = new Form();
		Flow flow = new Flow();
		flow.setDefaultFormId(null);
		form.setFlow(flow);
		Field field = new Field();
		field.setParentField(new Field());
		when(fieldRepo.findById(Mockito.any())).thenReturn(Optional.of(field));
		when(formTransformation.convertToFormEntity(Mockito.any(), Mockito.any())).thenReturn(form);
		when(formTransformation.convertToFormObjDto(Mockito.any())).thenReturn(formObjDto);
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(new ProductConfig());
		when(formRepository.save(Mockito.any())).thenReturn(form);
		when(formTransformation.createFormData(Mockito.any())).thenReturn(getFormData());
		BffCoreResponse response4 = formServiceImpl.modifyForm(ActionType.CONFIRM_PUBLISH, new FormData(),
				DefaultType.CHECK_DEFAULT,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_MODIFY_FORM.getCode(), response4.getCode());
		assertEquals(StatusCode.OK.getValue(), response4.getHttpStatusCode());
	}

	/**
	 * Test method for modifyForm for BffCoreException
	 */
	@Test
	public void testModifyFormException() {
		FormData formData = getFormData();
		when(formTransformation.convertToFormObjDto(formData)).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = formServiceImpl.modifyForm(ActionType.CHECK_PUBLISH, formData,
				DefaultType.CHECK_DEFAULT,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.ERR_FORM_API_UPDATE_FORM_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for modifyForm for DataBaseException
	 */
	@Test
	public void testModifyFormDataAccessException() {
		FormData formData = getFormData();
		when(flowRepository.findById(Mockito.any())).thenThrow(new DataBaseException("Flow retrieval failed"));
		BffCoreResponse response = formServiceImpl.modifyForm(ActionType.CHECK_PUBLISH, formData,
				DefaultType.CHECK_DEFAULT,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.DB_ERR_FORM_API_UPDATE_FORM.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getFormById
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetFormByIdRenderer() throws IOException {
		Form form = getForm2();
		form.setPublishedForm("{}".getBytes());
		when( bffCommonUtil.checkUserHasPermissionForFlow(Mockito.any(), Mockito.any())).thenReturn(true);
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(form));
		BffCoreResponse response = formServiceImpl.getFormById(UUID.randomUUID(), new ArrayList<String>(),
				UUID.randomUUID());
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_FETCH_FORM_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testGetFormByIdRendererwithoutPermisiions() throws IOException {
		Form form = getForm3();
		form.setPublishedForm("{}".getBytes());
		when( bffCommonUtil.checkUserHasPermissionForFlow(Mockito.any(), Mockito.any())).thenReturn(true);
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(form));
		BffCoreResponse response = formServiceImpl.getFormById(UUID.randomUUID(), new ArrayList<String>(),
				UUID.randomUUID());
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_FETCH_FORM_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testGetFormByIdRendererInvalidFlowPermission() {
		Form form = getForm2();
		form.setPublishedForm("{}".getBytes());
		when( bffCommonUtil.checkUserHasPermissionForFlow(Mockito.any(), Mockito.any())).thenReturn(false);
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(form));
		BffCoreResponse response = formServiceImpl.getFormById(UUID.randomUUID(), new ArrayList<String>(),
				UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FLOW_API_INVALID_FLOW_PERMISSION.getCode(), response.getCode());
		assertEquals(StatusCode.FORBIDDEN.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testGetFormByIdAdminUi() throws IOException {
		FormData formData = new FormData();
		FormProperties formProperties = new FormProperties();
		formData.setFormProperties(formProperties);
		Form form = getForm();
		form.setPublishedForm("{}".getBytes());
		Flow flow = new Flow();
		flow.setDefaultFormId(UUID.fromString("10608c5d-b455-4872-83ed-1fb4661b2514"));
		form.setFlow(flow);
		form.setUid(UUID.fromString("10608c5d-b455-4872-83ed-1fb4661b2514"));
		when(formTransformation.createFormData(Mockito.any())).thenReturn(formData);
		when(sessionDetails.getChannel()).thenReturn("ADMIN_UI");
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(form));
		BffCoreResponse response = formServiceImpl.getFormById(UUID.randomUUID(), new ArrayList<String>(),
				UUID.randomUUID());
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_FETCH_FORM_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetFormByIdNoForm() {
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.empty());
		BffCoreResponse response = formServiceImpl.getFormById(UUID.randomUUID(), new ArrayList<String>(),
				UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FORM_NOT_FOUND.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetFormByIdForMobileFlowNotPublished() {
		Form form = new Form();
		Flow flow = new Flow();
		form.setPublished(true);
		form.setFlow(flow);
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(form));
		BffCoreResponse response = formServiceImpl.getFormById(UUID.randomUUID(), new ArrayList<String>(),
				UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FLOW_UPUBLISH_CD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetFormByIdForMobileRendererNotpublished() throws IOException {
		Form form = new Form();
		form.setDisabled(false);
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(getForm2()));
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(new Flow()));
		BffCoreResponse response = formServiceImpl.getFormById(UUID.randomUUID(), new ArrayList<String>(),
				UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FORM_UPUBLISH_CD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetFormByIdForMobileRendererDisabled() throws IOException {
		Form form = getForm();
		form.setPublished(true);
		form.setDisabled(true);
		form.setPublishedForm("{}".getBytes());
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(form));
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(getFlow()));
		BffCoreResponse response = formServiceImpl.getFormById(UUID.randomUUID(), new ArrayList<String>(),
				UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FORM_DISABLE_CD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetFormByIdForMobileRendererFlowDisabled() throws IOException {
		Form form = getForm1();
		form.setPublished(true);
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(form));
		Flow flow = getFlow();
		flow.setDisabled(true);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(flow));
		BffCoreResponse response = formServiceImpl.getFormById(UUID.randomUUID(), new ArrayList<String>(),
				UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FLOW_DISABLE_CD.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getFormById for Exception
	 */
	@Test
	public void testGetFormByIdException() {
		when(formRepository.findById(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = formServiceImpl.getFormById(UUID.randomUUID(), new ArrayList<String>(),
				UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FORM_API_FETCH_FORM_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getFormById for DataBaseException
	 */
	@Test
	public void testGetFormByIdDataAccessException() {
		when(formRepository.findById(Mockito.any())).thenThrow(new DataBaseException("Form retrieval failed"));
		BffCoreResponse response = formServiceImpl.getFormById(UUID.randomUUID(), new ArrayList<String>(),
				UUID.randomUUID());
		assertEquals(BffResponseCode.DB_ERR_FORM_API_FETCH_FLOW_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for deleteForm
	 */
	@Test
	public void testDeleteFormById() {
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(getForm()));
		BffCoreResponse response = formServiceImpl.deleteFormByID(UUID.randomUUID(),
				BffAdminConstantsUtils.DeleteType.CONFIRM_DELETE);
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_DELETE_FORM_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
		Form form1 = getForm();
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(form1));
		BffCoreResponse response2 = formServiceImpl.deleteFormByID(UUID.randomUUID(),
				BffAdminConstantsUtils.DeleteType.CONFIRM_DELETE);
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_DELETE_FORM_BY_ID.getCode(), response2.getCode());
		assertEquals(StatusCode.OK.getValue(), response2.getHttpStatusCode());

	}

	/**
	 * Test method for deleteForm Exception
	 */
	@Test
	public void testDeleteFormByIdException() {
		when(formRepository.findById(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = formServiceImpl.deleteFormByID(UUID.randomUUID(),
				BffAdminConstantsUtils.DeleteType.CONFIRM_DELETE);
		assertEquals(BffResponseCode.ERR_FORM_API_DELETE_FORM_BY_ID_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testDeleteFormByIdDataBaseException() {

		when(formRepository.findById(Mockito.any())).thenThrow(new DataBaseException("Form retrieval failed"));
		BffCoreResponse response = formServiceImpl.deleteFormByID(UUID.randomUUID(),
				BffAdminConstantsUtils.DeleteType.CONFIRM_DELETE);
		assertEquals(BffResponseCode.DB_ERR_FORM_API_DELETE_FORM_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testDeleteFormByIdNoSuchElementException() {
		when(formRepository.findById(Mockito.any())).thenThrow(new NoSuchElementException("Fom retrieval failed"));
		BffCoreResponse response = formServiceImpl.deleteFormByID(UUID.randomUUID(),
				BffAdminConstantsUtils.DeleteType.CONFIRM_DELETE);
		assertEquals(BffResponseCode.ERR_FORM_API_DELETE_FORM_BY_ID.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for testFetchAllForms
	 * 
	 * @throws IOException
	 */

	@Test
	public void testFetchAllForms() throws IOException {
		String flowId = "2e82c433-fb5b-4257-8f79-ac4998569b4c";
		List<MenuListDto> menuListDtoList = new ArrayList<>();
		List<MenuListDto> subMenus = new ArrayList<>();
		MenuListDto menuListDto1 = new MenuListDto();
		subMenus.add(menuListDto1);
		MenuListDto menuListDto = new MenuListDto();
		menuListDto.setSubMenus(subMenus);
		menuListDtoList.add(menuListDto);
		MenuDto menuDto = new MenuDto();
		menuDto.setMenus(menuListDtoList);
		BffCoreResponse response1 = new BffCoreResponse();
		DetailResponse<MenuDto> details = new DetailResponse<>();
		details.setData(menuDto);
		response1.setDetails(details);
		when(menuServiceImpl.fetchMenusByFormId(Mockito.any(), Mockito.any(), Mockito.anyBoolean())).thenReturn(response1);
		when(flowRepository.findById(UUID.fromString(flowId))).thenReturn(Optional.of(getFlow()));
		when(formTransformation.createFormData(Mockito.any())).thenReturn(getFormData());
		when(formTransformation.convertToFieldDto(Mockito.any())).thenReturn(new ArrayList<FieldObjDto>());
		BffCoreResponse response = formServiceImpl.fetchAllForms(UUID.fromString(flowId));
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_FETCH_ALL_FORMS.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchAllFormsEmptyResponse() throws IOException {
		String flowId = "2e82c433-fb5b-4257-8f79-ac4998569b4c";
		when(menuServiceImpl.fetchMenusByFormId(Mockito.any(), Mockito.any(), Mockito.anyBoolean())).thenReturn(new BffCoreResponse());
		when(flowRepository.findById(UUID.fromString(flowId))).thenReturn(Optional.of(getFlow()));
		when(formTransformation.createFormData(Mockito.any())).thenReturn(getFormData());
		when(formTransformation.convertToFieldDto(Mockito.any())).thenReturn(new ArrayList<FieldObjDto>());
		BffCoreResponse response = formServiceImpl.fetchAllForms(UUID.fromString(flowId));
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_FETCH_ALL_FORMS.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchAllFormsFlowNotPresnt() {
		when(flowRepository.findById(UUID.randomUUID())).thenReturn(Optional.empty());
		BffCoreResponse response = formServiceImpl.fetchAllForms(UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FORM_NOT_FOUND.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for testFetchAllForms_DataAccessException
	 */

	@Test
	public void testFetchAllFormsDataAccessException() {
		String flowId = "2e82c433-fb5b-4257-8f79-ac4998569b4c";
		when(flowRepository.findById(UUID.fromString(flowId))).thenThrow(new DataBaseException("Flow retrieval failed"));
		BffCoreResponse response = formServiceImpl.fetchAllForms(UUID.fromString(flowId));
		assertEquals(BffResponseCode.DB_ERR_FORM_API_FETCH_ALL_FORMS.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for testFetchAllForms_Exception
	 * 
	 * @throws IOException
	 */

	@Test
	public void testFetchAllFormsException() throws IOException {
		UUID flowId = UUID.randomUUID();
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(getFlow()));
		when(formTransformation.convertToFieldDto(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = formServiceImpl.fetchAllForms(flowId);
		assertEquals(BffResponseCode.ERR_FORM_API_FETCH_ALL_FORMS_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for testFetchOrphanForms
	 * 
	 * @throws IOException
	 */

	@Test
	public void testFetchOrphanForms() throws IOException {
		List<Form> orphanForms = new ArrayList<>();
		orphanForms.add(getForm());
		FormData formData = getFormData();
		when(formTransformation.createFormData(Mockito.any())).thenReturn(formData);
		when(formRepository.getOrphanFormsByFlowId(Mockito.any())).thenReturn(orphanForms);
		BffCoreResponse response = formServiceImpl
				.fetchOrphanForms(UUID.fromString("2e82c433-fb5b-4257-8f79-ac4998569b4c"));
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_FETCH_ORPHAN_FORMS.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchOrphanFormsDataAccessException() {
		UUID flowId = UUID.randomUUID();
		when(formRepository.getOrphanFormsByFlowId(flowId)).thenThrow(new DataBaseException("Form retrieval failed"));
		BffCoreResponse response = formServiceImpl.fetchOrphanForms(flowId);
		assertEquals(BffResponseCode.DB_ERR_FORM_API_FETCH_ORPHAN_FORMS.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchOrphanFormsException() throws IOException {
		String flowId = "2e82c433-fb5b-4257-8f79-ac4998569b4c";
		List<Form> forms = new ArrayList<>();
		forms.add(getForm1());
		when(formRepository.getOrphanFormsByFlowId(Mockito.any())).thenReturn(forms);
		when(formTransformation.createFormData(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = formServiceImpl.fetchOrphanForms(UUID.fromString(flowId));
		assertEquals(BffResponseCode.ERR_FORM_API_FETCH_ORPHAN_FORMS_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for testFetchUnpublishForms
	 */

	@Test
	public void testFetchUnpublishForms() {
		List<ProductConfig> productconfigList = new ArrayList<>();
		RoleMaster roleMaster = new RoleMaster();
		ProductConfig prodConfig = new ProductConfig();
		prodConfig.setUid(UUID.fromString("0225c416-d15c-4c09-b01a-ae0ed50f17f7"));
		prodConfig.setRoleMaster(roleMaster);
		productconfigList.add(prodConfig);
		List<Form> formList = new ArrayList<Form>();
		Form form = new Form();
		formList.add(form);
		Flow flow = new Flow();
		flow.setProductConfig(prodConfig);
		form.setFlow(flow);
		when(formRepository.findByIsPublishedFalseAndProductConfigIdInOrderByLastModifiedDateDesc(Mockito.any()))
				.thenReturn(formList);
		when(productPrepareService.getLayeredProductConfigList()).thenReturn(productconfigList);
		BffCoreResponse response = formServiceImpl.fetchUnpublishForms();
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_FECTH_UNPUBLISH_FORMS.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());

	}

	@Test
	public void testFetchUnpublishFormsBffExcption() {
		when(productPrepareService.getLayeredProductConfigList()).thenReturn(new ArrayList<>());
		BffCoreResponse response = formServiceImpl.fetchUnpublishForms();
		assertEquals(BffResponseCode.ERR_FORM_API_FECTH_UNPUBLISH_FORMS.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchUnpublishFormsDataAccessException() {
		when(productPrepareService.getLayeredProductConfigList()).thenThrow(new DataBaseException("Product config details retrieval failed"));
		BffCoreResponse response = formServiceImpl.fetchUnpublishForms();
		assertEquals(BffResponseCode.DB_ERR_FORM_API_015_FECTH_UNPUBLISH_FORMS.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchUnpublishFormsException() {
		when(productMasterRepo.findByName(BffAdminConstantsUtils.WMS)).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = formServiceImpl.fetchUnpublishForms();
		assertEquals(BffResponseCode.ERR_FORM_API_FECTH_UNPUBLISH_FORMS.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for testCreateDefaultForm
	 */

	@Test
	public void testCreateDefaultForm() {
		String formId = "2e82c433-fb5b-4257-8f79-ac4998569b4c";
		Form form = getForm();
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(form));
		when(formRepository.findById(UUID.fromString(formId))).thenReturn(Optional.of(getForm()));
		formServiceImpl.createDefaultForm(UUID.fromString(formId), BffAdminConstantsUtils.DefaultType.CHECK_DEFAULT);
		form.getFlow().setDefaultFormId(null);
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(form));
		formServiceImpl.createDefaultForm(UUID.fromString(formId), BffAdminConstantsUtils.DefaultType.CHECK_DEFAULT);
		BffCoreResponse response1 = formServiceImpl.createDefaultForm(UUID.fromString(formId),
				BffAdminConstantsUtils.DefaultType.CONFIRM_DEFAULT);
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_CREATE_DEFAULT_FORM.getCode(), response1.getCode());
		assertEquals(StatusCode.OK.getValue(), response1.getHttpStatusCode());
	}

	@Test
	public void testCreateDefaultFormDefFormExist() {
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(getForm1()));
		BffCoreResponse response1 = formServiceImpl.createDefaultForm(
				UUID.fromString("2e82c433-fb5b-4257-8f79-ac4998569b4c"),
				BffAdminConstantsUtils.DefaultType.CHECK_DEFAULT);
		assertEquals(BffResponseCode.ERR_FORM_API_ENUM_CHECK_DEFAULT.getCode(), response1.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response1.getHttpStatusCode());
	}

	@Test
	public void testCreateDefaultFormFormNotPresent() {
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.empty());
		BffCoreResponse response1 = formServiceImpl.createDefaultForm(UUID.randomUUID(),
				BffAdminConstantsUtils.DefaultType.CONFIRM_DEFAULT);
		assertEquals(BffResponseCode.ERR_FORM_NOT_FOUND.getCode(), response1.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response1.getHttpStatusCode());
	}

	@Test
	public void testCreateDefaultFormDataAccessException() {
		String flowId = "2e82c433-fb5b-4257-8f79-ac4998569b4c";
		when(formRepository.findById(UUID.fromString(flowId))).thenThrow(new DataBaseException("Form retrieval failed"));
		BffCoreResponse response = formServiceImpl.createDefaultForm(UUID.fromString(flowId),
				DefaultType.CONFIRM_DEFAULT);
		assertEquals(BffResponseCode.DB_ERR_FORM_API_CREATE_DEFAULT_FORM.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testCreateDefaultFormException() {
		UUID formId = UUID.randomUUID();
		when(formRepository.findById(formId)).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = formServiceImpl.createDefaultForm(formId, DefaultType.CONFIRM_DEFAULT);
		assertEquals(BffResponseCode.ERR_FORM_API_CREATE_DEFAULT_FORM_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getCustomComponent
	 * 
	 * @throws IOException
	 */

	@Test
	public void testGetFormDetails() throws IOException {
		when(customComponentMasterRepository.findById(Mockito.any()))
				.thenReturn(Optional.of(getSampleCustomComponentMaster()));
		List<FieldObjDto> fieldList = new ArrayList<FieldObjDto>();
		when(formTransformation.convertToFieldDto(Mockito.any())).thenReturn(fieldList);
		BffCoreResponse response = formServiceImpl.getFormDetails(UUID.randomUUID());
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_FORM_DETAILS.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetFormDetailsNoCustomComp() {
		when(customComponentMasterRepository.findById(Mockito.any())).thenReturn(Optional.empty());
		BffCoreResponse response = formServiceImpl.getFormDetails(UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_CUSTOM_CTRL_NOT_FOUND.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getCustomComponent
	 */

	@Test
	public void testGetFormDetailsDatabaseException() {
		when(customComponentMasterRepository.findById(Mockito.any())).thenThrow(new DataBaseException("Custom control retrieval failed"));
		BffCoreResponse response = formServiceImpl.getFormDetails(UUID.randomUUID());
		assertEquals(BffResponseCode.DB_ERR_FORM_API_FORM_DETAILS.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getCustomComponent
	 * @throws IOException 
	 */

	@Test
	public void testGetFormDetailsException() throws IOException {
		CustomComponentMaster customComponentMaster = new CustomComponentMaster();
		when(customComponentMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(customComponentMaster));
		when(formTransformation.createFormData(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = formServiceImpl.getFormDetails(UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FORM_API_FORM_DETAILS.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testPublishForm() throws IOException {
		Form form = getForm();
		form.setDisabled(true);
		List<FormDependency> formDependencies = new ArrayList<>();
		FormDependency formDependency = new FormDependency();
		formDependency.setInboundFormId(UUID.fromString("10608c5d-b455-4872-83ed-1fb4661b2514"));
		formDependency.setOutboundFormId(UUID.fromString("10608c5d-b455-4872-83ed-1fb4661b2514"));
		formDependencies.add(formDependency);
		List<Form> forms = new ArrayList<>();
		forms.add(form);
		when(formRepository.findByPublishedFormNotNullAndUidIn(Mockito.any())).thenReturn(forms);
		when(formDependencyRepository.findByInboundFormIdOrOutboundFormId(Mockito.any(), Mockito.any()))
				.thenReturn(formDependencies);
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(form));
		when(formTransformation.convertToFieldDto(Mockito.any())).thenReturn(new ArrayList<FieldObjDto>());
		when(formTransformation.createFormData(Mockito.any())).thenReturn(getFormData());
		formServiceImpl.publishForm(UUID.randomUUID(), ActionType.CHECK_PUBLISH,Arrays.asList("footprint"));
		BffCoreResponse response2 = formServiceImpl.publishForm(UUID.randomUUID(), ActionType.CONFIRM_PUBLISH,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_PUBLISH_FORM.getCode(), response2.getCode());
		assertEquals(StatusCode.OK.getValue(), response2.getHttpStatusCode());

		formServiceImpl.publishForm(UUID.randomUUID(), ActionType.CHECK_PUBLISH,Arrays.asList("footprint"));
		BffCoreResponse response3 = formServiceImpl.publishForm(UUID.randomUUID(), ActionType.CHECK_PUBLISH,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.ERR_FORM_API_CHECK_PUBLISH.getCode(), response3.getCode());
		assertEquals(StatusCode.OK.getValue(), response3.getHttpStatusCode());
	}

	@Test
	public void testPublishFormNoForm() {
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.empty());
		BffCoreResponse response3 = formServiceImpl.publishForm(UUID.randomUUID(), ActionType.CHECK_PUBLISH,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.ERR_FORM_NOT_FOUND.getCode(), response3.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response3.getHttpStatusCode());
	}

	@Test
	public void testPublishFormFormNotDisabled() {
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(new Form()));
		BffCoreResponse response = formServiceImpl.publishForm(UUID.randomUUID(), ActionType.CHECK_PUBLISH,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.ERR_FORM_API_CHECK_PUBLISH.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testPublishFormException() throws IOException {
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(getForm()));
		when(formTransformation.convertToFieldDto(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = formServiceImpl.publishForm(UUID.randomUUID(), ActionType.CONFIRM_PUBLISH,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.ERR_FORM_API_PUBLISH_FORM_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testPublishFormDatabaseException() {
		when(formRepository.findById(Mockito.any())).thenThrow(new DataBaseException("Form retrieval failed"));
		BffCoreResponse response = formServiceImpl.publishForm(UUID.randomUUID(), ActionType.CONFIRM_PUBLISH,Arrays.asList("footprint"));
		assertEquals(BffResponseCode.DB_ERR_FORM_API_PUBLISH_FORM.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchFormBasicList() {
		List<FormLiteDto> formLiteDtoList = new ArrayList<>();
		FormLiteDto formLiteDto = new FormLiteDto(null, null, false, false);
		formLiteDtoList.add(formLiteDto);
		when(formRepository.getFormBasicList(Mockito.any())).thenReturn(formLiteDtoList);
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(getFlow()));
		BffCoreResponse response = formServiceImpl
				.fetchFormBasicList(UUID.fromString("2e82c433-fb5b-4257-8f79-ac4998569b4c"));
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_FETCH_FORM_NAMES.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchFormBasicListForEmpty() {
		Optional<Flow> empty = Optional.empty();
		when(flowRepository.findById(Mockito.any())).thenReturn(empty);
		BffCoreResponse response = formServiceImpl.fetchFormBasicList(UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FORM_API_FETCH_FORM_NAMES.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchFormBasicListEmptyForm() {
		Flow flow = getFlow();
		flow.setForms(new ArrayList<>());
		when(flowRepository.findById(Mockito.any())).thenReturn(Optional.of(flow));
		BffCoreResponse response = formServiceImpl.fetchFormBasicList(UUID.randomUUID());
		assertEquals(BffResponseCode.ERR_FORM_API_FETCH_FORM_LIST_EMPTY_CHECK.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchFormBasicListDataAccessException() {
		when(flowRepository.findById(Mockito.any())).thenThrow(new DataBaseException("Flow retrieval failed"));
		BffCoreResponse response = formServiceImpl.fetchFormBasicList(Mockito.any());
		assertEquals(BffResponseCode.DB_ERR_FORM_API_FETCH_FORM_NAMES.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchFormBasicListException() {
		when(flowRepository.findById(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = formServiceImpl.fetchFormBasicList(Mockito.any());
		assertEquals(BffResponseCode.ERR_FORM_API_FETCH_FORM_NAMES_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchUnpublishOrphanForms() {

		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setLevel(1);

		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		productConfig.setRoleMaster(roleMaster);
		List<ProductConfig> prodConfigList = new ArrayList<>();
		prodConfigList.add(productConfig);
		when(productPrepareService.getLayeredProductConfigList()).thenReturn(prodConfigList);
		List<Form> formList = new ArrayList<>();
		Flow flow = new Flow();
		flow.setUid(UUID.randomUUID());
		flow.setProductConfig(productConfig);
		Form form = new Form();
		form.setFlow(flow);
		form.setUid(UUID.randomUUID());
		form.setProductConfigId(productConfig.getUid());
		formList.add(form);
		when(formRepository.findByProductConfigIdInOrderByLastModifiedDateDesc(Arrays.asList(productConfig.getUid())))
				.thenReturn(formList);

		BffCoreResponse response = formServiceImpl.fetchUnpublishOrphanForms(UUID.randomUUID(), FormStatus.ALL);
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_ORPHAN_FORMS.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
		when(formRepository.findByIsOrphanTrueAndProductConfigIdInOrderByLastModifiedDateDesc(
				Arrays.asList(productConfig.getUid()))).thenReturn(formList);
		BffCoreResponse response2 = formServiceImpl.fetchUnpublishOrphanForms(UUID.randomUUID(), FormStatus.ORPHAN);
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_ORPHAN_FORMS.getCode(), response2.getCode());
		assertEquals(StatusCode.OK.getValue(), response2.getHttpStatusCode());
		when(formRepository.findByIsPublishedFalseAndProductConfigIdInOrderByLastModifiedDateDesc(
				Arrays.asList(productConfig.getUid()))).thenReturn(formList);
		BffCoreResponse response3 = formServiceImpl.fetchUnpublishOrphanForms(UUID.randomUUID(), FormStatus.UNPUBLISH);
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_UNPUBLISH_ORPHAN_FORMS.getCode(), response3.getCode());
		assertEquals(StatusCode.OK.getValue(), response3.getHttpStatusCode());
	}

	@Test
	public void testFetchUnpublishOrphanFormsDataAccessException() {
		when(productPrepareService.getLayeredProductConfigList()).thenThrow(new DataBaseException("Product config details retrieval failed"));
		BffCoreResponse response = formServiceImpl.fetchUnpublishOrphanForms(null, FormStatus.ORPHAN);
		assertEquals(BffResponseCode.DB_ERR_FORM_API_UNPUBLISH_ORPHAN_FORMS.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testFetchUnpublishOrphanFormsException() {
		when(productPrepareService.getLayeredProductConfigList()).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = formServiceImpl.fetchUnpublishOrphanForms(null, FormStatus.ORPHAN);
		assertEquals(BffResponseCode.ERR_FORM_API_UNPUBLISH_ORPHAN_FORMS_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetForm() {
		List<AppConfigRequest> appConfigList = new ArrayList<>();
		AppConfigRequest appConfigRequest = new AppConfigRequest();
		appConfigList.add(appConfigRequest);
		Form form = getForm();
		form.setPublishedForm("{}".getBytes());
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(form));
		when(bffCommonUtil.checkUserHasPermissionForFlow(Mockito.any(), Mockito.any())).thenReturn(true);
		List<String> permissionIds = new ArrayList<>();
		BffCoreResponse bffCoreResponse = formServiceImpl.getForm(UUID.randomUUID(), permissionIds, appConfigList);
		assertEquals(BffResponseCode.FORM_SUCCESS_CODE_FETCH_FORM_BY_ID.getCode(), bffCoreResponse.getCode());
		assertEquals(StatusCode.OK.getValue(), bffCoreResponse.getHttpStatusCode());
	}

	@Test
	public void testGetFormInvalidPermissions() {
		List<AppConfigRequest> appConfigList = new ArrayList<>();
		AppConfigRequest appConfigRequest = new AppConfigRequest();
		appConfigList.add(appConfigRequest);
		Form form = getForm();
		form.setPublishedForm("{}".getBytes());
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(form));
		when(bffCommonUtil.checkUserHasPermissionForFlow(Mockito.any(), Mockito.any())).thenReturn(false);
		List<String> permissionIds = new ArrayList<>();
		BffCoreResponse bffCoreResponse = formServiceImpl.getForm(UUID.randomUUID(), permissionIds, appConfigList);
		assertEquals(BffResponseCode.ERR_FLOW_API_INVALID_FLOW_PERMISSION.getCode(), bffCoreResponse.getCode());
		assertEquals(StatusCode.FORBIDDEN.getValue(), bffCoreResponse.getHttpStatusCode());
	}

	@Test
	public void testGetFormFormNotPresent() {
		List<AppConfigRequest> appConfigList = new ArrayList<>();
		AppConfigRequest appConfigRequest = new AppConfigRequest();
		appConfigList.add(appConfigRequest);
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.empty());
		when(bffCommonUtil.checkUserHasPermissionForFlow(Mockito.any(), Mockito.any())).thenReturn(false);
		List<String> permissionIds = new ArrayList<>();
		BffCoreResponse bffCoreResponse = formServiceImpl.getForm(UUID.randomUUID(), permissionIds, appConfigList);
		assertEquals(BffResponseCode.ERR_FORM_NOT_FOUND.getCode(), bffCoreResponse.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), bffCoreResponse.getHttpStatusCode());
	}

	@Test
	public void testGetFormDbException() {
		List<AppConfigRequest> appConfigList = new ArrayList<>();
		AppConfigRequest appConfigRequest = new AppConfigRequest();
		appConfigList.add(appConfigRequest);
		when(formRepository.findById(Mockito.any())).thenThrow(new DataBaseException("Form retrieval failed"));
		List<String> permissionIds = new ArrayList<>();
		BffCoreResponse bffCoreResponse = formServiceImpl.getForm(UUID.randomUUID(), permissionIds, appConfigList);
		assertEquals(BffResponseCode.DB_ERR_FORM_API_FETCH_FLOW_BY_ID.getCode(), bffCoreResponse.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), bffCoreResponse.getHttpStatusCode());
	}

	@Test
	public void testGetFormException() {
		List<AppConfigRequest> appConfigList = new ArrayList<>();
		AppConfigRequest appConfigRequest = new AppConfigRequest();
		appConfigList.add(appConfigRequest);
		when(formRepository.findById(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		List<String> permissionIds = new ArrayList<>();
		BffCoreResponse bffCoreResponse = formServiceImpl.getForm(UUID.randomUUID(), permissionIds, appConfigList);
		assertEquals(BffResponseCode.ERR_FORM_API_FETCH_FORM_BY_ID.getCode(), bffCoreResponse.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), bffCoreResponse.getHttpStatusCode());
	}

	private FieldObjDto createChildFieldObjDto() {
		List<FieldObjDto> childFieldObjDtoList = new ArrayList<>();
		FieldObjDto childFieldObjDto = new FieldObjDto(createFieldComponent(),
				UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"), null, new ArrayList<>(), 1, UUID.randomUUID());
		childFieldObjDtoList.add(childFieldObjDto);
		FieldObjDto fieldObjDto = new FieldObjDto(createFieldComponent(),
				UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"), null, childFieldObjDtoList, 1,
				UUID.randomUUID());
		return fieldObjDto;
	}

	private FieldComponent createFieldComponent() {
		FieldComponent fieldcomp = new FieldComponent();
		fieldcomp.setFieldId(UUID.randomUUID());
		fieldcomp.setLabel(TranslationRequest.builder().locale(BffAdminConstantsUtils.LOCALE).rbkey("1000")
				.rbvalue("Test").type("INTERNAL").uid(UUID.randomUUID()).build());
		fieldcomp.setMask(false);
		fieldcomp.setTableView(false);
		fieldcomp.setAlwaysEnabled(false);
		fieldcomp.setType("textfield");
		fieldcomp.setInput(false);
		fieldcomp.setKey("textField33");
		fieldcomp.setComponents(new ArrayList<>());
		fieldcomp.setHideLabel(false);
		fieldcomp.setCustomClass("");
		fieldcomp.setAlignment("left");
		Style style = new Style();
		style.setStyle("primary");
		style.setFontType("small");
		style.setFontSize("12");
		style.setFontColor("secondary");
		style.setBackgroundColor("primary");
		style.setFontWeight("bold");
		style.setWidth("20");
		style.setHeight("20");
		style.setPadding("0,0,0,20");
		style.setMargin("0,0,0,20");
		fieldcomp.setStyle(style);
		fieldcomp.setIcon(true);
		fieldcomp.setAutoCorrect(false);
		fieldcomp.setCapitalization(false);
		fieldcomp.setDefaultValue("");
		Validate validate = new Validate();
		validate.setMaxLength(0.01d);
		validate.setMinLength(0.01d);
		validate.setInteger("");
		fieldcomp.setValidate(validate);
		FieldDependency fieldDependency = new FieldDependency();
		fieldcomp.setFieldDependency(fieldDependency);
		List<Event> eventList = new ArrayList<>();
		fieldcomp.setEvents(eventList);
		fieldcomp.setCustomComponentId(null);
		fieldcomp.setIconAlignment("left");
		fieldcomp.setImageSource("https://cdn2.iconfinder.com/data/icons/pittogrammi/142/32-48.png");
		fieldcomp.setFormat("YYYY-MM-DD");
		fieldcomp.setAllowInput(true);
		fieldcomp.setEnableDate(true);
		DatePicker datePicker = new DatePicker();
		fieldcomp.setDatePicker(datePicker);
		fieldcomp.setMaxDate(null);
		fieldcomp.setMinDate(null);
		fieldcomp.setFontColor("Secondary");
		fieldcomp.setRadius("");
		fieldcomp.setWidth("");
		fieldcomp.setHeight("");
		fieldcomp.setButtonType("rectangle");
		fieldcomp.setBackgroundColor("");
		fieldcomp.setInline(false);
		LabelDetails value = new LabelDetails();
		value.setLabel(TranslationRequest.builder().locale(BffAdminConstantsUtils.LOCALE).rbkey("1000").rbvalue("Test")
				.type("INTERNAL").uid(UUID.randomUUID()).build());
		value.setValue("abc");
		List<LabelDetails> valueList = new ArrayList<>();
		valueList.add(value);
		fieldcomp.setValues(valueList);
		fieldcomp.setData(new com.jda.mobility.framework.extensions.model.Data());
		fieldcomp.setValueProperty("value");
		fieldcomp.setLazyLoad(false);
		fieldcomp.setDescription(TranslationRequest.builder().locale(BffAdminConstantsUtils.LOCALE).rbkey("1000")
				.rbvalue("Test").type("INTERNAL").uid(UUID.randomUUID()).build());
		fieldcomp.setSelectValues("");
		fieldcomp.setDisableLimit(false);
		fieldcomp.setSort("");
		fieldcomp.setReference(false);
		fieldcomp.setCustomFormat("HH:mm:ss");
		IconInfo iconInfo = new IconInfo();
		iconInfo.setIconName("iconName");
		iconInfo.setIconCode("iconCode");
		fieldcomp.setIconInfo(iconInfo);
		List<FieldComponent> components = new ArrayList<>();
		FieldComponent fieldComponent = new FieldComponent();
		fieldComponent.setType("columns");
		List<FieldComponent> columns = new ArrayList<>();
		FieldComponent column = new FieldComponent();
		column.setType("columns");
		columns.add(column);
		fieldComponent.setColumns(columns);
		components.add(fieldComponent);
		fieldcomp.setComponents(components);
		return fieldcomp;
	}

	private Form getForm2() {
		Form form = new Form();
		Form form1 = new Form();
		form.setUid(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"));
		form.setParentFormId(UUID.randomUUID());
		Flow flow = new Flow();
		ProductConfig config = new ProductConfig();
		config.setUid(UUID.randomUUID());
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setName("role master");
		roleMaster.setLevel(1);
		config.setRoleMaster(roleMaster);
		flow.setProductConfig(config);
		flow.setUid(UUID.randomUUID());
		flow.setPublishedFlow(true);
		List<Form> forms = new ArrayList<Form>();
		form.setName("str1");
		form.setPublished(true);
		form.setDisabled(false);
		form.setPublished(false);
		forms.add(form);
		forms.add(form1);
		flow.setForms(forms);
		flow.setDefaultFormId(form.getUid());
		flow.setPublished(true);
		List<FlowPermission> flowPermissions = new ArrayList<>();
		FlowPermission flowPermission = new FlowPermission();
		flowPermissions.add(flowPermission);
		flow.setFlowPermission(flowPermissions);
		form.setFlow(flow);
		FormDependent formIndep = new FormDependent();
		formIndep.setUid(UUID.randomUUID());
		FormDependency formOutdep = new FormDependency();
		formOutdep.setUid(UUID.randomUUID());
		return form;
	}
	
	private Form getForm3() {
		Form form = new Form();
		form.setUid(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"));
		form.setParentFormId(UUID.randomUUID());
		Flow flow = new Flow();
		ProductConfig config = new ProductConfig();
		config.setUid(UUID.randomUUID());
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setName("role master");
		roleMaster.setLevel(1);
		config.setRoleMaster(roleMaster);
		flow.setProductConfig(config);
		flow.setUid(UUID.randomUUID());
		flow.setPublishedFlow(true);
		List<Form> forms = new ArrayList<Form>();
		form.setName("str1");
		form.setPublished(true);
		form.setDisabled(false);
		form.setPublished(false);
		forms.add(form);
		flow.setForms(forms);
		flow.setDefaultFormId(form.getUid());
		flow.setPublished(true);
		form.setFlow(flow);
		FormDependent formIndep = new FormDependent();
		formIndep.setUid(UUID.randomUUID());
		FormDependency formOutdep = new FormDependency();
		formOutdep.setUid(UUID.randomUUID());
		return form;
	}

	private FormData getFormData() {
		FormData formdata = new FormData();
		formdata.setName("login");
		formdata.setDescription("description");
		FormProperties formProperties = new FormProperties();
		formProperties.setProperties(new FormAttributes());
		MenuListRequest menuListRequest = new MenuListRequest();
		List<MenuListRequest> menus = new ArrayList<>();
		menus.add(menuListRequest);
		List<UUID> deleteField = new ArrayList<>();
		deleteField.add(UUID.randomUUID());
		formdata.setDeleteFields(deleteField);
		formProperties.setMenus(menus);
		formdata.setFormProperties(formProperties);
		formdata.setFormId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"));
		return formdata;

	}
	
	private FormData getFormData1() {
		FormData formdata = new FormData();
		formdata.setName("login");
		formdata.setDescription("description");
		FormProperties formProperties = new FormProperties();
		formProperties.setProperties(new FormAttributes());
		MenuListRequest menuListRequest = new MenuListRequest();
		List<MenuListRequest> menus = new ArrayList<>();
		menus.add(menuListRequest);
		List<UUID> deleteField = new ArrayList<>();
		deleteField.add(UUID.randomUUID());
		formdata.setDeleteFields(deleteField);
		formProperties.setMenus(menus);
		formdata.setFormProperties(formProperties);
		formdata.setFormId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"));
		formdata.setDefaultForm(true);
		return formdata;

	}

	private Form getForm() {
		Form form = new Form();
		form.setUid(UUID.randomUUID());
		form.setParentFormId(UUID.randomUUID());
		Field field = new Field();
		field.setForm(form);
		field.setUid(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"));
		CustomComponentMaster customComponentMaster = new CustomComponentMaster();
		customComponentMaster.setUid(UUID.randomUUID());
		ResourceBundle resourceBundle1 = new ResourceBundle();
		resourceBundle1.setUid(UUID.randomUUID());
		resourceBundle1.setLocale(BffAdminConstantsUtils.EMPTY_SPACES);
		resourceBundle1.setRbkey(BffAdminConstantsUtils.EMPTY_SPACES);
		resourceBundle1.setRbvalue(BffAdminConstantsUtils.EMPTY_SPACES);
		ResourceBundle resourceBundle2 = new ResourceBundle();
		resourceBundle2.setUid(UUID.randomUUID());
		resourceBundle2.setLocale(BffAdminConstantsUtils.EMPTY_SPACES);
		resourceBundle2.setRbkey(BffAdminConstantsUtils.EMPTY_SPACES);
		resourceBundle2.setRbvalue(BffAdminConstantsUtils.EMPTY_SPACES);

		form.setFields(Arrays.asList(field));
		Flow flow = new Flow();
		ProductConfig config = new ProductConfig();
		config.setUid(UUID.randomUUID());
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setName("role master");
		roleMaster.setLevel(1);
		config.setRoleMaster(roleMaster);
		flow.setProductConfig(config);
		flow.setUid(UUID.randomUUID());
		List<Form> forms = new ArrayList<Form>();
		form.setName("str1");
		form.setPublished(true);
		form.setDisabled(false);
		forms.add(form);
		flow.setForms(forms);
		flow.setPublished(true);
		List<FlowPermission> flowPermissions = new ArrayList<>();
		FlowPermission flowPermission = FlowPermission.builder().permission("footprint_add").build();
		flowPermissions.add(flowPermission);
		flow.setFlowPermission(flowPermissions);
		flow.setPublishedFlow(true);
		form.setFlow(flow);
		FormDependent formIndep = new FormDependent();
		formIndep.setUid(UUID.randomUUID());
		FormDependency formOutdep = new FormDependency();
		formOutdep.setUid(UUID.randomUUID());
		Events events = new Events();
		events.setAction("\"{\\\"defaultFormId\\\":\\\"a032b1f8-22df-40d2-a494-9550e1c80c26\\\"}\"");
		Events events2 = new Events();
		events.setAction("{\"formId\":\"a032b1f8-22df-40d2-a494-9550e1c80c26\"}");
		List<Events> eventsList = new ArrayList<>();
		eventsList.add(events);
		eventsList.add(events2);
		form.setEvents(eventsList);
		form.setPublishedForm("{}".getBytes());
		return form;
	}

	private Form getForm1() {
		Form form = new Form();
		form.setUid(UUID.randomUUID());
		form.setParentFormId(UUID.randomUUID());
		Field field = new Field();
		field.setForm(form);
		field.setUid(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"));
		CustomComponentMaster customComponentMaster = new CustomComponentMaster();
		customComponentMaster.setUid(UUID.randomUUID());
		ResourceBundle resourceBundle1 = new ResourceBundle();
		resourceBundle1.setUid(UUID.randomUUID());
		resourceBundle1.setLocale(BffAdminConstantsUtils.EMPTY_SPACES);
		resourceBundle1.setRbkey(BffAdminConstantsUtils.EMPTY_SPACES);
		resourceBundle1.setRbvalue(BffAdminConstantsUtils.EMPTY_SPACES);
		ResourceBundle resourceBundle2 = new ResourceBundle();
		resourceBundle2.setUid(UUID.randomUUID());
		resourceBundle2.setLocale(BffAdminConstantsUtils.EMPTY_SPACES);
		resourceBundle2.setRbkey(BffAdminConstantsUtils.EMPTY_SPACES);
		resourceBundle2.setRbvalue(BffAdminConstantsUtils.EMPTY_SPACES);

		form.setFields(Arrays.asList(field));
		Flow flow = new Flow();
		ProductConfig config = new ProductConfig();
		config.setUid(UUID.randomUUID());
		flow.setProductConfig(config);
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setName("role master");
		roleMaster.setLevel(1);
		config.setRoleMaster(roleMaster);
		flow.setProductConfig(config);
		flow.setUid(UUID.randomUUID());
		List<Form> forms = new ArrayList<Form>();
		form.setName("str1");
		form.setPublished(false);
		form.setDisabled(true);
		forms.add(form);
		flow.setForms(forms);
		flow.setDefaultFormId(UUID.randomUUID());
		flow.setPublished(true);
		flow.setDisabled(true);
		form.setFlow(flow);
		FormDependent formIndep = new FormDependent();
		formIndep.setUid(UUID.randomUUID());
		FormDependency formOutdep = new FormDependency();
		formOutdep.setUid(UUID.randomUUID());
		return form;
	}

	private Flow getFlow() {
		Flow flow = new Flow();
		ProductConfig config = new ProductConfig();
		config.setUid(UUID.randomUUID());
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setName("role master");
		roleMaster.setLevel(1);
		config.setRoleMaster(roleMaster);
		flow.setProductConfig(config);
		flow.setUid(UUID.fromString("2e82c433-fb5b-4257-8f79-ac4998569b4c"));
		List<Form> formList = new ArrayList<>();
		Form form = getForm();
		formList.add(form);
		Field field = new Field();
		field.setAddFilter(false);
		field.setAddAnother("data");
		List<Field> fields = new ArrayList<>();
		fields.add(field);
		form.setFields(fields);
		flow.setDefaultFormId(form.getUid());
		flow.setPublished(false);
		form.setFlow(flow);
		flow.setForms(formList);
		flow.setPublishedFlow(true);
		return flow;
	}

	/**
	 * @return CustomComponentMaster
	 */
	private CustomComponentMaster getSampleCustomComponentMaster() {
		CustomComponentMaster ccm = new CustomComponentMaster();
		ccm.setUid(UUID.randomUUID());
		ccm.setName("Test custom component");
		ccm.setIsdisabled(false);
		ccm.setVisibility(true);
		List<Field> fieldList = new ArrayList<>();
		Field field = new Field();
		field.setUid(UUID.randomUUID());
		field.setAlignment("left");
		field.setCreatedBy("SYSTEM");
		field.setUid(UUID.fromString("af0b505b-2780-4f00-89c9-03bdada98555"));
		fieldList.add(field);
		Form form = new Form();
		form.setUid(UUID.randomUUID());

		List<FormCustomComponent> frmCCList = new ArrayList<FormCustomComponent>();
		FormCustomComponent fcc = new FormCustomComponent();
		fcc.setCustomComponentMaster(ccm);
		fcc.setUid(UUID.randomUUID());

		form.setFields(fieldList);
		RoleMaster roleMaster = new RoleMaster();
		ProductConfig prodConfig = new ProductConfig();
		prodConfig.setUid(UUID.fromString("0225c416-d15c-4c09-b01a-ae0ed50f17f7"));
		prodConfig.setRoleMaster(roleMaster);
		Flow flow = new Flow();
		flow.setProductConfig(prodConfig);
		form.setFlow(flow);
		fcc.setForm(form);
		frmCCList.add(fcc);
		ccm.setFormCustomComponent(frmCCList);
		field.setAlignment("str");
		ResourceBundle resourceBundle1 = new ResourceBundle();
		resourceBundle1.setUid(UUID.randomUUID());
		resourceBundle1.setLocale(BffAdminConstantsUtils.EMPTY_SPACES);
		resourceBundle1.setRbkey(BffAdminConstantsUtils.EMPTY_SPACES);
		resourceBundle1.setRbvalue(BffAdminConstantsUtils.EMPTY_SPACES);
		ResourceBundle resourceBundle2 = new ResourceBundle();
		resourceBundle2.setUid(UUID.randomUUID());
		resourceBundle2.setLocale(BffAdminConstantsUtils.EMPTY_SPACES);
		resourceBundle2.setRbkey(BffAdminConstantsUtils.EMPTY_SPACES);
		resourceBundle2.setRbvalue(BffAdminConstantsUtils.EMPTY_SPACES);
		return ccm;
	}
	
	
	private FormCustomComponent getFormCustomComponent() {
		CustomComponentMaster ccm = new CustomComponentMaster();
		ccm.setUid(UUID.randomUUID());
		ccm.setName("Test custom component");
		ccm.setIsdisabled(false);
		ccm.setVisibility(true);
		List<Field> fieldList = new ArrayList<>();
		Field field = new Field();
		field.setUid(UUID.randomUUID());
		field.setAlignment("left");
		field.setCreatedBy("SYSTEM");
		field.setUid(UUID.fromString("af0b505b-2780-4f00-89c9-03bdada98555"));
		fieldList.add(field);
		Form form = new Form();
		form.setUid(UUID.randomUUID());

		List<FormCustomComponent> frmCCList = new ArrayList<FormCustomComponent>();
		FormCustomComponent fcc = new FormCustomComponent();
		fcc.setCustomComponentMaster(ccm);
		fcc.setUid(UUID.randomUUID());

		form.setFields(fieldList);
		RoleMaster roleMaster = new RoleMaster();
		ProductConfig prodConfig = new ProductConfig();
		prodConfig.setUid(UUID.fromString("0225c416-d15c-4c09-b01a-ae0ed50f17f7"));
		prodConfig.setRoleMaster(roleMaster);
		Flow flow = new Flow();
		flow.setProductConfig(prodConfig);
		form.setFlow(flow);
		fcc.setForm(form);
		frmCCList.add(fcc);
		ccm.setFormCustomComponent(frmCCList);
		field.setAlignment("str");
		ResourceBundle resourceBundle1 = new ResourceBundle();
		resourceBundle1.setUid(UUID.randomUUID());
		resourceBundle1.setLocale(BffAdminConstantsUtils.EMPTY_SPACES);
		resourceBundle1.setRbkey(BffAdminConstantsUtils.EMPTY_SPACES);
		resourceBundle1.setRbvalue(BffAdminConstantsUtils.EMPTY_SPACES);
		ResourceBundle resourceBundle2 = new ResourceBundle();
		resourceBundle2.setUid(UUID.randomUUID());
		resourceBundle2.setLocale(BffAdminConstantsUtils.EMPTY_SPACES);
		resourceBundle2.setRbkey(BffAdminConstantsUtils.EMPTY_SPACES);
		resourceBundle2.setRbvalue(BffAdminConstantsUtils.EMPTY_SPACES);
		return fcc;
	}

}
