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
@Table(name="closure", indexes={ @Index(name="byLevel", columnList="Level") })
public class Closure implements Serializable {
	public Closure() {
	}
	
	@Column(name="ID", nullable=false, length=19)	
	@Id	
	@GeneratedValue(generator="ORG_MSH_PDEX2_MODEL_R2_CLOSURE_ID_GENERATOR")	
	@org.hibernate.annotations.GenericGenerator(name="ORG_MSH_PDEX2_MODEL_R2_CLOSURE_ID_GENERATOR", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns(value={ @JoinColumn(name="childID", referencedColumnName="ID") }, foreignKey=@ForeignKey(name="FKclosure121614"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.Concept child;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns(value={ @JoinColumn(name="parentID", referencedColumnName="ID") }, foreignKey=@ForeignKey(name="FKclosure226963"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.Concept parent;
	
	@Column(name="Level", nullable=false, length=10)	
	private int level;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public void setLevel(int value) {
		this.level = value;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setChild(org.msh.pdex2.model.r2.Concept value) {
		this.child = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getChild() {
		return child;
	}
	
	public void setParent(org.msh.pdex2.model.r2.Concept value) {
		this.parent = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getParent() {
		return parent;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
