/**
 * 
 */
package com.jda.mobility.framework.extensions.transformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jda.mobility.framework.extensions.dto.CustomFieldObjDto;
import com.jda.mobility.framework.extensions.dto.CustomFormDto;
import com.jda.mobility.framework.extensions.dto.DataDto;
import com.jda.mobility.framework.extensions.dto.EventsDto;
import com.jda.mobility.framework.extensions.dto.FieldObjDto;
import com.jda.mobility.framework.extensions.dto.FormCustomComponentDto;
import com.jda.mobility.framework.extensions.dto.TranslationRequest;
import com.jda.mobility.framework.extensions.dto.ValuesDto;
import com.jda.mobility.framework.extensions.entity.CustomComponentMaster;
import com.jda.mobility.framework.extensions.entity.CustomData;
import com.jda.mobility.framework.extensions.entity.CustomEvents;
import com.jda.mobility.framework.extensions.entity.CustomField;
import com.jda.mobility.framework.extensions.entity.CustomFieldValues;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.FormCustomComponent;
import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.model.CustomFormData;
import com.jda.mobility.framework.extensions.model.FieldComponent;
import com.jda.mobility.framework.extensions.model.FormCustomComponentType;
import com.jda.mobility.framework.extensions.model.Layer;
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
import com.jda.mobility.framework.extensions.utils.BffUtils;

/**
 * Custom create DTO's to and from CustomForm entity's and provide common logic
 * to persist related data.
 * 
 * HCL Technologies Ltd.
 */
@Component
public class CustomFormTransformation {

	@Autowired
	private FormRepository formRepo;
	@Autowired
	private CustomComponentMasterRepository customComponentMasterRepo;
	@Autowired
	private CustomFieldRepository customFieldRepo;
	@Autowired
	private FormCustomComponentRepository formCustomComponentRepo;
	@Autowired
	private CustomDataRepository customDataRepo;
	@Autowired
	private CustomEventsRepository customEventsRepo;
	@Autowired
	private CustomComponentConverter fieldComponentConverter;
	@Autowired
	private CustomFieldValuesRepository valueRepo;
	@Autowired
	private BffCommonUtil bffCommonUtil;
	@Autowired
	private ProductPrepareService productPrepareService;
	@Autowired
	private ProductConfigRepository productConfigRepo;

	/**
	 * Convert CustomFormData to CustomFormDto
	 * 
	 * @param customFormData
	 * @return customFormObjDto CustomComponentMasterDto
	 */
	public CustomFormDto convertToCustomFormObjDto(CustomFormData customFormData) {
		UUID customFormId = null;
		String name = customFormData.getName();
		boolean visibility = customFormData.isVisibility();
		boolean disabled = customFormData.isDisabled();

		if (customFormData.getCustomComponentId() != null) {
			customFormId = customFormData.getCustomComponentId();
		}
		List<UUID> deleteFieldList = customFormData.getDeleteFields();
		List<UUID> deleteEventList = customFormData.getDeleteEvents();
		List<UUID> deleteValues = customFormData.getDeleteValues();
		List<UUID> deleteDataValues = customFormData.getDeleteDataValues();
		String formTitle = BffUtils.getNullable(customFormData.getFormTitle(), TranslationRequest::getRbkey);
		UUID productConfigId = productPrepareService.getCurrentLayerProdConfigId().getUid();
		List<CustomFieldObjDto> fields = null;
		if (customFormData.getComponents() != null && !customFormData.getComponents().isEmpty()) {
			fields = new ArrayList<>();
			int masterSeq = 0;
			for (FieldComponent fieldComponent : customFormData.getComponents()) {
				CustomFieldObjDto fieldObjDto = convertToFieldObjDto(fieldComponent, customFormId, masterSeq++,productConfigId);
				fields.add(fieldObjDto);
			}
		}
		List<FormCustomComponentDto> formCustomComponentDtoList = null;
		if (customFormData.getFormCustomComponentTypes() != null
				&& !customFormData.getFormCustomComponentTypes().isEmpty()) {
			formCustomComponentDtoList = new ArrayList<>();
			for (FormCustomComponentType formCusObjType : customFormData.getFormCustomComponentTypes()) {
				UUID formCusId = formCusObjType.getFormCusId();
				UUID formId = formCusObjType.getFormId();
				FormCustomComponentDto formCustomComponentDto = new FormCustomComponentDto(formCusId, formId,
						customFormId);
				formCustomComponentDtoList.add(formCustomComponentDto);
			}
		}
		CustomFormDto customFormObjDto = new CustomFormDto.CustomFormBuilder()
				.setName(name)
				.setDescription(customFormData.getDescription())
				.setCustomFormId(customFormId).setVisibility(visibility).setDisabled(disabled).setFields(fields)
				.setFormCustomComponentDto(formCustomComponentDtoList)
				.setDeleteFields(deleteFieldList).setDeleteEvents(deleteEventList).setDeleteValues(deleteValues)
				.setDeleteDataValues(deleteDataValues).setFormTitle(formTitle).setProductConfigId(productConfigId).build();
		return customFormObjDto;
	}

