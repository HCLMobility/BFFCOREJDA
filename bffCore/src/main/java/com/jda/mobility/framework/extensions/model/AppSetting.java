/**
 * 
 */
package com.jda.mobility.framework.extensions.model;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author HCL Technologies Ltd.
 *
 */
@Data
public class AppSetting {
	@Digits(fraction = 0, integer = 10)
	@Min(value = 1, message = "Inactive time period cannot be less than 1 min.")
	@NotNull
	private int appInactivePeriod;

}
