package com.jda.mobility.framework.extensions.transformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.dto.DataDto;
import com.jda.mobility.framework.extensions.dto.EventsDto;
import com.jda.mobility.framework.extensions.dto.FieldObjDto;
import com.jda.mobility.framework.extensions.dto.FormObjDto;
import com.jda.mobility.framework.extensions.dto.TabDto;
import com.jda.mobility.framework.extensions.dto.ValuesDto;
import com.jda.mobility.framework.extensions.entity.CustomComponentMaster;
import com.jda.mobility.framework.extensions.entity.Data;
import com.jda.mobility.framework.extensions.entity.Events;
import com.jda.mobility.framework.extensions.entity.ExtendedFieldBase;
import com.jda.mobility.framework.extensions.entity.Field;
import com.jda.mobility.framework.extensions.entity.FieldValues;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.FormCustomComponent;
import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.entity.Tabs;
import com.jda.mobility.framework.extensions.model.Event;
import com.jda.mobility.framework.extensions.model.FieldComponent;
import com.jda.mobility.framework.extensions.model.FormAttributes;
import com.jda.mobility.framework.extensions.model.FormData;
import com.jda.mobility.framework.extensions.model.FormProperties;
import com.jda.mobility.framework.extensions.model.Layer;
import com.jda.mobility.framework.extensions.model.Tab;
import com.jda.mobility.framework.extensions.repository.CustomComponentMasterRepository;
import com.jda.mobility.framework.extensions.repository.DataRepository;
import com.jda.mobility.framework.extensions.repository.EventsRepository;
import com.jda.mobility.framework.extensions.repository.ExtendedFieldBaseRepository;
import com.jda.mobility.framework.extensions.repository.FieldRepository;
import com.jda.mobility.framework.extensions.repository.FieldValuesRepository;
import com.jda.mobility.framework.extensions.repository.FormCustomComponentRepository;
import com.jda.mobility.framework.extensions.repository.FormRepository;
import com.jda.mobility.framework.extensions.repository.ProductConfigRepository;
import com.jda.mobility.framework.extensions.repository.TabRepository;
import com.jda.mobility.framework.extensions.service.ProductPrepareService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffCommonUtil;
import com.jda.mobility.framework.extensions.utils.FieldComparator;

/**
 * Custom create DTO's to and from Field and Form entity's and provide common
 * logic to persist related data.
 * 
 * @author HCL Technologies Ltd.
 */
@Component
public class FormTransformation {
	private static final Logger LOGGER = LogManager.getLogger(FormTransformation.class);

	@Autowired
	private FormRepository formRepo;
	@Autowired
	private CustomComponentMasterRepository customComponentMasterRepo;
	@Autowired
	FieldRepository fieldRepo;
	@Autowired
	private FormCustomComponentRepository formCustomComponentRepo;
	@Autowired
	private DataRepository dataRepo;
	@Autowired
	private EventsRepository eventsRepo;
	@Autowired
	private FieldComponentConverter fieldComponentConverter;
	@Autowired
	private ProductConfigRepository productConfigRepo;
	@Autowired
	private TabRepository tabRepository;
	@Autowired
	private BffCommonUtil bffCommonUtil;
	@Autowired
	private FieldValuesRepository valueRepo;
	@Autowired
	private ExtendedFieldBaseRepository extFieldRepo;
	@Autowired
	private ProductPrepareService productPrepareService;


	/**
	 * @param formRequest
	 * @return FormObjDto
	 */
	public FormObjDto convertToFormObjDto(FormData formRequest) {
		List<FieldObjDto> fieldObjDtos = new ArrayList<>();
		UUID productConfigId = productPrepareService.getCurrentLayerProdConfigId().getUid();
		if (formRequest.getComponents() != null && !formRequest.getComponents().isEmpty()) {
			int masterSeq = 0;
			for (FieldComponent fieldComponent : formRequest.getComponents()) {
				fieldObjDtos.add(convertToFieldObjDto(fieldComponent, formRequest.getFormId(),
						productConfigId, masterSeq++));
			}
		}
		return new FormObjDto(formRequest, fieldObjDtos,productConfigId);
	}

	/**
	 * @param fieldComponent
	 * @param formId
	 * @param productConfigId
	 * @param sequence
	 * @return FieldObjDto
	 */
	private FieldObjDto convertToFieldObjDto(FieldComponent fieldComponent, UUID formId, UUID productConfigId,
			int sequence) {
		List<FieldObjDto> fieldObjDtoList = null;

		if (fieldComponent.getComponents() != null && !fieldComponent.getComponents().isEmpty()) {
			fieldObjDtoList = new ArrayList<>();
			int childSeq = 0;
			for (FieldComponent childfield : fieldComponent.getComponents()) {
				fieldObjDtoList
						.add(convertChildToFieldObjDto(childfield, formId, productConfigId, childSeq++, null).get(0));
			}
		}

		FieldObjDto fieldObjDto = new FieldObjDto(fieldComponent, formId, null, fieldObjDtoList, sequence,productConfigId);
		return fieldObjDto;
	}

