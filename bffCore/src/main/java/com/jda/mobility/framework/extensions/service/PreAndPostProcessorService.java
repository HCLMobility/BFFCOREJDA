package com.jda.mobility.framework.extensions.service;

import java.util.UUID;

import org.springframework.retry.annotation.Retryable;
import org.springframework.web.multipart.MultipartFile;

import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ApiUploadMode;

@Retryable(value = { Exception.class }, maxAttempts = 5)
public interface PreAndPostProcessorService {

	/**
	 * @param preProcessorFile The pre-processor DSLR file as bytes
	 * @param postProcessorFile The post-processor DSLR file as bytes
	 * @param apiMasterId The ID of API to which the pre/post-processor needs to be associated
	 * @param apiUploadMode The API upload mode - CHECK/CONFIRM UPLOAD
	 * @return BffCoreResponse The success/error response object
	 */
	BffCoreResponse importApiIntoNewRegistry(MultipartFile preProcessorFile, MultipartFile postProcessorFile, UUID apiMasterId, ApiUploadMode apiUploadMode);

}
