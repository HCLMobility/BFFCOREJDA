package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * The class UserNameDto.java
 * 
 * @author HCL Technologies Ltd.
 */
@Data
public class UserNameDto implements Serializable{

	private static final long serialVersionUID = -8685429545693914068L;
	
	private String userId;
	private boolean valid;
	private boolean superUser;
}
