package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

/**
 * The persistent class for the custom_events database table.
 *
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(exclude = "field") @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name="CUSTOM_EVENTS")
@NamedQuery(name="CustomEvents.findAll", query="SELECT e FROM CustomEvents e")
public class CustomEvents implements Serializable {

	private static final long serialVersionUID = -8973066348631164381L;
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID", unique=true, length=16, nullable=false)
	private UUID uid;
	@Column(name="EVENT",length=45)
	@Nationalized
	private String event;
	@Lob
	@Nationalized
	@Column(name="ACTION")
	private String action;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name="FIELD_ID")
	private CustomField field;

	public CustomEvents(String event, String action) {
		this.event = event;
		this.action = action;
	}
}