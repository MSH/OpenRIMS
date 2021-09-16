package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Table of applications accessible in a url given
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationsDTO extends AllowValidation {
	private String url="";
	private String title="";
	private String description = "";
	private long dictItemId=0;
	private TableQtb table = new TableQtb();
	private TableQtb scheduled = new TableQtb();

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getDictItemId() {
		return dictItemId;
	}

	public void setDictItemId(long dictItemId) {
		this.dictItemId = dictItemId;
	}

	public TableQtb getTable() {
		return table;
	}

	public void setTable(TableQtb table) {
		this.table = table;
	}

	public TableQtb getScheduled() {
		return scheduled;
	}

	public void setScheduled(TableQtb scheduled) {
		this.scheduled = scheduled;
	}
	
}
