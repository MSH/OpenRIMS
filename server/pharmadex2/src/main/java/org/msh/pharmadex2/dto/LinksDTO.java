package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Responsible for identified links between objects data
 * The user defined dictionary is for identification
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class LinksDTO extends AllowValidation{
	private long nodeID=0;		//Concept to which the object will be linked
	private String linkUrl="";	//URL of the link to distinct links
	private String varName="";	//variable name to clarify to distinct links with the same URL
	private String objectUrl="";	//to distinct linked objects
	private String dictUrl="";		//dictionary to identify a link
	private TableQtb table = new TableQtb();	//list of objects
	private boolean mult=false;						//do we allow multiply links?
	private boolean readOnly=false;				//is this links read only?
	private boolean required=true;				//should the user link at least one object?
	private boolean copyLiterals=false;			//should prefLabel, altLabel and description be copied to ThingDTO				
	private List<LinkDTO> links = new ArrayList<LinkDTO>();	//already linked
	private long selectedObj=0;					//the currently selected object (concept ID) 
	private LinkDTO selectedLink= new LinkDTO();				//the currently selected link
	private String description="";										//help string
	
	public long getNodeID() {
		return nodeID;
	}
	public void setNodeID(long nodeID) {
		this.nodeID = nodeID;
	}
	
	public String getLinkUrl() {
		return linkUrl;
	}
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public String getObjectUrl() {
		return objectUrl;
	}
	public void setObjectUrl(String objectUrl) {
		this.objectUrl = objectUrl;
	}
	
	public String getDictUrl() {
		return dictUrl;
	}
	public void setDictUrl(String dictUrl) {
		this.dictUrl = dictUrl;
	}
	public boolean isMult() {
		return mult;
	}
	public void setMult(boolean mult) {
		this.mult = mult;
	}
	public boolean isCopyLiterals() {
		return copyLiterals;
	}
	public void setCopyLiterals(boolean copyLiterals) {
		this.copyLiterals = copyLiterals;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public List<LinkDTO> getLinks() {
		return links;
	}
	public void setLinks(List<LinkDTO> links) {
		this.links = links;
	}
	public long getSelectedObj() {
		return selectedObj;
	}
	public void setSelectedObj(long selectedObj) {
		this.selectedObj = selectedObj;
	}
	public LinkDTO getSelectedLink() {
		return selectedLink;
	}
	public void setSelectedLink(LinkDTO selectedLink) {
		this.selectedLink = selectedLink;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
