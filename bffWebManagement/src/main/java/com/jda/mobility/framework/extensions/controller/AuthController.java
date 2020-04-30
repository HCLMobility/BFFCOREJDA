package com.jda.mobility.framework.extensions.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.dto.RoleMasterDto;
import com.jda.mobility.framework.extensions.dto.UserAuthDto;
import com.jda.mobility.framework.extensions.dto.UserDto;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.entity.ProductTenantConfig;
import com.jda.mobility.framework.extensions.model.AppSetting;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.LoginRequest;
import com.jda.mobility.framework.extensions.model.UserSessionDetails;
import com.jda.mobility.framework.extensions.repository.AppConfigMasterRepository;
import com.jda.mobility.framework.extensions.repository.MasterUserRepository;
import com.jda.mobility.framework.extensions.repository.ProductMasterRepository;
import com.jda.mobility.framework.extensions.repository.ProductTenantConfigRepository;
import com.jda.mobility.framework.extensions.repository.VersionMasterRepository;
import com.jda.mobility.framework.extensions.security.TokenProvider;
import com.jda.mobility.framework.extensions.security.UserPrincipal;
import com.jda.mobility.framework.extensions.service.TranslationService;
import com.jda.mobility.framework.extensions.service.UserService;
import com.jda.mobility.framework.extensions.util.ConstantsUtils;
import com.jda.mobility.framework.extensions.util.SessionUtils;
import com.jda.mobility.framework.extensions.util.TenantSetting;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.AppCfgRequestType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ChannelType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.SessionAttribute;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.TenantConfigName;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;

/**
 * The class implements process to authenticate User
 */
/**
 * @author vijjagiri.subbarao
 *
 */
/**
 * @author vijjagiri.subbarao
 *
 */
@RestController
@RequestMapping("/api/auth/v1")
public class AuthController {

	private static final String ACTIVE = "active";
	private static final Logger LOGGER = LogManager.getLogger(AuthController.class);

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private TokenProvider tokenProvider;
	@Autowired
	private UserService userService;
	@Autowired
	private BffResponse bffResponse;
	@Autowired
	public SessionDetails sessionDetails;
	@Autowired
	private MasterUserRepository masterUserRepository;
	@SuppressWarnings({ ConstantsUtils.RAWTYPE })
	@Autowired
	private FindByIndexNameSessionRepository sessionRepository;
	@SuppressWarnings({ ConstantsUtils.RAWTYPE })
	@Autowired
	private SessionUtils sessionUtils;
	@Autowired
	private ProductMasterRepository productMasterRepo;	
	@Autowired
	private VersionMasterRepository versionMasterRepository;
	@Autowired
	private TenantSetting tenantSetting;
	@Autowired
	private ProductTenantConfigRepository prodTenantConfigRepo;
	@Autowired
	private AppConfigMasterRepository appConfigRepository;
	@Autowired
	private TranslationService translationService;
	@Value("${spring.session.timeout:}")
	private String sessionTimeout;
	
