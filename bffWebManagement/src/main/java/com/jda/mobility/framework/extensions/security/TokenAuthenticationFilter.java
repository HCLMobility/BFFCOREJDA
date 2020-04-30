package com.jda.mobility.framework.extensions.security;

import java.io.IOException;
import java.util.Map;

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
import org.springframework.security.core.session.SessionInformation;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.jda.iam.client.TokenValidator;
import com.jda.iam.config.ClientConfiguration;
import com.jda.mobility.framework.extensions.util.ConstantsUtils;
import com.jda.mobility.framework.extensions.util.WebCommonUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;

/**
 * This Class filter request as per information provided by token.
 */

public class TokenAuthenticationFilter extends OncePerRequestFilter  {

	private static final Logger LOGGER = LogManager.getLogger(TokenAuthenticationFilter.class);
	@SuppressWarnings(ConstantsUtils.RAWTYPE)
	@Autowired
	private SpringSessionBackedSessionRegistry sessionRegistry;
	@Autowired
	private TokenProvider tokenProvider;
	private BffAuthorizationValidator bffAuthValidator;

	public TokenAuthenticationFilter(TokenValidator tokenValidator, ClientConfiguration clientConfiguration) {
		this.bffAuthValidator = new BffAuthorizationValidator(tokenValidator, clientConfiguration);
	}

	public TokenAuthenticationFilter() {
		super();

	}

	/**
	 * Filter request for Valid tokens and sessionId
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		if ("/api/form/v1/log/".equals(request.getRequestURI())) {
			chain.doFilter(request, response);
			return;
		}
		boolean validToken = false;
		String userId = null;
		Map<String, String> authInfo = null;
		String jwt = tokenProvider.getJwtFromRequest(request);
		// switch to validate various token authentication scheme [Basic Authentication/oidc-scheme]
		if (bffAuthValidator != null) {
			LOGGER.log(Level.DEBUG, "Validate token from Openid Provider");
			validToken = bffAuthValidator.authProcessor(request, response);
			LOGGER.log(Level.DEBUG, "Openid token validated as {}", validToken);
			if (validToken) {
				authInfo = tokenProvider.getAuthMapFromToken(jwt);
				userId = authInfo.get("name");
				if (request.getSession(false) != null && !validateUserWithCurrentContext(request, userId)) {
					WebCommonUtils.respondUnAuthorized(BffAdminConstantsUtils.AUTHENTICATION_FAILED, response);
					return;
				}
				LOGGER.log(Level.DEBUG, "***Claim details~ SUB: {}, Audience: {}, Name: {}, Expiry ms: {}",
						authInfo.get("sub"), authInfo.get("aud"), authInfo.get("name"), authInfo.get("exp"));
				chain.doFilter(request, response);
			}
		} else {
			LOGGER.log(Level.DEBUG, "Validate token from BFF Provider");
			if (StringUtils.hasText(jwt)) {
				LOGGER.log(Level.DEBUG, "BFF token contains text to be validated.");
				validToken = tokenProvider.validateToken(jwt);
				LOGGER.log(Level.DEBUG, "BFF token validated as {}", validToken);
			}
			if (validToken) {
				userId = tokenProvider.getUserIdFromToken(jwt);
			}
			if (request.getSession(false) != null && !validateUserWithCurrentContext(request, userId)) {
				WebCommonUtils.respondUnAuthorized(BffAdminConstantsUtils.AUTHENTICATION_FAILED, response);
			} else {
				chain.doFilter(request, response);
			}
		}
	}

	/**
	 * @param request
	 * @param userId
	 * @return boolean
	 */
	private boolean validateUserWithCurrentContext(HttpServletRequest request, String userId) {
		boolean validateCheck = false;
		if (request.getSession(false) != null) {
			SessionInformation info = sessionRegistry.getSessionInformation(request.getSession(false).getId());
			if (info != null && !info.isExpired()) {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				if (authentication != null && authentication.isAuthenticated()) {
					UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
					if (userPrincipal.getUserId() != null && userPrincipal.getUserId().equals(userId)) {
						validateCheck = true;
					}
				}
			}

		}

		return validateCheck;

	}
}