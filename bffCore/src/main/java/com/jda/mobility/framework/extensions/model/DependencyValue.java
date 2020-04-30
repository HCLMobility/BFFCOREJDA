/**
 * 
 */
package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * The class DependencyValue.java
 */
public class DependencyValue implements Serializable {
	
	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 4062046434106432148L;
	
	private String condition;
	private String value;
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public int hashCode() {
		return Objects.hash(condition, value);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DependencyValue other = (DependencyValue) obj;
		return Objects.equals(condition, other.condition) && Objects.equals(value, other.value);
	}

}
