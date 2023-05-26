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
 * This table is only for efficiency. The main task is to get workflow dictionary and name by the URL
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="dwhworkflowlist")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class dwhworkflowlist implements Serializable {
	public dwhworkflowlist() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222771884EAAE2A303E68")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222771884EAAE2A303E68", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.dwh.ReportSession.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns({ @JoinColumn(name="reportsessionID") })	
	@org.hibernate.annotations.Index(name="byLang")	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.dwh.ReportSession reportSession;
	
	@Column(name="WorkflowDictUrl", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byWorkflowDictUrl")	
	private String workflowDictUrl;
	
	@Column(name="WorkflowDictName", nullable=true, length=255)	
	private String workflowDictName;
	
	@Column(name="WorkflowDictItemID", nullable=false, length=20)	
	@org.hibernate.annotations.Index(name="byWorkflowDictItemID")	
	private long workflowDictItemID;
	
	@Column(name="WorkflowUrl", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byWorkflowUrl")	
	private String workflowUrl;
	
	@Column(name="Workflow", nullable=true, length=255)	
	private String workflow;
	
	@Column(name="Lang", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="byLang")	
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
	 * guest, host, modi...
	 */
	public void setWorkflowDictUrl(String value) {
		this.workflowDictUrl = value;
	}
	
	/**
	 * guest, host, modi...
	 */
	public String getWorkflowDictUrl() {
		return workflowDictUrl;
	}
	
	/**
	 * Workflow dictionary name or name of workflow class
	 */
	public void setWorkflowDictName(String value) {
		this.workflowDictName = value;
	}
	
	/**
	 * Workflow dictionary name or name of workflow class
	 */
	public String getWorkflowDictName() {
		return workflowDictName;
	}
	
	/**
	 * Workflow definition dictionary item ID
	 */
	public void setWorkflowDictItemID(long value) {
		this.workflowDictItemID = value;
	}
	
	/**
	 * Workflow definition dictionary item ID
	 */
	public long getWorkflowDictItemID() {
		return workflowDictItemID;
	}
	
	/**
	 * URL of workflow configuration
	 */
	public void setWorkflowUrl(String value) {
		this.workflowUrl = value;
	}
	
	/**
	 * URL of workflow configuration
	 */
	public String getWorkflowUrl() {
		return workflowUrl;
	}
	
	/**
	 * Name of workflow, e.g., Vaccine registration
	 */
	public void setWorkflow(String value) {
		this.workflow = value;
	}
	
	/**
	 * Name of workflow, e.g., Vaccine registration
	 */
	public String getWorkflow() {
		return workflow;
	}
	
	/**
	 * The language of the workflow name
	 */
	public void setLang(String value) {
		this.lang = value;
	}
	
	/**
	 * The language of the workflow name
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
