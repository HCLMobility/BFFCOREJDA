/**
 * 
 */
package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * The class RoleMasterDto.java
 * @author HCL Technologies Ltd.
 */
@Data
@JsonInclude(Include.NON_NULL)
public final class RoleMasterDto implements Serializable {
	
	private static final long serialVersionUID = 4377306606820775778L;
	private final UUID id;	
	private final String name;	
	private final int level;		
	private final List<RolePrivilegeDto> rolePrivileges;	
	private final List<AccessDto> userRoles;
	
	private final boolean isSuperUser;
}
