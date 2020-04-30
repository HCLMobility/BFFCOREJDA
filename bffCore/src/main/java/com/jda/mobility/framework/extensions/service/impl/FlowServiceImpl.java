package com.jda.mobility.framework.extensions.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.dto.FieldObjDto;
import com.jda.mobility.framework.extensions.dto.FlowDefaultFormDto;
import com.jda.mobility.framework.extensions.dto.FlowDto;
import com.jda.mobility.framework.extensions.dto.FormObjDto;
import com.jda.mobility.framework.extensions.entity.AppConfigDetail;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.entity.Events;
import com.jda.mobility.framework.extensions.entity.ExtendedFlowBase;
import com.jda.mobility.framework.extensions.entity.Field;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.FlowPermission;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.entity.Tabs;
import com.jda.mobility.framework.extensions.entity.projection.FlowLiteDto;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.CloneRequest;
import com.jda.mobility.framework.extensions.model.FlowRequest;
import com.jda.mobility.framework.extensions.model.FormData;
import com.jda.mobility.framework.extensions.model.Layer;
import com.jda.mobility.framework.extensions.model.MenuListRequest;
import com.jda.mobility.framework.extensions.model.UserPermissionRequest;
import com.jda.mobility.framework.extensions.repository.ApiMasterRepository;
import com.jda.mobility.framework.extensions.repository.ApiRegistryRepository;
import com.jda.mobility.framework.extensions.repository.AppConfigMasterRepository;
import com.jda.mobility.framework.extensions.repository.CustomComponentMasterRepository;
import com.jda.mobility.framework.extensions.repository.ExtendedFlowBaseRepository;
import com.jda.mobility.framework.extensions.repository.FlowPermissionRepository;
import com.jda.mobility.framework.extensions.repository.FlowRepository;
import com.jda.mobility.framework.extensions.repository.FormRepository;
import com.jda.mobility.framework.extensions.repository.MenuMasterRepository;
import com.jda.mobility.framework.extensions.repository.TabRepository;
import com.jda.mobility.framework.extensions.service.FlowService;
import com.jda.mobility.framework.extensions.service.ProductPrepareService;
import com.jda.mobility.framework.extensions.transformation.FormTransformation;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ActionType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.AppCfgRequestType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ChannelType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.CloneType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DeleteType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DisableType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.FlowType;
import com.jda.mobility.framework.extensions.utils.BffCommonUtil;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.FormDependencyUtil;

/**
 * Implementation for create /update /fetch/ delete flows
 * 
 * @author HCL Technologies
 */
@Service
public class FlowServiceImpl implements FlowService {
	private static final String FETCHING_THE_FLOWS_PID_MSG = "Fetching the flows with product Config Id: {}";

	private static final Logger LOGGER = LogManager.getLogger(FlowServiceImpl.class);

	@Autowired
	private FlowRepository flowRepository;
	@Autowired
	private ApiMasterRepository apiMasterRepository;
	@Autowired
	private ApiRegistryRepository apiRegistryRepository;
	@Autowired
	private CustomComponentMasterRepository customComponentMasterRepository;
	@Autowired
	private AppConfigMasterRepository appConfigRepo;
	@Autowired
	private FormRepository formRepo;
	@Autowired
	private BffResponse bffResponse;
	/** The field sessionDetails of type SessionDetails */
	@Autowired
	private SessionDetails sessionDetails;
	@Autowired
	BffCommonUtil commonUtil;
	@Autowired
	MenuMasterRepository menuMasterRepo;
	@Autowired
	private ExtendedFlowBaseRepository extendedFlowBaseRepo;
	@Autowired
	private FormTransformation formTransformation;
	@Autowired
	private ProductPrepareService productPrepareService;
	@Autowired
	private FlowPermissionRepository flowPermissionRepository;
	@Autowired
	private FormServiceImpl formServiceImpl;
	@Autowired
	private BffCommonUtil bffCommonUtil;
	@Autowired
	private TabRepository tabRepo;
	@Autowired
	private FormDependencyUtil dependencyUtil;

	/**
	 * Creates a new flow 
	 * - Check for uniqueness in name
	 * e
	 * @param flowRequest
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse createFlow(FlowRequest flowRequest) {
		BffCoreResponse bffCoreResponse = null;
		try {
			// Check for existing flow
			List<Flow> flowList = flowRepository.findByNameAndVersion(flowRequest.getName(),
					BffAdminConstantsUtils.FLOW_INITIAL_VERSION);
			// If flow name already present , send conflict code
			if (!flowList.isEmpty()) {
				bffCoreResponse = bffResponse.errResponse(
						List.of(BffResponseCode.ERR_FLOW_API_CREATE_FLOW, BffResponseCode.ERR_FLOW_USER_CREATE_FLOW),
						StatusCode.CONFLICT, null, flowRequest.getName());
			}
			// if flow name is unique then save it
			else {
				Flow flow = convertToFlowEntity(flowRequest);

				flow = flowRepository.save(flow);
				LOGGER.log(Level.DEBUG, "Flow saved with generate flowId : {}", flow.getUid());

				List<String> permissionList = getFlowPermissionList(flow);
				FlowDto flowDto = convertToFlowDto(flow, permissionList);

				bffCoreResponse = bffResponse.response(flowDto, BffResponseCode.FLOW_SUCCESS_CODE_CREATE_FLOW,
						BffResponseCode.FLOW_USER_CODE_CREATE_FLOW, StatusCode.CREATED, null,
						flow.getUid().toString());
			}
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, flowRequest.getName(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FLOW_API_CREATE_FLOW, BffResponseCode.DB_ERR_FLOW_USER_CREATE_FLOW),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, flowRequest.getName(), exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_FLOW_API_CREATE_FLOW_EXCEPTION,
					BffResponseCode.ERR_FLOW_USER_CREATE_FLOW_EXCEPTION), StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**
	 * Update existing flow using its ID
	 * - Check for uniqueness with name and version
	 * - Validate Default and home flow cannot be disabled check
	 * 
	 * @param flowRequest
	 * @param actionType
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse modifyFlow(FlowRequest flowRequest, ActionType actionType, DisableType identifier,
			List<String> permissionIds) {
		BffCoreResponse bffCoreResponse = null;
		try {
			final ActionType action = actionType;
			LOGGER.log(Level.DEBUG, "Modify flow for flowId : {} and ActionType : {}", flowRequest.getFlowId(), action);
			Optional<Flow> optionalFlow = flowRepository.findById(flowRequest.getFlowId());
			if (!optionalFlow.isPresent()) {
				return bffResponse.errResponse(
						List.of(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_BY_ID,
								BffResponseCode.DB_ERR_FLOW_USER_FETCH_FLOW_BY_ID),
						StatusCode.INTERNALSERVERERROR, null, flowRequest.getFlowId().toString());
			}
			Flow flow = optionalFlow.get();

			// Validating flow name
			if (!flow.getName().equalsIgnoreCase(flowRequest.getName())) {
				List<Flow> flowList = flowRepository.findByNameAndVersion(flowRequest.getName(),
						flowRequest.getVersion());
				if (flowList != null && !flowList.isEmpty()) {
					return bffResponse.errResponse(
							List.of(BffResponseCode.ERR_FLOW_API_MODIFY_FLOW,
									BffResponseCode.ERR_FLOW_USER_MODIFY_FLOW),
							StatusCode.BADREQUEST, null, flowRequest.getName());
				}
			}

			bffCoreResponse = validateDisableFlow(flowRequest, identifier, flow);
			if (null != bffCoreResponse) {
				return bffCoreResponse;
			}

			// For action SAVE and CHECK_PUBLISH
			if (!action.equals(ActionType.CONFIRM_PUBLISH)) {
				flow = updateExistingFlowWithPermission(flow, flowRequest);
				// Call to update
				flow = flowRepository.save(flow);

				List<String> permissionList = getFlowPermissionList(flow);
				FlowDto flowDto = convertToFlowDto(flow, permissionList);

				// Validation for CHECK_PUBLISH
				if (action.equals(ActionType.CHECK_PUBLISH)) {
					LOGGER.log(Level.DEBUG, "Flow is validated successfully for id {}", flow.getUid());
					return bffResponse.response(BffAdminConstantsUtils.EMPTY_SPACES,
							BffResponseCode.FLOW_SUCCESS_CODE_MODIFY_FLOW_CHECK_PUBLISH,
							BffResponseCode.FLOW_USER_CODE_MODIFY_FLOW_CHECK_PUBLISH, StatusCode.OK, null,
							flowRequest.getFlowId().toString());
				} else {
					LOGGER.log(Level.DEBUG, "Flow is updated successfully for id {}", flow.getUid());
					return bffResponse.response(flowDto, BffResponseCode.FLOW_SUCCESS_CODE_MODIFY_FLOW,
							BffResponseCode.FLOW_USER_CODE_MODIFY_FLOW, StatusCode.OK, null,
							flow.getUid().toString());
				}
			}
			// For Action CONFIRM_PUBLISH
			else {
				updateExistingFlowWithPermission(flow, flowRequest);
				publishFormFlow(flow, permissionIds);
				LOGGER.log(Level.DEBUG, "Published flow for flowId : {} and  action {}", flowRequest.getFlowId(),
						ActionType.CONFIRM_PUBLISH);
				return bffResponse.response(flowRequest.getFlowId(), BffResponseCode.FLOW_SUCCESS_CODE_FLOW_PUBLISH,
						BffResponseCode.FLOW_USER_CODE_FLOW_PUBLISH, StatusCode.OK, null,
						flowRequest.getFlowId().toString());
			}

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, flowRequest.getFlowId(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FLOW_API_MODIFY_FLOW, BffResponseCode.DB_ERR_FLOW_USER_MODIFY_FLOW),
					StatusCode.INTERNALSERVERERROR);

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, flowRequest.getFlowId(), exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_FLOW_API_MODIFY_FLOW_EXCEPTION,
					BffResponseCode.ERR_FLOW_USER_MODIFY_FLOW_EXCEPTION), StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/** Validate and throw error message if user tried to disable default /home flow
	 * @param flowRequest
	 * @param identifier
	 * @param flow
	 * @return
	 */
	private BffCoreResponse validateDisableFlow(FlowRequest flowRequest, DisableType identifier, Flow flow) {
		// Making a flow disabled
		if (flowRequest.isDisabled()) {
			// Check if current flow is the default flow
			AppConfigMaster appConfig = appConfigRepo.findByConfigNameAndConfigType(
					BffAdminConstantsUtils.DEFAULT_FLOW_KEY, AppCfgRequestType.APPLICATION.getType());

			if (null != appConfig && !CollectionUtils.isEmpty(appConfig.getAppConfigDetails())) {
				for (AppConfigDetail appConfigDetail : appConfig.getAppConfigDetails()) {
					if (null != appConfigDetail.getConfigValue()
							&& flow.getUid().toString().equals(appConfigDetail.getConfigValue())) {
						return bffResponse.errResponse(List.of(BffResponseCode.ERR_FLOW_API_CHECK_DEFAULT_FLOW,
								BffResponseCode.ERR_FLOW_USER_CHECK_DEFAULT_FLOW), StatusCode.BADREQUEST);
					}
				}
			}
			// Check if current flow is the home flow
			appConfig = appConfigRepo.findByConfigNameAndConfigType(BffAdminConstantsUtils.HOME_FLOW_KEY,
					AppCfgRequestType.APPLICATION.getType());
			if (null != appConfig && !CollectionUtils.isEmpty(appConfig.getAppConfigDetails())) {
				for (AppConfigDetail appConfigDetail : appConfig.getAppConfigDetails()) {
					if (null != appConfigDetail.getConfigValue()
							&& flow.getUid().toString().equals(appConfigDetail.getConfigValue())) {
						return bffResponse.errResponse(List.of(BffResponseCode.ERR_FLOW_API_CHECK_CURRENT_FLOW_HOME,
								BffResponseCode.ERR_FLOW_USER_CHECK_CURRENT_FLOW_HOME), StatusCode.BADREQUEST);
					}
				}
			}
			
		} 
		flow.setDisabled(flowRequest.isDisabled());
		return null;
	}

