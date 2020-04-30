/**
 * 
 */
package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;

/**
 * The class IconInfo.java
 * @author HCL Technologies Ltd.
 */

public class IconInfo implements Serializable{

	private static final long serialVersionUID = 5364543285716203629L;
	private String iconName;
	private String iconCode;
	/**
	 * @return the iconName of type String
	 */
	public String getIconName() {
		return iconName;
	}
	/**
	 * @param iconName of type String
	 */
	public void setIconName(String iconName) {
		this.iconName = iconName;
	}
	/**
	 * @return the iconCode of type String
	 */
	public String getIconCode() {
		return iconCode;
	}
	/**
	 * @param iconCode of type String
	 */
	public void setIconCode(String iconCode) {
		this.iconCode = iconCode;
	}
	

}
