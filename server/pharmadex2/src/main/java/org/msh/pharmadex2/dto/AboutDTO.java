package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;

/**
 * Release and build date for footer
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class AboutDTO extends AllowValidation {
	private String buildTime="";
	private String release="";
	public String getBuildTime() {
		return buildTime;
	}
	public void setBuildTime(String buildTime) {
		this.buildTime = buildTime;
	}
	public String getRelease() {
		return release;
	}
	public void setRelease(String release) {
		this.release = release;
	}
	
	
}
