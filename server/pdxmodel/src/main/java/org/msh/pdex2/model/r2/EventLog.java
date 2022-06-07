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
 * Log for application events
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="eventlog")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class EventLog implements Serializable {
	public EventLog() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227418129ECB57C0CEBB")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227418129ECB57C0CEBB", strategy="native")	
	private long ID;
	
	@Column(name="Email", nullable=true, length=255)	
	private String email;
	
	@Column(name="Source", nullable=true, length=255)	
	private String source;
	
	@Column(name="Message", nullable=true, length=255)	
	private String message;
	
	@Column(name="ConceptId", nullable=false, length=20)	
	private long conceptId;
	
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
	 * User's email
	 */
	public void setEmail(String value) {
		this.email = value;
	}
	
	/**
	 * User's email
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * Where this message came from
	 */
	public void setSource(String value) {
		this.source = value;
	}
	
	/**
	 * Where this message came from
	 */
	public String getSource() {
		return source;
	}
	
	/**
	 * message from the user
	 */
	public void setMessage(String value) {
		this.message = value;
	}
	
	/**
	 * message from the user
	 */
	public String getMessage() {
		return message;
	}
	
	public void setConceptId(long value) {
		this.conceptId = value;
	}
	
	public long getConceptId() {
		return conceptId;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
