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
@Audited
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper = false) @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name="KEY_CODE_MASTER")
@NamedQuery(name="HotKeyCode.findAll", query="SELECT h FROM HotKeyCode h")
public class HotKeyCode extends BffAuditableData<String> implements Serializable {

	private static final long serialVersionUID = 818772058195375211L;
    
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique = true, length = 16, nullable = false)
	private UUID uid;
	

	@Column(name="KEY_NAME",nullable=false, length=255 , unique= true)
	@Nationalized
	private String keyName;
	
	
	@Column(name="KEY_DISPLAY_NAME",nullable=false, length=255)
	@Nationalized
	private String keyDisplayName;
	
	
	@Column(name="KEY_DESCRIPTION",nullable=false, length=255)
	@Nationalized
	private String keyDescription;
	
	@Column(name="CODE",nullable=false, length=45)
	@Nationalized
	private String code;
	
	@Column(name = "IS_CTRL", nullable = false)
	private boolean ctrl;
	
	
	@Column(name = "IS_SHIFT", nullable = false)
	private boolean shift;
	
	@Column(name = "IS_ALT", nullable = false)
	private boolean alt;
	
	@Column(name = "IS_METAKEY", nullable = false)
	private boolean metaKey;
	
	@Column(name="TYPE",nullable=false, length=45)
	private String type;
	
	
	@Column(name="SEQUENCE",nullable=false, length=45)
	private int sequence;
	
	
	

}
	