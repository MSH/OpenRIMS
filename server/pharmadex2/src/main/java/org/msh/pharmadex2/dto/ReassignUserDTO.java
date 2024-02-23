package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
/**
 * First usage is in the component ReassignUsers
 * Reassign gmail of an applicant user
 * @author alexk
 *
 */
import org.msh.pharmadex2.dto.form.FormFieldDTO;
public class ReassignUserDTO extends AllowValidation {
	private String execName="";			//NRA executor name
	private TableQtb applicants = new TableQtb();
	private FormFieldDTO<String> reassignTo = FormFieldDTO.of("");
	private TableQtb applications = new TableQtb();
	private TableQtb dataTable = new TableQtb();
	private TableQtb activities = new TableQtb();
	private TableQtb eventLog = new TableQtb();
	

	public String getExecName() {
		return execName;
	}
	public void setExecName(String execName) {
		this.execName = execName;
	}
	public TableQtb getApplicants() {
		return applicants;
	}
	public void setApplicants(TableQtb applicants) {
		this.applicants = applicants;
	}
	public FormFieldDTO<String> getReassignTo() {
		return reassignTo;
	}
	public void setReassignTo(FormFieldDTO<String> reassignTo) {
		this.reassignTo = reassignTo;
	}
	public TableQtb getApplications() {
		return applications;
	}
	public void setApplications(TableQtb applications) {
		this.applications = applications;
	}

	public TableQtb getDataTable() {
		return dataTable;
	}
	public void setDataTable(TableQtb dataTable) {
		this.dataTable = dataTable;
	}
	public TableQtb getActivities() {
		return activities;
	}
	public void setActivities(TableQtb activities) {
		this.activities = activities;
	}
	public TableQtb getEventLog() {
		return eventLog;
	}
	public void setEventLog(TableQtb eventLog) {
		this.eventLog = eventLog;
	}
}
