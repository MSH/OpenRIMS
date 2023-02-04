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
 * To keep binary data and technical metadata for files stored.
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="fileresource")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class FileResource implements Serializable {
	public FileResource() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222741860911A15608607")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222741860911A15608607", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="activityDataID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept activityData;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="dictconceptID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept classifier;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns({ @JoinColumn(name="conceptID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
	@Column(name="FileSize", nullable=false, length=20)	
	private long fileSize;
	
	@Column(name="File", nullable=false)	
	private byte[] file;
	
	@Column(name="ChangedAt", nullable=true)	
	private java.util.Date changedAt;
	
	@Column(name="Mediatype", nullable=true, length=255)	
	private String mediatype;
	
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
	 * File size in bytes
	 */
	public void setFileSize(long value) {
		this.fileSize = value;
	}
	
	/**
	 * File size in bytes
	 */
	public long getFileSize() {
		return fileSize;
	}
	
	public void setMediatype(String value) {
		this.mediatype = value;
	}
	
	public String getMediatype() {
		return mediatype;
	}
	
	/**
	 * Binary representation of a file
	 */
	public void setFile(byte[] value) {
		this.file = value;
	}
	
	/**
	 * Binary representation of a file
	 */
	public byte[] getFile() {
		return file;
	}
	
	public void setChangedAt(java.util.Date value) {
		this.changedAt = value;
	}
	
	public java.util.Date getChangedAt() {
		return changedAt;
	}
	
	public void setConcept(org.msh.pdex2.model.r2.Concept value) {
		this.concept = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getConcept() {
		return concept;
	}
	
	public void setClassifier(org.msh.pdex2.model.r2.Concept value) {
		this.classifier = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getClassifier() {
		return classifier;
	}
	
	public void setActivityData(org.msh.pdex2.model.r2.Concept value) {
		this.activityData = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getActivityData() {
		return activityData;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