	/** Get List of flow permission
	 * @param flow
	 * @return
	 */
	private List<String> getFlowPermissionList(Flow flow) {
		// Set permissions
		List<String> permissionList = new ArrayList<>();
		if (null != flow.getFlowPermission()) {
			for (FlowPermission flowPermission : flow.getFlowPermission()) {
				permissionList.add(flowPermission.getPermission());
			}
		}
		return permissionList;
	}


	/**
	 *Publish a formflow
	 * - Set the published flag
	 * - Prepare the published JSON for all form in the flow and save them
	 * - Update the published dependencies table
	 */
	public void publishFormFlow(Flow flow, List<String> permissionIds) throws IOException {
		FormObjDto formDto = null;
		ObjectMapper objectMapper = null;
		// Set Publish to true on CONFIRM_PUBLISH
		flow.setPublished(ActionType.CONFIRM_PUBLISH.isValue());
		flow.setPublishedDefaultFormId(flow.getDefaultFormId());
		flow.setPublishedFlow(ActionType.CONFIRM_PUBLISH.isValue());
		flow = flowRepository.save(flow);

		if (flow.getForms() != null && !flow.getForms().isEmpty()) {
			for (Form form : flow.getForms()) {
				form.setPublished(ActionType.CONFIRM_PUBLISH.isValue());

				List<FieldObjDto> fieldObjDtoList = null;
				if (form.getFields() != null && !form.getFields().isEmpty()) {
					fieldObjDtoList = formTransformation.convertToFieldDto(form.getFields());
				}
				formDto = new FormObjDto(form, fieldObjDtoList);
				FormData formData = formTransformation.createFormData(formDto);

				// Get the Form context menus
				formData.getFormProperties().setMenus(formServiceImpl.getFormMenus(form.getUid(), permissionIds,true));

				// Generating publishedFormJson for formData
				objectMapper = new ObjectMapper();
				form.setPublishedForm(objectMapper.writeValueAsBytes(formData));
				dependencyUtil.managePublishedWorkFlowDependency(form);
				dependencyUtil.findLinkedPublishedFormIds(form, flow.getPublishedDefaultFormId());
				
				form = formRepo.save(form);
			}
		}
	}

