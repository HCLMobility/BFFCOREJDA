package com.jda.mobility.framework.extensions.common;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.ErrorResponse;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;

/**
 * BffResponse Class for building response.
 * 
 * @author HCL Technologies Ltd.
 */
@Component
public class BffResponse {
	/** The field resourceBundleRepo of type ResourceBundleRepository */
	@Autowired
	private ResourceBundleRepository resourceBundleRepo;
	/** The field sessionDetails of type SessionDetails */
	@Autowired
	private SessionDetails sessionDetails;

	/**
	 * @param <T> The template generic for the object
	 * @param obj The input object to generate response
	 * @param code The API error code object
	 * @param userCode The user error code object
	 * @param httpStatus The HTTP status code to generate response
	 * @return BffCoreResponse The success/error response object
	 */
	public <T> BffCoreResponse response(T obj, BffResponseCode code, BffResponseCode userCode, StatusCode httpStatus) {
		String locale = sessionDetails.getLocale() != null ? sessionDetails.getLocale() : BffAdminConstantsUtils.LOCALE;
		
		String codeMessage = getMessage(code, locale, BffAdminConstantsUtils.EMPTY_SPACES);
		String detailMessage = getMessage(userCode, locale, BffAdminConstantsUtils.EMPTY_SPACES);
		
		return BffUtils.buildResponse(obj, code.getCode(), codeMessage, detailMessage, httpStatus.getValue());

	}

	/**
	 * @param <T> The template generic for the object
	 * @param obj The input object to generate response
	 * @param code The API error code object
	 * @param userCode The user error code object
	 * @param httpStatus The HTTP status code to generate response
	 * @param msgCodeObj The replacement strings for API error messages
	 * @param msgUserObj The replacement strings for user error messages
	 * @return BffCoreResponse The success/error response object
	 */
	public <T> BffCoreResponse response(T obj, BffResponseCode code, BffResponseCode userCode, StatusCode httpStatus,
			String msgCodeObj, String msgUserObj) {
		String locale = sessionDetails.getLocale() != null ? sessionDetails.getLocale() : BffAdminConstantsUtils.LOCALE;
		String msgCodeObjStr = Optional.ofNullable(msgCodeObj).orElse(BffAdminConstantsUtils.EMPTY);
		String msgUserObjStr = Optional.ofNullable(msgUserObj).orElse(BffAdminConstantsUtils.EMPTY);
		
		String codeMessage = getMessage(code, locale, msgCodeObjStr);
		
		String detailMessage = getMessage(userCode, locale, msgUserObjStr);

		return BffUtils.buildResponse(obj, code.getCode(), codeMessage, detailMessage, httpStatus.getValue());

	}


	/**
	 * @param userCodeList The list of API and user error message objects
	 * @param httpStatus The HTTP status object to generate error response
	 * @return BffCoreResponse The success/error response object
	 */
	public BffCoreResponse errResponse(List<BffResponseCode> userCodeList, StatusCode httpStatus) {
		String locale = sessionDetails.getLocale() != null ? sessionDetails.getLocale() : BffAdminConstantsUtils.LOCALE;
		
		String codeMessage = getMessage(userCodeList.get(0), locale, BffAdminConstantsUtils.EMPTY_SPACES);
		
		List<ErrorResponse> errors = new ArrayList<>();
		for (BffResponseCode userCode : userCodeList.subList(1, userCodeList.size())) {
			String detailMessage = getMessage(userCode, locale, BffAdminConstantsUtils.EMPTY_SPACES);
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(userCode.getCode());
			errorResponse.setUserMessage(detailMessage);
			errors.add(errorResponse);
		}

		return BffUtils.buildErrResponse(userCodeList.get(0).getCode(), codeMessage, errors, httpStatus.getValue());
	}

	/**
	 * @param userCodeList The list of API and user error message objects
	 * @param httpStatus The HTTP status code object to generate response
	 * @param msgCodeObj The replacement strings for API error messages
	 * @param msgUserObj The replacement strings for user error messages
	 * @return BffCoreResponse The success/error response object
	 */
	public BffCoreResponse errResponse(List<BffResponseCode> userCodeList, StatusCode httpStatus,
			String msgCodeObj, String msgUserObj) {
		String locale = sessionDetails.getLocale() != null ? sessionDetails.getLocale() : BffAdminConstantsUtils.LOCALE;
		String msgCodeObjStr = Optional.ofNullable(msgCodeObj).orElse(BffAdminConstantsUtils.EMPTY);
		String msgUserObjStr = Optional.ofNullable(msgUserObj).orElse(BffAdminConstantsUtils.EMPTY);

		Object[] userCodeArray = msgCodeObjStr.split(BffAdminConstantsUtils.COMMA);

		String codeMessage = getMessage(userCodeList.get(0), locale, userCodeArray);

		Object[] userMessageArray = msgUserObjStr.split(BffAdminConstantsUtils.COMMA);
		
		List<ErrorResponse> errors = new ArrayList<>();
		for (BffResponseCode userCode : userCodeList.subList(1, userCodeList.size())) {
			String detailMessage = getMessage(userCode, locale,userMessageArray);
			
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(userCode.getCode());
			errorResponse.setUserMessage(detailMessage);
			errors.add(errorResponse);
		}

		return BffUtils.buildErrResponse(userCodeList.get(0).getCode(), codeMessage, errors, httpStatus.getValue());
	}
	
