package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChildValue implements Serializable {
	private static final long serialVersionUID = 7146447978291624740L;
	private UUID dataValId;
    private String label;
    private String value;

	public UUID getDataValId() {
		return dataValId;
	}

	public void setDataValId(UUID dataValId) {
		this.dataValId = dataValId;
	}

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

	@Override
	public int hashCode() {
		return Objects.hash(dataValId, label, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChildValue other = (ChildValue) obj;
		return Objects.equals(dataValId, other.dataValId) && Objects.equals(label, other.label)
				&& Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ChildValue [dataValId=").append(dataValId).append(", label=").append(label).append(", value=")
				.append(value).append("]");
		return builder.toString();
	}
}