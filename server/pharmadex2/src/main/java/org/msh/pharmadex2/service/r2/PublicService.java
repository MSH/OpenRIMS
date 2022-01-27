package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.r2.ClosureRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.ContentDTO;
import org.msh.pharmadex2.dto.DictNodeDTO;
import org.msh.pharmadex2.dto.LayoutCellDTO;
import org.msh.pharmadex2.dto.LayoutRowDTO;
import org.msh.pharmadex2.dto.TileDTO;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.exception.DataNotFoundException;
import org.msh.pharmadex2.service.common.DtoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
/**
 * Services for supervisor
 * @author alexk
 *
 */
@Service
public class PublicService {
	private static final Logger logger = LoggerFactory.getLogger(PublicService.class);

	@Value( "${pharmadex.country:NNN}" )
	String currentCountry;
	@Autowired
	Messages messages;
	@Autowired
	DtoService dtoServ;
	@Autowired
	ClosureService closureServ;
	@Autowired
	LiteralService literalServ;
	@Autowired
	ClosureRepo closureRepo;
	@Autowired
	DictService dictServ;
	@Autowired
	PubOrgService pubOrgServ;

	@Autowired
	private PublicService publicServ;
	@Autowired
	AssemblyService assemblyServ;

	public ContentDTO buildTiles(ContentDTO data, String pagename) throws ObjectNotFoundException{
		String urlDict = "dictionary.system.tiles";

		data.getTiles().clear();
		data.getLayout().clear();
		//load tiles dictionary
		Concept root = closureServ.loadRoot(urlDict);
		if(root != null) {
			List<Concept> pages = literalServ.loadOnlyChilds(root);
			for(Concept p:pages) {
				if(p.getActive()) {
					DictNodeDTO node = dictServ.createNode(p);
					if(node.fetchPrefLabel().getValue().equalsIgnoreCase(pagename)) {
						List<Concept> children = literalServ.loadOnlyChilds(p);
						for(Concept c:children) {
							if(c.getActive()) {
								node = dictServ.createNode(c);
								TileDTO tile = publicServ.createTile(dictServ.literalsLoad(node));
								tile.setIdentifier(node.getIdentifier());
								tile.setNodeID(node.getNodeId());

								if(tile.getImage().length() > 0 &&
										tile.getNumRow() != -1 && tile.getNumCol() != -1) {
									data.getTiles().put(tile.getTitle(), tile);
								}
							}
						}

						break;
					}
				}
			}

			if(data.getTiles().size() > 0) {
				Map<Integer, List<TileDTO>> mapRow = new HashMap<Integer, List<TileDTO>>();
				for(TileDTO tile:data.getTiles().values()) {
					int r = tile.getNumRow();
					if(mapRow.get(r) == null)
						mapRow.put(r, new ArrayList<TileDTO>());
					mapRow.get(r).add(tile);
				}

				List<Integer> rows = new ArrayList<Integer>(mapRow.keySet());
				Collections.sort(rows, new Comparator<Integer>() {
					@Override
					public int compare(Integer o1, Integer o2) {
						return o1.compareTo(o2);
					}}
						);

				LayoutCellDTO cell = null;
				for(Integer r:rows) {
					LayoutRowDTO row = new LayoutRowDTO();
					cell = new LayoutCellDTO();
					cell.getVariables().add("");
					row.getCells().add(cell);
					cell = new LayoutCellDTO();
					cell.getVariables().add("");
					row.getCells().add(cell);
					cell = new LayoutCellDTO();
					cell.getVariables().add("");
					row.getCells().add(cell);

					List<TileDTO> tiles = mapRow.get(r);
					for(int i = 0; i < tiles.size(); i++) {
						cell = row.getCells().get(tiles.get(i).getNumCol());
						cell.getVariables().set(0, tiles.get(i).getTitle());
					}

					data.getLayout().add(row);
				}
			}
		}
		return data;
	}

	/**
	 * Create data for About Us tile
	 * @param imageUrl
	 * @param title
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws DataNotFoundException 
	 */
	public TileDTO createTile(DictNodeDTO node) throws ObjectNotFoundException {
		TileDTO ret = new TileDTO();
		ret.setEmpty(false);
		Map<String, FormFieldDTO<String>> literals = node.getLiterals();
		ret.setImage(literals.get(LiteralService.ICON_URL).getValue());
		ret.setTitle(node.fetchPrefLabel().getValue());
		ret.setDescription(node.fetchDescription().getValue());
		ret.setMoreLbl(literals.get(LiteralService.MORE_LBL).getValue());
		ret.setMore(literals.get(LiteralService.MORE_URL).getValue());
		ret.setDownload("");
		FormFieldDTO<String> fldLayout=literals.get(LiteralService.LAYOUT);
		if(fldLayout != null) {
			String layout = fldLayout.getValue();
			if(layout != null && layout.length() > 0) {
				String[] l = layout.split(";");
				if(l.length == 2) {
					ret.setNumRow(Integer.valueOf(l[0]));
					ret.setNumCol(Integer.valueOf(l[1]));
				}
			}
		}
		return ret;
	}

	public TileDTO createEmptyTile(String title, int numRow, int numCol) throws ObjectNotFoundException {
		TileDTO ret = new TileDTO();
		ret.setEmpty(true);
		ret.setFree(false);
		ret.setImage("/img/empty.jpg");
		ret.setTitle(title);
		ret.setDescription("");
		ret.setMoreLbl("");
		ret.setMore("");
		ret.setDownload("");
		ret.setNumRow(numRow);
		ret.setNumCol(numCol);
		return ret;
	}

	/**
	 * Create a administrate tab to start job
	 * @return
	 */
	public TileDTO createAdminTile() {
		TileDTO ret = new TileDTO();
		ret.setEmpty(false);
		ret.setFree(false);
		ret.setImage("/img/tiles/specialfeatures.jpg");
		ret.setTitle(messages.get("specialfeatures"));
		ret.setDescription("");
		ret.setMoreLbl(messages.get("start"));
		ret.setMore("/admin#administrate");
		ret.setDownload("");
		ret.setNumRow(0);
		ret.setNumCol(0);
		return ret;
	}

}
