package com.jda.mobility.framework.extensions.security;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Validates that all of the scopes required by an endpoint are found in the set of requested scopes, that is,
 * the endpoint scopes are a subset of the request scopes.
 */
public class ScopeValidator implements AuthorizationValidator {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public boolean validate(HttpServletRequest request, Map<String,Object> claims, Set<String> authorizations) {

        if(authorizations == null || authorizations.isEmpty()) {
            logger.debug("Unexpected missing or empty set of endpoint authorizations. Nothing to validate.");
            return true;
        }

        Set<String> requestScopes = new HashSet<>();
        if(claims != null) {
            String[] scopes = StringUtils.split(claims.getOrDefault("scope", "").toString(), ' ');
            if(scopes != null) {
                requestScopes = Sets.newHashSet(scopes);
            }
        }

        if(requestScopes.containsAll(authorizations)) {
            return true;
        }
        else {
            logger.debug("Endpoint authorization failed. Endpoint authorizations {} request authorizations {}", authorizations, requestScopes);
            return false;
        }
    }
}
