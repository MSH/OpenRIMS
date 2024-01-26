package org.msh.pharmadex2.dto;

import java.util.LinkedHashMap;
import java.util.Map;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.Validator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MessageDTO extends AllowValidation {
	private long maxFileSize=new Long(0l); //in KBytes
	private TableQtb table = new TableQtb();
	private Long selected = new Long(-1);
	private Map<String, Long> selectedIds = new LinkedHashMap<String, Long>();
	
	private FormFieldDTO<String> search = FormFieldDTO.of("");

	@Validator(above=3,below=255)
	private FormFieldDTO<String> res_key= FormFieldDTO.of("");
	private Map<String, FormFieldDTO<String>> values = new LinkedHashMap<String, FormFieldDTO<String>>();	
	

	public long getMaxFileSize() {
		return maxFileSize;
	}
	public void setMaxFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}

	public Long getSelected() {
		return selected;
	}
	public void setSelected(Long selected) {
		this.selected = selected;
	}
	public FormFieldDTO<String> getRes_key() {
		return res_key;
	}
	public void setRes_key(FormFieldDTO<String> res_key) {
		this.res_key = res_key;
	}
	public Map<String, FormFieldDTO<String>> getValues() {
		return values;
	}
	public void setValues(Map<String, FormFieldDTO<String>> values) {
		this.values = values;
	}
	
	public Map<String, Long> getSelectedIds() {
		return selectedIds;
	}
	public void setSelectedIds(Map<String, Long> selectedIds) {
		this.selectedIds = selectedIds;
	}
	public FormFieldDTO<String> getSearch() {
		return search;
	}
	public void setSearch(FormFieldDTO<String> search) {
		this.search = search;
	}

	
}
