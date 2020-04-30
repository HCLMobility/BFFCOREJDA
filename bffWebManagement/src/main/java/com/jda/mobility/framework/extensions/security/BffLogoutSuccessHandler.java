/**
 * 
 */
package com.jda.mobility.framework.extensions.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import com.jda.mobility.framework.extensions.service.AppConfigService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ChannelType;

/**
 * @author HCL Technologies Ltd.
 *
 */
public class BffLogoutSuccessHandler extends HttpStatusReturningLogoutSuccessHandler {
	private static final Logger LOGGER = LogManager.getLogger(BffLogoutSuccessHandler.class);
	@Autowired
	private AppConfigService appConfigService;
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException{

		String refererUrl = request.getHeader("Referer");
		LOGGER.log(Level.DEBUG, "Logout from: {}" , refererUrl);
		if (authentication != null && authentication.getPrincipal() != null) {
			UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
			LOGGER.log(Level.DEBUG, "Logout from principal: {}", principal);
			if (principal.getChannel().equals(ChannelType.MOBILE_RENDERER)) {
				appConfigService.clearAppConfig(principal.getUserId(),principal.getDeviceId());
			} 
		}
		super.onLogoutSuccess(request, response, authentication);
	}
}
