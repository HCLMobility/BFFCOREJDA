package com.jda.mobility.framework.extensions.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jda.mobility.framework.extensions.entity.PublishedFormDependency;

/**
 * The class FormOutdepRepository.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */
public interface PublishedFormDependencyRepository extends CrudRepository<PublishedFormDependency, UUID>{

	List<PublishedFormDependency> findByInboundFormIdAndOutboundFlowIdNotNull(UUID uid);

	List<PublishedFormDependency> findByOutboundFlowId(UUID uid);
	
	@Query("select DISTINCT pfd.inboundFormId from PublishedFormDependency pfd where pfd.outboundFlowId in ?1")
	Set<UUID> findOutboundFormIdByInboundFormId(UUID uid);

	@Query("select DISTINCT pfd.inboundFormId from PublishedFormDependency pfd where pfd.outboundFormId = ?1")
	Set<UUID> findByOutboundFormId(UUID uid);

	List<PublishedFormDependency> findByInboundFormId(UUID uid);

}
