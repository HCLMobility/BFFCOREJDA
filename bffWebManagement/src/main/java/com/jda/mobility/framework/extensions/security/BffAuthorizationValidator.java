package com.jda.mobility.framework.extensions.security;


import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jda.iam.client.TokenValidator;
import com.jda.iam.config.ClientConfiguration;
import com.jda.mobility.framework.extensions.security.MethodAuthorizationMapping.Method;


/**
 * The class that will reject requests that don't meet the configured authorization.
 *
 * The following limitations apply:
 *
 * 1. It should only be used for endpoints with simpler path-based (without regex patterns, etc) mapping requirements. 
 * This is because it applies only to the configured filter mapping "servlet name" or "url pattern" options.
 * 2. The class must be registered using the Java-based API, not XML. 
 * This is due to dependencies on IAM configuration/validation.
 */
public class BffAuthorizationValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(BffAuthorizationValidator.class);
	protected final RequestProcessor requestProcessor;
	protected final Set<MethodAuthorizationMapping> mappings = new HashSet<>();

	public BffAuthorizationValidator(TokenValidator tokenValidator, ClientConfiguration clientConfiguration) {
		this.requestProcessor = new RequestProcessor(tokenValidator, clientConfiguration);
		this.requestProcessor.authorizationValidator(new ScopeValidator());
	}

	/**
	 * By default, an access token will need to pass general validations. 
	 * Provide these extra mappings to define which HTTP methods ({@link Method} is handy)
	 * are restricted by authorizations. 
	 * If mappings are omitted, there will be no additional validation performed beyond the general access token checks.
	 * 
	 * @param mappings the mappings that define HTTP methods and the associated
	 *                 authorizations
	 * @return chained
	 */
	public BffAuthorizationValidator authorizations(Set<MethodAuthorizationMapping> mappings) {
		this.mappings.clear();
		if (mappings != null) {
			this.mappings.addAll(mappings);
		}
		return this;
	}

	/**
	 * Provide a custom validator which overrides the default
	 * {@link ScopeValidator}.
	 * 
	 * @param authorizationValidator the validation behavior to use
	 * @return chained
	 */
	public BffAuthorizationValidator overrideValidator(AuthorizationValidator authorizationValidator) {
		if (authorizationValidator != null) {
			this.requestProcessor.authorizationValidator(authorizationValidator);
		}
		return this;
	}

	protected boolean authProcessor(HttpServletRequest request, HttpServletResponse response){

		String requestHttpMethod = request.getMethod();
		String requestPath = request.getPathInfo();

		LOGGER.debug("Locating authorizations for request[{} {}]...", requestHttpMethod, requestPath);
		// use authorizations from any mappings that have the same http method or have
		// the ALL method
		Set<String> applicableAuthorizations = mappings.stream().filter(mapping -> mapping.methods().stream().anyMatch(
				method -> method.equalsIgnoreCase(Method.ALL.toString()) || method.equalsIgnoreCase(requestHttpMethod)))
				.flatMap(mapping -> mapping.authorizations().stream()).collect(Collectors.toSet());

		LOGGER.debug("Found the following authorizations for request[{} {}]: {}.", requestHttpMethod, requestPath,
				applicableAuthorizations);

		return requestProcessor.process(request, response, applicableAuthorizations);
		
	}
}
