package com.jda.mobility.framework.extensions.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.ChannelType;

import lombok.Builder;
import lombok.Getter;

/**
 * The class UserPrincipal.java
 * 
 * @author HCL Technologies Ltd.
 */
@Getter
@Builder(toBuilder = true)
public class UserPrincipal implements UserDetails {
	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 3823476815501093290L;
	private final String userId;
	private final String password;
	private final Collection<? extends GrantedAuthority> authorities;
	private final ChannelType channel;
	private final String version;
	private final String tenant;
	private final String locale;
	private final String deviceId;
	private final String idToken;
	private final String accessToken;
	private final String refreshToken;
	private final List<String> roleIds;	
	private final List<String> permissionIds;
	private final String prdAuthCookie;
	@Override
	public String getPassword() {
		return password;
	}

	@SuppressWarnings("all")
	@Override
	public String getUsername() {
		return userId;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
}