package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Validate implements Serializable {
	private static final long serialVersionUID = -4736581469000657014L;
	
	private String integer;
	private String pattern;
	private Double minLength;
	private Double maxLength;
	private Double min;
	private Double max;
	private String minDate;
	private String maxDate;
	private String minTime;
	private String maxTime;
	private String minRows;
	private String maxRows;

	public String getInteger() {
		return integer;
	}

	public void setInteger(String integer) {
		this.integer = integer;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Double getMinLength() {
		return minLength;
	}

	public void setMinLength(Double minLength) {
		this.minLength = minLength;
	}

	public Double getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Double maxLength) {
		this.maxLength = maxLength;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public String getMinDate() {
		return minDate;
	}

	public void setMinDate(String minDate) {
		this.minDate = minDate;
	}

	public String getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(String maxDate) {
		this.maxDate = maxDate;
	}

	public String getMinTime() {
		return minTime;
	}

	public void setMinTime(String minTime) {
		this.minTime = minTime;
	}

	public String getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(String maxTime) {
		this.maxTime = maxTime;
	}

	public String getMinRows() {
		return minRows;
	}

	public void setMinRows(String minRows) {
		this.minRows = minRows;
	}

	public String getMaxRows() {
		return maxRows;
	}

	public void setMaxRows(String maxRows) {
		this.maxRows = maxRows;
	}

	@Override
	public int hashCode() {
		return Objects.hash(integer, max, maxDate, maxLength, maxRows, maxTime, min, minDate, minLength, minRows,
				minTime, pattern);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Validate other = (Validate) obj;
		return Objects.equals(integer, other.integer) && Objects.equals(max, other.max)
				&& Objects.equals(maxDate, other.maxDate) && Objects.equals(maxLength, other.maxLength)
				&& Objects.equals(maxRows, other.maxRows) && Objects.equals(maxTime, other.maxTime)
				&& Objects.equals(min, other.min) && Objects.equals(minDate, other.minDate)
				&& Objects.equals(minLength, other.minLength) && Objects.equals(minRows, other.minRows)
				&& Objects.equals(minTime, other.minTime) && Objects.equals(pattern, other.pattern);
	}

	
}
