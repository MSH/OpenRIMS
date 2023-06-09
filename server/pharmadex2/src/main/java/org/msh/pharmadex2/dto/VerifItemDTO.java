package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class VerifItemDTO extends AllowValidation{

	private String url = "";
    private Long applDictNodeId = 0l;
    private Long applID = 0l;
    
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Long getApplDictNodeId() {
		return applDictNodeId;
	}
	public void setApplDictNodeId(Long applDictNodeId) {
		this.applDictNodeId = applDictNodeId;
	}
	public Long getApplID() {
		return applID;
	}
	public void setApplID(Long applID) {
		this.applID = applID;
	}
    
    
}
