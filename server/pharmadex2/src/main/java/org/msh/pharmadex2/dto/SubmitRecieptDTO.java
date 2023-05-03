package org.msh.pharmadex2.dto;

import java.time.LocalDate;

import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SubmitRecieptDTO extends AllowValidation {
	private long historyId=0l;
	private FormFieldDTO<LocalDate> submitted_date = FormFieldDTO.of(LocalDate.now());
	private FormFieldDTO<String> reg_processing= FormFieldDTO.of("");
	private FormFieldDTO<String> description= FormFieldDTO.of("");
	private FormFieldDTO<String> prefLabel=FormFieldDTO.of("");
	private FormFieldDTO<String> references=FormFieldDTO.of("");
	private FormFieldDTO<String> office=FormFieldDTO.of("");
	
	public long getHistoryId() {
		return historyId;
	}
	public void setHistoryId(long historyId) {
		this.historyId = historyId;
	}
	
	public FormFieldDTO<LocalDate> getSubmitted_date() {
		return submitted_date;
	}
	public void setSubmitted_date(FormFieldDTO<LocalDate> submitted_date) {
		this.submitted_date = submitted_date;
	}
	public FormFieldDTO<String> getReg_processing() {
		return reg_processing;
	}
	public void setReg_processing(FormFieldDTO<String> reg_processing) {
		this.reg_processing = reg_processing;
	}
	public FormFieldDTO<String> getDescription() {
		return description;
	}
	public void setDescription(FormFieldDTO<String> description) {
		this.description = description;
	}
	public FormFieldDTO<String> getPrefLabel() {
		return prefLabel;
	}
	public void setPrefLabel(FormFieldDTO<String> prefLabel) {
		this.prefLabel = prefLabel;
	}
	public FormFieldDTO<String> getReferences() {
		return references;
	}
	public void setReferences(FormFieldDTO<String> references) {
		this.references = references;
	}
	public FormFieldDTO<String> getOffice() {
		return office;
	}
	public void setOffice(FormFieldDTO<String> office) {
		this.office = office;
	}

}
