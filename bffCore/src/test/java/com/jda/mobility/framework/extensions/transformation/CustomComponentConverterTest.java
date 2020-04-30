package com.jda.mobility.framework.extensions.transformation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.dto.CustomFieldObjDto;
import com.jda.mobility.framework.extensions.dto.TranslationRequest;
import com.jda.mobility.framework.extensions.dto.ValuesDto;
import com.jda.mobility.framework.extensions.entity.Field;
import com.jda.mobility.framework.extensions.model.DatePicker;
import com.jda.mobility.framework.extensions.model.DependencyValue;
import com.jda.mobility.framework.extensions.model.Disable;
import com.jda.mobility.framework.extensions.model.Enable;
import com.jda.mobility.framework.extensions.model.Event;
import com.jda.mobility.framework.extensions.model.FieldComponent;
import com.jda.mobility.framework.extensions.model.FieldDependency;
import com.jda.mobility.framework.extensions.model.Hide;
import com.jda.mobility.framework.extensions.model.IconInfo;
import com.jda.mobility.framework.extensions.model.LabelDetails;
import com.jda.mobility.framework.extensions.model.Required;
import com.jda.mobility.framework.extensions.model.Show;
import com.jda.mobility.framework.extensions.model.Style;
import com.jda.mobility.framework.extensions.model.Validate;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffCommonUtil;

@RunWith(SpringJUnit4ClassRunner.class)
public class CustomComponentConverterTest {
	@InjectMocks
	private CustomComponentConverter customComponentConverter;
	@Mock
	private SessionDetails sessionDetails;
	@Mock
	private BffCommonUtil bffCommonUtil;
	
	@Test
	public void testCreateCustomFieldComponent() throws IOException {		
		FieldComponent fieldComponent1 = getFieldComponent();
		CustomFieldObjDto fieldObjDto = new CustomFieldObjDto(fieldComponent1, UUID.randomUUID(),  new ArrayList<>(), 0, UUID.randomUUID());
		List<ValuesDto> values = new ArrayList<>();
		ValuesDto valuesDto = new ValuesDto(null, null, null);
		values.add(valuesDto);
		fieldObjDto.setValues(values);
	
		FieldComponent fieldComponent = customComponentConverter.createCustomFieldComponent(fieldObjDto,"");
		assertEquals(BffAdminConstantsUtils.CUSTOM_CONTAINER, fieldComponent.getType());
	}
	
	@Test
	public void testConvertToCustomFieldDtoObj() {
		FieldComponent fieldComponent1 = new FieldComponent();
		fieldComponent1.setType(BffAdminConstantsUtils.CUSTOM_CONTAINER);
		fieldComponent1.setDatePicker(new DatePicker());
		Style style = new Style();
		style.setFontType("");
		Validate validate = new Validate();
		FieldDependency fieldDependency = new FieldDependency();
		Show show = new Show();
		show.setCondition("");
		Disable disable = new Disable();
		disable.setCondition("");
		Enable enable = new Enable();
		enable.setCondition("");
		Hide hide = new Hide();
		hide.setCondition("");
		Required required = new Required();
		required.setCondition("");
		fieldDependency.setShow(show);
		fieldDependency.setDisable(disable);
		fieldDependency.setEnable(enable);
		fieldDependency.setHide(hide);
		fieldDependency.setRequiredReq(required);
		fieldDependency.setValues(new ArrayList<DependencyValue>());
		fieldComponent1.setStyle(style);
		fieldComponent1.setValidate(validate);
		fieldComponent1.setFieldDependency(fieldDependency);
		IconInfo iconInfo = new IconInfo();
		iconInfo.setIconName("iconName");
		iconInfo.setIconCode("iconCode");
		fieldComponent1.setIconInfo(iconInfo);
		List<Event> eventList = new ArrayList<>();
		eventList.add(new Event());
		fieldComponent1.setEvents(eventList);
		com.jda.mobility.framework.extensions.model.Data data = new com.jda.mobility.framework.extensions.model.Data();
		fieldComponent1.setData(data);
		//CustomFieldObjDto fieldObjDto = convertToCustomFieldDtoObj(fieldComponent1, UUID.randomUUID(),  new ArrayList<>(), UUID.randomUUID(), 0);
		CustomFieldObjDto fieldObjDto = new CustomFieldObjDto(fieldComponent1, UUID.randomUUID(),  new ArrayList<>(), 0,UUID.randomUUID());
		assertEquals(BffAdminConstantsUtils.CUSTOM_CONTAINER, fieldObjDto.getType());
	}
	
	@Test
	public void testConvertFieldComponentsToCustomFieldObjDto() {
		CustomFieldObjDto customFieldObjDto = customComponentConverter.convertFieldComponentsToCustomFieldObjDto(getGridFieldComponent(), UUID.randomUUID(), 1,UUID.randomUUID());
		assertEquals(BffAdminConstantsUtils.DATAGRID, customFieldObjDto.getType());
	}
	
