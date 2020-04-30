/**
 * 
 */
package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.dto.TranslationRequest;

/**
 * The class FieldComponent.java
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FieldComponent implements Serializable {

	private static final long serialVersionUID = 7900313133012049903L;
	private TranslationRequest label;
	private boolean mask;
	private boolean tableView;
	private boolean alwaysEnabled;
	private String type;
	private boolean input;
	private String key;
	@Valid
	private List<FieldComponent> components;
	private List<FieldComponent> columns;
	private boolean hideLabel;
	private String customClass;
	private String alignment;
	private Style style;
	private boolean icon;
	private boolean autoCorrect;
	private boolean capitalization;
	private String defaultValue;
	@Valid
	private Validate validate;
	@Valid
	private FieldDependency fieldDependency;
	private List<Event> events;
	private UUID fieldId;
	private UUID customComponentId;
	private String iconAlignment;
	private String imageSource;
	private String format;
	private boolean allowInput;
	private boolean enableDate;
	private DatePicker datePicker;
	private String maxDate;
	private String minDate;
	private String fontColor;
	private String radius;
	private String width;
	private String height;
	private String buttonType;
	private String backgroundColor;
	private boolean inline;
	@Valid
	private List<LabelDetails> values;
	@Valid
	private Data data;
	private String valueProperty;
	private boolean lazyLoad;
	private TranslationRequest description;
	private String selectValues;
	private boolean disableLimit;
	private String sort;
	private boolean reference;
	private String customFormat;
	private String offset;
	private String push;
	private String pull;
	private boolean hideOnChildrenHidden;
	private TranslationRequest placeholder;
	private String inputFieldType;
	private IconInfo iconInfo;
	private String fontType;
	private String fontSize;
	private String lineBreakMode;
	private boolean disableAddingRemovingRows;
	@Size(max = 30)
	private String addAnother;
	@Size(max = 30)
	private String addAnotherPosition;
	@Size(max = 30)
	private String removePlacement;
	private boolean striped;
	private boolean bordered;
	private boolean selected;
	private boolean condensed;
	private boolean addSorting;
	private boolean addFilter;
	@Size(max = 30)
	private String styleType;
	private boolean clearOnHide;
	private boolean addPagination;
	@Size(max = 5)
	private String numberOfRows;
	private String prefix;
	private String suffix;
	private String setValue;
	private TranslationRequest headerLabel;
	private String listImageAlignment;
	private int rows;
	private ObjectNode defaultApiValue;
	private ObjectNode apiDataSource;
	@Size(max = 45)
	private String defaultValueType;
	private String defaultStaticValue;
	private boolean modifyStatus;
	private int textAreaHeight;
	private boolean bold;
	private boolean italic;
	private boolean underline;
	private String decimalPlaces;
	private boolean currentDate;
	private boolean currentTime;
	private ObjectNode autoCompleteApi;
	private String buttonSize;
	private boolean autoAdjust;
	private String customComponentName;
	private String customComponentDesc;
	private String hotKeyName;
	
	public TranslationRequest getLabel() {
		return label;
	}

	public void setLabel(TranslationRequest label) {
		this.label = label;
	}

	public boolean getMask() {
		return mask;
	}

	public void setMask(boolean mask) {
		this.mask = mask;
	}

	/**
	 * @return the iconInfo of type IconInfo
	 */
	public IconInfo getIconInfo() {
		return iconInfo;
	}

	/**
	 * @param iconInfo of type IconInfo
	 */
	public void setIconInfo(IconInfo iconInfo) {
		this.iconInfo = iconInfo;
	}

	public boolean getTableView() {
		return tableView;
	}

	public void setTableView(boolean tableView) {
		this.tableView = tableView;
	}

	public boolean getAlwaysEnabled() {
		return alwaysEnabled;
	}

	public void setAlwaysEnabled(boolean alwaysEnabled) {
		this.alwaysEnabled = alwaysEnabled;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean getInput() {
		return input;
	}

	public void setInput(boolean input) {
		this.input = input;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<FieldComponent> getComponents() {
		return components;
	}

	public void setComponents(List<FieldComponent> components) {
		this.components = components;
	}

	public boolean getHideLabel() {
		return hideLabel;
	}

	public void setHideLabel(boolean hideLabel) {
		this.hideLabel = hideLabel;
	}

	public String getCustomClass() {
		return customClass;
	}

	public void setCustomClass(String customClass) {
		this.customClass = customClass;
	}

	public String getAlignment() {
		return alignment;
	}

	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}

	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}

	public boolean getIcon() {
		return icon;
	}

	public void setIcon(boolean icon) {
		this.icon = icon;
	}

	public boolean getAutoCorrect() {
		return autoCorrect;
	}

	public void setAutoCorrect(boolean autoCorrect) {
		this.autoCorrect = autoCorrect;
	}

	public boolean getCapitalization() {
		return capitalization;
	}

	public void setCapitalization(boolean capitalization) {
		this.capitalization = capitalization;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Validate getValidate() {
		return validate;
	}

	public void setValidate(Validate validate) {
		this.validate = validate;
	}

	public FieldDependency getFieldDependency() {
		return fieldDependency;
	}

	public void setFieldDependency(FieldDependency fieldDependency) {
		this.fieldDependency = fieldDependency;
	}

	/**
	 * @return the events of type List
	 */
	public List<Event> getEvents() {
		return events;
	}

	/**
	 * @param events of type List
	 */
	public void setEvents(List<Event> events) {
		this.events = events;
	}

	/**
	 * @return the fieldId of type UUID
	 */
	public UUID getFieldId() {
		return fieldId;
	}

	/**
	 * @param fieldId of type UUID
	 */
	public void setFieldId(UUID fieldId) {
		this.fieldId = fieldId;
	}

	/**
	 * @return the iconAlignment of type String
	 */
	public String getIconAlignment() {
		return iconAlignment;
	}

	/**
	 * @param iconAlignment of type String
	 */
	public void setIconAlignment(String iconAlignment) {
		this.iconAlignment = iconAlignment;
	}

	/**
	 * @return the imageSource of type String
	 */
	public String getImageSource() {
		return imageSource;
	}

	/**
	 * @param imageSource of type String
	 */
	public void setImageSource(String imageSource) {
		this.imageSource = imageSource;
	}

	/**
	 * @return the format of type String
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format of type String
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the allowInput of type boolean
	 */
	public boolean getAllowInput() {
		return allowInput;
	}

	/**
	 * @param allowInput of type boolean
	 */
	public void setAllowInput(boolean allowInput) {
		this.allowInput = allowInput;
	}

	/**
	 * @return the enableDate of type boolean
	 */
	public boolean getEnableDate() {
		return enableDate;
	}

	/**
	 * @param enableDate of type boolean
	 */
	public void setEnableDate(boolean enableDate) {
		this.enableDate = enableDate;
	}

	/**
	 * @return the datePicker of type DatePicker
	 */
	public DatePicker getDatePicker() {
		return datePicker;
	}

	/**
	 * @param datePicker of type DatePicker
	 */
	public void setDatePicker(DatePicker datePicker) {
		this.datePicker = datePicker;
	}

	/**
	 * @return the maxDate of type String
	 */
	public String getMaxDate() {
		return maxDate;
	}

	/**
	 * @param maxDate of type String
	 */
	public void setMaxDate(String maxDate) {
		this.maxDate = maxDate;
	}

	/**
	 * @return the minDate of type String
	 */
	public String getMinDate() {
		return minDate;
	}

	/**
	 * @param minDate of type String
	 */
	public void setMinDate(String minDate) {
		this.minDate = minDate;
	}

	/**
	 * @return the fontColor of type String
	 */
	public String getFontColor() {
		return fontColor;
	}

	/**
	 * @param fontColor of type String
	 */
	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	/**
	 * @return the radius of type String
	 */
	public String getRadius() {
		return radius;
	}

	/**
	 * @param radius of type String
	 */
	public void setRadius(String radius) {
		this.radius = radius;
	}

	/**
	 * @return the width of type String
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * @param width of type String
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * @return the height of type String
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * @param height of type String
	 */
	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * @return the buttonType of type String
	 */
	public String getButtonType() {
		return buttonType;
	}

	/**
	 * @param buttonType of type String
	 */
	public void setButtonType(String buttonType) {
		this.buttonType = buttonType;
	}

	/**
	 * @return the backgroundColor of type String
	 */
	public String getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param backgroundColor of type String
	 */
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * @return the inline of type boolean
	 */
	public boolean getInline() {
		return inline;
	}

	/**
	 * @param inline of type boolean
	 */
	public void setInline(boolean inline) {
		this.inline = inline;
	}

	/**
	 * @return the values of type List
	 */
	public List<LabelDetails> getValues() {
		return values;
	}

	/**
	 * @param values of type List
	 */
	public void setValues(List<LabelDetails> values) {
		this.values = values;
	}

	/**
	 * @return the data of type Data
	 */
	public Data getData() {
		return data;
	}

	/**
	 * @param data of type Data
	 */
	public void setData(Data data) {
		this.data = data;
	}

	/**
	 * @return the valueProperty of type String
	 */
	public String getValueProperty() {
		return valueProperty;
	}

	/**
	 * @param valueProperty of type String
	 */
	public void setValueProperty(String valueProperty) {
		this.valueProperty = valueProperty;
	}

	/**
	 * @return the lazyLoad of type boolean
	 */
	public boolean getLazyLoad() {
		return lazyLoad;
	}

	/**
	 * @param lazyLoad of type boolean
	 */
	public void setLazyLoad(boolean lazyLoad) {
		this.lazyLoad = lazyLoad;
	}

	/**
	 * @return the description of type String
	 */
	public TranslationRequest getDescription() {
		return description;
	}

	/**
	 * @param description of type String
	 */
	public void setDescription(TranslationRequest description) {
		this.description = description;
	}

	/**
	 * @return the selectValues of type String
	 */
	public String getSelectValues() {
		return selectValues;
	}

	/**
	 * @param selectValues of type String
	 */
	public void setSelectValues(String selectValues) {
		this.selectValues = selectValues;
	}

	/**
	 * @return the disableLimit of type boolean
	 */
	public boolean getDisableLimit() {
		return disableLimit;
	}

	/**
	 * @param disableLimit of type boolean
	 */
	public void setDisableLimit(boolean disableLimit) {
		this.disableLimit = disableLimit;
	}

	/**
	 * @return the sort of type String
	 */
	public String getSort() {
		return sort;
	}

	/**
	 * @param sort of type String
	 */
	public void setSort(String sort) {
		this.sort = sort;
	}

	/**
	 * @return the reference of type boolean
	 */
	public boolean getReference() {
		return reference;
	}

	/**
	 * @param reference of type boolean
	 */
	public void setReference(boolean reference) {
		this.reference = reference;
	}

	/**
	 * @return the customFormat of type String
	 */
	public String getCustomFormat() {
		return customFormat;
	}

	/**
	 * @param customFormat of type String
	 */
	public void setCustomFormat(String customFormat) {
		this.customFormat = customFormat;
	}

	public List<FieldComponent> getColumns() {
		return columns;
	}

	public void setColumns(List<FieldComponent> columns) {
		this.columns = columns;
	}

	public String getOffset() {
		return offset;
	}

	public void setOffset(String offset) {
		this.offset = offset;
	}

	public String getPush() {
		return push;
	}

	public void setPush(String push) {
		this.push = push;
	}

	public String getPull() {
		return pull;
	}

	public void setPull(String pull) {
		this.pull = pull;
	}

	public boolean getHideOnChildrenHidden() {
		return hideOnChildrenHidden;
	}

	public void setHideOnChildrenHidden(boolean hideOnChildrenHidden) {
		this.hideOnChildrenHidden = hideOnChildrenHidden;
	}

	public TranslationRequest getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(TranslationRequest placeholder) {
		this.placeholder = placeholder;
	}

	public String getInputFieldType() {
		return inputFieldType;
	}

	public void setInputFieldType(String inputType) {
		this.inputFieldType = inputType;
	}

	public String getFontType() {
		return fontType;
	}

	public void setFontType(String fontType) {
		this.fontType = fontType;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

	public String getLineBreakMode() {
		return lineBreakMode;
	}

	public void setLineBreakMode(String lineBreakMode) {
		this.lineBreakMode = lineBreakMode;
	}

	public boolean isDisableAddingRemovingRows() {
		return disableAddingRemovingRows;
	}

	public void setDisableAddingRemovingRows(boolean disableAddingRemovingRows) {
		this.disableAddingRemovingRows = disableAddingRemovingRows;
	}

	public String getAddAnother() {
		return addAnother;
	}

	public void setAddAnother(String addAnother) {
		this.addAnother = addAnother;
	}

	public String getAddAnotherPosition() {
		return addAnotherPosition;
	}

	public void setAddAnotherPosition(String addAnotherPosition) {
		this.addAnotherPosition = addAnotherPosition;
	}

	public String getRemovePlacement() {
		return removePlacement;
	}

	public void setRemovePlacement(String removePlacement) {
		this.removePlacement = removePlacement;
	}

	public boolean isStriped() {
		return striped;
	}

	public void setStriped(boolean striped) {
		this.striped = striped;
	}

	public boolean isBordered() {
		return bordered;
	}

	public void setBordered(boolean bordered) {
		this.bordered = bordered;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isCondensed() {
		return condensed;
	}

	public void setCondensed(boolean condensed) {
		this.condensed = condensed;
	}

	public boolean isAddSorting() {
		return addSorting;
	}

	public void setAddSorting(boolean addSorting) {
		this.addSorting = addSorting;
	}

	public boolean isAddFilter() {
		return addFilter;
	}

	public void setAddFilter(boolean addFilter) {
		this.addFilter = addFilter;
	}

	public String getStyleType() {
		return styleType;
	}

	public void setStyleType(String styleType) {
		this.styleType = styleType;
	}

	public boolean isClearOnHide() {
		return clearOnHide;
	}

	public void setClearOnHide(boolean clearOnHide) {
		this.clearOnHide = clearOnHide;
	}

	public boolean isAddPagination() {
		return addPagination;
	}

	public void setAddPagination(boolean addPagination) {
		this.addPagination = addPagination;
	}

	public String getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(String numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getSetValue() {
		return setValue;
	}

	public void setSetValue(String setValue) {
		this.setValue = setValue;
	}

	public TranslationRequest getHeaderLabel() {
		return headerLabel;
	}

	public void setHeaderLabel(TranslationRequest headerLabel) {
		this.headerLabel = headerLabel;
	}

	public String getListImageAlignment() {
		return listImageAlignment;
	}

	public void setListImageAlignment(String listImageAlignment) {
		this.listImageAlignment = listImageAlignment;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public ObjectNode getDefaultApiValue() {
		return defaultApiValue;
	}

	public void setDefaultApiValue(ObjectNode defaultApiValue) {
		this.defaultApiValue = defaultApiValue;
	}

	public ObjectNode getApiDataSource() {
		return apiDataSource;
	}

	public void setApiDataSource(ObjectNode apiDataSource) {
		this.apiDataSource = apiDataSource;
	}

	public String getDefaultValueType() {
		return defaultValueType;
	}

	public void setDefaultValueType(String defaultValueType) {
		this.defaultValueType = defaultValueType;
	}

	public String getDefaultStaticValue() {
		return defaultStaticValue;
	}

	public void setDefaultStaticValue(String defaultStaticValue) {
		this.defaultStaticValue = defaultStaticValue;
	}

	public boolean isModifyStatus() {
		return modifyStatus;
	}

	public void setModifyStatus(boolean modifyStatus) {
		this.modifyStatus = modifyStatus;
	}

	public int getTextAreaHeight() {
		return textAreaHeight;
	}

	public void setTextAreaHeight(int textAreaHeight) {
		this.textAreaHeight = textAreaHeight;
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public boolean isItalic() {
		return italic;
	}

	public void setItalic(boolean italic) {
		this.italic = italic;
	}

	public boolean isUnderline() {
		return underline;
	}

	public void setUnderline(boolean underline) {
		this.underline = underline;
	}

	public String getDecimalPlaces() {
		return decimalPlaces;
	}

	public void setDecimalPlaces(String decimalPlaces) {
		this.decimalPlaces = decimalPlaces;
	}

	public boolean isCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(boolean currentDate) {
		this.currentDate = currentDate;
	}

	public boolean isCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(boolean currentTime) {
		this.currentTime = currentTime;
	}

	public ObjectNode getAutoCompleteApi() {
		return autoCompleteApi;
	}

	public void setAutoCompleteApi(ObjectNode autoCompleteApi) {
		this.autoCompleteApi = autoCompleteApi;
	}

	public String getButtonSize() {
		return buttonSize;
	}

	public void setButtonSize(String buttonSize) {
		this.buttonSize = buttonSize;
	}

	public UUID getCustomComponentId() {
		return customComponentId;
	}

	public void setCustomComponentId(UUID customComponentId) {
		this.customComponentId = customComponentId;
	}

	public boolean isAutoAdjust() {
		return autoAdjust;
	}

	public void setAutoAdjust(boolean autoAdjust) {
		this.autoAdjust = autoAdjust;
	}

	public String getCustomComponentName() {
		return customComponentName;
	}

	public void setCustomComponentName(String customComponentName) {
		this.customComponentName = customComponentName;
	}

	public String getCustomComponentDesc() {
		return customComponentDesc;
	}

	public void setCustomComponentDesc(String customComponentDesc) {
		this.customComponentDesc = customComponentDesc;
	}

	public String getHotKeyName() {
		return hotKeyName;
	}

	public void setHotKeyName(String hotKeyName) {
		this.hotKeyName = hotKeyName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(alignment, allowInput, alwaysEnabled, autoCorrect, backgroundColor, buttonType,
				capitalization, components, customClass, customFormat, data, datePicker, defaultValue, description,
				disableLimit, enableDate, events, fieldDependency, fieldId, customComponentId, fontColor, format, height,
				hideLabel, icon, iconAlignment, imageSource, inline, input, key, label, lazyLoad, mask, maxDate,
				minDate, radius, reference, selectValues, sort, style, tableView, type, validate, valueProperty, values,
				width, offset, push, pull, hideOnChildrenHidden, placeholder, inputFieldType, iconInfo, lineBreakMode,
				fontType, fontSize, disableAddingRemovingRows, addAnother, addAnotherPosition, removePlacement, striped,
				bordered, selected, condensed, addSorting, addFilter, styleType, clearOnHide, addPagination,
				numberOfRows, prefix, suffix, setValue, listImageAlignment, headerLabel,  rows, defaultApiValue,
				apiDataSource, defaultValueType, defaultStaticValue, modifyStatus, textAreaHeight,bold,italic,underline,
				decimalPlaces,currentDate, currentTime,autoCompleteApi,buttonSize,autoAdjust,customComponentDesc,customComponentName,hotKeyName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldComponent other = (FieldComponent) obj;
		return Objects.equals(alignment, other.alignment) && Objects.equals(allowInput, other.allowInput)
				&& Objects.equals(alwaysEnabled, other.alwaysEnabled) && Objects.equals(autoCorrect, other.autoCorrect)
				&& Objects.equals(backgroundColor, other.backgroundColor)
				&& Objects.equals(buttonType, other.buttonType) && Objects.equals(capitalization, other.capitalization)
				&& Objects.equals(components, other.components) && Objects.equals(customClass, other.customClass)
				&& Objects.equals(customFormat, other.customFormat) && Objects.equals(data, other.data)
				&& Objects.equals(datePicker, other.datePicker) && Objects.equals(defaultValue, other.defaultValue)
				&& Objects.equals(description, other.description) && Objects.equals(disableLimit, other.disableLimit)
				&& Objects.equals(enableDate, other.enableDate) && Objects.equals(events, other.events)
				&& Objects.equals(fieldDependency, other.fieldDependency) && Objects.equals(fieldId, other.fieldId)
				&& Objects.equals(customComponentId, other.customComponentId) && Objects.equals(fontColor, other.fontColor)
				&& Objects.equals(format, other.format) && Objects.equals(height, other.height)
				&& Objects.equals(hideLabel, other.hideLabel) && Objects.equals(icon, other.icon)
				&& Objects.equals(iconAlignment, other.iconAlignment) && Objects.equals(imageSource, other.imageSource)
				&& Objects.equals(inline, other.inline) && Objects.equals(input, other.input)
				&& Objects.equals(key, other.key) && Objects.equals(label, other.label)
				&& Objects.equals(lazyLoad, other.lazyLoad) && Objects.equals(mask, other.mask)
				&& Objects.equals(maxDate, other.maxDate) && Objects.equals(minDate, other.minDate)
				&& Objects.equals(radius, other.radius) && Objects.equals(reference, other.reference)
				&& Objects.equals(selectValues, other.selectValues) && Objects.equals(sort, other.sort)
				&& Objects.equals(style, other.style) && Objects.equals(tableView, other.tableView)
				&& Objects.equals(type, other.type) && Objects.equals(validate, other.validate)
				&& Objects.equals(valueProperty, other.valueProperty) && Objects.equals(values, other.values)
				&& Objects.equals(width, other.width) && Objects.equals(offset, other.offset)
				&& Objects.equals(push, other.push) && Objects.equals(pull, other.pull)
				&& Objects.equals(hideOnChildrenHidden, other.hideOnChildrenHidden)
				&& Objects.equals(placeholder, other.placeholder)
				&& Objects.equals(inputFieldType, other.inputFieldType) && Objects.equals(iconInfo, other.iconInfo)
				&& Objects.equals(lineBreakMode, other.lineBreakMode) && Objects.equals(fontType, other.fontType)
				&& Objects.equals(fontSize, other.fontSize)
				&& Objects.equals(disableAddingRemovingRows, other.disableAddingRemovingRows)
				&& Objects.equals(addAnother, other.addAnother)
				&& Objects.equals(addAnotherPosition, other.addAnotherPosition)
				&& Objects.equals(removePlacement, other.removePlacement) && Objects.equals(striped, other.striped)
				&& Objects.equals(bordered, other.bordered) && Objects.equals(selected, other.selected)
				&& Objects.equals(condensed, other.condensed) && Objects.equals(addSorting, other.addSorting)
				&& Objects.equals(addFilter, other.addFilter) && Objects.equals(styleType, other.styleType)
				&& Objects.equals(clearOnHide, other.clearOnHide) && Objects.equals(addPagination, other.addPagination)
				&& Objects.equals(numberOfRows, other.numberOfRows) && Objects.equals(prefix, other.prefix)
				&& Objects.equals(suffix, other.suffix) && Objects.equals(setValue, other.setValue)
				&& Objects.equals(listImageAlignment, other.listImageAlignment)
				&& Objects.equals(headerLabel, other.headerLabel) 
				&& Objects.equals(rows, other.rows) && Objects.equals(defaultApiValue, other.defaultApiValue)
				&& Objects.equals(apiDataSource, other.apiDataSource)
				&& Objects.equals(defaultValueType, other.defaultValueType)
				&& Objects.equals(defaultStaticValue, other.defaultStaticValue)
				&& Objects.equals(modifyStatus, other.modifyStatus) && Objects.equals(textAreaHeight, other.textAreaHeight)
				 && Objects.equals(bold, other.bold)&& Objects.equals(italic, other.italic)
				 && Objects.equals(underline, other.underline)  && Objects.equals(decimalPlaces, other.decimalPlaces)
				 && Objects.equals(currentDate, other.currentDate) && Objects.equals(currentTime, other.currentTime)
				 && Objects.equals(autoCompleteApi, other.autoCompleteApi) &&  Objects.equals(buttonSize, other.buttonSize)
				 && Objects.equals(autoAdjust, other.autoAdjust) && Objects.equals(customComponentDesc, other.customComponentDesc)
				 && Objects.equals(customComponentName, other.customComponentName) &&   Objects.equals(hotKeyName, other.hotKeyName);
	}

}
