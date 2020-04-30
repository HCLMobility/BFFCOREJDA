/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jda.mobility.framework.extensions.entity.ProductDataSourceMaster;

/**
 * The class ProductDataSourceMasterRepository.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */
@Repository
public interface ProductDataSourceMasterRepository extends CrudRepository<ProductDataSourceMaster, UUID>{

}
