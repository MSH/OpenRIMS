package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Responsible for a data collection
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DataCollectionDTO extends AllowValidation {
	private long nodeId=0;
	private FormFieldDTO<String> url= FormFieldDTO.of("");
	private FormFieldDTO<String> description = FormFieldDTO.of("");
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	public FormFieldDTO<String> getUrl() {
		return url;
	}
	public void setUrl(FormFieldDTO<String> url) {
		this.url = url;
	}
	public FormFieldDTO<String> getDescription() {
		return description;
	}
	public void setDescription(FormFieldDTO<String> description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "DataCollectionDTO [nodeId=" + nodeId + ", url=" + url + ", description=" + description + "]";
	}
	
}
