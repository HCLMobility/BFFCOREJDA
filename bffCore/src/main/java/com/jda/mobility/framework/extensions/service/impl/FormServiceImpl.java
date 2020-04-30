package com.jda.mobility.framework.extensions.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.dto.FieldObjDto;
import com.jda.mobility.framework.extensions.dto.FormCustomDto;
import com.jda.mobility.framework.extensions.dto.FormObjDto;
import com.jda.mobility.framework.extensions.dto.MenuDto;
import com.jda.mobility.framework.extensions.dto.MenuListDto;
import com.jda.mobility.framework.extensions.entity.CustomComponentMaster;
import com.jda.mobility.framework.extensions.entity.Field;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.FlowPermission;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.FormCustomComponent;
import com.jda.mobility.framework.extensions.entity.FormDependency;
import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.entity.Tabs;
import com.jda.mobility.framework.extensions.entity.projection.FormLiteDto;
import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.model.AppConfigRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.FormData;
import com.jda.mobility.framework.extensions.model.FormResponse;
import com.jda.mobility.framework.extensions.model.MenuListRequest;
import com.jda.mobility.framework.extensions.model.MenuRequest;
import com.jda.mobility.framework.extensions.repository.CustomComponentMasterRepository;
import com.jda.mobility.framework.extensions.repository.DataRepository;
import com.jda.mobility.framework.extensions.repository.EventsRepository;
import com.jda.mobility.framework.extensions.repository.FieldRepository;
import com.jda.mobility.framework.extensions.repository.FieldValuesRepository;
import com.jda.mobility.framework.extensions.repository.FlowRepository;
import com.jda.mobility.framework.extensions.repository.FormCustomComponentRepository;
import com.jda.mobility.framework.extensions.repository.FormDependencyRepository;
import com.jda.mobility.framework.extensions.repository.FormRepository;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;
import com.jda.mobility.framework.extensions.repository.TabRepository;
import com.jda.mobility.framework.extensions.service.FormService;
import com.jda.mobility.framework.extensions.service.ProductPrepareService;
import com.jda.mobility.framework.extensions.transformation.FormTransformation;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ActionType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ChannelType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DefaultType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DeleteType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.FormStatus;
import com.jda.mobility.framework.extensions.utils.BffCommonUtil;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.FormDependencyUtil;

/**
 *Implementation for create /update /fetch/ delete forms
 * 
 * @author HCL Technologies
 */
@Service
public class FormServiceImpl implements FormService {

	private static final Logger LOGGER = LogManager.getLogger(FormServiceImpl.class);
	private static final String CHECK_FORM_DELETION_MESSAGE = "Form might have linked forms";

	@Autowired
	private FormRepository formRepo;
	@Autowired
	private FlowRepository flowRepo;
	@Autowired
	FieldRepository fieldRepo;
	@Autowired
	private FormTransformation formTransformation;
	@Autowired
	private BffResponse bffResponse;
	@Autowired
	private CustomComponentMasterRepository customComponentMasterRepo;
	@Autowired
	private EventsRepository eventRepo;
	@Autowired
	private DataRepository dataRepo;
	@Autowired
	private FieldValuesRepository fieldValRepo;
	@Autowired
	private SessionDetails sessionDetails;
	@Autowired
	private MenuServiceImpl menuServiceImpl;
	@Autowired
	TabRepository tabRepo;
	@Autowired
	private ProductPrepareService productPrepareService;
	@Autowired
	private BffCommonUtil bffCommonUtil;
	@Autowired
	private ResourceBundleRepository resourceBundleRepository;
	@Autowired
	private FormDependencyUtil formDependencyUtil;
	@Autowired
	private FormDependencyRepository formDependencyRepository;
	@Autowired
	private AppConfigServiceImpl appConfigServiceImpl;
	@Autowired
	FormCustomComponentRepository formCustomComponentRepo;
	

	/**
	 * Create a form
	 *  - Validate form name is unique across the flow
	 *  - Update dependencies table
	 * 
	 * @param formData
	 * @param actionType
	 * @param identifier
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse createForm(FormData formData, ActionType actionType, DefaultType identifier,List<String> permissionIds) {
		BffCoreResponse bffCoreResponse = null;
		Form form = null;
		try {
			Optional<Flow> flow = flowRepo.findById(formData.getFlowId());

			if (flow.isPresent()) {
				if (!formTransformation.checkUniqueFormName(formData, flow.get().getForms())) {

					return bffResponse.errResponse(
							List.of(BffResponseCode.ERR_FORM_API_FLOW_GIVEN_FORM,
									BffResponseCode.ERR_FORM_USER_FLOW_GIVEN_FORM),
							StatusCode.BADREQUEST, null, formData.getName());

				}

				// Check whether already default form is available for that flow
				if (identifier.equals(DefaultType.CHECK_DEFAULT)) {
					if (formData.isDefaultForm()) {
						if (flow.get().getDefaultFormId() != null) {

							return bffResponse.errResponse(
									List.of(BffResponseCode.ERR_FORM_API_CHECK_DEFAULT,
											BffResponseCode.ERR_FORM_USER_CHECK_DEFAULT),
									StatusCode.BADREQUEST, null, flow.get().getUid().toString());
						} else {
							form = saveNewFormComponent(formData, flow.get());
							setDefaultFormForFlow(form, actionType);
						}
					} else {
						form = saveNewFormComponent(formData, flow.get());
					}
				} else {
					if (formData.isDefaultForm()) {
						form = saveNewFormComponent(formData, flow.get());
						setDefaultFormForFlow(form, actionType);
					} else {
						form = saveNewFormComponent(formData, flow.get());
					}
				}
				// Create form context menus
				if (formData.getFormProperties() != null && formData.getFormProperties().getMenus() != null) {
					createFormMenu(formData.getFormProperties().getMenus(), form);
				}

				// Publishing the form
				if (actionType.equals(ActionType.CONFIRM_PUBLISH)) {
					publishForm(formData.isDefaultForm(), form,permissionIds);
				}
				
				//saving the form
				flowRepo.save(form.getFlow());
				
				formDependencyUtil.manageWorkFlowDependency(form);
				formDependencyUtil.findlinkedForms(form, form.getFlow().getDefaultFormId());
				
				FormResponse createdFormData = new FormResponse();
				createdFormData.setFormId(form.getUid());
				createdFormData.setName(form.getName());

				bffCoreResponse = bffResponse.response(createdFormData, BffResponseCode.FORM_SUCCESS_CODE_CREATE_FORM,
						BffResponseCode.FORM_USER_CODE_CREATE_FORM, StatusCode.CREATED, null,
						formData.getName());
			} else {
				return bffResponse.errResponse(
						List.of(BffResponseCode.ERR_FORM_FLOW_NOT_FOUND, BffResponseCode.ERR_FORM_FLOW_NOT_FOUND),
						StatusCode.BADREQUEST);
			}

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, formData.getName());
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FORM_API_CREATE_FORM, BffResponseCode.DB_ERR_FORM_USER_CREATE_FORM),
					StatusCode.INTERNALSERVERERROR, null, formData.getName());
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, formData.getName());
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FORM_API_CREATE_FORM_EXCEPTION,
							BffResponseCode.ERR_FORM_USER_CREATE_FORM_EXCEPTION),
					StatusCode.BADREQUEST, null, formData.getName());
		}
		return bffCoreResponse;
	}

	/**Method to publish a form
	 *  - update dependencies table
	 *  - update published json
	 * 
	 * @param defaultFormFlag
	 * @param form
	 * @throws IOException
	 */
	private void publishForm(boolean defaultFormFlag, Form form,List<String> permissionIds) throws IOException {
		form.setPublished(ActionType.CONFIRM_PUBLISH.isValue());

		// Prepare fields for publishing.
		// Form should be saved before publish.Unique Id for Field is necessary for
		// publishing form complete.
		List<FieldObjDto> fieldObjDtoList = null;
		if (!form.getFields().isEmpty()) {
			fieldObjDtoList = formTransformation.convertToFieldDto(form.getFields());
		}
		// conversion of form to formData
		FormObjDto formDto = new FormObjDto(form, fieldObjDtoList);
		formDto.setDefaultForm(defaultFormFlag);

		// Generating publishedFormJson for formData
		ObjectMapper objectMapper = new ObjectMapper();
		FormData formData = formTransformation.createFormData(formDto);
		//Get the Form context menus
		formData.getFormProperties().setMenus(getFormMenus(form.getUid(),permissionIds,true));
		
		form.setPublishedForm(objectMapper.writeValueAsBytes(formData));
		formDependencyUtil.managePublishedWorkFlowDependency(form);
		formDependencyUtil.findLinkedPublishedFormIds(form, form.getFlow().getPublishedDefaultFormId());
	}

