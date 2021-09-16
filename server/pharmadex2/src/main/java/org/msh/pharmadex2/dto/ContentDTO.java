package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * List of tiles in the content area
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ContentDTO  extends AllowValidation{
	private Map<String, TileDTO> tiles = new LinkedHashMap<String, TileDTO>();
	private List<LayoutRowDTO> layout = new ArrayList<LayoutRowDTO>();
	
	public Map<String, TileDTO> getTiles() {
		return tiles;
	}

	public void setTiles(Map<String, TileDTO> tiles) {
		this.tiles = tiles;
	}

	public List<LayoutRowDTO> getLayout() {
		return layout;
	}

	public void setLayout(List<LayoutRowDTO> layout) {
		this.layout = layout;
	}
	
}
