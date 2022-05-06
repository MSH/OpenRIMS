package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingAmendment;
import org.msh.pdex2.model.r2.ThingDict;
import org.msh.pdex2.model.r2.ThingDoc;
import org.msh.pdex2.model.r2.ThingOld;
import org.msh.pdex2.model.r2.ThingPerson;
import org.msh.pdex2.model.r2.ThingThing;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.r2.ThingAmendmentRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.ActivityDTO;
import org.msh.pharmadex2.dto.ActivitySubmitDTO;
import org.msh.pharmadex2.dto.AmendmentDTO;
import org.msh.pharmadex2.dto.AmendmentNewDTO;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.DataUnitDTO;
import org.msh.pharmadex2.dto.FileDTO;
import org.msh.pharmadex2.dto.PersonDTO;
import org.msh.pharmadex2.dto.PersonSelectorDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Amendment related services
 * 
 * @author alexk
 *
 */
@Service
public class AmendmentService {
	public static final String REMOVE_PERSON = "_REMOVE_PERSON_";
	private static final Logger logger = LoggerFactory.getLogger(AmendmentService.class);
	@Autowired
	private AssemblyService assemblyServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private Messages mess;
	@Autowired
	private ThingAmendmentRepo thingAmendmentRepo;
	@Autowired
	private ValidationService validServ;

