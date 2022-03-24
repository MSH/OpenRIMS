package org.msh.pharmadex2.dto;

import java.time.LocalDate;

import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Dates interval component
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class IntervalDTO extends AllowValidation{
	private FormFieldDTO<LocalDate> from = FormFieldDTO.of(LocalDate.now());
	private FormFieldDTO<LocalDate> to = FormFieldDTO.of(LocalDate.now());
	private String varname="";
	private boolean readonly=false;
	
	public FormFieldDTO<LocalDate> getFrom() {
		return from;
	}
	public void setFrom(FormFieldDTO<LocalDate> from) {
		this.from = from;
	}
	public FormFieldDTO<LocalDate> getTo() {
		return to;
	}
	public void setTo(FormFieldDTO<LocalDate> to) {
		this.to = to;
	}
	public String getVarname() {
		return varname;
	}
	public void setVarname(String varname) {
		this.varname = varname;
	}
	public boolean isReadonly() {
		return readonly;
	}
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}
	
}
