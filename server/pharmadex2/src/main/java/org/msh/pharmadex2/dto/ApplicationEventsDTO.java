package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationEventsDTO extends AllowValidation {
	private long appldataid=0;
	private TableQtb table= new TableQtb();
	private long selected=0;
	private String eventDate="";
	private String title="";
	private String leftTitle="";
	private String rigthTitle="";
	private ThingDTO leftThing = new ThingDTO();
	private ThingDTO rightThing = new ThingDTO();
	private CheckListDTO checklist = new CheckListDTO();
	public long getAppldataid() {
		return appldataid;
	}
	public void setAppldataid(long appldataid) {
		this.appldataid = appldataid;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	public long getSelected() {
		return selected;
	}
	public void setSelected(long selected) {
		this.selected = selected;
	}
	
	public String getEventDate() {
		return eventDate;
	}
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLeftTitle() {
		return leftTitle;
	}
	public void setLeftTitle(String leftTitle) {
		this.leftTitle = leftTitle;
	}
	public String getRigthTitle() {
		return rigthTitle;
	}
	public void setRigthTitle(String rigthTitle) {
		this.rigthTitle = rigthTitle;
	}
	public ThingDTO getLeftThing() {
		return leftThing;
	}
	public void setLeftThing(ThingDTO leftThing) {
		this.leftThing = leftThing;
	}
	public ThingDTO getRightThing() {
		return rightThing;
	}
	public void setRightThing(ThingDTO rightThing) {
		this.rightThing = rightThing;
	}
	public CheckListDTO getChecklist() {
		return checklist;
	}
	public void setChecklist(CheckListDTO checklist) {
		this.checklist = checklist;
	}
	
}
