/**
 * 
 */
package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Nationalized;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the extended_field_base database table.
 * 
 * @author HCL Technologies Ltd.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "EXTENDED_FIELD_BASE")
@NamedQuery(name = "ExtendedFieldBase.findAll", query = "SELECT f FROM ExtendedFieldBase f")
public class ExtendedFieldBase extends BffAuditableData<String> implements Serializable {
	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = -592773720420077149L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID", unique=true, length=16, nullable=false)
	private UUID uid;

	@Column(name = "SEQUENCE")
	public int sequence;

	@Column(name = "PRODUCT_CONFIG_ID", length=16, nullable = false)
	private UUID productConfigId;

	@ManyToOne
	@JoinColumn(name = "EXTENDED_FORM_ID")
	private ExtendedFormBase extendedFormBase;

	@Column(name = "KEYS", length=45)
	@Nationalized
	private String keys;

	@Column(name = "LABEL", length=45)
	@Nationalized
	private String label;

	@Column(name = "CUSTOM_FORMAT", length=255)
	@Nationalized
	private String customFormat;

	@Column(name = "BUTTON_TYPE", length=45)
	@Nationalized
	private String buttonType;

	@Column(name = "FORMAT", length=255)
	@Nationalized
	private String format;

	@Column(name = "IMAGE_SOURCE", length=255)
	@Nationalized
	private String imageSource;

	@Column(name = "ALIGNMENT", length=45)
	@Nationalized
	private String alignment;

	@Column(name = "STYLE", length=45)
	@Nationalized
	private String style;

	@Column(name = "STYLE_FONT_TYPE", length=45)
	@Nationalized
	private String styleFontType;

	@Column(name = "STYLE_FONT_SIZE", length=45)
	@Nationalized
	private String styleFontSize;

	@Column(name = "STYLE_FONT_COLOR", length=45)
	@Nationalized
	private String styleFontColor;

	@Column(name = "STYLE_BACKGROUND_COLOR", length=45)
	@Nationalized
	private String styleBackgroundColor;

	@Column(name = "STYLE_FONT_WEIGHT", length=45)
	@Nationalized
	private String styleFontWeight;

	@Column(name = "STYLE_WIDTH", length=45)
	@Nationalized
	private String styleWidth;

	@Column(name = "STYLE_HEIGHT", length=45)
	@Nationalized
	private String styleHeight;

	@Column(name = "STYLE_PADDING", length=45)
	@Nationalized
	private String stylePadding;

	@Column(name = "STYLE_MARGIN", length=45)
	@Nationalized
	private String styleMargin;

	@Column(name = "INLINE")
	private boolean inline;

	@Column(name = "ICON")
	private boolean icon;

	@Column(name = "AUTOCORRECT")
	private boolean autoCorrect;

	@Column(name = "CAPITALIZATION")
	private boolean capitalization;

	@Column(name = "TYPE", length=45)
	@Nationalized
	private String type;

	@Column(name = "INPUT")
	private boolean input;

	@Column(name = "DEFAULT_VALUE")
	@Nationalized
	@Lob
	private String defaultValue;

	@Column(name = "TABLE_VIEW")
	private boolean tableView;

	@Column(name = "VALUE_PROPERTY", length=45)
	@Nationalized
	private String valueProperty;

	@Column(name = "FONT_COLOR", length=45)
	@Nationalized
	private String fontColor;

	@Column(name = "ALLOW_INPUT")
	private boolean allowInput;

	@Column(name = "ENABLE_DATE")
	private boolean enableDate;

