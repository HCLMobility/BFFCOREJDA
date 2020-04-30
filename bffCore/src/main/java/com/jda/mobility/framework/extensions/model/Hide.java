
package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;

public class Hide implements Serializable {
	private static final long serialVersionUID = -9134991575843298961L;
	
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
		Hide other = (Hide) obj;
		return Objects.equals(condition, other.condition);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Hide [condition=").append(condition).append("]");
		return builder.toString();
	}

}
