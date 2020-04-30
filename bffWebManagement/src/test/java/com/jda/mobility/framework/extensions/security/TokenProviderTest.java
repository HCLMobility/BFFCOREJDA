/**
 * 
 */
package com.jda.mobility.framework.extensions.security;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jda.mobility.framework.extensions.config.AppProperties;
import com.jda.mobility.framework.extensions.config.AppProperties.Auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * The class TokenProviderTest.java
 */

@RunWith(SpringJUnit4ClassRunner.class)
public class TokenProviderTest {

	/** The field tokenProvider of type TokenProvider */
	@InjectMocks
	private TokenProvider tokenProvider;

	/** The field appProperties of type AppProperties */
	@Mock
	private AppProperties appProperties;

	/** The field auth of type Auth */
	@Mock
	private Auth auth;

	/** The field authentication of type Authentication */
	@Mock
	private Authentication authentication;

	/** The field userPrincipal of type UserPrincipal */
	@Mock
	private UserPrincipal userPrincipal;
	private final String tokenOpenId = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Impyb2JwNkhwZmFTc2pFMml3OWJ4NThUajlGZkVERllxVFdFQ2w4cDl0RlUifQ.eyJzdWIiOiJBTlRPTiIsImlkIjoiQU5UT04iLCJlbWFpbCI6ImFudG9uQGpkYS5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibm9uY2UiOiJOMC4yMTgzMzU0OTU4MjY5NzM3NjE1NzMyMDk1NzU1NTMiLCJhdF9oYXNoIjoibGc1by1PamlzZURBNnVXUWhPLTE2dyIsImF1ZCI6IndtcyIsImV4cCI6MTU3MzIxMzE4NSwiaWF0IjoxNTczMjA5NTg1LCJpc3MiOiJodHRwOi8vc2ltcGxlLW9pZGMtcHJvdmlkZXIifQ.HL4rshalBqlacICOeZElcRdJxCVw5n8eDMz9MCIMoJWXl7TT3Lw2iuJhpFmyOLA8h1-RXlvmuwEWbOcJ9dXfANBKMERVE_WemjfMoI2h5w1Krv6FQZIYx0rmoETXZXpF4SZSzQsa5Rj6Z5ACMDS9qjYjbUag-qEwnxqyyVszr_zT2JUj_v5uPI8NlGZ4L9GodqiGsQxo-0liigHM8UTyM03KpPGA4S1Chb-hP5afJQOselIm2f8C-bVldzr8UzAlF7l3_j2U7YbreN0SjOrtUn9NM6lql1P2Am-z_DjKvdZozT-oMVUEaLv1Ab1z0UAaKM5SYbZm-Zl7dfrsYpcBjg";
	private final String nonBearerToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Impyb2JwNkhwZmFTc2pFMml3OWJ4NThUajlGZkVERllxVFdFQ2w4cDl0RlUifQ.eyJzdWIiOiJBTlRPTiIsImlkIjoiQU5UT04iLCJlbWFpbCI6ImFudG9uQGpkYS5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibm9uY2UiOiJOMC4yMTgzMzU0OTU4MjY5NzM3NjE1NzMyMDk1NzU1NTMiLCJhdF9oYXNoIjoibGc1by1PamlzZURBNnVXUWhPLTE2dyIsImF1ZCI6IndtcyIsImV4cCI6MTU3MzIxMzE4NSwiaWF0IjoxNTczMjA5NTg1LCJpc3MiOiJodHRwOi8vc2ltcGxlLW9pZGMtcHJvdmlkZXIifQ.HL4rshalBqlacICOeZElcRdJxCVw5n8eDMz9MCIMoJWXl7TT3Lw2iuJhpFmyOLA8h1-RXlvmuwEWbOcJ9dXfANBKMERVE_WemjfMoI2h5w1Krv6FQZIYx0rmoETXZXpF4SZSzQsa5Rj6Z5ACMDS9qjYjbUag-qEwnxqyyVszr_zT2JUj_v5uPI8NlGZ4L9GodqiGsQxo-0liigHM8UTyM03KpPGA4S1Chb-hP5afJQOselIm2f8C-bVldzr8UzAlF7l3_j2U7YbreN0SjOrtUn9NM6lql1P2Am-z_DjKvdZozT-oMVUEaLv1Ab1z0UAaKM5SYbZm-Zl7dfrsYpcBjg";
	/**
	 * Test method for createToken.
	 */
	@Test
	public void testCreateToken() {
		when(userPrincipal.getUserId()).thenReturn("SUPER");
		when(appProperties.getAuth()).thenReturn(auth);
		when(auth.getTokenSecret()).thenReturn("926D96C90030DD58429D2751AC1BDBBC");
		when(auth.getTokenExpirationMsec()).thenReturn(864000000L);
		when(authentication.getPrincipal()).thenReturn(userPrincipal);
		String response = tokenProvider.createToken(authentication);
		Assert.assertTrue(response.length()>0);
	}

