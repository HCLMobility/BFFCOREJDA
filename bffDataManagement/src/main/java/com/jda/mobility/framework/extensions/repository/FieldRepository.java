/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.Field;

/**
 * The class FieldRepository.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */
@Repository
@Transactional
public interface FieldRepository extends CrudRepository<Field, UUID> {
	@Modifying
	@Query("delete from Field f where f.linkedComponentId=:linkedComponentId")
	void deleteByLinkedComponentId(UUID linkedComponentId);
}
