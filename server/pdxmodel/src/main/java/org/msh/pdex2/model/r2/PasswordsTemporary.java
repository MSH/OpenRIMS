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
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="passwordstemporary")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class PasswordsTemporary implements Serializable {
	public PasswordsTemporary() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227718DA768E99102BEE")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227718DA768E99102BEE", strategy="native")	
	private long ID;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="conceptID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept companyUser;
	
	@Column(name="Password", nullable=true, length=255)	
	private String password;
	
	@Column(name="Expiration", nullable=true)	
	private java.util.Date expiration;
	
	@Column(name="UserName", nullable=true, length=255)	
	private String userName;
	
	@Column(name="Useremail", nullable=true, length=255)	
	private String useremail;
	
	@Column(name="Companyemail", nullable=true, length=255)	
	private String companyemail;
	
	@Column(name="Counter", nullable=false, length=11)	
	private int counter = 0;
	
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
	 * Encoded password
	 */
	public void setPassword(String value) {
		this.password = value;
	}
	
	/**
	 * Encoded password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Expiry date
	 */
	public void setExpiration(java.util.Date value) {
		this.expiration = value;
	}
	
	/**
	 * Expiry date
	 */
	public java.util.Date getExpiration() {
		return expiration;
	}
	
	public void setUserName(String value) {
		this.userName = value;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUseremail(String value) {
		this.useremail = value;
	}
	
	public String getUseremail() {
		return useremail;
	}
	
	public void setCompanyemail(String value) {
		this.companyemail = value;
	}
	
	public String getCompanyemail() {
		return companyemail;
	}
	
	/**
	 * Attemps counter. Will reset to 0 after success
	 */
	public void setCounter(int value) {
		this.counter = value;
	}
	
	/**
	 * Attemps counter. Will reset to 0 after success
	 */
	public int getCounter() {
		return counter;
	}
	
	public void setCompanyUser(org.msh.pdex2.model.r2.Concept value) {
		this.companyUser = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getCompanyUser() {
		return companyUser;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
