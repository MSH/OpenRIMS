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
 * Indexed buffer contains all pages for external report
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="pagesall")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class PagesAll implements Serializable {
	public PagesAll() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222741845D6414EC04DF4")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222741845D6414EC04DF4", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@Column(name="DataModuleID", nullable=false, length=20)	
	private long dataModuleID;
	
	@Column(name="DataModuleUrl", nullable=true, length=255)	
	private String dataModuleUrl;
	
	@Column(name="DataModuleType", nullable=true, length=255)	
	private String dataModuleType;
	
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
	 * ID of data page
	 */
	public void setDataModuleID(long value) {
		this.dataModuleID = value;
	}
	
	/**
	 * ID of data page
	 */
	public long getDataModuleID() {
		return dataModuleID;
	}
	
	/**
	 * URL of data page
	 */
	public void setDataModuleUrl(String value) {
		this.dataModuleUrl = value;
	}
	
	/**
	 * URL of data page
	 */
	public String getDataModuleUrl() {
		return dataModuleUrl;
	}
	
	public void setDataModuleType(String value) {
		this.dataModuleType = value;
	}
	
	public String getDataModuleType() {
		return dataModuleType;
	}
	
	public void setOwner(String value) {
		this.owner = value;
	}
	
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
