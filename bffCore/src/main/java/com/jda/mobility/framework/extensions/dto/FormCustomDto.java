package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.jda.mobility.framework.extensions.model.FormData;

import lombok.Data;
@Data
public class FormCustomDto implements Serializable{
	
	private static final long serialVersionUID = 2034577169964694665L;
	private final String customComponentName;
	private final UUID customComponentId;
	private final List<FormData> forms;
}
