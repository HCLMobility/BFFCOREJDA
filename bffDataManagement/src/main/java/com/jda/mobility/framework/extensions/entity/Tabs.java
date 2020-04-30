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
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * The persistent class for the Tabs database table.
 * 
 */
@Audited
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name="TABS")
@NamedQuery(name="Tabs.findAll", query="SELECT t FROM Tabs t")
public class Tabs implements Serializable {
	private static final long serialVersionUID = -7705699646081043350L;
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;
	@Nationalized
	@Column(name="TAB_NAME", length=255)
	private String tabName;
	@Nationalized
	@Column(name="LINKED_FORM_NAME",length=255)
	private String linkedFormName;
	@Column(name="LINKED_FORM_ID", length=16)
	private UUID linkedFormId;
	@Column(name="SEQUENCE")
	private int sequence;
	@EqualsAndHashCode.Exclude 
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name="FORM_ID")
	private Form form;
	@Column(name="IS_DEFAULT")
	private boolean isDefault;

	/**
	 * @param tabs
	 */
	public Tabs(Tabs tabs) {
		this.tabName = tabs.getTabName();
		this.linkedFormId = tabs.getLinkedFormId();
		this.sequence = tabs.getSequence();
		this.linkedFormName = tabs.getLinkedFormName();
		this.isDefault = tabs.isDefault();
	}

}