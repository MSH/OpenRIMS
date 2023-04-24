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
 * Common superclass for all legacy data classes
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="legacydata")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class LegacyData implements Serializable {
	public LegacyData() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC222277187B38344410A5FF")	
	@org.hibernate.annotations.GenericGenerator(name="VAC222277187B38344410A5FF", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({})	
	@JoinColumns({ @JoinColumn(name="conceptID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
	@Column(name="Register", nullable=true, length=255)	
	private String register;
	
	@Column(name="RegDate", nullable=true)	
	private java.util.Date regDate;
	
	@Column(name="ExpDate", nullable=true)	
	private java.util.Date expDate;
	
	@Column(name="Url", nullable=true, length=255)	
	private String url;
	
	@Column(name="Note", nullable=true, length=2048)	
	private String note;
	
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
	 * Registration number, should be unique
	 */
	public void setRegister(String value) {
		this.register = value;
	}
	
	/**
	 * Registration number, should be unique
	 */
	public String getRegister() {
		return register;
	}
	
	/**
	 * Registration date
	 */
	public void setRegDate(java.util.Date value) {
		this.regDate = value;
	}
	
	/**
	 * Registration date
	 */
	public java.util.Date getRegDate() {
		return regDate;
	}
	
	/**
	 * Expiration date
	 */
	public void setExpDate(java.util.Date value) {
		this.expDate = value;
	}
	
	/**
	 * Expiration date
	 */
	public java.util.Date getExpDate() {
		return expDate;
	}
	
	/**
	 * This URL reflect a meaning of this data, like a dictionary URL does
	 */
	public void setUrl(String value) {
		this.url = value;
	}
	
	/**
	 * This URL reflect a meaning of this data, like a dictionary URL does
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Additional notes
	 */
	public void setNote(String value) {
		this.note = value;
	}
	
	/**
	 * Additional notes
	 */
	public String getNote() {
		return note;
	}
	
	public void setConcept(org.msh.pdex2.model.r2.Concept value) {
		this.concept = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getConcept() {
		return concept;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
