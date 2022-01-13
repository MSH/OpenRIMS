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
	@GeneratedValue(generator="VAC22227217E2154D02D03FC8")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227217E2154D02D03FC8", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="conceptID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.WebResource.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns({ @JoinColumn(name="webresourceID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.WebResource logo;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.OrgRole.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="orgroleID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.OrgRole orgRole;
	
	@OneToMany(targetEntity=org.msh.pdex2.model.r2.PublicOrgSubject.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumn(name="publicorganizationID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.PublicOrgSubject> subjects = new java.util.HashSet<org.msh.pdex2.model.r2.PublicOrgSubject>();
	
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
	 * A property to link an organization to its logo. The value of this property can simply be the URL of the logo but it is better for developers if it links to an object that provides the URL of the image and essential metadata about it, notably its dimensions
	 */
	public void setLogo(org.msh.pdex2.model.r2.WebResource value) {
		this.logo = value;
	}
	
	/**
	 * A property to link an organization to its logo. The value of this property can simply be the URL of the logo but it is better for developers if it links to an object that provides the URL of the image and essential metadata about it, notably its dimensions
	 */
	public org.msh.pdex2.model.r2.WebResource getLogo() {
		return logo;
	}
	
	public void setConcept(org.msh.pdex2.model.r2.Concept value) {
		this.concept = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getConcept() {
		return concept;
	}
	
	public void setOrgRole(org.msh.pdex2.model.r2.OrgRole value) {
		this.orgRole = value;
	}
	
	public org.msh.pdex2.model.r2.OrgRole getOrgRole() {
		return orgRole;
	}
	
	/**
	 * Relation to subjects.
	 */
	public void setSubjects(java.util.Set<org.msh.pdex2.model.r2.PublicOrgSubject> value) {
		this.subjects = value;
	}
	
	/**
	 * Relation to subjects.
	 */
	public java.util.Set<org.msh.pdex2.model.r2.PublicOrgSubject> getSubjects() {
		return subjects;
	}
	
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
