/**
 * 
 */
package com.jda.mobility.framework.extensions.transformation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.dto.CustomFieldObjDto;
import com.jda.mobility.framework.extensions.dto.CustomFormDto;
import com.jda.mobility.framework.extensions.dto.FormCustomComponentDto;
import com.jda.mobility.framework.extensions.dto.TranslationRequest;
import com.jda.mobility.framework.extensions.entity.CustomComponentMaster;
import com.jda.mobility.framework.extensions.entity.CustomField;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.FormCustomComponent;
import com.jda.mobility.framework.extensions.entity.FormDependency;
import com.jda.mobility.framework.extensions.entity.FormDependent;
import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.model.CustomFormData;
import com.jda.mobility.framework.extensions.model.Data;
import com.jda.mobility.framework.extensions.model.DatePicker;
import com.jda.mobility.framework.extensions.model.Event;
import com.jda.mobility.framework.extensions.model.FieldComponent;
import com.jda.mobility.framework.extensions.model.FieldDependency;
import com.jda.mobility.framework.extensions.model.FormCustomComponentType;
import com.jda.mobility.framework.extensions.model.IconInfo;
import com.jda.mobility.framework.extensions.model.LabelDetails;
import com.jda.mobility.framework.extensions.model.Style;
import com.jda.mobility.framework.extensions.model.Validate;
import com.jda.mobility.framework.extensions.repository.CustomComponentMasterRepository;
import com.jda.mobility.framework.extensions.repository.CustomDataRepository;
import com.jda.mobility.framework.extensions.repository.CustomEventsRepository;
import com.jda.mobility.framework.extensions.repository.CustomFieldRepository;
import com.jda.mobility.framework.extensions.repository.CustomFieldValuesRepository;
import com.jda.mobility.framework.extensions.repository.FormCustomComponentRepository;
import com.jda.mobility.framework.extensions.repository.FormRepository;
import com.jda.mobility.framework.extensions.repository.ProductConfigRepository;
import com.jda.mobility.framework.extensions.service.ProductPrepareService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.RequestType;
import com.jda.mobility.framework.extensions.utils.BffCommonUtil;

