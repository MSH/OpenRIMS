package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationSelectDTO extends AllowValidation {
	private String userName="";
	private DictionaryDTO appListDictionary=new DictionaryDTO();
	private ApplicationsDTO applications=new ApplicationsDTO();
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public DictionaryDTO getAppListDictionary() {
		return appListDictionary;
	}
	public void setAppListDictionary(DictionaryDTO appListDictionary) {
		this.appListDictionary = appListDictionary;
	}
	public ApplicationsDTO getApplications() {
		return applications;
	}
	public void setApplications(ApplicationsDTO applications) {
		this.applications = applications;
	}

	
	
}
