/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jda.mobility.framework.extensions.entity.FlowPermission;

@Repository
public interface FlowPermissionRepository extends JpaRepository<FlowPermission, UUID>{
	
}
