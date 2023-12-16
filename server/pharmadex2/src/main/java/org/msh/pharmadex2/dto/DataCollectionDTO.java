package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.enums.AssistantEnum;
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
	private FormFieldDTO<String> url= FormFieldDTO.of("",false,false,AssistantEnum.URL_DATA_NEW);
	private FormFieldDTO<String> description = FormFieldDTO.of("");
	//reporting
	private TableQtb table = new TableQtb();
	private String varName="";
	
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
	
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	@Override
	public String toString() {
		return "DataCollectionDTO [nodeId=" + nodeId + ", url=" + url + ", description=" + description + "]";
	}
	
}
