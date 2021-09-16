package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class LayoutRowDTO extends AllowValidation {
	private List<LayoutCellDTO> cells = new ArrayList<LayoutCellDTO>();

	public List<LayoutCellDTO> getCells() {
		return cells;
	}

	public void setCells(List<LayoutCellDTO> cells) {
		this.cells = cells;
	}
	
}
