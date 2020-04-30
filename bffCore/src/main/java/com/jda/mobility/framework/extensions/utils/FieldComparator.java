/**
 * 
 */
package com.jda.mobility.framework.extensions.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.entity.Data;
import com.jda.mobility.framework.extensions.entity.Events;
import com.jda.mobility.framework.extensions.entity.ExtendedDataBase;
import com.jda.mobility.framework.extensions.entity.ExtendedEventsBase;
import com.jda.mobility.framework.extensions.entity.ExtendedFieldBase;
import com.jda.mobility.framework.extensions.entity.ExtendedFieldValuesBase;
import com.jda.mobility.framework.extensions.entity.Field;
import com.jda.mobility.framework.extensions.entity.FieldValues;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.TriggerAction;

/**
 * The class provides custom comparator to compare field and extendedFielBase object only on property defined.
 * ExtendedFieldBase will hold data from where this field was extended at time of clone process.
 *
 */
public class FieldComparator{
	private static final Logger LOGGER = LogManager.getLogger(FieldComparator.class);
	private FieldComparator() {
		super();
	}

	/**Compare all field attributes
	 * @param field Attributes of Field object
	 * @param exField Attributes of ExtendedFieldBase object
	 * @return int Returns 0 if difference is found
	 */
	public static int compare(Field field, ExtendedFieldBase exField){
		   return new CompareToBuilder()
				   .append(field.getKeys(), exField.getKeys())
				   .append(field.getLabel(), exField.getLabel())
				   .append(field.getCustomFormat(), exField.getCustomFormat())
				   .append(field.getButtonType(), exField.getButtonType())
				   .append(field.getFormat(), exField.getFormat())
				   .append(field.getImageSource(), exField.getImageSource())
				   .append(field.getAlignment(), exField.getAlignment())
				   .append(field.getStyle(), exField.getStyle())
				   .append(field.getStyleFontType(), exField.getStyleFontType())
				   .append(field.getStyleFontSize(), exField.getStyleFontSize())
				   .append(field.getStyleFontColor(), exField.getStyleFontColor())
				   .append(field.getStyleBackgroundColor(), exField.getStyleBackgroundColor())
				   .append(field.getStyleFontWeight(), exField.getStyleFontWeight())
				   .append(field.getStyleWidth(), exField.getStyleWidth())
				   .append(field.getStyleHeight(), exField.getStyleHeight())
				   .append(field.getStylePadding(), exField.getStylePadding())
				   .append(field.getStyleMargin(), exField.getStyleMargin())
				   .append(field.isInline(), exField.isInline())
				   .append(field.isIcon(), exField.isIcon())
				   .append(field.isAutoCorrect(), exField.isAutoCorrect())
				   .append(field.isCapitalization(), exField.isCapitalization())
				   .append(field.getType(), exField.getType())
				   .append(field.isInput(), exField.isInput())
				   .append(field.getDefaultValue(), exField.getDefaultValue())
				   .append(field.isTableView(), exField.isTableView())
				   .append(field.getValueProperty(), exField.getValueProperty())
				   .append(field.getFontColor(), exField.getFontColor())
				   .append(field.isAllowInput(), exField.isAllowInput())
				   .append(field.isEnableDate(), exField.isEnableDate())
				   .append(field.getDatePickerMinDate(), exField.getDatePickerMinDate())
				   .append(field.getDatePickerMaxDate(), exField.getDatePickerMaxDate())
				   .append(field.getValidateMin(),  exField.getValidateMin())
				   .append(field.getValidateMax(), exField.getValidateMax())
				   .append(field.getValidateInteger(), exField.getValidateInteger())
				   .append(field.getFieldDependencyShowCondition(), exField.getFieldDependencyShowCondition())
				   .append(field.getFieldDependencyHideCondition(), exField.getFieldDependencyHideCondition())
				   .append(field.getFieldDependencyEnableCondition(), exField.getFieldDependencyEnableCondition())
				   .append(field.getFieldDependencyDisableCondition(), exField.getFieldDependencyDisableCondition())
				   .append(field.getFieldDependencyRequiredCondition(), exField.getFieldDependencyRequiredCondition())
				   .append(field.getFieldDependencySetValue(), exField.getFieldDependencySetValue())
				   .append(field.isFieldDependencyRequired(), exField.isFieldDependencyRequired())
				   .append(field.isFieldDependencyHidden(), exField.isFieldDependencyHidden())
				   .append(field.isFieldDependencyDisabled(), exField.isFieldDependencyDisabled())
				   .append(field.isHideLabel(), exField.isHideLabel())
				   .append(field.getCustomClass(), exField.getCustomClass())
				   .append(field.isMask(), exField.isMask())
				   .append(field.isAlwaysEnabled(), exField.isAlwaysEnabled())
				   .append(field.isLazyLoad(), exField.isLazyLoad())
				   .append(field.getDescription(), exField.getDescription())
				   .append(field.getSelectValues(), exField.getSelectValues())
				   .append(field.isDisableLimit(), exField.isDisableLimit())
				   .append(field.getSort(), exField.getSort())
				   .append(field.isReference(), exField.isReference())
				   .append(field.getRadius(), exField.getRadius())
				   .append(field.getBackGroundColor(), exField.getBackGroundColor())
				   .append(field.getWidth(), exField.getWidth())
				   .append(field.getHeight(), exField.getHeight())
				   .append(field.getMaxDate(), exField.getMaxDate())
				   .append(field.getMinDate(), exField.getMinDate())
				   .append(field.getIconAlignment(), exField.getIconAlignment())
				   .append(field.getOffset(), exField.getOffset())
				   .append(field.getPush(), exField.getPush())
				   .append(field.getPull(), exField.getPull())
				   .append(field.isHideOnChildrenHidden(), exField.isHideOnChildrenHidden())
				   .append(field.getValidatePattern(), exField.getValidatePattern())
				   .append(field.getLinkedComponentId(), exField.getLinkedComponentId())
				   .append(field.getPlaceHolder(), exField.getPlaceHolder())
				   .append(field.getInputType(), exField.getInputType())
				   .append(field.getIconName(), exField.getIconName())
				   .append(field.getIconCode(), exField.getIconCode())
				   .append(field.getLineBreakMode(), exField.getLineBreakMode())
				   .append(field.getFontSize(), exField.getFontSize())
				   .append(field.getFontType(), exField.getFontType())
				   .append(field.isDisableAddingRemovingRows(), exField.isDisableAddingRemovingRows())
				   .append(field.getAddAnother(), exField.getAddAnother())
				   .append(field.getAddAnotherPosition(), exField.getAddAnotherPosition())
				   .append(field.getRemovePlacement(), exField.getRemovePlacement())
				   .append(field.isStriped(), exField.isStriped())
				   .append(field.isBordered(), exField.isBordered())
				   .append(field.isSelected(), exField.isSelected())
				   .append(field.isCondensed(), exField.isCondensed())	
				   .append(field.isAddSorting(), exField.isAddSorting())
				   .append(field.isAddFilter(), exField.isAddFilter())
				   .append(field.getStyleType(), exField.getStyleType())
				   .append(field.isClearOnHide(), exField.isClearOnHide())
				   .append(field.getNumberOfRows(), exField.getNumberOfRows())
				   .append(field.isAddPagination(), exField.isAddPagination())
				   .append(field.getPrefix(), exField.getPrefix())
				   .append(field.getSuffix(), exField.getSuffix())
				   .append(field.getListImageAlignment(), exField.getListImageAlignment())
				   .append(field.getHeaderLabel(), exField.getHeaderLabel())
				   .append(field.getHotKeyName(), exField.getHotKeyName())
				   .append(field.getApiDataSource(), exField.getApiDataSource())
				   .append(field.getDefaultApiValue(), exField.getDefaultApiValue())
				   .append(field.getDefaultStaticValue(), exField.getDefaultStaticValue())
				   .append(field.getDefaultValueType(), exField.getDefaultValueType())
				   .append(field.getTextAreaHeight(), exField.getTextAreaHeight())
				   .append(field.getRows(), exField.getRows())
				   .append(field.getValidateMinLength(), exField.getValidateMinLength())
				   .append(field.getValidateMaxLength(), exField.getValidateMaxLength())
				   .append(field.getValidateMinDate(), exField.getValidateMinDate())
				   .append(field.getValidateMaxDate(), exField.getValidateMaxDate())
				   .append(field.getValidateMinTime(), exField.getValidateMinTime())
				   .append(field.getValidateMaxTime(), exField.getValidateMaxTime())
				   .append(field.getValidateMinRow(), exField.getValidateMinRow())
				   .append(field.getValidateMaxRow(), exField.getValidateMaxRow())
				   .append(field.getTextAreaHeight(), exField.getTextAreaHeight())
				   .append(field.isBold(), exField.isBold())
				   .append(field.isItalic(), exField.isItalic())
				   .append(field.isUnderline(), exField.isUnderline())
				   .append(field.getDecimalPlaces(), exField.getDecimalPlaces())
				   .append(field.isCurrDateDefSet(), exField.isCurrDateDefSet())
				   .append(field.isCurrDateTimeSet(), exField.isCurrDateTimeSet())
				   .append(field.getAutoCompleteApi(), exField.getAutoCompleteApi())
				   .append(field.getButtonSize(), exField.getButtonSize())
				   .append(field.isAutoAdjust(), exField.isAutoAdjust())
				 .toComparison();
	}
	
