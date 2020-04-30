/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jda.mobility.framework.extensions.entity.PrivilegeMaster;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.entity.RolePrivilege;

/**
 * The class RolePrivilegeRepository.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */
@Repository
public interface RolePrivilegeRepository extends CrudRepository<RolePrivilege, UUID>{

	RolePrivilege findByPrivilegeMasterAndRoleMaster(PrivilegeMaster privilegeMaster, RoleMaster roleMaster);	
}
