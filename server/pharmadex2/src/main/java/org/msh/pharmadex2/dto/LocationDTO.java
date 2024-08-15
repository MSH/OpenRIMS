package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Location point on Google Map
 *lat:0, lng:0
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class LocationDTO extends AllowValidation{
	
	private double lat = 0.000000;
	private double lng = 0.000000;
	
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	
	public String gisLocation() {
		return "" + lat + ";" + lng;
	}
	public boolean isEmpty() {
		return getLat()<0.000001 && getLng()<0.000001;
	}
	
	
}
