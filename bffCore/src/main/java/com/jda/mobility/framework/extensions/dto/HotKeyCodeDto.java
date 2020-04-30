package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HotKeyCodeDto implements Serializable {

	private static final long serialVersionUID = 4377306606820775778L;

	private String keyName;
	private String keyDisplayName;
	private String keyDescription;

	private KeyEventDto keyEvent;

}