	/**
	 * Convert FieldComponent to FieldObjDto
	 * 
	 * @param fieldComponent
	 * @param requestType
	 * @param customFormId
	 * @param productConfigId
	 * @return
	 */
	private CustomFieldObjDto convertToFieldObjDto(FieldComponent fieldComponent, UUID customFormId,
			int sequence , UUID productConfigId) {
		List<CustomFieldObjDto> fieldObjDtoList = null;
		int childSeq = 0;
		if (fieldComponent.getComponents() != null && !fieldComponent.getComponents().isEmpty()) {
			fieldObjDtoList = new ArrayList<>();

			for (FieldComponent childfield : fieldComponent.getComponents()) {
				fieldObjDtoList.add(
						convertChildToFieldObjDto(childfield, customFormId, childSeq++, null,productConfigId).get(0));
			}
		}

		CustomFieldObjDto fieldObjDto = convertChildToFieldObjDto(fieldComponent, customFormId,
				sequence, null,productConfigId).get(0);
		return fieldObjDto;
	}

	private List<CustomFieldObjDto> convertChildToFieldObjDto(FieldComponent fieldChild, UUID customFormId,
			int sequence, List<FieldObjDto> childFieldObjDtoList, UUID productConfigId) {
		int childSeq = 0;
		int grandChildSeq = 0;
		List<CustomFieldObjDto> fieldObjDtoList = null;
		List<CustomFieldObjDto> columnFieldObjDto = null;
		List<CustomFieldObjDto> childFieldObjDtos = new ArrayList<>();
		if (BffAdminConstantsUtils.COLUMNS.equalsIgnoreCase(fieldChild.getType())) {
			columnFieldObjDto = new ArrayList<>();
			for (FieldComponent column : fieldChild.getColumns()) {
				fieldObjDtoList = new ArrayList<>();
				if (BffAdminConstantsUtils.COLUMN.equalsIgnoreCase(column.getType())
						&& column.getComponents() != null) {
					for (FieldComponent columnField : column.getComponents()) {
						if (BffAdminConstantsUtils.COLUMNS.equalsIgnoreCase(columnField.getType())
								|| BffAdminConstantsUtils.DATAGRID.equalsIgnoreCase(columnField.getType())
								|| BffAdminConstantsUtils.LISTVIEW.equalsIgnoreCase(columnField.getType())) {
							fieldObjDtoList.add(convertChildToFieldObjDto(columnField, customFormId,
									sequence, childFieldObjDtoList,productConfigId).get(0));
						} else {
							fieldObjDtoList.add(new CustomFieldObjDto(columnField, customFormId, childFieldObjDtos,
									grandChildSeq++,productConfigId));
						}
					}
				}
				columnFieldObjDto
						.add(new CustomFieldObjDto(column, customFormId, fieldObjDtoList, childSeq++,productConfigId));
			}
			childFieldObjDtos = new ArrayList<>();
			childFieldObjDtos
					.add(new CustomFieldObjDto(fieldChild, customFormId, columnFieldObjDto, sequence,productConfigId));
		} else if (BffAdminConstantsUtils.DATAGRID.equalsIgnoreCase(fieldChild.getType())
				|| BffAdminConstantsUtils.LISTVIEW.equalsIgnoreCase(fieldChild.getType())) {
			childFieldObjDtos.add(fieldComponentConverter.convertFieldComponentsToCustomFieldObjDto(fieldChild,
					customFormId, sequence,productConfigId));
		} else {
			childFieldObjDtos = new ArrayList<>();
			childFieldObjDtos
					.add(new CustomFieldObjDto(fieldChild, customFormId, columnFieldObjDto, sequence,productConfigId));
		}
		return childFieldObjDtos;
	}

