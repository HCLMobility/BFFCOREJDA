package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;

/**
 * The class ApiMasterDto.java
 * HCL Technologies Ltd.
 */
@Data @Builder(toBuilder = true)
public final class ApiMasterDto implements Serializable {

	private static final long serialVersionUID = -7260423091915008563L;
	@JsonInclude(Include.NON_NULL)
	private UUID uid;
	private String name;
	private String requestBody;
	private String requestEndpoint;
	private String requestMethod;
	private String requestPathparams;
	private String requestPreproc;
	private String requestQuery;
	private String responsePostproc;
	private String responseSchema;
	private String version;
	@JsonInclude(Include.NON_NULL)
	private String regName;
		
}