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
@Entity
@org.hibernate.annotations.Proxy(lazy=false)
@Table(name="workspace")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class Workspace implements Serializable {
	public Workspace() {
	}
	
	@Column(name="id", nullable=false)	
	@Id	
	@GeneratedValue(generator="VAC222274183363D22380E2BB")	
	@org.hibernate.annotations.GenericGenerator(name="VAC222274183363D22380E2BB", strategy="identity")	
	private long ID;
	
	@Column(name="createdDate", nullable=true)	
	private java.sql.Timestamp createdDate;
	
	@Column(name="updatedDate", nullable=true)	
	private java.sql.Timestamp updatedDate;
	
	@Column(name="datePattern", nullable=true, length=255)	
	private String datePattern;
	
	@Column(name="defaultLocale", nullable=true, length=10)	
	private String defaultLocale;
	
	@Column(name="detailReview", nullable=true, length=1)	
	private Boolean detailReview;
	
	@Column(name="displatPricing", nullable=true, length=1)	
	private Boolean displatPricing;
	
	@Column(name="name", nullable=true, length=255)	
	private String name;
	
	@Column(name="pipRegDuration", nullable=true, length=10)	
	private Integer pipRegDuration;
	
	@Column(name="prodRegDuration", nullable=true, length=10)	
	private Integer prodRegDuration;
	
	@Column(name="secReview", nullable=true, length=1)	
	private Boolean secReview;
	
	@Column(name="registrarName", nullable=true, length=255)	
	private String registrarName;
	
	@Column(name="registraremail", nullable=true, length=255)	
	private String registraremail;
	
	@Column(name="contentType", nullable=true, length=500)	
	private String contentType;
	
	@Column(name="file", nullable=true)	
	private java.sql.Blob file;
	
	@Column(name="fileName", nullable=true, length=500)	
	private String fileName;
	
	@Column(name="emblemSvg", nullable=true)	
	private String emblemSvg;
	
	@Column(name="title", nullable=true, length=255)	
	private String title;
	
	@Column(name="subtitle", nullable=true, length=255)	
	private String subtitle;
	
	@Column(name="address1", nullable=true, length=255)	
	private String address1;
	
	@Column(name="address2", nullable=true, length=255)	
	private String address2;
	
	@Column(name="slaQuantity", nullable=false, length=11)	
	private int slaQuantity = 0;
	
	@Column(name="slaDuration", nullable=false, length=11)	
	private int slaDuration = 0;
	
	@Column(name="slaMax", nullable=false, length=11)	
	private int slaMax = 0;
	
	private void setID(long value) {
		this.ID = value;
	}
	
	public long getID() {
		return ID;
	}
	
	public long getORMID() {
		return getID();
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
	
	public void setDatePattern(String value) {
		this.datePattern = value;
	}
	
	public String getDatePattern() {
		return datePattern;
	}
	
	public void setDefaultLocale(String value) {
		this.defaultLocale = value;
	}
	
	public String getDefaultLocale() {
		return defaultLocale;
	}
	
	public void setDetailReview(boolean value) {
		setDetailReview(new Boolean(value));
	}
	
	public void setDetailReview(Boolean value) {
		this.detailReview = value;
	}
	
	public Boolean getDetailReview() {
		return detailReview;
	}
	
	public void setDisplatPricing(boolean value) {
		setDisplatPricing(new Boolean(value));
	}
	
	public void setDisplatPricing(Boolean value) {
		this.displatPricing = value;
	}
	
	public Boolean getDisplatPricing() {
		return displatPricing;
	}
	
	public void setName(String value) {
		this.name = value;
	}
	
	public String getName() {
		return name;
	}
	
	public void setPipRegDuration(int value) {
		setPipRegDuration(new Integer(value));
	}
	
	public void setPipRegDuration(Integer value) {
		this.pipRegDuration = value;
	}
	
	public Integer getPipRegDuration() {
		return pipRegDuration;
	}
	
	public void setProdRegDuration(int value) {
		setProdRegDuration(new Integer(value));
	}
	
	public void setProdRegDuration(Integer value) {
		this.prodRegDuration = value;
	}
	
	public Integer getProdRegDuration() {
		return prodRegDuration;
	}
	
	public void setSecReview(boolean value) {
		setSecReview(new Boolean(value));
	}
	
	public void setSecReview(Boolean value) {
		this.secReview = value;
	}
	
	public Boolean getSecReview() {
		return secReview;
	}
	
	public void setRegistrarName(String value) {
		this.registrarName = value;
	}
	
	public String getRegistrarName() {
		return registrarName;
	}
	
	public void setRegistraremail(String value) {
		this.registraremail = value;
	}
	
	public String getRegistraremail() {
		return registraremail;
	}
	
	public void setContentType(String value) {
		this.contentType = value;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setFile(java.sql.Blob value) {
		this.file = value;
	}
	
	public java.sql.Blob getFile() {
		return file;
	}
	
	public void setFileName(String value) {
		this.fileName = value;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setEmblemSvg(String value) {
		this.emblemSvg = value;
	}
	
	public String getEmblemSvg() {
		return emblemSvg;
	}
	
	public void setTitle(String value) {
		this.title = value;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setSubtitle(String value) {
		this.subtitle = value;
	}
	
	public String getSubtitle() {
		return subtitle;
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
	
	/**
	 * SLA HTTP queries per minute
	 */
	public void setSlaQuantity(int value) {
		this.slaQuantity = value;
	}
	
	/**
	 * SLA HTTP queries per minute
	 */
	public int getSlaQuantity() {
		return slaQuantity;
	}
	
	/**
	 * SLA duration of a http query
	 */
	public void setSlaDuration(int value) {
		this.slaDuration = value;
	}
	
	/**
	 * SLA duration of a http query
	 */
	public int getSlaDuration() {
		return slaDuration;
	}
	
	/**
	 * SLA duration of long running query
	 */
	public void setSlaMax(int value) {
		this.slaMax = value;
	}
	
	/**
	 * SLA duration of long running query
	 */
	public int getSlaMax() {
		return slaMax;
	}
	
	public String toString() {
		return String.valueOf(getID());
	}
	
}
