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
@Table(name="concept", indexes={ @Index(name="identifier", columnList="Identifier"), @Index(name="active", columnList="Active") })
public class Concept implements Serializable {
	public Concept() {
	}
	
	@Column(name="ID", nullable=false, length=19)	
	@Id	
	@GeneratedValue(generator="ORG_MSH_PDEX2_MODEL_R2_CONCEPT_ID_GENERATOR")	
	@org.hibernate.annotations.GenericGenerator(name="ORG_MSH_PDEX2_MODEL_R2_CONCEPT_ID_GENERATOR", strategy="native")	
	private long ID;
	
	@Column(name="Identifier", nullable=true, length=255)	
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
	
	public void setIdentifier(String value) {
		this.identifier = value;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setLabel(String value) {
		this.label = value;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setActive(boolean value) {
		this.active = value;
	}
	
	public boolean getActive() {
		return active;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
