/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.ProductConfig;
import com.jda.mobility.framework.extensions.entity.projection.FlowLiteDto;

/**
 * The class FlowRepository.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */
@Repository
@Transactional
public interface FlowRepository extends JpaRepository<Flow, UUID>{

	/**
	 * @param productConfigId
	 * @return List&lt;Flow&gt;
	 */
	List<Flow> findByIsPublishedTrueAndProductConfigInOrderByLastModifiedDateDesc(List<ProductConfig> productConfigId);
	
	/**
	 * @param productConfigId
	 * @return List&lt;Flow&gt;
	 */
	List<Flow> findByIsPublishedFalseAndProductConfigInOrderByLastModifiedDateDesc(List<ProductConfig> productConfigId);
	
	/**
	 * @param productConfigId
	 * @return List&lt;Flow&gt;
	 */
	List<Flow> findByProductConfigInOrderByLastModifiedDateDesc(List<ProductConfig> productConfigId);
	
	/**
	 * @param defaultFormId
	 * @return List&lt;Flow&gt;
	 */
	Flow findByDefaultFormId(UUID defaultFormId);	
	
	/**
	 * @param name
	 * @param version
	 * @return List&lt;Flow&gt;
	 */
	List<Flow> findByNameAndVersion(String name,long version);

	/**
	 * @param productConfigList
	 * @return int
	 */
	int countByIsPublishedFalseAndProductConfigIn(List<ProductConfig> productConfigList);
	
	/**
	 * @param flowId
	 * @return flow name of type String
	 */
	@Query("select f.name from Flow f where f.uid =?1")
	String getFlowName(UUID flowId);

	/**
	 * @param flowName
	 * @return version of type long
	 */	
	@Query("select max(f.version) from Flow f where f.name =?1")
	long getFlowLatestVersion(String flowName);
	
	/**
	 * @param prodConfigList
	 * @return List&lt;FlowLiteDto&gt;
	 */		
	@Query("select new com.jda.mobility.framework.extensions.entity.projection.FlowLiteDto(f.uid, f.name, f.version, f.defaultFormTabbed, f.defaultModalForm, f.publishedDefaultFormId) from Flow f where f.productConfig IN (?1) order by f.lastModifiedDate desc")
    List<FlowLiteDto> getFlowBasicList(List<ProductConfig> prodConfigList);
	
	/**
	 * @param prodConfigList
	 * @param pageable
	 * @return List&lt;FlowLiteDto&gt;
	 */			
	@Query("select new com.jda.mobility.framework.extensions.entity.projection.FlowLiteDto(f.uid, f.name, f.version, f.defaultFormTabbed, f.defaultModalForm, f.publishedDefaultFormId) from Flow f where f.productConfig IN (?1)")
    List<FlowLiteDto> getFlowBasicListByPage(List<ProductConfig> prodConfigList, Pageable pageable);
	
	/**
	 * @param linkedFlowIds
	 * @return Set&lt;UUID&gt;
	 */		
	@Query("select flow.uid from Flow flow where flow.uid in ?1")
	Set<UUID> findExistingFormIds(Set<UUID> linkedFlowIds);

	/**
	 * @param keySet
	 * @return Set&lt;Flow&gt;
	 */		
	Set<Flow> findByUidIn(Set<UUID> keySet);
}
