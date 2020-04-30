package com.jda.mobility.framework.extensions.exception;

import org.springframework.dao.DataAccessException;

public class DataBaseException extends DataAccessException {

	private static final long serialVersionUID = -5395540022836234356L;

	/**
	 * @param exceptionMessage
	 */
	public DataBaseException(String exceptionMessage) {
		super(exceptionMessage);
	}

}