	/**
	 * Convert CustomFormDto to CustomomponentMaster
	 * 
	 * @param customFormObjDto CustomComponentMasterDto
	 * @return customComponentMaster CustomComponentMaster
	 */
	public CustomComponentMaster convertToCustomMasterEntity(CustomFormDto customFormObjDto) {
		CustomComponentMaster customComponentMaster = null;
		if (customFormObjDto.getCustomFormId() != null) {
			customComponentMaster = customComponentMasterRepo.findById(customFormObjDto.getCustomFormId())
					.orElseThrow();
		} else {
			customComponentMaster = new CustomComponentMaster();
		}
		customComponentMaster.setName(customFormObjDto.getName());
		customComponentMaster.setDescription(customFormObjDto.getDescription());
		customComponentMaster.setVisibility(customFormObjDto.isVisibility());
		customComponentMaster.setIsdisabled(customFormObjDto.isDisabled());
		customComponentMaster.setProductConfigId(customFormObjDto.getProductConfigId());
		customComponentMaster.setFormTitle(customFormObjDto.getFormTitle());
		customComponentMaster = customComponentMasterRepo.save(customComponentMaster);
		return customComponentMaster;
	}

	/**
	 * Convert FormCustomComponentDto to FormCustomComponent
	 * 
	 * @param formCustomComponentDto FormCustomComponentDto
	 * @param customFormId           UUID
	 * @return formCustomComponent FormCustomComponent
	 */
	public FormCustomComponent convertToFormCustomComponentEntity(FormCustomComponentDto formCustomComponentDto,
			UUID customFormId) {
		FormCustomComponent formCustomComponent = null;

		if (formCustomComponentDto.getFormCusId() != null) {
			formCustomComponent = formCustomComponentRepo.findById(formCustomComponentDto.getFormCusId()).orElseThrow();
		} else {
			formCustomComponent = new FormCustomComponent();
		}
		if (formCustomComponentDto.getFormId() != null) {
			Form form = formRepo.findById(formCustomComponentDto.getFormId()).orElseThrow();
			formCustomComponent.setForm(form);
		}
		CustomComponentMaster customComponentMaster = null;
		if (formCustomComponentDto.getCustomFormId() != null) {
			customComponentMaster = customComponentMasterRepo.findById(formCustomComponentDto.getCustomFormId())
					.orElseThrow();
			formCustomComponent.setCustomComponentMaster(customComponentMaster);
		} else {
			customComponentMaster = customComponentMasterRepo.findById(customFormId).orElseThrow();
			formCustomComponent.setCustomComponentMaster(customComponentMaster);
		}
		formCustomComponent = formCustomComponentRepo.save(formCustomComponent);
		return formCustomComponent;
	}

