package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;
@Data
public class FormAttributes implements Serializable{

	private static final long serialVersionUID = 2712210516849703800L;
	private boolean disableForm;
	private boolean disableFormExtensions;
	private boolean disableAllExtensions;
	private boolean modalForm;
	private boolean hideToolbar;
	private boolean hideLeftNavigation;
	private boolean hideBottomNavigation;
	private boolean hideGs1Barcode;
	private ObjectNode gs1Form;
}
