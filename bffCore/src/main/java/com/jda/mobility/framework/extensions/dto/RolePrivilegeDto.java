/**
 * 
 */
package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;

/**
 * The class RolePrivilegeDto.java
 * @author  HCL Technologies Ltd.
 */
@Data
public final class RolePrivilegeDto implements Serializable {

	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 3790692874856192598L;

	private final UUID id;
	private final String privilegeName;
}