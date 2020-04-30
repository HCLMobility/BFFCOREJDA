/**
 * 
 */
package com.jda.mobility.framework.extensions.dto;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.entity.Events;
import com.jda.mobility.framework.extensions.entity.Field;
import com.jda.mobility.framework.extensions.entity.FieldValues;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.model.DependencyValue;
import com.jda.mobility.framework.extensions.model.Event;
import com.jda.mobility.framework.extensions.model.FieldComponent;
import com.jda.mobility.framework.extensions.model.FieldDependency;
import com.jda.mobility.framework.extensions.model.LabelDetails;
import com.jda.mobility.framework.extensions.model.Style;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffUtils;

import lombok.Data;

/**
 * The class FieldRequestDto.java HCL Technologies Ltd.
 */
@Data
public final class FieldObjDto implements Serializable, Comparable<FieldObjDto> {

	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 1168348776795918809L;
	/** The field fieldId of type String */
	private UUID fieldId;

	private UUID formId;
	/** The field customComponentMasterId of type UUID */
	private UUID customComponentMasterId;
	private int sequence;
	private String key;
	private String label;
	private String customFormat;
	private String buttonType;
	private String format;
	private String imageSource;
	private String alignment;
	private String style;
	private String styleFontType;
	private String styleFontSize;
	private String styleFontColor;
	private String styleBackgroundColor;
	private String styleFontWeight;
	private String styleWidth;
	private String styleHeight;
	private String stylePadding;
	private String styleMargin;
	private boolean inline;
	private boolean icon;
	private boolean autoCorrect;
	private boolean capitalization;
	private String type;
	private boolean input;
	private String defaultValue;
	private boolean tableView;
	private String valueProperty;
	private String fontColor;
	private boolean allowInput;
	private boolean enableDate;
	private Date datePickerMinDate;
	private Date datePickerMaxDate;
	private String validateInteger;
	private String fieldDependencyShowCondition;
	private String fieldDependencyHideCondition;
	private String fieldDependencyEnableCondition;
	private String fieldDependencyDisableCondition;
	private String fieldDependencyRequiredCondition;
	private String fieldDependencyValues;
	private boolean fieldDependencyRequired;
	private boolean fieldDependencyHidden;
	private boolean fieldDependencyDisabled;
	private boolean hideLabel;
	private String customClass;
	private boolean mask;
	private boolean alwaysEnabled;
	private boolean lazyLoad;
	private String description;
	private String selectValues;
	private boolean disableLimit;
	private String sort;
	private boolean reference;
	private String radius;
	private String backGroundColor;
	private String width;
	private String height;
	private String maxDate;
	private String minDate;
	private String iconAlignment;
	private List<EventsDto> events;
	private List<DataDto> data;
	private UUID linkedComponentId;

	private List<FieldObjDto> childFieldObjDtoList;
	private String offset;
	private String push;
	private String pull;
	private boolean hideOnChildrenHidden;
	private String placeHolder;
	private String inputType;
	private String pattern;
	private String iconName;
	private String iconCode;
	private String fontType;
	private String fontSize;
	private String lineBreakMode;
	private boolean disableAddingRemovingRows;
	private String addAnother;
	private String addAnotherPosition;
	private String removePlacement;
	private boolean striped;
	private boolean bordered;
	private boolean selected;
	private boolean condensed;
	private boolean addSorting;
	private boolean addFilter;
	private String styleType;
	private boolean clearOnHide;
	private boolean addPagination;
	private String numberOfRows;
	private String prefix;
	private String suffix;
	private String headerLabel;
	private List<ValuesDto> values;
	private String listImageAlignment;
	private int rows;
	private ObjectNode defaultApiValue;
	private ObjectNode apiDataSource;
	private String defaultValueType;
	private String defaultStaticValue;
	private Double validateMin;
	private Double validateMax;
	private Double validateMinLength;
	private Double validateMaxLength;
	private String validateMinDate;
	private String validateMaxDate;
	private String validateMinTime;
	private String validateMaxTime;
	private String minRows;
	private String maxRows;
	private boolean modifyStatus;
	private int textAreaHeight;
	private boolean bold;
	private boolean italic;
	private boolean underline;
	private String decimalPlaces;
	private boolean currentDate;
	private boolean currentTime;
	private ObjectNode autoCompleteApi;
	private UUID productConfigId;
	private String buttonSize;
	private boolean autoAdjust;
	private String hotKeyName;

