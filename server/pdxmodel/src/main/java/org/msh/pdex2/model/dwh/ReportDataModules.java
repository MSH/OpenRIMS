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
 * Data module is guest or detailed data pages.
 * The guest data pages are main application page + auxilary application pages + pages collected in host and shutdown processes
 * The detailed data pages are main page + additional page of data related to the application data as 1:m using thingperson table
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="reportdatamodules")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ReportDataModules implements Serializable {
	public ReportDataModules() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227718DA768E99C02BFB")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227718DA768E99C02BFB", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@org.hibernate.annotations.Index(name="byPage")	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@Column(name="MainPageId", nullable=false, length=20)	
	@org.hibernate.annotations.Index(name="byMain")	
	private long mainPageId;
	
	@Column(name="PageId", nullable=false, length=20)	
	@org.hibernate.annotations.Index(name="byPage")	
	private long pageId;
	
	@Column(name="MainPageUrl", nullable=true, length=255)	
	private String mainPageUrl;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public void setMainPageId(long value) {
		this.mainPageId = value;
	}
	
	public long getMainPageId() {
		return mainPageId;
	}
	
	public void setMainPageUrl(String value) {
		this.mainPageUrl = value;
	}
	
	public String getMainPageUrl() {
		return mainPageUrl;
	}
	
	public void setPageId(long value) {
		this.pageId = value;
	}
	
	public long getPageId() {
		return pageId;
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