	/**Save the new form data along with its dependencies
	 * 
	 * @param formData
	 * @param flow
	 * @return
	 */
	private Form saveNewFormComponent(FormData formData, Flow flow) {
		FormObjDto formObjDto = formTransformation.convertToFormObjDto(formData);
		LOGGER.log(Level.DEBUG, "Flow for the given form : {}", formData.getFlowId());
		// setting prodConfigId of default-warehouse of current layer
		Form form = formTransformation.convertToFormEntity(formObjDto, flow);
		formTransformation.saveEvents(formObjDto, form);
		formTransformation.saveTabs(formObjDto, form);
		LOGGER.log(Level.DEBUG, "Form created with formId : {}", form.getUid());
		if (!CollectionUtils.isEmpty(formObjDto.getFields())) {

			for (FieldObjDto fieldRequest : formObjDto.getFields()) {
				formTransformation.convertToFieldEntity(fieldRequest, form);
			}
		}

		form = formRepo.save(form);
		return form;
	}

	/**Create form context menu for given form ID
	 * @param menuList
	 * @param form
	 */
	public void createFormMenu(List<MenuListRequest> menuList, Form form) {
		if(menuList!=null && !menuList.isEmpty())
		{
			MenuRequest request = new MenuRequest();
			request.setMenus(menuList);
			request.setFormId(form.getUid());
			menuServiceImpl.createMenuListByType(request, BffAdminConstantsUtils.MenuType.FORM_CONTEXT.getType());
		}
	}

	/**Set default form for the flow
	 * @param form
	 * @param actionType
	 */
	private void setDefaultFormForFlow(Form form, ActionType actionType) {
		// setting up default form in flow
		form.getFlow().setDefaultFormId(form.getUid());
		
		if(actionType.equals(ActionType.CONFIRM_PUBLISH)) {
			form.getFlow().setPublishedDefaultFormId(form.getUid());
			form.getFlow().setDefaultFormTabbed(form.isTabbedForm());
			form.getFlow().setDefaultModalForm(form.isModalForm());
		}
	}

	/**
	 * Update the form along with dependencies
	 * 
	 * @param actionType
	 * @param formData
	 * @param identifier
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse modifyForm(ActionType actionType, FormData formData, DefaultType identifier,List<String> permissionIds) {
		BffCoreResponse bffCoreResponse = null;
		try {
			Optional<Flow> flow = flowRepo.findById(formData.getFlowId());

			//Validating form name are unique
			if (flow.isPresent() && !formTransformation.checkUniqueFormName(formData, flow.get().getForms())) {
				return bffResponse.errResponse(
						List.of(BffResponseCode.ERR_FORM_API_FLOW_GIVEN_FORM,
								BffResponseCode.ERR_FORM_USER_FLOW_GIVEN_FORM),
						StatusCode.BADREQUEST, null, formData.getName());

			}
			FormObjDto formObjDto = formTransformation.convertToFormObjDto(formData);
			Form form = formTransformation.convertToFormEntity(formObjDto, null);
			formTransformation.saveEvents(formObjDto, form);
			formTransformation.saveTabs(formObjDto, form);

			bffCoreResponse = validateAndUpdateDefaultFormStrategy(identifier, formObjDto, form, actionType);
			if (bffCoreResponse != null) {
				return bffCoreResponse;
			}

			bffCoreResponse = validateDisableFormCondition(formObjDto, form);
			if (bffCoreResponse != null) {
				return bffCoreResponse;
			}
			// delete field attributes events, data and field values if available
			deleteFieldAttributes(formObjDto);

			// delete field
			deleteField(formObjDto, form);

			// delete tabs
			deleteTabs(formObjDto, form);

			if (formObjDto.getFields() != null && !formObjDto.getFields().isEmpty()) {
				LOGGER.log(Level.DEBUG, "Form is updated for formId : {}", form.getUid());
				for (FieldObjDto fieldObjDto : formObjDto.getFields()) {
					formTransformation.convertToFieldEntity(fieldObjDto, form);
				}
			}
			// Create form context menus
			if (formData.getFormProperties() != null && formData.getFormProperties().getMenus() != null) {
				createFormMenu(formData.getFormProperties().getMenus(), form);
			}
			formDependencyUtil.manageWorkFlowDependency(form);
			formDependencyUtil.findlinkedForms(form, form.getFlow().getDefaultFormId());

			//saving the form
			flowRepo.save(form.getFlow());
			
			if (form != null) {
				if (actionType.equals(ActionType.CONFIRM_PUBLISH)) {
					publishForm(formData.isDefaultForm(), form,permissionIds);
					form = formRepo.save(form);
					LOGGER.log(Level.DEBUG, "Form is updated for action : {}", actionType);

				} else if (actionType.equals(ActionType.CHECK_PUBLISH)) {
					if (form.isDisabled()) {
						return bffResponse.response(BffAdminConstantsUtils.EMPTY_SPACES,
								BffResponseCode.ERR_FORM_API_CHECK_PUBLISH,
								BffResponseCode.ERR_FORM_USER_CHECK_PUBLISH_DISABLE, StatusCode.OK);
					} else {
						return bffResponse.response(BffAdminConstantsUtils.EMPTY_SPACES,
								BffResponseCode.ERR_FORM_API_CHECK_PUBLISH, BffResponseCode.ERR_FORM_USER_CHECK_PUBLISH,
								StatusCode.OK);
					}
				}

				FormResponse modifiedFormData = new FormResponse();
				modifiedFormData.setFormId(form.getUid());
				modifiedFormData.setName(form.getName());

				bffCoreResponse = bffResponse.response(modifiedFormData, BffResponseCode.FORM_SUCCESS_CODE_MODIFY_FORM,
						BffResponseCode.FORM_USER_CODE_MODIFY_FORM, StatusCode.OK, null,
						formObjDto.getName());

			} 
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, formData.getName());
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FORM_API_UPDATE_FORM, BffResponseCode.DB_ERR_FORM_USER_UPDATE_FORM),
					StatusCode.INTERNALSERVERERROR, null, formData.getName());

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, formData.getName());
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FORM_API_UPDATE_FORM_EXCEPTION,
							BffResponseCode.ERR_FORM_USER_UPDATE_FORM_EXCEPTION),
					StatusCode.BADREQUEST, null, formData.getName());
		}

		return bffCoreResponse;
	}

	/**
	 * Validate for setting default form for a flow
	 * 
	 * @param identifier
	 * @param formObjDto
	 * @param form
	 * @return BffCoreResponse
	 */
	private BffCoreResponse validateAndUpdateDefaultFormStrategy(DefaultType identifier, FormObjDto formObjDto,
			Form form, ActionType actionType) {
		BffCoreResponse bffCoreResponse = null;
		boolean changeInDefaultForm = false;
		// Check form is default
		if (formObjDto.isDefaultForm()) {
			if (identifier.equals(DefaultType.CHECK_DEFAULT)) {
				// Already default form exists for this flow
				if (form.getFlow().getDefaultFormId() != null
						&& !(form.getFlow().getDefaultFormId().equals(formObjDto.getFormId()))) {
					bffCoreResponse = bffResponse.errResponse(
							List.of(BffResponseCode.ERR_FORM_API_CHECK_DEFAULT,
									BffResponseCode.ERR_FORM_USER_CHECK_DEFAULT),
							StatusCode.BADREQUEST, null, form.getFlow().getUid().toString());

				} else {
					setDefaultFormForFlow(form, actionType);
				}
			} else if (identifier.equals(DefaultType.CONFIRM_DEFAULT)) {
				setDefaultFormForFlow(form, actionType);
			}
		} else {
			// If default is made false, then reset
			if (null != form.getFlow().getDefaultFormId() && form.getFlow().getDefaultFormId().equals(form.getUid())) {
				form.getFlow().setDefaultFormId(null);
				changeInDefaultForm = true;
			}
			if(actionType.equals(ActionType.CONFIRM_PUBLISH) && null != form.getFlow().getPublishedDefaultFormId()
					&& form.getFlow().getPublishedDefaultFormId().equals(form.getUid())) {
				form.getFlow().setPublishedDefaultFormId(null);
				changeInDefaultForm = true;
			}
			if(changeInDefaultForm) {
				flowRepo.save(form.getFlow());
			}
		}
		return bffCoreResponse;
	}