	/**
	 * @param userCodeList The list of API and user error message objects
	 * @param httpStatus The HTTP status code object to generate response
	 * @param locale The input locale to generate the error response messages
	 * @param msgCodeObj The replacement strings for API error messages
	 * @param msgUserObj The replacement strings for user error messages
	 * @return BffCoreResponse The success/error response object
	 */
	public BffCoreResponse errResponse(List<BffResponseCode> userCodeList, StatusCode httpStatus,String locale,
			String msgCodeObj, String msgUserObj) {
		String msgCodeObjStr = Optional.ofNullable(msgCodeObj).orElse(BffAdminConstantsUtils.EMPTY);
		String msgUserObjStr = Optional.ofNullable(msgUserObj).orElse(BffAdminConstantsUtils.EMPTY);

		Object[] userCodeArray = msgCodeObjStr.split(BffAdminConstantsUtils.COMMA);

		String codeMessage = getMessage(userCodeList.get(0), locale, userCodeArray);

		Object[] userMessageArray = msgUserObjStr.split(BffAdminConstantsUtils.COMMA);
		
		List<ErrorResponse> errors = new ArrayList<>();
		for (BffResponseCode userCode : userCodeList.subList(1, userCodeList.size())) {
			String detailMessage = getMessage(userCode, locale,userMessageArray);
			
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(userCode.getCode());
			errorResponse.setUserMessage(detailMessage);
			errors.add(errorResponse);
		}

		return BffUtils.buildErrResponse(userCodeList.get(0).getCode(), codeMessage, errors, httpStatus.getValue());
	}	

	/**
	 * @param userCodeList The list of API and user error message objects
	 * @param httpStatusCode The HTTP status code to generate response
	 * @param msgCodeObj The replacement strings for API error messages
	 * @param msgUserObj The replacement strings for user error messages
	 * @return BffCoreResponse The success/error response object
	 */
	public BffCoreResponse errResponseIgnoreDelim(List<BffResponseCode> userCodeList, int httpStatusCode,
			String msgCodeObj, String msgUserObj) {
		String locale = sessionDetails.getLocale() != null ? sessionDetails.getLocale() : BffAdminConstantsUtils.LOCALE;
		String msgCodeObjStr = Optional.ofNullable(msgCodeObj).orElse(BffAdminConstantsUtils.EMPTY);
		String msgUserObjStr = Optional.ofNullable(msgUserObj).orElse(BffAdminConstantsUtils.EMPTY);
		
		String codeMessage = getMessage(userCodeList.get(0), locale, msgCodeObjStr);
		
		List<ErrorResponse> errors = new ArrayList<>();
		for (BffResponseCode userCode : userCodeList.subList(1, userCodeList.size())) {

			String detailMessage = getMessage(userCode, locale, msgUserObjStr);
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(userCode.getCode());
			errorResponse.setUserMessage(detailMessage);
			errors.add(errorResponse);
		}

		return BffUtils.buildErrResponse(userCodeList.get(0).getCode(), codeMessage, errors, httpStatusCode);
	}
	
	/**
	 * @param userCodeList The list of API and user error message objects
	 * @param httpStatus The HTTP status code object to generate response
	 * @param locale The input locale to generate the error response messages
	 * @return BffCoreResponse The success/error response object
	 */
	public BffCoreResponse errResponse(List<BffResponseCode> userCodeList, StatusCode httpStatus, String locale) {
		locale = locale != null ? locale : BffAdminConstantsUtils.LOCALE;
		
		String codeMessage = getMessage(userCodeList.get(0), locale, BffAdminConstantsUtils.EMPTY_SPACES);
		
		List<ErrorResponse> errors = new ArrayList<>();
		for (BffResponseCode userCode : userCodeList.subList(1, userCodeList.size())) {
			String detailMessage = getMessage(userCode, locale, BffAdminConstantsUtils.EMPTY_SPACES);
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrorCode(userCode.getCode());
			errorResponse.setUserMessage(detailMessage);
			errors.add(errorResponse);
		}

		return BffUtils.buildErrResponse(userCodeList.get(0).getCode(), codeMessage, errors, httpStatus.getValue());
	}
	
	private String getMessage(BffResponseCode keyCode, String locale, Object... msgStr) {
		String msg ;
		List<ResourceBundle> userResBundle = resourceBundleRepo.findByLocaleAndRbkey(locale, keyCode.getKey());
		
		if (Optional.ofNullable(userResBundle).isPresent() && !userResBundle.isEmpty()) {
			msg = MessageFormat.format(userResBundle.get(0).getRbvalue(), msgStr);
		}
		// No value is present , then send "en" locale value or key itself
		else {
			List<ResourceBundle> userResBundleEn = resourceBundleRepo
					.findByLocaleAndRbkey(BffAdminConstantsUtils.LOCALE, keyCode.getKey());
			if (Optional.ofNullable(userResBundleEn).isPresent() && !userResBundleEn.isEmpty()) {
				msg = MessageFormat.format(userResBundleEn.get(0).getRbvalue(), msgStr);
			} else {
				msg = MessageFormat.format(keyCode.getKey(), msgStr);
			}
		}
		return msg;
	}
}