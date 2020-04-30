package com.jda.mobility.framework.extensions.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.dto.CustomFieldObjDto;
import com.jda.mobility.framework.extensions.dto.CustomFormDto;
import com.jda.mobility.framework.extensions.dto.FormCustomComponentDto;
import com.jda.mobility.framework.extensions.entity.CustomComponentMaster;
import com.jda.mobility.framework.extensions.entity.CustomField;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.FormCustomComponent;
import com.jda.mobility.framework.extensions.entity.projection.CustomFormLiteDto;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.CustomFormData;
import com.jda.mobility.framework.extensions.repository.CustomComponentMasterRepository;
import com.jda.mobility.framework.extensions.repository.CustomDataRepository;
import com.jda.mobility.framework.extensions.repository.CustomEventsRepository;
import com.jda.mobility.framework.extensions.repository.CustomFieldRepository;
import com.jda.mobility.framework.extensions.repository.CustomFieldValuesRepository;
import com.jda.mobility.framework.extensions.repository.FieldRepository;
import com.jda.mobility.framework.extensions.repository.FormRepository;
import com.jda.mobility.framework.extensions.service.CustomFormService;
import com.jda.mobility.framework.extensions.transformation.CustomFormTransformation;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.CustomFormFilterMode;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DeleteType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.RequestType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 * The class implements for create new CustomComponent and update/delete the existing
 * CustomComponent
 * 
 * @author HCL Technologies
 */
@Service
public class CustomFormServiceImpl implements CustomFormService {
	private static final Logger LOGGER = LogManager.getLogger(CustomFormServiceImpl.class);
	@Autowired
	private CustomComponentMasterRepository customComponentMasterRepository;
	@Autowired
	CustomFieldRepository customFieldRepo;
	@Autowired
	private BffResponse bffResponse;
	@Autowired
	private CustomFormTransformation customFormTransformation;
	@Autowired
	private CustomEventsRepository customEventRepo;
	@Autowired
	private CustomDataRepository customDataRepo;
	@Autowired
	private CustomFieldValuesRepository customFieldValRepo;
	@Autowired
	private FormRepository formRepo;
	@Autowired
	private FieldRepository fieldRepo;

