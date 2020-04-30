package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data @Builder(toBuilder = true)
public  class HotKeyCodeListDto implements Serializable{
	
	private static final long serialVersionUID = 5664535146493318832L;

	private List<HotKeyCodeDto> contextHotKeyList;
	
	private List<HotKeyCodeDto> globalHotKeyList;

}
