package com.jda.mobility.framework.extensions.transformation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.dto.CustomFieldObjDto;
import com.jda.mobility.framework.extensions.dto.DataDto;
import com.jda.mobility.framework.extensions.dto.EventsDto;
import com.jda.mobility.framework.extensions.dto.FieldObjDto;
import com.jda.mobility.framework.extensions.dto.FlowDto;
import com.jda.mobility.framework.extensions.dto.FormObjDto;
import com.jda.mobility.framework.extensions.dto.TabDto;
import com.jda.mobility.framework.extensions.dto.TranslationRequest;
import com.jda.mobility.framework.extensions.dto.ValuesDto;
import com.jda.mobility.framework.extensions.entity.CustomComponentMaster;
import com.jda.mobility.framework.extensions.entity.CustomField;
import com.jda.mobility.framework.extensions.entity.Data;
import com.jda.mobility.framework.extensions.entity.Events;
import com.jda.mobility.framework.extensions.entity.ExtendedEventsBase;
import com.jda.mobility.framework.extensions.entity.ExtendedFieldBase;
import com.jda.mobility.framework.extensions.entity.Field;
import com.jda.mobility.framework.extensions.entity.FieldValues;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.FormDependency;
import com.jda.mobility.framework.extensions.entity.FormDependent;
import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.entity.Tabs;
import com.jda.mobility.framework.extensions.model.DatePicker;
import com.jda.mobility.framework.extensions.model.Event;
import com.jda.mobility.framework.extensions.model.FieldComponent;
import com.jda.mobility.framework.extensions.model.FieldDependency;
import com.jda.mobility.framework.extensions.model.FormData;
import com.jda.mobility.framework.extensions.model.IconInfo;
import com.jda.mobility.framework.extensions.model.LabelDetails;
import com.jda.mobility.framework.extensions.model.Layer;
import com.jda.mobility.framework.extensions.model.Style;
import com.jda.mobility.framework.extensions.model.Validate;
import com.jda.mobility.framework.extensions.repository.CustomComponentMasterRepository;
import com.jda.mobility.framework.extensions.repository.DataRepository;
import com.jda.mobility.framework.extensions.repository.EventsRepository;
import com.jda.mobility.framework.extensions.repository.ExtendedFieldBaseRepository;
import com.jda.mobility.framework.extensions.repository.FieldRepository;
import com.jda.mobility.framework.extensions.repository.FieldValuesRepository;
import com.jda.mobility.framework.extensions.repository.FlowRepository;
import com.jda.mobility.framework.extensions.repository.FormCustomComponentRepository;
import com.jda.mobility.framework.extensions.repository.FormRepository;
import com.jda.mobility.framework.extensions.repository.ProductConfigRepository;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;
import com.jda.mobility.framework.extensions.repository.TabRepository;
import com.jda.mobility.framework.extensions.service.ProductPrepareService;
import com.jda.mobility.framework.extensions.service.impl.AbstractPrepareTest;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffCommonUtil;
import com.jda.mobility.framework.extensions.utils.FieldComparator;

@RunWith(SpringJUnit4ClassRunner.class)
public class FormTransformationTest extends AbstractPrepareTest {

	/** The field formTransformation of type FormTransformation */

	@InjectMocks
	private FormTransformation formTransformation;
	/** The field flowRepo of type FlowRepository */
	@Mock
	private FlowRepository flowRepo;

	@Mock
	private FormRepository formRepo;

	@Mock
	private FieldRepository fieldRepo;

	@Mock
	private ProductConfigRepository productConfigRepo;
	@Mock
	private ResourceBundleRepository resBundleRepo;

	@Mock
	private FieldComponentConverter fieldComponentConverter;

	@Mock
	private EventsRepository eventsRepo;

	@Mock
	private TabRepository tabRepository;

	@Mock
	private CustomComponentMasterRepository customComponentMasterRepo;

	@Mock
	private FormCustomComponentRepository formCustomComponentRepo;

	@Mock
	private CustomFormTransformation customFormTransformation;

	@Mock
	private DataRepository dataRepo;

	@Mock
	private FieldValuesRepository fieldValuesRepository;
	@Spy
	private FieldComparator fieldComparator;

	@Mock
	private ExtendedFieldBaseRepository extFieldRepo;

	@Mock
	private ProductPrepareService productPrepareService;

