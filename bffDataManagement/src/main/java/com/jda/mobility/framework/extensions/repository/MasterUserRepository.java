/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jda.mobility.framework.extensions.entity.MasterUser;

/**
 * The class SuperUserRepository.java
 * 
 * @author HCL Technologies
 */
@Repository
public interface MasterUserRepository extends CrudRepository<MasterUser, UUID> {
	
	/*
	 * Count by UserId
	 * @param userId
	 * @return count int
	 */
	public int countByUserId(String userId);
	
	/*
	 * Find by UserId
	 * @param userId
	 * @return Optional&lt;MasterUser&gt;
	 */
	public Optional<MasterUser> findByUserId(String userId);	
	

}
