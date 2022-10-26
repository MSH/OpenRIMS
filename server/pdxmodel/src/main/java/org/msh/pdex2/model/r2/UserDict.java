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
 * Responsible to link dictionaries with the users
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="userdict")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class UserDict implements Serializable {
	public UserDict() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222741838448924C07362")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222741838448924C07362", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="conceptID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
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
	
	public void setUrl(String value) {
		this.url = value;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setPredicate(String value) {
		this.predicate = value;
	}
	
	public String getPredicate() {
		return predicate;
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
