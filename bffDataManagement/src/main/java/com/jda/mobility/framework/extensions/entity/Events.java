/**
 * 
 */
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
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * The persistent class for the Events database table.
 * 
 */
@Audited
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper=false) @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@JsonIgnoreProperties(value = { "extendedFromEventId" })
@Entity
@Table(name="EVENTS")
@NamedQuery(name="Events.findAll", query="SELECT e FROM Events e")
public class Events extends BffAuditableData<String> implements Serializable{
	private static final long serialVersionUID = -7705699646081043350L;
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID", unique=true, length=16, nullable=false)
	private UUID uid;
	@Column(name="EVENT",length=45)
	@Nationalized
	private String event;
	@Nationalized
	@Lob
	@Column(name="ACTION")
	private String action;
	@EqualsAndHashCode.Exclude 
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name="FIELD_ID")
	private Field field;
	@EqualsAndHashCode.Exclude 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="FORM_ID")
	private Form form;
	@Column(name="EXTENDED_PARENT_EVENT_ID", length=16) 
	private UUID extendedFromEventId;


	/**
	 * @param event
	 * @param action
	 */
	public Events(String event, String action) {
		super();
		this.event = event;
		this.action = action;
	}

	/**
	 * Copy constructor for events to be extended.
	 * @param events
	 */
	public Events(Events events, boolean copyFlag ,boolean extendedFlag) {
		this.event = events.event;
		this.action = events.action;
		if (extendedFlag) {
			this.extendedFromEventId = events.getUid();
			
		} else {
			this.extendedFromEventId = events.getExtendedFromEventId();
		}
		if(copyFlag) {
			this.extendedFromEventId = null;
		}
	}	
}

