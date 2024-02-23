package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * A Data Table in ELAssistance
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ELAssistanceData extends AllowValidation {
	private String breadCrumb="";
	private TableQtb table= new TableQtb();
	private long selectedID=0;
	public String getBreadCrumb() {
		return breadCrumb;
	}
	public void setBreadCrumb(String breadCrumb) {
		this.breadCrumb = breadCrumb;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	public long getSelectedID() {
		return selectedID;
	}
	public void setSelectedID(long selectedID) {
		this.selectedID = selectedID;
	}
	
}
