package org.msh.pharmadex2.dto.log;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

public class EventLogDTO extends AllowValidation {
	private String title="";
	private TableQtb eventLog = new TableQtb();
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public TableQtb getEventLog() {
		return eventLog;
	}
	public void setEventLog(TableQtb eventLog) {
		this.eventLog = eventLog;
	}
	
	
	
}