	/**
	 * Convert CustomFieldObjDto to CustomField
	 * 
	 * @param fieldObjDto
	 * @param customFormId
	 * @param requestType
	 * @return CustomField
	 */
	public CustomField convertToFieldEntity(CustomFieldObjDto fieldObjDto, UUID customFormId, RequestType requestType) {
		CustomComponentMaster customComponentMaster = customComponentMasterRepo.findById(customFormId).orElseThrow();
		CustomField field = null;
		if (fieldObjDto.getFieldId() != null && !requestType.equals(RequestType.POST)) {
			field = customFieldRepo.findById(fieldObjDto.getFieldId()).orElseThrow();
			field.setCustomComponentMaster(customComponentMaster);
			field.setParentField(null);
			field.setParentFieldId(null);
		} else {
			field = new CustomField();
			field.setCustomComponentMaster(customComponentMaster);
		}

		field = convertToFieldObj(fieldObjDto, field, requestType);

		if (fieldObjDto.getChildFieldObjDtoList() != null) {
			createFieldBasedOnParentId(fieldObjDto, field, requestType);
		}

		return field;
	}

	private CustomField createFieldBasedOnParentId(CustomFieldObjDto customFieldObjDto, CustomField parentfield,
			RequestType requestType) {
		CustomField childField = null;
		for (CustomFieldObjDto childFieldObjDto : customFieldObjDto.getChildFieldObjDtoList()) {
			if (null!= customFieldObjDto.getFieldId() && childFieldObjDto.getFieldId() != null) {
				childField = customFieldRepo.findById(childFieldObjDto.getFieldId()).orElseThrow();
				childField.setCustomComponentMaster(null);
				childField.setParentFieldId(parentfield.getUid());
				childField.setParentField(parentfield);
			} else {
				childField = new CustomField();
				childField.setParentFieldId(parentfield.getUid());
				childField.setParentField(parentfield);
			}
			childField = convertToFieldObj(childFieldObjDto, childField, requestType);
			if (childFieldObjDto.getChildFieldObjDtoList() != null) {
				childField = createFieldBasedOnParentId(childFieldObjDto, childField, requestType);
			}
		}
		return childField;
	}