	@Test
	public void testConvertFieldObjDtoToFieldComponents() throws IOException {
		FieldComponent fieldComponent = customComponentConverter.convertFieldObjDtoToFieldComponents(getCustomFieldObjDto(),"");
		assertEquals(BffAdminConstantsUtils.DATAGRID, fieldComponent.getType());
		
	}
	
	@Test
	public void testConvertToColumnsFieldComponent() throws IOException {
		FieldComponent fieldComponent = customComponentConverter.convertToColumnsFieldComponent(getColumnLayoutFieldComponent(),"");
		assertEquals(BffAdminConstantsUtils.COLUMN, fieldComponent.getType());
	}
	
	@Test
	public void testCreateCustomContainer() throws IOException {
		List<CustomFieldObjDto> childFieldObjDtoList = new ArrayList<>();
		childFieldObjDtoList.add(new CustomFieldObjDto(getGridFieldComponent(), UUID.randomUUID(), new ArrayList<>(), 1, UUID.randomUUID()));
		childFieldObjDtoList.add(new CustomFieldObjDto(getFieldComponent(), UUID.randomUUID(), new ArrayList<>(), 1, UUID.randomUUID()));
		FieldComponent columnFieldComponent = getFieldComponent();
		columnFieldComponent.setType(BffAdminConstantsUtils.COLUMNS);
		childFieldObjDtoList.add(new CustomFieldObjDto(columnFieldComponent, UUID.randomUUID(), new ArrayList<>(), 1, UUID.randomUUID()));
		CustomFieldObjDto customFieldObjDto = new CustomFieldObjDto(getFieldComponent(), UUID.randomUUID(), childFieldObjDtoList, 1, UUID.randomUUID());
		List<FieldComponent> fieldComponents = customComponentConverter.createCustomContainer(customFieldObjDto, BffAdminConstantsUtils.EMPTY_SPACES);
		assertEquals("Grid", fieldComponents.get(0).getKey());
	}
	
	@Test
	public void testSetNewKeys() throws JsonMappingException, JsonProcessingException {
		String jsonString = "{\"condition\": \"FIELD.textField1 == FIELD.textField1\"}";
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode objectNode = (ObjectNode) mapper.readTree(jsonString);
		ObjectNode resultNode = customComponentConverter.setNewKeys(objectNode, "customContainer_", "textField");
		assertEquals("FIELD.customContainer_textField1 == FIELD.customContainer_textField1", resultNode.get("condition").asText());
	}
	
	@Test
	public void testSetNewKeysForValuePicker() throws JsonMappingException, JsonProcessingException {
		String jsonString = "{\"condition\": \"FIELD.valuePicker == FIELD.valuePicker\"}";
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode objectNode = (ObjectNode) mapper.readTree(jsonString);
		ObjectNode resultNode = customComponentConverter.setNewKeys(objectNode, "customContainer_", "valuePicker");
		assertEquals("FIELD.customContainer_valuePicker == FIELD.customContainer_valuePicker", resultNode.get("condition").asText());
	}
	
	private FieldComponent getGridFieldComponent() {
		FieldComponent component = new FieldComponent();
		component.setFieldId(UUID.randomUUID());
		component.setType(BffAdminConstantsUtils.DATAGRID);
		component.setKey("Grid");
		FieldComponent childComponent = new FieldComponent();
		childComponent.setType("textField");
		List<FieldComponent> fieldComponentList = new ArrayList<>();
		fieldComponentList.add(childComponent);
		component.setComponents(fieldComponentList);
		return component;
		
	}
	
	private CustomFieldObjDto getColumnLayoutFieldComponent() {
		FieldComponent component = new FieldComponent();
		component.setType("textField");
		CustomFieldObjDto childCustomFieldObjDto = new CustomFieldObjDto(component, UUID.randomUUID(),  new ArrayList<>(),  0,UUID.randomUUID());
		List<CustomFieldObjDto> customFieldObjDtoList = new ArrayList<>();
		customFieldObjDtoList.add(childCustomFieldObjDto);
		
		FieldComponent columnComponent = new FieldComponent();
		columnComponent.setType(BffAdminConstantsUtils.COLUMNS);
		CustomFieldObjDto columnChildCustomFieldObjDto = new CustomFieldObjDto(component, UUID.randomUUID(),  customFieldObjDtoList,0,UUID.randomUUID());
		customFieldObjDtoList.clear();
		customFieldObjDtoList.add(columnChildCustomFieldObjDto);
		
		FieldComponent columnLayoutComponent = new FieldComponent();
		columnLayoutComponent.setType(BffAdminConstantsUtils.COLUMN);
		CustomFieldObjDto columnsChildCustomFieldObjDto = new CustomFieldObjDto(columnLayoutComponent, UUID.randomUUID(),  customFieldObjDtoList,  0, UUID.randomUUID());
		return columnsChildCustomFieldObjDto;		
	}
	
