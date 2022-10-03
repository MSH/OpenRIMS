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
@Table(name="reportclassifier")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ReportClassifier implements Serializable {
	public ReportClassifier() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222741838448926707376")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222741838448926707376", strategy="native")	
	private long ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.dwh.ReportPage.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="reportPageID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportPage reportPage;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@Column(name="DataModuleID", nullable=false, length=20)	
	private long dataModuleID;
	
	@Column(name="DictUrl", nullable=true, length=255)	
	private String dictUrl;
	
	@Column(name="Level", nullable=false, length=11)	
	private int level;
	
	@Column(name="ItemId", nullable=false, length=20)	
	private long itemId;
	
	@Column(name="PageUrl", nullable=true, length=255)	
	private String pageUrl;
	
	@Column(name="Variable", nullable=true, length=255)	
	private String variable;
	
	@Column(name="Lang", nullable=true, length=255)	
	private String lang;
	
	@Column(name="DictPrefLabel", nullable=true, length=255)	
	private String dictPrefLabel;
	
	@Column(name="ItemPrefLabel", nullable=true, length=255)	
	private String itemPrefLabel;
	
	@Column(name="ItemAltLabel", nullable=true, length=255)	
	private String itemAltLabel;
	
	@Column(name="PathToItem", nullable=true, length=1024)	
	private String pathToItem;
	
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
	 * ID of the dictionary item's concept in the database
	 */
	public void setDataModuleID(long value) {
		this.dataModuleID = value;
	}
	
	/**
	 * ID of the dictionary item's concept in the database
	 */
	public long getDataModuleID() {
		return dataModuleID;
	}
	
	/**
	 * URL of the dictionary
	 */
	public void setDictUrl(String value) {
		this.dictUrl = value;
	}
	
	/**
	 * URL of the dictionary
	 */
	public String getDictUrl() {
		return dictUrl;
	}
	
	/**
	 * The level of the classifier. From Zero. The Zero points to the selected classifier in the hierarchy
	 */
	public void setLevel(int value) {
		this.level = value;
	}
	
	/**
	 * The level of the classifier. From Zero. The Zero points to the selected classifier in the hierarchy
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * The concept of the Classifier's item
	 */
	public void setItemId(long value) {
		this.itemId = value;
	}
	
	/**
	 * The concept of the Classifier's item
	 */
	public long getItemId() {
		return itemId;
	}
	
	/**
	 * URL of thing on which this classifier placed. NULL for root
	 */
	public void setPageUrl(String value) {
		this.pageUrl = value;
	}
	
	/**
	 * URL of thing on which this classifier placed. NULL for root
	 */
	public String getPageUrl() {
		return pageUrl;
	}
	
	/**
	 * Variable name of the classifier in the electronic form
	 */
	public void setVariable(String value) {
		this.variable = value;
	}
	
	/**
	 * Variable name of the classifier in the electronic form
	 */
	public String getVariable() {
		return variable;
	}
	
	/**
	 * Language value
	 */
	public void setLang(String value) {
		this.lang = value;
	}
	
	/**
	 * Language value
	 */
	public String getLang() {
		return lang;
	}
	
	/**
	 * The name of the classifier
	 */
	public void setDictPrefLabel(String value) {
		this.dictPrefLabel = value;
	}
	
	/**
	 * The name of the classifier
	 */
	public String getDictPrefLabel() {
		return dictPrefLabel;
	}
	
	/**
	 * PrefLabel value
	 */
	public void setItemPrefLabel(String value) {
		this.itemPrefLabel = value;
	}
	
	/**
	 * PrefLabel value
	 */
	public String getItemPrefLabel() {
		return itemPrefLabel;
	}
	
	/**
	 * Alternative label for an item. Depends on a particular dictionary. For dictionary.admin.unit it is GIS coordinates - latitude;longitude
	 */
	public void setItemAltLabel(String value) {
		this.itemAltLabel = value;
	}
	
	/**
	 * Alternative label for an item. Depends on a particular dictionary. For dictionary.admin.unit it is GIS coordinates - latitude;longitude
	 */
	public String getItemAltLabel() {
		return itemAltLabel;
	}
	
	/**
	 * The path to this choice
	 */
	public void setPathToItem(String value) {
		this.pathToItem = value;
	}
	
	/**
	 * The path to this choice
	 */
	public String getPathToItem() {
		return pathToItem;
	}
	
	public void setReportSession(org.msh.pdex2.model.dwh.ReportSession value) {
		this.reportSession = value;
	}
	
	public org.msh.pdex2.model.dwh.ReportSession getReportSession() {
		return reportSession;
	}
	
	public void setReportPage(org.msh.pdex2.model.dwh.ReportPage value) {
		this.reportPage = value;
	}
	
	public org.msh.pdex2.model.dwh.ReportPage getReportPage() {
		return reportPage;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
