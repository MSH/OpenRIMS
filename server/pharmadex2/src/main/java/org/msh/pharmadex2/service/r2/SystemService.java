package org.msh.pharmadex2.service.r2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.msh.pdex2.dto.i18n.Language;
import org.msh.pdex2.dto.i18n.Languages;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.i18n.ResourceBundle;
import org.msh.pdex2.model.i18n.ResourceMessage;
import org.msh.pdex2.model.r2.User;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.Scheduler;
import org.msh.pdex2.model.r2.ThingScheduler;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.common.UserRepo;
import org.msh.pdex2.repository.i18n.ResourceBundleRepo;
import org.msh.pdex2.repository.i18n.ResourceMessageRepo;
import org.msh.pdex2.repository.r2.HistoryRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.ActivitySubmitDTO;
import org.msh.pharmadex2.dto.Dict2DTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.FormatsDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.auth.UserRoleDto;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Responsible for hard coded system setting In contrast AssemblyService.
 * 
 * @author alexk
 *
 */
@Service
public class SystemService {
	public static final String DS_CATALOGUE = "CATALOGUE";
	public static final String DS_KPI_APPLICANTS = "APPLICANTS";
	public static final String DS_KPI_WORKFLOWS = "WORKFLOWS";
	public static final String DS_KPI_ACTIVITIES = "ACTIVITIES";
	public static final String DS_KPI_APPLICATIONS = "APPLICATIONS";
	public static final String DS_STATISTIC = "STATISTIC";
	public static final Logger logger = LoggerFactory.getLogger(SystemService.class);
	// dictionary that is backed import data file upload
	public static final String DICTIONARY_SYSTEM_IMPORT_DATA = "dictionary.system.import.data";
	// Finalization outcomes values and the dictionary for them
	public static final String FINAL_DEREGISTRATION = "deregistration";
	public static final String FINAL_AMEND = "AMEND";
	public static final String FINAL_DECLINE = "DECLINE";
	public static final String FINAL_ACCEPT = "ACCEPT";
	public static final String FINAL_COMPANY = "COMPANY";
	public static final String FINAL_NO = "NO";
	public static final String DICTIONARY_SYSTEM_FINALIZATION = "dictionary.system.finalization";
	//possible applications
	public static final String DICTIONARY_SYSTEM_LIFECYCLE = "dictionary.system.lifecycle";	//references for dictionaries below 
	public static final String DICTIONARY_GUEST_DEREGISTRATION = "dictionary.guest.deregistration";
	public static final String DICTIONARY_GUEST_AMENDMENTS = "dictionary.guest.amendments";
	public static final String DICTIONARY_GUEST_APPLICATIONS = "dictionary.guest.applications";
	public static final String DICTIONARY_GUEST_INSPECTIONS = "dictionary.guest.inspections";
	public static final String DICTIONARY_SHUTDOWN_APPLICATIONS = "dictionary.shutdown.applications";
	public static final String DICTIONARY_HOST_APPLICATIONS = "dictionary.host.applications";
	//possible submit actions
	public static final String DICTIONARY_SYSTEM_SUBMIT = "dictionary.system.submit";
	//Access roles and tiles for them
	public static final String DICTIONARY_SYSTEM_ROLES = "dictionary.system.roles";
	public static final String ROOT_SYSTEM_TILES = "dictionary.system.tiles";
	//Data sources
	public static final String DICTIONARY_SYSTEM_DATA_SOURCES = "dictionary.system.data.sources";
	//administrative units and related
	public static final String DICTIONARY_ADMIN_UNITS = "dictionary.admin.units";
	public static final String CONFIGURATION_ADMIN_UNITS = "configuration.admin.units";
	public static final Integer DEFAULT_ZOOM = 7;
	public static List<String> APPLICATION_DICTIONARIES = Arrays.asList(DICTIONARY_GUEST_DEREGISTRATION,
			DICTIONARY_GUEST_AMENDMENTS, DICTIONARY_GUEST_APPLICATIONS, DICTIONARY_GUEST_INSPECTIONS,
			DICTIONARY_SHUTDOWN_APPLICATIONS, DICTIONARY_HOST_APPLICATIONS);
	public static List<String> SINLE_LEVEL_DICTIONARIES = Arrays.asList(DICTIONARY_SYSTEM_FINALIZATION,
			DICTIONARY_SYSTEM_LIFECYCLE, DICTIONARY_SYSTEM_SUBMIT, DICTIONARY_SYSTEM_ROLES);

	public static final String PRODUCTCLASSIFICATION_ATC_HUMAN = "who.atc.human";

	public static final String DICTIONARY_SYSTEM_LOCALES = "dictionary.system.locales";
	public static final String SUFF_RECEIPT_BYTEMPL = ".receipt";
	/**
	 * Tree for persons, not dictionary
	 */
	public static final String PERSON_URL = "user.data";

	public static final String FILE_STORAGE_BUSINESS = "file.storage.business";
	public static final String DATA_COLLECTIONS_ROOT = "configuration.data";
	public static final String RESOURCES_COLLECTIONS_ROOT = "configuration.resources";
	public static final String RECYCLE = "system.recycle.bin";

