package org.msh.pharmadex2.dto;

import java.util.LinkedHashMap;
import java.util.Map;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class VariableAssistantDTO extends AllowValidation {
	//select form
	private String currentName="";	//a current name of the variable
	private TableQtb table = new TableQtb(); //table for a choice from existing
	//edit form
	private FormFieldDTO<String> varName = FormFieldDTO.of("");
	private Map<String, FormFieldDTO<String>> labels = new LinkedHashMap<String, FormFieldDTO<String>>(); //labels on all languages
	public int usageCount=0;	//quantity of usage of this name as a variable name 
	
	public String getCurrentName() {
		return currentName;
	}
	public void setCurrentName(String currentName) {
		this.currentName = currentName;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	public FormFieldDTO<String> getVarName() {
		return varName;
	}
	public void setVarName(FormFieldDTO<String> varName) {
		this.varName = varName;
	}
	public Map<String, FormFieldDTO<String>> getLabels() {
		return labels;
	}
	public void setLabels(Map<String, FormFieldDTO<String>> labels) {
		this.labels = labels;
	}
	public int getUsageCount() {
		return usageCount;
	}
	public void setUsageCount(int usageCount) {
		this.usageCount = usageCount;
	}
	
	
}
