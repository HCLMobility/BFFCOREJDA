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
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

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
 * The class FormCustomComponent.java
 * @author puneet-m
 * HCL Technologies Ltd.
 */
/**
 * The persistent class for the form_custom_component database table.
 * 
 */
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name="FORM_CUSTOM_COMPONENT")
@NamedQuery(name="FormCustomComponent.findAll", query="SELECT f FROM FormCustomComponent f")
public class FormCustomComponent implements Serializable {
	private static final long serialVersionUID = -3227920259907828436L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;

	@EqualsAndHashCode.Exclude 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FORM_ID")
	private Form form;

	@EqualsAndHashCode.Exclude 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CUSTOM_COMPONENT_ID")
	private CustomComponentMaster customComponentMaster;
	
	public FormCustomComponent(FormCustomComponent formCustomComponent) {
		this.customComponentMaster = formCustomComponent.getCustomComponentMaster();
	}
}
