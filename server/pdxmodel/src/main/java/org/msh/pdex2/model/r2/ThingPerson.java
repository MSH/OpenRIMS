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
 * Allows use reference to one or more persons in the application - owners, pharmacists, etc
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="thingperson")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ThingPerson implements Serializable {
	public ThingPerson() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC222274184AF102BAB0E80F")	
	@org.hibernate.annotations.GenericGenerator(name="VAC222274184AF102BAB0E80F", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="conceptID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
	@Column(name="PersonUrl", nullable=true, length=255)	
	private String personUrl;
	
	@Column(name="VarName", nullable=true, length=255)	
	private String varName;
	
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
	 * Root url of the tree keeps this category of persons
	 */
	public void setPersonUrl(String value) {
		this.personUrl = value;
	}
	
	/**
	 * Root url of the tree keeps this category of persons
	 */
	public String getPersonUrl() {
		return personUrl;
	}
	
	public void setVarName(String value) {
		this.varName = value;
	}
	
	public String getVarName() {
		return varName;
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
