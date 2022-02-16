package org.msh.pharmadex2.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Allows to assembly auxiliary components to Organization, Application, Person Data, etc.
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AssemblyDTO extends AllowValidation {
	private String url="";
	private String dictUrl="";
	private String auxDataUrl="";
	private String propertyName="";
	private boolean required=false;
	private boolean readOnly=false;
	private boolean textArea=false;
	private boolean mult=false;
	private boolean unique=false;
	private LocalDate minDate=LocalDate.now().minusYears(3);	//default
	private LocalDate maxDate=LocalDate.now().plusYears(3);
	//borders for numbers
	private BigDecimal min = BigDecimal.ZERO;
	private BigDecimal max = BigDecimal.valueOf(100000000L);
	//file types allowed to upload
	private String fileTypes=""; //see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/accept
	//description for help
	private String description="";
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getDictUrl() {
		return dictUrl;
	}
	public void setDictUrl(String dictUrl) {
		this.dictUrl = dictUrl;
	}
	
	public String getAuxDataUrl() {
		return auxDataUrl;
	}
	public void setAuxDataUrl(String auxDataUrl) {
		this.auxDataUrl = auxDataUrl;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public boolean isTextArea() {
		return textArea;
	}
	public void setTextArea(boolean textArea) {
		this.textArea = textArea;
	}
	public boolean isMult() {
		return mult;
	}
	public void setMult(boolean mult) {
		this.mult = mult;
	}
	public boolean isUnique() {
		return unique;
	}
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	

	public BigDecimal getMin() {
		return min;
	}
	public void setMin(BigDecimal min) {
		this.min = min;
	}
	public BigDecimal getMax() {
		return max;
	}
	public void setMax(BigDecimal max) {
		this.max = max;
	}
	
	public String getFileTypes() {
		return fileTypes;
	}
	public void setFileTypes(String fileTypes) {
		this.fileTypes = fileTypes;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "AssemblyDTO [url=" + url + ", propertyName=" + propertyName + ", required=" + required + ", readOnly="
				+ readOnly + ", textArea=" + textArea + ", mult=" + mult + "]";
	}
	
	
}
