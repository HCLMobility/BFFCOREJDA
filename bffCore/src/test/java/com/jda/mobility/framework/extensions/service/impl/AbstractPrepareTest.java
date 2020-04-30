package com.jda.mobility.framework.extensions.service.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.common.SessionDetails;
import com.jda.mobility.framework.extensions.entity.ResourceBundle;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;

public abstract class AbstractPrepareTest {
	@Spy
	public BffResponse bffResponse = new BffResponse();
	@Spy
	public SessionDetails sessionDetails =  new SessionDetails();
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
}
