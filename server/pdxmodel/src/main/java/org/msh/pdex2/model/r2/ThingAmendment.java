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
 * Responsible for amendment - amended relations
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="thingamendment")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ThingAmendment implements Serializable {
	public ThingAmendment() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227418129ECB5810CEBF")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227418129ECB5810CEBF", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="conceptID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns({ @JoinColumn(name="applicationDataID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept applicationData;
	
	@Column(name="Url", nullable=true, length=255)	
	private String url;
	
	@Column(name="VarName", nullable=true, length=255)	
	private String varName;
	
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
	
	public void setVarName(String value) {
		this.varName = value;
	}
	
	public String getVarName() {
		return varName;
	}
	
	public void setApplicationData(org.msh.pdex2.model.r2.Concept value) {
		this.applicationData = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getApplicationData() {
		return applicationData;
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
