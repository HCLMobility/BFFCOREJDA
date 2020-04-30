package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
 * The persistent class for the entity_tag database table.
 * 
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode @Builder(toBuilder = true)
@Entity
@Table(name="ENTITY_TAG")
@NamedQuery(name="EntityTag.findAll", query="SELECT e FROM EntityTag e")
public class EntityTag implements Serializable {

	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 360458755057736237L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name = "ID", unique=true, length=16, nullable=false)
	private UUID id;

	@Column(name="ENTITY_ID", nullable=false)
	private int entityId;

	@Column(nullable=false, length=45)
	@Nationalized
	private String tag;
}