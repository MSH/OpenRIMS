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
 * Archives of the thing. It may be archives for applications data and or for any other particular thing
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="thingarchive")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ThingArchive implements Serializable {
	public ThingArchive() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227217BE3E951D80B4D4")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227217BE3E951D80B4D4", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="amendedID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept amended;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="filecopyID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept fileCopy;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="conceptID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
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
	
	public void setFileCopy(org.msh.pdex2.model.r2.Concept value) {
		this.fileCopy = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getFileCopy() {
		return fileCopy;
	}
	
	public void setAmended(org.msh.pdex2.model.r2.Concept value) {
		this.amended = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getAmended() {
		return amended;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
