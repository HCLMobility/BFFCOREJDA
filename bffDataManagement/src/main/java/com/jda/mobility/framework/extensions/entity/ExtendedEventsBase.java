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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the extended_events database table.
 * 
 * @author HCL Technologies Ltd.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode @Builder(toBuilder = true)
@Entity
@Table(name = "EXTENDED_EVENTS")
@NamedQuery(name = "ExtendedEventsBase.findAll", query = "SELECT e FROM ExtendedEventsBase e")
public class ExtendedEventsBase implements Serializable {
	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = -7705699646081043350L;
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID", unique=true, length=16, nullable=false)
	private UUID uid;
	@Column(name = "EVENT",length=45)
	@Nationalized
	private String event;
	@Lob
	@Column(name = "ACTION")
	@Nationalized
	private String action;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "EXTENDED_FIELD_ID")
	private ExtendedFieldBase extendedField;

	@ManyToOne
	@JoinColumn(name = "EXTENDED_FORM_ID")
	private ExtendedFormBase extendedForm;

	/**
	 * @param event
	 * @param action
	 */
	public ExtendedEventsBase(UUID uid, String event, String action) {
		super();
		this.uid = uid;
		this.event = event;
		this.action = action;
	}
}