	/**
	 * @param fieldObjDto
	 * @param field
	 * @param customComponent
	 * @param form
	 */
	@SuppressWarnings("all")
	private CustomField convertToFieldObj(CustomFieldObjDto fieldObjDto, CustomField field, RequestType requestType) {
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
		field.setValidatePattern(fieldObjDto.getPattern());
		field.setFieldDependencyShowCondition(fieldObjDto.getFieldDependencyShowCondition());
		field.setFieldDependencyHideCondition(fieldObjDto.getFieldDependencyHideCondition());
		field.setFieldDependencyEnableCondition(fieldObjDto.getFieldDependencyEnableCondition());
		field.setFieldDependencyDisableCondition(fieldObjDto.getFieldDependencyDisableCondition());
		field.setFieldDependencyRequiredCondition(fieldObjDto.getFieldDependencyRequiredCondition());
		field.setFieldDependencySetValue(fieldObjDto.getFieldDependencySetValue());
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
		field.setOffset(fieldObjDto.getOffset());
		field.setPush(fieldObjDto.getPush());
		field.setPull(fieldObjDto.getPull());
		field.setHideOnChildrenHidden(fieldObjDto.isHideOnChildrenHidden());
		field.setPrefix(fieldObjDto.getPrefix());
		field.setSuffix(fieldObjDto.getSuffix());
		field.setRows(fieldObjDto.getRows());
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

		field.setValidateMinLength(fieldObjDto.getValidateMinLength());
		field.setValidateMaxLength(fieldObjDto.getValidateMaxLength());
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
		field.setButtonSize(fieldObjDto.getButtonSize());
		field.setAutoAdjust(fieldObjDto.isAutoAdjust());
		field.setHotKeyName(fieldObjDto.getHotKeyName());
		
		if (null != fieldObjDto.getLinkedComponentId()) {
			Optional<CustomComponentMaster> master = customComponentMasterRepo
					.findById(fieldObjDto.getLinkedComponentId());

			if (master != null && master.get() != null) {
				field.setLinkedComponentId(master.get());
			}
		}
		if (fieldObjDto.getData() != null && !fieldObjDto.getData().isEmpty()) {
			if (field.getData() == null) {
				field.setData(new ArrayList<CustomData>());
			}
			for (DataDto dataDto : fieldObjDto.getData()) {
				CustomData data = null;
				if (dataDto.getUid() != null) {
					data = customDataRepo.findById(dataDto.getUid()).orElseThrow();
					int index = field.getData().indexOf(data);
					field.getData().get(index).setDatalabel(dataDto.getDatalabel());
					field.getData().get(index).setDatavalue(dataDto.getDatavalue());

				} else {
					data = new CustomData(dataDto.getDatalabel(), dataDto.getDatavalue());
					data.setField(field);
					field.getData().add(customDataRepo.save(data));
				}
			}
		}

		if (fieldObjDto.getEvents() != null && !fieldObjDto.getEvents().isEmpty()) {
			if (field.getEvents() == null) {
				field.setEvents(new ArrayList<CustomEvents>());
			}
			for (EventsDto eventsDto : fieldObjDto.getEvents()) {
				CustomEvents events = null;
				if (eventsDto.getUid() != null) {

					events = customEventsRepo.findById(eventsDto.getUid()).orElseThrow();
					int index = field.getEvents().indexOf(events);
					field.getEvents().get(index).setEvent(eventsDto.getEvent());
					field.getEvents().get(index).setAction(eventsDto.getAction());

				} else {
					events = new CustomEvents(eventsDto.getEvent(), eventsDto.getAction());
					events.setField(field);
					field.getEvents().add(customEventsRepo.save(events));

				}
			}

			if (fieldObjDto.getValues() != null && !fieldObjDto.getValues().isEmpty()) {
				if (field.getValues() == null) {
					field.setValues(new ArrayList<CustomFieldValues>());
				}
				for (ValuesDto valueDto : fieldObjDto.getValues()) {
					CustomFieldValues fieldValues = null;
					if (valueDto.getValueId() != null) {
						fieldValues = valueRepo.findById(valueDto.getValueId()).orElseThrow();
						int index = field.getValues().indexOf(fieldValues);
						field.getValues().get(index).setLabel(valueDto.getLabel());
						field.getValues().get(index).setLabelValue(valueDto.getValue());

					} else {
						fieldValues = new CustomFieldValues(valueDto.getLabel(), valueDto.getValue(), field);
						fieldValues.setField(field);
						field.getValues().add(valueRepo.save(fieldValues));
					}
				}
			}

		}
		field = customFieldRepo.save(field);
		return field;
	}

	/**
	 * @param formCustomComponentList
	 * @return List&lt;FormCustomComponentDto&gt;
	 */
	public List<FormCustomComponentDto> convertToFormCustomComponentDto(
			List<FormCustomComponent> formCustomComponentList) {
		List<FormCustomComponentDto> formCustomComponentDtoList = new ArrayList<>();
		for (FormCustomComponent formCustomComponent : formCustomComponentList) {
			UUID formId = null;
			if (formCustomComponent.getForm() != null) {
				formId = formCustomComponent.getForm().getUid();
			}
			UUID customFormId = null;
			if (formCustomComponent.getCustomComponentMaster() != null) {
				customFormId = formCustomComponent.getCustomComponentMaster().getUid();
			}
			FormCustomComponentDto formCustomComponentDto = new FormCustomComponentDto(formCustomComponent.getUid(),
					formId, customFormId);
			formCustomComponentDtoList.add(formCustomComponentDto);
		}
		return formCustomComponentDtoList;
	}

	private List<CustomField> getListFields(List<CustomField> fieldList) {
		List<CustomField> fieldsList = new ArrayList<>();
		for (CustomField field : fieldList) {
			fieldsList.add(field);
			if (field.getCustomFieldList() != null && !field.getCustomFieldList().isEmpty()) {
				fieldsList.addAll(getListFields(field.getCustomFieldList()));
			}
		}
		return fieldsList;
	}

