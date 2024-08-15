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
@Table(name="thing")
public class Thing implements Serializable {
	public Thing() {
	}
	
	@Column(name="ID", nullable=false, length=19)	
	@Id	
	@GeneratedValue(generator="ORG_MSH_PDEX2_MODEL_R2_THING_ID_GENERATOR")	
	@org.hibernate.annotations.GenericGenerator(name="ORG_MSH_PDEX2_MODEL_R2_THING_ID_GENERATOR", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns(value={ @JoinColumn(name="conceptID", referencedColumnName="ID", nullable=false) }, foreignKey=@ForeignKey(name="FKthing613083"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
	@Column(name="CreatedAt", nullable=false, insertable=false, updatable=false, length=19)	
	private java.sql.Timestamp createdAt;
	
	@Column(name="ChangedAt", nullable=false, insertable=false, updatable=false, length=19)	
	private java.sql.Timestamp changedAt;
	
	@Column(name="Url", nullable=true, length=255)	
	private String url;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.ThingOld.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns(value={ @JoinColumn(name="thingoldID", referencedColumnName="ID") }, foreignKey=@ForeignKey(name="FKthing644254"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.ThingOld oldValue;
	
	@OneToMany(orphanRemoval=true, targetEntity=org.msh.pdex2.model.r2.ThingAmendment.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL})	
	@JoinColumns({ @JoinColumn(name="thingID", nullable=true) })	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingAmendment> amendments = new java.util.HashSet<org.msh.pdex2.model.r2.ThingAmendment>();
	
	@OneToMany(orphanRemoval=true, targetEntity=org.msh.pdex2.model.r2.ThingAtc.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL})	
	@JoinColumns({ @JoinColumn(name="thingID", nullable=true) })	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingAtc> atcodes = new java.util.HashSet<org.msh.pdex2.model.r2.ThingAtc>();
	
	@OneToMany(orphanRemoval=true, targetEntity=org.msh.pdex2.model.r2.ThingDict.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL})	
	@JoinColumns({ @JoinColumn(name="thingID", nullable=true) })	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingDict> dictionaries = new java.util.HashSet<org.msh.pdex2.model.r2.ThingDict>();
	
	@OneToMany(orphanRemoval=true, targetEntity=org.msh.pdex2.model.r2.ThingDoc.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL})	
	@JoinColumns({ @JoinColumn(name="thingID", nullable=true) })	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingDoc> documents = new java.util.HashSet<org.msh.pdex2.model.r2.ThingDoc>();
	
	@OneToMany(orphanRemoval=true, targetEntity=org.msh.pdex2.model.r2.ThingIngredient.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL})	
	@JoinColumns({ @JoinColumn(name="thingID", nullable=true) })	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingIngredient> ingredients = new java.util.HashSet<org.msh.pdex2.model.r2.ThingIngredient>();
	
	@OneToMany(orphanRemoval=true, targetEntity=org.msh.pdex2.model.r2.ThingLegacyData.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL})	
	@JoinColumns({ @JoinColumn(name="thingID", nullable=true) })	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingLegacyData> legacyData = new java.util.HashSet<org.msh.pdex2.model.r2.ThingLegacyData>();
	
	@OneToMany(orphanRemoval=true, targetEntity=org.msh.pdex2.model.r2.ThingLink.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL})	
	@JoinColumns({ @JoinColumn(name="thingID", nullable=true) })	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingLink> thingLinks = new java.util.HashSet<org.msh.pdex2.model.r2.ThingLink>();
	
	@OneToMany(orphanRemoval=true, targetEntity=org.msh.pdex2.model.r2.ThingPerson.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL})	
	@JoinColumns({ @JoinColumn(name="thingID", nullable=true) })	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingPerson> persons = new java.util.HashSet<org.msh.pdex2.model.r2.ThingPerson>();
	
	@OneToMany(orphanRemoval=true, targetEntity=org.msh.pdex2.model.r2.ThingRegister.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL})	
	@JoinColumns({ @JoinColumn(name="thingID", nullable=true) })	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingRegister> registers = new java.util.HashSet<org.msh.pdex2.model.r2.ThingRegister>();
	
	@OneToMany(orphanRemoval=true, targetEntity=org.msh.pdex2.model.r2.ThingScheduler.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL})	
	@JoinColumns({ @JoinColumn(name="thingID", nullable=true) })	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingScheduler> schedulers = new java.util.HashSet<org.msh.pdex2.model.r2.ThingScheduler>();
	
	@OneToMany(orphanRemoval=true, targetEntity=org.msh.pdex2.model.r2.ThingThing.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL})	
	@JoinColumns({ @JoinColumn(name="thingID", nullable=true) })	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.ThingThing> things = new java.util.HashSet<org.msh.pdex2.model.r2.ThingThing>();
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public java.sql.Timestamp getCreatedAt() {
		return createdAt;
	}
	
	public java.sql.Timestamp getChangedAt() {
		return changedAt;
	}
	
	public void setUrl(String value) {
		this.url = value;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setConcept(org.msh.pdex2.model.r2.Concept value) {
		this.concept = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getConcept() {
		return concept;
	}
	
	public void setOldValue(org.msh.pdex2.model.r2.ThingOld value) {
		this.oldValue = value;
	}
	
	public org.msh.pdex2.model.r2.ThingOld getOldValue() {
		return oldValue;
	}
	
	public void setAmendments(java.util.Set<org.msh.pdex2.model.r2.ThingAmendment> value) {
		this.amendments = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.ThingAmendment> getAmendments() {
		return amendments;
	}
	
	
	public void setAtcodes(java.util.Set<org.msh.pdex2.model.r2.ThingAtc> value) {
		this.atcodes = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.ThingAtc> getAtcodes() {
		return atcodes;
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
	
	
	public void setIngredients(java.util.Set<org.msh.pdex2.model.r2.ThingIngredient> value) {
		this.ingredients = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.ThingIngredient> getIngredients() {
		return ingredients;
	}
	
	
	public void setLegacyData(java.util.Set<org.msh.pdex2.model.r2.ThingLegacyData> value) {
		this.legacyData = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.ThingLegacyData> getLegacyData() {
		return legacyData;
	}
	
	
	public void setThingLinks(java.util.Set<org.msh.pdex2.model.r2.ThingLink> value) {
		this.thingLinks = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.ThingLink> getThingLinks() {
		return thingLinks;
	}
	
	
	public void setPersons(java.util.Set<org.msh.pdex2.model.r2.ThingPerson> value) {
		this.persons = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.ThingPerson> getPersons() {
		return persons;
	}
	
	
	public void setRegisters(java.util.Set<org.msh.pdex2.model.r2.ThingRegister> value) {
		this.registers = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.ThingRegister> getRegisters() {
		return registers;
	}
	
	
	public void setSchedulers(java.util.Set<org.msh.pdex2.model.r2.ThingScheduler> value) {
		this.schedulers = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.ThingScheduler> getSchedulers() {
		return schedulers;
	}
	
	
	public void setThings(java.util.Set<org.msh.pdex2.model.r2.ThingThing> value) {
		this.things = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.ThingThing> getThings() {
		return things;
	}
	
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
