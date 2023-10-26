package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;

/**
 * DTO for the URL assistant
 * @author alexk
 *
 */
public class URLAssistantDTO extends AllowValidation{
	// assistant parameters
	private String assistant="";
	private boolean select=false;
	//assistant results
	private TableQtb domain = new TableQtb();
	private String selectedDomain = "";
	private TableQtb subDomain=new TableQtb();
	private String selectedSubDomain="";
	private TableQtb urls	= new TableQtb();
	private String selectedUrl="";
	private FormFieldDTO<String> url = FormFieldDTO.of("");
	public String getAssistant() {
		return assistant;
	}
	public void setAssistant(String assistant) {
		this.assistant = assistant;
	}
	public boolean isSelect() {
		return select;
	}
	public void setSelect(boolean select) {
		this.select = select;
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
	
}
