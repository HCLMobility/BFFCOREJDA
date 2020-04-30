package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the api_registry database table.
 * 
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper = false) @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name="API_REGISTRY", indexes = {
		@Index(name = "idx_unqreg", columnList= "NAME,API_TYPE,ROLE_ID", unique = true)})
@NamedQuery(name="ApiRegistry.findAll", query="SELECT a FROM ApiRegistry a")
public class ApiRegistry extends BffAuditableData<String> implements Serializable {

	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = 3472221505616622666L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID", unique=true, length=16, nullable=false)
	private UUID uid;

	@Column(name="API_TYPE", nullable=false, length=20)
	@Nationalized
	private String apiType;

	@Column(name="API_VERSION", nullable=false,length=5)
	@Nationalized
	private String apiVersion;

	@Lob
	@Column(name="BASE_PATH",nullable=true)
	@Nationalized
	private String basePath;

	@Lob
	@Column(name="CONTEXT_PATH", nullable=true)
	@Nationalized
	private String contextPath;

	@Lob
	@Column(name="HELPER_CLASS")
	@Nationalized
	private String helperClass;

	@Column(name="NAME", nullable=false,length=255)
	@Nationalized
	private String name;

	@Column(name="PORT", nullable=true,length=45)
	@Nationalized
	private String port;

	@Column(name="VERSION_ID")
	private int versionId;

	//bi-directional many-to-one association to ApiMaster
	@OneToMany(mappedBy="apiRegistry" , cascade = CascadeType.ALL , orphanRemoval = true, fetch = FetchType.EAGER)
	private List<ApiMaster> apiMasters;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="ROLE_ID", nullable=false)
	private RoleMaster roleMaster;
	
	@Nationalized
	@Column(name="SCHEME_LIST",length=255)
	private String schemeList;
	

	public ApiMaster addApiMaster(ApiMaster apiMaster) {
		getApiMasters().add(apiMaster);
		apiMaster.setApiRegistry(this);

		return apiMaster;
	}

	public ApiMaster removeApiMaster(ApiMaster apiMaster) {
		getApiMasters().remove(apiMaster);
		apiMaster.setApiRegistry(null);

		return apiMaster;
	}
}