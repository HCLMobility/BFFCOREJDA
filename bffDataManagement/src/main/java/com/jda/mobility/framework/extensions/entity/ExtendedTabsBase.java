/**
 * 
 */
package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Nationalized;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the extended_tabs database table.
 * 
 * @author HCL Technologies Ltd.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode @Builder(toBuilder = true)
@Entity
@Table(name = "EXTENDED_TABS")
@NamedQuery(name = "ExtendedTabsBase.findAll", query = "SELECT t FROM ExtendedTabsBase t")
public class ExtendedTabsBase implements Serializable {

	private static final long serialVersionUID = -7705699646081043350L;
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;
	@Column(name = "TAB_NAME", length=255)
	@Nationalized
	private String tabName;
	@Column(name = "LINKED_FORM_NAME", length=255)
	@Nationalized
	private String linkedFormName;
	@Column(name = "LINKED_FORM_ID", length=16)
	private UUID linkedFormId;
	@Column(name = "SEQUENCE")
	private int sequence;
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "EXTENDED_FORM_ID")
	private ExtendedFormBase extendedForm;
	@Column(name = "IS_DEFAULT")
	private boolean isDefault;
	

	/**
	 * @param tabName
	 * @param linkedFormName
	 * @param linkedFormId
	 * @param sequence
	 * @param isDefault
	 */
	public ExtendedTabsBase(String tabName, String linkedFormName, UUID linkedFormId, int sequence, boolean isDefault, UUID uid) {
		super();
		this.tabName = tabName;
		this.linkedFormName = linkedFormName;
		this.linkedFormId = linkedFormId;
		this.sequence = sequence;
		this.isDefault = isDefault;
		this.uid = uid;
	}
	/**
	 * @param tabs
	 */
	public ExtendedTabsBase(Tabs tabs) {
		this.tabName = tabs.getTabName();
		this.linkedFormId = tabs.getLinkedFormId();
		this.sequence = tabs.getSequence();
		this.linkedFormName = tabs.getLinkedFormName();
		this.isDefault = tabs.isDefault();
		this.uid = tabs.getUid();				
	}
}
