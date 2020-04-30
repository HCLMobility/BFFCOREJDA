/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.ExtendedFieldValuesBase;

/**
 * The class ExtendedFieldValuesBaseRepository.java
 * 
 * @author HCL Technologies Ltd.
 */
@Repository
@Transactional
public interface ExtendedFieldValuesBaseRepository extends JpaRepository<ExtendedFieldValuesBase, UUID> {

}
