package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Tile on the content page
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class TilesDTO extends AllowValidation {
	private DictionaryDTO dictionary = new DictionaryDTO();
	private boolean editForm = false;
	private Map<String, TileDTO> tiles = new LinkedHashMap<String, TileDTO>();
	//layout on the screen
	private List<LayoutRowDTO> layout = new ArrayList<LayoutRowDTO>();
	private List<LayoutRowDTO> layoutByFree = new ArrayList<LayoutRowDTO>();

	public DictionaryDTO getDictionary() {
		return dictionary;
	}

	public void setDictionary(DictionaryDTO dictionary) {
		this.dictionary = dictionary;
	}

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

	public List<LayoutRowDTO> getLayoutByFree() {
		return layoutByFree;
	}

	public void setLayoutByFree(List<LayoutRowDTO> layoutByFree) {
		this.layoutByFree = layoutByFree;
	}

	public boolean isEditForm() {
		return editForm;
	}

	public void setEditForm(boolean editForm) {
		this.editForm = editForm;
	}
	
}
