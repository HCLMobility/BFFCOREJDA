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
 * The persistent class for the custom_field_values database table.
 *
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name="CUSTOM_FIELD_VALUES")
@NamedQuery(name="CustomFieldValues.findAll", query="SELECT v FROM CustomFieldValues v")
public class CustomFieldValues implements Serializable {

	private static final long serialVersionUID = 4303511738300528494L;
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name = "UID", unique=true, length=16, nullable=false)
	private UUID uid;
	@Column(name = "LABEL",length=255)
	@Nationalized
	private String label;
	@Column(name = "VALUE",length=255)
	@Nationalized
	private String labelValue;
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name="FIELD_ID")
	private CustomField field;
	
	
	public CustomFieldValues(String label, String labelValue, CustomField field) {
		this.label = label;
		this.labelValue = labelValue;
		this.field = field;
	}
}