	/**
	 * Retrieves for the flow for given flowId
	 * 
	 * @param flowId
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse getFlowById(UUID flowId) {
		BffCoreResponse bffCoreResponse = null;
		try {
			Optional<Flow> flow = flowRepository.findById(flowId);
			if (!flow.isPresent()) {
				return bffResponse.errResponse(
						List.of(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_BY_ID,
								BffResponseCode.DB_ERR_FLOW_USER_FETCH_FLOW_BY_ID),
						StatusCode.INTERNALSERVERERROR, null, flowId.toString());
			}

			if (null != sessionDetails.getChannel()
					&& sessionDetails.getChannel().contains(ChannelType.MOBILE_RENDERER.getType())) {
				bffCoreResponse = flowValidationsForRenderer(flow.get());
				if (bffCoreResponse != null) {
					return bffCoreResponse;
				}
			}
			// Set permissions
			List<String> permissionList = getFlowPermissionList(flow.get());

			// Setting is tabForm for mobile renderer to know tab details
			FlowDto flowDto = convertToFlowDto(flow.get(), permissionList);

			bffCoreResponse = bffResponse.response(flowDto, BffResponseCode.FLOW_SUCCESS_CODE_FETCH_FLOW_BY_ID,
					BffResponseCode.FLOW_USER_CODE_FETCH_FLOW_BY_ID, StatusCode.OK, null,
					flowDto.getFlowId().toString());

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, flowId, exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_BY_ID,
							BffResponseCode.DB_ERR_FLOW_USER_FETCH_FLOW_BY_ID),
					StatusCode.INTERNALSERVERERROR, null, flowId.toString());

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, flowId, exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FLOW_API_FETCH_FLOW_BY_ID,
							BffResponseCode.ERR_FLOW_USER_FETCH_FLOW_BY_ID),
					StatusCode.BADREQUEST, null, flowId.toString());
		}
		return bffCoreResponse;
	}

	/**Get the flow and its default form for given flow Id
	 * - Validate the user permission 
	 * - Validate flow is disabled , published and other checks required for mobile
	 * 
	 * @param userPermission
	 * @return
	 */
	@Override
	public BffCoreResponse getDefaultFormForFlowId(UserPermissionRequest userPermission) {
		BffCoreResponse bffCoreResponse = null;
		FlowDefaultFormDto flowDefaultFormDto = null;
		try {
			Optional<Flow> optionalFlow = flowRepository.findById(userPermission.getFlowId());
			Flow flow = null;
			if (optionalFlow.isPresent()) {
				flow = optionalFlow.get();
				bffCoreResponse = validateFlow(flow);
				if (bffCoreResponse != null) {
					return bffCoreResponse;
				}

				List<FlowPermission> flowPermissionList = flow.getFlowPermission();
				List<String> userPermissions = userPermission.getUserPermissions();

				if (!flowPermissionList.isEmpty()) {
					boolean isPresent = bffCommonUtil.checkUserHasPermissionForFlow(flowPermissionList,
							userPermissions);
					if (isPresent) {
						Optional<Form> defaultForm = formRepo.findById(flow.getDefaultFormId());

						bffCoreResponse = validateDefFormOfFlow(defaultForm);
						if (bffCoreResponse != null) {
							return bffCoreResponse;
						}
						if (defaultForm.isPresent()) {
							Form defForm = defaultForm.get();
							ObjectMapper objectMapper = new ObjectMapper();
							flowDefaultFormDto = new FlowDefaultFormDto();
							flowDefaultFormDto
									.setFormData(objectMapper.readValue(defForm.getPublishedForm(), FormData.class));
							flowDefaultFormDto.setUid(userPermission.getFlowId());
							flowDefaultFormDto.setName(flow.getName());
							flowDefaultFormDto.setModalForm(defForm.isModalForm());
							flowDefaultFormDto.setTabbedForm(defForm.isTabbedForm());
							flowDefaultFormDto.setVersion(flow.getVersion());
						}
					} else {
						return bffResponse.errResponse(
								List.of(BffResponseCode.ERR_FLOW_API_INVALID_FLOW_PERMISSION,
										BffResponseCode.ERR_USER_FLOW_API_INVALID_FLOW_PERMISSION),
								StatusCode.BADREQUEST);
					}
				} else {
					return bffResponse.errResponse(List.of(BffResponseCode.ERR_FLOW_API_FLOW_PERMISSIONS_EMPTY,
							BffResponseCode.ERR_USER_FLOW_API_FLOW_PERMISSIONS_EMPTY), StatusCode.BADREQUEST);
				}
				bffCoreResponse = bffResponse.response(flowDefaultFormDto,
						BffResponseCode.FLOW_SUCCESS_CODE_FETCH_FLOW_DEF_FORM,
						BffResponseCode.FLOW_USER_CODE_FETCH_FLOW_DEF_FORM, StatusCode.OK);
			} else {
				bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_FLOW_API_FETCH_FLOW_BY_ID,
						BffResponseCode.ERR_FLOW_USER_FETCH_FLOW_BY_ID), StatusCode.BADREQUEST);
			}
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, userPermission.getFlowId(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_DEF_FORM,
							BffResponseCode.DB_ERR_USER_FLOW_API_FETCH_FLOW_DEF_FORM),
					StatusCode.INTERNALSERVERERROR, null, userPermission.getFlowId().toString());

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, userPermission.getFlowId(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FLOW_API_FETCH_FLOW_DEF_FORM,
							BffResponseCode.ERR_USER_FLOW_API_FETCH_FLOW_DEF_FORM),
					StatusCode.BADREQUEST, null, userPermission.getFlowId().toString());
		}
		return bffCoreResponse;

	}

	/**Validation for flow before sending to mobile
	 * - If validation fails then throw an message
	 * @param optionalFlow
	 * @return
	 */
	private BffCoreResponse validateFlow(Flow flow) {
		BffCoreResponse bffCoreResponse = null;
		if (flow.isDisabled()) {
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FLOW_DISABLE_CD, BffResponseCode.ERR_FLOW_DISABLE_CD),
					StatusCode.BADREQUEST);
		}
		else if (!flow.isPublishedFlow()) {
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FLOW_UPUBLISH_CD, BffResponseCode.ERR_FLOW_UPUBLISH_CD),
					StatusCode.BADREQUEST);
		}
		else if (flow.getDefaultFormId() == null) {
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FLOW_NO_DEFAULT_FORM, BffResponseCode.ERR_FLOW_NO_DEFAULT_FORM),
					StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**Validate the default form of the flow before sending to mobile
	 * - If validation fails throw a message
	 * @param defaultFormOfFlow
	 * @return
	 */
	private BffCoreResponse validateDefFormOfFlow(Optional<Form> defaultFormOfFlow) {
		if (!defaultFormOfFlow.isPresent()) {
			return bffResponse.errResponse(
					List.of(BffResponseCode.ERR_NO_FORM_FOUND, BffResponseCode.ERR_NO_FORM_FOUND),
					StatusCode.BADREQUEST);
		}
		if (defaultFormOfFlow.get().isDisabled()) {
			return bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FORM_DISABLE_CD, BffResponseCode.ERR_FORM_DISABLE_CD),
					StatusCode.BADREQUEST);
		}
		if (null == defaultFormOfFlow.get().getPublishedForm()) {
			return bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FORM_UPUBLISH_CD, BffResponseCode.ERR_FORM_UPUBLISH_CD),
					StatusCode.BADREQUEST);
		}

		return null;

	}

	/**Validate the flow and default form before sending to mobile
	 * @param flowId
	 * @param flow
	 * @param defaultForm
	 * @return BffCoreResponse
	 */
	private BffCoreResponse flowValidationsForRenderer(Flow flow) {
		// Validating flow
		if (flow.getDefaultFormId() == null) {
			return bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FORM_DEFAULT_FORM_NOT_FOUND,
							BffResponseCode.ERR_FORM_DEFAULT_FORM_NOT_FOUND),
					StatusCode.BADREQUEST, null, flow.getUid().toString());
		} else {
			Optional<Form> defaultForm = formRepo.findById(flow.getDefaultFormId());
			if (!flow.isPublishedFlow()) {
				return bffResponse.errResponse(
						List.of(BffResponseCode.ERR_FLOW_UPUBLISH_CD, BffResponseCode.ERR_FLOW_UPUBLISH_CD),
						StatusCode.BADREQUEST, null, flow.getUid().toString());
			}
			if (flow.isDisabled()) {
				return bffResponse.errResponse(
						List.of(BffResponseCode.ERR_FLOW_DISABLE_CD, BffResponseCode.ERR_FLOW_DISABLE_CD),
						StatusCode.BADREQUEST, null, flow.getUid().toString());
			}

			// validating form

			if (!defaultForm.isPresent()) {
				return bffResponse.errResponse(
						List.of(BffResponseCode.ERR_FORM_NOT_FOUND, BffResponseCode.ERR_FORM_NOT_FOUND),
						StatusCode.BADREQUEST, null, flow.getDefaultFormId().toString());
			} else {
				if (defaultForm.get().isDisabled()) {
					return bffResponse.errResponse(
							List.of(BffResponseCode.ERR_FLOW_DEFAULT_FORM_DISABLED,
									BffResponseCode.ERR_FLOW_DEFAULT_FORM_DISABLED),
							StatusCode.BADREQUEST, null, flow.getDefaultFormId().toString());
				}
				if (defaultForm.get().getPublishedForm() == null) {
					return bffResponse.errResponse(
							List.of(BffResponseCode.ERR_FLOW_DEFAULT_FORM_NOT_PUBLISHED,
									BffResponseCode.ERR_FLOW_DEFAULT_FORM_NOT_PUBLISHED),
							StatusCode.BADREQUEST, null, flow.getDefaultFormId().toString());
				}
			}
		}
		return null;
	}

	/**
	 * Fetch the flwo count , unpublished form count , API count and custom control count
	 *
	 */
	@Override
	public BffCoreResponse fetchCount() {
		Map<String, Integer> countMap = new HashMap<>();

		BffCoreResponse bffCoreResponse = null;
		try {
			List<ProductConfig> productConfigIdList = productPrepareService.getLayeredProductConfigList();
			int flowCount = getUnPublishedFlowCount(productConfigIdList, BffAdminConstantsUtils.FlowType.UNPUBLISHED);
			int formCountUnPublish = getUnPublishedFormCount(productConfigIdList,
					BffAdminConstantsUtils.FlowType.UNPUBLISHED);
			int formCountOrphan = getOrphanFormCount(productConfigIdList, BffAdminConstantsUtils.FlowType.UNPUBLISHED);
			int apiCount = apiMasterRepository.countAllApis();
			int customComponentCount = customComponentMasterRepository.customComponentCount();
			int registryCount = apiRegistryRepository.countAllRegistry();

			countMap.put(BffAdminConstantsUtils.UNPUBLISHED_FLOWS, flowCount);
			countMap.put(BffAdminConstantsUtils.API_COUNT, apiCount);
			countMap.put(BffAdminConstantsUtils.CUSTOM_COMPONENT_COUNT, customComponentCount);
			countMap.put(BffAdminConstantsUtils.UNPUBLISHED_FORMS, formCountUnPublish);
			countMap.put(BffAdminConstantsUtils.ORPHAN_FORMS, formCountOrphan);
			countMap.put(BffAdminConstantsUtils.API_REGISTRY_COUNT, registryCount);

			bffCoreResponse = bffResponse.response(countMap, BffResponseCode.FLOW_SUCCESS_CODE_FLOW_COUNT,
					BffResponseCode.FLOW_USER_CODE_FLOW_COUNT, StatusCode.OK);
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FLOW_API_FLOW_COUNT, BffResponseCode.DB_ERR_FLOW_USER_FLOW_COUNT),
					StatusCode.INTERNALSERVERERROR);

		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FLOW_API_FLOW_COUNT, BffResponseCode.ERR_FLOW_USER_FLOW_COUNT),
					StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**
	 * Retrieve all the flows as per user layer eligibility
	 * 
	 * @param identifier
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchFlows(FlowType identifier) {
		BffCoreResponse bffCoreResponse = null;
		List<Flow> fetchedFlows = null;
		List<ProductConfig> prodConfigIdList = null;
		LOGGER.log(Level.DEBUG, FETCHING_THE_FLOWS_PID_MSG, identifier);
		try {
			prodConfigIdList = productPrepareService.getLayeredProductConfigList();
			fetchedFlows = new ArrayList<>();
			if (!prodConfigIdList.isEmpty()) {
				// Get the flows of warehouse supplied and default warehouse
				if (FlowType.UNPUBLISHED.equals(identifier)) {
					fetchedFlows.addAll(flowRepository
							.findByIsPublishedFalseAndProductConfigInOrderByLastModifiedDateDesc(prodConfigIdList));
				} else if (FlowType.PUBLISHED.equals(identifier)) {
					fetchedFlows.addAll(flowRepository
							.findByIsPublishedTrueAndProductConfigInOrderByLastModifiedDateDesc(prodConfigIdList));
				} else {
					fetchedFlows
							.addAll(flowRepository.findByProductConfigInOrderByLastModifiedDateDesc(prodConfigIdList));

				}
				LOGGER.log(Level.DEBUG, "Total No of flows returned is: {}.", fetchedFlows.size());
			}

			List<FlowDto> flowDtoList = new ArrayList<>();
			for (Flow flow : fetchedFlows) {
				// Setting is tabForm for mobile renderer to know tab details
				flowDtoList.add(convertToFlowDto(flow, null));
			}

			bffCoreResponse = bffResponse.response(flowDtoList, BffResponseCode.FLOW_SUCCESS_CODE_FETCH_ALL_FLOWS,
					BffResponseCode.FLOW_USER_CODE_FETCH_ALL_FLOWS, StatusCode.OK);

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, prodConfigIdList, exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.DB_ERR_FLOW_API_FETCH_ALL_FLOWS,
					BffResponseCode.DB_ERR_FLOW_USER_FETCH_ALL_FLOWS), StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, prodConfigIdList, exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_FLOW_API_FETCH_ALL_FLOWS,
					BffResponseCode.ERR_FLOW_USER_FETCH_ALL_FLOWS), StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**Get the count for unpublished flow
	 * @param productConfigIdList
	 * @param identifier
	 * @return int
	 */
	public int getUnPublishedFlowCount(List<ProductConfig> productConfigIdList, FlowType identifier) {
		int count = 0;

		if (!productConfigIdList.isEmpty() && (FlowType.UNPUBLISHED.equals(identifier))) {
			// Get the flows of warehouse supplied and default warehosue
			count = flowRepository.countByIsPublishedFalseAndProductConfigIn(productConfigIdList);
		}

		return count;
	}

	/**Get the count for unpublished form
	 * @param productConfigIdList
	 * @param productConfigIdList
	 * @param identifier
	 * @return
	 */
	public int getUnPublishedFormCount(List<ProductConfig> productConfigIdList, FlowType identifier) {
		int count = 0;
		List<UUID> prodConfigIdList = productConfigIdList.stream().distinct().flatMap(item -> Stream.of(item.getUid()))
				.collect(Collectors.toList());

		if (!prodConfigIdList.isEmpty() && (BffAdminConstantsUtils.FlowType.UNPUBLISHED.equals(identifier))) {
			// Get the flows of warehouse supplied and default warehosue
			count = formRepo.countByIsPublishedFalseAndProductConfigIdIn(prodConfigIdList);
		}
		return count;
	}

	/**Get the count for orphan forms
	 * @param productConfigIdList
	 * @param identifier
	 * @return
	 */
	public int getOrphanFormCount(List<ProductConfig> productConfigIdList, FlowType identifier) {
		int count = 0;
		LOGGER.log(Level.DEBUG, FETCHING_THE_FLOWS_PID_MSG, productConfigIdList);
		List<UUID> prodConfigIdList = productConfigIdList.stream().distinct().flatMap(item -> Stream.of(item.getUid()))
				.collect(Collectors.toList());
		if ((!prodConfigIdList.isEmpty()) && (BffAdminConstantsUtils.FlowType.UNPUBLISHED.equals(identifier))) {
			// Get the flows of warehouse supplied and default warehosue
			count = formRepo.countOrphanForms(prodConfigIdList);
		}
		return count;
	}

	/**
	 * Validate whether flow name is unique or not
	 * 
	 * @param flowName
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse uniqueFlow(String flowName, long version) {
		BffCoreResponse bffCoreResponse = null;
		String detailMessage = flowName;
		try {
			// Find the flow by name
			List<Flow> flowList = flowRepository.findByNameAndVersion(flowName, version);
			if (flowList.isEmpty()) {
				bffCoreResponse = bffResponse.response(detailMessage, BffResponseCode.FLOW_SUCCESS_CODE_UNIQUE_FLOW,
						BffResponseCode.FLOW_USER_CODE_UNIQUE_FLOW, StatusCode.OK, flowName,
						flowName);
				return bffCoreResponse;
			} else {
				bffCoreResponse = bffResponse.errResponse(
						List.of(BffResponseCode.ERR_FLOW_API_CREATE_FLOW, BffResponseCode.ERR_FLOW_USER_CREATE_FLOW),
						StatusCode.BADREQUEST, null, flowName);
				LOGGER.log(Level.DEBUG, "Not a unique flow {}.", flowName);

			}
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, flowName, exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FLOW_API_UNIQUE_FLOW, BffResponseCode.DB_ERR_FLOW_USER_UNIQUE_FLOW),
					StatusCode.INTERNALSERVERERROR, null, flowName);
		}
		return bffCoreResponse;
	}

	/**
	 * Delete a flow by its flowId
	 * - Validate deleting flow id is default or home flow and throw message
	 * 
	 * @param flowId
	 * @return BffCoreResponse
	 */

	@Override
	@Transactional
	public BffCoreResponse deleteFlowById(UUID flowId, DeleteType identifier) {
		BffCoreResponse bffCoreResponse = null;
		try {
			Optional<Flow> flow = flowRepository.findById(flowId);
			if (!flow.isPresent()) {
				return bffResponse.errResponse(
						List.of(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_BY_ID,
								BffResponseCode.DB_ERR_FLOW_USER_FETCH_FLOW_BY_ID),
						StatusCode.INTERNALSERVERERROR, null, flowId.toString());
			}

			// Check if current flow is the default flow
			AppConfigMaster appConfig = appConfigRepo.findByConfigNameAndConfigType(
					BffAdminConstantsUtils.DEFAULT_FLOW_KEY, AppCfgRequestType.APPLICATION.getType());
			if (appConfig != null && !CollectionUtils.isEmpty(appConfig.getAppConfigDetails())) {
				for (AppConfigDetail appConfigDetail : appConfig.getAppConfigDetails()) {
					if (!StringUtils.isEmpty(appConfigDetail.getConfigValue())
							&& flowId.equals(UUID.fromString(appConfigDetail.getConfigValue()))) {
						bffCoreResponse = bffResponse
								.errResponse(
										List.of(BffResponseCode.ERR_FLOW_API_DELETE_FLOW_UNSUCCESS,
												BffResponseCode.ERR_FLOW_USER_DELETE_DEFAULT_FLOW),
										StatusCode.BADREQUEST);
						return bffCoreResponse;
					}
				}
			}

			// Check if current flow is the home flow
			appConfig = appConfigRepo.findByConfigNameAndConfigType(BffAdminConstantsUtils.HOME_FLOW_KEY,
					AppCfgRequestType.APPLICATION.getType());
			if (appConfig != null && !CollectionUtils.isEmpty(appConfig.getAppConfigDetails())) {
				for (AppConfigDetail appConfigDetail : appConfig.getAppConfigDetails()) {
					if (!StringUtils.isEmpty(appConfigDetail.getConfigValue())
							&& flowId.equals(UUID.fromString(appConfigDetail.getConfigValue()))) {
						bffCoreResponse = bffResponse
								.errResponse(List.of(BffResponseCode.ERR_FLOW_API_DELETE_FLOW_UNSUCCESS,
										BffResponseCode.ERR_FLOW_API_DELETE_HOME_FLOW), StatusCode.BADREQUEST);
						return bffCoreResponse;
					}
				}
			}
			if (identifier != null && identifier.equals(DeleteType.CHECK_DELETE)) {

				List<BffResponseCode> errMessage = new ArrayList<>();
				errMessage.add(BffResponseCode.ERR_FLOW_API_DELETE_FLOW_UNSUCCESS);
				errMessage.add(BffResponseCode.ERR_FLOW_USER_DELETE_FORM_FLOW);
				bffCoreResponse = bffResponse.errResponse(errMessage, StatusCode.BADREQUEST);

				return bffCoreResponse;
			}
			flowRepository.deleteById(flowId);
			bffCoreResponse = bffResponse.response(flowId, BffResponseCode.FLOW_SUCCESS_CODE_DELETE_FLOW,
					BffResponseCode.FLOW_USER_CODE_DELETE_FLOW, StatusCode.OK, null,
					flowId.toString());

			LOGGER.log(Level.DEBUG, "Deleted flow for id {}.", flowId);
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, flowId, exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FLOW_API_DELETE_FLOW, BffResponseCode.DB_ERR_FLOW_USER_DELETE_FLOW),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, flowId, exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_FLOW_API_DELETE_FLOW, BffResponseCode.ERR_FLOW_USER_DELETE_FLOW),
					StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**
	 * Disable a flow based in flow ID
	 * - Validate flow is going to be disabled is home or default flow and throws error
	 * 
	 * @param flowId
	 * @param identifier
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse disableFlow(UUID flowId, DisableType identifier) {
		BffCoreResponse bffCoreResponse = null;
		try {
			Optional<Flow> flow = flowRepository.findById(flowId);
			if (!flow.isPresent()) {
				return bffResponse.errResponse(
						List.of(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_BY_ID,
								BffResponseCode.DB_ERR_FLOW_USER_FETCH_FLOW_BY_ID),
						StatusCode.INTERNALSERVERERROR, null, flowId.toString());
			}

			// Check if current flow is the default flow
			AppConfigMaster appConfig = appConfigRepo.findByConfigNameAndConfigType(
					BffAdminConstantsUtils.DEFAULT_FLOW_KEY, AppCfgRequestType.APPLICATION.getType());
			if (null != appConfig && !CollectionUtils.isEmpty(appConfig.getAppConfigDetails())) {
				for (AppConfigDetail appConfigDetail : appConfig.getAppConfigDetails()) {
					if (null != appConfigDetail.getConfigValue()
							&& flow.get().getUid().toString().equals(appConfigDetail.getConfigValue())) {
						bffCoreResponse = bffResponse
								.errResponse(
										List.of(BffResponseCode.ERR_FLOW_API_CHECK_DEFAULT_FLOW,
												BffResponseCode.ERR_FLOW_USER_CHECK_DEFAULT_FLOW),
										StatusCode.BADREQUEST);
						return bffCoreResponse;
					}
				}
			}

			// Check if current flow is the home flow
			appConfig = appConfigRepo.findByConfigNameAndConfigType(BffAdminConstantsUtils.HOME_FLOW_KEY,
					AppCfgRequestType.APPLICATION.getType());
			if (null != appConfig && !CollectionUtils.isEmpty(appConfig.getAppConfigDetails())) {
				for (AppConfigDetail appConfigDetail : appConfig.getAppConfigDetails()) {
					if (null != appConfigDetail.getConfigValue()
							&& flow.get().getUid().toString().equals(appConfigDetail.getConfigValue())) {
						bffCoreResponse = bffResponse
								.errResponse(
										List.of(BffResponseCode.ERR_FLOW_API_CHECK_CURRENT_FLOW_HOME,
												BffResponseCode.ERR_FLOW_USER_CHECK_CURRENT_FLOW_HOME),
										StatusCode.BADREQUEST);
						return bffCoreResponse;
					}
				}
			}

			List<Form> formList = flow.get().getForms();

			if (identifier.equals(DisableType.CHECK_DISABLE) && !formList.isEmpty()) {
				bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_FLOW_API_CHECK_DISABLE_FLOW,
						BffResponseCode.ERR_FLOW_USER_CHECK_DISABLE_FLOW), StatusCode.BADREQUEST);
			} else {
				flow.get().setDisabled(true);
				bffCoreResponse = bffResponse.response(flowId, BffResponseCode.FLOW_SUCCESS_CODE_DISABLE_FLOW,
						BffResponseCode.FLOW_USER_CODE_DISABLE_FLOW, StatusCode.OK, null,
						flowId.toString());
			}

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, flowId, exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.DB_ERR_FLOW_API_DISABLE_FLOW,
					BffResponseCode.DB_ERR_FLOW_USER_DISABLE_FLOW), StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, flowId, exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_FLOW_API_DISABLE_FLOW_EXCEPTION,
					BffResponseCode.ERR_FLOW_USER_DISABLE_FLOW_EXCEPTION), StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**
	 *Publish a flow based on its ID
	 *
	 */
	@Override
	public BffCoreResponse publishFlow(UUID flowId, ActionType actionType, List<String> permissionIds) {
		BffCoreResponse bffCoreResponse = null;
		try {
			Optional<Flow> flow = flowRepository.findById(flowId);
			if (!flow.isPresent()) {
				return bffResponse.errResponse(
						List.of(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_BY_ID,
								BffResponseCode.DB_ERR_FLOW_USER_FETCH_FLOW_BY_ID),
						StatusCode.INTERNALSERVERERROR, null, flowId.toString());
			}
			if (actionType.equals(ActionType.CHECK_PUBLISH) && (flow.get().getForms() != null)) {
				return bffResponse.response(BffAdminConstantsUtils.EMPTY_SPACES,
						BffResponseCode.FLOW_SUCCESS_CODE_MODIFY_FLOW_CHECK_PUBLISH,
						BffResponseCode.FLOW_USER_CODE_MODIFY_FLOW_CHECK_PUBLISH, StatusCode.OK, null,
						flowId.toString());

			}
			// For Action = CONFIRM_PUBLISH
			publishFormFlow(flow.get(), permissionIds);

			FlowDto flowDto = new FlowDto.FlowBuilder(flow.get().getName()).setFlowId(flow.get().getUid())
					.setPublished(flow.get().isPublished()).setVersion(flow.get().getVersion()).build();
			bffCoreResponse = bffResponse.response(flowDto, BffResponseCode.FLOW_SUCCESS_CODE_CONFIRM_PUBLISH,
					BffResponseCode.FLOW_USER_CODE_CONFIRM_PUBLISH, StatusCode.OK, null,
					flowId.toString());
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, flowId, exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.DB_ERR_FLOW_API_PUBLISH_FLOW,
					BffResponseCode.DB_ERR_FLOW_USER_PUBLISH_FLOW), StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, flowId, exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_FLOW_API_PUBLISH_FLOW_EXCEPTION,
					BffResponseCode.ERR_FLOW_USER_PUBLISH_FLOW_EXCEPTION), StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	
	/**Convert Flow to FlowDto
	 * @param flow
	 * @param permissionList
	 * @param productConfig
	 * @return FlowDto
	 */
	private FlowDto convertToFlowDto(Flow flow, List<String> permissionList) {
		Layer layer = null;
		if (flow.getProductConfig() != null && flow.getProductConfig().getRoleMaster() != null) {
			layer = new Layer();
			layer.setLevel(flow.getProductConfig().getRoleMaster().getLevel());
			layer.setName(flow.getProductConfig().getRoleMaster().getName());
		}

		return new FlowDto.FlowBuilder(flow.getName()).setFlowId(flow.getUid()).setDescription(flow.getDescription())
				.setDefaultFormId(flow.getDefaultFormId()).setDisabled(flow.isDisabled())
				.setExtDisabled(flow.isExtDisabled()).setPublished(flow.isPublished()).setTag(flow.getTag())
				.setLayer(layer).setVersion(flow.getVersion()).setPermissions(permissionList)
				.setExtendedFromFlowId(flow.getExtendedFromFlowId())
				.setExtendedFromFlowName(flow.getExtendedFromFlowName()).setTabbedForm(flow.isDefaultFormTabbed())
				.setModalForm(flow.isDefaultModalForm()).setExtendedFromFlowVersion(flow.getExtendedFromFlowVersion()).build();
	}

	/**Convert FlowRequest to Flow
	 * @param flowRequest
	 * @return Flow
	 */
	private Flow convertToFlowEntity(FlowRequest flowRequest) {
		Flow flowEntity = new Flow();
		flowEntity.setDefaultFormId(flowRequest.getDefaultFormId());
		flowEntity.setDescription(flowRequest.getDescription());
		flowEntity.setDisabled(flowRequest.isDisabled());
		flowEntity.setExtDisabled(flowRequest.isExtDisabled());
		flowEntity.setPublished(false);
		flowEntity.setName(flowRequest.getName());
		flowEntity.setTag(flowRequest.getTag());
		flowEntity.setVersion(BffAdminConstantsUtils.FLOW_INITIAL_VERSION);
		flowEntity.setProductConfig(productPrepareService.getCurrentLayerProdConfigId());

		List<FlowPermission> permissionList = new ArrayList<>();
		// Set the permissions
		if (flowRequest.getPermissions() != null) {
			for (String permission : flowRequest.getPermissions()) {
				FlowPermission flowPermission = new FlowPermission();
				flowPermission.setPermission(permission);
				flowPermission.setFlow(flowEntity);
				permissionList.add(flowPermission);
			}
			flowEntity.setFlowPermission(permissionList);
		}
		return flowEntity;

	}

	/**
	 *Update the FLOW table
	 * 
	 * @param flow
	 * @param flowRequest
	 * @return Flow
	 */
	private Flow updateExistingFlowWithPermission(Flow flow, FlowRequest flowRequest) {
		if (Optional.ofNullable(flowRequest).isPresent() && Optional.ofNullable(flow).isPresent()) {
			flow.setName(flowRequest.getName());
			flow.setDescription(flowRequest.getDescription());

			flow.setExtDisabled(flowRequest.isExtDisabled());
			flow.setTag(flowRequest.getTag());
			flow.setPublished(false);
			List<FlowPermission> deletedFlowPermissions = new ArrayList<>();

			if (!CollectionUtils.isEmpty(flow.getFlowPermission())) {
				for (FlowPermission flowPermission : flow.getFlowPermission()) {
					if (CollectionUtils.isEmpty(flowRequest.getPermissions())
							|| !flowRequest.getPermissions().contains(flowPermission.getPermission())) {
						deletedFlowPermissions.add(flowPermission);
					}
				}
			}
			for (FlowPermission deletedFlowPermission : deletedFlowPermissions) {
				flow.removePermission(deletedFlowPermission);
				flowPermissionRepository.delete(deletedFlowPermission);
			}
			boolean newPermission = true;
			if (!CollectionUtils.isEmpty(flowRequest.getPermissions())) {
				for (String updatedPermission : flowRequest.getPermissions()) {
					for (FlowPermission existingFlowPermission : flow.getFlowPermission()) {
						if (updatedPermission.equals(existingFlowPermission.getPermission())) {
							newPermission = false;
							break;
						}
					}
					if (newPermission) {
						FlowPermission flowPermission = new FlowPermission();
						flowPermission.setPermission(updatedPermission);
						flow.addPermission(flowPermission);
					}
					newPermission = true;
				}
			}
		}
		return flow;
	}

	/**
	 * Returns Basic FlowDetails(flowId, name, version, modal , tabbed)
	 * - To populate in dropdown in ADMIN UI (NAVIGATE_TO_FORMFLOW)
	 * 
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse fetchFlowBasicList() {
		BffCoreResponse bffCoreResponse = null;
		List<FlowLiteDto> flowDetailsList = null;
		try {
			List<ProductConfig> prodConfigList = productPrepareService.getLayeredProductConfigList();
			flowDetailsList = flowRepository.getFlowBasicList(prodConfigList);
			bffCoreResponse = bffResponse.response(flowDetailsList,
					BffResponseCode.FLOW_SUCCESS_CODE_FLOW_BASIC_LIST_FETCH,
					BffResponseCode.FLOW_USER_CODE_FLOW_BASIC_LIST_FETCH, StatusCode.OK);

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FLOW_API_FLOW_BASIC_LIST_FETCH,
							BffResponseCode.DB_ERR_FLOW_USER_API_FLOW_BASIC_LIST_FETCH),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_FLOW_API_FLOW_BASIC_LIST_FETCH,
					BffResponseCode.ERR_FLOW_USER_API_FLOW_BASIC_LIST_FETCH), StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**
	 * Method helps to clone a flow with all forms or form in same/different flow  and versioning a flow
	 * 
	 * 
	 * @param cloneRequest
	 * @param actionType
	 * @param identifier
	 * @return BffCoreResponse
	 */
	public BffCoreResponse cloneComponent(CloneRequest cloneRequest, CloneType actionType, String identifier) {
		BffCoreResponse bffCoreResponse = null;
		boolean extendedFlag = false;
		try {
			long version = BffAdminConstantsUtils.FLOW_INITIAL_VERSION;
			Map<UUID, UUID> formAndClonedFormIds = new HashMap<>();
			if (identifier != null && identifier.equals(BffAdminConstantsUtils.VERSIONING)) {
				version = flowRepository.getFlowLatestVersion(cloneRequest.getName());
				version = version + 1;

			}
			ProductConfig productConfigCurrent = productPrepareService.getCurrentLayerProdConfigId();
			if (actionType.equals(CloneType.FLOW)) {
				List<Flow> flowList = flowRepository.findByNameAndVersion(cloneRequest.getName(), version);
				// If flow name already present , send conflict code
				if (!flowList.isEmpty()) {
					bffCoreResponse = checkFlowNameUnique(cloneRequest, identifier);
				} else {
					Optional<Flow> flow = flowRepository.findById(cloneRequest.getId());
					if (!flow.isPresent()) {
						return bffResponse.errResponse(
								List.of(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_BY_ID,
										BffResponseCode.DB_ERR_FLOW_USER_FETCH_FLOW_BY_ID),
								StatusCode.INTERNALSERVERERROR, null,
								cloneRequest.getId().toString());
					}
					
					if (!flow.get().getProductConfig().getRoleMaster().equals(productConfigCurrent.getRoleMaster())) {
						// flow to be cloned is not created in same layer as of the user who is trying
						// to clone it
						extendedFlag = true;
					}
					Flow clonedFlow = new Flow(flow.get(), extendedFlag, cloneRequest.getName(), productConfigCurrent);
					// Setting version
					clonedFlow.setVersion(version);

					clonedFlow = flowRepository.save(clonedFlow);
					clonedFlow = setDefaultFormForClonedFlow(clonedFlow);

					updateClonedEventTabAndManageFormDependency(flow.get().getForms(), formAndClonedFormIds,
							clonedFlow);

					extendedFlowBaseRepo.save(new ExtendedFlowBase(flow.get()));

					LOGGER.log(Level.DEBUG, "Flow is cloned successfully to id {}", clonedFlow.getUid());

					FlowDto clonedFlowDto = new FlowDto.FlowBuilder(clonedFlow.getName()).setFlowId(clonedFlow.getUid())
							.setDescription(clonedFlow.getDescription()).setDefaultFormId(clonedFlow.getDefaultFormId())
							.setDisabled(clonedFlow.isDisabled()).setExtDisabled(clonedFlow.isExtDisabled())
							.setPublished(clonedFlow.isPublished()).setTag(clonedFlow.getTag())
							.setVersion(clonedFlow.getVersion())
							.setExtendedFromFlowId(clonedFlow.getExtendedFromFlowId())
							.setExtendedFromFlowName(clonedFlow.getExtendedFromFlowName())
							.setExtendedFromFlowVersion(clonedFlow.getExtendedFromFlowVersion()).build();

					bffCoreResponse = getCloneSuccessMsg(identifier, flow.get(), clonedFlowDto, extendedFlag);
				}
			} else if (actionType.equals(CloneType.FORM_IN_SAME_FLOW)) {
				bffCoreResponse = copyFormForParentFlow(cloneRequest);
			} else {
				bffCoreResponse = copyFormForDiffFlow(cloneRequest);
			}
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, cloneRequest.getName(), exp);
			if (identifier != null && identifier.equals(BffAdminConstantsUtils.VERSIONING)) {
				bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.DB_ERR_FLOW_API_CREATE_FLOW_VERSIONED,
						BffResponseCode.DB_ERR_FLOW_API_CREATE_FLOW_VERSIONED), StatusCode.INTERNALSERVERERROR);
			} else if(extendedFlag){
				bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.DB_ERR_FLOW_API_CREATE_FLOW_EXTENDED,
						BffResponseCode.DB_ERR_FLOW_USER_CREATE_FLOW_EXTENDED), StatusCode.INTERNALSERVERERROR);
			} else {
				bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.DB_ERR_FLOW_API_CREATE_FLOW_CLONED,
						BffResponseCode.DB_ERR_FLOW_USER_CREATE_FLOW_CLONED), StatusCode.INTERNALSERVERERROR);
			}
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, cloneRequest.getName(), exp);
			if (identifier != null && identifier.equals(BffAdminConstantsUtils.VERSIONING)) {
				bffCoreResponse = bffResponse
						.errResponse(
								List.of(BffResponseCode.ERR_FLOW_API_CREATE_FLOW_EXCEPTION_VERSIONED,
										BffResponseCode.ERR_FLOW_API_CREATE_FLOW_EXCEPTION_VERSIONED),
								StatusCode.BADREQUEST);
			} else if(extendedFlag){
				bffCoreResponse = bffResponse
						.errResponse(
								List.of(BffResponseCode.ERR_FLOW_API_CREATE_FLOW_EXCEPTION_EXTENDED,
										BffResponseCode.ERR_FLOW_USER_CREATE_FLOW_EXCEPTION_EXTENDED),
								StatusCode.BADREQUEST);
			}else {
				bffCoreResponse = bffResponse
						.errResponse(
								List.of(BffResponseCode.ERR_FLOW_API_CREATE_FLOW_EXCEPTION_CLONED,
										BffResponseCode.ERR_FLOW_USER_CREATE_FLOW_EXCEPTION_CLONED),
								StatusCode.BADREQUEST);
			}
		}
		return bffCoreResponse;
	}

	/**Update the linked form Id as per cloning in the Tab form which is cloned
	 * 
	 * @param formList
	 * @param form
	 */
	private void updateTabLinkedFormId(List<Form> formList, Form form) {
		if (form.isTabbedForm()) {
			// Prepare the Map Key = "FormName" and value = "FormId" of clonedForms
			Map<String, UUID> formIdMap = getFormIdByName(formList);

			for (Tabs tab : form.getTabs()) {
				Optional<Tabs> rtrvTabs = tabRepo.findById(tab.getUid());
				if (rtrvTabs.isPresent()) {
					rtrvTabs.get().setLinkedFormId(formIdMap.get(tab.getLinkedFormName()));
					tabRepo.save(rtrvTabs.get());
				}
			}
		}
	}

	/**Prepare a map with name as key and UUID as value
	 * 
	 * @param formList
	 * @return
	 */
	private Map<String, UUID> getFormIdByName(List<Form> formList) {
		Map<String, UUID> formIdMap = new HashMap<>();
		for (Form form : formList) {
			formIdMap.put(form.getName(), form.getUid());
		}

		return formIdMap;
	}

	/**Check for flow name is unique for cloning and update the version number for versioning
	 * 
	 * @param cloneRequest
	 * @param identifier
	 * @return
	 */
	private BffCoreResponse checkFlowNameUnique(CloneRequest cloneRequest, String identifier) {
		BffCoreResponse bffCoreResponse;
		if (identifier != null && identifier.equals(BffAdminConstantsUtils.VERSIONING)) {
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_VERSION_API_FLOW_NAME_UNIQUE_CHECK,
							BffResponseCode.ERR_VERSION_API_FLOW_NAME_UNIQUE_CHECK),
					StatusCode.CONFLICT, null, cloneRequest.getName());
		} else {
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_CLONE_API_FLOW_NAME_UNIQUE_CHECK,
							BffResponseCode.ERR_CLONE_USER_API_FLOW_NAME_UNIQUE_CHECK),
					StatusCode.CONFLICT, null, cloneRequest.getName());
		}
		return bffCoreResponse;
	}

	/**Prepare the message to be returned after cloning or versioning
	 * 
	 * @param identifier
	 * @param flow
	 * @param clonedFlowDto
	 * @param extendedFlag
	 * @return
	 */
	private BffCoreResponse getCloneSuccessMsg(String identifier, Flow flow, FlowDto clonedFlowDto, boolean extendedFlag) {
		BffCoreResponse bffCoreResponse;
		if (identifier != null && identifier.equals(BffAdminConstantsUtils.VERSIONING)) {
			bffCoreResponse = bffResponse.response(clonedFlowDto, BffResponseCode.FLOW_SUCCESS_CODE_VERSION_COMPONENT,
					BffResponseCode.FLOW_SUCCESS_CODE_VERSION_COMPONENT, StatusCode.CREATED, null,
					flow.getUid().toString());
		} else if(extendedFlag){
			bffCoreResponse = bffResponse.response(clonedFlowDto, BffResponseCode.FLOW_SUCCESS_CODE_EXTENDED_COMPONENT,
					BffResponseCode.FLOW_USER_CODE_EXTENDED_COMPONENT, StatusCode.CREATED, null,
					flow.getUid().toString());
		}else {
			bffCoreResponse = bffResponse.response(clonedFlowDto, BffResponseCode.FLOW_SUCCESS_CODE_CLONE_COMPONENT,
					BffResponseCode.FLOW_USER_CODE_CLONE_COMPONENT, StatusCode.CREATED, null,
					flow.getUid().toString());
		}
		return bffCoreResponse;
	}

	/**Update the cloned form id while cloning/versioning  in Events JSON and Form Dependency
	 * @param formList
	 * @param formAndClonedFormIds
	 * @param clonedFlow
	 */
	private void updateClonedEventTabAndManageFormDependency(List<Form> formList, Map<UUID, UUID> formAndClonedFormIds,
			Flow clonedFlow) {

		Map<UUID, String> formDetailsMap = new HashMap<>();
		Map<String, List<MenuListRequest>> menusMap = new HashMap<>();

		if (!CollectionUtils.isEmpty(formList)) {
			for (Form form : formList) {
				formDetailsMap.put(form.getUid(), form.getName());

				// Prepare - Form context Menus
				getFormContextMenus(form, menusMap);

			}
		}

		// add cloned form id and form id into map formAndClonedFormIds
		if (!org.springframework.util.CollectionUtils.isEmpty(clonedFlow.getForms())) {
			CopyOnWriteArrayList<Form> clonedForms = new CopyOnWriteArrayList<>();
			clonedFlow.getForms().forEach(clonedForm -> {

				// If tabbed form , then get the tab and replace the linkedFormId to cloned form
				updateTabLinkedFormId(clonedFlow.getForms(), clonedForm);
				clonedForms.add(clonedForm);

				// Clone Form context Menus
				formServiceImpl.createFormMenu(menusMap.get(clonedForm.getName()), clonedForm);

				formDetailsMap.forEach((formId, formName) -> {
					if (formName.equalsIgnoreCase(clonedForm.getName())) {
						formAndClonedFormIds.put(clonedForm.getUid(), formId);
					}
				});
			});
			// update cloned form event action
			for (Form clonedForm : clonedForms) {
				updateAction(formAndClonedFormIds, clonedForm.getEvents());
				if (!CollectionUtils.isEmpty(clonedForm.getFields())) {
					List<Field> fileds = formTransformation.getListFields(clonedForm.getFields());
					fileds.forEach(field -> updateAction(formAndClonedFormIds, field.getEvents()));
				}
				clonedForm = formRepo.save(clonedForm);
				dependencyUtil.manageWorkFlowDependency(clonedForm);
				dependencyUtil.findlinkedForms(clonedForm, clonedForm.getFlow().getDefaultFormId());
			}
		}
	}

	/**Get the form context menu for given form id
	 * @param form
	 * @param menusMap
	 */
	private void getFormContextMenus(Form form, Map<String, List<MenuListRequest>> menusMap) {
		List<MenuListRequest> menuList = formServiceImpl.getFormMenus(form.getUid(), null,false);
		menusMap.put(form.getName(), menuList);
	}

	/**
	 * Method used to replace form id by cloned form id
	 * 
	 * @param formAndClonedFormIds
	 * @param events
	 */
	private void updateAction(Map<UUID, UUID> formAndClonedFormIds, List<Events> events) {
		if (!CollectionUtils.isEmpty(events)) {
			for (Events clonedEvent : events) {
				if (!StringUtils.isEmpty(clonedEvent.getAction())) {
					formAndClonedFormIds.forEach((clonedFormId, oldFormId) -> {
						if (clonedEvent.getAction().contains(oldFormId.toString())) {
							// Replacing form id by cloned form id
							String updatedAction = clonedEvent.getAction().replaceAll(oldFormId.toString(),
									clonedFormId.toString());
							clonedEvent.setAction(updatedAction);
						}
					});
				}
			}
		}
	}

	/**Copies a form from given form ID
	 * 
	 * @param cloneRequest
	 * @return BffCoreResponse
	 */
	private BffCoreResponse copyFormForParentFlow(CloneRequest cloneRequest) {
		BffCoreResponse bffCoreResponse;
		Optional<Form> form = formRepo.findById(cloneRequest.getId());
		if (!form.isPresent()) {
			return bffResponse.errResponse(
					List.of(BffResponseCode.ERR_NO_FORM_FOUND, BffResponseCode.ERR_NO_FORM_FOUND),
					StatusCode.BADREQUEST);
		}
		UUID productConfigId = productPrepareService.getCurrentLayerProdConfigId().getUid();
		// Form name unique check.
		List<Form> formList = formRepo.findByNameAndFlow(cloneRequest.getName(), form.get().getFlow());
		if (formList != null && !formList.isEmpty()) {
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_API_FORM_NAME_UNIQUE_CHECK,
							BffResponseCode.ERR_CLONE_USER_API_FORM_NAME_UNIQUE_CHECK),
					StatusCode.CONFLICT, null, cloneRequest.getName());

		} else {
			Form clonedForm = new Form(form.get(), false, productConfigId, true);
			clonedForm.setName(cloneRequest.getName());
			clonedForm.setFlow(form.get().getFlow());
			clonedForm = formRepo.save(clonedForm);

			dependencyUtil.manageWorkFlowDependency(clonedForm);
			dependencyUtil.findlinkedForms(clonedForm, clonedForm.getFlow().getDefaultFormId());
			bffCoreResponse = bffResponse.response(clonedForm.getUid(),
					BffResponseCode.FORM_SUCCESS_CODE_CLONE_FORM_IN_SAME_FLOW,
					BffResponseCode.FORM_USER_CODE_CLONE_FORM_IN_SAME_FLOW, StatusCode.CREATED, null,
					clonedForm.getUid().toString());
		}
		return bffCoreResponse;
	}

	/**Copying a form for given flow and form ID
	 * @param cloneRequest
	 * @return BffCoreResponse
	 */
	private BffCoreResponse copyFormForDiffFlow(CloneRequest cloneRequest) {
		BffCoreResponse bffCoreResponse;
		Optional<Form> form = formRepo.findById(cloneRequest.getId());
		if (!form.isPresent()) {
			return bffResponse.errResponse(
					List.of(BffResponseCode.ERR_NO_FORM_FOUND, BffResponseCode.ERR_NO_FORM_FOUND),
					StatusCode.BADREQUEST);
		}
		// Form name unique check.
		Optional<Flow> flowForClonedForm = flowRepository.findById(cloneRequest.getFlowIdForClonedForm());
		if (!flowForClonedForm.isPresent()) {
			return bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_FLOW_API_FETCH_FLOW_BY_ID,
							BffResponseCode.DB_ERR_FLOW_USER_FETCH_FLOW_BY_ID),
					StatusCode.INTERNALSERVERERROR, null,
					cloneRequest.getFlowIdForClonedForm().toString());
		}
		List<Form> formList = formRepo.findByNameAndFlow(cloneRequest.getName(), flowForClonedForm.get());
		if (formList != null && !formList.isEmpty()) {
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.ERR_API_FORM_NAME_UNIQUE_CHECK,
							BffResponseCode.ERR_CLONE_USER_API_FORM_NAME_UNIQUE_CHECK),
					StatusCode.CONFLICT, null, cloneRequest.getName());

		} else {
			Form clonedForm = new Form(form.get(), true, flowForClonedForm.get().getProductConfig().getUid(), true);
			clonedForm.setName(cloneRequest.getName());
			clonedForm.setFlow(flowForClonedForm.get());
			clonedForm = formRepo.save(clonedForm);
			dependencyUtil.manageWorkFlowDependency(clonedForm);
			dependencyUtil.findlinkedForms(clonedForm, clonedForm.getFlow().getDefaultFormId());
			bffCoreResponse = bffResponse.response(clonedForm.getUid(),
					BffResponseCode.FORM_SUCCESS_CODE_CLONE_FORM_IN_DIFF_FLOW,
					BffResponseCode.FORM_USER_CODE_CLONE_FORM_IN_DIFF_FLOW, StatusCode.CREATED, null,
					clonedForm.getUid().toString());
		}
		return bffCoreResponse;
	}

	/**update the default form id as per cloning/versioning
	 * 
	 * @param clonedFlow
	 * @return Flow
	 */
	private Flow setDefaultFormForClonedFlow(Flow clonedFlow) {
		if (clonedFlow.getForms() != null && !clonedFlow.getForms().isEmpty()
				&& clonedFlow.getDefaultFormId() != null) {

			// Get the default form of parent (from where it is cloned)
			Optional<Form> parentForm = formRepo.findById(clonedFlow.getDefaultFormId());

			if (parentForm.isPresent()) {
				// Iterate over cloned forms
				for (Form form : clonedFlow.getForms()) {
					// When name matches , then replace the default form id , tabbed and modal
					// details
					if (form.getName().equals(parentForm.get().getName())) {
						clonedFlow.setDefaultFormId(form.getUid());
						clonedFlow.setDefaultFormTabbed(form.isTabbedForm());
						clonedFlow.setDefaultModalForm(form.isModalForm());
						clonedFlow = flowRepository.save(clonedFlow);
						break;
					}
				}
			}

		}
		return clonedFlow;
	}
}