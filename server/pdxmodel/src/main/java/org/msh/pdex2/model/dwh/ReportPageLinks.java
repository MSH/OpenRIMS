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
	@GeneratedValue(generator="VAC2222741836B156F980E3AC")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222741836B156F980E3AC", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.dwh.ReportPage.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="dataPageID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportPage dataPage;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.dwh.ReportPage.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="linkedPageID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportPage linkedPage;
	
	@Column(name="DictUrl", nullable=true, length=255)	
	private String dictUrl;
	
	@Column(name="DictPrefLabel", nullable=true, length=255)	
	private String dictPrefLabel;
	
	@Column(name="DictLevel", nullable=false, length=11)	
	private int dictLevel = 0;
	
	@Column(name="DictItem", nullable=true, length=255)	
	private String dictItem;
	
	@Column(name="DictPath", nullable=true, length=255)	
	private String dictPath;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
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
	
	public void setDictLevel(int value) {
		this.dictLevel = value;
	}
	
	public int getDictLevel() {
		return dictLevel;
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
