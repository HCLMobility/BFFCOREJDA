package com.jda.mobility.framework.extensions.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.entity.Events;
import com.jda.mobility.framework.extensions.entity.Field;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.FormDependency;
import com.jda.mobility.framework.extensions.entity.PublishedFormDependency;
import com.jda.mobility.framework.extensions.entity.Tabs;
import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.repository.EventsRepository;
import com.jda.mobility.framework.extensions.repository.FlowRepository;
import com.jda.mobility.framework.extensions.repository.FormDependencyRepository;
import com.jda.mobility.framework.extensions.repository.FormRepository;
import com.jda.mobility.framework.extensions.repository.PublishedFormDependencyRepository;

/**
 * Utility to find/save dependency detail of form 
 * - Helps to find orphan form
 * @author HCL
 *
 */
@Component
public class FormDependencyUtil {
	
	private static final String FLOW_ID = "flowId";
	private static final String TABBED_FORM = "tabbedForm";
	private static final String MODAL_FORM = "modalForm";
	private static final Logger LOGGER = LogManager.getLogger(FormDependencyUtil.class);

	
	@Autowired
	private PublishedFormDependencyRepository publishedFormDependencyRepository;
	@Autowired
	private EventsRepository eventsRepository;
	@Autowired
	private FormDependencyRepository formDependencyRepository;
	@Autowired
	private FormRepository formRepo;
	@Autowired
	private FlowRepository flowRepo;
	/**
	 * Method used to manage inbounds and outbound form Id's
	 * 
	 * @param form Form object for which we need to calculate inbound and outbound details
	 */
	public void manageWorkFlowDependency(Form form) {
		Map<UUID, UUID> flowAndDefaultFormMap = new HashMap<>();
		Set<UUID> navigateToFormIds = new HashSet<>(); 
		List<FormDependency> deletedFormDependencies = new ArrayList<>();
		findOutboundFormIds(form, navigateToFormIds, flowAndDefaultFormMap);
		manageFormDependency(form, navigateToFormIds);
		
		List<FormDependency> formDependencies = formDependencyRepository.findByInboundFormIdAndOutboundFlowIdNotNull(form.getUid());
		formDependencies.parallelStream().forEach(formDependency -> {
			if(flowAndDefaultFormMap.get(formDependency.getOutboundFlowId()) == null) {
				deletedFormDependencies.add(formDependency);
			}else {
				formDependency.setOutboundFormId(flowAndDefaultFormMap.get(formDependency.getOutboundFlowId()));
			}
		});
		flowAndDefaultFormMap.entrySet().stream().forEach(entry -> {
			if(formDependencies.parallelStream().noneMatch(formDependency -> formDependency.getOutboundFlowId().equals(entry.getKey()))) {
				formDependencies.add(FormDependency.builder()
						.inboundFlow(form.getFlow())
						.inboundFormId(form.getUid())
						.outboundFlowId(entry.getKey())
						.outboundFormId(entry.getValue())
						.build());
			}
		});
		if(!deletedFormDependencies.isEmpty()) {
			formDependencies.removeAll(deletedFormDependencies);
			formDependencyRepository.deleteAll(deletedFormDependencies);
		}
		if(!formDependencies.isEmpty()) {
			formDependencyRepository.saveAll(formDependencies);
		}
	}

	/**Method to calculate in and out bound for form to arrive at orphan form calculation
	 * @param form Form for which we need to calculate in and out bounds
	 * @param navigateToFormIds - Set navigate to form (Form Id) that incoming form possess
	 */
	public void manageFormDependency(Form form, Set<UUID> navigateToFormIds) {
		List<FormDependency> dependencies = formDependencyRepository.findByInboundFormId(form.getUid());
		List<FormDependency> removedFormDependencies = dependencies.stream()
				.filter(formDependency -> !navigateToFormIds.contains(formDependency.getOutboundFormId()))
				.collect(Collectors.toList());
		if(!removedFormDependencies.isEmpty()) {
			formDependencyRepository.deleteAll(removedFormDependencies);
			dependencies.removeAll(removedFormDependencies);
		}
		Set<UUID> existingOutboundFormIds = dependencies.stream().map(FormDependency::getOutboundFormId)
				.collect(Collectors.toSet());
		Set<UUID> newOutboundFormIds = navigateToFormIds.stream()
				.filter(outboundFormId -> !existingOutboundFormIds.contains(outboundFormId))
				.collect(Collectors.toSet());
		if(!newOutboundFormIds.isEmpty()) {
			Set<FormDependency> newDependencies = new HashSet<>();
			newOutboundFormIds.parallelStream().forEach(outboundFormId ->  newDependencies.add(FormDependency.builder()
						.inboundFlow(form.getFlow())
						.inboundFormId(form.getUid())
						.outboundFormId(outboundFormId)
						.build()));
			formDependencyRepository.saveAll(newDependencies);
		}
	}

