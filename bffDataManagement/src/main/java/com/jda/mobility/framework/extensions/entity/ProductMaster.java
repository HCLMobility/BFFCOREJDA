package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.SelectBeforeUpdate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the product_master database table.
 * 
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper = false) @Builder(toBuilder = true)
@Entity
@Table(name="PRODUCT_MASTER")
@NamedQuery(name="ProductMaster.findAll", query="SELECT p FROM ProductMaster p")
@SelectBeforeUpdate
public class ProductMaster extends BffAuditableData<String> implements Serializable {
	private static final long serialVersionUID = -6780835823539658486L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;
	@Nationalized
	@Lob
	@Column(name="CONFIG_PROPERTIES")
	private String configProperties;
	@Nationalized
	@Column(name="NAME",nullable=false, length=255)
	private String name;
	
	@Nationalized
	@Column(name="SCHEME",nullable=false, length=45)
	private String scheme;
	
	@Nationalized
	@Column(name="CONTEXT_PATH",nullable=false, length=255)
	private String contextPath;
	
	@Nationalized
	@Column(name="PORT",nullable=false, length=45)
	private String port;

	//bi-directional many-to-one association to productDataSourceMaster
	@OneToMany(mappedBy="productMaster")
	private List<ProductDataSourceMaster> productDataSourceMaster;

	//bi-directional many-to-one association to ProductProperty
	@OneToMany(mappedBy="productMaster")
	private List<ProductProperty> productProperties;



	public ProductDataSourceMaster addProductDataSourceMaster(ProductDataSourceMaster productDataSourceMaster) {
		getProductDataSourceMaster().add(productDataSourceMaster);
		productDataSourceMaster.setProductMaster(this);
		return productDataSourceMaster;
	}

	public ProductDataSourceMaster removeProductConfig(ProductDataSourceMaster productDataSourceMaster) {
		getProductDataSourceMaster().remove(productDataSourceMaster);
		productDataSourceMaster.setProductMaster(null);
		return productDataSourceMaster;
	}
	
	public ProductProperty addProductProperty(ProductProperty productProperty) {
		getProductProperties().add(productProperty);
		productProperty.setProductMaster(this);

		return productProperty;
	}

	public ProductProperty removeProductProperty(ProductProperty productProperty) {
		getProductProperties().remove(productProperty);
		productProperty.setProductMaster(null);

		return productProperty;
	}

}