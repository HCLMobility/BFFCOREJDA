package com.jda.mobility.framework.extensions.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.jda.mobility.framework.extensions.common.BffResponse;
import com.jda.mobility.framework.extensions.dto.AccessDto;
import com.jda.mobility.framework.extensions.dto.RoleMasterDto;
import com.jda.mobility.framework.extensions.dto.RolePrivilegeDto;
import com.jda.mobility.framework.extensions.entity.MasterUser;
import com.jda.mobility.framework.extensions.entity.PrivilegeMaster;
import com.jda.mobility.framework.extensions.entity.RoleMaster;
import com.jda.mobility.framework.extensions.entity.RolePrivilege;
import com.jda.mobility.framework.extensions.entity.UserRole;
import com.jda.mobility.framework.extensions.model.AccessRequest;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.repository.MasterUserRepository;
import com.jda.mobility.framework.extensions.repository.PrivilegeMasterRepository;
import com.jda.mobility.framework.extensions.repository.RoleMasterRepository;
import com.jda.mobility.framework.extensions.repository.RolePrivilegeRepository;
import com.jda.mobility.framework.extensions.repository.UserRoleRepository;
import com.jda.mobility.framework.extensions.service.UserService;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils;
import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.UserRoleActionType;
import com.jda.mobility.framework.extensions.utils.BffMessageConstantsUtils.StatusCode;
import com.jda.mobility.framework.extensions.utils.BffResponseCode;

/**
 * The class implements to fetch, create, update and delete - User layer and privileges
 * Also map user with layers and privileges
 * 
 * @author HCL Technologies Ltd.
 */