	private CustomFieldObjDto getCustomFieldObjDto() {
		FieldComponent childComponent = new FieldComponent();
		childComponent.setType("textField");
		CustomFieldObjDto childCustomFieldObjDto = new CustomFieldObjDto(childComponent, UUID.randomUUID(),  new ArrayList<>(),  0,UUID.randomUUID());
		List<CustomFieldObjDto> customFieldObjDtoList = new ArrayList<>();
		customFieldObjDtoList.add(childCustomFieldObjDto);
		CustomFieldObjDto customFieldObjDto = new CustomFieldObjDto(getGridFieldComponent(), UUID.randomUUID(), customFieldObjDtoList, 0,UUID.randomUUID());
		return customFieldObjDto;
	}
	
	private FieldComponent getFieldComponent() throws JsonMappingException, JsonProcessingException {
		FieldComponent fieldComponent1 = new FieldComponent();
		fieldComponent1.setType(BffAdminConstantsUtils.CUSTOM_CONTAINER);
		DatePicker datePicker = new DatePicker();
		datePicker.setMaxDate(new Date());
		datePicker.setMinDate(new Date());
		fieldComponent1.setDatePicker(datePicker);
		Style style = new Style();
		style.setFontType("Arabic");
		style.setFontSize("14");
		style.setFontColor("black");
		style.setBackgroundColor("red");
		style.setHeight("3.2");
		style.setPadding("paddng");
		style.setMargin("0.12");
		Validate validate = new Validate();
		validate.setInteger("1");
		validate.setMaxDate("10/03/2020");
		FieldDependency fieldDependency = new FieldDependency();
		Show show = new Show();
		show.setCondition("");
		Disable disable = new Disable();
		disable.setCondition("");
		Enable enable = new Enable();
		enable.setCondition("");
		Hide hide = new Hide();
		hide.setCondition("");
		Required required = new Required();
		required.setCondition("");
		fieldDependency.setShow(show);
		fieldDependency.setDisable(disable);
		fieldDependency.setEnable(enable);
		fieldDependency.setHide(hide);
		fieldDependency.setRequiredReq(required);
		fieldDependency.setValues(new ArrayList<DependencyValue>());
		fieldDependency.setDisabled(true);
		fieldDependency.setHidden(true);
		fieldDependency.setRequired(true);
		List<DependencyValue> values = new ArrayList<>();
		DependencyValue dependencyValue = new DependencyValue();
		dependencyValue.setValue("val1");
		dependencyValue.setValue("SET_VALUE_BREAK");
		values.add(dependencyValue);
		fieldDependency.setValues(values);
		fieldComponent1.setStyle(style);
		fieldComponent1.setValidate(validate);
		fieldComponent1.setFieldDependency(fieldDependency);
		fieldComponent1.setDatePicker(datePicker);
		fieldComponent1.setValidate(validate);
		fieldComponent1.setStyle(style);
		List<Event> eventList = new ArrayList<>();
		Event event = new Event();
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode objectNode = (ObjectNode) mapper.readTree("{}"); 
		event.setAction(objectNode);
		event.setEventName("eventName");
		eventList.add(event);
		com.jda.mobility.framework.extensions.model.Data data = new com.jda.mobility.framework.extensions.model.Data();
		data.setFieldId(UUID.randomUUID());
		LabelDetails labelDetails = new LabelDetails();
		labelDetails.setValueId(UUID.randomUUID());
		labelDetails.setLabel(TranslationRequest.builder()
				.locale(BffAdminConstantsUtils.LOCALE)
				.rbkey("1000")
				.rbvalue("Test")
				.type("INTERNAL")
				.uid(UUID.randomUUID())
				.build());
		labelDetails.setValue("value");
		List<LabelDetails> labelDetailsList = new ArrayList<>();
		labelDetailsList.add(labelDetails);
		data.setValues(labelDetailsList);
		fieldComponent1.setData(data);
		fieldComponent1.setEvents(eventList);
		IconInfo iconInfo = new IconInfo();
		iconInfo.setIconName("iconName");
		iconInfo.setIconCode("iconCode");
		fieldComponent1.setIconInfo(iconInfo);
		Field field= new Field();
		field.setFieldDependencyShowCondition("");
		field.setFieldDependencyHideCondition("");
		field.setFieldDependencyEnableCondition("");
		field.setFieldDependencyDisableCondition("");
		field.setFieldDependencyRequiredCondition("");
		field.setFieldDependencyRequired(false);
		field.setFieldDependencyHidden(false);
		field.setFieldDependencyDisabled(false);
		return fieldComponent1;
	}
	
}
