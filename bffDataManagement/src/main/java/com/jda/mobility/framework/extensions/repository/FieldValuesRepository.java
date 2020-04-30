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

import com.jda.mobility.framework.extensions.entity.FieldValues;

/**
 * The class ValuesRepository.java
 * @author puneet-m
 * HCL Technologies Ltd.
 */
@Repository
@Transactional
public interface FieldValuesRepository extends CrudRepository<FieldValues, UUID> {
	
	@Modifying
	@Query("delete from FieldValues f where f.uid=:fieldValuesId")
	void deleteByValueId(UUID fieldValuesId);

}
