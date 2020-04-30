/**
 * 
 */
package com.jda.mobility.framework.extensions.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.jda.mobility.framework.extensions.entity.VersionMaster;

/**
 * The class VersionMasterRepository.java
 * @author ChittipalliN
 * HCL Technologies Ltd.
 */
@Repository
public interface VersionMasterRepository extends CrudRepository<VersionMaster, UUID>{

	List<VersionMaster> findByActiveAndChannel(boolean active, String channel);
	
	@Query("select COUNT(1) FROM VersionMaster vma JOIN vma.mappedVersionMappingList vmp \r\n" + 
			"WHERE \r\n" + 
			"vma.uid = vmp.mappedAppVersionMaster.uid\r\n" + 
			"AND\r\n" + 
			"EXISTS (SELECT COUNT(1) FROM VersionMaster vmi \r\n" + 
			"		WHERE vmi.uid = vmp.bffCoreVersionMaster.uid \r\n" + 
			"		AND vmi.channel = :providerChannel \r\n" + 
			"		AND vmi.active = 1)\r\n" +
			"AND\r\n" +
			"vma.channel = :consumerChannel\r\n" + 
			"AND\r\n" + 
			"vma.version = :version")
	int countOfCompatibleVersions(String providerChannel, String consumerChannel, String version);

}