	@Mock
	private BffCommonUtil bffCommonUtil;

	@Mock
	private FieldValuesRepository valueRepo;

	@Test
	public void testConvertToFormObjDto() {
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(getProductConfig());
		FormObjDto formObjDto = formTransformation.convertToFormObjDto(getFormData());
		assertEquals("Pick Flow Form 1", formObjDto.getName());
	}

	private FormData getFormData() {
		FormData formData = new FormData();
		formData.setName("Pick Flow Form 1");
		List<FieldComponent> components = new ArrayList<>();
		FieldComponent component = new FieldComponent();
		components.add(component);
		List<FieldComponent> components1 = new ArrayList<>();
		FieldComponent component1 = new FieldComponent();

		List<FieldComponent> componentschild = new ArrayList<>();

		FieldComponent component2 = new FieldComponent();
		component2.setType("COLUMNS");
		FieldComponent component3 = new FieldComponent();
		component3.setType("DATAGRID");

		component2.setColumns(componentschild);
		component1.setType("COLUMN");
		component1.setNumberOfRows("3");
		component1.setComponents(componentschild);
		components1.add(component1);
		components1.add(component2);
		components1.add(component3);

		componentschild.add(component1);

		component.setComponents(components1);
		formData.setComponents(components);
		return formData;
	}

	@Test
	public void testConvertToFormEntity() {
		Events events = new Events();
		EventsDto eventsDto = EventsDto.builder().uid(UUID.randomUUID()).event("Test").action("data")
				.fieldId(UUID.randomUUID()).formId(UUID.randomUUID()).build();
		List<EventsDto> eventsDtoList = new ArrayList<>();
		eventsDtoList.add(eventsDto);
		Form form = new Form();
		events.setUid(eventsDto.getUid());
		events.setEvent("on click");
		List<Events> eventList = new ArrayList<>();
		eventList.add(events);
		form.setEvents(eventList);
		Flow flow = new Flow();
		List<FieldObjDto> fieldobjDtoList = new ArrayList<>();
		FormData formRequest = new FormData();
		FormObjDto formObjDto = new FormObjDto(formRequest, fieldobjDtoList, UUID.randomUUID());
		formObjDto.setEvents(eventsDtoList);
		Layer layer = new Layer();
		layer.setName("JDA");
		layer.setLevel(1);
		formObjDto.setLayer(layer);
		formObjDto.setFlowId(UUID.randomUUID());
		formObjDto.setName("Test");
		formObjDto.setDescription("FORM DESCRIPTION2");
		TabDto tabDto = TabDto.builder().linkedFormId(UUID.fromString("0ac2cd6d-6f9e-4f8a-963c-309261e39924"))
				.linkedFormName("Tabn").tabId(UUID.fromString("0ac2cd6d-6f9e-4f8a-963c-309261e39924")).tabName("tabd")
				.sequence(3).defaultForm(true).build();
		List<TabDto> tabList = new ArrayList<>();
		tabList.add(tabDto);
		formObjDto.setTabs(tabList);
		formObjDto.setFormId(UUID.randomUUID());
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setName("jda");
		roleMaster.setLevel(1);
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		productConfig.setRoleMaster(roleMaster);
		flow.setProductConfig(productConfig);
		form.setProductConfigId(UUID.randomUUID());
		form.setFlow(flow);
		Tabs tab = Tabs.builder().tabName("tabm").linkedFormId(UUID.fromString("0ac2cd6d-6f9e-4f8a-963c-309261e39924"))
				.linkedFormName("tab").sequence(3).isDefault(true).build();
		when(tabRepository.findById(UUID.fromString("0ac2cd6d-6f9e-4f8a-963c-309261e39924")))
				.thenReturn(Optional.of(tab));
		when(eventsRepo.findById(eventsDto.getUid())).thenReturn(Optional.of(events));
		when(formRepo.save(Mockito.any())).thenReturn(getForm());
		when(flowRepo.findById(formObjDto.getFlowId())).thenReturn(Optional.of(getFlow()));
		when(formRepo.findById(formObjDto.getFormId())).thenReturn(Optional.of(getForm()));
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(productConfig);
		form = formTransformation.convertToFormEntity(formObjDto, flow);
		assertEquals("FORM DESCRIPTION2", form.getDescription());
	}

