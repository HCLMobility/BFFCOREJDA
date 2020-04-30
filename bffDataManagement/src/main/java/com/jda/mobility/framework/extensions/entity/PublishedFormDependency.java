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
import org.hibernate.envers.Audited;

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
 * The persistent class for the form_outdep database table.
 * 
 */
@Audited
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name="PUBLISHED_FORM_DEPENDENCY")
@NamedQuery(name="PublishedFormDependency.findAll", query="SELECT f FROM PublishedFormDependency f")
public class PublishedFormDependency implements Serializable {
	private static final long serialVersionUID = -5101135955447634556L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;

	@ManyToOne
	@JoinColumn(name="INBOUND_FLOW_ID", nullable=false)
	private Flow inboundFlow;
	
	@Column(name = "INBOUND_FORM_ID",  length=16, nullable=false)
	private UUID inboundFormId;
	
	@Column(name = "OUTBOUND_FLOW_ID", length = 16)
	private UUID outboundFlowId;
	
	@Column(name = "OUTBOUND_FORM_ID", length = 16)
	private UUID outboundFormId;
	
}