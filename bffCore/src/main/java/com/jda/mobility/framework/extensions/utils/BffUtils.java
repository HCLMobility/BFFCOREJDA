package com.jda.mobility.framework.extensions.utils;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jda.mobility.framework.extensions.exception.BffException;
import com.jda.mobility.framework.extensions.model.BffCoreResponse;
import com.jda.mobility.framework.extensions.model.DetailResponse;
import com.jda.mobility.framework.extensions.model.ErrorResponse;

/**
 * The class for BffUtils CoreResponse
 * 
 * @author HCL Technologies Ltd.
 */
public class BffUtils {
	
	private static final Logger LOGGER = LogManager.getLogger(BffUtils.class);
	
	private BffUtils() {
		super();
	}


	/**
	 * @param <T>
	 * @param obj JSON data that carries the request information 
	 * @param code Code of the success message
	 * @param msg Description of the success message
	 * @param detailMessage Detailed description of the success message
	 * @param httpStatus  HTTP status code
	 * @return &lt;T&gt; Object that carries the response object
	 */
	public static <T> BffCoreResponse buildResponse(T obj, int code, String msg, String detailMessage, int httpStatus) {
		BffCoreResponse bffCoreResponse = new BffCoreResponse();
		bffCoreResponse.setTimestamp(new Date().toString());
		bffCoreResponse.setCode(code);
		bffCoreResponse.setHttpStatusCode(httpStatus);
		bffCoreResponse.setMessage(msg);
		DetailResponse<T> detailResponse = new DetailResponse<>();
		detailResponse.setDetailMessage(detailMessage);
		detailResponse.setData(obj);
		bffCoreResponse.setDetails(detailResponse);

		return bffCoreResponse;
	}

	/**
	 * @param code Code of the error
	 * @param msg Description of the error
	 * @param errors List of error messages
	 * @param httpStatus HTTP status code
	 * @return BffCoreResponse Object that carries error object
	 */
	public static BffCoreResponse buildErrResponse(int code, String msg, List<ErrorResponse> errors, int httpStatus) {
		BffCoreResponse bffCoreResponse = new BffCoreResponse();
		bffCoreResponse.setTimestamp(new Date().toString());
		bffCoreResponse.setCode(code);
		bffCoreResponse.setHttpStatusCode(httpStatus);
		bffCoreResponse.setMessage(msg);
		bffCoreResponse.setErrors(errors);
		return bffCoreResponse;
	}

	/**
	 * @param <T>
	 * @param <U>
	 * @param nullable
	 * @param getter
	 * @return
	 */
	public static <T, U> U getNullable(T nullable, Function<T, U> getter) {
		return nullable != null ? getter.apply(nullable) : null;
	}
	
	/**
	 * @param headerVal Information about the header
	 * @return String Validated header information
	 * Validates if the header value contains any carriage return or line feed characters in an OS-agnostic fashion
	 * and throws an exception if the input string does
	 */
	public static String buildValidHeader(String headerVal) {
		if (headerVal != null && headerVal.contains(System.lineSeparator())){
			LOGGER.log(Level.DEBUG, "Invalid header! Contains a carraige return or line feed character: {}", headerVal);
			throw new BffException("Invalid header! Contains a carraige return or line feed character!");
		}
		return headerVal;
	}
}
