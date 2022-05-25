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
@Table(name="reportregister")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ReportRegister implements Serializable {
	public ReportRegister() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227418098A2C22103833")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227418098A2C22103833", strategy="native")	
	private long ID;
	
	@Column(name="ObjectConceptId", nullable=false, length=20)	
	private long objectConceptId;
	
	@Column(name="Url", nullable=true, length=255)	
	private String url;
	
	@Column(name="Expirable", nullable=false, length=1)	
	private boolean expirable;
	
	@Column(name="RecordNo", nullable=true, length=255)	
	private String recordNo;
	
	@Column(name="Assigned", nullable=true)	
	private java.util.Date assigned;
	
	@Column(name="Expired", nullable=true)	
	private java.util.Date expired;
	
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
	 * Concept id of the related object
	 */
	public void setObjectConceptId(long value) {
		this.objectConceptId = value;
	}
	
	/**
	 * Concept id of the related object
	 */
	public long getObjectConceptId() {
		return objectConceptId;
	}
	
	/**
	 * The url of the register
	 */
	public void setUrl(String value) {
		this.url = value;
	}
	
	/**
	 * The url of the register
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Id this record expirable?
	 */
	public void setExpirable(boolean value) {
		this.expirable = value;
	}
	
	/**
	 * Id this record expirable?
	 */
	public boolean getExpirable() {
		return expirable;
	}
	
	/**
	 * The number of the record
	 */
	public void setRecordNo(String value) {
		this.recordNo = value;
	}
	
	/**
	 * The number of the record
	 */
	public String getRecordNo() {
		return recordNo;
	}
	
	public void setAssigned(java.util.Date value) {
		this.assigned = value;
	}
	
	public java.util.Date getAssigned() {
		return assigned;
	}
	
	public void setExpired(java.util.Date value) {
		this.expired = value;
	}
	
	public java.util.Date getExpired() {
		return expired;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
