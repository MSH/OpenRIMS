package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Should we start new application or just an activity
 * Resolve historyId if needed
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationOrActivityDTO extends AllowValidation{
	private boolean application=true;	//true  application, false - activity
	private long historyId=0;					// resolved history id
	private long dataId=0;						// application data id 
	private String url="";						// Outdated requirement
	private long applDictNodeId=0l	;		//selected dictionary node
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
	public long getDataId() {
		return dataId;
	}
	public void setDataId(long dataId) {
		this.dataId = dataId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getApplDictNodeId() {
		return applDictNodeId;
	}
	public void setApplDictNodeId(long applDictNodeId) {
		this.applDictNodeId = applDictNodeId;
	}
	@Override
	public String toString() {
		return "ApplicationOrActivityDTO [application=" + application + ", historyId=" + historyId + ", dataId="
				+ dataId + "]";
	}
	
	
}
