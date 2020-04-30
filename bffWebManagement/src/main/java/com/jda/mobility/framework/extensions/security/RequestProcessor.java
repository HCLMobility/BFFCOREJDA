package com.jda.mobility.framework.extensions.security;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jda.iam.client.OIDCUtils;
import com.jda.iam.client.TokenValidator;
import com.jda.iam.config.ClientConfiguration;
import com.jda.iam.core.IAMException;
import com.jda.mobility.framework.extensions.util.WebCommonUtils;

public class RequestProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestProcessor.class);
    private final TokenValidator tokenValidator;
    private final ClientConfiguration clientConfiguration;
    private AuthorizationValidator authorizationValidator;

    public RequestProcessor(TokenValidator tokenValidator, ClientConfiguration clientConfiguration) {
        this.tokenValidator = tokenValidator;
        this.clientConfiguration = clientConfiguration;
        this.authorizationValidator = new ScopeValidator();
    }

    public RequestProcessor authorizationValidator(AuthorizationValidator authorizationValidator) {
        this.authorizationValidator = authorizationValidator;
        return this;
    }

    public boolean process(HttpServletRequest request, HttpServletResponse response, Set<String> authorizations) {

        String requestHttpMethod = request.getMethod();
        String requestPath = request.getPathInfo();
        String endpointName = requestHttpMethod + " " + requestPath;

        LOGGER.debug("Validating authorization for request[ {} ]...", endpointName);

        //do general validation of the access token (expiration, audience, etc)
        Map<String,Object> claims = processAccessToken(request);
        if(claims == null) {
        	WebCommonUtils.respondForbidden("Access token initial validation failed.", response);
            return false;
        }

        LOGGER.debug("Access token has passed initial checks. Validating any additional required claims...");

        //validate authorizations if there are any
        if(authorizations.isEmpty()) {
        	LOGGER.debug("The requested endpoint requires no further authorization. Continuing request...");
            return true;
        }

        if(!authorizationValidator.validate(request, claims, authorizations)) {
        	WebCommonUtils.respondForbidden("Request does not satisfy authorization restrictions for " +
                    "endpoint[" + endpointName + "]. Unable to continue.", response);
            return false;
        }

        LOGGER.debug("Request for endpoint[ {} ] is authorized.", endpointName);
        return true;
    }

    private Map<String,Object> processAccessToken(HttpServletRequest request) {
    	
        try {
        	Map<String,Object> accessTokenMap = tokenValidator.validateAccessToken(OIDCUtils.getAuthorizationHeaderToken(request), clientConfiguration);
            return accessTokenMap;
        }
        catch(IAMException e) {
            LOGGER.debug("Failure validating general access token claims.", e);
            return null;
        }
    }

	   
}