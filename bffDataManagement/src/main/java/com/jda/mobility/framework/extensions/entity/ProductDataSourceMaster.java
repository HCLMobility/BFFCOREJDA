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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the product_data_source_master database table.
 * 
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode @Builder(toBuilder = true)
@Entity
@Table(name="PRODUCT_DATA_SOURCE_MASTER")
@NamedQuery(name="ProductDataSourceMaster.findAll", query="SELECT p FROM ProductDataSourceMaster p")
public class ProductDataSourceMaster implements Serializable {
	private static final long serialVersionUID = 5928509991638760469L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;
	@Nationalized
	@Lob
	@Column(name="BASE_PATH", nullable=false)
	private String basePath;
	@Nationalized
	@Lob
	@Column(name="CONTEXT_PATH", nullable=false)
	private String contextPath;
	@Nationalized
	@Column(name="NAME",nullable=false, length=255)
	private String name;

	@Column(name="PORT",nullable=false)
	private int port;
	
	//bi-directional many-to-one association to ProductMaster
	@ManyToOne
	@JoinColumn(name="PRODUCT_ID", nullable=false)
	private ProductMaster productMaster;
}