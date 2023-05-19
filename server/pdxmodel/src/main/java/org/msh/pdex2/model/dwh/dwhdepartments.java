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
 * Auxiliarry table to easy get department name by the NMRA user email
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="dwhdepartments")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class dwhdepartments implements Serializable {
	public dwhdepartments() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222771882F86B33B099FE")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222771882F86B33B099FE", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@org.hibernate.annotations.Index(name="byDepartment")	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@Column(name="Email", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byEmail")	
	private String email;
	
	@Column(name="Department", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byDepartment")	
	private String department;
	
	@Column(name="Lang", nullable=true, length=255)	
	private String lang;
	
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
	 * email of a NMRA user
	 */
	public void setEmail(String value) {
		this.email = value;
	}
	
	/**
	 * email of a NMRA user
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * The department the user belong to
	 */
	public void setDepartment(String value) {
		this.department = value;
	}
	
	/**
	 * The department the user belong to
	 */
	public String getDepartment() {
		return department;
	}
	
	/**
	 * Language of the department name
	 */
	public void setLang(String value) {
		this.lang = value;
	}
	
	/**
	 * Language of the department name
	 */
	public String getLang() {
		return lang;
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
