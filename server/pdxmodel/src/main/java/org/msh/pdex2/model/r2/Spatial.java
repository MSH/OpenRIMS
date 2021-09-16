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
@Table(name="spatialdata")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("Spatial")
public class Spatial implements Serializable {
	public Spatial() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227217BE3E951C30B4C3")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227217BE3E951C30B4C3", strategy="native")	
	private long ID;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
