package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Nationalized;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the product_properties database table.
 * 
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper = false) @Builder(toBuilder = true)
@JsonIgnoreProperties(value = { "productMaster" })
@Entity
@Table(name="PRODUCT_PROPERTIES")
@NamedQuery(name="ProductProperty.findAll", query="SELECT p FROM ProductProperty p")
public class ProductProperty extends BffAuditableData<String> implements Serializable {
	private static final long serialVersionUID = -2890878901597196896L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;
	@Nationalized
	@Lob
	@Column(name="ADDL_CONFIG")
	private String addlConfig;

	@Column(name="IS_PRIMARY_REF")
	private boolean isPrimaryRef;

	@Column(name="IS_SECONDARY_REF")
	private boolean isSecondaryRef;
	@Nationalized
	@Column(name="NAME",nullable=false, length=255)
	private String name;
	@Nationalized
	@Column(name="PROP_VALUE", length=255)
	private String propValue;

	//bi-directional many-to-one association to ProductMaster
	@ManyToOne
	@JoinColumn(name="PRODUCT_ID", nullable=false)
	private ProductMaster productMaster;

}