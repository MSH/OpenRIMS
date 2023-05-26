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
 * Many to Many links from one report page to others. Implemented by ThingsPerson
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="reportpagelinks")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ReportPageLinks implements Serializable {
	public ReportPageLinks() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222771884EAAE29D03E62")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222771884EAAE29D03E62", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.dwh.ReportPage.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="reportpageID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportPage dataPage;
	
	@Column(name="DataModuleUrl", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byDataModuleUrl")	
	private String dataModuleUrl;
	
	@Column(name="DataModuleID", nullable=false, length=20)	
	@org.hibernate.annotations.Index(name="byDatamoduleID")	
	private long dataModuleID;
	
	@Column(name="DataPrefLabel", nullable=true)	
	private String dataPrefLabel;
	
	@Column(name="Varname", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byVarName")	
	private String varname;
	
	@Column(name="LinkedDataModuleUrl", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byLinkedDataModuleUrl")	
	private String linkedDataModuleUrl;
	
	@Column(name="LinkedDataModuleID", nullable=false, length=20)	
	@org.hibernate.annotations.Index(name="byLinkedDataModuleID")	
	private long linkedDataModuleID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.dwh.ReportPage.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="linkedPageID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportPage linkedPage;
	
	@Column(name="LinkedPrefLabel", nullable=true)	
	private String linkedPrefLabel;
	
	@Column(name="DictUrl", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byDictUrl")	
	private String dictUrl;
	
	@Column(name="DictPrefLabel", nullable=true)	
	private String dictPrefLabel;
	
	@Column(name="DictItem", nullable=true)	
	private String dictItem;
	
	@Column(name="DictPath", nullable=true)	
	private String dictPath;
	
	@Column(name="Lang", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byLang")	
	private String lang;
	
	@Column(name="State", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byState")	
	private String state;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public void setDataModuleUrl(String value) {
		this.dataModuleUrl = value;
	}
	
	public String getDataModuleUrl() {
		return dataModuleUrl;
	}
	
	public void setDataModuleID(long value) {
		this.dataModuleID = value;
	}
	
	public long getDataModuleID() {
		return dataModuleID;
	}
	
	public void setDataPrefLabel(String value) {
		this.dataPrefLabel = value;
	}
	
	public String getDataPrefLabel() {
		return dataPrefLabel;
	}
	
	public void setVarname(String value) {
		this.varname = value;
	}
	
	public String getVarname() {
		return varname;
	}
	
	public void setLinkedDataModuleUrl(String value) {
		this.linkedDataModuleUrl = value;
	}
	
	public String getLinkedDataModuleUrl() {
		return linkedDataModuleUrl;
	}
	
	public void setLinkedDataModuleID(long value) {
		this.linkedDataModuleID = value;
	}
	
	public long getLinkedDataModuleID() {
		return linkedDataModuleID;
	}
	
	public void setLinkedPrefLabel(String value) {
		this.linkedPrefLabel = value;
	}
	
	public String getLinkedPrefLabel() {
		return linkedPrefLabel;
	}
	
	public void setDictUrl(String value) {
		this.dictUrl = value;
	}
	
	public String getDictUrl() {
		return dictUrl;
	}
	
	public void setDictPrefLabel(String value) {
		this.dictPrefLabel = value;
	}
	
	public String getDictPrefLabel() {
		return dictPrefLabel;
	}
	
	public void setDictItem(String value) {
		this.dictItem = value;
	}
	
	public String getDictItem() {
		return dictItem;
	}
	
	public void setDictPath(String value) {
		this.dictPath = value;
	}
	
	public String getDictPath() {
		return dictPath;
	}
	
	public void setLang(String value) {
		this.lang = value;
	}
	
	public String getLang() {
		return lang;
	}
	
	public void setState(String value) {
		this.state = value;
	}
	
	public String getState() {
		return state;
	}
	
	public void setDataPage(org.msh.pdex2.model.dwh.ReportPage value) {
		this.dataPage = value;
	}
	
	public org.msh.pdex2.model.dwh.ReportPage getDataPage() {
		return dataPage;
	}
	
	public void setLinkedPage(org.msh.pdex2.model.dwh.ReportPage value) {
		this.linkedPage = value;
	}
	
	public org.msh.pdex2.model.dwh.ReportPage getLinkedPage() {
		return linkedPage;
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
