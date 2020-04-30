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
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the form database table.
 * 
 */
@Audited
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper=false, exclude = "flow")
@JsonIgnoreProperties(value = { "flow", "extendedFromFormId" })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name = "FORM")
@NamedQuery(name = "Form.findAll", query = "SELECT f FROM Form f")
@SelectBeforeUpdate
public class Form extends BffAuditableData<String> implements Serializable {
	private static final long serialVersionUID = 6054370625327848130L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique = true, length = 16, nullable = false)
	private UUID uid;

	@Column(name = "APPLY_TO_ALL_CLONES", nullable = false)
	private boolean applyToAllClones;

	@Column(name = "DESCRIPTION", length = 255)
	@Nationalized
	private String description;

	@Column(name = "FORM_TEMPLATE", length = 255)
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

	@Column(name="NAME",nullable = false, length = 255)
	@Nationalized
	private String name;

	@Column(name = "PARENT_FORM_ID", length = 16)
	private UUID parentFormId;

	@Column(name = "TAG", length = 45)
	@Nationalized
	private String tag;

	@Column(name = "PRODUCT_CONFIG_ID", length = 16, nullable = false)
	private UUID productConfigId;

	@Column(name = "EXTENDED_PARENT_FORM_ID", length = 16)
	private UUID extendedFromFormId;

	@OneToMany(mappedBy = "form", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<Field> fields = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "FLOW_ID", nullable = false)
	private Flow flow;

	@OneToMany(mappedBy = "form", cascade = CascadeType.ALL)
	private List<FormCustomComponent> formCustomComponent = new ArrayList<>();

	@OneToMany(mappedBy = "form", cascade = CascadeType.ALL)
	private List<Tabs> tabs = new ArrayList<>();
	@Column(name = "IS_TABBED_FORM", nullable = true)
	private boolean isTabbedForm;

	@OneToMany(mappedBy = "form", cascade = CascadeType.ALL)
	private List<Events> events = new ArrayList<>();

	@Column(name = "HIDE_GS1_BAR_CODE")
	private boolean hideGs1Barcode;

	@Lob
	@Column(name = "GS1_FORM")
	@Nationalized
	private String gs1Form;

	@Lob
	@Column(name = "PUBLISHED_FORM")
	@Nationalized
	private byte[] publishedForm;

	@Column(nullable = true, length = 255, name = "FORM_TITLE")
	@Nationalized
	private String formTitle;

	public Form(boolean modalForm, boolean isTabbedForm,boolean isDisabled, byte[] publishedForm)
	{
		this.modalForm =modalForm;
		this.isTabbedForm = isTabbedForm;
		this.isDisabled = isDisabled;
		this.publishedForm = publishedForm;
	}

	/**
	 * Copy constructor to clone form.
	 * 
	 * @param form
	 * @param extendedFlag
	 * @param productConfigId
	 * @param copyFlag
	 */
	public Form(Form form, boolean extendedFlag, UUID productConfigId, boolean copyFlag) {
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
		this.productConfigId = productConfigId;
		if (extendedFlag) {
			this.extendedFromFormId = form.getUid();
		} else {
			this.extendedFromFormId = form.getExtendedFromFormId();
		}
		if (copyFlag) {
			this.extendedFromFormId = null;
		}
		this.isTabbedForm = form.isTabbedForm();
		this.hideGs1Barcode = form.isHideGs1Barcode();
		this.gs1Form = form.getGs1Form();
		this.formTitle = form.getFormTitle();

		if (form.getFields() != null && !form.getFields().isEmpty()) {
			for (Field field : form.getFields()) {
				addField(new Field(field,  extendedFlag, productConfigId, copyFlag));
			}
		}
		buildFormHelper(form, copyFlag,extendedFlag);
	}

	/**
	 * @param form
	 */
	private void buildFormHelper(Form form, boolean copyFlag,boolean extendedFlag) {

		if (form.getFormCustomComponent() != null && !form.getFormCustomComponent().isEmpty()) {
			for (FormCustomComponent formCustomComp : form.getFormCustomComponent()) {
				addFormCustomComponent(new FormCustomComponent(formCustomComp));
			}
		}
		if (form.getTabs() != null && !form.getTabs().isEmpty()) {
			for (Tabs tab : form.getTabs()) {
				addTabs(new Tabs(tab));
			}
		}
		if (form.getEvents() != null && !form.getEvents().isEmpty()) {
			for (Events event : form.getEvents()) {
				addEvents(new Events(event, copyFlag,extendedFlag));
			}
		}
	}

	public Field addField(Field field) {
		getFields().add(field);
		field.setForm(this);

		return field;
	}

	public Field removeField(Field field) {
		getFields().remove(field);
		field.setForm(null);

		return field;
	}

	public FormCustomComponent addFormCustomComponent(FormCustomComponent formCustomComponent) {
		getFormCustomComponent().add(formCustomComponent);
		formCustomComponent.setForm(this);

		return formCustomComponent;
	}

	public FormCustomComponent removeFormOutdep(FormCustomComponent formCustomComponent) {
		getFormCustomComponent().remove(formCustomComponent);
		formCustomComponent.setForm(null);

		return formCustomComponent;
	}

	public Tabs addTabs(Tabs tabs) {
		getTabs().add(tabs);
		tabs.setForm(this);
		return tabs;
	}

	public Tabs removeTabs(Tabs tabs) {
		getTabs().remove(tabs);
		tabs.setForm(null);
		return tabs;
	}

	public Events addEvents(Events event) {
		getEvents().add(event);
		event.setForm(this);
		return event;
	}

	public Events removeEvents(Events event) {
		getEvents().remove(event);
		event.setForm(null);
		return event;
	}
}