	@Test
	public void testConvertToFormEntityForNewForm() {
		FormData formData = new FormData();
		formData.setName("FORM1");
		Flow flow = new Flow();
		FormObjDto formObjDto = new FormObjDto(formData, new ArrayList<>(), UUID.randomUUID());
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(getProductConfig());
		when(formRepo.save(Mockito.any())).thenReturn(getForm());
		Form form = formTransformation.convertToFormEntity(formObjDto, flow);
		assertEquals("FORM1", form.getName());
	}

	private ProductConfig getProductConfig() {
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setName("jda");
		roleMaster.setLevel(1);
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		productConfig.setRoleMaster(roleMaster);
		return productConfig;
	}

	@Test
	public void testConvertToFieldEntity() {
		Field field = createField();
		String formId = "a56c5647-b134-43f2-87a4-5e0d3c344ae1";
		CustomComponentMaster master = new CustomComponentMaster();
		master.setName("ccm");
		Data data = new Data();
		List<FieldValues> fieldValueList = new ArrayList<>();
		fieldValueList.add(new FieldValues());
		data.setField(new Field());
		CustomComponentMaster customMaster = new CustomComponentMaster();
		customMaster.setUid(UUID.fromString("007dfd94-0968-4db7-89e2-d3449957737"));
		when(valueRepo.findById(Mockito.any())).thenReturn(Optional.of(new FieldValues()));
		when(eventsRepo.findById(Mockito.any())).thenReturn(Optional.of(new Events()));
		when(formCustomComponentRepo.findByFormAndCustomComponentMaster(Mockito.any(), Mockito.any())).thenReturn(null);
		when(extFieldRepo.findById(Mockito.any())).thenReturn(Optional.of(createExtendedField()));
		when(fieldRepo.findById(Mockito.any())).thenReturn(Optional.of(createField()));
		when(dataRepo.findById(Mockito.any())).thenReturn(Optional.of(data));
		when(customComponentMasterRepo.findById(Mockito.any())).thenReturn(Optional.of(master));
		when(extFieldRepo.save(Mockito.any())).thenReturn(createExtendedField());
		when(fieldRepo.save(Mockito.any())).thenReturn(field);
		when(formRepo.findById(UUID.fromString(formId))).thenReturn(Optional.of(getForm()));
		when(fieldValuesRepository.save(Mockito.any())).thenReturn(new FieldValues());
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(getProductConfig());
		FieldObjDto fieldObjDto = createFieldObjDto1();
		fieldObjDto.setFieldId(null);
		Field responseField = formTransformation.convertToFieldEntity(fieldObjDto, getForm());
		assertEquals("textfield", responseField.getType());
	}