	/**
	 * @param fieldComponent
	 * @param formId
	 * @param customFormId
	 * @param childFieldObjDtoList
	 * @param sequence
	 */
	public FieldObjDto(FieldComponent fieldComponent, UUID formId, UUID customFormId,
			List<FieldObjDto> childFieldObjDtoList, int sequence, UUID productConfigId) {
		super();

		String styleOfFieldComponent = null;
		String styleFontTypeOfFieldComponent = null;
		String styleFontSizeOfFieldComponent = null;
		String styleFontColorOfFieldComponent = null;
		String styleBackgroundColorOfFieldComponent = null;
		String styleFontWeightOfFieldComponent = null;
		String styleWidthOfFieldComponent = null;
		String styleHeightOfFieldComponent = null;
		String stylePaddingOfFieldComponent = null;
		String styleMarginOfFieldComponent = null;
		Style styleObj = fieldComponent.getStyle();
		if (!ObjectUtils.isEmpty(styleObj)) {

			styleOfFieldComponent = styleObj.getStyle();
			styleFontTypeOfFieldComponent = styleObj.getFontType();
			styleFontSizeOfFieldComponent = styleObj.getFontSize();
			styleFontColorOfFieldComponent = styleObj.getFontColor();
			styleBackgroundColorOfFieldComponent = styleObj.getBackgroundColor();
			styleFontWeightOfFieldComponent = styleObj.getFontWeight();
			styleWidthOfFieldComponent = styleObj.getWidth();
			styleHeightOfFieldComponent = styleObj.getHeight();
			stylePaddingOfFieldComponent = styleObj.getPadding();
			styleMarginOfFieldComponent = styleObj.getMargin();
		}
		Date datePickerMinDateOfFieldComponent = null;
		Date datePickerMaxDateOfFieldComponent = null;
		if (!ObjectUtils.isEmpty(fieldComponent.getDatePicker())) {
			if(!ObjectUtils.isEmpty(fieldComponent.getDatePicker().getMinDate())) {
				datePickerMinDateOfFieldComponent = (Date) fieldComponent.getDatePicker().getMinDate();
			}
			if(!ObjectUtils.isEmpty(fieldComponent.getDatePicker().getMaxDate())) {
				datePickerMaxDateOfFieldComponent = (Date) fieldComponent.getDatePicker().getMaxDate();
			}			
		}
		
		String validateIntegerOfFieldComponent = null;
		String patternOfFieldComponent = null;
		Double validateMinOfFieldComponent = null;
		Double validateMaxOfFieldComponent = null;
		Double validateMinLengthOfFieldComponent = null;
		Double validateMaxLengthOfFieldComponent = null;
		String validateMaxDateOfFieldComponent = null;
		String validateMinDateOfFieldComponent = null;
		String validateMaxTimeOfFieldComponent = null;
		String validateMinTimeOfFieldComponent = null;
		String validateMinRow = null;
		String validateMaxRow = null;

		if (!ObjectUtils.isEmpty(fieldComponent.getValidate())) {
			validateMinLengthOfFieldComponent = fieldComponent.getValidate().getMinLength();
			validateMaxLengthOfFieldComponent = fieldComponent.getValidate().getMaxLength();
			validateIntegerOfFieldComponent = fieldComponent.getValidate().getInteger();
			patternOfFieldComponent = fieldComponent.getValidate().getPattern();
			validateMinOfFieldComponent = fieldComponent.getValidate().getMin();
			validateMaxOfFieldComponent = fieldComponent.getValidate().getMax();
			validateMaxDateOfFieldComponent = fieldComponent.getValidate().getMaxDate();
			validateMinDateOfFieldComponent = fieldComponent.getValidate().getMinDate();
			validateMaxTimeOfFieldComponent = fieldComponent.getValidate().getMaxTime();
			validateMinTimeOfFieldComponent = fieldComponent.getValidate().getMinTime();
			validateMinRow = fieldComponent.getValidate().getMinRows();
			validateMaxRow = fieldComponent.getValidate().getMaxRows();

		}
		String fieldDependencyShowConditionOfFieldComponent = null;
		String fieldDependencyHideConditionOfFieldComponent = null;
		String fieldDependencyEnableConditionOfFieldComponent = null;
		String fieldDependencyDisableConditionOfFieldComponent = null;

		String fieldDependencyRequiredConditionOfFieldComponent = null;
		String fieldDependencySetValueOfFieldComponent = null;
		boolean fieldDependencyRequiredOfFieldComponent = false;
		boolean fieldDependencyHiddenOfFieldComponent = false;
		boolean fieldDependencyDisabledOfFieldComponent = false;
		if (!ObjectUtils.isEmpty(fieldComponent.getFieldDependency())) {
			FieldDependency fieldDependency = fieldComponent.getFieldDependency();
			if (!ObjectUtils.isEmpty(fieldDependency.getShow())) {
				fieldDependencyShowConditionOfFieldComponent = fieldDependency.getShow().getCondition();
			}
			if (!ObjectUtils.isEmpty(fieldDependency.getHide())) {
				fieldDependencyHideConditionOfFieldComponent = fieldDependency.getHide().getCondition();
			}
			if (!ObjectUtils.isEmpty(fieldDependency.getEnable())) {
				fieldDependencyEnableConditionOfFieldComponent = fieldDependency.getEnable().getCondition();
			}
			if (!ObjectUtils.isEmpty(fieldDependency.getDisable())) {
				fieldDependencyDisableConditionOfFieldComponent = fieldDependency.getDisable().getCondition();
			}
			if (!ObjectUtils.isEmpty(fieldDependency.getRequiredReq())) {
				fieldDependencyRequiredConditionOfFieldComponent = fieldDependency.getRequiredReq().getCondition();
			}
			if (!ObjectUtils.isEmpty(fieldDependency.getValues())) {
				StringBuilder sb = new StringBuilder();
				for (DependencyValue setObject : fieldDependency.getValues()) {
					sb.append(setObject.getCondition() != null ? setObject.getCondition()
							: BffAdminConstantsUtils.EMPTY_SPACES).append(BffAdminConstantsUtils.DEPENDENCY_BREAK);
					sb.append(
							setObject.getValue() != null ? setObject.getValue() : BffAdminConstantsUtils.EMPTY_SPACES);
					sb.append(BffAdminConstantsUtils.DEPENDENCY_OBJ_BREAK);
				}

				fieldDependencySetValueOfFieldComponent = sb.toString();
			}

			fieldDependencyRequiredOfFieldComponent = fieldDependency.getRequired();

			fieldDependencyHiddenOfFieldComponent = fieldDependency.getHidden();

			fieldDependencyDisabledOfFieldComponent = fieldDependency.getDisabled();

		}
		List<EventsDto> eventsOfFieldComponent = null;
		if (!CollectionUtils.isEmpty(fieldComponent.getEvents())) {
			eventsOfFieldComponent = new ArrayList<>();
			for (Event event : fieldComponent.getEvents()) {
				ObjectNode eventAction = event.getAction();
				if (eventAction != null && !eventAction.isEmpty()) {
					eventsOfFieldComponent.add(new EventsDto(event.getEventId(), event.getEventName(),
							eventAction.toString(), fieldComponent.getFieldId(), null));
				} else {
					eventsOfFieldComponent.add(new EventsDto(event.getEventId(), event.getEventName(), null,
							fieldComponent.getFieldId(), null));
				}
			}
		}
		List<DataDto> dataList = null;
		if (!ObjectUtils.isEmpty(fieldComponent.getData()) && !CollectionUtils.isEmpty(fieldComponent.getData().getValues())) {
			dataList = new ArrayList<>();
			for (LabelDetails value : fieldComponent.getData().getValues()) {
				dataList.add(new DataDto(value.getValueId(), BffUtils.getNullable(value.getLabel(), TranslationRequest::getRbkey), value.getValue(),
						fieldComponent.getFieldId()));
			}
		}
		UUID linkedComponentIdOfFieldComponent = null;
		// If Custom control within custom control , store the inside custom control id
		// reference in parentFieldId
		if (fieldComponent.getType() != null
				&& fieldComponent.getType().contains(BffAdminConstantsUtils.CUSTOM_CONTAINER)) {
			linkedComponentIdOfFieldComponent = fieldComponent.getCustomComponentId();
		}
		String iconNameOfFieldComponent = null;
		String iconCodeOfFieldComponent = null;
		if (fieldComponent.getIconInfo() != null) {
			iconNameOfFieldComponent = fieldComponent.getIconInfo().getIconName();
			iconCodeOfFieldComponent = fieldComponent.getIconInfo().getIconCode();
		}

		List<ValuesDto> valueList = null;
		if (!CollectionUtils.isEmpty(fieldComponent.getValues())) {
			valueList = new ArrayList<>();
			for (LabelDetails value : fieldComponent.getValues()) {
				ValuesDto valueDto = new ValuesDto(BffUtils.getNullable(value.getLabel(), TranslationRequest::getRbkey), value.getValue(), value.getValueId());
				valueList.add(valueDto);
			}
		}
		this.fieldId = fieldComponent.getFieldId();
		this.formId = formId;
		this.customComponentMasterId = customFormId != null ? customFormId : fieldComponent.getCustomComponentId();
		this.sequence = sequence;
		this.key = fieldComponent.getKey();
		this.label = BffUtils.getNullable(fieldComponent.getLabel(), TranslationRequest::getRbkey);
		this.customFormat = fieldComponent.getCustomFormat();
		this.buttonType = fieldComponent.getButtonType();
		this.format = fieldComponent.getFormat();
		this.imageSource = fieldComponent.getImageSource();
		this.alignment = fieldComponent.getAlignment();
		this.style = styleOfFieldComponent;
		this.styleFontType = styleFontTypeOfFieldComponent;
		this.styleFontSize = styleFontSizeOfFieldComponent;
		this.styleFontColor = styleFontColorOfFieldComponent;
		this.styleBackgroundColor = styleBackgroundColorOfFieldComponent;
		this.styleFontWeight = styleFontWeightOfFieldComponent;
		this.styleWidth = styleWidthOfFieldComponent;
		this.styleHeight = styleHeightOfFieldComponent;
		this.stylePadding = stylePaddingOfFieldComponent;
		this.styleMargin = styleMarginOfFieldComponent;
		this.inline = fieldComponent.getInline();
		this.icon = fieldComponent.getIcon();
		this.autoCorrect = fieldComponent.getAutoCorrect();
		this.capitalization = fieldComponent.getCapitalization();
		this.type = fieldComponent.getType();
		this.input = fieldComponent.getInput();
		this.defaultValue = fieldComponent.getDefaultValue();
		this.tableView = fieldComponent.getTableView();
		this.valueProperty = fieldComponent.getValueProperty();
		this.fontColor = fieldComponent.getFontColor();
		this.allowInput = fieldComponent.getAllowInput();
		this.enableDate = fieldComponent.getEnableDate();
		this.datePickerMinDate = datePickerMinDateOfFieldComponent;
		this.datePickerMaxDate = datePickerMaxDateOfFieldComponent;
		this.validateInteger = validateIntegerOfFieldComponent;
		this.fieldDependencyShowCondition = fieldDependencyShowConditionOfFieldComponent;
		this.fieldDependencyHideCondition = fieldDependencyHideConditionOfFieldComponent;
		this.fieldDependencyEnableCondition = fieldDependencyEnableConditionOfFieldComponent;
		this.fieldDependencyDisableCondition = fieldDependencyDisableConditionOfFieldComponent;
		this.fieldDependencyRequiredCondition = fieldDependencyRequiredConditionOfFieldComponent;
		this.fieldDependencyValues = fieldDependencySetValueOfFieldComponent;
		this.fieldDependencyRequired = fieldDependencyRequiredOfFieldComponent;
		this.fieldDependencyHidden = fieldDependencyHiddenOfFieldComponent;
		this.fieldDependencyDisabled = fieldDependencyDisabledOfFieldComponent;
		this.hideLabel = fieldComponent.getHideLabel();
		this.customClass = fieldComponent.getCustomClass();
		this.mask = fieldComponent.getMask();
		this.alwaysEnabled = fieldComponent.getAlwaysEnabled();
		this.lazyLoad = fieldComponent.getLazyLoad();
		this.description = BffUtils.getNullable(fieldComponent.getDescription(), TranslationRequest::getRbkey);
		this.selectValues = fieldComponent.getSelectValues();
		this.disableLimit = fieldComponent.getDisableLimit();
		this.sort = fieldComponent.getSort();
		this.reference = fieldComponent.getReference();
		this.radius = fieldComponent.getRadius();
		this.backGroundColor = fieldComponent.getBackgroundColor();
		this.width = fieldComponent.getWidth();
		this.height = fieldComponent.getHeight();
		this.maxDate = fieldComponent.getMaxDate();
		this.minDate = fieldComponent.getMinDate();
		this.iconAlignment = fieldComponent.getIconAlignment();
		this.events = eventsOfFieldComponent;
		this.data = dataList;
		this.childFieldObjDtoList = childFieldObjDtoList;
		this.linkedComponentId = linkedComponentIdOfFieldComponent;
		this.offset = fieldComponent.getOffset();
		this.push = fieldComponent.getPush();
		this.pull = fieldComponent.getPull();
		this.hideOnChildrenHidden = fieldComponent.getHideOnChildrenHidden();
		this.pattern = patternOfFieldComponent;
		this.placeHolder = BffUtils.getNullable(fieldComponent.getPlaceholder(), TranslationRequest::getRbkey);
		this.inputType = fieldComponent.getInputFieldType();
		this.iconName = iconNameOfFieldComponent;
		this.iconCode = iconCodeOfFieldComponent;
		this.lineBreakMode = fieldComponent.getLineBreakMode();
		this.fontType = fieldComponent.getFontType();
		this.fontSize = fieldComponent.getFontSize();
		this.disableAddingRemovingRows = fieldComponent.isDisableAddingRemovingRows();
		this.addAnother = fieldComponent.getAddAnother();
		this.addAnotherPosition = fieldComponent.getAddAnother();
		this.removePlacement = fieldComponent.getRemovePlacement();
		this.striped = fieldComponent.isStriped();
		this.bordered = fieldComponent.isBordered();
		this.selected = fieldComponent.isSelected();
		this.condensed = fieldComponent.isCondensed();
		this.addSorting = fieldComponent.isAddSorting();
		this.addFilter = fieldComponent.isAddFilter();
		this.styleType = fieldComponent.getStyleType();
		this.clearOnHide = fieldComponent.isClearOnHide();
		this.addPagination = fieldComponent.isAddPagination();
		this.numberOfRows = fieldComponent.getNumberOfRows();
		this.prefix = fieldComponent.getPrefix();
		this.suffix = fieldComponent.getSuffix();
		this.values = valueList;
		this.listImageAlignment = fieldComponent.getListImageAlignment();
		this.headerLabel = BffUtils.getNullable(fieldComponent.getHeaderLabel(), TranslationRequest::getRbkey);
		if(fieldComponent.getApiDataSource() != null && !fieldComponent.getApiDataSource().isEmpty()) {
			this.apiDataSource = fieldComponent.getApiDataSource();
		}
		if(fieldComponent.getDefaultApiValue() != null && !fieldComponent.getDefaultApiValue().isEmpty()) {
			this.defaultApiValue = fieldComponent.getDefaultApiValue();
		}		
		this.defaultStaticValue = fieldComponent.getDefaultStaticValue();
		this.defaultValueType = fieldComponent.getDefaultValueType();
		this.rows = fieldComponent.getRows();
		this.validateMin = validateMinOfFieldComponent;
		this.validateMax = validateMaxOfFieldComponent;
		this.validateMaxDate = validateMaxDateOfFieldComponent;
		this.validateMinDate = validateMinDateOfFieldComponent;
		this.validateMaxLength = validateMaxLengthOfFieldComponent;
		this.validateMinLength = validateMinLengthOfFieldComponent;
		this.validateMaxTime = validateMaxTimeOfFieldComponent;
		this.validateMinTime = validateMinTimeOfFieldComponent;
		this.minRows = validateMinRow;
		this.maxRows = validateMaxRow;
		this.modifyStatus = fieldComponent.isModifyStatus();
		this.textAreaHeight = fieldComponent.getTextAreaHeight();
		this.bold = fieldComponent.isBold();
		this.italic = fieldComponent.isItalic();
		this.underline = fieldComponent.isUnderline();
		this.decimalPlaces = fieldComponent.getDecimalPlaces();
		this.currentDate = fieldComponent.isCurrentDate();
		this.currentTime = fieldComponent.isCurrentTime();
		if(fieldComponent.getAutoCompleteApi() != null && !fieldComponent.getAutoCompleteApi().isEmpty()) {
			this.autoCompleteApi = fieldComponent.getAutoCompleteApi();
		}
		this.productConfigId = productConfigId;
		this.buttonSize = fieldComponent.getButtonSize();
		this.autoAdjust = fieldComponent.isAutoAdjust();
		this.hotKeyName = fieldComponent.getHotKeyName();
	}