	/**
	 * This method used to convert FieldComponent to FieldObjDto. If FieldComponent
	 * has nested columns, it will work as recursive method
	 * 
	 * @param fieldChild
	 * @param formId
	 * @param productConfigId
	 * @param sequence
	 * @param childFieldObjDtoList
	 * @return List<FieldObjDto>
	 */
	private List<FieldObjDto> convertChildToFieldObjDto(FieldComponent fieldChild, UUID formId, UUID productConfigId,
			int sequence, List<FieldObjDto> childFieldObjDtoList) {
		int childSeq = 0;
		int grandChildSeq = 0;
		List<FieldObjDto> fieldObjDtoList = null;
		List<FieldObjDto> columnFieldObjDtoList = null;
		List<FieldObjDto> parentFieldObjDtoList = new ArrayList<>();
		if (BffAdminConstantsUtils.COLUMNS.equalsIgnoreCase(fieldChild.getType())) {
			columnFieldObjDtoList = new ArrayList<>();
			for (FieldComponent column : fieldChild.getColumns()) {
				fieldObjDtoList = new ArrayList<>();
				if (BffAdminConstantsUtils.COLUMN.equalsIgnoreCase(column.getType())
						&& column.getComponents() != null) {
					for (FieldComponent columnField : column.getComponents()) {
						if (BffAdminConstantsUtils.COLUMNS.equalsIgnoreCase(columnField.getType()) 
								|| BffAdminConstantsUtils.DATAGRID.equalsIgnoreCase(columnField.getType())
								|| BffAdminConstantsUtils.LISTVIEW.equalsIgnoreCase(columnField.getType())) {
							fieldObjDtoList.add(convertChildToFieldObjDto(columnField, formId, productConfigId,
									sequence, childFieldObjDtoList).get(0));
						} else {
							fieldObjDtoList.add(new FieldObjDto(columnField, formId, null, parentFieldObjDtoList,
									grandChildSeq++,productConfigId));
						}
					}
				}
				columnFieldObjDtoList
						.add(new FieldObjDto(column, formId, null, fieldObjDtoList, childSeq++,productConfigId));
			}
			parentFieldObjDtoList = new ArrayList<>();
			parentFieldObjDtoList
					.add(new FieldObjDto(fieldChild, formId, null, columnFieldObjDtoList, sequence,productConfigId));
		} else if (BffAdminConstantsUtils.DATAGRID.equalsIgnoreCase(fieldChild.getType())
				|| BffAdminConstantsUtils.LISTVIEW.equalsIgnoreCase(fieldChild.getType())) {
			parentFieldObjDtoList.add(fieldComponentConverter.convertFieldComponentsToFieldObjDto(fieldChild, formId,
					productConfigId, sequence));
		} else {
			parentFieldObjDtoList = new ArrayList<>();
			parentFieldObjDtoList
					.add(new FieldObjDto(fieldChild, formId, null, columnFieldObjDtoList, sequence,productConfigId));
		}
		return parentFieldObjDtoList;
	}

	/**
	 * @param formObjDto
	 * @param flow
	 * @return Form
	 */
	public Form convertToFormEntity(FormObjDto formObjDto, Flow flow) {
		
		Form form = null;

		if (formObjDto.getFormId() != null) {
			form = formRepo.findById(formObjDto.getFormId()).orElseThrow();
			
		} else {
			form = new Form();
			form.setFlow(flow);
		}

		form.setApplyToAllClones(formObjDto.isApplyToAllClones());
		form.setDescription(formObjDto.getDescription());
		form.setFormTemplate(formObjDto.getFormTemplate());
		form.setCloneable(formObjDto.isClonableForm());
		form.setDisabled(formObjDto.isDisabledForm());
		form.setExtDisabled(formObjDto.isExtDisabled());
		form.setOrphan(formObjDto.isOrphanForm());
		form.setPublished(formObjDto.isPublished());

		form.setName(formObjDto.getName());
		form.setParentFormId(formObjDto.getParentFormId());
		form.setTag(formObjDto.getTag());

		form.setProductConfigId(formObjDto.getProductConfigId());
		form.setExtFieldAllDisabled(formObjDto.isExtFieldAllDisabled());
		form.setModalForm(formObjDto.isModalForm());
		form.setHideToolbar(formObjDto.isHideToolbar());
		form.setHideLeftNavigation(formObjDto.isHideLeftNavigation());
		form.setHideBottomNavigation(formObjDto.isHideBottomNavigation());
		form.setShowOnce(formObjDto.isShowOnce());
		form.setTabbedForm(formObjDto.isTabbedForm());
		form.setGs1Form(formObjDto.getGs1Form());
		form.setHideGs1Barcode(formObjDto.isHideGs1Barcode());
		form.setFormTitle(formObjDto.getFormTitle());

		return form;
	}

