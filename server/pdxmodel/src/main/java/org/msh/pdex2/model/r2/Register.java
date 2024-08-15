/**
 * "Visual Paradigm: DO NOT MODIFY THIS FILE!"
 * 
 * This is an automatic generated file. It will be regenerated every time 
 * you generate persistence class.
 * 
 * Modifying its content may cause the program not work, or your work may lost.
 */

/**
 * Licensee: 
 * License Type: Evaluation
 */
package org.msh.pdex2.model.r2;

import java.io.Serializable;
import javax.persistence.*;
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="register", indexes={ @Index(name="byConceptID", columnList="conceptID") })
public class Register implements Serializable {
	public Register() {
	}
	
	@Column(name="ID", nullable=false, length=19)	
	@Id	
	@GeneratedValue(generator="ORG_MSH_PDEX2_MODEL_R2_REGISTER_ID_GENERATOR")	
	@org.hibernate.annotations.GenericGenerator(name="ORG_MSH_PDEX2_MODEL_R2_REGISTER_ID_GENERATOR", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns(value={ @JoinColumn(name="conceptID", referencedColumnName="ID", nullable=false) }, foreignKey=@ForeignKey(name="FKregister932047"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
	@Column(name="RegisteredAt", nullable=true)	
	@Temporal(TemporalType.DATE)	
	private java.util.Date registeredAt;
	
	@Column(name="Register", nullable=true, length=255)	
	private String register;
	
	@Column(name="ValidTo", nullable=true)	
	@Temporal(TemporalType.DATE)	
	private java.util.Date validTo;
	
	@Column(name="CreatedAt", nullable=false, insertable=false, updatable=false, length=19)	
	private java.sql.Timestamp createdAt;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public void setRegisteredAt(java.util.Date value) {
		this.registeredAt = value;
	}
	
	public java.util.Date getRegisteredAt() {
		return registeredAt;
	}
	
	public void setRegister(String value) {
		this.register = value;
	}
	
	public String getRegister() {
		return register;
	}
	
	public void setValidTo(java.util.Date value) {
		this.validTo = value;
	}
	
	public java.util.Date getValidTo() {
		return validTo;
	}
	
	public java.sql.Timestamp getCreatedAt() {
		return createdAt;
	}
	
	public void setConcept(org.msh.pdex2.model.r2.Concept value) {
		this.concept = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getConcept() {
		return concept;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
