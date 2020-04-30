package com.jda.mobility.framework.extensions.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The class is used for BadRequestExceptions
 * HCL Technologies Ltd.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    /** The field serialVersionUID of type long */
	private static final long serialVersionUID = -8003578813726859240L;

	/**
     * @param message
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
