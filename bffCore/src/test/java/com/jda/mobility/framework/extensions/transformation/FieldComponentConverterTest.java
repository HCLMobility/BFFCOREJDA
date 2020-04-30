/**
 * 
 */
package com.jda.mobility.framework.extensions.transformation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
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
import com.jda.mobility.framework.extensions.dto.FieldObjDto;
import com.jda.mobility.framework.extensions.dto.TranslationRequest;
import com.jda.mobility.framework.extensions.model.Data;
import com.jda.mobility.framework.extensions.model.DatePicker;
import com.jda.mobility.framework.extensions.model.Event;
import com.jda.mobility.framework.extensions.model.FieldComponent;
import com.jda.mobility.framework.extensions.model.FieldDependency;
import com.jda.mobility.framework.extensions.model.IconInfo;
import com.jda.mobility.framework.extensions.model.LabelDetails;
import com.jda.mobility.framework.extensions.model.Style;
import com.jda.mobility.framework.extensions.model.Validate;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffCommonUtil;

/**
 * Junit test class for FieldComponentConverterTest
 * 
 * @author HCL Technologies Ltd.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class FieldComponentConverterTest {

	@InjectMocks
	private FieldComponentConverter fieldComponentConverter;

	@Mock
	private BffCommonUtil bffCommonUtil;

	/**
	 * @throws IOException
	 * 
	 */

	@Test
	public void testCreateFieldComponent() throws IOException {
		bffCommonUtil.getResourceBundle("1223");
		FieldComponent fieldComponent = fieldComponentConverter.createFieldComponent(createFieldObjDto());
		assertEquals("88759e5c-c917-48eb-b03b-81f8a6a6d75f", fieldComponent.getFieldId().toString());
		assertEquals("textField33", fieldComponent.getKey());
	}

	/**
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	* 
	*/
	@Test
	public void testConvertToFieldDtoObj() throws JsonMappingException, JsonProcessingException  {
		List<FieldObjDto> fieldObjDtoList = new ArrayList<>();
		fieldObjDtoList.add(new FieldObjDto(createFieldComponent(), UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"), null, new ArrayList<>(), 1,UUID.randomUUID()));
		String formId = "a56c5647-b134-43f2-87a4-5e0d3c344ae1";
		String customFormId = "187926e2-956f-4f54-b039-11aeb0721b38";
		int sequence = 1;
		FieldObjDto fieldObj = new FieldObjDto(createFieldComponent(), UUID.fromString(formId),
				UUID.fromString(customFormId), fieldObjDtoList,  sequence,UUID.randomUUID());
		assertEquals("a56c5647-b134-43f2-87a4-5e0d3c344ae1", fieldObj.getFormId().toString());
		assertEquals("187926e2-956f-4f54-b039-11aeb0721b38", fieldObj.getCustomComponentMasterId().toString());

	}
	
	@Test
	public void testConvertFieldObjDtoToFieldComponents() throws IOException {
		FieldComponent fieldComponent = fieldComponentConverter.convertFieldObjDtoToFieldComponents(createFieldObjDto());
		assertEquals(UUID.fromString("88759e5c-c917-48eb-b03b-81f8a6a6d75f"), fieldComponent.getFieldId());
	}
	
	@Test
	public void testConvertFieldComponentsToFieldObjDto() {
		FieldObjDto fieldObjDto  = fieldComponentConverter.convertFieldComponentsToFieldObjDto(getGridFieldComponent(), UUID.randomUUID(), UUID.randomUUID(), 0);
		assertEquals(BffAdminConstantsUtils.DATAGRID, fieldObjDto.getType());
		
	}
	
	@Test
	public void testConvertToColumnsFieldComponent() throws IOException {
		FieldComponent fieldComponent = fieldComponentConverter.convertToColumnsFieldComponent(getColumnLayoutFieldComponent());
		assertEquals(BffAdminConstantsUtils.COLUMN, fieldComponent.getType());
	}
	
	private FieldObjDto getColumnLayoutFieldComponent() {
		FieldComponent component = new FieldComponent();
		component.setType("textField");
		FieldObjDto childCustomFieldObjDto = new FieldObjDto(component, UUID.randomUUID(), null, new ArrayList<>(), 0,UUID.randomUUID());
		List<FieldObjDto> fieldObjDtoList = new ArrayList<>();
		fieldObjDtoList.add(childCustomFieldObjDto);
		
		FieldComponent columnComponent = new FieldComponent();
		columnComponent.setType(BffAdminConstantsUtils.COLUMNS);
		FieldObjDto columnChildFieldObjDto = new FieldObjDto(component, UUID.randomUUID(), null, fieldObjDtoList,0,UUID.randomUUID());
		fieldObjDtoList.clear();
		fieldObjDtoList.add(columnChildFieldObjDto);
		
		FieldComponent columnLayoutComponent = new FieldComponent();
		columnLayoutComponent.setType(BffAdminConstantsUtils.COLUMN);
		FieldObjDto columnsChildFieldObjDto = new FieldObjDto(columnLayoutComponent, UUID.randomUUID(), null, fieldObjDtoList,  0,UUID.randomUUID());
		return columnsChildFieldObjDto;
		
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
	private FieldObjDto createFieldObjDto() throws JsonMappingException, JsonProcessingException {
		List<FieldObjDto> childFieldObjDtoList = new ArrayList<>();
		FieldComponent fieldComponent = createFieldComponent();
		fieldComponent.setFieldId(UUID.fromString("88759e5c-c917-48eb-b03b-81f8a6a6d75f"));
		Data data = new Data();
		data.setFieldId(UUID.fromString("88759e5c-c917-48eb-b03b-81f8a6a6d75f"));
		LabelDetails labelDetails = new LabelDetails();
		List<LabelDetails> labelDetailsList = new ArrayList<>();
		labelDetailsList.add(labelDetails);
		data.setValues(labelDetailsList);
		fieldComponent.setData(data);
		childFieldObjDtoList.add(new FieldObjDto(createFieldComponent(), UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"), null, new ArrayList<>(), 1,UUID.randomUUID()));
		FieldObjDto fieldObjDto = new FieldObjDto(fieldComponent, UUID.fromString("a56c5647-b134-43f2-87a4-5e0d3c344ae1"), null, childFieldObjDtoList, 1,UUID.randomUUID());
		return fieldObjDto;
	}

	private FieldComponent createFieldComponent() throws JsonMappingException, JsonProcessingException {
		FieldComponent fieldcomp =  new FieldComponent();
		fieldcomp.setLabel(TranslationRequest.builder()
				.locale(BffAdminConstantsUtils.LOCALE)
				.rbkey("1000")
				.rbvalue("Test")
				.type("INTERNAL")
				.uid(UUID.randomUUID())
				.build());
		fieldcomp.setMask(false);
		fieldcomp.setTableView(false);
		fieldcomp.setAlwaysEnabled(false);
		fieldcomp.setType("textfield");
		fieldcomp.setInput(false);
		fieldcomp.setKey("textField33"); //
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
		fieldcomp.setDefaultValue(""); Validate
		validate = new Validate(); 
		validate.setMaxLength(0.01d);
		validate.setMinLength(0.01d);
		validate.setInteger("");
		fieldcomp.setValidate(validate); 
		FieldDependency fieldDependency = new FieldDependency();
		fieldcomp.setFieldDependency(fieldDependency);
		List<Event>	eventList = new ArrayList<>();
		Event event = new Event();
		event.setEventId(UUID.randomUUID());
		event.setEventName("Event Name");
		eventList.add(event);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode objectNode = (ObjectNode) mapper.readTree("{}");
		event.setAction(objectNode);
		fieldcomp.setEvents(eventList);
		fieldcomp.setFieldId(null);
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
		fieldcomp.setInline(false); LabelDetails value = new LabelDetails();
		value.setLabel(TranslationRequest.builder()
				.locale(BffAdminConstantsUtils.LOCALE)
				.rbkey("1000")
				.rbvalue("Test")
				.type("INTERNAL")
				.uid(UUID.randomUUID())
				.build());
		value.setValue("abc");
		List<LabelDetails> valueList = new ArrayList<>();
		valueList.add(value); 
		fieldcomp.setValues(valueList);
		fieldcomp.setValueProperty("value");
		fieldcomp.setLazyLoad(false);
		fieldcomp.setDescription(TranslationRequest.builder()
				.locale(BffAdminConstantsUtils.LOCALE)
				.rbkey("1000")
				.rbvalue("Test")
				.type("INTERNAL")
				.uid(UUID.randomUUID())
				.build());
		fieldcomp.setSelectValues("");
		fieldcomp.setDisableLimit(false); 
		fieldcomp.setSort("");
		fieldcomp.setReference(false);
		fieldcomp.setCustomFormat("HH:mm:ss");
		IconInfo iconInfo = new IconInfo();
		iconInfo.setIconName("iconName");
		iconInfo.setIconCode("iconCode");
		fieldcomp.setIconInfo(iconInfo);
		return	fieldcomp; 
	}

}
