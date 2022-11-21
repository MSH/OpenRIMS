package org.msh.pharmadex2.dto;

import org.springframework.core.io.Resource;

/**
 * images by footer
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class SystemImageDTO{
	
	private Resource res = null;
	private String filename = "";
	private String mediatype = "";
	
	public Resource getResource() {
		return res;
	}
	public void setResource(Resource fres) {
		this.res = fres;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getMediatype() {
		return mediatype;
	}
	public void setMediatype(String mediatype) {
		this.mediatype = mediatype;
	}
}
