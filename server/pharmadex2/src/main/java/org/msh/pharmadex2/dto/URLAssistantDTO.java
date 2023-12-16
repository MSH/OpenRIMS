package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.enums.AssistantEnum;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;

/**
 * DTO for the URL assistant
 * @author alexk
 *
 */
public class URLAssistantDTO extends AllowValidation{
	// assistant parameters
	private AssistantEnum assistant=AssistantEnum.NO;
	private String oldValue="";
	private String title="";
	//assistant results
	private TableQtb domain = new TableQtb();
	private String selectedDomain = "";
	private TableQtb subDomain=new TableQtb();
	private String selectedSubDomain="";
	private TableQtb urls	= new TableQtb();
	private String selectedUrl="";
	private FormFieldDTO<String> url = FormFieldDTO.of("");
	private DictionaryDTO previewDict = new DictionaryDTO();
	private ThingDTO previewThing = new ThingDTO();
	private String previewOther="";


	public AssistantEnum getAssistant() {
		return assistant;
	}
	public void setAssistant(AssistantEnum assistant) {
		this.assistant = assistant;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public TableQtb getDomain() {
		return domain;
	}
	public void setDomain(TableQtb domain) {
		this.domain = domain;
	}
	public String getSelectedDomain() {
		return selectedDomain;
	}
	public void setSelectedDomain(String selectedDomain) {
		this.selectedDomain = selectedDomain;
	}
	public TableQtb getSubDomain() {
		return subDomain;
	}
	public void setSubDomain(TableQtb subDomain) {
		this.subDomain = subDomain;
	}
	public String getSelectedSubDomain() {
		return selectedSubDomain;
	}
	public void setSelectedSubDomain(String selectedSubDomain) {
		this.selectedSubDomain = selectedSubDomain;
	}
	public TableQtb getUrls() {
		return urls;
	}
	public void setUrls(TableQtb urls) {
		this.urls = urls;
	}
	public String getSelectedUrl() {
		return selectedUrl;
	}
	public void setSelectedUrl(String selectedUrl) {
		this.selectedUrl = selectedUrl;
	}
	public FormFieldDTO<String> getUrl() {
		return url;
	}
	public void setUrl(FormFieldDTO<String> url) {
		this.url = url;
	}
	public DictionaryDTO getPreviewDict() {
		return previewDict;
	}
	public void setPreviewDict(DictionaryDTO previewDict) {
		this.previewDict = previewDict;
	}
	public ThingDTO getPreviewThing() {
		return previewThing;
	}
	public void setPreviewThing(ThingDTO previewThing) {
		this.previewThing = previewThing;
	}
	public String getPreviewOther() {
		return previewOther;
	}
	public void setPreviewOther(String previewOther) {
		this.previewOther = previewOther;
	}
	
}
