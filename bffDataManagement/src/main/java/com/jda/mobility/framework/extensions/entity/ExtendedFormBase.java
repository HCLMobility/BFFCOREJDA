/**
 * 
 */
package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.SelectBeforeUpdate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the extended_form database table.
 * 
 * @author HCL Technologies Ltd.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "EXTENDED_FORM")
@NamedQuery(name = "ExtendedFormBase.findAll", query = "SELECT f from ExtendedFormBase f")
@SelectBeforeUpdate
public class ExtendedFormBase extends BffAuditableData<String> implements Serializable {

	private static final long serialVersionUID = 6054370625327848130L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;

	@Column(name = "APPLY_TO_ALL_CLONES", nullable = false)
	private boolean applyToAllClones;

	@Column(name="DESCRIPTION",length=255)
	@Nationalized
	private String description;

	@Column(name = "FORM_TEMPLATE", length=255)
	@Nationalized
	private String formTemplate;

	@Column(name = "IS_CLONEABLE", nullable = false)
	private boolean isCloneable;

	@Column(name = "EXT_FIELD_ALL_DISABLED")
	private boolean extFieldAllDisabled;

	@Column(name = "MODAL_FORM")
	private boolean modalForm;

	@Column(name = "HIDE_TOOLBAR")
	private boolean hideToolbar;

	@Column(name = "HIDE_LEFT_NAVIGATION")
	private boolean hideLeftNavigation;

	@Column(name = "HIDE_BOTTOM_NAVIGATION")
	private boolean hideBottomNavigation;

	@Column(name = "SHOW_ONCE")
	private boolean showOnce;

	@Column(name = "IS_DISABLED")
	private boolean isDisabled;

	@Column(name = "IS_EXT_DISABLED")
	private boolean isExtDisabled;

	@Column(name = "IS_ORPHAN")
	private boolean isOrphan;

	@Column(name = "IS_PUBLISHED", nullable = false)
	private boolean isPublished;

	@Column(name="NAME", nullable = false, length=255)
	@Nationalized
	private String name;

	@Column(name = "PARENT_FORM_ID", length=16)
	private UUID parentFormId;
	@Nationalized
	@Column(name="TAG", length=45)
	private String tag;

	@Column(name = "PRODUCT_CONFIG_ID", length=16, nullable = false)
	private UUID productConfigId;

	@Column(name = "EXTENDED_PARENT_FORM_ID", length=16)
	private UUID extendedFromFormId;

	@OneToMany(mappedBy = "extendedFormBase", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<ExtendedFieldBase> extendedFields = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "EXTENDED_FLOW_ID", nullable = false)
	private ExtendedFlowBase extendedFlow;

//	@OneToMany(mappedBy = "extendedForm", cascade = CascadeType.ALL)
//	private List<ExtendedFormCustomComponentBase> extendedFormCustomComponent = new ArrayList<>();

	@OneToMany(mappedBy = "extendedForm", cascade = CascadeType.ALL)
	private List<ExtendedTabsBase> extendedTabs = new ArrayList<>();

	@Column(name = "IS_TABBED_FORM", nullable = true)
	private boolean isTabbedForm;

	@OneToMany(mappedBy = "extendedForm", cascade = CascadeType.ALL)
	private List<ExtendedEventsBase> extendedEvents = new ArrayList<>();

	@Column(name = "HIDE_GS1_BAR_CODE")
	private boolean hideGs1Barcode;
	@Nationalized
	@Lob
	@Column(name = "GS1_FORM")
	private String gs1Form;
	
	@Column(nullable=true, length=255,name ="FORM_TITLE")
	@Nationalized
	private String formTitle;

	/**
	 * Copy constructor to clone form.
	 * @param form
	 */
	public ExtendedFormBase(Form form) {
		super();
		this.applyToAllClones = form.isApplyToAllClones();
		this.description = form.getDescription();
		this.formTemplate = form.getFormTemplate();
		this.isCloneable = form.isCloneable();
		this.extFieldAllDisabled = form.isExtFieldAllDisabled();
		this.modalForm = form.isModalForm();
		this.hideToolbar = form.isHideToolbar();
		this.hideLeftNavigation = form.isHideLeftNavigation();
		this.hideBottomNavigation = form.isHideBottomNavigation();	
		this.showOnce = form.isShowOnce();
		this.isDisabled = form.isDisabled();
		this.isExtDisabled = form.isExtDisabled();
		this.isOrphan = form.isOrphan();
		this.isPublished = false;
		this.name = form.getName();
		this.parentFormId = form.getParentFormId();
		this.tag = form.getTag();
		this.productConfigId = form.getProductConfigId();
		if(form.getExtendedFromFormId() != null) {
			this.extendedFromFormId = form.getExtendedFromFormId();			
		}else {
			this.extendedFromFormId = form.getUid();
		}
		
		this.isTabbedForm = form.isTabbedForm();
		this.hideGs1Barcode = form.isHideGs1Barcode();
		this.gs1Form = form.getGs1Form();
		this.uid = form.getUid();
		this.formTitle = form.getFormTitle();
		
		if (form.getFields() != null && !form.getFields().isEmpty()) {
			for (Field field : form.getFields()) {
				addField(new ExtendedFieldBase(field));
			}
		}
		buildFormHelper(form);
	}

	/**
	 * @param form
	 */
	private void buildFormHelper(Form form) {
		
		/**if (form.getFormCustomComponent() != null && !form.getFormCustomComponent().isEmpty()) {
			for (FormCustomComponent formCustomComp : form.getFormCustomComponent()) {
				addFormCustomComponent(new ExtendedFormCustomComponentBase(formCustomComp));
			}
		}*/
		if (form.getTabs() != null && !form.getTabs().isEmpty()) {
			for (Tabs tab : form.getTabs()) {
				addTabs(new ExtendedTabsBase(tab));
			}
		}
		if (form.getEvents() != null && !form.getEvents().isEmpty()) {
			for (Events event : form.getEvents()) {
				addEvents(new ExtendedEventsBase(event.getUid(),event.getEvent(), event.getAction()));
			}
		}
	}
	
	public ExtendedFieldBase addField(ExtendedFieldBase field) {
		getExtendedFields().add(field);
		field.setExtendedFormBase(this);

		return field;
	}

	public ExtendedFieldBase removeField(ExtendedFieldBase field) {
		getExtendedFields().remove(field);
		field.setExtendedFormBase(null);

		return field;
	}
	/**public ExtendedFormCustomComponentBase addFormCustomComponent(ExtendedFormCustomComponentBase formCustomComponent) {
		getExtendedFormCustomComponent().add(formCustomComponent);
		formCustomComponent.setExtendedForm(this);

		return formCustomComponent;
	}*/
	public ExtendedTabsBase addTabs(ExtendedTabsBase tabs) {
		getExtendedTabs().add(tabs);
		tabs.setExtendedForm(this);
		return tabs;
	}

	public ExtendedTabsBase removeTabs(ExtendedTabsBase tabs) {
		getExtendedTabs().remove(tabs);
		tabs.setExtendedForm(null);
		return tabs;
	}
	public ExtendedEventsBase addEvents(ExtendedEventsBase event) {
		getExtendedEvents().add(event);
		event.setExtendedForm(this);
		return event;
	}

	public ExtendedEventsBase removeEvents(ExtendedEventsBase event) {
		getExtendedEvents().remove(event);
		event.setExtendedForm(null);
		return event;
	}

}
