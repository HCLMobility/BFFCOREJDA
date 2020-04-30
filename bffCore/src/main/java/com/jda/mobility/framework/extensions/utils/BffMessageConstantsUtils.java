package com.jda.mobility.framework.extensions.utils;

/**
 * The class for MessageConstants
 * 
 * @author HCL Technologies Ltd.
 */
public class BffMessageConstantsUtils {

	/**
	 * constructor for BffErrorConstantsUtils
	 */
	private BffMessageConstantsUtils() {
		super();
	}

	public enum StatusCode {
		OK(200), CREATED(201), BADREQUEST(400), UNAUTHORIZED(401), FORBIDDEN(403), NOTFOUND(404), CONFLICT(409), INTERNALSERVERERROR(500);
		private final int value;

		private StatusCode(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

}
