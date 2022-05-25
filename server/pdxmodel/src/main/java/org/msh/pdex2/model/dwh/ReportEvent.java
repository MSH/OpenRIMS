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
 * An event, related to the object
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="reportevent")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ReportEvent implements Serializable {
	public ReportEvent() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227418098A2C21F03832")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227418098A2C21F03832", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@Column(name="ObjectConceptId", nullable=false, length=20)	
	private long objectConceptId;
	
	@Column(name="Url", nullable=true, length=255)	
	private String url;
	
	@Column(name="Come", nullable=true)	
	private java.util.Date come;
	
	@Column(name="Go", nullable=true)	
	private java.util.Date go;
	
	@Column(name="First", nullable=false, length=1)	
	private boolean first;
	
	@Column(name="Last", nullable=false, length=1)	
	private boolean last;
	
	@Column(name="Appldictid", nullable=false, length=20)	
	private long appldictid;
	
	@Column(name="ActivityConfigId", nullable=false, length=20)	
	private long activityConfigId;
	
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
	 * The concept related to
	 */
	public void setObjectConceptId(long value) {
		this.objectConceptId = value;
	}
	
	/**
	 * The concept related to
	 */
	public long getObjectConceptId() {
		return objectConceptId;
	}
	
	/**
	 * The url of the event
	 */
	public void setUrl(String value) {
		this.url = value;
	}
	
	/**
	 * The url of the event
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Start of the event
	 */
	public void setCome(java.util.Date value) {
		this.come = value;
	}
	
	/**
	 * Start of the event
	 */
	public java.util.Date getCome() {
		return come;
	}
	
	/**
	 * End date of the event
	 */
	public void setGo(java.util.Date value) {
		this.go = value;
	}
	
	/**
	 * End date of the event
	 */
	public java.util.Date getGo() {
		return go;
	}
	
	/**
	 * the first event
	 */
	public void setFirst(boolean value) {
		this.first = value;
	}
	
	/**
	 * the first event
	 */
	public boolean getFirst() {
		return first;
	}
	
	/**
	 * Is this event last in the process
	 */
	public void setLast(boolean value) {
		this.last = value;
	}
	
	/**
	 * Is this event last in the process
	 */
	public boolean getLast() {
		return last;
	}
	
	/**
	 * Application concept in the guest dictionary
	 */
	public void setAppldictid(long value) {
		this.appldictid = value;
	}
	
	/**
	 * Application concept in the guest dictionary
	 */
	public long getAppldictid() {
		return appldictid;
	}
	
	/**
	 * ID of activity configuration
	 */
	public void setActivityConfigId(long value) {
		this.activityConfigId = value;
	}
	
	/**
	 * ID of activity configuration
	 */
	public long getActivityConfigId() {
		return activityConfigId;
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
