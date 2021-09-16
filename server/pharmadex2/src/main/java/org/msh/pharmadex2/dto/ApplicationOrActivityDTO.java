package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Should we start new application or just an activity
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationOrActivityDTO extends AllowValidation{
	private boolean application=true;	//true  application, false - activity
	private long historyId=0;
	public boolean isApplication() {
		return application;
	}
	public void setApplication(boolean application) {
		this.application = application;
	}
	public long getHistoryId() {
		return historyId;
	}
	public void setHistoryId(long historyId) {
		this.historyId = historyId;
	}
	
}