	/**
	 * Create a Custom component
	 * 
	 * @param customFormData CustomFormData
	 * @return BffCoreResponse
	 */
	@Override
	@Transactional
	public BffCoreResponse createCustomComponent(CustomFormData customFormData) {
		BffCoreResponse bffCoreResponse = null;
		try {
			// Check for existing custom control
			List<CustomComponentMaster> customControlList = customComponentMasterRepository
					.findByName(customFormData.getName());
			// If custom control name already present , send conflict code
			if (customControlList != null && !customControlList.isEmpty()) {
				return bffResponse.errResponse(
						List.of(BffResponseCode.ERR_CUSTOM_CTRL_UNIQUE_CHECK,
								BffResponseCode.ERR_USER_CUSTOM_CTRL_UNIQUE_CHECK),
						StatusCode.CONFLICT, null, customFormData.getName());
			}
			CustomFormDto customFormObjDto = customFormTransformation.convertToCustomFormObjDto(customFormData);
			LOGGER.log(Level.DEBUG, "Custom component created :{} ", customFormObjDto.getName());
			CustomComponentMaster customComponentMaster = customFormTransformation
					.convertToCustomMasterEntity(customFormObjDto);
			LOGGER.log(Level.DEBUG, "Custom Component id  :{}", customComponentMaster.getUid());
			// delete field attributes events, data and field values if available
			deleteFieldAttributes(customFormObjDto);

			// delete field
			deleteField(customFormObjDto, customComponentMaster);

			if (customFormObjDto.getFields() != null) {

				for (CustomFieldObjDto fieldRequest : customFormObjDto.getFields()) {
					customFormTransformation.convertToFieldEntity(fieldRequest, customComponentMaster.getUid(),
							RequestType.POST);
				}
			}

			CustomFormDto customFormDto = customFormTransformation.convertToCustomFormDto(customComponentMaster, null,
					null);
			CustomFormData customFormDataRes = customFormTransformation.createCustomFormData(customFormDto,BffAdminConstantsUtils.EMPTY);
			bffCoreResponse = bffResponse.response(customFormDataRes,
					BffResponseCode.CUSTOM_COMPONENT_SUCCESS_CODE_CREATE,
					BffResponseCode.CUSTOM_COMPONENT_USER_CODE_CREATE, StatusCode.CREATED, null,
					customComponentMaster.getUid().toString());
		} catch (DataAccessException exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, customFormData.getName());
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_CUSTOM_COMPONENT_API_CREATE_DBEXCEPTION,
							BffResponseCode.ERR_CUSTOM_COMPONENT_USER_CREATE_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR, null, customFormData.getName());
		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, customFormData.getName());
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_CUSTOM_COMPONENT_API_CREATE_EXCEPTION,
							BffResponseCode.ERR_CUSTOM_COMPONENT_USER_CREATE_EXCEPTION),
					StatusCode.BADREQUEST, null, customFormData.getName());
		}
		return bffCoreResponse;
	}

	
	/**Delete the attributes of Field - Data , Event and Values
	 * 
	 * @param customFormObjDto
	 */
	private void deleteFieldAttributes(CustomFormDto customFormObjDto) {
		if (customFormObjDto.getDeleteEvents() != null && !customFormObjDto.getDeleteEvents().isEmpty()) {
			for (UUID deletEventId : customFormObjDto.getDeleteEvents()) {
				customEventRepo.deleteById(deletEventId);
			}
		}
		if (customFormObjDto.getDeleteValues() != null && !customFormObjDto.getDeleteValues().isEmpty()) {
			for (UUID deletValuesId : customFormObjDto.getDeleteValues()) {
				customFieldValRepo.deleteById(deletValuesId);
			}
		}
		if (customFormObjDto.getDeleteDataValues() != null && !customFormObjDto.getDeleteDataValues().isEmpty()) {
			for (UUID deletDataValuesId : customFormObjDto.getDeleteDataValues()) {
				customDataRepo.deleteById(deletDataValuesId);
			}
		}
	}

	/**Delete the field and its associated attributes
	 * 
	 * @param customFormObjDto
	 * @param customComponentMaster
	 */
	private void deleteField(CustomFormDto customFormObjDto, CustomComponentMaster customComponentMaster) {
		if (!CollectionUtils.isEmpty(customFormObjDto.getDeleteFields())) {
			for (UUID deletFieldId : customFormObjDto.getDeleteFields()) {
				Optional<CustomField> optionalfield = customFieldRepo.findById(deletFieldId);
				if (optionalfield.isPresent()
							&& optionalfield.get().getCustomComponentMaster().getUid()
								.equals(customComponentMaster.getUid())) {
					CustomField field = optionalfield.get();
					if (field.getParentField() == null) {
						customComponentMaster.removeField(field);
						customComponentMasterRepository.save(customComponentMaster);
					} else {
						Optional<CustomField> optionalParent = customFieldRepo
								.findById(field.getParentField().getUid());
						if (optionalParent.isPresent()) {
							CustomField parentField = optionalParent.get();
							parentField.removeChildFields(field);
							customFieldRepo.save(parentField);
						}
					}
					customFieldRepo.delete(field);
				}
			}
		}
	}

	/**
	 * Retrieves the custom component by its Id
	 * 
	 * @param customComponentId
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse getCustomComponentById(UUID customComponentId) {
		BffCoreResponse bffCoreResponse = null;
		try {
			LOGGER.log(Level.DEBUG, "Get custom component with ID: {}", customComponentId);
			Optional<CustomComponentMaster> optionalMaster = customComponentMasterRepository.findById(customComponentId);
			
			if(optionalMaster.isPresent())
			{
				CustomComponentMaster customComponentMaster = optionalMaster.get();
				List<CustomFieldObjDto> fieldObjDtoList = null;
				if (!CollectionUtils.isEmpty(customComponentMaster.getFields())) {
					fieldObjDtoList = customFormTransformation.convertToFieldDto(customComponentMaster.getFields());
				}
				List<FormCustomComponentDto> formCustomCompDtoList = null;
				if (!CollectionUtils.isEmpty(customComponentMaster.getFormCustomComponent())) {
					formCustomCompDtoList = customFormTransformation
							.convertToFormCustomComponentDto(customComponentMaster.getFormCustomComponent());
				}
				CustomFormDto customFormObjDto = customFormTransformation.convertToCustomFormDto(customComponentMaster,
						fieldObjDtoList, formCustomCompDtoList);
	
				CustomFormData customFormDataRes = customFormTransformation.createCustomFormData(customFormObjDto,BffAdminConstantsUtils.EMPTY);
				LOGGER.log(Level.DEBUG, "Retrive CustomComponent service end");
	
				bffCoreResponse = bffResponse.response(customFormDataRes,
						BffResponseCode.CUSTOM_COMPONENT_SUCCESS_CODE_FETCH,
						BffResponseCode.CUSTOM_COMPONENT_USER_CODE_FETCH, StatusCode.OK, null,
						customComponentId.toString());
			}
			else
			{
				bffCoreResponse =  bffResponse.errResponse(
						List.of(BffResponseCode.ERR_CUSTOM_CTRL_NOT_FOUND, BffResponseCode.ERR_CUSTOM_CTRL_NOT_FOUND),
						StatusCode.BADREQUEST);
			}
		} catch (DataAccessException exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, customComponentId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_CUSTOM_COMPONENT_API_FETCH_DBEXCEPTION,
							BffResponseCode.ERR_CUSTOM_COMPONENT_USER_FETCH_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR, null, customComponentId.toString());
		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, customComponentId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_CUSTOM_COMPONENT_API_FETCH_EXCEPTION,
							BffResponseCode.ERR_CUSTOM_COMPONENT_USER_FETCH_EXCEPTION),
					StatusCode.BADREQUEST, null, customComponentId.toString());
		}
		return bffCoreResponse;
	}

	/**
	 * Update the customComponent
	 * - Make update to form where this custom component is used
	 * 
	 * @param customFormData
	 * @return BffCoreResponse
	 */
	@Override
	@Transactional
	public BffCoreResponse modifyCustomComponent(CustomFormData customFormData) {
		BffCoreResponse bffCoreResponse = null;
		try {

			// Check for existing custom control
			Optional<CustomComponentMaster> customControl = customComponentMasterRepository
					.findById(customFormData.getCustomComponentId());
			
			if(customControl.isPresent())
			{
			
				if (!customControl.get().getName().equals(customFormData.getName())) {
					List<CustomComponentMaster> customControlList = customComponentMasterRepository
							.findByName(customFormData.getName());
					if (!customControlList.isEmpty()) {
						return bffResponse.errResponse(
								List.of(BffResponseCode.ERR_CUSTOM_CTRL_UNIQUE_CHECK,
										BffResponseCode.ERR_USER_CUSTOM_CTRL_UNIQUE_CHECK),
								StatusCode.CONFLICT, null, customFormData.getName());
					}
				}
	
				CustomFormDto customFormObjDto = customFormTransformation.convertToCustomFormObjDto(customFormData);
				CustomComponentMaster customComponentMaster = customFormTransformation
						.convertToCustomMasterEntity(customFormObjDto);
	
				// delete field attributes events, data and field values if available
				deleteFieldAttributes(customFormObjDto);
	
				// delete field
				deleteField(customFormObjDto, customComponentMaster);
	
				LOGGER.log(Level.DEBUG, "CustomComponent update with CustomComponentId : {}",
						customFormData.getCustomComponentId());
				List<CustomField> fieldLst = null;
				if (customFormObjDto.getFields() != null && !customFormObjDto.getFields().isEmpty()) {
					fieldLst = new ArrayList<>();
					for (CustomFieldObjDto fieldObjDto : customFormObjDto.getFields()) {
						CustomField field = customFormTransformation.convertToFieldEntity(fieldObjDto,
								customComponentMaster.getUid(), RequestType.PUT);
						fieldLst.add(field);
					}
				}
				List<CustomFieldObjDto> fieldObjDtoList = null;
				if (fieldLst != null && !fieldLst.isEmpty()) {
					fieldObjDtoList = customFormTransformation.convertToFieldDto(fieldLst);
				}
				List<FormCustomComponentDto> formCustomCompDtoList = null;
				if (customComponentMaster.getFormCustomComponent() != null
						&& !customComponentMaster.getFormCustomComponent().isEmpty()) {
					formCustomCompDtoList = customFormTransformation
							.convertToFormCustomComponentDto(customComponentMaster.getFormCustomComponent());
				}
	
				updateCustomControlUsedForms(customControl.get());
	
				LOGGER.log(Level.DEBUG, "Custom Componet updated  with id :{}", customComponentMaster.getUid());
				CustomFormDto customFormDto = customFormTransformation.convertToCustomFormDto(customComponentMaster,
						fieldObjDtoList, formCustomCompDtoList);
				CustomFormData customFormDataRes = customFormTransformation.createCustomFormData(customFormDto,BffAdminConstantsUtils.EMPTY);
				LOGGER.log(Level.DEBUG, "CustomComponent updated successfully");
	
				bffCoreResponse = bffResponse.response(customFormDataRes,
						BffResponseCode.CUSTOM_COMPONENT_SUCCESS_CODE_UPDATE,
						BffResponseCode.CUSTOM_COMPONENT_USER_CODE_UPDATE, StatusCode.OK, null,
						customComponentMaster.getUid().toString());
			}
			else {
				bffCoreResponse = bffResponse.errResponse(
						List.of(BffResponseCode.ERR_CUSTOM_CTRL_NOT_FOUND, BffResponseCode.ERR_CUSTOM_CTRL_NOT_FOUND),
						StatusCode.BADREQUEST);
			}
		} catch (DataAccessException exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, customFormData.getName());
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_CUSTOM_COMPONENT_API_UPDATE_DBEXCEPTION,
							BffResponseCode.ERR_CUSTOM_COMPONENT_USER_UPDATE_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR, null,
					customFormData.getCustomComponentId().toString());
		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, customFormData.getName());
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_CUSTOM_COMPONENT_API_UPDATE_EXCEPTION,
							BffResponseCode.ERR_CUSTOM_COMPONENT_USER_UPDATE_EXCEPTION),
					StatusCode.BADREQUEST, null, customFormData.getCustomComponentId().toString());
		}
		return bffCoreResponse;
	}

	/**Update the form as non published when there is change in custom component it has used
	 * 
	 * @param customControl
	 */
	private void updateCustomControlUsedForms(CustomComponentMaster customControl) {
		List<FormCustomComponent> formCustomComponentList = customControl.getFormCustomComponent();

		for (FormCustomComponent formCustomComp : formCustomComponentList) {
			Optional<Form> form = formRepo.findById(formCustomComp.getForm().getUid());

			if (form.isPresent()) {
				form.get().setPublished(false);
				formRepo.save(form.get());
			}
		}
	}

	/**
	 * Delete the customComponent  by ID
	 * 
	 * @param customComponentId
	 * @param identifier
	 * @return BffCoreResponse
	 */
	@Override
	@Transactional
	public BffCoreResponse deleteCustomComponentById(UUID customComponentId, DeleteType identifier) {

		BffCoreResponse bffCoreResponse = null;
		try {
			LOGGER.log(Level.DEBUG, "Delete customComponentId:{} for {}", customComponentId, identifier);
			Optional<CustomComponentMaster> customComponentMaster = customComponentMasterRepository.findById(customComponentId);

			if(customComponentMaster.isPresent())
			{
				if (identifier != null && identifier.equals(DeleteType.CHECK_DELETE)) {
					List<FormCustomComponent> formCustomComponentList = customComponentMaster.get().getFormCustomComponent();
					if (!formCustomComponentList.isEmpty()) {
						bffCoreResponse = bffResponse.errResponse(
								List.of(BffResponseCode.ERR_CUSTOM_COMPONENT_API_CHECK_DELETE_NOT_EMPTY,
										BffResponseCode.ERR_CUSTOM_COMPONENT_USER_CHECK_DELETE_NOT_EMPTY),
								StatusCode.OK, null, customComponentId.toString());
					} else {
						bffCoreResponse = bffResponse.errResponse(
								List.of(BffResponseCode.ERR_CUSTOM_COMPONENT_API_CHECK_DELETE,
										BffResponseCode.ERR_CUSTOM_COMPONENT_USER_CHECK_DELETE),
								StatusCode.OK, null, customComponentId.toString());
					}
	
				} else if (identifier != null && identifier.equals(DeleteType.CONFIRM_DELETE)) {
					
					updateCustomControlUsedForms(customComponentMaster.get());
					
					customComponentMasterRepository.delete(customComponentMaster.get());
	
					// Delete the reference in forms -> Fields
					fieldRepo.deleteByLinkedComponentId(customComponentId);
	
					LOGGER.log(Level.DEBUG, "customcomponent successfully deleted with id :{}", customComponentId);
					bffCoreResponse = bffResponse.response(customComponentId,
							BffResponseCode.CUSTOM_COMPONENT_SUCCESS_CODE_DELETE,
							BffResponseCode.CUSTOM_COMPONENT_USER_CODE_DELETE, StatusCode.OK,
							identifier.getType(), customComponentId.toString());
				}
			}
			else
			{
				bffCoreResponse =  bffResponse.errResponse(
						List.of(BffResponseCode.ERR_CUSTOM_CTRL_NOT_FOUND, BffResponseCode.ERR_CUSTOM_CTRL_NOT_FOUND),
						StatusCode.BADREQUEST);
			}

		} catch (DataAccessException exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, customComponentId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_CUSTOM_COMPONENT_API_DELETE_DBEXCEPTION,
							BffResponseCode.ERR_CUSTOM_COMPONENT_USER_DELETE_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR, null, customComponentId.toString());
		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, customComponentId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_CUSTOM_COMPONENT_API_DELETE_EXCEPTION,
							BffResponseCode.ERR_CUSTOM_COMPONENT_USER_DELETE_EXCEPTION),
					StatusCode.BADREQUEST, null, customComponentId.toString());
		}
		return bffCoreResponse;

	}

	/**Fetch all available custom component as list
	 * @param identifier
	 * @param pageNo
	 * @param pageSize
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchCustomCompList(CustomFormFilterMode identifier, Integer pageNo, Integer pageSize) {
		BffCoreResponse bffCoreResponse = null;
		
		List<FormCustomComponentDto> formCustomCompDtoList = null;
		List<CustomFormData> cusFormDataList = null;
		List<CustomFormLiteDto> customFormList = null;
		List<CustomComponentMaster> customComponentMasterList = null;
		try {
			if (identifier != null && identifier.equals(CustomFormFilterMode.BASIC)) {
				if (pageNo != null && pageSize != null) {
					customFormList = customComponentMasterRepository
							.getCustomFormBasicListByPage(PageRequest.of(pageNo, pageSize));
				} else {
					customFormList = customComponentMasterRepository.getCustomFormBasicList();
				}
				bffCoreResponse = bffResponse.response(customFormList,
						BffResponseCode.CUSTOM_COMPONENT_SUCCESS_CODE_FETCH,
						BffResponseCode.CUSTOM_COMPONENT_USER_CODE_FETCH, StatusCode.OK);
				return bffCoreResponse;
			} else if (identifier == null || identifier.equals(CustomFormFilterMode.ALL)) {
				if (pageNo != null && pageSize != null) {
					customComponentMasterList = customComponentMasterRepository
							.findAllByOrderByLastModifiedDateDesc(PageRequest.of(pageNo, pageSize));
				} else {
					customComponentMasterList = customComponentMasterRepository.findAllByOrderByLastModifiedDateDesc();
				}
				cusFormDataList = new ArrayList<>();
				if (customComponentMasterList != null) {
					for (CustomComponentMaster customComponentMaster : customComponentMasterList) {
						List<CustomFieldObjDto> fieldObjDtoList = null;
						List<CustomField> customCompFields = customComponentMaster.getFields();
						if (customCompFields != null && !customCompFields.isEmpty()) {
							fieldObjDtoList = customFormTransformation.convertToFieldDto(customCompFields);
						}
						List<FormCustomComponent> formCustComps = customComponentMaster.getFormCustomComponent();
						if (formCustComps != null && !formCustComps.isEmpty()) {
							formCustomCompDtoList = customFormTransformation
									.convertToFormCustomComponentDto(formCustComps);
						}
						CustomFormDto customFormObjDto = customFormTransformation
								.convertToCustomFormDto(customComponentMaster, fieldObjDtoList, formCustomCompDtoList);
						CustomFormData customFormDataRes = customFormTransformation
								.createCustomFormData(customFormObjDto, BffAdminConstantsUtils.EMPTY);
						cusFormDataList.add(customFormDataRes);
					}
				}
				bffCoreResponse = bffResponse.response(cusFormDataList,
						BffResponseCode.CUSTOM_COMPONENT_SUCCESS_CODE_FETCH,
						BffResponseCode.CUSTOM_COMPONENT_USER_CODE_FETCH, StatusCode.OK);
			}
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_CUSTOM_COMPONENT_API_FETCH_DBEXCEPTION,
							BffResponseCode.ERR_CUSTOM_COMPONENT_USER_FETCH_DBEXCEPTION),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_CUSTOM_COMPONENT_API_FETCH_EXCEPTION,
							BffResponseCode.ERR_CUSTOM_COMPONENT_USER_FETCH_EXCEPTION),
					StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**Retrieve the custom component by its ID
	 * - Append the custom component key to all its child to identify them with uniqueness when dragged and dropped in form.
	 * - Invoke API , validation , data and events are updated with new updated key generated and saved  in form
	 * @param customComponentId
	 * @param parentKey
	 * @return CustomFormData
	 * @throws IOException
	 */
	public CustomFormData getCustomControlComponents(UUID customComponentId,String parentKey) throws IOException {
		Optional<CustomComponentMaster> customCompMaster = customComponentMasterRepository.findById(customComponentId);

		if (customCompMaster.isPresent()) {
			CustomComponentMaster customComponentMaster = customCompMaster.get();

			List<CustomFieldObjDto> fieldObjDtoList = null;
			if (customComponentMaster.getFields() != null && !customComponentMaster.getFields().isEmpty()) {
				fieldObjDtoList = customFormTransformation.convertToFieldDto(customComponentMaster.getFields());
			}

			List<FormCustomComponentDto> formCustomCompDtoList = null;
			if (customComponentMaster.getFormCustomComponent() != null
					&& !customComponentMaster.getFormCustomComponent().isEmpty()) {
				formCustomCompDtoList = customFormTransformation
						.convertToFormCustomComponentDto(customComponentMaster.getFormCustomComponent());
			}

			CustomFormDto customFormObjDto = customFormTransformation.convertToCustomFormDto(customComponentMaster,
					fieldObjDtoList, formCustomCompDtoList);

			CustomFormData customFormDataRes = customFormTransformation.createCustomFormData(customFormObjDto, parentKey);
			return customFormDataRes;
		}

		return new CustomFormData();
	}
}