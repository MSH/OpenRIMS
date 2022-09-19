package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingDict;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.Dict2DTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.auth.UserRoleDto;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.common.BoilerService;
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
	public static final String DICTIONARY_SYSTEM_IMPORT_DATA = "dictionary.system.import.data";
	//public static final String DICTIONARY_SYSTEM_IMPORT_LEGACY_DATA = "dictionary.system.import.legacy.data";
	//Finalization activity related
	private static final String FINAL_DEREGISTRATION = "deregistration";
	public static final String FINAL_AMEND = "AMEND";
	public static final String FINAL_DECLINE = "DECLINE";
	public static final String FINAL_ACCEPT = "ACCEPT";
	public static final String FINAL_NO = "NO";
	public static final String DICTIONARY_SYSTEM_FINALIZATION = "dictionary.system.finalization";
	
	
	private static final Logger logger = LoggerFactory.getLogger(SystemService.class);
	public static final String DICTIONARY_GUEST_DEREGISTRATION = "dictionary.guest.deregistration";
	public static final String DICTIONARY_GUEST_AMENDMENTS = "dictionary.guest.amendments";
	public static final String DICTIONARY_GUEST_APPLICATIONS = "dictionary.guest.applications";
	public static final String DICTIONARY_GUEST_RENEWAL = "dictionary.guest.renewal";
	public static final String DICTIONARY_SHUTDOWN_APPLICATIONS = "dictionary.shutdown.applications";
	public static final String DICTIONARY_HOST_APPLICATIONS = "dictionary.host.applications";
	public static final String DICTIONARY_SYSTEM_LIFECYCLE = "dictionary.system.lifecycle";
	public static final String DICTIONARY_SYSTEM_SUBMIT = "dictionary.system.submit";
	public static final String ROOT_SYSTEM_TILES = "dictionary.system.tiles";
	public static final String DICTIONARY_ADMIN_UNITS = "dictionary.admin.units";
	public static final String DICTIONARY_SYSTEM_ROLES = "dictionary.system.roles";
	public static final String CONFIGURATION_ADMIN_UNITS = "configuration.admin.units";
	public static final Integer DEFAULT_ZOOM = 7;

	public static final String PRODUCTCLASSIFICATION_ATC_HUMAN = "medicinalproductdefinition.productclassification.atc.human";
	
	/**
	 * Tree for persons, not dictionary
	 */
	public static final String PERSON_URL = "user.data";

	public static final String FILE_STORAGE_BUSINESS = "file.storage.business";
	public static final String DATA_COLLECTIONS_ROOT = "configuration.data";
	public static final String RECYCLE = "system.recycle.bin";

	//*************************** ROLES ******************************************************************
	public static String ROLE_ADMIN="ROLE_ADMIN";
	public static String ROLE_SECRETARY="ROLE_SECRETARY";		
	public static String ROLE_APPLICANT="APPLICANT";
	public static String[] ROLES = {ROLE_ADMIN, "ROLE_MODERATOR", "ROLE_REVIEWER", "ROLE_INSPECTOR", 
			"ROLE_ACCOUNTANT", "ROLE_SCREENER", ROLE_SECRETARY,ROLE_APPLICANT};
	
	@Autowired
	private DictService dictServ;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private Messages messages;
	@Autowired
	private BoilerService boilerServ;


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
	 * Possible finalization scenarious
	 * @param dictionaryDTO
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private void finalizeDict() throws ObjectNotFoundException {
		// create or update the dictionary
		Concept root = closureServ.loadRoot(DICTIONARY_SYSTEM_FINALIZATION);
		String rootPref=literalServ.readPrefLabel(root);
		String rootDescr = literalServ.readDescription(root);
		if(rootPref.length()==0) {
			literalServ.createUpdatePrefLabel(messages.get("finalize"), root);
		}
		if(rootDescr.length()==0) {
			literalServ.createUpdateDescription(messages.get("finalize"), root);
		}
		literalServ.createUpdateLiteral("type", "system", root);
		addRoleToDictionary(root, FINAL_NO);
		addRoleToDictionary(root, FINAL_ACCEPT);
		addRoleToDictionary(root, FINAL_DECLINE);
		addRoleToDictionary(root, FINAL_AMEND);
		addRoleToDictionary(root, FINAL_DEREGISTRATION);
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
	 * New applications dictionary
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public DictionaryDTO applicationsDictionary(DictionaryDTO data) throws ObjectNotFoundException {
		data.setUrl(DICTIONARY_GUEST_APPLICATIONS);
		Concept root = closureServ.loadRoot(data.getUrl());
		String prefLabel=literalServ.readPrefLabel(root);
		String descr = literalServ.readDescription(root);
		if(prefLabel.length()==0) {
			prefLabel=messages.get("newapplications");
			descr="";
		}
		literalServ.prefAndDescription(prefLabel, descr, root);
		data = dictServ.createDictionary(data);
		return data;
	}
	
	/**
	 * All amendment types implemented in the system
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public DictionaryDTO amendmentDictionary(DictionaryDTO data) throws ObjectNotFoundException {
		data.setUrl(DICTIONARY_GUEST_AMENDMENTS);
		Concept root = closureServ.loadRoot(data.getUrl());
		String prefLabel=literalServ.readPrefLabel(root);
		String descr = literalServ.readDescription(root);
		if(prefLabel.length()==0) {
			prefLabel=messages.get("amdmt_type");
			descr="";
		}
		literalServ.prefAndDescription(prefLabel, descr, root);
		data = dictServ.createDictionary(data);
		return data;
	}
	/**
	 * Create or get dictionary for renewal processes
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public DictionaryDTO renewalDict(DictionaryDTO data) throws ObjectNotFoundException {
		data.setUrl(DICTIONARY_GUEST_RENEWAL);
		Concept root = closureServ.loadRoot(data.getUrl());
		String prefLabel=literalServ.readPrefLabel(root);
		String descr = literalServ.readDescription(root);
		if(prefLabel.length()==0) {
			prefLabel=messages.get("renew");
			descr="";
		}
		literalServ.prefAndDescription(prefLabel, descr, root);
		data = dictServ.createDictionary(data);
		return data;
	}
	/**
	 * DE-registration applications
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public DictionaryDTO deregistrationDict(DictionaryDTO data) throws ObjectNotFoundException {
		data.setUrl(DICTIONARY_GUEST_DEREGISTRATION);
		Concept root = closureServ.loadRoot(data.getUrl());
		String prefLabel=literalServ.readPrefLabel(root);
		String descr = literalServ.readDescription(root);
		if(prefLabel.length()==0) {
			prefLabel=messages.get("suspend_info");
			descr="";
		}
		literalServ.prefAndDescription(prefLabel, descr, root);
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
		if(level.size()!=9) {
			//create level
			systemDictNode(root, "0", messages.get("continue"));
			systemDictNode(root, "1", messages.get("route_action"));
			systemDictNode(root, "2", messages.get("newactivity"));
			systemDictNode(root, "3", messages.get("cancel"));
			systemDictNode(root, "4", messages.get("approve"));
			systemDictNode(root, "5", messages.get("reject"));
			systemDictNode(root, "6", messages.get("reassign"));
			systemDictNode(root, "7", messages.get("amendment"));
			systemDictNode(root, "8", messages.get("deregistration"));
		}
		ret=dictServ.createDictionary(ret);
		ret.setMult(false);
		ret.setSystem(true);
		return ret;
	}
	
	/**
	 * Upload data for import dictionary
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private DictionaryDTO uploadImportDataDictionary() throws ObjectNotFoundException {
		DictionaryDTO ret = new DictionaryDTO();
		ret.setUrl(DICTIONARY_SYSTEM_IMPORT_DATA);
		Concept root = closureServ.loadRoot(DICTIONARY_SYSTEM_IMPORT_DATA);
		String prefLabel=literalServ.readPrefLabel(root);
		String descr = literalServ.readDescription(root);
		if(prefLabel.length()==0 && descr.length()==0) {
			root=literalServ.prefAndDescription(messages.get("dataimport"), "", root);
			literalServ.createUpdateLiteral("type", "system", root);
		}
		List<Concept> level = literalServ.loadOnlyChilds(root);
		if(level.size() == 0) {
			systemDictNode(root, AssemblyService.DATAIMPORT_DATA, "XLSX file contains data to import");
			systemDictNode(root, AssemblyService.DATAIMPORT_DATA_ERROR, "XLSX file contains data import errors");
		}else if(level.size() == 1) {
			Concept c = level.get(0);
			c.setIdentifier(AssemblyService.DATAIMPORT_DATA);
			closureServ.save(c);
			systemDictNode(root, AssemblyService.DATAIMPORT_DATA_ERROR, "XLSX file contains data import errors");
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
		String pl=literalServ.readPrefLabel(node);
		if(pl.length()==0) {
			node = literalServ.prefAndDescription(prefLabel, "", node);
		}
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
		//create level
		systemDictNode(root, DICTIONARY_GUEST_APPLICATIONS, messages.get("guest"));
		systemDictNode(root, DICTIONARY_GUEST_AMENDMENTS, messages.get("amdmt_type"));
		systemDictNode(root, DICTIONARY_GUEST_DEREGISTRATION, messages.get(FINAL_DEREGISTRATION));
		systemDictNode(root, DICTIONARY_HOST_APPLICATIONS, messages.get("host"));
		systemDictNode(root, DICTIONARY_SHUTDOWN_APPLICATIONS, messages.get("shutdown"));
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
	 * Is it amendment application?
	 * @param curHis
	 * @return
	 */
	public boolean isAmend(History curHis) {
		Concept thisDictRoot = closureServ.getParent(curHis.getApplDict());
		return thisDictRoot.getIdentifier().equalsIgnoreCase(DICTIONARY_GUEST_AMENDMENTS);
	}
	/**
	 * Is it de-registration application?
	 * @param curHis
	 * @return
	 */
	public boolean isDeregistration(History curHis) {
		Concept thisDictRoot = closureServ.getParent(curHis.getApplDict());
		return thisDictRoot.getIdentifier().equalsIgnoreCase(DICTIONARY_GUEST_DEREGISTRATION);
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
			if(aurl.equalsIgnoreCase(processUrl) && conc.getActive()) {
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
	
	/**
	 * Find in "dictionary.guest.applications" guest workflow description by application url
	 * @param applicationUrl
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<Concept> guestWorkflows(String appicationUrl) throws ObjectNotFoundException {
		List<Concept> ret = new ArrayList<Concept>();
		ret = guestWorkflow(DICTIONARY_GUEST_APPLICATIONS,appicationUrl, ret);
		ret = guestWorkflow(DICTIONARY_GUEST_RENEWAL,appicationUrl, ret);
		ret = guestWorkflow(DICTIONARY_GUEST_AMENDMENTS,appicationUrl, ret);
		ret = guestWorkflow(DICTIONARY_GUEST_DEREGISTRATION,appicationUrl, ret);
		return ret;
	}
	/**
	 * Process a guest workflow dictionary
	 * @param guestDictUrl - url of the guest dictionary
	 * @param appicationUrl
	 * @param ret
	 * @throws ObjectNotFoundException
	 */
	public List<Concept> guestWorkflow(String guestDictUrl, String appicationUrl, List<Concept> ret) throws ObjectNotFoundException {
		List<OptionDTO> dictOpt = dictServ.loadPlain(guestDictUrl);
		for(OptionDTO opt : dictOpt) {
			Concept node = closureServ.loadConceptById(opt.getId());
			String applUrl = literalServ.readValue("applicationurl", node);
			if(applUrl.equalsIgnoreCase(appicationUrl)) {
				ret.add(node);
			}
		}
		return ret;
	}
	/**
	 * Ensure, that all system dictionaries are existing and have been initialized properly
	 * @throws ObjectNotFoundException 
	 */
	public void checkDictionaries() throws ObjectNotFoundException {
		checkAddressDict();
		userRolesDict(new DictionaryDTO());
		checkLifeCycleDicts();
		submitActionDictionary();
		uploadImportDataDictionary();
		finalizeDict();
	}

	/**
	 * Guest, Host and Shutdown
	 * @throws ObjectNotFoundException 
	 */
	private void checkLifeCycleDicts() throws ObjectNotFoundException {
		List<String> appl = applicationLifeCycleUrls();
		for(String url : appl) {
			dictServ.checkDictionary(url);
		}
	}
	/**
	 * List URLs of all lifecycle applications
	 * @return
	 */
	public List<String> applicationLifeCycleUrls() {
		List<String> ret = new ArrayList<String>();
		ret.add(DICTIONARY_GUEST_APPLICATIONS);
		ret.add(DICTIONARY_HOST_APPLICATIONS);
		ret.add(DICTIONARY_GUEST_AMENDMENTS);
		ret.add(DICTIONARY_GUEST_DEREGISTRATION);
		ret.add(DICTIONARY_SHUTDOWN_APPLICATIONS);
		return ret;
	}

	/**
	 * If address dictionary does not exist, create new one
	 * @throws ObjectNotFoundException 
	 */
	public void checkAddressDict() throws ObjectNotFoundException {
		dictServ.checkDictionary(DICTIONARY_ADMIN_UNITS);
	}

	/**
	 * Load a role concept from roles dictionary by role name
	 * @param roleName
	 * @return null, if not found
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept loadRole(String roleName) throws ObjectNotFoundException {
		Concept ret=null;
		Concept dict = closureServ.loadRoot(DICTIONARY_SYSTEM_ROLES);
		List<Concept> nodes = literalServ.loadOnlyChilds(dict);
		for(Concept node : nodes) {
			if(node.getActive()) {
				if(node.getIdentifier().equalsIgnoreCase(roleName)) {
					ret=node;
					break;
				}
			}
		}
		return ret;
	}
	/**
	 * Check and store full address under a selected
	 * @param addrUrl - url under which addresses are stored
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public String storeFullAddress(String addressUrl) throws ObjectNotFoundException {
		String message=messages.get("global.success");
		Concept root = closureServ.loadRoot(addressUrl);
		List<Concept> owners = closureServ.loadLevel(root);
		for(Concept owner : owners) {
			List<Concept> addresses = closureServ.loadLevel(owner);
			for(Concept addr : addresses) {
				Thing thing = boilerServ.thingByNode(addr);
				Set<ThingDict> tdset=thing.getDictionaries();
				if(tdset.size()==1) {
					ThingDict td =tdset.iterator().next();
					//System.out.println(addr.getLabel()+ " " + dictServ.dictPath("en_us", td.getConcept()));
					dictServ.storePath(td.getConcept(), addr);
				}else {
					message=messages.get("global_fail");
				}
			}
		}
		return message;
	}

}
