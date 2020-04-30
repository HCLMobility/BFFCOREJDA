package com.jda.mobility.framework.extensions.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jda.mobility.framework.extensions.entity.CustomComponentMaster;
import com.jda.mobility.framework.extensions.entity.projection.CustomFormLiteDto;

/**
 * The class CustomComponentMasterRepository.java
 * 
 * @author ChittipalliN HCL Technologies Ltd.
 */
@Repository
@Transactional(readOnly = true)
public interface CustomComponentMasterRepository extends JpaRepository<CustomComponentMaster, UUID> {

	/**
	 * @return int
	 */
	@Query("select count(1) from CustomComponentMaster")
	int customComponentCount();

	/**
	 * @param name
	 * @return List&lt;CustomComponentMaster&gt;
	 */
	List<CustomComponentMaster> findByName(String name);

	/**
	 * @return List&lt;CustomComponentMaster&gt;
	 */
	List<CustomComponentMaster> findAllByOrderByLastModifiedDateDesc();
	
	List<CustomComponentMaster> findAllByOrderByLastModifiedDateDesc(Pageable pageable);

	@Query("select new com.jda.mobility.framework.extensions.entity.projection.CustomFormLiteDto(c.uid, c.name, c.description, c.visibility, c.isdisabled) from CustomComponentMaster c order by c.lastModifiedDate desc")
	List<CustomFormLiteDto> getCustomFormBasicList();
	
	@Query("select new com.jda.mobility.framework.extensions.entity.projection.CustomFormLiteDto(c.uid, c.name, c.description, c.visibility, c.isdisabled) from CustomComponentMaster c")
	List<CustomFormLiteDto> getCustomFormBasicListByPage(Pageable pageable);

	List<CustomComponentMaster> findByProductConfigIdIn(Collection<UUID> uuid);
}
