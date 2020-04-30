/**
 * 
 */
package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The class FormCustomComponentType.java
 * @author HCL Technologies Ltd.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormCustomComponentType implements Serializable {

	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 4980529535304588146L;
	/** The field formCusId of type UUID */
	private UUID formCusId;
	/** The field formId of type UUID */
	private UUID formId;
	/** The field customFormId of type UUID */
	private UUID customFormId;
	/**
	 * @return the formCusId of type UUID
	 */
	public UUID getFormCusId() {
		return formCusId;
	}
	/**
	 * @param formCusId of type UUID
	 */
	public void setFormCusId(UUID formCusId) {
		this.formCusId = formCusId;
	}
	/**
	 * @return the formId of type UUID
	 */
	public UUID getFormId() {
		return formId;
	}
	/**
	 * @param formId of type UUID
	 */
	public void setFormId(UUID formId) {
		this.formId = formId;
	}
	/**
	 * @return the customFormId of type UUID
	 */
	public UUID getCustomFormId() {
		return customFormId;
	}
	/**
	 * @param customFormId of type UUID
	 */
	public void setCustomFormId(UUID customFormId) {
		this.customFormId = customFormId;
	}
	@Override
	public int hashCode() {
		return Objects.hash(customFormId, formCusId, formId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormCustomComponentType other = (FormCustomComponentType) obj;
		return Objects.equals(customFormId, other.customFormId) && Objects.equals(formCusId, other.formCusId)
				&& Objects.equals(formId, other.formId);
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FormCustomComponentType [formCusId=").append(formCusId).append(", formId=").append(formId)
				.append(", customFormId=").append(customFormId).append("]");
		return builder.toString();
	}	
}