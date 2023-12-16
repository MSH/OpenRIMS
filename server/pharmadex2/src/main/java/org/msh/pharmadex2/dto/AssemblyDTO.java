package org.msh.pharmadex2.dto;

import java.math.BigDecimal;

import org.msh.pharmadex2.dto.enums.AssistantEnum;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Allows to assembly auxiliary components to Organization, Application, Person Data, etc.
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AssemblyDTO extends AllowValidation {
	private String clazz = "";
	private boolean publicAvailable=false;
	private boolean hideFromApplicant=false;
	private String url="";
	private String dictUrl="";
	private String auxDataUrl="";
	private String propertyName="";
	private boolean required=false;
	private boolean readOnly=false;
	private boolean textArea=false;
	private boolean mult=false;
	private boolean unique=false;
	private boolean prefLabel = false;
	//borders for numbers
	private BigDecimal min = BigDecimal.ZERO;
	private BigDecimal max = BigDecimal.valueOf(100000000L);
	//file types allowed to upload
	private String fileTypes=""; //see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/accept
	//description for help
	private String description="";
	//assistant required
	private AssistantEnum assistant = AssistantEnum.NO;
	
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public boolean isPublicAvailable() {
		return publicAvailable;
	}
	public void setPublicAvailable(boolean publicAvailable) {
		this.publicAvailable = publicAvailable;
	}
	public boolean isHideFromApplicant() {
		return hideFromApplicant;
	}
	public void setHideFromApplicant(boolean hideFromApplicant) {
		this.hideFromApplicant = hideFromApplicant;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public boolean isPrefLabel() {
		return prefLabel;
	}
	public void setPrefLabel(boolean prefLabel) {
		this.prefLabel = prefLabel;
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
	public AssistantEnum getAssistant() {
		return assistant;
	}
	public void setAssistant(AssistantEnum assistant) {
		this.assistant = assistant;
	}
	@Override
	public String toString() {
		return "AssemblyDTO [publicAvailable=" + publicAvailable + ", hideFromApplicant=" + hideFromApplicant + ", url="
				+ url + ", dictUrl=" + dictUrl + ", auxDataUrl=" + auxDataUrl + ", propertyName=" + propertyName
				+ ", required=" + required + ", readOnly=" + readOnly + ", textArea=" + textArea + ", mult=" + mult
				+ ", unique=" + unique + ", prefLabel=" + prefLabel + ", min=" + min + ", max=" + max + ", fileTypes="
				+ fileTypes + ", description=" + description + ", assistant=" + assistant + "]";
	}
	

	
	
}
