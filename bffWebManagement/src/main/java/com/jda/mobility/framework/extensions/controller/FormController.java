/**
 * 
 */
package com.jda.mobility.framework.extensions.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jda.mobility.framework.extensions.model.AppConfigRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.FormData;
import com.jda.mobility.framework.extensions.security.UserPrincipal;
import com.jda.mobility.framework.extensions.service.FormService;
import com.jda.mobility.framework.extensions.service.ProductPrepareService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ActionType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ChannelType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DefaultType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DeleteType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.FormStatus;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.SessionAttribute;

/**
 * The class implements for create new form and update the existing form
 * 
 * @author HCL Technologies
 */

@RestController
@RequestMapping("/api/form/v1")
public class FormController {

	@Autowired
	private FormService formService;

	@Autowired
	private ProductPrepareService productPrepareService;

	private static final Logger LOGGER = LogManager.getLogger(FormController.class);

	/**
	 * Implementation for create new form
	 *
	 * @param formData
	 * @param actionType
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@PostMapping("/{actionType}")
	public ResponseEntity<BffCoreResponse> createForm(@Valid @RequestBody FormData formData,
			@PathVariable("actionType") ActionType actionType, @RequestParam(required = false) DefaultType identifier) {
		List<String> permissionIds = new ArrayList<>();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() != null) {
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			if (ChannelType.MOBILE_RENDERER.equals(userPrincipal.getChannel())) {
				permissionIds = userPrincipal.getPermissionIds();
			}
		}
		
		BffCoreResponse responseModel = formService.createForm(formData, actionType, identifier,permissionIds);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for update existing form.
	 *
	 * @param formId
	 * @param actionType
	 * @param formData
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@PutMapping("/{formId}/{actionType}")
	public ResponseEntity<BffCoreResponse> modifyForm(@Valid @PathVariable("formId") UUID formId,
			@PathVariable("actionType") ActionType actionType, @Valid @RequestBody FormData formData,
			@RequestParam(required = false) DefaultType identifier) {
		formData.setFormId(formId);
		List<String> permissionIds = new ArrayList<>();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() != null) {
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			if (ChannelType.MOBILE_RENDERER.equals(userPrincipal.getChannel())) {
				permissionIds = userPrincipal.getPermissionIds();
			}
		}
		BffCoreResponse responseModel = formService.modifyForm(actionType, formData, identifier,permissionIds);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for retrieving the form based existing formid
	 *
	 * @param formId
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/{formId}")
	public ResponseEntity<BffCoreResponse> getFormById(@Valid @PathVariable("formId") UUID formId,
			@RequestParam(required = false) UUID menuId, HttpServletRequest request) {
		List<String> permissionIds = new ArrayList<>();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() != null) {
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			if (ChannelType.MOBILE_RENDERER.equals(userPrincipal.getChannel())) {
				permissionIds = userPrincipal.getPermissionIds();
				
				//Only for Mobile , we need to maintain them in session
				setSessionAttribute(SessionAttribute.FORM_ID.name(), formId, request);
				setSessionAttribute(SessionAttribute.MENU_ID.name(), menuId, request);
			}
		}
		BffCoreResponse responseModel = formService.getFormById(formId, permissionIds, menuId);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Updates global and context variables from mobile and gets the form data based on supplied parameters
	 *
	 * @param formId
	 * @param menuId
	 * @param request
	 * @param appConfigList
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@PutMapping("/formdata")
	public ResponseEntity<BffCoreResponse> getForm(@RequestParam("formId") UUID formId,
			@RequestParam(required = false) UUID menuId,HttpServletRequest request , @RequestBody(required=false) List<AppConfigRequest> appConfigList) {
		
		List<String> permissionIds = new ArrayList<>();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() != null) {
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			if (ChannelType.MOBILE_RENDERER.equals(userPrincipal.getChannel())) {
				permissionIds = userPrincipal.getPermissionIds();
				
				//Only for Mobile , we need to maintain them in session
				setSessionAttribute(SessionAttribute.FORM_ID.name(), formId, request);
				setSessionAttribute(SessionAttribute.MENU_ID.name(), menuId, request);
			}
		}
		BffCoreResponse responseModel = formService.getForm(formId, permissionIds,appConfigList);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for delete existing record based on formId
	 *
	 * @param formId
	 * @param identifier
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@DeleteMapping("/{formId}/{identifier}")
	public ResponseEntity<BffCoreResponse> deleteForm(@Valid @PathVariable("formId") UUID formId,
			@PathVariable("identifier") DeleteType identifier) {
		BffCoreResponse responseModel = formService.deleteFormByID(formId, identifier);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for fetch AllForms based on flowId
	 *
	 * @param flowId
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/list/{flowId}")
	public ResponseEntity<BffCoreResponse> fetchAllForms(@Valid @PathVariable("flowId") UUID flowId,
			HttpServletRequest request) {
		setSessionAttribute(SessionAttribute.FLOW_ID.name(), flowId, request);
		BffCoreResponse responseModel = formService.fetchAllForms(flowId);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for retrieving form list based on flowId
	 *
	 * @param flowId
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/orphanforms/{flowId}")
	public ResponseEntity<BffCoreResponse> fetchOrphanForms(@Valid @PathVariable("flowId") UUID flowId,
			HttpServletRequest request) {
		setSessionAttribute(SessionAttribute.FLOW_ID.name(), flowId, request);
		BffCoreResponse responseModel = formService.fetchOrphanForms(flowId);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for fetch UnpublishForms based on secondaryRefId
	 *
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/unpublishforms")
	public ResponseEntity<BffCoreResponse> fetchUnpublishForms() {
		BffCoreResponse responseModel = formService.fetchUnpublishForms();
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for fetch DefaultForm based on existing formId
	 *
	 * @param formId
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/defaultform/{formId}")
	public ResponseEntity<BffCoreResponse> createDefaultForm(@Valid @PathVariable("formId") UUID formId,
			@RequestParam(required = false) DefaultType identifier) {
		BffCoreResponse responseModel = formService.createDefaultForm(formId, identifier);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for GetFormComponent based on existing customComponentId
	 *
	 * @param customComponentId
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/formlist/{customComponentId}")
	public ResponseEntity<BffCoreResponse> getFormComponent(
			@Valid @PathVariable("customComponentId") UUID customComponentId) {
		BffCoreResponse responseModel = formService.getFormDetails(customComponentId);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Test controller URL
	 *
	 * @return String
	 */
	@PostMapping("/log/")
	public String testLog() {
		return BffAdminConstantsUtils.SUCCESS;
	}

