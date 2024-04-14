package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * DTO for HostSchedule component
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class HostScheduleDTO extends AllowValidation {
	private String dictURL="";							// the dictionary with host processes
	private String hostDictionary="";				// name of the dictionary
	private TableQtb table= new TableQtb();
	private int count=0;
	public String getDictURL() {
		return dictURL;
	}
	public void setDictURL(String dictURL) {
		this.dictURL = dictURL;
	}
	
	public String getHostDictionary() {
		return hostDictionary;
	}
	public void setHostDictionary(String hostDictionary) {
		this.hostDictionary = hostDictionary;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
}
