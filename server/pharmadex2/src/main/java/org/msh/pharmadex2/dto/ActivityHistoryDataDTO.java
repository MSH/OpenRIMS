package org.msh.pharmadex2.dto;

import java.time.LocalDate;

import org.msh.pharmadex2.dto.form.FormFieldDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Get data from any activity in the history
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ActivityHistoryDataDTO {
	private long historyId=0l;
	private String workflow="";
	private String activity="";
	private String prefLabel="";
	private FormFieldDTO<LocalDate> global_startdate = FormFieldDTO.of(LocalDate.now());
	private FormFieldDTO<LocalDate> completeddate = FormFieldDTO.of(LocalDate.now());
	private FormFieldDTO<String> expert = FormFieldDTO.of("");
	private boolean completed = false;
	private FormFieldDTO<String> notes = FormFieldDTO.of("");
	private long activityDataId = 0l;
	
	public long getHistoryId() {
		return historyId;
	}
	public void setHistoryId(long historyId) {
		this.historyId = historyId;
	}
	public String getWorkflow() {
		return workflow;
	}
	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public String getPrefLabel() {
		return prefLabel;
	}
	public void setPrefLabel(String prefLabel) {
		this.prefLabel = prefLabel;
	}
	public FormFieldDTO<LocalDate> getGlobal_startdate() {
		return global_startdate;
	}
	public void setGlobal_startdate(FormFieldDTO<LocalDate> global_startdate) {
		this.global_startdate = global_startdate;
	}
	public FormFieldDTO<LocalDate> getCompleteddate() {
		return completeddate;
	}
	public void setCompleteddate(FormFieldDTO<LocalDate> completeddate) {
		this.completeddate = completeddate;
	}
	public FormFieldDTO<String> getExpert() {
		return expert;
	}
	public void setExpert(FormFieldDTO<String> expert) {
		this.expert = expert;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	public FormFieldDTO<String> getNotes() {
		return notes;
	}
	public void setNotes(FormFieldDTO<String> notes) {
		this.notes = notes;
	}
	public long getActivityDataId() {
		return activityDataId;
	}
	public void setActivityDataId(long activityDataId) {
		this.activityDataId = activityDataId;
	}
	
}