	// *************************** ROLES for WebSecurity.java
	public static String ROLE_ADMIN = "ROLE_ADMIN";
	public static String ROLE_SECRETARY = "ROLE_SECRETARY";
	public static String ROLE_APPLICANT = "APPLICANT";
	public static String[] ROLES = { ROLE_ADMIN, "ROLE_MODERATOR", "ROLE_REVIEWER", "ROLE_INSPECTOR", "ROLE_ACCOUNTANT",
			"ROLE_SCREENER", ROLE_SECRETARY, ROLE_APPLICANT };
	/**
	 * list of NMRA actors to use in ..mapping.. annotations
	 */
	public static final String ACTORS_NMRA = String.join(",", ROLES).toLowerCase().replace("role_", "");
	/**
	 * list of all authenticated actors to use in ..mapping.. annotations
	 */
	public static final String ACTORS_AUTHENTICATED = ACTORS_NMRA + ",guest";
	/**
	 * List of all possible actors to use in ..mapping.. annotations
	 */
	public static final String ACTORS_ALL = ACTORS_AUTHENTICATED + ",public";
	//Appliction states for DWH
	public static final String STATE_ACTIVE = "ACTIVE";
	public static final String STATE_ONAPPROVAL = "ONAPPROVAL";
	public static final String STATE_REVOKED = "REVOKED";
	public static final String STATE_LOST = "LOST";
	public static final String STATE_DEREGISTERED = "DEREGISTERED";

	@Autowired
	UserRepo userRepo;
	@Autowired
	PasswordEncoder password;
	@Autowired
	private DictService dictServ;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private Messages messages;
	@Autowired
	private HistoryRepo historyRepo;
	@Autowired
	private ValidationService validator;
	@Autowired
	private ResourceMessageRepo resourceMessageRepo;
	@Autowired
	private ResourceBundleRepo resourceBundleRepo;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private JdbcRepository jdbcRepo;

	/**
	 * Add role to roles dictionary
	 * 
	 * @param root
	 * @param role
	 * @throws ObjectNotFoundException
	 */
	public void addRoleToDictionary(Concept root, String role) throws ObjectNotFoundException {
		Concept node = new Concept();
		node.setIdentifier(role);
		node = closureServ.saveToTree(root, node);
		String prefLabel = literalServ.readPrefLabel(node);
		String description = literalServ.readDescription(node);
		if (prefLabel.length() == 0) {
			literalServ.createUpdatePrefLabel(role, node);
		}
		if (description.length() == 0) {
			literalServ.createUpdateDescription(role, node);
		}
	}

	/**
	 * Some system dictionary need to be actualize by removing deprecated roles
	 * These roles should be inactive to ensure backward compatibility
	 * 
	 * @param root
	 * @param roleToRemove
	 */
	@Transactional
	private void removeRoleFromDictionary(Concept root, String roleToRemove) {
		List<Concept> childs = closureServ.loadLevel(root);
		if (childs != null) {
			for (Concept child : childs) {
				if (child.getIdentifier().equalsIgnoreCase(roleToRemove)) {
					if (child.getActive()) {
						child.setActive(false);
						child = closureServ.save(child);
						break;
					}
				}
			}
		}

	}

