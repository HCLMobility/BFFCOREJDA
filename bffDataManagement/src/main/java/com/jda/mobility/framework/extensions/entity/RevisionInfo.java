/**
 * 
 */
package com.jda.mobility.framework.extensions.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import com.jda.mobility.framework.extensions.common.RevisionInfoListener;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author HCL Technologies Ltd.
 *
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE) @EqualsAndHashCode
@Table(name = "REVISION_INFO")
@Entity
@RevisionEntity(RevisionInfoListener.class)
public class RevisionInfo implements Serializable {

	private static final long serialVersionUID = -8256493854539429801L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rev_info_generator")
	@SequenceGenerator(name = "rev_info_generator", allocationSize = 10, sequenceName = "rev_info_seq")
	@RevisionNumber
	@Column(name="ID",unique=true, length=16, nullable=false)
	private int id;

	@RevisionTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE")
	private Date date;

	@Column(name = "USER_NAME")
	private String userName;
}
