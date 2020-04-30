/**
 * 
 */
package com.jda.mobility.framework.extensions.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jda.mobility.framework.extensions.entity.MasterUser;
import com.jda.mobility.framework.extensions.entity.PrivilegeMaster;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.entity.RolePrivilege;
import com.jda.mobility.framework.extensions.entity.UserRole;
import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.exception.DataBaseException;
import com.jda.mobility.framework.extensions.model.AccessRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.repository.MasterUserRepository;
import com.jda.mobility.framework.extensions.repository.PrivilegeMasterRepository;
import com.jda.mobility.framework.extensions.repository.ResourceBundleRepository;
import com.jda.mobility.framework.extensions.repository.RoleMasterRepository;
import com.jda.mobility.framework.extensions.repository.RolePrivilegeRepository;
import com.jda.mobility.framework.extensions.repository.UserRoleRepository;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.UserRoleActionType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 * JUnit test class for UserServiceImpl
 * 
 * @author HCL Technologies
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceImplTest extends AbstractPrepareTest {

	@InjectMocks
	private UserServiceImpl userServiceImpl;
	@Mock
	private RoleMasterRepository roleMasterRepository;
	@Mock
	private ResourceBundleRepository resourceBundleRepo;
	@Mock
	private UserRoleRepository userRoleRepository;
	@Mock
	private PrivilegeMasterRepository privilegeMasterRepository;
	@Mock
	private RolePrivilegeRepository rolePrivilegeRepository;
	@Mock
	private MasterUserRepository masterUserRepository;

	/**
	 * Test method for GetRoles
	 */
	@Test
	public void testGetRoles() {
		when(roleMasterRepository.findAllByOrderByLevelAsc()).thenReturn(getroleList());

		BffCoreResponse response = userServiceImpl.getRoles();

		assertEquals(BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_FETCH_ROLES.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for GetRoles_DBExcption
	 */
	@Test
	public void testGetRolesDBExcption() {
		when(roleMasterRepository.findAllByOrderByLevelAsc()).thenThrow(new DataBaseException("Layer list retrieval failed"));
		BffCoreResponse response = userServiceImpl.getRoles();
		assertEquals(BffResponseCode.ERR_ACCESS_SERVICE_API_FETCH_ROLES.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	@Test
	public void testGetRolesForUser() {
		when(userRoleRepository.findByUserId(Mockito.any())).thenReturn(Optional.of(getUserRole()));
		when(roleMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(getRoleMaster()));
		BffCoreResponse response = userServiceImpl.getRolesForUser("SUPER");
		assertEquals(BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_ROLES_PARTICULAR_USER.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testGetRolesForUserEmptyUserRole() {
		when(userRoleRepository.findByUserId(Mockito.any())).thenReturn(Optional.empty());		
		BffCoreResponse response = userServiceImpl.getRolesForUser("SUPER");
		assertEquals(BffResponseCode.ERR_ACCESS_SERVICE_API_ROLES_PARTICULAR_USER.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for GetRoles_DBExcption
	 */
	@Test
	public void testGetRolesForUserDBExcption() {
		when(userRoleRepository.findByUserId(Mockito.any())).thenThrow(new DataBaseException("Layer retrival failed"));
		BffCoreResponse response = userServiceImpl.getRolesForUser("SUPER");
		assertEquals(BffResponseCode.DB_ERR_ACCESS_SERVICE_API_ROLES_PARTICULAR_USER.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for MapUserRole
	 */
	@Test
	public void testMapUserRole() {
		String uuid = "eec6e916-b409-4f13-baf1-7728302b91de";
		when(userRoleRepository.findByUserId(BffAdminConstantsUtils.EMPTY_SPACES)).thenReturn(Optional.of(getUserRole()));
		when(userRoleRepository.save(Mockito.any())).thenReturn(getUserRole());
		when(roleMasterRepository.findById(UUID.fromString(uuid))).thenReturn(Optional.of(getRoleMaster()));
		BffCoreResponse response = userServiceImpl.mapUserRole(getAccessRequest());
		assertEquals(BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_MODIFY_USER_ROLE.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
		when(userRoleRepository.findByUserId(BffAdminConstantsUtils.EMPTY_SPACES)).thenReturn(Optional.empty());
		BffCoreResponse response1 = userServiceImpl.mapUserRole(getAccessRequest1());
		assertEquals(BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_USER_ROLE_MAPPING.getCode(), response1.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
		when(userRoleRepository.findByUserId(BffAdminConstantsUtils.EMPTY_SPACES)).thenReturn(Optional.of(getUserRole()));
		BffCoreResponse response2 = userServiceImpl.mapUserRole(getAccessRequest1());
		assertEquals(BffResponseCode.DB_ERR_ACCESS_SERVICE_API_USER_ROLE_MAPPING_EXISTS.getCode(), response2.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response2.getHttpStatusCode());
	}
	

	@Test
	public void testMapUserRoleSuper() {
		String uuid = "eec6e916-b409-4f13-baf1-7728302b91de";
		MasterUser masterUser = new MasterUser();
		masterUser.setUid(UUID.fromString("eec6e916-b409-4f13-baf1-7728302b91de"));
		when(masterUserRepository.save(Mockito.any())).thenReturn(masterUser);
		when(masterUserRepository.findByUserId(BffAdminConstantsUtils.SUPER)).thenReturn(Optional.empty());
		when(userRoleRepository.save(Mockito.any())).thenReturn(getUserRole());
		when(roleMasterRepository.findById(UUID.fromString(uuid))).thenReturn(Optional.of(getRoleMaster()));
		BffCoreResponse response = userServiceImpl.mapUserRole(getAccessRequestSuper());
		assertEquals(BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_USER_ROLE_MAPPING.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}
	
	@Test
	public void testMapUserRoleSuperExist() {
		String uuid = "eec6e916-b409-4f13-baf1-7728302b91de";
		MasterUser masterUser = new MasterUser();
		when(masterUserRepository.findByUserId(BffAdminConstantsUtils.SUPER)).thenReturn(Optional.of(masterUser));
		when(userRoleRepository.save(Mockito.any())).thenReturn(getUserRole());
		when(roleMasterRepository.findById(UUID.fromString(uuid))).thenReturn(Optional.of(getRoleMaster()));
		BffCoreResponse response = userServiceImpl.mapUserRole(getAccessRequestSuper());
		assertEquals(BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_USER_ROLE_MAPPING.getCode(), response.getCode());
		assertEquals(StatusCode.CREATED.getValue(), response.getHttpStatusCode());
	}
	
	private AccessRequest getAccessRequestSuper() {
		AccessRequest accessRequest = new AccessRequest();
		accessRequest.setUserId(BffAdminConstantsUtils.SUPER);
		accessRequest.setSuperUser(true);
		accessRequest.setId(UUID.fromString("e8fcb1a7-d453-4ee3-8aaa-b0bd230e27f3"));
		accessRequest.setRoleId(UUID.fromString("eec6e916-b409-4f13-baf1-7728302b91de"));
		
		accessRequest.setActionType(UserRoleActionType.ADD_USER_ROLE.getType());
		return accessRequest;
	}


	/**
	 * Test method for MapUserRole_Excption
	 */
	@Test
	public void testMapUserRoleExcption() {
		AccessRequest accessRequest = new AccessRequest();
		accessRequest.setSuperUser(true);
		BffCoreResponse response = userServiceImpl.mapUserRole(accessRequest);
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
		assertEquals(BffResponseCode.ERR_ACCESS_API_VALIDATE_ENUM_CHECK_SEARCH.getCode(), response.getCode());
		when(masterUserRepository.findByUserId(Mockito.any())).thenThrow(new BffException(BffAdminConstantsUtils.EXP_MSG));
		BffCoreResponse response1 = userServiceImpl.mapUserRole(getAccessRequest1());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
		assertEquals(BffResponseCode.ERR_ACCESS_SERVICE_API_USER_ROLE_MAPPING.getCode(), response1.getCode());
	}

	/**
	 * Test method for testMapUserRole_DBExcption
	 */
	@Test
	public void testMapUserRoleDBExcption() {
		String uuid = "eec6e916-b409-4f13-baf1-7728302b91de";
		when(userRoleRepository.findByUserId(BffAdminConstantsUtils.EMPTY_SPACES)).thenReturn(Optional.of(getUserRole()));
		when(roleMasterRepository.findById(UUID.fromString(uuid))).thenThrow(new DataBaseException("User and Layer mapping failed"));
		BffCoreResponse response = userServiceImpl.mapUserRole(getAccessRequest());
		assertEquals(BffResponseCode.DB_ERR_ACCESS_SERVICE_API_USER_ROLE_MAPPING.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
		
	}

	/**
	 * Test method for ModifyUserRole
	 */
	@Test
	public void testModifyUserRole() {
		String uuid = "eec6e916-b409-4f13-baf1-7728302b91de";
		String id = "e8fcb1a7-d453-4ee3-8aaa-b0bd230e27f3";
		when(userRoleRepository.save(Mockito.any())).thenReturn(newUserRole());
		when(userRoleRepository.findById(UUID.fromString(id))).thenReturn(Optional.of(getUserRole()));
		when(roleMasterRepository.findById(UUID.fromString(uuid))).thenReturn(Optional.of(getRoleMaster()));
		BffCoreResponse response = userServiceImpl.modifyUserRole(getAccessRequest(), new UserRole());
		assertEquals(BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_MODIFY_USER_ROLE.getCode(), response.getCode());
		assertEquals(StatusCode.OK.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for ModifyUserRole_Excption
	 */
	@Test
	public void testModifyUserRoleExcption() {
		BffCoreResponse response = userServiceImpl.modifyUserRole(new AccessRequest(), new UserRole());
		assertEquals(BffResponseCode.ERR_ACCESS_SERVICE_API_MODIFY_USER_ROLE.getCode(), response.getCode());
		assertEquals(StatusCode.BADREQUEST.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for ModifyUserRole_DBExcption
	 */
	@Test
	public void testModifyUserRoleDBExcption() {
		when(roleMasterRepository.findById(Mockito.any())).thenThrow(new DataBaseException("User and Layer mapping update failed"));
		BffCoreResponse response = userServiceImpl.modifyUserRole(getAccessRequest(), new UserRole());
		assertEquals(BffResponseCode.DB_ERR_ACCESS_SERVICE_API_MODIFY_USER_ROLE.getCode(), response.getCode());
		assertEquals(StatusCode.INTERNALSERVERERROR.getValue(), response.getHttpStatusCode());
	}

	/**
	 * Test method for CreateRoleMaster
	 */
	@Test
	public void testCreateRoleMaster() {
		when(roleMasterRepository.save(Mockito.any())).thenReturn(getRoleMaster());
		String response = userServiceImpl.createRoleMaster(Mockito.anyString());
		assertEquals("1e82c433-fb5b-4257-8f79-ac4998569b4c", response);
	}

	/**
	 * Test method for CreatePrivilegeMaster
	 */
	@Test
	public void testCreatePrivilegeMaster() {
		when(privilegeMasterRepository.save(Mockito.any())).thenReturn(getPrivilegeMaster());
		String response = userServiceImpl.createPrivilegeMaster(Mockito.anyString());
		assertEquals("3e82c433-fb5b-4257-8f79-ac4998569b4c", response);
	}

	/**
	 * Test method for MapRolePrivilege
	 */
	@Test
	public void testMapRolePrivilege() {
		when(rolePrivilegeRepository.save(Mockito.any())).thenReturn(getRolePrivilege());
		when(roleMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(getRoleMaster()));
		when(privilegeMasterRepository.findById(Mockito.any())).thenReturn(Optional.of(getPrivilegeMaster()));
		when(rolePrivilegeRepository.findByPrivilegeMasterAndRoleMaster(getPrivilegeMaster(),getRoleMaster())).thenReturn(null);
		String response = userServiceImpl.mapRolePrivilege(UUID.randomUUID(), UUID.randomUUID());
		assertEquals("2e82c433-fb5b-4257-8f79-ac4998569b4c", response);
		when(rolePrivilegeRepository.findByPrivilegeMasterAndRoleMaster(getPrivilegeMaster(),getRoleMaster())).thenReturn(new RolePrivilege());
		String response1 = userServiceImpl.mapRolePrivilege(UUID.randomUUID(), UUID.randomUUID());
		assertEquals("Already Role and privileges are mapped to each other", response1);
		
	}

	/**
	 * @return List<RoleMaster>
	 */
	private List<RoleMaster> getroleList() {
		List<RoleMaster> role = null;
		List<RoleMaster> roleMasterList = new ArrayList<>();
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setName("admin");
		roleMaster.setUid(UUID.randomUUID());
		List<RolePrivilege> rolePrivilegeList = new ArrayList<RolePrivilege>();
		RolePrivilege rolePrivilege = new RolePrivilege();
		rolePrivilege.setUid(UUID.randomUUID());
		PrivilegeMaster privilegeMaster = new PrivilegeMaster();
		privilegeMaster.setName("");
		rolePrivilege.setPrivilegeMaster(privilegeMaster);
		rolePrivilegeList.add(rolePrivilege);
		roleMaster.setRolePrivileges(rolePrivilegeList);
		roleMasterList.add(roleMaster);
		UserRole userRole = new UserRole();
		userRole.setUid(UUID.randomUUID());
		userRole.setUserId("");
		userRole.setRoleMaster(roleMaster);
		List<UserRole> userRoleList = new ArrayList<UserRole>();
		userRoleList.add(userRole);
		roleMaster.setUserRoles(userRoleList);
		role = roleMasterList;
		return role;
	}

	/**
	 * @return AccessRequest
	 */
	private AccessRequest getAccessRequest() {
		AccessRequest accessRequest = new AccessRequest();
		accessRequest.setUserId("");
		accessRequest.setId(UUID.fromString("e8fcb1a7-d453-4ee3-8aaa-b0bd230e27f3"));
		accessRequest.setRoleId(UUID.fromString("eec6e916-b409-4f13-baf1-7728302b91de"));
		accessRequest.setSuperUser(false);
		accessRequest.setActionType(UserRoleActionType.MODIFY_USER_ROLE.getType());
		return accessRequest;
	}
	private AccessRequest getAccessRequest1() {
		AccessRequest accessRequest = new AccessRequest();
		accessRequest.setUserId("");
		accessRequest.setSuperUser(false);
		accessRequest.setId(UUID.fromString("e8fcb1a7-d453-4ee3-8aaa-b0bd230e27f3"));
		accessRequest.setRoleId(UUID.fromString("eec6e916-b409-4f13-baf1-7728302b91de"));
		
		accessRequest.setActionType(UserRoleActionType.ADD_USER_ROLE.getType());
		return accessRequest;
	}
	
	/**
	 * @return RoleMaster
	 */
	private RoleMaster getRoleMaster() {
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setUid(UUID.fromString("1e82c433-fb5b-4257-8f79-ac4998569b4c"));
		return roleMaster;
	}

	/**
	 * @return PrivilegeMaster
	 */
	private PrivilegeMaster getPrivilegeMaster() {
		PrivilegeMaster privilegeMaster = new PrivilegeMaster();
		privilegeMaster.setUid(UUID.fromString("3e82c433-fb5b-4257-8f79-ac4998569b4c"));
		return privilegeMaster;
	}

	/**
	 * @return RolePrivilege
	 */
	private RolePrivilege getRolePrivilege() {
		RolePrivilege rolePrivilege = new RolePrivilege();
		rolePrivilege.setUid(UUID.fromString("2e82c433-fb5b-4257-8f79-ac4998569b4c"));
		return rolePrivilege;
	}

	/**
	 * @return UserRole
	 */
	private UserRole getUserRole() {
		UserRole userRole = new UserRole();
		userRole.setUid(UUID.fromString("e8fcb1a7-d453-4ee3-8aaa-b0bd230e27f3"));
		userRole.setUserId("TEST");
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setUid(UUID.randomUUID());
		roleMaster.setName("TEST");
		userRole.setRoleMaster(roleMaster);
		return userRole;
	}

	/**
	 * @return UserRole
	 */
	private UserRole newUserRole() {
		UserRole userRole = new UserRole();
		userRole.setUid(UUID.randomUUID());
		userRole.setUserId("TEST");
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setUid(UUID.randomUUID());
		userRole.setRoleMaster(roleMaster);
		return userRole;
	}
}
