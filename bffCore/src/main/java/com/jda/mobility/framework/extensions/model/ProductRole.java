package com.jda.mobility.framework.extensions.model;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The class ProductRoleDto.java
 * 
 * @author HCL Technologies Ltd.
 */
@JsonInclude(Include.NON_NULL)
public class ProductRole {

	private List<String> roleIds;

	public List<String> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<String> roleIds) {
		this.roleIds = roleIds;
	}

	@Override
	public int hashCode() {
		return Objects.hash(roleIds);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductRole other = (ProductRole) obj;
		return Objects.equals(roleIds, other.roleIds);
	}

}
