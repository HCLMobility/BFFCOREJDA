/**
 * 
 */
package com.jda.mobility.framework.extensions.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jda.mobility.framework.extensions.repository.MasterUserRepository;
import com.jda.mobility.framework.extensions.util.WebCommonUtils;

/**
 * @author HCL Technologies Ltd.
 *
 */
@Component
public class PermissionAuthorizationFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LogManager.getLogger(PermissionAuthorizationFilter.class);
	@Autowired
	private MasterUserRepository masterUserRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() != null) {
				UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

				if (masterUserRepository.countByUserId(userPrincipal.getUserId()) <= 0) {
					LOGGER.log(Level.DEBUG, "Current user is not super user.");
					WebCommonUtils.respondForbidden("Access disallowed. Current user is not super user.", response);
					return;
				}
			} else {
				WebCommonUtils.respondForbidden("Access disallowed due to failed authorization.", response);
				return;
			}
			filterChain.doFilter(request, response);
		} catch (RuntimeException e) {
			LOGGER.log(Level.ERROR, "Runtime error in permissionAuthorizationFilter: {}", e.getCause(), e);
			WebCommonUtils.respondRuntimeError("Execution error for resource requested.", response);
		}
	}
}
