package com.jda.mobility.framework.extensions.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.AppConfigDetail;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;

/**
 * The class AppConfigDetailRepository.java
 * HCL Technologies Ltd.
 */
@Repository
@Transactional
public interface AppConfigDetailRepository extends JpaRepository<AppConfigDetail, UUID>{
	
	
		List<AppConfigDetail> findAll(UUID appConfigMasterId);

		List<AppConfigDetail> findByUserIdAndFlowId(String userId, UUID flowId);

		Optional<AppConfigDetail> findByAppConfigMaster(AppConfigMaster appConfigMaster);

		Optional<AppConfigDetail> findByAppConfigMasterAndUserIdAndDeviceName(AppConfigMaster appConfigMaster, String userId,String deviceName);

		Optional<AppConfigDetail> findByAppConfigMasterAndUserIdAndFlowIdAndDeviceName(AppConfigMaster appConfigMaster, String userId ,UUID flowid,String deviceName);
		
		@Modifying
		@Query("delete from AppConfigDetail where uid in ?1" )
		void deleteByIdInList(List<UUID> appConfigId);
}
