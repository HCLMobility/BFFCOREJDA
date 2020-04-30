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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the product_config database table.
 * 
 */
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode
@JsonIgnoreProperties(value = { "flow" })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "uid")
@Entity
@Table(name = "PRODUCT_CONFIG")
@NamedQuery(name = "ProductConfig.findAll", query = "SELECT p FROM ProductConfig p")
public class ProductConfig implements Serializable {
	private static final long serialVersionUID = 4317913604559471608L;

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "com.jda.mobility.framework.extensions.common.BffUUIDGenerator")
	@Column(name="UID",unique=true, length=16, nullable=false)
	private UUID uid;

	@Column(name = "PRIMARY_REF_ID", length=16)
	private UUID primaryRefId;

	@Column(name = "SECONDARY_REF_ID", length=16)
	private UUID secondaryRefId;

	@Column(name = "VERSION_ID", nullable = false)
	private int versionId;

	// bi-directional many-to-one association to RoleMaster
	@ManyToOne
	@JoinColumn(name = "ROLE_ID", nullable = false)
	private RoleMaster roleMaster;

	@OneToMany(mappedBy="productConfig", cascade = CascadeType.ALL)
	private List<Flow> flow = new ArrayList<>();
}