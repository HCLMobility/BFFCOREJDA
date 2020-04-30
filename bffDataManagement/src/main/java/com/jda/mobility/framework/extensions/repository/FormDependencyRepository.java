package com.jda.mobility.framework.extensions.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jda.mobility.framework.extensions.entity.FormDependency;

/**
 * The class FormOutdepRepository.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */
public interface FormDependencyRepository extends CrudRepository<FormDependency, UUID>{

	List<FormDependency> findByInboundFormId(UUID formId);

	List<FormDependency> findByInboundFormIdOrOutboundFormId(UUID inboundFormId, UUID outboundFormId);
	
	List<FormDependency> findByInboundFormIdAndOutboundFlowIdNotNull(UUID uid);

	List<FormDependency> findByOutboundFlowId(UUID uid);
	
	@Query("select DISTINCT pfd.inboundFormId from FormDependency pfd where pfd.outboundFormId = ?1")
	Set<UUID> findByOutboundFormId(UUID uid);

	List<FormDependency> findByInboundFormIdAndOutboundFormId(UUID uid, UUID linkedFormId);
}
