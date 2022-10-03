package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingLink;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.LinkDTO;
import org.msh.pharmadex2.dto.LinksDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Responsible to manage links between objects, e.g. Medicinal Product->Manufacturer
 * @author alexk
 *
 */
@Service
public class LinkService {
	@Autowired
	private DictService dictServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;

	/**
	 * Create/load links for a thing
	 * @param links 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ThingDTO createLinks(List<AssemblyDTO> links, ThingDTO data) throws ObjectNotFoundException {
		for(AssemblyDTO  aDto : links) {
			String key=aDto.getPropertyName();
			if(data.getLinks().get(key)== null) {
				LinksDTO value = new LinksDTO();
				value.setNodeID(data.getNodeId());
				value.setDictUrl(aDto.getDictUrl());
				value.setLinkUrl(aDto.getUrl());
				value.setMult(aDto.isMult());
				value.setReadOnly(aDto.isReadOnly());
				value.setRequired(aDto.isRequired());
				value.setObjectUrl(aDto.getAuxDataUrl());
				value.setVarName(aDto.getPropertyName());
				value.setDescription(aDto.getDescription());
				data.getLinks().put(key, value);
			}
		}
		//load data to the elements
		for(String key : data.getLinks().keySet()) {
			LinksDTO dto = loadLinks(data.getLinks().get(key), key);
			data.getLinks().put(key,dto);
		}
		return data;
	}
	/**
	 * load object's table and selected links for initial appearance
	 * @param dto
	 * @param varName 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public LinksDTO loadLinks(LinksDTO dto, String varName) throws ObjectNotFoundException {
		dto=loadSelectedLinks(dto, varName);
		dto=loadObjectsTable(dto);
		return dto;
	}

	/**
	 * Load links for a link dto given
	 * @param dto
	 * @param varName 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public LinksDTO loadSelectedLinks(LinksDTO dto, String varName) throws ObjectNotFoundException {
		jdbcRepo.links(dto.getNodeID(), varName);
		List<TableHeader> headlist= jdbcRepo.headersFromSelect("select * from _links",new ArrayList<String>());
		Headers headers = new Headers();
		headers.getHeaders().addAll(headlist);
		for(TableHeader th : headers.getHeaders()) {
			th.setSort(true);
			th.setSortValue(TableHeader.SORT_ASC);
		}
		List<TableRow> rows=jdbcRepo.qtbGroupReport("select * from _links", "", "", headers);
		dto.getLinks().clear();
		for(TableRow row : rows) {
			dto.getLinks().add(loadLink(row.getDbID(), row.getCellByKey("preflabel").getValue(),
					row.getCellByKey("dictpreflabel").getValue()));
		}
		return dto;
	}
	/**
	 * Create a link selected by a user
	 * @param tlID ID of thinglinks
	 * @param prefLabel
	 * @param dictPrefLabel
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private LinkDTO loadLink(long tlID, String prefLabel, String dictPrefLabel) throws ObjectNotFoundException {
		LinkDTO ret = new LinkDTO();
		ThingLink tl = boilerServ.thingLink(tlID);
		if(tl.getDictItem()!=null) {
			ret.setDictLabel(dictPrefLabel);
			ret.setDictITemID(tl.getDictItem().getID());
		}
		ret.setID(tlID);
		ret.setObjectID(tl.getLinkedObject().getID());
		ret.setObjectLabel(prefLabel);
		return ret;
	}

	/**
	 * Active objects for selection by URL
	 * @param dto
	 * @return
	 */
	@Transactional
	public LinksDTO loadObjectsTable(LinksDTO dto) {
		dto.getTable().setSelectable(true);
		jdbcRepo.reporting_objects(dto.getObjectUrl(), "ACTIVE");
		if(dto.getTable().getHeaders().getHeaders().size()==0) {
			dictServ.createHeaders(dto.getTable().getHeaders(), false);
			dto.getTable().getHeaders().setPageSize(10);
		}
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from reporting_objects", "","", dto.getTable().getHeaders());
		if(dto.getSelectedObj()>0) {
			// set a page with the a currently selected object
			int pages=dto.getTable().getHeaders().getPages();
			int pageSize=dto.getTable().getHeaders().getPageSize();
			for(int page=1;page<=pages;page++) {
				List<TableRow> rows1=TableHeader.fetchPage(rows, page, pageSize);
				for(TableRow row : rows1) {
					if(row.getDbID()==dto.getSelectedObj()) {
						dto.getTable().getHeaders().setPage(page);
					}
				}
			}
		}
		TableQtb.tablePage(rows, dto.getTable());
		selectObjects(dto);
		return dto;
	}
	/**
	 * Select objects on the current page
	 * @param dto
	 * @return
	 */
	public LinksDTO selectObjects(LinksDTO dto) {
		List<Long> selected = new ArrayList<Long>();
		for(LinkDTO link : dto.getLinks()) {
			selected.add(new Long(link.getObjectID()));
		}
		for(TableRow row : dto.getTable().getRows()) {
			if(selected.contains(row.getDbID())){
				row.setSelected(true);
			}else {
				row.setSelected(false);
			}
		}
		return dto;
	}


