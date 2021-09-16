package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * History of an application
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationHistoryDTO extends AllowValidation {
	//current history ID, mandatory
	private long historyId=0;
	//application dictionary node id, mandatory
	private long applDictNodeId=0;
	//application name from the dictionary
	private String applName="";
	//table for a history
	private TableQtb table = new TableQtb();
	//current activity ID
	private String url="";
	//application title
	private String title="";
	//selected column name
	private String  columnName="";
	
	public long getApplDictNodeId() {
		return applDictNodeId;
	}
	public void setApplDictNodeId(long applDictNodeId) {
		this.applDictNodeId = applDictNodeId;
	}
	public String getApplName() {
		return applName;
	}
	public void setApplName(String applName) {
		this.applName = applName;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}

	public long getHistoryId() {
		return historyId;
	}
	public void setHistoryId(long historyId) {
		this.historyId = historyId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
}
