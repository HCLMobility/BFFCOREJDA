package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event implements Serializable{
    /** The field serialVersionUID of type long */
	private static final long serialVersionUID = -982518669388726392L;
	private UUID eventId;
	@JsonProperty("event")	
    private String eventName;
    private ObjectNode action;

    /**
	 * @return the eventId of type UUID
	 */
	public UUID getEventId() {
		return eventId;
	}

	/**
	 * @param eventId of type UUID
	 */
	public void setEventId(UUID eventId) {
		this.eventId = eventId;
	}

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public ObjectNode getAction() {
        return action;
    }

    public void setAction(ObjectNode action) {
        this.action = action;
    }

	@Override
	public int hashCode() {
		return Objects.hash(action, eventId, eventName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		return Objects.equals(action, other.action) && Objects.equals(eventId, other.eventId)
				&& Objects.equals(eventName, other.eventName);
	}
}