/**
 * 
 */
package com.jda.mobility.framework.extensions.transformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.dto.DataDto;
import com.jda.mobility.framework.extensions.dto.EventsDto;
import com.jda.mobility.framework.extensions.dto.FieldObjDto;
import com.jda.mobility.framework.extensions.dto.ValuesDto;
import com.jda.mobility.framework.extensions.model.CustomFormData;
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
import com.jda.mobility.framework.extensions.service.impl.CustomFormServiceImpl;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffCommonUtil;

/**
 * The class FieldComponentConverter.java Build Field component from field data
 * object.
 * 
 * @author HCL Technologies Ltd.
 */
@Component
public class FieldComponentConverter {
	private static final Logger LOGGER = LogManager.getLogger(FieldComponentConverter.class);

	@Autowired
	private BffCommonUtil bffCommonUtil;
	@Autowired
	private CustomFormServiceImpl customFormServiceImpl;

	/**
	 * @param fieldObjDto
	 * @return FieldComponent
	 * @throws IOException
	 */
	public FieldComponent createFieldComponent(FieldObjDto fieldObjDto) throws IOException {
		FieldComponent fieldComponent = new FieldComponent();
		fieldComponent.setLabel(bffCommonUtil.getResourceBundle(fieldObjDto.getLabel()));
		fieldComponent.setMask(fieldObjDto.isMask());
		fieldComponent.setTableView(fieldObjDto.isTableView());
		fieldComponent.setAlwaysEnabled(fieldObjDto.isAlwaysEnabled());
		fieldComponent.setType(fieldObjDto.getType());
		fieldComponent.setInput(fieldObjDto.isInput());
		fieldComponent.setKey(fieldObjDto.getKey());

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
		fieldComponent.setOffset(fieldObjDto.getOffset());
		fieldComponent.setPull(fieldObjDto.getPull());
		fieldComponent.setPush(fieldObjDto.getPush());
		fieldComponent.setHideOnChildrenHidden(fieldObjDto.isHideOnChildrenHidden());
		fieldComponent.setPlaceholder(bffCommonUtil.getResourceBundle(fieldObjDto.getPlaceHolder()));
		fieldComponent.setInputFieldType(fieldObjDto.getInputType());
		fieldComponent.setFontType(fieldObjDto.getFontType());
		fieldComponent.setFontSize(fieldObjDto.getFontSize());
		fieldComponent.setLineBreakMode(fieldObjDto.getLineBreakMode());
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
		fieldComponent.setPrefix(fieldObjDto.getPrefix());
		fieldComponent.setSuffix(fieldObjDto.getSuffix());
		fieldComponent.setListImageAlignment(fieldObjDto.getListImageAlignment());
		fieldComponent.setHeaderLabel(bffCommonUtil.getResourceBundle(fieldObjDto.getHeaderLabel()));
		fieldComponent.setApiDataSource(fieldObjDto.getApiDataSource());
		fieldComponent.setDefaultApiValue(fieldObjDto.getDefaultApiValue());
		fieldComponent.setDefaultStaticValue(fieldObjDto.getDefaultStaticValue());
		fieldComponent.setDefaultValueType(fieldObjDto.getDefaultValueType());
		fieldComponent.setTextAreaHeight(fieldObjDto.getTextAreaHeight());
		fieldComponent.setModifyStatus(fieldObjDto.isModifyStatus());
		fieldComponent.setBold(fieldObjDto.isBold());
		fieldComponent.setItalic(fieldObjDto.isItalic());
		fieldComponent.setUnderline(fieldObjDto.isUnderline());
		fieldComponent.setDecimalPlaces(fieldObjDto.getDecimalPlaces());
		fieldComponent.setCurrentDate(fieldObjDto.isCurrentDate());
		fieldComponent.setCurrentTime(fieldObjDto.isCurrentTime());
		fieldComponent.setAutoCompleteApi(fieldObjDto.getAutoCompleteApi());
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
		buildFieldEventElement(fieldObjDto, fieldComponent);
		buildFieldValidateElement(fieldObjDto, fieldComponent);
		buildFieldIconInfoElement(fieldObjDto, fieldComponent);
		buildFieldDependencyElement(fieldObjDto, fieldComponent);
		if (fieldObjDto.getType() != null && fieldObjDto.getType().contains(BffAdminConstantsUtils.CUSTOM_CONTAINER)
				&& fieldObjDto.getLinkedComponentId() != null) {
			String prefixKey = fieldObjDto.getKey() + BffAdminConstantsUtils.UNDERSCORE;
			CustomFormData customComponents = customFormServiceImpl
					.getCustomControlComponents(fieldObjDto.getLinkedComponentId(), prefixKey);
			fieldComponent.setComponents(customComponents.getComponents());
			fieldComponent.setCustomComponentName(customComponents.getName());
			fieldComponent.setCustomComponentDesc(customComponents.getDescription());
			fieldComponent.setCustomComponentId(customComponents.getCustomComponentId());
		}
		return fieldComponent;
	}

