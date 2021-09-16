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
 * A structured value providing information about the opening hours of a place or a certain service inside a place.
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="openinghoursspec")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class OpeningHoursSpec implements Serializable {
	public OpeningHoursSpec() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227217BE3E951C00B4C1")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227217BE3E951C00B4C1", strategy="native")	
	private long ID;
	
	@Column(name="Dow", nullable=false, length=11)	
	private int dow;
	
	@Column(name="Closes", nullable=false, length=11)	
	private int closes;
	
	@Column(name="Opens", nullable=false, length=11)	
	private int opens;
	
	@Column(name="ValidFrom", nullable=true)	
	private java.util.Date validFrom;
	
	@Column(name="ValidThrough", nullable=true)	
	private java.util.Date validThrough;
	
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
	 * Day of week from 1-7 Monday-Sunday
	 */
	public void setDow(int value) {
		this.dow = value;
	}
	
	/**
	 * Day of week from 1-7 Monday-Sunday
	 */
	public int getDow() {
		return dow;
	}
	
	/**
	 * The closing hour of the place or service on the given day of the week. In minutes since midnight
	 */
	public void setCloses(int value) {
		this.closes = value;
	}
	
	/**
	 * The closing hour of the place or service on the given day of the week. In minutes since midnight
	 */
	public int getCloses() {
		return closes;
	}
	
	/**
	 * The opening hour of the place or service on the given day of the week. In minutes since midnight
	 */
	public void setOpens(int value) {
		this.opens = value;
	}
	
	/**
	 * The opening hour of the place or service on the given day of the week. In minutes since midnight
	 */
	public int getOpens() {
		return opens;
	}
	
	/**
	 * The date when the item becomes valid
	 */
	public void setValidFrom(java.util.Date value) {
		this.validFrom = value;
	}
	
	/**
	 * The date when the item becomes valid
	 */
	public java.util.Date getValidFrom() {
		return validFrom;
	}
	
	/**
	 * he date after when the item is not valid. For example the end of an offer, salary period, or a period of opening hours.
	 */
	public void setValidThrough(java.util.Date value) {
		this.validThrough = value;
	}
	
	/**
	 * he date after when the item is not valid. For example the end of an offer, salary period, or a period of opening hours.
	 */
	public java.util.Date getValidThrough() {
		return validThrough;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
