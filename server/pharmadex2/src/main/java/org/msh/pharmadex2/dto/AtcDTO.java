package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;

public class AtcDTO {

	private String title = "ATC codes";
	private TableQtb table = new TableQtb();
	private TableQtb selectedtable = new TableQtb();
	
	private boolean readOnly=false;
	//show only selected rows
	private boolean selectedOnly=false;

	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
