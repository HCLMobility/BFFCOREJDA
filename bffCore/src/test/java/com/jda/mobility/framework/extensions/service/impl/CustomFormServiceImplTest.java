/**
 * 
 */
package com.jda.mobility.framework.extensions.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jda.mobility.framework.extensions.dto.CustomFieldObjDto;
import com.jda.mobility.framework.extensions.dto.CustomFormDto;
import com.jda.mobility.framework.extensions.dto.FormCustomComponentDto;
import com.jda.mobility.framework.extensions.entity.CustomComponentMaster;
import com.jda.mobility.framework.extensions.entity.CustomField;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.FormCustomComponent;
import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.exception.DataBaseException;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.CustomFormData;
import com.jda.mobility.framework.extensions.repository.CustomComponentMasterRepository;
import com.jda.mobility.framework.extensions.repository.CustomDataRepository;
import com.jda.mobility.framework.extensions.repository.CustomEventsRepository;
import com.jda.mobility.framework.extensions.repository.CustomFieldRepository;
import com.jda.mobility.framework.extensions.repository.CustomFieldValuesRepository;
import com.jda.mobility.framework.extensions.repository.FieldRepository;
import com.jda.mobility.framework.extensions.repository.FormRepository;
import com.jda.mobility.framework.extensions.transformation.CustomFormTransformation;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.CustomFormFilterMode;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DeleteType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 * The class CustomFormServiceImplTest.java
 * 
 * @author HCL Technologies Ltd.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class CustomFormServiceImplTest extends AbstractPrepareTest {
	@InjectMocks
	private CustomFormServiceImpl customFormServiceImpl;
	@Mock
	private CustomFormTransformation customFormTransformation;
	@Mock
	private CustomFieldRepository customFieldRepo;
	@Mock
	private CustomComponentMasterRepository customComponentMasterRepository;
	@Mock
	private CustomEventsRepository customEventRepo;
	@Mock
	private CustomDataRepository customDataRepo;
	@Mock
	private CustomFieldValuesRepository customFieldValRepo;
	@Mock
	private FormRepository formRepository;
	@Mock
	private FieldRepository fieldRepo;

	@Test
	public void testCreateCustomComponentIfExist() throws JsonProcessingException {
		List<CustomComponentMaster> customControlList = new ArrayList<CustomComponentMaster>();
		customControlList.add(new CustomComponentMaster());
		when(customComponentMasterRepository.findByName(Mockito.any())).thenReturn(customControlList);

		BffCoreResponse response = customFormServiceImpl.createCustomComponent(getCustomFormData());
		assertEquals(BffResponseCode.ERR_CUSTOM_CTRL_UNIQUE_CHECK.getCode(), response.getCode());
		assertEquals(StatusCode.CONFLICT.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for createCustomComponent
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testCreateCustomComponent() throws JsonProcessingException {
		when(customFormTransformation.convertToCustomMasterEntity(Mockito.any()))
				.thenReturn(getCustomComponentMaster());
		when(customFormTransformation.convertToCustomFormObjDto(getCustomFormData())).thenReturn(getCustomFormDto());
		CustomField customField = new CustomField();
		customField.setCreatedBy(BffAdminConstantsUtils.SUPER);
		CustomComponentMaster customComponentMaster= new CustomComponentMaster();
		customComponentMaster.setUid(UUID.fromString("e8fcb1a7-d453-4ee3-8aaa-b0bd230e27f3"));
		customField.setCustomComponentMaster(customComponentMaster);
		Optional<CustomField> customFld = Optional.of(customField);
		when(customFieldRepo.findById(Mockito.any())).thenReturn(customFld);
		BffCoreResponse response = customFormServiceImpl.createCustomComponent(getCustomFormData());
		assertEquals(BffResponseCode.CUSTOM_COMPONENT_SUCCESS_CODE_CREATE.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for createCustomComponent_DataAccessException
	 */
	@Test
	public void testCreateCustomComponent_DataAccessException() {
		when(customFormTransformation.convertToCustomMasterEntity(Mockito.any()))
				.thenReturn(getCustomComponentMaster());
		when(customFormTransformation.convertToCustomFormObjDto(getCustomFormData()))
				.thenThrow(new DataBaseException("Custom control creation failed"));
		BffCoreResponse response = customFormServiceImpl.createCustomComponent(getCustomFormData());
		assertEquals(BffResponseCode.ERR_CUSTOM_COMPONENT_API_CREATE_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for createCustomComponent_Exception
	 */
	@Test
	public void testCreateCustomComponent_Exception() {
		when(customFormTransformation.convertToCustomFormObjDto(getCustomFormData())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = customFormServiceImpl.createCustomComponent(getCustomFormData());
		assertEquals(BffResponseCode.ERR_CUSTOM_COMPONENT_API_CREATE_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getCustomComponentById
	 */
	@Test
	public void testGetCustomComponentById() {
		UUID customComponentId = UUID.fromString("eec6e916-b409-4f13-baf1-7728302b91de");
		when(customComponentMasterRepository.findById(customComponentId))
				.thenReturn(Optional.of(getCustomComponentMaster()));
		BffCoreResponse response = customFormServiceImpl.getCustomComponentById(customComponentId);
		assertEquals(BffResponseCode.CUSTOM_COMPONENT_SUCCESS_CODE_FETCH.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getCustomComponentById_DataAccessException
	 */
	@Test
	public void testGetCustomComponentById_DataAccessException() {
		UUID customComponentId = UUID.fromString("eec6e916-b409-4f13-baf1-7728302b91de");
		when(customComponentMasterRepository.findById(customComponentId)).thenThrow(new DataBaseException("Custom control retrieval failed"));
		BffCoreResponse response = customFormServiceImpl.getCustomComponentById(customComponentId);
		assertEquals(BffResponseCode.ERR_CUSTOM_COMPONENT_API_FETCH_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getCustomComponentById_Exception
	 */
	@Test
	public void testGetCustomComponentById_Exception() {
		UUID customComponentId = UUID.fromString("eec6e916-b409-4f13-baf1-7728302b91de");
		when(customComponentMasterRepository.findById(customComponentId)).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = customFormServiceImpl.getCustomComponentById(customComponentId);
		assertEquals(BffResponseCode.ERR_CUSTOM_COMPONENT_API_FETCH_EXCEPTION.getCode(), response.getCode());
	}
	
	@Test
	public void testGetCustomComponentById_CustomComponentMaster_Exception() {
		UUID customComponentId = UUID.fromString("eec6e916-b409-4f13-baf1-7728302b91de");
		BffCoreResponse response = customFormServiceImpl.getCustomComponentById(customComponentId);
		assertEquals(BffResponseCode.ERR_CUSTOM_CTRL_NOT_FOUND.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testModifyCustomComponentIfNameExist() throws JsonProcessingException {
		CustomComponentMaster customCompMaster = new CustomComponentMaster();
		customCompMaster.setName("Test");
		Optional<CustomComponentMaster> customMaster = Optional.of(customCompMaster);
		when(customComponentMasterRepository.findById(Mockito.any())).thenReturn(customMaster);
		List<CustomComponentMaster> customControlList = new ArrayList<CustomComponentMaster>();
		customControlList.add(new CustomComponentMaster());
		when(customComponentMasterRepository.findByName(Mockito.any())).thenReturn(customControlList);

		BffCoreResponse response = customFormServiceImpl.modifyCustomComponent(getCustomFormData());
		assertEquals(BffResponseCode.ERR_CUSTOM_CTRL_UNIQUE_CHECK.getCode(), response.getCode());
		assertEquals(StatusCode.CONFLICT.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for modifyCustomComponent
	 * 
	 * @throws JsonProcessingException
	 */
	@Test
	public void testModifyCustomComponent() throws JsonProcessingException {

		CustomFormData customFormData = new CustomFormData();
		customFormData.setCustomComponentId(UUID.fromString("e8fcb1a7-d453-4ee3-8aaa-b0bd230e27f3"));
		customFormData.setName("Test");
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(new Form()));
		when(customFormTransformation.convertToCustomMasterEntity(Mockito.any()))
				.thenReturn(getCustomComponentMaster());
		when(customComponentMasterRepository.findById(customFormData.getCustomComponentId()))
				.thenReturn(Optional.of(getCustomComponentMaster()));
		when(customFormTransformation.convertToCustomFormObjDto(getCustomFormData())).thenReturn(getCustomFormDto());
		CustomField customField = new CustomField();
		customField.setCreatedBy(BffAdminConstantsUtils.SUPER);
		CustomComponentMaster customComponentMaster= new CustomComponentMaster();
		customComponentMaster.setUid(UUID.fromString("e8fcb1a7-d453-4ee3-8aaa-b0bd230e27f3"));
		customField.setCustomComponentMaster(customComponentMaster);
		Optional<CustomField> customFld = Optional.of(customField);
		when(customFieldRepo.findById(Mockito.any())).thenReturn(customFld);
		BffCoreResponse response = customFormServiceImpl.modifyCustomComponent(getCustomFormData());
		assertEquals(BffResponseCode.CUSTOM_COMPONENT_SUCCESS_CODE_UPDATE.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}
	
	
	@Test
	public void testModifyCustomComponent_customField() throws JsonProcessingException {

		CustomFormData customFormData = new CustomFormData();
		customFormData.setCustomComponentId(UUID.fromString("e8fcb1a7-d453-4ee3-8aaa-b0bd230e27f3"));
		customFormData.setName("Test");
		when(formRepository.findById(Mockito.any())).thenReturn(Optional.of(new Form()));
		when(customFormTransformation.convertToCustomMasterEntity(Mockito.any()))
				.thenReturn(getCustomComponentMaster());
		when(customComponentMasterRepository.findById(customFormData.getCustomComponentId()))
				.thenReturn(Optional.of(getCustomComponentMaster()));
		when(customFormTransformation.convertToCustomFormObjDto(getCustomFormData())).thenReturn(getCustomFormDto());
		CustomField customField = new CustomField();
		customField.setCreatedBy(BffAdminConstantsUtils.SUPER);
		CustomComponentMaster customComponentMaster= new CustomComponentMaster();
		customComponentMaster.setUid(UUID.fromString("e8fcb1a7-d453-4ee3-8aaa-b0bd230e27f3"));
		customField.setCustomComponentMaster(customComponentMaster);
		Optional<CustomField> customFld = Optional.of(customField);
		customField.setParentField(customField);
		when(customFieldRepo.findById(Mockito.any())).thenReturn(customFld);
		BffCoreResponse response = customFormServiceImpl.modifyCustomComponent(getCustomFormData());
		assertEquals(BffResponseCode.CUSTOM_COMPONENT_SUCCESS_CODE_UPDATE.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}


	/**
	 * Test method for modifyCustomComponent_DataAccessException
	 */
	@Test
	public void testModifyCustomComponent_DataAccessException() {
		CustomFormData customFormData = new CustomFormData();
		customFormData.setCustomComponentId(UUID.fromString("e8fcb1a7-d453-4ee3-8aaa-b0bd230e27f3"));
		when(customComponentMasterRepository.findById(customFormData.getCustomComponentId()))
				.thenReturn(Optional.of(getCustomComponentMaster()));
		when(customFormTransformation.convertToCustomFormObjDto(getCustomFormData()))
				.thenThrow(new DataBaseException("Custom control update failed"));
		BffCoreResponse response = customFormServiceImpl.modifyCustomComponent(getCustomFormData());
		assertEquals(BffResponseCode.ERR_CUSTOM_COMPONENT_API_UPDATE_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for modifyCustomComponent_Exception
	 */
	@Test
	public void testModifyCustomComponent_Exception() {
		when(customComponentMasterRepository.findById(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = customFormServiceImpl.modifyCustomComponent(getCustomFormData());
		assertEquals(BffResponseCode.ERR_CUSTOM_COMPONENT_API_UPDATE_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for modifyCustomComponent_Exception
	 */
	@Test
	public void testModifyCustomComponent_customControl_Exception() {
		BffCoreResponse response = customFormServiceImpl.modifyCustomComponent(getCustomFormData());
		assertEquals(BffResponseCode.ERR_CUSTOM_CTRL_NOT_FOUND.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for deleteCustomComponentById_CHECK_DELETE
	 */
	@Test
	public void testDeleteCustomComponentById_CHECK_DELETE() {
		UUID customComponentId = UUID.fromString("eec6e916-b409-4f13-baf1-7728302b91de");
		when(customComponentMasterRepository.findById(customComponentId))
				.thenReturn(Optional.of(getCustomComponentMaster()));
		BffCoreResponse response = customFormServiceImpl.deleteCustomComponentById(customComponentId,
				DeleteType.CHECK_DELETE);
		assertEquals(BffResponseCode.ERR_CUSTOM_COMPONENT_API_CHECK_DELETE_NOT_EMPTY.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for deleteCustomComponentById_CHECK_DELETE_Else
	 */
	@Test
	public void testDeleteCustomComponentById_CHECK_DELETE_Else() {
		UUID customComponentId = UUID.fromString("eec6e916-b409-4f13-baf1-7728302b91de");
		CustomComponentMaster customComponentMaster = getCustomComponentMaster();
		customComponentMaster.getFormCustomComponent().clear();
		when(customComponentMasterRepository.findById(customComponentId))
				.thenReturn(Optional.of(customComponentMaster));
		BffCoreResponse response = customFormServiceImpl.deleteCustomComponentById(customComponentId,
				DeleteType.CHECK_DELETE);
		assertEquals(BffResponseCode.ERR_CUSTOM_COMPONENT_API_CHECK_DELETE.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for deleteCustomComponentById_CONFIRM_DELETE
	 */
	@Test
	public void testDeleteCustomComponentById_CONFIRM_DELETE() {
		UUID customComponentId = UUID.fromString("eec6e916-b409-4f13-baf1-7728302b91de");
		when(customComponentMasterRepository.findById(customComponentId))
				.thenReturn(Optional.of(getCustomComponentMaster()));
		BffCoreResponse response = customFormServiceImpl.deleteCustomComponentById(customComponentId,
				DeleteType.CONFIRM_DELETE);
		assertEquals(BffResponseCode.CUSTOM_COMPONENT_SUCCESS_CODE_DELETE.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testDeleteCustomComponentById_CustomCTRL_NotFound() {
		UUID customComponentId = UUID.fromString("eec6e916-b409-4f13-baf1-7728302b91de");
		BffCoreResponse response = customFormServiceImpl.deleteCustomComponentById(customComponentId,
				DeleteType.CONFIRM_DELETE);
		assertEquals(BffResponseCode.ERR_CUSTOM_CTRL_NOT_FOUND.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for deleteCustomComponentById_DataAccessException
	 */
	@Test
	public void testDeleteCustomComponentById_DataAccessException() {
		UUID customComponentId = UUID.fromString("eec6e916-b409-4f13-baf1-7728302b91de");
		when(customComponentMasterRepository.findById(customComponentId)).thenThrow(new DataBaseException("Custom control retrieval failed"));
		BffCoreResponse response = customFormServiceImpl.deleteCustomComponentById(customComponentId,
				DeleteType.CHECK_DELETE);
		assertEquals(BffResponseCode.ERR_CUSTOM_COMPONENT_API_DELETE_DBEXCEPTION.getCode(), response.getCode());
	}

	/**
	 * Test method for deleteCustomComponentById_Exception
	 */
	@Test
	public void testDeleteCustomComponentById_Exception() {
		UUID customComponentId = UUID.fromString("eec6e916-b409-4f13-baf1-7728302b91de");
		when(customComponentMasterRepository.findById(customComponentId)).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = customFormServiceImpl.deleteCustomComponentById(customComponentId,
				DeleteType.CHECK_DELETE);
		assertEquals(BffResponseCode.ERR_CUSTOM_COMPONENT_API_DELETE_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * @return CustomFormDto
	 * @throws JsonProcessingException
	 */
	private CustomFormDto getCustomFormDto() throws JsonProcessingException {
		List<CustomFieldObjDto> fields = new ArrayList<>();
		CustomFieldObjDto fieldObjDto = new CustomFieldObjDto(new CustomField(), new ArrayList<>());
		fieldObjDto.setFontSize("test");
		fields.add(fieldObjDto);
		List<UUID> testUUIDList = new ArrayList<>();
		testUUIDList.add(UUID.fromString("eec6e916-b409-4f13-baf1-7728302b91de"));

		FormCustomComponentDto formCustomComponentDto = new FormCustomComponentDto(null, null, null);
		List<FormCustomComponentDto> formCustomComponentDtoList = new ArrayList<>();
		formCustomComponentDtoList.add(formCustomComponentDto);
		CustomFormDto customFormDto = new CustomFormDto.CustomFormBuilder().setName("Flow").setFields(fields)
				.setDeleteDataValues(testUUIDList).setDeleteEvents(testUUIDList).setDeleteValues(testUUIDList)
				.setDeleteFields(testUUIDList).build();

		return customFormDto;
	}

	/**
	 * @return CustomComponentMaster
	 */
	private CustomComponentMaster getCustomComponentMaster() {
		CustomComponentMaster customComponentMaster = new CustomComponentMaster();
		customComponentMaster.setUid(UUID.fromString("e8fcb1a7-d453-4ee3-8aaa-b0bd230e27f3"));
		List<CustomField> fields = new ArrayList<>();
		CustomField field = new CustomField();
		fields.add(field);
		customComponentMaster.setFields(fields);
		List<FormCustomComponent> formCustomComponentList = new ArrayList<FormCustomComponent>();
		FormCustomComponent formCustomComponent = new FormCustomComponent();
		Form form = new Form();
		form.setUid(UUID.randomUUID());
		formCustomComponent.setForm(form);
		formCustomComponentList.add(formCustomComponent);
		customComponentMaster.setFormCustomComponent(formCustomComponentList);
		customComponentMaster.setName("TEST");
		return customComponentMaster;
	}

	/**
	 * @return CustomComponentMaster
	 */
	private List<CustomComponentMaster> getCustomComponentMasterList() {
		List<CustomComponentMaster> CustomComponentMasterList = new ArrayList<>();
		CustomComponentMaster customComponentMaster = new CustomComponentMaster();
		customComponentMaster.setUid(UUID.fromString("e8fcb1a7-d453-4ee3-8aaa-b0bd230e27f3"));
		List<CustomField> fields = new ArrayList<>();
		CustomField field = new CustomField();
		fields.add(field);
		customComponentMaster.setFields(fields);
		List<FormCustomComponent> formCustomComponentList = new ArrayList<FormCustomComponent>();
		FormCustomComponent formCustomComponent = new FormCustomComponent();
		formCustomComponentList.add(formCustomComponent);
		customComponentMaster.setFormCustomComponent(formCustomComponentList);
		customComponentMaster.setName("TEST");
		CustomComponentMasterList.add(customComponentMaster);
		return CustomComponentMasterList;
	}

	/**
	 * @return CustomFormData
	 */
	private CustomFormData getCustomFormData() {
		CustomFormData customFormData = new CustomFormData();
		customFormData.setName("TEST NAME");
		customFormData.setCustomComponentId(UUID.fromString("e8fcb1a7-d453-4ee3-8aaa-b0bd230e27f3"));
		customFormData.setComponents(new ArrayList<>());
		return customFormData;
	}

	/**
	 * Test method for getCustomComponentList
	 */
	@Test
	public void testGetCustomComponentList() {
		when(customComponentMasterRepository.findAllByOrderByLastModifiedDateDesc(Mockito.any()))
				.thenReturn(getCustomComponentMasterList());
		BffCoreResponse response = customFormServiceImpl.fetchCustomCompList(CustomFormFilterMode.ALL, 0, 3);
		assertEquals(BffResponseCode.CUSTOM_COMPONENT_SUCCESS_CODE_FETCH.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getCustomComponentList_DataAccessException
	 */
	@Test
	public void testGetCustomComponentList_DataAccessException() {
		when(customComponentMasterRepository.findAllByOrderByLastModifiedDateDesc()).thenThrow(new DataBaseException("Custom control retrieval failed"));
		BffCoreResponse response = customFormServiceImpl.fetchCustomCompList(CustomFormFilterMode.ALL, null, null);
		assertEquals(BffResponseCode.ERR_CUSTOM_COMPONENT_API_FETCH_DBEXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for getCustomComponentList_Exception
	 */
	@Test
	public void testGetCustomComponentList_Exception() {
		when(customComponentMasterRepository.findAllByOrderByLastModifiedDateDesc()).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response = customFormServiceImpl.fetchCustomCompList(Mockito.any(), Mockito.any(), Mockito.any());
		assertEquals(BffResponseCode.ERR_CUSTOM_COMPONENT_API_FETCH_EXCEPTION.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testGetCustomComponentList_Basic() {
		when(customComponentMasterRepository.findAllByOrderByLastModifiedDateDesc())
				.thenReturn(getCustomComponentMasterList());
		BffCoreResponse response = customFormServiceImpl.fetchCustomCompList(CustomFormFilterMode.BASIC, 0, 3);
		assertEquals(BffResponseCode.CUSTOM_COMPONENT_SUCCESS_CODE_FETCH.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}
	@Test
	public void testGetCustomComponentList_Basic_pageNull() {
		when(customComponentMasterRepository.findAllByOrderByLastModifiedDateDesc())
				.thenReturn(getCustomComponentMasterList());
		BffCoreResponse response = customFormServiceImpl.fetchCustomCompList(CustomFormFilterMode.BASIC, 0, null);
		assertEquals(BffResponseCode.CUSTOM_COMPONENT_SUCCESS_CODE_FETCH.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
		
	}

	/**
	 * Test method for getCustomComponentList
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetCustomControlComponents() throws IOException {
		UUID customComponentId = UUID.fromString("eec6e916-b409-4f13-baf1-7728302b91de");
		CustomComponentMaster customComponentMaster = new CustomComponentMaster();
		FormCustomComponent formCustomComponent = new FormCustomComponent();
		formCustomComponent.setCustomComponentMaster(customComponentMaster);
		List<FormCustomComponent> formCustomComponentlist = new ArrayList<>();
		customComponentMaster.setFormCustomComponent(formCustomComponentlist);
		List<CustomFieldObjDto> fields = new ArrayList<>();
		CustomFieldObjDto fieldObjDto = new CustomFieldObjDto(new CustomField(), new ArrayList<>());
		fieldObjDto.setFontSize("test");
		fields.add(fieldObjDto);
		FormCustomComponentDto formCustomComponentDto = new FormCustomComponentDto(null, null, null);
		List<FormCustomComponentDto> formCustomComponentDtoList = new ArrayList<>();
		formCustomComponentDtoList.add(formCustomComponentDto);
		when(customComponentMasterRepository.findById(customComponentId))
				.thenReturn(Optional.of(getCustomComponentMaster()));
		when(customFormTransformation.convertToFieldDto(Mockito.any())).thenReturn(fields);
		when(customFormTransformation.convertToFormCustomComponentDto(Mockito.any()))
				.thenReturn(formCustomComponentDtoList);
		when(customFormTransformation.createCustomFormData(Mockito.any(),Mockito.any())).thenReturn(getCustomFormData());
		when(customFormTransformation.convertToCustomFormDto(getCustomComponentMaster(), fields,
				formCustomComponentDtoList)).thenReturn(getCustomFormDto());
		CustomFormData response = customFormServiceImpl.getCustomControlComponents(customComponentId,"");
		assertEquals("TEST NAME", response.getName());
	}

}
