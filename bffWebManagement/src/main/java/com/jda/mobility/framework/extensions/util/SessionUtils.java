package com.jda.mobility.framework.extensions.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.session.web.http.HttpSessionIdResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.entity.AppConfigDetail;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.MenuMaster;
import com.jda.mobility.framework.extensions.model.LoginRequest;
import com.jda.mobility.framework.extensions.model.UserSessionDetails;
import com.jda.mobility.framework.extensions.repository.AppConfigDetailRepository;
import com.jda.mobility.framework.extensions.repository.AppConfigMasterRepository;
import com.jda.mobility.framework.extensions.repository.FormRepository;
import com.jda.mobility.framework.extensions.repository.MenuMasterRepository;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.AppCfgRequestType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.SessionAttribute;

/**
 * The class SessionUtils is intended to provide the utilities to store and retrieve values from Sprint
 * JDBC session
 */
@Component
public class SessionUtils<S extends Session> {
	private static final Logger LOGGER = LogManager.getLogger(SessionUtils.class);
	@Autowired
	private FindByIndexNameSessionRepository<S> sessionRepository;
	@Autowired
	private SpringSessionBackedSessionRegistry<S> sessionRegistry;
	@Autowired
	public SessionDetails sessionDetails;
	@Autowired
	public HttpSessionIdResolver httpSessionIdResolver;
	@Autowired
	private MenuMasterRepository menuMasterRepository;
	@Autowired
	private FormRepository formRepository;
	@Autowired
	private AppConfigDetailRepository appConfigDetailRepository;
	@Autowired
	private AppConfigMasterRepository appConfigRepository;
	/**
	 * To get All session List for principal
	 * 
	 * @param principalName
	 * @return
	 */
	public Map<String, S> getSessionMap(String principalName) {
		return this.sessionRepository.findByPrincipalName(principalName);
	}

	public void removeSession(String principalName, String sessionIdToDelete) {
		Set<String> usersSessionIds = this.sessionRepository.findByPrincipalName(principalName).keySet();
		if (usersSessionIds.contains(sessionIdToDelete)) {
			this.sessionRepository.deleteById(sessionIdToDelete);
		}

	}

	/**
	 * check and fetch session which is available to merge for given device and user
	 * and for non expired session.
	 * 
	 * @param loginRequest
	 * @return Session
	 */
	public S validateSessionInfo(LoginRequest loginRequest) {

		Map<String, S> sessionMap = getSessionMap(loginRequest.getUserId());
		S sessionMain = null;
		if (sessionMap != null && !sessionMap.isEmpty()) {
			for (Entry<String, S> sessionEntry : sessionMap.entrySet()) {
				S session = sessionEntry.getValue();
				SessionInformation info = sessionRegistry.getSessionInformation(session.getId());
				if (info != null && !info.isExpired()) {
					String deviceName = session.getAttribute(SessionAttribute.DEVICE_NAME.name());
					if (deviceName != null && deviceName.equals(loginRequest.getDeviceId())) {
						sessionMain = session;
						break;
					}
				}

			}
		}

		return sessionMain;
	}

	/**
	 * @param currentSession
	 * @param sessionId
	 * @param principalName
	 */
	public void regenerateSession(HttpSession currentSession, String sessionId, String principalName) {
		Map<String, S> sessionMap = getSessionMap(principalName);
		if (sessionMap != null && !sessionMap.isEmpty()) {
			S session = sessionMap.get(sessionId);

			SessionInformation info = sessionRegistry.getSessionInformation(sessionId);
			if (info != null && !info.isExpired()) {
				for (String attrName : session.getAttributeNames()) {
					Object sessionObj = session.getAttribute(attrName);
					if (sessionObj.getClass().isInstance(UUID.class)) {
						currentSession.setAttribute(attrName, (UUID) sessionObj);
					} else {
						currentSession.setAttribute(attrName, String.valueOf(sessionObj));
					}
				}
			}
		}

	}

