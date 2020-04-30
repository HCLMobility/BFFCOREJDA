package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormProperties implements Serializable {

	private static final long serialVersionUID = -3495310919968585690L;
	
	
	private List<Event> events;
	
	private List<MenuListRequest> menus;
	private FormAttributes properties;
	
	public List<Event> getEvents() {
		return events;
	}
	public void setEvents(List<Event> events) {
		this.events = events;
	}
	
	public List<MenuListRequest> getMenus() {
		return menus;
	}
	public void setMenus(List<MenuListRequest> menus) {
		this.menus = menus;
	}
	
	public FormAttributes getProperties() {
		return properties;
	}
	public void setProperties(FormAttributes properties) {
		this.properties = properties;
	}
	@Override
	public int hashCode() {
		return Objects.hash(events,menus,properties);
		
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormProperties other = (FormProperties) obj;
		return Objects.equals(events, other.events)
				&& menus == other.menus 
						&& properties == other.properties ;
	}
}
