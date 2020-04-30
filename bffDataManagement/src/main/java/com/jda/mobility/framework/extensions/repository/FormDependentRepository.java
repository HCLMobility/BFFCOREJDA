package com.jda.mobility.framework.extensions.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.jda.mobility.framework.extensions.entity.FormDependent;

/**
 * The class FormIndepRepository.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */
public interface FormDependentRepository extends CrudRepository<FormDependent, UUID>{

}