	/**
	 * Authenticate logged in user request
	 * 
	 * @param loginRequest user authentication Request details  
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	* @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after Authenticate user
	 */
	@PostMapping("/user")
	public ResponseEntity<BffCoreResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
			HttpServletRequest request, HttpServletResponse response) {
		BffCoreResponse bffCoreResponse = null;
		try {
			//Product validity check
			if (productMasterRepo.countByName(loginRequest.getTenant()) == 0) {
				bffCoreResponse = bffResponse.errResponse(
						List.of(BffResponseCode.ERR_LOGIN_PRODUCT_API_NOT_COMPATIBLE,
								BffResponseCode.ERR_LOGIN_PRODUCT_USER_NOT_COMPATIBLE),
						StatusCode.BADREQUEST, loginRequest.getLocale(), loginRequest.getTenant(), loginRequest.getTenant());
				return generateResponse(bffCoreResponse);
			}
			// Version compatibility validation
			if (versionMasterRepository.countOfCompatibleVersions(ChannelType.BFFCORE.getType(),
					loginRequest.getChannel().getType(), loginRequest.getVersion()) == 0) {
				StringBuilder userMesg = new StringBuilder(loginRequest.getChannel().getType())
						.append(BffAdminConstantsUtils.COMMA).append(loginRequest.getVersion())
						.append(BffAdminConstantsUtils.COMMA).append(ChannelType.BFFCORE)
						.append(BffAdminConstantsUtils.COMMA).append(ACTIVE);
				List.of(loginRequest.getChannel(), loginRequest.getVersion(), ChannelType.BFFCORE, ACTIVE);
				bffCoreResponse = bffResponse.errResponse(
						List.of(BffResponseCode.ERR_LOGIN_UI_API_NOT_COMPATIBLE,
								BffResponseCode.ERR_LOGIN_UI_API_NOT_COMPATIBLE),
						StatusCode.BADREQUEST, userMesg.toString(), userMesg.toString());
				return generateResponse(bffCoreResponse);
			}
			String token = tokenProvider.getJwtFromRequest(request);
			Map<String, String> authInfo = tokenProvider.getAuthMapFromToken(token);
			String password = loginRequest.getPassword();
			if(authInfo != null && authInfo.get("name") != null) {
				LOGGER.log(Level.DEBUG, "Valid name claim details found from token");
				loginRequest.setUserId(authInfo.get("name"));
				password = tokenProvider.getBearerJwtFromRequest(request);
			}
			
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(loginRequest, password));
			if (authentication != null && authentication.isAuthenticated()) {
				UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
				String prdAuthCookie = principal.getPrdAuthCookie();
				sessionDetails.setPrdAuthCookie(prdAuthCookie);
				saveSessionAttributes(request, loginRequest);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				LOGGER.log(Level.DEBUG, "Thread Local Context updated with Authentication");
				if (token == null) {
					LOGGER.log(Level.DEBUG, "Token to be created");
					token = tokenProvider.createToken(authentication);
				}
				if (loginRequest.getChannel().equals(ChannelType.ADMIN_UI)) {
					// Check if the user is a super user and set flag to true
					bffCoreResponse = fetchAdminUIAccessData(loginRequest, token, prdAuthCookie);
				} else if (loginRequest.getChannel().equals(ChannelType.MOBILE_RENDERER)) {
					bffCoreResponse = fetchMobileAccessData(principal.getUserId(), request, token, prdAuthCookie,
							principal.getRoleIds(), principal.getPermissionIds());
				}

			} else {
				invalidateSession(request);
				bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_LOGIN_API_AUTH_FAILED,
						BffResponseCode.ERR_LOGIN_USER_ACCESS_NOT_AUTHORIZED), StatusCode.BADREQUEST);
			}
		} catch (RestClientException e) {
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, loginRequest.getUserId(), e);
			invalidateSession(request);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_LOGIN_API_REST_CLIENT_EXCEPTION,
					BffResponseCode.ERR_LOGIN_USER_REST_CLIENT_EXCEPTION), StatusCode.BADREQUEST, loginRequest.getLocale());
		} catch (Exception exp) {			
			LOGGER.log(Level.ERROR, BffAdminConstantsUtils.APP_EXP_MSG, loginRequest.getUserId(), exp);
			invalidateSession(request);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_LOGIN_API_AUTH_EXCEPTION,
					BffResponseCode.ERR_LOGIN_USER_AUTH_EXCEPTION), StatusCode.INTERNALSERVERERROR, loginRequest.getLocale());
		}
		return generateResponse(bffCoreResponse);
	}

	/**
	 * Update Inactive time period cannot be less than 1 min
	 * 
	 * @param request HttpServletRequest
	 * @param appSetting Inactive time period details for application
	* @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after update Inactive time period
	 */
	@PostMapping("/inactiveperiod")
	public ResponseEntity<BffCoreResponse> updateBffInactivePeriodForTenant(HttpServletRequest request,
			@Valid @RequestBody AppSetting appSetting) {
		BffCoreResponse bffCoreResponse = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated() && request.getSession(false) != null
				&& authentication.getPrincipal() != null) {
			UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
			if (tenantSetting.getTenantConfigMap().keySet().contains(principal.getTenant())) {
				tenantSetting.getTenantConfigMap().get(principal.getTenant()).put(
						TenantConfigName.INACTIVE_SESSION_PERIOD.name(),
						String.valueOf(appSetting.getAppInactivePeriod() * 60));
			} else {
				final Map<String, String> configMap = new HashMap<>();
				configMap.put(TenantConfigName.INACTIVE_SESSION_PERIOD.name(),
						String.valueOf(appSetting.getAppInactivePeriod() * 60));
				tenantSetting.getTenantConfigMap().put(principal.getTenant(), configMap);
			}

			request.getSession(false).setMaxInactiveInterval(appSetting.getAppInactivePeriod() * 60);
			ProductTenantConfig config = prodTenantConfigRepo.findByConfigNameAndTenant(TenantConfigName.INACTIVE_SESSION_PERIOD.name(),principal.getTenant());
			UUID uid = config!=null ? config.getUid() : null;
			prodTenantConfigRepo.save(ProductTenantConfig.builder().tenant(principal.getTenant())
					.configName(TenantConfigName.INACTIVE_SESSION_PERIOD.name()).uid(uid)
					.configValue(String.valueOf(appSetting.getAppInactivePeriod() * 60)).build());
			bffCoreResponse = bffResponse.response(BffAdminConstantsUtils.EMPTY_SPACES,
					BffResponseCode.INACTIVE_PERIOD_UPDATE_SUCCESS_CODE,
					BffResponseCode.INACTIVE_PERIOD_UPDATE_USER_CODE, StatusCode.OK);
		} else {
			bffCoreResponse = BffUtils.buildErrResponse(StatusCode.UNAUTHORIZED.getValue(),
					BffAdminConstantsUtils.AUTHENTICATION_FAILED, null, StatusCode.UNAUTHORIZED.getValue());
		}
		return generateResponse(bffCoreResponse);

	}

	/**
	 * Fetch the inactive time period sessions  
	 * 
	 * @param request HttpServletRequest
	* @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after Fetch Inactive time period sessions
	 */
	@GetMapping("/inactiveperiod")
	public ResponseEntity<BffCoreResponse> getBffInactivePeriodForTenant(HttpServletRequest request) {
		BffCoreResponse bffCoreResponse = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated() && request.getSession(false) != null
				&& authentication.getPrincipal() != null) {
			UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
			ProductTenantConfig productTenantConfig = prodTenantConfigRepo
					.findByTenantAndConfigName(principal.getTenant(), TenantConfigName.INACTIVE_SESSION_PERIOD.name());

			String configValue = sessionTimeout;
			if (productTenantConfig != null) {
				int minutes = (Integer.parseInt(productTenantConfig.getConfigValue()) / 60);
				configValue = String.valueOf(minutes);
			}
			bffCoreResponse = bffResponse.response(configValue, BffResponseCode.INACTIVE_PERIOD_RETRIVAL_SUCCESS_CODE,
					BffResponseCode.INACTIVE_PERIOD_RETRIVAL_USER_CODE, StatusCode.OK);
		} else {
			bffCoreResponse = BffUtils.buildErrResponse(StatusCode.UNAUTHORIZED.getValue(),
					BffAdminConstantsUtils.AUTHENTICATION_FAILED, null, StatusCode.UNAUTHORIZED.getValue());
		}
		return generateResponse(bffCoreResponse);
	}

	/**
	 * @param loginRequest user authentication Request details  
	 * @param token Token for authentication 
	 * @param prdAuthCookie user authentication cookie
	* @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after Authenticate user for adminUI
	 */
	private BffCoreResponse fetchAdminUIAccessData(LoginRequest loginRequest, String token, String prdAuthCookie) {
		BffCoreResponse bffCoreResponse;
		boolean isSuperUser = (masterUserRepository.countByUserId(loginRequest.getUserId()) > 0);
		UserDto userDto = UserDto.builder()
				.userId(loginRequest.getUserId())
				.authToken(token)
				.prdAuthCookie(prdAuthCookie)
				.superUser(isSuperUser)
				.build();
		bffCoreResponse = userService.getRolesForUser(loginRequest.getUserId());
		if (bffCoreResponse.getHttpStatusCode() == StatusCode.OK.getValue()) {
			LOGGER.log(Level.DEBUG, "Roles for user found");
			userDto.setRoleMasterDto((RoleMasterDto) (bffCoreResponse.getDetails().getData()));
			bffCoreResponse = bffResponse.response(userDto, BffResponseCode.LOGIN_SUCCESS_CODE_AUTHENTICATION,
					BffResponseCode.LOGIN_USER_CODE_AUTHENTICATION, StatusCode.OK);
		} else if (isSuperUser && bffCoreResponse.getHttpStatusCode() == StatusCode.BADREQUEST.getValue()) {
			bffCoreResponse = bffResponse.response(userDto, BffResponseCode.LOGIN_SUCCESS_CODE_AUTHENTICATION,
					BffResponseCode.LOGIN_USER_CODE_AUTHENTICATION, StatusCode.OK);
		}
		return bffCoreResponse;
	}

	/**
	 * @param userId userdId for session
	 * @param request HttpServletRequest
	 * @param token Token for authentication 
	 * @param prdAuthCookie user authentication cookie
	 * @param roleIds List&lt;roleIds&gt; List for Roles for based on user
	 * @param permissionIds List&lt;roleIds&gt; List for permission for based on user
	 * @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after Fetch the Mobile User details
	 */
	private BffCoreResponse fetchMobileAccessData(String userId, HttpServletRequest request, String token,
			String prdAuthCookie, List<String> roleIds, List<String> permissionIds) {
		BffCoreResponse bffCoreResponse;
		try {
			final String sessionRecMode = String.valueOf(request.getSession().getAttribute(SessionAttribute.SESSION_RECORDING.name()));
			UserAuthDto userAuthDto = UserAuthDto.builder()
					.userId(userId)
					.authToken(token)
					.prdAuthCookie(prdAuthCookie)
					.roleIds(roleIds)
					.permissionIds(permissionIds)
					.openSessionCount(sessionUtils.fetchUserOpenSessionCount(userId, request.getSession().getId(), sessionRecMode))
					.sessionRecMode(sessionRecMode)
					.localizedResBundleEntries(translationService.getlocalizedResBundleEntries (BffAdminConstantsUtils.ResourceBundleType.MOBILE.getType()))
					.build();
			
			//Call to update deviceId in Global variable
			sessionUtils.updateDeviceIdAsGlobalVariable();
			bffCoreResponse = bffResponse.response(userAuthDto, BffResponseCode.LOGIN_SUCCESS_CODE_AUTHENTICATION,
					BffResponseCode.LOGIN_USER_CODE_AUTHENTICATION, StatusCode.OK);
		} catch (RestClientException e) {
			LOGGER.log(Level.ERROR, "RestClientException while fetching roles and permissions.", e);
			invalidateSession(request);
			bffCoreResponse = bffResponse
					.errResponse(
							List.of(BffResponseCode.ERR_LOGIN_ROLESPERMS_API_REST_CLIENT_EXCEPTION,
									BffResponseCode.ERR_LOGIN_ROLESPERMS_USER_REST_CLIENT_EXCEPTION),
							StatusCode.BADREQUEST);
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Exception  while fetching roles and permissions.", e);
			invalidateSession(request);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_LOGIN_ROLESPERMS_API_EXCEPTION,
					BffResponseCode.ERR_LOGIN_ROLESPERMS_USER_EXCEPTION), StatusCode.INTERNALSERVERERROR);
		}
		return bffCoreResponse;
	}

	/**
	 * @param request HttpServletRequest
	 * @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after InValidate user session
	 */
	private void invalidateSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			@SuppressWarnings({ ConstantsUtils.RAWTYPE, ConstantsUtils.UNCHECKED })
			SpringSessionBackedSessionRegistry sessionRegistry = new SpringSessionBackedSessionRegistry(
					sessionRepository);
			SessionInformation info = sessionRegistry.getSessionInformation(session.getId());
			if (null != info && !info.isExpired()) {
				info.expireNow();
			}
			SecurityContextHolder.clearContext();
			try {
				session.invalidate();
			} catch (IllegalStateException e) {
				LOGGER.log(Level.ERROR, "Session is already invalidated for sessionId {}", session.getId());
			}
		}

	}

	/**
	 * Save/update the new entry for session attributes if "X-Auth-Token" is either
	 * null or invalid/expired in request header.
	 * 
	 * @param request HttpServletRequest
	 * @param loginRequest user authentication Request details
	 * @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after SaveSession attributes
	 */
	private void saveSessionAttributes(HttpServletRequest request, LoginRequest loginRequest) {
		HttpSession httpSession = request.getSession(true);
		if (!StringUtils.isEmpty(loginRequest.getTenant()) && !ObjectUtils.isEmpty(tenantSetting.getTenantConfigMap())
				&& tenantSetting.getTenantConfigMap().keySet().contains(loginRequest.getTenant())) {
			httpSession.setMaxInactiveInterval(Integer.valueOf(tenantSetting.getTenantConfigMap()
					.get(loginRequest.getTenant()).get(TenantConfigName.INACTIVE_SESSION_PERIOD.name())));
		}
		if (StringUtils.isEmpty(loginRequest.getDeviceId())) {
			loginRequest.setDeviceId(loginRequest.getChannel().getType());
		}		
		if (sessionDetails != null) {
			sessionDetails.setSessionId(httpSession.getId());
			sessionDetails.setLocale(loginRequest.getLocale());
			sessionDetails.setChannel(loginRequest.getChannel().getType());
			sessionDetails.setTenant(loginRequest.getTenant());
			sessionDetails.setVersion(loginRequest.getVersion());
			sessionDetails.setPrincipalName(loginRequest.getUserId());
			sessionDetails.setDeviceName(loginRequest.getDeviceId());
		}
		httpSession.setAttribute(SessionAttribute.DEVICE_NAME.name(), loginRequest.getDeviceId());
		httpSession.setAttribute(SessionAttribute.VERSION.name(), loginRequest.getVersion());
		httpSession.setAttribute(SessionAttribute.LOCALE.name(), loginRequest.getLocale());
		httpSession.setAttribute(SessionAttribute.USER_ID.name(), loginRequest.getUserId());
		httpSession.setAttribute(SessionAttribute.REFERER_URL.name(), UrlUtils.buildFullRequestUrl(request));
		httpSession.setAttribute(SessionAttribute.CHANNEL.name(), loginRequest.getChannel().getType());
		AppConfigMaster configMaster = appConfigRepository.findByConfigNameAndConfigType(SessionAttribute.SESSION_RECORDING.name(),
				AppCfgRequestType.APPLICATION.getType());
		if(configMaster != null && !configMaster.getAppConfigDetails().isEmpty()) {
			httpSession.setAttribute(SessionAttribute.SESSION_RECORDING.name(), configMaster.getAppConfigDetails().get(0).getConfigValue());
		}
	}

	/**
	 * @param sessionId Required session id for merge
	 * @param request HttpServletRequest
	* @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after MergeSession 
	 */
	@GetMapping("/session/recover/{sessionId}")
	public ResponseEntity<BffCoreResponse> mergeSession(@Valid @PathVariable("sessionId") String sessionId,
			HttpServletRequest request) {
		HttpSession httpSession = request.getSession(false);
		String currentSessionId = null;
		if (httpSession != null) {
			currentSessionId = httpSession.getId();
			httpSession.getAttribute(SessionAttribute.USER_ID.name());
			sessionUtils.regenerateSession(httpSession, sessionId,
					String.valueOf(httpSession.getAttribute(SessionAttribute.USER_ID.name())));
		}
		BffCoreResponse bffCoreResponse = bffResponse.response(currentSessionId,
				BffResponseCode.LOGIN_SUCCESS_CODE_AUTHENTICATION, BffResponseCode.LOGIN_USER_CODE_AUTHENTICATION,
				StatusCode.OK);
		return generateResponse(bffCoreResponse);
	}

	/**
	 * @param request HttpServletRequest
	 * @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after Fetch list of sessions 
	 */
	@SuppressWarnings(ConstantsUtils.UNCHECKED)
	@GetMapping("/session/list/")
	public ResponseEntity<BffCoreResponse> fetchOpenSession(HttpServletRequest request) {
		HttpSession httpSession = request.getSession(false);
		List<UserSessionDetails> sessionDetailList = null;
		if (httpSession != null && httpSession.getAttribute(SessionAttribute.USER_ID.name()) != null) {
			sessionDetailList = sessionUtils.fetchUserSessionDetails(
					(String) httpSession.getAttribute(SessionAttribute.USER_ID.name()), httpSession.getId());
			Collections.sort(sessionDetailList, Collections.reverseOrder());
		}
		BffCoreResponse bffCoreResponse = bffResponse.response(sessionDetailList,
				BffResponseCode.LOGIN_SUCCESS_CODE_AUTHENTICATION, BffResponseCode.LOGIN_USER_CODE_AUTHENTICATION,
				StatusCode.OK);
		return generateResponse(bffCoreResponse);
	}

	/**
	 * @param bffCoreResponse Response code for status 
	 * @return ResponseEntity&lt;BffCoreResponse&gt; The success/error response after generate BffcoreResponse 
	 */
	private ResponseEntity<BffCoreResponse> generateResponse(BffCoreResponse bffCoreResponse) {
		if (bffCoreResponse != null) {
			return new ResponseEntity<>(bffCoreResponse, HttpStatus.valueOf(bffCoreResponse.getHttpStatusCode()));
		} else {
			return new ResponseEntity<>(bffCoreResponse, HttpStatus.valueOf(StatusCode.INTERNALSERVERERROR.getValue()));
		}
	}
}