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
 * Relation to public organization responsibility description (URL)
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="publicorgsubject")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class PublicOrgSubject implements Serializable {
	public PublicOrgSubject() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227217E2154D03303FCD")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227217E2154D03303FCD", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="conceptID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept node;
	
	@Column(name="Url", nullable=true, length=255)	
	private String url;
	
	@Column(name="Predicate", nullable=true, length=255)	
	private String predicate;
	
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
	 * URI of a subject
	 */
	public void setUrl(String value) {
		this.url = value;
	}
	
	/**
	 * URI of a subject
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Describe a relation. For future extension
	 */
	public void setPredicate(String value) {
		this.predicate = value;
	}
	
	/**
	 * Describe a relation. For future extension
	 */
	public String getPredicate() {
		return predicate;
	}
	
	/**
	 * A node of the subject
	 */
	public void setNode(org.msh.pdex2.model.r2.Concept value) {
		this.node = value;
	}
	
	/**
	 * A node of the subject
	 */
	public org.msh.pdex2.model.r2.Concept getNode() {
		return node;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
