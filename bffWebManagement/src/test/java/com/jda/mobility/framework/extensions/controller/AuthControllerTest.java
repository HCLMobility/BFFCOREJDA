package com.jda.mobility.framework.extensions.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.dto.RoleMasterDto;
import com.jda.mobility.framework.extensions.dto.TranslationDto;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.entity.ProductTenantConfig;
import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.entity.VersionMapping;
import com.jda.mobility.framework.extensions.entity.VersionMaster;
import com.jda.mobility.framework.extensions.model.AppSetting;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.DetailResponse;
import com.jda.mobility.framework.extensions.model.LoginRequest;
import com.jda.mobility.framework.extensions.model.ProductRole;
import com.jda.mobility.framework.extensions.model.ProductRolePermission;
import com.jda.mobility.framework.extensions.repository.AppConfigMasterRepository;
import com.jda.mobility.framework.extensions.repository.MasterUserRepository;
import com.jda.mobility.framework.extensions.repository.ProductMasterRepository;
import com.jda.mobility.framework.extensions.repository.ProductTenantConfigRepository;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;
import com.jda.mobility.framework.extensions.repository.VersionMasterRepository;
import com.jda.mobility.framework.extensions.security.TokenProvider;
import com.jda.mobility.framework.extensions.security.UserPrincipal;
import com.jda.mobility.framework.extensions.service.TranslationService;
import com.jda.mobility.framework.extensions.service.UserService;
import com.jda.mobility.framework.extensions.service.impl.AppConfigServiceImpl;
import com.jda.mobility.framework.extensions.util.ConstantsUtils;
import com.jda.mobility.framework.extensions.util.SessionUtils;
import com.jda.mobility.framework.extensions.util.TenantSetting;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.AppCfgRequestType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ChannelType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 * This test classes unit tests the Admin UI and MobileRenderer authentication
 * success and failure scenarios including the roles and permissions returned
 * for each scenario
 *
 * TODO Fix these tests. All the restTemplate logic is completely unnecessary.
 *
 * @author HCL Technologies Ltd.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class AuthControllerTest {

	/** The field authController of type AuthController */
	@InjectMocks
	private AuthController authController;

	/** The field authenticationManager of type AuthenticationManager */
	@Mock
	private AuthenticationManager authenticationManager;

	/** The field tokenProvider of type TokenProvider */
	@Mock
	private TokenProvider tokenProvider;

	/** The field authentication of type Authentication */
	@Mock
	private Authentication authentication;

	/** The field restTemplate of type RestTemplate */
	@Mock
	private RestTemplate restTemplate;

	@Mock
	private ResourceBundleRepository rbRepository;

	@Mock
	private MasterUserRepository masterUserRepository;

	@Mock
	private UserService userService;

	@SuppressWarnings("rawtypes")
	@Mock
	private FindByIndexNameSessionRepository sessionRepository;

	@Mock
	private AppConfigServiceImpl appConfigServiceImpl;
	@SuppressWarnings(ConstantsUtils.RAWTYPE)
	@Mock
	private SessionUtils sessionUtils;
	
	@Spy
	private BffResponse bffResponse = new BffResponse();
	
	@Spy
	private SessionDetails sessionDetails =  new SessionDetails();
	
	@Mock
	private VersionMasterRepository versionMasterRepository;
	@Mock
	private TenantSetting tenantSetting;
	@Mock
	private ProductTenantConfigRepository prodTenantConfigRepo;
	@Mock
	private AppConfigMasterRepository appConfigRepository;
	@Mock
	private TranslationService translationService;
	@Mock
	private ProductMasterRepository productMasterRepo;

	@Before
    public void setUp(){
		List<ResourceBundle> resourceBundleList = new ArrayList<>();
		ResourceBundle resourceBundle = new ResourceBundle();
		resourceBundle.setLocale(BffAdminConstantsUtils.LOCALE);
		resourceBundle.setRbkey(BffAdminConstantsUtils.RB_TEST_KEY);
		resourceBundle.setRbvalue(BffAdminConstantsUtils.RB_TEST_VAL);
		resourceBundle.setCreatedBy(BffAdminConstantsUtils.SUPER);
		resourceBundle.setCreationDate(new Date());
		resourceBundle.setType("INTERNAL");
		resourceBundleList.add(resourceBundle);
		
		ResourceBundleRepository resourceBundleRepo = mock(ResourceBundleRepository.class);
		when(resourceBundleRepo.findByLocaleAndRbkey(Mockito.any(), Mockito.any())).thenReturn(resourceBundleList);
		ReflectionTestUtils.setField(bffResponse, "resourceBundleRepo", resourceBundleRepo);
		
		sessionDetails.setSessionId("df44ec7f-2dcc-4d76-8906-c615182fc851");
		sessionDetails.setLocale(BffAdminConstantsUtils.LOCALE);
		sessionDetails.setPrincipalName(BffAdminConstantsUtils.SUPER);
		sessionDetails.setVersion("1");
		sessionDetails.setChannel("MOBILE_RENDERER");
		sessionDetails.setTenant("SOURCE_A");
		sessionDetails.setPrdAuthCookie("COOKIE");
		ReflectionTestUtils.setField(bffResponse, "sessionDetails", sessionDetails);
		
		
		
    }
	
	/*
	 * @Mock private BffCoreResponse bffCoreResponse;
	 */

	private HttpServletRequest httpRequest = mock(HttpServletRequest.class);
	private HttpServletResponse httpResponse = mock(HttpServletResponse.class);
	private javax.servlet.http.HttpSession session = mock(javax.servlet.http.HttpSession.class);

	/**
	 * Test method for authenticateUser success scenario for Admin UI authentication
	 */

	@Test
	public void testAuthenticateUserAdminUIAuthSuccess() throws IOException{
		final List<GrantedAuthority> grantedAuths = new ArrayList<>();
		grantedAuths.add(new SimpleGrantedAuthority(BffAdminConstantsUtils.ROLE_USER));
		List<String> permissionIds= new ArrayList<>();
		String permision="Test";
		permissionIds.add(permision);
		UserPrincipal principal = UserPrincipal.builder()
				.userId("SUPER")
				.password("SUPER")
				.channel(ChannelType.ADMIN_UI)
				.deviceId("")
				.locale(BffAdminConstantsUtils.LOCALE)
				.tenant("SOURCE_A")				
				.version("1")
				.prdAuthCookie("COOKIE")
				.permissionIds(permissionIds)
				.build();
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal,"SUPER", grantedAuths);
	    SecurityContextHolder.getContext().setAuthentication(authentication);
	    Map<String, Map<String, String>> tenantConfigMap = new HashMap<>();
	    tenantConfigMap.put("SOURCE_A", new HashMap<String, String>());
	   tenantSetting.setTenantConfigMap(tenantConfigMap);
	   when(tenantSetting.getTenantConfigMap()).thenReturn(tenantConfigMap);
	   when(sessionUtils.fetchUserOpenSessionCount(Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(1);
	   when(httpRequest.getSession()).thenReturn(session);

		when(httpRequest.getSession(true)).thenReturn(session);
		when(sessionDetails.getLocale()).thenReturn("en");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		BffCoreResponse bffCoreResponse = new BffCoreResponse();
		bffCoreResponse.setHttpStatusCode(StatusCode.BADREQUEST.getValue());
		DetailResponse<String> details = new DetailResponse<>();
		details.setData("");
		bffCoreResponse.setDetails(details);

		List<VersionMaster> versionMasterlist =new ArrayList<>();
		VersionMaster versionMaster= new VersionMaster();
		versionMaster.setVersion("1.0");
		versionMaster.setChannel("BFFCORE");
		versionMaster.setActive(true);
		List<VersionMapping>  VersionMappinglist= new ArrayList<>();
		VersionMapping versionMapping= new VersionMapping();
		VersionMaster masterForMobile = new VersionMaster();
		masterForMobile.setVersion("1.0");
		masterForMobile.setChannel("ADMIN_UI");
		versionMapping.setMappedAppVersionMaster(masterForMobile);
		versionMapping.setUid(UUID.randomUUID());
		VersionMappinglist.add(versionMapping);
		versionMaster.setBffCoreVersionMappingList(VersionMappinglist);
		versionMapping.setBffCoreVersionMaster(versionMaster);
		versionMasterlist.add(versionMaster);
		AppConfigMaster configMaster = new AppConfigMaster();
		configMaster.setConfigType(AppCfgRequestType.APPLICATION.getType());
		configMaster.setConfigName("Test");
		configMaster.setUid(UUID.randomUUID());
		when(appConfigRepository.findByConfigNameAndConfigType(Mockito.any(), Mockito.any())).thenReturn(configMaster);
		when(versionMasterRepository.findByActiveAndChannel(true, "BFFCORE")).thenReturn(versionMasterlist);
		when(productMasterRepo.countByName(Mockito.any())).thenReturn(1);
		when(versionMasterRepository.countOfCompatibleVersions(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(1);
		when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
		when(tokenProvider.createToken(authentication)).thenReturn("success");
		LoginRequest loginreq = createLoginRequest(ChannelType.ADMIN_UI.getType());
		loginreq.setChannel(ChannelType.ADMIN_UI);
		String authPayload = "{\"usr_id\":" + "\"" + loginreq.getUserId() + "\"" + ",\"password\":" + "\""
				+ loginreq.getPassword() + "\"}";
		when(restTemplate.postForEntity("http://10.144.196.194:4500/ws/auth/login",
				new HttpEntity<>(authPayload, headers), String.class)).thenReturn(createResponseEntity(HttpStatus.OK));
		DetailResponse<RoleMasterDto> detailResponse = new DetailResponse<>();
		detailResponse.setData(new RoleMasterDto(UUID.randomUUID(), "JDA Service", 1, null, null,false));

		when(masterUserRepository.countByUserId(loginreq.getUserId())).thenReturn(1);

		// Resource bundle mocks
		List<ResourceBundle> rbList = new ArrayList<>();
		ResourceBundle rb = ResourceBundle.builder()
				.locale("en")
				.rbkey(BffResponseCode.LOGIN_SUCCESS_CODE_AUTHENTICATION.getKey())
				.rbvalue("Login authentication success.")
				.type("Internal")
				.uid(UUID.randomUUID())
				.build();
		rbList.add(rb);
		when(rbRepository.findByLocaleAndRbkey("en", BffResponseCode.LOGIN_SUCCESS_CODE_AUTHENTICATION.getKey()))
				.thenReturn(rbList);
		rbList = new ArrayList<>();
		ResourceBundle rb1 = ResourceBundle.builder()
				.locale("en")
				.rbkey(BffResponseCode.LOGIN_USER_CODE_AUTHENTICATION.getKey())
				.rbvalue("User logged in is authenticated")
				.type("Internal")
				.uid(UUID.randomUUID())
				.build();
		rbList.add(rb1);
		when(rbRepository.findByLocaleAndRbkey("en", BffResponseCode.LOGIN_SUCCESS_CODE_AUTHENTICATION.getKey()))
				.thenReturn(rbList);
		rbList = new ArrayList<>();
		ResourceBundle rb2 = ResourceBundle.builder()
				.locale("en")
				.rbkey(BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_ROLES_PARTICULAR_USER.getKey())
				.rbvalue("User Layer Retreival Successful")
				.type("Internal")
				.uid(UUID.randomUUID())
				.build();
		rbList.add(rb2);
		when(rbRepository.findByLocaleAndRbkey("en",
				BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_ROLES_PARTICULAR_USER.getKey())).thenReturn(rbList);
		rbList = new ArrayList<>();
		ResourceBundle rb3 = ResourceBundle.builder()
				.locale("en")
				.rbkey(BffResponseCode.ACCESS_SERVICE_USER_CODE_ROLES_PARTICULAR_USER.getKey())
				.rbvalue("User Layer Retreival Successful")
				.type("Internal")
				.uid(UUID.randomUUID())
				.build();
		rbList.add(rb3);
		when(rbRepository.findByLocaleAndRbkey("en",
				BffResponseCode.ACCESS_SERVICE_USER_CODE_ROLES_PARTICULAR_USER.getKey())).thenReturn(rbList);
		when(userService.getRolesForUser(Mockito.any())).thenReturn(bffCoreResponse);
		when(httpRequest.getScheme()).thenReturn("mock");
		when(httpRequest.getServerPort()).thenReturn(8080);
		when(httpRequest.getServerName()).thenReturn("localhost");
		when(httpRequest.getRequestURI()).thenReturn("/api");
		when(httpRequest.getQueryString()).thenReturn("mock");
		Map<String, String> authMap = new HashMap<>();
		authMap.put("aud", "name");
		String token="name";
		when(tokenProvider.getJwtFromRequest(Mockito.any())).thenReturn(token);
		when(tokenProvider.getAuthMapFromToken(Mockito.any())).thenReturn(authMap);
		ResponseEntity<BffCoreResponse> response = authController.authenticateUser(loginreq, httpRequest, httpResponse);
		assertEquals(StatusCode.OK.getValue(), response.getBody().getHttpStatusCode());
		assertEquals(BffResponseCode.LOGIN_SUCCESS_CODE_AUTHENTICATION.getCode(), response.getBody().getCode());
	}

	@Test
	public void testAuthenticateUserMobileRendererAuthSuccess() throws IOException{
		final List<GrantedAuthority> grantedAuths = new ArrayList<>();
		grantedAuths.add(new SimpleGrantedAuthority(BffAdminConstantsUtils.ROLE_USER));
		List<String> permissionIds= new ArrayList<>();
		String permision="Test";
		permissionIds.add(permision);
		UserPrincipal principal = UserPrincipal.builder()
				.userId("SUPER")
				.password("SUPER")
				.channel(ChannelType.MOBILE_RENDERER)
				.deviceId("")
				.locale(BffAdminConstantsUtils.LOCALE)
				.tenant("SOURCE_A")				
				.version("1")
				.prdAuthCookie("COOKIE")
				.permissionIds(permissionIds)
				.build();
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal,"SUPER", grantedAuths);
	    SecurityContextHolder.getContext().setAuthentication(authentication); 
	    Map<String, Map<String, String>> tenantConfigMap = new HashMap<>();
	    tenantConfigMap.put("SOURCE_A", new HashMap<String, String>());
	   tenantSetting.setTenantConfigMap(tenantConfigMap);
	  // when(sessionUtils.fetchUserOpenSessionCount(Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(1);
	   when(httpRequest.getSession()).thenReturn(session);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		BffCoreResponse bffCoreResponse = new BffCoreResponse();
		bffCoreResponse.setHttpStatusCode(StatusCode.OK.getValue());
		List<VersionMaster> versionMasterlist =new ArrayList<>();
		VersionMaster versionMaster= new VersionMaster();
		versionMaster.setVersion("1.0");
		versionMaster.setChannel("BFFCORE");
		versionMaster.setActive(true);
		List<VersionMapping>  VersionMappinglist= new ArrayList<>();
		VersionMapping versionMapping= new VersionMapping();
		VersionMaster masterForMobile = new VersionMaster();
		masterForMobile.setVersion("1.0");
		masterForMobile.setChannel("MOBILE_RENDERER");
		versionMapping.setMappedAppVersionMaster(masterForMobile);
		versionMapping.setUid(UUID.randomUUID());
		VersionMappinglist.add(versionMapping);
		versionMaster.setBffCoreVersionMappingList(VersionMappinglist);
		versionMapping.setBffCoreVersionMaster(versionMaster);
		versionMasterlist.add(versionMaster);
		when(httpRequest.getSession(true)).thenReturn(session);
		when(httpRequest.getSession()).thenReturn(session);
		AppConfigMaster configMaster = new AppConfigMaster();
		configMaster.setConfigType(AppCfgRequestType.APPLICATION.getType());
		configMaster.setConfigName("Test");
		configMaster.setUid(UUID.randomUUID());
		List<TranslationDto> resBundleEntries= new ArrayList<>();
		resBundleEntries.add(new TranslationDto(UUID.randomUUID().toString(), "en_US", "key1", "value1", "MOBILE"));
		when(productMasterRepo.countByName(Mockito.any())).thenReturn(1);
		when(appConfigRepository.findByConfigNameAndConfigType(Mockito.any(), Mockito.any())).thenReturn(configMaster);
		when(versionMasterRepository.findByActiveAndChannel(true, "BFFCORE")).thenReturn(versionMasterlist);
		when(versionMasterRepository.countOfCompatibleVersions(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(1);
		when(translationService.getlocalizedResBundleEntries (Mockito.any())).thenReturn(resBundleEntries);
		when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
		when(tokenProvider.createToken(authentication)).thenReturn("success");
		LoginRequest loginreq = createLoginRequest(ChannelType.MOBILE_RENDERER.getType());
		loginreq.setChannel(ChannelType.MOBILE_RENDERER);
		loginreq.setVersion("1.0");
		loginreq.setLocale("en");
		String authPayload = "{\"usr_id\":" + "\"" + loginreq.getUserId() + "\"" + ",\"password\":" + "\""
				+ loginreq.getPassword() + "\"}";

		when(restTemplate.postForEntity("http://10.144.196.194:4500/ws/auth/login",
				new HttpEntity<>(authPayload, headers), String.class)).thenReturn(createResponseEntity(HttpStatus.OK));

		headers.add("Cookie", null);
		when(restTemplate.exchange("http://10.144.196.194:4500/api/user/v1beta/currentUser/roles", HttpMethod.GET,
				new HttpEntity<>(null, headers), ProductRole.class))
						.thenReturn(createResponseEntityProductRole(HttpStatus.OK));
		when(restTemplate.exchange("http://10.144.196.194:4500/api/user/v1beta/currentUser/permissions", HttpMethod.GET,
				new HttpEntity<>(null, headers), ProductRolePermission.class))
						.thenReturn(createResponseEntityProductRolePerm(HttpStatus.OK));
		ProductRole productRole = new ProductRole();
		ResponseEntity<ProductRole> responseEntity = new ResponseEntity<>(productRole, HttpStatus.OK);
		when(restTemplate.exchange("http://10.144.196.194:4500/api/user/v1beta/roles", HttpMethod.GET,new HttpEntity<>(null, headers),
				ProductRole.class)).thenReturn(responseEntity);
		
		ProductRolePermission productRolePermission = new ProductRolePermission();
		ResponseEntity<ProductRolePermission> roleResponseEntity = new ResponseEntity<>(productRolePermission, HttpStatus.OK);
		when(restTemplate.exchange("http://10.144.196.194:4500/api/user/v1betanull?type=MOBILE", HttpMethod.GET,new HttpEntity<>(null, headers),
				ProductRolePermission.class)).thenReturn(roleResponseEntity);
		when(httpRequest.getScheme()).thenReturn("mock");
		when(httpRequest.getServerPort()).thenReturn(8080);
		when(httpRequest.getServerName()).thenReturn("localhost");
		when(httpRequest.getRequestURI()).thenReturn("/api");
		when(httpRequest.getQueryString()).thenReturn("mock");
		Map<String, String> authMap = new HashMap<>();
		authMap.put("aud", "wms");
		when(tokenProvider.getAuthMapFromToken(Mockito.any())).thenReturn(authMap);
		
		ResponseEntity<BffCoreResponse> response = authController.authenticateUser(loginreq, httpRequest, httpResponse);
		assertEquals(StatusCode.OK.getValue(), response.getBody().getHttpStatusCode());
		assertEquals(BffResponseCode.LOGIN_SUCCESS_CODE_AUTHENTICATION.getCode(), response.getBody().getCode());
	}
	
	@Test
	public void testAuthenticateUserFetchMobileAccessDataRestClientException() throws IOException{

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		BffCoreResponse bffCoreResponse = new BffCoreResponse();
		bffCoreResponse.setHttpStatusCode(StatusCode.OK.getValue());
		
		List<VersionMaster> versionMasterlist =new ArrayList<>();
		VersionMaster versionMaster= new VersionMaster();
		versionMaster.setVersion("1.0");
		versionMaster.setChannel("BFFCORE");
		versionMaster.setActive(true);
		List<VersionMapping>  VersionMappinglist= new ArrayList<>();
		VersionMapping versionMapping= new VersionMapping();
		VersionMaster masterForMobile = new VersionMaster();
		masterForMobile.setVersion("1.0");
		masterForMobile.setChannel("MOBILE_RENDERER");
		versionMapping.setMappedAppVersionMaster(masterForMobile);
		versionMapping.setUid(UUID.randomUUID());
		VersionMappinglist.add(versionMapping);
		versionMaster.setBffCoreVersionMappingList(VersionMappinglist);
		versionMapping.setBffCoreVersionMaster(versionMaster);
		versionMasterlist.add(versionMaster);
		when(productMasterRepo.countByName(Mockito.any())).thenReturn(1);
		when(versionMasterRepository.findByActiveAndChannel(true, "BFFCORE")).thenReturn(versionMasterlist);

		when(httpRequest.getSession(true)).thenReturn(session);
		when(httpRequest.getSession(false)).thenReturn(session);
		when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
		when(tokenProvider.createToken(authentication)).thenReturn("success");
		LoginRequest loginreq = createLoginRequest(ChannelType.MOBILE_RENDERER.getType());
		loginreq.setChannel(ChannelType.MOBILE_RENDERER);
		String authPayload = "{\"usr_id\":" + "\"" + loginreq.getUserId() + "\"" + ",\"password\":" + "\""
				+ loginreq.getPassword() + "\"}";

		when(restTemplate.postForEntity("http://10.144.196.194:4500/ws/auth/login",
				new HttpEntity<>(authPayload, headers), String.class)).thenReturn(createResponseEntity(HttpStatus.OK));

		headers.add("Cookie", null);
		when(restTemplate.exchange("http://10.144.196.194:4500/api/user/v1beta/currentUser/roles", HttpMethod.GET,
				new HttpEntity<>(null, headers), ProductRole.class))
						.thenReturn(createResponseEntityProductRole(HttpStatus.OK));
		when(restTemplate.exchange("http://10.144.196.194:4500/api/user/v1beta/currentUser/permissions", HttpMethod.GET,
				new HttpEntity<>(null, headers), ProductRolePermission.class))
						.thenReturn(createResponseEntityProductRolePerm(HttpStatus.OK));
		when(restTemplate.exchange("http://10.144.196.194:4500/api/user/v1beta/roles", HttpMethod.GET,new HttpEntity<>(null, headers), 
				ProductRole.class)).thenThrow(new RestClientException(""));
		
		when(httpRequest.getScheme()).thenReturn("mock");
		when(httpRequest.getServerPort()).thenReturn(8080);
		when(httpRequest.getServerName()).thenReturn("localhost");
		when(httpRequest.getRequestURI()).thenReturn("/api");
		when(httpRequest.getQueryString()).thenReturn("mock");
		Map<String, String> authMap = new HashMap<>();
		authMap.put("aud", "wms");
		when(tokenProvider.getAuthMapFromToken(Mockito.any())).thenReturn(authMap);
		
		ResponseEntity<BffCoreResponse> response = authController.authenticateUser(loginreq, httpRequest, httpResponse);
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getBody().getHttpStatusCode());
		assertEquals(BffResponseCode.ERR_LOGIN_UI_API_NOT_COMPATIBLE.getCode(), response.getBody().getCode());
	}
	@Test
	public void testAuthenticateUserFetchMobileAccessException() throws IOException{
		
		List<VersionMaster> versionMasterlist =new ArrayList<>();
		VersionMaster versionMaster= new VersionMaster();
		versionMaster.setVersion("1.0");
		versionMaster.setChannel("BFFCORE");
		versionMaster.setActive(true);
		List<VersionMapping>  VersionMappinglist= new ArrayList<>();
		VersionMapping versionMapping= new VersionMapping();
		VersionMaster masterForMobile = new VersionMaster();
		masterForMobile.setVersion("1.0");
		masterForMobile.setChannel("MOBILE_RENDERER");
		versionMapping.setMappedAppVersionMaster(masterForMobile);
		versionMapping.setUid(UUID.randomUUID());
		VersionMappinglist.add(versionMapping);
		versionMaster.setBffCoreVersionMappingList(VersionMappinglist);
		versionMapping.setBffCoreVersionMaster(versionMaster);
		versionMasterlist.add(versionMaster);
		when(productMasterRepo.countByName(Mockito.any())).thenReturn(1);
		when(versionMasterRepository.countOfCompatibleVersions(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(1);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		BffCoreResponse bffCoreResponse = new BffCoreResponse();
		bffCoreResponse.setHttpStatusCode(StatusCode.OK.getValue());
		when(httpRequest.getSession(true)).thenReturn(session);
		when(httpRequest.getSession(false)).thenReturn(session);
		when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
		when(tokenProvider.createToken(authentication)).thenReturn("success");
		LoginRequest loginreq = createLoginRequest(ChannelType.MOBILE_RENDERER.getType());
		loginreq.setChannel(null);
		String authPayload = "{\"usr_id\":" + "\"" + loginreq.getUserId() + "\"" + ",\"password\":" + "\""
				+ loginreq.getPassword() + "\"}";

		when(restTemplate.postForEntity("http://10.144.196.194:4500/ws/auth/login",
				new HttpEntity<>(authPayload, headers), String.class)).thenReturn(createResponseEntity(HttpStatus.OK));

		headers.add("Cookie", null);
		when(restTemplate.exchange("http://10.144.196.194:4500/api/user/v1beta/currentUser/roles", HttpMethod.GET,
				new HttpEntity<>(null, headers), ProductRole.class))
						.thenReturn(createResponseEntityProductRole(HttpStatus.OK));
		when(restTemplate.exchange("http://10.144.196.194:4500/api/user/v1beta/currentUser/permissions", HttpMethod.GET,
				new HttpEntity<>(null, headers), ProductRolePermission.class))
						.thenReturn(createResponseEntityProductRolePerm(HttpStatus.OK));
		when(httpRequest.getScheme()).thenReturn("mock");
		when(httpRequest.getServerPort()).thenReturn(8080);
		when(httpRequest.getServerName()).thenReturn("localhost");
		when(httpRequest.getRequestURI()).thenReturn("/api");
		when(httpRequest.getQueryString()).thenReturn("mock");
		Map<String, String> authMap = new HashMap<>();
		authMap.put("aud", "wms");
		when(tokenProvider.getAuthMapFromToken(Mockito.any())).thenReturn(authMap);
		
		ResponseEntity<BffCoreResponse> response = authController.authenticateUser(loginreq, httpRequest, httpResponse);
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getBody().getHttpStatusCode());
		assertEquals(BffResponseCode.ERR_LOGIN_API_AUTH_EXCEPTION.getCode(), response.getBody().getCode());
	}
	@Test
	public void testAuthenticateUserMobileRendererAuthSuccessException() {
		when(httpRequest.getSession(false)).thenReturn(session);
		when(productMasterRepo.countByName(Mockito.any())).thenReturn(1);
		when(versionMasterRepository.countOfCompatibleVersions(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(1);
		LoginRequest loginreq = createLoginRequest(ChannelType.MOBILE_RENDERER.getType());
		loginreq.setChannel(null);
		ResponseEntity<BffCoreResponse> response = authController.authenticateUser(loginreq, httpRequest, httpResponse);
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getBody().getHttpStatusCode());
		assertEquals(BffResponseCode.ERR_LOGIN_API_AUTH_EXCEPTION.getCode(), response.getBody().getCode());
		
	}

	@Test
	public void testAuthenticateUserMobileRendererAuthSuccessRestClientException() {
		when(httpRequest.getSession(false)).thenReturn(session);
		when(productMasterRepo.countByName(Mockito.any())).thenReturn(1);
		when(versionMasterRepository.countOfCompatibleVersions(Mockito.any(),Mockito.any(),Mockito.any())).thenThrow(new RestClientException(""));
		LoginRequest loginreq = createLoginRequest(ChannelType.MOBILE_RENDERER.getType());
		loginreq.setChannel(ChannelType.MOBILE_RENDERER);
		ResponseEntity<BffCoreResponse> response = authController.authenticateUser(loginreq, httpRequest, httpResponse);
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getBody().getHttpStatusCode());
		assertEquals(BffResponseCode.ERR_LOGIN_API_REST_CLIENT_EXCEPTION.getCode(), response.getBody().getCode());
	}

	/**
	 * Test method for authenticateUser authentication failure scenario for Admin UI
	 * authentication
	 */
	@Test
	public void testAuthenticateUserAuthenticationFailure() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		when(httpRequest.getSession(false)).thenReturn(session);
		when(httpRequest.getSession(true)).thenReturn(session);
		when(productMasterRepo.countByName(Mockito.any())).thenReturn(1);
		when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
		when(tokenProvider.createToken(authentication)).thenReturn("success");
		LoginRequest loginreq = createLoginRequest("ADMIN_UI");
		loginreq.setChannel(ChannelType.ADMIN_UI);
		String authPayload = "{\"usr_id\":" + "\"" + loginreq.getUserId() + "\"" + ",\"password\":" + "\""
				+ loginreq.getPassword() + "\"}";

		when(restTemplate.postForEntity("http://10.144.196.194:4500/ws/auth/login",
				new HttpEntity<>(authPayload, headers), String.class))
						.thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
		ResponseEntity<BffCoreResponse> response = authController.authenticateUser(loginreq, httpRequest, httpResponse);

		assertEquals(StatusCode.BADREQUEST.getValue(), response.getBody().getHttpStatusCode());
		assertEquals(BffResponseCode.ERR_LOGIN_UI_API_NOT_COMPATIBLE.getCode(), response.getBody().getCode());
	}

	/**
	 * Test method for authenticateUser authentication failure scenario for Admin UI
	 * authentication
	 */
	@Test
	public void testAuthenticateUserAuthenticationSystemError() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		when(httpRequest.getSession(false)).thenReturn(session);
		when(httpRequest.getSession(true)).thenReturn(session);
		when(productMasterRepo.countByName(Mockito.any())).thenReturn(1);
		when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
		when(tokenProvider.createToken(authentication)).thenReturn("success");
		LoginRequest loginreq = createLoginRequest("ADMIN_UI");
		String authPayload = "{\"usr_id\":" + "\"" + loginreq.getUserId() + "\"" + ",\"password\":" + "\""
				+ loginreq.getPassword() + "\"}";

		when(restTemplate.postForEntity("http://10.144.196.194:4500/ws/auth/login",
				new HttpEntity<>(authPayload, headers), String.class)).thenThrow(new RuntimeException("System error"));
		ResponseEntity<BffCoreResponse> response = authController.authenticateUser(loginreq, httpRequest, httpResponse);
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getStatusCodeValue());
		assertEquals(BffResponseCode.ERR_LOGIN_UI_API_NOT_COMPATIBLE.getCode(), response.getBody().getCode());
	}

	@Test
	public void testAuthenticateUserAuthenticationRestClientError() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		when(httpRequest.getSession(true)).thenReturn(session);
		when(httpRequest.getSession(false)).thenReturn(session);
		when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
		when(tokenProvider.createToken(authentication)).thenReturn("success");
		LoginRequest loginreq = createLoginRequest("ADMIN_UI");
		
		List<VersionMaster> versionMasterlist =new ArrayList<>();
		VersionMaster versionMaster= new VersionMaster();
		versionMaster.setVersion("1.0");
		versionMaster.setChannel("BFFCORE");
		versionMaster.setActive(true);
		List<VersionMapping>  VersionMappinglist= new ArrayList<>();
		VersionMapping versionMapping= new VersionMapping();
		VersionMaster masterForMobile = new VersionMaster();
		masterForMobile.setVersion("1.0");
		masterForMobile.setChannel("ADMIN_UI");
		versionMapping.setMappedAppVersionMaster(masterForMobile);
		versionMapping.setUid(UUID.randomUUID());
		VersionMappinglist.add(versionMapping);
		versionMaster.setBffCoreVersionMappingList(VersionMappinglist);
		versionMapping.setBffCoreVersionMaster(versionMaster);
		versionMasterlist.add(versionMaster);
		when(productMasterRepo.countByName(Mockito.any())).thenReturn(1);
		when(versionMasterRepository.findByActiveAndChannel(true, "BFFCORE")).thenReturn(versionMasterlist);

		when(masterUserRepository.countByUserId(Mockito.any())).thenThrow(new RestClientException(""));
		ResponseEntity<BffCoreResponse> response = authController.authenticateUser(loginreq, httpRequest, httpResponse);
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getBody().getHttpStatusCode());
		assertEquals(BffResponseCode.ERR_LOGIN_UI_API_NOT_COMPATIBLE.getCode(), response.getBody().getCode());
	}

	@Test
	public void testAuthenticateUserException() {
		when(httpRequest.getSession(false)).thenReturn(session);
		LoginRequest loginReq = createLoginRequest("ADMIN_UI");
		loginReq.setChannel(null);
		when(productMasterRepo.countByName(Mockito.any())).thenReturn(1);
		ResponseEntity<BffCoreResponse> response = authController.authenticateUser(loginReq, httpRequest, httpResponse);
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getBody().getHttpStatusCode());
		assertEquals(BffResponseCode.ERR_LOGIN_API_AUTH_EXCEPTION.getCode(), response.getBody().getCode());
	}
	
	@Test
	public void testMergeSession() {
		ResponseEntity<BffCoreResponse> response = authController.mergeSession("", httpRequest);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(BffResponseCode.LOGIN_SUCCESS_CODE_AUTHENTICATION.getCode(), response.getBody().getCode());
		
	}
	
	@Test
	public void testMergeSessionNotNull() {
		when(httpRequest.getSession(false)).thenReturn(session);
		ResponseEntity<BffCoreResponse> response = authController.mergeSession("", httpRequest);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(BffResponseCode.LOGIN_SUCCESS_CODE_AUTHENTICATION.getCode(), response.getBody().getCode());
	}
	
	@Test
	public void testFetchOpenSession() {
		ResponseEntity<BffCoreResponse> response = authController.fetchOpenSession(httpRequest);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(BffResponseCode.LOGIN_SUCCESS_CODE_AUTHENTICATION.getCode(), response.getBody().getCode());
	}
	
	
	@Test
	public void testGetBffInactivePeriodForTenant() {
		
		final List<GrantedAuthority> grantedAuths = new ArrayList<>();
		grantedAuths.add(new SimpleGrantedAuthority(BffAdminConstantsUtils.ROLE_USER));
		List<String> permissionIds= new ArrayList<>();
		String permision="Test";
		permissionIds.add(permision);
		UserPrincipal principal = UserPrincipal.builder()
				.userId("SUPER")
				.password("SUPER")
				.channel(ChannelType.MOBILE_RENDERER)
				.deviceId("")
				.locale(BffAdminConstantsUtils.LOCALE)
				.tenant("SOURCE_A")				
				.version("1")
				.prdAuthCookie("COOKIE")
				.permissionIds(permissionIds)
				.build();
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal,"SUPER", grantedAuths);
	    SecurityContextHolder.getContext().setAuthentication(authentication); 
	    Map<String, Map<String, String>> tenantConfigMap = new HashMap<>();
	    tenantConfigMap.put("SOURCE_A", new HashMap<String, String>());
	   tenantSetting.setTenantConfigMap(tenantConfigMap);
	   when(sessionUtils.fetchUserOpenSessionCount(Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(1);
	   when(httpRequest.getSession()).thenReturn(session);
		
		when(httpRequest.getSession(false)).thenReturn(session);

		ProductTenantConfig productTenantConfig=  ProductTenantConfig.builder()
				.configValue("120")
				.configName("APPLICATIION")
				.tenant("SOURCE_A")
				.build();
		 when(prodTenantConfigRepo.findByTenantAndConfigName(Mockito.any(),Mockito.any())).thenReturn(productTenantConfig);
		 when(prodTenantConfigRepo.save(Mockito.any())).thenReturn(productTenantConfig);
		ResponseEntity<BffCoreResponse> response = authController.getBffInactivePeriodForTenant(httpRequest);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(BffResponseCode.INACTIVE_PERIOD_RETRIVAL_SUCCESS_CODE.getCode(), response.getBody().getCode());
	}
	
	@Test
	public void testGetBffInactivePeriodForTenant_Exception(){
		AppSetting appSetting = new AppSetting();
		appSetting.setAppInactivePeriod(30);
		ResponseEntity<BffCoreResponse> response = authController.getBffInactivePeriodForTenant(httpRequest);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	@Test
	public void testUpdateBffInactivePeriodForTenant() {
		
		final List<GrantedAuthority> grantedAuths = new ArrayList<>();
		grantedAuths.add(new SimpleGrantedAuthority(BffAdminConstantsUtils.ROLE_USER));
		List<String> permissionIds= new ArrayList<>();
		String permision="Test";
		permissionIds.add(permision);
		UserPrincipal principal = UserPrincipal.builder()
				.userId("SUPER")
				.password("SUPER")
				.channel(ChannelType.MOBILE_RENDERER)
				.deviceId("")
				.locale(BffAdminConstantsUtils.LOCALE)
				.tenant("SOURCE_A")				
				.version("1")
				.prdAuthCookie("COOKIE")
				.permissionIds(permissionIds)
				.build();
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal,"SUPER", grantedAuths);
	    SecurityContextHolder.getContext().setAuthentication(authentication); 
	    Map<String, Map<String, String>> tenantConfigMap = new HashMap<>();
	    tenantConfigMap.put("SOURCE_A", new HashMap<String, String>());
	   tenantSetting.setTenantConfigMap(tenantConfigMap);
	   when(tenantSetting.getTenantConfigMap()).thenReturn(tenantConfigMap);
	   when(sessionUtils.fetchUserOpenSessionCount(Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(1);
	   when(httpRequest.getSession()).thenReturn(session);
		when(httpRequest.getSession(false)).thenReturn(session);

		ProductTenantConfig productTenantConfig=  ProductTenantConfig.builder()
				.configValue("120")
				.configName("APPLICATIION")
				.tenant("SOURCE_A")
				.build();
		AppSetting appSetting = new AppSetting();
		appSetting.setAppInactivePeriod(30);
		 when(prodTenantConfigRepo.findByTenantAndConfigName(Mockito.any(),Mockito.any())).thenReturn(productTenantConfig);
		 when(prodTenantConfigRepo.save(Mockito.any())).thenReturn(productTenantConfig);
		ResponseEntity<BffCoreResponse> response = authController.updateBffInactivePeriodForTenant(httpRequest,appSetting);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(BffResponseCode.INACTIVE_PERIOD_UPDATE_SUCCESS_CODE.getCode(), response.getBody().getCode());
	}
	
	
	@Test
	public void testUpdateBffInactivePeriodForTenant_Exception(){
		AppSetting appSetting = new AppSetting();
		appSetting.setAppInactivePeriod(30);
		ResponseEntity<BffCoreResponse> response = authController.updateBffInactivePeriodForTenant(httpRequest,appSetting);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}
	
	//Product validity check
	@Test
	public void testAuthenticateUser_Exception(){
		LoginRequest loginreq = createLoginRequest(ChannelType.ADMIN_UI.getType());
		loginreq.setChannel(ChannelType.ADMIN_UI);
		when(productMasterRepo.countByName(Mockito.any())).thenReturn(0);
		ResponseEntity<BffCoreResponse> response = authController.authenticateUser(loginreq, httpRequest, httpResponse);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(BffResponseCode.ERR_LOGIN_PRODUCT_API_NOT_COMPATIBLE.getCode(), response.getBody().getCode());
	}


	private ResponseEntity<ProductRole> createResponseEntityProductRole(HttpStatus status) {
		ProductRole prDto = new ProductRole();
		prDto.setRoleIds(new CopyOnWriteArrayList<String>());
		ResponseEntity<ProductRole> responseEntity = new ResponseEntity<ProductRole>(prDto, status);
		return responseEntity;
	}

	private ResponseEntity<ProductRolePermission> createResponseEntityProductRolePerm(HttpStatus status) {
		ProductRolePermission prpmDto = new ProductRolePermission();
		prpmDto.setPermissions(new CopyOnWriteArrayList<String>());
		ResponseEntity<ProductRolePermission> responseEntity = new ResponseEntity<ProductRolePermission>(prpmDto,
				status);
		return responseEntity;
	}

	private ResponseEntity<String> createResponseEntity(HttpStatus status) {
		ResponseEntity<String> responseEntity = new ResponseEntity<String>("sample body", status);
		return responseEntity;
	}

	private LoginRequest createLoginRequest(String channel) {
		LoginRequest loginreq = new LoginRequest();
		loginreq.setUserId("testwmsuser1");
		loginreq.setPassword("testpass");
		loginreq.setChannel(ChannelType.valueOf(channel));
		loginreq.setVersion("1.0");
		loginreq.setTenant("WMS Server 1");
		return loginreq;
	}
}