package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Report configuration
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ReportConfigDTO extends AllowValidation{
	private String dataUrl="";
	private String dictStageUrl="";
	private String ownerUrl="";
	private String addressUrl="";
	private String inspectAppUrl ="";
	private String renewAppUrl ="";
	private String registerAppUrl="";
	private boolean applicantRestriction=false;		//applicant should see only own
	private boolean registered=true;
	public String getDataUrl() {
		return dataUrl;
	}
	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}
	public String getOwnerUrl() {
		return ownerUrl;
	}
	public void setOwnerUrl(String ownerUrl) {
		this.ownerUrl = ownerUrl;
	}
	public String getAddressUrl() {
		return addressUrl;
	}
	public void setAddressUrl(String addressUrl) {
		this.addressUrl = addressUrl;
	}
	public boolean isRegistered() {
		return registered;
	}
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}
	public String getDictStageUrl() {
		return dictStageUrl;
	}
	public void setDictStageUrl(String dictStageUrl) {
		this.dictStageUrl = dictStageUrl;
	}
	public String getInspectAppUrl() {
		return inspectAppUrl;
	}
	public void setInspectAppUrl(String inspectAppUrl) {
		this.inspectAppUrl = inspectAppUrl;
	}
	public String getRenewAppUrl() {
		return renewAppUrl;
	}
	public void setRenewAppUrl(String renewAppUrl) {
		this.renewAppUrl = renewAppUrl;
	}
	public String getRegisterAppUrl() {
		return registerAppUrl;
	}
	public void setRegisterAppUrl(String registerAppUrl) {
		this.registerAppUrl = registerAppUrl;
	}
	public boolean isApplicantRestriction() {
		return applicantRestriction;
	}
	public void setApplicantRestriction(boolean applicantRestriction) {
		this.applicantRestriction = applicantRestriction;
	}

}
