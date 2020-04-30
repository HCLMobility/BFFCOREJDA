package com.jda.mobility.framework.extensions.security;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jda.mobility.framework.extensions.config.AppProperties;
import com.jda.mobility.framework.extensions.config.ProductApiSettings;
import com.jda.mobility.framework.extensions.model.LoginRequest;
import com.jda.mobility.framework.extensions.model.ProductRole;
import com.jda.mobility.framework.extensions.model.ProductRolePermission;
import com.jda.mobility.framework.extensions.util.RequestHelper;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ChannelType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * The class CustomAuthenticationProviderTest.java
 * 
 * @author V.Rama HCL Technologies Ltd.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class CustomAuthenticationProviderTest {
	private CustomAuthenticationProvider customAuthenticationProvider;

	@Mock
	private AppProperties appProperties;
	
	@Mock
	private RestTemplate restTemplate;

	@Mock
	private ProductApiSettings productApis;

	@Before
	public void setUp() {

		customAuthenticationProvider = new CustomAuthenticationProvider(
				restTemplate, appProperties, productApis, new RequestHelper(appProperties));

		when(productApis.rolesUrl("WMS"))
				.thenAnswer(invocation -> UriComponentsBuilder.fromHttpUrl("https://localhost/roles"));
		when(productApis.permissionsUrl("WMS"))
				.thenAnswer(invocation -> UriComponentsBuilder.fromHttpUrl("https://localhost/permissions"));
	}

	/**
	 * Test method for authenticate
	 */
	@Test
	public void testAuthenticate() {
		final List<GrantedAuthority> grantedAuths = new ArrayList<>();
		grantedAuths.add(new SimpleGrantedAuthority(BffAdminConstantsUtils.ROLE_USER));
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUserId("SUPER");
		loginRequest.setDeviceId("TEST");
		loginRequest.setPassword("SUPER");
		loginRequest.setTenant(BffAdminConstantsUtils.WMS);
		loginRequest.setChannel(ChannelType.MOBILE_RENDERER);
		Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest,"SUPER", grantedAuths);
	    SecurityContextHolder.getContext().setAuthentication(authentication);
	    when(appProperties.isOidcEnabled()).thenReturn(true);
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.add(HttpHeaders.AUTHORIZATION, "SUPER");
		HttpEntity<String> httpRequest = new HttpEntity<>(null, headers);
		headers.setContentType(MediaType.APPLICATION_JSON);
	    when(restTemplate.exchange("https://localhost/roles",HttpMethod.GET,
	    		httpRequest, ProductRole.class)).thenReturn(createResponseEntityProductRole());
	    when(restTemplate.exchange("https://localhost/permissions?type=MOBILE",HttpMethod.GET,
	    		httpRequest, ProductRolePermission.class)).thenReturn(createResponseEntityProductRolePerm());
		Authentication response = customAuthenticationProvider.authenticate(authentication);
		assertTrue(response.isAuthenticated());
	}
	private ResponseEntity<ProductRole> createResponseEntityProductRole() {
		ProductRole prpmDto = new ProductRole();
		List<String> roleIds= new ArrayList<>();
		prpmDto.setRoleIds(roleIds);
		return new ResponseEntity<>(prpmDto, HttpStatus.OK);
	}
	
	private ResponseEntity<ProductRolePermission> createResponseEntityProductRolePerm() {
		ProductRolePermission prpmDto = new ProductRolePermission();
		prpmDto.setPermissions(new CopyOnWriteArrayList<>());
		return new ResponseEntity<>(prpmDto, HttpStatus.OK);
	}
	/**
	 * Test method for supports
	 */
	@Test
	public void testSupports() {
		assertFalse(customAuthenticationProvider.supports(Authentication.class));
	}

}
