package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.ContentDTO;
import org.msh.pharmadex2.dto.DictNodeDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.LayoutCellDTO;
import org.msh.pharmadex2.dto.LayoutRowDTO;
import org.msh.pharmadex2.dto.TileDTO;
import org.msh.pharmadex2.dto.TilesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible to place content to pages
 * @author alexk
 *
 */
@Service
public class ContentService {

	@Autowired
	private PublicService publicServ;
	@Autowired
	DictService dictServ;
	@Autowired
	AssemblyService assemblyServ;
	@Autowired
	ClosureService closureServ;
	@Autowired
	LiteralService literalServ;

	/**
	 * load tiles data for user page
	 * landing, admin, guest ....
	 * @param data
	 * @return
	 */
	@Transactional
	public ContentDTO loadContent(ContentDTO data, String page) throws ObjectNotFoundException{
		data = publicServ.buildTiles(data, page);
		return data;
	}

	/**
	 * сохраняем компановку страницы с Tiles для конкретной страницы 
	 * Пустые тайлы НЕ сохраняем
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public TilesDTO saveTiles(TilesDTO data) throws ObjectNotFoundException {
		Map<String, TileDTO> tiles = data.getTiles();
		Iterator<String> it = tiles.keySet().iterator();
		while(it.hasNext()) {
			TileDTO tile = tiles.get(it.next());
			if(!tile.isEmpty() && !tile.isFree()) {
				String num = tile.getNumRow() + ";" + tile.getNumCol();
				Concept node = closureServ.loadConceptById(tile.getNodeID());
				node.setIdentifier(tile.getIdentifier());
				literalServ.createUpdateLiteral(LiteralService.LAYOUT, num, node);
			}
		}
		return data;
	}

	@Transactional
	public TilesDTO buildTiles(TilesDTO data) throws ObjectNotFoundException {
		String urlDict = "dictionary.system.tiles";
		DictionaryDTO dict = data.getDictionary();
		dict.setUrl(urlDict);

		if(dict.getPath().size()==1) {
			data.getTiles().clear();
			data.getLayout().clear();
			data.getLayoutByFree().clear();

			// грузим данные для выбранной страницы - словарь не перегружаем
			//long idpage = dict.getPrevSelected().get(dict.getPrevSelected().size() - 1);
			long idpage=dict.getPath().get(0).getId();
			Concept page = closureServ.loadConceptById(idpage);
			if(page != null){
				List<Concept> children = literalServ.loadOnlyChilds(page);
				for(Concept c:children) {
					if(c.getActive()) {
						DictNodeDTO node = dictServ.createNode(c);
						TileDTO tile = publicServ.createTile(dictServ.literalsLoad(node));
						tile.setIdentifier(node.getIdentifier());
						tile.setNodeID(node.getNodeId());

						if(tile.getImage().length() > 0) {
							tile.setFree(!(tile.getNumRow() != -1 && tile.getNumCol() != -1));
							data.getTiles().put(tile.getTitle(), tile);
						}
					}
				}

				if(data.getTiles().size() > 0) {
					updateLayoutFree(data);
					updateLayout(data);
				}
			}
		}else{// иначе - грузим словарь заново
			Concept root = closureServ.loadRoot(urlDict);
			dict.setUrlId(root.getID());
			dict.getPrevSelected().clear();
			dict.setSystem(dictServ.checkSystem(root));//ika
			dict = dictServ.createDictionaryFromRoot(dict, root);
			data.setDictionary(dict);
		}

		return data;
	}

	@Transactional
	public TilesDTO updateTiles(TilesDTO data) throws ObjectNotFoundException {
		if(data.getTiles().size() > 0) {
			updateLayoutFree(data);
			updateLayout(data);
		}
		return data;
	}

	@Transactional
	private void updateLayoutFree(TilesDTO data) throws ObjectNotFoundException {
		data.getLayoutByFree().clear();

		if(data.getTiles().keySet() != null && data.getTiles().keySet().size() > 0){
			// Выберем те у которых free=true
			List<TileDTO> tiles = new ArrayList<TileDTO>();
			Collections.sort(tiles, new Comparator<TileDTO>() {
				@Override
				public int compare(TileDTO o1, TileDTO o2) {
					Integer r1 = o1.getNumRow();
					Integer r2 = o2.getNumRow();
					return r1.compareTo(r2);
				}
			});
			for(TileDTO t:data.getTiles().values()) {
				if(t.isFree())
					tiles.add(t);
			}

			int numC = 0;
			LayoutRowDTO row = null;
			for(int i = 0; i < tiles.size(); i++) {
				int mod = i%2;
				if(mod == 0) {
					numC = 0;
				}else
					numC++;

				if(numC == 0) {
					row = new LayoutRowDTO();
					data.getLayoutByFree().add(row);
				}

				LayoutCellDTO cell = new LayoutCellDTO();
				cell.getVariables().add(tiles.get(i).getTitle());
				row.getCells().add(cell);
			}
		}
	}

	@Transactional
	private void updateLayout(TilesDTO data) throws ObjectNotFoundException {
		data.getLayout().clear();
		Map<String, TileDTO> emptyTiles = new HashMap<String, TileDTO>();
		/**
		 * построим сетку 3х3 из пустых ячеек
		 * потом заполним размеченными тайлами
		 */
		for(int r = 0; r < 3; r++) {
			LayoutRowDTO row = new LayoutRowDTO();
			for(int c = 0; c < 3; c++) {
				LayoutCellDTO cell = new LayoutCellDTO();
				TileDTO empty = publicServ.createEmptyTile("empty" + r + c, r, c);
				cell.getVariables().add(empty.getTitle());
				row.getCells().add(cell);
				emptyTiles.put(empty.getTitle(), empty);
			}
			data.getLayout().add(row);
		}
		// удалим старіе пустышки
		List<String> removeEmpty = new ArrayList<String>();
		for(TileDTO tile:data.getTiles().values()) {
			if(tile.isEmpty())
				removeEmpty.add(tile.getTitle());
			else if(!tile.isFree()) {
				int r = tile.getNumRow();
				int c = tile.getNumCol();
				if(r<3 && c<3) {
					data.getLayout().get(r).getCells().get(c).getVariables().set(0, tile.getTitle());
					emptyTiles.remove("empty" + r + c);
				}
			}
		}
		for(String k:removeEmpty) {
			data.getTiles().remove(k);
		}
		if(emptyTiles.keySet().size() > 0) {
			data.getTiles().putAll(emptyTiles);
		}
	}

	/**
	 * Create a tile "Administrate" for the first time to allow configure tiles, dictionaries etc
	 * @param data
	 * @return
	 */
	public ContentDTO adminTile(ContentDTO data) {
		//admin tile
		TileDTO tile = publicServ.createAdminTile();
		data.getTiles().put("administrate", tile);
		LayoutRowDTO row = new LayoutRowDTO();
		LayoutCellDTO cell = new LayoutCellDTO();
		List<String> variables= new ArrayList<String>();
		variables.add("administrate");

		cell.getVariables().addAll(variables);
		row.getCells().add(cell);
		data.getLayout().add(row);
		return data;
	}

	public ContentDTO accountantTile(ContentDTO data) {
		// TODO Auto-generated method stub
		return null;
	}
}
