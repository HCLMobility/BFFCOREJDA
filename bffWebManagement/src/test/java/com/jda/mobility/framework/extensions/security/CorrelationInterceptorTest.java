package com.jda.mobility.framework.extensions.security;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.ThreadContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;

@RunWith(SpringJUnit4ClassRunner.class)
public class CorrelationInterceptorTest {
	
	
	/** The field tokenProvider of type TokenProvider */
	@InjectMocks
	private CorrelationInterceptor correlationInterceptor;
	
	@Mock
	private Environment env;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private SessionDetails sessionDetails;

	private HttpServletRequest httpRequest = mock(HttpServletRequest.class);
	private HttpServletResponse httpResponse = mock(HttpServletResponse.class);

	@Test
	public void testPreHandle() throws Exception {
		 final Object handler="";
		boolean response = correlationInterceptor.preHandle(httpRequest,httpResponse,handler);
		assertTrue(response);
	}
	
	@Test
	public void testAfterCompletion() throws Exception {
		 final Object handler="";
		 final Exception ex = null;
		 ThreadContext.put("correlationId", "correlationId");
		 env.getProperty(BffAdminConstantsUtils.APP_ANALYTICS_ENABLED_KEY);
		 when(env.getProperty("app.analytics.enabled")).thenReturn("true");
		 when(httpRequest.getAttribute("app.analytics.api.startime")).thenReturn(Long.valueOf(129));
		correlationInterceptor.afterCompletion(httpRequest,httpResponse,handler, ex);
		String correlationId = ThreadContext.get("correlationId");
		assertNull(correlationId);
	}
	
	@Test
	public void testAfterCompletion_Exception() throws Exception {
		 final Object handler="";
		 final Exception ex = null;
		 ThreadContext.put("correlationId", "correlationId");
		 env.getProperty(BffAdminConstantsUtils.APP_ANALYTICS_ENABLED_KEY);
		 when(env.getProperty("app.analytics.enabled")).thenReturn("true");
		correlationInterceptor.afterCompletion(httpRequest,httpResponse,handler, ex);
		String correlationId = ThreadContext.get("correlationId");
		assertNull(correlationId);
	}
	
	
	

}
