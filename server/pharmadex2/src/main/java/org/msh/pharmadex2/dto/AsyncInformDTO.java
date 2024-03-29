package org.msh.pharmadex2.dto;

import java.time.LocalDateTime;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * To feed the AsyncInform component by data. The progress bar, etc
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AsyncInformDTO extends AllowValidation{
	//data
	private String processName="";			// name of the process, see const PROCESS_ in AsyncService					
	private boolean completed=true;			// process is completed successfully
	private boolean cancelled=false;			// process is canceled by a user
	private long duration=0l;						//duration of the process
	private String title="";							// title for the AsyncInform.js form
	private String progressMessage="";		//message on the progress bar 
	private String elapsedMessage="";		//message under the progress bar
	private long compl=0l;							//how many items is completed.? It is using by log
	private long complOf=0l;						//how many items in total?It is using by log
	private int complPercent=0;					//percents of completion for the progress bar
	private LocalDateTime startedAt = LocalDateTime.now().minusYears(100);		//when the process is started?
	// client control
	private String cancelAPI="";	//e.g., /api/reassign/applicant/cancel
	private int pollInSeconds=5;	//default progress pooling interval in seconds
	
	
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
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
	
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
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
	
	public String getElapsedMessage() {
		return elapsedMessage;
	}
	public void setElapsedMessage(String elapsedMessage) {
		this.elapsedMessage = elapsedMessage;
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
	/**
	 * Set startedAt once
	 * @param startedAt
	 */
	public void setStartedAt(LocalDateTime startedAt) {
		if(isStartDateEmpty()) {
			this.startedAt = startedAt;
		}
	}
	/**
	 * the started datetime should be initialized once
	 * @return
	 */
	private boolean isStartDateEmpty() {
		return LocalDateTime.now().getYear()-getStartedAt().getYear()>3;
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
