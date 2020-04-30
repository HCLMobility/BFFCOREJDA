package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import lombok.Data;

/**
 * The class CustomFormDto.java
 * @author HCL Technologies Ltd.
 */
@Data
public final class CustomFormDto implements Serializable{
                  
	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 802292641793198027L;

	/** The field customFormId of type String */
	private UUID customFormId;
	/** The field name of type String */
	private String name;
	/** The field description of type String */
	private String description;	
	/** The field visibility of type boolean */
	private boolean visibility;	
	/** The field disabled of type boolean */
	private boolean disabled;	
	private List<CustomFieldObjDto> fields;	
	private List<FormCustomComponentDto> formCustomComponentDto;
	private List<UUID> deleteFields;
	private List<UUID> deleteEvents;
	private List<UUID> deleteValues;
	private List<UUID> deleteDataValues;
	private String formTitle;
	private UUID productConfigId;
	
	/**
	 * @param customFormBuilder
	 */
	private CustomFormDto(CustomFormBuilder customFormBuilder) {
		super();
		this.customFormId = customFormBuilder.customFormId;
		this.name = customFormBuilder.name;
		this.description = customFormBuilder.description;
		this.visibility = customFormBuilder.visibility;
		this.disabled = customFormBuilder.disabled;
		this.fields = customFormBuilder.fields;
		this.formCustomComponentDto = customFormBuilder.formCustomComponentDto;
		this.deleteFields = customFormBuilder.deleteFields;
		this.deleteEvents = customFormBuilder.deleteEvents;
		this.deleteValues = customFormBuilder.deleteValues;
		this.deleteDataValues = customFormBuilder.deleteDataValues;
		this.formTitle = customFormBuilder.formTitle;
		this.productConfigId = customFormBuilder.productConfigId;
	}
	
	public static class CustomFormBuilder{
		private UUID customFormId;
		private String name;
		private String description;
		private boolean visibility;	
		private boolean disabled;	
		private List<CustomFieldObjDto> fields;	
		private List<FormCustomComponentDto> formCustomComponentDto;
		private List<UUID> deleteFields;
		private List<UUID> deleteEvents;
		private List<UUID> deleteValues;
		private List<UUID> deleteDataValues;
		private String formTitle;
		private UUID productConfigId;
		
		public CustomFormBuilder() {
			super();
		}
		
		public CustomFormBuilder setName(String name) {
			this.name = name;
			return this;
		}

		public CustomFormBuilder setDescription(String description) {
			this.description = description;
			return this;
		}

		public CustomFormBuilder setCustomFormId(UUID customFormId) {
			this.customFormId = customFormId;
			return this;
		}
		public CustomFormBuilder setVisibility(boolean visibility) {
			this.visibility = visibility;
			return this;
		}
		public CustomFormBuilder setDisabled(boolean disabled) {
			this.disabled = disabled;
			return this;
		}
		public CustomFormBuilder setFields(List<CustomFieldObjDto> fields) {
			this.fields = fields;
			return this;
		}
		public CustomFormBuilder setFormCustomComponentDto(List<FormCustomComponentDto> formCustomComponentDto) {
			this.formCustomComponentDto = formCustomComponentDto;
			return this;
		}
		public CustomFormBuilder setDeleteFields(List<UUID> deleteFields) {
			this.deleteFields = deleteFields;
			return this;
		}
		public CustomFormBuilder setDeleteEvents(List<UUID> deleteEvents) {
			this.deleteEvents = deleteEvents;
			return this;
		}
		public CustomFormBuilder setDeleteValues(List<UUID> deleteValues) {
			this.deleteValues = deleteValues;
			return this;
		}
		public CustomFormBuilder setDeleteDataValues(List<UUID> deleteDataValues) {
			this.deleteDataValues = deleteDataValues;
			return this;
		}
		
		public CustomFormBuilder setFormTitle(String formTitle) {
			this.formTitle = formTitle;
			return this;
		}
		public CustomFormBuilder setProductConfigId(UUID productConfigId) {
			this.productConfigId = productConfigId;
			return this;
		}

		
		public CustomFormDto build() {
			return new CustomFormDto(this);
		}
	}
	
}