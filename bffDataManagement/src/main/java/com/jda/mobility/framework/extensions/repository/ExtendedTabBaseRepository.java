/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.ExtendedTabsBase;

/**
 * The class ExtendedTabBaseRepository.java
 * 
 * @author HCL Technologies Ltd.
 */
@Repository
@Transactional
public interface ExtendedTabBaseRepository extends JpaRepository<ExtendedTabsBase, UUID> {

}
