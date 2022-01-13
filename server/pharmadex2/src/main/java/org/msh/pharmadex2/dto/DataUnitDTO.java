package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import org.msh.pharmadex2.dto.form.AllowValidation;
/**
 * Data unit for select amendment
 * @author alexk
 *
 */
public class DataUnitDTO extends AllowValidation {
	private long nodeId=0l;					//data node
	private String label="";
	private String mainLabel="";
	private String url="";
	private List<ThingDTO> path= new ArrayList<ThingDTO>(); //all things loaded for nodeId
	
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getMainLabel() {
		return mainLabel;
	}
	public void setMainLabel(String mainLabel) {
		this.mainLabel = mainLabel;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public List<ThingDTO> getPath() {
		return path;
	}
	public void setPath(List<ThingDTO> path) {
		this.path = path;
	}
	@Override
	public String toString() {
		return "DataUnitDTO [nodeId=" + nodeId + ", label=" + label + ", mainLabel=" + mainLabel + ", url=" + url + "]";
	}
	
	
}
