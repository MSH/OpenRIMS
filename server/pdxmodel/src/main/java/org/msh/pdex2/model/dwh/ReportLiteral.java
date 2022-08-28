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
 * Literals related to the any concept
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="reportliteral")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ReportLiteral implements Serializable {
	public ReportLiteral() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC222274182C63978250B654")	
	@org.hibernate.annotations.GenericGenerator(name="VAC222274182C63978250B654", strategy="native")	
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
	
	@Column(name="Variable", nullable=true, length=255)	
	private String variable;
	
	@Column(name="Lang", nullable=true, length=255)	
	private String lang;
	
	@Column(name="ValueStr", nullable=true, length=2048)	
	private String valueStr;
	
	@Column(name="PageUrl", nullable=true, length=255)	
	private String pageUrl;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public void setDataModuleID(long value) {
		this.dataModuleID = value;
	}
	
	public long getDataModuleID() {
		return dataModuleID;
	}
	
	/**
	 * URL of a page, URL of root for a root page
	 */
	public void setPageUrl(String value) {
		this.pageUrl = value;
	}
	
	/**
	 * URL of a page, URL of root for a root page
	 */
	public String getPageUrl() {
		return pageUrl;
	}
	
	/**
	 * variable name
	 */
	public void setVariable(String value) {
		this.variable = value;
	}
	
	/**
	 * variable name
	 */
	public String getVariable() {
		return variable;
	}
	
	/**
	 * The language
	 */
	public void setLang(String value) {
		this.lang = value;
	}
	
	/**
	 * The language
	 */
	public String getLang() {
		return lang;
	}
	
	/**
	 * value as a string
	 */
	public void setValueStr(String value) {
		this.valueStr = value;
	}
	
	/**
	 * value as a string
	 */
	public String getValueStr() {
		return valueStr;
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
