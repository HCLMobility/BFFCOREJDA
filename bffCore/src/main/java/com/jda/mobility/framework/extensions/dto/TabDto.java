package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;
@Data @Builder(toBuilder = true)
public class TabDto implements Serializable,Comparable<TabDto>{

	private static final long serialVersionUID = -8915494719366325534L;

	private UUID linkedFormId;	
	private String linkedFormName;	
	private UUID tabId;	
	private String tabName;	
	private int sequence;	
	private boolean defaultForm;

	@Override
	public int compareTo(TabDto tabDto) {
		int compareSeq = tabDto.getSequence();
		/* For Ascending order */
		return this.sequence - compareSeq;
	}

}
