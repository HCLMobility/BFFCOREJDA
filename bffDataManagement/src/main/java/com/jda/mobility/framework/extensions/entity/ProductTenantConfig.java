package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
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

@Getter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name="PRODUCT_TENANT_CONFIG", indexes = {
		@Index(name = "idx_tenant_config", columnList= "TENANT, CONFIG_NAME", unique = true)})
@NamedQuery(name="ProductTenantConfig.findAll", query="SELECT p FROM ProductTenantConfig p")
public class ProductTenantConfig implements Serializable {

	private static final long serialVersionUID = 166592234875913246L;
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable = false)
	private UUID uid;
	@Column(name = "TENANT", nullable = false, length = 150)
	@Nationalized
	private String tenant;
	@Column(name = "CONFIG_NAME", nullable = false, length = 255)
	@Nationalized
	private String configName;
	@Column(name = "CONFIG_VALUE", nullable = false, length = 255)
	@Nationalized
	private String configValue;
	

}
