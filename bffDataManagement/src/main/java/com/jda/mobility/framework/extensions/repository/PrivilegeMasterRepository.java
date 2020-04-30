/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jda.mobility.framework.extensions.entity.PrivilegeMaster;

/**
 * The class PrivilegeMasterRepository.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */
@Repository
public interface PrivilegeMasterRepository extends CrudRepository<PrivilegeMaster, UUID>{

}
