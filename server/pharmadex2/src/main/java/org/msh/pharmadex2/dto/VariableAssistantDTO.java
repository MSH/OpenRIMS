package org.msh.pharmadex2.dto;

import java.util.LinkedHashMap;
import java.util.Map;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class VariableAssistantDTO extends AllowValidation {
	private long currentDataConfID=0l;
	private String currentName="";	//a current name of the variable
	private Map<String, FormFieldDTO<String>> labels = new LinkedHashMap<String, FormFieldDTO<String>>(); //labels on all languages
	private TableQtb table = new TableQtb(); //table for a choice from existing
	
	public long getCurrentDataConfID() {
		return currentDataConfID;
	}
	public void setCurrentDataConfID(long currentDataConfID) {
		this.currentDataConfID = currentDataConfID;
	}
	public String getCurrentName() {
		return currentName;
	}
	public void setCurrentName(String currentName) {
		this.currentName = currentName;
	}
	public Map<String, FormFieldDTO<String>> getLabels() {
		return labels;
	}
	public void setLabels(Map<String, FormFieldDTO<String>> labels) {
		this.labels = labels;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	
	
}
