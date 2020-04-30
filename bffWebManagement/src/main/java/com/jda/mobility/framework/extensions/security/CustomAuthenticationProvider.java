package com.jda.mobility.framework.extensions.security;

import java.util.List;
import java.util.Map;

import com.jda.mobility.framework.extensions.config.AppProperties;
import com.jda.mobility.framework.extensions.config.ProductApiSettings;
import com.jda.mobility.framework.extensions.model.LoginRequest;
import com.jda.mobility.framework.extensions.model.ProductRole;
import com.jda.mobility.framework.extensions.model.ProductRolePermission;
import com.jda.mobility.framework.extensions.util.RequestHelper;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ChannelType;
import com.jda.mobility.framework.extensions.utils.BffUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

/**
 * This class is used for get authentication details.
 * HCL Technologies Ltd.
 */
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider{

	private final AppProperties appProperties;
	private final ProductApiSettings productApi;
	private final RestTemplate restTemplate;
	private final RequestHelper requestHelper;

	public CustomAuthenticationProvider(RestTemplate restTemplate,
										AppProperties appProperties,
										ProductApiSettings productApi,
										RequestHelper requestHelper) {
		this.restTemplate = restTemplate;
		this.appProperties = appProperties;
		this.productApi = productApi;
		this.requestHelper = requestHelper;
	}
	
	/**
	 *@param authentication Authentication
	 */
	@Override
	public Authentication authenticate(Authentication authentication) {
		LoginRequest loginRequest = (LoginRequest)authentication.getPrincipal();
		String password = null;
		if(loginRequest.getPassword() != null) {
			password = new BCryptPasswordEncoder().encode(loginRequest.getPassword());
		}
		
		ResponseEntity<String> responseEntityStr = null;
		String bearerToken =  null;
		if (appProperties.isOidcEnabled()) {
			responseEntityStr = new ResponseEntity<>(HttpStatus.OK);
			bearerToken = (String)authentication.getCredentials();
		} else {
			responseEntityStr = performProductAuth(loginRequest);
		}
		if (responseEntityStr.getStatusCode().equals(HttpStatus.OK)) {
			String prdAuthCookie = responseEntityStr.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
			List<String> rolesList = null;
			List<String> permissionList = null;
			if (loginRequest.getChannel().equals(ChannelType.MOBILE_RENDERER)) {
				rolesList = fetchUserProductRoles( prdAuthCookie, bearerToken, loginRequest.getTenant());
				permissionList = fetchUserProductPermissions( prdAuthCookie, bearerToken,loginRequest.getTenant());
			}
			final UserPrincipal principal = UserPrincipal.builder()
					.userId(loginRequest.getUserId())
					.password(password)
					.channel(loginRequest.getChannel())
					.deviceId(loginRequest.getDeviceId())
					.locale(loginRequest.getLocale())
					.tenant(loginRequest.getTenant())
					.version(loginRequest.getVersion())
					.prdAuthCookie(prdAuthCookie)
					.roleIds(rolesList)
					.permissionIds(permissionList)
					.build();

			return new UsernamePasswordAuthenticationToken(principal, password, null);
		}else {
			return null;
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
	/**
	 * The product authentication API call with WMS
	 * @param loginRequest
	 * @return ResponseEntity<String>
	 */
	private ResponseEntity<String> performProductAuth(LoginRequest loginRequest) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Map<String, String>> httpRequest = new HttpEntity<>(
				Map.of(
						"usr_id", loginRequest.getUserId(),
						"password", loginRequest.getPassword()
				),
				headers
		);

		UriComponents uriComponents = productApi.loginUrl(loginRequest.getTenant()).build();

		return restTemplate.postForEntity(uriComponents.toUriString(), httpRequest, String.class);
	}
	

	/**
	 * Get the product roles from WMS APIs
	 * @param authCookie
	 * @param bearerToken
	 * @param productName
	 * @return List<String>
	 */
	private List<String> fetchUserProductRoles(String authCookie, String bearerToken, String productName) {
		HttpEntity<String> httpRequest = createRequestEntity(authCookie, bearerToken);

		UriComponents uriComponents = productApi.rolesUrl(productName).build();

		ResponseEntity<ProductRole> roleResponseEntity = restTemplate.exchange(uriComponents.toString(), HttpMethod.GET,
				httpRequest, ProductRole.class);

		return BffUtils.getNullable(roleResponseEntity.getBody(), ProductRole::getRoleIds);
	}

	/**
	 * Get the product permissions from WMS APIs
	 * @param authCookie
	 * @param bearerToken
	 * @return List<String>
	 */
	private List<String> fetchUserProductPermissions(String authCookie, String bearerToken, String productName) {
		HttpEntity<String> httpRequest = createRequestEntity(authCookie, bearerToken);

		UriComponents uriComponents = productApi.permissionsUrl(productName)
				.queryParam("type", "MOBILE").build();
		ResponseEntity<ProductRolePermission> permResponseEntity = restTemplate.exchange(uriComponents.toString(),
				HttpMethod.GET, httpRequest, ProductRolePermission.class);

		return BffUtils.getNullable(permResponseEntity.getBody(), ProductRolePermission::getPermissions);
	}

	private HttpEntity<String> createRequestEntity(String authCookie, String bearerToken) {
		return new HttpEntity<>(null, requestHelper.initHeadersWith(authCookie, bearerToken));
	}
}
