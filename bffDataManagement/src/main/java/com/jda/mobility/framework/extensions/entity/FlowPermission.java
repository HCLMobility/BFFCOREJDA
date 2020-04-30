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
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the flow_permission database table.
 * 
 */
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
@Table(name="FLOW_PERMISSION")
@NamedQuery(name="FlowPermission.findAll", query="SELECT f FROM FlowPermission f")
public class FlowPermission implements Serializable {
	private static final long serialVersionUID = -8702330299092360319L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="ID",unique=true, length=16, nullable=false)
	private UUID id;
	
	@Column(name="PERMISSION")
	@Nationalized
	private String permission;
	
	@ManyToOne
	@JoinColumn(name="FLOW_UUID")
	private Flow flow;
	
	/**
	 * Copy constructor for flow permission to be extended
	 * @param flowPermission
	 */
	public FlowPermission(FlowPermission flowPermission) {
		this.permission = flowPermission.getPermission();
	}	
}