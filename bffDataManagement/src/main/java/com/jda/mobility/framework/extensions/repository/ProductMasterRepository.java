/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.ProductMaster;

/**
 * The class ProductMasterRepository.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */
@Repository
@Transactional(readOnly = true)
public interface ProductMasterRepository extends JpaRepository<ProductMaster, UUID>{
	
	/**
	 * @param name
	 * @return ProductMaster
	 */
	ProductMaster findByName(String name);
	
	/**
	 * @param name
	 * @return ProductMaster
	 */
	int countByName(String name);	

}
