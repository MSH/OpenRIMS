package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Responsible for a files in a thing
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class FileDTO extends AllowValidation{
	//url under which a file should be saved
	private String url="";
	//url of a dictionary to get a root and then a level
	private String dictUrl="";
	//selected from tthe table node id in a dictionary
	private long dictNodeId=0;
	//thing to get all linked files
	private long thingNodeId=0;
	//variable name in the thing
	private String varName="";
	//only for download?
	private boolean readOnly=false;
	//table to place query result
	private TableQtb table = new TableQtb();
	//************* File ***********************************************************
	private String accept="*";						//filter for file types see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/accept
	private boolean editor = false;				//editor mode, if true
	private long nodeId=0l;						//Concept for the file
	private Long fileSize = new Long(0l); //in KBytes
	private String fileName = "";
	private String fileDescription="";			//description from dictionary
	private String mediaType = "";
	//stored uploads for the current session
	private Map<Long,Long> linked = new LinkedHashMap<Long, Long>(); // (dictNodeId, nodeId)

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDictUrl() {
		return dictUrl;
	}
	public void setDictUrl(String dictUrl) {
		this.dictUrl = dictUrl;
	}
	
	public long getDictNodeId() {
		return dictNodeId;
	}
	public void setDictNodeId(long dictNodeId) {
		this.dictNodeId = dictNodeId;
	}
	public long getThingNodeId() {
		return thingNodeId;
	}
	public void setThingNodeId(long thingNodeId) {
		this.thingNodeId = thingNodeId;
	}
	
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	
	public String getAccept() {
		return accept;
	}
	public void setAccept(String accept) {
		this.accept = accept;
	}
	public boolean isEditor() {
		return editor;
	}
	public void setEditor(boolean editor) {
		this.editor = editor;
	}
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	public Long getFileSize() {
		return fileSize;
	}
	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileDescription() {
		return fileDescription;
	}
	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}
	public String getMediaType() {
		return mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public Map<Long, Long> getLinked() {
		return linked;
	}
	public void setLinked(Map<Long, Long> linked) {
		this.linked = linked;
	}	
	
}
