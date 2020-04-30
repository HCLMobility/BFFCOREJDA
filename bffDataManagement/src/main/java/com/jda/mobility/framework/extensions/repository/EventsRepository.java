/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.Events;

/**
 * The class EventsRepository.java
 * @author puneet-m
 * HCL Technologies Ltd.
 */
@Repository
@Transactional
public interface EventsRepository extends CrudRepository<Events, UUID>{

}