	/**Compare the field attributes of the field
	 * 
	 * @param <T>
	 * @param <S>
	 * @param fieldAttrList List of field attributes
	 * @param exFieldAttrList List of extended attributes
	 * @return boolean Returns true if difference is found
	 */
	public static <T, S> boolean compareFieldAttributeOfTypeList(List<T> fieldAttrList, List<S> exFieldAttrList) {
		boolean compareList = false;
		if (CollectionUtils.isEmpty(fieldAttrList) && CollectionUtils.isEmpty(exFieldAttrList)) {
			compareList = true;
		} 
		//If list size are same , then compare object by object and look for difference. 
		//Otherwise if size are unequal there is some insertion or deletion at either parent or child and it is considered as change so return false.
		else if (!CollectionUtils.isEmpty(fieldAttrList) && !CollectionUtils.isEmpty(exFieldAttrList) 
							&& fieldAttrList.size() == exFieldAttrList.size()) {
				compareList = compareFieldObject(fieldAttrList, exFieldAttrList, compareList);
		}  

		return compareList;
	}

	/**
	 * @param <T>
	 * @param <S>
	 * @param fieldAttrList List of Event/Data/Value Field Object
	 * @param exFieldAttrList List of Event/data/Value ExtendedField object
	 * @param compareList List of compared object
	 * @return boolean Returns true if differences is found
	 */
	private static <T, S> boolean compareFieldObject(List<T> fieldAttrList, List<S> exFieldAttrList, boolean compareList) {
		for (T fieldAttr : fieldAttrList) {
			Events event = null;
			Data data = null;
			FieldValues value = null;
			if (fieldAttr instanceof Events) {
				event = (Events) fieldAttr;
			} else if (fieldAttr instanceof Data) {
				data = (Data) fieldAttr;
			} else if (fieldAttr instanceof FieldValues) {
				value = (FieldValues) fieldAttr;
			}

			if (event != null && event.getUid() != null && event.getExtendedFromEventId() != null) {
				compareList = fieldAttrCompare(exFieldAttrList, compareList, event);
				
			} else if (data != null && data.getUid() != null && data.getExtendedFromDataId() != null) {
				compareList = fieldAttrCompare(exFieldAttrList, compareList, data);

			} else if (value != null && value.getUid() != null && value.getExtendedFromFieldValuesId() != null) {
				compareList = fieldAttrCompare(exFieldAttrList, compareList, value);

			} else {
				// new Event to be persisted or already persisted event which is not extended is
				// modified.Comparison is False
				compareList = false;
				break;
			}
			//CompareList = false , then differences is found already , so return
			if(!compareList) {
				break;
			}
			
			//If compare result = true, check for is there any deletion at clone field in comparison to parent
			else
			{
				ExtendedDataBase exData = null;
				ExtendedFieldValuesBase exValue = null;
				
				for (S obj : exFieldAttrList) {		
					if (obj instanceof ExtendedDataBase) {
						exData = (ExtendedDataBase) obj;
						//If New Data is found at parent, then note as difference
						if(!exData.isCompared())
						{
							compareList = false;
						}
					
					} else if (obj instanceof ExtendedFieldValuesBase) {
						exValue = (ExtendedFieldValuesBase) obj;
						//If New value is found at parent, then note as difference
						if(!exValue.isCompared())
						{
							compareList = false;
						}
					}
				}
			}

		}
		return compareList;
	}

	
	/** Compare the events, data and values between field and extended field
	 * @param <S>
	 * @param exFieldAttrList List of compared Object
	 * @param compareList  Flag to hold comparison status 
	 * @param obj Object to be compared  
	 * @return boolean Returns true if differnence is found
	 */
	private static <T, S> boolean fieldAttrCompare(List<T> exFieldAttrList, boolean compareList, S objEntity) {
		Events event = null;
		Data data = null;
		FieldValues value = null;
		if (objEntity instanceof Events) {
			event = (Events) objEntity;
		} else if (objEntity instanceof Data) {
			data = (Data) objEntity;
		} else if (objEntity instanceof FieldValues) {
			value = (FieldValues) objEntity;
		}
		Boolean compVal = null;
		for (T obj : exFieldAttrList) {
			ExtendedEventsBase exEvent = null;
			ExtendedDataBase exData = null;
			ExtendedFieldValuesBase exValue = null;
			if (event != null && obj instanceof ExtendedEventsBase) {
				exEvent = (ExtendedEventsBase) obj;
				compVal = eventCompare(event, exEvent);
							
			} else if (data != null && obj instanceof ExtendedDataBase) {
				exData = (ExtendedDataBase) obj;
				compVal = dataCompare(data, exData);
			} else if (value != null && obj instanceof ExtendedFieldValuesBase) {
				exValue = (ExtendedFieldValuesBase) obj;
				compVal = fieldValueCompare( value, exValue);
			}
			
			if(compVal != null) {
				compareList = compVal;
			}	
		}
		
		return compareList;
	}

