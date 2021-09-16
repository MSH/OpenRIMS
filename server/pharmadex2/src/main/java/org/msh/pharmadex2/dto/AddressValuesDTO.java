package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Responsible for address data
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AddressValuesDTO extends AllowValidation {
	private String gisCoordinates="";
	private DictValuesDTO adminUnits = new DictValuesDTO();
	public String getGisCoordinates() {
		return gisCoordinates;
	}
	public void setGisCoordinates(String gisCoordinates) {
		this.gisCoordinates = gisCoordinates;
	}
	public DictValuesDTO getAdminUnits() {
		return adminUnits;
	}
	public void setAdminUnits(DictValuesDTO adminUnits) {
		this.adminUnits = adminUnits;
	}
	@Override
	public String toString() {
		return "AddressValuesDTO [gisCoordinates=" + gisCoordinates + ", adminUnits=" + adminUnits + "]";
	}
	
}
