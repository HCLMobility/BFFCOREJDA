/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.CustomComponentMaster;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.FormCustomComponent;

/**
 * The class FormCustomComponentRepository.java
 * @author puneet-m
 * HCL Technologies Ltd.
 */
@Repository
@Transactional(readOnly = true)
public interface FormCustomComponentRepository extends JpaRepository<FormCustomComponent, UUID> {
	/**
	 * @param form
	 * @param customComponentMaster
	 * @return FormCustomComponent
	 */
	FormCustomComponent findByFormAndCustomComponentMaster(Form form, CustomComponentMaster customComponentMaster);
}
