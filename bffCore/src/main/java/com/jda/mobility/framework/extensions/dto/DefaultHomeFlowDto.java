/**
 * 
 */
package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The class DefaultHomeFlowDto.java
 *  HCL Technologies Ltd.
 */
@Data @NoArgsConstructor
public class DefaultHomeFlowDto implements Serializable {

	private static final long serialVersionUID = 3895846813215036637L;
	private UUID defaultFlowId;
	private String defaultFlowName;
	private String defaultFlowdefFormId;
	private UUID homeFlowId;
	private String homeFlowName;
	private String homeFlowDefFormId;
	private boolean defaultFormTabbed;
	private boolean homeFormTabbed;
	private long homeFlowVersion;
	private long defaultFlowVersion;
	private boolean defaultFormModalForm;
	private boolean homeFormModalForm;
}