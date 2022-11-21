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
package org.msh.pdex2.model.old;

import java.io.Serializable;
import javax.persistence.*;
/**
 * Responsible for NMRA users configurations
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="`user`")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class User implements Serializable {
	public User() {
	}
	
	@Column(name="userId", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC2222741845D6414B504DCB")	
	@org.hibernate.annotations.GenericGenerator(name="VAC2222741845D6414B504DCB", strategy="native")	
	private long userId;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="conceptID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept conceptRole;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="organizationID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept organization;
	
	@OneToOne(targetEntity=org.msh.pdex2.model.r2.Concept.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="auxDataID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.r2.Concept concept;
	
	@Column(name="address1", nullable=true, length=500)	
	private String address1;
	
	@Column(name="address2", nullable=true, length=500)	
	private String address2;
	
	@Column(name="zipcode", nullable=true, length=500)	
	private String zipcode;
	
	@Column(name="comments", nullable=true, length=255)	
	private String comments;
	
	@Column(name="companyName", nullable=true, length=255)	
	private String companyName;
	
	@Column(name="Email", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="UK_f9dvvibvpfsldnu8wh3enop4i")	
	private String email;
	
	@Column(name="enabled", nullable=false, length=1)	
	private boolean enabled;
	
	@Column(name="faxNo", nullable=true, length=255)	
	private String faxNo;
	
	@Column(name="language", nullable=true, length=255)	
	private String language;
	
	@Column(name="Name", nullable=true, length=255)	
	private String name;
	
	@Column(name="Password", nullable=true, length=255)	
	private String password;
	
	@Column(name="phoneNo", nullable=true, length=255)	
	private String phoneNo;
	
	@Column(name="registrationDate", nullable=true)	
	private java.sql.Timestamp registrationDate;
	
	@Column(name="timeZone", nullable=true, length=255)	
	private String timeZone;
	
	@Column(name="type", nullable=true, length=255)	
	private String type;
	
	@Column(name="Username", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="UK_f9dvvibvpfsldnu8wh3enop4i")	
	private String username;
	
	@Column(name="zipaddress", nullable=true, length=32)	
	private String zipaddress;
	
	@Column(name="applicantApplcntId", nullable=true, length=19)	
	private Long applicantApplcntId;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.old.User_role.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="user_roleID", referencedColumnName="ID") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.old.User_role currentRole;
	
	@OneToMany(targetEntity=org.msh.pdex2.model.old.User_role.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumn(name="userId", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.old.User_role> roles = new java.util.HashSet<org.msh.pdex2.model.old.User_role>();
	
	@OneToMany(targetEntity=org.msh.pdex2.model.r2.UserDict.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})	
	@JoinColumn(name="useruserId", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.r2.UserDict> dictionaries = new java.util.HashSet<org.msh.pdex2.model.r2.UserDict>();
	
	private void setUserId(long value) {
		this.userId = value;
	}
	
	public long getUserId() {
		return userId;
	}
	
	public long getORMID() {
		return getUserId();
	}
	
	public void setAddress1(String value) {
		this.address1 = value;
	}
	
	public String getAddress1() {
		return address1;
	}
	
	public void setAddress2(String value) {
		this.address2 = value;
	}
	
	public String getAddress2() {
		return address2;
	}
	
	public void setZipcode(String value) {
		this.zipcode = value;
	}
	
	public String getZipcode() {
		return zipcode;
	}
	
	public void setComments(String value) {
		this.comments = value;
	}
	
	public String getComments() {
		return comments;
	}
	
	public void setCompanyName(String value) {
		this.companyName = value;
	}
	
	public String getCompanyName() {
		return companyName;
	}
	
	public void setEmail(String value) {
		this.email = value;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEnabled(boolean value) {
		this.enabled = value;
	}
	
	public boolean getEnabled() {
		return enabled;
	}
	
	public void setFaxNo(String value) {
		this.faxNo = value;
	}
	
	public String getFaxNo() {
		return faxNo;
	}
	
	public void setLanguage(String value) {
		this.language = value;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return name;
	}
	
	public void setPassword(String value) {
		this.password = value;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPhoneNo(String value) {
		this.phoneNo = value;
	}
	
	public String getPhoneNo() {
		return phoneNo;
	}
	
	public void setRegistrationDate(java.sql.Timestamp value) {
		this.registrationDate = value;
	}
	
	public java.sql.Timestamp getRegistrationDate() {
		return registrationDate;
	}
	
	public void setTimeZone(String value) {
		this.timeZone = value;
	}
	
	public String getTimeZone() {
		return timeZone;
	}
	
	public void setType(String value) {
		this.type = value;
	}
	
	public String getType() {
		return type;
	}
	
	public void setUsername(String value) {
		this.username = value;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setZipaddress(String value) {
		this.zipaddress = value;
	}
	
	public String getZipaddress() {
		return zipaddress;
	}
	
	public void setApplicantApplcntId(long value) {
		setApplicantApplcntId(new Long(value));
	}
	
	public void setApplicantApplcntId(Long value) {
		this.applicantApplcntId = value;
	}
	
	public Long getApplicantApplcntId() {
		return applicantApplcntId;
	}
	
	public void setCurrentRole(org.msh.pdex2.model.old.User_role value) {
		this.currentRole = value;
	}
	
	public org.msh.pdex2.model.old.User_role getCurrentRole() {
		return currentRole;
	}
	
	public void setRoles(java.util.Set<org.msh.pdex2.model.old.User_role> value) {
		this.roles = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.old.User_role> getRoles() {
		return roles;
	}
	
	
	public void setConcept(org.msh.pdex2.model.r2.Concept value) {
		this.concept = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getConcept() {
		return concept;
	}
	
	/**
	 * Link to organization hierarchy
	 */
	public void setOrganization(org.msh.pdex2.model.r2.Concept value) {
		this.organization = value;
	}
	
	/**
	 * Link to organization hierarchy
	 */
	public org.msh.pdex2.model.r2.Concept getOrganization() {
		return organization;
	}
	
	public void setDictionaries(java.util.Set<org.msh.pdex2.model.r2.UserDict> value) {
		this.dictionaries = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.r2.UserDict> getDictionaries() {
		return dictionaries;
	}
	
	
	public void setConceptRole(org.msh.pdex2.model.r2.Concept value) {
		this.conceptRole = value;
	}
	
	public org.msh.pdex2.model.r2.Concept getConceptRole() {
		return conceptRole;
	}
	
	public String toString() {
		return String.valueOf(getUserId());
	}
	
}
