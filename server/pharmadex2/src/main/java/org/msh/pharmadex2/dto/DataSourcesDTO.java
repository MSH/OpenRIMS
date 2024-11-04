package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Select source type, and, possibly, stored data source
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DataSourcesDTO extends AllowValidation {
	private TableQtb dataSources = new TableQtb();
	private boolean selectedOnly=false;

	public TableQtb getDataSources() {
		return dataSources;
	}

	public void setDataSources(TableQtb dataSources) {
		this.dataSources = dataSources;
	}

	public boolean isSelectedOnly() {
		return selectedOnly;
	}

	public void setSelectedOnly(boolean selectedOnly) {
		this.selectedOnly = selectedOnly;
	}
	
}
