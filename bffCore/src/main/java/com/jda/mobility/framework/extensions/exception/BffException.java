package com.jda.mobility.framework.extensions.exception;

public class BffException extends RuntimeException {

	private static final long serialVersionUID = -164987556386299619L;
	
	public BffException() {
		super();
	}

	public BffException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BffException(String message, Throwable cause) {
		super(message, cause);
	}

	public BffException(String message) {
		super(message);
	}

	public BffException(Throwable cause) {
		super(cause);
	}
}
