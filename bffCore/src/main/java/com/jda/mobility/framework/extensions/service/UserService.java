/**
 * 
 */
package com.jda.mobility.framework.extensions.service;

import java.util.UUID;

import org.springframework.retry.annotation.Retryable;

import com.jda.mobility.framework.extensions.entity.UserRole;
import com.jda.mobility.framework.extensions.model.AccessRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
/**
 *  Get Roles and privileges for Mobile from tables
 *  Get Layers and privileges for AdminUI from product(Ex: WMS)
 * 
 * @author HCL Technologies Ltd.
 */
@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface UserService {	
	/**
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse getRoles();

	/**
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse getRolesForUser(String userId);
	
	/**
	 * @param accessRequest The AccessRequest object to map a user to role
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse mapUserRole(AccessRequest accessRequest);
	/**
	 * @param accessRequest The AccessRequest object to modify user role
	 * @param existingUserRole The existing user role object to be modified
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse modifyUserRole(AccessRequest accessRequest, UserRole existingUserRole);
	/**
	 * @param name The role name to be created
	 * @return String The success/error return message
	 */
	String createRoleMaster(String name);	
	/**
	 * @param name The privilege name to be created
	 * @return String The success/error return message
	 */
	String createPrivilegeMaster(String name);	
	/**
	 * @param roleId The ID of the role to be mapped to a privilege
	 * @param privilegeId The ID of the privilege to be mapped
	 * @return String The success/error return message
	 */
	String mapRolePrivilege(UUID roleId, UUID privilegeId);
}