package org.msh.pharmadex2.service.r2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pharmadex2.dto.Dict2DTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.auth.UserRoleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Responsible for hard coded system setting
 * In contrast AssemblyService.
 * @author alexk
 *
 */
@Service
public class SystemService {
	private static final Logger logger = LoggerFactory.getLogger(SystemService.class);
	
	public static final String DICTIONARY_GUEST_APPLICATIONS = "dictionary.guest.applications";
	public static final String DICTIONARY_SHUTDOWN_APPLICATIONS = "dictionary.shutdown.applications";
	public static final String DICTIONARY_HOST_APPLICATIONS = "dictionary.host.applications";
	private static final String DICTIONARY_SYSTEM_LIFECYCLE = "dictionary.system.lifecycle";
	public static final String DICTIONARY_SYSTEM_SUBMIT = "dictionary.system.submit";
	public static final Integer DEFAULT_ZOOM = 7;
	

	public static final String DICTIONARY_ADMIN_UNITS = "dictionary.admin.units";

	public static final String DICTIONARY_SYSTEM_ROLES = "dictionary.system.roles";
	/**
	 * Tree for persons, not dictionary
	 */
	public static final String PERSON_URL = "user.data";

	public static final String FILE_STORAGE_BUSINESS = "file.storage.business";
	public static final String DATA_COLLECTIONS_ROOT = "configuration.data";


	public static String[] ROLES = {"ROLE_ADMIN", "ROLE_MODERATOR", "ROLE_REVIEWER", "ROLE_INSPECTOR", 
			"ROLE_ACCOUNTANT", "ROLE_SCREENER","APPLICANT"};

	@Autowired
	private DictService dictServ;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private Messages messages;

	
	/**
	 * Add role to roles dictionary
	 * @param root
	 * @param role
	 * @throws ObjectNotFoundException
	 */
	public void addRoleToDictionary(Concept root, String role) throws ObjectNotFoundException {
		Concept node = new Concept();
		node.setIdentifier(role);
		node = closureServ.saveToTree(root, node);
		String prefLabel = literalServ.readPrefLabel(node);
		String description=literalServ.readDescription(node);
		if(prefLabel.length()==0) {
			literalServ.createUpdatePrefLabel(role,node);
		}
		if(description.length()==0) {
			literalServ.createUpdateDescription(role, node);
		}
	}

