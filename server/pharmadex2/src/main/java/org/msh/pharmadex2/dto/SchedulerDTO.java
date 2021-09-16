package org.msh.pharmadex2.dto;

import java.time.LocalDate;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * REsponsible for the scheduler
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class SchedulerDTO extends AllowValidation {
	//visible
	private FormFieldDTO<LocalDate> schedule = FormFieldDTO.of(LocalDate.now());
	private TableQtb table = new TableQtb();
	//invisible
	private long nodeId=0;									//node of this schedule record
	private long applDataID=0;							//applicant data
	private String varName="";							//variable inside the thing
	private String dataUrl="";								//where to store
	private String processUrl="";					//what will be run
	private LocalDate createdAt=LocalDate.now();		//to validate
	
	
	public FormFieldDTO<LocalDate> getSchedule() {
		return schedule;
	}
	public void setSchedule(FormFieldDTO<LocalDate> schedule) {
		this.schedule = schedule;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public String getDataUrl() {
		return dataUrl;
	}
	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}
	public String getProcessUrl() {
		return processUrl;
	}
	public void setProcessUrl(String processUrl) {
		this.processUrl = processUrl;
	}
	public long getApplDataID() {
		return applDataID;
	}
	public void setApplDataID(long applDataID) {
		this.applDataID = applDataID;
	}
	public LocalDate getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	
}