	/**
	 * @param formData
	 * @param formList
	 * @return boolean
	 */
	public boolean checkUniqueFormName(FormData formData, List<Form> formList) {
		boolean unFormNameCheck = true;
		if (!formList.isEmpty()) {
			for (Form formLocal : formList) {
				if (formData.getName().equals(formLocal.getName()) && (formData.getFormId() == null
						|| !formData.getFormId().equals(formLocal.getUid()))) {
					unFormNameCheck = false;
					break;
				}
			}
		}
		return unFormNameCheck;
	}

	/**
	 * @param formObjDto
	 * @param form
	 */
	public void saveTabs(FormObjDto formObjDto, Form form) {
		if (!CollectionUtils.isEmpty(formObjDto.getTabs())) {
			for (TabDto tabDto : formObjDto.getTabs()) {
				if (tabDto.getTabId() != null) {
					Tabs tabMain = tabRepository.findById(tabDto.getTabId()).orElseThrow();
					if (form.getTabs().contains(tabMain)) {
						Tabs tab = form.getTabs().get(form.getTabs().indexOf(tabMain));
						tab.setLinkedFormId(tabDto.getLinkedFormId());
						tab.setLinkedFormName(tabDto.getLinkedFormName());
						tab.setSequence(tabDto.getSequence());
						tab.setTabName(tabDto.getTabName());
						tab.setDefault(tabDto.isDefaultForm());
					} else {
						tabMain.setLinkedFormId(tabDto.getLinkedFormId());
						tabMain.setLinkedFormName(tabDto.getLinkedFormName());
						tabMain.setSequence(tabDto.getSequence());
						tabMain.setTabName(tabDto.getTabName());
						tabMain.setDefault(tabDto.isDefaultForm());
						form.addTabs(tabMain);
					}
				} else {
					Tabs tab = Tabs.builder().tabName(tabDto.getTabName()).linkedFormId(tabDto.getLinkedFormId())
							.linkedFormName(tabDto.getLinkedFormName()).sequence(tabDto.getSequence())
							.isDefault(tabDto.isDefaultForm()).build();
					form.addTabs(tab);
				}

			}
		}

	}
	/**
	 * @param formObjDto
	 * @param form
	 */
	public void saveEvents(FormObjDto formObjDto, Form form) {
		if (!CollectionUtils.isEmpty(formObjDto.getEvents())) {
			for (EventsDto eventsDto : formObjDto.getEvents()) {
				if (eventsDto.getUid() != null) {

					Events events = eventsRepo.findById(eventsDto.getUid()).orElseThrow();
					if (form.getEvents().contains(events)) {
						int index = form.getEvents().indexOf(events);
						form.getEvents().get(index).setEvent(eventsDto.getEvent());
						form.getEvents().get(index).setAction(eventsDto.getAction());						
					}else {						
						events.setEvent(eventsDto.getEvent());
						events.setAction(eventsDto.getAction());
						form.addEvents(events);
					}

				} else {
					form.addEvents(new Events(eventsDto.getEvent(), eventsDto.getAction()));
				}
			}

		}
	}
	/**
	 * @param fieldObjDto
	 * @param form
	 * @return Field
	 */
	public Field convertToFieldEntity(FieldObjDto fieldObjDto, Form form) {
		Field field = null;
		if (fieldObjDto.getFieldId() != null) {
			Field fieldMain = fieldRepo.findById(fieldObjDto.getFieldId()).orElseThrow();
			if(form.getFields().contains(fieldMain)) {
				field = form.getFields().get(form.getFields().indexOf(fieldMain));
			}
		} else {
			field = new Field();
			form.addField(field);
		}
		

		field = convertToFieldObj(fieldObjDto, field);
		if (fieldObjDto.getChildFieldObjDtoList() != null && !fieldObjDto.getChildFieldObjDtoList().isEmpty()) {
			field = createFieldBasedOnParentId(fieldObjDto, field, form);
		}
		
		return field;
	}

