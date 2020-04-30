package com.jda.mobility.framework.extensions.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.ErrorResponse;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffUtils;

/**
 * HCL Technologies Ltd.
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final Logger LOGGER = LogManager.getLogger(RestAuthenticationEntryPoint.class);

	@Override
	public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			AuthenticationException e) throws IOException, ServletException {
		LOGGER.log(Level.DEBUG, "Responding with unauthorized error. Message -:- {}", e.getMessage());

		HttpSession session = httpServletRequest.getSession(false);		
		if (session != null) {
			session.invalidate();
		}
		SecurityContextHolder.clearContext();
		LOGGER.log(Level.DEBUG, "SecurityContext is now cleared for thread local");
		BffCoreResponse bffCoreResponse = null;
		List<ErrorResponse> errors = new ArrayList<>();
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(HttpServletResponse.SC_UNAUTHORIZED);
		errorResponse.setUserMessage(e.getLocalizedMessage());
		errors.add(errorResponse);
		bffCoreResponse = BffUtils.buildErrResponse(HttpServletResponse.SC_UNAUTHORIZED, BffAdminConstantsUtils.AUTHENTICATION_FAILED, errors,
				HttpServletResponse.SC_UNAUTHORIZED);

		String json = new ObjectMapper().writeValueAsString(bffCoreResponse);
		httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		httpServletResponse.getWriter().write(json);
		httpServletResponse.getWriter().flush();
	}
}
