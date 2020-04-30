package com.jda.mobility.framework.extensions.security;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

public interface AuthorizationValidator {

    /**
     * Validates the given request authorizations (sent via access token) against the authorizations required by an endpoint.
     * @param request The request that should be validated given the endpoint's authorization(s).
     * @param claims The claims from the request, already having passed initial validations. Available for claims inspection as needed.
     * @param authorizations The authorization(s) the endpoint requires to proceed.
     * @return true if the endpoint is accessible
     */
    boolean validate(HttpServletRequest request, Map<String,Object> claims, Set<String> authorizations);
}
