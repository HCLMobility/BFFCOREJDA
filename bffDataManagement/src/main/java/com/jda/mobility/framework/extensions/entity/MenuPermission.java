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
import javax.persistence.Transient;

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
 * The persistent class for the menu_permission database table.
 * 
 */
@Getter @Setter @NoArgsConstructor  @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name="MENU_PERMISSION")
@NamedQuery(name="MenuPermission.findAll", query="SELECT m FROM MenuPermission m")
public class MenuPermission implements Serializable{
	private static final long serialVersionUID = -8702330299092360319L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;
	@Nationalized
	@Column(name="PERMISSION",length=45)
	private String permission;
	
	@ManyToOne
	@JoinColumn(name="MENU_UUID")
	private MenuMaster menu;
	
	@Transient
	private boolean isExists;

	
	
	public MenuPermission(UUID uid, String permission) {
		super();
		this.uid = uid;
		this.permission = permission;
	}
}