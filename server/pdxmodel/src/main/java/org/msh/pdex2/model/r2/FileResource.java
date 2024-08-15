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
@Table(name="fileresource")
public class FileResource implements Serializable {
	public FileResource() {
	}
	
	@Column(name="ID", nullable=false, length=19)	
	@Id	
	@GeneratedValue(generator="ORG_MSH_PDEX2_MODEL_R2_FILERESOURCE_ID_GENERATOR")	
	@org.hibernate.annotations.GenericGenerator(name="ORG_MSH_PDEX2_MODEL_R2_FILERESOURCE_ID_GENERATOR", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns(value={ @JoinColumn(name="conceptID", referencedColumnName="ID", nullable=false) }, foreignKey=@ForeignKey(name="FKfileresour477120"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
	@Column(name="FileSize", nullable=false, length=19)	
	private long fileSize;
	
	@Column(name="`File`", nullable=false)	
	private byte[] file;
	
	@Column(name="ChangedAt", nullable=false, insertable=false, updatable=false, length=19)	
	private java.sql.Timestamp changedAt;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns(value={ @JoinColumn(name="dictconceptID", referencedColumnName="ID", nullable=false) }, foreignKey=@ForeignKey(name="FKfileresour491212"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.Concept classifier;
	
	@Column(name="Mediatype", nullable=true, length=255)	
	private String mediatype;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class, fetch=FetchType.LAZY)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns(value={ @JoinColumn(name="activityDataID", referencedColumnName="ID") }, foreignKey=@ForeignKey(name="FKfileresour258199"))	
	@org.hibernate.annotations.LazyToOne(value=org.hibernate.annotations.LazyToOneOption.NO_PROXY)	
	private org.msh.pdex2.model.r2.Concept activityData;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public void setFileSize(long value) {
		this.fileSize = value;
	}
	
	public long getFileSize() {
		return fileSize;
	}
	
	public void setFile(byte[] value) {
		this.file = value;
	}
	
	public byte[] getFile() {
		return file;
	}
	
	public java.sql.Timestamp getChangedAt() {
		return changedAt;
	}
	
	public void setMediatype(String value) {
		this.mediatype = value;
	}
	
	public String getMediatype() {
		return mediatype;
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
