package com.jda.mobility.framework.extensions.security;

import java.io.IOException;
import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * This filter secures /actuator endpoints and requires that all
 * requests to actuator endpoints (other than the /actuator/health)
 * have the expected {@code X-ACTUATOR-API-KEY} header value.
 * <p>
 * The key value is generally configured in an environment variable
 * and injected into the constructor.
 */
public class ActuatorApiKeyFilter extends OncePerRequestFilter {

    private final String actuatorApiKey;

    private static final String ACTUATOR_API_KEY_HEADER = "X-ACTUATOR-API-KEY";
    private static final String ACTUATOR_PATH = "/actuator";
    private static final String ACTUATOR_HEALTH_ENDPOINT = ACTUATOR_PATH + "/health";

    public ActuatorApiKeyFilter(String actuatorApiKey) {
        this.actuatorApiKey = actuatorApiKey;
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain chain) throws ServletException, IOException {
        if (isNonHealthActuatorRequestAndApiKeyIsIncorrect(request)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isNonHealthActuatorRequestAndApiKeyIsIncorrect(HttpServletRequest request) {
        return request.getServletPath().startsWith(ACTUATOR_PATH)
                && !request.getServletPath().equals(ACTUATOR_HEALTH_ENDPOINT)
                && !actuatorApiKey.equals(request.getHeader(ACTUATOR_API_KEY_HEADER));
    }
}