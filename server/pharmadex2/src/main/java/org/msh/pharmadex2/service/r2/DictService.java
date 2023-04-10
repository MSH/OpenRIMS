package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.msh.pdex2.dto.i18n.Language;
import org.msh.pdex2.dto.i18n.Languages;
import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.old.Query;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingDict;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.common.QueryRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AddressDTO;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.DictNodeDTO;
import org.msh.pharmadex2.dto.DictionariesDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.GisLocationDTO;
import org.msh.pharmadex2.dto.RootNodeDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.dto.mock.ChoiceDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.DtoService;
import org.msh.pharmadex2.service.common.EntityService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Responsible for dictionaries - DictNodeDTO and DictionaryDTO
 * DictNodeDTO represents a concept for any object
 * DictionaryDTO represents dictionaries, i.e. special structure that allows to classify any object or subject 
 * @author alexk
 *
 */
@Service
public class DictService {
	private static final Logger logger = LoggerFactory.getLogger(DictService.class);
	@Autowired
	ClosureService closureServ;
	@Autowired
	DtoService dtoServ;
	@Autowired
	LiteralService literalServ;
	@Autowired
	EntityService entityServ;
	@Autowired
	JdbcRepository jdbcRepo;
	@Autowired
	BoilerService boilerServ;
	@Autowired
	ValidationService validServ;
	@Autowired
	Messages messages;
	@Autowired
	AssemblyService assembServ;
	@Autowired
	QueryRepository queryRep;

	/**
	 * Save the node.
	 * @param node the element
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DictNodeDTO save(DictNodeDTO node) throws ObjectNotFoundException {
		if(node.getParentId()<=0 && node.getNodeId()<=0 && node.getUrl().length()==0) {
			throw new ObjectNotFoundException("save. Dictionary URL, Parent ID and element ID are not defined",logger);
		}
		node = validServ.node(node,"", true);
		if(node.isValid()) {
			Concept concept = node(node);
			concept=literalServ.saveFields(node.getLiterals(),concept);
			node=createNode(concept);
		}
		return node;
	}
	/**
	 * Load an node of dictionary by concept id
	 * @param id node id
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DictNodeDTO loadNodeById(long id) throws ObjectNotFoundException {
		Concept node = closureServ.loadConceptById(id);
		DictNodeDTO ret = createNode(node);
		return ret;
	}

	/**
	 * Delete a branch from this node and the node itself
	 * @param node
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DictNodeDTO deleteBranch(DictNodeDTO node) throws ObjectNotFoundException {
		if(node.getNodeId()>0) {
			Concept concept = closureServ.loadConceptById(node.getNodeId());
			closureServ.removeNode(concept);
			node.setNodeId(0);
		}
		return node;
	}


	/**
	 * Create a node from a concept
	 * @param concept
	 * @return node
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DictNodeDTO createNode(Concept concept) throws ObjectNotFoundException {
		concept = closureServ.loadConceptById(concept.getID());
		DictNodeDTO ret = new DictNodeDTO();
		ret.setIdentifier(concept.getIdentifier());
		ret.setLeaf(literalServ.isLeaf(concept));
		ret.setLiterals(dtoServ.readAllLiterals(ret.getLiterals(), concept));
		ret.setNodeId(concept.getID());
		ret.setParentId(closureServ.getParent(concept).getID());
		ret.setTable(loadTable(concept,ret.getTable(),new ArrayList<Long>(),false, false));
		ret.setTitle(createTitle(ret));
		ret.setUrl(closureServ.getUrlByNode(concept));
		ret.setValid(true);
		return ret;
	}

	/**
	 * Create or load dictionary table
	 * Excludes not active items that are not selected
	 * @param parentNode parent node 
	 * @param table table to load
	 * @param selectedIds list of previous selected 
	 * @param selectedOnly load selected only
	 * @param readOnly for readOnly tables
	 * @return 
	 */
	@Transactional
	public TableQtb loadTable(Concept parentNode, TableQtb table, List<Long> selectedIds, boolean selectedOnly, boolean readOnly) {
		//logger.trace("loadTable{");
		if(table.getHeaders().getHeaders().size()==0) {
			table.setHeaders(createHeaders(table.getHeaders(), readOnly));
		}
		if(parentNode!=null) {
			jdbcRepo.prepareDictionaryLevel(parentNode.getID());
			table.getHeaders().getHeaders().add(TableHeader.instanceOf("Active", TableHeader.COLUMN_LONG));
			List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from _dictlevel", "", "", table.getHeaders());
			List<TableRow> rows1 = new ArrayList<TableRow>();
			for(TableRow row : rows ) {
				int active = row.getCellByKey("Active").getIntValue();
				if(active>0 || selectedIds.contains(row.getDbID())) {
					row.getRow().remove(2);		//we will not need Active
					rows1.add(row);
				}
			}
			table.getHeaders().getHeaders().remove(2);		//we will not need Active
			List<TableRow> rowsToTable = new ArrayList<TableRow>();			
			if(selectedOnly) {
				for(TableRow row : rows1) {
					if(selectedIds.contains(row.getDbID())) {
						rowsToTable.add(row);
					}
				}
			}else {
				rowsToTable.addAll(rows1);
			}
			TableQtb.tablePage(rowsToTable, table);
		}
		//logger.trace("}");
		return table;
	}


