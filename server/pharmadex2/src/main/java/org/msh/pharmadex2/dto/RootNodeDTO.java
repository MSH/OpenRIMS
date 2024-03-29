package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.enums.AssistantEnum;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.Validator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 *DTO to create/edit a root node. Mainly for a dictionary
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class RootNodeDTO  extends AllowValidation {
	private long rootId=0;
	@Validator(above=3,below=80)
	private FormFieldDTO<String> url = FormFieldDTO.of("",true,false, AssistantEnum.URL_DICTIONARY_NEW);
	@Validator(above=3,below=80)
	private FormFieldDTO<String> prefLabel = FormFieldDTO.of("");
	private FormFieldDTO<String> description = FormFieldDTO.of("");
	
	private boolean gisvisible = false;
	private FormFieldDTO<String> gisLocation = FormFieldDTO.of("");
	private FormFieldDTO<String> zoom = FormFieldDTO.of("");
	
	public long getRootId() {
		return rootId;
	}
	public void setRootId(long rootId) {
		this.rootId = rootId;
	}
	public FormFieldDTO<String> getUrl() {
		return url;
	}
	public void setUrl(FormFieldDTO<String> url) {
		this.url = url;
	}
	public FormFieldDTO<String> getPrefLabel() {
		return prefLabel;
	}
	public void setPrefLabel(FormFieldDTO<String> prefLabel) {
		this.prefLabel = prefLabel;
	}
	public FormFieldDTO<String> getDescription() {
		return description;
	}
	public void setDescription(FormFieldDTO<String> description) {
		this.description = description;
	}
	
	public boolean isGisvisible() {
		return gisvisible;
	}
	public void setGisvisible(boolean gisvisible) {
		this.gisvisible = gisvisible;
	}
	public FormFieldDTO<String> getGisLocation() {
		return gisLocation;
	}
	public void setGisLocation(FormFieldDTO<String> gisLocation) {
		this.gisLocation = gisLocation;
	}
	public FormFieldDTO<String> getZoom() {
		return zoom;
	}
	public void setZoom(FormFieldDTO<String> zoom) {
		this.zoom = zoom;
	}
	
}
