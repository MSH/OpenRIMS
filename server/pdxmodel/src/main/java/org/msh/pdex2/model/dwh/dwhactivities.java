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
public class Dwhactivities implements Serializable {
	public Dwhactivities() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227718DA768E99902BF8")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227718DA768E99902BF8", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@org.hibernate.annotations.Index(name="byExecutor")	
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
	@org.hibernate.annotations.Index(name="byExecutor")	
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
	
	@Column(name="GroupUrl", nullable=true, length=255)	
	private String groupUrl;
	
	@Column(name="GroupName", nullable=true, length=255)	
	private String groupName;
	
	@Column(name="ApplicationUrl", nullable=true, length=255)	
	private String applicationUrl;
	
	@Column(name="ApplicationName", nullable=true, length=255)	
	private String applicationName;
	
	@Column(name="Outcome", nullable=true, length=255)	
	private String outcome;
	
	@Column(name="ExecFullName", nullable=true, length=255)	
	private String execFullName;
	
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
	 * URL of application dictionary, e.g. dictionary.host.applications
	 */
	public void setGroupUrl(String value) {
		this.groupUrl = value;
	}
	
	/**
	 * URL of application dictionary, e.g. dictionary.host.applications
	 */
	public String getGroupUrl() {
		return groupUrl;
	}
	
	/**
	 * Name of the application dictionary, e.g., Host applications
	 */
	public void setGroupName(String value) {
		this.groupName = value;
	}
	
	/**
	 * Name of the application dictionary, e.g., Host applications
	 */
	public String getGroupName() {
		return groupName;
	}
	
	/**
	 * Application URL, e.g., application.pharmacy.inspection
	 */
	public void setApplicationUrl(String value) {
		this.applicationUrl = value;
	}
	
	/**
	 * Application URL, e.g., application.pharmacy.inspection
	 */
	public String getApplicationUrl() {
		return applicationUrl;
	}
	
	/**
	 * Application name, e.g. Pharmacy Inspection
	 */
	public void setApplicationName(String value) {
		this.applicationName = value;
	}
	
	/**
	 * Application name, e.g. Pharmacy Inspection
	 */
	public String getApplicationName() {
		return applicationName;
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
	 * Activity outcome code
	 */
	public void setOutcome(String value) {
		this.outcome = value;
	}
	
	/**
	 * Activity outcome code
	 */
	public String getOutcome() {
		return outcome;
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
	 * Executor's full name
	 */
	public void setExecFullName(String value) {
		this.execFullName = value;
	}
	
	/**
	 * Executor's full name
	 */
	public String getExecFullName() {
		return execFullName;
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
