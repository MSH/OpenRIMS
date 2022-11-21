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
@Table(name="reportsession")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ReportSession implements Serializable {
	public ReportSession() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222741845D6414E704DF0")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222741845D6414E704DF0", strategy="native")	
	private long ID;
	
	@Column(name="StartedAt", nullable=true)	
	private java.util.Date startedAt;
	
	@Column(name="CompletedAt", nullable=true)	
	private java.util.Date completedAt;
	
	@Column(name="Actual", nullable=false, length=1)	
	private boolean actual;
	
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
	 * When data upload has been started
	 */
	public void setStartedAt(java.util.Date value) {
		this.startedAt = value;
	}
	
	/**
	 * When data upload has been started
	 */
	public java.util.Date getStartedAt() {
		return startedAt;
	}
	
	/**
	 * When data upload has been completed
	 */
	public void setCompletedAt(java.util.Date value) {
		this.completedAt = value;
	}
	
	/**
	 * When data upload has been completed
	 */
	public java.util.Date getCompletedAt() {
		return completedAt;
	}
	
	/**
	 * Only one may be actual
	 */
	public void setActual(boolean value) {
		this.actual = value;
	}
	
	/**
	 * Only one may be actual
	 */
	public boolean getActual() {
		return actual;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
