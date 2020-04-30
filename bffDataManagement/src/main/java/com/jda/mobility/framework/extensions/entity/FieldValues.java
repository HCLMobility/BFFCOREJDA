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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Audited
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE)  @EqualsAndHashCode(callSuper=false) @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@JsonIgnoreProperties(value = { "extendedFromFieldValuesId" })
@Entity
@Table(name="FIELD_VALUES")
@NamedQuery(name="FieldValues.findAll", query="SELECT v FROM FieldValues v")
public class FieldValues extends BffAuditableData<String> implements Serializable{
	private static final long serialVersionUID = -1459013830807757350L;
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;
	@Column(name = "LABEL",length=255)
	@Nationalized
	private String label;
	@Column(name = "VALUE",length=255)
	@Nationalized
	private String labelValue;
	@EqualsAndHashCode.Exclude 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="FIELD_ID")
	private Field field;
	@Column(name="EXTENDED_PARENT_FIELD_VALUE_ID", length=16) 
	private UUID extendedFromFieldValuesId;
	/**
	 * @param label
	 * @param labelValue
	 */
	public FieldValues(String label, String labelValue) {
		super();
		this.label = label;
		this.labelValue = labelValue;
	}
	public FieldValues(FieldValues values, boolean copyFlag,boolean extendedFlag) {
		super();
		this.label = values.label;
		this.labelValue = values.labelValue;
		if (extendedFlag) {
			this.extendedFromFieldValuesId = values.getUid();
		} else {
			this.extendedFromFieldValuesId = values.getExtendedFromFieldValuesId();
		}
		if(copyFlag) {
			this.extendedFromFieldValuesId = null;
		}
	}
}