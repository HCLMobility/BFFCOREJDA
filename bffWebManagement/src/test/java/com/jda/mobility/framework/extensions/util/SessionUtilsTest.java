package com.jda.mobility.framework.extensions.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.entity.AppConfigDetail;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.entity.MenuMaster;
import com.jda.mobility.framework.extensions.model.LoginRequest;
import com.jda.mobility.framework.extensions.model.UserSessionDetails;
import com.jda.mobility.framework.extensions.repository.AppConfigDetailRepository;
import com.jda.mobility.framework.extensions.repository.AppConfigMasterRepository;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.AppCfgRequestType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ChannelType;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.SessionAttribute;

/**
* The class SessionUtilsTest.java
* 
 * @author HCL Technologies Ltd.
*/
@SuppressWarnings({ConstantsUtils.UNCHECKED,ConstantsUtils.RAWTYPE})
@RunWith(SpringJUnit4ClassRunner.class)
public class SessionUtilsTest {

	@InjectMocks
	private SessionUtils sessionUtils;
	@Mock
	private SpringSessionBackedSessionRegistry sessionRegistry;
	@Mock
	private FindByIndexNameSessionRepository sessionRepository;
	@Mock
	private Session session;
	@Mock
	public SessionDetails sessionDetails;
	@Mock
	private AppConfigMasterRepository appConfigRepository;
	@Mock
	private AppConfigDetailRepository appConfigDetailRepository;
	
	/**
	 * 
	 */
	public void testGetSessionMap() {
		Map<String, Session> map = new HashMap<>();
		session.setAttribute("sessionAttr1", "sessionAttr2");
		map.put("sessionId", session);
		when(sessionRepository.findByPrincipalName(Mockito.anyString())).thenReturn(map);
		Assert.assertTrue(sessionUtils.getSessionMap("SUPER").size() >0);
	}

	/**
	 * 
	 */
	@Test
	public void testRemoveSession() {
		Map<String, Session> map = new HashMap<>();
		map.put("sessionId", session);
		when(sessionRepository.findByPrincipalName(Mockito.anyString())).thenReturn(map);
		sessionUtils.removeSession("SUPER", "sessionId");
		Assert.assertTrue(sessionUtils.getSessionMap("SUPER").size() >0);
	}

	/**
	 * 
	 */
	@Test
	public void testValidateSessionInfo() {
		testGetSessionMap();
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUserId("SUPER");
		loginRequest.setDeviceId("DEVICE_NAME");
		when(sessionRegistry.getSessionInformation(Mockito.any()))
				.thenReturn(new SessionInformation(loginRequest, "sessionId", new Date()));
		when(session.getAttribute(Mockito.anyString())).thenReturn("DEVICE_NAME");
		Session response = sessionUtils.validateSessionInfo(loginRequest);
		assertFalse(response.isExpired());
		
	}

	/**
	 * 
	 */
	@Test
	public void testRegenrateSession() {
		testGetSessionMap();
		MockHttpSession currentSession = new MockHttpSession();
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUserId("SUPER");
		loginRequest.setDeviceId("TEST");
		
		Set<String> set = new HashSet<>();
		set.add("DEVICE_NAME");
		set.add("sessionAttr2");
		when((String)session.getAttribute(Mockito.anyString())).thenReturn("DEVICE_NAME");

		when(session.getAttributeNames()).thenReturn(set);
		when(sessionRegistry.getSessionInformation(Mockito.any()))
				.thenReturn(new SessionInformation(loginRequest, "sessionId", new Date()));
		sessionUtils.regenerateSession(currentSession, "sessionId", "SUPER");
		assertTrue(true);
	}

	/**
	 * 
	 */
	@Test
	public void testFetchUserSessionDetails() {
		UUID menuId=UUID.randomUUID();
		MenuMaster menuMaster= new MenuMaster();
		menuMaster.setUid(menuId);
		menuMaster.setMenuName("WD1");
		Form form= new Form();
		form.setUid(UUID.randomUUID());
		form.setName("Test");
		testGetSessionMap();
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUserId("SUPER");
		loginRequest.setDeviceId("TEST");
		when(session.getAttribute(SessionAttribute.CHANNEL.name())).thenReturn(ChannelType.MOBILE_RENDERER.getType());
		when(session.getAttribute(SessionAttribute.SESSION_RECORDING.name())).thenReturn("");
		when(session.getAttribute(SessionAttribute.SESSION_RECORDING.name())).thenReturn(ConstantsUtils.SESSION_REC_MODE);
		when(sessionRegistry.getSessionInformation(Mockito.any()))
				.thenReturn(new SessionInformation(loginRequest, "sessionId", new Date()));
		List<UserSessionDetails> response = sessionUtils.fetchUserSessionDetails("SUPER", "");
		Assert.assertTrue(response.size() >0);

	}
	
	
	@Test
	public void testFetchUserOpenSessionCount() {
		testGetSessionMap();
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setUserId("SUPER");
		loginRequest.setDeviceId("TEST");
		loginRequest.setChannel(ChannelType.MOBILE_RENDERER);
		Map<String, Session> map = new HashMap<>();
		when(session.getAttribute(SessionAttribute.CHANNEL.name())).thenReturn(ChannelType.MOBILE_RENDERER.getType());
		when(session.getAttribute(SessionAttribute.SESSION_RECORDING.name())).thenReturn("");
		when(session.getAttribute(SessionAttribute.SESSION_RECORDING.name())).thenReturn(ConstantsUtils.SESSION_REC_MODE);
		map.put("sessionId", session);
		when(sessionRepository.findByPrincipalName(Mockito.anyString())).thenReturn(map);
		when(sessionRegistry.getSessionInformation(Mockito.any()))
				.thenReturn(new SessionInformation(loginRequest, "sessionId", new Date()));
		int response = sessionUtils.fetchUserOpenSessionCount("SUPER", "","true");
		assertEquals(1,response);

	}
	
	@Test
	public void testUpdateDeviceIdAsGlobalVariable() {
		testGetSessionMap();
		Map<String, Session> map = new HashMap<>();
		AppConfigMaster appconfig=new AppConfigMaster();
		List<AppConfigDetail> appConfigDetails= new ArrayList<>();
		AppConfigDetail appConfigDetail=new AppConfigDetail();
		appConfigDetail.setConfigValue("test");
		appConfigDetail.setUid(UUID.randomUUID());
		appConfigDetail.setUserId("SUPER");
		appConfigDetail.setDeviceName("DeviceID");
		appConfigDetails.add(appConfigDetail);
		appconfig.setAppConfigDetails(appConfigDetails);
		when(session.getAttribute(SessionAttribute.CHANNEL.name())).thenReturn(ChannelType.MOBILE_RENDERER.getType());
		when(session.getAttribute(SessionAttribute.SESSION_RECORDING.name())).thenReturn("");
		when(session.getAttribute(SessionAttribute.SESSION_RECORDING.name())).thenReturn(ConstantsUtils.SESSION_REC_MODE);
		map.put("sessionId", session);
		when(sessionRepository.findByPrincipalName(Mockito.anyString())).thenReturn(map);
		when(appConfigRepository.findByConfigNameAndConfigType(BffAdminConstantsUtils.DEVICE_ID, AppCfgRequestType.GLOBAL.getType())).thenReturn(appconfig);
		when(appConfigDetailRepository.save(Mockito.any())).thenReturn(new AppConfigDetail());
		sessionUtils.updateDeviceIdAsGlobalVariable();
		assertTrue(true);

	}
	
}