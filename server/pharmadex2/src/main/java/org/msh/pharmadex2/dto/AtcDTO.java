package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

public class AtcDTO extends AllowValidation{

	private String url = "";
	private String dictUrl="";
	private String varName="";
	private TableQtb table = new TableQtb();
	private TableQtb selectedtable = new TableQtb();
	private boolean readOnly=false;
	//show only selected rows
	private boolean selectedOnly=false;

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

	public TableQtb getSelectedtable() {
		return selectedtable;
	}

	public void setSelectedtable(TableQtb selectedtable) {
		this.selectedtable = selectedtable;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isSelectedOnly() {
		return selectedOnly;
	}

	public void setSelectedOnly(boolean selectedOnly) {
		this.selectedOnly = selectedOnly;
	}
	
	
}
