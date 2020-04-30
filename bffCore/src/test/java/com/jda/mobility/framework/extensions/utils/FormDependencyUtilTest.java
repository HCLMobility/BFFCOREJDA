package com.jda.mobility.framework.extensions.utils;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.jda.mobility.framework.extensions.entity.Events;
import com.jda.mobility.framework.extensions.entity.Field;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.FormDependency;
import com.jda.mobility.framework.extensions.entity.PublishedFormDependency;
import com.jda.mobility.framework.extensions.entity.Tabs;
import com.jda.mobility.framework.extensions.repository.EventsRepository;
import com.jda.mobility.framework.extensions.repository.FlowRepository;
import com.jda.mobility.framework.extensions.repository.FormDependencyRepository;
import com.jda.mobility.framework.extensions.repository.FormRepository;
import com.jda.mobility.framework.extensions.repository.PublishedFormDependencyRepository;

@RunWith(SpringJUnit4ClassRunner.class)
public class FormDependencyUtilTest {
	@InjectMocks
	private FormDependencyUtil formDependencyUtil;
	@Mock
	private PublishedFormDependencyRepository publishedFormDependencyRepository;
	@Mock
	private EventsRepository eventsRepository;
	@Mock
	private FormDependencyRepository formDependencyRepository;
	@Mock
	private FormRepository formRepo;
	@Mock
	private FlowRepository flowRepo;

	@Test
	public void testManageWorkFlowDependency() {
		Set<UUID> linkedFormIds = new HashSet<>();
		linkedFormIds.add(UUID.randomUUID());
		Map<UUID, UUID> linkedFlowAndDefaultFormIdMap = new HashMap<>();
		linkedFlowAndDefaultFormIdMap.put(UUID.randomUUID(), UUID.randomUUID());
		Set<UUID> set = new HashSet<>();
		set.add(UUID.randomUUID());
		when(formRepo.findExistingFormIds(Mockito.any())).thenReturn(set);
		Set<Flow> flows = new HashSet<>();
		flows.add(getFlow());
		when(flowRepo.findByUidIn(Mockito.any())).thenReturn(flows);
		when(formDependencyRepository.findByInboundFormId(Mockito.any())).thenReturn(getFormDependencies());
		when(formDependencyRepository.findByInboundFormIdAndOutboundFlowIdNotNull(Mockito.any())).thenReturn(getFormDependencies());
		formDependencyUtil.manageWorkFlowDependency(getForm1());
		assertTrue(true);
	}

	@Test
	public void testManagePublishedWorkFlowDependency() {
		Set<UUID> linkedFormIds = new HashSet<>();
		linkedFormIds.add(UUID.randomUUID());
		Map<UUID, UUID> linkedFlowAndDefaultFormIdMap = new HashMap<>();
		linkedFlowAndDefaultFormIdMap.put(UUID.randomUUID(), UUID.randomUUID());
		when(publishedFormDependencyRepository.findByInboundFormId(Mockito.any())).thenReturn(getPublishedFormDependencys());
		when(publishedFormDependencyRepository.findByInboundFormIdAndOutboundFlowIdNotNull(Mockito.any())).thenReturn(getPublishedFormDependencys());
		formDependencyUtil.managePublishedWorkFlowDependency(getForm1());
		assertTrue(true);
	}

	@Test
	public void testFindlinkedForms() {
		UUID defaultFormId = UUID.fromString("47b23ea6-ff9b-48f9-ab77-f869398c95ad");
		Form form = new Form();
		form.setUid(UUID.fromString("47b23ea6-ff9b-48f9-ab77-f869398c95ad"));
		Flow flow = new Flow();
		flow.setUid(UUID.fromString("e0ed9bf2-3e86-4a74-95b0-a4d67f956a59"));
		flow.setDefaultFormId(defaultFormId);
		form.setFlow(flow);
		Set<UUID> linkedFormIds = new HashSet<>();
		linkedFormIds.add(UUID.randomUUID());
		Map<UUID, UUID> linkedFlowAndDefaultFormIdMap = new HashMap<>();
		linkedFlowAndDefaultFormIdMap.put(UUID.randomUUID(), UUID.randomUUID());
		List<Form> forms = new ArrayList<>();
		forms.add(getForm1());
		when(formRepo.findByUidIn(Mockito.any())).thenReturn(forms);
		when(formDependencyRepository.findByOutboundFlowId(Mockito.any())).thenReturn(getFormDependencies());
		formDependencyUtil.findlinkedForms(getFormPublish(), defaultFormId);
		assertTrue(true);
	}

