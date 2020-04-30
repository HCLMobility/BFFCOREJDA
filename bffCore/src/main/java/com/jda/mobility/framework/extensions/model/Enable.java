
package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;

public class Enable implements Serializable {
	private static final long serialVersionUID = 5690035440656936283L;
	
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
		Enable other = (Enable) obj;
		return Objects.equals(condition, other.condition);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Enable [condition=").append(condition).append("]");
		return builder.toString();
	}
}