	/**
	 * This method used to set parent id for each field. If the object is nested, It
	 * will work as recursive method
	 *	
	 * @param fieldObjDto
	 * @param parentfield
	 * @param form
	 * @return Field
	 */
	private Field createFieldBasedOnParentId(FieldObjDto fieldObjDto, Field parentfield, Form form) {
		Field childField = null;
		for (FieldObjDto childFieldObjDto : fieldObjDto.getChildFieldObjDtoList()) {
			if (fieldObjDto.getFieldId()!=null && childFieldObjDto.getFieldId() != null) {
				childField = fieldRepo.findById(childFieldObjDto.getFieldId()).orElseThrow();
				childField.setParentField(parentfield);
				LOGGER.log(Level.DEBUG, "added parent {} to child {}", parentfield.getKeys(), childField.getKeys());
			} else {
				childField = new Field();
				parentfield.addChildFields(childField);
			}
			
			//Inserting into FormCustomComponent table , when custom control is added into form
			if (null != childFieldObjDto.getLinkedComponentId()) {
				Optional<CustomComponentMaster> master = customComponentMasterRepo
						.findById(childFieldObjDto.getLinkedComponentId());

				if (master.isPresent()) {
					FormCustomComponent formCustomComponent  =null;
					if(form.getUid()!=null)
					{
						formCustomComponent = formCustomComponentRepo.findByFormAndCustomComponentMaster(form, master.get());
					}
					if (formCustomComponent == null && form.getFormCustomComponent().stream()
								.noneMatch(formCustom -> formCustom.getCustomComponentMaster().getUid()
								.equals(master.get().getUid()))) 
					{
	                     formCustomComponent = new FormCustomComponent();
	                     formCustomComponent.setForm(form);
	                     formCustomComponent.setCustomComponentMaster(master.get());
	                     form.addFormCustomComponent(formCustomComponent);
					}

				}
			}
			
			childField = convertToFieldObj(childFieldObjDto, childField);
			
			if (childFieldObjDto.getChildFieldObjDtoList() != null
					&& !childFieldObjDto.getChildFieldObjDtoList().isEmpty()) {
				childField = createFieldBasedOnParentId(childFieldObjDto, childField,form);
			}
		}
		return childField;
	}