@Service
public class UserServiceImpl implements UserService {
	private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);
	@Autowired
	private RoleMasterRepository roleMasterRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private BffResponse bffResponse;
	@Autowired
	private PrivilegeMasterRepository privilegeMasterRepository;
	@Autowired
	private RolePrivilegeRepository rolePrivilegeRepository;
	@Autowired
	private MasterUserRepository masterUserRepository;

	/**
	 * Retrieve the list of layers from RoleMaster 
	 * 
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse getRoles() {
		BffCoreResponse bffCoreResponse = null;
		try {
			Iterable<RoleMaster> roles = roleMasterRepository.findAllByOrderByLevelAsc();
			List<RoleMasterDto> roleList = new ArrayList<>();
			Iterator<RoleMaster> itr = roles.iterator();
			while (itr.hasNext()) {
				RoleMaster rm = itr.next();
				roleList.add(convertRoleMasterToRoleMasterDto(rm, false,BffAdminConstantsUtils.EMPTY));
			}
			bffCoreResponse = bffResponse.response(roleList, BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_FETCH_ROLES,
					BffResponseCode.ACCESS_SERVICE_USER_CODE_FETCH_ROLES, StatusCode.OK);
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_ACCESS_SERVICE_API_FETCH_ROLES,
					BffResponseCode.ERR_ACCESS_SERVICE_USER_FETCH_ROLES), StatusCode.INTERNALSERVERERROR);

		}
		return bffCoreResponse;
	}

	/**
	 * Retrieve the Layer from RoleMaster for given user
	 * 
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse getRolesForUser(String userId) {
		BffCoreResponse bffCoreResponse = null;
		try {
			Optional<UserRole> userRole = userRoleRepository.findByUserId(userId);
			if (userRole.isEmpty()) {
				bffCoreResponse = bffResponse
						.errResponse(
								List.of(BffResponseCode.ERR_ACCESS_SERVICE_API_ROLES_PARTICULAR_USER,
										BffResponseCode.ERR_ACCESS_SERVICE_USER_ROLES_PARTICULAR_USER),
								StatusCode.BADREQUEST);
			} else {
				Optional<RoleMaster> roleMaster = roleMasterRepository
						.findById(userRole.orElseThrow().getRoleMaster().getUid());
				bffCoreResponse = bffResponse.response(convertRoleMasterToRoleMasterDto(roleMaster.orElseThrow(), true,userId),
						BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_ROLES_PARTICULAR_USER,
						BffResponseCode.ACCESS_SERVICE_USER_CODE_ROLES_PARTICULAR_USER, StatusCode.OK);
			}
		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_ACCESS_SERVICE_API_ROLES_PARTICULAR_USER,
							BffResponseCode.DB_ERR_ACCESS_SERVICE_USER_ROLES_PARTICULAR_USER),
					StatusCode.INTERNALSERVERERROR);

		}
		return bffCoreResponse;
	}

	/**
	 * Maps the user with respective layer
	 * 
	 * @param accessRequest
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse mapUserRole(AccessRequest accessRequest) {
		BffCoreResponse bffCoreResponse = null;
		try {
			String actionType = accessRequest.getActionType() != null ? accessRequest.getActionType()
					: BffAdminConstantsUtils.EMPTY;
			if (!EnumUtils.isValidEnum(UserRoleActionType.class, actionType)) {
				bffCoreResponse = bffResponse
						.errResponse(
								List.of(BffResponseCode.ERR_ACCESS_API_VALIDATE_ENUM_CHECK_SEARCH,
										BffResponseCode.ERR_ACCESS_USER_VALIDATE_ENUM_CHECK_SEARCH),
								StatusCode.BADREQUEST);
				return bffCoreResponse;
			}

			 if (UserRoleActionType.MODIFY_USER_ROLE.getType().equals(accessRequest.getActionType())) {
				// Get the existing user role
				UserRole existingUserRole = userRoleRepository.findByUserId(accessRequest.getUserId()).orElseThrow();
				
				 updateRoleMaster(accessRequest, existingUserRole);
				
				boolean masterUser = addOrDeleteSuperUser(accessRequest);
				
				LOGGER.debug("User role modified succesfully");
				AccessDto accessDto = convertUserRoleToAccessDto(existingUserRole,masterUser);
				bffCoreResponse = bffResponse.response(accessDto,
						BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_MODIFY_USER_ROLE,
						BffResponseCode.ACCESS_SERVICE_DEL_SUCC_USER_CODE_USER_ROLE_MAPPING, StatusCode.OK);
			}
			// If not present , create a new one
			else if (UserRoleActionType.ADD_USER_ROLE.getType().equals(accessRequest.getActionType())) {
				// Get the existing user role
				Optional<UserRole> existingUserRole = userRoleRepository.findByUserId(accessRequest.getUserId());
				// If present , then throw and error that user is already mapped to a layer
				if (existingUserRole.isPresent()) {
					return bffResponse.errResponse(
							List.of(BffResponseCode.DB_ERR_ACCESS_SERVICE_API_USER_ROLE_MAPPING_EXISTS,
									BffResponseCode.DB_ERR_ACCESS_SERVICE_USER_USER_ROLE_MAPPING_EXISTS),
							StatusCode.BADREQUEST, null,
							existingUserRole.get().getRoleMaster().getName());
				}
				UserRole userRole = convertAccessRequestToUserRole(accessRequest);
				
				updateRoleMaster(accessRequest, userRole);
				
				boolean masterUser = addOrDeleteSuperUser(accessRequest);
				
				AccessDto accessDto = convertUserRoleToAccessDto(userRole,masterUser);
				
				bffCoreResponse = bffResponse.response(accessDto,
						BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_USER_ROLE_MAPPING,
						BffResponseCode.ACCESS_SERVICE_USER_CODE_USER_ROLE_MAPPING, StatusCode.CREATED);
			}
			else if (UserRoleActionType.DELETE_USER_ROLE.getType().equals(accessRequest.getActionType())) {
				deleteUser(accessRequest);
				
				bffCoreResponse = bffResponse.response(BffAdminConstantsUtils.EMPTY_SPACES,
						BffResponseCode.ACCESS_SERVICE_DEL_SUCC_CODE_USER_ROLE_MAPPING,
						BffResponseCode.ACCESS_SERVICE_DEL_SUCC_USER_CODE_USER_ROLE_MAPPING, StatusCode.OK);
			}

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_ACCESS_SERVICE_API_USER_ROLE_MAPPING,
							BffResponseCode.DB_ERR_ACCESS_SERVICE_USER_USER_ROLE_MAPPING),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_ACCESS_SERVICE_API_USER_ROLE_MAPPING,
					BffResponseCode.ERR_ACCESS_SERVICE_USER_USER_ROLE_MAPPING), StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**Method to update user and layer information
	 * 
	 * @param accessRequest
	 * @param existingUserRole
	 * @return
	 */
	private UserRole updateRoleMaster(AccessRequest accessRequest, UserRole existingUserRole) {
		if(accessRequest.getRoleId()!=null)
		{
			Optional<RoleMaster> roleMaster = roleMasterRepository.findById(accessRequest.getRoleId());
			if(roleMaster.isPresent())
			{
				existingUserRole.setRoleMaster(roleMaster.get());
				//Updating User_role
				existingUserRole = userRoleRepository.save(existingUserRole);
			}
		}
		return existingUserRole;
	}

	/**Method to delete the association between User and Layer 
	 * 
	 * @param accessRequest
	 */
	private void deleteUser(AccessRequest accessRequest) {
		// Get the existing user role
		Optional<UserRole> existingUserRole = userRoleRepository.findByUserId(accessRequest.getUserId());
		
		if(existingUserRole.isPresent())
		{
			//Delete from User Role
			userRoleRepository.delete(existingUserRole.get());
		}
		if(accessRequest.isSuperUser())
		{
			Optional<MasterUser> optMasterUser = masterUserRepository.findByUserId(accessRequest.getUserId());
			if(optMasterUser.isPresent())
			{
				//Delete from master_use
				masterUserRepository.delete(optMasterUser.get());
			}
		}
	}

	/**Method to add or delete SUPER user information in Master_user table
	 * 
	 * @param accessRequest
	 * @return
	 */
	private boolean addOrDeleteSuperUser(AccessRequest accessRequest) {
		boolean superUser = false;
		//Get Super user
		Optional<MasterUser> optMasterUser = masterUserRepository.findByUserId(accessRequest.getUserId());
		
		if(accessRequest.isSuperUser() )
		{
			superUser = true;
			//Insert into master_user
			if (!optMasterUser.isPresent()) {
				MasterUser masterUser = new MasterUser();
				masterUser.setUserId(accessRequest.getUserId());
				masterUserRepository.save(masterUser);	
			}
		}
		else if(optMasterUser.isPresent())
		{
			//Delete from master_use
			masterUserRepository.delete(optMasterUser.get());
		}
		return superUser;
	}

	/**
	 * Method to modify the layer information for given user
	 * 
	 * @param accessRequest
	 * @return BffCoreResponse
	 */
	@Override
	public BffCoreResponse modifyUserRole(AccessRequest accessRequest, UserRole existingUserRole) {
		BffCoreResponse bffCoreResponse = null;
		try {
			RoleMaster role = roleMasterRepository.findById(accessRequest.getRoleId()).orElseThrow();
			existingUserRole.setRoleMaster(role);
			Optional.ofNullable(accessRequest.getUserId()).ifPresent(existingUserRole::setUserId);
			UserRole userRole1 = userRoleRepository.save(existingUserRole);
			LOGGER.debug("User role updated successfully");
			
			boolean masterUser = addOrDeleteSuperUser(accessRequest);
			
			AccessDto accessDto = convertUserRoleToAccessDto(userRole1,masterUser);

			bffCoreResponse = bffResponse.response(accessDto,
					BffResponseCode.ACCESS_SERVICE_SUCCESS_CODE_MODIFY_USER_ROLE,
					BffResponseCode.ACCESS_SERVICE_USER_CODE_MODIFY_USER_ROLE, StatusCode.OK);

		} catch (DataAccessException exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(
					List.of(BffResponseCode.DB_ERR_ACCESS_SERVICE_API_MODIFY_USER_ROLE,
							BffResponseCode.DB_ERR_ACCESS_SERVICE_USER_MODIFY_USER_ROLE),
					StatusCode.INTERNALSERVERERROR);
		} catch (Exception exp) {
			LOGGER.log(Level.ERROR, exp.getLocalizedMessage(), exp);
			bffCoreResponse = bffResponse.errResponse(List.of(BffResponseCode.ERR_ACCESS_SERVICE_API_MODIFY_USER_ROLE,
					BffResponseCode.ERR_ACCESS_SERVICE_USER_MODIFY_USER_ROLE), StatusCode.BADREQUEST);
		}
		return bffCoreResponse;
	}

	/**
	 * Convert AccessRequest to UserRole
	 * 
	 * @param accessRequest
	 * @return UserRole
	 */
	private UserRole convertAccessRequestToUserRole(AccessRequest accessRequest) {
		UserRole userRole = new UserRole();
		userRole.setUserId(accessRequest.getUserId());
		return userRole;
	}

	/**
	 * Convert UserRole to AccessDto
	 * 
	 * @param userRole
	 * @param optMasterUser 
	 * @return AccessDto
	 */
	private AccessDto convertUserRoleToAccessDto(UserRole userRole, boolean isSuperUser) {
		
		return new AccessDto(userRole.getUid(), userRole.getUserId(), 
				(userRole.getRoleMaster()!=null) ? userRole.getRoleMaster().getUid(): null,
				(userRole.getRoleMaster()!=null) ? userRole.getRoleMaster().getName() : null,isSuperUser);
	}

	/**
	 * Convert RoleMaster to RoleMasterDto
	 * 
	 * @param roleMaster
	 * @return RoleMasterDto
	 */
	private RoleMasterDto convertRoleMasterToRoleMasterDto(RoleMaster roleMaster, boolean ignoreUserRoles,String userId) {
		List<RolePrivilegeDto> privilegesDtoLst = null;
		if (roleMaster.getRolePrivileges() != null && !roleMaster.getRolePrivileges().isEmpty()) {
			privilegesDtoLst = new ArrayList<>();
			for (RolePrivilege rolePrivilege : roleMaster.getRolePrivileges()) {
				privilegesDtoLst.add(convertToRolePrivilegesrDto(rolePrivilege));
			}
		}
		List<AccessDto> accessDtoLst = null;

		if (!ignoreUserRoles && roleMaster.getUserRoles() != null && !roleMaster.getUserRoles().isEmpty()) {
			accessDtoLst = new ArrayList<>();
			for (UserRole userRole : roleMaster.getUserRoles()) {
				accessDtoLst.add(convertUserRoleToAccessDto(userRole,masterUserRepository.countByUserId(userRole.getUserId()) > 0));
			}
		}
		
		boolean isSuperUser = false;
		
		if(!userId.isEmpty() && (masterUserRepository.countByUserId(userId) > 0))
		{
			isSuperUser = true;
		}
		
		return new RoleMasterDto(roleMaster.getUid(), roleMaster.getName(),
				roleMaster.getLevel(), privilegesDtoLst, accessDtoLst,isSuperUser);
	}

	/**
	 * Convert RolePrivilege to RolePrivilegeDto
	 * 
	 * @param rolePrivilege
	 * @return RolePrivilegeDto
	 */
	private RolePrivilegeDto convertToRolePrivilegesrDto(RolePrivilege rolePrivilege) {
		return new RolePrivilegeDto(rolePrivilege.getUid(),
				rolePrivilege.getPrivilegeMaster().getName());
	}

	/**
	 * Method to add user layers
	 * 
	 * @param name
	 * @return RoleMaster UUID
	 */
	@Override
	public String createRoleMaster(String name) {
		RoleMaster roleMaster = new RoleMaster();
		roleMaster.setName(name);
		roleMaster = roleMasterRepository.save(roleMaster);
		return roleMaster.getUid().toString();
	}

	/**
	 * Method to add user privileges
	 * 
	 * @param name
	 * @return PrivilegeMaster UUID
	 */
	@Override
	public String createPrivilegeMaster(String name) {
		PrivilegeMaster privilegeMaster = new PrivilegeMaster();
		privilegeMaster.setName(name);
		privilegeMaster = privilegeMasterRepository.save(privilegeMaster);
		return privilegeMaster.getUid().toString();
	}

	/**
	 * Maps User with respective Privilege
	 * 
	 * @param roleId
	 * @param privilegeId
	 * @return RolePrivilege uuid
	 */
	@Override
	public String mapRolePrivilege(UUID roleId, UUID privilegeId) {
		RolePrivilege rolePrivilege = new RolePrivilege();
		RoleMaster roleMaster = roleMasterRepository.findById(roleId).orElseThrow();
		PrivilegeMaster privilegeMaster = privilegeMasterRepository.findById(privilegeId).orElseThrow();
		rolePrivilege.setRoleMaster(roleMaster);
		rolePrivilege.setPrivilegeMaster(privilegeMaster);

		RolePrivilege rtrvRolePrivilege = rolePrivilegeRepository.findByPrivilegeMasterAndRoleMaster(privilegeMaster,
				roleMaster);
		if (rtrvRolePrivilege != null) {
			return "Already Role and privileges are mapped to each other";
		} else {
			rolePrivilege = rolePrivilegeRepository.save(rolePrivilege);
		}
		return rolePrivilege.getUid().toString();
	}

}