	@Test
	public void testFindLinkedPublishedFormIds() throws JsonMappingException, JsonProcessingException {
		UUID defaultFormId = UUID.fromString("49b94476-2e9b-4374-a458-933b5f0ef0b4");
		Form form = new Form();
		form.setUid(UUID.fromString("49b94476-2e9b-4374-a458-933b5f0ef0b4"));
		form.setPublished(true);
		form.setModalForm(true);
		form.setTabbedForm(true);
		byte[] publishdForm = "{}".getBytes();
		form.setPublishedForm(publishdForm);
		Flow flow = new Flow();
		flow.setUid(UUID.randomUUID());
		form.setFlow(flow);
		Set<UUID> linkedFormIds = new HashSet<>();
		linkedFormIds.add(UUID.randomUUID());
		Map<UUID, UUID> linkedFlowAndDefaultFormIdMap = new HashMap<>();
		linkedFlowAndDefaultFormIdMap.put(UUID.randomUUID(), UUID.randomUUID());
		List<Form> forms = new ArrayList<>();
		forms.add(getForm1());
		when(formRepo.findByUidIn(Mockito.any())).thenReturn(forms);
		when(publishedFormDependencyRepository.findByOutboundFlowId(Mockito.any()))
				.thenReturn(getPublishedFormDependencys());
		when(publishedFormDependencyRepository.findByOutboundFormId(Mockito.any())).thenReturn(linkedFormIds);
		formDependencyUtil.findLinkedPublishedFormIds(getFormPublish(), defaultFormId);
		assertTrue(true);
	}

	private List<PublishedFormDependency> getPublishedFormDependencys() {
		Flow flow = new Flow();
		flow.setUid(UUID.randomUUID());
		List<PublishedFormDependency> dependencies = new ArrayList<>();
		PublishedFormDependency publishedFormDependency = new PublishedFormDependency();
		publishedFormDependency.setInboundFlow(flow);
		publishedFormDependency.setInboundFormId(UUID.randomUUID());
		publishedFormDependency.setOutboundFlowId(UUID.randomUUID());
		publishedFormDependency.setOutboundFormId(UUID.randomUUID());
		flow.setPublishedFormDependencies(dependencies);
		dependencies.add(publishedFormDependency);

		return dependencies;
	}

