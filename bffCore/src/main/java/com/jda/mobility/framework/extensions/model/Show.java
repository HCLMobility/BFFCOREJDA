
package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;

public class Show implements Serializable {
	private static final long serialVersionUID = 6356145402627626791L;
	
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
		Show other = (Show) obj;
		return Objects.equals(condition, other.condition);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Show [condition=").append(condition).append("]");
		return builder.toString();
	}
}