	/**
	 * Loads or creates user's roles dictionary
	 * 
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public DictionaryDTO userRolesDict(DictionaryDTO dict) throws ObjectNotFoundException {
		dict.setUrl(DICTIONARY_SYSTEM_ROLES);
		// create or update the dictionary
		Concept root = closureServ.loadRoot(DICTIONARY_SYSTEM_ROLES);
		String rootPref = literalServ.readPrefLabel(root);
		String rootDescr = literalServ.readDescription(root);
		if (rootPref.length() == 0) {
			literalServ.createUpdatePrefLabel(messages.get("role"), root);
		}
		if (rootDescr.length() == 0) {
			literalServ.createUpdateDescription(messages.get("role"), root);
		}
		literalServ.createUpdateLiteral("type", "system", root);
		for (String role : ROLES) {
			addRoleToDictionary(root, role);
		}
		dict = dictServ.createDictionary(dict);
		dict.setMult(true);
		dict.setSystem(true);
		return dict;
	}

	/**
	 * Possible finalization scenarious
	 * 
	 * @param dictionaryDTO
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private void finalizeDict() throws ObjectNotFoundException {
		// create or update the dictionary
		Concept root = closureServ.loadRoot(DICTIONARY_SYSTEM_FINALIZATION);
		String rootPref = literalServ.readPrefLabel(root);
		String rootDescr = literalServ.readDescription(root);
		if (rootPref.length() == 0) {
			literalServ.createUpdatePrefLabel(messages.get("finalize"), root);
		}
		if (rootDescr.length() == 0) {
			literalServ.createUpdateDescription(messages.get("finalize"), root);
		}
		literalServ.createUpdateLiteral("type", "system", root);
		addRoleToDictionary(root, FINAL_NO);
		addRoleToDictionary(root, FINAL_ACCEPT);
		addRoleToDictionary(root, FINAL_DECLINE);
		removeRoleFromDictionary(root, FINAL_AMEND);
		removeRoleFromDictionary(root, FINAL_DEREGISTRATION);
		addRoleToDictionary(root, FINAL_COMPANY);
	}

	/**
	 * Address or Admin unit dictionary. Allows to add new
	 * 
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
	 * 
	 * @deprecated
	 * @param url
	 * 
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public DictionaryDTO applicationsDictionary(String url, DictionaryDTO data) throws ObjectNotFoundException {
		data.setUrl(url);
		Concept root = closureServ.loadRoot(data.getUrl());
		String prefLabel = literalServ.readPrefLabel(root);
		String descr = literalServ.readDescription(root);
		if (prefLabel.length() == 0) {
			prefLabel = messages.get("newapplications");
			descr = "";
			literalServ.prefAndDescription(prefLabel, descr, root);
		}
		data = dictServ.createDictionary(data);
		return data;
	}

	public DictionaryDTO processesEnabled(String url, String email, DictionaryDTO data) throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot(url);
		String prefLabel = literalServ.readPrefLabel(root);
		// if (prefLabel.length() == 0) {
		if (url.equals(DICTIONARY_GUEST_APPLICATIONS)) {
			prefLabel = messages.get("prod_app_type");
		} else if (url.equals(DICTIONARY_GUEST_AMENDMENTS)) {
			prefLabel = messages.get("amdmt_type");
		} else if (url.equals(DICTIONARY_GUEST_DEREGISTRATION)) {
			prefLabel = messages.get("deregistration");
		} else if (url.equals(DICTIONARY_GUEST_INSPECTIONS)) {
			prefLabel = messages.get("inspections");
		} else {
			prefLabel = messages.get("newapplications");
		}
		// }
		data.setHome(prefLabel);

		TableQtb table = data.getTable();
		if (table.getHeaders().getHeaders().size() == 0) {
			table.setHeaders(dictServ.createHeaders(table.getHeaders(), true));
		}
		// add temporary column not display
		table.getHeaders().getHeaders().add(TableHeader.instanceOf("active", TableHeader.COLUMN_LONG));
		jdbcRepo.processesEnabled(url, email);
		String select = "select * from processes_enabled";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", "", table.getHeaders());

		String style = "text";
		String styleEnabled = "font-weight-light font-italic";
		for (TableRow row : rows) {
			int active = row.getCellByKey("active").getIntValue();
			if (active == 0) {
				for (TableCell cell : row.getRow()) {
					cell.setStyleClass(styleEnabled);
				}
			}
			row.getRow().remove(2);
		}
		// remove temporary column
		table.getHeaders().getHeaders().remove(2);

		TableQtb.tablePage(rows, table);
		table = boilerServ.translateRows(table);
		table.setSelectable(true);
		data.setTable(table);
		return data;
	}

	/**
	 * All amendment types implemented in the system
	 * 
	 * @deprecated
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public DictionaryDTO amendmentDictionary(DictionaryDTO data) throws ObjectNotFoundException {
		data.setUrl(DICTIONARY_GUEST_AMENDMENTS);
		Concept root = closureServ.loadRoot(data.getUrl());
		String prefLabel = literalServ.readPrefLabel(root);
		String descr = literalServ.readDescription(root);
		if (prefLabel.length() == 0) {
			prefLabel = messages.get("amdmt_type");
			descr = "";
		}
		literalServ.prefAndDescription(prefLabel, descr, root);
		data = dictServ.createDictionary(data);
		return data;
	}

	/**
	 * DE-registration applications
	 * 
	 * @deprecated
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public DictionaryDTO deregistrationDict(DictionaryDTO data) throws ObjectNotFoundException {
		data.setUrl(DICTIONARY_GUEST_DEREGISTRATION);
		Concept root = closureServ.loadRoot(data.getUrl());
		String prefLabel = literalServ.readPrefLabel(root);
		String descr = literalServ.readDescription(root);
		if (prefLabel.length() == 0) {
			prefLabel = messages.get("suspend_info");
			descr = "";
		}
		literalServ.prefAndDescription(prefLabel, descr, root);
		data = dictServ.createDictionary(data);
		return data;
	}

	/**
	 * Get nemes of user's roles from the dictionary
	 * 
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Map<String, String> roleNames() throws ObjectNotFoundException {
		Map<String, String> ret = new LinkedHashMap<String, String>();
		Concept root = closureServ.loadRoot(DICTIONARY_SYSTEM_ROLES);
		List<Concept> nodes = literalServ.loadOnlyChilds(root);
		for (Concept node : nodes) {
			String pref = literalServ.readPrefLabel(node);
			ret.put(node.getIdentifier(), pref);
		}
		return ret;
	}

	/**
	 * Get URL of node
	 * 
	 * @param nodeId
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public String objectUrl(long nodeId) throws ObjectNotFoundException {
		String ret = "";
		Concept node = closureServ.loadConceptById(nodeId);
		ret = closureServ.getUrlByNode(node);
		return ret;
	}

	/**
	 * Submit actions for all
	 * 
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public DictionaryDTO submitActionDictionary() throws ObjectNotFoundException {
		DictionaryDTO ret = new DictionaryDTO();
		ret.setUrl(DICTIONARY_SYSTEM_SUBMIT);
		Concept root = closureServ.loadRoot(DICTIONARY_SYSTEM_SUBMIT);
		String prefLabel = literalServ.readPrefLabel(root);
		String descr = literalServ.readDescription(root);
		if (prefLabel.length() == 0 && descr.length() == 0) {
			root = literalServ.prefAndDescription(messages.get("Submit actions"), "", root);
			literalServ.createUpdateLiteral("type", "system", root);
		}
		List<Concept> level = literalServ.loadOnlyChilds(root);
		if (level.size() != 12) {
			// create level
			systemDictNode(root, "0", messages.get("continue"));
			systemDictNode(root, "1", messages.get("route_action"));
			systemDictNode(root, "2", messages.get("newactivity"));
			systemDictNode(root, "3", messages.get("cancel"));
			systemDictNode(root, "4", messages.get("approve"));
			systemDictNode(root, "5", messages.get("reject"));
			systemDictNode(root, "6", messages.get("reassign"));
			systemDictNode(root, "7", messages.get("amendment"));
			systemDictNode(root, "8", messages.get("deregistration"));
			systemDictNode(root, "9", messages.get("revokepermit"));
			systemDictNode(root, "10", messages.get("decline"));
			systemDictNode(root, "11", messages.get("run"));
		}
		ret = dictServ.createDictionary(ret);
		ret.setMult(false);
		ret.setSystem(true);
		return ret;
	}

	/**
	 * Upload data for import dictionary
	 * 
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private DictionaryDTO uploadImportDataDictionary() throws ObjectNotFoundException {
		DictionaryDTO ret = new DictionaryDTO();
		ret.setUrl(DICTIONARY_SYSTEM_IMPORT_DATA);
		Concept root = closureServ.loadRoot(DICTIONARY_SYSTEM_IMPORT_DATA);
		String prefLabel = literalServ.readPrefLabel(root);
		String descr = literalServ.readDescription(root);
		if (prefLabel.length() == 0 && descr.length() == 0) {
			root = literalServ.prefAndDescription(messages.get("dataimport"), "", root);
			literalServ.createUpdateLiteral("type", "system", root);
		}
		List<Concept> level = literalServ.loadOnlyChilds(root);
		if (level.size() == 0) {
			systemDictNode(root, AssemblyService.DATAIMPORT_DATA, "File contains data to import (XLSX)");
			systemDictNode(root, AssemblyService.DATAIMPORT_DATA_ERROR, "File with data import errors(XLSX)");
		} else if (level.size() == 1) {
			Concept c = level.get(0);
			c.setIdentifier(AssemblyService.DATAIMPORT_DATA);
			closureServ.save(c);
			systemDictNode(root, AssemblyService.DATAIMPORT_DATA_ERROR, "XLSX file contains data import errors");
		}
		ret = dictServ.createDictionary(ret);
		ret.setMult(false);
		ret.setSystem(true);
		return ret;

	}

	/**
	 * Add a node to the system application conclusion dictionary
	 * 
	 * @param root
	 */
	private Concept systemDictNode(Concept root, String identifier, String prefLabel) throws ObjectNotFoundException {
		List<Concept> nodes=literalServ.loadOnlyChilds(root);
		for(Concept node :nodes) {
			if(node.getIdentifier().equalsIgnoreCase(identifier)) {
				return node;
			}
		}
		//create the node if not found
		Concept node = new Concept();
		node.setIdentifier(identifier);
		node = closureServ.saveToTree(root, node);
		String pl = literalServ.readPrefLabel(node);
		if (pl.length() == 0) {
			node = literalServ.prefAndDescription(prefLabel, "", node);
		}
		return node;
	}

