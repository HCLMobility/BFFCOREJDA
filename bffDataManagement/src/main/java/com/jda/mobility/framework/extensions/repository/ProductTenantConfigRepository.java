/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jda.mobility.framework.extensions.entity.ProductTenantConfig;

/**
 * @author HCL Technologies Ltd.
 *
 */
@Repository
public interface ProductTenantConfigRepository extends JpaRepository<ProductTenantConfig, UUID>{
	
	ProductTenantConfig findByTenantAndConfigName(String tenant, String configName);
	@Query("SELECT DISTINCT p.tenant FROM ProductTenantConfig p")
	List<String> findDistinctByTenant();
	
	ProductTenantConfig findByConfigNameAndTenant(String configName,String tenant);

}
