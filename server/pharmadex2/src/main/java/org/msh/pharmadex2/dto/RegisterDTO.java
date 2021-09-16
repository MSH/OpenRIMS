package org.msh.pharmadex2.dto;

import java.time.LocalDate;

import org.msh.pharmadex2.dto.form.AllowValidation;
/**
 * Responsible for 
 * @author alexk
 *
 */
import org.msh.pharmadex2.dto.form.FormFieldDTO;
public class RegisterDTO extends AllowValidation {
	private FormFieldDTO<LocalDate> registration_date = FormFieldDTO.of(LocalDate.now());
	private FormFieldDTO<LocalDate> expiry_date = FormFieldDTO.of(LocalDate.now());
	private FormFieldDTO<String> reg_number = FormFieldDTO.of("");
	private FormFieldDTO<String> prev = FormFieldDTO.of("");	//previous registration number, typically read only
	//is this date expirable
	private boolean expirable=false;
	//to include to the thing
	private String url="";
	private String varName="";
	private long nodeID=0l;
	private long appDataID=0l;
	
	public FormFieldDTO<LocalDate> getRegistration_date() {
		return registration_date;
	}
	public void setRegistration_date(FormFieldDTO<LocalDate> registration_date) {
		this.registration_date = registration_date;
	}
	public FormFieldDTO<LocalDate> getExpiry_date() {
		return expiry_date;
	}
	public void setExpiry_date(FormFieldDTO<LocalDate> expiry_date) {
		this.expiry_date = expiry_date;
	}
	public FormFieldDTO<String> getReg_number() {
		return reg_number;
	}
	public void setReg_number(FormFieldDTO<String> reg_number) {
		this.reg_number = reg_number;
	}
	
	public FormFieldDTO<String> getPrev() {
		return prev;
	}
	public void setPrev(FormFieldDTO<String> prev) {
		this.prev = prev;
	}
	public boolean isExpirable() {
		return expirable;
	}
	public void setExpirable(boolean expirable) {
		this.expirable = expirable;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public long getNodeID() {
		return nodeID;
	}
	public void setNodeID(long nodeID) {
		this.nodeID = nodeID;
	}
	public long getAppDataID() {
		return appDataID;
	}
	public void setAppDataID(long appDataID) {
		this.appDataID = appDataID;
	}
	
}
