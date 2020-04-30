/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jda.mobility.framework.extensions.entity.ApiRegistry;
import com.jda.mobility.framework.extensions.entity.RoleMaster;

/**
 * The class ApiRegistry.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */
@Repository
public interface ApiRegistryRepository extends JpaRepository<ApiRegistry, UUID> {

	/**
	 * @param name
	 * @return List&lt;ApiRegistry&gt;
	 */
	List<ApiRegistry> findByName(String name);
	
	
	/**
	 * @param apiType
	 * @return List&lt;ApiRegistry&gt;
	 */
	Optional<List<ApiRegistry>> findByApiTypeInOrderByName(List<String> apiType);	
	
	/**
	 * @param name
	 * @return int
	 */
	int countByNameAndApiTypeAndRoleMaster(String name, String type, RoleMaster roleMaster);	
	
	
	@Query("select count(1) from ApiRegistry")
	int countAllRegistry();
	
	/**
	 * @return List&lt;ApiRegistry&gt;
	 */
	List<ApiRegistry> findAllByOrderByName();
	
	/**
	 * @param apiType
	 * @param regName
	 * @param layer
	 * @return ApiRegistry
	 */	
	Optional<ApiRegistry> findByApiTypeAndNameAndRoleMaster_level(String apiType, String regName, int layer);	
	
	/**
	 * @param apiType
	 * @param regName
	 * @param layerName
	 * @return ApiRegistry
	 */	
	Optional<ApiRegistry> findByApiTypeAndNameAndRoleMaster_name(String apiType, String regName, String layerName);

	List<ApiRegistry> findByRoleMasterUidOrderByName(UUID uuid);
}
