/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jda.mobility.framework.extensions.entity.Flow;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.projection.FormLiteDto;

/**
 * The class Form.java
 * 
 * @author ChittipalliN HCL Technologies Ltd.
 */
@Repository
@Transactional
public interface FormRepository extends JpaRepository<Form, UUID> {

	/**
	 * @param flow
	 * @return List&lt;Form&gt;
	 */
	List<Form> findByFlowOrderByLastModifiedDateDesc(Flow flow);

	/**
	 * @param productConfigIds
	 * @return List&lt;Form&gt;
	 */
	List<Form> findByIsPublishedFalseAndProductConfigIdInOrderByLastModifiedDateDesc(List<UUID> productConfigIds);

	/**
	 * @param productConfigIds
	 * @return List&lt;Form&gt;
	 */
	List<Form> findByIsOrphanTrueAndProductConfigIdInOrderByLastModifiedDateDesc(List<UUID> productConfigIds);

	/**
	 * @param productConfigIds
	 * @return List&lt;Form&gt;
	 */
	List<Form> findByProductConfigIdInOrderByLastModifiedDateDesc(List<UUID> productConfigIds);

	/**
	 * @param productConfigList
	 * @return int
	 */
	int countByIsPublishedFalseAndProductConfigIdIn(List<UUID> productConfigList);

	/**
	 * @param productConfigList
	 * @return int
	 */
	int countByIsOrphanTrueAndProductConfigIdIn(List<UUID> productConfigList);

	/**
	 * @param extendedFromFormId
	 * @return List&lt;Form&gt;
	 */
	List<Form> findByExtendedFromFormId(UUID extendedFromFormId);

	/**
	 * @param formName
	 * @param flow
	 * @return List&lt;Form&gt;
	 */
	List<Form> findByNameAndFlow(String formName, Flow flow);
	
	/**
	 * @param flow
	 * @return List&lt;FormLiteDto&gt;
	 */
	@Query("select new com.jda.mobility.framework.extensions.entity.projection.FormLiteDto(f.uid, f.name, f.isTabbedForm, f.modalForm) from Form f where f.flow=?1 order by  f.lastModifiedDate desc")
	List<FormLiteDto> getFormBasicList(Flow flow);

	/**
	 * @param flow
	 * @param pageable
	 * @return List&lt;FormLiteDto&gt;
	 */
	@Query("select new com.jda.mobility.framework.extensions.entity.projection.FormLiteDto(f.uid, f.name, f.isTabbedForm, f.modalForm) from Form f where f.flow=?1")
	List<FormLiteDto> getFormBasicListByPage(Flow flow, Pageable pageable);

	/**
	 * @param prodConfigIdList
	 * @return int
	 */
	@Query("select count(fm.uid) from Form fm where (fm.uid not in (select fd.inboundFormId from FormDependency fd where fd.outboundFormId is not null) or (fm.uid not in (select fdo.outboundFormId from FormDependency fdo) and (fm.uid <> fm.flow.defaultFormId	or fm.flow.defaultFormId is null))) and fm.productConfigId in ?1")
	int countOrphanForms(List<UUID> prodConfigIdList);

	/**
	 * @param prodConfigIdList
	 * @return List&lt;Form&gt;
	 */	
	@Query("select fm from Form fm where (fm.uid not in (select fd.inboundFormId from FormDependency fd where fd.outboundFormId is not null) or (fm.uid not in (select fdo.outboundFormId from FormDependency fdo) and (fm.uid <> fm.flow.defaultFormId	or fm.flow.defaultFormId is null))) and fm.productConfigId in ?1 order by fm.lastModifiedDate desc")
	List<Form> getOrphanForms(List<UUID> prodConfigIdList);

	/**
	 * @param flowId
	 * @return List&lt;Form&gt;
	 */	
	@Query("select fm from Form fm where (fm.uid not in (select fd.inboundFormId from FormDependency fd) or (fm.uid not in (select fdo.outboundFormId from FormDependency fdo) and (fm.uid <> fm.flow.defaultFormId	or fm.flow.defaultFormId is null))) and fm.flow.uid = ?1")
	List<Form> getOrphanFormsByFlowId(UUID flowId);

	/**
	 * @param outboundFormIds
	 * @return List&lt;Form&gt;
	 */	
	List<Form> findByUidIn(Set<UUID> outboundFormIds);

	/**
	 * @param linkedFormIds
	 * @return List&lt;Form&gt;
	 */	
	List<Form> findByPublishedFormNotNullAndUidIn(Set<UUID> linkedFormIds);

	/**
	 * @param formId
	 * @return Optional&lt;Form&gt;
	 */	
	@Query("select new Form(f.modalForm , f.isTabbedForm , f.isDisabled , f.publishedForm)  from Form f where f.uid = ?1")
	Optional<Form> getModalAndTabbedDetails(UUID formId);

	/**
	 * @param outboundFormIds
	 * @return Set&lt;UUID&gt;
	 */	
	@Query("select fm.uid from Form fm where fm.uid in ?1")
	Set<UUID> findExistingFormIds(Set<UUID> outboundFormIds);

}