	private Form getForm1() {
		Form form = new Form();
		form.setUid(UUID.fromString("49b94476-2e9b-4374-a458-933b5f0ef0b4"));
		List<Field> fields = new ArrayList<>();
		Field field = new Field();
		field.setChildFields(fields);
		List<Events> events = new ArrayList<Events>();
		Events navigateToFormEvent = new Events();
		navigateToFormEvent.setAction("{\"actionType\":\"NAVIGATE_TO_FORM\",\"properties\":{\"form\":{\"formId\":\"47b23ea6-ff9b-48f9-ab77-f869398c95ad\",\"formName\":\"form111\",\"modalForm\":false,\"tabbedForm\":false}}}");
		events.add(navigateToFormEvent);
		Events navigateToFlowEvent = new Events();
		navigateToFlowEvent.setAction("{\"actionType\":\"NAVIGATE_TO_WORKFLOW\",\"properties\":{\"workflow\":{\"defaultFormId\":\"b5026868-1a5e-4266-82cd-c7bb11772cb2\",\"flowId\":\"e0ed9bf2-3e86-4a74-95b0-a4d67f956a59\",\"modalForm\":false,\"name\":\"DividerOne\",\"tabbedForm\":false,\"version\":1}}}");
		events.add(navigateToFlowEvent);
		form.setPublishedForm("{\"flowId\":\"e065e8e2-0780-4f29-9168-e69224cba19d\",\"formId\":\"49b94476-2e9b-4374-a458-933b5f0ef0b4\",\"formProperties\":{\"events\":[{\"event\":\"onautosubmit\",\"action\":{\"actionType\":\"NAVIGATE_TO_FORM\",\"properties\":{\"form\":{\"formId\":\"49b94476-2e9b-4374-a458-933b5f0ef0b4\",\"formName\":\"form111\",\"modalForm\":false,\"tabbedForm\":false}}}},{\"event\":\"onclick\",\"action\":{\"actionType\":\"NAVIGATE_TO_WORKFLOW\",\"properties\":{\"workflow\":{\"defaultFormId\":\"b5026868-1a5e-4266-82cd-c7bb11772cb2\",\"flowId\":\"e0ed9bf2-3e86-4a74-95b0-a4d67f956a59\",\"modalForm\":false,\"name\":\"DividerOne\",\"tabbedForm\":false,\"version\":1}}}}],\"menus\":[]}}".getBytes());
		form.setEvents(events);
		List<Tabs> tabs = new ArrayList<>();
		Tabs tab = new Tabs();
		tab.setLinkedFormId(UUID.randomUUID());
		tabs.add(tab);
		form.setTabs(tabs);
		field.setEvents(events);
		form.addField(field);

		return form;
	}

	private Form getFormPublish() {
		Flow flow = new Flow();
		flow.setUid(UUID.fromString("e0ed9bf2-3e86-4a74-95b0-a4d67f956a59"));
		Form form = new Form();
		form.setUid(UUID.fromString("47b23ea6-ff9b-48f9-ab77-f869398c95ad"));
		form.setPublished(true);
		byte[] publishdForm = "{}".getBytes();
		form.setPublishedForm(publishdForm);
		form.setPublished(true);
		form.setModalForm(true);
		form.setTabbedForm(true);
		form.setFlow(flow);
		List<Field> fields = new ArrayList<>();
		Field field = new Field();
		field.setChildFields(fields);
		List<Events> events = new ArrayList<Events>();
		Events event = new Events();
		event.setAction("\"{\\\"defaultFormId\\\":\\\"a032b1f8-22df-40d2-a494-9550e1c80c26\\\"}\"");
		Events event1 = new Events();
		event1.setAction("{\"formId\":\"a032b1f8-22df-40d2-a494-9550e1c80c26\"}");
		events.add(event);
		events.add(event1);
		form.setEvents(events);
		field.setEvents(events);
		form.addField(field);

		return form;
	}

	private List<FormDependency> getFormDependencies() {
		Flow flow = new Flow();
		flow.setUid(UUID.randomUUID());
		Form form = new Form();
		form.setUid(UUID.randomUUID());
		form.setFlow(flow);
		List<FormDependency> formDependencies = new ArrayList<>();
		FormDependency formDependency = new FormDependency();
		formDependency.setInboundFlow(flow);
		formDependency.setInboundFormId(UUID.randomUUID());
		formDependency.setOutboundFlowId(UUID.randomUUID());
		formDependency.setOutboundFormId(UUID.randomUUID());
		FormDependency formDependency1 = new FormDependency();
		formDependency1.setInboundFlow(flow);
		formDependency1.setInboundFormId(UUID.randomUUID());
		formDependency1.setOutboundFlowId(UUID.randomUUID());
		formDependency1.setOutboundFormId(UUID.randomUUID());
		flow.setFormDependencies(formDependencies);
		formDependencies.add(formDependency);
		formDependencies.add(formDependency1);
		return formDependencies;

	}
 
	public Flow getFlow() {
		Flow flow = new Flow();
		flow.setUid(UUID.randomUUID());
		flow.setDefaultFormId(UUID.randomUUID());
		return flow;
	}
}