	/**
	 * Select/de-select a row in the table of objects 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public LinksDTO selectRow(LinksDTO data) throws ObjectNotFoundException {
		//select or de-select
		boolean deselect=false;
		for(LinkDTO link : data.getLinks()) {
			deselect = deselect || link.getObjectID()==data.getSelectedObj();
			if(deselect) {
				data.getLinks().remove(link);
				break;
			}
		}
		if(!deselect) {								//select
			LinkDTO link = new LinkDTO();
			link.setObjectID(data.getSelectedObj());
			link.setObjectLabel(selectedObjectLabel(data));
			// the pnext ste
			if(data.getDictUrl().length()>0) {
				DictionaryDTO dict = new DictionaryDTO();
				dict.setUrl(data.getDictUrl());
				link.setDictDto(dictServ.createDictionary(dict));
				data.setSelectedLink(link);
			}else {
				data.getLinks().add(link);
			}
		}
		data=selectObjects(data);
		return data;
	}
	/**
	 * Preferred label for the selected object
	 * @param data
	 * @return
	 */
	private String selectedObjectLabel(LinksDTO data) {
		String ret="";
		for(TableRow row :data.getTable().getRows()) {
			if(row.getDbID()==data.getSelectedObj()) {
				ret=row.getRow().get(0).getValue();
				break;
			}
		}
		return ret;
	}
	/**
	 * Store all links in the link
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public LinksDTO save(LinksDTO data) throws ObjectNotFoundException {
		if(data.getNodeID()>0) {
			Concept node=closureServ.loadConceptById(data.getNodeID());
			Thing thing = boilerServ.thingByNode(node);
			thing.getThingLinks().clear();
			for(LinkDTO link : data.getLinks()) {
				if(link.getObjectID()>0) {
					ThingLink tl =new ThingLink();
					//general fields
					tl.setVarName(data.getVarName());
					tl.setLinkUrl(data.getLinkUrl());
					if(data.getDictUrl().length()>0) {
						tl.setDictUrl(data.getDictUrl());
						if(link.getDictITemID()>0) {
							Concept dictItem = closureServ.loadConceptById(link.getDictITemID());
							tl.setDictItem(dictItem);
						}
					}
					Concept objConc = closureServ.loadConceptById(link.getObjectID());
					tl.setLinkedObject(objConc);
					//store to thing
					thing.getThingLinks().add(tl);
				}
			}
			thing=boilerServ.saveThing(thing);
		}
		return data;
	}

	/**
	 * User has selected a classifier for the object
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public LinksDTO dictionarySelect(LinksDTO data) throws ObjectNotFoundException {
		// get selected item
		Set<Long> selected = dictServ.selectedItems(data.getSelectedLink().getDictDto());
		if(selected.size()==1) {
			Concept dictItem = closureServ.loadConceptById(selected.iterator().next());
			if (literalServ.isLeaf(dictItem)){
				data.getSelectedLink().setDictITemID(dictItem.getID());
				//data.getSelectedLink().setDictLabel(literalServ.readPrefLabel(dictItem));
				List<OptionDTO> path = data.getSelectedLink().getDictDto().getPath();
				List<String> pathl = new ArrayList<String>();
				for(OptionDTO opt : path) {
					pathl.add(opt.getCode());
				}
				pathl.add(literalServ.readPrefLabel(dictItem));
				data.getSelectedLink().setDictLabel(String.join(",", pathl));
				data.getLinks().add(data.getSelectedLink());
				data.setSelectedLink(new LinkDTO());
			}
		}
		data=selectObjects(data);
		return data;
	}
	/**
	 * Get list of main concepts of linked objects
	 * @param var concept of the page on which "links" components is placed
	 * @param varName 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<ThingLink> list(Concept var, String varName) throws ObjectNotFoundException {
		List<ThingLink> ret = new ArrayList<ThingLink>();
		LinksDTO dto = new LinksDTO();
		dto.setNodeID(var.getID());
		dto=loadLinks(dto, varName);
		for(LinkDTO ld :dto.getLinks()) {
			ret.add(boilerServ.thingLink(ld.getID()));
		}
		return ret;
	}

}