	/**
	 * @param fieldObjDto
	 * @param field
	 * @return Field
	 */
	@SuppressWarnings("all")
	private Field convertToFieldObj(FieldObjDto fieldObjDto, Field field) {
		if (null != fieldObjDto.getLinkedComponentId()) {
			Optional<CustomComponentMaster> master = customComponentMasterRepo
					.findById(fieldObjDto.getLinkedComponentId());

			if (master.isPresent()) {
				field.setLinkedComponentId(fieldObjDto.getLinkedComponentId());
			}
		}

		field.setAlignment(fieldObjDto.getAlignment());
		field.setProductConfigId(fieldObjDto.getProductConfigId());
		field.setKeys(fieldObjDto.getKey());
		field.setLabel(fieldObjDto.getLabel());
		field.setCustomFormat(fieldObjDto.getCustomFormat());

		field.setButtonType(fieldObjDto.getButtonType());
		field.setFormat(fieldObjDto.getFormat());
		field.setImageSource(fieldObjDto.getImageSource());
		field.setStyle(fieldObjDto.getStyle());
		field.setStyleFontType(fieldObjDto.getStyleFontType());
		field.setStyleFontSize(fieldObjDto.getStyleFontSize());
		field.setStyleFontColor(fieldObjDto.getStyleFontColor());
		field.setStyleBackgroundColor(fieldObjDto.getStyleBackgroundColor());
		field.setStyleFontWeight(fieldObjDto.getStyleFontWeight());
		field.setStyleWidth(fieldObjDto.getStyleWidth());
		field.setStyleHeight(fieldObjDto.getStyleHeight());
		field.setStylePadding(fieldObjDto.getStylePadding());
		field.setStyleMargin(fieldObjDto.getStyleMargin());
		field.setInline(fieldObjDto.isInline());
		field.setIcon(fieldObjDto.isIcon());
		field.setAutoCorrect(fieldObjDto.isAutoCorrect());
		field.setCapitalization(fieldObjDto.isCapitalization());
		field.setType(fieldObjDto.getType());
		field.setInput(fieldObjDto.isInput());
		field.setDefaultValue(fieldObjDto.getDefaultValue());
		field.setTableView(fieldObjDto.isTableView());
		field.setValueProperty(fieldObjDto.getValueProperty());
		field.setFontColor(fieldObjDto.getFontColor());
		field.setAllowInput(fieldObjDto.isAllowInput());
		field.setEnableDate(fieldObjDto.isEnableDate());
		field.setDatePickerMinDate(fieldObjDto.getDatePickerMinDate());
		field.setDatePickerMaxDate(fieldObjDto.getDatePickerMaxDate());
		field.setValidateMin(fieldObjDto.getValidateMin());
		field.setValidateMax(fieldObjDto.getValidateMax());
		field.setValidateInteger(fieldObjDto.getValidateInteger());
		field.setFieldDependencyShowCondition(fieldObjDto.getFieldDependencyShowCondition());
		field.setFieldDependencyHideCondition(fieldObjDto.getFieldDependencyHideCondition());
		field.setFieldDependencyEnableCondition(fieldObjDto.getFieldDependencyEnableCondition());
		field.setFieldDependencyDisableCondition(fieldObjDto.getFieldDependencyDisableCondition());
		field.setFieldDependencyRequiredCondition(fieldObjDto.getFieldDependencyRequiredCondition());
		field.setFieldDependencySetValue(fieldObjDto.getFieldDependencyValues());
		field.setFieldDependencyRequired(fieldObjDto.isFieldDependencyRequired());
		field.setFieldDependencyHidden(fieldObjDto.isFieldDependencyHidden());
		field.setFieldDependencyDisabled(fieldObjDto.isFieldDependencyDisabled());
		field.setHideLabel(fieldObjDto.isHideLabel());
		field.setCustomClass(fieldObjDto.getCustomClass());
		field.setMask(fieldObjDto.isMask());
		field.setAlwaysEnabled(fieldObjDto.isAlwaysEnabled());
		field.setLazyLoad(fieldObjDto.isLazyLoad());
		field.setSelectValues(fieldObjDto.getSelectValues());
		field.setDisableLimit(fieldObjDto.isDisableLimit());
		field.setSort(fieldObjDto.getSort());
		field.setReference(fieldObjDto.isReference());
		field.setRadius(fieldObjDto.getRadius());
		field.setBackGroundColor(fieldObjDto.getBackGroundColor());
		field.setWidth(fieldObjDto.getWidth());
		field.setHeight(fieldObjDto.getHeight());
		field.setMaxDate(fieldObjDto.getMaxDate());
		field.setMinDate(fieldObjDto.getMinDate());
		field.setIconAlignment(fieldObjDto.getIconAlignment());
		field.setSequence(fieldObjDto.getSequence());
		field.setDescription(fieldObjDto.getDescription());
		field.setOffset(fieldObjDto.getOffset());
		field.setPush(fieldObjDto.getPush());
		field.setPull(fieldObjDto.getPull());
		field.setHideOnChildrenHidden(fieldObjDto.isHideOnChildrenHidden());
		field.setValidatePattern(fieldObjDto.getPattern());
		field.setInputType(fieldObjDto.getInputType());
		field.setPlaceHolder(fieldObjDto.getPlaceHolder());
		field.setIconName(fieldObjDto.getIconName());
		field.setIconCode(fieldObjDto.getIconCode());
		field.setLineBreakMode(fieldObjDto.getLineBreakMode());
		field.setFontType(fieldObjDto.getFontType());
		field.setFontSize(fieldObjDto.getFontSize());
		field.setDisableAddingRemovingRows(fieldObjDto.isDisableAddingRemovingRows());
		field.setAddAnother(fieldObjDto.getAddAnother());
		field.setAddAnotherPosition(fieldObjDto.getAddAnotherPosition());
		field.setRemovePlacement(fieldObjDto.getRemovePlacement());
		field.setStriped(fieldObjDto.isStriped());
		field.setBordered(fieldObjDto.isBordered());
		field.setSelected(fieldObjDto.isSelected());
		field.setCondensed(fieldObjDto.isCondensed());
		field.setAddSorting(fieldObjDto.isAddSorting());
		field.setAddFilter(fieldObjDto.isAddFilter());
		field.setStyleType(fieldObjDto.getStyleType());
		field.setClearOnHide(fieldObjDto.isClearOnHide());
		field.setNumberOfRows(fieldObjDto.getNumberOfRows());
		field.setAddPagination(fieldObjDto.isAddPagination());
		field.setPrefix(fieldObjDto.getPrefix());
		field.setSuffix(fieldObjDto.getSuffix());
		field.setHeaderLabel(fieldObjDto.getHeaderLabel());
		field.setListImageAlignment(fieldObjDto.getListImageAlignment());
		field.setRows(fieldObjDto.getRows());
		field.setValidateMaxLength(fieldObjDto.getValidateMaxLength());
		field.setValidateMinLength(fieldObjDto.getValidateMinLength());
		field.setValidateMaxDate(fieldObjDto.getValidateMaxDate());
		field.setValidateMinDate(fieldObjDto.getValidateMinDate());
		field.setValidateMaxTime(fieldObjDto.getValidateMaxTime());
		field.setValidateMinTime(fieldObjDto.getValidateMinTime());
		field.setValidateMinRow(fieldObjDto.getMinRows());
		field.setValidateMaxRow(fieldObjDto.getMaxRows());
		field.setTextAreaHeight(fieldObjDto.getTextAreaHeight());
		field.setBold(fieldObjDto.isBold());
		field.setItalic(fieldObjDto.isItalic());
		field.setUnderline(fieldObjDto.isUnderline());
		field.setDecimalPlaces(fieldObjDto.getDecimalPlaces());
		field.setCurrDateDefSet(fieldObjDto.isCurrentDate());
		field.setCurrDateTimeSet(fieldObjDto.isCurrentTime());
		if (fieldObjDto.getApiDataSource() != null) {
			field.setApiDataSource(fieldObjDto.getApiDataSource().toString());
		}
		if (fieldObjDto.getDefaultApiValue() != null) {
			field.setDefaultApiValue(fieldObjDto.getDefaultApiValue().toString());
		}
		if (fieldObjDto.getAutoCompleteApi() != null) {
			field.setAutoCompleteApi(fieldObjDto.getAutoCompleteApi().toString());
		}
		field.setDefaultStaticValue(fieldObjDto.getDefaultStaticValue());
		field.setDefaultValueType(fieldObjDto.getDefaultValueType());
		field.setButtonSize(fieldObjDto.getButtonSize());
		field.setAutoAdjust(fieldObjDto.isAutoAdjust());
		field.setHotKeyName(fieldObjDto.getHotKeyName());
		
		if (!CollectionUtils.isEmpty(fieldObjDto.getData())) {
			List<Data> modifiedDataList = new ArrayList<>();
			for (DataDto dataDto : fieldObjDto.getData()) {
				if(!StringUtils.isEmpty(dataDto.getDatalabel()) && !StringUtils.isEmpty(dataDto.getDatavalue())) {
					Data data = null;
					if (dataDto.getUid() != null) {
						data = dataRepo.findById(dataDto.getUid()).orElseThrow();
						data.setDatalabel(dataDto.getDatalabel());
						data.setDatavalue(dataDto.getDatavalue());
						modifiedDataList.add(data);
					} else {
						modifiedDataList.add(new Data(dataDto.getDatalabel(), dataDto.getDatavalue()));
					}
				}
			}
			
			//Delete the removed data
			deleteData(field, modifiedDataList);
			
			field.getData().clear();
			for (Data data : modifiedDataList) {
				field.addData(data);
			}

			
		} else {
			field.getData().clear();
		}
		

		if (!CollectionUtils.isEmpty(fieldObjDto.getEvents())) {
			List<Events> modifiedEventList = new ArrayList<>();
			for (EventsDto eventsDto : fieldObjDto.getEvents()) {
				Events events = null;
				if (eventsDto.getUid() != null) {
					events = eventsRepo.findById(eventsDto.getUid()).orElseThrow();
					events.setEvent(eventsDto.getEvent());
					events.setAction(eventsDto.getAction());
					modifiedEventList.add(events);
				} else {
					modifiedEventList.add(new Events(eventsDto.getEvent(), eventsDto.getAction()));

				}
			}
			field.getEvents().clear();
			for (Events event : modifiedEventList) {
				field.addEvents(event);
			}
			
		} else {
			field.getEvents().clear();
		}

		if (!CollectionUtils.isEmpty(fieldObjDto.getValues())) {
			List<FieldValues> modifiedValueList = new ArrayList<>();
			for (ValuesDto valueDto : fieldObjDto.getValues()) {
				FieldValues fieldValues = null;
				if(!StringUtils.isEmpty(valueDto.getLabel()) && !StringUtils.isEmpty(valueDto.getValue())) {
					if (valueDto.getValueId() != null) {
						fieldValues = valueRepo.findById(valueDto.getValueId()).orElseThrow();
						fieldValues.setLabel(valueDto.getLabel());
						fieldValues.setLabelValue(valueDto.getValue());
						modifiedValueList.add(fieldValues);
					} else {
						modifiedValueList.add(new FieldValues(valueDto.getLabel(), valueDto.getValue()));
					}
				}
			}
			
			//Delete the removed values
			deleteValues(field, modifiedValueList);
			
			field.getValues().clear();
			
			for (FieldValues fieldValues : modifiedValueList) {
				field.addValues(fieldValues);
			}
			
		} else {
			field.getValues().clear();
		}
		if (field.getForm() == null && field.getParentField() != null
				&& field.getParentField().getExtendedFromFieldId() != null) {

			if (field.getExtendedFromFieldId() != null) {
				
				if (extFieldRepo.findById(field.getExtendedFromFieldId()).isPresent()) {
					ExtendedFieldBase extField = extFieldRepo.findById(field.getExtendedFromFieldId()).get();

					if (FieldComparator.compare(field, extField) != 0) {
						field.setModifyStatus(true);
					} else if (!FieldComparator.compareFieldAttributeOfTypeList(field.getEvents(),
							extField.getExtendedEventsBase())) {
						field.setModifyStatus(true);
					} else if (!FieldComparator.compareFieldAttributeOfTypeList(field.getData(),
							extField.getExtendedDataBase())) {
						field.setModifyStatus(true);
					} else if (!FieldComparator.compareFieldAttributeOfTypeList(field.getValues(),
							extField.getExtendedFieldValuesBase())) {
						field.setModifyStatus(true);
					} else {
						field.setModifyStatus(false);
					}
				}

			} else {
				field.setModifyStatus(true);
			}
		}
		//To get modified indicator for new field added in new column in extended form
		else if(field.getForm() == null && field.getParentField() != null 
				&& field.getParentField().getType().equalsIgnoreCase(BffAdminConstantsUtils.COLUMN) 
				&&field.getParentField().getExtendedFromFieldId() == null)
		{
			field.setModifyStatus(field.getParentField().isModifyStatus());
		}
		return field;
	}

