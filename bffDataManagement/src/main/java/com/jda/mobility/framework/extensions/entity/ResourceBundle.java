package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
 * The persistent class for the resource_bundle database table.
 * 
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper = false) @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name = "RESOURCE_BUNDLE", indexes = {
		@Index(name = "idx_key", columnList = "RESOURCE_KEY, locale", unique = true) }, 
		uniqueConstraints = @UniqueConstraint(columnNames = {"RESOURCE_KEY", "locale" }))
@NamedQuery(name = "ResourceBundle.findAll", query = "SELECT r FROM ResourceBundle r")
public class ResourceBundle extends BffAuditableData<String> implements Serializable {
	private static final long serialVersionUID = 8660766248416805627L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;
	
	@Nationalized
	@Column(name="LOCALE",nullable = false, length=10)
	private String locale;
	@Nationalized
	@Column(name = "RESOURCE_KEY", nullable = false, length=50)
	private String rbkey;
	@Nationalized
	@Lob
	@Column(name = "RESOURCE_VALUE")
	private String rbvalue;
	@Nationalized
	@Column(name = "TYPE",length=50)
	private String type;

}