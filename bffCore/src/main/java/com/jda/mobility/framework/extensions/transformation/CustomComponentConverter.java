package com.jda.mobility.framework.extensions.transformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.dto.CustomFieldObjDto;
import com.jda.mobility.framework.extensions.dto.DataDto;
import com.jda.mobility.framework.extensions.dto.EventsDto;
import com.jda.mobility.framework.extensions.dto.ValuesDto;
import com.jda.mobility.framework.extensions.model.Data;
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

/**
 * The class CustomComponentConverter.java Build custom field component from
 * custom component related custom fields object.
 * 
 * @author HCL Technologies Ltd.
 */
@Component
public class CustomComponentConverter {
	private static final Logger LOGGER = LogManager.getLogger(CustomComponentConverter.class);
	private static final Pattern PATTERN = Pattern.compile(BffAdminConstantsUtils.FIELD_PATTERN);

	@Autowired
	private BffCommonUtil bffCommonUtil;

	/**
	 * @param fieldObjDto
	 * @return FieldComponent
	 * @throws JsonProcessingException
	 */
	public FieldComponent createCustomFieldComponent(CustomFieldObjDto fieldObjDto, String parentKey)
			throws JsonProcessingException {
		FieldComponent fieldComponent = new FieldComponent();
		fieldComponent.setLabel(bffCommonUtil.getResourceBundle(fieldObjDto.getLabel()));
		fieldComponent.setMask(fieldObjDto.isMask());
		fieldComponent.setTableView(fieldObjDto.isTableView());
		fieldComponent.setAlwaysEnabled(fieldObjDto.isAlwaysEnabled());
		fieldComponent.setType(fieldObjDto.getType());
		fieldComponent.setInput(fieldObjDto.isInput());
		fieldComponent.setKey(parentKey + fieldObjDto.getKey());

		fieldComponent.setHideLabel(fieldObjDto.isHideLabel());
		fieldComponent.setCustomClass(fieldObjDto.getCustomClass());
		fieldComponent.setAlignment(fieldObjDto.getAlignment());

		fieldComponent.setIcon(fieldObjDto.isIcon());
		fieldComponent.setAutoCorrect(fieldObjDto.isAutoCorrect());
		fieldComponent.setCapitalization(fieldObjDto.isCapitalization());
		fieldComponent.setDefaultValue(fieldObjDto.getDefaultValue());

		fieldComponent.setFieldId(fieldObjDto.getFieldId());
		fieldComponent.setCustomComponentId(fieldObjDto.getCustomComponentMasterId());
		fieldComponent.setIconAlignment(fieldObjDto.getIconAlignment());
		fieldComponent.setImageSource(fieldObjDto.getImageSource());
		fieldComponent.setFormat(fieldObjDto.getFormat());
		fieldComponent.setAllowInput(fieldObjDto.isAllowInput());
		fieldComponent.setEnableDate(fieldObjDto.isEnableDate());

		fieldComponent.setMaxDate(fieldObjDto.getMaxDate());
		fieldComponent.setMinDate(fieldObjDto.getMinDate());
		fieldComponent.setFontColor(fieldObjDto.getFontColor());
		fieldComponent.setRadius(fieldObjDto.getRadius());
		fieldComponent.setWidth(fieldObjDto.getWidth());
		fieldComponent.setHeight(fieldObjDto.getHeight());
		fieldComponent.setButtonType(fieldObjDto.getButtonType());
		fieldComponent.setBackgroundColor(fieldObjDto.getBackGroundColor());
		fieldComponent.setInline(fieldObjDto.isInline());

		fieldComponent.setValueProperty(fieldObjDto.getValueProperty());
		fieldComponent.setLazyLoad(fieldObjDto.isLazyLoad());
		fieldComponent.setDescription(bffCommonUtil.getResourceBundle(fieldObjDto.getDescription()));
		fieldComponent.setSelectValues(fieldObjDto.getSelectValues());
		fieldComponent.setDisableLimit(fieldObjDto.isDisableLimit());
		fieldComponent.setSort(fieldObjDto.getSort());
		fieldComponent.setReference(fieldObjDto.isReference());
		fieldComponent.setCustomFormat(fieldObjDto.getCustomFormat());
		fieldComponent.setPlaceholder(bffCommonUtil.getResourceBundle(fieldObjDto.getPlaceHolder()));
		fieldComponent.setInputFieldType(fieldObjDto.getInputType());
		fieldComponent.setLineBreakMode(fieldObjDto.getLineBreakMode());
		fieldComponent.setFontType(fieldObjDto.getFontType());
		fieldComponent.setFontSize(fieldObjDto.getFontSize());
		fieldComponent.setDisableAddingRemovingRows(fieldObjDto.isDisableAddingRemovingRows());
		fieldComponent.setAddAnother(fieldObjDto.getAddAnother());
		fieldComponent.setAddAnotherPosition(fieldObjDto.getAddAnotherPosition());
		fieldComponent.setRemovePlacement(fieldObjDto.getRemovePlacement());
		fieldComponent.setStriped(fieldObjDto.isStriped());
		fieldComponent.setBordered(fieldObjDto.isBordered());
		fieldComponent.setSelected(fieldObjDto.isSelected());
		fieldComponent.setCondensed(fieldObjDto.isCondensed());
		fieldComponent.setAddSorting(fieldObjDto.isAddSorting());
		fieldComponent.setAddFilter(fieldObjDto.isAddFilter());
		fieldComponent.setStyleType(fieldObjDto.getStyleType());
		fieldComponent.setClearOnHide(fieldObjDto.isClearOnHide());
		fieldComponent.setNumberOfRows(fieldObjDto.getNumberOfRows());
		fieldComponent.setAddPagination(fieldObjDto.isAddPagination());
		fieldComponent.setOffset(fieldObjDto.getOffset());
		fieldComponent.setPush(fieldObjDto.getPush());
		fieldComponent.setPull(fieldComponent.getPull());
		fieldComponent.setHideOnChildrenHidden(fieldObjDto.isHideOnChildrenHidden());
		fieldComponent.setPrefix(fieldObjDto.getPrefix());
		fieldComponent.setSuffix(fieldObjDto.getSuffix());
		fieldComponent.setHeaderLabel(bffCommonUtil.getResourceBundle(fieldObjDto.getHeaderLabel()));
		fieldComponent.setApiDataSource(setNewKeys(fieldObjDto.getApiDataSource(), parentKey, fieldComponent.getType()));
		fieldComponent.setDefaultApiValue(setNewKeys(fieldObjDto.getDefaultApiValue(), parentKey, fieldComponent.getType()));
		fieldComponent.setDefaultStaticValue(fieldObjDto.getDefaultStaticValue());
		fieldComponent.setDefaultValueType(fieldObjDto.getDefaultValueType());
		fieldComponent.setTextAreaHeight(fieldObjDto.getTextAreaHeight());
		fieldComponent.setBold(fieldObjDto.isBold());
		fieldComponent.setItalic(fieldObjDto.isItalic());
		fieldComponent.setUnderline(fieldObjDto.isUnderline());
		fieldComponent.setDecimalPlaces(fieldObjDto.getDecimalPlaces());
		fieldComponent.setCurrentDate(fieldObjDto.isCurrentDate());
		fieldComponent.setCurrentTime(fieldObjDto.isCurrentTime());
		fieldComponent.setAutoCompleteApi(setNewKeys(fieldObjDto.getAutoCompleteApi(), parentKey, fieldComponent.getType()));
		fieldComponent.setButtonSize(fieldObjDto.getButtonSize());
		fieldComponent.setAutoAdjust(fieldObjDto.isAutoAdjust());
		fieldComponent.setHotKeyName(fieldObjDto.getHotKeyName());
		fieldComponent.setRows(fieldObjDto.getRows());
		buildStyleElement(fieldObjDto, fieldComponent);

		DatePicker datePicker = null;
		if (fieldObjDto.getDatePickerMaxDate() != null || fieldObjDto.getDatePickerMinDate() != null) {
			datePicker = new DatePicker();
			datePicker.setMaxDate(fieldObjDto.getDatePickerMaxDate());
			datePicker.setMinDate(fieldObjDto.getDatePickerMinDate());
			fieldComponent.setDatePicker(datePicker);
		}

		buildFieldDataElement(fieldObjDto, fieldComponent);
		buildValueElement(fieldObjDto, fieldComponent);
		buildFieldEventElement(fieldObjDto, fieldComponent, parentKey);
		buildFieldValidateElement(fieldObjDto, fieldComponent);
		buildFieldIconInfo(fieldObjDto, fieldComponent);
		buildFieldDependencyElement(fieldObjDto, fieldComponent, parentKey);
		return fieldComponent;
	}

