package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;

/**
 * The class RoleDto.java
 * 
 * @author HCL Technologies Ltd.
 */
@Data @Builder
@JsonInclude(Include.NON_NULL)
public class UserAuthDto implements Serializable{

	private static final long serialVersionUID = -8625762873034498849L;

	private String userId;	
	private String authToken;	
	private String prdAuthCookie;	
	private List<String> roleIds;	
	private List<String> permissionIds;
	private boolean superUser;
	private int openSessionCount;
	private String sessionRecMode;
	private List<TranslationDto> localizedResBundleEntries;
}
