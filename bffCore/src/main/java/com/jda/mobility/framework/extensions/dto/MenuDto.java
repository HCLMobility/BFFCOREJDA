package com.jda.mobility.framework.extensions.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import lombok.Data;
@Data
public class MenuDto implements Serializable{

	private static final long serialVersionUID = -8238075762400466152L;

	private UUID secondaryRefId;
	private String menuType;
	private List<MenuListDto> menus;
}
