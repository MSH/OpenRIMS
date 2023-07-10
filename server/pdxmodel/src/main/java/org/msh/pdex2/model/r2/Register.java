/**
 * "Visual Paradigm: DO NOT MODIFY THIS FILE!"
 * 
 * This is an automatic generated file. It will be regenerated every time 
 * you generate persistence class.
 * 
 * Modifying its content may cause the program not work, or your work may lost.
 */

/**
 * Licensee: DuKe TeAm
 * License Type: Purchased
 */
package org.msh.pdex2.model.r2;

import java.io.Serializable;
import javax.persistence.*;
/**
 * Contains registration numbers and dates. It is a relaxed implementation of filing system automation.
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="register")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Register implements Serializable {
	public Register() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222721892C64533F00474")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222721892C64533F00474", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns({ @JoinColumn(name="conceptID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
	@Column(name="RegisteredAt", nullable=true)	
	private java.util.Date registeredAt;
	
	@Column(name="Register", nullable=true, length=255)	
	private String register;
	
	@Column(name="ValidTo", nullable=true)	
	private java.util.Date validTo;
	
	@Column(name="CreatedAt", nullable=true)	
	private java.util.Date createdAt;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	/**
	 * Registration date
	 */
	public void setRegisteredAt(java.util.Date value) {
		this.registeredAt = value;
	}
	
	/**
	 * Registration date
	 */
	public java.util.Date getRegisteredAt() {
		return registeredAt;
	}
	
	/**
	 * Register number
	 */
	public void setRegister(String value) {
		this.register = value;
	}
	
	/**
	 * Register number
	 */
	public String getRegister() {
		return register;
	}
	
	/**
	 * Valid to date
	 */
	public void setValidTo(java.util.Date value) {
		this.validTo = value;
	}
	
	/**
	 * Valid to date
	 */
	public java.util.Date getValidTo() {
		return validTo;
	}
	
	public void setCreatedAt(java.util.Date value) {
		this.createdAt = value;
	}
	
	public java.util.Date getCreatedAt() {
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
