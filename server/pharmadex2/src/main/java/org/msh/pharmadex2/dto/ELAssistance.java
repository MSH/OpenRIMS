package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class ELAssistance extends AllowValidation {
	private String expression="";
	private List<ELAssistanceData> data = new ArrayList<ELAssistanceData>();
}
