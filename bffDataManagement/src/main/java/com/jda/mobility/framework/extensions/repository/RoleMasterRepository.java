/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.RoleMaster;

/**
 * The class RoleMasterRepository.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */
@Repository
@Transactional
public interface RoleMasterRepository extends JpaRepository<RoleMaster, UUID>{
	/**
	 * @param name
	 * @return RoleMaster
	 */
	RoleMaster findByName(String name);
	
	/**
	 * @return List&lt;RoleMaster&gt;
	 */
	List<RoleMaster> findAllByOrderByLevelAsc();	
}
