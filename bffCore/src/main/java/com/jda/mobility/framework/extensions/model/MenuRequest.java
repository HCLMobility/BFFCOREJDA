package com.jda.mobility.framework.extensions.model;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

public class MenuRequest {

	private String warehouseName;
	
	private boolean defaultWarehouse;
	
	private UUID formId;
	
	@Valid
	private List<MenuListRequest> menus;

	public List<MenuListRequest> getMenus() {
		return menus;
	}

	public void setMenus(List<MenuListRequest> menus) {
		this.menus = menus;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public UUID getFormId() {
		return formId;
	}

	public void setFormId(UUID formId) {
		this.formId = formId;
	}

	public boolean isDefaultWarehouse() {
		return defaultWarehouse;
	}

	public void setDefaultWarehouse(boolean defaultWarehouse) {
		this.defaultWarehouse = defaultWarehouse;
	}

	

}
