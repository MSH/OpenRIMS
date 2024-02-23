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
@Table(name="dwhpages")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Dwhpages implements Serializable {
	public Dwhpages() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227718DA768E99E02BFD")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227718DA768E99E02BFD", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@org.hibernate.annotations.Index(name="byPage")	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@Column(name="MainPageID", nullable=false, length=20)	
	@org.hibernate.annotations.Index(name="byMain")	
	private long mainPageID;
	
	@Column(name="MainPageUrl", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byMain")	
	private String mainPageUrl;
	
	@Column(name="PageID", nullable=false, length=20)	
	@org.hibernate.annotations.Index(name="byPage")	
	private long pageID;
	
	@Column(name="PageUrl", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byPage")	
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
	
	/**
	 * ID of application`s main page
	 */
	public void setMainPageID(long value) {
		this.mainPageID = value;
	}
	
	/**
	 * ID of application`s main page
	 */
	public long getMainPageID() {
		return mainPageID;
	}
	
	/**
	 * URL of the main application`s page
	 */
	public void setMainPageUrl(String value) {
		this.mainPageUrl = value;
	}
	
	/**
	 * URL of the main application`s page
	 */
	public String getMainPageUrl() {
		return mainPageUrl;
	}
	
	/**
	 * ID of the related page - application form or activity
	 */
	public void setPageID(long value) {
		this.pageID = value;
	}
	
	/**
	 * ID of the related page - application form or activity
	 */
	public long getPageID() {
		return pageID;
	}
	
	/**
	 * URL of the related page -
	 */
	public void setPageUrl(String value) {
		this.pageUrl = value;
	}
	
	/**
	 * URL of the related page -
	 */
	public String getPageUrl() {
		return pageUrl;
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
