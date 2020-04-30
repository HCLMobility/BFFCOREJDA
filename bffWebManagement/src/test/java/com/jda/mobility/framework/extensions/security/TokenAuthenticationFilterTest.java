/**
 * 
 */
package com.jda.mobility.framework.extensions.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.jda.iam.client.TokenValidator;
import com.jda.iam.config.ClientConfiguration;
import com.jda.mobility.framework.extensions.config.AppProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * The class TokenAuthenticationFilterTest.java
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class TokenAuthenticationFilterTest {
	@Mock
	private SpringSessionBackedSessionRegistry<Session> sessionRegistry;
	@Mock
	private FilterChain filterChain;
	@Spy
	private TokenProvider tokenProvider =  new TokenProvider(createTestAppProperties());
	@Mock
	private ClientConfiguration iamClientConfig;
	@Mock
	private TokenValidator tokenValidator;
	@Mock
	private BffAuthorizationValidator bffAuthValidator;
	@InjectMocks
	private TokenAuthenticationFilter tokenAuthenticationFilter= new TokenAuthenticationFilter(tokenValidator,iamClientConfig);
	@InjectMocks
	private TokenAuthenticationFilter tokenBffAuthenticationFilter = new TokenAuthenticationFilter();

	@Mock
	private FindByIndexNameSessionRepository<Session> sessionRepository;

    public BffAuthorizationValidator bffAuthValidator() {    	
    	return new BffAuthorizationValidator(tokenValidator, iamClientConfig);
    }

	private final String token = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTVVBFUiIsImlhdCI6MTU2NjMyMjIyMywiZXhwIjoxNTY3MTg2MjIzfQ.4uw17C8UkLo5JNBw_c4NRkOyAGq-ivWhTG3IzjkN8OmlKbZeq6XRQ_5IHJ4YsyglCLHev7aJS8gTeywMmz0DnA";
	private final String tokenOpenId = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Impyb2JwNkhwZmFTc2pFMml3OWJ4NThUajlGZkVERllxVFdFQ2w4cDl0RlUifQ.eyJzdWIiOiJBTlRPTiIsImlkIjoiQU5UT04iLCJlbWFpbCI6ImFudG9uQGpkYS5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibm9uY2UiOiJOMC4yMTgzMzU0OTU4MjY5NzM3NjE1NzMyMDk1NzU1NTMiLCJhdF9oYXNoIjoibGc1by1PamlzZURBNnVXUWhPLTE2dyIsImF1ZCI6IndtcyIsImV4cCI6MTU3MzIxMzE4NSwiaWF0IjoxNTczMjA5NTg1LCJpc3MiOiJodHRwOi8vc2ltcGxlLW9pZGMtcHJvdmlkZXIifQ.HL4rshalBqlacICOeZElcRdJxCVw5n8eDMz9MCIMoJWXl7TT3Lw2iuJhpFmyOLA8h1-RXlvmuwEWbOcJ9dXfANBKMERVE_WemjfMoI2h5w1Krv6FQZIYx0rmoETXZXpF4SZSzQsa5Rj6Z5ACMDS9qjYjbUag-qEwnxqyyVszr_zT2JUj_v5uPI8NlGZ4L9GodqiGsQxo-0liigHM8UTyM03KpPGA4S1Chb-hP5afJQOselIm2f8C-bVldzr8UzAlF7l3_j2U7YbreN0SjOrtUn9NM6lql1P2Am-z_DjKvdZozT-oMVUEaLv1Ab1z0UAaKM5SYbZm-Zl7dfrsYpcBjg";

	/**
	 * Test method for testDoFilter
	 * @throws ServletException
	 * @throws IOException
	 */
	@Test
	public void testDoFilter()
			throws ServletException, IOException{
		SessionInformation info = new SessionInformation("ANTON", "sessionId", new Date());		
	
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", tokenOpenId);
		request.setMethod("GET");
		request.getSession(true);
		MockHttpServletResponse response = new MockHttpServletResponse();
		when(sessionRegistry.getSessionInformation(request.getSession().getId())).thenReturn(info);
		when(bffAuthValidator.authProcessor(request, response)).thenReturn(true);
		tokenAuthenticationFilter.doFilter(request, response, filterChain);
		ReflectionTestUtils.setField(tokenBffAuthenticationFilter, "bffAuthValidator", null);
		MockHttpServletRequest request1 = new MockHttpServletRequest();
		request1.addHeader("Authorization", token);
		request1.setMethod("GET");
		request1.getSession(true);	
		tokenBffAuthenticationFilter.doFilter(request1, response, filterChain);
		MockHttpServletRequest request2 = new MockHttpServletRequest();
		request2.addHeader("Authorization", "Bearer ".concat(createTestBffToken(createTestAppProperties())));
		request2.setMethod("GET");
		request2.getSession(true);
		tokenBffAuthenticationFilter.doFilter(request2, response, filterChain);
		
		MockHttpServletRequest request3 = new MockHttpServletRequest();
		request3.addHeader("Authorization", "Bearer ");
		request3.setMethod("GET");
		request3.getSession(true);		
		tokenBffAuthenticationFilter.doFilter(request3, response, filterChain);
		
		MockHttpServletRequest request4 = new MockHttpServletRequest();
		request4.addHeader("Authorization", "Bearer ".concat(createTestBffToken(createTestAppProperties())));
		request4.setMethod("GET");
		request4.setSession(null);
		tokenBffAuthenticationFilter.doFilter(request4, response, filterChain);
	
		MockHttpServletRequest request5 = new MockHttpServletRequest();
		request5.addHeader("Authorization", "Bearer ".concat(createTestBffToken(createTestAppProperties())));
		request5.setMethod("GET");
		request5.getSession(false);
		when(sessionRegistry.getSessionInformation(request5.getSession().getId())).thenReturn(null);
		tokenBffAuthenticationFilter.doFilter(request5, response, filterChain);
		info.expireNow();
		when(sessionRegistry.getSessionInformation(request5.getSession().getId())).thenReturn(info);
		tokenBffAuthenticationFilter.doFilter(request5, response, filterChain);
		assertEquals("en_US", response.getLocale().toString());
	
	}
	
	@Test
	public void testDoFilterException()throws ServletException, IOException {
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer u9900");
		request.setMethod("GET");
		request.getSession(true);	
		String jwt = tokenProvider.getJwtFromRequest(request);
		MockHttpServletResponse response = new MockHttpServletResponse();
		doNothing().when(filterChain).doFilter(request, response);
		when(tokenProvider.getJwtFromRequest(request)).thenReturn(jwt);
		when(bffAuthValidator.authProcessor(request, response)).thenReturn(true);
		//tokenAuthenticationFilter.doFilter(request, response, filterChain);
		
		when(bffAuthValidator.authProcessor(request, response)).thenReturn(false);
		tokenAuthenticationFilter.doFilter(request, response, filterChain);
		assertEquals("en_US", response.getLocale().toString());
	}
	private String createTestBffToken(AppProperties appProperties) {
		Date now = new Date();
        Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec());
        return Jwts.builder()
                .setSubject("ANTON")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .setAudience(appProperties.getAuth().getClientId())
                .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getTokenSecret())
                .compact();
	}
	private final AppProperties createTestAppProperties() {
		AppProperties appProperties = new AppProperties();
    	appProperties.getAuth().setClientId("bff");
    	appProperties.getAuth().setTokenExpirationMsec(864000000);
    	appProperties.getAuth().setTokenSecret("926D96C90030DD58429D2751AC1BDBBC");
    	return appProperties;
	}
}
