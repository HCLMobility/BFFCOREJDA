package com.jda.mobility.framework.extensions.util;

import javax.servlet.http.HttpServletRequest;

import com.jda.mobility.framework.extensions.config.AppProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import static com.jda.mobility.framework.extensions.utils.BffUtils.buildValidHeader;

/**
 * Provides a few utility methods useful for extracting
 * auth related headers and creating new headers to send
 * along to backends.
 */
@Component
public class RequestHelper {

    private final AppProperties appProperties;

    public RequestHelper(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    /**
     * Extracts the {@code "Authorization"} header from the specified
     * request if OIDC Authentication is enabled.
     * @param request The request from which the header should
     * be extracted.
     * @return This may return {@code null} if OIDC is not enabled
     * or the header is not present on the request.
     */
    public String oidcToken(HttpServletRequest request) {
        if (this.appProperties.isOidcEnabled()) {
            return request.getHeader(HttpHeaders.AUTHORIZATION);
        }
        return null;
    }

    /**
     * Extracts the {@code "SET_COOKIE"} header from the specified
     * request if basic authentication is enabled.
     * @param request The request from which the header should
     * be extracted.
     * @return This may return {@code null} if basic auth is not
     * enabled or the header is not present on the request.
     */
    public String cookieValue(HttpServletRequest request) {
        if (appProperties.isBasicAuthEnabled()) {
            return request.getHeader(SET_COOKIE_HEADER_NAME);
        }
        return null;
    }

    /**
     * Creates a new HttpHeaders object with either the {@code "Cookie"}
     * or {@code "Authorization"} header present in the request set
     * on the headers.
     * <p>
     * The header that is added depends on which type of authentication
     * is enabled for the server. If {@link AppProperties#isBasicAuthEnabled()
     * basic auth is enabled}, the {@code "Cookie"} header will be added.
     * If {@link AppProperties#isOidcEnabled() oidc is enabled}, the {@code
     * "Authorization"} header will be added.
     * <p>
     * Additionally, {@link MediaType#APPLICATION_JSON_VALUE} is set
     * as the {@code "ContentType"}.
     *
     * @param request The request from which the auth headers should
     * be extracted.
     * @return A new {@code HttpHeaders} instance.
     */
    public HttpHeaders initHeadersFrom(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        return initHeadersFrom(headers, request);
    }

    /**
     * Updates the passed in headers with either the {@code "Cookie"}
     * or {@code "Authorization"} header present in the request set
     * on the headers.
     * <p>
     * The header that is added depends on which type of authentication
     * is enabled for the server. If {@link AppProperties#isBasicAuthEnabled()
     * basic auth is enabled}, the {@code "Cookie"} header will be added.
     * If {@link AppProperties#isOidcEnabled() oidc is enabled}, the {@code
     * "Authorization"} header will be added.
     * <p>
     * Additionally, {@link MediaType#APPLICATION_JSON_VALUE} is set
     * as the {@code "ContentType"}.
     *
     * @param headers The headers object to which header values should be added.
     * @param request The request from which the auth headers should
     * be extracted.
     * @return The passed in headers instance.
     */
    public HttpHeaders initHeadersFrom(HttpHeaders headers, HttpServletRequest request) {
        if (appProperties.isOidcEnabled()) {
            headers.add(HttpHeaders.AUTHORIZATION, buildValidHeader(request.getHeader(HttpHeaders.AUTHORIZATION)));
        } else {
            headers.add(HttpHeaders.COOKIE, buildValidHeader(request.getHeader(SET_COOKIE_HEADER_NAME)));
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * Creates a new HttpHeaders object with either the {@code "Cookie"}
     * or {@code "Authorization"} header present in the request set
     * on the headers.
     * <p>
     * The header that is added depends on which type of the parameters
     * is passed as a non-blank value.
     * <p>
     * Additionally, {@link MediaType#APPLICATION_JSON_VALUE} is set
     * as the {@code "ContentType"}.
     * @param cookieValue If {@code tokenValue} is blank, this value will
     * be added to the returned {@code HttpHeaders} object as the value of
     * the {@code "Cookie"} header.
     * @param tokenValue If this value is not blank, it will be added to
     * the returned {@code HttpHeaders} object as the value of the {@code
     * "Authentication"} header.
     * @return A new {@code HttpHeaders} instance.
     */
    public HttpHeaders initHeadersWith(String cookieValue, String tokenValue) {
        HttpHeaders headers = new HttpHeaders();
        if (StringUtils.isNotBlank(tokenValue)) {
            headers.add(HttpHeaders.AUTHORIZATION, buildValidHeader(tokenValue));
        } else {
            headers.add(HttpHeaders.COOKIE, buildValidHeader(cookieValue));
        }
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    static final String SET_COOKIE_HEADER_NAME = "SET_COOKIE";


}
