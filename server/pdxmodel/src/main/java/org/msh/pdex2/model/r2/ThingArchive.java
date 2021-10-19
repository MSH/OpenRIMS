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
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="thingarchive")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ThingArchive implements Serializable {
	public ThingArchive() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22226E17C9085EA0306EDE")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22226E17C9085EA0306EDE", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Thing.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="thingID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Thing thing;
	
	@Column(name="ThingDto", nullable=true, length=255)	
	private String thingDto;
	
	@Column(name="CreatedAt", nullable=true)	
	private java.util.Date createdAt;
	
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
	 * Thing DTO in JSON format
	 */
	public void setThingDto(String value) {
		this.thingDto = value;
	}
	
	/**
	 * Thing DTO in JSON format
	 */
	public String getThingDto() {
		return thingDto;
	}
	
	public void setCreatedAt(java.util.Date value) {
		this.createdAt = value;
	}
	
	public java.util.Date getCreatedAt() {
		return createdAt;
	}
	
	public void setThing(org.msh.pdex2.model.r2.Thing value) {
		this.thing = value;
	}
	
	public org.msh.pdex2.model.r2.Thing getThing() {
		return thing;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
