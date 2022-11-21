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
 * Responsible to use uploaded files in the application data
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="thingdoc")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ThingDoc implements Serializable {
	public ThingDoc() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222741845D6414D104DDE")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222741845D6414D104DDE", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="dictNodeID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept dictNode;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="conceptID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
	@Column(name="DictUrl", nullable=true, length=255)	
	private String dictUrl;
	
	@Column(name="VarName", nullable=true, length=255)	
	private String varName;
	
	@Column(name="DocUrl", nullable=true, length=255)	
	private String docUrl;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public void setDictUrl(String value) {
		this.dictUrl = value;
	}
	
	public String getDictUrl() {
		return dictUrl;
	}
	
	public void setDocUrl(String value) {
		this.docUrl = value;
	}
	
	public String getDocUrl() {
		return docUrl;
	}
	
	public void setVarName(String value) {
		this.varName = value;
	}
	
	public String getVarName() {
		return varName;
	}
	
	public void setConcept(org.msh.pdex2.model.r2.Concept value) {
		this.concept = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getConcept() {
		return concept;
	}
	
	public void setDictNode(org.msh.pdex2.model.r2.Concept value) {
		this.dictNode = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getDictNode() {
		return dictNode;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
