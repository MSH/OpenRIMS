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
@Table(name="thingdoc")
public class ThingDoc implements Serializable {
	public ThingDoc() {
	}
	
	@Column(name="ID", nullable=false, length=19)	
	@Id	
	@GeneratedValue(generator="ORG_MSH_PDEX2_MODEL_R2_THINGDOC_ID_GENERATOR")	
	@org.hibernate.annotations.GenericGenerator(name="ORG_MSH_PDEX2_MODEL_R2_THINGDOC_ID_GENERATOR", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns(value={ @JoinColumn(name="dictNodeID", referencedColumnName="ID", nullable=false) }, foreignKey=@ForeignKey(name="FKthingdoc250361"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.Concept dictNode;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns(value={ @JoinColumn(name="conceptID", referencedColumnName="ID", nullable=false) }, foreignKey=@ForeignKey(name="FKthingdoc737617"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
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
	
	public void setVarName(String value) {
		this.varName = value;
	}
	
	public String getVarName() {
		return varName;
	}
	
	public void setDocUrl(String value) {
		this.docUrl = value;
	}
	
	public String getDocUrl() {
		return docUrl;
	}
	
	public void setDictNode(org.msh.pdex2.model.r2.Concept value) {
		this.dictNode = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getDictNode() {
		return dictNode;
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
