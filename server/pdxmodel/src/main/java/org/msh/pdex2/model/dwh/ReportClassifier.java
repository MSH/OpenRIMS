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
	@GeneratedValue(generator="VAC22227418098A2C21B0382F")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227418098A2C21B0382F", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@Column(name="ObjectConceptId", nullable=false, length=20)	
	private long objectConceptId;
	
	@Column(name="Level", nullable=false, length=11)	
	private int level;
	
	@Column(name="ItemConceptId", nullable=false, length=20)	
	private long itemConceptId;
	
	@Column(name="DictUrl", nullable=true, length=255)	
	private String dictUrl;
	
	@Column(name="DictRootId", nullable=false, length=20)	
	private long dictRootId;
	
	@Column(name="Variable", nullable=true, length=255)	
	private String variable;
	
	@Column(name="PageUrl", nullable=true, length=255)	
	private String pageUrl;
	
	@Column(name="PrefLabel", nullable=true, length=255)	
	private String prefLabel;
	
	@Column(name="Lang", nullable=true, length=255)	
	private String lang;
	
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
	public void setObjectConceptId(long value) {
		this.objectConceptId = value;
	}
	
	/**
	 * ID of the dictionary item's concept in the database
	 */
	public long getObjectConceptId() {
		return objectConceptId;
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
	public void setItemConceptId(long value) {
		this.itemConceptId = value;
	}
	
	/**
	 * The concept of the Classifier's item
	 */
	public long getItemConceptId() {
		return itemConceptId;
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
	 * The root identifier for the dictionary
	 */
	public void setDictRootId(long value) {
		this.dictRootId = value;
	}
	
	/**
	 * The root identifier for the dictionary
	 */
	public long getDictRootId() {
		return dictRootId;
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
	 * PrefLabel value
	 */
	public void setPrefLabel(String value) {
		this.prefLabel = value;
	}
	
	/**
	 * PrefLabel value
	 */
	public String getPrefLabel() {
		return prefLabel;
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
