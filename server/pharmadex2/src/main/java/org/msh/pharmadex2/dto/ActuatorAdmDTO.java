package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ActuatorAdmDTO extends AllowValidation {

	private List<String> keys = new ArrayList<String>();
	private Map<String, FormFieldDTO<String>> literals = new LinkedHashMap<String, FormFieldDTO<String>>();

	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

	public Map<String, FormFieldDTO<String>> getLiterals() {
		return literals;
	}

	public void setLiterals(Map<String, FormFieldDTO<String>> literals) {
		this.literals = literals;
	}
}
