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
 * The persistent class for the role_privilege database table.
 * 
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name="ROLE_PRIVILEGE")
@NamedQuery(name="RolePrivilege.findAll", query="SELECT r FROM RolePrivilege r")
public class RolePrivilege implements Serializable {
	private static final long serialVersionUID = -6767688382247033985L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;

	//bi-directional many-to-one association to PrivilegeMaster
	@ManyToOne
	@JoinColumn(name="PRIVILEGE_ID", nullable=false)
	private PrivilegeMaster privilegeMaster;

	//bi-directional many-to-one association to RoleMaster
	@ManyToOne
	@JoinColumn(name="ROLE_ID", nullable=false)
	private RoleMaster roleMaster;
}