	private void deleteData(Field field, List<Data> modifiedDataList) {
		//Find the removed values
		List<Data> removedList = new ArrayList<>();
		for(Data data : field.getData()){
			if(!modifiedDataList.contains(data))
				removedList.add(data);
		}
		for(Data data :removedList)
		{
			dataRepo.deleteByDataId(data.getUid());
		}
		
	}

	private void deleteValues(Field field, List<FieldValues> modifiedValueList) {
		//Find the removed values
		List<FieldValues> removedList = new ArrayList<>();
		for(FieldValues value : field.getValues()){
			if(!modifiedValueList.contains(value)) {
				removedList.add(value);
			}
		}
		for(FieldValues value :removedList)
		{
			valueRepo.deleteByValueId(value.getUid());
		}
	}
	/**
	 * To return List of Fields without child field objects
	 * 
	 * @param fields
	 * @return List&lt;Field&gt;
	 */
	public List<Field> getListFields(List<Field> fields) {
		List<Field> fieldsList = new ArrayList<>();
		for (Field field : fields) {
			fieldsList.add(field);
			if (field.getChildFields() != null && !field.getChildFields().isEmpty()) {
				fieldsList.addAll(getListFields(field.getChildFields()));
			}
		}
		return fieldsList;
	}

	/**
	 * This method used to convert Field list to list of FieldObjDto
	 * 
	 * @param fieldList
	 * @return List&lt;FieldObjDto&gt;
	 * @throws IOException
	 */
	public List<FieldObjDto> convertToFieldDto(List<Field> fieldList) throws IOException {
		List<FieldObjDto> parentFieldObj = new ArrayList<>();
		fieldList = getListFields(fieldList);
		for (Field field : fieldList) {
			if (field.getParentField() == null) {
				parentFieldObj.add(createFieldObjDto(field, new ArrayList<>()));
			}
		}
		parentFieldObj = getFieldObjDtoWithChild(fieldList, parentFieldObj);
		return parentFieldObj;
	}

