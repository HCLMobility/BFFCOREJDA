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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the custom_field database table.
 * 
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false, exclude = "customComponentMaster")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name = "CUSTOM_FIELD")
@NamedQuery(name = "CustomField.findAll", query = "SELECT f FROM CustomField f")
public class CustomField extends BffAuditableData<String> implements Serializable {

	private static final long serialVersionUID = 1394925478053697091L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID", unique=true, length=16, nullable=false)
	private UUID uid;

	@Column(name = "SEQUENCE")
	public int sequence;

	@Column(name = "PRODUCT_CONFIG_ID", length=16, nullable = false)
	private UUID productConfigId;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOM_COMPONENT_ID")
	private CustomComponentMaster customComponentMaster;

	@Column(name = "KEYS", length = 45)
	@Nationalized
	private String keys;
	@Column(name = "LABEL", length = 45)
	@Nationalized
	private String label;
	@Column(name = "CUSTOM_FORMAT", length = 45)
	@Nationalized
	private String customFormat;
	@Column(name = "BUTTON_TYPE", length = 45)
	@Nationalized
	private String buttonType;
	@Column(name = "FORMAT", length = 255)
	@Nationalized
	private String format;
	@Column(name = "IMAGE_SOURCE", length = 255)
	@Nationalized
	private String imageSource;
	@Column(name = "ALIGNMENT", length = 45)
	@Nationalized
	private String alignment;
	@Column(name = "STYLE", length = 45)
	@Nationalized
	private String style;
	@Column(name = "STYLE_FONT_TYPE", length = 45)
	@Nationalized
	private String styleFontType;
	@Column(name = "STYLE_FONT_SIZE", length = 45)
	@Nationalized
	private String styleFontSize;
	@Column(name = "STYLE_FONT_COLOR", length = 45)
	@Nationalized
	private String styleFontColor;
	@Column(name = "STYLE_BACKGROUND_COLOR", length = 45)
	@Nationalized
	private String styleBackgroundColor;
	@Column(name = "STYLE_FONT_WEIGHT", length = 45)
	@Nationalized
	private String styleFontWeight;
	@Column(name = "STYLE_WIDTH", length = 45)
	@Nationalized
	private String styleWidth;
	@Column(name = "STYLE_HEIGHT", length = 45)
	@Nationalized
	private String styleHeight;
	@Column(name = "STYLE_PADDING", length = 45)
	@Nationalized
	private String stylePadding;
	@Column(name = "STYLE_MARGIN", length = 45)
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
	@Column(name = "TYPE", length = 45)
	@Nationalized
	private String type;
	@Column(name = "INPUT")
	private boolean input;
	@Column(name = "DEFAULT_VALUE")
	@Lob
	@Nationalized
	private String defaultValue;
	@Column(name = "TABLE_VIEW")
	private boolean tableView;
	@Column(name = "VALUE_PROPERTY", length = 45)
	@Nationalized
	private String valueProperty;
	@Column(name = "FONT_COLOR", length = 45)
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
	@Column(name = "VALIDATE_INTEGER", length = 45)
	@Nationalized
	private String validateInteger;
	@Column(name = "FIELD_DEPENDENCY_SHOW_CONDITION", length = 255)
	@Nationalized
	private String fieldDependencyShowCondition;
	@Column(name = "FIELD_DEPENDENCY_HIDE_CONDITION", length = 255)
	@Nationalized
	private String fieldDependencyHideCondition;
	@Column(name = "FIELD_DEPENDENCY_ENABLE_CONDITION", length = 255)
	@Nationalized
	private String fieldDependencyEnableCondition;
	@Column(name = "FIELD_DEPENDENCY_DISABLE_CONDITION", length = 255)
	@Nationalized
	private String fieldDependencyDisableCondition;
	@Column(name = "FIELD_DEPENDENCY_REQUIRED_CONDITION", length = 255)
	@Nationalized
	private String fieldDependencyRequiredCondition;
	@Column(name = "SET_VALUE")
	@Lob
	@Nationalized
	private String fieldDependencySetValue;
	@Column(name = "FIELD_DEPENDENCY_REQUIRED")
	private boolean fieldDependencyRequired;
	@Column(name = "FIELD_DEPENDENCY_HIDDEN")
	private boolean fieldDependencyHidden;
	@Column(name = "FIELD_DEPENDENCY_DISABLED")
	private boolean fieldDependencyDisabled;

	@Column(name = "HIDE_LABEL")
	private boolean hideLabel;
	@Column(name = "CUSTOM_CLASS", length = 45)
	@Nationalized
	private String customClass;
	@Column(name = "MASK")
	private boolean mask;
	@Column(name = "ALWAYS_ENABLED")
	private boolean alwaysEnabled;
	@Column(name = "LAZY_LOAD")
	private boolean lazyLoad;
	@Column(name = "DESCRIPTION", length = 255)
	@Nationalized
	private String description;
	@Column(name = "SELECT_VALUES", length = 45)
	@Nationalized
	private String selectValues;
	@Column(name = "DISABLE_LIMIT")
	private boolean disableLimit;
	@Column(name = "SORT", length = 45)
	@Nationalized
	private String sort;
	@Column(name = "REFERENCE")
	private boolean reference;
	@Column(name = "RADIUS", length = 45)
	@Nationalized
	private String radius;
	@Column(name = "BACKGROUND_COLOR", length = 45)
	@Nationalized
	private String backGroundColor;
	@Column(name = "WIDTH", length = 45)
	@Nationalized
	private String width;
	@Column(name = "HEIGHT", length = 45)
	@Nationalized
	private String height;
	@Column(name = "MAX_DATE", length = 45)
	@Nationalized
	private String maxDate;
	@Column(name = "MIN_DATE", length = 45)
	@Nationalized
	private String minDate;
	@Column(name = "ICON_ALIGNMENT", length = 45)
	@Nationalized
	private String iconAlignment;

