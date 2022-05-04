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
package org.msh.pdex2.model.r2;

import java.io.Serializable;
import javax.persistence.*;
/**
 * Responsible for follow-up tasks
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="scheduler")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Scheduler implements Serializable {
	public Scheduler() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227417FFB32C96805F76")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227417FFB32C96805F76", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumns({ @JoinColumn(name="conceptID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
	@Column(name="Scheduled", nullable=true)	
	private java.util.Date scheduled;
	
	@Column(name="ProcessUrl", nullable=true, length=255)	
	private String processUrl;
	
	@Column(name="CreatedAt", nullable=true)	
	private java.util.Date createdAt;
	
	@Column(name="ChangedAt", nullable=true)	
	private java.util.Date changedAt;
	
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
	 * Data to which this event has been scheduled
	 */
	public void setScheduled(java.util.Date value) {
		this.scheduled = value;
	}
	
	/**
	 * Data to which this event has been scheduled
	 */
	public java.util.Date getScheduled() {
		return scheduled;
	}
	
	public void setProcessUrl(String value) {
		this.processUrl = value;
	}
	
	public String getProcessUrl() {
		return processUrl;
	}
	
	/**
	 * Created date
	 */
	public void setCreatedAt(java.util.Date value) {
		this.createdAt = value;
	}
	
	/**
	 * Created date
	 */
	public java.util.Date getCreatedAt() {
		return createdAt;
	}
	
	public void setChangedAt(java.util.Date value) {
		this.changedAt = value;
	}
	
	public java.util.Date getChangedAt() {
		return changedAt;
	}
	
	public void setConcept(org.msh.pdex2.model.r2.Concept value) {
		this.concept = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getConcept() {
		return concept;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
