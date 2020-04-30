/**
 * 
 */
package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.jda.mobility.framework.extensions.entity.Events;
import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.FlowPermission;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.Tabs;
import com.jda.mobility.framework.extensions.model.Event;
import com.jda.mobility.framework.extensions.model.FormAttributes;
import com.jda.mobility.framework.extensions.model.FormData;
import com.jda.mobility.framework.extensions.model.FormProperties;
import com.jda.mobility.framework.extensions.model.Layer;
import com.jda.mobility.framework.extensions.model.Tab;
import com.jda.mobility.framework.extensions.utils.BffUtils;

import lombok.Data;

/**
 * The class FormRequestDto.java
 * HCL Technologies Ltd.
 */
@Data
@JsonInclude(Include.NON_NULL)
public class FormObjDto implements Serializable{

	private static final long serialVersionUID = 7832353162482076917L;
	private UUID formId;
	private String name;
	private String description;
	private boolean published;
	private boolean disabledForm;
	private boolean extDisabled;
	private UUID flowId;
	private String formTemplate;
	private String tag;
	private boolean orphanForm;
	private boolean clonableForm;
	private boolean applyToAllClones;
	private UUID parentFormId;
	private boolean isExtFieldAllDisabled;
	private boolean modalForm;
	private boolean hideToolbar;
	private boolean hideLeftNavigation;
	private boolean hideBottomNavigation;
	private boolean showOnce;
	private List<FieldObjDto> fields;
	private boolean defaultForm;	
	private List<UUID> deleteFields;
	private List<UUID> deleteEvents;
	private List<UUID> deleteValues;
	private List<UUID> deleteDataValues;
	private List<TabDto> tabs;
	private boolean tabbedForm;
	private List<EventsDto> events;
	private boolean hideGs1Barcode;
	private String gs1Form;
	private List<UUID> deleteTabs;
	private String hotKey;
	private FlowDto flowDto;
	private Layer layer;
	private String formTitle;
	private boolean inboundOrphan;
	private boolean outboundOrphan;
	private UUID productConfigId;
	/**
	 * @param form
	 * @param fieldObjDtos
	 */
	public FormObjDto(Form form, List<FieldObjDto> fieldObjDtos) {
		super();
		FlowDto flowDtoOfForm = null;		
		Flow flow = form.getFlow();
		if (flow != null) {
			List<String> permissionList = null;
			if (!CollectionUtils.isEmpty(flow.getFlowPermission())) {
				permissionList = new ArrayList<>();
				for (FlowPermission perm : flow.getFlowPermission()) {
					permissionList.add(perm.getPermission());
				}
			}
			this.flowId = flow.getUid();
			flowDtoOfForm = new FlowDto.FlowBuilder(flow.getName())
					.setFlowId(flow.getUid())
					.setDescription(flow.getDescription())
					.setDefaultFormId(flow.getDefaultFormId())
					.setDisabled(flow.isDisabled())
					.setExtDisabled(flow.isExtDisabled())
					.setPublished(flow.isPublished())
					.setTag(flow.getTag())
					.setVersion(flow.getVersion())
					.setPermissions(permissionList)
					.setExtendedFromFlowId(flow.getExtendedFromFlowId())
					.build();

			if (flow.getDefaultFormId() != null && flow.getDefaultFormId().equals(form.getUid())) {
				this.inboundOrphan = false;
			} else {
				this.inboundOrphan = true;
			}
			this.outboundOrphan = true;
			if (!CollectionUtils.isEmpty(flow.getFormDependencies())) {
				flow.getFormDependencies().stream().forEach(formDependency -> {
					if (form.getUid().equals(formDependency.getOutboundFormId())) {
						this.inboundOrphan = false;
					}
					if (form.getUid().equals(formDependency.getInboundFormId())
							&& formDependency.getOutboundFormId() != null) {
						this.outboundOrphan = false;
					}
				});
			}

			if (flow.getProductConfig() != null && flow.getProductConfig().getRoleMaster() != null) {
				layer = new Layer();
				layer.setLevel(flow.getProductConfig().getRoleMaster().getLevel());
				layer.setName(flow.getProductConfig().getRoleMaster().getName());
			}
		}
		List<EventsDto> eventsOfForm = new ArrayList<>();		
		if (form.getEvents() != null) {
			for (Events eventObj : form.getEvents()) {
				EventsDto dto = new EventsDto(eventObj.getUid(), eventObj.getEvent(), eventObj.getAction(), null,
						BffUtils.getNullable(eventObj.getForm(), Form::getUid));
				eventsOfForm.add(dto);
			}
		}
		List<TabDto> tabDtoList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(form.getTabs())) {
			for (Tabs tab : form.getTabs()) {
				TabDto tabDto = TabDto.builder()
					.linkedFormId(tab.getLinkedFormId())
					.linkedFormName(tab.getLinkedFormName())
					.tabId(tab.getUid())
					.tabName(tab.getTabName())
					.sequence(tab.getSequence())
					.defaultForm(tab.isDefault())
					.build();
				tabDtoList.add(tabDto);
			}
			Collections.sort(tabDtoList);
		}
		
		this.formId = form.getUid();
		this.name = form.getName();
		this.description = form.getDescription();
		this.published = form.isPublished();
		this.disabledForm = form.isDisabled();
		this.extDisabled = form.isExtDisabled();
		
