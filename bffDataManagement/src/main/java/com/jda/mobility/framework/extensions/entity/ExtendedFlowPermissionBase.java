/**
 * 
 */
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
import org.hibernate.annotations.Nationalized;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the extended_flow_permission database table.
 * 
 * @author HCL Technologies Ltd.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode @Builder(toBuilder = true)
@Entity
@Table(name = "EXTENDED_FLOW_PERMISSION")
@NamedQuery(name = "ExtendedFlowPermissionBase.findAll", query = "SELECT m FROM ExtendedFlowPermissionBase m")
public class ExtendedFlowPermissionBase implements Serializable {
	
	private static final long serialVersionUID = -8702330299092360319L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="ID",unique=true, length=16, nullable=false)
	private UUID id;

	@Column(name = "PERMISSION", length=45)
	@Nationalized
	private String permission;

	@ManyToOne
	@JoinColumn(name = "EXTENDED_FLOW_ID")
	private ExtendedFlowBase extendedFlow;

	public ExtendedFlowPermissionBase(FlowPermission flowPermission) {
		this.permission = flowPermission.getPermission();
	}
}