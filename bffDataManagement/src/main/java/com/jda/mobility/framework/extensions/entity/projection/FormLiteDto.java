/**
 * 
 */
package com.jda.mobility.framework.extensions.entity.projection;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;

/**
 * @author HCL Technologies
 *
 */
@Data
public class FormLiteDto implements Serializable{
	private static final long serialVersionUID = 6059950854638624130L;
	private UUID formId;
	private String formName;
	private boolean tabbedForm;
	private boolean modalForm;
	/**
	 * @param formId
	 * @param formName
	 * @param tabbedForm
	 */
	public FormLiteDto(UUID formId, String formName, boolean tabbedForm,boolean modalForm) {
		super();
		this.formId = formId;
		this.formName = formName;
		this.tabbedForm = tabbedForm;
		this.modalForm= modalForm;
	}
	
}
