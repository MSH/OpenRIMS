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
	@GeneratedValue(generator="VAC22227418129ECB58B0CEC7")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227418129ECB58B0CEC7", strategy="native")	
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
	
	@Column(name="Language", nullable=true, length=255)	
	private String language;
	
	@Column(name="ValueStr", nullable=true, length=255)	
	private String valueStr;
	
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
	public void setLanguage(String value) {
		this.language = value;
	}
	
	/**
	 * The language
	 */
	public String getLanguage() {
		return language;
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