	/**
	 * This method used to get list of FieldObjDto with child FieldObjDto
	 * 
	 * @param fieldList
	 * @param fieldObjDtos
	 * @return List<FieldObjDto>
	 * @throws IOException
	 */
	private List<FieldObjDto> getFieldObjDtoWithChild(List<Field> fieldList, List<FieldObjDto> fieldObjDtos)
			throws IOException {
		List<FieldObjDto> parent = new ArrayList<>();
		for (FieldObjDto fieldObjDto : fieldObjDtos) {
			List<FieldObjDto> child = findChildFieldObj(fieldObjDto.getFieldId(), fieldList);
			if (!child.isEmpty()) {
				List<FieldObjDto> childWithGrandChild = getFieldObjDtoWithChild(fieldList, child);
				Collections.sort(childWithGrandChild);
				fieldObjDto.getChildFieldObjDtoList().addAll(childWithGrandChild);
			}
			parent.add(fieldObjDto);
		}
		Collections.sort(parent);
		return parent;
	}

	/**
	 * This method used to find the list of child FieldObjDto from list of child
	 * 
	 * @param uuid
	 * @param fieldList
	 * @return List<FieldObjDto>
	 * @throws IOException
	 */
	private List<FieldObjDto> findChildFieldObj(UUID uuid, List<Field> fieldList) throws IOException {
		List<FieldObjDto> childFieldObjDto = new ArrayList<>();
		for (Field field : fieldList) {
			if (field.getParentField() != null && field.getParentField().getUid().equals(uuid)) {
				childFieldObjDto.add(createFieldObjDto(field, new ArrayList<>()));
			}
		}

		return childFieldObjDto;
	}

	/**
	 * @param field
	 * @param childFieldObjDtoList
	 * @return FieldObjDto
	 * @throws IOException
	 */
	public FieldObjDto createFieldObjDto(Field field, List<FieldObjDto> childFieldObjDtoList) throws IOException {
		FieldObjDto fieldObjDto = new FieldObjDto(field, childFieldObjDtoList);
		return fieldObjDto;
	}

