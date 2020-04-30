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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the version_mapping database table.
 * 
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper = false) @Builder(toBuilder = true)
@Entity
@Table(name="VERSION_MAPPING")
@NamedQuery(name="VersionMapping.findAll", query="SELECT v FROM VersionMapping v")
public class VersionMapping extends BffAuditableData<String> implements Serializable {
	private static final long serialVersionUID = 7980857078939347827L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;

	@ManyToOne
	@JoinColumn(name="BFFCORE_VERSION_ID", nullable=false)
	private VersionMaster bffCoreVersionMaster;
	
	@ManyToOne
	@JoinColumn(name="MAPPED_APP_ID", nullable=false)
	private VersionMaster mappedAppVersionMaster;

}