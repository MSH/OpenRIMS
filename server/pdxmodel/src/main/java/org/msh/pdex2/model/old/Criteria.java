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
 * Responsible to group data stored between HTMl calls
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="criteria")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Criteria implements Serializable {
	public Criteria() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222721894FAF244A01E40")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222721894FAF244A01E40", strategy="native")	
	private long ID;
	
	@Column(name="Name", nullable=true, length=255)	
	private String name;
	
	@Column(name="Criteria", nullable=true)	
	private String criteria;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return name;
	}
	
	public void setCriteria(String value) {
		this.criteria = value;
	}
	
	public String getCriteria() {
		return criteria;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
