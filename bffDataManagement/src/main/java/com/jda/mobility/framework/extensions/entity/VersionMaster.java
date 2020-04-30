package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
 * The persistent class for the version_master database table.
 * 
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper = false) @Builder(toBuilder = true)
@Entity
@Table(name="VERSION_MASTER")
@NamedQuery(name="VersionMaster.findAll", query="SELECT v FROM VersionMaster v")
public class VersionMaster extends BffAuditableData<String> implements Serializable {
	private static final long serialVersionUID = -3791079223030254773L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;
	
	@Nationalized
	@Column(name="VERSION", nullable=false, length=20)
	private String version;
	@Nationalized
	@Column(name="CHANNEL", nullable=false, length=20)
	private String channel;
	
	@Column(name = "ACTIVE")
	private boolean active;

	@OneToMany(mappedBy="bffCoreVersionMaster", fetch = FetchType.EAGER)
	private List<VersionMapping> bffCoreVersionMappingList;
	
	@OneToMany(mappedBy="mappedAppVersionMaster", fetch = FetchType.EAGER)
	private List<VersionMapping> mappedVersionMappingList;

}