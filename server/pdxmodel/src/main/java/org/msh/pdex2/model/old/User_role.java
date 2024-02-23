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
 * Old style M:M relation between users and roles
 */
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="user_role")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class User_role implements Serializable {
	public User_role() {
	}
	
	@Column(name="ID", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22227718DA768E97302BCD")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22227718DA768E97302BCD", strategy="identity")	
	private int ID;
	
	@ManyToOne(targetEntity=org.msh.pdex2.model.old.Role.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumns({ @JoinColumn(name="roleId", referencedColumnName="roleId") })	
	@Basic(fetch=FetchType.LAZY)	
	private org.msh.pdex2.model.old.Role role;
	
	@Column(name="Active", nullable=false)	
	private boolean active;
	
	private void setID(int value) {
		this.ID = value;
	}
	
	public int getID() {
		return ID;
	}
	
	public int getORMID() {
		return getID();
	}
	
	public void setActive(boolean value) {
		this.active = value;
	}
	
	public boolean getActive() {
		return active;
	}
	
	public void setRole(org.msh.pdex2.model.old.Role value) {
		this.role = value;
	}
	
	public org.msh.pdex2.model.old.Role getRole() {
		return role;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
