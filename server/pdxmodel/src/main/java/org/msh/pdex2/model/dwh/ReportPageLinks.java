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
	@GeneratedValue(generator="VAC222274183363D22630E2DD")	
	@org.hibernate.annotations.GenericGenerator(name="VAC222274183363D22630E2DD", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.dwh.ReportPage.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="reportpageAuxID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportPage reportPageAux;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.dwh.ReportPage.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="reportpageID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportPage reportPage;
	
	@Column(name="DataModuleId", nullable=false, length=20)	
	private long dataModuleId;
	
	@Column(name="DataModuleUrl", nullable=true, length=255)	
	private String dataModuleUrl;
	
	@Column(name="ReportPageUrl", nullable=true, length=255)	
	private String reportPageUrl;
	
	@Column(name="Lang", nullable=true, length=255)	
	private String Lang;
	
	@Column(name="AuxModuleUrl", nullable=true, length=255)	
	private String auxModuleUrl;
	
	@Column(name="AuxPageUrl", nullable=true, length=255)	
	private String auxPageUrl;
	
	@Column(name="PrefLabel", nullable=true, length=255)	
	private String prefLabel;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public void setDataModuleId(long value) {
		this.dataModuleId = value;
	}
	
	public long getDataModuleId() {
		return dataModuleId;
	}
	
	public void setDataModuleUrl(String value) {
		this.dataModuleUrl = value;
	}
	
	public String getDataModuleUrl() {
		return dataModuleUrl;
	}
	
	public void setReportPageUrl(String value) {
		this.reportPageUrl = value;
	}
	
	public String getReportPageUrl() {
		return reportPageUrl;
	}
	
	public void setLang(String value) {
		this.Lang = value;
	}
	
	public String getLang() {
		return Lang;
	}
	
	public void setAuxModuleUrl(String value) {
		this.auxModuleUrl = value;
	}
	
	public String getAuxModuleUrl() {
		return auxModuleUrl;
	}
	
	public void setAuxPageUrl(String value) {
		this.auxPageUrl = value;
	}
	
	public String getAuxPageUrl() {
		return auxPageUrl;
	}
	
	public void setPrefLabel(String value) {
		this.prefLabel = value;
	}
	
	public String getPrefLabel() {
		return prefLabel;
	}
	
	public void setReportPage(org.msh.pdex2.model.dwh.ReportPage value) {
		this.reportPage = value;
	}
	
	public org.msh.pdex2.model.dwh.ReportPage getReportPage() {
		return reportPage;
	}
	
	public void setReportPageAux(org.msh.pdex2.model.dwh.ReportPage value) {
		this.reportPageAux = value;
	}
	
	public org.msh.pdex2.model.dwh.ReportPage getReportPageAux() {
		return reportPageAux;
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
