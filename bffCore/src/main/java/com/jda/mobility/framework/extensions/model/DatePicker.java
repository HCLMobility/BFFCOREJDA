package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("all")
public class DatePicker implements Serializable {

    /** The field serialVersionUID of type long */
	private static final long serialVersionUID = -8608954956771083589L;
    private Object minDate;
    private Object maxDate;

    public Object getMinDate() {
        return minDate;
    }

    public void setMinDate(Object minDate) {
        this.minDate = minDate;
    }

    public Object getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Object maxDate) {
        this.maxDate = maxDate;
    }

	@Override
	public int hashCode() {
		return Objects.hash(maxDate, minDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DatePicker other = (DatePicker) obj;
		return Objects.equals(maxDate, other.maxDate) && Objects.equals(minDate, other.minDate);
	}
}