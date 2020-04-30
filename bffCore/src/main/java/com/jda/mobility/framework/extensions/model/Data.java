
package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Data implements Serializable {
    /** The field serialVersionUID of type long */
	
	private static final long serialVersionUID = 6008786345667061990L;
	private UUID fieldId;
	private List<LabelDetails> values;

	public UUID getFieldId() {
		return fieldId;
	}

	public void setFieldId(UUID fieldId) {
		this.fieldId = fieldId;
	}

	public List<LabelDetails> getValues() {
		return values;
	}

	public void setValues(List<LabelDetails> values) {
		this.values = values;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fieldId, values);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Data other = (Data) obj;
		return Objects.equals(fieldId, other.fieldId) && Objects.equals(values, other.values);
				
	}
	
}