	/**
	 * @param field
	 * @param childFieldObjDtoList
	 * @throws IOException
	 */
	public FieldObjDto(Field field, List<FieldObjDto> childFieldObjDtoList) throws IOException {
		super();
		List<DataDto> dataDtoList = null;
		ObjectMapper mapper = new ObjectMapper();
		if (!CollectionUtils.isEmpty(field.getData())) {
			dataDtoList = new ArrayList<>();
			for (com.jda.mobility.framework.extensions.entity.Data dataofField : field.getData()) {
				dataDtoList.add(new DataDto(dataofField.getUid(), dataofField.getDatalabel(),
						dataofField.getDatavalue(), dataofField.getField().getUid()));
			}
		}

		List<EventsDto> eventDtoList = null;
		if (!CollectionUtils.isEmpty(field.getEvents())) {
			eventDtoList = new ArrayList<>();
			for (Events eventsOfField : field.getEvents()) {
				eventDtoList
						.add(new EventsDto(eventsOfField.getUid(), eventsOfField.getEvent(), eventsOfField.getAction(),
								BffUtils.getNullable(eventsOfField.getField(), Field::getUid), null));
			}
		}

		List<ValuesDto> valueList = null;
		if (!CollectionUtils.isEmpty(field.getValues())) {
			valueList = new ArrayList<>();
			for (FieldValues value : field.getValues()) {
				ValuesDto valueDto = new ValuesDto(value.getLabel(), value.getLabelValue(), value.getUid());
				valueList.add(valueDto);
			}
		}

		this.fieldId = field.getUid();
		this.formId = BffUtils.getNullable(field.getForm(), Form::getUid);
		this.customComponentMasterId = null;
		this.sequence = field.getSequence();
		this.key = field.getKeys();
		this.label = field.getLabel();
		this.customFormat = field.getCustomFormat();
		this.buttonType = field.getButtonType();
		this.format = field.getFormat();
		this.imageSource = field.getImageSource();
		this.alignment = field.getAlignment();
		this.style = field.getStyle();
		this.styleFontType = field.getStyleFontType();
		this.styleFontSize = field.getStyleFontSize();
		this.styleFontColor = field.getStyleFontColor();
		this.styleBackgroundColor = field.getStyleBackgroundColor();
		this.styleFontWeight = field.getStyleFontWeight();
		this.styleWidth = field.getStyleWidth();
		this.styleHeight = field.getStyleHeight();
		this.stylePadding = field.getStylePadding();
		this.styleMargin = field.getStyleMargin();
		this.inline = field.isInline();
		this.icon = field.isIcon();
		this.autoCorrect = field.isAutoCorrect();
		this.capitalization = field.isCapitalization();
		this.type = field.getType();
		this.input = field.isInput();
		this.defaultValue = field.getDefaultValue();
		this.tableView = field.isTableView();
		this.valueProperty = field.getValueProperty();
		this.fontColor = field.getFontColor();
		this.allowInput = field.isAllowInput();
		this.enableDate = field.isEnableDate();
		this.datePickerMinDate = field.getDatePickerMinDate();
		this.datePickerMaxDate = field.getDatePickerMaxDate();
		this.validateInteger = field.getValidateInteger();
		this.fieldDependencyShowCondition = field.getFieldDependencyShowCondition();
		this.fieldDependencyHideCondition = field.getFieldDependencyHideCondition();
		this.fieldDependencyEnableCondition = field.getFieldDependencyEnableCondition();
		this.fieldDependencyDisableCondition = field.getFieldDependencyDisableCondition();
		this.fieldDependencyRequiredCondition = field.getFieldDependencyRequiredCondition();
		this.fieldDependencyValues = field.getFieldDependencySetValue();
		this.fieldDependencyRequired = field.isFieldDependencyRequired();
		this.fieldDependencyHidden = field.isFieldDependencyHidden();
		this.fieldDependencyDisabled = field.isFieldDependencyDisabled();
		this.hideLabel = field.isHideLabel();
		this.customClass = field.getCustomClass();
		this.mask = field.isMask();
		this.alwaysEnabled = field.isAlwaysEnabled();
		this.lazyLoad = field.isLazyLoad();
		this.description = field.getDescription();
		this.selectValues = field.getSelectValues();
		this.disableLimit = field.isDisableLimit();
		this.sort = field.getSort();
		this.reference = field.isReference();
		this.radius = field.getRadius();
		this.backGroundColor = field.getBackGroundColor();
		this.width = field.getWidth();
		this.height = field.getHeight();
		this.maxDate = field.getMaxDate();
		this.minDate = field.getMinDate();
		this.iconAlignment = field.getIconAlignment();
		this.events = eventDtoList;
		this.data = dataDtoList;
		this.childFieldObjDtoList = childFieldObjDtoList;
		this.linkedComponentId = field.getLinkedComponentId();
		this.offset = field.getOffset();
		this.push = field.getPush();
		this.pull = field.getPull();
		this.hideOnChildrenHidden = field.isHideOnChildrenHidden();
		this.pattern = field.getValidatePattern();
		this.placeHolder = field.getPlaceHolder();
		this.inputType = field.getInputType();
		this.iconName = field.getIconName();
		this.iconCode = field.getIconCode();
		this.lineBreakMode = field.getLineBreakMode();
		this.fontType = field.getFontType();
		this.fontSize = field.getFontSize();
		this.disableAddingRemovingRows = field.isDisableAddingRemovingRows();
		this.addAnother = field.getAddAnother();
		this.addAnotherPosition = field.getAddAnother();
		this.removePlacement = field.getRemovePlacement();
		this.striped = field.isStriped();
		this.bordered = field.isBordered();
		this.selected = field.isSelected();
		this.condensed = field.isCondensed();
		this.addSorting = field.isAddSorting();
		this.addFilter = field.isAddFilter();
		this.styleType = field.getStyleType();
		this.clearOnHide = field.isClearOnHide();
		this.addPagination = field.isAddPagination();
		this.numberOfRows = field.getNumberOfRows();
		this.prefix = field.getPrefix();
		this.suffix = field.getSuffix();
		this.headerLabel = field.getHeaderLabel();
		this.values = valueList;
		this.listImageAlignment = field.getListImageAlignment();
		this.rows = field.getRows();
		this.apiDataSource = field.getApiDataSource() != null ? (ObjectNode) mapper.readTree(field.getApiDataSource())
				: null;
		this.defaultApiValue = field.getDefaultApiValue() != null
				? (ObjectNode) mapper.readTree(field.getDefaultApiValue())
				: null;
		this.defaultStaticValue = field.getDefaultStaticValue();
		this.defaultValueType = field.getDefaultValueType();
		this.validateMin = field.getValidateMin();
		this.validateMax = field.getValidateMax();
		this.validateMaxLength = field.getValidateMaxLength();
		this.validateMinLength = field.getValidateMinLength();
		this.validateMaxDate = field.getValidateMaxDate();
		this.validateMinDate = field.getValidateMinDate();
		this.validateMaxTime = field.getValidateMaxTime();
		this.validateMinTime = field.getValidateMinTime();
		this.minRows = field.getValidateMinRow();
		this.maxRows = field.getValidateMaxRow();
		this.modifyStatus = field.isModifyStatus();
		this.textAreaHeight = field.getTextAreaHeight();
		this.bold = field.isBold();
		this.italic = field.isItalic();
		this.underline = field.isUnderline();
		this.decimalPlaces = field.getDecimalPlaces();
		this.currentDate = field.isCurrDateDefSet();
		this.currentTime = field.isCurrDateTimeSet();
		this.autoCompleteApi = field.getAutoCompleteApi() != null
				? (ObjectNode) mapper.readTree(field.getAutoCompleteApi())
				: null;
		this.buttonSize = field.getButtonSize();
		this.autoAdjust = field.isAutoAdjust();
		this.hotKeyName = field.getHotKeyName();
	}
	@Override
	public int compareTo(FieldObjDto fieldObjDto) {
		int compareSeq = fieldObjDto.getSequence();
		/* For Ascending order */
		return this.sequence - compareSeq;
	}

}