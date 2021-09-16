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
@Table(name="amendment")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Amendment implements Serializable {
	public Amendment() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227217BE3E951DA0B4D5")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227217BE3E951DA0B4D5", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="amendedThingID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept amendedThing;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="applicationDataID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept applicationData;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="conceptID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
	@Column(name="OldThing", nullable=true)	
	private String oldThing;
	
	@Column(name="NewThing", nullable=true)	
	private String newThing;
	
	@Column(name="CreatedAt", nullable=true)	
	private java.util.Date createdAt;
	
	@OneToMany(targetEntity=org.msh.pdex2.model.r2.Amended.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumn(name="amendmentID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.Amended> amended = new java.util.HashSet<org.msh.pdex2.model.r2.Amended>();
	
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
	 * JSON serialized old thing
	 */
	public void setOldThing(String value) {
		this.oldThing = value;
	}
	
	/**
	 * JSON serialized old thing
	 */
	public String getOldThing() {
		return oldThing;
	}
	
	/**
	 * JSON serialized new thing
	 */
	public void setNewThing(String value) {
		this.newThing = value;
	}
	
	/**
	 * JSON serialized new thing
	 */
	public String getNewThing() {
		return newThing;
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
	
	public void setApplicationData(org.msh.pdex2.model.r2.Concept value) {
		this.applicationData = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getApplicationData() {
		return applicationData;
	}
	
	public void setAmendedThing(org.msh.pdex2.model.r2.Concept value) {
		this.amendedThing = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getAmendedThing() {
		return amendedThing;
	}
	
	public void setAmended(java.util.Set<org.msh.pdex2.model.r2.Amended> value) {
		this.amended = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.Amended> getAmended() {
		return amended;
	}
	
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
