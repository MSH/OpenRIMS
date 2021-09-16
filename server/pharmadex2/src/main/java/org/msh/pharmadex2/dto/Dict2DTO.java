package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Responsible for two dictionaries related each other
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Dict2DTO extends AllowValidation{
	private DictionaryDTO masterDict = new DictionaryDTO();
	private DictionaryDTO slaveDict = new DictionaryDTO();
	public DictionaryDTO getMasterDict() {
		return masterDict;
	}
	public void setMasterDict(DictionaryDTO masterDict) {
		this.masterDict = masterDict;
	}
	public DictionaryDTO getSlaveDict() {
		return slaveDict;
	}
	public void setSlaveDict(DictionaryDTO slaveDict) {
		this.slaveDict = slaveDict;
	}
	
}
