package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Select a workflow DTO
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ELAssistantSelectDTO {
	private TableQtb table = new TableQtb();
	private String selectedURL="";
	public TableQtb getTable() {
		return table;
	}

	public void setTable(TableQtb table) {
		this.table = table;
	}

	public String getSelectedURL() {
		return selectedURL;
	}

	public void setSelectedURL(String selectedURL) {
		this.selectedURL = selectedURL;
	}
	
}
