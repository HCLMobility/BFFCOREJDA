package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
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
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@JsonIgnoreProperties(value = { "extendedFromDataId" })
@Entity
@Table(name="DATA")
@NamedQuery(name="Data.findAll", query="SELECT d FROM Data d")
public class Data implements Serializable {
	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 8796121536189502552L;
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name = "UID", unique=true, length=16, nullable=false)
	private UUID uid;
	@Column(name = "DATA_LABEL",length=255)
	@Nationalized
	private String datalabel;
	@Column(name = "DATA_VALUE")
	@Nationalized
	@Lob
	private String datavalue;
	@EqualsAndHashCode.Exclude 
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name="FIELD_ID")
	private Field field;
	
	@Column(name="EXTENDED_PARENT_DATA_ID", length=16) 
	private UUID extendedFromDataId;
	/**
	 * @param datalabel
	 * @param datavalue
	 */
	public Data(String datalabel, String datavalue) {
		super();
		this.datalabel = datalabel;
		this.datavalue = datavalue;
	}
	public Data(Data data, boolean copyFlag,boolean extendedFlag) {
		super();
		this.datalabel = data.datalabel;
		this.datavalue = data.datavalue;
		if (extendedFlag) {
			this.extendedFromDataId = data.getUid();
		} else {
			this.extendedFromDataId = data.getExtendedFromDataId();
		}
		if(copyFlag) {
			this.extendedFromDataId = null;
		}
	}
}