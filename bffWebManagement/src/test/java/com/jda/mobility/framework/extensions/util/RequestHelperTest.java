package com.jda.mobility.framework.extensions.util;

import com.jda.mobility.framework.extensions.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;

import static com.jda.mobility.framework.extensions.util.RequestHelper.SET_COOKIE_HEADER_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class RequestHelperTest {

    @Mock
    private AppProperties appProperties;

    private RequestHelper helper;

    @BeforeEach
    void setup() {
        initMocks(this);
        helper = new RequestHelper(appProperties);
    }

    @Test
    void initNewWithTokenInRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, TOKEN_VALUE);
        request.addHeader(SET_COOKIE_HEADER_NAME, COOKIE_VALUE);

        when(appProperties.isOidcEnabled()).thenReturn(true);
        when(appProperties.isBasicAuthEnabled()).thenReturn(false);

        HttpHeaders headers = helper.initHeadersFrom(request);

        assertEquals(TOKEN_VALUE, headers.getFirst(HttpHeaders.AUTHORIZATION));
        assertNull(headers.getFirst(HttpHeaders.COOKIE));
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
    }

    @Test
    void initNewWithoutTokenInRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(SET_COOKIE_HEADER_NAME, RequestHelperTest.COOKIE_VALUE);

        when(appProperties.isOidcEnabled()).thenReturn(true);
        when(appProperties.isBasicAuthEnabled()).thenReturn(false);

        HttpHeaders headers = helper.initHeadersFrom(request);

        assertNull(headers.getFirst(HttpHeaders.AUTHORIZATION));
        assertNull(headers.getFirst(HttpHeaders.COOKIE));
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
    }

    @Test
    void initNewWithCookieInRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, TOKEN_VALUE);
        request.addHeader(SET_COOKIE_HEADER_NAME, RequestHelperTest.COOKIE_VALUE);

        when(appProperties.isBasicAuthEnabled()).thenReturn(true);
        when(appProperties.isOidcEnabled()).thenReturn(false);

        HttpHeaders headers = helper.initHeadersFrom(request);

        assertNull(headers.getFirst(HttpHeaders.AUTHORIZATION));
        assertEquals(COOKIE_VALUE, headers.getFirst(HttpHeaders.COOKIE));
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
    }

    @Test
    void initNewWithoutCookieInRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, TOKEN_VALUE);

        when(appProperties.isBasicAuthEnabled()).thenReturn(true);
        when(appProperties.isOidcEnabled()).thenReturn(false);

        HttpHeaders headers = helper.initHeadersFrom(request);

        assertNull(headers.getFirst(HttpHeaders.AUTHORIZATION));
        assertNull(headers.getFirst(HttpHeaders.COOKIE));
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
    }

    @Test
    void initExistingWithTokenInRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, TOKEN_VALUE);
        request.addHeader(SET_COOKIE_HEADER_NAME, COOKIE_VALUE);

        when(appProperties.isOidcEnabled()).thenReturn(true);
        when(appProperties.isBasicAuthEnabled()).thenReturn(false);

        HttpHeaders original = new HttpHeaders();
        original.setContentLength(CONTENT_LENGTH);
        HttpHeaders headers = helper.initHeadersFrom(original, request);

        assertEquals(TOKEN_VALUE, headers.getFirst(HttpHeaders.AUTHORIZATION));
        assertNull(headers.getFirst(HttpHeaders.COOKIE));
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals(CONTENT_LENGTH, headers.getContentLength());
    }

    @Test
    void initExistingWithoutTokenInRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(SET_COOKIE_HEADER_NAME, RequestHelperTest.COOKIE_VALUE);

        when(appProperties.isOidcEnabled()).thenReturn(true);
        when(appProperties.isBasicAuthEnabled()).thenReturn(false);

        HttpHeaders original = new HttpHeaders();
        original.setContentLength(CONTENT_LENGTH);
        HttpHeaders headers = helper.initHeadersFrom(original, request);

        assertNull(headers.getFirst(HttpHeaders.AUTHORIZATION));
        assertNull(headers.getFirst(HttpHeaders.COOKIE));
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals(CONTENT_LENGTH, headers.getContentLength());
    }

    @Test
    void initExistingWithCookieInRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, TOKEN_VALUE);
        request.addHeader(SET_COOKIE_HEADER_NAME, RequestHelperTest.COOKIE_VALUE);

        when(appProperties.isBasicAuthEnabled()).thenReturn(true);
        when(appProperties.isOidcEnabled()).thenReturn(false);

        HttpHeaders original = new HttpHeaders();
        original.setContentLength(CONTENT_LENGTH);
        HttpHeaders headers = helper.initHeadersFrom(original, request);

        assertNull(headers.getFirst(HttpHeaders.AUTHORIZATION));
        assertEquals(COOKIE_VALUE, headers.getFirst(HttpHeaders.COOKIE));
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals(CONTENT_LENGTH, headers.getContentLength());
    }

    @Test
    void initExistingWithoutCookieInRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, TOKEN_VALUE);

        when(appProperties.isBasicAuthEnabled()).thenReturn(true);
        when(appProperties.isOidcEnabled()).thenReturn(false);

        HttpHeaders original = new HttpHeaders();
        original.setContentLength(CONTENT_LENGTH);
        HttpHeaders headers = helper.initHeadersFrom(original, request);

        assertNull(headers.getFirst(HttpHeaders.AUTHORIZATION));
        assertNull(headers.getFirst(HttpHeaders.COOKIE));
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
        assertEquals(CONTENT_LENGTH, headers.getContentLength());
    }

    @Test
    void initNewWithToken() {
        when(appProperties.isOidcEnabled()).thenReturn(true);
        when(appProperties.isBasicAuthEnabled()).thenReturn(false);

        HttpHeaders headers = helper.initHeadersWith(COOKIE_VALUE, TOKEN_VALUE);

        assertEquals(TOKEN_VALUE, headers.getFirst(HttpHeaders.AUTHORIZATION));
        assertNull(headers.getFirst(HttpHeaders.COOKIE));
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
    }

    @Test
    void initNewWithCookieAndWithoutToken() {
        when(appProperties.isOidcEnabled()).thenReturn(true);
        when(appProperties.isBasicAuthEnabled()).thenReturn(false);

        HttpHeaders headers = helper.initHeadersWith(COOKIE_VALUE, null);

        assertNull(headers.getFirst(HttpHeaders.AUTHORIZATION));
        assertEquals(COOKIE_VALUE, headers.getFirst(HttpHeaders.COOKIE));
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
    }

    @Test
    void initNewWithCookieAndToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, TOKEN_VALUE);
        request.addHeader(SET_COOKIE_HEADER_NAME, RequestHelperTest.COOKIE_VALUE);

        when(appProperties.isBasicAuthEnabled()).thenReturn(true);
        when(appProperties.isOidcEnabled()).thenReturn(false);

        HttpHeaders headers = helper.initHeadersWith(COOKIE_VALUE, TOKEN_VALUE);

        assertEquals(TOKEN_VALUE, headers.getFirst(HttpHeaders.AUTHORIZATION));
        assertNull(headers.getFirst(HttpHeaders.COOKIE));
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
    }

    @Test
    void initNewWithoutCookieAndToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, TOKEN_VALUE);

        when(appProperties.isBasicAuthEnabled()).thenReturn(true);
        when(appProperties.isOidcEnabled()).thenReturn(false);

        HttpHeaders headers = helper.initHeadersWith(null, null);

        assertNull(headers.getFirst(HttpHeaders.AUTHORIZATION));
        assertNull(headers.getFirst(HttpHeaders.COOKIE));
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
    }

    private static final String TOKEN_VALUE = "Bearer test";
    private static final String COOKIE_VALUE = "COOOOOKIE";
    private static final long CONTENT_LENGTH = 100L;
}