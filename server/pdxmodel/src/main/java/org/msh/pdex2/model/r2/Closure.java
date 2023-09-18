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
 * Responsible for tree-like relations between concepts. A part of "closure tree" pattern
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="closure")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Closure implements Serializable {
	public Closure() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227818A83E427CC05C49")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227818A83E427CC05C49", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="childID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept child;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="parentID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept parent;
	
	@Column(name="Level", nullable=false, length=11)	
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
	
	/**
	 * Hierarchy level
	 */
	public void setLevel(int value) {
		this.level = value;
	}
	
	/**
	 * Hierarchy level
	 */
	public int getLevel() {
		return level;
	}
	
	public void setChild(org.msh.pdex2.model.r2.Concept value) {
		this.child = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getChild() {
		return child;
	}
	
	/**
	 * The parent in the tree
	 */
	public void setParent(org.msh.pdex2.model.r2.Concept value) {
		this.parent = value;
	}
	
	/**
	 * The parent in the tree
	 */
	public org.msh.pdex2.model.r2.Concept getParent() {
		return parent;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
