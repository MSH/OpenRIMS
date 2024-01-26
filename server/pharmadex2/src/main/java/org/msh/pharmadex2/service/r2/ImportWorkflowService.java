package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.repository.r2.ConceptRepo;
import org.msh.pdex2.repository.r2.ThingRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.DataCollectionDTO;
import org.msh.pharmadex2.dto.DataVariableDTO;
import org.msh.pharmadex2.dto.DictNodeDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.ImportWorkflowDTO;
import org.msh.pharmadex2.dto.ResourceDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class ImportWorkflowService {
	private static final Logger logger = LoggerFactory.getLogger(ImportWorkflowService.class);
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private DictService dictServ;
	@Autowired
	private Messages messages;
	@Autowired
	private SupervisorService superService;
	@Autowired
	private ResourceService resServ;
	@Autowired
	private AssistanceService assistServ;
	@Autowired
	private SystemService systemServ;
	@Autowired
	private ThingService thingServ;
	@Autowired
	private ValidationService validServ;
	@Autowired
	private ThingRepo thingRepo;
	@Autowired
	private SystemService systemService;
	@Autowired
	private ConceptRepo conceptRepo;

	private String NAME_COL_VALIDATION = "validation";
	private String NAME_COL_IMPORT = "import";

	private String STATUS_RECEIVING = "Receiving";
	private String STATUS_SCHEDULED = "Scheduled";
	private String STATUS_RECEIVED = "Received";
	private String STATUS_SKIPT = "Skipt";
	private String STATUS_ERROR = "Error!";

	private static String req_ping = "/api/public/pingbyimport";
	private static String req_processesload = "/api/public/importwf/processes/load";
	private static String req_dictionaries = "/api/public/importwf/dictionaries";
	private static String req_resources = "/api/public/importwf/resources";
	private static String req_dataconfigs = "/api/public/importwf/dataconfigs";
	private static String req_wf = "/api/public/importwf/wf";
	//private static String req_activitiesconfigs = "/api/public/importwf/activitiesconfigs";

	public ImportWorkflowDTO load(ImportWorkflowDTO data) throws ObjectNotFoundException {
		data.getServerurl().setValue(data.getIdentifier());
		data.setConnect(false);
		data.getServerurl().setStrict(false);
		data.setIdentifier("");
		data.setValid(false);// by red check

		String url = data.getServerurl().getValue();
		if(url != null && url.startsWith("http")) {
			try {
				ResponseEntity<String> r = restTemplate.getForEntity(data.getServerurl().getValue() + req_ping, String.class);
				if(r != null) {
					if(r.getBody().equals("OK")) {
						data.setValid(true);
						data.getServerurl().setStrict(true);
					}
				}
			}
			catch (RestClientException e) {
				System.out.println(data.getIdentifier());
			}
		}
		return data;
	}
	
	public ImportWorkflowDTO reload(ImportWorkflowDTO data) throws ObjectNotFoundException {
		cleanImportLists(data);
		data.getProcTable().getRows().clear();
		data.getWfTable().getRows().clear();
		data.setProcessIDselect(0l);
		data.setProcessURL("");
		data.setWfIDselect(0l);
		data.setWfURL("");
		
		data = loadProccesses(data);
		
		return data;
	}

	/**
	 * пингуем указанный сервер
	 * если все ок - возвращаем таблицу словарей
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ImportWorkflowDTO connectMainServer(ImportWorkflowDTO data) throws ObjectNotFoundException{
		if(!data.isConnect()) {// first click Connect
			data.setValid(false);
			data.setConnect(false);
			data.getServerurl().setStrict(false);
			data.setIdentifier(messages.get("error") + " " + messages.get("serverurl"));
			String url = data.getServerurl().getValue();
			if(url != null && url.startsWith("http")) {
				try {
					ResponseEntity<String> r = restTemplate.getForEntity(data.getServerurl().getValue() + req_ping, String.class);
					if(r != null) {
						if(r.getBody().equals("OK")) {
							data.setValid(true);
							data.setConnect(true);
							data.getServerurl().setStrict(true);
							data.setIdentifier(messages.get("global.success"));
						}else if(r.getBody().equals("NOACCESS")) {
							data.setValid(false);
							data.setIdentifier(messages.get("noaccessmainbyimport"));
						}
					}
				}
				catch (RestClientException e) {
					System.out.println(data.getIdentifier());
				}
			}
		}
		data = loadProccesses(data);
		return data;
	}

	private ImportWorkflowDTO loadProccesses(ImportWorkflowDTO data) throws ObjectNotFoundException {
		data = requestToMainServer(data, req_processesload);

		// проверим наличие переданных процесов на текущем сервере
		List<TableRow> rows = data.getWfTable().getRows();
		List<TableRow> newRows = new ArrayList<TableRow>();
		//TODO расскоментировать
		for(TableRow r:rows) {
			String url = (String)r.getRow().get(0).getOriginalValue();
			Concept curProc = systemService.findProccessByUrl(data.getProcessURL(), url);
			if(curProc == null) {
				newRows.add(r);
			}
		}
		data.getWfTable().getRows().clear();
		data.getWfTable().getRows().addAll(newRows);
		return data;
	}

	public ImportWorkflowDTO runimport(ImportWorkflowDTO data) throws ObjectNotFoundException {
		LocaleContextHolder.setDefaultLocale(Messages.parseLocaleString("EN_US"));
		// создаем таблицу
		if(data.getStatusTable().getHeaders().getHeaders().size() == 0) {
			data.getStatusTable().setHeaders(createHeadersStatus(data.getStatusTable().getHeaders()));
		}
		data.getStatusTable().getRows().clear();

		TableRow rowDict = createRow("Dictionaries");
		data.getStatusTable().getRows().add(rowDict);

		TableRow rowRes = createRow("Resources");
		data.getStatusTable().getRows().add(rowRes);

		TableRow rowConfig = createRow("Data Configurations");
		data.getStatusTable().getRows().add(rowConfig);

		// адо сразу создать запись словаря, апотом уже активности к ней
		TableRow rowWFConfig = createRow("WorkFlow");
		data.getStatusTable().getRows().add(rowWFConfig);

		//TableRow rowActConfig = createRow("Activities Configurations");
		//data.getStatusTable().getRows().add(rowActConfig);

		data.getStatusTable().setSelectable(false);

		data.getStatusTable().getRows().get(0).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_RECEIVING);
		return data;
	}

	private TableRow createRow(String name) {
		TableRow row = new TableRow();
		//row.setDbID(1l);
		TableCell cell = new TableCell();
		cell.setKey("prefLbl");
		cell.setValue(name);
		row.getRow().add(cell);

		cell = new TableCell();
		cell.setKey(NAME_COL_VALIDATION);
		cell.setValue(STATUS_SCHEDULED);
		row.getRow().add(cell);

		cell = new TableCell();
		cell.setKey(NAME_COL_IMPORT);
		cell.setValue(STATUS_SCHEDULED);
		row.getRow().add(cell);

		return row;
	}

	public ImportWorkflowDTO dictionaries(ImportWorkflowDTO data) throws ObjectNotFoundException {
		// validate and import dictionaries 
		data = requestToMainServer(data, req_dictionaries);

		if(data.isValid()) {
			// создаем на локальном словарu
			data = createDictionariesOnLocal(data);
		}else {
			data.getStatusTable().getRows().get(0).getCellByKey(NAME_COL_VALIDATION).setValue(data.getIdentifier());
			data.getStatusTable().getRows().get(0).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_ERROR);
		}
		return data;
	}

	public ImportWorkflowDTO resources(ImportWorkflowDTO data) throws ObjectNotFoundException {
		data = requestToMainServer(data, req_resources);
		if(data.isValid()) {
			// создаем на локальном ресурсы
			data = createResourcesOnLocal(data);
		}else {
			data.getStatusTable().getRows().get(1).getCellByKey(NAME_COL_VALIDATION).setValue(data.getIdentifier());
			data.getStatusTable().getRows().get(1).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_ERROR);
		}
		return data;
	}

	public ImportWorkflowDTO dataConfigs(ImportWorkflowDTO data) {
		data = requestToMainServer(data, req_dataconfigs);
		if(data.isValid()) {
			// создаем на локальном ресурсы
			try {
				data = createDataConfigsOnLocal(data);
			} catch (ObjectNotFoundException e) {
				e.printStackTrace();
			}
		}else {
			data.getStatusTable().getRows().get(2).getCellByKey(NAME_COL_VALIDATION).setValue(data.getIdentifier());
			data.getStatusTable().getRows().get(2).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_ERROR);
		}
		return data;
	}

	public ImportWorkflowDTO wf(ImportWorkflowDTO data, UserDetailsDTO user) throws ObjectNotFoundException, JsonProcessingException {
		data = requestToMainServer(data, req_wf);
		if(data.isValid()) {
			// создаем на локальном 
			data = createWFOnLocal(data, user);
		}else {
			data.getStatusTable().getRows().get(3).getCellByKey(NAME_COL_VALIDATION).setValue(data.getIdentifier());
			data.getStatusTable().getRows().get(3).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_ERROR);
		}
		return data;
	}

	@Transactional
	private ImportWorkflowDTO createDictionariesOnLocal(ImportWorkflowDTO data) throws ObjectNotFoundException {
		int countSkipt = 0;

		Map<String, Map<Long, List<DictNodeDTO>>> map = data.getDictsImport();
		for(String url:map.keySet()) {
			Concept root = closureServ.loadConceptByIdentifierActive(url);
			if(root != null && root.getID() > 0) {
				countSkipt++;
			}else {// create
				Map<Long, List<DictNodeDTO>> dict = map.get(url);
				buildDict(url, dict, root);
			}
		}
		if(countSkipt == map.keySet().size()) {
			data.getStatusTable().getRows().get(0).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_RECEIVED);
			data.getStatusTable().getRows().get(0).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_SKIPT);
			data.getStatusTable().getRows().get(1).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_RECEIVING);
		}else {
			data.getStatusTable().getRows().get(0).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_RECEIVED);
			data.getStatusTable().getRows().get(0).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_RECEIVED);	
		}

		cleanImportLists(data);
		return data;
	}

	@Transactional
	private void recursionCreateDictionary(Map<Long, List<DictNodeDTO>> mapdic, long parIDmain, long parID) throws ObjectNotFoundException {
		List<DictNodeDTO> list = mapdic.get(parIDmain);
		if(list != null && list.size() > 0) {
			for(DictNodeDTO dto:list) {
				long parNextMain = dto.getNodeId();
				dto.setParentId(parID);
				dto.setNodeId(0);

				dto = dictServ.save(dto);
				recursionCreateDictionary(mapdic, parNextMain, dto.getNodeId());
			}
		}
	}
	
	@Transactional
	private void buildDict(String url, Map<Long, List<DictNodeDTO>> dict, Concept root) throws ObjectNotFoundException {
		if(dict != null) {
			List<DictNodeDTO> dictlist = dict.get(0l);
			DictNodeDTO rootDict = dictlist.get(0);
			Long nextParId = rootDict.getNodeId();

			root = closureServ.loadRoot(url);
			if(root != null) {
				String pref = rootDict.getLiterals().get(LiteralService.PREF_NAME).getValue();
				String desc = rootDict.getLiterals().get(LiteralService.DESCRIPTION).getValue();
				root = literalServ.prefAndDescription(pref, desc, root);
			}
			root.setActive(true);
			root = closureServ.saveToTree(null, root);

			recursionCreateDictionary(dict, nextParId, root.getID());
		}else {// create empty dict
			root = closureServ.loadRoot(url);
			root = literalServ.prefAndDescription(url, "", root);
			root.setActive(true);
			root = closureServ.saveToTree(null, root);
		}
	}

	@Transactional
	public ImportWorkflowDTO createResourcesOnLocal(ImportWorkflowDTO data) {
		int countSkipt = 0;
		Map<String, ResourceDTO> resources = data.getResImport();
		try {
			for(String url:resources.keySet()) {
				Concept root = closureServ.loadConceptByIdentifierActive(url);
				if(root != null && root.getID() > 0 && resServ.isResourceUrl(url)) {
					// такой урл уже есть и он для ресурса
					countSkipt++;
				}else {// create
					DataCollectionDTO config = data.getConfigImport().get(url);

					// сразу создадим конфигурацию и ее assembly
					Concept dataConf = closureServ.loadRoot(SystemService.DATA_COLLECTIONS_ROOT);

					List<Concept> concepts = literalServ.loadOnlyChilds(dataConf);
					Concept ret = new Concept();
					for(Concept c:concepts) {
						if(c.getIdentifier().equalsIgnoreCase(config.getUrl().getValue())) {
							ret = c;
						}
					}
					if(ret.getID() == 0 ) { // create
						config.setNodeId(0l);
						config = superService.dataCollectionDefinitionSave(config);
						if(config.isValid()) {
							List<DataVariableDTO> list = data.getVarsImport().get(url);
							for(DataVariableDTO dv:list) {
								dv.setVarNodeId(0);
								dv.setNodeId(config.getNodeId());
								dv = superService.dataCollectionVariableSave(dv);

								// теперь создаем словарь для этого ресурса
								if(dv.isValid() && dv.getClazz().getValue().equals("documents")) {
									String dictURL = dv.getDictUrl().getValue();
									Concept dictRoot = closureServ.loadRoot(dictURL);
									if(dictRoot == null) {
										Map<Long, List<DictNodeDTO>> dict = data.getDictsImport().get(dictURL);
										buildDict(url, dict, root);
									}
								}
							}
						}else {
							data.setValid(false);
							data.setIdentifier(config.getIdentifier());

							break;
						}
					}// иначе, считаем что она есть и все ок

					// теперь создаем запись сомого ресурса
					ResourceDTO resDTO = resources.get(url);
					resDTO = superService.resourceDefinitionSave(resDTO);
					if(!resDTO.isValid()) {
						data.setValid(false);
						data.setIdentifier(resDTO.getIdentifier());

						break;
					}
				}
			}
		} catch (ObjectNotFoundException e) {
			data.setValid(false);
			data.setIdentifier("Bad configuration resources!Error in log!");
			e.printStackTrace();
		}
		if(data.isValid()) {
			if(countSkipt == resources.keySet().size()) {
				data.getStatusTable().getRows().get(1).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_RECEIVED);
				data.getStatusTable().getRows().get(1).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_SKIPT);	
			}else {
				data.getStatusTable().getRows().get(1).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_RECEIVED);
				data.getStatusTable().getRows().get(1).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_RECEIVED);	
			}
			data.getStatusTable().getRows().get(2).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_RECEIVING);
		}else {
			data.getStatusTable().getRows().get(1).getCellByKey(NAME_COL_VALIDATION).setValue(data.getIdentifier());
			data.getStatusTable().getRows().get(1).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_ERROR);
		}

		cleanImportLists(data);
		return data;
	}

	@Transactional
	private ImportWorkflowDTO createDataConfigsOnLocal(ImportWorkflowDTO data) throws ObjectNotFoundException {
		int countSkipt = 0;
		Map<String, DataCollectionDTO> configs = data.getConfigImport();

		for(String url:configs.keySet()) {
			Concept root = closureServ.loadConceptByIdentifierActive(url);
			if(root != null && root.getID() > 0 && assistServ.isDataConfigurationUrl(url)) {
				// такой урл уже есть
				countSkipt++;
			}else {// create
				DataCollectionDTO config = data.getConfigImport().get(url);

				// сразу создадим конфигурацию и ее assembly
				Concept dataConf = closureServ.loadRoot(SystemService.DATA_COLLECTIONS_ROOT);
				List<Concept> concepts = literalServ.loadOnlyChilds(dataConf);
				Concept ret = new Concept();
				for(Concept c:concepts) {
					if(c.getIdentifier().equalsIgnoreCase(config.getUrl().getValue())) {
						ret = c;
					}
				}
				if(ret.getID() == 0 ) { // create
					config.setNodeId(0);
					config = superService.dataCollectionDefinitionSave(config);
					if(config.isValid()) {
						List<DataVariableDTO> list = data.getVarsImport().get(url);
						for(DataVariableDTO dv:list) {
							dv.setVarNodeId(0);
							dv.setNodeId(config.getNodeId());
							dv = superService.dataCollectionVariableSave(dv);

							// все словари создали ранее
						}
					}else {
						data.setValid(false);
						data.setIdentifier(config.getIdentifier());

						break;
					}
				}// иначе, считаем что она есть и все ок
			}
		}
		if(data.isValid()) {
			if(countSkipt == configs.keySet().size()) {
				data.getStatusTable().getRows().get(2).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_RECEIVED);
				data.getStatusTable().getRows().get(2).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_SKIPT);	
			}else {
				data.getStatusTable().getRows().get(2).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_RECEIVED);
				data.getStatusTable().getRows().get(2).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_RECEIVED);	
			}
			data.getStatusTable().getRows().get(3).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_RECEIVING);
		}else {
			data.getStatusTable().getRows().get(2).getCellByKey(NAME_COL_VALIDATION).setValue(data.getIdentifier());
			data.getStatusTable().getRows().get(2).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_ERROR);
		}

		cleanImportLists(data);
		return data;
	}

	/**создаем запись словаря на локальной машине
	 * если APPLICATION_URL уже существует - следующий шаг(импорт активностей) пропускаем вообще
	 * @throws JsonProcessingException */
	@Transactional
	public ImportWorkflowDTO createWFOnLocal(ImportWorkflowDTO data, UserDetailsDTO user) throws ObjectNotFoundException, JsonProcessingException {
		// ищем, может такой процес уже есть
		Concept root = systemServ.findProccessByUrl(data.getProcessURL(), data.getWfURL());
		if(root == null) {// create
			Concept procRoot = null;

			List<Concept> ret = new ArrayList<Concept>();
			ret = conceptRepo.findAllByIdentifier(data.getProcessURL());
			if(ret != null && ret.size() > 0) {
				for(Concept c:ret) {
					if(c.getActive()) {
						Concept parent = closureServ.getParent(c);
						if(parent == null) {
							procRoot = c;
							break;
						}
					}
				}
			}

			if(procRoot == null) {
				data.setValid(false);
				data.getStatusTable().getRows().get(3).getCellByKey(NAME_COL_VALIDATION).setValue("Not find procces dictionary");
				data.getStatusTable().getRows().get(3).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_ERROR);
				return data;
			}
			String applURL = null;
			root = new Concept();
			root.setIdentifier((new Date()).toString());
			root = closureServ.saveToTree(procRoot, root);
			root.setIdentifier(root.getID() + "");
			root = closureServ.save(root);

			DictNodeDTO dict = data.getDict();
			for(String lit:dict.getLiterals().keySet()){
				if(lit.equals(LiteralService.URL)) {
					literalServ.createUpdateLiteral(lit, dict.getLiterals().get(lit).getValue(), root);
				}else {
					literalServ.createUpdateLiteral(lit, dict.getLiterals().get(lit).getValue(), root);	
				}
				
				if(lit.equals(LiteralService.APPLICATION_URL)) {
					applURL = dict.getLiterals().get(lit).getValue();
				}
			}

			if(applURL != null) {
				// если по такому урлу уже есть конфигурация - пропускаем следующий шаг
				Concept confConcept = closureServ.loadConceptByIdentifierActive("configuration." + applURL);
				if(confConcept != null) {
					data.setValid(false);
					data.getStatusTable().getRows().get(3).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_RECEIVED);
					data.getStatusTable().getRows().get(3).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_SKIPT);
				}else {
					data = createActivitiesConfigsOnLocal(data, user);
				}
			}
		}else {// skipt
			data.getStatusTable().getRows().get(3).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_RECEIVED);
			data.getStatusTable().getRows().get(3).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_SKIPT);
		}

		if(data.isValid()) {
			data.getStatusTable().getRows().get(3).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_RECEIVED);
			data.getStatusTable().getRows().get(3).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_RECEIVED);
		}

		cleanImportLists(data);
		return data;
	}

	/**создаем на локале активити
	 * раз мы сюда пришли - то на локале нет конфигурации выбранного процеса 
	 * @throws JsonProcessingException */
	@Transactional
	private ImportWorkflowDTO createActivitiesConfigsOnLocal(ImportWorkflowDTO data, UserDetailsDTO user) throws ObjectNotFoundException, JsonProcessingException {
		data.setValid(false);
		List<ThingDTO> path = data.getPathImport();
		if(path != null && path.size() > 0) {
			// запись словаря должна уже быть создана - ищем ее
			Concept dictNode = systemServ.findProccessByUrl(data.getProcessURL(), data.getWfURL());
			if(dictNode != null) {
				String applurl = literalServ.readValue(LiteralService.APPLICATION_URL, dictNode);
				if (applurl.length() > 5) {
					Concept firstActivity = closureServ.loadRoot("configuration." + applurl.toLowerCase());

					//firstThing = thingServ.saveUnderOwner(firstThing, user);

					// 1 step - all valid
					int count = 0;
					for(ThingDTO dto:path) {
						dto.setNodeId(0l);
						dto.setParentId(0l);
						// нужно заменить выбранные значения в словарях
						//dto.getDictionaries().get("").get
						for(String key:dto.getDictionaries().keySet()) {
							DictionaryDTO d = dto.getDictionaries().get(key);
							Concept dc = closureServ.loadConceptByIdentifierActive(d.getUrl());
							if(dc != null) {// dictionary is present
								String selkey = "";
								for(TableRow r:d.getTable().getRows()) {
									if(r.getSelected()) {
										selkey = String.valueOf(r.getRow().get(1).getOriginalValue());
										break;
									}
								}
								if(!selkey.isEmpty()) {
									Concept newIt = closureServ.loadConceptByIdentifierActive(selkey);
									if(newIt != null) {
										dto.getDictionaries().get(key).getPrevSelected().clear();
										dto.getDictionaries().get(key).getPrevSelected().add(newIt.getID());
									}
								}
							}
						}
						dto = validServ.thing(dto, new ArrayList<AssemblyDTO>(), false);
						if(dto.isValid()) {
							count++;
						}
					}
					// 2 step - all valid - create all
					if(count == path.size()) {
						ThingDTO firstThing = path.get(0);
						firstThing.setParentId(0);
						firstThing.setNodeId(firstActivity.getID());
						thingServ.storeConfigurationToNode(firstThing.getUrl(), firstActivity);
						Thing thing = new Thing();
						thing = boilerServ.thingByNode(firstActivity, thing);
						thing.setConcept(firstActivity);
						thing.setUrl(firstThing.getUrl());
						firstThing = thingServ.storeDataUnderThing(firstThing, user, firstActivity, thing);
						thing = thingRepo.save(thing);

						for(int i = 1; i < path.size(); i++) {
							ThingDTO dto = path.get(i);

							Concept node = new Concept();
							node = closureServ.save(node);
							node.setIdentifier(node.getID()+"");
							node = closureServ.saveToTree(firstActivity, node);

							dto.setParentId(firstActivity.getID());
							dto.setNodeId(node.getID());

							thing = new Thing();
							thing = boilerServ.thingByNode(node, thing);
							thing.setConcept(node);
							thing.setUrl(dto.getUrl());

							//store data under the node and thing
							dto = thingServ.storeDataUnderThing(dto, user, node, thing);
							//store a thing
							thing = boilerServ.saveThing(thing);
						}
						data.setValid(true);
					}
				}
			}
		}

		if(data.isValid()) {
			data.getStatusTable().getRows().get(3).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_RECEIVED);
			data.getStatusTable().getRows().get(3).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_RECEIVED);	
		}else{
			data.getStatusTable().getRows().get(3).getCellByKey(NAME_COL_VALIDATION).setValue(data.getIdentifier());
			data.getStatusTable().getRows().get(3).getCellByKey(NAME_COL_IMPORT).setValue(STATUS_ERROR);
		}

		cleanImportLists(data);
		return data;
	}

	private ImportWorkflowDTO requestToMainServer(ImportWorkflowDTO data, String req) {
		try {
			String url = data.getServerurl() + req;
			ResponseEntity<ImportWorkflowDTO> r = restTemplate.postForEntity(url, data, ImportWorkflowDTO.class);

			if(r != null && r.getBody() != null && r.getStatusCode() == HttpStatus.OK) {
				data = r.getBody();
				data.setValid(true);
				data.setIdentifier("");
			}
		}
		catch (RestClientException e) {
			data.setValid(false);
			data.setIdentifier(messages.get("errorloaddata"));
			return data;
		}
		return data;
	}

	private void cleanImportLists(ImportWorkflowDTO data) {
		// очистим все списки, карті и т.д. для импорта
		data.getDictsImport().clear();
		data.getVarsImport().clear();
		data.getConfigImport().clear();
		data.getResImport().clear();
		data.getPathImport().clear();
	}

	private Headers createHeadersStatus(Headers ret) {
		ret.getHeaders().add(TableHeader.instanceOf(
				"prefLbl", 
				"components",
				false,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				NAME_COL_VALIDATION, 
				"descr_VALD",
				false,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				NAME_COL_IMPORT, 
				"global_import_short",
				false,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		ret= boilerServ.translateHeaders(ret);
		return ret;
	}
}
