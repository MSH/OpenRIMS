package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GisLocationDTO extends AllowValidation{

	private long id = 0l;
	private LocationDTO center = new LocationDTO();
	private Integer zoom = 0;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public LocationDTO getCenter() {
		return center;
	}
	public void setCenter(LocationDTO center) {
		this.center = center;
	}
	public Integer getZoom() {
		return zoom;
	}
	public void setZoom(Integer zoom) {
		this.zoom = zoom;
	}

}
