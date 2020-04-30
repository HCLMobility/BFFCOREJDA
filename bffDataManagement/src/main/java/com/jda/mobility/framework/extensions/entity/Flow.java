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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OrderBy;
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
 * The persistent class for the flow database table.
 * 
 */
@Audited
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper=false)
@JsonIgnoreProperties(value = { "forms", "extendedFromFlowId", "extendedFromFlowName","formDependencies","publishedFormDependencies" })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name = "FLOW", indexes = {
		@Index(name = "idx_name", columnList= "name,version", unique = true),
		@Index(name = "idx_prd_id", columnList = "PRODUCT_CONFIG_ID", unique = false)})
@NamedQuery(name = "Flow.findAll", query = "SELECT f FROM Flow f")
@SelectBeforeUpdate
public class Flow extends BffAuditableData<String> implements Serializable {
	private static final long serialVersionUID = -4767086159841282712L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique = true, length = 16, nullable = false)
	private UUID uid;

	@Column(name = "DEFAULT_FORM_ID", length = 16)
	private UUID defaultFormId;

	@Column(length = 255)
	@Nationalized
	private String description;

	@Column(name = "IS_DISABLED", nullable = false)
	private boolean isDisabled;

	@Column(name = "IS_EXT_DISABLED")
	private boolean isExtDisabled;

	@Column(name = "IS_PUBLISHED", nullable = false)
	private boolean isPublished;

	@Column(name="NAME",nullable = false)
	@Nationalized
	private String name;

	@Column(name="TAG",length = 45)
	@Nationalized
	private String tag;

	@ManyToOne
	@JoinColumn(name = "PRODUCT_CONFIG_ID")
	private ProductConfig productConfig;

	@Column(name = "EXTENDED_PARENT_FLOW_ID", length = 16)
	private UUID extendedFromFlowId;

	@Column(name = "EXTENDED_PARENT_FLOW_NAME")
	private String extendedFromFlowName;

	@OneToMany(mappedBy = "flow", cascade = CascadeType.ALL)
	@OrderBy(clause = "lastModifiedDate DESC")
	private List<Form> forms = new ArrayList<>();

	@OneToMany(mappedBy = "flow", cascade = CascadeType.ALL)
	private List<FlowPermission> flowPermission = new ArrayList<>();

	@Column(name = "VERSION")
	private long version;

	@Column(name = "DEFAULT_FORM_TABBED", nullable = false)
	private boolean defaultFormTabbed;
	
	@Column(name = "DEFAULT_MODAL_FORM")
	private boolean defaultModalForm;
	
	//To maintain published default form id
	@Column(name = "PUBLISHED_DEFAULT_FORM_ID", length = 16)
	private UUID publishedDefaultFormId;
	
	@Column(name = "PUBLISHED_FLOW")
	private boolean publishedFlow;
	
	
	@OneToMany(mappedBy = "inboundFlow", cascade = CascadeType.ALL)
	private List<FormDependency> formDependencies = new ArrayList<>();
	
	@OneToMany(mappedBy = "inboundFlow", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PublishedFormDependency> publishedFormDependencies = new ArrayList<>();
	
	@Column(name = "EXTENDED_PARENT_FLOW_VERSION")
	private long extendedFromFlowVersion;

	/**
	 * Copy constructor to clones FLow to be extended.
	 * 
	 * @param flow
	 * @param extendedFlag
	 * @param newFlowName
	 * @param productConfig
	 */
	public Flow(Flow flow,  boolean extendedFlag, String newFlowName, ProductConfig productConfig) {
		super();
		this.defaultFormId = flow.getDefaultFormId();
		this.description = flow.getDescription();
		this.isDisabled = flow.isDisabled();
		this.isExtDisabled = flow.isExtDisabled();
		this.isPublished = false;
		this.publishedFlow = false;
		this.name = newFlowName;
		this.tag = flow.getTag();
		this.productConfig = productConfig;
		this.version = flow.getVersion();
		this.defaultFormTabbed = flow.isDefaultFormTabbed();
		this.defaultModalForm = flow.isDefaultModalForm();
		if (extendedFlag) {
			this.extendedFromFlowId = flow.getUid();
			this.extendedFromFlowName = flow.getName();
			this.extendedFromFlowVersion = flow.getVersion();
		} else {
			this.extendedFromFlowId = flow.getExtendedFromFlowId();
			this.extendedFromFlowName = flow.getExtendedFromFlowName();
			this.extendedFromFlowVersion = flow.getExtendedFromFlowVersion();
		}
		if (!flow.getForms().isEmpty()) {
			for (Form form : flow.getForms()) {
				addForm(new Form(form,  extendedFlag, productConfig.getUid(), false));
			}
		}
		if (!flow.getFlowPermission().isEmpty()) {
			for (FlowPermission flowPermissions : flow.getFlowPermission()) {
				addPermission(new FlowPermission(flowPermissions));
			}
		}
	}

	
	public Form addForm(Form form) {
		getForms().add(form);
		form.setFlow(this);

		return form;
	}

	public Form removeForm(Form form) {
		getForms().remove(form);
		form.setFlow(null);

		return form;
	}

	public FlowPermission addPermission(FlowPermission flowPermission) {
		getFlowPermission().add(flowPermission);
		flowPermission.setFlow(this);
		return flowPermission;
	}

	public FlowPermission removePermission(FlowPermission flowPermission) {
		getFlowPermission().remove(flowPermission);
		flowPermission.setFlow(null);
		return flowPermission;
	}
	
	public FormDependency addFormDependency(FormDependency formDependency) {
		getFormDependencies().add(formDependency);
		formDependency.setInboundFlow(this);
		return formDependency;
	}
	
	public FormDependency removeFormDependency(FormDependency formDependency) {
		getFormDependencies().remove(formDependency);
		formDependency.setInboundFlow(null);
		return formDependency;
	}
}