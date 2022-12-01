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
 * Old style roles list
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="role")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Role implements Serializable {
	public Role() {
	}
	
	@Column(name="roleId", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC222274184AF102B890E7F9")	
	@org.hibernate.annotations.GenericGenerator(name="VAC222274184AF102B890E7F9", strategy="identity")	
	private int roleId;
	
	@Column(name="createdDate", nullable=true)	
	private java.sql.Timestamp createdDate;
	
	@Column(name="updatedDate", nullable=true)	
	private java.sql.Timestamp updatedDate;
	
	@Column(name="description", nullable=true, length=255)	
	private String description;
	
	@Column(name="Displayname", nullable=true, length=255)	
	private String displayname;
	
	@Column(name="Rolename", nullable=true, length=255)	
	@org.hibernate.annotations.Index(name="UK_nctmxadhieiw7aduxjy4dfglt")	
	private String rolename;
	
	private void setRoleId(int value) {
		this.roleId = value;
	}
	
	public int getRoleId() {
		return roleId;
	}
	
	public int getORMID() {
		return getRoleId();
	}
	
	public void setCreatedDate(java.sql.Timestamp value) {
		this.createdDate = value;
	}
	
	public java.sql.Timestamp getCreatedDate() {
		return createdDate;
	}
	
	public void setUpdatedDate(java.sql.Timestamp value) {
		this.updatedDate = value;
	}
	
	public java.sql.Timestamp getUpdatedDate() {
		return updatedDate;
	}
	
	public void setDescription(String value) {
		this.description = value;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDisplayname(String value) {
		this.displayname = value;
	}
	
	public String getDisplayname() {
		return displayname;
	}
	
	public void setRolename(String value) {
		this.rolename = value;
	}
	
	public String getRolename() {
		return rolename;
	}
	
	public String toString() {
		return String.valueOf(getRoleId());
	}
	
}
