package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * File resource.
 * Created for ResolverServer
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class FileResourceDTO extends AllowValidation {
	private long nodeId =0;
	private String mime="";
	private byte[] file= new byte[0];
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	public String getMime() {
		return mime;
	}
	public void setMime(String mime) {
		this.mime = mime;
	}
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}
	
}
