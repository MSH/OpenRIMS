package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Select Address with location on Google Map 
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AddressDTO extends AllowValidation{
	private boolean readOnly=false;
	private boolean changed=false;
	private DictionaryDTO dictionary = new DictionaryDTO();
	private GisLocationDTO homecenter = new GisLocationDTO();
	/** координаты выбранной точки на карте - для сохранения локации в БД */
	private LocationDTO marker = new LocationDTO();
	//variable name in a thing
	private String varName="";
	//url of this class of addresses
	private String url="";
	//node id of this address
	private long nodeId=0;
	private String googleMapApiKey="";
	
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public boolean isChanged() {
		return changed;
	}
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	public DictionaryDTO getDictionary() {
		return dictionary;
	}
	public void setDictionary(DictionaryDTO dictionary) {
		this.dictionary = dictionary;
	}
	public LocationDTO getMarker() {
		return marker;
	}
	public void setMarker(LocationDTO marker) {
		this.marker = marker;
	}
	public String getVarName() {
		return varName;
	}
	public GisLocationDTO getHomecenter() {
		return homecenter;
	}
	public void setHomecenter(GisLocationDTO homecenter) {
		this.homecenter = homecenter;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	public String getGoogleMapApiKey() {
		return googleMapApiKey;
	}
	public void setGoogleMapApiKey(String googleMapApiKey) {
		this.googleMapApiKey = googleMapApiKey;
	}

	
}
