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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the extended_data database table.
 * 
 * @author HCL Technologies Ltd.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode @Builder(toBuilder = true)
@Entity
@Table(name = "EXTENDED_DATA")
@NamedQuery(name = "ExtendedDataBase.findAll", query = "SELECT d FROM ExtendedDataBase d")
public class ExtendedDataBase implements Serializable {
	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 8796121536189502552L;
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID", unique=true, length=16, nullable=false)
	private UUID uid;
	@Column(name = "DATA_LABEL", length=255)
	@Nationalized
	private String datalabel;
	@Column(name = "DATA_VALUE")
	@Lob
	@Nationalized
	private String datavalue;
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "EXTENDED_FIELD_ID")
	private ExtendedFieldBase extendedField;
	@Column(name = "IS_COMPARED")
	private boolean isCompared;

	/**
	 * @param datalabel
	 * @param datavalue
	 */
	public ExtendedDataBase(String datalabel, String datavalue, UUID uid) {
		super();
		this.datalabel = datalabel;
		this.datavalue = datavalue;
		this.uid = uid;
	}
}