	/**
	 * @param fieldObjDto
	 * @param fieldComponent
	 * @throws IOException
	 */
	private void buildFieldDataElement(CustomFieldObjDto fieldObjDto, FieldComponent fieldComponent) {
		if (fieldObjDto.getData() != null && !fieldObjDto.getData().isEmpty()) {
			List<LabelDetails> valueList = new ArrayList<>();
			Data data = new Data();
			for (DataDto dataDto : fieldObjDto.getData()) {
				LabelDetails value = new LabelDetails();
				value.setLabel(bffCommonUtil.getResourceBundle(dataDto.getDatalabel()));
				value.setValue(dataDto.getDatavalue());
				value.setValueId(dataDto.getUid());
				valueList.add(value);
			}
			data.setFieldId(fieldObjDto.getFieldId());
			data.setValues(valueList);
			fieldComponent.setData(data);
		}
	}

	/**
	 * @param fieldObjDto
	 * @param fieldComponent
	 */
	private void buildFieldEventElement(CustomFieldObjDto fieldObjDto, FieldComponent fieldComponent,String parentKey) {
		List<Event> eventList = null;
		if (fieldObjDto.getEvents() != null) {
			eventList = new ArrayList<>();
			for (EventsDto eventsDto : fieldObjDto.getEvents()) {
				if (eventsDto != null && eventsDto.getEvent() != null) {
					Event event = new Event();
					event.setEventId(eventsDto.getUid());
					event.setEventName(eventsDto.getEvent());

					ObjectMapper objectMapper = new ObjectMapper();
					try {
						if (eventsDto.getAction() != null) {
							String eventAction = setNewKeys(eventsDto.getAction(), parentKey, fieldComponent.getType());
							ObjectNode action = (ObjectNode) objectMapper.readTree(eventAction);
							event.setAction(action);
						}
					} catch (IOException exp) {
						LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, fieldObjDto.getFieldId());
						LOGGER.log(Level.ERROR,
								"Exception occured while reading Json Node tree from event stored in database for event: {} and field Label : {}",
								eventsDto.getEvent(), fieldObjDto.getLabel(), exp);
					} finally {
						eventList.add(event);
					}
				}
			}
		}
		fieldComponent.setEvents(eventList);
	}

	/**
	 * @param fieldObjDto
	 * @param fieldComponent
	 */
	private void buildFieldValidateElement(CustomFieldObjDto fieldObjDto, FieldComponent fieldComponent) {
		Validate validate = null;
		if (fieldObjDto.getValidateInteger() != null || fieldObjDto.getValidateMaxDate() != null
				|| fieldObjDto.getValidateMinDate() != null || fieldObjDto.getValidateMaxTime() != null
				|| fieldObjDto.getValidateMinTime() != null || fieldObjDto.getMinRows() != null
				|| fieldObjDto.getMaxRows() != null || fieldObjDto.getValidateMinLength() != null
				|| fieldObjDto.getValidateMaxLength() != null) {
			validate = new Validate();
			validate.setInteger(fieldObjDto.getValidateInteger());
			validate.setMaxLength(fieldObjDto.getValidateMaxLength());
			validate.setMinLength(fieldObjDto.getValidateMinLength());
			validate.setMax(fieldObjDto.getValidateMax());
			validate.setMin(fieldObjDto.getValidateMin());
			validate.setMaxDate(fieldObjDto.getValidateMaxDate());
			validate.setMinDate(fieldObjDto.getValidateMinDate());
			validate.setMaxTime(fieldObjDto.getValidateMaxTime());
			validate.setMinTime(fieldObjDto.getValidateMinTime());
			validate.setPattern(fieldObjDto.getPattern());
			validate.setMinRows(fieldObjDto.getMinRows());
			validate.setMaxRows(fieldObjDto.getMaxRows());
			fieldComponent.setValidate(validate);
		}
	}

	/**
	 * @param fieldObjDto
	 * @param fieldComponent
	 */
	private void buildFieldIconInfo(CustomFieldObjDto fieldObjDto, FieldComponent fieldComponent) {
		IconInfo iconInfo = null;
		if (fieldObjDto.getIconName() != null || fieldObjDto.getIconCode() != null) {
			iconInfo = new IconInfo();
			iconInfo.setIconName(fieldObjDto.getIconName());
			iconInfo.setIconCode(fieldObjDto.getIconCode());
			fieldComponent.setIconInfo(iconInfo);
		}
	}

	/**
	 * @param fieldObjDto
	 * @param fieldComponent
	 */
	private void buildFieldDependencyElement(CustomFieldObjDto fieldObjDto, FieldComponent fieldComponent,String parentKey) {
		Show show = new Show();
		show.setCondition(null != fieldObjDto.getFieldDependencyShowCondition()
				? setNewKeys(fieldObjDto.getFieldDependencyShowCondition(), parentKey, fieldComponent.getType())
				: BffAdminConstantsUtils.EMPTY_SPACES);
		Hide hide = new Hide();
		hide.setCondition(null != fieldObjDto.getFieldDependencyHideCondition()
				? setNewKeys(fieldObjDto.getFieldDependencyHideCondition(), parentKey, fieldComponent.getType())
				: BffAdminConstantsUtils.EMPTY_SPACES);

		Disable disable = new Disable();
		disable.setCondition(null != fieldObjDto.getFieldDependencyDisableCondition()
				? setNewKeys(fieldObjDto.getFieldDependencyDisableCondition(), parentKey, fieldComponent.getType())
				: BffAdminConstantsUtils.EMPTY_SPACES);
		Enable enable = new Enable();
		enable.setCondition(null != fieldObjDto.getFieldDependencyEnableCondition()
				? setNewKeys(fieldObjDto.getFieldDependencyEnableCondition(), parentKey, fieldComponent.getType())
				: BffAdminConstantsUtils.EMPTY_SPACES);
		Required required = new Required();
		required.setCondition(null != fieldObjDto.getFieldDependencyRequiredCondition()
				? setNewKeys(fieldObjDto.getFieldDependencyRequiredCondition(), parentKey, fieldComponent.getType())
				: BffAdminConstantsUtils.EMPTY_SPACES);

		boolean depDisabled = fieldObjDto.isFieldDependencyDisabled();
		boolean depHidden = fieldObjDto.isFieldDependencyHidden();
		boolean depRequired = fieldObjDto.isFieldDependencyRequired();

		List<DependencyValue> depList = null;
		if (fieldObjDto.getFieldDependencySetValue() != null) {
			depList = new ArrayList<>();
			String[] depValArray = fieldObjDto.getFieldDependencySetValue()
					.split(BffAdminConstantsUtils.DEPENDENCY_OBJ_BREAK);
			for (String depVal : depValArray) {
				String[] arrayVal = depVal.split(BffAdminConstantsUtils.DEPENDENCY_BREAK);
				DependencyValue depValue = new DependencyValue();
				if (arrayVal != null && arrayVal.length > 0) {
					if (arrayVal.length >= 2) {
						depValue.setCondition(setNewKeys(arrayVal[0], parentKey, fieldComponent.getType()));
						depValue.setValue(setNewKeys(arrayVal[1], parentKey, fieldComponent.getType()));
					} else {
						depValue.setCondition(setNewKeys(arrayVal[0], parentKey, fieldComponent.getType()));
						depValue.setValue(BffAdminConstantsUtils.EMPTY_SPACES);
					}
				} else {
					depValue.setCondition(BffAdminConstantsUtils.EMPTY_SPACES);
					depValue.setValue(BffAdminConstantsUtils.EMPTY_SPACES);
				}
				depList.add(depValue);
			}
		}

		FieldDependency fieldDependency = new FieldDependency();
		fieldDependency.setShow(show);
		fieldDependency.setHide(hide);
		fieldDependency.setDisable(disable);
		fieldDependency.setEnable(enable);
		fieldDependency.setRequiredReq(required);
		fieldDependency.setDisabled(depDisabled);
		fieldDependency.setHidden(depHidden);
		fieldDependency.setRequired(depRequired);
		fieldDependency.setValues(depList);
		fieldComponent.setFieldDependency(fieldDependency);
	}

	/**
	 * @param fieldObjDto
	 * @param fieldComponent
	 */
	private void buildStyleElement(CustomFieldObjDto fieldObjDto, FieldComponent fieldComponent) {
		boolean styleExists = false;
		String style = null;
		String fontType = null;
		if (fieldObjDto.getStyle() != null || fieldObjDto.getStyleFontType() != null) {
			styleExists = true;
			style = fieldObjDto.getStyle();
			fontType = fieldObjDto.getStyleFontType();
		}
		String fontSize = null;
		String fontColor = null;
		if (fieldObjDto.getStyleFontSize() != null || fieldObjDto.getStyleFontColor() != null) {
			styleExists = true;
			fontSize = fieldObjDto.getStyleFontSize();
			fontColor = fieldObjDto.getStyleFontColor();
		}
		String bgColor = null;
		String fontWeight = null;
		if (fieldObjDto.getStyleBackgroundColor() != null || fieldObjDto.getStyleFontWeight() != null) {
			styleExists = true;
			bgColor = fieldObjDto.getStyleBackgroundColor();
			fontWeight = fieldObjDto.getStyleFontWeight();
		}
		String width = null;
		String height = null;
		if (fieldObjDto.getStyleWidth() != null || fieldObjDto.getStyleHeight() != null) {
			styleExists = true;
			width = fieldObjDto.getStyleWidth();
			height = fieldObjDto.getStyleHeight();
		}
		String padding = null;
		String margin = null;
		if (fieldObjDto.getStylePadding() != null || fieldObjDto.getStyleMargin() != null) {
			styleExists = true;
			padding = fieldObjDto.getStylePadding();
			margin = fieldObjDto.getStyleMargin();
		}
		Style styleObj = null;
		if (styleExists) {
			styleObj = new Style();
			styleObj.setStyle(style);
			styleObj.setFontType(fontType);
			styleObj.setFontSize(fontSize);
			styleObj.setFontColor(fontColor);
			styleObj.setBackgroundColor(bgColor);
			styleObj.setFontWeight(fontWeight);
			styleObj.setWidth(width);
			styleObj.setHeight(height);
			styleObj.setPadding(padding);
			styleObj.setMargin(margin);
			fieldComponent.setStyle(styleObj);
		}
	}

	/**
	 * This method used to convert data grid FieldComponent to FieldObjDto
	 * 
	 * @param gridFieldComponent
	 * @param formId
	 * @param productConfigId
	 * @param sequence
	 * @return FieldObjDto
	 */
	public CustomFieldObjDto convertFieldComponentsToCustomFieldObjDto(FieldComponent gridFieldComponent, UUID formId,
			int sequence,UUID productConfigId) {
		int gridComponentSequence = 0;
		List<CustomFieldObjDto> gridChildFieldObjDtoList = new ArrayList<>();
		if (gridFieldComponent.getComponents() != null && !gridFieldComponent.getComponents().isEmpty()) {
			for (FieldComponent gridComponent : gridFieldComponent.getComponents()) {
				gridChildFieldObjDtoList.add(new CustomFieldObjDto(gridComponent, formId, new ArrayList<>(),
						gridComponentSequence++,productConfigId));
			}
		}
		return new CustomFieldObjDto(gridFieldComponent, formId, gridChildFieldObjDtoList, sequence,productConfigId);

	}

	/**
	 * @param childFieldObjDto
	 * @return FieldComponent
	 * @throws JsonProcessingException
	 */
	public FieldComponent convertFieldObjDtoToFieldComponents(CustomFieldObjDto childFieldObjDto, String parentKey)throws JsonProcessingException {
		FieldComponent chilFieldComponent = createCustomFieldComponent(childFieldObjDto, parentKey);
		List<FieldComponent> gridComponentList = new ArrayList<>();
		for (CustomFieldObjDto gridChildObj : childFieldObjDto.getChildFieldObjDtoList()) {
			gridComponentList.add(createCustomFieldComponent(gridChildObj,parentKey));
		}
		chilFieldComponent.setComponents(gridComponentList);
		return chilFieldComponent;
	}

	/**
	 * @param fieldObjDto
	 * @param fieldComponent
	 */
	private void buildValueElement(CustomFieldObjDto fieldObjDto, FieldComponent fieldComponent) {
		if (!CollectionUtils.isEmpty(fieldObjDto.getValues())) {
			List<LabelDetails> valueList = new ArrayList<>();
			for (ValuesDto valueDto : fieldObjDto.getValues()) {
				LabelDetails value = new LabelDetails();
				value.setValueId(valueDto.getValueId());
				value.setLabel(bffCommonUtil.getResourceBundle(valueDto.getLabel()));
				value.setValue(valueDto.getValue());
				valueList.add(value);
			}
			fieldComponent.setValues(valueList);

		}
	}

	/**
	 * @param fieldObjDto
	 * @return FieldComponent
	 * @throws IOException
	 */
	public FieldComponent convertToColumnsFieldComponent(CustomFieldObjDto fieldObjDto,String parentKey) throws IOException {
		FieldComponent columnFieldComponent = null;
		FieldComponent columnLayoutFieldComponent = createCustomFieldComponent(fieldObjDto,parentKey);
		if (columnLayoutFieldComponent.getColumns() == null)
			columnLayoutFieldComponent.setColumns(new ArrayList<>());
		for (CustomFieldObjDto columnFieldObjDto : fieldObjDto.getChildFieldObjDtoList()) {
			columnFieldComponent = createCustomFieldComponent(columnFieldObjDto,parentKey);
			if (columnFieldObjDto.getChildFieldObjDtoList() != null) {
				for (CustomFieldObjDto columnComponentFieldObjDto : columnFieldObjDto.getChildFieldObjDtoList()) {
					FieldComponent columnComponentFieldComponent = null;
					if (BffAdminConstantsUtils.COLUMNS.equalsIgnoreCase(columnComponentFieldObjDto.getType())) {
						columnComponentFieldComponent = convertToColumnsFieldComponent(columnComponentFieldObjDto,parentKey);
					} else if (BffAdminConstantsUtils.LISTVIEW.equalsIgnoreCase(columnComponentFieldObjDto.getType())
							|| BffAdminConstantsUtils.DATAGRID.equalsIgnoreCase(columnComponentFieldObjDto.getType())){
						columnComponentFieldComponent = convertFieldObjDtoToFieldComponents(columnComponentFieldObjDto,parentKey);
					}else{
						columnComponentFieldComponent = createCustomFieldComponent(columnComponentFieldObjDto,parentKey);
					}
					if (columnComponentFieldComponent != null && columnComponentFieldComponent.getType().contains(BffAdminConstantsUtils.CUSTOM_CONTAINER)) {
						List<FieldComponent> childOfCustom = createCustomContainer(columnComponentFieldObjDto, parentKey);
						columnComponentFieldComponent.setComponents(childOfCustom);
					}
					if (columnFieldComponent.getComponents() == null)
						columnFieldComponent.setComponents(new ArrayList<>());
					columnFieldComponent.getComponents().add(columnComponentFieldComponent);
				}
			}
			columnLayoutFieldComponent.getColumns().add(columnFieldComponent);
		}
		return columnLayoutFieldComponent;
	}
	

	public List<FieldComponent> createCustomContainer(CustomFieldObjDto fieldObjDto, String parentKey) throws IOException {
		List<FieldComponent> childOfCustomList = new ArrayList<>();
		if (fieldObjDto.getChildFieldObjDtoList() != null
				&& !fieldObjDto.getChildFieldObjDtoList().isEmpty()) {
			FieldComponent childOfCustom = null;
			for (CustomFieldObjDto childCustom : fieldObjDto.getChildFieldObjDtoList()) {
				if ((BffAdminConstantsUtils.DATAGRID.equalsIgnoreCase(childCustom.getType())
						|| BffAdminConstantsUtils.LISTVIEW.equalsIgnoreCase(childCustom.getType()))
						&& childCustom.getChildFieldObjDtoList() != null) {
					childOfCustom = convertFieldObjDtoToFieldComponents(childCustom, parentKey);
				} else if (BffAdminConstantsUtils.COLUMNS.equalsIgnoreCase(childCustom.getType())
						&& childCustom.getChildFieldObjDtoList() != null) {
					childOfCustom = convertToColumnsFieldComponent(childCustom, parentKey);
				} else {
					childOfCustom = createCustomFieldComponent(childCustom, parentKey);
				}
				
				if (childOfCustom != null
						&& childOfCustom.getType().contains(BffAdminConstantsUtils.CUSTOM_CONTAINER)) {
					String parentChildKey = parentKey.isEmpty() ?  BffAdminConstantsUtils.EMPTY :  childOfCustom.getKey()+ BffAdminConstantsUtils.UNDERSCORE;
					List<FieldComponent> childOfChildCustom = createCustomContainer(childCustom, parentChildKey);
					childOfCustom.setComponents(childOfChildCustom);
				}
				childOfCustomList.add(childOfCustom);
			}
		}
		return childOfCustomList;
	}
	
	public ObjectNode setNewKeys(ObjectNode input, String parentKey, String type) throws JsonProcessingException {
		if (parentKey.isEmpty() || input == null) {
			return input;
		} else {
			ObjectMapper mapper = new ObjectMapper();
			String output = setNewKeys(input.toString(), parentKey, type);
			return (ObjectNode) mapper.readTree(output);
		}
	}

	public String setNewKeys(String input, String parentKey, String type) {
		if (!parentKey.isEmpty() && input.contains(BffAdminConstantsUtils.FIELD)) {
			// To Handle Value Picker
			if (type.equalsIgnoreCase("valuePicker")) {
				input = patternCheckerForValuePicker(input, parentKey);
			}
			// To Handle other type
			else {
				input = patternChecker(input, parentKey);
			}
		}
		return input;

	}

	private static String patternCheckerForValuePicker(String input, String parentKey) {
		final Matcher matcher = PATTERN.matcher(input);

		while (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				StringBuilder tempStr = new StringBuilder(BffAdminConstantsUtils.EMPTY);
				String[] splitArr = matcher.group(i).split("\\.");
				if (splitArr.length > 0) {
					for (int j = 0; j < splitArr.length; j++) {
						String token = splitArr[j];
						// To Skip - label, value , img after valuePicket - Ex: FIELD.valuePicker1[].label1
						if (!BffAdminConstantsUtils.FIELD.equals(token) && j <= 1) {
							tempStr.append(parentKey);
						}
						tempStr.append(token + BffAdminConstantsUtils.PERIOD);
					}
					input = input.replaceAll(Pattern.quote(matcher.group(i)),
							tempStr.substring(0, tempStr.toString().length() - 1));
				}
			}
		}
		return input;
	}

	private static String patternChecker(String input, String parentKey) {
		final Matcher matcher = PATTERN.matcher(input);

		while (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				StringBuilder tempStr = new StringBuilder(BffAdminConstantsUtils.EMPTY);
				StringTokenizer tokenizer = new StringTokenizer(matcher.group(i), BffAdminConstantsUtils.PERIOD);
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
					if (!BffAdminConstantsUtils.FIELD.equals(token)) {
						tempStr.append(parentKey);
					}
					tempStr.append(token + BffAdminConstantsUtils.PERIOD);
				}

				input = input.replaceAll(Pattern.quote(matcher.group(i)),
						tempStr.substring(0, tempStr.toString().length() - 1));
			}
		}
		return input;
	}
}
