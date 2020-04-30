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
 * The persistent class for the extended_flow database table.
 * 
 * @author HCL Technologies Ltd.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "EXTENDED_FLOW")
@NamedQuery(name = "ExtendedFlowBase.findAll", query = "SELECT f FROM ExtendedFlowBase f")
@SelectBeforeUpdate
public class ExtendedFlowBase extends BffAuditableData<String> implements Serializable {
	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = -4767086159841282712L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;

	@Column(name = "DEFAULT_FORM_ID", length=16)
	private UUID defaultFormId;

	@Column(name="DESCRIPTION",length=255)
	@Nationalized
	private String description;

	@Column(name = "IS_DISABLED", nullable = false)
	private boolean isDisabled;

	@Column(name = "IS_EXT_DISABLED")
	private boolean isExtDisabled;

	@Column(name = "IS_PUBLISHED", nullable = false)
	private boolean isPublished;

	@Column(name="NAME",nullable = false, length=255)
	@Nationalized
	private String name;

	@Column(name="TAG",length = 45)
	@Nationalized
	private String tag;

	@Column(name = "PRODUCT_CONFIG_ID", length=16, nullable = false)
	private UUID productConfigId;

	@Column(name = "EXTENDED_PARENT_FLOW_ID", length=16)
	private UUID extendedFromFlowId;

	@OneToMany(mappedBy = "extendedFlow", cascade = CascadeType.ALL)
	private List<ExtendedFormBase> extendedForms = new ArrayList<>();

	@OneToMany(mappedBy = "extendedFlow", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<ExtendedFlowPermissionBase> extendedFlowPermission = new ArrayList<>();
	
	@Column(name="VERSION")
	private long version;
	
	@Column(name="DEFAULT_FORM_TABBED", nullable=false)
	private boolean defaultFormTabbed;
	
	
	/**
	 * Copy constructor to clones FLow to be extended.
	 * @param flow
	 */
	public ExtendedFlowBase(Flow flow) {
		super();
		this.defaultFormId = flow.getDefaultFormId();
		this.description = flow.getDescription();
		this.isDisabled = flow.isDisabled();
		this.isExtDisabled = flow.isExtDisabled();
		this.isPublished = flow.isPublished();
		this.name = flow.getName();
		this.tag = flow.getTag();
		this.productConfigId = flow.getProductConfig().getUid();
		this.extendedFromFlowId = flow.getUid();
		this.uid = flow.getUid();
		this.version = flow.getVersion();
		this.defaultFormTabbed = flow.isDefaultFormTabbed();

		if (flow.getForms() != null && !flow.getForms().isEmpty()) {
			for (Form form : flow.getForms()) {
				addForm(new ExtendedFormBase(form));
			}
		}
		if (flow.getFlowPermission() != null && !flow.getFlowPermission().isEmpty()) {
			for (FlowPermission flowPermissions : flow.getFlowPermission()) {
				addPermission(new ExtendedFlowPermissionBase(flowPermissions));
			}
		}
	}

	public ExtendedFormBase addForm(ExtendedFormBase form) {
		getExtendedForms().add(form);
		form.setExtendedFlow(this);

		return form;
	}

	public ExtendedFormBase removeForm(ExtendedFormBase form) {
		getExtendedForms().remove(form);
		form.setExtendedFlow(null);

		return form;
	}

	public ExtendedFlowPermissionBase addPermission(ExtendedFlowPermissionBase flowPermission) {
		getExtendedFlowPermission().add(flowPermission);
		flowPermission.setExtendedFlow(this);
		return flowPermission;
	}

	public ExtendedFlowPermissionBase removePermission(ExtendedFlowPermissionBase flowPermission) {
		getExtendedFlowPermission().remove(flowPermission);
		flowPermission.setExtendedFlow(null);
		return flowPermission;
	}
	
}