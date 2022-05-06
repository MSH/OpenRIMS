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
 * The relaxed implementation of the SCOS concept model. The closure table is using to implement the tree
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="concept")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("Concept")
public class Concept implements Serializable {
	public Concept() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222741804C28663F075C3")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222741804C28663F075C3", strategy="native")	
	private long ID;
	
	@Column(name="Identifier", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="identifier")	
	private String identifier;
	
	@Column(name="Label", nullable=true)	
	private String label;
	
	@Column(name="Active", nullable=false, length=1)	
	private boolean active = true;
	
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
	 * Any more or less unique identifier for this resource that represented as String type. The name of tree, branch, leaf. Examples are NMRA, Import Department, Ministry of Health, etc
	 */
	public void setIdentifier(String value) {
		this.identifier = value;
	}
	
	/**
	 * Any more or less unique identifier for this resource that represented as String type. The name of tree, branch, leaf. Examples are NMRA, Import Department, Ministry of Health, etc
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * Any string label. Semantic of it is in identifier
	 */
	public void setLabel(String value) {
		this.label = value;
	}
	
	/**
	 * Any string label. Semantic of it is in identifier
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * To implement possibility to turn on/off dictionary items
	 */
	public void setActive(boolean value) {
		this.active = value;
	}
	
	/**
	 * To implement possibility to turn on/off dictionary items
	 */
	public boolean getActive() {
		return active;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
