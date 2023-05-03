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
 * activities to use to calculate KPI
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="dwhactivities")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class dwhactivities implements Serializable {
	public dwhactivities() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC222277187B383444F0A60C")	
	@org.hibernate.annotations.GenericGenerator(name="VAC222277187B383444F0A60C", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@org.hibernate.annotations.Index(name="byApplication")	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@Column(name="Come", nullable=true)	
	private java.util.Date come;
	
	@Column(name="Go", nullable=true)	
	private java.util.Date go;
	
	@Column(name="Activity", nullable=true, length=255)	
	private String activity;
	
	@Column(name="Department", nullable=true, length=255)	
	private String department;
	
	@Column(name="ExecEmail", nullable=true, length=255)	
	private String execEmail;
	
	@Column(name="Lang", nullable=true, length=255)	
	private String lang;
	
	@Column(name="ApplicationID", nullable=false, length=20)	
	@org.hibernate.annotations.Index(name="byApplication")	
	private long applicationID;
	
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
	 * When activity has been started
	 */
	public void setCome(java.util.Date value) {
		this.come = value;
	}
	
	/**
	 * When activity has been started
	 */
	public java.util.Date getCome() {
		return come;
	}
	
	/**
	 * when activity has been finished, if not finished yet - now datetime
	 */
	public void setGo(java.util.Date value) {
		this.go = value;
	}
	
	/**
	 * when activity has been finished, if not finished yet - now datetime
	 */
	public java.util.Date getGo() {
		return go;
	}
	
	/**
	 * Activity name
	 */
	public void setActivity(String value) {
		this.activity = value;
	}
	
	/**
	 * Activity name
	 */
	public String getActivity() {
		return activity;
	}
	
	/**
	 * Department name
	 */
	public void setDepartment(String value) {
		this.department = value;
	}
	
	/**
	 * Department name
	 */
	public String getDepartment() {
		return department;
	}
	
	/**
	 * Activity executor's email
	 */
	public void setExecEmail(String value) {
		this.execEmail = value;
	}
	
	/**
	 * Activity executor's email
	 */
	public String getExecEmail() {
		return execEmail;
	}
	
	/**
	 * The language used
	 */
	public void setLang(String value) {
		this.lang = value;
	}
	
	/**
	 * The language used
	 */
	public String getLang() {
		return lang;
	}
	
	/**
	 * ID of an application for grouping purpose
	 */
	public void setApplicationID(long value) {
		this.applicationID = value;
	}
	
	/**
	 * ID of an application for grouping purpose
	 */
	public long getApplicationID() {
		return applicationID;
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
