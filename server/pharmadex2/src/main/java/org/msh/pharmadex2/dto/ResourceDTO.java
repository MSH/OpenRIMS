package org.msh.pharmadex2.dto;

import java.util.List;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Responsible for resource
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ResourceDTO extends AllowValidation {
	private TableQtb table = new TableQtb();
	private Long selected = 0l;
	private long nodeId=0;
	private String varName="";
	private FormFieldDTO<String> url= FormFieldDTO.of("");
	private FormFieldDTO<String> configUrl= FormFieldDTO.of("");
	private FormFieldDTO<String> description = FormFieldDTO.of("");
	//link to an application
	private long historyId=0;
	//link to a file
	private String fileName="";
	private String mediaType="";
	private String contentDisp="";
	private long fileSize=0;
	private long fileId=0;
	//data from unsaved thing
	private ThingValuesDTO data = new ThingValuesDTO();

	
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}

	public Long getSelected() {
		return selected;
	}
	public void setSelected(Long selected) {
		this.selected = selected;
	}

	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public FormFieldDTO<String> getUrl() {
		return url;
	}
	public void setUrl(FormFieldDTO<String> url) {
		this.url = url;
	}

	public FormFieldDTO<String> getConfigUrl() {
		return configUrl;
	}
	public void setConfigUrl(FormFieldDTO<String> configUrl) {
		this.configUrl = configUrl;
	}
	public FormFieldDTO<String> getDescription() {
		return description;
	}
	public void setDescription(FormFieldDTO<String> description) {
		this.description = description;
	}
	public long getHistoryId() {
		return historyId;
	}
	public void setHistoryId(long historyId) {
		this.historyId = historyId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getMediaType() {
		return mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public long getFileId() {
		return fileId;
	}
	public void setFileId(long fileId) {
		this.fileId = fileId;
	}
	public String getContentDisp() {
		return contentDisp;
	}
	public void setContentDisp(String contentDisp) {
		this.contentDisp = contentDisp;
	}
	public ThingValuesDTO getData() {
		return data;
	}
	public void setData(ThingValuesDTO data) {
		this.data = data;
	}
	
}
