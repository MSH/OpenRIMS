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
package org.msh.pdex2.model.dwh;

import java.io.Serializable;
import javax.persistence.*;
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="reportassembly")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ReportAssembly implements Serializable {
	public ReportAssembly() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227418098A2C21E03831")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227418098A2C21E03831", strategy="native")	
	private long ID;
	
	@Column(name="ObjectConceptId", nullable=false, length=20)	
	private long objectConceptId;
	
	@Column(name="AssmConceptID", nullable=false, length=20)	
	private long assmConceptID;
	
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
	 * Concept of the main object - the initial application
	 */
	public void setObjectConceptId(long value) {
		this.objectConceptId = value;
	}
	
	/**
	 * Concept of the main object - the initial application
	 */
	public long getObjectConceptId() {
		return objectConceptId;
	}
	
	/**
	 * The related concept
	 */
	public void setAssmConceptID(long value) {
		this.assmConceptID = value;
	}
	
	/**
	 * The related concept
	 */
	public long getAssmConceptID() {
		return assmConceptID;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
