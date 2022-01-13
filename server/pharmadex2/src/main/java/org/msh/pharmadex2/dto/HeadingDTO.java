package org.msh.pharmadex2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * HEading value
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class HeadingDTO {
	private String value="";
	private String url="";
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
