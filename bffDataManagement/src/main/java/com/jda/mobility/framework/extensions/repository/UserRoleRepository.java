/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jda.mobility.framework.extensions.entity.UserRole;

/**
 * The class UserRoleRepository.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */
@Repository
public interface UserRoleRepository extends CrudRepository<UserRole, UUID>{
	
	Optional<UserRole> findByUserId(String userId);

}
