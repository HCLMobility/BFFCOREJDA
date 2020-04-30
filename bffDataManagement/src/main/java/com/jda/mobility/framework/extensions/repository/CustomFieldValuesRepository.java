package com.jda.mobility.framework.extensions.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.CustomFieldValues;
/**
 * The CustomFieldValuesRepository interface.
 *
 */
@Repository
@Transactional
public interface CustomFieldValuesRepository extends CrudRepository<CustomFieldValues, UUID>{

}
