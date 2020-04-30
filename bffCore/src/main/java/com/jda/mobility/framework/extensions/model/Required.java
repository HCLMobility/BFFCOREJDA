package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;

public class Required implements Serializable{
    /** The field serialVersionUID of type long */
	private static final long serialVersionUID = -1093570841373839731L;
    private String condition;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

	@Override
	public int hashCode() {
		return Objects.hash(condition);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Required other = (Required) obj;
		return Objects.equals(condition, other.condition);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Required [condition=").append(condition).append("]");
		return builder.toString();
	}

}
