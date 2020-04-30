/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.ProductMaster;
import com.jda.mobility.framework.extensions.entity.ProductProperty;

/**
 * The class ProductPropertyRepository.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */
@Repository
@Transactional(readOnly = true)
public interface ProductPropertyRepository extends CrudRepository<ProductProperty, UUID> {

	/**
	 * @param name
	 * @param propValue
	 * @param productMaster
	 * @return
	 */
	
	public List<ProductProperty> findByNameAndPropValueAndProductMasterAndIsSecondaryRefTrue(String name, String propValue,
			ProductMaster productMaster);
	
	public List<ProductProperty> findByNameAndPropValueAndIsSecondaryRefTrue(String name, String propValue);

}
