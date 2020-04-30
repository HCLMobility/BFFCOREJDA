package com.jda.mobility.framework.extensions.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.ErrorResponse;
import com.jda.mobility.framework.extensions.utils.BffUtils;

public class WebCommonUtils {
	private static final Logger LOGGER = LogManager.getLogger(WebCommonUtils.class);
	
	private WebCommonUtils() {
		super();
	}

	public static void respondForbidden(String logMsg, HttpServletResponse response) {
		BffCoreResponse bffCoreResponse = null;
		List<ErrorResponse> errors = new ArrayList<>();
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(403);
		errorResponse.setUserMessage(logMsg);
		errors.add(errorResponse);
		bffCoreResponse = BffUtils.buildErrResponse(403, "The requested endpoint requires proper authorization", errors,
				HttpServletResponse.SC_FORBIDDEN);

		response.setStatus(HttpServletResponse.SC_FORBIDDEN);

		LOGGER.debug("{} Replying with {} FORBIDDEN.", logMsg, HttpServletResponse.SC_FORBIDDEN);

		try {
			String json = new ObjectMapper().writeValueAsString(bffCoreResponse);
			response.getWriter().write(json);
			response.getWriter().flush();
		} catch (IOException e) {
			LOGGER.error("Unexpected failure writing to authorization forbidden response.", e);
		}
	}
	public static void respondRuntimeError(String logMsg, HttpServletResponse response) {
		BffCoreResponse bffCoreResponse = null;
		List<ErrorResponse> errors = new ArrayList<>();
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(500);
		errorResponse.setUserMessage(logMsg);
		errors.add(errorResponse);
		bffCoreResponse = BffUtils.buildErrResponse(500, "Execution error for the requested endpoint.", errors,
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		LOGGER.debug("{} Replying with {} runtime error.", logMsg, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		try {
			String json = new ObjectMapper().writeValueAsString(bffCoreResponse);
			response.getWriter().write(json);
			response.getWriter().flush();
		} catch (IOException e) {
			LOGGER.error("Unexpected error condition writing to runtime error response.", e);
		}
	}
	public static void respondUnAuthorized(String logMsg, HttpServletResponse response) {
		BffCoreResponse bffCoreResponse = null;
		List<ErrorResponse> errors = new ArrayList<>();
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(HttpServletResponse.SC_UNAUTHORIZED);
		errorResponse.setUserMessage(logMsg);
		errors.add(errorResponse);
		bffCoreResponse = BffUtils.buildErrResponse(HttpServletResponse.SC_UNAUTHORIZED, "The requested endpoint requires proper authorization", errors,
				HttpServletResponse.SC_FORBIDDEN);

		response.setStatus(HttpServletResponse.SC_FORBIDDEN);

		LOGGER.debug("{} Replying with {} Unauthorized activity.", logMsg, HttpServletResponse.SC_UNAUTHORIZED);

		try {
			String json = new ObjectMapper().writeValueAsString(bffCoreResponse);
			response.getWriter().write(json);
			response.getWriter().flush();
		} catch (IOException e) {
			LOGGER.error("Unexpected failure writing for unauthorized response.", e);
		}
	}
}
