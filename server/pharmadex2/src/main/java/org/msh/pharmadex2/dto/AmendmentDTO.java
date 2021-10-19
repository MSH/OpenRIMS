package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Responsible for all amendment - related things - select object, etc
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AmendmentDTO extends AllowValidation {
	private String url = "";
	private String varName="";
	private String pattern="";
	private long dataNodeId=0l;
	private TableQtb table = new TableQtb();
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public long getDataNodeId() {
		return dataNodeId;
	}
	public void setDataNodeId(long dataNodeId) {
		this.dataNodeId = dataNodeId;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	
}
