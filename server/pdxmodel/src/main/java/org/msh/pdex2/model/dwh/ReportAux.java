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
@Table(name="reportaux")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ReportAux implements Serializable {
	public ReportAux() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227418129ECB5900CECB")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227418129ECB5900CECB", strategy="native")	
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
	
	@Column(name="AuxId", nullable=false, length=20)	
	private long auxId;
	
	@Column(name="AuxUrl", nullable=true, length=255)	
	private String auxUrl;
	
	@Column(name="Lang", nullable=true, length=255)	
	private String lang;
	
	@Column(name="PrefLabel", nullable=true, length=255)	
	private String prefLabel;
	
	@Column(name="Owner", nullable=true, length=255)	
	private String owner;
	
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
	 * URL of the parent page
	 */
	public void setDataModuleUrl(String value) {
		this.dataModuleUrl = value;
	}
	
	/**
	 * URL of the parent page
	 */
	public String getDataModuleUrl() {
		return dataModuleUrl;
	}
	
	/**
	 * ID of an aux page linked to the parent
	 */
	public void setAuxId(long value) {
		this.auxId = value;
	}
	
	/**
	 * ID of an aux page linked to the parent
	 */
	public long getAuxId() {
		return auxId;
	}
	
	/**
	 * URL of aux page
	 */
	public void setAuxUrl(String value) {
		this.auxUrl = value;
	}
	
	/**
	 * URL of aux page
	 */
	public String getAuxUrl() {
		return auxUrl;
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
	public void setOwner(String value) {
		this.owner = value;
	}
	
	/**
	 * email of the parent and aux pages creator
	 */
	public String getOwner() {
		return owner;
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
