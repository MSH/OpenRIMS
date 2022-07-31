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
	private List<String> keysSLA = new ArrayList<String>();
	private Map<String, FormFieldDTO<Integer>> sla = new LinkedHashMap<String, FormFieldDTO<Integer>>();
	private Map<String, FormFieldDTO<String>> literals = new LinkedHashMap<String, FormFieldDTO<String>>();
	private String linkReport = "";

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

	public List<String> getKeysSLA() {
		return keysSLA;
	}

	public void setKeysSLA(List<String> keysSLA) {
		this.keysSLA = keysSLA;
	}

	public Map<String, FormFieldDTO<Integer>> getSla() {
		return sla;
	}

	public void setSla(Map<String, FormFieldDTO<Integer>> sla) {
		this.sla = sla;
	}

	public String getLinkReport() {
		return linkReport;
	}

	public void setLinkReport(String linkReport) {
		this.linkReport = linkReport;
	}
	
	
}
