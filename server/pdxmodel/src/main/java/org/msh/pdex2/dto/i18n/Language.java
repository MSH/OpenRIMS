package org.msh.pdex2.dto.i18n;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO to pass a single language
 * @author Alex Kurasoff
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Language {
	private String localeAsString; //i.e. en-US. ru-RU
	private String displayName;
	private String flag64=""; //base64 encoded flag
	private String flagSVG=""; //SVG flag
	private String nmraLogo=""; //logo of the NMRA (SVG as well)
	
	public String getLocaleAsString() {
		return localeAsString;
	}
	public void setLocaleAsString(String localeAsString) {
		this.localeAsString = localeAsString;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getFlag64() {
		return flag64;
	}
	public void setFlag64(String flag64) {
		this.flag64 = flag64;
	}
	public String getFlagSVG() {
		return flagSVG;
	}
	public void setFlagSVG(String flagSVG) {
		this.flagSVG = flagSVG;
	}
	public String getNmraLogo() {
		return nmraLogo;
	}
	public void setNmraLogo(String nmraLogo) {
		this.nmraLogo = nmraLogo;
	}
	
	
}
