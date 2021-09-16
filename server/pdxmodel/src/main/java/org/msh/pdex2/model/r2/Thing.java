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
@Table(name="thing")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Thing implements Serializable {
	public Thing() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227217BE3E951CB0B4CA")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227217BE3E951CB0B4CA", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="conceptID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
	@Column(name="CreatedAt", nullable=true)	
	private java.util.Date createdAt;
	
	@Column(name="ChangedAt", nullable=true)	
	private java.util.Date changedAt;
	
	@Column(name="Url", nullable=true, length=255)	
	private String url;
	
	@OneToMany(targetEntity=org.msh.pdex2.model.r2.ThingPerson.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumn(name="thingID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingPerson> persons = new java.util.HashSet<org.msh.pdex2.model.r2.ThingPerson>();
	
	@OneToMany(targetEntity=org.msh.pdex2.model.r2.ThingDict.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumn(name="thingID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingDict> dictionaries = new java.util.HashSet<org.msh.pdex2.model.r2.ThingDict>();
	
	@OneToMany(targetEntity=org.msh.pdex2.model.r2.ThingDoc.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumn(name="thingID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingDoc> documents = new java.util.HashSet<org.msh.pdex2.model.r2.ThingDoc>();
	
	@OneToMany(targetEntity=org.msh.pdex2.model.r2.ThingThing.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumn(name="thingID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingThing> things = new java.util.HashSet<org.msh.pdex2.model.r2.ThingThing>();
	
	@OneToMany(targetEntity=org.msh.pdex2.model.r2.ThingScheduler.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumn(name="thingID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingScheduler> schedulers = new java.util.HashSet<org.msh.pdex2.model.r2.ThingScheduler>();
	
	@OneToMany(targetEntity=org.msh.pdex2.model.r2.ThingRegister.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumn(name="thingID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingRegister> registers = new java.util.HashSet<org.msh.pdex2.model.r2.ThingRegister>();
	
	@OneToMany(targetEntity=org.msh.pdex2.model.r2.ThingArchive.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumn(name="thingID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingArchive> archives = new java.util.HashSet<org.msh.pdex2.model.r2.ThingArchive>();
	
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
	 * URL of this thing
	 */
	public void setUrl(String value) {
		this.url = value;
	}
	
	/**
	 * URL of this thing
	 */
	public String getUrl() {
		return url;
	}
	
	public void setCreatedAt(java.util.Date value) {
		this.createdAt = value;
	}
	
	public java.util.Date getCreatedAt() {
		return createdAt;
	}
	
	public void setChangedAt(java.util.Date value) {
		this.changedAt = value;
	}
	
	public java.util.Date getChangedAt() {
		return changedAt;
	}
	
	public void setPersons(java.util.Set<org.msh.pdex2.model.r2.ThingPerson> value) {
		this.persons = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.ThingPerson> getPersons() {
		return persons;
	}
	
	
	public void setConcept(org.msh.pdex2.model.r2.Concept value) {
		this.concept = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getConcept() {
		return concept;
	}
	
	public void setDictionaries(java.util.Set<org.msh.pdex2.model.r2.ThingDict> value) {
		this.dictionaries = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.ThingDict> getDictionaries() {
		return dictionaries;
	}
	
	
	public void setDocuments(java.util.Set<org.msh.pdex2.model.r2.ThingDoc> value) {
		this.documents = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.ThingDoc> getDocuments() {
		return documents;
	}
	
	
	public void setThings(java.util.Set<org.msh.pdex2.model.r2.ThingThing> value) {
		this.things = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.ThingThing> getThings() {
		return things;
	}
	
	
	public void setSchedulers(java.util.Set<org.msh.pdex2.model.r2.ThingScheduler> value) {
		this.schedulers = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.ThingScheduler> getSchedulers() {
		return schedulers;
	}
	
	
	public void setRegisters(java.util.Set<org.msh.pdex2.model.r2.ThingRegister> value) {
		this.registers = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.ThingRegister> getRegisters() {
		return registers;
	}
	
	
	public void setArchives(java.util.Set<org.msh.pdex2.model.r2.ThingArchive> value) {
		this.archives = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.ThingArchive> getArchives() {
		return archives;
	}
	
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