	private List<CustomFieldObjDto> findChildFieldObj(UUID uuid, List<CustomField> fieldList) throws IOException {
		List<CustomFieldObjDto> childFieldObjDto = new ArrayList<>();
		for (CustomField field : fieldList) {
			if (field.getParentFieldId() != null && field.getParentFieldId().equals(uuid)) {
				childFieldObjDto.add(createFieldObjDto(field, new ArrayList<>()));
			}
		}

		return childFieldObjDto;
	}

	private List<CustomFieldObjDto> getFieldObjDtoWithChild(List<CustomField> fieldList,
			List<CustomFieldObjDto> fieldObjDtos) throws IOException {
		List<CustomFieldObjDto> parent = new ArrayList<>();
		for (CustomFieldObjDto fieldObjDto : fieldObjDtos) {
			List<CustomFieldObjDto> child = findChildFieldObj(fieldObjDto.getFieldId(), fieldList);
			if (!child.isEmpty()) {
				List<CustomFieldObjDto> childWithGrandChild = getFieldObjDtoWithChild(fieldList, child);
				Collections.sort(childWithGrandChild);
				fieldObjDto.getChildFieldObjDtoList().addAll(childWithGrandChild);
			}
			parent.add(fieldObjDto);
		}
		Collections.sort(parent);
		return parent;
	}

	/**
	 * @param fieldList
	 * @return List&lt;FieldObjDto&gt;
	 * @throws IOException
	 */
	public List<CustomFieldObjDto> convertToFieldDto(List<CustomField> fieldList) throws IOException {
		List<CustomFieldObjDto> parentFieldObjList = new ArrayList<>();
		fieldList = getListFields(fieldList);
		for (CustomField field : fieldList) {
			if (field.getParentFieldId() == null) {
				parentFieldObjList.add(createFieldObjDto(field, new ArrayList<>()));
			}
		}
		parentFieldObjList = getFieldObjDtoWithChild(fieldList, parentFieldObjList);
		return parentFieldObjList;
	}

	/**
	 * @param field                CustomField
	 * @param childFieldObjDtoList List<FieldObjDto>
	 * @return FieldObjDto
	 * @throws IOException
	 */
	private CustomFieldObjDto createFieldObjDto(CustomField field, List<CustomFieldObjDto> childFieldObjDtoList)
			throws IOException {
		if (field.getType() != null && field.getType().contains(BffAdminConstantsUtils.CUSTOM_CONTAINER)
				&& field.getLinkedComponentId() != null && field.getLinkedComponentId().getFields() != null) {

			List<CustomField> fieldList = field.getLinkedComponentId().getFields();
			if (fieldList != null && !fieldList.isEmpty()) {
				List<CustomFieldObjDto> childrenCustom = convertToFieldDto(fieldList);
				childFieldObjDtoList = childrenCustom;
			}
		}
		CustomFieldObjDto fieldObjDto = new CustomFieldObjDto(field, childFieldObjDtoList);
		return fieldObjDto;
	}

	/**
	 * @param customComponentMaster
	 * @param fieldDtoList
	 * @param formCustomComponentDtoList
	 * @return CustomComponentMasterDto
	 */
	public CustomFormDto convertToCustomFormDto(CustomComponentMaster customComponentMaster,
			List<CustomFieldObjDto> fieldDtoList, List<FormCustomComponentDto> formCustomComponentDtoList) {
		return new CustomFormDto.CustomFormBuilder()
				.setName(customComponentMaster.getName())
				.setDescription(customComponentMaster.getDescription())
				.setCustomFormId(customComponentMaster.getUid()).setVisibility(customComponentMaster.isVisibility())
				.setDisabled(customComponentMaster.isIsdisabled()).setFields(fieldDtoList)
				.setFormCustomComponentDto(formCustomComponentDtoList)
				.setFormTitle(customComponentMaster.getFormTitle()).setProductConfigId(customComponentMaster.getProductConfigId()).build();
	
	}

