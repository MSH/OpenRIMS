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
 * Responsible to use ingredients references in the application data
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="thingingredient")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ThingIngredient implements Serializable {
	public ThingIngredient() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC222274181AB73CE5E0B147")	
	@org.hibernate.annotations.GenericGenerator(name="VAC222274181AB73CE5E0B147", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="conceptID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
	@Column(name="Varname", nullable=true, length=255)	
	private String varname;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public void setVarname(String value) {
		this.varname = value;
	}
	
	public String getVarname() {
		return varname;
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