	/**
	 * Loads or creates user's roles dictionary
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DictionaryDTO userRolesDict(DictionaryDTO dict) throws ObjectNotFoundException {
		dict.setUrl(DICTIONARY_SYSTEM_ROLES);
		// create or update the dictionary
		Concept root = closureServ.loadRoot(DICTIONARY_SYSTEM_ROLES);
		String rootPref=literalServ.readPrefLabel(root);
		String rootDescr = literalServ.readDescription(root);
		if(rootPref.length()==0) {
			literalServ.createUpdatePrefLabel(messages.get("role"), root);
		}
		if(rootDescr.length()==0) {
			literalServ.createUpdateDescription(messages.get("role"), root);
		}
		literalServ.createUpdateLiteral("type", "system", root);
		for(String role : ROLES) {
			addRoleToDictionary(root, role);
		}
		dict=dictServ.createDictionary(dict);
		dict.setMult(true);
		dict.setSystem(true);
		return dict;
	}
	
	

	/**
	 * Address or Admin unit dictionary.
	 * Allows to add new 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public DictionaryDTO addressDictionary(DictionaryDTO data) throws ObjectNotFoundException {
		data.setUrl(DICTIONARY_ADMIN_UNITS);
		data = dictServ.createDictionary(data);
		return data;
	}
	/**
	 * All application types implemented in the system
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public DictionaryDTO applDictionary(DictionaryDTO data) throws ObjectNotFoundException {
		data.setUrl(DICTIONARY_GUEST_APPLICATIONS);
		data = dictServ.createDictionary(data);
		return data;
	}
	/**
	 * Get nemes of user's roles from the dictionary
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Map<String, String> roleNames() throws ObjectNotFoundException {
		Map<String,String> ret = new LinkedHashMap<String, String>();
		Concept root=closureServ.loadRoot(DICTIONARY_SYSTEM_ROLES);
		List<Concept> nodes= literalServ.loadOnlyChilds(root);
		for(Concept node : nodes) {
			String pref = literalServ.readPrefLabel(node);
			ret.put(node.getIdentifier(),pref);
		}
		return ret;
	}
	/**
	 * Get URL of node
	 * @param nodeId
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public String objectUrl(long nodeId) throws ObjectNotFoundException {
		String ret="";
		Concept node = closureServ.loadConceptById(nodeId);
		ret = closureServ.getUrlByNode(node);
		return ret;
	}
	/**
	 * Submit actions for all
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DictionaryDTO submitActionDictionary() throws ObjectNotFoundException {
		DictionaryDTO ret = new DictionaryDTO();
		ret.setUrl(DICTIONARY_SYSTEM_SUBMIT);
		Concept root = closureServ.loadRoot(DICTIONARY_SYSTEM_SUBMIT);
		String prefLabel=literalServ.readPrefLabel(root);
		String descr = literalServ.readDescription(root);
		if(prefLabel.length()==0 && descr.length()==0) {
			root=literalServ.prefAndDescription(messages.get("Submit actions"), "", root);
			literalServ.createUpdateLiteral("type", "system", root);
		}
		List<Concept> level = literalServ.loadOnlyChilds(root);
		if(level.size()==0) {
			//create level
			systemDictNode(root, "0", messages.get("continue"));
			systemDictNode(root, "1", messages.get("route_action"));
			systemDictNode(root, "2", messages.get("newactivity"));
			systemDictNode(root, "3", messages.get("cancel"));
			systemDictNode(root, "4", messages.get("approve"));
			systemDictNode(root, "5", messages.get("reject"));
			systemDictNode(root, "6", messages.get("reassign"));
		}
		ret=dictServ.createDictionary(ret);
		ret.setMult(false);
		ret.setSystem(true);
		return ret;
	}
	/**
	 * Add a node to the system application conclusion dictionary
	 * @param root
	 */
	private void systemDictNode(Concept root, String identifier, String prefLabel) throws ObjectNotFoundException {
		Concept node = new Concept();
		node.setIdentifier(identifier);
		node= closureServ.saveToTree(root, node);
		node = literalServ.prefAndDescription(prefLabel, "", node);
	}
	/**
	 * Responsible to work with system dictionaries
	 * Master dictionary is process stages
	 * Slave one is one workflow configurations
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Dict2DTO stagesWorkflow(Dict2DTO data) throws ObjectNotFoundException {
		if(data.getMasterDict().getUrl().length()==0) {
			data.setMasterDict(stagesDictionary());
		}
		Set<Long> stageNodeIds = dictServ.selectedItems(data.getMasterDict());
		if(stageNodeIds.size()==1) {
			Concept stageNode=closureServ.loadConceptById(stageNodeIds.iterator().next());
			//select a second dictionary
			if(!data.getSlaveDict().getUrl().equalsIgnoreCase(stageNode.getIdentifier())) {
				data.setSlaveDict(workflowDictionary(stageNode.getIdentifier()));
			}
		}else {
			data.setSlaveDict(new DictionaryDTO());
		}
		return data;
	}
	
	/**
	 * Workflow dictionary may or may not be created yet.
	 * @param dictUrl
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DictionaryDTO workflowDictionary(String dictUrl) throws ObjectNotFoundException {
		DictionaryDTO ret = new DictionaryDTO();
		ret.setUrl(dictUrl);
		ret = dictServ.createDictionary(ret);
		if(ret.getHome().length()==0) {
			Concept root = closureServ.loadRoot(dictUrl);
			literalServ.createUpdatePrefLabel(messages.get(dictUrl), root);
			String descr=literalServ.readDescription(root);
			if(descr.length()==0) {
				literalServ.createUpdateDescription("description", root);
			}
			ret=dictServ.createDictionary(ret);
		}
		return ret;
	}

	/**
	 * Create new or load dictionary.system.lifecycle
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public DictionaryDTO stagesDictionary() throws ObjectNotFoundException {
		DictionaryDTO ret = new DictionaryDTO();
		ret.setUrl(DICTIONARY_SYSTEM_LIFECYCLE);
		Concept root = closureServ.loadRoot(DICTIONARY_SYSTEM_LIFECYCLE);
		String prefLabel=literalServ.readPrefLabel(root);
		String descr = literalServ.readDescription(root);
		if(prefLabel.length()==0 && descr.length()==0) {
			root=literalServ.prefAndDescription(messages.get("processes"), "", root);
			literalServ.createUpdateLiteral("type", "system", root);
		}
		List<Concept> level = literalServ.loadOnlyChilds(root);
		if(level.size()==0) {
			//create level
			systemDictNode(root, DICTIONARY_GUEST_APPLICATIONS, messages.get("guest"));
			systemDictNode(root, DICTIONARY_HOST_APPLICATIONS, messages.get("host"));
			systemDictNode(root, DICTIONARY_SHUTDOWN_APPLICATIONS, messages.get("shutdown"));
		}
		ret=dictServ.createDictionary(ret);
		ret.setMult(false);
		ret.setSystem(true);
		return ret;
	}
	/**
	 * Determine the next stage dictionary URL by this stage dictionary entry
	 * @param his the current history record
	 * @param approved - action, true is approved, otherwise is rejected
	 * @return the dictionary url for the next stage, empty string after shutdown stage
	 */
	@Transactional
	public String nextStageByApplDict(History his, boolean approved) {
		String ret = "";
		if(isGuest(his)) {
			return DICTIONARY_HOST_APPLICATIONS;
		}
		if(isHost(his)) {
			if(approved) {
				return DICTIONARY_HOST_APPLICATIONS;
			}else {
				return DICTIONARY_SHUTDOWN_APPLICATIONS;
			}
		}
		return ret;
	}
	/**
	 * Is current stage GUEST?
	 * @param curHis
	 * @return
	 */
	@Transactional
	public boolean isGuest(History curHis) {
		Concept thisDictRoot = closureServ.getParent(curHis.getApplDict());
		return thisDictRoot.getIdentifier().equalsIgnoreCase(DICTIONARY_GUEST_APPLICATIONS);
	}
	/**
	 * Is current stage HOST?
	 * @param curHis
	 * @return
	 */
	public boolean isHost(History curHis) {
		Concept thisDictRoot = closureServ.getParent(curHis.getApplDict());
		return thisDictRoot.getIdentifier().equalsIgnoreCase(DICTIONARY_HOST_APPLICATIONS);
	}
	/**
	 * Recognize host dictionary node by host process URL
	 * @param processUrl
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept hostDictNode(String processUrl) throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot(DICTIONARY_HOST_APPLICATIONS);
		List<Concept> level = literalServ.loadOnlyChilds(root);
		for(Concept conc : level) {
			String aurl = literalServ.readValue("applicationurl", conc);
			if(aurl.equalsIgnoreCase(processUrl)) {
				return conc;
			}
		}
		throw new ObjectNotFoundException("dictionary node for host process not found. URL is"+processUrl,logger);
	}
	/**
	 * Return report configuration dictionary depends on the user category
	 * <ul>
	 * <li>public
	 * <li>guest
	 * <li>others roles
	 * <li>supervisor
	 * </ul>
	 * 
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public DictionaryDTO reportDictionary(UserDetailsDTO user) throws ObjectNotFoundException {
		List<UserRoleDto> authority = user.getGranted();
		String auth="public";
		if(authority.size()==1) {
			auth=authority.get(0).getAuthority();
		}
		DictionaryDTO ret = new DictionaryDTO();
		ret.setUrl("dictionary.reports."+auth);
		Concept root = closureServ.loadRoot(ret.getUrl());
		String prefLabel=literalServ.readPrefLabel(root);
		String descr = literalServ.readDescription(root);
		if(prefLabel.length()==0 && descr.length()==0) {
			root=literalServ.prefAndDescription("reports for "+auth, "", root);
		}
		ret.setMult(false);
		ret = dictServ.createDictionary(ret);
		return ret;
	}


}
