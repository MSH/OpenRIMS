package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.OrgAdmin;
import org.msh.pdex2.model.r2.PublicOrganization;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.r2.PubOrgRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.DictNodeDTO;
import org.msh.pharmadex2.dto.PublicOrgDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.EntityService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service is to manage public organizations, both government and business
 * @author alexk
 *
 */
@Service
public class PubOrgService {
	@Value( "${pharmadex.territory.responsible:1}" )
	private int territoryLevel;

	private static final Logger logger = LoggerFactory.getLogger(PubOrgService.class);
	@Autowired
	private Messages messages;
	@Autowired
	private PubOrgRepo pubOrgRepo;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private EntityService entityServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private DictService dictServ;
	@Autowired
	private ValidationService validationServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private BoilerService boilerServ;

	/**
	 * Load an organization by the concept or create a new. Assembly:
	 * <ul>
	 * <li> concept node for the organization
	 * <li>references to all linked classifiers and auxiliary data
	 * <li>
	 * </ul>
	 * @param id
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public PublicOrgDTO loadByConcept(DictNodeDTO data) throws ObjectNotFoundException {
		PublicOrgDTO ret = new PublicOrgDTO();
		PublicOrganization org = new PublicOrganization();
		// this organization
		if(data.getNodeId()>0) {
			Concept concept =dictServ.node(data);
			org=fetchByConcept(concept);
			ret.setId(org.getID());
			ret.setNode(dictServ.createNode(concept));
			//set path in organization chart
			List<Concept> path = closureServ.loadParents(concept);
			ret.getNode().getTitle().clear();
			for(Concept conc :path) {
				String code = literalServ.readValue("prefLabel", conc);
				ret.getNode().getTitle().add(code);
			}
			ret.getSelected().clear();
			for(OrgAdmin oa : org.getAdminUnits()) {
				ret.getSelected().add(oa.getAdminUnit().getID());
			}
		}
		//territory responsibility
		ret=createLoadTerritories(ret, territoryLevel);
		return ret;
	}

	/**
	 * Load links organization-territory
	 * Create a table
	 * @param ret
	 * @param tLevel
	 * @return
	 */
	private PublicOrgDTO createLoadTerritories(PublicOrgDTO data, int tLevel) {
		jdbcRepo.admin_units2();
		if(territoryLevel==1) {
			data=provincesTable(data);
		}else {
			data=districtsTable(data);
		}
		return data;
	}
	/**
	 * Provinces + Districts selection to determine territory responsibility 
	 * @param data
	 * @return
	 */
	private PublicOrgDTO districtsTable(PublicOrgDTO data) {
		TableQtb table = data.getTable();
		if(table.getHeaders().getHeaders().size()==0) {
			table.setHeaders(districtHeaders(table.getHeaders()));
		}
		String select= "select distinct ID2 as 'ID', level1, level2 from admin_units2";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", "", table.getHeaders());
		rows=markSelected(data,rows);
		TableQtb.tablePage(rows, table);		//TODO should be replaced
		return data;
	}
	/**
	 * Headers for provinces selection
	 * @param headers
	 * @return
	 */
	private Headers districtHeaders(Headers headers) {
		headers.getHeaders().add(TableHeader.instanceOf(
				"level1",
				"adminunit1",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"level2",
				"adminunit2",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers=boilerServ.translateHeaders(headers);
		headers.getHeaders().get(0).setSort(true);
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		headers.getHeaders().get(1).setSort(true);
		headers.getHeaders().get(1).setSortValue(TableHeader.SORT_ASC);
		return headers;
	}

	/**
	 * Provinces selection to determine territory responsibility
	 * @param data
	 * @return
	 */
	private PublicOrgDTO provincesTable(PublicOrgDTO data) {
		TableQtb table = data.getTable();
		if(table.getHeaders().getHeaders().size()==0) {
			table.setHeaders(provinceHeaders(table.getHeaders()));
		}
		String select= "select distinct ID1 as 'ID', level1 from admin_units2";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", "", table.getHeaders());
		rows=markSelected(data,rows);
		TableQtb.tablePage(rows, table);		//TODO should be replaced
		return data;
	}

	/**
	 * Mark all selected rows
	 * @param data.getSelected()
	 * @param rows
	 * @return
	 */
	private List<TableRow> markSelected(PublicOrgDTO data, List<TableRow> rows) {
 		for(TableRow row :rows) {
			if(data.getSelected().contains(row.getDbID())) {
				row.setSelected(true);
			}else {
				row.setSelected(false);
			}
		}
 		//cleanup old selected - it is really need when territorylevel has been changed
 		data.getSelected().clear();
 		for(TableRow row : rows) {
 			if(row.getSelected()) {
 				data.getSelected().add(row.getDbID());
 			}
 		}
		if(data.isAll()) {
			return rows;
		}else {
			List<TableRow> selRows = new ArrayList<TableRow>();
			for(TableRow row : rows) {
				if(row.getSelected()) {
					selRows.add(row);
				}
			}
			return selRows;
		}
	}

	/**
	 * Headers for provinces selection
	 * @param headers
	 * @return
	 */
	private Headers provinceHeaders(Headers headers) {
		headers.getHeaders().add(TableHeader.instanceOf(
				"level1",
				"adminunit1",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers=boilerServ.translateHeaders(headers);
		headers.getHeaders().get(0).setSort(true);
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		return headers;
	}

	/**
	 * Load all parent organizations from root
	 * @param rootNode
	 * @param parentNode
	 * @param org
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private List<Concept> loadAllParent(Concept rootNode, Concept parentNode, PublicOrganization org) throws ObjectNotFoundException {
		List<Concept> ret = new ArrayList<Concept>();
		if(org.getID()>0) {
			//organization is existing
			ret.addAll(closureServ.loadParents(org.getConcept()));	
		}else {
			if(parentNode.getID()>0 && rootNode.getID() != parentNode.getID()) {
				//parent is existing and not root
				ret.addAll(closureServ.loadParents(parentNode));
				Concept newConc = new Concept();
				newConc.setIdentifier("0");
				ret.add(newConc);
			}else {
				if(rootNode.getID()>0) {
					ret.add(rootNode);
				}
			}
		}
		return ret;
	}


	/**
	 * Fetch organization by concept
	 * @param concept
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private PublicOrganization fetchByConcept(Concept concept) throws ObjectNotFoundException {
		Optional<PublicOrganization> orgo = pubOrgRepo.findByConcept(concept);
		if(orgo.isPresent()) {
			return orgo.get();
		}else {
			throw new ObjectNotFoundException("fetchByConcept. Organization not found. Concept id is "+concept.getID(),logger);
		}
	}

	/**
	 * Make an organization inactive
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public PublicOrgDTO suspend(PublicOrgDTO data) throws ObjectNotFoundException {
		if(data.getNode().getNodeId()>0) {
			Concept node = closureServ.loadConceptById(data.getNode().getNodeId());
			if(literalServ.isLeaf(node)) {
				node.setActive(false);
				closureServ.save(node);
			}else {
				data.setValid(false);
				data.setIdentifier(messages.get(""));
			}
		}
		return data;
	}

	/**
	 * Load any organization for test purpose
	 * @return
	 */
	@Transactional
	public PublicOrganization loadAnyOrganization() {
		Iterable<PublicOrganization> list = pubOrgRepo.findAll();
		return list.iterator().next();
	}

	/**
	 * load public organization by PublicOrganization ID
	 * @param id
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public PublicOrganization loadById(long id) throws ObjectNotFoundException {
		Optional<PublicOrganization> orgo = pubOrgRepo.findById(id);
		if(orgo.isPresent()) {
			return orgo.get();
		}else {
			throw new ObjectNotFoundException("LoadByID. Public organization not found. Id is "+id,logger);
		}
	}

	/**
	 * Save an organization
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public PublicOrgDTO save(PublicOrgDTO data) throws ObjectNotFoundException {
		Concept orgNode = savePublOrg(data);
		if(orgNode != null) {
			//and return
			data.setNode(dictServ.createNode(orgNode));
			data = loadByConcept(data.getNode());
		}
		return data;
	}

	@Transactional
	public Concept savePublOrg(PublicOrgDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		data=validationServ.organization(data,true);
		if(data.isValid()) {
			//organization itself
			PublicOrganization org = new PublicOrganization();
			if(data.getId()>0) {
				org=loadById(data.getId());
			}
			//node of it in the tree
			Concept orgNode = new Concept();
			if(org.getConcept() != null) {
				orgNode=org.getConcept();
			}
			orgNode=entityServ.node(data.getNode());
			org.setConcept(orgNode);

			//territory responsibilities
			org.getAdminUnits().clear();
			for(Long id :data.getSelected()) {
				Concept au = closureServ.loadConceptById(id);
				OrgAdmin oa = new OrgAdmin();
				oa.setAdminUnit(au);
				org.getAdminUnits().add(oa);
			}
			//finally save
			org=pubOrgRepo.save(org);

			return orgNode;
		}
		return null;
	}
	/**
	 * Load only responsibility table
	 * @param node
	 * @return
	 */
	public PublicOrgDTO loadResponsibility(PublicOrgDTO data) {
		data=createLoadTerritories(data, territoryLevel);
		return data;
	}
	/**
	 * Select/deselect an administrative unit
	 * @param data
	 * @return
	 */
	public PublicOrgDTO loadResponsibilitySelect(PublicOrgDTO data) {
		if(data.getRowId()!=0) {
			if(data.getSelected().contains(data.getRowId())) {
				data.getSelected().remove(data.getRowId());
			}else {
				data.getSelected().add(data.getRowId());
			}
			data=createLoadTerritories(data, territoryLevel());
		}
		data.setRowId(0l);
		return data;
	}
	/**
	 * Level in territory units
	 * @return
	 */
	public int territoryLevel() {
		return territoryLevel;
	}

}
