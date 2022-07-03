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
 * Literals related to the any concept
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="reportliteral")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ReportLiteral implements Serializable {
	public ReportLiteral() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC222274181AB73CE660B14D")	
	@org.hibernate.annotations.GenericGenerator(name="VAC222274181AB73CE660B14D", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@Column(name="ConceptID", nullable=false, length=20)	
	private long conceptID;
	
	@Column(name="Variable", nullable=true, length=255)	
	private String variable;
	
	@Column(name="Lang", nullable=true, length=255)	
	private String lang;
	
	@Column(name="ValueStr", nullable=true, length=2048)	
	private String valueStr;
	
	@Column(name="ValueDate", nullable=true)	
	private java.util.Date valueDate;
	
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
	 * Identifier of the concept in the main database
	 */
	public void setConceptID(long value) {
		this.conceptID = value;
	}
	
	/**
	 * Identifier of the concept in the main database
	 */
	public long getConceptID() {
		return conceptID;
	}
	
	/**
	 * variable name
	 */
	public void setVariable(String value) {
		this.variable = value;
	}
	
	/**
	 * variable name
	 */
	public String getVariable() {
		return variable;
	}
	
	/**
	 * The language
	 */
	public void setLang(String value) {
		this.lang = value;
	}
	
	/**
	 * The language
	 */
	public String getLang() {
		return lang;
	}
	
	/**
	 * value as a string
	 */
	public void setValueStr(String value) {
		this.valueStr = value;
	}
	
	/**
	 * value as a string
	 */
	public String getValueStr() {
		return valueStr;
	}
	
	/**
	 * In case the value can be represented as Date
	 */
	public void setValueDate(java.util.Date value) {
		this.valueDate = value;
	}
	
	/**
	 * In case the value can be represented as Date
	 */
	public java.util.Date getValueDate() {
		return valueDate;
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
