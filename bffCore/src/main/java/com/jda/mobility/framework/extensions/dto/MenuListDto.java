package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.model.MenuAction;

import lombok.Data;

/**
 * The class MenuListDto.java HCL Technologies Ltd.
 */
@Data
public class MenuListDto implements Serializable {

	private static final long serialVersionUID = -113145410320317004L;

	private UUID uid;
	private UUID parentMenuId;
	private int sequence;
	private List<MenuListDto> subMenus;
	private List<String> permissions;
	private String iconName;
	private String iconAlignment;
	private String menuType;
	private boolean showInToolBar;
	private UUID defaultFormId;
	private TranslationRequest menuName;
	private MenuAction menuAction;
	private UUID flowId;
	private boolean tabbedForm;
	private ObjectNode hotKey;
	private boolean modalForm;
	private String hotKeyName;

}
