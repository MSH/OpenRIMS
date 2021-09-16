package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Represent a cell on a on-screen form
 * A cell contains components arranged in rows, e.g., address dictionary and street address literal below it
 * Variables are keys in maps literals, dictionaries, documents, etc 
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class LayoutCellDTO extends AllowValidation {
	private List<String> variables = new ArrayList<String>();

	public List<String> getVariables() {
		return variables;
	}

	public void setVariables(List<String> variables) {
		this.variables = variables;
	}
	
	
}
