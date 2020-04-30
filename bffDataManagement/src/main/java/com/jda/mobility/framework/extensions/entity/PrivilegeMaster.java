package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the privilege_master database table.
 * 
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper=false) @Builder(toBuilder = true)
@Entity
@Table(name="PRIVILEGE_MASTER")
@NamedQuery(name="PrivilegeMaster.findAll", query="SELECT p FROM PrivilegeMaster p")
public class PrivilegeMaster extends BffAuditableData<String> implements Serializable {
	private static final long serialVersionUID = -5335675868624339391L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;

	@Column(name="NAME",nullable=false, length=255)
	private String name;

	//bi-directional many-to-one association to RolePrivilege
	@OneToMany(mappedBy="privilegeMaster")
	private List<RolePrivilege> rolePrivileges;

	public RolePrivilege addRolePrivilege(RolePrivilege rolePrivilege) {
		getRolePrivileges().add(rolePrivilege);
		rolePrivilege.setPrivilegeMaster(this);

		return rolePrivilege;
	}

	public RolePrivilege removeRolePrivilege(RolePrivilege rolePrivilege) {
		getRolePrivileges().remove(rolePrivilege);
		rolePrivilege.setPrivilegeMaster(null);

		return rolePrivilege;
	}
}