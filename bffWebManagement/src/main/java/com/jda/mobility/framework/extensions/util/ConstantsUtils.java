/**
 * 
 */
package com.jda.mobility.framework.extensions.util;

/**
 * Constants for WebManagement
 *
 */
public class ConstantsUtils {

	private ConstantsUtils() {
		super();
	}

	public static final String UNCHECKED = "unchecked";
	public static final String RAWTYPE = "rawtypes";
	public static final String SESSION_REC_MODE = "true";

	public enum AuthScheme {
		BASIC_AUTH, OPENID;
	}
}