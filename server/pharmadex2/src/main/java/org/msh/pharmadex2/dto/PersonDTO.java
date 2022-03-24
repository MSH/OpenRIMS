package org.msh.pharmadex2.dto;

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
	// to mark amendments
	private boolean changed=false;
	private String url=""; //url of persons
	//dictionary to classify persons
	private String dictUrl="";
	//variable name in a main thing
	private String varName="";
	//list of already defined persons
	private TableQtb table = new TableQtb();
	//list of persons to remove in amendment
	@Deprecated
	private TableQtb rtable = new TableQtb();
	private boolean required=false;
	private boolean readOnly=false;
	//selected node
	private long nodeId=0;
	//node of thing to which person included
	private long thingNodeId=0;
	//ID of amended node
	private long amendedNodeId=0l;
	
	
	public boolean isChanged() {
		return changed;
	}
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
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
	
	public TableQtb getRtable() {
		return rtable;
	}
	public void setRtable(TableQtb rtable) {
		this.rtable = rtable;
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
	public long getAmendedNodeId() {
		return amendedNodeId;
	}
	public void setAmendedNodeId(long amendedNodeId) {
		this.amendedNodeId = amendedNodeId;
	}
	
}
