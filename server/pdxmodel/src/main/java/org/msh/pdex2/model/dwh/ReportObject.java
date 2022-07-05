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
/**
 * contains all data objects managed by Pharmadex 2. Their configuration can be found in the “Data Configuration” feature. Most of the data objects are applications, persons, warehouses, etc
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="reportobject")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ReportObject implements Serializable {
	public ReportObject() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227418098A2C2190382E")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227418098A2C2190382E", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@Column(name="ObjectConceptID", nullable=true, length=20)	
	private long objectConceptID;
	
	@Column(name="Url", nullable=true, length=255)	
	private String url;
	
	@Column(name="Email", nullable=true, length=255)	
	private String email;
	
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
	 * Identifier of the concept in the main database
	 */
	public void setObjectConceptID(long value) {
		this.objectConceptID = value;
	}
	
	/**
	 * Identifier of the concept in the main database
	 */
	public long getObjectConceptID() {
		return objectConceptID;
	}
	
	/**
	 * url of the object
	 */
	public void setUrl(String value) {
		this.url = value;
	}
	
	/**
	 * url of the object
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Author of the object
	 */
	public void setEmail(String value) {
		this.email = value;
	}
	
	/**
	 * Author of the object
	 */
	public String getEmail() {
		return email;
	}
	
	public void setReportSession(org.msh.pdex2.model.dwh.ReportSession value) {
		this.reportSession = value;
	}
	
	public org.msh.pdex2.model.dwh.ReportSession getReportSession() {
		return reportSession;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
