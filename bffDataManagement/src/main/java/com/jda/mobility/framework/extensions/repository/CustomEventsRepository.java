package com.jda.mobility.framework.extensions.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.CustomEvents;
/**
 * The CustomEventsRepository interface.
 *
 */
@Repository
@Transactional
public interface CustomEventsRepository extends CrudRepository<CustomEvents, UUID>{

}
