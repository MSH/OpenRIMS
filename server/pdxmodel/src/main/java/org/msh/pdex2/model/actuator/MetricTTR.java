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
package org.msh.pdex2.model.actuator;

import java.io.Serializable;
import javax.persistence.*;
/**
 * Keeps data to build time to reply histogram
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="metricttr")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class MetricTTR implements Serializable {
	public MetricTTR() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222741823EA5BF1E08ACE")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222741823EA5BF1E08ACE", strategy="native")	
	private long ID;
	
	@Column(name="Minute", nullable=true)	
	private java.util.Date minute;
	
	@Column(name="Quantity", nullable=false, length=11)	
	private int quantity;
	
	@Column(name="DurationMills", nullable=false, length=11)	
	private int durationMills;
	
	@Column(name="MaxMills", nullable=false, length=11)	
	private int maxMills;
	
	@Column(name="QuantityTotal", nullable=false, length=20)	
	private long quantityTotal;
	
	@Column(name="DurationTotal", nullable=false, length=20)	
	private long durationTotal;
	
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
	 * Date, and time to minute when this metric has been collected. It means that metric is valid from the start of this minute until end of this minute
	 */
	public void setMinute(java.util.Date value) {
		this.minute = value;
	}
	
	/**
	 * Date, and time to minute when this metric has been collected. It means that metric is valid from the start of this minute until end of this minute
	 */
	public java.util.Date getMinute() {
		return minute;
	}
	
	/**
	 * Quantity of queries
	 */
	public void setQuantity(int value) {
		this.quantity = value;
	}
	
	/**
	 * Quantity of queries
	 */
	public int getQuantity() {
		return quantity;
	}
	
	/**
	 * Duration of queries in msec
	 */
	public void setDurationMills(int value) {
		this.durationMills = value;
	}
	
	/**
	 * Duration of queries in msec
	 */
	public int getDurationMills() {
		return durationMills;
	}
	
	/**
	 * Maximum time to execute a query - msec
	 */
	public void setMaxMills(int value) {
		this.maxMills = value;
	}
	
	/**
	 * Maximum time to execute a query - msec
	 */
	public int getMaxMills() {
		return maxMills;
	}
	
	public void setQuantityTotal(long value) {
		this.quantityTotal = value;
	}
	
	public long getQuantityTotal() {
		return quantityTotal;
	}
	
	public void setDurationTotal(long value) {
		this.durationTotal = value;
	}
	
	public long getDurationTotal() {
		return durationTotal;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
