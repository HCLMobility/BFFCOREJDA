package com.jda.mobility.framework.extensions.security;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffUtils;

@Component
public class CorrelationInterceptor extends HandlerInterceptorAdapter {
	private static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-Id";
	private static final String CORRELATION_ID_LOG_VAR_NAME = "correlationId";
	private static final String HEADER_X_AUTH_TOKEN = "X-Auth-Token";
	private static final String SESSION_ID_LOG_VAR_NAME = "sessionId";

	@Autowired
	private Environment env;

	@Autowired
	private RestTemplate restTemplate;

	private static final Logger LOGGER = LogManager.getLogger(CorrelationInterceptor.class);

	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
			throws Exception {
		request.setAttribute(BffAdminConstantsUtils.APP_ANALYTICS_API_STARTTIME_KEY, System.currentTimeMillis());
		final String correlationId = getCorrelationIdFromHeader(request);
		ThreadContext.put(CORRELATION_ID_LOG_VAR_NAME, correlationId);
		final String sessionId = request.getHeader(HEADER_X_AUTH_TOKEN);
		ThreadContext.put(SESSION_ID_LOG_VAR_NAME, sessionId);
		response.addHeader(CORRELATION_ID_HEADER_NAME, correlationId);
		return true;
	}

	@Override
	public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
			final Object handler, final Exception ex) throws Exception {
		String analyticsEnabled = env.getProperty(BffAdminConstantsUtils.APP_ANALYTICS_ENABLED_KEY);
		if (analyticsEnabled != null && "true".equals(analyticsEnabled)) {
			recordAndPostAnalytics(request);
		}

		ThreadContext.removeAll(List.of(CORRELATION_ID_LOG_VAR_NAME, SESSION_ID_LOG_VAR_NAME));
	}

	private void recordAndPostAnalytics(final HttpServletRequest request) {
		final String sessionId = request.getHeader(HEADER_X_AUTH_TOKEN);
		try {
			long timeTaken = System.currentTimeMillis() - (Long) request.getAttribute(BffAdminConstantsUtils.APP_ANALYTICS_API_STARTTIME_KEY);
			String pageTitle = "UNAUTHORIZED";
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && authentication.isAuthenticated() && 
					authentication.getPrincipal() != null && authentication.getPrincipal() instanceof UserPrincipal) {
				pageTitle = ((UserPrincipal) authentication.getPrincipal()).getChannel().getType();
			}
			String randomUUID = UUID.randomUUID().toString();
			StringBuilder payloadBuilder = new StringBuilder();
			payloadBuilder.append("v").append(BffAdminConstantsUtils.EQUAL)
					.append(env.getProperty(BffAdminConstantsUtils.APP_ANALYTICS_API_VERSION_KEY))
					.append(BffAdminConstantsUtils.AMPERSAND);
			payloadBuilder.append("tid").append(BffAdminConstantsUtils.EQUAL)
					.append(env.getProperty(BffAdminConstantsUtils.APP_ANALYTICS_API_TRACKID_KEY))
					.append(BffAdminConstantsUtils.AMPERSAND);
			payloadBuilder.append("t").append(BffAdminConstantsUtils.EQUAL)
					.append(env.getProperty(BffAdminConstantsUtils.APP_ANALYTICS_API_PAGEHITTYPE_KEY))
					.append(BffAdminConstantsUtils.AMPERSAND);
			payloadBuilder.append("cid").append(BffAdminConstantsUtils.EQUAL)
					.append(sessionId != null ? sessionId : randomUUID).append(BffAdminConstantsUtils.AMPERSAND);
			payloadBuilder.append("dh").append(BffAdminConstantsUtils.EQUAL)
					.append(request.getHeader(BffAdminConstantsUtils.REQHEADER_HOST_KEY))
					.append(BffAdminConstantsUtils.AMPERSAND);
			payloadBuilder.append("dp").append(BffAdminConstantsUtils.EQUAL).append(request.getRequestURI())
					.append(BffAdminConstantsUtils.AMPERSAND);
			payloadBuilder.append("dt").append(BffAdminConstantsUtils.EQUAL).append(pageTitle)
					.append(BffAdminConstantsUtils.AMPERSAND);
			payloadBuilder.append("cm1").append(BffAdminConstantsUtils.EQUAL).append(timeTaken);

			UriComponents uriComponents = UriComponentsBuilder.newInstance()
					.scheme(env.getProperty(BffAdminConstantsUtils.APP_ANALYTICS_API_URLSCHEME_KEY))
					.host(env.getProperty(BffAdminConstantsUtils.APP_ANALYTICS_API_HOST_KEY))
					.path(env.getProperty(BffAdminConstantsUtils.APP_ANALYTICS_API_CONTEXTPATH_KEY)).build();

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.USER_AGENT, BffUtils.buildValidHeader(request.getHeader(HttpHeaders.USER_AGENT)));

			restTemplate.exchange(uriComponents.toUriString(), HttpMethod.POST,
					new HttpEntity<>(payloadBuilder.toString(), headers), String.class);
			LOGGER.log(Level.INFO, "Analytics recorded successfully for: {} hosted at {}", request.getRequestURI(),
					request.getHeader(BffAdminConstantsUtils.REQHEADER_HOST_KEY));
		} catch (HttpStatusCodeException hexp) {
			LOGGER.log(Level.ERROR, hexp.getLocalizedMessage(), hexp);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
		}
	}

	private String getCorrelationIdFromHeader(final HttpServletRequest request) {
		String correlationId = request.getHeader(CORRELATION_ID_HEADER_NAME);
		if (StringUtils.isBlank(correlationId)) {
			correlationId = generateUniqueCorrelationId();
		}
		return correlationId;
	}

	private String generateUniqueCorrelationId() {
		return UUID.randomUUID().toString().toUpperCase().replace("-", "");
	}
}