	@OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
	private List<CustomEvents> events;

	@OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
	private List<CustomData> data;

	@OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
	private List<CustomFieldValues> values;

	@Column(name = "PARENT_FIELD_ID", length=16)
	private UUID parentFieldId;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "LINKED_COMPONENT_ID", nullable = true)
	@NotFound(action = NotFoundAction.IGNORE)
	private CustomComponentMaster linkedComponentId;

	@Column(name = "VALIDATE_PATTERN", length = 4000)
	@Nationalized
	private String validatePattern;

	@Column(name = "PLACEHOLDER")
	@Nationalized
	private String placeHolder;

	@Column(name = "INPUT_TYPE")
	@Nationalized
	private String inputType;

	@Column(name = "ICON_NAME")
	@Nationalized
	private String iconName;

	@Column(name = "ICON_CODE", length = 50)
	@Nationalized
	private String iconCode;

	@Column(name = "LINE_BREAK_MODE")
	@Nationalized
	private String lineBreakMode;

	@Column(name = "FONT_TYPE")
	@Nationalized
	private String fontType;

	@Column(name = "FONT_SIZE")
	@Nationalized
	private String fontSize;

	@Column(name = "DISABLE_ADDING_REMOVING_ROWS")
	private boolean disableAddingRemovingRows;

	@Column(name = "ADD_ANOTHER", length = 30)
	@Nationalized
	private String addAnother;

	@Column(name = "ADD_ANOTHER_POSITION", length = 30)
	@Nationalized
	private String addAnotherPosition;

	@Column(name = "REMOVE_PLACEMENT", length = 30)
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

	@Column(name = "STYLE_TYPE", length = 30)
	@Nationalized
	private String styleType;

	@Column(name = "CLEAR_ON_HIDE")
	private boolean clearOnHide;

	@Column(name = "NUMBER_OF_ROWS", length = 5)
	@Nationalized
	private String numberOfRows;

	@Column(name = "ADD_PAGINATION")
	private boolean addPagination;

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

	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "PARENT_FIELD_UN_ID")
	private CustomField parentField;
	
	@OneToMany(mappedBy = "parentField", cascade = CascadeType.ALL)
	private List<CustomField> customFieldList = new ArrayList<>();

	@Column(name = "PREFIX", length = 45)
	@Nationalized
	private String prefix;

	@Column(name = "SUFFIX", length = 45)
	@Nationalized
	private String suffix;

	@Column(name = "HEADER_LABEL", length = 45)
	@Nationalized
	private String headerLabel;

	@Column(name = "LIST_IMAGE_ALIGNMENT", length = 45)
	private String listImageAlignment;

	@Column(name = "ROWS", length = 45)
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
	@Lob
	@Nationalized
	private String defaultStaticValue;

	@Column(name = "VALIDATE_MIN", length = 45)
	@Nationalized
	private Double validateMin;
	@Column(name = "VALIDATE_MAX", length = 255)
	@Nationalized
	private Double validateMax;
	@Column(name = "VALIDATE_MIN_LENGTH", length = 45)
	@Nationalized
	private Double validateMinLength;
	@Column(name = "VALIDATE_MAX_LENGTH", length = 45)
	@Nationalized
	private Double validateMaxLength;
	@Column(name = "VALIDATE_MIN_DATE", length = 45)
	@Nationalized
	private String validateMinDate;
	@Column(name = "VALIDATE_MAX_DATE", length = 45)
	@Nationalized
	private String validateMaxDate;
	@Column(name = "VALIDATE_MIN_TIME", length = 45)
	@Nationalized
	private String validateMinTime;
	@Column(name = "VALIDATE_MAX_TIME", length = 45)
	@Nationalized
	private String validateMaxTime;

	@Column(name = "VALIDATE_MIN_ROW", length = 10)
	@Nationalized
	private String validateMinRow;
	@Column(name = "VALIDATE_MAX_ROW", length = 10)
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
	
	@Column(name = "HOT_KEY_NAME",length = 255)
	@Nationalized
	private String hotKeyName;
	
	@Column(name = "AUTO_ADJUST")
	private boolean autoAdjust;
	
	public CustomData addData(CustomData data) {
		getData().add(data);
		data.setField(this);

		return data;
	}

	public CustomData removeData(CustomData data) {
		getData().remove(data);
		data.setField(null);

		return data;
	}

	
	public CustomEvents addEvents(CustomEvents events) {
		getEvents().add(events);
		events.setField(this);

		return events;
	}

	public CustomEvents removeEvents(CustomEvents events) {
		getEvents().remove(events);
		events.setField(null);

		return events;
	}

	

	public CustomFieldValues addValues(CustomFieldValues values) {
		getValues().add(values);
		values.setField(this);

		return values;
	}

	public CustomFieldValues removeValues(CustomFieldValues values) {
		getValues().remove(values);
		values.setField(null);

		return values;
	}

	public CustomField removeChildFields(CustomField childField) {
		getCustomFieldList().remove(childField);
		childField.setParentField(null);
		return childField;
	}
}