package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Nationalized;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the app_config database table.
 * 
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper = false)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name = "APP_CONFIG_MASTER", indexes = {
		@Index(name = "idx_name_type", columnList= "CONFIG_NAME,CONFIG_TYPE", unique = true)})
@NamedQuery(name="AppConfigMaster.findAll", query="SELECT a FROM AppConfigMaster a")
public class AppConfigMaster extends BffAuditableData<String> implements Serializable {

	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 6727735214930802410L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID", unique=true, length=16, nullable=false)
	private UUID uid;

	@Column(name="CONFIG_NAME", nullable=false, length=25)
	@Nationalized
	private String configName;

	@Column(name="CONFIG_TYPE", nullable=false, length=45)
	@Nationalized
	private String configType;
	
	@Column(name="RAW_VALUE", length=45)
	@Nationalized
	private String rawValue;
	
	//bi-directional One-To-Many association to AppConfigDetail
	@OneToMany(mappedBy="appConfigMaster" ,  orphanRemoval = true ,cascade = CascadeType.ALL , fetch = FetchType.EAGER)
	private List<AppConfigDetail> appConfigDetails = new ArrayList<>();


}