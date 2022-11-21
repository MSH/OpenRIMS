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
 * Allows link other objects to a given. Relation is m:m
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="thinglink")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ThingLink implements Serializable {
	public ThingLink() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222741845D6414E104DEC")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222741845D6414E104DEC", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="linkedObjectID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept linkedObject;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="dictItemID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept dictItem;
	
	@Column(name="LinkUrl", nullable=true, length=255)	
	private String linkUrl;
	
	@Column(name="DictUrl", nullable=true, length=255)	
	private String dictUrl;
	
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
	
	/**
	 * URL of a linked object
	 */
	public void setLinkUrl(String value) {
		this.linkUrl = value;
	}
	
	/**
	 * URL of a linked object
	 */
	public String getLinkUrl() {
		return linkUrl;
	}
	
	/**
	 * Optional classifier for a linked object, e.g. The Beatles->Finished Product Manufacturer
	 */
	public void setDictUrl(String value) {
		this.dictUrl = value;
	}
	
	/**
	 * Optional classifier for a linked object, e.g. The Beatles->Finished Product Manufacturer
	 */
	public String getDictUrl() {
		return dictUrl;
	}
	
	public void setVarName(String value) {
		this.varName = value;
	}
	
	public String getVarName() {
		return varName;
	}
	
	public void setLinkedObject(org.msh.pdex2.model.r2.Concept value) {
		this.linkedObject = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getLinkedObject() {
		return linkedObject;
	}
	
	public void setDictItem(org.msh.pdex2.model.r2.Concept value) {
		this.dictItem = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getDictItem() {
		return dictItem;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