	public FormData createFormData(FormObjDto formObjDto) throws IOException {
		FormData formData = new FormData();

		formData.setFormId(formObjDto.getFormId());
		formData.setName(formObjDto.getName());
		formData.setDescription(formObjDto.getDescription());
		formData.setPublished(formObjDto.isPublished());
		formData.setFlowId(formObjDto.getFlowId());
		formData.setFormTemplate(formObjDto.getFormTemplate());
		formData.setTag(formObjDto.getTag());
		formData.setOrphanForm(formObjDto.isOrphanForm());
		formData.setClonableForm(formObjDto.isClonableForm());
		formData.setApplyToAllClones(formObjDto.isApplyToAllClones());
		formData.setParentFormId(formObjDto.getParentFormId());
		formData.setDefaultForm(formObjDto.isDefaultForm());
		formData.setShowonce(formObjDto.isShowOnce());
		formData.setTabbedForm(formObjDto.isTabbedForm());
		formData.setFormTitle(bffCommonUtil.getResourceBundle(formObjDto.getFormTitle()));
		formData.setInboundOrphan(formObjDto.isInboundOrphan());
		formData.setOutboundOrphan(formObjDto.isOutboundOrphan());
		if (null != formObjDto.getFlowDto()) {
			formData.setFlowName(formObjDto.getFlowDto().getName());
			formData.setFlowPermissions(formObjDto.getFlowDto().getPermissions());
			formData.setFlowVersion(formObjDto.getFlowDto().getVersion());
		}

		FormProperties formProperties = new FormProperties();
		FormAttributes properties = new FormAttributes();
		properties.setDisableForm(formObjDto.isDisabledForm());
		properties.setDisableFormExtensions(formObjDto.isExtDisabled());
		properties.setDisableAllExtensions(formObjDto.isExtFieldAllDisabled());
		properties.setModalForm(formObjDto.isModalForm());
		properties.setHideToolbar(formObjDto.isHideToolbar());
		properties.setHideLeftNavigation(formObjDto.isHideLeftNavigation());
		properties.setHideBottomNavigation(formObjDto.isHideBottomNavigation());
		properties.setHideGs1Barcode(formObjDto.isHideGs1Barcode());

		ObjectMapper objectMapper = new ObjectMapper();

		if (formObjDto.getGs1Form() != null
				&& !BffAdminConstantsUtils.EMPTY_SPACES.equalsIgnoreCase(formObjDto.getGs1Form())) {

			ObjectNode gs1Form = (ObjectNode) objectMapper.readTree(formObjDto.getGs1Form());
			properties.setGs1Form(gs1Form);
		}
		formProperties.setProperties(properties);
		List<Event> eventList = new ArrayList<>();
		if (formObjDto.getEvents() != null && !formObjDto.getEvents().isEmpty()) {
			for (EventsDto eventsDto : formObjDto.getEvents()) {
				Event event = new Event();
				event.setEventId(eventsDto.getUid());
				event.setEventName(eventsDto.getEvent());
				if (eventsDto.getAction() != null
						&& !BffAdminConstantsUtils.EMPTY_SPACES.equalsIgnoreCase(eventsDto.getAction())) {
					ObjectNode action = (ObjectNode) objectMapper.readTree(eventsDto.getAction());
					event.setAction(action);
				}
				eventList.add(event);
			}
		}
		formProperties.setEvents(eventList);

		formData.setFormProperties(formProperties);

		List<FieldComponent> fieldCompMasterList = null;
		FieldComponent childFieldComponent = null;
		if (formObjDto.getFields() != null && !formObjDto.getFields().isEmpty()) {
			fieldCompMasterList = new ArrayList<>();
			for (FieldObjDto fieldObjDto : formObjDto.getFields()) {
				FieldComponent fieldComponent = fieldComponentConverter.createFieldComponent(fieldObjDto);

				List<FieldComponent> fieldCompChildList = null;
				if (fieldObjDto.getChildFieldObjDtoList() != null && !fieldObjDto.getChildFieldObjDtoList().isEmpty()) {
					fieldCompChildList = new ArrayList<>();
					for (FieldObjDto childFieldObjDto : fieldObjDto.getChildFieldObjDtoList()) {

						if (BffAdminConstantsUtils.COLUMNS.equalsIgnoreCase(childFieldObjDto.getType())
								&& childFieldObjDto.getChildFieldObjDtoList() != null) {
							childFieldComponent = fieldComponentConverter
									.convertToColumnsFieldComponent(childFieldObjDto);
						} else if ((BffAdminConstantsUtils.DATAGRID.equalsIgnoreCase(childFieldObjDto.getType())
								|| BffAdminConstantsUtils.LISTVIEW.equalsIgnoreCase(childFieldObjDto.getType()))
								&& childFieldObjDto.getChildFieldObjDtoList() != null) {
							childFieldComponent = fieldComponentConverter
									.convertFieldObjDtoToFieldComponents(childFieldObjDto);
						} else {
							childFieldComponent = fieldComponentConverter.createFieldComponent(childFieldObjDto);
						}
						fieldCompChildList.add(childFieldComponent);
					}

				}
				fieldComponent.setComponents(fieldCompChildList);
				fieldCompMasterList.add(fieldComponent);
			}
		}
		formData.setComponents(fieldCompMasterList);

		if (formObjDto.getTabs() != null && !formObjDto.getTabs().isEmpty()) {
			List<Tab> tabList = new ArrayList<>();

			for (TabDto tabDto : formObjDto.getTabs()) {
				Tab tab = new Tab();
				tab.setLinkedFormId(tabDto.getLinkedFormId());
				tab.setLinkedFormName(tabDto.getLinkedFormName());
				tab.setTabId(tabDto.getTabId());
				tab.setTabName(bffCommonUtil.getResourceBundle(tabDto.getTabName()));
				tab.setDefaultForm(tabDto.isDefaultForm());
				tabList.add(tab);
			}
			formData.setTabs(tabList);
		}
		ProductConfig prodConfig = productConfigRepo.findById(formObjDto.getProductConfigId()).orElseThrow();
		Layer layer = new Layer();
		if (prodConfig.getRoleMaster() != null) {
			layer.setLevel(prodConfig.getRoleMaster().getLevel());
			layer.setName(prodConfig.getRoleMaster().getName());
			formData.setLayer(layer);
		}
		return formData;
	}

}