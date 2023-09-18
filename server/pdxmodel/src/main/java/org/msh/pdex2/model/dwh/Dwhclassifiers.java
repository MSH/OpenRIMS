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
@Table(name="dwhclassifiers")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Dwhclassifiers implements Serializable {
	public Dwhclassifiers() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227818A83E427EE05C6E")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227818A83E427EE05C6E", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@org.hibernate.annotations.Index(name="byMainPageID")	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@Column(name="Lang", nullable=true, length=255)	
	private String lang;
	
	@Column(name="PageID", nullable=false, length=20)	
	@org.hibernate.annotations.Index(name="byPage")	
	private long pageID;
	
	@Column(name="ClassifierVar", nullable=true, length=255)	
	private String classifierVar;
	
	@Column(name="ClassifierURL", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byClassifierUrl")	
	private String classifierURL;
	
	@Column(name="ClassifierID", nullable=false, length=20)	
	@org.hibernate.annotations.Index(name="byClassifierID")	
	private long classifierID;
	
	@Column(name="ClassifierData", nullable=true, length=255)	
	private String classifierData;
	
	@Column(name="ClassifierAuxData", nullable=true, length=255)	
	private String classifierAuxData;
	
	@Column(name="PageURL", nullable=true, length=255)	
	private String pageURL;
	
	@Column(name="MainPageID", nullable=false, length=20)	
	@org.hibernate.annotations.Index(name="byMainPageID")	
	private long mainPageID;
	
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
	 * language
	 */
	public void setLang(String value) {
		this.lang = value;
	}
	
	/**
	 * language
	 */
	public String getLang() {
		return lang;
	}
	
	/**
	 * page on which the classifier is defined
	 */
	public void setPageID(long value) {
		this.pageID = value;
	}
	
	/**
	 * page on which the classifier is defined
	 */
	public long getPageID() {
		return pageID;
	}
	
	/**
	 * URL of the page on which classifier is placed
	 */
	public void setPageURL(String value) {
		this.pageURL = value;
	}
	
	/**
	 * URL of the page on which classifier is placed
	 */
	public String getPageURL() {
		return pageURL;
	}
	
	/**
	 * Main application page - applDataID in the History
	 */
	public void setMainPageID(long value) {
		this.mainPageID = value;
	}
	
	/**
	 * Main application page - applDataID in the History
	 */
	public long getMainPageID() {
		return mainPageID;
	}
	
	/**
	 * Variable name of the classifier
	 */
	public void setClassifierVar(String value) {
		this.classifierVar = value;
	}
	
	/**
	 * Variable name of the classifier
	 */
	public String getClassifierVar() {
		return classifierVar;
	}
	
	/**
	 * unique URL of the classifier, e.g., dictionary.admin.units
	 */
	public void setClassifierURL(String value) {
		this.classifierURL = value;
	}
	
	/**
	 * unique URL of the classifier, e.g., dictionary.admin.units
	 */
	public String getClassifierURL() {
		return classifierURL;
	}
	
	/**
	 * Classifier dictionary item id
	 */
	public void setClassifierID(long value) {
		this.classifierID = value;
	}
	
	/**
	 * Classifier dictionary item id
	 */
	public long getClassifierID() {
		return classifierID;
	}
	
	/**
	 * Comma separated list all classifier's values selected, include dictionary name, e.g., Winnsbro,Franklin Parish, Louisiana,USA Addresses
	 */
	public void setClassifierData(String value) {
		this.classifierData = value;
	}
	
	/**
	 * Comma separated list all classifier's values selected, include dictionary name, e.g., Winnsbro,Franklin Parish, Louisiana,USA Addresses
	 */
	public String getClassifierData() {
		return classifierData;
	}
	
	/**
	 * Value of the Label of dictionary item concept. Current use is only for GIS in dictionary.admin.units
	 */
	public void setClassifierAuxData(String value) {
		this.classifierAuxData = value;
	}
	
	/**
	 * Value of the Label of dictionary item concept. Current use is only for GIS in dictionary.admin.units
	 */
	public String getClassifierAuxData() {
		return classifierAuxData;
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