	/**Validate before making a flow as disable
	 * 
	 * @param formObjDto
	 * @param form
	 * @return BffCoreResponse
	 */
	private BffCoreResponse validateDisableFormCondition(FormObjDto formObjDto, Form form) {
		BffCoreResponse bffCoreResponse = null;
		// Check form is disabled
		if (formObjDto.isDisabledForm() && form.getFlow().getDefaultFormId() != null
				&& form.getUid().equals(form.getFlow().getDefaultFormId())) {
			// If disabled form is default , then show alert
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FORM_API_IS_DISABLED, BffResponseCode.ERR_FORM_USER_IS_DISABLED),
					StatusCode.BADREQUEST, null, form.getUid().toString());
		}
		return bffCoreResponse;
	}

	/** Delete the field attributes like event , data and value
	 * @param formObjDto
	 * @param form
	 */
	private void deleteFieldAttributes(FormObjDto formObjDto) {
		if (formObjDto.getDeleteEvents() != null && !formObjDto.getDeleteEvents().isEmpty()) {
			for (UUID deletEventId : formObjDto.getDeleteEvents()) {
				eventRepo.deleteById(deletEventId);
			}
		}
		if (formObjDto.getDeleteValues() != null && !formObjDto.getDeleteValues().isEmpty()) {
			for (UUID deletValuesId : formObjDto.getDeleteValues()) {
				fieldValRepo.deleteById(deletValuesId);
			}
		}
		if (formObjDto.getDeleteDataValues() != null && !formObjDto.getDeleteDataValues().isEmpty()) {
			for (UUID deletDataValuesId : formObjDto.getDeleteDataValues()) {
				dataRepo.deleteById(deletDataValuesId);
			}
		}
	}

	/**Delete a field and its corresponding children
	 * @param formObjDto
	 * @param form
	 */
	private void deleteField(FormObjDto formObjDto, Form form) {
		if (formObjDto.getDeleteFields() != null && !formObjDto.getDeleteFields().isEmpty()) {
			for (UUID deletFieldId : formObjDto.getDeleteFields()) {
				Optional<Field> rtrvField = fieldRepo.findById(deletFieldId);
				
				if (rtrvField.isPresent()) {
					Field field = rtrvField.get();
					
					//Delete from  custom control - form linkage table
					if(null!= field.getType() && field.getType().contains(BffAdminConstantsUtils.CUSTOM_CONTAINER) && field.getLinkedComponentId()!=null)
					{
						Optional<CustomComponentMaster> customMaster = customComponentMasterRepo.findById(field.getLinkedComponentId());
						if(customMaster.isPresent())
						{
							FormCustomComponent formCustomComponent  = formCustomComponentRepo.findByFormAndCustomComponentMaster(form, customMaster.get());
							if(null!=formCustomComponent)
							{
								formCustomComponentRepo.delete(formCustomComponent);
							}
						}
					}
					
					if (field.getParentField() == null) {
						form.removeField(field);
						formRepo.save(form);
					} else {
						deleteParentAndChild(field);
					}
					fieldRepo.delete(field);
				}
			}
		}
	}

	/**
	 * @param field
	 */
	private void deleteParentAndChild(Field field) {
		Optional<Field> rtrvParentField = fieldRepo.findById(field.getParentField().getUid());
		if (rtrvParentField.isPresent()) {
			Field parentField = rtrvParentField.get();

			parentField.removeChildFields(field);
			fieldRepo.save(parentField);
		}
	}

	/**
	 * Get a form by Id
	 * - Retrieve from table , prepare them and give to ADMIN UI
	 * - Get the data from published json column and give to mobile renderer
	 *  	- Update locale information
	 * 
	 * @param formId
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse getFormById(UUID formId, List<String> permissionIds, UUID menuId) {
		BffCoreResponse bffCoreResponse = null;
		Object responseObject = null;
		try {
			Optional<Form> form = formRepo.findById(formId);
			LOGGER.log(Level.DEBUG, "Requested form for id : {}", formId);

			// If form is present
			if (form.isPresent()) {
				Flow flow = form.get().getFlow();
				// Fetch for mobile renderer
				if (null != sessionDetails.getChannel()
						&& ChannelType.MOBILE_RENDERER.getType().equals(sessionDetails.getChannel())) {

					List<FlowPermission> flowPermissionList = form.get().getFlow().getFlowPermission();
					
					bffCoreResponse = validateFlowAndForm(formId, form, flow);
					
					if (bffCoreResponse != null) {
						return bffCoreResponse;
					}

					//If flow has permission , check with user permission 
					if (flowPermissionList != null && !flowPermissionList.isEmpty()) 
					{
						boolean userPermissionForFlow = bffCommonUtil.checkUserHasPermissionForFlow(flowPermissionList,
								permissionIds);
						if (userPermissionForFlow) 
						{
							responseObject = getPublishedJson(form);
						}
						else
						{
							return bffResponse.errResponse(
									List.of(BffResponseCode.ERR_FLOW_API_INVALID_FLOW_PERMISSION,
											BffResponseCode.ERR_USER_FLOW_API_INVALID_FLOW_PERMISSION),
									StatusCode.FORBIDDEN);
						}
						
					}
					else
					{
						
						responseObject = getPublishedJson(form);
					}
				}
				else {
					responseObject = prepareFormAndFieldData(form, flow);
					FormData formData = (FormData) responseObject;

					if (formData!=null && formData.getFormProperties() != null) {
						formData.getFormProperties().setMenus(getFormMenus(form.get().getUid(),permissionIds,false));
					}
				}
				
			}
			// Check for form is deleted
			else {
				return bffResponse.errResponse(
						List.of(BffResponseCode.ERR_FORM_NOT_FOUND, BffResponseCode.ERR_FORM_NOT_FOUND),
						StatusCode.BADREQUEST, null, formId.toString());
			}

			bffCoreResponse = bffResponse.response(responseObject, BffResponseCode.FORM_SUCCESS_CODE_FETCH_FORM_BY_ID,
					BffResponseCode.FORM_USER_CODE_FETCH_FORM_BY_ID, StatusCode.OK, null,
					formId.toString());	

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, formId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FORM_API_FETCH_FLOW_BY_ID,
							BffResponseCode.DB_ERR_FORM_USER_FETCH_FLOW_BY_ID),
					StatusCode.INTERNALSERVERERROR, null, formId.toString());

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, formId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FORM_API_FETCH_FORM_BY_ID,
							BffResponseCode.ERR_FORM_USER_FETCH_FORM_BY_ID),
					StatusCode.BADREQUEST, null, formId.toString());
		}
		return bffCoreResponse;
	}

	/**Get the published json from the form table
	 *  - update localization variable as per session locale and send back
	 * @param form
	 * @return
	 * @throws IOException
	 */
	private Object getPublishedJson(Optional<Form> form) throws IOException {
		Object responseObject=null;
		if (form.isPresent()) {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode formDataNode = objectMapper.readTree(form.get().getPublishedForm());
		Map<String, String> rbKeyValuePair = new HashMap<>();
		updateResourceBundleMap(formDataNode, rbKeyValuePair);
		updateRbValueBasedOnLocale(formDataNode, rbKeyValuePair);
		responseObject = objectMapper.convertValue(formDataNode, FormData.class);
		}
		return responseObject;
	}

	/**
	 *Get the form for given form ID
	 * -  validate with user permission 
	 *  - validate flow and form
	 *  - Get from published json (Mobile)
	 *  - Update context and global varaibles while navigating to next form(mobile)
	 */
	@Override
	public BffCoreResponse getForm(UUID formId, List<String> permissionIds,List<AppConfigRequest> appConfigList) {
		BffCoreResponse bffCoreResponse = null;
		FormData formData = null;
		try {
			Optional<Form> form = formRepo.findById(formId);
			LOGGER.log(Level.DEBUG, "Form requested for id : {}", formId);

			if (form.isPresent()) {
				bffCoreResponse = validateFlowAndForm(formId, form, form.get().getFlow());

				if (bffCoreResponse != null) {
					return bffCoreResponse;
				}
				List<FlowPermission> flowPermissionList = form.get().getFlow().getFlowPermission();
				if (flowPermissionList != null && !flowPermissionList.isEmpty()) {
					boolean userPermissionForFlow = bffCommonUtil.checkUserHasPermissionForFlow(flowPermissionList,
							permissionIds);
					if (userPermissionForFlow) {

						Object responseObject = getPublishedJson(form);
						formData = (FormData) responseObject;
					} else {
						return bffResponse.errResponse(
								List.of(BffResponseCode.ERR_FLOW_API_INVALID_FLOW_PERMISSION,
										BffResponseCode.ERR_USER_FLOW_API_INVALID_FLOW_PERMISSION),
								StatusCode.FORBIDDEN);
					}

				} else {
					Object responseObject  = getPublishedJson(form);
					formData = (FormData) responseObject;
				}
			} else {
				return bffResponse.errResponse(
						List.of(BffResponseCode.ERR_FORM_NOT_FOUND, BffResponseCode.ERR_FORM_NOT_FOUND),
						StatusCode.BADREQUEST);
			}
			
			//Update the context and global variables from mobile
			if(!CollectionUtils.isEmpty(appConfigList))
			{
				appConfigServiceImpl.createUpdateAppConfigList(appConfigList);
			}

			bffCoreResponse = bffResponse.response(formData, BffResponseCode.FORM_SUCCESS_CODE_FETCH_FORM_BY_ID,
					BffResponseCode.FORM_USER_CODE_FETCH_FORM_BY_ID, StatusCode.OK, null,
					formId.toString());

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, formId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FORM_API_FETCH_FLOW_BY_ID,
							BffResponseCode.DB_ERR_FORM_USER_FETCH_FLOW_BY_ID),
					StatusCode.INTERNALSERVERERROR, null,
					formId.toString());

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, formId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FORM_API_FETCH_FORM_BY_ID,
							BffResponseCode.ERR_FORM_USER_FETCH_FORM_BY_ID),
					StatusCode.BADREQUEST, null, formId.toString());
		}
		return bffCoreResponse;
	}
	/**Prepare a resource bundle as Map
	 * - Collect list of keys
	 * - As per locale prepare the value
	 *   
	 * @param formDataNode
	 * @param rbKeyValuePair
	 * @return Map<String, String> 
	 */
	private Map<String, String> updateResourceBundleMap(JsonNode formDataNode, Map<String, String> rbKeyValuePair) {
		List<JsonNode> rbNodes = formDataNode.findParents(BffAdminConstantsUtils.RBKEY);
		Set<String> rbKeys = new HashSet<>();
		//Find rbkeys from published JSON and add to set 
		rbNodes.forEach(rbNode -> rbKeys.add(rbNode.at(BffAdminConstantsUtils.FORWARD_SLASH+BffAdminConstantsUtils.RBKEY).asText()));
		String locale = sessionDetails.getLocale() != null ? sessionDetails.getLocale()
				: BffAdminConstantsUtils.LOCALE;
		List<ResourceBundle> resourceBundles = resourceBundleRepository.findByLocaleAndRbkeyIn(locale, rbKeys);
		//Add rbkey and rbvalue to map for published form
		for(ResourceBundle resourceBundle : resourceBundles) {
			rbKeyValuePair.put(resourceBundle.getRbkey(), resourceBundle.getRbvalue());
		}
		return rbKeyValuePair;
		
	}
	
	/**Update value based on lcoale and key
	 * @param jsonNode
	 * @param rbKeyValuePair
	 */
	private void updateRbValueBasedOnLocale(JsonNode jsonNode, Map<String, String> rbKeyValuePair) {
		if(jsonNode.isObject() && jsonNode.toString().contains(BffAdminConstantsUtils.RBKEY)) {
			if(jsonNode.has(BffAdminConstantsUtils.RBKEY)) {
				ObjectNode objectNode = (ObjectNode) jsonNode;
				String rbValue = StringUtils.isEmpty(rbKeyValuePair.get(jsonNode.get(BffAdminConstantsUtils.RBKEY).asText())) ? jsonNode.get(BffAdminConstantsUtils.RBKEY).asText() : rbKeyValuePair.get(jsonNode.get(BffAdminConstantsUtils.RBKEY).asText());
				objectNode.put(BffAdminConstantsUtils.RBVALUE, rbValue);
			} else {
				//update locale in published form
				jsonNode.forEach(jsonElement -> {
					if(jsonElement.isObject() || jsonElement.isArray()) {
						updateRbValueBasedOnLocale(jsonElement, rbKeyValuePair);
					}
				});
			}
		} else if(jsonNode.isArray()) {
			jsonNode.forEach(arrayElement ->  updateRbValueBasedOnLocale(arrayElement, rbKeyValuePair));
		}
	}
	/**Validate the flow and form
	 * - Send error message
	 * @param formId
	 * @param form
	 * @param flow
	 * @return
	 */
	private BffCoreResponse validateFlowAndForm(UUID formId, Optional<Form> form, Flow flow) {
		// Check for flow is disabled
		if (flow.isDisabled()) {
			return bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FLOW_DISABLE_CD, BffResponseCode.ERR_FLOW_DISABLE_CD),
					StatusCode.BADREQUEST, null, formId.toString());
		}
		// Check for flow is published
		if (!flow.isPublishedFlow()) {
			return bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FLOW_UPUBLISH_CD, BffResponseCode.ERR_FLOW_UPUBLISH_CD),
					StatusCode.BADREQUEST, null, formId.toString());
		}
		// Check for form is published
		if (form.isPresent() && form.get().getPublishedForm() == null) {
			return bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FORM_UPUBLISH_CD, BffResponseCode.ERR_FORM_UPUBLISH_CD),
					StatusCode.BADREQUEST, null, formId.toString());
		}

		// Check for form is disabled
		if (form.isPresent() && form.get().isDisabled()) {
			return bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FORM_DISABLE_CD, BffResponseCode.ERR_FORM_DISABLE_CD),
					StatusCode.BADREQUEST, null, formId.toString());
		}
		return null;

	}

	/** Prepare the form and field attributes 
	 * @param form
	 * @param flow
	 * @return
	 * @throws IOException
	 */
	private Object prepareFormAndFieldData(Optional<Form> form, Flow flow) throws IOException {
		Object responseObject = null;
		List<FieldObjDto> fieldObjDtoList = null;
		if (form.isPresent()) {
			if (form.get().getFields() != null && !form.get().getFields().isEmpty()) {
				fieldObjDtoList = formTransformation.convertToFieldDto(form.get().getFields());
			}
			FormObjDto formObjDto = new FormObjDto(form.get(), fieldObjDtoList);

			if (flow.getDefaultFormId() != null && form.get().getUid().equals(flow.getDefaultFormId())) {
				formObjDto.setDefaultForm(true);
			}
			responseObject = formTransformation.createFormData(formObjDto);
		}
		return responseObject;
	}

	/** Fetches the form context menu for given form id
	 * @param formId
	 * @param permissionIds
	 * @param isPublished
	 * @return
	 */
	public List<MenuListRequest> getFormMenus(UUID formId, List<String> permissionIds,boolean isPublished) {
		BffCoreResponse response = menuServiceImpl.fetchMenusByFormId(formId,permissionIds,isPublished);
		if (response != null && response.getDetails() != null && response.getDetails().getData() != null) {
			MenuDto menuDto = (MenuDto) response.getDetails().getData();
			if (null != menuDto && !menuDto.getMenus().isEmpty()) {
				return convertToMenuListRequest(menuDto.getMenus());
			}
		}		
		return new ArrayList<>();
	}

	/**Convert MenuListDto to MenuListRequest
	 * @param menus
	 * @return
	 */
	private List<MenuListRequest> convertToMenuListRequest(List<MenuListDto> menus) {
		List<MenuListRequest> menuList = new ArrayList<>();

		for (MenuListDto menu : menus) {
			MenuListRequest menuListRequest = setToMenuListRequest(menu);
			List<MenuListRequest> subMenuList = new ArrayList<>();
			if (menu.getSubMenus() != null && !menu.getSubMenus().isEmpty()) {
				for (MenuListDto subMenu : menu.getSubMenus()) {
					MenuListRequest subMenuRequest = setToMenuListRequest(subMenu);
					subMenuList.add(subMenuRequest);
				}
			}

			menuListRequest.setSubMenus(subMenuList);
			menuList.add(menuListRequest);
		}
		return menuList;
	}

	/**Convert MenuListDto to MenuListRequest
	 * 
	 * @param menu
	 * @return
	 */
	private MenuListRequest setToMenuListRequest(MenuListDto menu) {
		MenuListRequest request = new MenuListRequest();
		request.setIconAlignment(menu.getIconAlignment());
		request.setIconName(menu.getIconName());
		request.setMenuType(menu.getMenuType());
		request.setPermissions(menu.getPermissions());
		request.setMenuName(menu.getMenuName());
		request.setMenuAction(menu.getMenuAction());
		request.setShowInToolBar(menu.isShowInToolBar());
		request.setDefaultFormId(menu.getDefaultFormId());
		request.setFlowId(menu.getFlowId());
		request.setTabbedForm(menu.isTabbedForm());
		request.setHotKey(menu.getHotKey());
		request.setHotKeyName(menu.getHotKeyName());
		request.setUid(menu.getUid());

		return request;
	}

	/**
	 * Delete a form by Id
	 * 
	 * @param formId
	 * @param identifier
	 * @return BffCoreResponse
	 */
	@Override
	@Transactional
	public BffCoreResponse deleteFormByID(UUID formId, DeleteType identifier) {
		BffCoreResponse bffCoreResponse = null;
		try {
			Optional<Form> form = formRepo.findById(formId);
			
			String result = null;
			if(form.isPresent())
			{
				LOGGER.log(Level.DEBUG, "Form is deleted for id : {} and type {}", formId, identifier);
	
				result = performDeleteOnActionType(identifier, form.get());
			}
			bffCoreResponse = bffResponse.response(result, BffResponseCode.FORM_SUCCESS_CODE_DELETE_FORM_BY_ID,
					BffResponseCode.FORM_USER_CODE_DELETE_FORM_BY_ID, StatusCode.OK,
					identifier.getType(),formId.toString());

		} catch (NoSuchElementException exp) {

			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, formId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FORM_API_DELETE_FORM_BY_ID,
							BffResponseCode.ERR_FORM_USER_DELETE_FORM_BY_ID),
					StatusCode.BADREQUEST, null, formId.toString());
		} catch (DataAccessException exp) {

			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, formId);
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, identifier);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FORM_API_DELETE_FORM_BY_ID,
							BffResponseCode.DB_ERR_FORM_USER_DELETE_FORM_BY_ID),
					StatusCode.INTERNALSERVERERROR, null, formId.toString());

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, formId);
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, identifier, exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FORM_API_DELETE_FORM_BY_ID_EXCEPTION,
							BffResponseCode.ERR_FORM_USER_DELETE_FORM_BY_ID_EXCEPTION),
					StatusCode.BADREQUEST, null, formId.toString());

		}
		return bffCoreResponse;
	}

	/**Publish a form
	 * - Prepare the published form
	 * - update dependencies table
	 *
	 */
	@Override
	public BffCoreResponse publishForm(UUID formId, ActionType actionType,List<String> permissionIds) {
		BffCoreResponse bffCoreResponse = null;
		ObjectMapper objectMapper = null;
		try {
			Optional<Form> optionalForm = formRepo.findById(formId);
			
			if(optionalForm.isPresent())
			{
				Form form = optionalForm.get();
				LOGGER.log(Level.DEBUG, "Form is fetched for id : {}", formId);
	
				if (actionType.equals(ActionType.CHECK_PUBLISH)) {
					if (form.isDisabled()) {
						return bffResponse.response(BffAdminConstantsUtils.EMPTY_SPACES,
								BffResponseCode.ERR_FORM_API_CHECK_PUBLISH,
								BffResponseCode.ERR_FORM_USER_CHECK_PUBLISH_DISABLE, StatusCode.OK);
					} else {
						return bffResponse.response(BffAdminConstantsUtils.EMPTY_SPACES,
								BffResponseCode.ERR_FORM_API_CHECK_PUBLISH, BffResponseCode.ERR_FORM_USER_CHECK_PUBLISH,
								StatusCode.OK);
					}
				} else {					
					form.setPublished(true);
					
					//If default form , then update in flow table
					if(form.getFlow().getDefaultFormId()!=null && form.getFlow().getDefaultFormId().equals(form.getUid()))
					{
						form.getFlow().setPublishedDefaultFormId(form.getUid());
						form.getFlow().setDefaultFormTabbed(form.isTabbedForm());
						form.getFlow().setDefaultModalForm(form.isModalForm());
					}
					
					List<FieldObjDto> fieldObjDtoList = null;
					if (form.getFields() != null && !form.getFields().isEmpty()) {
						fieldObjDtoList = formTransformation.convertToFieldDto(form.getFields());
					}
					FormObjDto formObjDto = new FormObjDto(form, fieldObjDtoList);
					FormData formData = formTransformation.createFormData(formObjDto);
					
					//Get the Form context menus
					formData.getFormProperties().setMenus(getFormMenus(form.getUid(),permissionIds,true));
	
					// Generating publishedFormJson for formData
					objectMapper = new ObjectMapper();
					form.setPublishedForm(objectMapper.writeValueAsBytes(formData));
					formDependencyUtil.managePublishedWorkFlowDependency(form);
					formDependencyUtil.findLinkedPublishedFormIds(form, form.getFlow().getPublishedDefaultFormId());
					bffCoreResponse = bffResponse.response(formData, BffResponseCode.FORM_SUCCESS_CODE_PUBLISH_FORM,
							BffResponseCode.FORM_USER_CODE_PUBLISH_FORM, StatusCode.OK, null,
							formId.toString());
				}
			}
			else
			{
				return bffResponse.errResponse(
						List.of(BffResponseCode.ERR_FORM_NOT_FOUND, BffResponseCode.ERR_FORM_NOT_FOUND),
						StatusCode.BADREQUEST);
			}
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, formId);
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, actionType);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FORM_API_PUBLISH_FORM,
							BffResponseCode.DB_ERR_FORM_USER_PUBLISH_FORM),
					StatusCode.INTERNALSERVERERROR, null, formId.toString());
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, formId);
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, actionType);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FORM_API_PUBLISH_FORM_EXCEPTION,
							BffResponseCode.ERR_FORM_USER_PUBLISH_FORM_EXCEPTION),
					StatusCode.BADREQUEST, null, formId.toString());
		}
		return bffCoreResponse;
	}

	/**
	 * @param identifier
	 * @param form
	 * @return String
	 */
	private String performDeleteOnActionType(DeleteType identifier, Form form) {
		String result = null;
		if (identifier.equals(DeleteType.CONFIRM_DELETE)) {
			result = deleteForm(form);

		} else {
			List<FormDependency> formDependencies = formDependencyRepository
					.findByInboundFormIdOrOutboundFormId(form.getUid(), form.getUid());
			if (!CollectionUtils.isEmpty(formDependencies)) {
				LOGGER.debug("formIndepList not empty");
				result = CHECK_FORM_DELETION_MESSAGE;
			}
		}
		return result;
	}

	/**Delete a form and its dependencies table
	 * @param form
	 * @return String
	 */
	private String deleteForm(Form form) {

		formRepo.deleteById(form.getUid());

		Flow flow = flowRepo.findByDefaultFormId(form.getUid());
		if (flow != null) {
			flow.setDefaultFormId(null);
			flowRepo.save(flow);
		}
		List<FormDependency> formDependencies = formDependencyRepository
				.findByInboundFormIdOrOutboundFormId(form.getUid(), form.getUid());
		List<FormDependency> deletedFormDependencies = new ArrayList<>();
		formDependencies.parallelStream().forEach(formDependency -> {
			
			if(formDependency.getOutboundFlowId() == null || formDependency.getOutboundFormId().equals(form.getUid())) {
				deletedFormDependencies.add(formDependency);
			}
			if(formDependency.getOutboundFlowId() != null) {
				formDependency.setOutboundFormId(null);
			}
		});
		formDependencies.removeAll(deletedFormDependencies);
		formDependencyRepository.deleteAll(deletedFormDependencies);
		formDependencyRepository.saveAll(formDependencies);
		LOGGER.log(Level.DEBUG, "form successfully deleted with formId : {}", form.getUid());
		return form.getUid().toString();
	}

	/**
	 * Retrieves all forms for given flowID
	 * 
	 * @param flowId
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchAllForms(UUID flowId) {
		BffCoreResponse bffCoreResponse = null;
		List<FormData> formDtoList = new ArrayList<>();
		List<Form> formList = null;
		try {
			Optional<Flow> flow = flowRepo.findById(flowId);

			if (flow.isPresent()) {
				LOGGER.log(Level.DEBUG, "FlowId for all form reterival : {}", flowId);
				formList = flow.get().getForms();

				if (!CollectionUtils.isEmpty(formList)) {
					for (Form form : formList) {

						List<FieldObjDto> fieldObjDtoList = null;
						if (!CollectionUtils.isEmpty(form.getFields())) {
							fieldObjDtoList = formTransformation.convertToFieldDto(form.getFields());
						}
						FormObjDto formObjDto = new FormObjDto(form, fieldObjDtoList);
						if ((flow.get().getDefaultFormId() != null)
								&& (form.getUid().equals(flow.get().getDefaultFormId()))) {
							formObjDto.setDefaultForm(true);
						}
						FormData formData = formTransformation.createFormData(formObjDto);
						if (formData.getFormProperties() != null) {
							formData.getFormProperties().setMenus(getFormMenus(form.getUid(),null,false));
						}
						formDtoList.add(formData);
					}
				}
				bffCoreResponse = bffResponse.response(formDtoList, BffResponseCode.FORM_SUCCESS_CODE_FETCH_ALL_FORMS,
						BffResponseCode.FORM_USER_CODE_FETCH_ALL_FORMS, StatusCode.OK, null,
						flowId.toString());
			}

			else {
				bffCoreResponse = bffResponse.errResponse(
						List.of(BffResponseCode.ERR_FORM_NOT_FOUND, BffResponseCode.ERR_FORM_NOT_FOUND),
						StatusCode.BADREQUEST);
			}

		} catch (DataAccessException exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, flowId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FORM_API_FETCH_ALL_FORMS,
							BffResponseCode.DB_ERR_FORM_USER_FETCH_ALL_FORMS),
					StatusCode.INTERNALSERVERERROR, null, flowId.toString());

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, flowId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FORM_API_FETCH_ALL_FORMS_EXCEPTION,
							BffResponseCode.ERR_FORM_USER_FETCH_ALL_FORMS_EXCEPTION),
					StatusCode.BADREQUEST, null, flowId.toString());
		}
		return bffCoreResponse;
	}

	/**
	 * Retrieves Orphan forms
	 * 
	 * @param flowId
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchOrphanForms(UUID flowId) {
		BffCoreResponse bffCoreResponse = null;
		LOGGER.log(Level.DEBUG, "FlowId for all Orphan form reterival : {}", flowId);
		try {
			List<Form> orphanForms = formRepo.getOrphanFormsByFlowId(flowId);
			List<FormData> formDtoList = new ArrayList<>();
			if (!CollectionUtils.isEmpty(orphanForms)) {
				for (Form form : orphanForms) {
					FormObjDto formObjDto = new FormObjDto(form, null);
					FormData formData = formTransformation.createFormData(formObjDto);
					formDtoList.add(formData);
				}

			}
			bffCoreResponse = bffResponse.response(formDtoList, BffResponseCode.FORM_SUCCESS_CODE_FETCH_ORPHAN_FORMS,
					BffResponseCode.FORM_USER_CODE_FETCH_ORPHAN_FORMS, StatusCode.OK, null,
					flowId.toString());

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, flowId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FORM_API_FETCH_ORPHAN_FORMS,
							BffResponseCode.DB_ERR_FORM_USER_FETCH_ORPHAN_FORMS),
					StatusCode.INTERNALSERVERERROR, null, flowId.toString());

		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, flowId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FORM_API_FETCH_ORPHAN_FORMS_EXCEPTION,
							BffResponseCode.ERR_FORM_USER_FORM_DETAILS),
					StatusCode.BADREQUEST, null, flowId.toString());
		}
		return bffCoreResponse;
	}

	/**
	 * Retrieves unPublish forms
	 * 
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchUnpublishForms() {
		BffCoreResponse bffCoreResponse = null;
		List<FormObjDto> fetchAllForms = null;
		List<Form> formList = null;
		try {
			List<ProductConfig> prodConfigList = productPrepareService.getLayeredProductConfigList();
			List<UUID> prodConfigIdList = prodConfigList.stream().distinct().flatMap(item -> Stream.of(item.getUid()))
					.collect(Collectors.toList());
			if (!prodConfigIdList.isEmpty()) {
				fetchAllForms = new ArrayList<>();
				formList = formRepo
						.findByIsPublishedFalseAndProductConfigIdInOrderByLastModifiedDateDesc(prodConfigIdList);
				for (Form form : formList) {
					fetchAllForms.add(new FormObjDto(form, null));
				}
				bffCoreResponse = bffResponse.response(fetchAllForms,
						BffResponseCode.FORM_SUCCESS_CODE_FECTH_UNPUBLISH_FORMS,
						BffResponseCode.FORM_USER_CODE_FECTH_UNPUBLISH_FORMS, StatusCode.OK);
			} else {
				throw new BffException("Error encountered while fetching ProductConfigList");
			}

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.DB_ERR_FORM_API_015_FECTH_UNPUBLISH_FORMS,
					BffResponseCode.DB_ERR_FORM_USER_FECTH_UNPUBLISH_FORMS), StatusCode.INTERNALSERVERERROR);

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_FORM_API_FECTH_UNPUBLISH_FORMS,
					BffResponseCode.ERR_FORM_USER_FECTH_UNPUBLISH_FORMS), StatusCode.BADREQUEST);

		}
		return bffCoreResponse;
	}

	/**
	 * Create default form
	 * 
	 * @param formId String
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse createDefaultForm(UUID formId, DefaultType identifier) {
		BffCoreResponse bffCoreResponse = null;
		try {
			Optional<Form> form = formRepo.findById(formId);
			if(form.isPresent())
			{
				Flow flow = form.get().getFlow();
				if (identifier.equals(DefaultType.CHECK_DEFAULT)) {
					if (flow.getDefaultFormId() != null) {
						bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_FORM_API_ENUM_CHECK_DEFAULT,
								BffResponseCode.ERR_FORM_USER_ENUM_CHECK_DEFAULT), StatusCode.BADREQUEST);
					} else {
						flow.setDefaultFormId(formId);
						formDependencyUtil.findlinkedForms(form.get(), form.get().getFlow().getDefaultFormId());
						bffCoreResponse = bffResponse.response(formId.toString(),
								BffResponseCode.FORM_SUCCESS_CODE_CREATE_DEFAULT_FORM,
								BffResponseCode.FORM_USER_CODE_CREATE_DEFAULT_FORM, StatusCode.OK, null,
								formId.toString());
					}
				} else {
					flow.setDefaultFormId(formId);
					flow.setDefaultFormTabbed(form.get().isTabbedForm());
					formDependencyUtil.findlinkedForms(form.get(), form.get().getFlow().getDefaultFormId());
					bffCoreResponse = bffResponse.response(formId.toString(),
							BffResponseCode.FORM_SUCCESS_CODE_CREATE_DEFAULT_FORM,
							BffResponseCode.FORM_USER_CODE_CREATE_DEFAULT_FORM, StatusCode.OK, null,
							formId.toString());
				}
			}
			else
			{
				bffCoreResponse =  bffResponse.errResponse(
						List.of(BffResponseCode.ERR_FORM_NOT_FOUND, BffResponseCode.ERR_FORM_NOT_FOUND),
						StatusCode.BADREQUEST);
			}

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, formId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FORM_API_CREATE_DEFAULT_FORM,
							BffResponseCode.DB_ERR_FORM_USER_CREATE_DEFAULT_FORM),
					StatusCode.INTERNALSERVERERROR, null, formId.toString());

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, formId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FORM_API_CREATE_DEFAULT_FORM_EXCEPTION,
							BffResponseCode.ERR_FORM_USER_CREATE_DEFAULT_FORM_EXCEPTION),
					StatusCode.BADREQUEST, null, formId.toString());
		}
		return bffCoreResponse;
	}

	/**Retrieves the list of form where the given custom control is used
	 * 
	 * @param customComponentId
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse getFormDetails(UUID customComponentId) {
		BffCoreResponse bffCoreResponse = null;
		try {
			Optional<CustomComponentMaster> customComponentMaster = customComponentMasterRepo
					.findById(customComponentId);

			if (customComponentMaster.isPresent()) {
				List<FormCustomComponent> formCustomComponentList = customComponentMaster.get()
						.getFormCustomComponent();
				// Extracting list of forms using given custom component
				List<Form> customCompFormList = formCustomComponentList.stream().map(FormCustomComponent::getForm)
						.collect(Collectors.toList());
				LOGGER.log(Level.DEBUG, "Extracting the forms with CustomComponentid: {}", customComponentId);
				List<FormData> formDtoList = null;
				if (!CollectionUtils.isEmpty(customCompFormList)) {
					formDtoList = new ArrayList<>();
					for (Form form : customCompFormList) {
						FormObjDto formObjDto = new FormObjDto(form, null);
						FormData formData = formTransformation.createFormData(formObjDto);
						formDtoList.add(formData);
					}
				}

				FormCustomDto formCustom = new FormCustomDto(customComponentMaster.get().getName(),
						customComponentMaster.get().getUid(), formDtoList);

				bffCoreResponse = bffResponse.response(formCustom, BffResponseCode.FORM_SUCCESS_CODE_FORM_DETAILS,
						BffResponseCode.FORM_USER_CODE_FORM_DETAILS, StatusCode.OK, null,
						customComponentId.toString());
			} else {
				bffCoreResponse = bffResponse.errResponse(
						List.of(BffResponseCode.ERR_CUSTOM_CTRL_NOT_FOUND, BffResponseCode.ERR_CUSTOM_CTRL_NOT_FOUND),
						StatusCode.BADREQUEST);
			}

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, customComponentId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FORM_API_FORM_DETAILS,
							BffResponseCode.DB_ERR_FORM_USER_013_FORM_DETAILS),
					StatusCode.INTERNALSERVERERROR, null, customComponentId.toString());
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, customComponentId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FORM_API_FORM_DETAILS, BffResponseCode.ERR_FORM_USER_FORM_DETAILS),
					StatusCode.BADREQUEST, null, customComponentId.toString());
		}
		return bffCoreResponse;
	}

	/**
	 *Fetches list of form with lite/basic information
	 * - Used in populating drop down in ADMIN UI
	 */
	@Override
	public BffCoreResponse fetchFormBasicList(UUID flowId) {
		BffCoreResponse bffCoreResponse = null;
		List<FormLiteDto> formDetailsDto = null;
		try {
			Optional<Flow> optionalFlow = flowRepo.findById(flowId);
			if (optionalFlow.isPresent()) {
				formDetailsDto = formRepo.getFormBasicList(optionalFlow.get());
				
				if (CollectionUtils.isEmpty(formDetailsDto)) {
					bffCoreResponse = bffResponse.response(formDetailsDto,
							BffResponseCode.ERR_FORM_API_FETCH_FORM_LIST_EMPTY_CHECK,
							BffResponseCode.ERR_FORM_USER_FETCH_FORM_LIST_EMPTY_CHECK, StatusCode.OK, null,
							flowId.toString());
					return bffCoreResponse;
				}
				bffCoreResponse = bffResponse.response(formDetailsDto,
						BffResponseCode.FORM_SUCCESS_CODE_FETCH_FORM_NAMES,
						BffResponseCode.FORM_USER_CODE_FETCH_FORM_NAMES, StatusCode.OK);
			} else {
				bffCoreResponse = bffResponse.errResponse(
						List.of(BffResponseCode.ERR_FORM_API_FETCH_FORM_NAMES,
								BffResponseCode.ERR_FORM_USER_FETCH_FORM_NAMES),
						StatusCode.OK, null, flowId.toString());
				return bffCoreResponse;
			}
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, flowId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.DB_ERR_FORM_API_FETCH_FORM_NAMES,
					BffResponseCode.DB_ERR_FORM_USER_FETCH_FORM_NAMES), StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, flowId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_FORM_API_FETCH_FORM_NAMES_EXCEPTION,
					BffResponseCode.ERR_FORM_USER_FETCH_FORM_NAMES_EXCEPTION), StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**
	 *Fetches unpublished and orphan forms
	 */
	@Override
	public BffCoreResponse fetchUnpublishOrphanForms(UUID productConfigId, FormStatus identifier) {
		BffCoreResponse bffCoreResponse = null;
		List<FormObjDto> fetchAllForms = new ArrayList<>();
		List<Form> formList = new ArrayList<>();
		try {
			List<ProductConfig> prodConfigList = productPrepareService.getLayeredProductConfigList();
			List<UUID> prodConfigIdList = prodConfigList.stream().distinct().flatMap(item -> Stream.of(item.getUid()))
					.collect(Collectors.toList());
			switch (identifier) {

			case UNPUBLISH:
				if (!prodConfigIdList.isEmpty()) {
					formList = formRepo
							.findByIsPublishedFalseAndProductConfigIdInOrderByLastModifiedDateDesc(prodConfigIdList);
				}
				for (Form form : formList) {
					fetchAllForms.add(new FormObjDto(form, null));
				}
				bffCoreResponse = bffResponse.response(fetchAllForms,
						BffResponseCode.FORM_SUCCESS_CODE_UNPUBLISH_ORPHAN_FORMS,
						BffResponseCode.FORM_USER_CODE_UNPUBLISH_ORPHAN_FORMS, StatusCode.OK);
				break;
			case ORPHAN:
				if (!prodConfigIdList.isEmpty()) {
					formList = formRepo.getOrphanForms(prodConfigIdList);
				}
				for (Form form : formList) {
					fetchAllForms.add(new FormObjDto(form, null));
				}
				bffCoreResponse = bffResponse.response(fetchAllForms, BffResponseCode.FORM_SUCCESS_CODE_ORPHAN_FORMS,
						BffResponseCode.FORM_USER_CODE_ORPHAN_FORMS, StatusCode.OK);
				break;
			case ALL:
				if (!prodConfigIdList.isEmpty()) {
					formList = formRepo.findByProductConfigIdInOrderByLastModifiedDateDesc(prodConfigIdList);
				}
				for (Form form : formList) {
					fetchAllForms.add(new FormObjDto(form, null));
				}
				bffCoreResponse = bffResponse.response(fetchAllForms, BffResponseCode.FORM_SUCCESS_CODE_ORPHAN_FORMS,
						BffResponseCode.FORM_USER_CODE_ORPHAN_FORMS, StatusCode.OK);
				break;
			}

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, productConfigId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.DB_ERR_FORM_API_UNPUBLISH_ORPHAN_FORMS,
					BffResponseCode.DB_ERR_FORM_USER_UNPUBLISH_ORPHAN_FORMS), StatusCode.INTERNALSERVERERROR);

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, productConfigId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse
					.errResponse(
							List.of(BffResponseCode.ERR_FORM_API_UNPUBLISH_ORPHAN_FORMS_EXCEPTION,
									BffResponseCode.ERR_FORM_USER_UNPUBLISH_ORPHAN_FORMS_EXCEPTION),
							StatusCode.BADREQUEST);

		}
		return bffCoreResponse;
	}

	/**Delete the tab from a form
	 * 
	 * @param formObjDto
	 * @param form
	 */
	private void deleteTabs(FormObjDto formObjDto, Form form) {
		if (!CollectionUtils.isEmpty(formObjDto.getDeleteTabs())) {
			for (UUID tabId : formObjDto.getDeleteTabs()) {
				Optional<Tabs> tab = tabRepo.findById(tabId);
				if (tab.isPresent()) {
					form.removeTabs(tab.get());
					formRepo.save(form);
					tabRepo.deleteById(tabId);
					List<FormDependency> dependencies = formDependencyRepository
							.findByInboundFormIdAndOutboundFormId(form.getUid(), tab.get().getLinkedFormId());
					if (!CollectionUtils.isEmpty(dependencies)) {
						formDependencyRepository.deleteAll(dependencies);
					}
				}
			}
		}
	}
	
}