	@Column(name = "DATEPICKER_MINDATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date datePickerMinDate;

	@Column(name = "DATEPICKER_MAXDATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date datePickerMaxDate;
	@Column(name = "VALIDATE_INTEGER", length=45)
	@Nationalized
	private String validateInteger;

	@Column(name = "FIELD_DEPENDENCY_SHOW_CONDITION")
	@Lob
	@Nationalized
	private String fieldDependencyShowCondition;

	@Column(name = "FIELD_DEPENDENCY_HIDE_CONDITION")
	@Lob
	@Nationalized
	private String fieldDependencyHideCondition;

	@Column(name = "FIELD_DEPENDENCY_ENABLE_CONDITION")
	@Lob
	@Nationalized
	private String fieldDependencyEnableCondition;

	@Column(name = "FIELD_DEPENDENCY_DISABLE_CONDITION")
	@Lob
	@Nationalized
	private String fieldDependencyDisableCondition;

	@Column(name = "FIELD_DEPENDENCY_REQUIRED_CONDITION")
	@Lob
	@Nationalized
	private String fieldDependencyRequiredCondition;

	@Column(name = "SET_VALUE")
	@Nationalized
	@Lob
	private String fieldDependencySetValue;

	@Column(name = "FIELD_DEPENDENCY_REQUIRED")
	private boolean fieldDependencyRequired;

	@Column(name = "FIELD_DEPENDENCY_HIDDEN")
	private boolean fieldDependencyHidden;

	@Column(name = "FIELD_DEPENDENCY_DISABLED")
	private boolean fieldDependencyDisabled;

	@Column(name = "HIDE_LABEL")
	private boolean hideLabel;

	@Column(name = "CUSTOM_CLASS", length=255)
	@Nationalized
	private String customClass;

	@Column(name = "MASK")
	private boolean mask;

	@Column(name = "ALWAYS_ENABLED")
	private boolean alwaysEnabled;

	@Column(name = "LAZY_LOAD")
	private boolean lazyLoad;

	@Column(name = "DESCRIPTION", length=255)
	@Nationalized
	private String description;

	@Column(name = "SELECT_VALUES", length=45)
	@Nationalized
	private String selectValues;

	@Column(name = "DISABLE_LIMIT")
	private boolean disableLimit;

	@Column(name = "SORT", length=45)
	@Nationalized
	private String sort;

	@Column(name = "REFERENCE")
	private boolean reference;

	@Column(name = "RADIUS", length=45)
	@Nationalized
	private String radius;

	@Column(name = "BACKGROUND_COLOR", length=45)
	@Nationalized
	private String backGroundColor;

	@Column(name = "WIDTH", length=45)
	@Nationalized
	private String width;

	@Column(name = "HEIGHT", length=45)
	@Nationalized
	private String height;

	@Column(name = "MAX_DATE", length=45)
	@Nationalized
	private String maxDate;

	@Column(name = "MIN_DATE", length=45)
	@Nationalized
	private String minDate;

	@Column(name = "ICON_ALIGNMENT", length=45)
	@Nationalized
	private String iconAlignment;

	@Column(name = "OFFSET_BY")
	@Nationalized
	private String offset;

	@Column(name = "PUSH")
	@Nationalized
	private String push;

	@Column(name = "PULL")
	@Nationalized
	private String pull;

	@Column(name = "HIDE_ON_CHILDREN_HIDDEN")
	private boolean hideOnChildrenHidden;

	@OneToMany(mappedBy = "extendedField", cascade = CascadeType.ALL)
	private List<ExtendedEventsBase> extendedEventsBase = new ArrayList<>();
	@OrderBy(value = "datavalue")
	@OneToMany(mappedBy = "extendedField", cascade = CascadeType.ALL)
	private List<ExtendedDataBase> extendedDataBase = new ArrayList<>();
	@OrderBy(value = "labelValue")
	@OneToMany(mappedBy = "extendedField", cascade = CascadeType.ALL)
	private List<ExtendedFieldValuesBase> extendedFieldValuesBase = new ArrayList<>();

	@Column(name = "LINKED_COMPONENT_ID", length=16)
	private UUID linkedComponentId;
	
	@Column(name = "VALIDATE_PATTERN", length=200)
	@Nationalized
	private String validatePattern;

	@Column(name = "EXTENDED_PARENT_FIELD_ID", length=16)
	private UUID extendedFromFieldId;

	@Column(name = "PLACEHOLDER")
	@Nationalized
	private String placeHolder;

	@Column(name = "INPUT_TYPE")
	@Nationalized
	private String inputType;

	@Column(name = "ICON_NAME")
	@Nationalized
	private String iconName;

	@Column(name = "ICON_CODE")
	@Nationalized
	private String iconCode;

	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name="PARENT_FIELD_UN_ID")
	private ExtendedFieldBase parentField;

	@OneToMany(mappedBy = "parentField", cascade = CascadeType.ALL)
	private List<ExtendedFieldBase> childFields = new ArrayList<>();

	@Column(name = "LINE_BREAK_MODE")
	@Nationalized
	private String lineBreakMode;

	@Column(name = "FONT_TYPE")
	@Nationalized
	private String fontType;

	@Column(name = "FONT_SIZE")
	@Nationalized
	private String fontSize;
	
	@Column(name = "PREFIX",length=45)
	@Nationalized
	private String prefix;

	@Column(name = "SUFFIX", length=45)
	@Nationalized
	private String suffix;

	@Column(name = "LIST_IMAGE_ALIGNMENT",length = 45)
	private String listImageAlignment;
	@Column(name = "DISABLE_ADDING_REMOVING_ROWS")
	private boolean disableAddingRemovingRows;
	  
	@Column(name = "ADD_ANOTHER",length=30)
	@Nationalized
	private String addAnother;
	  
	@Column(name = "ADD_ANOTHER_POSITION",length=30)
	@Nationalized
	private String addAnotherPosition;
	  
	@Column(name = "REMOVE_PLACEMENT",length=30)
	@Nationalized
	private String removePlacement;
	  
	@Column(name = "STRIPED")
	private boolean striped;
	  
	@Column(name = "BORDERED")
	private boolean bordered;
	  
	@Column(name = "SELECTED")
	private boolean selected;
	  
	@Column(name = "CONDENSED")
	private boolean condensed;
	  
	@Column(name = "ADD_SORTING")
	private boolean addSorting;

	@Column(name = "ADD_FILTER")
	private boolean addFilter;
	  
	@Column(name = "STYLE_TYPE",length=30)
	@Nationalized
	private String styleType;
	  
	@Column(name = "CLEAR_ON_HIDE")
	private boolean clearOnHide;

	@Column(name = "NUMBER_OF_ROWS",length = 5)
	@Nationalized
	private String numberOfRows;
	  
	@Column(name = "ADD_PAGINATION")
	private boolean addPagination;
	@Column(name = "HEADER_LABEL",length = 45)
	@Nationalized
	private String headerLabel;
	
	@Column(name = "ROWS",length = 45)
	@Nationalized
	private int rows;
	@Nationalized
	@Lob
	@Column(name = "DEFAULT_API_VALUE")
	private String defaultApiValue;
	@Nationalized
	@Lob
	@Column(name = "API_DATA_SOURCE")
	private String apiDataSource;
	
	@Column(name = "DEFAULT_VALUE_TYPE", length = 45)
	@Nationalized
	private String defaultValueType;
	
	@Column(name = "DEFAULT_STATIC_VALUE")
	@Nationalized
	@Lob
	private String defaultStaticValue;

	@Column(name = "MODIFY_STATUS")
	private boolean modifyStatus;
	
	@Column(name = "VALIDATE_MIN",length = 45)
	@Nationalized
	private Double validateMin;
	@Column(name = "VALIDATE_MAX",length = 255)
	@Nationalized
	private Double validateMax;
	@Column(name = "VALIDATE_MIN_LENGTH", length=45)
	@Nationalized
	private Double validateMinLength;
	@Column(name = "VALIDATE_MAX_LENGTH", length=45)
	@Nationalized
	private Double validateMaxLength;
	@Column(name = "VALIDATE_MIN_DATE", length=45)
	@Nationalized
	private String validateMinDate;
	@Column(name = "VALIDATE_MAX_DATE", length=45)
	@Nationalized
	private String validateMaxDate;
	@Column(name = "VALIDATE_MIN_TIME", length=45)
	@Nationalized
	private String validateMinTime;
	@Column(name = "VALIDATE_MAX_TIME", length=45)
	@Nationalized
	private String validateMaxTime;
	
	@Column(name = "VALIDATE_MIN_ROW", length=10)
	@Nationalized
	private String validateMinRow;
	@Column(name = "VALIDATE_MAX_ROW", length=10)
	@Nationalized
	private String validateMaxRow;
	
	@Column(name = "TEXT_AREA_HEIGHT")
	private int textAreaHeight;
	
	@Column(name = "BOLD")
	private boolean bold;
	
	@Column(name = "ITALIC")
	private boolean italic;
	
	@Column(name = "UNDERLINE")
	private boolean underline;
	
	@Column(name = "DECIMAL_PLACES" , length=45)
	private String decimalPlaces;
	
	@Column(name = "CURRDATEDEFSET")
	private boolean currDateDefSet;
	
	@Column(name = "CURRTIMEDEFSET")
	private boolean currDateTimeSet; 
	
	@Column(name = "AUTOCOMPLETE_API")
	@Nationalized
	@Lob
	private String autoCompleteApi;
	
	@Column(name = "BUTTON_SIZE",length = 45)
	@Nationalized
	private String buttonSize;
	
	@Column(name = "AUTO_ADJUST")
	private boolean autoAdjust;
	
	@Column(name = "HOT_KEY_NAME" , length=255)
	@Nationalized
	private String hotKeyName;


	/**
	 * Copy constructor to clone field to be extended.
	 * @param field
	 */
	public ExtendedFieldBase(Field field) {
		this.sequence = field.getSequence();
		this.productConfigId = field.getProductConfigId();
		this.keys = field.getKeys();
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
		this.validateMin = field.getValidateMin();
		this.validateMax = field.getValidateMax();
		this.validateInteger = field.getValidateInteger();
		this.fieldDependencyShowCondition = field.getFieldDependencyShowCondition();
		this.fieldDependencyHideCondition = field.getFieldDependencyHideCondition();
		this.fieldDependencyEnableCondition = field.getFieldDependencyEnableCondition();
		this.fieldDependencyDisableCondition = field.getFieldDependencyDisableCondition();
		this.fieldDependencyRequiredCondition = field.getFieldDependencyRequiredCondition();
		this.fieldDependencySetValue = field.getFieldDependencySetValue();
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
		this.offset = field.getOffset();
		this.push = field.getPush();
		this.pull = field.getPull();
		this.hideOnChildrenHidden = field.isHideOnChildrenHidden();
		this.validatePattern = field.getValidatePattern();
		this.linkedComponentId = field.getLinkedComponentId();
		if(field.getExtendedFromFieldId() != null) {
			this.extendedFromFieldId = field.getExtendedFromFieldId();			
		}else {
			this.extendedFromFieldId = field.getUid();
		}
		this.placeHolder = field.getPlaceHolder();
		this.inputType = field.getInputType();
		this.iconName = field.getIconName();
		this.iconCode = field.getIconCode();
		this.lineBreakMode = field.getLineBreakMode();
		this.fontSize=field.getFontSize();
		this.fontType= field.getFontType();
		this.uid = field.getUid();
		this.disableAddingRemovingRows = field.isDisableAddingRemovingRows();
		this.addAnother = field.getAddAnother();
		this.addAnotherPosition = field.getAddAnotherPosition();
		this.removePlacement = field.getRemovePlacement();
		this.striped = field.isStriped();
		this.bordered = field.isBordered();
		this.selected = field.isSelected();
		this.condensed = field.isCondensed();
		this.addSorting = field.isAddSorting();
		this.addFilter = field.isAddFilter();
		this.styleType = field.getStyleType();
		this.clearOnHide = field.isClearOnHide();
		this.numberOfRows = field.getNumberOfRows();
		this.addPagination = field.isAddPagination();
		this.prefix=field.getPrefix();
		this.suffix=field.getSuffix();
		this.listImageAlignment = field.getListImageAlignment();
		this.headerLabel = field.getHeaderLabel();
		this.rows=field.getRows();
		this.apiDataSource = field.getApiDataSource();
		this.defaultApiValue = field.getDefaultApiValue();
		this.defaultStaticValue = field.getDefaultStaticValue();
		this.defaultValueType = field.getDefaultValueType();
		this.modifyStatus = field.isModifyStatus();
		this.validateMaxLength=field.getValidateMaxLength();
		this.validateMinLength=field.getValidateMinLength();
		this.validateMaxDate=field.getValidateMaxDate();
		this.validateMinDate=field.getValidateMinDate();
		this.validateMinTime=field.getValidateMinTime();
		this.validateMaxTime=field.getValidateMaxTime();
		this.validateMinRow=field.getValidateMinRow();
		this.validateMaxRow=field.getValidateMaxRow();
		this.textAreaHeight = field.getTextAreaHeight();
		this.bold = field.isBold();
		this.italic = field.isItalic();
		this.underline = field.isUnderline();
		this.decimalPlaces = field.getDecimalPlaces();
		this.currDateDefSet = field.isCurrDateDefSet();
		this.currDateTimeSet = field.isCurrDateTimeSet();
		this.autoCompleteApi = field.getAutoCompleteApi();
		this.buttonSize = field.getButtonSize();
		this.autoAdjust = field.isAutoAdjust();
		this.hotKeyName = field.getHotKeyName();
		
		buildFieldHelper(field);
		
		if(field.getChildFields() != null && !field.getChildFields().isEmpty()) {
			for(Field childField : field.getChildFields()) {
				addChildFields(new ExtendedFieldBase(childField));
			}
			
		}
	}

	/**
	 * @param field
	 */
	private void buildFieldHelper(Field field) {
		if (field.getEvents() != null && !field.getEvents().isEmpty()) {
			for (Events event : field.getEvents()) {
				addEvents(new ExtendedEventsBase(event.getUid(), event.getEvent(), event.getAction()));
			}
		}
		if (field.getData() != null && !field.getData().isEmpty()) {
			for (Data dat : field.getData()) {
				addData(new ExtendedDataBase(dat.getDatalabel(), dat.getDatavalue(), dat.getUid()));
			}
		}
		if (field.getValues() != null && !field.getValues().isEmpty()) {
			for (FieldValues value : field.getValues()) {
				addValues(new ExtendedFieldValuesBase(value.getLabel(), value.getLabelValue(), value.getUid()));
			}
		}
	}
	
	
	
	public ExtendedEventsBase addEvents(ExtendedEventsBase events) {
		getExtendedEventsBase().add(events);
		events.setExtendedField(this);

		return events;
	}

	public ExtendedEventsBase removeEvents(ExtendedEventsBase events) {
		getExtendedEventsBase().remove(events);
		events.setExtendedField(null);

		return events;
	}
	public ExtendedDataBase addData(ExtendedDataBase data) {
		getExtendedDataBase().add(data);
		data.setExtendedField(this);
		return data;
	}

	public ExtendedDataBase removeData(ExtendedDataBase data) {
		getExtendedDataBase().remove(data);
		data.setExtendedField(null);
		return data;
	}
	public ExtendedFieldValuesBase addValues(ExtendedFieldValuesBase values) {
		getExtendedFieldValuesBase().add(values);
		values.setExtendedField(this);
		return values;
	}

	public ExtendedFieldValuesBase removeValues(ExtendedFieldValuesBase values) {
		getExtendedFieldValuesBase().remove(values);
		values.setExtendedField(null);
		return values;
	}
	public ExtendedFieldBase addChildFields(ExtendedFieldBase childField) {
		getChildFields().add(childField);
		childField.setParentField(this);
		return childField;
	}
	public ExtendedFieldBase removeChildFields(ExtendedFieldBase childField) {
		getChildFields().remove(childField);
		childField.setParentField(null);
		return childField;
	}
}
