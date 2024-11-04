package org.msh.pharmadex2.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.enums.AssistantEnum;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Uniform DTO for any data source
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DataSourceDTO extends AllowValidation {
	private long dataSourceID=0l;
	private FormFieldDTO<String> url = FormFieldDTO.of("", true, false, AssistantEnum.URL_DATA_SOURCE_NEW);
	private FormFieldDTO<String> description = FormFieldDTO.of("");
	private TableQtb dataTypes = new TableQtb();
	private Map<String, List<Long>> filters = new HashMap<String, List<Long>>();
	private Map<String, List<Long>> fields = new HashMap<String, List<Long>>();
	private String sql="";
	private TableQtb testSql = new TableQtb();
	
	public long getDataSourceID() {
		return dataSourceID;
	}
	public void setDataSourceID(long dataSourceID) {
		this.dataSourceID = dataSourceID;
	}

	public FormFieldDTO<String> getUrl() {
		return url;
	}
	public void setUrl(FormFieldDTO<String> url) {
		this.url = url;
	}
	public FormFieldDTO<String> getDescription() {
		return description;
	}
	public void setDescription(FormFieldDTO<String> description) {
		this.description = description;
	}

	public TableQtb getDataTypes() {
		return dataTypes;
	}
	public void setDataTypes(TableQtb dataTypes) {
		this.dataTypes = dataTypes;
	}

	public Map<String, List<Long>> getFilters() {
		return filters;
	}
	public void setFilters(Map<String, List<Long>> filters) {
		this.filters = filters;
	}
	public Map<String, List<Long>> getFields() {
		return fields;
	}
	public void setFields(Map<String, List<Long>> fields) {
		this.fields = fields;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public TableQtb getTestSql() {
		return testSql;
	}
	public void setTestSql(TableQtb testSql) {
		this.testSql = testSql;
	}

}
