package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jda.mobility.framework.extensions.dto.TranslationRequest;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabelDetails implements Serializable {

    /** The field serialVersionUID of type long */
	private static final long serialVersionUID = -4567347048537885213L;
    private TranslationRequest label;
    private String value;
	private UUID valueId;

    public TranslationRequest getLabel() {
		return label;
	}

	public void setLabel(TranslationRequest label) {
		this.label = label;
	}

	public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

	public UUID getValueId() {
		return valueId;
	}

	public void setValueId(UUID valueId) {
		this.valueId = valueId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(label, value,valueId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LabelDetails other = (LabelDetails) obj;
		return Objects.equals(label, other.label) && Objects.equals(value, other.value) && Objects.equals(valueId, other.valueId);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Value [label=").append(label).append(", value=").append(value).append("]");
		return builder.toString();
	}
    
}