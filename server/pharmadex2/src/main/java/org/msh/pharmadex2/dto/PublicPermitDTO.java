package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class PublicPermitDTO extends AllowValidation {
	private long permitDataID=0;															//ID of the main concept in the permit data
	private long historyID=0;																	//ID of the current history record
	private String title="";																		// name of the permit from the dictionary
	private String description="";															// description of the permit
	private List<ThingDTO> application = new ArrayList<ThingDTO>();	// allowed data pages from the application
	private List<ThingDTO> applHistory = new ArrayList<ThingDTO>();	//alloed data pages from the application history

	public long getPermitDataID() {
		return permitDataID;
	}

	public void setPermitDataID(long permitDataID) {
		this.permitDataID = permitDataID;
	}

	public long getHistoryID() {
		return historyID;
	}

	public void setHistoryID(long historyID) {
		this.historyID = historyID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ThingDTO> getApplication() {
		return application;
	}

	public void setApplication(List<ThingDTO> application) {
		this.application = application;
	}

	public List<ThingDTO> getApplHistory() {
		return applHistory;
	}

	public void setApplHistory(List<ThingDTO> applHistory) {
		this.applHistory = applHistory;
	}
	
}
