/**
 * 
 */
package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.jda.mobility.framework.extensions.model.Layer;

import lombok.Data;

/**
 * The class FlowDto.java
 * HCL Technologies Ltd.
 */
@Data
@JsonInclude(Include.NON_NULL)
public final class FlowDto implements Serializable{

	private static final long serialVersionUID = -6065313474750118013L;

	private UUID flowId;
	private String name;
	private String description;
	private UUID defaultFormId;
	private boolean disabled;
	private boolean extDisabled;
	private boolean published;
	private String tag;	
	private List<String> permissions;	
	private boolean tabbedForm;	
	private UUID extendedFromFlowId;	
	private String extendedFromFlowName;
	private Layer layer;
	private long version;
	private boolean modalForm;
	private long extendedFromFlowVersion;

	/**
	 * @param builder
	 */
	private FlowDto(FlowBuilder builder) {
		super();
		this.flowId = builder.flowId;
		this.name = builder.name;
		this.description = builder.description;
		this.defaultFormId = builder.defaultFormId;
		this.disabled = builder.disabled;
		this.extDisabled = builder.extDisabled;
		this.published = builder.published;
		this.tag = builder.tag;
		this.permissions = builder.permissions;
		this.tabbedForm = builder.tabbedForm;
		this.extendedFromFlowId = builder.extendedFromFlowId;
		this.extendedFromFlowName = builder.extendedFromFlowName;
		this.layer = builder.layer;
		this.version = builder.version;
		this.modalForm = builder.modalForm;
		this.extendedFromFlowVersion = builder.extendedFromFlowVersion;
	}
	
	public static class FlowBuilder{
		private UUID flowId;
		private String name;
		private String description;
		private UUID defaultFormId;
		private boolean disabled;
		private boolean extDisabled;
		private boolean published;
		private String tag;
		private List<String> permissions;
		private boolean tabbedForm;
		private UUID extendedFromFlowId;
		private String extendedFromFlowName;
		private Layer layer;
		private long version;
		private boolean modalForm;
		private long extendedFromFlowVersion;
		
		public FlowBuilder(String name) {
			this.name = name;
		}

		public FlowBuilder setFlowId(UUID flowId) {
			this.flowId = flowId;
			return this;
		}
		public FlowBuilder setDescription(String description) {
			this.description = description;
			return this;
		}

		public FlowBuilder setDefaultFormId(UUID defaultFormId) {
			this.defaultFormId = defaultFormId;
			return this;
		}

		public FlowBuilder setDisabled(boolean disabled) {
			this.disabled = disabled;
			return this;
		}

		public FlowBuilder setExtDisabled(boolean extDisabled) {
			this.extDisabled = extDisabled;
			return this;
		}

		public FlowBuilder setPublished(boolean published) {
			this.published = published;
			return this;
		}

		public FlowBuilder setTag(String tag) {
			this.tag = tag;
			return this;
		}

		public FlowBuilder setPermissions(List<String> permissions) {
			this.permissions = permissions;
			return this;
		}

		public FlowBuilder setTabbedForm(boolean tabbedForm) {
			this.tabbedForm = tabbedForm;
			return this;
		}

		public FlowBuilder setExtendedFromFlowId(UUID extendedFromFlowId) {
			this.extendedFromFlowId = extendedFromFlowId;
			return this;
		}

		public FlowBuilder setExtendedFromFlowName(String extendedFromFlowName) {
			this.extendedFromFlowName = extendedFromFlowName;
			return this;
		}

		public FlowBuilder setLayer(Layer layer) {
			this.layer = layer;
			return this;
		}

		public FlowBuilder setVersion(long version) {
			this.version = version;
			return this;
		}
		
		public FlowBuilder setModalForm(boolean modalForm) {
			this.modalForm = modalForm;
			return this;
		}
		
		public FlowBuilder setExtendedFromFlowVersion(long extendedFromFlowVersion) {
			this.extendedFromFlowVersion = extendedFromFlowVersion;
			return this;
		}

		
		public FlowDto build() {
			return new FlowDto(this);
		}
	}
}