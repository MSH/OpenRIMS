package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Responsible for a single link
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class LinkDTO extends AllowValidation {
	private long ID=0;
	private long objectID=0;
	private long dictITemID=0;
	private String objectLabel="";
	private String objectDescription="";
	private DictionaryDTO dictDto = new DictionaryDTO();
	private String dictLabel="";
	public long getID() {
		return ID;
	}
	public void setID(long iD) {
		ID = iD;
	}
	public String getObjectLabel() {
		return objectLabel;
	}
	public void setObjectLabel(String objectLabel) {
		this.objectLabel = objectLabel;
	}
	
	public String getObjectDescription() {
		return objectDescription;
	}
	public void setObjectDescription(String objectDescription) {
		this.objectDescription = objectDescription;
	}
	public String getDictLabel() {
		return dictLabel;
	}
	public void setDictLabel(String dictLabel) {
		this.dictLabel = dictLabel;
	}
	public long getObjectID() {
		return objectID;
	}
	public void setObjectID(long objectID) {
		this.objectID = objectID;
	}
	
	public long getDictITemID() {
		return dictITemID;
	}
	public void setDictITemID(long dictITemID) {
		this.dictITemID = dictITemID;
	}
	public DictionaryDTO getDictDto() {
		return dictDto;
	}
	public void setDictDto(DictionaryDTO dictDto) {
		this.dictDto = dictDto;
	}

}
