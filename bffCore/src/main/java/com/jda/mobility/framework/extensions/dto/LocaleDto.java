package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)


public class LocaleDto implements Serializable {

	private static final long serialVersionUID = 4377306606820775778L;

	private String localeId;
	private String localeCode;
	

}