	/**
	 * Method used to get and prepare set of outbound form Id's from form

	 * @param form Form from which we are going to extract outbound form id 
	 * @param outboundFormIds Set to hold list of outbound ids
	 * @param flowAndDefaultFormIdMap Map to hold flow and its default form Id
	 */
	private void findOutboundFormIds(Form form, Set<UUID> outboundFormIds, Map<UUID, UUID> flowAndDefaultFormIdMap) {
		Set<UUID> linkedFormIds = new HashSet<>();
		Map<UUID, UUID> linkedFlowAndDefaultFormIdMap = new HashMap<>();
		List<Field> fields = getListFields(form.getFields());
		for (Field field : fields) {
			extracteOutboundFormIdsFromEvents(linkedFormIds, linkedFlowAndDefaultFormIdMap, field.getEvents());
		}
		extracteOutboundFormIdsFromEvents(linkedFormIds, linkedFlowAndDefaultFormIdMap, form.getEvents());
		if(!CollectionUtils.isEmpty(form.getTabs())) {
			linkedFormIds.addAll(form.getTabs().stream().map(Tabs::getLinkedFormId).collect(Collectors.toSet()));
		}
		// Filter deleted form Id's
		if (!CollectionUtils.isEmpty(linkedFormIds)) {
			outboundFormIds.addAll(formRepo.findExistingFormIds(linkedFormIds));
		}
		if(!linkedFlowAndDefaultFormIdMap.isEmpty()) {
			Set<Flow> linkedFlows = flowRepo.findByUidIn(linkedFlowAndDefaultFormIdMap.keySet());
			
			linkedFlows.parallelStream().forEach(linkedFlow -> 	flowAndDefaultFormIdMap.put(linkedFlow.getUid(), linkedFlow.getDefaultFormId()));
		}
	}