	/**
	 * @param userId
	 * @param userId
	 * @param currentSessionId
	 * @return List&lt;UserSessionDetails&gt;
	 */
	public List<UserSessionDetails> fetchUserSessionDetails(String userId, String currentSessionId) {
		Map<String, S> sessionMap = getSessionMap(userId);

		List<UserSessionDetails> userSessionListInfo = new ArrayList<>();
		if (sessionMap != null && !sessionMap.isEmpty()) {
			for (Entry<String, S> sessionEntry : sessionMap.entrySet()) {
				S session = sessionEntry.getValue();
				SessionInformation info = sessionRegistry.getSessionInformation(session.getId());
				if (info != null && !info.isExpired() 
						&& !currentSessionId.equalsIgnoreCase(session.getId())
						&& null != session.getAttribute(SessionAttribute.CHANNEL.name())
						&& session.getAttribute(SessionAttribute.CHANNEL.name())
								.equals(BffAdminConstantsUtils.ChannelType.MOBILE_RENDERER.getType())
						&& null != session.getAttribute(SessionAttribute.SESSION_RECORDING.name())
						&& session.getAttribute(SessionAttribute.SESSION_RECORDING.name())
								.equals(ConstantsUtils.SESSION_REC_MODE)) {
					String menuType = null;
					if (session.getAttribute(SessionAttribute.MENU_ID.name()) != null) {
						Optional<MenuMaster> menuMaster = menuMasterRepository
								.findById(session.getAttribute(SessionAttribute.MENU_ID.name()));
						if (menuMaster.isPresent()) {
							menuType = menuMaster.get().getMenuType().getType();
						}
					}
					boolean tabbedForm = false;
					boolean modalForm = false;
					if (session.getAttribute(SessionAttribute.FORM_ID.name()) != null) {
						Optional<Form> form = formRepository
								.getModalAndTabbedDetails(session.getAttribute(SessionAttribute.FORM_ID.name()));
						if (form.isPresent()) {
							tabbedForm = form.get().isTabbedForm();
							modalForm = form.get().isModalForm();
						}
					}
					UserSessionDetails userSessionDetails = UserSessionDetails.builder()
							.deviceId(session.getAttribute(SessionAttribute.DEVICE_NAME.name()))
							.sessionId(session.getId())
							.userId(userId)
							.locale(session.getAttribute(SessionAttribute.LOCALE.name()))
							.flowId(session.getAttribute(SessionAttribute.FLOW_ID.name()))
							.formId(session.getAttribute(SessionAttribute.FORM_ID.name()))
							.warehouseId(session.getAttribute(SessionAttribute.WAREHOUSE_ID.name()))
							.productConfigId(session.getAttribute(SessionAttribute.PRODUCT_CONFIG_ID.name()))
							.version(session.getAttribute(SessionAttribute.VERSION.name()))
							.refererUrl(session.getAttribute(SessionAttribute.REFERER_URL.name()))
							.menuId(session.getAttribute(SessionAttribute.MENU_ID.name()))
							.menuType(menuType)
							.tabbedForm(tabbedForm)
							.modalForm(modalForm)
							.lastAccessTime(session.getLastAccessedTime())
							.build();
					userSessionListInfo.add(userSessionDetails);
				}
			}

		}
		return userSessionListInfo;
	}

	/**
	 * Retrieve user Open Session Count.
	 * 
	 * @param userId
	 * @param currentSessionId
	 * @param currentRecMode
	 * @return int
	 */
	public int fetchUserOpenSessionCount(String userId, String currentSessionId, String currentRecMode) {		
		int openSessionCount = 0;
		if(currentRecMode.equalsIgnoreCase(ConstantsUtils.SESSION_REC_MODE)) {
			Map<String, S> sessionMap = getSessionMap(userId);
			if (!CollectionUtils.isEmpty(sessionMap)) {
				for (Entry<String, S> sessionEntry : sessionMap.entrySet()) {
					S session = sessionEntry.getValue();
					SessionInformation info = sessionRegistry.getSessionInformation(session.getId());
					if (info != null && !info.isExpired() && !currentSessionId.equalsIgnoreCase(session.getId())
							&& null != session.getAttribute(SessionAttribute.CHANNEL.name())
							&& session.getAttribute(SessionAttribute.CHANNEL.name())
								.equals(BffAdminConstantsUtils.ChannelType.MOBILE_RENDERER.getType())
							&& null != session.getAttribute(SessionAttribute.SESSION_RECORDING.name())
							&& session.getAttribute(SessionAttribute.SESSION_RECORDING.name())
							.equals(ConstantsUtils.SESSION_REC_MODE)) {
						LOGGER.log(Level.DEBUG, "User open session_id: {} and last_access_time: {}", session.getId(),
								info.getLastRequest());
						openSessionCount++;
					}
				}
			}
		}
		return openSessionCount;
	}
	
	public void updateDeviceIdAsGlobalVariable() {
		if (sessionDetails != null) {
			// Update device Id in App Config - GLOBAL
			AppConfigMaster globalAppConfig = appConfigRepository.findByConfigNameAndConfigType(
					BffAdminConstantsUtils.DEVICE_ID, AppCfgRequestType.GLOBAL.getType());

			if (globalAppConfig != null) {
				AppConfigDetail appConfig = new AppConfigDetail();

				// If Config name is already present for given userId and deviceId , then update
				// them
				if (!globalAppConfig.getAppConfigDetails().isEmpty()) {
					for (AppConfigDetail appConfigDetail : globalAppConfig.getAppConfigDetails()) {
						if (null!= appConfigDetail.getDeviceName() && null!=appConfigDetail.getUserId() 
								&& appConfigDetail.getUserId().equalsIgnoreCase(sessionDetails.getPrincipalName())
								&& appConfigDetail.getDeviceName().equalsIgnoreCase(sessionDetails.getDeviceName())) {
							appConfig = appConfigDetail;
							break;
						}
					}
				}

				appConfig.setConfigValue(sessionDetails.getDeviceName());
				appConfig.setAppConfigMaster(globalAppConfig);
				appConfig.setUserId(sessionDetails.getPrincipalName());
				appConfig.setDeviceName(sessionDetails.getDeviceName());
				appConfigDetailRepository.save(appConfig);
			}
		}
	}
		
}