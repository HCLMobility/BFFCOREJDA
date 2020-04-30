package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Nationalized;

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


/**
 * The persistent class for the custom_component_master database table.
 * 
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper = false) @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@JsonIgnoreProperties(value = { "formCustomComponent" })
@Entity
@Table(name="CUSTOM_COMPONENT_MASTER")
@NamedQuery(name="CustomComponentMaster.findAll", query="SELECT c FROM CustomComponentMaster c")
public class CustomComponentMaster extends BffAuditableData<String> implements Serializable {

	private static final long serialVersionUID = 818772058195375211L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID", unique=true, length=16, nullable=false)
	private UUID uid;

	@Column(name="NAME", nullable=false, length=255)
	@Nationalized
	private String name;
	
	@Lob
	@Nationalized
	@Column(name="DESCRIPTION")
	private String description;	

	@Column(name="VISIBILITY", length=4)
	private boolean visibility;
	
	@Column(name="ISDISABLED", length=4)
	private boolean isdisabled;
	
	//bi-directional many-to-one association to Field
	@OneToMany(mappedBy="customComponentMaster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<CustomField> fields;
	
	@OneToMany(mappedBy="customComponentMaster",cascade = CascadeType.REMOVE)
	private List<FormCustomComponent> formCustomComponent;
	
	@Column(name="PRODUCT_CONFIG_ID", length=16, nullable=false)
	private UUID productConfigId;
	
	@Column(nullable=true, length=255,name ="FORM_TITLE")
	@Nationalized
	private String formTitle;

	public CustomField addField(CustomField field) {
		getFields().add(field);
		field.setCustomComponentMaster(this);

		return field;
	}

	public CustomField removeField(CustomField field) {
		getFields().remove(field);
		field.setCustomComponentMaster(null);
		return field;
	}
}