	/**
	 * Save an amendment data
	 * 
	 * @param user
	 * @param node
	 * @param thing
	 * @param data
	 * @param dto
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ThingDTO save(UserDetailsDTO user, Concept node, Thing thing, ThingDTO data, AmendmentDTO dto)
			throws ObjectNotFoundException {
		// get selected
		long id = 0;
		for (TableRow row : dto.getTable().getRows()) {
			if (row.getSelected()) {
				id = row.getDbID();
			}
		}
		if (id > 0) {
			Concept amended = closureServ.loadConceptById(id);
			String prefLabel = literalServ.readPrefLabel(amended);
			literalServ.createUpdatePrefLabel(prefLabel, node);
			data.setTitle(prefLabel);
			thing.getAmendments().clear();
			ThingAmendment ta = new ThingAmendment();
			ta.setConcept(amended);
			ta.setUrl(dto.getUrl());
			ta.setVarName(dto.getVarName());
			thing.getAmendments().add(ta);
		}
		return data;
	}

	/**
	 * Create a person selector table from an amended data
	 * 
	 * @param dto
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public PersonSelectorDTO personSelectorTable(PersonSelectorDTO dto) throws ObjectNotFoundException {
		if (dto.getHistoryId() > 0) {
			History his = boilerServ.historyById(dto.getHistoryId());
			Concept amended = amendedConcept(his.getApplicationData());
			if (amended.getID() > 0) {
				TableQtb table = dto.getTable();
				if (table.getHeaders().getHeaders().size() == 0) {
					table.setHeaders(boilerServ.headersPersonSelector(table.getHeaders()));
				}
				List<Long> selected = boilerServ.saveSelectedRows(table);
				// get data
				String lang = LocaleContextHolder.getLocale().toString().toUpperCase();
				String where = "appldataid='" + amended.getID() +
						// "' and personrooturl='"+dto.getPersonUrl()+
						"' and lang='" + lang + "'";
				List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from personlist", "", where,
						table.getHeaders());
				TableQtb.tablePage(rows, table);
				table = boilerServ.selectedRowsRestore(selected, table);
			}
		}
		return dto;
	}

	/**
	 * Search for of amended data in the original application unit using amendment application
	 * It is presumed that the link to
	 * ameded data is always at the root thing!
	 * 
	 * @param applicationData amendment application
	 * @return ID>0 if found, otherwise ID==0
	 */
	@Transactional
	public Concept amendedConcept(Concept applicationData) {
		Concept ret = new Concept();
		if (applicationData.getID() > 0) {
			Thing thing = new Thing();
			thing = boilerServ.thingByNode(applicationData, thing);
			if (thing.getID() > 0) {
				for (ThingAmendment ta : thing.getAmendments()) {
					ret = ta.getConcept();
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * If the next step is Finalize.AMEND, implement the amendment
	 * 
	 * @param curHis
	 * @param nextActConf
	 * @param user 
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ActivitySubmitDTO implement(History curHis, Concept nextActConf, ActivitySubmitDTO data, UserDetailsDTO user)
			throws ObjectNotFoundException {
		// which data should be amended
		if (isAmendment(nextActConf)) {
			Concept amended = amendedConcept(curHis.getApplicationData());
			Concept amendment = amendmentConcept(curHis.getApplicationData(), amended);
			Concept oldStored = storedValues(amendment, user);
			if (amended != null && amendment != null) {
				String configUrl = closureServ.getUrlByNode(amendment);
				oldStored = implementLiterals(configUrl, amendment, amended, oldStored);
				oldStored = implementAddress(configUrl, amendment, amended, oldStored);
				oldStored = implementFiles(configUrl, amendment, amended, oldStored);
				oldStored = implementDictionaries(configUrl, amendment, amended, oldStored);
				oldStored = implementPersons(configUrl, amendment, amended, oldStored);
			} else {
				data.setValid(false);
				data.setIdentifier(mess.get("invalidmodification"));
			}
		}
		return data;
	}

	/**
	 * Implement persons modifications
	 * @param configUrl 
	 * @param amendment amendment data
	 * @param amended amended data
	 * @param storedValues to store old persons
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Concept implementPersons(String configUrl, Concept amendment, Concept amended, Concept storedValues) throws ObjectNotFoundException {
		List<Assembly> assemblies =assemblyServ.loadDataConfiguration(configUrl);
		List<AssemblyDTO> ads = assemblyServ.auxPersons(configUrl,assemblies);
		//prepare
		List<String> keys = new ArrayList<String>();
		for(AssemblyDTO ad : ads) {
			keys.add(ad.getPropertyName());
		}
		Thing amendmentThing = boilerServ.thingByNode(amendment);
		Thing amendedThing=boilerServ.thingByNode(amended);
		//store previous
		Thing storedThing = storedThing(storedValues);
		storedThing.getPersons().clear();
		for(ThingPerson tp :amendedThing.getPersons()) {
			storedThing.getPersons().add(personClone(tp));
		}
		storedThing = boilerServ.saveThing(storedThing);
		//implement the amendment
		List<ThingPerson> toDel = linkedPersons(amendmentThing, false);
		List<Long> toDelIds = new ArrayList<Long>();
		for(ThingPerson tp : toDel) {
			toDelIds.add(tp.getConcept().getID());
		}
		List<ThingPerson> toAdd= linkedPersons(amendmentThing, true);
		//remove
		for(ThingPerson tp : amendedThing.getPersons()) {
			if(toDelIds.contains(tp.getConcept().getID())) {
				tp.getConcept().setActive(false);
			}
		}
		//add new
		for(ThingPerson tp :toAdd) {
			ThingPerson tp1 = new ThingPerson();
			tp1.setPersonUrl(tp.getPersonUrl());
			tp1.setVarName(tp.getVarName());
			tp1.setConcept(tp.getConcept());
			amendedThing.getPersons().add(tp1);
		}
		amendedThing = boilerServ.saveThing(amendedThing);
		return storedValues;
	}
	/**
	 * Create a clone of ThingPerson and person itself
	 * not a deep clone, it will be excess...
	 * @param tp
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingPerson personClone(ThingPerson tpOld) throws ObjectNotFoundException {
		ThingPerson tp = new ThingPerson();
		//clone the concept
		Concept owner = closureServ.getParent(tpOld.getConcept());
		Concept clone = closureServ.cloneTree(owner,tpOld.getConcept());
		//clone thing
		Thing oldThing = boilerServ.thingByNode(tpOld.getConcept());
		Thing persThing =cloneThing(clone, oldThing);
		boilerServ.saveThing(persThing);
		tp.setPersonUrl(tpOld.getPersonUrl());
		tp.setVarName(tpOld.getVarName());
		tp.setConcept(clone);
		return tp;
	}

	/**
	 * Clone a thing, assign concept to the clone 
	 * @param clone
	 * @param oldThing
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Thing cloneThing(Concept concept, Thing oldThing) throws ObjectNotFoundException {
		Thing ret = new Thing();
		for(ThingDict tdi: oldThing.getDictionaries()) {
			ret.getDictionaries().add(cloneThingDict(tdi));
		}
		for(ThingDoc tdo: oldThing.getDocuments()) {
			ret.getDocuments().add(cloneThingDoc(tdo));
		}
		for(ThingThing tt : oldThing.getThings()) {
			ret.getThings().add(cloneThingThing(tt));
		}
		ret.setConcept(concept);
		ret.setUrl(oldThing.getUrl());
		return ret;
	}
	/**
	 * Clone ThingThing, using the clone of the concept and clone related to the concept thing
	 * @param tt
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ThingThing cloneThingThing(ThingThing ttOld) throws ObjectNotFoundException {
		ThingThing ret = new ThingThing();
		Concept root = closureServ.getParent(ttOld.getConcept());
		Concept node = closureServ.cloneTree(root, ttOld.getConcept());
		Thing oldThing = boilerServ.thingByNode(ttOld.getConcept());
		Thing thing = cloneThing(node, oldThing);
		boilerServ.saveThing(thing);
		ret.setConcept(node);
		ret.setUrl(ttOld.getUrl());
		ret.setVarname(ttOld.getVarname());
		return ret;
	}

	/**
	 * Implement dictionaries modification
	 * @param configUrl
	 * @param amendment
	 * @param amended
	 * @param oldStored
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Concept implementDictionaries(String configUrl, Concept amendment, Concept amended, Concept storedValues) throws ObjectNotFoundException {
		List<Assembly> assemblies =assemblyServ.loadDataConfiguration(configUrl);
		List<AssemblyDTO> ads = assemblyServ.auxDictionaries(configUrl,assemblies);
		if(ads.size()>0) {
			//prepare
			List<String> keys = new ArrayList<String>();
			for(AssemblyDTO ad : ads) {
				keys.add(ad.getPropertyName());
			}
			Thing amendmentThing = boilerServ.thingByNode(amendment);
			Thing amendedThing=boilerServ.thingByNode(amended);
			//---------------------------store previous----------------------------------------
			Thing storedThing = storedThing(storedValues);
			if(storedThing.getDictionaries().size()==0) {
				for(ThingDict td :amendedThing.getDictionaries()) {
					storedThing.getDictionaries().add(cloneThingDict(td));
				}
			}
			storedThing = boilerServ.saveThing(storedThing);
			//---------------------------- amend -------------------------------------------------
			//remove amended
			List<ThingDict> notAmended = new ArrayList<ThingDict>();
			for(ThingDict td : amendedThing.getDictionaries()) {
				if(!keys.contains(td.getVarname())) {
					notAmended.add(td);
				}
			}
			amendedThing.getDictionaries().clear();
			amendedThing.getDictionaries().addAll(notAmended);
			//add amended
			for(ThingDict td : amendmentThing.getDictionaries()) {
				amendedThing.getDictionaries().add(cloneThingDict(td));
			}
			amendedThing = boilerServ.saveThing(amendedThing);
		}
		return storedValues;
	}
	/**
	 * Create a clone of a ThingDict given
	 * @param td
	 * @return
	 */
	private ThingDict cloneThingDict(ThingDict td) {
		ThingDict ret = new ThingDict();
		ret.setConcept(td.getConcept());
		ret.setUrl(td.getUrl());
		ret.setVarname(td.getVarname());
		return ret;
	}

	/**
	 * Implement changes in files
	 * @param configUrl amendment configuration URL
	 * @param amendment amendment concept
	 * @param amended amended concept
	 * @param oldStored storage for old values
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Concept implementFiles(String configUrl, Concept amendment, Concept amended, Concept storedValues) throws ObjectNotFoundException {
		List<Assembly> assemblies =assemblyServ.loadDataConfiguration(configUrl);
		List<AssemblyDTO> ads = assemblyServ.auxDocuments(configUrl,assemblies);
		if(ads.size()>0) {
			//prepare
			List<String> keys = new ArrayList<String>();
			for(AssemblyDTO ad : ads) {
				keys.add(ad.getPropertyName());
			}
			Thing amendmentThing = boilerServ.thingByNode(amendment);
			Thing amendedThing=boilerServ.thingByNode(amended);

			//---------------------------store previous----------------------------------------
			Thing storedThing = storedThing(storedValues);
			storedThing.getDocuments().clear();
			for(ThingDoc td :amendedThing.getDocuments()) {
				storedThing.getDocuments().add(cloneThingDoc(td));
			}
			storedThing = boilerServ.saveThing(storedThing);
			//---------------------------- amend -------------------------------------------------
			//remove amended
			List<ThingDoc> notAmended = new ArrayList<ThingDoc>();
			for(ThingDoc td : amendedThing.getDocuments()) {
				if(!keys.contains(td.getVarName())) {
					notAmended.add(td);
				}
			}
			amendedThing.getDocuments().clear();
			amendedThing.getDocuments().addAll(notAmended);
			//add amended
			for(ThingDoc td : amendmentThing.getDocuments()) {
				amendedThing.getDocuments().add(cloneThingDoc(td));
			}
			amendedThing = boilerServ.saveThing(amendedThing);
		}

		return storedValues;
	}

	/**
	 * Create a new ThingDoc that is a clone of ThingDoc given
	 * @param td
	 * @return
	 */
	private ThingDoc cloneThingDoc(ThingDoc td) {
		ThingDoc ret = new ThingDoc();
		ret.setConcept(td.getConcept());
		ret.setDictNode(td.getDictNode());
		ret.setDictUrl(td.getDictUrl());
		ret.setDocUrl(td.getDocUrl());
		ret.setVarName(td.getVarName());
		return ret;
	}

	/**
	 * Get or create stored values concept attached to the amendment data
	 * @param applicationData
	 * @param amendment
	 * @param user 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Concept storedValues(Concept amendment, UserDetailsDTO user) throws ObjectNotFoundException {
		Thing th = new Thing();
		th=boilerServ.thingByNode(amendment);
		if(th.getID()>0) {
			ThingOld ret = th.getOldValue();
			if(ret!=null) {
				return ret.getConcept();
			}else {
				String url = closureServ.getUrlByNode(amendment);
				ret=addStoredValuesConcept(amendment,url, user);
				return ret.getConcept();
			}
		}else {
			throw new ObjectNotFoundException("storedValues Thing for application data not found",logger);
		}
	}
	/**
	 * Ensure the stored values concept under applicationData
	 * @param amendment
	 * @param url 
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private ThingOld addStoredValuesConcept(Concept amendment, String url, UserDetailsDTO user) throws ObjectNotFoundException {
		//create ThingOld
		ThingOld ret=new ThingOld();
		Concept concept = new Concept();
		concept = closureServ.placeConceptToTree(url, user.getEmail(),concept);
		ret.setConcept(concept);
		ret.setCreatedAt(new Date());
		//add it to applicationData
		Thing th = new Thing();
		th=boilerServ.thingByNode(amendment);
		if(th.getID()>0) {
			th.setOldValue(ret);
			boilerServ.saveThing(th);
		}else {
			throw new ObjectNotFoundException("addStoredValuesConcept Thing for application data not found",logger);
		}
		return ret;
	}

	/**
	 * Implement addresses from amendment to amended. Store old to oldValues
	 * 
	 * @param configUrl    configuration URL
	 * @param amendment
	 * @param amended
	 * @param storedValues
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private Concept implementAddress(String configUrl, Concept amendment, Concept amended, Concept storedValues)
			throws ObjectNotFoundException {
		Thing storedThing = storedThing(storedValues);
		Thing amendedThing = boilerServ.thingByNode(amended);
		List<Assembly> assemblies =assemblyServ.loadDataConfiguration(configUrl);
		List<AssemblyDTO> ads = assemblyServ.auxAddresses(configUrl,assemblies);
		Map<String, ThingThing> oldValue = mapThingThing(ads, amended);
		Map<String, ThingThing> newValue = mapThingThing(ads, amendment);
		Map<String, ThingThing> oldStored = mapThingThing(ads, storedValues);
		// store old
		for (String key : oldValue.keySet()) {
			ThingThing tt = oldStored.get(key);
			if (tt == null) {					//plus the latch
				tt = new ThingThing();
				tt.setConcept(oldValue.get(key).getConcept());
				tt.setUrl(oldValue.get(key).getUrl());
				tt.setVarname(oldValue.get(key).getVarname());
				storedThing.getThings().add(tt);
			}
		}
		storedThing = boilerServ.saveThing(storedThing);
		// amend
		for (String key : newValue.keySet()) {
			ThingThing ttNew = newValue.get(key);
			ThingThing ttOld = oldValue.get(key);
			if (ttOld == null) {
				ttOld = new ThingThing();
				amendedThing.getThings().add(ttOld);
			}
			ttOld.setUrl(ttNew.getUrl());
			ttOld.setVarname(ttNew.getVarname());
			ttOld.setConcept(ttNew.getConcept());
		}
		amendedThing = boilerServ.saveThing(amendedThing);

		return storedValues;
	}

	/**
	 * Get or initiate a thing related to oldStored
	 * 
	 * @param oldStored
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private Thing storedThing(Concept storedValues) throws ObjectNotFoundException {
		Thing ret = new Thing();
		ret = boilerServ.thingByNode(storedValues, ret);
		if (ret.getID() == 0) {
			ret.setConcept(storedValues);
			String url = closureServ.getUrlByNode(storedValues);
			ret.setUrl(url);
			boilerServ.saveThing(ret);
		}
		return ret;
	}

	/**
	 * create ThingThing map from assemblyDTO
	 * 
	 * @param ads
	 * @param concept
	 * @return map key,ThingThing Key is in upper case
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private Map<String, ThingThing> mapThingThing(List<AssemblyDTO> ads, Concept concept)
			throws ObjectNotFoundException {
		Map<String, ThingThing> ret = new LinkedHashMap<String, ThingThing>();
		// build keys in upper case
		List<String> keys = new ArrayList<String>();
		for (AssemblyDTO ad : ads) {
			keys.add(ad.getPropertyName().toUpperCase());
		}
		Thing th = new Thing();
		th = boilerServ.thingByNode(concept, th);
		if (th.getID() > 0) {
			for (ThingThing tt : th.getThings()) {
				String key = tt.getVarname().toUpperCase();
				if (keys.contains(key)) {
					ret.put(key, tt);
				}
			}
		} else {
			th.setConcept(concept);
		}
		return ret;
	}

	/**
	 * Is it amendment implementation activity?
	 * 
	 * @param activityConfig
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private boolean isAmendment(Concept activityConfig) throws ObjectNotFoundException {
		Thing th = boilerServ.thingByNode(activityConfig);
		for (ThingDict td : th.getDictionaries()) {
			if (td.getUrl().equalsIgnoreCase(SystemService.DICTIONARY_SYSTEM_FINALIZATION)) {
				if (td.getConcept().getIdentifier().equalsIgnoreCase(SystemService.FINAL_AMEND)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Create copies of amended literals in oldValue and, then, amend them amendment
	 * 
	 * @param configUrl
	 * @param amendment
	 * @param amended
	 * @param oldValues
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private Concept implementLiterals(String configUrl, Concept amendment, Concept amended, Concept oldValues)
			throws ObjectNotFoundException {
		List<AssemblyDTO> all = primitiveCollect(configUrl, amendment);
		for (AssemblyDTO lit : all) {
			String oldValue = literalServ.readValue(lit.getPropertyName(), amended);
			String newValue = literalServ.readValue(lit.getPropertyName(), amendment);
			if(!oldValue.equals(newValue)) {			//the latch
				literalServ.createUpdateLiteral(lit.getPropertyName(), oldValue, oldValues);
				literalServ.createUpdateLiteral(lit.getPropertyName(), newValue, amended);
			}
		}
		return oldValues;
	}

	/**
	 * Collect all primitives (strings, literals, numbers, dates,logicals)
	 * 
	 * @param configUrl
	 * @param amendment
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public List<AssemblyDTO> primitiveCollect(String configUrl, Concept amendment) throws ObjectNotFoundException {
		List<AssemblyDTO> all = new ArrayList<AssemblyDTO>();
		List<Assembly> assemblies =assemblyServ.loadDataConfiguration(configUrl);
		all.addAll(assemblyServ.auxStrings(configUrl,assemblies));
		all.addAll(assemblyServ.auxLiterals(configUrl,assemblies));
		all.addAll(assemblyServ.auxNumbers(configUrl,assemblies));
		all.addAll(assemblyServ.auxDates(configUrl,assemblies));
		all.addAll(assemblyServ.auxLogicals(configUrl,assemblies));
		return all;
	}

	/**
	 * In an amendment application data one node is amendment. Search for it
	 * 
	 * @param applicationData
	 * @param amended
	 * @return null if not found
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Concept amendmentConcept(Concept applicationData, Concept amended) throws ObjectNotFoundException {
		// get all data nodes of the application
		String amendmentUrl = closureServ.getUrlByNode(applicationData);
		List<DataUnitDTO> dataNodes = new ArrayList<DataUnitDTO>();
		//dataNodes = dataNodes(applicationData, amendmentUrl, literalServ.readPrefLabel(applicationData), "", dataNodes);
		dataNodes=dataNodes(applicationData);
		// get URL of amended data
		String amendedUrl = closureServ.getUrlByNode(amended);
		// which data node of the amendment application is suit amended URL
		for (DataUnitDTO du : dataNodes) {
			if (canBeModified(amendedUrl, du.getUrl())) {
				Concept ret = closureServ.loadConceptById(du.getNodeId());
				return ret;
			}
		}
		return null;
	}

	/**
	 * Create
	 * 
	 * @param applicationData
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Concept storedConcept(Concept applicationData) throws ObjectNotFoundException {
		String storedUrl = closureServ.getUrlByNode(applicationData) + ".stored";
		Concept root = closureServ.loadRoot(storedUrl);
		Concept oldValues = new Concept();
		oldValues = closureServ.save(oldValues);
		oldValues.setIdentifier(oldValues.getID() + "");
		oldValues = closureServ.saveToTree(root, oldValues);
		return oldValues;
	}

	/**
	 * Propose to add new amendment
	 * 
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public AmendmentNewDTO proposeAdd(UserDetailsDTO user, AmendmentNewDTO data) throws ObjectNotFoundException {
		data = proposeApplications(user, data);
		if (data.getDataNodeId() > 0) {
			return proposeDataUnit(data);
		}
		return data;
	}

	/**
	 * Propose a data units for data selected Data selecte is a root node of an
	 * application data
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private AmendmentNewDTO proposeDataUnit(AmendmentNewDTO data) throws ObjectNotFoundException {
		if (data.getDataNodeId() > 0 && data.getDictItemId() > 0) {
			// prepare data units
			Concept dataNode = closureServ.loadConceptById(data.getDataNodeId());
			String dataUrl = closureServ.getUrlByNode(dataNode);
			List<DataUnitDTO> dataNodes = new ArrayList<DataUnitDTO>();
			//dataNodes = dataNodes(dataNode, dataUrl, literalServ.readPrefLabel(dataNode), "", dataNodes);
			dataNodes=dataNodes(dataNode);
			// prepare a table
			data.getDataUnits().getRows().clear();
			data.getDataUnits().getHeaders().getHeaders().clear();
			data.getDataUnits().setHeaders(headersApplications(data.getDataUnits().getHeaders()));
			// modification data URL
			Concept dictNode = closureServ.loadConceptById(data.getDictItemId());
			String modiUrl = literalServ.readValue("url", dictNode);
			// for all data units, search possibility to modify
			for (DataUnitDTO du : dataNodes) {
				if (dataUrl.length() > 0 && modiUrl.length() > 0) {
					if (canBeModified(du.getUrl(), modiUrl)) {
						TableRow row = dataUnitRow(du);
						if (row != null) {
							data.getDataUnits().getRows().add(row);
						}
					}
				}
			}
			// TODO all auxiliary data units
		}
		data.getDataUnits().setSelectable(false);
		return data;
	}

	/**
	 * Create a list of all root and auxiliary data nodes
	 * @param appldata
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<DataUnitDTO> dataNodes(Concept appldata) throws ObjectNotFoundException{
		List<DataUnitDTO> ret = new ArrayList<DataUnitDTO>();
		jdbcRepo.data_units(appldata.getID());
		Headers headers = new Headers();
		headers.setPageSize(Integer.MAX_VALUE);
		headers.getHeaders().add(TableHeader.instanceOf("varname", TableHeader.COLUMN_STRING));
		headers.getHeaders().add(TableHeader.instanceOf("url", TableHeader.COLUMN_STRING));
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from data_units", "", "", headers);
		for(TableRow row : rows) {
			DataUnitDTO dto = new DataUnitDTO();
			String label=row.getRow().get(0).getValue();
			String url=row.getRow().get(1).getValue();
			String mainLabel = "";
			Concept node = closureServ.loadConceptById(row.getDbID());
			if(label.length()==0) {
				mainLabel=literalServ.readPrefLabel(node);
			}else {
				label=mess.get(label);
				ThingThing tt=boilerServ.thingThing(node, true);
				Thing t = boilerServ.thingByThingThing(tt, true);
				mainLabel=literalServ.readPrefLabel(t.getConcept());
			}
			dto.setLabel(label);
			dto.setMainLabel(mainLabel);
			dto.setNodeId(row.getDbID());
			dto.setUrl(url);
			//
			ret.add(dto);
		}
		return ret;
	}

	/**
	 * Create a list of all root and auxiliary data nodes
	 * @deprecated
	 * @param dataNode
	 * @param dataNodes
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public List<DataUnitDTO> dataNodes(Concept dataNode, String url, String mainLabel, String varName,
			List<DataUnitDTO> dataNodes) throws ObjectNotFoundException {
		dataNodes.add(dataUnit(dataNode, url, mainLabel, varName));
		Thing th = boilerServ.thingByNode(dataNode);
		// things
		for (ThingThing tt : th.getThings()) {
			Concept node = tt.getConcept();
			ArrayList<DataUnitDTO> thingNodes = new ArrayList<DataUnitDTO>();
			dataNodes.addAll(dataNodes(node, tt.getUrl(), mainLabel, mess.get(tt.getVarname()), thingNodes));
		}
		// persons
		for (ThingPerson tp : th.getPersons()) {
			Concept nodeP = tp.getConcept();
			ArrayList<DataUnitDTO> pNodes = new ArrayList<DataUnitDTO>();
			String label = mess.get(tp.getVarName());
			dataNodes.addAll(dataNodes(nodeP, tp.getPersonUrl(), literalServ.readPrefLabel(nodeP), label, pNodes));
		}
		// TODO ingredients

		return dataNodes;
	}

	/**
	 * Convert data node to data unit
	 * 
	 * @param dataNode
	 * @param url
	 * @param label
	 * @return
	 */
	@Transactional
	private DataUnitDTO dataUnit(Concept dataNode, String url, String mainLabel, String label) {
		DataUnitDTO ret = new DataUnitDTO();
		ret.setNodeId(dataNode.getID());
		ret.setUrl(url);
		ret.setLabel(label);
		ret.setMainLabel(mainLabel);
		return ret;
	}

	/**
	 * Is dataUrl can be modified by modiUrl? It will be possible if dataUrl
	 * contains all variables from modiUrl with exactly same definitions
	 * 
	 * @param dataUrl
	 * @param modiUrl
	 * @return
	 */
	@Transactional
	private boolean canBeModified(String dataUrl, String modiUrl) {
		jdbcRepo.modification_check(modiUrl, dataUrl);
		// System.out.println(dataUrl+"---"+modiUrl);
		Headers h = new Headers();
		h.getHeaders().add(TableHeader.instanceOf("modificationID", TableHeader.COLUMN_LONG));
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from modification_check", "", "", h);
		return rows.size() == 0;
	}

	/**
	 * Create a row for a data unit
	 * 
	 * @param du
	 * @param dataConfigs
	 * @param modiUrl     data structure to modify
	 * @return null if not found
	 * @throws ObjectNotFoundException
	 */
	private TableRow dataUnitRow(DataUnitDTO du) throws ObjectNotFoundException {
		if (du != null) {
			TableRow row = TableRow.instanceOf(du.getNodeId());
			String prefLabel = du.getMainLabel();
			String description = du.getLabel();
			row.getRow().add(TableCell.instanceOf("prefLabel", prefLabel));
			row.getRow().add(TableCell.instanceOf("description", description));
			return row;
		} else {
			return null;
		}
	}

	/**
	 * Calculate a human readable title.
	 * 
	 * @param dataNode
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private String calcTitle(Concept dataNode) throws ObjectNotFoundException {
		String title = literalServ.readPrefLabel(dataNode);
		if (title.length() == 0) {
			ThingThing tt = boilerServ.thingThing(dataNode, false);
			if (tt != null) {
				title = mess.get(tt.getVarname());
			}
			if (title.length() == 0) {
				title = mess.get("unknown");
			}
		}
		return title;
	}

	/**
	 * Propose applications for known amendment type
	 * 
	 * @param user
	 * @param data
	 * @return
	 */
	@Transactional
	private AmendmentNewDTO proposeApplications(UserDetailsDTO user, AmendmentNewDTO data) {
		TableQtb table = data.getApplications();
		if (table.getHeaders().getHeaders().size() == 0) {
			table.setHeaders(headersApplications(table.getHeaders()));
		}
		jdbcRepo.applications_hosted_inactive(null, user.getEmail(), true);
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from applications_hosted_inactive", "", "", table.getHeaders());
		TableQtb.tablePage(rows, table);
		table.setSelectable(true);
		for (TableRow row : table.getRows()) {
			if (row.getDbID() == data.getDataNodeId()) {
				row.setSelected(true);
			} else {
				row.setSelected(false);
			}
		}
		return data;
	}

	/**
	 * Headers for applications list
	 * 
	 * @param headers
	 * @return
	 */
	private Headers headersApplications(Headers headers) {
		headers.getHeaders()
		.add(TableHeader.instanceOf("prefLabel", "prefLabel", true, true, true, TableHeader.COLUMN_LINK, 0));
		headers.getHeaders().add(
				TableHeader.instanceOf("description", "description", true, true, true, TableHeader.COLUMN_STRING, 0));
		headers = boilerServ.translateHeaders(headers);
		headers.setPageSize(50);
		return headers;
	}

	/**
	 * Get amended data and application if ones
	 * 
	 * @param applicationData
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ActivityDTO amended(Concept applicationData, ActivityDTO data) throws ObjectNotFoundException {
		data.getModiPath().clear();
		if (applicationData != null) {
			Thing th = boilerServ.thingByNode(applicationData);
			for (ThingAmendment ta : th.getAmendments()) { // only one always, the loop is for convince only
				data.getModiPath().addAll(reversePath(ta.getConcept()));
				break;
			}
		}
		if (data.getModiPath().size() > 0) {
			for (ThingDTO th : data.getApplication()) {
				th.setModiUnitId(data.getModiPath().get(0).getNodeId());
			}
		}
		return data;
	}

	/**
	 * Reverse path from the data unit to application data we presume regular
	 * structure of an application data as
	 * root-aux-[[{person-root-aux],[ingredient-root-aux]]
	 * 
	 * @param concept
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public List<DataUnitDTO> reversePath(Concept concept) throws ObjectNotFoundException {
		List<DataUnitDTO> ret = new ArrayList<DataUnitDTO>();
		ThingThing tt = boilerServ.thingThing(concept, false);
		if (tt == null) {
			ret.addAll(processRoot(concept));
		} else {
			ret.addAll(processAux(concept, tt));
		}
		return ret;
	}

	/**
	 * Auxiliary data unit.
	 * 
	 * @param concept
	 * @param tt
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private List<DataUnitDTO> processAux(Concept concept, ThingThing tt) throws ObjectNotFoundException {
		List<DataUnitDTO> ret = new ArrayList<DataUnitDTO>();
		DataUnitDTO du = new DataUnitDTO();
		du.setNodeId(concept.getID());
		du.setLabel(mess.get(tt.getVarname()));
		ret.add(du);
		Thing th = boilerServ.thingByThingThing(tt, false);
		if (th != null) {
			ret.addAll(processRoot(th.getConcept()));
		}
		return ret;
	}

	/**
	 * Process a root node
	 * 
	 * @param concept
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private List<DataUnitDTO> processRoot(Concept concept) throws ObjectNotFoundException {
		List<DataUnitDTO> ret = new ArrayList<DataUnitDTO>();
		DataUnitDTO du = new DataUnitDTO();
		du.setNodeId(concept.getID());
		du.setLabel(literalServ.readPrefLabel(concept)); // root must contain prefLabel
		ret.add(du);
		ThingPerson tp = boilerServ.thingPerson(concept, false);
		if (tp != null) {
			// aux of main
			Thing th = boilerServ.thingByThingPerson(tp, false);
			if (th != null) {
				ThingThing tt = boilerServ.thingThing(th.getConcept(), false);
				if (tt != null) {
					ret.addAll(processAux(th.getConcept(), tt));
				}
			}
		}
		// TODO process ingredient
		return ret;
	}

	/**
	 * Compare amendment and amended data. Mark changed amendment data
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ThingDTO diffMark(ThingDTO data) throws ObjectNotFoundException {
		if (data.getModiUnitId() > 0 && data.getNodeId() > 0 && data.getApplDictNodeId() > 0) {
			Concept dictNode = closureServ.loadConceptById(data.getApplDictNodeId());
			Concept node = closureServ.loadConceptById(data.getNodeId());
			Concept modiUnit = closureServ.loadConceptById(data.getModiUnitId());
			String amdUrl = literalServ.readValue("url", dictNode);
			if (data.getUrl().equalsIgnoreCase(amdUrl)) {
				data = primitivesDiffMark(data, node, modiUnit);
				data = addressesDiffMark(data, node, modiUnit);
				data = dictionariesDiffMark(data, node, modiUnit);
				data = documentDiffMark(data, node, modiUnit);
				data = personsDiffMark(data, node, modiUnit);
			}
		}
		return data;
	}
	/**
	 * Compare persons in node and modiUnit
	 * All persons from node will be in modiUnit, however not all persons from modiUnit will be in the node 
	 * @param data - amendment application data
	 * @param node - node of amendment data contains persons
	 * @param modiUnit - node of amended data contains persons
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ThingDTO personsDiffMark(ThingDTO data, Concept node, Concept modiUnit) throws ObjectNotFoundException {
		for(String key : data.getPersons().keySet()) {
			data.getPersons().get(key).setChanged(false);
		}
		Set<String> keys = personsCompare(node, modiUnit);
		if(keys.size()>0) {
			for(String key : keys) {
				if(!key.equalsIgnoreCase(REMOVE_PERSON)) {
					PersonDTO dto = data.getPersons().get(key);
					dto.setChanged(true);
				}
			}
		}
		return data;
	}
	/**
	 * Compare persons
	 * Yes, we are know the person control maybe only one...
	 * @param amendment
	 * @param amended
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Set<String> personsCompare(Concept amendment, Concept amended) throws ObjectNotFoundException {
		Set<String> ret = new LinkedHashSet<String>();
		Map<String, List<Long>> pers1 = personsByVariable(amendment);
		Map<String, List<Long>> pers2 = personsByVariable(amended);
		if(pers1.size()>0) {
			for(String key : pers1.keySet()) {
				List<Long> pl1 = pers1.get(key);
				List<Long> pl2 = pers2.get(key);
				if(pl2!=null) {
					//all pl1 should be in pl2, but not vice versa
					for(Long l : pl1) {
						if(!pl2.contains(l)) {
							ret.add(key);
							break;
						}
					}
				}else {
					ret.add(key);
				}
			}
		}
		return ret;
	}
	/**
	 * Persons by a variable in the thing
	 * @param amendment
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public Map<String, List<Long>> personsByVariable(Concept amendment) throws ObjectNotFoundException {
		Map<String, List<Long>> vars = new LinkedHashMap<String, List<Long>>();
		Thing t =boilerServ.thingByNode(amendment);
		for(ThingPerson tp : t.getPersons()) {
			if(!vars.containsKey(tp.getVarName())) {
				vars.put(tp.getVarName(), new ArrayList<Long>());
			}
			if(tp.getConcept().getActive()) {
				vars.get(tp.getVarName()).add(tp.getConcept().getID());
			}
		}
		return vars;
	}

	/**
	 * Compare documents attached For current It is presumed that documents changed
	 * anyway
	 * 
	 * @param data
	 * @param node
	 * @param modiUnit
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private ThingDTO documentDiffMark(ThingDTO data, Concept node, Concept modiUnit) throws ObjectNotFoundException {
		List<String> keys = documentsCompare(node, modiUnit);
		for (String key : keys) {
			FileDTO fd = data.getDocuments().get(key);
			fd.setChanged(true);
		}
		return data;
	}

	/**
	 * Compare documents in amendment and amended
	 * 
	 * @param amendment
	 * @param amended
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private List<String> documentsCompare(Concept amendment, Concept amended) throws ObjectNotFoundException {
		List<String> ret = new ArrayList<String>();
		Thing oldThing = boilerServ.thingByNode(amended);
		Thing newThing = boilerServ.thingByNode(amendment);
		Map<String, List<Long>> newDoc = new LinkedHashMap<String, List<Long>>();
		Map<String, List<Long>> oldDoc = new LinkedHashMap<String, List<Long>>();
		// extract data
		for (ThingDoc td : newThing.getDocuments()) {
			newDoc = createDocMap(newDoc, td);
		}
		for (ThingDoc td : oldThing.getDocuments()) {
			oldDoc = createDocMap(oldDoc, td);
		}
		// compare data
		for (String key : newDoc.keySet()) {
			List<Long> newList = newDoc.get(key);
			List<Long> oldList = oldDoc.get(key);
			if (oldList == null) {
				ret.add(key);
			} else {
				if (!compareIds(newList, oldList)) {
					ret.add(key);
				}
			}
		}
		return ret;
	}

	/**
	 * Compare dictionaries in old and new data variables+liks
	 * 
	 * @param data
	 * @param node
	 * @param modiUnit
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private ThingDTO dictionariesDiffMark(ThingDTO data, Concept node, Concept modiUnit)
			throws ObjectNotFoundException {
		List<String> keys = dictionariesCompare(node, modiUnit);
		for (String key : keys) {
			data.getDictionaries().get(key).setChanged(true);
		}
		return data;
	}

	/**
	 * Compare dictionaries in amendment and amended
	 * 
	 * @param amendment
	 * @param amended
	 * @return list of variables names for different dictionaries
	 * @throws ObjectNotFoundException
	 */
	private List<String> dictionariesCompare(Concept amendment, Concept amended) throws ObjectNotFoundException {
		List<String> ret = new ArrayList<String>();
		Thing oldThing = boilerServ.thingByNode(amended);
		Thing newThing = boilerServ.thingByNode(amendment);
		Map<String, List<Long>> newDic = new LinkedHashMap<String, List<Long>>();
		Map<String, List<Long>> oldDic = new LinkedHashMap<String, List<Long>>();
		// extract data
		for (ThingDict td : newThing.getDictionaries()) {
			newDic = createDictMap(newDic, td);
		}
		for (ThingDict td : oldThing.getDictionaries()) {
			oldDic = createDictMap(oldDic, td);
		}
		// compare data
		for (String key : newDic.keySet()) {
			List<Long> newList = newDic.get(key);
			List<Long> oldList = oldDic.get(key);
			if (oldList == null) {
				ret.add(key);
			} else {
				if (!compareIds(newList, oldList)) {
					ret.add(key);
				}
			}
		}
		return ret;
	}

	/**
	 * Compare IDS
	 * 
	 * @param newList
	 * @param oldList
	 * @return
	 */
	private boolean compareIds(List<Long> newList, List<Long> oldList) {
		if (newList.size() != oldList.size()) {
			return false;
		} else {
			Collections.sort(newList);
			Collections.sort(oldList);
			return newList.equals(oldList);
		}
	}

	/**
	 * Create a map of dictionary values
	 * 
	 * @param dicMap
	 * @param td
	 * @return
	 */
	public Map<String, List<Long>> createDictMap(Map<String, List<Long>> dicMap, ThingDict td) {
		List<Long> ids = dicMap.get(td.getVarname());
		if (ids == null) {
			ids = new ArrayList<Long>();
			dicMap.put(td.getVarname(), ids);
		}
		ids.add(td.getConcept().getID());
		return dicMap;
	}

	/**
	 * Create a map of documents values
	 * 
	 * @param dicMap
	 * @param td
	 * @return
	 */
	public Map<String, List<Long>> createDocMap(Map<String, List<Long>> docMap, ThingDoc td) {
		List<Long> ids = docMap.get(td.getVarName());
		if (ids == null) {
			ids = new ArrayList<Long>();
			docMap.put(td.getVarName(), ids);
		}
		ids.add(td.getConcept().getID());
		return docMap;
	}

	/**
	 * GEt and compare addresses
	 * 
	 * @param data
	 * @param node
	 * @param modiUnit
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private ThingDTO addressesDiffMark(ThingDTO data, Concept node, Concept modiUnit) throws ObjectNotFoundException {
		List<String> difKeys = addressesCompare(node, modiUnit);
		for (String key : difKeys) {
			data.getAddresses().get(key).setChanged(true);
		}
		return data;
	}

	/**
	 * Compare addresses for amendment and amended data units
	 * 
	 * @param amendment
	 * @param amended
	 * @return list of addresses variables names for which addresses are different
	 * @throws ObjectNotFoundException
	 */
	List<String> addressesCompare(Concept amendment, Concept amended) throws ObjectNotFoundException {
		List<String> ret = new ArrayList<String>();
		String nodeUrl = closureServ.getUrlByNode(amendment);
		Thing oldThing = boilerServ.thingByNode(amended);
		Thing newThing = boilerServ.thingByNode(amendment);
		Map<String, Concept> newAddr = new LinkedHashMap<String, Concept>();
		Map<String, Concept> oldAddr = new LinkedHashMap<String, Concept>();
		List<Assembly> assemblies =assemblyServ.loadDataConfiguration(nodeUrl);
		List<AssemblyDTO> adl = assemblyServ.auxAddresses(nodeUrl,assemblies);
		for (AssemblyDTO ad : adl) {
			String key = ad.getPropertyName();
			// collect addresses
			for (ThingThing tt : newThing.getThings()) {
				if (tt.getVarname().equalsIgnoreCase(key)) {
					newAddr.put(key, tt.getConcept());
				}
			}
			for (ThingThing tt : oldThing.getThings()) {
				if (tt.getVarname().equalsIgnoreCase(key)) {
					oldAddr.put(key, tt.getConcept());
				}
			}
		}
		for (String key : newAddr.keySet()) {
			Concept newValue = newAddr.get(key);
			Concept oldValue = oldAddr.get(key);
			if (newValue != null && oldValue == null) {
				ret.add(key);
			}
			if (!newValue.getLabel().equalsIgnoreCase(oldValue.getLabel())) {
				// GIS
				ret.add(key);
			}
		}
		return ret;
	}

	/**
	 * Compare data stored as string literals
	 * 
	 * @param data
	 * @param node
	 * @param modiUnit
	 * @throws ObjectNotFoundException
	 */
	public ThingDTO primitivesDiffMark(ThingDTO data, Concept node, Concept modiUnit) throws ObjectNotFoundException {
		for (String key : data.getLiterals().keySet()) {
			data.getLiterals().get(key).setMark(!compareLiteral(key, node, modiUnit));
		}
		for (String key : data.getStrings().keySet()) {
			data.getStrings().get(key).setMark(!compareLiteral(key, node, modiUnit));
		}
		for (String key : data.getDates().keySet()) {
			data.getDates().get(key).setMark(!compareLiteral(key, node, modiUnit));
		}
		for (String key : data.getNumbers().keySet()) {
			data.getNumbers().get(key).setMark(!compareLiteral(key, node, modiUnit));
		}
		for (String key : data.getLogical().keySet()) {
			data.getLogical().get(key).setMark(!compareLiteral(key, node, modiUnit));
		}
		return data;
	}

	/**
	 * Compare literal backed fields
	 * 
	 * @param key
	 * @param node
	 * @param modiUnit
	 * @return true if equals ignore cases
	 * @throws ObjectNotFoundException
	 */
	private boolean compareLiteral(String key, Concept node, Concept modiUnit) throws ObjectNotFoundException {
		String newValue = literalServ.readValue(key, node);
		String oldValue = literalServ.readValue(key, modiUnit);
		return newValue.equalsIgnoreCase(oldValue);
	}

	/**
	 * Determine amendment application by the amended unit
	 * 
	 * @param amendedUnit any unit inside the application data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Concept amendmentApplicationByAmendmentUnit(Concept amendmentUnit) throws ObjectNotFoundException {
		List<DataUnitDTO> reversePath = reversePath(amendmentUnit);
		int size = reversePath.size();
		if (size > 0) {
			DataUnitDTO applRoot = reversePath.get(size - 1);
			Concept ret = closureServ.loadConceptById(applRoot.getNodeId());
			return ret;
		} else {
			throw new ObjectNotFoundException("amendedApplication. Not found for modification ID=" + amendmentUnit.getID());
		}
	}

	/**
	 * Determine initial application data using the current application data
	 * Initial application data is data of an application initiated by an applicant to receive a permit for product, service, facility
	 * The current application data may be initial or amendment application data  
	 * @param currentApplData
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Concept initialApplicationData(Concept currentApplData) throws ObjectNotFoundException {
		Concept amended = amendedConcept(currentApplData);	//does the application data contain an amendment for some amended data?
		if(amended.getID()>0) {
			return amendmentApplicationByAmendmentUnit(amended);	//current application data is amendment, thus search for amended data 
		}else {
			return currentApplData; //current application data is the initial application data
		}
	}

	/**
	 * Compare amendment and amended data - primitives, addresses, dictionaries,
	 * files
	 * 
	 * @param amendment
	 * @param amended
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public boolean compareConcepts(Concept amendment, Concept amended) throws ObjectNotFoundException {
		String configUrl = closureServ.getUrlByNode(amendment);
		// primitives
		List<AssemblyDTO> primitives = primitiveCollect(configUrl, amendment);
		for (AssemblyDTO ad : primitives) {
			if (!compareLiteral(ad.getPropertyName(), amendment, amended)) {
				return false;
			}
		}
		// addresses
		List<String> diff = addressesCompare(amendment, amended);
		if (diff.size() > 0) {
			return false;
		}
		// dictionaries
		diff = dictionariesCompare(amendment, amended);
		if (diff.size() > 0) {
			return false;
		}
		// files
		diff = documentsCompare(amendment, amended);
		if (diff.size() > 0) {
			return false;
		}
		return true;
	}
	/**
	 * Store a person to remove by amendment, if one
	 * Store means link to thing with the special variable name _REMOVE_
	 * @param data
	 * @param thing
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO storePersonToRemove(ThingDTO data, Thing thing) throws ObjectNotFoundException {
		Map<Long, String> toRemove = new HashMap<Long, String>();
		for(String key : data.getPersons().keySet()) {
			PersonDTO dto = data.getPersons().get(key);
			for(TableRow row :dto.getRtable().getRows()) {
				if(row.getSelected()) {
					toRemove.put(row.getDbID(),dto.getUrl());
					row.setSelected(false);
				}
			}
		}
		if(toRemove.size()>0) {
			List<ThingPerson> linkedPersons = linkedPersons(thing,true);
			thing.getPersons().clear();
			thing.getPersons().addAll(linkedPersons);
			for(Long nodeId : toRemove.keySet()) {
				Concept persNode = closureServ.loadConceptById(nodeId);
				ThingPerson tp = new ThingPerson();
				tp.setVarName(REMOVE_PERSON);
				tp.setPersonUrl(toRemove.get(nodeId));
				tp.setConcept(persNode);
				thing.getPersons().add(tp);
			}
		}else {
			//unmark if one
			List<ThingPerson> unmark = linkedPersons(thing,false);
			if(unmark.size()>0) {
				thing.getPersons().removeAll(unmark);
			}
		}
		return data;
	}
	/**
	 * Persons linked to the thing or only removed
	 * @param thing
	 * @param linkedOnly - only really linked, not removed
	 * @return
	 */
	@Transactional
	public List<ThingPerson> linkedPersons(Thing thing, boolean linkedOnly) {
		List<ThingPerson> linkedPersons = new ArrayList<ThingPerson>();
		for(ThingPerson tp : thing.getPersons()) {
			boolean condition = tp.getVarName().equalsIgnoreCase(REMOVE_PERSON);
			if(linkedOnly) {
				condition=!condition;
			}
			if(condition){
				linkedPersons.add(tp);
			}
		}
		return linkedPersons;
	}

	/**
	 * Load person to remove if one
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO personToRemove(ThingDTO data) throws ObjectNotFoundException {
		if(data.getNodeId()>0 && data.getModiUnitId()>0) {
			Concept node = closureServ.loadConceptById(data.getNodeId());
			Thing thing = boilerServ.thingByNode(node);
			List<ThingPerson> removedPersons = linkedPersons(thing,false);
			List<Long> ids = new ArrayList<Long>();
			for(ThingPerson tp : removedPersons) {
				ids.add(tp.getConcept().getID());
			}
			for(String key :data.getPersons().keySet()) {
				PersonDTO dto = data.getPersons().get(key);
				for(TableRow row: dto.getRtable().getRows()) {
					if(ids.contains(row.getDbID())) {
						row.setSelected(true);
					}
				}
			}
		}
		return data;
	}

	/**
	 * Rewrite ameded data if empty
	 * Should be called at once
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public void rewriteAmendedData() throws ObjectNotFoundException {
		Iterable<ThingAmendment> taList = thingAmendmentRepo.findAll();
		for(ThingAmendment ta : taList) {
			if(ta.getApplicationData()==null) {
				ta.setApplicationData(amendmentApplicationByAmendmentUnit(ta.getConcept()));
				thingAmendmentRepo.save(ta);
			}
		}
	}

}
