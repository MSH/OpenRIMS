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
 * On-screen forms and their pages
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="reportpage")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ReportPage implements Serializable {
	public ReportPage() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222741823EA5BF1B08ACC")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222741823EA5BF1B08ACC", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@Column(name="DataModuleId", nullable=false, length=20)	
	private long dataModuleId;
	
	@Column(name="DataModuleUrl", nullable=true, length=255)	
	private String dataModuleUrl;
	
	@Column(name="Lang", nullable=true, length=255)	
	private String lang;
	
	@Column(name="PrefLabel", nullable=true, length=255)	
	private String prefLabel;
	
	@Column(name="PageId", nullable=false, length=20)	
	private long pageId;
	
	@Column(name="PageUrl", nullable=true, length=255)	
	private String pageUrl;
	
	@Column(name="PageVar", nullable=true, length=255)	
	private String pageVar;
	
	@Column(name="Owner", nullable=true, length=255)	
	private String owner;
	
	@Column(name="State", nullable=true, length=255)	
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
	
	/**
	 * Id of root (first) page in the form
	 */
	public void setDataModuleId(long value) {
		this.dataModuleId = value;
	}
	
	/**
	 * Id of root (first) page in the form
	 */
	public long getDataModuleId() {
		return dataModuleId;
	}
	
	/**
	 * URL of the root page
	 */
	public void setDataModuleUrl(String value) {
		this.dataModuleUrl = value;
	}
	
	/**
	 * URL of the root page
	 */
	public String getDataModuleUrl() {
		return dataModuleUrl;
	}
	
	/**
	 * Language of the prefLabel on the root page
	 */
	public void setLang(String value) {
		this.lang = value;
	}
	
	/**
	 * Language of the prefLabel on the root page
	 */
	public String getLang() {
		return lang;
	}
	
	/**
	 * PrefLabel on the root page. If no - sorry...
	 */
	public void setPrefLabel(String value) {
		this.prefLabel = value;
	}
	
	/**
	 * PrefLabel on the root page. If no - sorry...
	 */
	public String getPrefLabel() {
		return prefLabel;
	}
	
	/**
	 * ID of a page on the form
	 */
	public void setPageId(long value) {
		this.pageId = value;
	}
	
	/**
	 * ID of a page on the form
	 */
	public long getPageId() {
		return pageId;
	}
	
	/**
	 * URL of a page on the form
	 */
	public void setPageUrl(String value) {
		this.pageUrl = value;
	}
	
	/**
	 * URL of a page on the form
	 */
	public String getPageUrl() {
		return pageUrl;
	}
	
	/**
	 * Variable name for a page on the form
	 */
	public void setPageVar(String value) {
		this.pageVar = value;
	}
	
	/**
	 * Variable name for a page on the form
	 */
	public String getPageVar() {
		return pageVar;
	}
	
	/**
	 * Owner's (creator's) email
	 */
	public void setOwner(String value) {
		this.owner = value;
	}
	
	/**
	 * Owner's (creator's) email
	 */
	public String getOwner() {
		return owner;
	}
	
	/**
	 * This data module is dereregistered of in process of registration
	 */
	public void setState(String value) {
		this.state = value;
	}
	
	/**
	 * This data module is dereregistered of in process of registration
	 */
	public String getState() {
		return state;
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
