package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.entity.CustomComponentMaster;
import com.jda.mobility.framework.extensions.entity.CustomData;
import com.jda.mobility.framework.extensions.entity.CustomEvents;
import com.jda.mobility.framework.extensions.entity.CustomField;
import com.jda.mobility.framework.extensions.entity.CustomFieldValues;
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
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffUtils;

import lombok.Data;

/**
 * The class CustomFieldObjDto.java HCL Technologies Ltd.
 */
@Data
public class CustomFieldObjDto implements Serializable, Comparable<CustomFieldObjDto> {

	private static final long serialVersionUID = -3942944047475356113L;

	/** The field fieldId of type String */
	private final UUID fieldId;

	/** The field customComponentMasterId of type UUID */
	private final UUID linkedComponentId;
	private final UUID customComponentMasterId;
	private final int sequence;
	private final String key;
	private final String label;
	private final String customFormat;
	private final String buttonType;
	private final String format;
	private final String imageSource;
	private final String alignment;
	private final String style;
	private final String styleFontType;
	private final String styleFontSize;
	private final String styleFontColor;
	private final String styleBackgroundColor;
	private final String styleFontWeight;
	private final String styleWidth;
	private final String styleHeight;
	private final String stylePadding;
	private final String styleMargin;
	private final boolean inline;
	private final boolean icon;
	private final boolean autoCorrect;
	private final boolean capitalization;
	private final String type;
	private final boolean input;
	private final String defaultValue;
	private final boolean tableView;
	private final String valueProperty;
	private final String fontColor;
	private final boolean allowInput;
	private final boolean enableDate;
	private final Date datePickerMinDate;
	private final Date datePickerMaxDate;
	private final String validateInteger;
	private final String fieldDependencyShowCondition;
	private final String fieldDependencyHideCondition;
	private final String fieldDependencyEnableCondition;
	private final String fieldDependencyDisableCondition;
	private final String fieldDependencyRequiredCondition;
	private final String fieldDependencySetValue;
	private final boolean fieldDependencyRequired;
	private final boolean fieldDependencyHidden;
	private final boolean fieldDependencyDisabled;
	private final boolean hideLabel;
	private final String customClass;
	private final boolean mask;
	private final boolean alwaysEnabled;
	private final boolean lazyLoad;
	private final String description;
	private final String selectValues;
	private final boolean disableLimit;
	private final String sort;
	private final boolean reference;
	private final String radius;
	private final String backGroundColor;
	private final String width;
	private final String height;
	private final String maxDate;
	private final String minDate;
	private final String iconAlignment;
	private final List<EventsDto> events;
	private final List<DataDto> data;
	private String placeHolder;
	private String inputType;
	private String iconName;
	private String iconCode;
	private final List<CustomFieldObjDto> childFieldObjDtoList;
	private String pattern;
	private String fontType;
	private String fontSize;
	private String lineBreakMode;
	private final boolean disableAddingRemovingRows;
	private final String addAnother;
	private final String addAnotherPosition;
	private final String removePlacement;
	private final boolean striped;
	private final boolean bordered;
	private final boolean selected;
	private final boolean condensed;
	private final boolean addSorting;
	private final boolean addFilter;
	private final String styleType;
	private final boolean clearOnHide;
	private final boolean addPagination;
	private final String numberOfRows;
	private final String offset;
	private final String push;
	private final String pull;
	private final boolean hideOnChildrenHidden;
	private final String prefix;
	private final String suffix;
	private final String headerLabel;
	private String listImageAlignment;
	private int rows;
	private final ObjectNode defaultApiValue;
	private final ObjectNode apiDataSource;
	private final String defaultValueType;
	private final String defaultStaticValue;
	private List<ValuesDto> values;