	/**
	 * @param formId
	 * @param actionType
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@PutMapping("/publish/{formId}/{actionType}")
	public ResponseEntity<BffCoreResponse> publishForm(@PathVariable UUID formId, @PathVariable ActionType actionType) {
		List<String> permissionIds = new ArrayList<>();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() != null) {
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			if (ChannelType.MOBILE_RENDERER.equals(userPrincipal.getChannel())) {
				permissionIds = userPrincipal.getPermissionIds();
			}
		}
		BffCoreResponse responseModel = formService.publishForm(formId, actionType,permissionIds);
		
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * @param flowId
	 * @param request
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/list/basic/{flowId}")
	public ResponseEntity<BffCoreResponse> fetchFormBasicList(@PathVariable UUID flowId, HttpServletRequest request) {
		setSessionAttribute(SessionAttribute.FLOW_ID.name(), flowId, request);
		BffCoreResponse responseModel = formService.fetchFormBasicList(flowId);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * @param identifier
	 * @param request
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/forms")
	public ResponseEntity<BffCoreResponse> fetchForms(@RequestParam FormStatus identifier, HttpServletRequest request) {
		UUID productConfigId = productPrepareService.getCurrentLayerProdConfigId().getUid();
		setSessionAttribute(SessionAttribute.PRODUCT_CONFIG_ID.name(), productConfigId, request);
		BffCoreResponse responseModel = formService.fetchUnpublishOrphanForms(productConfigId, identifier);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * @param attributeId
	 * @param id
	 * @param request
	 */
	private void setSessionAttribute(String attributeId, UUID id, HttpServletRequest request) {
		if (request.getSession(false) != null) {
			UUID sessionUuid = (UUID) request.getSession(false).getAttribute(attributeId);
			LOGGER.log(Level.DEBUG, "Updating session attribute {}", id);
			//If Attribute is not present , then insert them
			if(sessionUuid==null) {
				request.getSession(false).setAttribute(attributeId, id);
			}
			else {
				//If attribute is present , when values are different then update them
				if(!sessionUuid.equals(id))
				{
					request.getSession(false).setAttribute(attributeId, id);
				}
			}
			
			
		}
	}
}