	/**
	 * Method used to extract outbound form Ids from events
	
	 * @param outboundFormIds Set to hold list of outbould form ids
	 * @param flowAndDefaultFormIdMap Map to hold flow and its default form id
	 * @param eventsList List of events in form and its field
	 */
	private void extracteOutboundFormIdsFromEvents(Set<UUID> outboundFormIds,  Map<UUID, UUID> flowAndDefaultFormIdMap, List<Events> eventsList) {
		JsonNode actionNode;
		ObjectMapper objectMapper = new ObjectMapper();
		UUID defaultFormId = null;
		if (!CollectionUtils.isEmpty(eventsList)) {
			for (Events events : eventsList) {
				if (!StringUtils.isEmpty(events.getAction())) {
					try {
						actionNode = objectMapper.readTree(events.getAction());
					} catch (JsonProcessingException exp) {
						LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
						throw new BffException("Event Extraction for OutboundFormId failed.", exp.getCause());
					}
					List<JsonNode> navigateToFormNodes = actionNode.findParents(BffAdminConstantsUtils.FORM);
					// Get outbound form Id of navigate to from
					navigateToFormNodes.parallelStream().forEach(navigateToFormNode -> {
						String outboundFormIdString = navigateToFormNode.at("/form/formId").asText();
						if(!StringUtils.isEmpty(outboundFormIdString)) {
							outboundFormIds.add(UUID.fromString(outboundFormIdString));
						}
					});
					// Get outbound form Id of navigate to from flow
					List<JsonNode> parentNodes = actionNode.findParents(BffAdminConstantsUtils.WORKFLOW);
					for(JsonNode node : parentNodes) {
						String navigateToWorkFlowIdString = node.at("/workflow/flowId").asText();
						if(!StringUtils.isEmpty(navigateToWorkFlowIdString)) {
							if(!(node.at("/workflow/defaultFormId") instanceof NullNode) 
									&& !BffAdminConstantsUtils.EMPTY_SPACES.equals(node.at("/workflow/defaultFormId").asText())) {
								defaultFormId = UUID.fromString(node.at("/workflow/defaultFormId").asText());
							}
							flowAndDefaultFormIdMap.put(UUID.fromString(navigateToWorkFlowIdString), defaultFormId);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Method used to update modalForm and tabbedForm in publishedForm 
	 * @param jsonNode Json node where we need to look for form ids and flow ids
	 * @param modalForm Tells whether form is modal form or not
	 * @param tabbedForm Tells whether form is tabbed form or not
	 */
	private void updateNavigateToFormJsonNode(JsonNode jsonNode, boolean modalForm, boolean tabbedForm, UUID defaultFormId, Form form) {
		if (jsonNode.isObject() && jsonNode.toString().contains(MODAL_FORM) && jsonNode.toString().contains(TABBED_FORM)) {
			if(jsonNode.has(MODAL_FORM) && jsonNode.has(TABBED_FORM)) {
				ObjectNode objectNode = (ObjectNode) jsonNode;
				if(jsonNode.has(BffAdminConstantsUtils.DEFAULT_FORM_ID) && jsonNode.get(FLOW_ID).asText().equalsIgnoreCase(form.getFlow().getUid().toString())) {
					if(defaultFormId != null) {
						objectNode.put(BffAdminConstantsUtils.DEFAULT_FORM_ID, defaultFormId.toString());
					} else {
						objectNode.replace(BffAdminConstantsUtils.DEFAULT_FORM_ID, null);
					}
					objectNode.put(MODAL_FORM, modalForm);
					objectNode.put(TABBED_FORM, tabbedForm);
				} else if(jsonNode.has(BffAdminConstantsUtils.FORM_ID) && jsonNode.get(BffAdminConstantsUtils.FORM_ID).asText().equalsIgnoreCase(form.getUid().toString())) {
					objectNode.put(MODAL_FORM, modalForm);
					objectNode.put(TABBED_FORM, tabbedForm);
				}
			} else {
				jsonNode.forEach(jsonElement -> {
					if(jsonElement.isObject() || jsonElement.isArray()) {
						updateNavigateToFormJsonNode(jsonElement, modalForm, tabbedForm, defaultFormId, form);
					}
				});
			}
		} else if(jsonNode.isArray()) {
			jsonNode.forEach(arrayElement ->  updateNavigateToFormJsonNode(arrayElement, modalForm, tabbedForm, defaultFormId, form));
		}
	}
	
	
	/**
	 * Method used to manage navigate to work flow dependencies of published forms 
	 * @param form - Form object in which we are going look for navigate to form flow
	 */
	public void managePublishedWorkFlowDependency(Form form) {
		Map<UUID, UUID> flowAndDefaultFormMap = new HashMap<>();
		List<PublishedFormDependency> deletedPublishedFormDependencies = new ArrayList<>();
		UUID defaultFormId = null;
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode publishedForm;
		try {
			publishedForm = objectMapper.readTree(form.getPublishedForm());
		} catch (IOException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			throw new BffException("Event Extraction for OutboundFormId failed.", exp.getCause());
		}
		managePublishedFormDependency(form, publishedForm);
		//fetch workflow nodes
		List<JsonNode> parentNodes = publishedForm.findParents(BffAdminConstantsUtils.WORKFLOW);
		for(JsonNode node : parentNodes) {
			String navigateToWorkFlowIdString = node.at("/workflow/flowId").asText();
			if(!StringUtils.isEmpty(navigateToWorkFlowIdString)) {
				if(!(node.at("/workflow/defaultFormId") instanceof NullNode) && !BffAdminConstantsUtils.EMPTY_SPACES.equals(node.at("/workflow/defaultFormId").asText())) {
					defaultFormId = UUID.fromString(node.at("/workflow/defaultFormId").asText());
				}
				//add flow if as key and default form id as value in to map
				flowAndDefaultFormMap.put(UUID.fromString(navigateToWorkFlowIdString), defaultFormId);
			}
		}
		List<PublishedFormDependency> publishedFormDependencies = publishedFormDependencyRepository.findByInboundFormIdAndOutboundFlowIdNotNull(form.getUid());
		//find removed workflow dependencies 
		publishedFormDependencies.parallelStream().forEach(formDependency -> {
			if(flowAndDefaultFormMap.get(formDependency.getOutboundFlowId()) == null) {
				deletedPublishedFormDependencies.add(formDependency);
			}else {
				formDependency.setOutboundFormId(flowAndDefaultFormMap.get(formDependency.getOutboundFlowId()));
			}
		});
		//add newly added navigate to workflow dependencies to list
		flowAndDefaultFormMap.entrySet().stream().forEach(entry -> {
			if(publishedFormDependencies.parallelStream().noneMatch(formDependency -> formDependency.getOutboundFlowId().equals(entry.getKey()))) {
				publishedFormDependencies.add(PublishedFormDependency.builder()
						.inboundFlow(form.getFlow())
						.inboundFormId(form.getUid())
						.outboundFlowId(entry.getKey())
						.outboundFormId(entry.getValue())
						.build());
			}
		});
		if(!deletedPublishedFormDependencies.isEmpty()) {
			publishedFormDependencies.removeAll(deletedPublishedFormDependencies);
			publishedFormDependencyRepository.deleteAll(deletedPublishedFormDependencies);
		}
		if(!publishedFormDependencies.isEmpty()) {
			publishedFormDependencyRepository.saveAll(publishedFormDependencies);
		}
	}

	/**
	 * Method used to manage navigate to form dependencies of published forms 
	 * @param form Form Object in which we are going to look for naviagte to form
	 * @param publishedForm JSON object that contains entire form data 
	 */
	private void managePublishedFormDependency(Form form, JsonNode publishedForm) {
		List<JsonNode> navigateToFormNodes = publishedForm.findParents(BffAdminConstantsUtils.FORM);
		Set<UUID> navigateToFormIds = new HashSet<>();
		navigateToFormNodes.parallelStream().forEach(navigateToFormNode -> {
			String outboundFormIdString = navigateToFormNode.at("/form/formId").asText();
			if(!StringUtils.isEmpty(outboundFormIdString)) {
				navigateToFormIds.add(UUID.fromString(outboundFormIdString));
			}
		});
		List<PublishedFormDependency> dependencies = publishedFormDependencyRepository.findByInboundFormId(form.getUid());
		//find deleted navigate to form dependencies 
		List<PublishedFormDependency> removedFormDependencies = dependencies.stream()
				.filter(formDependency -> !navigateToFormIds.contains(formDependency.getOutboundFormId()))
				.collect(Collectors.toList());
		if(!removedFormDependencies.isEmpty()) {
			publishedFormDependencyRepository.deleteAll(removedFormDependencies);
			dependencies.removeAll(removedFormDependencies);
		}
		Set<UUID> existingOutboundFormIds = dependencies.stream().map(PublishedFormDependency::getOutboundFormId)
				.collect(Collectors.toSet());
		//Find newly added navigate to form Id's
		Set<UUID> newOutboundFormIds = navigateToFormIds.stream()
				.filter(outboundFormId -> !existingOutboundFormIds.contains(outboundFormId))
				.collect(Collectors.toSet());
		if(!newOutboundFormIds.isEmpty()) {
			Set<PublishedFormDependency> newDependencies = new HashSet<>();
			newOutboundFormIds.parallelStream().forEach(outboundFormId ->  newDependencies.add(PublishedFormDependency.builder()
						.inboundFlow(form.getFlow())
						.inboundFormId(form.getUid())
						.outboundFormId(outboundFormId)
						.build()));
			publishedFormDependencyRepository.saveAll(newDependencies);
		}
	}
	
	/**
	 * Method used to find the linked published forms
	 * @param form Form object in which we are going extract linked form ids details
	 * @param defaultFormId  Form id which need to be updated
	 */
	public void findLinkedPublishedFormIds(Form form, UUID defaultFormId) {
		Set<UUID> linkedFormIds = new HashSet<>();
		List<PublishedFormDependency> publishedFormDependencies = publishedFormDependencyRepository.findByOutboundFlowId(form.getFlow().getUid());
		if(!CollectionUtils.isEmpty(publishedFormDependencies)) {
			for(PublishedFormDependency publishedFormDependency : publishedFormDependencies) {
				if(defaultFormId != null && defaultFormId.equals(form.getUid())) {
					linkedFormIds.add(publishedFormDependency.getInboundFormId());
				}
				//update default form Id
				publishedFormDependency.setOutboundFormId(defaultFormId);
			}
			publishedFormDependencyRepository.saveAll(publishedFormDependencies);
		}
		//Get navigate to form dependency Id's
		linkedFormIds.addAll(publishedFormDependencyRepository.findByOutboundFormId(form.getUid()));
		if(!linkedFormIds.isEmpty()) {
			updateLinkedPublishedForm(form, linkedFormIds, defaultFormId);
		}
	}
	
	/**
	 * This method is used to update default form id, modal form and tabbed form status in published forms
	 * @param form Form object
	 * @param formIds Set of linked form ids
	 * @param defaultFormId Form id which need to be updated
	 */
	private void updateLinkedPublishedForm(Form form, Set<UUID> formIds, UUID defaultFormId) {
		ObjectMapper objectMapper = new ObjectMapper();
		List<Form> forms = formRepo.findByUidIn(formIds);
		for(Form publishedForm : forms) {
			JsonNode publishedFormNode;
			try {
				if(null!=publishedForm.getPublishedForm())
				{
					publishedFormNode = objectMapper.readTree(publishedForm.getPublishedForm());
					updateNavigateToFormJsonNode(publishedFormNode, form.isModalForm(), form.isTabbedForm(), defaultFormId, form);
					publishedForm.setPublishedForm(objectMapper.writeValueAsBytes(publishedFormNode));
				}
			} catch (IOException exp) {
				LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
				throw new BffException("Event Extraction for OutboundFormId failed.", exp.getCause());
			}
		}
		formRepo.saveAll(forms);
	}
	
	/**
	 * Method is used to find linked forms 
	 * @param form Form Object to extract linked form ids
	 * @param defaultFormId Form Id which need to be updated
	 */
	public void findlinkedForms(Form form, UUID defaultFormId) {
		Set<UUID> linkedFormIds = new HashSet<>();
		List<FormDependency> formDependencies = formDependencyRepository.findByOutboundFlowId(form.getFlow().getUid());
		if(!CollectionUtils.isEmpty(formDependencies)) {
			for(FormDependency publishedFormDependency : formDependencies) {
				if(null!= defaultFormId && defaultFormId.equals(form.getUid())) {
					linkedFormIds.add(publishedFormDependency.getInboundFormId());
				}
				//update default form id in form dependency table
				publishedFormDependency.setOutboundFormId(defaultFormId);
			}
			formDependencyRepository.saveAll(formDependencies);
		}
		linkedFormIds.addAll(formDependencyRepository.findByOutboundFormId(form.getUid()));
		if(!linkedFormIds.isEmpty()) {
			updatelinkedForm(form, linkedFormIds, defaultFormId);
		}
	}
	
	/**
	 * Method used to update modal form, tabbed form status and default form id in linked forms
	 * @param form Form object
	 * @param formIds Set of form ids that are linked
	 * @param defaultFormId form id that need to be updated
	 */
	private void updatelinkedForm(Form form, Set<UUID> formIds, UUID defaultFormId) {
		ObjectMapper objectMapper = new ObjectMapper();
		List<Form> forms = formRepo.findByUidIn(formIds);
		List<Field> fields;
		List<Events> events = new ArrayList<>();
		JsonNode eventActionNode;
		for(Form linkedForm : forms) {
			fields = getListFields(linkedForm.getFields());
			fields.parallelStream().forEach(field -> {
				if(!CollectionUtils.isEmpty(field.getEvents())) {
					events.addAll(field.getEvents());
				}
			});
			if(!CollectionUtils.isEmpty(linkedForm.getEvents())) {
				events.addAll(linkedForm.getEvents());
			}
		}
		for(Events event : events) {
			try {
				if(!StringUtils.isEmpty(event.getAction())) {
					eventActionNode = objectMapper.readTree(event.getAction());
					updateNavigateToFormJsonNode(eventActionNode, form.isModalForm(), form.isTabbedForm(), defaultFormId, form);
					event.setAction(eventActionNode.toString());
				}
			} catch (JsonProcessingException exp) {
				LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
				throw new BffException("Event Extraction for OutboundFormId failed.", exp.getCause());
			}

		}
		eventsRepository.saveAll(events);

	}
	
	/**
	 * To return List of Fields without child field objects
	 * 
	 * @param fields Field object to be examined
	 * @return List<Field> List of field that has no children
	 */
	private List<Field> getListFields(List<Field> fields) {
		List<Field> fieldsList = new ArrayList<>();
		for (Field field : fields) {
			fieldsList.add(field);
			if (field.getChildFields() != null && !field.getChildFields().isEmpty()) {
				fieldsList.addAll(getListFields(field.getChildFields()));
			}
		}
		return fieldsList;
	}
	
	

}
