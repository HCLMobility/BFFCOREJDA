/**
 * 
 */
package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * The class AccessDto.java
 * HCL Technologies Ltd.
 */
@Data
public final class AccessDto implements Serializable{

	private static final long serialVersionUID = 1953500412220371494L;
	private final UUID id;
	private final String userId;
	@JsonInclude(Include.NON_NULL)
	private final UUID roleId;
	@JsonInclude(Include.NON_NULL)
	private final String roleName;	
	private final boolean superUser;
}
