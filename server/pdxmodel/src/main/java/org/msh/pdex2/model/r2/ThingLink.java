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
@Table(name="thinglink")
public class ThingLink implements Serializable {
	public ThingLink() {
	}
	
	@Column(name="ID", nullable=false, length=19)	
	@Id	
	@GeneratedValue(generator="ORG_MSH_PDEX2_MODEL_R2_THINGLINK_ID_GENERATOR")	
	@org.hibernate.annotations.GenericGenerator(name="ORG_MSH_PDEX2_MODEL_R2_THINGLINK_ID_GENERATOR", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns(value={ @JoinColumn(name="linkedObjectID", referencedColumnName="ID", nullable=false) }, foreignKey=@ForeignKey(name="FKthinglink598140"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.Concept linkedObject;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns(value={ @JoinColumn(name="dictItemID", referencedColumnName="ID") }, foreignKey=@ForeignKey(name="FKthinglink78883"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
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
	
	public void setLinkUrl(String value) {
		this.linkUrl = value;
	}
	
	public String getLinkUrl() {
		return linkUrl;
	}
	
	public void setDictUrl(String value) {
		this.dictUrl = value;
	}
	
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
