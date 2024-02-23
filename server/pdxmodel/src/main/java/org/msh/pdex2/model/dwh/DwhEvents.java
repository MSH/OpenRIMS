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
 * Currently only registers and schedulers
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="dwhevents")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class DwhEvents implements Serializable {
	public DwhEvents() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227718DA768E99F02BFF")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227718DA768E99F02BFF", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@org.hibernate.annotations.Index(name="byEventURL")	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@Column(name="PageID", nullable=false, length=20)	
	@org.hibernate.annotations.Index(name="byPage")	
	private long pageID;
	
	@Column(name="PageURL", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byPageURL")	
	private String pageURL;
	
	@Column(name="MainPageID", nullable=false, length=20)	
	@org.hibernate.annotations.Index(name="byMainPage")	
	private long mainPageID;
	
	@Column(name="MainPageURL", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byMainPageURL")	
	private String mainPageURL;
	
	@Column(name="EventVar", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byEventVar")	
	private String eventVar;
	
	@Column(name="EventURL", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byEventURL")	
	private String eventURL;
	
	@Column(name="EventDate", nullable=true)	
	private java.util.Date eventDate;
	
	@Column(name="EventNumber", nullable=true, length=255)	
	private String eventNumber;
	
	@Column(name="NextEventDate", nullable=true)	
	private java.util.Date nextEventDate;
	
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
	 * ID of a page on which event is defined
	 */
	public void setPageID(long value) {
		this.pageID = value;
	}
	
	/**
	 * ID of a page on which event is defined
	 */
	public long getPageID() {
		return pageID;
	}
	
	/**
	 * URL of a page on which the event is defined
	 */
	public void setPageURL(String value) {
		this.pageURL = value;
	}
	
	/**
	 * URL of a page on which the event is defined
	 */
	public String getPageURL() {
		return pageURL;
	}
	
	/**
	 * ID of main application page
	 */
	public void setMainPageID(long value) {
		this.mainPageID = value;
	}
	
	/**
	 * ID of main application page
	 */
	public long getMainPageID() {
		return mainPageID;
	}
	
	/**
	 * URL of the main application page
	 */
	public void setMainPageURL(String value) {
		this.mainPageURL = value;
	}
	
	/**
	 * URL of the main application page
	 */
	public String getMainPageURL() {
		return mainPageURL;
	}
	
	/**
	 * Variable name of the event component on the page
	 */
	public void setEventVar(String value) {
		this.eventVar = value;
	}
	
	/**
	 * Variable name of the event component on the page
	 */
	public String getEventVar() {
		return eventVar;
	}
	
	/**
	 * URL of the event component
	 */
	public void setEventURL(String value) {
		this.eventURL = value;
	}
	
	/**
	 * URL of the event component
	 */
	public String getEventURL() {
		return eventURL;
	}
	
	/**
	 * Typical event contains two dates- mandatory event date and optional next event date. For registers it is registration and expiration dates. For schedulers it is creation and scheduled date
	 */
	public void setEventDate(java.util.Date value) {
		this.eventDate = value;
	}
	
	/**
	 * Typical event contains two dates- mandatory event date and optional next event date. For registers it is registration and expiration dates. For schedulers it is creation and scheduled date
	 */
	public java.util.Date getEventDate() {
		return eventDate;
	}
	
	/**
	 * Registration number for a register, concept ID for a scheduler
	 */
	public void setEventNumber(String value) {
		this.eventNumber = value;
	}
	
	/**
	 * Registration number for a register, concept ID for a scheduler
	 */
	public String getEventNumber() {
		return eventNumber;
	}
	
	/**
	 * Optional expiration date for a register or scheduled date for scheduler
	 */
	public void setNextEventDate(java.util.Date value) {
		this.nextEventDate = value;
	}
	
	/**
	 * Optional expiration date for a register or scheduled date for scheduler
	 */
	public java.util.Date getNextEventDate() {
		return nextEventDate;
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
