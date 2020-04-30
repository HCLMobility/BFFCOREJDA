/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jda.mobility.framework.extensions.entity.VersionMapping;

/**
 * The class VersionMappingRepository.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */
@Repository
public interface VersionMappingRepository extends CrudRepository<VersionMapping, UUID>{

}
