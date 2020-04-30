package com.jda.mobility.framework.extensions.service.impl;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.entity.ApiMaster;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.repository.ApiMasterRepository;
import com.jda.mobility.framework.extensions.service.PreAndPostProcessorService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ApiUploadMode;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

@Service
public class PreAndPostProcessorServiceImpl implements PreAndPostProcessorService {

	private static final Logger LOGGER = LogManager.getLogger(PreAndPostProcessorServiceImpl.class);
	
	@Autowired
	private ApiMasterRepository apiMasterRepository;
	
	@Autowired
	private BffResponse bffResponse;
	
	
	/**
	 * Upload pre and post processor files for API master Id 
	 */
	@Override
	public BffCoreResponse importApiIntoNewRegistry(MultipartFile preProcessorFile, MultipartFile postProcessorFile,
			UUID apiMasterId, ApiUploadMode apiUploadMode) {
		BffCoreResponse bffCoreResponse = null;
		StringBuilder userMsg = new StringBuilder();
		try {
			if ((preProcessorFile == null || preProcessorFile.isEmpty())
					&& (postProcessorFile == null || postProcessorFile.isEmpty())) {
				return bffResponse.errResponse(List.of(BffResponseCode.ERR_PRE_AND_POST_PROCESSOR_FILE_NULL,
						BffResponseCode.ERR_PRE_AND_POST_PROCESSOR_FILE_NULL), StatusCode.CONFLICT);
			}
			ApiMaster apiMaster = apiMasterRepository.findById(apiMasterId).orElseThrow();

			if (apiUploadMode.equals(ApiUploadMode.CHECK_UPLOAD)) {
				return checkFileUpload(preProcessorFile, postProcessorFile, apiMasterId, apiMaster);
			}
			if (preProcessorFile != null && !preProcessorFile.isEmpty()) {
				apiMaster.setPreProcessor(preProcessorFile.getBytes());
				userMsg.append(BffAdminConstantsUtils.PRE_PROCESSOR);
			}
			if (postProcessorFile != null && !postProcessorFile.isEmpty()) {
				apiMaster.setPostProcessor(postProcessorFile.getBytes());
				if (!userMsg.toString().isEmpty()) {
					userMsg.append(BffAdminConstantsUtils.SPACE).append(BffAdminConstantsUtils.AMPERSAND)
							.append(BffAdminConstantsUtils.SPACE).append(BffAdminConstantsUtils.POST_PROCESSOR);
				} else {
					userMsg.append(BffAdminConstantsUtils.POST_PROCESSOR);
				}
			}
			apiMasterRepository.save(apiMaster);

			return bffResponse.response(apiMasterId, BffResponseCode.PRE_AND_POST_PROCESSOR_UPLOAD_SUCCESS,
					BffResponseCode.PRE_AND_POST_PROCESSOR_UPLOAD_SUCCESS, StatusCode.OK, null, userMsg.toString());
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.DB_EXP_MSG, apiMasterId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_PRE_AND_POST_PROCESSOR_UPLOAD,
							BffResponseCode.DB_ERR_PRE_AND_POST_PROCESSOR_UPLOAD),
					StatusCode.INTERNALSERVERERROR, null, userMsg.toString());
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, apiMasterId);
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_PRE_AND_POST_PROCESSOR_UPLOAD,
					BffResponseCode.ERR_PRE_AND_POST_PROCESSOR_UPLOAD), StatusCode.BADREQUEST, null, userMsg.toString());
		}
		return bffCoreResponse;
	}


	private BffCoreResponse checkFileUpload(MultipartFile preProcessorFile, MultipartFile postProcessorFile,
			UUID apiMasterId, ApiMaster apiMaster) {
		StringBuilder userMsg = new StringBuilder();
		if (preProcessorFile != null && !preProcessorFile.isEmpty() && apiMaster.getPreProcessor() != null) {
			userMsg.append(BffAdminConstantsUtils.PRE_PROCESSOR);
		}
		if (postProcessorFile != null && !postProcessorFile.isEmpty() && apiMaster.getPostProcessor() != null) {
			if (!ObjectUtils.isEmpty(userMsg)) {
				userMsg.append(BffAdminConstantsUtils.SPACE).append(BffAdminConstantsUtils.AMPERSAND)
						.append(BffAdminConstantsUtils.SPACE).append(BffAdminConstantsUtils.POST_PROCESSOR);
			} else {
				userMsg.append(BffAdminConstantsUtils.POST_PROCESSOR);
			}
		}
		if (!ObjectUtils.isEmpty(userMsg)) {
			return bffResponse.errResponse(List.of(BffResponseCode.ERR_PRE_AND_POST_PROCESSOR_EXISTING,
					BffResponseCode.ERR_PRE_AND_POST_PROCESSOR_EXISTING), StatusCode.BADREQUEST, null, userMsg.toString());
		} else {
			return bffResponse.response(apiMasterId, BffResponseCode.PRE_AND_POST_PROCESSOR_UPLOAD_CONFIRM_SUCCESS,
					BffResponseCode.PRE_AND_POST_PROCESSOR_UPLOAD_CONFIRM_SUCCESS, StatusCode.OK);
		}
	}

}
