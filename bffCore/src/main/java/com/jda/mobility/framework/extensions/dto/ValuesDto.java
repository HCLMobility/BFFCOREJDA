package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;
@Data
public class ValuesDto implements Serializable {

	private static final long serialVersionUID = -9032227467434523208L;
	private String label;
	private String value;
	private UUID valueId;

	public ValuesDto(String label, String value, UUID valueId) {
		super();
		this.label = label;
		this.value = value;
		this.valueId = valueId;
	}	
		
}
