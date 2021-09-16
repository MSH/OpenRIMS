package org.msh.pharmadex2.dto.mock;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Only for Mock!
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ChoiceDTO extends AllowValidation {
	private String url="";
	private long dictNodeId=0l;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getDictNodeId() {
		return dictNodeId;
	}
	public void setDictNodeId(long dictNodeId) {
		this.dictNodeId = dictNodeId;
	}
	
}