		this.formTemplate = form.getFormTemplate();
		this.tag = form.getTag();
		this.orphanForm = form.isOrphan();
		this.clonableForm = form.isCloneable();
		this.applyToAllClones = form.isApplyToAllClones();
		this.parentFormId = form.getParentFormId();
		this.isExtFieldAllDisabled = form.isExtFieldAllDisabled();
		this.modalForm = form.isModalForm();
		this.hideToolbar = form.isHideToolbar();
		this.hideLeftNavigation = form.isHideLeftNavigation();
		this.hideBottomNavigation = form.isHideBottomNavigation();
		this.showOnce = form.isShowOnce();
		this.fields = fieldObjDtos;
		this.defaultForm = false;
		this.deleteFields = null;
		this.deleteEvents = null;
		this.deleteValues = null;
		this.deleteDataValues = null;
		this.tabbedForm = form.isTabbedForm();
		this.events = eventsOfForm;
		this.hideGs1Barcode = form.isHideGs1Barcode();
		this.gs1Form = form.getGs1Form();
		this.tabs = tabDtoList;
		this.flowDto = flowDtoOfForm;
		this.formTitle = form.getFormTitle();
		this.productConfigId = form.getProductConfigId();
	}

	/**
	 * @param formRequest
	 * @param fields
	 * @param productConfigId
	 */
	public FormObjDto(FormData formRequest, List<FieldObjDto> fields, UUID productConfigId) {
		super();
		boolean disabledFormRequest = false;
		boolean extDisabledFormRequest = false;
		boolean isExtFieldAllDisabledFormRequest = false;
		boolean isModalFormFormRequest = false;
		boolean hideToolbarFormRequest = false;
		boolean hideLeftNavigationFormRequest = false;
		boolean hideBottomNavigationFormRequest = false;
		boolean hideBarCodeNavigationFormRequest = false;
		String gs1FormFormRequest = null;
		List<EventsDto> eventDtoList = new ArrayList<>();
		List<TabDto> tabsDtoList = new ArrayList<>();
		FormProperties formProperties = formRequest.getFormProperties();
		if (formProperties != null) {
			FormAttributes formAttributes = formProperties.getProperties();
			if (formAttributes != null) {
				disabledFormRequest = formAttributes.isDisableForm();
				extDisabledFormRequest = formAttributes.isDisableFormExtensions();
				isExtFieldAllDisabledFormRequest = formAttributes.isDisableAllExtensions();
				isModalFormFormRequest = formAttributes.isModalForm();
				hideToolbarFormRequest = formAttributes.isHideToolbar();
				hideLeftNavigationFormRequest = formAttributes.isHideLeftNavigation();
				hideBottomNavigationFormRequest = formAttributes.isHideBottomNavigation();
				hideBarCodeNavigationFormRequest = formAttributes.isHideGs1Barcode();
				if (formAttributes.getGs1Form() != null && formAttributes.isHideGs1Barcode()) {
					gs1FormFormRequest = formAttributes.getGs1Form().toString();
				}
			}
			if (!CollectionUtils.isEmpty(formProperties.getEvents())) {
				for (Event eventsOfFormRequest : formProperties.getEvents()) {
					eventDtoList.add(new EventsDto(eventsOfFormRequest.getEventId(), eventsOfFormRequest.getEventName(),
							eventsOfFormRequest.getAction().toString(), null, formRequest.getFormId()));
				}
			}
		}
		if (!CollectionUtils.isEmpty(formRequest.getTabs())) {
			int sequence = 0;
			for (Tab tab : formRequest.getTabs()) {
				TabDto tabDto = TabDto.builder()
						.linkedFormId(tab.getLinkedFormId())
						.linkedFormName(tab.getLinkedFormName())
						.tabId(tab.getTabId())
						.tabName(tab.getTabName().getRbkey())
						.sequence(sequence++)
						.defaultForm(tab.isDefaultForm())
						.build();
				tabsDtoList.add(tabDto);
			}
		}
		this.formId = formRequest.getFormId();
		this.name = formRequest.getName();
		this.description = formRequest.getDescription();
		this.published = formRequest.isPublished();
		this.disabledForm = disabledFormRequest;
		this.extDisabled = extDisabledFormRequest;
		this.flowId = formRequest.getFlowId();
		this.formTemplate = formRequest.getFormTemplate();
		this.tag = formRequest.getTag();
		this.orphanForm = formRequest.isOrphanForm();
		this.clonableForm = formRequest.isClonableForm();
		this.applyToAllClones = formRequest.isApplyToAllClones();
		this.parentFormId = formRequest.getParentFormId();
		this.isExtFieldAllDisabled = isExtFieldAllDisabledFormRequest;
		this.modalForm = isModalFormFormRequest;
		this.hideToolbar = hideToolbarFormRequest;
		this.hideLeftNavigation = hideLeftNavigationFormRequest;
		this.hideBottomNavigation = hideBottomNavigationFormRequest;
		this.showOnce = formRequest.isShowonce();
		this.fields = fields;
		this.defaultForm = formRequest.isDefaultForm();
		this.deleteFields = formRequest.getDeleteFields();
		this.deleteEvents = formRequest.getDeleteEvents();
		this.deleteValues = formRequest.getDeleteValues();
		this.deleteDataValues = formRequest.getDeleteDataValues();
		this.tabbedForm = formRequest.isTabbedForm();
		this.events = eventDtoList;
		this.hideGs1Barcode = hideBarCodeNavigationFormRequest;
		this.gs1Form = gs1FormFormRequest;
		this.tabs = tabsDtoList;
		this.deleteTabs = formRequest.getDeleteTabs();
		this.formTitle = BffUtils.getNullable(formRequest.getFormTitle(), TranslationRequest::getRbkey);
		this.productConfigId = productConfigId;
	}
}