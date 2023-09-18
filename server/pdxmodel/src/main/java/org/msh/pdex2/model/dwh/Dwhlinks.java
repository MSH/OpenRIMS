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
@Table(name="dwhlinks")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Dwhlinks implements Serializable {
	public Dwhlinks() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227818A83E427F005C70")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227818A83E427F005C70", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@org.hibernate.annotations.Index(name="byMainPageID")	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@Column(name="Lang", nullable=true, length=255)	
	private String lang;
	
	@Column(name="PageID", nullable=false, length=20)	
	@org.hibernate.annotations.Index(name="byPageID")	
	private long pageID;
	
	@Column(name="PageURL", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byPageURL")	
	private String pageURL;
	
	@Column(name="LinkVar", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byLinkVar")	
	private String linkVar;
	
	@Column(name="LinkPage", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byLinkPage")	
	private String linkPage;
	
	@Column(name="LinkURL", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byLinkURL")	
	private String linkURL;
	
	@Column(name="PrefLabel", nullable=true, length=255)	
	private String prefLabel;
	
	@Column(name="LinkIdentificatorURL", nullable=true, length=255)	
	private String linkIdentificatorURL;
	
	@Column(name="LinkIdentificator", nullable=true, length=255)	
	private String linkIdentificator;
	
	@Column(name="MainPageID", nullable=false, length=20)	
	@org.hibernate.annotations.Index(name="byMainPageID")	
	private long mainPageID;
	
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
	 * language
	 */
	public void setLang(String value) {
		this.lang = value;
	}
	
	/**
	 * language
	 */
	public String getLang() {
		return lang;
	}
	
	/**
	 * ID of page on which link is placed
	 */
	public void setPageID(long value) {
		this.pageID = value;
	}
	
	/**
	 * ID of page on which link is placed
	 */
	public long getPageID() {
		return pageID;
	}
	
	/**
	 * URL of page on which link is placed
	 */
	public void setPageURL(String value) {
		this.pageURL = value;
	}
	
	/**
	 * URL of page on which link is placed
	 */
	public String getPageURL() {
		return pageURL;
	}
	
	/**
	 * Main application page - applDataID in the History
	 */
	public void setMainPageID(long value) {
		this.mainPageID = value;
	}
	
	/**
	 * Main application page - applDataID in the History
	 */
	public long getMainPageID() {
		return mainPageID;
	}
	
	/**
	 * An identified relation is possible for "links", not for person. In the example "Warehouse 1 is a main warehouse for the wholesaler, the "main warehouse" is the identifier of relation. This URL is URL of a dictionary contains possible identifiers
	 */
	public void setLinkIdentificatorURL(String value) {
		this.linkIdentificatorURL = value;
	}
	
	/**
	 * An identified relation is possible for "links", not for person. In the example "Warehouse 1 is a main warehouse for the wholesaler, the "main warehouse" is the identifier of relation. This URL is URL of a dictionary contains possible identifiers
	 */
	public String getLinkIdentificatorURL() {
		return linkIdentificatorURL;
	}
	
	/**
	 * An identified relation is possible for "links", not for person. In the example "Warehouse 1 is a main warehouse for the wholesaler, the "main warehouse" is the identifier of relation.The relation is a classifier contains the full relation identifier
	 */
	public void setLinkIdentificator(String value) {
		this.linkIdentificator = value;
	}
	
	/**
	 * An identified relation is possible for "links", not for person. In the example "Warehouse 1 is a main warehouse for the wholesaler, the "main warehouse" is the identifier of relation.The relation is a classifier contains the full relation identifier
	 */
	public String getLinkIdentificator() {
		return linkIdentificator;
	}
	
	/**
	 * Variable to recognize the link inside the page
	 */
	public void setLinkVar(String value) {
		this.linkVar = value;
	}
	
	/**
	 * Variable to recognize the link inside the page
	 */
	public String getLinkVar() {
		return linkVar;
	}
	
	/**
	 * Main page of linked data
	 */
	public void setLinkPage(String value) {
		this.linkPage = value;
	}
	
	/**
	 * Main page of linked data
	 */
	public String getLinkPage() {
		return linkPage;
	}
	
	/**
	 * URL of link (main page of linked data)
	 */
	public void setLinkURL(String value) {
		this.linkURL = value;
	}
	
	/**
	 * URL of link (main page of linked data)
	 */
	public String getLinkURL() {
		return linkURL;
	}
	
	/**
	 * Pref label of the link
	 */
	public void setPrefLabel(String value) {
		this.prefLabel = value;
	}
	
	/**
	 * Pref label of the link
	 */
	public String getPrefLabel() {
		return prefLabel;
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
