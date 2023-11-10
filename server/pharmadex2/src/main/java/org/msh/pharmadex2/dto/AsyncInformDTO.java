package org.msh.pharmadex2.dto;

import java.time.LocalDateTime;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * To feed the AsyncInform component by data 
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AsyncInformDTO extends AllowValidation{
	//data
	private boolean completed=true;
	private boolean cancelled=false;
	private String title="";
	private String  progressMessage="";
	private long compl=0l;
	private long complOf=0l;
	private int complPercent=0;
	private LocalDateTime startedAt = LocalDateTime.now();
	// client control
	private String cancelAPI="";	//e.g., /api/reassign/applicant/cancel
	private int pollInSeconds=5;	//default progress pooling interval in seconds
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getProgressMessage() {
		return progressMessage;
	}
	public void setProgressMessage(String progressMessage) {
		this.progressMessage = progressMessage;
	}
	public long getCompl() {
		return compl;
	}
	public void setCompl(long compl) {
		this.compl = compl;
	}
	public long getComplOf() {
		return complOf;
	}
	public void setComplOf(long complOf) {
		this.complOf = complOf;
	}
	
	public int getComplPercent() {
		return complPercent;
	}
	public void setComplPercent(int complPercent) {
		this.complPercent = complPercent;
	}
	public LocalDateTime getStartedAt() {
		return startedAt;
	}
	public void setStartedAt(LocalDateTime startedAt) {
		this.startedAt = startedAt;
	}
	public String getCancelAPI() {
		return cancelAPI;
	}
	public void setCancelAPI(String cancelAPI) {
		this.cancelAPI = cancelAPI;
	}
	public int getPollInSeconds() {
		return pollInSeconds;
	}
	public void setPollInSeconds(int pollInSeconds) {
		this.pollInSeconds = pollInSeconds;
	}
	@Override
	public String toString() {
		return "AsyncInformDTO [completed=" + completed + ", cancelled=" + cancelled + ", title=" + title
				+ ", progressMessage=" + progressMessage + ", compl=" + compl + ", complOf=" + complOf
				+ ", complPercent=" + complPercent + ", startedAt=" + startedAt + ", cancelAPI=" + cancelAPI
				+ ", pollInSeconds=" + pollInSeconds + "]";
	}
	
}