	private FieldObjDto createFieldObjDto1() {
		List<FieldObjDto> childFieldObjDtoList = new ArrayList<>();
		FieldObjDto childFieldObjDto = new FieldObjDto(createFieldComponent(),
				UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"), null, new ArrayList<>(), 1, UUID.randomUUID());
		childFieldObjDto.setLinkedComponentId(UUID.randomUUID());
		childFieldObjDtoList.add(childFieldObjDto);
		FieldObjDto fieldObjDto = new FieldObjDto(createFieldComponent(),
				UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"), null, childFieldObjDtoList, 1,
				UUID.randomUUID());
		fieldObjDto.setApiDataSource(new ObjectNode(new JsonNodeFactory(true), new HashMap<>()));
		fieldObjDto.setDefaultApiValue(new ObjectNode(new JsonNodeFactory(true), new HashMap<>()));
		fieldObjDto.setAutoCompleteApi(new ObjectNode(new JsonNodeFactory(true), new HashMap<>()));
		List<DataDto> data = new ArrayList<>();
		DataDto dataDto = new DataDto(UUID.randomUUID(), "datalabel", "dataval", UUID.randomUUID());
		data.add(dataDto);
		fieldObjDto.setData(data);
		List<EventsDto> events = new ArrayList<>();
		EventsDto eventsDto = EventsDto.builder().uid(UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"))
				.event("event-blur").action("gs1Form").fieldId(UUID.fromString("0ac2cd6d-6f9e-4f8a-963c-309261e39924"))
				.formId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360")).build();
		events.add(eventsDto);
		fieldObjDto.setEvents(events);
		List<ValuesDto> values = new ArrayList<>();
		ValuesDto ValuesDto = new ValuesDto("lbl", "val", UUID.randomUUID());
		values.add(ValuesDto);
		fieldObjDto.setValues(values);
		return fieldObjDto;
	}

	@Test
	public void testConvertToFieldEntity1() {
		Form form = getForm();
		List<Field> fields = new ArrayList<>();
		Field field = new Field();
		Field field1 = new Field();
		field1.setExtendedFromFieldId(UUID.randomUUID());
		field.setParentField(field1);
		field.setExtendedFromFieldId(UUID.randomUUID());
		fields.add(field);
		form.setFields(fields);
		FieldObjDto fieldObjDto = createFieldObjDto();
		fieldObjDto.setFieldId(UUID.randomUUID());
		when(extFieldRepo.findById(Mockito.any())).thenReturn(Optional.of(createExtendedField()));
		when(fieldRepo.findById(Mockito.any())).thenReturn(Optional.of(field));
		Field responseField = formTransformation.convertToFieldEntity(fieldObjDto, form);
		assertEquals("textfield", responseField.getType());
	}

	@Test
	public void testConvertToFieldDto() throws IOException {
		Field field1 = createField();
		List<Field> childfieldList = new ArrayList<>();
		Field childfield = createField();
		childfieldList.add(childfield);
		field1.setChildFields(childfieldList);
		// Field field = new Field();
		// field.setUid(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"));
		// field1.setParentField(field);
		Field field2 = createField();
		Field field3 = createField();
		Field field4 = createField();
		List<Field> fieldList = new ArrayList<>();
		fieldList.add(field1);
		fieldList.add(field2);
		fieldList.add(field3);
		fieldList.add(field4);
		List<FieldObjDto> response = formTransformation.convertToFieldDto(fieldList);
		assertTrue(response.size() > 0);
	}

	@Test
	public void testCreateFormData() throws JsonParseException, JsonMappingException, IOException {
		Form form = new Form();
		Flow flow = new Flow();
		List<FieldObjDto> fieldobjDtoList = new ArrayList<>();
		FormData formRequest = new FormData();
		formRequest.setDescription("FORM DESCRIPTION2");
		FormObjDto formObjDto = new FormObjDto(formRequest, fieldobjDtoList, UUID.randomUUID());
		Layer layer = new Layer();
		layer.setName("JDA");
		List<FieldObjDto> fieldObjdto = new ArrayList<>();
		FieldObjDto fieldobj1 = createFieldObjDto();
		fieldObjdto.add(fieldobj1);
		formObjDto.setFields(fieldObjdto);

		layer.setLevel(1);
		formObjDto.setLayer(layer);
		String gs1Form = "{\"TEST2\":1,\"TEST3\":\"true\",\"pk\":{\"0\":\"1\",\"id\":2},\"TEST4\":2.0,\"TEST1\":\"WMD1\"}";
		formObjDto.setGs1Form(gs1Form);
		formObjDto.setFlowId(UUID.randomUUID());
		formObjDto.setName("Test");
		formObjDto.setFlowDto(
				new FlowDto.FlowBuilder("flow1")
				.setVersion(1)
				.setPermissions(new ArrayList<String>())
				.build());

		formObjDto.setFormId(UUID.randomUUID());
		List<EventsDto> events = new ArrayList<>();
		EventsDto eventDto = EventsDto.builder().uid(UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"))
				.event("event-blur").action(gs1Form).fieldId(UUID.fromString("0ac2cd6d-6f9e-4f8a-963c-309261e39924"))
				.formId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360")).build();
		;
		events.add(eventDto);
		formObjDto.setEvents(events);
		List<TabDto> tabs = new ArrayList<>();
		TabDto tabDto = TabDto.builder().linkedFormId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"))
				.linkedFormName("tabbedform").tabId(UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"))
				.tabName("form1").sequence(1).defaultForm(false).build();
		tabs.add(tabDto);
		formObjDto.setTabs(tabs);
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setName("jda");
		roleMaster.setLevel(1);
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		productConfig.setRoleMaster(roleMaster);
		flow.setProductConfig(productConfig);
		form.setProductConfigId(UUID.randomUUID());
		form.setFlow(flow);

		when(fieldComponentConverter.createFieldComponent(Mockito.any())).thenReturn(createFieldComponent());
		ProductConfig prodConfig = new ProductConfig();
		RoleMaster rm = new RoleMaster();
		rm.setLevel(0);
		prodConfig.setRoleMaster(rm);
		when(productConfigRepo.findById(Mockito.any())).thenReturn(Optional.of(prodConfig));
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(getProductConfig());
		FormData response = formTransformation.createFormData(formObjDto);
		assertEquals("FORM DESCRIPTION2", response.getDescription());
	}

	@Test
	public void testCreateFieldObjDto() throws IOException {
		when(customFormTransformation.convertToFieldDto(Mockito.any())).thenReturn(getCustomFieldObjDto());
		FieldObjDto response = formTransformation.createFieldObjDto(createField(), new ArrayList<>());
		assertEquals("customContainer", response.getType());
	}

	@Test
	public void testCheckUniqueFormName() {
		List<Form> formList = new ArrayList<>();
		formList.add(getForm());
		FormData formData = new FormData();
		formData.setName("FORM1");
		formData.setFormId(UUID.randomUUID());
		boolean response = formTransformation.checkUniqueFormName(formData, formList);
		assertFalse(response);
	}

	@Test
	public void testSaveTabs() {
		Form form = getForm();
		FormObjDto formObjDto = new FormObjDto(form, null);
		List<TabDto> tabs = new ArrayList<>();
		TabDto tabDto = TabDto.builder().linkedFormId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"))
				.linkedFormName("tabbedform").tabId(UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"))
				.tabName("form1").sequence(1).defaultForm(false).build();
		tabs.add(tabDto);
		formObjDto.setTabs(tabs);
		when(tabRepository.findById(UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1")))
				.thenReturn(Optional.of(new Tabs()));
		formTransformation.saveTabs(formObjDto, form);
		assertTrue(true);
	}

	@Test
	public void testSaveTabs1() {
		Form form = new Form();
		List<Tabs> tabList = new ArrayList<>();
		Tabs tabs = new Tabs();
		tabList.add(tabs);
		form.setTabs(tabList);
		FormObjDto formObjDto = new FormObjDto(form, null);
		List<TabDto> tabDtoList = new ArrayList<>();
		TabDto tabDto = TabDto.builder().linkedFormId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"))
				.linkedFormName("tabbedform").tabId(UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"))
				.tabName("form1").sequence(1).defaultForm(false).build();
		tabDtoList.add(tabDto);
		formObjDto.setTabs(tabDtoList);
		when(tabRepository.findById(UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1")))
				.thenReturn(Optional.of(tabs));
		formTransformation.saveTabs(formObjDto, form);
		assertTrue(true);
	}

	@Test
	public void testSaveTabsTabidNull() {
		Form form = getForm();
		FormObjDto formObjDto = new FormObjDto(form, null);
		List<TabDto> tabs = new ArrayList<>();
		TabDto tabDto = TabDto.builder().linkedFormId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"))
				.linkedFormName("tabbedform").tabId(null).tabName("form1").sequence(1).defaultForm(false).build();
		tabs.add(tabDto);
		formObjDto.setTabs(tabs);
		when(tabRepository.findById(UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1")))
				.thenReturn(Optional.of(new Tabs()));
		formTransformation.saveTabs(formObjDto, form);
		assertTrue(true);
	}

	@Test
	public void testSaveEvents() {
		Form form = getForm();
		Events events = new Events();
		List<Events> eventList = new ArrayList<Events>();
		eventList.add(events);
		form.setEvents(eventList);
		FormObjDto formObjDto = new FormObjDto(form, null);
		List<EventsDto> eventDtoList = new ArrayList<EventsDto>();
		EventsDto eventsDto = EventsDto.builder().uid(UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"))
				.event("On click").action("event").fieldId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"))
				.formId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360")).build();
		eventDtoList.add(eventsDto);

		when(eventsRepo.findById(UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1")))
				.thenReturn(Optional.of(events));
		formObjDto.setEvents(eventDtoList);
		formTransformation.saveEvents(formObjDto, form);
		assertTrue(true);
	}

	@Test
	public void testSaveEvents1() {
		Form form = getForm();
		FormObjDto formObjDto = new FormObjDto(form, null);
		List<EventsDto> eventDtoList = new ArrayList<EventsDto>();
		EventsDto eventsDto = EventsDto.builder().uid(UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"))
				.event("On click").action("event").fieldId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"))
				.formId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360")).build();
		eventDtoList.add(eventsDto);

		when(eventsRepo.findById(UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1")))
				.thenReturn(Optional.of(new Events()));
		formObjDto.setEvents(eventDtoList);
		formTransformation.saveEvents(formObjDto, form);
		assertTrue(true);
	}

	@Test
	public void testSaveEventsEventsidNull() {
		Form form = getForm();
		Events events = new Events();
		List<Events> eventList = new ArrayList<Events>();
		eventList.add(events);
		form.setEvents(eventList);
		FormObjDto formObjDto = new FormObjDto(form, null);
		List<EventsDto> eventDtoList = new ArrayList<EventsDto>();
		EventsDto eventsDto = EventsDto.builder().uid(UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"))
				.event("On click").action("event").fieldId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"))
				.formId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360")).build();
		eventDtoList.add(eventsDto);

		when(eventsRepo.findById(UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1")))
				.thenReturn(Optional.of(events));
		formObjDto.setEvents(eventDtoList);
		formTransformation.saveEvents(formObjDto, form);
		assertTrue(true);
	}

	@Test
	public void testSaveEventsFormEventsNull() {
		Form form = getForm();
		FormObjDto formObjDto = new FormObjDto(form, null);
		List<EventsDto> eventDtoList = new ArrayList<EventsDto>();
		EventsDto eventsDto = EventsDto.builder().uid(null).event("On click").action("event")
				.fieldId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"))
				.formId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360")).build();
		eventDtoList.add(eventsDto);
		formObjDto.setEvents(eventDtoList);
		formTransformation.saveEvents(formObjDto, form);
		assertTrue(true);
	}

	private Flow getFlow() {
		Flow flow = new Flow();
		List<Form> formList = new ArrayList<>();
		Form form = new Form();
		form.setName("Form1");
		formList.add(form);
		flow.setForms(formList);
		ProductConfig productConfig = new ProductConfig();
		productConfig.setUid(UUID.randomUUID());
		flow.setProductConfig(productConfig);
		return flow;
	}

	private List<CustomFieldObjDto> getCustomFieldObjDto() {
		List<CustomFieldObjDto> customFieldObjDtoList = new ArrayList<>();
		CustomFieldObjDto customFieldObjDto = new CustomFieldObjDto(createFieldComponent(),
				UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"), new ArrayList<>(), 1, UUID.randomUUID());
		customFieldObjDtoList.add(customFieldObjDto);
		return customFieldObjDtoList;
	}

	private FieldObjDto createFieldObjDto() {
		List<FieldObjDto> childFieldObjDtoList = new ArrayList<>();
		childFieldObjDtoList.add(createChildFieldObjDto());
		FieldObjDto fieldObjDto = new FieldObjDto(createFieldComponent(),
				UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"), null, childFieldObjDtoList, 1,
				UUID.randomUUID());
		return fieldObjDto;
	}

	private FieldObjDto createChildFieldObjDto() {
		List<FieldObjDto> childFieldObjDtoList = new ArrayList<>();
		FieldObjDto childFieldObjDto = new FieldObjDto(createFieldComponent(),
				UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"), null, new ArrayList<>(), 1, UUID.randomUUID());
		childFieldObjDtoList.add(childFieldObjDto);
		FieldObjDto fieldObjDto = new FieldObjDto(createFieldComponent(),
				UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"), null, childFieldObjDtoList, 1,
				UUID.randomUUID());
		fieldObjDto.setFieldId(null);
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

	private Form getForm() {
		Form form = new Form();
		form.setUid(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"));
		form.setParentFormId(UUID.randomUUID());
		form.setDescription("FORM DESCRIPTION2");
		Field field = new Field();
		field.setForm(form);
		field.setUid(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"));
		CustomComponentMaster customComponentMaster = new CustomComponentMaster();
		customComponentMaster.setUid(UUID.randomUUID());
		ResourceBundle resourceBundle1 = new ResourceBundle();
		resourceBundle1.setUid(UUID.randomUUID());
		resourceBundle1.setLocale("");
		resourceBundle1.setRbkey("");
		resourceBundle1.setRbvalue("");
		ResourceBundle resourceBundle2 = new ResourceBundle();
		resourceBundle2.setUid(UUID.randomUUID());
		resourceBundle2.setLocale("");
		resourceBundle2.setRbkey("");
		resourceBundle2.setRbvalue("");
		Flow flow = getFlow();
		List<Form> forms = new ArrayList<Form>();
		form.setName("FORM1");
		forms.add(form);
		flow.setForms(forms);
		form.setFlow(flow);
		FormDependent formIndep = new FormDependent();
		formIndep.setUid(UUID.randomUUID());
		List<FormDependent> formInDepList = new ArrayList<>();
		formInDepList.add(formIndep);
		FormDependency formOutdep = new FormDependency();
		formOutdep.setUid(UUID.randomUUID());
		List<FormDependency> formOutDepList = new ArrayList<>();
		formOutDepList.add(formOutdep);
		field.setIconName("iconName");
		field.setIconCode("iconCode");
		return form;
	}

	private Field createField() {
		Field field = new Field();
		field.setUid(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"));
		field.setAlignment("left");
		field.setCreatedBy("SUPER");
		field.setCreationDate(new Timestamp(100000));
		field.setLastModifiedBy("SUPER");
		field.setLastModifiedDate(new Timestamp(100000));
		field.setForm(getForm());
		field.setProductConfigId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"));
		field.setKeys("keys");
		field.setLabel("label");
		field.setCustomFormat("");
		field.setButtonType("primary");
		field.setFormat("");
		field.setImageSource("https://cdn2.iconfinder.com/data/icons/pittogrammi/142/32-48.png");
		field.setStyle("style");
		field.setStyleFontType("small");
		field.setStyleFontSize("20");
		field.setStyleFontColor("red");
		field.setStyleBackgroundColor("black");
		field.setStyleFontWeight("20");
		field.setStyleWidth("20");
		field.setStyleHeight("20");
		field.setStylePadding("0,0,0,20");
		field.setStyleMargin("0,0,0,20");
		field.setInline(false);
		field.setIcon(false);
		field.setAutoCorrect(false);
		field.setCapitalization(false);
		field.setType("customContainer");
		field.setInput(false);
		field.setDefaultValue("");
		field.setTableView(false);
		field.setValueProperty("");
		field.setFontColor("red");
		field.setAllowInput(false);
		field.setEnableDate(false);
		field.setDatePickerMinDate(new Date());
		field.setDatePickerMaxDate(new Date());
		field.setValidateMin(0.01d);
		field.setValidateMax(0.01d);
		field.setValidateInteger("");
		field.setFieldDependencyShowCondition("");
		field.setFieldDependencyHideCondition("");
		field.setFieldDependencyEnableCondition("");
		field.setFieldDependencyDisableCondition("");
		field.setFieldDependencyRequiredCondition("");
		field.setFieldDependencyRequired(false);
		field.setFieldDependencyHidden(false);
		field.setFieldDependencyDisabled(false);
		field.setHideLabel(false);
		field.setCustomClass("");
		field.setMask(false);
		field.setAlwaysEnabled(false);
		field.setLazyLoad(false);
		field.setSelectValues("");
		field.setDisableLimit(false);
		field.setSort("");
		field.setReference(false);
		field.setRadius("10");
		field.setBackGroundColor("red");
		field.setWidth("20");
		field.setHeight("20");
		field.setMaxDate("");
		field.setMinDate("");
		field.setIconAlignment("right");
		field.setSequence(1);
		field.setDescription("Field Description");
		field.setData(new ArrayList<>());
		field.setEvents(new ArrayList<>());
		field.setValues(new ArrayList<>());
		field.setFieldDependencySetValue("");
		// field.setParentFieldId(UUID.randomUUID());
		field.setIconName("iconName");
		field.setIconCode("iconCode");
		List<Events> events = new ArrayList<>();
		Events event = new Events();
		events.add(event);
		event.setUid(UUID.fromString("af0b505b-2780-4f00-89c9-03bdada98555"));
		event.setAction("action");
		event.setEvent("event");
		field.setEvents(events);
		List<Data> dataList = new ArrayList<>();
		Data data = new Data();
		data.setUid(UUID.fromString("9f90dfba-2489-4e91-9476-294fd8e64a6b"));
		data.setDatalabel("datalabel");
		data.setDatavalue("datavalue");
		Field datafield = new Field();
		datafield.setUid(UUID.fromString("9f90dfba-2489-4e91-9476-294fd8e64a6b"));
		data.setField(datafield);
		dataList.add(data);
		field.setData(dataList);
		CustomComponentMaster linkedComponentId = new CustomComponentMaster();
		List<CustomField> fields = new ArrayList<CustomField>();
		CustomField customField = new CustomField();
		fields.add(customField);
		linkedComponentId.setFields(fields);
		field.setLinkedComponentId(UUID.randomUUID());
		field.setExtendedFromFieldId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"));
		return field;
	}

	private ExtendedFieldBase createExtendedField() {
		ExtendedFieldBase field = new ExtendedFieldBase();
		field.setUid(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"));
		field.setAlignment("left");
		field.setCreatedBy("SUPER");
		field.setCreationDate(new Timestamp(100000));
		field.setLastModifiedBy("SUPER");
		field.setLastModifiedDate(new Timestamp(100000));
		// field.setForm(getForm());

		field.setProductConfigId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"));
		field.setKeys("keys");
		field.setLabel("label");
		field.setCustomFormat("");
		field.setButtonType("primary");
		field.setFormat("");
		field.setImageSource("https://cdn2.iconfinder.com/data/icons/pittogrammi/142/32-48.png");
		field.setStyle("style");
		field.setStyleFontType("small");
		field.setStyleFontSize("20");
		field.setStyleFontColor("red");
		field.setStyleBackgroundColor("black");
		field.setStyleFontWeight("20");
		field.setStyleWidth("20");
		field.setStyleHeight("20");
		field.setStylePadding("0,0,0,20");
		field.setStyleMargin("0,0,0,20");
		field.setInline(false);
		field.setIcon(false);
		field.setAutoCorrect(false);
		field.setCapitalization(false);
		field.setType("customContainer");
		field.setInput(false);
		field.setDefaultValue("");
		field.setTableView(false);
		field.setValueProperty("");
		field.setFontColor("red");
		field.setAllowInput(false);
		field.setEnableDate(false);
		field.setDatePickerMinDate(new Date());
		field.setDatePickerMaxDate(new Date());
		field.setValidateMin(0.01d);
		field.setValidateMax(0.01d);
		field.setValidateInteger("");
		field.setFieldDependencyShowCondition("");
		field.setFieldDependencyHideCondition("");
		field.setFieldDependencyEnableCondition("");
		field.setFieldDependencyDisableCondition("");
		field.setFieldDependencyRequiredCondition("");
		field.setFieldDependencyRequired(false);
		field.setFieldDependencyHidden(false);
		field.setFieldDependencyDisabled(false);
		field.setHideLabel(false);
		field.setCustomClass("");
		field.setMask(false);
		field.setAlwaysEnabled(false);
		field.setLazyLoad(false);
		field.setSelectValues("");
		field.setDisableLimit(false);
		field.setSort("");
		field.setReference(false);
		field.setRadius("10");
		field.setBackGroundColor("red");
		field.setWidth("20");
		field.setHeight("20");
		field.setMaxDate("");
		field.setMinDate("");
		field.setIconAlignment("right");
		field.setSequence(1);
		field.setDescription("Field Description");

		field.setFieldDependencySetValue("");

		field.setIconName("iconName");
		field.setIconCode("iconCode");
		List<ExtendedEventsBase> events = new ArrayList<>();
		ExtendedEventsBase event = new ExtendedEventsBase();
		events.add(event);
		event.setUid(UUID.fromString("af0b505b-2780-4f00-89c9-03bdada98555"));
		event.setAction("action");
		event.setEvent("event");
		field.setExtendedEventsBase(events);
		List<Data> dataList = new ArrayList<>();
		Data data = new Data();
		data.setUid(UUID.fromString("9f90dfba-2489-4e91-9476-294fd8e64a6b"));
		data.setDatalabel("datalabel");
		data.setDatavalue("datavalue");
		Field datafield = new Field();
		datafield.setUid(UUID.fromString("9f90dfba-2489-4e91-9476-294fd8e64a6b"));
		data.setField(datafield);
		dataList.add(data);
		// field.setData(dataList);
		CustomComponentMaster linkedComponentId = new CustomComponentMaster();
		List<CustomField> fields = new ArrayList<CustomField>();
		CustomField customField = new CustomField();
		fields.add(customField);
		linkedComponentId.setFields(fields);
		field.setLinkedComponentId(UUID.randomUUID());
		field.setExtendedFromFieldId(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"));
		return field;
	}
}
