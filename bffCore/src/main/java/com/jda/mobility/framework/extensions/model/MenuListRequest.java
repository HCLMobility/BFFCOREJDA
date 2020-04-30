package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.dto.TranslationRequest;

public class MenuListRequest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1516943130245140167L;

	@Valid
	private List<MenuListRequest> subMenus;	
	private List<String> permissions;	
	private String iconName;	
	private String iconAlignment;	
	private boolean showInToolBar;	
	private String menuType;	
	private TranslationRequest menuName;	
	private MenuAction menuAction;
	private UUID defaultFormId;	
	private UUID flowId;	
	private boolean tabbedForm;	
	private ObjectNode hotKey;	
	private boolean modalForm;
	private UUID uid;
	private String hotKeyName;
	

	public UUID getUid() {
		return uid;
	}

	public void setUid(UUID uid) {
		this.uid = uid;
	}

	public List<MenuListRequest> getSubMenus() {
		return subMenus;
	}

	public void setSubMenus(List<MenuListRequest> subMenus) {
		this.subMenus = subMenus;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	public String getIconAlignment() {
		return iconAlignment;
	}

	public void setIconAlignment(String iconAlignment) {
		this.iconAlignment = iconAlignment;
	}

	

	public boolean isShowInToolBar() {
		return showInToolBar;
	}

	public void setShowInToolBar(boolean showInToolBar) {
		this.showInToolBar = showInToolBar;
	}

	public String getMenuType() {
		return menuType;
	}

	public void setMenuType(String menuType) {
		this.menuType = menuType;
	}

	public TranslationRequest getMenuName() {
		return menuName;
	}

	public void setMenuName(TranslationRequest menuName) {
		this.menuName = menuName;
	}

	public MenuAction getMenuAction() {
		return menuAction;
	}

	public void setMenuAction(MenuAction menuAction) {
		this.menuAction = menuAction;
	}

	public UUID getDefaultFormId() {
		return defaultFormId;
	}

	public void setDefaultFormId(UUID defaultFormId) {
		this.defaultFormId = defaultFormId;
	}

	public UUID getFlowId() {
		return flowId;
	}

	public void setFlowId(UUID flowId) {
		this.flowId = flowId;
	}

	public boolean isTabbedForm() {
		return tabbedForm;
	}
	public void setTabbedForm(boolean isTabbedForm) {
		this.tabbedForm = isTabbedForm;
	}
	
	
	public ObjectNode getHotKey() {
		return hotKey;
	}

	public void setHotKey(ObjectNode hotKey) {
		this.hotKey = hotKey;
	}

	public boolean isModalForm() {
		return modalForm;
	}

	public void setModalForm(boolean modalForm) {
		this.modalForm = modalForm;
	}

	
	public String getHotKeyName() {
		return hotKeyName;
	}

	public void setHotKeyName(String hotKeyName) {
		this.hotKeyName = hotKeyName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(iconAlignment,iconName,menuType,permissions,showInToolBar,subMenus, menuName, menuAction,defaultFormId,flowId,tabbedForm,hotKeyName, modalForm);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MenuListRequest other = (MenuListRequest) obj;
		return iconAlignment == other.iconAlignment && Objects.equals(iconName, other.iconName)
				&& Objects.equals(menuType, other.menuType) && Objects.equals(permissions, other.permissions) 
				&& Objects.equals(showInToolBar, other.showInToolBar) && Objects.equals(subMenus, other.subMenus)
				&& Objects.equals(menuName, other.menuName) && Objects.equals(menuAction, other.menuAction) 
				&&  Objects.equals(defaultFormId, other.defaultFormId) &&  Objects.equals(flowId, other.flowId)
				&&  Objects.equals(tabbedForm, other.tabbedForm)
				&&  Objects.equals(hotKeyName, other.hotKeyName) && Objects.equals(modalForm, other.modalForm);
	
	}
	

	
	
}
