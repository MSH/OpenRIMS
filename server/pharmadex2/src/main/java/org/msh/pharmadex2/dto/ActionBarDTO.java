package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class ActionBarDTO extends AllowValidation{
	List<String> actions = new ArrayList<String>();

	public List<String> getActions() {
		return actions;
	}

	public void setActions(List<String> actions) {
		this.actions = actions;
	}
	
	
}