	/**
	 * Responsible to work with system dictionaries Master dictionary is process
	 * stages Slave one is one workflow configurations
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Dict2DTO stagesWorkflow(Dict2DTO data) throws ObjectNotFoundException {
		if (data.getMasterDict().getUrl().length() == 0) {
			data.setMasterDict(stagesDictionary());
		}

		Set<Long> stageNodeIds = dictServ.selectedItems(data.getMasterDict());
		if (stageNodeIds.size() == 1) {
			Concept stageNode = closureServ.loadConceptById(stageNodeIds.iterator().next());
			// select a second dictionary
			if (!data.getSlaveDict().getUrl().equalsIgnoreCase(stageNode.getIdentifier())) {
				data.setSlaveDict(workflowDictionary(stageNode.getIdentifier()));
			}
		} else {
			data.setSlaveDict(new DictionaryDTO());
		}
		return data;
	}

	/**
	 * Workflow dictionary may or may not be created yet.
	 * 
	 * @param dictUrl
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public DictionaryDTO workflowDictionary(String dictUrl) throws ObjectNotFoundException {
		DictionaryDTO ret = new DictionaryDTO();
		ret.setUrl(dictUrl);
		ret = dictServ.createDictionary(ret);
		if (ret.getHome().length() == 0) {
			Concept root = closureServ.loadRoot(dictUrl);
			literalServ.createUpdatePrefLabel(messages.get(dictUrl), root);
			String descr = literalServ.readDescription(root);
			if (descr.length() == 0) {
				literalServ.createUpdateDescription("description", root);
			}
			ret = dictServ.createDictionary(ret);
		}
		return ret;
	}

	/**
	 * Create new or load dictionary.system.lifecycle
	 * 
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public DictionaryDTO stagesDictionary() throws ObjectNotFoundException {
		DictionaryDTO ret = new DictionaryDTO();
		ret.setUrl(DICTIONARY_SYSTEM_LIFECYCLE);
		Concept root = closureServ.loadRoot(DICTIONARY_SYSTEM_LIFECYCLE);
		String prefLabel = literalServ.readPrefLabel(root);
		String descr = literalServ.readDescription(root);
		if (prefLabel.length() == 0 && descr.length() == 0) {
			root = literalServ.prefAndDescription(messages.get("processes"), "", root);
			literalServ.createUpdateLiteral("type", "system", root);
		}
		// create level
		systemDictNode(root, DICTIONARY_GUEST_APPLICATIONS, messages.get("guest"));
		systemDictNode(root, DICTIONARY_GUEST_INSPECTIONS, messages.get("inspections"));
		systemDictNode(root, DICTIONARY_GUEST_AMENDMENTS, messages.get("amdmt_type"));
		systemDictNode(root, DICTIONARY_GUEST_DEREGISTRATION, messages.get(FINAL_DEREGISTRATION));
		systemDictNode(root, DICTIONARY_HOST_APPLICATIONS, messages.get("host"));
		systemDictNode(root, DICTIONARY_SHUTDOWN_APPLICATIONS, messages.get("shutdown"));
		ret = dictServ.createDictionary(ret);
		ret.setMult(false);
		ret.setSystem(true);
		return ret;
	}

	/**
	 * Determine the next stage dictionary URL by this stage dictionary entry
	 * 
	 * @param his      the current history record
	 * @param approved - action, true is approved, otherwise is rejected
	 * @return the dictionary url for the next stage, empty string after shutdown
	 *         stage
	 */
	@Transactional
	public String nextStageByApplDict(History his, boolean approved) {
		String ret = "";
		if (isGuest(his)) {
			return DICTIONARY_HOST_APPLICATIONS;
		}
		if (isHost(his)) {
			if (approved) {
				return DICTIONARY_HOST_APPLICATIONS;
			} else {
				return DICTIONARY_SHUTDOWN_APPLICATIONS;
			}
		}
		return ret;
	}

