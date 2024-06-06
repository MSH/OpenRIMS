package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import org.msh.pdex2.dto.table.TableQtb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ELAssistantBuildDTO {
	private String workflowURL = "";
	private String thisPageURL="";	//for "this" data source
	private String el="";
	private TableQtb sources = new TableQtb();
	private TableQtb representations = new TableQtb();
	private List<String> source= new ArrayList<String>();
	private List<String> clazz = new ArrayList<String>();
	private String representation="";
	
	public String getWorkflowURL() {
		return workflowURL;
	}
	public void setWorkflowURL(String workflowURL) {
		this.workflowURL = workflowURL;
	}
	public String getThisPageURL() {
		return thisPageURL;
	}
	public void setThisPageURL(String thisPageURL) {
		this.thisPageURL = thisPageURL;
	}
	public TableQtb getSources() {
		return sources;
	}
	public void setSources(TableQtb sources) {
		this.sources = sources;
	}
	public TableQtb getRepresentations() {
		return representations;
	}
	public void setRepresentations(TableQtb representations) {
		this.representations = representations;
	}
	public String getEl() {
		return el;
	}
	public void setEl(String el) {
		this.el = el;
	}
	public List<String> getSource() {
		return source;
	}
	public void setSource(List<String> source) {
		this.source = source;
	}
	
	public List<String> getClazz() {
		return clazz;
	}
	public void setClazz(List<String> clazz) {
		this.clazz = clazz;
	}
	public String getRepresentation() {
		return representation;
	}
	public void setRepresentation(String representation) {
		this.representation = representation;
	}

}
