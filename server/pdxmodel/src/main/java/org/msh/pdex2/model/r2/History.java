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
 * Responsible for relation inside the application
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="history")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class History implements Serializable {
	public History() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222721892C64533500469")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222721892C64533500469", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="executorConceptID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept executor;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns({ @JoinColumn(name="actConfigID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept actConfig;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns({ @JoinColumn(name="applConfigID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept applConfig;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns({ @JoinColumn(name="applDictID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept applDict;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="applDataID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept applicationData;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="activityDataID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept activityData;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="applicationID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept application;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="activityID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept activity;
	
	@Column(name="Come", nullable=true)	
	@Basic	
	@Temporal(value=TemporalType.TIMESTAMP)	
	private java.util.Date come;
	
	@Column(name="Go", nullable=true)	
	private java.util.Date go;
	
	@Column(name="DataUrl", nullable=true, length=255)	
	private String dataUrl;
	
	@Column(name="PrevNotes", nullable=true)	
	private String prevNotes;
	
	@Column(name="Cancelled", nullable=false, length=1)	
	private boolean cancelled = false;
	
	@Column(name="Expire", nullable=true)	
	private java.util.Date expire;
	
	@Column(name="CreatedAt", nullable=true)	
	private java.util.Date createdAt;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public void setCome(java.util.Date value) {
		this.come = value;
	}
	
	public java.util.Date getCome() {
		return come;
	}
	
	public void setGo(java.util.Date value) {
		this.go = value;
	}
	
	public java.util.Date getGo() {
		return go;
	}
	
	/**
	 * Expiration date. Mainly for monitoring activity
	 */
	public void setExpire(java.util.Date value) {
		this.expire = value;
	}
	
	/**
	 * Expiration date. Mainly for monitoring activity
	 */
	public java.util.Date getExpire() {
		return expire;
	}
	
	/**
	 * If activity require an additional data. It is a url of this data configuration
	 */
	public void setDataUrl(String value) {
		this.dataUrl = value;
	}
	
	/**
	 * If activity require an additional data. It is a url of this data configuration
	 */
	public String getDataUrl() {
		return dataUrl;
	}
	
	/**
	 * Notes from the previous step
	 */
	public void setPrevNotes(String value) {
		this.prevNotes = value;
	}
	
	/**
	 * Notes from the previous step
	 */
	public String getPrevNotes() {
		return prevNotes;
	}
	
	/**
	 * Is this activity has been cancelled
	 */
	public void setCancelled(boolean value) {
		this.cancelled = value;
	}
	
	/**
	 * Is this activity has been cancelled
	 */
	public boolean getCancelled() {
		return cancelled;
	}
	
	/**
	 * Real date to create this record
	 */
	public void setCreatedAt(java.util.Date value) {
		this.createdAt = value;
	}
	
	/**
	 * Real date to create this record
	 */
	public java.util.Date getCreatedAt() {
		return createdAt;
	}
	
	public void setActivity(org.msh.pdex2.model.r2.Concept value) {
		this.activity = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getActivity() {
		return activity;
	}
	
	public void setApplication(org.msh.pdex2.model.r2.Concept value) {
		this.application = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getApplication() {
		return application;
	}
	
	public void setActivityData(org.msh.pdex2.model.r2.Concept value) {
		this.activityData = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getActivityData() {
		return activityData;
	}
	
	public void setApplicationData(org.msh.pdex2.model.r2.Concept value) {
		this.applicationData = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getApplicationData() {
		return applicationData;
	}
	
	public void setApplDict(org.msh.pdex2.model.r2.Concept value) {
		this.applDict = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getApplDict() {
		return applDict;
	}
	
	public void setApplConfig(org.msh.pdex2.model.r2.Concept value) {
		this.applConfig = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getApplConfig() {
		return applConfig;
	}
	
	public void setActConfig(org.msh.pdex2.model.r2.Concept value) {
		this.actConfig = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getActConfig() {
		return actConfig;
	}
	
	public void setExecutor(org.msh.pdex2.model.r2.Concept value) {
		this.executor = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getExecutor() {
		return executor;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
