package com.jda.mobility.framework.extensions.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jda.mobility.framework.extensions.security.UserPrincipal;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ChannelType;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("unittest")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser
public abstract class AbstractBaseControllerTest {
	@Before
    public void setUp(){
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
	}

}
