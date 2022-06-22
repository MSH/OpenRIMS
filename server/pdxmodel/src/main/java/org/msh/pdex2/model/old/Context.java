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
package org.msh.pdex2.model.old;

import java.io.Serializable;
import javax.persistence.*;
/**
 * Responsible for store data between HTML calls
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="context")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Context implements Serializable {
	public Context() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222741818604E90407124")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222741818604E90407124", strategy="native")	
	private long ID;
	
	@OneToMany(targetEntity=org.msh.pdex2.model.old.Criteria.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumn(name="ContextID", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.old.Criteria> criteria = new java.util.HashSet<org.msh.pdex2.model.old.Criteria>();
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public void setCriteria(java.util.Set<org.msh.pdex2.model.old.Criteria> value) {
		this.criteria = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.old.Criteria> getCriteria() {
		return criteria;
	}
	
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
