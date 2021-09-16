package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Responsible for a person
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class PersonDTO  extends AllowValidation{
	//dictionary to classify persons
	private String dictUrl="";
	//variable name in a main thing
	private String varName="";
	//list of already defined persons
	private TableQtb table = new TableQtb();
	private boolean required=false;
	private boolean readOnly=false;
	//selected node
	private long nodeId=0;
	//node of thing to which person included
	private long thingNodeId=0;
	public String getDictUrl() {
		return dictUrl;
	}
	public void setDictUrl(String dictUrl) {
		this.dictUrl = dictUrl;
	}
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	public long getThingNodeId() {
		return thingNodeId;
	}
	public void setThingNodeId(long thingNodeId) {
		this.thingNodeId = thingNodeId;
	}
	
}