	/**
	 * Is current stage GUEST?
	 * 
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
	 * 
	 * @param curHis
	 * @return
	 */
	public boolean isHost(History curHis) {
		Concept thisDictRoot = closureServ.getParent(curHis.getApplDict());
		return thisDictRoot.getIdentifier().equalsIgnoreCase(DICTIONARY_HOST_APPLICATIONS);
	}

	/**
	 * Is it amendment application?
	 * 
	 * @param curHis
	 * @return
	 */
	public boolean isAmend(History curHis) {
		Concept thisDictRoot = closureServ.getParent(curHis.getApplDict());
		return thisDictRoot.getIdentifier().equalsIgnoreCase(DICTIONARY_GUEST_AMENDMENTS);
	}

	/**
	 * Is it de-registration application?
	 * 
	 * @param curHis
	 * @return
	 */
	public boolean isDeregistration(History curHis) {
		Concept thisDictRoot = closureServ.getParent(curHis.getApplDict());
		return thisDictRoot.getIdentifier().equalsIgnoreCase(DICTIONARY_GUEST_DEREGISTRATION);
	}

	/**
	 * Is it shutdown application?
	 * 
	 * @param curHis
	 * @return
	 */
	public boolean isShutdown(History curHis) {
		Concept thisDictRoot = closureServ.getParent(curHis.getApplDict());
		return thisDictRoot.getIdentifier().equalsIgnoreCase(DICTIONARY_SHUTDOWN_APPLICATIONS);
	}

	/**
	 * Is it shutdown application?
	 * 
	 * @param curHis
	 * @return
	 */
	public boolean isGuestInspection(History curHis) {
		Concept thisDictRoot = closureServ.getParent(curHis.getApplDict());
		return thisDictRoot.getIdentifier().equalsIgnoreCase(DICTIONARY_GUEST_INSPECTIONS);
	}

