package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
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
 * The persistent class for the api_master database table.
 * 
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode(callSuper=false) @Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name="API_MASTER")
@NamedQuery(name="ApiMaster.findAll", query="SELECT a FROM ApiMaster a")
public class ApiMaster extends BffAuditableData<String> implements Serializable {

	/** The field serialVersionUID of type long */
	private static final long serialVersionUID = -1853671297901380907L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID", unique=true, length=16, nullable=false)
	 private UUID uid;

	@Column(name="NAME", nullable=false, length=255)
	@Nationalized
	private String name;

	@Lob
	@Column(name="REQUEST_BODY")
	@Nationalized
	private String requestBody;

	@Lob
	@Column(name="REQUEST_ENDPOINT", nullable=false)
	@Nationalized
	private String requestEndpoint;

	@Column(name="REQUEST_METHOD", nullable=false, length=10)
	@Nationalized
	private String requestMethod;

	@Lob
	@Column(name="REQUEST_PATHPARAMS")
	@Nationalized
	private String requestPathparams;

	@Column(name="REQUEST_PREPROC",length=50)
	@Nationalized
	private String requestPreproc;

	@Lob
	@Column(name="REQUEST_QUERY")
	@Nationalized
	private String requestQuery;

	@Column(name="RESPONSE_POSRPROC", length=255)
	@Nationalized
	private String responsePostproc;

	@Lob
	@Column(name="RESPONSE_SCHEMA", nullable=false)
	@Nationalized
	private String responseSchema;

	@Column(name="VERSION", nullable=false, length=100)
	@Nationalized
	private String version;
	
	@Nationalized
	@Column(name="ORCHESTRATION_NAME",length=100)
	private String orchestrationName;
	
	@Lob
	@Column(name="RULE_CONTENT")
	private byte[] ruleContent;
	
	@Lob
	@Column(name="PRE_PROCESSOR")
	private byte[] preProcessor;
	
	@Lob
	@Column(name="POST_PROCESSOR")
	private byte[] postProcessor;

	//bi-directional many-to-one association to ApiRegistry
	@ManyToOne
	@JoinColumn(name="REGISTRY_ID", nullable=false)
	private ApiRegistry apiRegistry;

}