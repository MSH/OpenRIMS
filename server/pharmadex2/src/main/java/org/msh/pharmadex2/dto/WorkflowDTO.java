package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import org.msh.pharmadex2.dto.form.AllowValidation;

public class WorkflowDTO extends AllowValidation {
	long dictNodeId=0;
	String title="";
	//index of the currently selected in the path
	int selected=-1;
	List<ThingDTO> path = new ArrayList<ThingDTO>();
	
	public long getDictNodeId() {
		return dictNodeId;
	}
	public void setDictNodeId(long dictNodeId) {
		this.dictNodeId = dictNodeId;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getSelected() {
		return selected;
	}
	public void setSelected(int selected) {
		this.selected = selected;
	}
	public List<ThingDTO> getPath() {
		return path;
	}
	public void setPath(List<ThingDTO> path) {
		this.path = path;
	}

	
}
