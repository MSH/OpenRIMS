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
@Table(name="amended")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Amended implements Serializable {
	public Amended() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227217BE3E951DC0B4D6")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227217BE3E951DC0B4D6", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Assembly.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="assemblyID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Assembly assembly;
	
	@Column(name="OldValue", nullable=true, length=255)	
	private String oldValue;
	
	@Column(name="NewValue", nullable=true, length=255)	
	private String newValue;
	
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
	 * JSON serialized if possible
	 */
	public void setOldValue(String value) {
		this.oldValue = value;
	}
	
	/**
	 * JSON serialized if possible
	 */
	public String getOldValue() {
		return oldValue;
	}
	
	/**
	 * JSON serialized, if possible
	 */
	public void setNewValue(String value) {
		this.newValue = value;
	}
	
	/**
	 * JSON serialized, if possible
	 */
	public String getNewValue() {
		return newValue;
	}
	
	public void setAssembly(org.msh.pdex2.model.r2.Assembly value) {
		this.assembly = value;
	}
	
	public org.msh.pdex2.model.r2.Assembly getAssembly() {
		return assembly;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
