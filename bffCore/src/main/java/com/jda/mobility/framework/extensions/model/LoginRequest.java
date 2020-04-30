package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ChannelType;

import lombok.Data;
@Data
public class LoginRequest implements Serializable{
	private static final long serialVersionUID = -50623495536668650L;

    private String userId;
    private String password;
    private ChannelType channel;
    @NotBlank
    private String version;
    @NotBlank
    private String tenant;
    @NotBlank
    private String locale;
    private String deviceId;
}