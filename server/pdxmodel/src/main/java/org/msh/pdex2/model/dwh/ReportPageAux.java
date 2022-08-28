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
@Table(name="reportpageaux")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ReportPageAux implements Serializable {
	public ReportPageAux() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC222274182C639782A0B658")	
	@org.hibernate.annotations.GenericGenerator(name="VAC222274182C639782A0B658", strategy="native")	
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
	
	@Column(name="DataModuleId", nullable=false, length=20)	
	private long dataModuleId;
	
	@Column(name="AuxModuleId", nullable=false, length=20)	
	private long auxModuleId;
	
	@Column(name="AuxModuleUrl", nullable=true, length=255)	
	private String auxModuleUrl;
	
	@Column(name="Lang", nullable=true, length=255)	
	private String lang;
	
	@Column(name="PrefLabel", nullable=true, length=255)	
	private String prefLabel;
	
	@Column(name="AuxPageUrl", nullable=true, length=255)	
	private String auxPageUrl;
	
	@Column(name="AuxPageVar", nullable=true, length=255)	
	private String auxPageVar;
	
	@Column(name="AuxPageId", nullable=false, length=20)	
	private long auxPageId;
	
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
	 * ID of parent page for aux pages
	 */
	public void setDataModuleId(long value) {
		this.dataModuleId = value;
	}
	
	/**
	 * ID of parent page for aux pages
	 */
	public long getDataModuleId() {
		return dataModuleId;
	}
	
	/**
	 * ID of an aux page linked to the parent
	 */
	public void setAuxModuleId(long value) {
		this.auxModuleId = value;
	}
	
	/**
	 * ID of an aux page linked to the parent
	 */
	public long getAuxModuleId() {
		return auxModuleId;
	}
	
	/**
	 * URL of aux page
	 */
	public void setAuxModuleUrl(String value) {
		this.auxModuleUrl = value;
	}
	
	/**
	 * URL of aux page
	 */
	public String getAuxModuleUrl() {
		return auxModuleUrl;
	}
	
	/**
	 * Language of the aux page prefLabel
	 */
	public void setLang(String value) {
		this.lang = value;
	}
	
	/**
	 * Language of the aux page prefLabel
	 */
	public String getLang() {
		return lang;
	}
	
	/**
	 * prefLabel of the aux page
	 */
	public void setPrefLabel(String value) {
		this.prefLabel = value;
	}
	
	/**
	 * prefLabel of the aux page
	 */
	public String getPrefLabel() {
		return prefLabel;
	}
	
	/**
	 * email of the parent and aux pages creator
	 */
	public void setAuxPageUrl(String value) {
		this.auxPageUrl = value;
	}
	
	/**
	 * email of the parent and aux pages creator
	 */
	public String getAuxPageUrl() {
		return auxPageUrl;
	}
	
	public void setAuxPageVar(String value) {
		this.auxPageVar = value;
	}
	
	public String getAuxPageVar() {
		return auxPageVar;
	}
	
	public void setAuxPageId(long value) {
		this.auxPageId = value;
	}
	
	public long getAuxPageId() {
		return auxPageId;
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
