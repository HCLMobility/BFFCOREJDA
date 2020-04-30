package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The class TranslationRequest.java
 * @author HCL Technologies Ltd.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data @Builder @NoArgsConstructor
public class TranslationRequest implements Serializable{

	private static final long serialVersionUID = 1307336867411744825L;

	private UUID uid;
	private String locale;
	private String rbkey;
	private String rbvalue;
	private String type;
	
	public TranslationRequest(UUID uid, String locale, String rbkey, String rbvalue, String type) {
		super();
		this.uid = uid;
		this.locale = locale;
		this.rbkey = rbkey;
		this.rbvalue = rbvalue;
		this.type = type;
	}
	
}
