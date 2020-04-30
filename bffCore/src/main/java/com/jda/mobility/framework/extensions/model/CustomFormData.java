package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jda.mobility.framework.extensions.dto.TranslationRequest;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomFormData implements Serializable {

	private static final long serialVersionUID = 3727366230804857942L;

	private UUID customComponentId;
	private String name;
	private String description;	
	private boolean visibility;
	private boolean disabled;
	@JsonProperty("formCustomComponentType")
	private List<FormCustomComponentType> formCustomComponentTypes;
	@Valid
	private List<UUID> deleteFields;

	@Valid
	private List<UUID> deleteEvents;

	@Valid
	private List<UUID> deleteValues;
	
	@Valid
	private List<UUID> deleteDataValues;
	@SuppressWarnings("all")
	@Valid
	private List<FieldComponent> components = null;
	
	private Layer layer;
	
	private TranslationRequest formTitle; 
	

	/**
	 * @return the customComponentId of type String
	 */
	public UUID getCustomComponentId() {
		return customComponentId;
	}

	/**
	 * @param customComponentId of type String
	 */
	public void setCustomComponentId(UUID customComponentId) {
		this.customComponentId = customComponentId;
	}

	/**
	 * @return the name of type String
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name of type String
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description of type String
	 */	
	public String getDescription() {
		return description;
	}
	/**
	 * @param description of type String
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the visibility of type boolean
	 */
	public boolean isVisibility() {
		return visibility;
	}

	/**
	 * @param visibility of type boolean
	 */
	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}

	/**
	 * @return the disabled of type boolean
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * @param disabled of type boolean
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public List<FormCustomComponentType> getFormCustomComponentTypes() {
		return formCustomComponentTypes;
	}

	public void setFormCustomComponentTypes(List<FormCustomComponentType> formCustomComponentTypes) {
		this.formCustomComponentTypes = formCustomComponentTypes;
	}

	public List<UUID> getDeleteFields() {
		return deleteFields;
	}

	public void setDeleteFields(List<UUID> deleteFields) {
		this.deleteFields = deleteFields;
	}

	public List<UUID> getDeleteEvents() {
		return deleteEvents;
	}

	public void setDeleteEvents(List<UUID> deleteEvents) {
		this.deleteEvents = deleteEvents;
	}

	public List<UUID> getDeleteValues() {
		return deleteValues;
	}

	public void setDeleteValues(List<UUID> deleteValues) {
		this.deleteValues = deleteValues;
	}

	public List<UUID> getDeleteDataValues() {
		return deleteDataValues;
	}
	public void setDeleteDataValues(List<UUID> deleteDataValues) {
		this.deleteDataValues = deleteDataValues;
	}

	
	public List<FieldComponent> getComponents() {
		return components;
	}

	public void setComponents(List<FieldComponent> components) {
		this.components = components;
	}
	

	public Layer getLayer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	public TranslationRequest getFormTitle() {
		return formTitle;
	}

	public void setFormTitle(TranslationRequest formTitle) {
		this.formTitle = formTitle;
	}

	@Override
	public int hashCode() {
		return Objects.hash(components, customComponentId, deleteDataValues, deleteEvents, deleteFields, deleteValues,
				disabled, formCustomComponentTypes, name, visibility,layer,formTitle);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomFormData other = (CustomFormData) obj;
		return Objects.equals(components, other.components)
				&& Objects.equals(customComponentId, other.customComponentId)
				&& Objects.equals(deleteDataValues, other.deleteDataValues)
				&& Objects.equals(deleteEvents, other.deleteEvents) && Objects.equals(deleteFields, other.deleteFields)
				&& Objects.equals(deleteValues, other.deleteValues) && disabled == other.disabled
				&& Objects.equals(formCustomComponentTypes, other.formCustomComponentTypes)
				&& Objects.equals(name, other.name) && visibility == other.visibility
				&& layer == other.layer && formTitle == other.formTitle;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CustomFormData [customComponentId=").append(customComponentId).append(", name=").append(name)
				.append(", visibility=").append(visibility).append(", disabled=").append(disabled)
				.append(", formCustomComponentType=")
				.append(formCustomComponentTypes).append(", deleteField=").append(deleteFields).append(", deleteEvent=")
				.append(deleteEvents).append(", deleteValues=").append(deleteValues).append(", deleteDataValues=")
				.append(deleteDataValues).append(", components=").append(components).append(", layer=").append(layer).append("]");
		return builder.toString();
	}
	
}