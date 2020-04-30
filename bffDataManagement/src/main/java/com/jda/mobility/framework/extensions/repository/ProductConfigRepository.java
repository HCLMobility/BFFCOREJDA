/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.List;
import java.util.UUID;

import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * The class ProductConfigRepository.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */
@Repository
@Transactional(readOnly = true)
public interface ProductConfigRepository extends JpaRepository<ProductConfig, UUID>{

	/**
	 * @param secRefId
	 * @return List&lt;ProductConfig&gt;
	 */
	List<ProductConfig> findBySecondaryRefId(UUID secRefId);
	
	/**
	 * @param secRefId
	 * @param roleMaster
	 * @return ProductConfig
	 */
	ProductConfig findBySecondaryRefIdAndRoleMaster(UUID secRefId, RoleMaster roleMaster);

	List<ProductConfig> findByRoleMasterUid(UUID uuid);
}
