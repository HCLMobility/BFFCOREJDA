package com.jda.mobility.framework.extensions.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The class ProductRolePermissionDto.java
 * 
 * @author HCL Technologies Ltd.
 */
@JsonInclude(Include.NON_NULL)
public class ProductRolePermission {

	
	private String userId;
	
	private List<String> permissions;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}

	@Override
	public int hashCode() {
		return Objects.hash(permissions, userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductRolePermission other = (ProductRolePermission) obj;
		return Objects.equals(permissions, other.permissions) && Objects.equals(userId, other.userId);
	}

}
