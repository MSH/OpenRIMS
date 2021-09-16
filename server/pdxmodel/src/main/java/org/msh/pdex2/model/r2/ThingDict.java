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
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="thingdict")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ThingDict implements Serializable {
	public ThingDict() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227217BE3E951CD0B4CB")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227217BE3E951CD0B4CB", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="conceptID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
	@Column(name="Url", nullable=true, length=255)	
	private String url;
	
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
	
	public void setUrl(String value) {
		this.url = value;
	}
	
	public String getUrl() {
		return url;
	}
	
	/**
	 * variable name
	 */
	public void setVarname(String value) {
		this.varname = value;
	}
	
	/**
	 * variable name
	 */
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