	/**
	 * Recognize host dictionary node by host process URL
	 * 
	 * @param dictRootUrl URL of the dictionary to search in
	 * @param processUrl  URL of the applicationurl in the dictionary item
	 * @return dictionary item
	 * @throws ObjectNotFoundException is not found
	 */
	@Transactional
	public Concept applDictItemByUrl(String dictRootUrl, String processUrl) throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot(dictRootUrl);
		List<Concept> level = literalServ.loadOnlyChilds(root);
		for (Concept conc : level) {
			String aurl = literalServ.readValue(LiteralService.APPLICATION_URL, conc);
			if (aurl.equalsIgnoreCase(processUrl) && conc.getActive()) {
				return conc;
			}
		}
		throw new ObjectNotFoundException("dictionary node for host process not found. URL is - " + processUrl, logger);
	}

	/**
	 * Recognize revokepermit dictionary node by revokepermit process URL
	 * 
	 * @param processUrl
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Concept revokepermitDictNode(String processUrl) throws ObjectNotFoundException {

		Concept root = new Concept();

		try {
			root = closureServ.loadRoot(DICTIONARY_SHUTDOWN_APPLICATIONS);
		} catch (ObjectNotFoundException e) {
			throw new ObjectNotFoundException("No dictionary DICTIONARY_SHUTDOWN_APPLICATIONS " + e, logger);
		}
		List<Concept> level = literalServ.loadOnlyChilds(root);
		Concept conDict = null;
		for (Concept conc : level) {
			String aurl = literalServ.readValue(LiteralService.URL, conc);
			if (aurl.equalsIgnoreCase(processUrl) && conc.getActive()) {
				conDict = conc;
			}
		}
		return conDict;
	}

	/**
	 * For Example Find concept "New Retail Pharmacy - Individually
	 * Owned"(url=retail.site.owned.persons) in dictionary
	 * dictionary.guest.applications
	 * 
	 * @param dictUrl
	 * @param processUrl
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Concept findProccessByUrl(String dictUrl, String processUrl) throws ObjectNotFoundException {
		Concept root = new Concept();
		try {
			root = closureServ.loadRoot(dictUrl);
		} catch (ObjectNotFoundException e) {
			throw new ObjectNotFoundException("No dictionary " + dictUrl + " " + e, logger);
		}
		List<Concept> level = literalServ.loadOnlyChilds(root);
		Concept conDict = null;
		for (Concept conc : level) {
			String aurl = literalServ.readValue(LiteralService.URL, conc);
			if (aurl.equalsIgnoreCase(processUrl) && conc.getActive()) {
				conDict = conc;
				break;
			}
		}
		return conDict;
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
		String auth = "public";
		if (authority.size() == 1) {
			auth = authority.get(0).getAuthority();
		}
		DictionaryDTO ret = new DictionaryDTO();
		ret.setUrl("dictionary.reports." + auth);
		Concept root = closureServ.loadRoot(ret.getUrl());
		String prefLabel = literalServ.readPrefLabel(root);
		String descr = literalServ.readDescription(root);
		if (prefLabel.length() == 0 && descr.length() == 0) {
			root = literalServ.prefAndDescription("reports for " + auth, "", root);
		}
		ret.setMult(false);
		ret = dictServ.createDictionary(ret);
		return ret;
	}

	/**
	 * Find in "dictionary.guest.applications" guest workflow description by
	 * application url
	 * 
	 * @param applicationUrl
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public List<Concept> guestWorkflows(String appicationUrl) throws ObjectNotFoundException {
		List<Concept> ret = new ArrayList<Concept>();
		ret = guestWorkflow(DICTIONARY_GUEST_APPLICATIONS, appicationUrl, ret);
		ret = guestWorkflow(DICTIONARY_GUEST_AMENDMENTS, appicationUrl, ret);
		ret = guestWorkflow(DICTIONARY_GUEST_DEREGISTRATION, appicationUrl, ret);
		return ret;
	}

	/**
	 * Process a guest workflow dictionary
	 * 
	 * @param guestDictUrl  - url of the guest dictionary
	 * @param appicationUrl
	 * @param ret
	 * @throws ObjectNotFoundException
	 */
	public List<Concept> guestWorkflow(String guestDictUrl, String appicationUrl, List<Concept> ret)
			throws ObjectNotFoundException {
		// 02112023 khomenska refactoring code
		// List<OptionDTO> dictOpt = dictServ.loadPlain(guestDictUrl);
		Concept root = closureServ.loadRoot(guestDictUrl);
		List<OptionDTO> dictOpt = dictServ.loadLevelAsOptions(root);
		for (OptionDTO opt : dictOpt) {
			Concept node = closureServ.loadConceptById(opt.getId());
			String applUrl = literalServ.readValue("applicationurl", node);
			if (applUrl.equalsIgnoreCase(appicationUrl)) {
				ret.add(node);
			}
		}
		return ret;
	}

	/**
	 * Ensure, that all system dictionaries are existing and have been initialized
	 * properly
	 * 
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public void checkDictionaries() throws ObjectNotFoundException {
		checkAddressDict();
		userRolesDict(new DictionaryDTO());
		checkLifeCycleDicts();
		submitActionDictionary();
		uploadImportDataDictionary();
		inspectionDictionaries();
		finalizeDict();
		languagesDict();
		createDS();
	}



	/**
	 * create data sources dictionary
	 * @throws ObjectNotFoundException 
	 */
	private void createDS() throws ObjectNotFoundException {
		dictServ.checkDictionary(DICTIONARY_SYSTEM_DATA_SOURCES);
		Concept dict=closureServ.loadRoot(DICTIONARY_SYSTEM_DATA_SOURCES);
		literalServ.createUpdateLiteral("type", "system", dict);
		List<Concept> nodes = literalServ.loadOnlyChilds(dict);
		if(nodes.size()!=3) {
			dict=createDSCat(dict);
			dict=createDSKPI(dict);
			dict=createDSStat(dict);
		}

	}
	/**
	 * Statistical data sources
	 * @param dict
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private Concept createDSStat(Concept dict) throws ObjectNotFoundException {
		Concept	node=systemDictNode(dict, DS_STATISTIC, messages.get("statistic"));
		systemDictNode(node, "PRODUCT_SHARE", messages.get("products_share"));
		systemDictNode(node, "APPLICANT_SHARE", messages.get("applicants_share"));
		return dict;
	}

	/**
	 * KPI for data sources
	 * @param dict
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private Concept createDSKPI(Concept dict) throws ObjectNotFoundException {
		Concept node=systemDictNode(dict, "KPI", messages.get("Indicators"));
		systemDictNode(node, DS_KPI_APPLICATIONS, messages.get("manageapplications"));
		systemDictNode(node, DS_KPI_ACTIVITIES, messages.get("activities"));
		systemDictNode(node, DS_KPI_WORKFLOWS, messages.get("workflows"));
		systemDictNode(node, DS_KPI_APPLICANTS, messages.get("applicants"));
		return dict;
	}

	/**
	 * Catalogues branch in the data sources 
	 * @param dict
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private Concept createDSCat(Concept dict) throws ObjectNotFoundException {
		systemDictNode(dict, DS_CATALOGUE, messages.get("catalogue"));
		return dict;
	}

	/**
	 * Create and fill out the languages dict if one is empty public because
	 * transactional
	 * 
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public void languagesDict() throws ObjectNotFoundException {
		dictServ.checkDictionary(DICTIONARY_SYSTEM_LOCALES);
		Languages langs = messages.getLanguages();
		Concept dict = closureServ.loadRoot(DICTIONARY_SYSTEM_LOCALES);
		List<Concept> nodes = literalServ.loadOnlyChilds(dict);
		Map<String, Concept> nodesMap = new HashMap<String, Concept>();
		Map<String, String> otherLangs = new HashMap<String, String>();
		otherLangs.put("pt_PT", "Português");
		otherLangs.put("pt_BR", "Português Brasil");
		otherLangs.put("fr_FR", "Français");
		otherLangs.put("de_DE", "Deutsch");
		otherLangs.put("es_ES", "Española");
		otherLangs.put("bn", "Bangla");
		otherLangs.put("ne_NP", "नेपाली");
		// existing nodes
		for (Concept node : nodes) {
			if (node.getActive()) {
				nodesMap.put(node.getIdentifier(), node);
			}
		}
		// add locales defined in the resource bundle
		for (Language lang : langs.getLangs()) {
			if (!nodesMap.containsKey(lang.getLocaleAsString())) {
				if (!lang.getLocaleAsString().equalsIgnoreCase("EN_US")) {
					Concept node = new Concept();
					node.setIdentifier(lang.getLocaleAsString());
					node = closureServ.saveToTree(dict, node);
					nodesMap.put(node.getIdentifier(), node);
					literalServ.prefAndDescription(lang.getLocaleAsString(), lang.getDisplayName(), node);
				}
			}
		}
		// add rst of the most used locales
		for (String key : otherLangs.keySet()) {
			if (!nodesMap.containsKey(key)) {
				Concept node = new Concept();
				node.setIdentifier(key);
				node = closureServ.saveToTree(dict, node);
				literalServ.prefAndDescription(key, otherLangs.get(key), node);
			}
		}
	}

	/**
	 * inspection's guest, host and inspection
	 * 
	 * @throws ObjectNotFoundException
	 */
	private void inspectionDictionaries() throws ObjectNotFoundException {
		List<String> appl = inspectionDictUrs();
		for (String url : appl) {
			dictServ.checkDictionary(url);
		}

	}

	private List<String> inspectionDictUrs() {
		List<String> ret = new ArrayList<String>();
		ret.add(SystemService.DICTIONARY_GUEST_INSPECTIONS);
		//ret.add(SystemService.DICTIONARY_INSPECTIONS);
		return ret;
	}

	/**
	 * Guest, Host, Shutdown, etc.
	 * 
	 * @throws ObjectNotFoundException
	 */
	private void checkLifeCycleDicts() throws ObjectNotFoundException {
		for (String url : APPLICATION_DICTIONARIES) {
			dictServ.checkDictionary(url);
		}
	}

	/**
	 * If address dictionary does not exist, create new one
	 * 
	 * @throws ObjectNotFoundException
	 */
	public void checkAddressDict() throws ObjectNotFoundException {
		dictServ.checkDictionary(DICTIONARY_ADMIN_UNITS);
	}
	/**
	 * Load a role concept from roles dictionary by role name
	 * 
	 * @param roleName
	 * @return null, if not found
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Concept loadRole(String roleName) throws ObjectNotFoundException {
		Concept ret = null;
		Concept dict = closureServ.loadRoot(DICTIONARY_SYSTEM_ROLES);
		List<Concept> nodes = literalServ.loadOnlyChilds(dict);
		for (Concept node : nodes) {
			if (node.getActive()) {
				if (node.getIdentifier().equalsIgnoreCase(roleName)) {
					ret = node;
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * Is the same process to the same data is already running?
	 * 
	 * @param dictItem
	 * @param applicationData
	 * @return not running processes found
	 */
	@Transactional
	public boolean isUniqueProcess(Concept dictItem, Concept applicationData) {
		List<History> list = historyRepo.findAllByApplDictAndApplicationDataAndGo(dictItem, applicationData, null);
		return list.isEmpty();
	}

	/**
	 * Initialize dates format form
	 * 
	 * @param data
	 * @return
	 */
	public FormatsDTO formatDates(FormatsDTO data) {
		// initial format
		String format = "MMM dd yyyy";
		if (!Messages.dateFormat.isEmpty()) {
			format = Messages.dateFormat;
		}
		data.getFormatDate().setValue(format);
		formatSamples(data);
		return data;
	}

	/**
	 * Format samples in the custom format form
	 * 
	 * @param data
	 */
	public FormatsDTO formatSamples(FormatsDTO data) {
		// format samples
		LocalDate ld = data.getDateInputSample().getValue();
		data.setDateDisplaySample(FormFieldDTO.of(ld));
		data.setDateCell(TableCell.instanceOf(data.getDateCell().getKey(), ld, LocaleContextHolder.getLocale()));
		// format hints
		TableQtb table = data.getTable();
		table.getHeaders().getHeaders().clear();
		table.getRows().clear();
		table.getHeaders().getHeaders()
		.add(TableHeader.instanceOf("pattern", messages.get("pattern"), 0, TableHeader.COLUMN_STRING));
		table.getHeaders().getHeaders()
		.add(TableHeader.instanceOf("meaning", messages.get("meaning"), 0, TableHeader.COLUMN_STRING));
		table.getHeaders().getHeaders()
		.add(TableHeader.instanceOf("samples", messages.get("samples"), 0, TableHeader.COLUMN_STRING));
		table.getRows().add(formatTableRow(ld, "yy"));
		table.getRows().add(formatTableRow(ld, "yyyy"));
		table.getRows().add(formatTableRow(ld, "MM"));
		table.getRows().add(formatTableRow(ld, "MMM"));
		table.getRows().add(formatTableRow(ld, "MMMM"));
		table.getRows().add(formatTableRow(ld, "MM"));
		table.getRows().add(formatTableRow(ld, "dd"));
		table.setSelectable(false);
		// set suggestes
		data.getDateInputSample().setSuggest(messages.get("inputformatting"));
		data.getDateInputSample().setError(true);
		return data;
	}

	/**
	 * A row in the proposed format table
	 * 
	 * @param ld
	 * @param pattern
	 * @return
	 */
	public TableRow formatTableRow(LocalDate ld, String pattern) {
		TableRow row = TableRow.instanceOf(1l);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern).withLocale(LocaleContextHolder.getLocale());
		row.getRow().add(TableCell.instanceOf("pattern", pattern));
		row.getRow().add(TableCell.instanceOf("meaning", messages.get(pattern)));
		row.getRow().add(TableCell.instanceOf("samples", ld.format(dtf)));
		return row;
	}

	/**
	 * Save new date format definition into the Messages
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public FormatsDTO formatsSave(FormatsDTO data) throws ObjectNotFoundException {
		data = validator.format(data);
		if (data.isValid()) {
			Messages.dateFormat = data.getFormatDate().getValue();
			Iterable<ResourceBundle> bundleList = resourceBundleRepo.findAll();
			for (ResourceBundle rb : bundleList) {
				rb = formatForLanguageSave(Messages.dateFormat, "dateFormat", LocaleContextHolder.getLocale(), rb);
			}
			// reload messages
			messages.getMessages().clear();
			messages.loadLanguages();
		}
		return data;
	}

	/**
	 * Save or create new format for language defined
	 * 
	 * @param formatValue
	 * @param formatKey
	 * @param currentLocale
	 * @param rb
	 * @return
	 */
	private ResourceBundle formatForLanguageSave(String formatValue, String formatKey, Locale currentLocale,
			ResourceBundle rb) {
		ResourceMessage mess = new ResourceMessage();
		mess.setMessage_key(formatKey);
		mess.setMessage_value(formatValue);
		for (ResourceMessage rm : rb.getMessages()) {
			if (rm.getMessage_key().equalsIgnoreCase(formatKey)) {
				mess = rm;
				break;
			}
		}
		if (mess.getId() == 0l) {
			rb.getMessages().add(mess);
		} else {
			if (rb.getLocale().equalsIgnoreCase(messages.getCurrentLocaleStr())) {
				mess.setMessage_value(formatValue); // for the existing messages, only for the current locale
			}
		}
		mess = resourceMessageRepo.save(mess);
		rb = resourceBundleRepo.save(rb);
		return rb;
	}

	@Transactional
	public ActivitySubmitDTO validSchedulers(ActivitySubmitDTO data) throws ObjectNotFoundException {
		List<String> list = new ArrayList<String>();
		for (TableRow row : data.getScheduled().getRows()) {
			ThingScheduler ts = boilerServ.thingSchedulerById(row.getDbID());
			Scheduler sch = boilerServ.schedulerByNode(ts.getConcept());
			try { // 03102023 khomenska
				Concept dictConc = applDictItemByUrl(SystemService.DICTIONARY_HOST_APPLICATIONS, sch.getProcessUrl());
			} catch (ObjectNotFoundException e) {
				list.add(sch.getProcessUrl());
			}
		}
		if (list.size() > 0) {
			String err = "dictionary node for host process not found. URLs are - ";
			for (String l : list) {
				err += l + "; ";
			}
			data.setValid(false);
			data.setIdentifier(err);
		}
		return data;
	}

	/**
	 * Assign default passwords=MD5 representation of user's raw password
	 */
	public void assignDefaultPasswords() {
		Iterable<User> users = userRepo.findAll();
		if (users != null) {
			for (User u : users) {
				if (u.getPassword() == null) {
					u.setPassword(password.encode(u.getUsername()));
					userRepo.save(u);
				}
				if (u.getPassword().equalsIgnoreCase("123")) {
					u.setPassword(password.encode(";f,jtl12_hfp"));
					userRepo.save(u);
				}
			}
		}
	}
}
