package com.jda.mobility.framework.extensions.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jda.iam.client.OIDCUtils;
import com.jda.iam.client.TokenValidator;
import com.jda.iam.config.ClientConfiguration;
import com.jda.iam.core.IAMException;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;


@RunWith(SpringJUnit4ClassRunner.class)
public class BffAuthorizationValidatorTest {
	
	@Mock
	private TokenValidator tokenValidator;
	@InjectMocks
	private BffAuthorizationValidator bffAuthorizationValidator;//= bffAuthValidator();
	
	@Mock
	private ClientConfiguration iamClientConfig;
	@InjectMocks
	private RequestProcessor RequestProcessor;// = new RequestProcessor(tokenValidator, iamClientConfig);
	@InjectMocks
	private BffAuthorizationValidator bffAuthValidator;// = bffAuthValidator();
    public BffAuthorizationValidator bffAuthValidator() {    	
    	return new BffAuthorizationValidator(tokenValidator, iamClientConfig);
    }
    @Test
    public void authorizationsTest() throws IAMException {  
    	Set<MethodAuthorizationMapping> mappings = new HashSet<>();
    	Set<String> methods  = new HashSet<>();
		methods.add(MethodAuthorizationMapping.Method.ALL.name());
		methods.add(MethodAuthorizationMapping.Method.GET.name());
		Set<String> authorizations  = new HashSet<>();
		authorizations.add("VALID");
		MethodAuthorizationMapping mapi = new MethodAuthorizationMapping(methods, authorizations);
		mappings.add(mapi);
    	BffAuthorizationValidator bffAuthorizationValidator= bffAuthValidator().authorizations(mappings) ;
    	assertTrue(bffAuthorizationValidator.mappings.size()>0);
    	
	}
    @Test
    public void overrideValidatorTest() throws IAMException{
    	ScopeValidator ScopeValidator= new ScopeValidator();
    	assertNotNull(bffAuthValidator().overrideValidator(ScopeValidator));
	}
    @Test
	public void authProcessorTest() throws IAMException {
    	@SuppressWarnings("unused")
		OIDCUtils oidscUtils = Mockito.any();
    	Map<String,Object> map = new HashMap<>();
    	map.put("aud", "wms");
    	map.put("scope", "openId");
    	map.put("sub", "AMANDA");
      
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", BffAdminConstantsUtils.BEARER.concat("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Impyb2JwNkhwZmFTc2pFMml3OWJ4NThUajlGZkVERllxVFdFQ2w4cDl0RlUifQ.eyJzdWIiOiJBTlRPTiIsImlkIjoiQU5UT04iLCJlbWFpbCI6ImFudG9uQGpkYS5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibm9uY2UiOiJOMC4yMTgzMzU0OTU4MjY5NzM3NjE1NzMyMDk1NzU1NTMiLCJhdF9oYXNoIjoibGc1by1PamlzZURBNnVXUWhPLTE2dyIsImF1ZCI6IndtcyIsImV4cCI6MTU3MzIxMzE4NSwiaWF0IjoxNTczMjA5NTg1LCJpc3MiOiJodHRwOi8vc2ltcGxlLW9pZGMtcHJvdmlkZXIifQ.HL4rshalBqlacICOeZElcRdJxCVw5n8eDMz9MCIMoJWXl7TT3Lw2iuJhpFmyOLA8h1-RXlvmuwEWbOcJ9dXfANBKMERVE_WemjfMoI2h5w1Krv6FQZIYx0rmoETXZXpF4SZSzQsa5Rj6Z5ACMDS9qjYjbUag-qEwnxqyyVszr_zT2JUj_v5uPI8NlGZ4L9GodqiGsQxo-0liigHM8UTyM03KpPGA4S1Chb-hP5afJQOselIm2f8C-bVldzr8UzAlF7l3_j2U7YbreN0SjOrtUn9NM6lql1P2Am-z_DjKvdZozT-oMVUEaLv1Ab1z0UAaKM5SYbZm-Zl7dfrsYpcBjg"));
		request.setMethod("GET");
		
		MockHttpServletResponse response = new MockHttpServletResponse();
		when(tokenValidator.validateAccessToken(OIDCUtils.getAuthorizationHeaderToken(request),Mockito.any())).thenReturn(map);
		Set<MethodAuthorizationMapping> mappings = new HashSet<>();
		Set<String> methods  = new HashSet<>();
		methods.add(MethodAuthorizationMapping.Method.ALL.name());
		methods.add(MethodAuthorizationMapping.Method.GET.name());
		Set<String> authorizations  = new HashSet<>();
		authorizations.add("VALID");
		MethodAuthorizationMapping mapi = new MethodAuthorizationMapping(methods, authorizations);
		mappings.add(mapi);
		BffAuthorizationValidator bffAuthValidator = bffAuthValidator();
		bffAuthValidator = bffAuthValidator.authorizations(mappings);
		//bffAuthValidator.authProcessor(request, response);
		assertFalse(bffAuthValidator.authProcessor(request, response));
		assertTrue(bffAuthValidator.mappings.size()>0);

	}
}
