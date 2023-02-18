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
package org.msh.pdex2.model.i18n;

import java.io.Serializable;
import javax.persistence.*;
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="resource_bundle")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class ResourceBundle implements Serializable {
	public ResourceBundle() {
	}
	
	@Column(name="Id", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC22226E1864BE1799F0BC90")	
	@org.hibernate.annotations.GenericGenerator(name="VAC22226E1864BE1799F0BC90", strategy="native")	
	private long id;
	
	@Column(name="SortOrder", nullable=true, length=1)	
	private Boolean sortOrder;
	
	@Column(name="Basename", nullable=true, length=255)	
	private String basename;
	
	@Column(name="Locale", nullable=true, length=255)	
	private String locale;
	
	@Column(name="DisplayName", nullable=true, length=255)	
	private String displayName;
	
	@Column(name="SvgFlag", nullable=true)	
	private String svgFlag;
	
	@Column(name="UsaidLogo", nullable=true)	
	private String usaidLogo;
	
	@Column(name="NmraLogo", nullable=true)	
	private String nmraLogo;
	
	@OneToMany(targetEntity=org.msh.pdex2.model.i18n.ResourceMessage.class)	
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.LOCK})	
	@JoinColumn(name="key_bundle", nullable=true)	
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.TRUE)	
	private java.util.Set<org.msh.pdex2.model.i18n.ResourceMessage> messages = new java.util.HashSet<org.msh.pdex2.model.i18n.ResourceMessage>();
	
	private void setId(long value) {
		this.id = value;
	}
	
	public long getId() {
		return id;
	}
	
	public long getORMID() {
		return getId();
	}
	
	public void setSortOrder(boolean value) {
		setSortOrder(new Boolean(value));
	}
	
	public void setSortOrder(Boolean value) {
		this.sortOrder = value;
	}
	
	public Boolean getSortOrder() {
		return sortOrder;
	}
	
	public void setBasename(String value) {
		this.basename = value;
	}
	
	public String getBasename() {
		return basename;
	}
	
	public void setLocale(String value) {
		this.locale = value;
	}
	
	public String getLocale() {
		return locale;
	}
	
	public void setDisplayName(String value) {
		this.displayName = value;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setSvgFlag(String value) {
		this.svgFlag = value;
	}
	
	public String getSvgFlag() {
		return svgFlag;
	}
	
	public void setUsaidLogo(String value) {
		this.usaidLogo = value;
	}
	
	public String getUsaidLogo() {
		return usaidLogo;
	}
	
	public void setNmraLogo(String value) {
		this.nmraLogo = value;
	}
	
	public String getNmraLogo() {
		return nmraLogo;
	}
	
	public void setMessages(java.util.Set<org.msh.pdex2.model.i18n.ResourceMessage> value) {
		this.messages = value;
	}
	
	public java.util.Set<org.msh.pdex2.model.i18n.ResourceMessage> getMessages() {
		return messages;
	}
	
	
	public String toString() {
		return String.valueOf(getId());
	}
	
}
