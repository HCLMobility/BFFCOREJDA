/**
 * 
 */
package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
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
 * The persistent class for the extended_field_values database table.
 * 
 * @author HCL Technologies Ltd.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode @Builder(toBuilder = true)
@Entity
@Table(name = "EXTENDED_FIELD_VALUES")
@NamedQuery(name = "ExtendedFieldValuesBase.findAll", query = "SELECT v FROM ExtendedFieldValuesBase v")
public class ExtendedFieldValuesBase implements Serializable {
	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = -1459013830807757350L;
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;
	@Column(name = "LABEL", length=255)
	@Nationalized
	private String label;
	@Column(name = "VALUE", length=255)
	@Nationalized
	private String labelValue;
	@ManyToOne
	@JoinColumn(name = "EXTENDED_FIELD_ID")
	private ExtendedFieldBase extendedField;
	@Column(name = "IS_COMPARED")
	private boolean isCompared;

	/**
	 * @param label
	 * @param labelValue
	 */
	public ExtendedFieldValuesBase(String label, String labelValue, UUID uid) {
		super();
		this.label = label;
		this.labelValue = labelValue;
		this.uid = uid;
	}
}