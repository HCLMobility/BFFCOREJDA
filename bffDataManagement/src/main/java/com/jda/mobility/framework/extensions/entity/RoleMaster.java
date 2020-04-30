package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Nationalized;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the role_master database table.
 * 
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper = false)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@JsonIgnoreProperties(value = { "productConfigs", "productRoleMappings", "rolePrivileges", "userRoles", "apiRegistries" })
@Entity
@Table(name="ROLE_MASTER")
@NamedQuery(name="RoleMaster.findAll", query="SELECT r FROM RoleMaster r")
public class RoleMaster extends BffAuditableData<String> implements Serializable {
	private static final long serialVersionUID = 2671724301278733988L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;
	@Nationalized
	@Column(name="NAME",nullable=false, length=255)
	private String name;
	
	@Column(name="LEVEL",nullable=false)
	@ColumnDefault("0")
	private int level;	

	//bi-directional many-to-one association to ProductConfig
	@OneToMany(mappedBy="roleMaster")
	private List<ProductConfig> productConfigs;

	//bi-directional many-to-one association to ProductRoleMapping
	@OneToMany(mappedBy="roleMaster")
	private List<ProductRoleMapping> productRoleMappings;

	//bi-directional many-to-one association to RolePrivilege
	@OneToMany(mappedBy="roleMaster")
	private List<RolePrivilege> rolePrivileges;

	//bi-directional many-to-one association to UserRole
	@OneToMany(mappedBy="roleMaster")
	private List<UserRole> userRoles;

	@OneToMany(mappedBy="roleMaster", cascade = CascadeType.ALL)
	private List<ApiRegistry> apiRegistries = new ArrayList<>();
	
	public ProductConfig addProductConfig(ProductConfig productConfig) {
		getProductConfigs().add(productConfig);
		productConfig.setRoleMaster(this);

		return productConfig;
	}

	public ProductConfig removeProductConfig(ProductConfig productConfig) {
		getProductConfigs().remove(productConfig);
		productConfig.setRoleMaster(null);

		return productConfig;
	}

	public ProductRoleMapping addProductRoleMapping(ProductRoleMapping productRoleMapping) {
		getProductRoleMappings().add(productRoleMapping);
		productRoleMapping.setRoleMaster(this);

		return productRoleMapping;
	}

	public ProductRoleMapping removeProductRoleMapping(ProductRoleMapping productRoleMapping) {
		getProductRoleMappings().remove(productRoleMapping);
		productRoleMapping.setRoleMaster(null);

		return productRoleMapping;
	}

	public RolePrivilege addRolePrivilege(RolePrivilege rolePrivilege) {
		getRolePrivileges().add(rolePrivilege);
		rolePrivilege.setRoleMaster(this);

		return rolePrivilege;
	}

	public RolePrivilege removeRolePrivilege(RolePrivilege rolePrivilege) {
		getRolePrivileges().remove(rolePrivilege);
		rolePrivilege.setRoleMaster(null);

		return rolePrivilege;
	}

	public UserRole addUserRole(UserRole userRole) {
		getUserRoles().add(userRole);
		userRole.setRoleMaster(this);

		return userRole;
	}

	public UserRole removeUserRole(UserRole userRole) {
		getUserRoles().remove(userRole);
		userRole.setRoleMaster(null);

		return userRole;
	}
}