	/**
	 * Test method for getUserIdFromToken.
	 */
	@Test
	public void testGetUserIdFromToken() {
		when(appProperties.getAuth()).thenReturn(auth);
		when(auth.getTokenSecret()).thenReturn("926D96C90030DD58429D2751AC1BDBBC");
		String response = tokenProvider.getUserIdFromToken(createToken());
		Assert.assertEquals("SUPER", response);
	}

	/**
	 * Test method for validateToken.
	 */
	@Test
	public void testValidateToken() {
		when(appProperties.getAuth()).thenReturn(auth);
		when(auth.getTokenSecret()).thenReturn("926D96C90030DD58429D2751AC1BDBBC");
		boolean response =tokenProvider.validateToken(createToken());
		Assert.assertTrue(response);
	}

	/**
	 * Test method for validateToken SignatureException.
	 */
	@Test
	public void testValidateTokenSignatureException() {
		when(appProperties.getAuth()).thenReturn(auth);
		when(auth.getTokenSecret()).thenReturn("926D96C90030DD58429D2751AC1BDBBC");
		boolean response=tokenProvider.validateToken(
				"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJTVVBFUiIsImlhdCI6MTU2NjMyMjIyMywiZXhwIjoxNTY3MTg2MjIzfQ.4uw17C8UkLo5JNBw_c4NRkOyAGq-ivWhTG3IzjkN8OmlKbZeq6XRQ_5IHJ4YsyglCLHev7aJS8gTeywMmz0DnA");
		Assert.assertEquals(false, response);
		boolean response1=tokenProvider.validateToken(
				"eJzdWIiOiJTVVBFUiIsImlhdCI6MTU2NjMyMjIyMywiZXhwIjoxNTY3MTg2MjIzfQ.4uw17C8UkLo5JNBw_c4NRkOyAGq-ivWhTG3IzjkN8OmlKbZeq6XRQ_5IHJ4YsyglCLHev7aJS8gTeywMmz0DnA");
		Assert.assertEquals(false, response1);
		boolean response2=tokenProvider.validateToken(
				"");
		Assert.assertEquals(false, response2);
		String token = Jwts.builder()
        .setSubject(userPrincipal.getUserId())
        .setIssuedAt(new Date())
        .setExpiration(new Date(new Date().getTime() + 864000000))
        .setAudience(appProperties.getAuth().getClientId())
        .compact();
		boolean response3=tokenProvider.validateToken(
				token);
		Assert.assertEquals(false, response3);
		String token1 = Jwts.builder().setSubject(userPrincipal.getUserId()).setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + 864000000))
				.setAudience(appProperties.getAuth().getClientId()).signWith(SignatureAlgorithm.HS512, "12345")
				.compact();
		boolean response4 = tokenProvider.validateToken(token1);
		Assert.assertEquals(false, response4);
	}

	/**
	 * @return String
	 */
	private String createToken() {

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + 864000000);

		return Jwts.builder().setSubject("SUPER").setIssuedAt(new Date()).setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getTokenSecret()).compact();
	}
	@Test
	public void getAuthMapFromTokenTest() throws IOException {
		 Map<String, String> map = tokenProvider.getAuthMapFromToken(getJwtFromRequestTest());
		 Assert.assertFalse(map.isEmpty());
		 Map<String, String> map1 = tokenProvider.getAuthMapFromToken(getEmptyJwtFromRequestTest());
		 Assert.assertNull(map1);
		 Map<String, String> map2 = tokenProvider.getAuthMapFromToken(getNonBearerJwtFromRequestTest());
		 Assert.assertNull(map2);
	}

	private String getJwtFromRequestTest() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", tokenOpenId);
		request.setMethod("GET");
		request.getSession(true);
		String jwt = tokenProvider.getJwtFromRequest(request);
		return jwt;
	}
	private String getEmptyJwtFromRequestTest() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "");
		request.setMethod("GET");
		request.getSession(true);
		String jwt = tokenProvider.getJwtFromRequest(request);
		Assert.assertNull(jwt);
		return jwt;
	}
	private String getNonBearerJwtFromRequestTest() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", nonBearerToken);
		request.setMethod("GET");
		request.getSession(true);
		String jwt = tokenProvider.getJwtFromRequest(request);
		Assert.assertNull(jwt);
		return jwt;
	}	
}
