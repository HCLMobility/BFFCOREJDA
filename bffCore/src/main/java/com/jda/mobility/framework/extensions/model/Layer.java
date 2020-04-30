package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;

public class Layer implements Serializable{

	private static final long serialVersionUID = 3900903265942953589L;
	/** The field layer of type Integer */
	private int level;
	/** The field layerName of type String */
	private String name;
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public int hashCode() {
		return Objects.hash(level, name);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Layer other = (Layer) obj;
		return level == other.level && Objects.equals(name, other.name);
	}
}
