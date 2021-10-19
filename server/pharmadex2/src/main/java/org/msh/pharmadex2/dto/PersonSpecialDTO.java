package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * DTO to determine the special person, like Pharmacist
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class PersonSpecialDTO extends AllowValidation {
	//Name of the variable in a thing
	private String varName = "";
	//object's data
	private long parentId=0l;
	//person data to store
	private long personDataId=0l;
	//where to store (or stored) part of person data (URL in the configuration)
	private String presonDataUrl="";
	//person selection may be restricted by this URL (dictionary URL in the configuration)
	private String restrictByURl="";
	//where to store person selection
	private long nodeId=0l;
	private String nodeUrl="";
	//the table with all persons
	private TableQtb table = new TableQtb();
	private boolean selected=false;
	private String selectedName = "";
	
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	public long getPersonDataId() {
		return personDataId;
	}
	public void setPersonDataId(long personDataId) {
		this.personDataId = personDataId;
	}
	public String getPresonDataUrl() {
		return presonDataUrl;
	}
	public void setPresonDataUrl(String presonDataUrl) {
		this.presonDataUrl = presonDataUrl;
	}
	public String getRestrictByURl() {
		return restrictByURl;
	}
	public void setRestrictByURl(String restrictByURl) {
		this.restrictByURl = restrictByURl;
	}
	public String getNodeUrl() {
		return nodeUrl;
	}
	public void setNodeUrl(String nodeUrl) {
		this.nodeUrl = nodeUrl;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getSelectedName() {
		return selectedName;
	}
	public void setSelectedName(String selectedName) {
		this.selectedName = selectedName;
	}
	
}
