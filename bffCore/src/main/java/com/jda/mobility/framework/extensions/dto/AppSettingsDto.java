/**
 * 
 */
package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The class DefaultHomeFlowDto.java
 *  HCL Technologies Ltd.
 */
@Data @NoArgsConstructor
public class AppSettingsDto implements Serializable {

	private static final long serialVersionUID = 1756586125434159096L;

	private DefaultHomeFlowDto defaultHomeFlow;
	private List<AppConfigDto> appConfigs;
	private List<HotKeyCodeDto> hotKeyCodeDtoList;
}