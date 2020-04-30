
package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldDependency implements Serializable {
	private static final long serialVersionUID = 966183066273060362L;

	@Valid
	private Show show;
	@Valid
	private Hide hide;
	@Valid
	private Enable enable;
	@Valid
	private Disable disable;
	@JsonProperty("setValue")
	@Valid
	private List<DependencyValue> values;
	@JsonProperty("setRequired")
	private Required requiredReq;
	private boolean hidden;
	private boolean disabled;
    private boolean required;
	
	public List<DependencyValue> getValues() {
		return values;
	}

	public void setValues(List<DependencyValue> values) {
		this.values = values;
	}

	public Show getShow() {
		return show;
	}

	public void setShow(Show show) {
		this.show = show;
	}

	public Hide getHide() {
		return hide;
	}

	public void setHide(Hide hide) {
		this.hide = hide;
	}

	public Enable getEnable() {
		return enable;
	}

	public void setEnable(Enable enable) {
		this.enable = enable;
	}

	public Disable getDisable() {
		return disable;
	}

	public void setDisable(Disable disable) {
		this.disable = disable;
	}

	public boolean getHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * @return the requiredReq of type Required
	 */
	public Required getRequiredReq() {
		return requiredReq;
	}

	/**
	 * @param requiredReq of type Required
	 */
	public void setRequiredReq(Required requiredReq) {
		this.requiredReq = requiredReq;
	}

	/**
	 * @return the required of type boolean
	 */
	public boolean getRequired() {
		return required;
	}

	/**
	 * @param required of type boolean
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	@Override
	public int hashCode() {
		return Objects.hash(disable, disabled, enable, hidden, hide, required, requiredReq, values, show);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldDependency other = (FieldDependency) obj;
		return Objects.equals(disable, other.disable) && disabled == other.disabled
				&& Objects.equals(enable, other.enable) && hidden == other.hidden && Objects.equals(hide, other.hide)
				&& Objects.equals(required, other.required) && Objects.equals(requiredReq, other.requiredReq)
				&& Objects.equals(values, other.values) && Objects.equals(show, other.show);
	}
	
}