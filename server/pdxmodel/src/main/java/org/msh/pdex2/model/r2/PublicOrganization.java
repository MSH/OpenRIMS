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
 * The Public Organization class represents the organization. One organization may comprise several sub-organizations and any organization may have one or more organizational units. Each of these is described using the same properties and relationships.
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="publicorganization")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("PublicOrganization")
public class PublicOrganization implements Serializable {
	public PublicOrganization() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22226E1864BE179A20BC92")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22226E1864BE179A20BC92", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="conceptID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
	@OneToMany(targetEntity=org.msh.pdex2.model.r2.OrgAdmin.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumn(name="publicorganizationID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.OrgAdmin> adminUnits = new java.util.HashSet<org.msh.pdex2.model.r2.OrgAdmin>();
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public void setConcept(org.msh.pdex2.model.r2.Concept value) {
		this.concept = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getConcept() {
		return concept;
	}
	
	public void setAdminUnits(java.util.Set<org.msh.pdex2.model.r2.OrgAdmin> value) {
		this.adminUnits = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.OrgAdmin> getAdminUnits() {
		return adminUnits;
	}
	
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