	/**
	 * Create dictionary table headers
	 * @param ret 
	 * @param readOnly 
	 * @return
	 */
	public Headers createHeaders(Headers ret, boolean readOnly) {
		ret.getHeaders().clear();
		ret.setPageSize(20);
		int firstHeader=TableHeader.COLUMN_LINK;
		if(readOnly) {
			firstHeader=TableHeader.COLUMN_STRING;
		}
		/*ret.getHeaders().add(TableHeader.instanceOf(
				"ID", 
				"ID",
				true,
				true,
				true,
				TableHeader.COLUMN_LONG,
				0));*/
		ret.getHeaders().add(TableHeader.instanceOf(
				"pref", 
				"global_name",
				true,
				true,
				true,
				firstHeader,
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				"description", 
				"description",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		ret= boilerServ.translateHeaders(ret);
		return ret;
	}

	/**
	 * Create dictionary table headers
	 * @param ret 
	 * @param readOnly 
	 * @return
	 */
	public Headers createHeadersAllDict(Headers ret) {
		ret.getHeaders().clear();
		ret.getHeaders().add(TableHeader.instanceOf(
				"pref", 
				"global_name",
				true,
				true,
				true,
				TableHeader.COLUMN_LINK,
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				"url", 
				"Url",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				"description", 
				"description",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		ret.getHeaders().get(1).setSortValue(TableHeader.SORT_ASC);
		ret= boilerServ.translateHeaders(ret);
		return ret;
	}

	/*public Headers createHeadersAllDictShort(Headers ret) {
		ret.getHeaders().clear();
		ret.getHeaders().add(TableHeader.instanceOf(
				"pref", 
				"global_name",
				true,
				true,
				true,
				TableHeader.COLUMN_LINK,
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				"url", 
				"Url",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		ret= boilerServ.translateHeaders(ret);
		return ret;
	}*/

	/**
	 * Load or create a concept for the node
	 * @param node
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public Concept node(DictNodeDTO node) throws ObjectNotFoundException {
		Concept concept = new Concept();
		if(node.getNodeId()>0) {
			//try load by node, if will not be loaded, it is a new
			concept = closureServ.loadConceptById(node.getNodeId());
		}else {
			if(node.getParentId()>0) {
				//try create new node under the parent
				Concept parent=closureServ.loadConceptById(node.getParentId());
				concept = closureServ.save(concept);
				concept.setIdentifier(concept.getID()+"");
				concept = closureServ.saveToTree(parent, concept);
			}else {
				if(node.getUrl() != null && node.getUrl().length()>0) {
					//try to create new node under the root
					Concept root = closureServ.loadRoot(node.getUrl());
					concept = closureServ.save(concept);
					concept.setIdentifier(concept.getID()+"");
					concept = closureServ.saveToTree(root, concept);
				}else {
					throw new ObjectNotFoundException("loadByConcept. Cannot load or create organization. Node, Parent node and URL are not defined",logger);
				}
			}
		}
		return concept;
	}
	/**
	 * Load literals or create them, but do not save to the database
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public DictNodeDTO literalsLoad(DictNodeDTO data) throws ObjectNotFoundException {
		data.getLiterals().clear();
		//create empty literals
		data.getLiterals().putAll(literalServ.mandatoryLiterals());
		List<AssemblyDTO> auxLit = assembServ.auxLiterals(data.getUrl());
		for(AssemblyDTO adto : auxLit) {
			data.getLiterals().put(adto.getPropertyName(),FormFieldDTO.of(""));
		}
		if(data.getNodeId()>0) {
			//load existing literals
			Concept node = closureServ.loadConceptById(data.getNodeId());
			dtoServ.readAllLiterals(data.getLiterals(),node);
			data.setLeaf(literalServ.isLeaf(node));
		}

		return data;
	}



	/**
	 * Create a list of string for breadcrumbs
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<String> createTitle(DictNodeDTO data) throws ObjectNotFoundException {
		List<String> ret = new ArrayList<String>();
		ret.add(messages.getUrl(data.getUrl()));
		if(data.getParentId()>0) {
			Concept node = closureServ.loadConceptById(data.getParentId());
			ret.add(literalServ.readValue("prefLabel", node));
		}
		return ret;
	}

	/**
	 * Load root or defined level of a dictionary
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DictNodeDTO loadLevel( DictNodeDTO data) throws ObjectNotFoundException {
		TableQtb table = data.getTable();
		Concept parentNode = parentNode(data);
		if(parentNode != null) {
			table=loadTable(parentNode, table,new ArrayList<Long>(),false, false);
			data.setTable(table);
		}
		data.getTitle().clear();
		data.getTitle().addAll(createTitle(data));
		return data;
	}
	/**
	 * Load dictionary level as a list of OptionDTO
	 * @param parentNode - thhe parent concept of a level
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<OptionDTO> loadLevelAsOptions(Concept parentNode) throws ObjectNotFoundException {
		List<OptionDTO> ret = new ArrayList<OptionDTO>();
		TableQtb table = new TableQtb();
		table.getHeaders().setPageSize(Integer.MAX_VALUE);
		table = loadTable(parentNode, new TableQtb(), new ArrayList<Long>(),false,false);
		for(TableRow row : table.getRows()) {
			OptionDTO odto = new OptionDTO();
			odto.setId(row.getDbID());
			odto.setCode(row.getCellValue("pref", table.getHeaders()));
			odto.setDescription(row.getCellValue("description", table.getHeaders()));
			ret.add(odto);
		}
		return ret;
	}

	/**
	 * Load parent concept of the node
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Concept parentNode(DictNodeDTO data) throws ObjectNotFoundException {
		Concept parentNode=null;
		if(data.getParentId()>0) {
			parentNode=closureServ.loadConceptById(data.getParentId());
		}else {
			parentNode=closureServ.loadRoot(data.getUrl());
		}
		return parentNode;
	}

	/**
	 * Load root of dict given
	 * @param node
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Concept loadRoot(DictNodeDTO node) throws ObjectNotFoundException {
		Concept ret = closureServ.loadRoot(node.getUrl());
		return ret;
	}
	/**
	 * Path is empty, selection is empty, "brand new" dictionary
	 * @param ret
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DictionaryDTO createDictionaryFromRoot(DictionaryDTO ret, Concept root) throws ObjectNotFoundException {
		//ika
		if(root==null) {
		//Concept 
		root = closureServ.loadRoot(ret.getUrl());
		ret.setSystem(checkSystem(root));
		}
		if(ret.isReadOnly()) {
			ret.setSelectedOnly(true);
		}
		ret.setTable(loadTable(root, ret.getTable(),ret.getPrevSelected(),ret.isSelectedOnly(), ret.isReadOnly()));
		String home=literalServ.readValue("prefLabel", root);
		ret.setHome(home);
		ret = reviseSelected(root, ret);
		ret = selectRows(ret);
		return ret;
	}

	/**
	 * Select rows in the table, based on prevSelected
	 * @param ret
	 * @return
	 */
	private DictionaryDTO selectRows(DictionaryDTO ret) {
		for(TableRow row :ret.getTable().getRows()) {
			if(ret.getPrevSelected().contains(row.getDbID())) {
				row.setSelected(true);
			}else {
				row.setSelected(false);
			}
		}
		return ret;
	}

	/**
	 * Decide do we need selected?
	 * @param root
	 * @param ret
	 * @return
	 */
	@Transactional
	private DictionaryDTO reviseSelected(Concept parentChoice, DictionaryDTO ret) {
		if(parentChoice != null) {
			TableQtb level = new TableQtb();
			level.setHeaders(createHeaders(level.getHeaders(),ret.isReadOnly()));
			level.getHeaders().setPageSize(Integer.MAX_VALUE);
			level = loadTable(parentChoice,level, ret.getPrevSelected(), false, false);
			List<Long> selected = new ArrayList<Long>();
			for(TableRow row :level.getRows()) {
				if(ret.getPrevSelected().contains(row.getDbID())) {
					selected.add(row.getDbID());
				}
			}
			ret.getPrevSelected().clear();
			ret.getPrevSelected().addAll(selected);
		}
		return ret;
	}
	/**
	 * Create a dictionary from selected value(s)
	 * @param selected
	 * @param ret
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DictionaryDTO createDictionaryFromSelected(List<Long> selected, DictionaryDTO ret) throws ObjectNotFoundException {
		if(selected.size()>0) {
			Concept any = closureServ.loadConceptById(selected.get(0));
			ret.getPath().clear();
			ret.getPath().addAll(pathFromNode(any, ret.getUrl()));
			if(!ret.isReadOnly()) {
				List<Concept> childs = literalServ.loadOnlyChilds(any);
				if(childs != null && childs.size() > 0){
					ret.getPath().add(optionFromNode(any));
					ret.getPrevSelected().clear();
				}
			}

			//create a table to select on this previous level
			if(ret.getPath().size()>0) {
				OptionDTO prev = ret.getPath().get(ret.getPath().size()-1);
				Concept prevNode = closureServ.loadConceptById(prev.getId());
				if(ret.isReadOnly()) {
					ret.setSelectedOnly(true);
				}
				//ret.setTable(loadTable(prevNode, ret.getTable(),ret.getPrevSelected(),ret.isSelectedOnly(), ret.isReadOnly()));
				ret.setTable(loadTable(prevNode, ret.getTable(), selected, ret.isSelectedOnly(), ret.isReadOnly()));
				//create a selection from parent level
				OptionDTO opt = optionFromNode(prevNode);
				ret.getSelection().setValue(opt);
			}else {
				ret = createDictionaryFromRoot(ret, null);
			}
		}
		ret= selectRows(ret);
		ret.getTable().setSelectable(!ret.isReadOnly());
		return ret;
	}

	/**
	 * Create path to a node given 
	 * @param nodeId
	 * @param dictUrl
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public List<OptionDTO> pathFromNode(Concept node, String dictUrl) throws ObjectNotFoundException {
		List<Concept> concPath = closureServ.loadParents(node);
		List<OptionDTO> ret = new ArrayList<OptionDTO>();
		for(Concept conc : concPath) {
			if(!conc.getIdentifier().equalsIgnoreCase(dictUrl)) {
				if(conc.getID()!=node.getID()) {
					ret.add(optionFromNode(conc));
				}
			}
		}
		return ret;
	}
	/**
	 * Create OptionDTO (single) from a dictioanry node
	 * @param node
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private OptionDTO optionFromNode(Concept node) throws ObjectNotFoundException {
		String code = literalServ.readValue("prefLabel", node);
		String description=literalServ.readValue("description", node);
		return OptionDTO.of(node.getID(), code, description);
	}


	/**
	 * Create dictionary from selected on the previous level
	 * @param selected
	 * @param ret
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DictionaryDTO createDictionaryFromPrevSelected(List<Long> selected, DictionaryDTO ret) throws ObjectNotFoundException {
		if(selected.size()>0) {
			ret.getSelection().getValue().getOptions().clear();
			for(Long id : selected) {
				if(id>0) {
					Concept node = closureServ.loadConceptById(id);
					OptionDTO opt = optionFromNode(node);
					ret.getSelection().getValue().getOptions().add(opt);
				}
			}
			if(ret.getSelection().getValue().getOptions().size()>0) {
				OptionDTO opt = ret.getSelection().getValue().getOptions().get(0);
				ret.getSelection().getValue().setCode(opt.getCode());
				ret.getSelection().getValue().setDescription(opt.getDescription());
				ret.getSelection().getValue().setId(opt.getId());
				Concept any = closureServ.loadConceptById(opt.getId());
				ret.getPath().clear();
				ret.getPath().addAll(pathFromNode(any, ret.getUrl()));
				ret.getPath().add(opt);
				ret=loadTable(ret);
			}
		}
		return ret;
	}
	/**
	 * Load - reload a dictionary
	 * We already have ready to use dictionary.
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DictionaryDTO load(DictionaryDTO data) throws ObjectNotFoundException {
		if(data.getSelection().getValue().getId()>0) {
			//do we need reload?
			Concept selectedNode=closureServ.loadConceptById(data.getSelection().getValue().getId());
			if(data.getPrevSelected().size()>0) {
				Concept parentNode=closureServ.loadConceptById(data.getPrevSelected().get(0));
				if(selectedNode.getID()!=parentNode.getID()) {
					data.getPrevSelected().clear();
				}
			}
			data.setTable(loadTable(selectedNode, data.getTable(), data.getPrevSelected(), data.isSelectedOnly(), false));
			List<OptionDTO> path= pathFromNode(selectedNode,data.getUrl());
			//add selected to the path
			OptionDTO lastOpt = new OptionDTO();
			if(path.size()>0) {
				lastOpt = path.get(path.size()-1);
			}
			OptionDTO nextOpt = data.getSelection().getValue();
			if(lastOpt.getId() != nextOpt.getId() && nextOpt.getId()!=0) {
				path.add(nextOpt);
			}
			data.setPath(path);
		}
		return data;
	}

	/**
	 * load the root level of a dictionary defined by URL 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public DictionaryDTO rootDictionary(DictionaryDTO data) throws ObjectNotFoundException {
		DictionaryDTO ret = data.cloneImportant();
		ret=createDictionaryFromRoot(ret, null);
		return ret;
	}
	/**
	 * load next level of a dictionary based on values of the previous one
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public DictionaryDTO nextDictionary(DictionaryDTO data) throws ObjectNotFoundException {
		if(data.getPrevSelected().size()>0) {
			DictionaryDTO ret = data.cloneImportant();
			List<Long> prevSelected = new ArrayList<Long>();
			prevSelected.addAll(ret.getPrevSelected());
			ret.getPrevSelected().clear();
			ret = createDictionaryFromPrevSelected(prevSelected, ret);
			return ret;
		}else {
			return data;
		}
	}
	/**
	 * Create a dictionary from the path selection
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public DictionaryDTO loadPath(DictionaryDTO data) throws ObjectNotFoundException {
		if(data.getPathSelected().getId()>0) {
			DictionaryDTO ret = data.cloneImportant();
			Concept node = closureServ.loadConceptById(ret.getPathSelected().getId());
			ret.setPath(pathFromNode(node, ret.getUrl()));
			OptionDTO lastPath = optionFromNode(node);
			ret.getPath().add(lastPath);
			ret.getSelection().setValue(optionFromNode(node));
			ret.setTable(loadTable(node,data.getTable(),data.getPrevSelected(),data.isSelectedOnly(), false));
			ret=reviseSelected(node, ret);
			ret=selectRows(ret);
			return ret;
		}else {
			return data;
		}
	}
	/**
	 * reload only a table
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public DictionaryDTO loadTable(DictionaryDTO data) throws ObjectNotFoundException {
		Concept node = closureServ.loadRoot(data.getUrl());
		if(data.getSelection().getValue().getId()>0) {
			node = closureServ.loadConceptById(data.getSelection().getValue().getId());
		}
		data.setTable(loadTable(node, data.getTable(),data.getPrevSelected(),data.isSelectedOnly(), false));
		data = selectRows(data);
		return data;
	}

	/**
	 * Suspend usage of this node
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public DictNodeDTO nodeSuspend(DictNodeDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		data=validServ.node(data,"", true);
		if(data.isValid() && data.getNodeId()>0) {
			Concept conc=closureServ.loadConceptById(data.getNodeId());
			conc.setActive(false);
			conc = closureServ.save(conc);
			/*
			recycle= closureServ.loadRoot(SystemService.RECYCLE);
			jdbcRepo.moveSubTree(conc, recycle);
			*/
		}
		if(data.getNodeId()==0 && data.isValid()) {
			data.setValid(false);
			data.setIdentifier(messages.get("global_fail"));
		}
		return data;
	}

	/**
	 * Get all configured dictionaries
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 * 
	 * data.selectId > 0 && data.editor - edit fields dictionary
	 * data.selectId > 0 && data.editor=false - show list item dictionary
	 * data.selectId == 0 && data.editor - create new fields dictionary
	 * 
	 * 
	 */
	@Transactional
	public DictionariesDTO all(DictionariesDTO data) throws ObjectNotFoundException {
		boolean reloadTable = true;

		if(data.getSelectId() > 0) {
			DictionaryDTO dict = new DictionaryDTO();
			Concept root = closureServ.loadConceptById(data.getSelectId());
			dict.setUrlId(root.getID());
			dict.setUrl(root.getIdentifier());

			dict.setSystem(checkSystem(root));//ika
			data.setSelect(createDictionaryFromRoot(dict, root));
			reloadTable = false;
		}else if(data.getSelectId() == 0 && data.isEditor()) {//create new fields dictionary
			DictionaryDTO dict = new DictionaryDTO();
			dict.setUrl("");
			data.setSelect(dict);
			reloadTable = false;
		}

		if(reloadTable)
			loadTableAllDictionaries(data);
		
		return data;
	}

	public DictionariesDTO loadTableAllDictionaries(DictionariesDTO data) {
		data.getTable().setSelectable(true);
		if(data.getTable().getHeaders().getHeaders().size()==0) {
			data.getTable().setHeaders(createHeadersAllDict(data.getTable().getHeaders()));
		}
		Optional<Query> optional = queryRep.findByKey("alldictionary");
		if(optional.isPresent()) {
			Query query = optional.get();
			String lang = messages.getCurrentLocaleStr().toUpperCase();
			String where = "url LIKE 'dictionary.%' and lang like '" + lang + "'";

			List<TableRow> rows = jdbcRepo.qtbGroupReport(query.getSql(), "", where, data.getTable().getHeaders());
			data.getTable().getHeaders().setPageSize(100);
			TableQtb.tablePage(rows, data.getTable());
			//data.setTable(boilerServ.translateRows(data.getTable()));
		}

		return data;
	}

	/**
	 * load or create a root node
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public RootNodeDTO rootNode(RootNodeDTO data) throws ObjectNotFoundException {
		Concept root = loadRootByRootNode(data);
		if(root.getID()>0) {
			data.getUrl().setValue(root.getIdentifier());
			String prefLabel=literalServ.readValue("prefLabel", root);
			String description=literalServ.readValue("description", root);
			data.getPrefLabel().setValue(prefLabel);
			data.getDescription().setValue(description);
			
			if(root.getIdentifier().equals(SystemService.DICTIONARY_ADMIN_UNITS)) {
				data.setGisvisible(true);
				String gisloc = literalServ.readValue(LiteralService.GIS_LOCATION, root);
				String z = literalServ.readValue(LiteralService.ZOMM, root);
				data.getGisLocation().setValue(gisloc);
				data.getZoom().setValue(z);
			}else {
				data.setGisvisible(false);
				data.getGisLocation().setValue("");
				data.getZoom().setValue("");
			}
		}
		return data;
	}
	/**
	 * Load root concept using root node DTO
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Concept loadRootByRootNode(RootNodeDTO data) throws ObjectNotFoundException {
		Concept root = new Concept();
		if(data.getRootId()>0) {
			root=closureServ.loadConceptById(data.getRootId());
		}else {
			if(data.getUrl().getValue().length()>0) {
				root=closureServ.loadRoot(data.getUrl().getValue());
			}
		}
		return root;
	}

	/**
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public RootNodeDTO rootNodeSave(RootNodeDTO data) throws ObjectNotFoundException {
		data=validServ.rootNode(data);
		if(data.isValid()) {
			Concept root = loadRootByRootNode(data);
			root.setIdentifier(data.getUrl().getValue());
			literalServ.createUpdateLiteral("prefLabel", data.getPrefLabel().getValue(), root);
			literalServ.createUpdateLiteral("description", data.getDescription().getValue(), root);
			
			if(root.getIdentifier().equals(SystemService.DICTIONARY_ADMIN_UNITS)) {
				literalServ.createUpdateLiteral(LiteralService.GIS_LOCATION, data.getGisLocation().getValue(), root);
				literalServ.createUpdateLiteral(LiteralService.ZOMM, data.getZoom().getValue(), root);
			}
		}
		return data;
	}
	/**
	 * Create a dictionary from the DTO froo selected or from root
	 * @param dictionaryDTO
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DictionaryDTO createDictionary(DictionaryDTO data) throws ObjectNotFoundException {
		Concept root=closureServ.loadRoot(data.getUrl());
		data.setSystem(checkSystem(root));
		data.setMaxDepth(jdbcRepo.dict_depth(data.getUrl()));
		String home=literalServ.readValue("prefLabel", root);
		data.setHome(home);
		//determine the algorithm 
		if(data.getPrevSelected().size()>0) {
			data= createDictionaryFromSelected(data.getPrevSelected(), data);
		}else {
			data=createDictionaryFromRoot(data, root);
		}
		return data;
	}
	/**
	 * Fill out list of the current selections.
	 * Known usage 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DictionaryDTO createCurrentSelections(DictionaryDTO data) throws ObjectNotFoundException {
		data.getCurrentSelections().clear();
		for(Long itemId :data.getPrevSelected()) {
			Concept item = closureServ.loadConceptById(itemId);
			String prefLabel=literalServ.readPrefLabel(item);
			String description=literalServ.readDescription(item);
			OptionDTO opt= new OptionDTO();
			opt.setId(item.getID());
			opt.setCode(prefLabel);
			opt.setDescription(description);
			data.getCurrentSelections().add(opt);
		}
		return data;
	}
	@Transactional
	public String getGISlocation(String url) throws ObjectNotFoundException {
		Concept root=closureServ.loadRoot(url);
		String center = literalServ.readValue(LiteralService.GIS_LOCATION, root);

		return center;
	}

	@Transactional
	public String getZoom(String url) throws ObjectNotFoundException {
		Concept root=closureServ.loadRoot(url);
		String center = literalServ.readValue(LiteralService.ZOMM, root);

		return center;
	}

	/**
	 * Check is this dictionary system?
	 * @param root
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean checkSystem(Concept root) throws ObjectNotFoundException {
		String dicType = literalServ.readValue("type", root);
		return dicType.equalsIgnoreCase("system");
	}
	/**
	 * Create mandatory and aux literals
	 * @param auxLiterals
	 * @param data
	 * @return
	 */
	public DictNodeDTO createLiterals(List<AssemblyDTO> auxLiterals, DictNodeDTO data) {
		data.getLiterals().clear();
		boolean prefLabel=false;
		boolean description=false;
		if(auxLiterals != null) {
			for(AssemblyDTO adto : auxLiterals) {
				prefLabel=prefLabel || adto.getPropertyName().equals("prefLabel");
				description=description || adto.getPropertyName().equals("description");
				data.getLiterals().put(adto.getPropertyName(), FormFieldDTO.of("",adto.isReadOnly(), adto.isTextArea()));
			}
		}
		if(!prefLabel) {
			data.getLiterals().put("prefLabel", FormFieldDTO.of(""));
		}
		if(!description) {
			data.getLiterals().put("description", FormFieldDTO.of("",false,true));
		}
		return data;
	}
	/**
	 * Fetch all selected items from the dictionary
	 * @param dict
	 * @return empty if nothing
	 */
	@Transactional
	public Set<Long> selectedItems(DictionaryDTO dict) {
		Set<Long> ret = new LinkedHashSet<Long>();
		if(dict.getPrevSelected().size()>0) {
			for(Long id : dict.getPrevSelected()) {
				if(id>0) {
					ret.add(id);
				}
			}
		}else {
			if(dict.getSelection().getValue().getId()>0) {
				ret.add(dict.getSelection().getValue().getId());	//the latest "Next"
			}
		}
		return ret;
	}

	/**
	 * получим центр и зум для Непала
	 * loc = "49.189829;32.382276";
	 * z = "6";
	 */
	public void loadHomeLocation(AddressDTO data) throws ObjectNotFoundException {
		DictionaryDTO dict = data.getDictionary();
		Concept root = closureServ.loadRoot(dict.getUrl());
		//-- default values - by Nepal select
		String loc = literalServ.readValue(LiteralService.GIS_LOCATION, root);
		data.getHomecenter().setCenter(dtoServ.createLocationDTO(loc));

		String z = literalServ.readValue(LiteralService.ZOMM, root);
		int zoom = SystemService.DEFAULT_ZOOM;
		if(z != null && z.trim().length() > 0)
			zoom = new Integer(z);
		data.getHomecenter().setZoom(zoom);
	}

	public GisLocationDTO loadCenterMap(GisLocationDTO data) throws ObjectNotFoundException {
		if(data.getId() > 0) {
			Concept adm = closureServ.loadConceptById(data.getId());
			if(adm != null) {
				String loc = literalServ.readValue(LiteralService.GIS_LOCATION, adm);
				if(loc.contains("0.0")) {
					loc = loadCenterContry();
				}
				data.setCenter(dtoServ.createLocationDTO(loc));
				String z = literalServ.readValue(LiteralService.ZOMM, adm);
				Integer zoom = SystemService.DEFAULT_ZOOM;
				if(z != null && z.length() > 0) {
					zoom = new Integer(z);
				}
				data.setZoom(zoom);
			}
		}
		return data;
	}

	private String loadCenterContry() throws ObjectNotFoundException {
		String loc = "";
		Concept country = closureServ.loadRoot(SystemService.DICTIONARY_ADMIN_UNITS);
		if(country != null) {
			loc = literalServ.readValue(LiteralService.GIS_LOCATION, country);
		}
		return loc;
	}
	/**
	 * Load all dictionary as a plain list. Sort order is natural
	 * @param dictUrl
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<OptionDTO> loadPlain(String dictUrl) throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot(dictUrl);
		List<OptionDTO> firstLevel = loadLevelAsOptions(root);
		List<OptionDTO> ret = questFromDict(firstLevel);
		return ret;
	}

	/**
	 * load whole tree from the dictionary recursive
	 * @param level
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private List<OptionDTO> questFromDict(List<OptionDTO> level) throws ObjectNotFoundException {
		List<OptionDTO> ret = new ArrayList<OptionDTO>();
		for(OptionDTO opt :level) {
			ret.add(opt);
			Concept node = closureServ.loadConceptById(opt.getId());
			List<OptionDTO> nextLevel=loadLevelAsOptions(node);
			if(nextLevel.size()>0) {
				opt.setActive(false);									//not terminal
				ret.addAll(questFromDict(nextLevel));
			}else {
				opt.setActive(true);									//terminal
			}
		}
		return ret;
	}

	/**
	 * Dict node and url
	 * For mock only, not for the real job
	 * @param dict
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ChoiceDTO mockChoice(DictionaryDTO dict) throws ObjectNotFoundException {
		ChoiceDTO ret = new ChoiceDTO();
		Set<Long> sel = selectedItems(dict);
		if(sel.size()>0) {
			Long selected = sel.iterator().next();
			Concept node = closureServ.loadConceptById(selected);
			String url = literalServ.readValue("url", node);
			DictionaryDTO guest = new DictionaryDTO();
			guest.setUrl(SystemService.DICTIONARY_GUEST_APPLICATIONS);
			guest = createDictionaryFromRoot(guest, null);
			List<TableRow> rows = guest.getTable().getRows();
			long dictNodeId=0;
			for(TableRow row : rows) {
				Concept conc = closureServ.loadConceptById(row.getDbID());
				String u = literalServ.readValue("url", conc);
				if(u.equalsIgnoreCase(url)) {
					dictNodeId=row.getDbID();
					break;
				}
			}
			ret.setDictNodeId(dictNodeId);
			ret.setUrl(url);
			return ret;
		}else {
			throw new ObjectNotFoundException("mockChoice No Dictionary selection", logger);
		}
	}
	/**
	 * Create or get a simple dictionary
	 * @param url
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public void checkDictionary(String url) throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot(url);
		String prefLabel = literalServ.readPrefLabel(root);
		if(prefLabel.length()==0) {
			literalServ.createUpdatePrefLabel(messages.get("aminunits"), root);
		}
		String description=literalServ.readDescription(root);
		if(description == null || description.length()==0) {
			literalServ.createUpdateDescription("", root);
		}

	}
	/**
	 * Store path from the dictnode under node. All languages
	 * @param dictNode
	 * @param node
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept storePath(Concept dictNode, Concept node) throws ObjectNotFoundException {
		Languages langs = messages.getLanguages();
		for(Language lang : langs.getLangs()) {
			String path = dictPath(lang.getLocaleAsString(),dictNode);
			Concept pNode = new Concept();
			pNode.setIdentifier(lang.getLocaleAsString().toUpperCase());
			pNode.setLabel(path);
			closureServ.saveToTree(node, pNode);
		}
		return node;
	}
	/**
	 * Path to the dictnode on the language
	 * @param lang
	 * @param dictNode
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public String dictPath(String locale, Concept dictNode) throws ObjectNotFoundException {
		String ret="";
		 List<String> retList = literalServ.loadAllParentPrefLabels(dictNode, locale);
		 ret=String.join(",", retList);
		return ret;
	}
	
	public boolean isAdminUnits(Concept root) {
		return root.getIdentifier().equalsIgnoreCase("dictionary.admin.units");
	}
	/**
	 * Create droplist from dictionaries
	 * data from url configuration dictionsru is in use. The value by default is "-" (none)
	 * @param data
	 * @param droplist
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ThingDTO createDropList(ThingDTO data, List<AssemblyDTO> droplist) throws ObjectNotFoundException {
		data.getDroplist().clear();		
		if(droplist != null) {
			for(AssemblyDTO list : droplist) {
				//???
				OptionDTO ret = new OptionDTO();
				List<OptionDTO> plainDictionary = loadPlain(list.getUrl());
				if(plainDictionary!=null) {
					for(OptionDTO opt:plainDictionary) {
						ret.getOptions().add(opt);
					}
					FormFieldDTO<OptionDTO> fld = FormFieldDTO.of(ret);
					fld.setReadOnly(list.isReadOnly());
					data.getDroplist().put(list.getPropertyName(),fld);
				}
			}
			if(data.getNodeId()>0) {
				Concept node = closureServ.loadConceptById(data.getNodeId());
				Thing thing = new Thing();
				thing= boilerServ.thingByNode(node,thing);
				for(ThingDict th:thing.getDictionaries()) {
					FormFieldDTO<OptionDTO> dl=data.getDroplist().get(th.getVarname());
					if(dl!=null){
						dl.getValue().setId(th.getConcept().getID());
						dl.getValue().setCode(literalServ.readPrefLabel(th.getConcept()));
						dl.getValue().setOriginalCode(literalServ.readPrefLabel(th.getConcept()));
						dl.getValue().setDescription(literalServ.readDescription(th.getConcept()));
						dl.getValue().setOriginalDescription(literalServ.readDescription(th.getConcept()));
					}
				}
			}
		}
		return data;
	}
}