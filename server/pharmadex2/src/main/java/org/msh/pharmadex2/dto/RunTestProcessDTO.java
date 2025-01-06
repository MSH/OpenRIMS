package org.msh.pharmadex2.dto;

import java.time.LocalDate;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;

/**
 * Run test processes
 */
public class RunTestProcessDTO extends AllowValidation {
	private DictNodeDTO node = new DictNodeDTO();
	private FormFieldDTO<String> application_info = FormFieldDTO.of("");
	private FormFieldDTO<String> applicationurl = FormFieldDTO.of("");
	private FormFieldDTO<String> dataurl = FormFieldDTO.of("");
	private FormFieldDTO<String> prefLabel = FormFieldDTO.of("");
	private FormFieldDTO<String> applicant_email = FormFieldDTO.of("");
	private FormFieldDTO<Long> current_applications = FormFieldDTO.of(0l);
	private FormFieldDTO<Long> repeat = FormFieldDTO.of(1l);
	private FormFieldDTO<Long> year = FormFieldDTO.of(Long.valueOf(LocalDate.now().getYear()));
	private FormFieldDTO<Long> daysonreview = FormFieldDTO.of(1l);
	private TableQtb stages = new TableQtb();
	private String stagesError="";
	
	public DictNodeDTO getNode() {
		return node;
	}
	public void setNode(DictNodeDTO node) {
		this.node = node;
	}
	public FormFieldDTO<String> getApplication_info() {
		return application_info;
	}
	public void setApplication_info(FormFieldDTO<String> application_info) {
		this.application_info = application_info;
	}
	public FormFieldDTO<String> getApplicationurl() {
		return applicationurl;
	}
	public void setApplicationurl(FormFieldDTO<String> applicationurl) {
		this.applicationurl = applicationurl;
	}
	public FormFieldDTO<String> getDataurl() {
		return dataurl;
	}
	public void setDataurl(FormFieldDTO<String> dataurl) {
		this.dataurl = dataurl;
	}
	public FormFieldDTO<String> getPrefLabel() {
		return prefLabel;
	}
	public void setPrefLabel(FormFieldDTO<String> prefLabel) {
		this.prefLabel = prefLabel;
	}
	
	public FormFieldDTO<String> getApplicant_email() {
		return applicant_email;
	}
	public void setApplicant_email(FormFieldDTO<String> applicant_email) {
		this.applicant_email = applicant_email;
	}
	public FormFieldDTO<Long> getCurrent_applications() {
		return current_applications;
	}
	public void setCurrent_applications(FormFieldDTO<Long> current_applications) {
		this.current_applications = current_applications;
	}
	public FormFieldDTO<Long> getRepeat() {
		return repeat;
	}
	public void setRepeat(FormFieldDTO<Long> repeat) {
		this.repeat = repeat;
	}
	public FormFieldDTO<Long> getYear() {
		return year;
	}
	public void setYear(FormFieldDTO<Long> year) {
		this.year = year;
	}
	public FormFieldDTO<Long> getDaysonreview() {
		return daysonreview;
	}
	public void setDaysonreview(FormFieldDTO<Long> daysonreview) {
		this.daysonreview = daysonreview;
	}
	public TableQtb getStages() {
		return stages;
	}
	public void setStages(TableQtb stages) {
		this.stages = stages;
	}
	public String getStagesError() {
		return stagesError;
	}
	public void setStagesError(String stagesError) {
		this.stagesError = stagesError;
	}
	
	
}
