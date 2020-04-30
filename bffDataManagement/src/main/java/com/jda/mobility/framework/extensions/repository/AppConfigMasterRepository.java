/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.entity.projection.AppConfigDetailDto;


/**
 * The class AppConfigrepository.java
 * HCL Technologies Ltd.
 */
@Repository
@Transactional
public interface AppConfigMasterRepository extends JpaRepository<AppConfigMaster, UUID>{

	
	List<AppConfigMaster> findByConfigName(String configName);

	AppConfigMaster findByConfigNameAndConfigType(String configName, String configType);

	boolean existsByConfigNameAndConfigType(String configName, String configType);
	
	List<AppConfigMaster> findByConfigNameInAndConfigType(Collection<String> configName, String configType);
	
	List<AppConfigMaster> findByConfigType(String configType);
	
	List<AppConfigMaster> findByConfigTypeOrderByConfigName(String configType);
	
	@Query("SELECT a FROM AppConfigMaster a WHERE a.configType = :configType AND a.configName LIKE %:searchTerm%")
	List<AppConfigMaster> search(@Param("searchTerm") String searchTerm, @Param("configType") String configType);
	
	@Modifying
	@Query("update AppConfigMaster a set a.rawValue =:rawValue WHERE a.uid = :uid")
	int updateAppConfigRawValue(UUID uid,String rawValue);
	
	List<AppConfigMaster> findByConfigTypeIn(List<String> configType);
	
	@Query("Select b.uid from AppConfigMaster a   join AppConfigDetail b  on a.uid = b.appConfigMaster and a.configType IN ?1 and b.userId = ?2 and b.deviceName = ?3  ")
	List<UUID> fetchUserAndSpecificVariables(List<String> configType,String userId, String deviceName);
	
	@Query("Select new com.jda.mobility.framework.extensions.entity.projection.AppConfigDetailDto(a.uid, a.configName ,a.configType , a.rawValue, b.configValue,b.flowId , b.userId, b.description,b.deviceName) from AppConfigMaster a left join AppConfigDetail b  on a.uid = b.appConfigMaster and a.configType IN ?1 and (b.userId = ?2 or b.userId is null) and (b.deviceName = ?3 or b.deviceName is null)")
	List<AppConfigDetailDto> fetchAllAndUserAndDeviceSpecificVariables(List<String> types , String userId,String deviceName);

}
