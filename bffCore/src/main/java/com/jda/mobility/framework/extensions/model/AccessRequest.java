/**
 * 
 */
package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * The class AccessRequest.java
 * @author HCL Technologies Ltd.
 */
public class AccessRequest implements Serializable{
	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = -5582576514570329601L;
	/** The field Id of type UUID */
	private UUID id;
	/** The field userId of type String */
	private String userId;
	/** The field roleId of type UUID */
	private UUID roleId;	
	/** The field isSuper of type boolean */
	private boolean superUser;
	/** The field actionType of type String */
	private String actionType;	
	/**
	 * @return the id of type UUID
	 */
	public UUID getId() {
		return id;
	}
	/**
	 * @param id of type UUID
	 */
	public void setId(UUID id) {
		this.id = id;
	}
	/**
	 * @return the userId of type String
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId of type String
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the roleId of type UUID
	 */
	public UUID getRoleId() {
		return roleId;
	}
	/**
	 * @param roleId of type UUID
	 */
	public void setRoleId(UUID roleId) {
		this.roleId = roleId;
	}	
	
	/**
	 * @return the actionType of type String
	 */	
	public String getActionType() {
		return actionType;
	}
	/**
	 * @param actionType of type String
	 */	
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public boolean isSuperUser() {
		return superUser;
	}
	public void setSuperUser(boolean superUser) {
		this.superUser = superUser;
	}
	@Override
	public int hashCode() {
		return Objects.hash(actionType, id, superUser, roleId, userId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof AccessRequest))
			return false;
		AccessRequest other = (AccessRequest) obj;
		return Objects.equals(actionType, other.actionType) && Objects.equals(id, other.id) && superUser == other.superUser
				&& Objects.equals(roleId, other.roleId) && Objects.equals(userId, other.userId);
	}
	
	
}