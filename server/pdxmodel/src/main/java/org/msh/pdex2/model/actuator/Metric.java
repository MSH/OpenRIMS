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
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="metric")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="Discriminator", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("metric")
public class Metric implements Serializable {
	public Metric() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC222274181AB73CE6E0B152")	
	@org.hibernate.annotations.GenericGenerator(name="VAC222274181AB73CE6E0B152", strategy="native")	
	private long ID;
	
	@Column(name="Application", nullable=true, length=255)	
	private String application;
	
	@Column(name="ApplVersion", nullable=true, length=255)	
	private String applVersion;
	
	@Column(name="Counter200", nullable=false, length=20)	
	private long counter200;
	
	@Column(name="Counter404", nullable=false, length=20)	
	private long counter404;
	
	@Column(name="Counter500", nullable=false, length=20)	
	private long counter500;
	
	@Column(name="RequestsTotalTime", nullable=false, length=20)	
	private long requestsTotalTime;
	
	@Column(name="RequestsMaxTime", nullable=false, length=20)	
	private long requestsMaxTime;
	
	@Column(name="FreeDisk", nullable=false, length=20)	
	private long freeDisk;
	
	@Column(name="CpuPercents", nullable=false, length=20)	
	private long cpuPercents;
	
	@Column(name="JvmMemoryMax", nullable=false, length=20)	
	private long jvmMemoryMax;
	
	@Column(name="HikariConnMax", nullable=false, length=20)	
	private long hikariConnMax;
	
	@Column(name="JdbcConnMax", nullable=false, length=20)	
	private long jdbcConnMax;
	
	@Column(name="TomcatConnMax", nullable=false, length=20)	
	private long tomcatConnMax;
	
	@Column(name="JvmThreadsMax", nullable=false, length=20)	
	private long jvmThreadsMax;
	
	@Column(name="CreatedAt", nullable=true)	
	private java.util.Date createdAt;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
	}
	
	public void setApplication(String value) {
		this.application = value;
	}
	
	public String getApplication() {
		return application;
	}
	
	public void setApplVersion(String value) {
		this.applVersion = value;
	}
	
	public String getApplVersion() {
		return applVersion;
	}
	
	/**
	 * Quantity of successful POSTs
	 */
	public void setCounter200(long value) {
		this.counter200 = value;
	}
	
	/**
	 * Quantity of successful POSTs
	 */
	public long getCounter200() {
		return counter200;
	}
	
	/**
	 * Counter of managed errors. All managed errors are 404
	 */
	public void setCounter404(long value) {
		this.counter404 = value;
	}
	
	/**
	 * Counter of managed errors. All managed errors are 404
	 */
	public long getCounter404() {
		return counter404;
	}
	
	/**
	 * Counter of unmanaged errors with code 500
	 */
	public void setCounter500(long value) {
		this.counter500 = value;
	}
	
	/**
	 * Counter of unmanaged errors with code 500
	 */
	public long getCounter500() {
		return counter500;
	}
	
	/**
	 * Total time to complete all POST requests in mils
	 */
	public void setRequestsTotalTime(long value) {
		this.requestsTotalTime = value;
	}
	
	/**
	 * Total time to complete all POST requests in mils
	 */
	public long getRequestsTotalTime() {
		return requestsTotalTime;
	}
	
	/**
	 * Maximum time to complete POST request in mils
	 */
	public void setRequestsMaxTime(long value) {
		this.requestsMaxTime = value;
	}
	
	/**
	 * Maximum time to complete POST request in mils
	 */
	public long getRequestsMaxTime() {
		return requestsMaxTime;
	}
	
	/**
	 * Free disk space in MB
	 */
	public void setFreeDisk(long value) {
		this.freeDisk = value;
	}
	
	/**
	 * Free disk space in MB
	 */
	public long getFreeDisk() {
		return freeDisk;
	}
	
	/**
	 * Percents of CPU usage
	 */
	public void setCpuPercents(long value) {
		this.cpuPercents = value;
	}
	
	/**
	 * Percents of CPU usage
	 */
	public long getCpuPercents() {
		return cpuPercents;
	}
	
	/**
	 * Max amount of memory that is using by JVM
	 */
	public void setJvmMemoryMax(long value) {
		this.jvmMemoryMax = value;
	}
	
	/**
	 * Max amount of memory that is using by JVM
	 */
	public long getJvmMemoryMax() {
		return jvmMemoryMax;
	}
	
	/**
	 * Maximum Hikari pool connections used
	 */
	public void setHikariConnMax(long value) {
		this.hikariConnMax = value;
	}
	
	/**
	 * Maximum Hikari pool connections used
	 */
	public long getHikariConnMax() {
		return hikariConnMax;
	}
	
	/**
	 * Maximum JDBC connections
	 */
	public void setJdbcConnMax(long value) {
		this.jdbcConnMax = value;
	}
	
	/**
	 * Maximum JDBC connections
	 */
	public long getJdbcConnMax() {
		return jdbcConnMax;
	}
	
	/**
	 * Maximum number of Tomcat HTTP connections
	 */
	public void setTomcatConnMax(long value) {
		this.tomcatConnMax = value;
	}
	
	/**
	 * Maximum number of Tomcat HTTP connections
	 */
	public long getTomcatConnMax() {
		return tomcatConnMax;
	}
	
	/**
	 * Maximum number of JVM threads
	 */
	public void setJvmThreadsMax(long value) {
		this.jvmThreadsMax = value;
	}
	
	/**
	 * Maximum number of JVM threads
	 */
	public long getJvmThreadsMax() {
		return jvmThreadsMax;
	}
	
	public void setCreatedAt(java.util.Date value) {
		this.createdAt = value;
	}
	
	public java.util.Date getCreatedAt() {
		return createdAt;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
