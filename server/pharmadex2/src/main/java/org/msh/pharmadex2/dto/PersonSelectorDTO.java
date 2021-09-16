package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Responsible for person selection
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class PersonSelectorDTO extends AllowValidation {
	private long historyId=0l;				//where the all application data is
	private String personUrl="";			//which persons we need to select
	
	private TableQtb table = new TableQtb();
	
	public long getHistoryId() {
		return historyId;
	}
	public void setHistoryId(long historyId) {
		this.historyId = historyId;
	}
	public String getPersonUrl() {
		return personUrl;
	}
	public void setPersonUrl(String personUrl) {
		this.personUrl = personUrl;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	
}
