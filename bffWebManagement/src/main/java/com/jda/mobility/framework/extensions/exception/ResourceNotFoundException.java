package com.jda.mobility.framework.extensions.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This Class is used for identifying ResourceNotFoundExceptions
 * HCL Technologies Ltd.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = -1507881583315350066L;
	/** The field resourceName of type String */
    private final String resourceName;
    /** The field fieldName of type String */
    private final String fieldName;
    /** The field fieldValue of type Object */
    private final transient Object fieldValue;

    /**
     * @param resourceName
     * @param fieldName
     * @param fieldValue
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * @return resourceName
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * @return fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @return fieldValue
     */
    public Object getFieldValue() {
        return fieldValue;
    }
}
