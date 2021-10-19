package org.msh.pharmadex2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Responsible for passing parameters between workflows
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class WorkflowParamDTO {
	private String checklistUrl="";
	private String dataUrl="";
	private String nextWorkflowUrl="";
	public String getChecklistUrl() {
		return checklistUrl;
	}
	public void setChecklistUrl(String checklistUrl) {
		this.checklistUrl = checklistUrl;
	}
	public String getDataUrl() {
		return dataUrl;
	}
	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}
	public String getNextWorkflowUrl() {
		return nextWorkflowUrl;
	}
	public void setNextWorkflowUrl(String nextWorkflowUrl) {
		this.nextWorkflowUrl = nextWorkflowUrl;
	}
	
}
