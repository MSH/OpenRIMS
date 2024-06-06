package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Element of a dictionary or the concept. 
 * Contains as much as possible data regarding concept as well as a table for list of concepts
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DictNodeDTO  extends AllowValidation {
	private static final String DESCRIPTION = "description";
	private static final String PREF_LABEL = "prefLabel";
	private String url="";																		//url of the dictionary
	private long parentId=0;																	//id of the parent element
	private long nodeId=0;																				//id of this element
	//map with variables on the same language
	Map<String, FormFieldDTO<String>> literals = new LinkedHashMap<String, FormFieldDTO<String>>();
	//labels for prefLabel and description node fields if need to rewrite
	private Map<String,String> mainLabels = new HashMap<String, String>();
	//table to display a level
	private TableQtb table = new TableQtb();
	//How we  will call it
	private List<String> title= new ArrayList<String>();
	//is it a leaf or a branch 
	private boolean leaf=false;
	private DictionaryDTO dict= new DictionaryDTO();

	public long getParentId() {
		return parentId;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public long getNodeId() {
		return nodeId;
	}

	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

	public FormFieldDTO<String> fetchPrefLabel() {
		return getLiterals().get(PREF_LABEL);
	}
	public void changePrefLabel(FormFieldDTO<String> prefLabel) {
		getLiterals().put(PREF_LABEL, prefLabel);
	}
	public FormFieldDTO<String> fetchDescription() {
		return getLiterals().get(DESCRIPTION);
	}
	public void changeDescription(FormFieldDTO<String> description) {
		getLiterals().put(DESCRIPTION, description);
	}
	public Map<String, FormFieldDTO<String>> getLiterals() {
		if(literals.get(PREF_LABEL)==null) {
			literals.put(PREF_LABEL, FormFieldDTO.of(""));
		}
		if(literals.get(DESCRIPTION)==null) {
			literals.put(DESCRIPTION, FormFieldDTO.of(""));
		}
		return literals;
	}
	public void setLiterals(Map<String, FormFieldDTO<String>> literals) {
		this.literals = literals;
	}
	
	public Map<String, String> getMainLabels() {
		return mainLabels;
	}

	public void setMainLabels(Map<String, String> mainLabels) {
		this.mainLabels = mainLabels;
	}

	public TableQtb getTable() {
		return table;
	}

	public void setTable(TableQtb table) {
		this.table = table;
	}

	public List<String> getTitle() {
		return title;
	}

	public void setTitle(List<String> title) {
		this.title = title;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	/**
	 * Clean literals values
	 */
	public void cleanLiterals() {
		for(String key : getLiterals().keySet()) {
			getLiterals().get(key).setValue("");
		}
	}

	public DictionaryDTO getDict() {
		return dict;
	}

	public void setDict(DictionaryDTO dict) {
		this.dict = dict;
	}
	
}