	private Double validateMin;
	private Double validateMax;
	private Double validateMinLength;
	private Double validateMaxLength;
	private String validateMinDate;
	private String validateMaxDate;
	private String validateMinTime;
	private String validateMaxTime;
	private final String minRows;
	private final String maxRows;
	private final int textAreaHeight;
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
	 * @param customComponentMasterId
	 * @param childFieldObjDtoList
	 * @param sequence
	 */
	public CustomFieldObjDto(FieldComponent fieldComponent, UUID customComponentMasterId,
			List<CustomFieldObjDto> childFieldObjDtoList, int sequence,UUID productConfigId) {
		super();
		Style styleObj = fieldComponent.getStyle();
		List<EventsDto> fieldComponentEvents = null;
		if (fieldComponent.getEvents() != null && !fieldComponent.getEvents().isEmpty()) {
			fieldComponentEvents = new ArrayList<>();
			for (Event event : fieldComponent.getEvents()) {
				ObjectNode eventAction = event.getAction();
				if (eventAction != null) {
					fieldComponentEvents.add(new EventsDto(event.getEventId(), event.getEventName(),
							eventAction.toString(), fieldComponent.getFieldId(), null));
				} else {
					fieldComponentEvents.add(new EventsDto(event.getEventId(), event.getEventName(), null,
							fieldComponent.getFieldId(), null));
				}
			}
		}
		List<DataDto> dataList = null;
		if (fieldComponent.getData() != null && fieldComponent.getData().getValues() != null) {
			dataList = new ArrayList<>();
			List<LabelDetails> valueList = fieldComponent.getData().getValues();
			for (LabelDetails value : valueList) {
				DataDto dataDto = new DataDto(value.getValueId(), BffUtils.getNullable(value.getLabel(), TranslationRequest::getRbkey), value.getValue(),
						fieldComponent.getFieldId());
				dataList.add(dataDto);
			}
		}
		UUID fieldComponentLinkedComponentId = null;
		// If Custom control within custom control , store the inside custom control id
		// reference in parentFieldId
		if (fieldComponent.getType().contains(BffAdminConstantsUtils.CUSTOM_CONTAINER)
				&& null != fieldComponent.getComponents() && !fieldComponent.getComponents().isEmpty()) {
			fieldComponentLinkedComponentId = fieldComponent.getComponents().get(0).getCustomComponentId();
		}
		List<ValuesDto> valueList = new ArrayList<>();
		if (fieldComponent.getValues() != null && !fieldComponent.getValues().isEmpty()) {
			for (LabelDetails value : fieldComponent.getValues()) {
				ValuesDto valueDto = new ValuesDto(BffUtils.getNullable(value.getLabel(), TranslationRequest::getRbkey), value.getValue(), value.getValueId());
				valueList.add(valueDto);
			}
		}
		this.fieldId = fieldComponent.getFieldId();
		this.customComponentMasterId = customComponentMasterId != null ? customComponentMasterId
				: fieldComponent.getCustomComponentId();
		this.sequence = sequence;
		this.key = fieldComponent.getKey();
		this.label = BffUtils.getNullable(fieldComponent.getLabel(), TranslationRequest::getRbkey);
		this.customFormat = fieldComponent.getCustomFormat();
		this.buttonType = fieldComponent.getButtonType();
		this.format = fieldComponent.getFormat();
		this.imageSource = fieldComponent.getImageSource();
		this.alignment = fieldComponent.getAlignment();
		if (styleObj != null) {
			this.style = styleObj.getStyle();
			this.styleFontType = styleObj.getFontType();
			this.styleFontSize = styleObj.getFontSize();
			this.styleFontColor = styleObj.getFontColor();
			this.styleBackgroundColor = styleObj.getBackgroundColor();
			this.styleFontWeight = styleObj.getFontWeight();
			this.styleWidth = styleObj.getWidth();
			this.styleHeight = styleObj.getHeight();
			this.stylePadding = styleObj.getPadding();
			this.styleMargin = styleObj.getMargin();
		} else {
			this.style = null;
			this.styleFontType = null;
			this.styleFontSize = null;
			this.styleFontColor = null;
			this.styleBackgroundColor = null;
			this.styleFontWeight = null;
			this.styleWidth = null;
			this.styleHeight = null;
			this.stylePadding = null;
			this.styleMargin = null;
		}
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
		if (fieldComponent.getDatePicker() != null) {
			this.datePickerMinDate = (Date) fieldComponent.getDatePicker().getMinDate();
			this.datePickerMaxDate = (Date) fieldComponent.getDatePicker().getMaxDate();
		} else {
			this.datePickerMinDate = null;
			this.datePickerMaxDate = null;
		}
		if (fieldComponent.getValidate() != null) {
			this.validateMinLength = fieldComponent.getValidate().getMinLength();
			this.validateMaxLength = fieldComponent.getValidate().getMaxLength();
			this.validateInteger = fieldComponent.getValidate().getInteger();
			this.pattern = fieldComponent.getValidate().getPattern();
			this.validateMin = fieldComponent.getValidate().getMin();
			this.validateMax = fieldComponent.getValidate().getMax();
			this.validateMaxDate = fieldComponent.getValidate().getMaxDate();
			this.validateMinDate = fieldComponent.getValidate().getMinDate();
			this.validateMinTime = fieldComponent.getValidate().getMaxTime();
			this.validateMaxTime = fieldComponent.getValidate().getMinTime();
			this.minRows = fieldComponent.getValidate().getMinRows();
			this.maxRows = fieldComponent.getValidate().getMaxRows();

		} else {
			this.validateMin = null;
			this.validateMax = null;
			this.validateMinLength = null;
			this.validateMaxLength = null;
			this.validateInteger = null;
			this.pattern = null;
			this.validateMaxDate = null;
			this.validateMinDate = null;
			this.validateMinTime = null;
			this.validateMaxTime = null;
			this.minRows = null;
			this.maxRows = null;
		}
		if (fieldComponent.getFieldDependency() != null) {
			FieldDependency fieldDependency = fieldComponent.getFieldDependency();
			this.fieldDependencyShowCondition = BffUtils.getNullable(fieldDependency.getShow(), Show::getCondition);
			this.fieldDependencyHideCondition = BffUtils.getNullable(fieldDependency.getHide(), Hide::getCondition);
			this.fieldDependencyEnableCondition = BffUtils.getNullable(fieldDependency.getEnable(),
					Enable::getCondition);
			this.fieldDependencyDisableCondition = BffUtils.getNullable(fieldDependency.getDisable(),
					Disable::getCondition);
			this.fieldDependencyRequiredCondition = BffUtils.getNullable(fieldDependency.getRequiredReq(),
					Required::getCondition);
			if (fieldDependency.getValues() != null && !fieldDependency.getValues().isEmpty()) {
				StringBuilder sb = new StringBuilder();
				for (DependencyValue setObject : fieldDependency.getValues()) {
					sb.append(setObject.getCondition() != null ? setObject.getCondition()
							: BffAdminConstantsUtils.EMPTY_SPACES).append(BffAdminConstantsUtils.DEPENDENCY_BREAK);
					sb.append(
							setObject.getValue() != null ? setObject.getValue() : BffAdminConstantsUtils.EMPTY_SPACES);
					sb.append(BffAdminConstantsUtils.DEPENDENCY_OBJ_BREAK);
				}
				this.fieldDependencySetValue = sb.toString();
			} else {
				this.fieldDependencySetValue = null;
			}
			this.fieldDependencyRequired = fieldDependency.getRequired();
			this.fieldDependencyHidden = fieldDependency.getHidden();
			this.fieldDependencyDisabled = fieldDependency.getDisabled();
		} else {
			this.fieldDependencyShowCondition = null;
			this.fieldDependencyHideCondition = null;
			this.fieldDependencyEnableCondition = null;
			this.fieldDependencyDisableCondition = null;
			this.fieldDependencyRequiredCondition = null;
			this.fieldDependencySetValue = null;
			this.fieldDependencyRequired = false;
			this.fieldDependencyHidden = false;
			this.fieldDependencyDisabled = false;
		}
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
		this.events = fieldComponentEvents;
		this.data = dataList;
		this.childFieldObjDtoList = childFieldObjDtoList;
		this.linkedComponentId = fieldComponentLinkedComponentId;
		this.placeHolder = BffUtils.getNullable(fieldComponent.getPlaceholder(), TranslationRequest::getRbkey);
		this.inputType = fieldComponent.getInputFieldType();
		this.iconName = BffUtils.getNullable(fieldComponent.getIconInfo(), IconInfo ::getIconName);
		this.iconCode = BffUtils.getNullable(fieldComponent.getIconInfo(), IconInfo ::getIconCode);
		this.lineBreakMode = fieldComponent.getLineBreakMode();
		this.fontType = fieldComponent.getFontType();
		this.fontSize = fieldComponent.getFontSize();
		this.disableAddingRemovingRows = fieldComponent.isDisableAddingRemovingRows();
		this.addAnother = fieldComponent.getAddAnother();
		this.addAnotherPosition = fieldComponent.getAddAnotherPosition();
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
		this.offset = fieldComponent.getOffset();
		this.push = fieldComponent.getPush();
		this.pull = fieldComponent.getPull();
		this.hideOnChildrenHidden = fieldComponent.getHideOnChildrenHidden();
		this.prefix = fieldComponent.getPrefix();
		this.suffix = fieldComponent.getSuffix();
		this.headerLabel = BffUtils.getNullable(fieldComponent.getHeaderLabel(), TranslationRequest::getRbkey);
		this.listImageAlignment = fieldComponent.getListImageAlignment();
		this.rows = fieldComponent.getRows();
		this.defaultApiValue = fieldComponent.getDefaultApiValue();
		this.defaultStaticValue = fieldComponent.getDefaultStaticValue();
		this.defaultValueType = fieldComponent.getDefaultValueType();
		this.apiDataSource = fieldComponent.getApiDataSource();
		this.values = valueList;
		this.textAreaHeight = fieldComponent.getTextAreaHeight();
		this.bold = fieldComponent.isBold();
		this.italic = fieldComponent.isItalic();
		this.underline = fieldComponent.isUnderline();
		this.decimalPlaces = fieldComponent.getDecimalPlaces();
		this.currentDate = fieldComponent.isCurrentDate();
		this.currentTime = fieldComponent.isCurrentTime();
		this.autoCompleteApi = fieldComponent.getAutoCompleteApi();
		this.productConfigId = productConfigId;
		this.buttonSize= fieldComponent.getButtonSize();
		this.autoAdjust= fieldComponent.isAutoAdjust();
		this.hotKeyName = fieldComponent.getHotKeyName();
	}

