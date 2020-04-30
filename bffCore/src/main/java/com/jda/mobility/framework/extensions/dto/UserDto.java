package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;

/**
 * The class UserAuthDto.java which hold the data to be passed onto the consumer 
 * with the role and privilege information
 * 
 * @author HCL Technologies Ltd.
 */
@Data
@JsonInclude(Include.NON_NULL)
@Builder
public class UserDto implements Serializable{
	
	private static final long serialVersionUID = 7711841045642746078L;

	private String userId;	
	private String authToken;	
	private String prdAuthCookie;	
	private RoleMasterDto roleMasterDto;	
	private boolean superUser;
}
