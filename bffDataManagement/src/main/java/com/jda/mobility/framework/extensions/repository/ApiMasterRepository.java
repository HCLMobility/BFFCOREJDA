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

import com.jda.mobility.framework.extensions.entity.ApiMaster;
import com.jda.mobility.framework.extensions.entity.ApiRegistry;

/**
 * The class ApiMaster.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */
@Repository
public interface ApiMasterRepository extends JpaRepository<ApiMaster, UUID>{

	/**
	 * @return int
	 */
	@Query("select count(1) from ApiMaster")
	int countAllApis();
	
	Optional<ApiMaster> findByApiRegistryAndOrchestrationName(ApiRegistry apiRegistry, String orchestrationName);

	List<ApiMaster> findByName(String name);
	
	Optional<ApiMaster> findByApiRegistryAndName(ApiRegistry registry, String name);
	
	Optional<ApiMaster> findByNameAndRequestEndpointAndRequestMethod(String apiName, String requestEndpoint,
			String requestMethod);
	
	Optional<ApiMaster> findByRequestMethodAndRequestEndpointAndApiRegistry_uid(String requestMethod, String requestEndpoint, UUID uuid);
	

	Optional<ApiMaster> findByNameAndRequestEndpointAndRequestMethodAndApiRegistry_uid(String apiName, String requestEndpoint,
			String requestMethod, UUID uuid);

	Optional<ApiMaster> findByRequestEndpointAndRequestMethodAndApiRegistryNameAndApiRegistryApiType(String endpoint, String method, String name, String type);
}
