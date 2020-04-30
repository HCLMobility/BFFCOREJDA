package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Nationalized;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the menu_master database table.
 * 
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper = false)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name="MENU_MASTER")
@NamedQuery(name="MenuMaster.findAll", query="SELECT m FROM MenuMaster m")
public class MenuMaster extends BffAuditableData<String> implements Serializable {	

	private static final long serialVersionUID = -872985659712924495L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;

	@Column(name="PARENT_MENU_ID", length=16, nullable=true)
	private UUID parentMenuId;

	@Column(name="SEQUENCE", nullable=false)
	private int sequence;
	
	@ManyToOne
	@JoinColumn(name="SECONDARY_REF_ID", nullable=true)
	private ProductProperty productProperty;
	
	@Column(name="MENU_NAME", nullable=true,length=45)
	@Nationalized
	private String menuName;

	@OneToMany(mappedBy="menu", orphanRemoval = true , cascade = CascadeType.ALL)
	private List<MenuPermission> menupermission = new ArrayList<>();
	
	@Column(name="LINKED_FORM_ID", length=16,nullable=true)
	private UUID linkedFormId;
	
	@Column(name="ICON_NAME", nullable=true,length=50)
	@Nationalized
	private String iconName;
	
	@Column(name="ICON_ALIGNMENT", nullable=true,length=50)
	private String iconAlignment;
	
	@ManyToOne
	@JoinColumn(name="MENU_TYPE_UID", nullable=false)
	private MenuType menuType;
	
	@Column(name="SHOW_IN_TOOLBAR" ,nullable=true)
	private boolean showInToolBar;
	
	@Column(name="ACTION", nullable=true)
	@Nationalized
	private String action;
	
	@Lob
	@Column(name="PROPERTIES")
	@Nationalized
	private String properties;
	
	@Lob	
	@Column(name="HOT_KEY")
	@Nationalized
	private String hotKey;
	
	@Column(name="HOT_KEY_NAME" , length=255)
	@Nationalized
	private String hotKeyName;
	
	public MenuPermission addPermissions(MenuPermission permissions) {
		getMenupermission().add(permissions);
		permissions.setMenu(this);
		return permissions;
	}

	public MenuPermission removePermissions(MenuPermission permissions) {
		getMenupermission().remove(permissions);
		permissions.setMenu(null);
		return permissions;
	}
	
	
}