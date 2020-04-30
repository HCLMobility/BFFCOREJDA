/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.jda.mobility.framework.extensions.entity.ProductRoleMapping;

/**
 * The class ProductRoleMappingRepository.java
 * @author puneet-m
 * HCL Technologies Ltd.
 */
public interface ProductRoleMappingRepository extends CrudRepository<ProductRoleMapping, UUID>{

}
