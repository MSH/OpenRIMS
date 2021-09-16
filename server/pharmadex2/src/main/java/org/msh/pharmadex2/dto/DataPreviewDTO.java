package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Responsible for form data preview
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DataPreviewDTO extends AllowValidation {
	private long nodeId=0;
	private ThingDTO thing = new ThingDTO();
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	public ThingDTO getThing() {
		return thing;
	}
	public void setThing(ThingDTO thing) {
		this.thing = thing;
	}
	

}