	/**
	 * @param customField
	 * @param childFieldObjDtoList
	 * @throws JsonProcessingException
	 */
	public CustomFieldObjDto(CustomField customField, List<CustomFieldObjDto> childFieldObjDtoList)
			throws JsonProcessingException {
		super();
		ObjectMapper objectMapper = new ObjectMapper();
		List<ValuesDto> valueList = null;
		if (customField.getValues() != null && !customField.getValues().isEmpty()) {
			valueList = new ArrayList<>();
			for (CustomFieldValues customFieldValues : customField.getValues()) {
				valueList.add(new ValuesDto(customFieldValues.getLabel(), customFieldValues.getLabelValue(),
						customFieldValues.getUid()));
			}
		}
		List<EventsDto> eventDtoList = null;
		if (customField.getEvents() != null && !customField.getEvents().isEmpty()) {
			eventDtoList = new ArrayList<>();
			for (CustomEvents customEvents : customField.getEvents()) {
				eventDtoList.add(new EventsDto(customEvents.getUid(), customEvents.getEvent(), customEvents.getAction(),
						customEvents.getField().getUid(), null));
			}
		}
		List<DataDto> dataDtoList = null;
		if (customField.getData() != null && !customField.getData().isEmpty()) {
			dataDtoList = new ArrayList<>();
			for (CustomData customData : customField.getData()) {
				dataDtoList.add(new DataDto(customData.getUid(), customData.getDatalabel(), customData.getDatavalue(),
						customData.getField().getUid()));
			}
		}

		this.fieldId = customField.getUid();
		this.customComponentMasterId = BffUtils.getNullable(customField.getCustomComponentMaster(),
				CustomComponentMaster::getUid);
		this.sequence = customField.getSequence();
		this.key = customField.getKeys();
		this.label = customField.getLabel();
		this.customFormat = customField.getCustomFormat();
		this.buttonType = customField.getButtonType();
		this.format = customField.getFormat();
		this.imageSource = customField.getImageSource();
		this.alignment = customField.getAlignment();
		this.style = customField.getStyle();
		this.styleFontType = customField.getStyleFontType();
		this.styleFontSize = customField.getStyleFontSize();
		this.styleFontColor = customField.getStyleFontColor();
		this.styleBackgroundColor = customField.getStyleBackgroundColor();
		this.styleFontWeight = customField.getStyleFontWeight();
		this.styleWidth = customField.getStyleWidth();
		this.styleHeight = customField.getStyleHeight();
		this.stylePadding = customField.getStylePadding();
		this.styleMargin = customField.getStyleMargin();
		this.inline = customField.isInline();
		this.icon = customField.isIcon();
		this.autoCorrect = customField.isAutoCorrect();
		this.capitalization = customField.isCapitalization();
		this.type = customField.getType();
		this.input = customField.isInput();
		this.defaultValue = customField.getDefaultValue();
		this.tableView = customField.isTableView();
		this.valueProperty = customField.getValueProperty();
		this.fontColor = customField.getFontColor();
		this.allowInput = customField.isAllowInput();
		this.enableDate = customField.isEnableDate();
		this.datePickerMinDate = customField.getDatePickerMinDate();
		this.datePickerMaxDate = customField.getDatePickerMaxDate();
		this.validateMin = customField.getValidateMin();
		this.validateMax = customField.getValidateMax();
		this.validateMinLength = customField.getValidateMinLength();
		this.validateMaxLength = customField.getValidateMaxLength();
		this.validateInteger = customField.getValidateInteger();
		this.fieldDependencyShowCondition = customField.getFieldDependencyShowCondition();
		this.fieldDependencyHideCondition = customField.getFieldDependencyHideCondition();
		this.fieldDependencyEnableCondition = customField.getFieldDependencyEnableCondition();
		this.fieldDependencyDisableCondition = customField.getFieldDependencyDisableCondition();
		this.fieldDependencyRequiredCondition = customField.getFieldDependencyRequiredCondition();
		this.fieldDependencySetValue = customField.getFieldDependencySetValue();
		this.fieldDependencyRequired = customField.isFieldDependencyRequired();
		this.fieldDependencyHidden = customField.isFieldDependencyHidden();
		this.fieldDependencyDisabled = customField.isFieldDependencyDisabled();
		this.hideLabel = customField.isHideLabel();
		this.customClass = customField.getCustomClass();
		this.mask = customField.isMask();
		this.alwaysEnabled = customField.isAlwaysEnabled();
		this.lazyLoad = customField.isLazyLoad();
		this.description = customField.getDescription();
		this.selectValues = customField.getSelectValues();
		this.disableLimit = customField.isDisableLimit();
		this.sort = customField.getSort();
		this.reference = customField.isReference();
		this.radius = customField.getRadius();
		this.backGroundColor = customField.getBackGroundColor();
		this.width = customField.getWidth();
		this.height = customField.getHeight();
		this.maxDate = customField.getMaxDate();
		this.minDate = customField.getMinDate();
		this.iconAlignment = customField.getIconAlignment();
		this.events = eventDtoList;
		this.data = dataDtoList;
		this.childFieldObjDtoList = childFieldObjDtoList;
		this.linkedComponentId = BffUtils.getNullable(customField.getLinkedComponentId(),
				CustomComponentMaster::getUid);
		this.pattern = customField.getValidatePattern();
		this.placeHolder = customField.getPlaceHolder();
		this.inputType = customField.getInputType();
		this.iconName = customField.getIconName();
		this.iconCode = customField.getIconCode();
		this.lineBreakMode = customField.getLineBreakMode();
		this.fontType = customField.getFontType();
		this.fontSize = customField.getFontSize();
		this.disableAddingRemovingRows = customField.isDisableAddingRemovingRows();
		this.addAnother = customField.getAddAnother();
		this.addAnotherPosition = customField.getAddAnotherPosition();
		this.removePlacement = customField.getRemovePlacement();
		this.striped = customField.isStriped();
		this.bordered = customField.isBordered();
		this.selected = customField.isSelected();
		this.condensed = customField.isCondensed();
		this.addSorting = customField.isAddSorting();
		this.addFilter = customField.isAddFilter();
		this.styleType = customField.getStyleType();
		this.clearOnHide = customField.isClearOnHide();
		this.addPagination = customField.isAddPagination();
		this.numberOfRows = customField.getNumberOfRows();
		this.offset = customField.getOffset();
		this.push = customField.getPush();
		this.pull = customField.getPull();
		this.hideOnChildrenHidden = customField.isHideOnChildrenHidden();
		this.prefix = customField.getPrefix();
		this.suffix = customField.getSuffix();
		this.headerLabel = customField.getHeaderLabel();
		this.listImageAlignment = customField.getListImageAlignment();
		this.rows = customField.getRows();
		this.defaultApiValue = customField.getDefaultApiValue() != null
				? (ObjectNode) objectMapper.readTree(customField.getDefaultApiValue())
				: null;
		this.defaultStaticValue = customField.getDefaultStaticValue();
		this.defaultValueType = customField.getDefaultValueType();
		this.apiDataSource = customField.getApiDataSource() != null
				? (ObjectNode) objectMapper.readTree(customField.getApiDataSource())
				: null;
		this.values = valueList;
		this.minRows = customField.getValidateMinRow();
		this.maxRows = customField.getValidateMaxRow();
		this.textAreaHeight = customField.getTextAreaHeight();
		this.bold = customField.isBold();
		this.italic = customField.isItalic();
		this.underline = customField.isUnderline();
		this.decimalPlaces = customField.getDecimalPlaces();
		this.currentDate = customField.isCurrDateDefSet();
		this.currentTime = customField.isCurrDateTimeSet();
		this.autoCompleteApi = customField.getAutoCompleteApi() != null
				? (ObjectNode) objectMapper.readTree(customField.getAutoCompleteApi())
				: null;
		this.buttonSize = customField.getButtonSize();
		this.autoAdjust = customField.isAutoAdjust();
		this.hotKeyName = customField.getHotKeyName();
	}

	@Override
	public int compareTo(CustomFieldObjDto fieldObjDto) {
		int compareSeq = fieldObjDto.getSequence();
		/* For Ascending order */
		return this.sequence - compareSeq;
	}

}