	/**Compare the event and extended event to calculate modifiedStatus
	 * @param event Event of the field 
	 * @param exEvent Event of the extendedField
	 * @return Boolean Returns true if differnce is found
	 */
	private static Boolean eventCompare(Events event, ExtendedEventsBase exEvent) {
		Boolean compare = null;

		if (null != exEvent.getEvent() && event.getEvent() != null && exEvent.getEvent().equals(event.getEvent())) {
			String actualAction = event.getAction();
			String parentAction = exEvent.getAction();
			try {
				if (actualAction != null && parentAction != null) {
					JsonNode actualActiondNode = new ObjectMapper().readTree(actualAction);
					JsonNode parentActionNode = new ObjectMapper().readTree(parentAction);
					JsonNode actualActionType = actualActiondNode.get("actionType");
					JsonNode parentActionType = parentActionNode.get("actionType");
					if (actualActionType != null && !actualActionType.isNull() && parentActionType != null
							&& !parentActionType.isNull()
							&& actualActionType.asText().equalsIgnoreCase(parentActionType.asText())
							&& (actualActionType.asText().equalsIgnoreCase(TriggerAction.NAVIGATE_TO_FORM.name())
									|| actualActionType.asText()
											.equalsIgnoreCase(TriggerAction.NAVIGATE_TO_WORKFLOW.name()))) {
						traverse(actualActiondNode, List.of("formId","defaultFormId","flowId"));
						traverse(parentActionNode, List.of("formId","defaultFormId","flowId"));
						actualAction = actualActiondNode.toString();
						parentAction = parentActionNode.toString();
					} 
				}
			} catch (IOException e) {
				LOGGER.log(Level.DEBUG, "Action inside event is not a valid json.");
			}

			int compareValue = new CompareToBuilder()
					.append(parentAction, actualAction)
					.append(exEvent.getEvent(), event.getEvent())
					.append(exEvent.getUid(), event.getExtendedFromEventId())
					.toComparison();
			if (compareValue == 0) {
				compare = true;
			} else {
				compare = false;
			}
		}

		return compare;
	}
	public static void traverse(JsonNode root, List<String> fieldToIgnoreList){
	    
	    if(root.isObject()){
	        Iterator<String> fieldNames = root.fieldNames();

	        while(fieldNames.hasNext()) {
	            String fieldName = fieldNames.next();	          
	            if(fieldToIgnoreList.contains(fieldName)) {
	            	((ObjectNode) root).put(fieldName, BffAdminConstantsUtils.EMPTY_SPACES);
	            }
	            JsonNode fieldValue = root.get(fieldName);
	            traverse(fieldValue, fieldToIgnoreList);
	        }
	    } else if(root.isArray()){
	        ArrayNode arrayNode = (ArrayNode) root;
	        for(int i = 0; i < arrayNode.size(); i++) {
	            JsonNode arrayElement = arrayNode.get(i);
	            traverse(arrayElement, fieldToIgnoreList);
	        }
	    }
	}
	/**Compare the data and extended data to calculate modifiedStatus
	 * @param data Data of the field
	 * @param exData Data of the extended field
	 * @return boolean Return true if differnce is found
	 */
	private static Boolean dataCompare(Data data, ExtendedDataBase exData) {
		
		Boolean compare = null;
		if(null!= data.getDatalabel() && exData.getDatalabel()!=null && data.getDatalabel().equals(exData.getDatalabel()))
		{
			int compareValue = new CompareToBuilder()
					.append(exData.getDatalabel(), data.getDatalabel())
					.append(exData.getDatavalue(), data.getDatavalue())
					.append(exData.getUid(), data.getExtendedFromDataId())
					.toComparison();
			if (compareValue == 0) {
				compare = true;
			}
			else 
			{
				compare = false;
			}
			exData.setCompared(true);
		}
		return compare;

	}
	/**Compare the value and extended value to calculate modifiedStatus
	 * 
	 * @param value Value of the field
	 * @param exValue Value of the extended field
	 * @return boolean returns true if difference is found
	 */
	private static Boolean fieldValueCompare(FieldValues value, ExtendedFieldValuesBase exValue) {
		Boolean compare = null;
		if(null!= value.getLabel() && exValue.getLabel()!=null &&  value.getLabel().equals(exValue.getLabel()))
		{
			int compareValue = new CompareToBuilder()
					.append(exValue.getLabel(), value.getLabel())
					.append(exValue.getLabelValue(), value.getLabelValue())
					.append(exValue.getUid(), value.getExtendedFromFieldValuesId())
					.toComparison();
			if (compareValue == 0) {
				compare = true;
			}
			else
			{
				compare = false;
			}
			exValue.setCompared(true);
		}
		
		return compare;
	}

}