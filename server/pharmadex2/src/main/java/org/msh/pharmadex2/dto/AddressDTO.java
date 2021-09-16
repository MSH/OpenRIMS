package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Select Address with location on Google Map 
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AddressDTO extends AllowValidation{
	private boolean readOnly=false;
	private DictionaryDTO dictionary = new DictionaryDTO();
	private GisLocationDTO homecenter = new GisLocationDTO();
	
	/** with key '0' - add Location center Nepal */
	//private Map<Long, LocationDTO> locations = new HashMap<Long, LocationDTO>();
	/** with key '0' - add Zoom center Nepal */
	//private Map<Long, Integer> zooms = new HashMap<Long, Integer>();
	/** координаты выбранной точки на карте - для сохранения локации в БД */
	private LocationDTO marker = new LocationDTO();
	//variable name in a thing
	private String varName="";
	//url of this class of addresses
	private String url="";
	//node id of this address
	private long nodeId=0;
	
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
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
	/*public Map<Long, LocationDTO> getLocations() {
		return locations;
	}
	public void setLocations(Map<Long, LocationDTO> locations) {
		this.locations = locations;
	}
	public Map<Long, Integer> getZooms() {
		return zooms;
	}
	public void setZooms(Map<Long, Integer> zooms) {
		this.zooms = zooms;
	}*/
	
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
	
}