/**
 * @author V.Rama
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class CustomFormTransformationTest {

	@InjectMocks
	private CustomFormTransformation customFormTransformation;

	@Mock
	private CustomComponentConverter fieldComponentConverter;

	@Mock
	private CustomComponentMasterRepository customComponentMasterRepo;

	@Mock
	private FormCustomComponentRepository formCustomComponentRepo;

	@Mock
	private FormRepository formRepo;

	@Mock
	private CustomFieldRepository fieldRepo;

	@Mock
	private ProductConfigRepository productConfigRepo;
	
	@Mock
	private SessionDetails sessionDetails;
	
	@Mock
	private ProductPrepareService productPrepareService;
	
	@Mock
	private BffCommonUtil bffCommonUtil;
	
	@Mock
	private CustomDataRepository customDataRepo;
	
	@Mock
	private CustomEventsRepository customEventsRepo;
	
	@Mock
	private CustomFieldValuesRepository valueRepo;
	

	/**
	 * Test method for convertToCustomFormObjDto
	 */

	@Test
	public void testConvertToCustomFormObjDto() {
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(getProductConfig());
		CustomFormDto customFormDto = customFormTransformation.convertToCustomFormObjDto(createCustomFormData());
		assertEquals("9f90dfba-2489-4e91-9476-294fd8e64a6b", customFormDto.getCustomFormId().toString());

	}

	/**
	 * Test method for convertToCustomMasterEntity
	 */

	@Test
	
	public void testConvertToCustomMasterEntity() {
		CustomFormDto customFormDto = createCustomFormDto();
		customFormDto.setCustomFormId(UUID.randomUUID());
		CustomComponentMaster customComponentMaster = new CustomComponentMaster();
		customComponentMaster.setName(customFormDto.getName());
		customComponentMaster.setVisibility(customFormDto.isVisibility());
		customComponentMaster.setIsdisabled(customFormDto.isDisabled());
		when(sessionDetails.getPrincipalName()).thenReturn("SUPER");
		when(customComponentMasterRepo.save(Mockito.any())).thenReturn(customComponentMaster);
		when(customComponentMasterRepo.findById(Mockito.any())).thenReturn(Optional.of(customComponentMaster));
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(getProductConfig());
		CustomComponentMaster response = customFormTransformation.convertToCustomMasterEntity(customFormDto);
		assertEquals("Pick_flow_new", response.getName());
		assertEquals(false, response.isVisibility());
	}

	/**
	 * Test method for convertToFormCustomComponentEntity
	 */

	@Test
	public void testConvertToFormCustomComponentEntity() {
		FormCustomComponentDto formCustomComponentDto = new FormCustomComponentDto(UUID.fromString("0ac2cd6d-6f9e-4f8a-963c-309261e39924"),
				UUID.fromString("0ac2cd6d-6f9e-4f8a-963c-309261e39924"),
				UUID.fromString("9f90dfba-2489-4e91-9476-294fd8e64a6b"));
		when(formCustomComponentRepo.save(Mockito.any())).thenReturn(createFormCustomComponent());
		when(formCustomComponentRepo.findById(Mockito.any())).thenReturn(Optional.of(createFormCustomComponent()));
		CustomFormDto customFormDto = createCustomFormDto();
		CustomComponentMaster customComponentMaster = new CustomComponentMaster();
		customComponentMaster.setName(customFormDto.getName());
		customComponentMaster.setVisibility(customFormDto.isVisibility());
		customComponentMaster.setIsdisabled(customFormDto.isDisabled());
		when(formRepo.findById(Mockito.any())).thenReturn(Optional.of(getForm()));
		when(customComponentMasterRepo.findById(Mockito.any())).thenReturn(Optional.of(customComponentMaster));
		FormCustomComponent formCustomComponent = customFormTransformation.convertToFormCustomComponentEntity(
				formCustomComponentDto, UUID.fromString("0ac2cd6d-6f9e-4f8a-963c-309261e39924"));
		assertEquals(UUID.fromString("0ac2cd6d-6f9e-4f8a-963c-309261e39924"), formCustomComponent.getUid());
		
	}

	@Test
	public void testConvertToFieldEntity() {
		UUID customFormId = UUID.fromString("0ac2cd6d-6f9e-4f8a-963c-309261e39924");
		
		when(customComponentMasterRepo.findById(Mockito.any())).thenReturn(Optional.of(createCustomComponentMaster()));
		when(fieldRepo.save(Mockito.any())).thenReturn(createField());
		when(fieldRepo.findById(Mockito.any())).thenReturn(Optional.of(createField()));
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(getProductConfig());
		CustomField field = customFormTransformation.convertToFieldEntity(createFieldObjDto(), customFormId,
				RequestType.POST);
		assertEquals("primary", field.getButtonType());
		assertEquals("https://cdn2.iconfinder.com/data/icons/pittogrammi/142/32-48.png", field.getImageSource());
	}

	/**
	 * Test method for convertToFormCustomComponentDto
	 */

	@Test
	public void testConvertToFormCustomComponentDto() {
		List<FormCustomComponent> formCustomComponentList = new ArrayList<>();
		formCustomComponentList.add(createFormCustomComponent());
		List<FormCustomComponentDto> response = customFormTransformation
				.convertToFormCustomComponentDto(formCustomComponentList);
		assertEquals("0ac2cd6d-6f9e-4f8a-963c-309261e39924", response.get(0).getFormCusId().toString());

	}

	/**
	 * Test method for convertToFieldDto
	 * @throws IOException 
	 */

	@Test
	public void testConvertToFieldDto() throws IOException {
		CustomField field1 = createField();
		CustomField field2 = createField();
		field2.setParentFieldId(null);
		field2.setUid(field1.getParentFieldId());
		CustomField field3 = createField();
		field3.setParentFieldId(field1.getParentFieldId());
		CustomField field4 = createField();
		field4.setParentFieldId(null);
		List<CustomField> fieldList = new ArrayList<>();
		fieldList.add(field1);
		fieldList.add(field2);
		fieldList.add(field3);
		fieldList.add(field4);
		List<CustomFieldObjDto> response = customFormTransformation.convertToFieldDto(fieldList);
		assertTrue(response.size() > 0);
	}

	/**
	 * Test method for convertToCustomFormDto
	 */

	@Test
	public void testConvertToCustomFormDto() {
		CustomFieldObjDto fieldObjDto = createFieldObjDto();
		List<CustomFieldObjDto> fieldObjDtoList = new ArrayList<>();
		fieldObjDtoList.add(fieldObjDto);
		List<FormCustomComponentDto> formCustomComponentDtoList = new ArrayList<>();
		FormCustomComponentDto FormCustomComponentDto = createFormCustomComponentDto();
		formCustomComponentDtoList.add(FormCustomComponentDto);
		CustomFormDto customFormDto = customFormTransformation.convertToCustomFormDto(createCustomComponentMaster(),
				fieldObjDtoList, formCustomComponentDtoList);
		assertEquals("Pick_flow_new", customFormDto.getName());
	}

	@Test
	public void testCreateCustomFormData() throws IOException {
		when(fieldComponentConverter.createCustomFieldComponent(Mockito.any(),Mockito.any())).thenReturn(createFieldComponent());
		when(productPrepareService.getCurrentLayerProdConfigId()).thenReturn(getProductConfig());
		when(productConfigRepo.findById(Mockito.any())).thenReturn(Optional.of(getProductConfig()));
		CustomFormData customFormData = customFormTransformation.createCustomFormData(createCustomFormDto(),"");
		assertEquals("Pick_flow_new", customFormData.getName());
	}

	private CustomFormData createCustomFormData() {
		CustomFormData customFormRequest = new CustomFormData();
		customFormRequest.setCustomComponentId((UUID.fromString("9f90dfba-2489-4e91-9476-294fd8e64a6b")));
		customFormRequest.setName("Pick_flow_new");
		customFormRequest.setVisibility(false);
		customFormRequest.setDisabled(false);
		List<FieldComponent> fieldComponentList = new ArrayList<>();
		fieldComponentList.add(createCustomFieldComponent());
		fieldComponentList.add(getNestedColumnFieldComponent());
		FieldComponent gridComponenet = new FieldComponent();
		gridComponenet.setType(BffAdminConstantsUtils.DATAGRID);
		fieldComponentList.add(gridComponenet);
		customFormRequest.setComponents(fieldComponentList);
		List<FormCustomComponentType> formCustomComponentTypeList = new ArrayList<>();
		formCustomComponentTypeList.add(createFormCustomComponentType());
		customFormRequest.setFormCustomComponentTypes(formCustomComponentTypeList);
		return customFormRequest;
	}

	private CustomFormDto createCustomFormDto() {
		CustomFieldObjDto fieldObjDto = createFieldObjDto();
		List<CustomFieldObjDto> fieldObjDtoList = new ArrayList<>();
		fieldObjDtoList.add(fieldObjDto);
		List<FormCustomComponentDto> formCustomComponentDtoList = new ArrayList<>();
		FormCustomComponentDto FormCustomComponentDto = createFormCustomComponentDto();
		formCustomComponentDtoList.add(FormCustomComponentDto);
		CustomFormDto customFormDto = new CustomFormDto.CustomFormBuilder()
				.setName("Pick_flow_new")
				.build();
		customFormDto.setFields(fieldObjDtoList);
		customFormDto.setProductConfigId(UUID.randomUUID());
		customFormDto.setFormCustomComponentDto(formCustomComponentDtoList);
		return customFormDto;
	}

	private FormCustomComponentType createFormCustomComponentType() {
		FormCustomComponentType formCustomComponentType = new FormCustomComponentType();
		formCustomComponentType.setCustomFormId(UUID.fromString("9f90dfba-2489-4e91-9476-294fd8e64a6b"));
		formCustomComponentType.setFormCusId(UUID.fromString("0ac2cd6d-6f9e-4f8a-963c-309261e39924"));
		formCustomComponentType.setFormId(UUID.fromString("0ac2cd6d-6f9e-4f8a-963c-309261e39924"));
		return formCustomComponentType;
	}

	private FormCustomComponentDto createFormCustomComponentDto() {
		FormCustomComponentDto formCustomComponentDto = new FormCustomComponentDto(
				UUID.fromString("0ac2cd6d-6f9e-4f8a-963c-309261e39924"),
				UUID.fromString("0ac2cd6d-6f9e-4f8a-963c-309261e39924"),
				UUID.fromString("9f90dfba-2489-4e91-9476-294fd8e64a6b"));
		return formCustomComponentDto;
	}

	private FieldComponent createCustomFieldComponent() {
		FieldComponent fieldcomp = new FieldComponent();
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
		fieldcomp.setKey("textField33");
		fieldcomp.setHideLabel(false);
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
		fieldcomp.setInline(false);
		LabelDetails value = new LabelDetails();
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
		fieldcomp.setData(new Data());
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
		return fieldcomp;
	}

	private FieldComponent getNestedColumnFieldComponent() {
		List<FieldComponent> columnList = new ArrayList<>();
		FieldComponent column = new FieldComponent();
		column.setType(BffAdminConstantsUtils.COLUMN);
		column.setNumberOfRows("3");
		List<FieldComponent> columnsList = new ArrayList<>();
		columnsList.add(getColumnFieldComponent());
		column.setComponents(columnsList);
		columnList.add(column);
		
		FieldComponent columns = new FieldComponent();
		columns.setType(BffAdminConstantsUtils.COLUMNS);
		columns.setColumns(columnList);
		return columns;
	}
	private FieldComponent getColumnFieldComponent() {
		List<FieldComponent> columnList = new ArrayList<>();
		FieldComponent column = new FieldComponent();
		column.setType(BffAdminConstantsUtils.COLUMN);
		column.setNumberOfRows("3");
		columnList.add(column);
		
		FieldComponent columns = new FieldComponent();
		columns.setType(BffAdminConstantsUtils.COLUMNS);
		columns.setColumns(columnList);
		return columns;
	}
	private CustomFieldObjDto createFieldObjDto() {
		List<CustomFieldObjDto> childFieldObjDtoList = new ArrayList<>();
		childFieldObjDtoList.add(new CustomFieldObjDto(createFieldComponent(), UUID.randomUUID(), new ArrayList<>(), 1,UUID.randomUUID()));
		CustomFieldObjDto fieldObjDto = new CustomFieldObjDto(createFieldComponent(), UUID.randomUUID(), childFieldObjDtoList,1,UUID.randomUUID());
		return fieldObjDto;
	}

	private FormCustomComponent createFormCustomComponent() {
		FormCustomComponent formCustomComponent = new FormCustomComponent();
		formCustomComponent.setUid((UUID.fromString("0ac2cd6d-6f9e-4f8a-963c-309261e39924")));
		formCustomComponent.setCustomComponentMaster(createCustomComponentMaster());
		return formCustomComponent;
	}

	private CustomComponentMaster createCustomComponentMaster() {
		CustomFormDto customFormDto = createCustomFormDto();
		CustomComponentMaster customComponentMaster = new CustomComponentMaster();
		customComponentMaster.setName(customFormDto.getName());
		customComponentMaster.setVisibility(customFormDto.isVisibility());
		customComponentMaster.setIsdisabled(customFormDto.isDisabled());
		return customComponentMaster;
	}

	private Form getForm() {
		Form form = new Form();
		form.setUid(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"));
		form.setParentFormId(UUID.randomUUID());
		form.setDescription("FORM DESCRIPTION2");
		CustomField field = new CustomField();
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
		field.setCustomComponentMaster(customComponentMaster);
		Flow flow = new Flow();
		flow.setUid(UUID.randomUUID());
		List<Form> forms = new ArrayList<Form>();
		form.setName("str1");
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
		return form;
	}

	private CustomField createField() {
		CustomField field = new CustomField();
		field.setUid(UUID.fromString("d242effe-6f03-419e-9bff-17f81e4a5360"));
		field.setAlignment("left");
		field.setCreatedBy("SUPER");
		field.setCreationDate(new Timestamp(100000));
		field.setLastModifiedBy("SUPER");
		field.setLastModifiedDate(new Timestamp(100000));
		field.setCustomComponentMaster(getSampleCustomComponentMaster());

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
		field.setType("textfield");
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
		field.setParentFieldId(UUID.randomUUID());
		field.setIconName("iconName");
		field.setIconCode("iconCode");
		return field;
	}

	private CustomComponentMaster getSampleCustomComponentMaster() {
		CustomComponentMaster ccm = new CustomComponentMaster();
		ccm.setUid(UUID.randomUUID());
		ccm.setName("Test custom component");
		ccm.setIsdisabled(false);
		ccm.setVisibility(true);
		List<CustomField> fieldList = new ArrayList<>();
		CustomField field = new CustomField();
		field.setUid(UUID.randomUUID());
		Form form = new Form();
		form.setUid(UUID.randomUUID());

		List<FormCustomComponent> frmCCList = new ArrayList<FormCustomComponent>();
		FormCustomComponent fcc = new FormCustomComponent();
		fcc.setCustomComponentMaster(ccm);
		fcc.setUid(UUID.randomUUID());
		ccm.setFormCustomComponent(frmCCList);

		field.setAlignment("left");

		field.setCreatedBy("SYSTEM");
		field.setCustomComponentMaster(ccm);
		field.setUid(UUID.fromString("af0b505b-2780-4f00-89c9-03bdada98555"));		
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
		fieldList.add(field);
		ccm.setFields(fieldList);

		field.setAlignment("str");

		return ccm;
	}

	private FieldComponent createFieldComponent() { 
		FieldComponent fieldcomp = new FieldComponent(); 
		fieldcomp.setLabel(TranslationRequest.builder()
						.locale(BffAdminConstantsUtils.LOCALE)
						.rbkey("1000")
						.rbvalue("Test")
						.type("INTERNAL")
						.uid(UUID.randomUUID())
						.build()); 
		fieldcomp.setFieldId(UUID.randomUUID());
		fieldcomp.setMask(false);
		fieldcomp.setTableView(false);
		fieldcomp.setAlwaysEnabled(false);
		fieldcomp.setType(BffAdminConstantsUtils.CUSTOM_CONTAINER);
		fieldcomp.setInput(false);
		fieldcomp.setKey("textField33"); //
		fieldcomp.setComponents(new ArrayList<>());
		fieldcomp.setHideLabel(false); // fieldcomp.setCustomClass();
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
		Event event = new Event();
		eventList.add(event);
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
		fieldcomp.setInline(false);
		LabelDetails value = new LabelDetails();
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
		Data data = new Data();
		LabelDetails labelDetails = new LabelDetails();
		List<LabelDetails> labelDetailsList = new ArrayList<>();
		labelDetailsList.add(labelDetails);
		data.setValues(labelDetailsList);
		fieldcomp.setData(data);
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
		return fieldcomp;
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
}