	/**
	 * @param fieldObjDto
	 * @param fieldComponent
	 * @throws IOException
	 */
	private void buildFieldDataElement(FieldObjDto fieldObjDto, FieldComponent fieldComponent) {
		Data data = new Data();
		if (fieldObjDto.getData() != null && !fieldObjDto.getData().isEmpty()) {
			List<LabelDetails> valueList = new ArrayList<>();
			for (DataDto dataDto : fieldObjDto.getData()) {
				LabelDetails value = new LabelDetails();
				value.setLabel(bffCommonUtil.getResourceBundle(dataDto.getDatalabel()));
				value.setValue(dataDto.getDatavalue());
				value.setValueId(dataDto.getUid());
				valueList.add(value);
			}
			data.setFieldId(fieldObjDto.getFieldId());
			data.setValues(valueList);
		}
		fieldComponent.setData(data);
	}

	/**
	 * @param fieldObjDto
	 * @param fieldComponent
	 */
	private void buildFieldEventElement(FieldObjDto fieldObjDto, FieldComponent fieldComponent) {
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
							ObjectNode action = (ObjectNode) objectMapper.readTree(eventsDto.getAction());
							event.setAction(action);
						}

					} catch (IOException exp) {
						LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, fieldObjDto.getFieldId());
						LOGGER.log(Level.ERROR,
								"Exception occured while reading Json Node tree from event stored in database for event: {} and field Label : {}",
								eventsDto.getEvent(), fieldObjDto.getLabel(),exp);
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
	private void buildFieldValidateElement(FieldObjDto fieldObjDto, FieldComponent fieldComponent) {
		Validate validate = null;
		if (fieldObjDto.getValidateInteger() != null || fieldObjDto.getValidateMinDate() != null
				|| fieldObjDto.getValidateMaxDate() != null
				|| fieldObjDto.getValidateMaxTime() != null || fieldObjDto.getMinRows() != null
				|| fieldObjDto.getMaxRows() != null || fieldObjDto.getValidateMaxLength() != null
				|| fieldObjDto.getValidateMinLength() != null || fieldObjDto.getValidateMax() != null
				|| fieldObjDto.getValidateMin() != null || fieldObjDto.getPattern() != null) {
			validate = new Validate();
			validate.setInteger(fieldObjDto.getValidateInteger());
			validate.setPattern(fieldObjDto.getPattern());
			validate.setMax(fieldObjDto.getValidateMax());
			validate.setMin(fieldObjDto.getValidateMin());
			validate.setMaxLength(fieldObjDto.getValidateMaxLength());
			validate.setMinLength(fieldObjDto.getValidateMinLength());
			validate.setMinDate(fieldObjDto.getValidateMinDate());
			validate.setMaxDate(fieldObjDto.getValidateMaxDate());
			validate.setMaxTime(fieldObjDto.getValidateMaxTime());
			validate.setMinTime(fieldObjDto.getValidateMinTime());
			validate.setMinRows(fieldObjDto.getMinRows());
			validate.setMaxRows(fieldObjDto.getMaxRows());
			fieldComponent.setValidate(validate);
		}
	}

	private void buildFieldIconInfoElement(FieldObjDto fieldObjDto, FieldComponent fieldComponent) {
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
	private void buildFieldDependencyElement(FieldObjDto fieldObjDto, FieldComponent fieldComponent) {
		Show show = new Show();
		show.setCondition(
				null != fieldObjDto.getFieldDependencyShowCondition() ? fieldObjDto.getFieldDependencyShowCondition()
						: BffAdminConstantsUtils.EMPTY_SPACES);
		Hide hide = new Hide();
		hide.setCondition(
				null != fieldObjDto.getFieldDependencyHideCondition() ? fieldObjDto.getFieldDependencyHideCondition()
						: BffAdminConstantsUtils.EMPTY_SPACES);

		Disable disable = new Disable();
		disable.setCondition(null != fieldObjDto.getFieldDependencyDisableCondition()
				? fieldObjDto.getFieldDependencyDisableCondition()
				: BffAdminConstantsUtils.EMPTY_SPACES);
		Enable enable = new Enable();
		enable.setCondition(null != fieldObjDto.getFieldDependencyEnableCondition()
				? fieldObjDto.getFieldDependencyEnableCondition()
				: BffAdminConstantsUtils.EMPTY_SPACES);
		Required required = new Required();
		required.setCondition(null != fieldObjDto.getFieldDependencyRequiredCondition()
				? fieldObjDto.getFieldDependencyRequiredCondition()
				: BffAdminConstantsUtils.EMPTY_SPACES);

		boolean depDisabled = fieldObjDto.isFieldDependencyDisabled();
		boolean depHidden = fieldObjDto.isFieldDependencyHidden();
		boolean depRequired = fieldObjDto.isFieldDependencyRequired();

		List<DependencyValue> depList = null;
		if (fieldObjDto.getFieldDependencyValues() != null) {
			depList = new ArrayList<>();
			String[] depValArray = fieldObjDto.getFieldDependencyValues()
					.split(BffAdminConstantsUtils.DEPENDENCY_OBJ_BREAK);
			for (String depVal : depValArray) {
				String[] arrayVal = depVal.split(BffAdminConstantsUtils.DEPENDENCY_BREAK);
				DependencyValue depValue = new DependencyValue();
				if (arrayVal != null && arrayVal.length > 0) {
					if (arrayVal.length >= 2) {
						depValue.setCondition(arrayVal[0]);
						depValue.setValue(arrayVal[1]);
					} else {
						depValue.setCondition(arrayVal[0]);
						depValue.setValue(BffAdminConstantsUtils.EMPTY_SPACES);
					}
					
				}else {
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
	private void buildStyleElement(FieldObjDto fieldObjDto, FieldComponent fieldComponent) {
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
	 * This method used to grid components to FieldComponent
	 * 
	 * @param childFieldObjDto
	 * @return FieldComponent
	 * @throws IOException
	 */
	public FieldComponent convertFieldObjDtoToFieldComponents(FieldObjDto childFieldObjDto) throws IOException {
		FieldComponent chilFieldComponent = createFieldComponent(childFieldObjDto);
		List<FieldComponent> gridComponentList = new ArrayList<>();
		for (FieldObjDto gridChildObj : childFieldObjDto.getChildFieldObjDtoList()) {
			gridComponentList.add(createFieldComponent(gridChildObj));
		}
		chilFieldComponent.setComponents(gridComponentList);
		return chilFieldComponent;
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
	public FieldObjDto convertFieldComponentsToFieldObjDto(FieldComponent gridFieldComponent, UUID formId,
			UUID productConfigId, int sequence) {
		int gridComponentSequence = 0;
		List<FieldObjDto> gridChildFieldObjDtoList = new ArrayList<>();
		if (gridFieldComponent.getComponents() != null && !gridFieldComponent.getComponents().isEmpty()) {
			for (FieldComponent gridComponent : gridFieldComponent.getComponents()) {
				gridChildFieldObjDtoList.add(new FieldObjDto(gridComponent, formId, null, new ArrayList<>(),
						gridComponentSequence++,productConfigId));
			}
		}
		return new FieldObjDto(gridFieldComponent, formId, null, gridChildFieldObjDtoList, sequence,productConfigId);
	}

	/**
	 * @param fieldObjDto
	 * @return FieldComponent
	 * @throws IOException
	 */
	public FieldComponent convertToColumnsFieldComponent(FieldObjDto fieldObjDto) throws IOException {
		FieldComponent columnFieldComponent = null;
		FieldComponent columnLayoutFieldComponent = createFieldComponent(fieldObjDto);
		if (columnLayoutFieldComponent.getColumns() == null)
			columnLayoutFieldComponent.setColumns(new ArrayList<>());
		for (FieldObjDto columnFieldObjDto : fieldObjDto.getChildFieldObjDtoList()) {
			columnFieldComponent = createFieldComponent(columnFieldObjDto);
			if (columnFieldObjDto.getChildFieldObjDtoList() != null) {
				for (FieldObjDto columnComponentFieldObjDto : columnFieldObjDto.getChildFieldObjDtoList()) {
					FieldComponent columnComponentFieldComponent = null;
					if (BffAdminConstantsUtils.COLUMNS.equalsIgnoreCase(columnComponentFieldObjDto.getType())) {
						columnComponentFieldComponent = convertToColumnsFieldComponent(columnComponentFieldObjDto);
					} else if(BffAdminConstantsUtils.DATAGRID.equalsIgnoreCase(columnComponentFieldObjDto.getType())
							|| BffAdminConstantsUtils.LISTVIEW.equalsIgnoreCase(columnComponentFieldObjDto.getType())){
						columnComponentFieldComponent = convertFieldObjDtoToFieldComponents(columnComponentFieldObjDto);
					}else{
						columnComponentFieldComponent = createFieldComponent(columnComponentFieldObjDto);
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

	private void buildValueElement(FieldObjDto fieldObjDto, FieldComponent fieldComponent) {
		if (!CollectionUtils.isEmpty(fieldObjDto.getValues())) {

			List<LabelDetails> valueList = new ArrayList<>();
			for (ValuesDto valueDto : fieldObjDto.getValues()) {
				com.jda.mobility.framework.extensions.model.LabelDetails value = new com.jda.mobility.framework.extensions.model.LabelDetails();
				value.setValueId(valueDto.getValueId());
				value.setLabel(bffCommonUtil.getResourceBundle(valueDto.getLabel()));
				value.setValue(valueDto.getValue());

				valueList.add(value);
			}
			fieldComponent.setValues(valueList);

		}
	}

}