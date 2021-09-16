package org.msh.pharmadex2.dto;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Any public organization, i.e. NMRA main office, departments, local offices
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class PublicOrgDTO extends AllowValidation {
	//ID of tthe organization
	private long id =0;
	//node in the tree for this organization
	DictNodeDTO node = new DictNodeDTO();
	//dictionaries have been configured for this organization
	Map<String,DictionaryDTO> dictionaries = new HashMap<String, DictionaryDTO>();
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public Map<String, DictionaryDTO> getDictionaries() {
		return dictionaries;
	}
	public void setDictionaries(Map<String, DictionaryDTO> dictionaries) {
		this.dictionaries = dictionaries;
	}
	public DictNodeDTO getNode() {
		return node;
	}
	public void setNode(DictNodeDTO node) {
		this.node = node;
	}
	/**
	 * Create DTO from concept node
	 * @param node
	 * @return
	 */
	public static PublicOrgDTO of(DictNodeDTO node) {
		PublicOrgDTO ret = new PublicOrgDTO();
		ret.setNode(node);
		return ret;
	}
	
}
