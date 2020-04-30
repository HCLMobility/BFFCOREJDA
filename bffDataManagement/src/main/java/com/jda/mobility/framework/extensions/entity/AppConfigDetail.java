package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
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

@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper = false) @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name="APP_CONFIG_DETAIL", indexes = {
		@Index(name = "idx_config_master_id", columnList= "APP_CONFIG_MASTER_UID")})
@NamedQuery(name="AppConfigDetail.findAll", query="SELECT a FROM AppConfigDetail a")
public class AppConfigDetail extends BffAuditableData<String> implements Serializable{
	

	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 2040166597747117058L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID", unique=true, length=16, nullable=false)
	private UUID uid;
	
	@Column(name="CONFIG_VALUE", nullable=true,length=45)
	@Nationalized
	private String configValue;
	
	@Column(name="FLOW_ID", length=16, nullable=true)
	private UUID flowId;
	
	@Column(name="USER_ID", nullable=true,length=45)
	@Nationalized
	private String userId;
	
	@Lob
	@Column(name="DESCRIPTION")
	@Nationalized
	private String description;
	
	//bi-directional many-to-one association to appConfigMaster
	@ManyToOne
	@JoinColumn(name="APP_CONFIG_MASTER_UID")
	private AppConfigMaster appConfigMaster;
	
	
	@Column(name="DEVICE_NAME", nullable=true,length=255)
	@Nationalized
	private String deviceName;

}
