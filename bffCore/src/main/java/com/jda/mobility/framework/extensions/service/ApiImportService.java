package com.jda.mobility.framework.extensions.service;

import java.util.List;
import java.util.UUID;

import org.springframework.retry.annotation.Retryable;
import org.springframework.web.multipart.MultipartFile;

import com.jda.mobility.framework.extensions.model.ApiMasterRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ApiRegistryType;

/**
 * Uploaded file is parsed and set in ApiRegistry, ApiMaster entities
 * 
 * @author HCL Technologies Ltd.
 */

@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface ApiImportService {

	/**
	 * @param file The swagger file
	 * @param registryName The name of the registry
	 * @param apiType The registry type - INTERNAL or EXTERNAL
	 * @param ruleFile The orchestration rule file
	 * @return BffCoreResponse The success/error message object
	 */
	public BffCoreResponse importApiIntoNewRegistry(MultipartFile file, String registryName, ApiRegistryType apiType,
			MultipartFile ruleFile);

	/**
	 * @param fileAsBytes The swagger file
	 * @param override The API operation flag - override or append
	 * @param registryId The ID of the registry to be modified
	 * @param ruleFile  The orchestration rule file 
	 * @return BffCoreResponse The success/error message object
	 */
	public BffCoreResponse importApiIntoExistingRegistry(byte[] fileAsBytes, boolean override, UUID registryId, MultipartFile ruleFile);

	/**
	 * @param apisToOverride The list of APIs to be overriden
	 * @param registryId The ID of the registry to be modified 
	 * @return BffCoreResponse The success/error message object
	 */
	public BffCoreResponse overrideExistingApis(List<ApiMasterRequest> apisToOverride, UUID registryId);

}