	/**
	 * @param customFormObjDto
	 * @return customFormData
	 * @throws IOException
	 */
	@SuppressWarnings("all")
	public CustomFormData createCustomFormData(CustomFormDto customFormObjDto,String parentKey) throws IOException {
		CustomFormData customFormData = new CustomFormData();
		customFormData.setCustomComponentId(customFormObjDto.getCustomFormId());
		customFormData.setName(customFormObjDto.getName());
		customFormData.setDescription(customFormObjDto.getDescription());
		customFormData.setVisibility(customFormObjDto.isVisibility());
		customFormData.setDisabled(customFormObjDto.isDisabled());
		customFormData.setFormTitle(bffCommonUtil.getResourceBundle(customFormObjDto.getFormTitle()));

		List<FieldComponent> fieldCompMasterList = null;
		if (customFormObjDto.getFields() != null && !customFormObjDto.getFields().isEmpty()) {
			fieldCompMasterList = new ArrayList<>();
			FieldComponent fieldComponent = null;
			for (CustomFieldObjDto fieldObjDto : customFormObjDto.getFields()) {
				if (BffAdminConstantsUtils.COLUMNS.equalsIgnoreCase(fieldObjDto.getType())
						&& fieldObjDto.getChildFieldObjDtoList() != null) {
					fieldComponent = fieldComponentConverter.convertToColumnsFieldComponent(fieldObjDto,parentKey);
				} else if ((BffAdminConstantsUtils.DATAGRID.equalsIgnoreCase(fieldObjDto.getType())
						|| BffAdminConstantsUtils.LISTVIEW.equalsIgnoreCase(fieldObjDto.getType()))
						&& fieldObjDto.getChildFieldObjDtoList() != null) {
					fieldComponent = fieldComponentConverter.convertFieldObjDtoToFieldComponents(fieldObjDto,parentKey);
				} else {
					fieldComponent = fieldComponentConverter.createCustomFieldComponent(fieldObjDto,parentKey);
				}
				if (fieldComponent != null
						&& fieldComponent.getType().contains(BffAdminConstantsUtils.CUSTOM_CONTAINER)) {
					String parentChildKey = parentKey.isEmpty() ?  BffAdminConstantsUtils.EMPTY :  fieldComponent.getKey()+ BffAdminConstantsUtils.UNDERSCORE;
					List<FieldComponent> childOfCustom = fieldComponentConverter.createCustomContainer(fieldObjDto, parentChildKey);
					fieldComponent.setComponents(childOfCustom);
				}
				fieldCompMasterList.add(fieldComponent);
			}
			customFormData.setComponents(fieldCompMasterList);
		}
		List<FormCustomComponentType> formCustomComponentType = null;
		if (customFormObjDto.getFormCustomComponentDto() != null
				&& !customFormObjDto.getFormCustomComponentDto().isEmpty()) {
			formCustomComponentType = new ArrayList<>();
			for (FormCustomComponentDto formCustomComponentDto : customFormObjDto.getFormCustomComponentDto()) {
				FormCustomComponentType formCustomComponentObj = new FormCustomComponentType();
				formCustomComponentObj.setFormCusId(formCustomComponentDto.getFormCusId());
				formCustomComponentObj.setFormId(formCustomComponentDto.getFormId());
				formCustomComponentObj.setCustomFormId(formCustomComponentDto.getCustomFormId());

				formCustomComponentType.add(formCustomComponentObj);
			}
		}
		customFormData.setFormCustomComponentTypes(formCustomComponentType);
		ProductConfig prodConfig =  productConfigRepo.findById(customFormObjDto.getProductConfigId()).orElseThrow();
		Layer layer = new Layer();
		if (prodConfig.getRoleMaster() != null) {
			layer.setLevel(prodConfig.getRoleMaster().getLevel());
			layer.setName(prodConfig.getRoleMaster().getName());
			customFormData.setLayer(layer);
		}
		return customFormData;
	}
}