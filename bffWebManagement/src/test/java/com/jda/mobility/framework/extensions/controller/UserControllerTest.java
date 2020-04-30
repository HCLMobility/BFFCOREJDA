/**
 * 
 */
package com.jda.mobility.framework.extensions.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.dto.AccessDto;
import com.jda.mobility.framework.extensions.dto.RoleMasterDto;
import com.jda.mobility.framework.extensions.entity.ProductMaster;
import com.jda.mobility.framework.extensions.model.AccessRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.ProductRole;
import com.jda.mobility.framework.extensions.model.ProductRolePermission;
import com.jda.mobility.framework.extensions.repository.MasterUserRepository;
import com.jda.mobility.framework.extensions.repository.ProductMasterRepository;
import com.jda.mobility.framework.extensions.security.UserPrincipal;
import com.jda.mobility.framework.extensions.service.impl.UserServiceImpl;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ChannelType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;
import com.jda.mobility.framework.extensions.utils.BffUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The class BffAccessControllerTest.java
 * 
 * @author ChittipalliN HCL Technologies Ltd.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("unittest")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser
public class UserControllerTest {
	private static final String userUrl = "/api/user/v1/";

	@MockBean
	private RestTemplate restTemplate;

	@MockBean
	private UserServiceImpl bffAccessService;
	@MockBean
	private MasterUserRepository masterUserRepository;
	@MockBean
	private ProductMasterRepository productMasterRepo;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testValidateUser() throws Exception {
		StringBuilder url = new StringBuilder(userUrl);
		url.append("SUPER");
		url.append("/");
		url.append("validation");
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Cookie", BffUtils.buildValidHeader(authCookie));
		HttpEntity<String> request = new HttpEntity<>(headers);
		when(productMasterRepo.findByName(Mockito.any())).thenReturn(getProductMaster());
		when(restTemplate.exchange("http://3.13.173.174:4500/api/user/v1beta/users/SUPER", HttpMethod.GET, request,
				Object.class)).thenReturn(
						new ResponseEntity<>(BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_VALIDATE_USER, HttpStatus.OK));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).header("SET_COOKIE", authCookie))
				.andExpect(status().isOk());
	}

	@Test
	public void testValidateUserHttpClientErrorException() throws Exception {
		StringBuilder url = new StringBuilder(userUrl);
		url.append("SUPER");
		url.append("/");
		url.append("validation");
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Cookie", BffUtils.buildValidHeader(authCookie));
		HttpEntity<String> request = new HttpEntity<>(headers);
		when(productMasterRepo.findByName(Mockito.any())).thenReturn(getProductMaster());
		when(restTemplate.exchange("http://3.13.173.174:4500/api/user/v1beta/users/SUPER", HttpMethod.GET, request,
				Object.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "HttpClientError"));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).header("SET_COOKIE", authCookie))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testValidateUserException() throws Exception {
		StringBuilder url = new StringBuilder(userUrl);
		url.append("SUPER");
		url.append("/");
		url.append("validation");
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Cookie", BffUtils.buildValidHeader(authCookie));
		HttpEntity<String> request = new HttpEntity<>(headers);
		when(restTemplate.exchange("http://3.13.173.174:4500/api/user/v1beta/users/SUPER", HttpMethod.GET, request,
				Object.class)).thenThrow(new RuntimeException());
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).header("SET_COOKIE", authCookie))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testFetchUserRole() throws Exception {
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
	    when(masterUserRepository.countByUserId(Mockito.any())).thenReturn(1);
		StringBuilder url = new StringBuilder(userUrl);
		url.append("layers");
		List<RoleMasterDto> roleList = new ArrayList<>();
		roleList.add(getRoleMasterDto());
		when(bffAccessService.getRoles()).thenReturn(buildResponse(roleList));
		mockMvc.perform(MockMvcRequestBuilders
				.get(url.toString())
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data[0].name").value("ThirdParty Implementor"));
	}

	@Test
	public void testMapUserRole() throws Exception {
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
	    when(masterUserRepository.countByUserId(Mockito.any())).thenReturn(1);
		StringBuilder url = new StringBuilder(userUrl);
		url.append("layers").append("/").append("map");
		when(bffAccessService.mapUserRole(Mockito.any())).thenReturn(buildResponse(getAccessDto()));
		mockMvc.perform(MockMvcRequestBuilders.post(url.toString()).content(asJsonString(getAccessRequest()))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.userId").value("SUPER"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.details.data.roleName").value("ThirdParty Implementor"));
	}

	@Test
	public void testCreateRoleMaster() throws Exception {
		String roleId = "043a765c-768b-486c-8632-5ae0efe505e4";
		StringBuilder url = new StringBuilder(userUrl);
		url.append("layers").append("/");
		when(bffAccessService.createRoleMaster(Mockito.anyString())).thenReturn(roleId);
		mockMvc.perform(MockMvcRequestBuilders.post(url.toString()).queryParam("roleName", "ThirdParty Implementor")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
				.andExpect(content().string(roleId));
	}

	@Test
	public void testCreatePrivilegeMaster() throws Exception {
		String privilegeMasterId = "043a765c-768b-486c-8632-5ae0efe505e4";
		StringBuilder url = new StringBuilder(userUrl);
		url.append("privileges").append("/");
		when(bffAccessService.createPrivilegeMaster(Mockito.anyString())).thenReturn(privilegeMasterId);
		mockMvc.perform(MockMvcRequestBuilders.post(url.toString())
				.queryParam("privilegeName", "ThirdParty Implementor").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(content().string(privilegeMasterId));
	}

	@Test
	public void testMapRolePrivilege() throws Exception {
		String privilegeId = "043a765c-768b-486c-8632-5ae0efe505e4";
		String roleId = "1d8ae7f4-2957-47eb-8243-a5d960abf084";
		String rolePrivilegeId = "167a0cae-d299-45a1-80fa-7aaf180bf6d7";
		StringBuilder url = new StringBuilder(userUrl);
		url.append("privileges").append("/").append("map").append("/");
		when(bffAccessService.mapRolePrivilege(Mockito.any(), Mockito.any())).thenReturn(rolePrivilegeId);
		mockMvc.perform(MockMvcRequestBuilders.post(url.toString()).queryParam("roleId", roleId)
				.queryParam("privilegeId", privilegeId).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andExpect(content().string(rolePrivilegeId));
	}

	@Test
	public void testFetchProductUserRoles() throws Exception {
		StringBuilder url = new StringBuilder(userUrl);
		url.append("productroles");
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Cookie", BffUtils.buildValidHeader(authCookie));
		HttpEntity<String> request = new HttpEntity<>(headers);
		when(productMasterRepo.findByName(Mockito.any())).thenReturn(getProductMaster());
		when(restTemplate.exchange("http://3.13.173.174:4500/api/user/v1beta/roles", HttpMethod.GET, request,
				Object.class)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).header("SET_COOKIE", authCookie))
				.andExpect(status().isOk());
	}

	@Test
	public void testFetchProductUserRolesException() throws Exception {
		StringBuilder url = new StringBuilder(userUrl);
		url.append("productroles");
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Cookie", BffUtils.buildValidHeader(authCookie));
		HttpEntity<String> request = new HttpEntity<>(headers);
		when(restTemplate.exchange("http://3.13.173.174:4500/api/user/v1beta/roles", HttpMethod.GET, request,
				ProductRole.class)).thenThrow(new RuntimeException());
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).header("SET_COOKIE", authCookie))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testFetchProductUserPermissions() throws Exception {
		StringBuilder url = new StringBuilder(userUrl);
		url.append("productperms");
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Cookie", BffUtils.buildValidHeader(authCookie));
		HttpEntity<String> request = new HttpEntity<>(headers);
		when(productMasterRepo.findByName(Mockito.any())).thenReturn(getProductMaster());
		when(restTemplate.exchange("http://3.13.173.174:4500/api/user/v1beta/permissions?type=MOBILE&roleId=USER_API",
				HttpMethod.GET, request, ProductRolePermission.class))
						.thenReturn(new ResponseEntity<ProductRolePermission>(HttpStatus.OK));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).queryParam("roleId", "USER_API").header("SET_COOKIE",
				authCookie)).andExpect(status().isOk());
	}

	@Test
	public void testFetchProductUserPermissionsElse() throws Exception {
		StringBuilder url = new StringBuilder(userUrl);
		url.append("productperms");
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Cookie", BffUtils.buildValidHeader(authCookie));
		HttpEntity<String> request = new HttpEntity<>(headers);
		when(productMasterRepo.findByName(Mockito.any())).thenReturn(getProductMaster());
		when(restTemplate.exchange("http://3.13.173.174:4500/api/user/v1beta/permissions?type=MOBILE", HttpMethod.GET, request,
				ProductRolePermission.class)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).header("SET_COOKIE", authCookie))
				.andExpect(status().isOk());
	}

	@Test
	public void testFetchProductUserPermissionsException() throws Exception {
		StringBuilder url = new StringBuilder(userUrl);
		url.append("productperms");
		String authCookie = "MOCA-WS-SESSIONKEY=%3Buid%3DSUPER%7Csid%3D445e6ce1-c896-46d7-bb35-0dc0115a1db0%7Cdt%3Dk0m6d597%7Csec%3DALL%3BJzOU.W1QXVEGgGKkrNIrxpvcB3";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Cookie", BffUtils.buildValidHeader(authCookie));
		HttpEntity<String> request = new HttpEntity<>(headers);
		when(restTemplate.exchange("http://3.13.173.174:4500/api/user/v1beta/permissions?roleId=USER_API&type=MOBILE",
				HttpMethod.GET, request, ProductRolePermission.class)).thenThrow(new RuntimeException());
		mockMvc.perform(MockMvcRequestBuilders.get(url.toString()).queryParam("roleId", "USER_API").header("SET_COOKIE",
				authCookie)).andExpect(status().isBadRequest());
	}

	private static <T> BffCoreResponse buildResponse(T data) {
		return BffUtils.buildResponse(data, BffResponseCode.FLOW_SUCCESS_CODE_CREATE_FLOW.getCode(), "message",
				"detailMessage", StatusCode.OK.getValue());
	}

	private String asJsonString(final Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
	}

	private RoleMasterDto getRoleMasterDto() {
		RoleMasterDto roleMasterDto = new RoleMasterDto(UUID.randomUUID(), "ThirdParty Implementor", 2, null, null, false);
		return roleMasterDto;
	}

	private AccessRequest getAccessRequest() {
		AccessRequest accessRequest = new AccessRequest();
		accessRequest.setUserId("SUPER");
		accessRequest.setSuperUser(true);
		accessRequest.setRoleId(UUID.randomUUID());
		return accessRequest;
	}

	private AccessDto getAccessDto() {
		return new AccessDto(UUID.randomUUID(), "SUPER", UUID.randomUUID(), "ThirdParty Implementor", false);
	}
	
	private ProductMaster getProductMaster(){
		ProductMaster productMaster = new ProductMaster();
		productMaster.setScheme("http");
		productMaster.setContextPath("3.13.173.174");
		productMaster.setPort("4500");
		return productMaster;
	}
}
