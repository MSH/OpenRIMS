package org.msh.pharmadex2.dto;


import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Filters and fields for a particular source of data - applications, classifiers, etc
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DataSourceDetailsDTO extends AllowValidation {
	private String source="";
	private String sourceLabel="";
	private TableQtb filtersTable = new TableQtb();
	private boolean filtersSelectedOnly = false;
	private TableQtb fieldsTable = new TableQtb();
	private boolean fieldsSelectedOnly=false;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getSourceLabel() {
		return sourceLabel;
	}
	public void setSourceLabel(String sourceLabel) {
		this.sourceLabel = sourceLabel;
	}

	public TableQtb getFiltersTable() {
		return filtersTable;
	}
	public void setFiltersTable(TableQtb filtersTable) {
		this.filtersTable = filtersTable;
	}
	public TableQtb getFieldsTable() {
		return fieldsTable;
	}
	public void setFieldsTable(TableQtb fieldsTable) {
		this.fieldsTable = fieldsTable;
	}
	public boolean isFiltersSelectedOnly() {
		return filtersSelectedOnly;
	}
	public void setFiltersSelectedOnly(boolean filtersSelectedOnly) {
		this.filtersSelectedOnly = filtersSelectedOnly;
	}
	public boolean isFieldsSelectedOnly() {
		return fieldsSelectedOnly;
	}
	public void setFieldsSelectedOnly(boolean fieldsSelectedOnly) {
		this.fieldsSelectedOnly = fieldsSelectedOnly;
	}
	

}
