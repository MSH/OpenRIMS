package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.PublicOrgSubject;
import org.msh.pdex2.model.r2.PublicOrganization;
import org.msh.pdex2.repository.r2.PubOrgRepo;
import org.msh.pdex2.repository.r2.PubOrgSubjRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.DictNodeDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.PublicOrgDTO;
import org.msh.pharmadex2.service.common.EntityService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service is to manage public organizations, both government and business
 * @author alexk
 *
 */
@Service
public class PubOrgService {
	private static final Logger logger = LoggerFactory.getLogger(PubOrgService.class);
	@Autowired
	private Messages messages;
	@Autowired
	private AssemblyService assemblyServ;
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
	private PubOrgSubjRepo pubOrgSubjRepo;



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
		Concept root=dictServ.loadRoot(data);
		// this organization
		if(data.getNodeId()>0) {
			Concept concept =dictServ.node(data);
			org=fetchByConcept(concept);
			ret.setId(org.getID());
			ret.setNode(dictServ.createNode(concept));
			//set path in organization chart
			List<Concept> path = closureServ.loadParents(concept);
			ret.getNode().getTitle().clear();
			String rootStr = literalServ.readValue("prefLabel", root);		//forget-me-not
			for(Concept conc :path) {
				String code = literalServ.readValue("prefLabel", conc);
				ret.getNode().getTitle().add(code);
			}
		}
		//parent organization
		Concept parentOrgConcept = dictServ.parentNode(data);
		PublicOrganization parentOrg = new PublicOrganization();
		if(parentOrgConcept.getID()!=root.getID()) {
			parentOrg=fetchByConcept(parentOrgConcept);
		}
		//assembly organization DTO
		List<AssemblyDTO> adicts = assemblyServ.pubOrgDict();
		ret.setDictionaries(createDictionaries(parentOrg,org, adicts));
		
		return ret;
	}


	/**
	 * Create all dictionaries defined for this type of organization
	 * @param parentOrg parent organization
	 * @param org this organization
	 * @param adicts list of dictionaries defined
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private Map<String,DictionaryDTO> createDictionaries(PublicOrganization parentOrg, PublicOrganization org,
			List<AssemblyDTO> adicts) throws ObjectNotFoundException {
		Map<String,DictionaryDTO> ret = new HashMap<String, DictionaryDTO>();
		for(AssemblyDTO adict : adicts) {
			ret.put(adict.getPropertyName(),createDictionary(parentOrg, org, adict));
		}
		return ret;
	}


	/**
	 * Create a dictionary use selections in this or parent organization or from the root
	 * @param parent
	 * @param org
	 * @param adict
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private DictionaryDTO createDictionary(PublicOrganization parent, PublicOrganization org, AssemblyDTO adict) throws ObjectNotFoundException {
		DictionaryDTO ret = new DictionaryDTO();
		//general
		ret.setUrl(adict.getUrl());
		ret.setMult(adict.isMult());
		ret.setRequired(adict.isRequired());
		Concept root=closureServ.loadRoot(adict.getUrl());
		String home=literalServ.readValue("prefLabel", root);
		ret.setHome(home);
		//determine the algorithm - 
		List<Long> selected = selectedInDictionary(org, adict.getUrl());
		ret.getPrevSelected().clear();
		ret.getPrevSelected().addAll(selected);
		if(selected.size()>0) {
			ret= dictServ.createDictionaryFromSelected(selected, ret);
		}else {
			selected = selectedInDictionary(parent, adict.getUrl());
			if(selected.size()>0) {
				ret=dictServ.createDictionaryFromPrevSelected(selected,ret);
			}else {
				ret=dictServ.createDictionaryFromRoot(ret);
			}
		}
		return ret;
	}

	/**
	 * Get IDs of dictionary nodes selected in organization org
	 * @param org the organization
	 * @param dictUrl url of dictionary to search
	 * @return
	 */
	@Transactional
	public List<Long> selectedInDictionary(PublicOrganization org, String dictUrl) {
		List<Long> selected = new ArrayList<Long>();
		for(PublicOrgSubject subj : org.getSubjects()) {
			if(subj.getUrl().equalsIgnoreCase(dictUrl)) {
				selected.add(subj.getNode().getID());
			}
		}
		return selected;
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
			//dictionaries
			if(org.getSubjects().size()>0) {
				pubOrgSubjRepo.deleteAll(org.getSubjects());
				org.getSubjects().clear();
			}
			for(String key:data.getDictionaries().keySet()) {
				Long mainId = data.getDictionaries().get(key).getSelection().getValue().getId();
				List<Long> prevSelected = data.getDictionaries().get(key).getPrevSelected();
				if(mainId>0 && prevSelected.size()==0) {
					prevSelected.add(mainId);
				}
				for(Long id : prevSelected ) {
					if(id>0) {
						Concept node=closureServ.loadConceptById(id);
						PublicOrgSubject subj = new PublicOrgSubject();
						subj.setNode(node);
						subj.setPredicate("");
						subj.setUrl(data.getDictionaries().get(key).getUrl());
						org.getSubjects().add(subj);
					}
				}
			}
			//finally save
			org=pubOrgRepo.save(org);
			
			return orgNode;
		}
		return null;
	}
	
}
