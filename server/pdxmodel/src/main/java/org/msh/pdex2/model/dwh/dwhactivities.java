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
	@GeneratedValue(generator="VAC2222771884EAAE2A103E66")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222771884EAAE2A103E66", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
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
	
	@Column(name="ActivityUrl", nullable=true, length=255)	
	private String activityUrl;
	
	@Column(name="ActivityID", nullable=false, length=20)	
	private long activityID;
	
	@Column(name="ActivityConfigID", nullable=false, length=20)	
	private long activityConfigID;
	
	@Column(name="DepartmentID", nullable=false, length=20)	
	private long departmentID;
	
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
	
	public void setActivityConfigID(long value) {
		this.activityConfigID = value;
	}
	
	public long getActivityConfigID() {
		return activityConfigID;
	}
	
	/**
	 * Activity URL from the activity config
	 */
	public void setActivityUrl(String value) {
		this.activityUrl = value;
	}
	
	/**
	 * Activity URL from the activity config
	 */
	public String getActivityUrl() {
		return activityUrl;
	}
	
	public void setActivityID(long value) {
		this.activityID = value;
	}
	
	public long getActivityID() {
		return activityID;
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
	
	public void setDepartmentID(long value) {
		this.departmentID = value;
	}
	
	public long getDepartmentID() {
		return departmentID;
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
