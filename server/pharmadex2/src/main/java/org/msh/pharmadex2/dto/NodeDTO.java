package org.msh.pharmadex2.dto;

import java.util.LinkedHashMap;
import java.util.Map;

import org.msh.pharmadex2.dto.form.AllowValidation;
/**
 * Simple Node. To avoid use huge and inconvenient DictNodeDTO
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class NodeDTO extends AllowValidation {
	private long nodeId=0;
	private String identifier="";
	private String label="";
	private boolean active=false;
	private Map<String,String> literals = new LinkedHashMap<String, String>();
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public Map<String, String> getLiterals() {
		return literals;
	}
	public void setLiterals(Map<String, String> literals) {
		this.literals = literals;
	}
	/**
	 * Fetch prefLabel from literals
	 * @return prefLabel or empty string
	 */
	public String fetchPrefLabel() {
		String ret="";
		String lit = getLiterals().get("prefLabel");
		if(lit!=null) {
			ret=lit;
		}
		return ret;
	}
	/**
	 * Fetch description
	 * @return
	 */
	public String fetchDescription() {
		String ret="";
		String lit = getLiterals().get("description");
		if(lit!=null) {
			ret=lit;
		}
		return ret;
	}
	/**
	 * Quick create
	 * @param id
	 * @param pref
	 * @param descr
	 * @return
	 */
	public static NodeDTO of(long id, String pref, String descr) {
		NodeDTO ret = new NodeDTO();
		ret.setNodeId(id);
		ret.getLiterals().put("prefLabel", pref);
		ret.getLiterals().put("description", descr);
		return ret;
	}
	@Override
	public String toString() {
		return "NodeDTO [nodeId=" + nodeId + ", identifier=" + identifier + ", label=" + label + ", active=" + active
				+ ", literals=" + literals + "]";
	}
	
}
