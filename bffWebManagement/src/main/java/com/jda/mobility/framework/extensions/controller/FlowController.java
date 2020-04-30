/**
 * Controller Class for Flow
 */
package com.jda.mobility.framework.extensions.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.CloneRequest;
import com.jda.mobility.framework.extensions.model.FlowRequest;
import com.jda.mobility.framework.extensions.model.UserPermissionRequest;
import com.jda.mobility.framework.extensions.security.UserPrincipal;
import com.jda.mobility.framework.extensions.service.FlowService;
import com.jda.mobility.framework.extensions.service.ProductPrepareService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ActionType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ChannelType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.CloneType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DeleteType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DisableType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.FlowType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.SessionAttribute;

/**
 * The class implements for create new flow and update the existing flow
 * 
 * @author HCL Technologies
 */
@RestController
@RequestMapping("/api/flow/v1")
public class FlowController {

	@Autowired
	private FlowService flowService;

	@Autowired
	private ProductPrepareService productPrepareService;

	/**
	 * Implementation for create a new flow.
	 * @param flowRo
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@PostMapping("/")
	public ResponseEntity<BffCoreResponse> createFlow(@Valid @RequestBody FlowRequest flowRo) {
		BffCoreResponse responseModel = flowService.createFlow(flowRo);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for count of customComponet registry of form based on
	 * productConfigId.
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/count")
	public ResponseEntity<BffCoreResponse> fetchCount() {
		BffCoreResponse responseModel = flowService.fetchCount();
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for update the existing flow based on flowId and actionType.
	 * @param flowRo
	 * @param flowId
	 * @param actionType
	 * @param identifier
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@PutMapping("/{flowId}/{actionType}")
	public ResponseEntity<BffCoreResponse> updateFlow(@Valid @RequestBody FlowRequest flowRo,
			@Valid @PathVariable("flowId") UUID flowId, @PathVariable("actionType") ActionType actionType,
			@RequestParam(required = false) DisableType identifier) {
		flowRo.setFlowId(flowId);
		List<String> permissionIds = new ArrayList<>();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() != null) {
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			if (ChannelType.MOBILE_RENDERER.equals(userPrincipal.getChannel())) {
				permissionIds = userPrincipal.getPermissionIds();
			}
		}
		BffCoreResponse responseModel = flowService.modifyFlow(flowRo, actionType, identifier,permissionIds);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for getting unique flow based on flowName.
	 * @param flowName
	 * @param version
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/unique/{flowname}/{version}")
	public ResponseEntity<BffCoreResponse> uniqueFlow(@PathVariable("flowname") String flowName,
			@PathVariable("version") long version) {
		BffCoreResponse responseModel = flowService.uniqueFlow(flowName, version);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for find the flowName based on flowId.
	 * @param flowId
	 * @param request
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/{flowId}")
	public ResponseEntity<BffCoreResponse> getFlowById(@Valid @PathVariable("flowId") UUID flowId,
			HttpServletRequest request) {
		setSessionAttribute(SessionAttribute.FLOW_ID.name(), flowId, request);
		BffCoreResponse responseModel = flowService.getFlowById(flowId);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * @param userPermissionRequest
	 * @param request
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@PostMapping("/defaultform")
	public ResponseEntity<BffCoreResponse> getDefaultFormForFlowId(
			@RequestBody UserPermissionRequest userPermissionRequest, HttpServletRequest request) {
		setSessionAttribute(SessionAttribute.FLOW_ID.name(), userPermissionRequest.getFlowId(), request);
		BffCoreResponse responseModel = flowService.getDefaultFormForFlowId(userPermissionRequest);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for get list of flows based on productConfigId.
	 * @param identifier
	 * @param request
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/list/{identifier}")
	public ResponseEntity<BffCoreResponse> fetchFlows(@PathVariable("identifier") FlowType identifier,
			HttpServletRequest request) {
		UUID productConfigId = productPrepareService.getCurrentLayerProdConfigId().getUid();
		setSessionAttribute(SessionAttribute.PRODUCT_CONFIG_ID.name(), productConfigId, request);
		BffCoreResponse responseModel = flowService.fetchFlows(identifier);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Implementation for delete flow based on flowId.
	 * @param flowId
	 * @param identifier
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@DeleteMapping("/{flowId}/{identifier}")
	public ResponseEntity<BffCoreResponse> deleteFlowById(@Valid @PathVariable("flowId") UUID flowId,
			@PathVariable DeleteType identifier) {
		BffCoreResponse responseModel = flowService.deleteFlowById(flowId, identifier);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Disable flow for formflowid.
	 * @param flowId
	 * @param identifier
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/disableflow/{flowId}/{identifier}")
	public ResponseEntity<BffCoreResponse> disableFlow(@PathVariable UUID flowId,
			@PathVariable DisableType identifier) {
		BffCoreResponse responseModel = flowService.disableFlow(flowId, identifier);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * Publishes formflow for actionType 'CONFIRM_PUBLISH'.
	 * @param flowId
	 * @param actionType
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@PutMapping("/publish/{flowId}/{actionType}")
	public ResponseEntity<BffCoreResponse> publishFlow(@PathVariable UUID flowId, @PathVariable ActionType actionType) {
		List<String> permissionIds = new ArrayList<>();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() != null) {
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			if (ChannelType.MOBILE_RENDERER.equals(userPrincipal.getChannel())) {
				permissionIds = userPrincipal.getPermissionIds();
			}
		}
		BffCoreResponse responseModel = flowService.publishFlow(flowId, actionType,permissionIds);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * @param cloneRequest
	 * @param actionType
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@PostMapping("/clone/{actionType}")
	public ResponseEntity<BffCoreResponse> cloneComponent(@Valid @RequestBody CloneRequest cloneRequest,
			@PathVariable CloneType actionType) {
		BffCoreResponse responseModel = flowService.cloneComponent(cloneRequest, actionType,
				BffAdminConstantsUtils.EXTENDED);
		return new ResponseEntity<>(responseModel, HttpStatus.valueOf(responseModel.getHttpStatusCode()));
	}

	/**
	 * @return ResponseEntity&lt;BffCoreResponse&gt;
	 */
	@GetMapping("/list/basic")
	public ResponseEntity<BffCoreResponse> fetchFlowBasicList() {
		BffCoreResponse responseModel = flowService.fetchFlowBasicList();
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
			if (sessionUuid != null && !sessionUuid.equals(id)) {
				request.getSession(false).setAttribute(attributeId, id);
			}
		}
	}
}