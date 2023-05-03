package org.msh.pharmadex2.dto;

import java.time.LocalDate;

public class ReportPageGQL {
	private String lang = "";
	private String url = "";
	private String pharmacy = "";
	private String district = "";
	private String address = "";
	private String pharmtype = "";
	private String registerNo = "";
	private LocalDate registered;
	private LocalDate expired;
	private String owners = "";
	private String pharmacists = "";
	
	
	public LocalDate getRegistered() {
		return registered;
	}
	public void setRegistered(LocalDate registered) {
		this.registered = registered;
	}
	public LocalDate getExpired() {
		return expired;
	}
	public void setExpired(LocalDate expired) {
		this.expired = expired;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPharmacy() {
		return pharmacy;
	}
	public void setPharmacy(String pharmacy) {
		this.pharmacy = pharmacy;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPharmtype() {
		return pharmtype;
	}
	public void setPharmtype(String pharmtype) {
		this.pharmtype = pharmtype;
	}
	public String getRegisterNo() {
		return registerNo;
	}
	public void setRegisterNo(String registerNo) {
		this.registerNo = registerNo;
	}
	public String getOwners() {
		return owners;
	}
	public void setOwners(String owners) {
		this.owners = owners;
	}
	public String getPharmacists() {
		return pharmacists;
	}
	public void setPharmacists(String pharmacists) {
		this.pharmacists = pharmacists;
	}
}
