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
@Table(name="history", indexes={ @Index(name="byGo", columnList="Go"), @Index(name="byApplication", columnList="applicationID"), @Index(name="byCancelled", columnList="Cancelled"), @Index(name="byCome", columnList="Come") })
public class History implements Serializable {
	public History() {
	}
	
	@Column(name="ID", nullable=false, length=19)	
	@Id	
	@GeneratedValue(generator="ORG_MSH_PDEX2_MODEL_R2_HISTORY_ID_GENERATOR")	
	@org.hibernate.annotations.GenericGenerator(name="ORG_MSH_PDEX2_MODEL_R2_HISTORY_ID_GENERATOR", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns(value={ @JoinColumn(name="applicationID", referencedColumnName="ID", nullable=false) }, foreignKey=@ForeignKey(name="FKhistory608612"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.Concept application;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns(value={ @JoinColumn(name="activityID", referencedColumnName="ID") }, foreignKey=@ForeignKey(name="FKhistory387311"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.Concept activity;
	
	@Column(name="Come", nullable=true, length=19)	
	@Temporal(TemporalType.DATE)	
	private java.util.Date come;
	
	@Column(name="Go", nullable=true, length=19)	
	@Temporal(TemporalType.DATE)	
	private java.util.Date go;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns(value={ @JoinColumn(name="activityDataID", referencedColumnName="ID") }, foreignKey=@ForeignKey(name="FKhistory19325"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.Concept activityData;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns(value={ @JoinColumn(name="applDataID", referencedColumnName="ID", nullable=false) }, foreignKey=@ForeignKey(name="FKhistory877482"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.Concept applicationData;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns(value={ @JoinColumn(name="applConfigID", referencedColumnName="ID", nullable=false) }, foreignKey=@ForeignKey(name="FKhistory733279"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.Concept applConfig;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns(value={ @JoinColumn(name="applDictID", referencedColumnName="ID", nullable=false) }, foreignKey=@ForeignKey(name="FKhistory777469"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.Concept applDict;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns(value={ @JoinColumn(name="actConfigID", referencedColumnName="ID") }, foreignKey=@ForeignKey(name="FKhistory555751"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.Concept actConfig;
	
	@Column(name="DataUrl", nullable=true, length=255)	
	private String dataUrl;
	
	@Column(name="PrevNotes", nullable=true)	
	private String prevNotes;
	
	@Column(name="Cancelled", nullable=false, length=1)	
	private boolean cancelled;
	
	@Column(name="Expire", nullable=true)	
	@Temporal(TemporalType.DATE)	
	private java.util.Date expire;
	
	@Column(name="CreatedAt", nullable=false, insertable=false, updatable=false, length=19)	
	private java.sql.Timestamp createdAt;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns(value={ @JoinColumn(name="executorConceptID", referencedColumnName="ID") }, foreignKey=@ForeignKey(name="FKhistory364237"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.Concept executor;
	
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
	
	public void setDataUrl(String value) {
		this.dataUrl = value;
	}
	
	public String getDataUrl() {
		return dataUrl;
	}
	
	public void setPrevNotes(String value) {
		this.prevNotes = value;
	}
	
	public String getPrevNotes() {
		return prevNotes;
	}
	
	public void setCancelled(boolean value) {
		this.cancelled = value;
	}
	
	public boolean getCancelled() {
		return cancelled;
	}
	
	public void setExpire(java.util.Date value) {
		this.expire = value;
	}
	
	public java.util.Date getExpire() {
		return expire;
	}
	
	public java.sql.Timestamp getCreatedAt() {
		return createdAt;
	}
	
	public void setApplication(org.msh.pdex2.model.r2.Concept value) {
		this.application = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getApplication() {
		return application;
	}
	
	public void setActivity(org.msh.pdex2.model.r2.Concept value) {
		this.activity = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getActivity() {
		return activity;
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
	
	public void setApplConfig(org.msh.pdex2.model.r2.Concept value) {
		this.applConfig = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getApplConfig() {
		return applConfig;
	}
	
	public void setApplDict(org.msh.pdex2.model.r2.Concept value) {
		this.applDict = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getApplDict() {
		return applDict;
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
