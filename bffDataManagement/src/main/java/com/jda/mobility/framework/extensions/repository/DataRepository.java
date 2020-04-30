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

import com.jda.mobility.framework.extensions.entity.Data;

/**
 * The class DataRepository.java
 * @author puneet-m
 * HCL Technologies Ltd.
 */
@Repository
@Transactional
public interface DataRepository extends CrudRepository<Data, UUID>{
	

	@Modifying
	@Query("delete from Data f where f.uid=:dataId")
	void deleteByDataId(UUID dataId);

}
