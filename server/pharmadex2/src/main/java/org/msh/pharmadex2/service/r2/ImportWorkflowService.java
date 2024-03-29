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
import org.msh.pharmadex2.dto.AsyncInformDTO;
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

	private String RESULT = "Result";
	private String STATUS = "Status";
	
	private String RESULT_DICTIONARIES = "Dictionaries";
	private String RESULT_RESOURCES = "Resources";
	private String RESULT_DATACONFIG = "Data Configurations";
	private String RESULT_WORKFLOW = "WorkFlow";

	private String STATUS_IMPORTING = "Importing";
	private String STATUS_SCHEDULED = "Scheduled";
	private String STATUS_RECEIVED = "Received";
	//private String STATUS_SKIPT = "Skipt";
	private String STATUS_ERROR = "Error!";

	private static String req_ping = "/api/public/pingbyimport";
	private static String req_processesload = "/api/public/importwf/processes/load";
	private static String req_dictionaries = "/api/public/importwf/dictionaries";
	private static String req_resources = "/api/public/importwf/resources";
	private static String req_dataconfigs = "/api/public/importwf/dataconfigs";
	private static String req_wf = "/api/public/importwf/wf";
	//private static String req_activitiesconfigs = "/api/public/importwf/activitiesconfigs";

	public ImportWorkflowDTO load(ImportWorkflowDTO data) throws ObjectNotFoundException {
		//data.getServerurl().setValue(data.getIdentifier());
		data.setConnect(false);
		data.getServerurl().setStrict(false);
		data.setIdentifier("");
		data.setValid(false);// by red check
		cleanImportLists(data);
		data.getProcTable().getRows().clear();
		data.getWfTable().getRows().clear();
		data.setProcessIDselect(0l);
		data.setProcessURL("");
		data.setWfIDselect(0l);
		data.setWfURL("");

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

	/*public ImportWorkflowDTO reload(ImportWorkflowDTO data) throws ObjectNotFoundException {
		cleanImportLists(data);
		data.getProcTable().getRows().clear();
		data.getWfTable().getRows().clear();
		data.setProcessIDselect(0l);
		data.setProcessURL("");
		data.setWfIDselect(0l);
		data.setWfURL("");

		data = loadProccesses(data);

		return data;
	}*/

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

		for(TableRow r:rows) {
			String url = (String)r.getRow().get(0).getOriginalValue();
			Concept curProc = systemService.findProccessByUrl(data.getProcessURL(), url);
			if(curProc == null) {
				newRows.add(r);
			}
		}
		data.getWfTable().getRows().clear();
		data.getWfTable().getRows().addAll(newRows);
		data.setIdentifier("");
		return data;
	}

	@Transactional
	public ImportWorkflowDTO importRunWorker(ImportWorkflowDTO data, UserDetailsDTO user) throws ObjectNotFoundException, JsonProcessingException {
		messages.setDefaultLocaleToLCH();
		AsyncService.writeAsyncContext(AsyncService.PROGRESS_SHEETS, "4");

		// run next step
		data = dictionaries(data);
		if(data.isValid()) {
			data = resources(data);
			if(data.isValid()) {
				data = dataConfigs(data);
				if(data.isValid()) {
					data = wf(data, user);
				}
			}
		}
		return data;
	}

	/**
	 * load result import from AsyncService and build result table
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ImportWorkflowDTO loadResultTabel(ImportWorkflowDTO data) throws ObjectNotFoundException {
		String title = "";
		if(data.getProcTable().getRows() != null) {
			for(TableRow r:data.getProcTable().getRows()) {
				if(r.getSelected()) {
					title = r.getRow().get(0).getValue();
					break;
				}
			}
		}
		if(data.getWfTable().getRows() != null) {
			for(TableRow r:data.getWfTable().getRows()) {
				if(r.getSelected()) {
					title += " - " + r.getRow().get(0).getValue();
					break;
				}
			}
		}
		data.setTitleResultTable(title);

		// создаем таблицу
		if(data.getStatusTable().getHeaders().getHeaders().size() == 0) {
			data.getStatusTable().setHeaders(createHeadersStatus(data.getStatusTable().getHeaders()));
		}
		data.getStatusTable().getRows().clear();

		TableRow rowDict = createRow(RESULT_DICTIONARIES);
		data.getStatusTable().getRows().add(rowDict);

		TableRow rowRes = createRow(RESULT_RESOURCES);
		data.getStatusTable().getRows().add(rowRes);

		TableRow rowConfig = createRow(RESULT_DATACONFIG);
		data.getStatusTable().getRows().add(rowConfig);

		// надо сразу создать запись словаря, а потом уже активности к ней
		TableRow rowWFConfig = createRow(RESULT_WORKFLOW);
		data.getStatusTable().getRows().add(rowWFConfig);

		data.getStatusTable().setSelectable(false);

		//data.getStatusTable().getRows().get(0).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_RECEIVING);

		String row1_status = AsyncService.readAsyncContext(RESULT_DICTIONARIES + "/" + STATUS);
		String row1_result = AsyncService.readAsyncContext(RESULT_DICTIONARIES + "/" + RESULT);
		rowDict.getRow().get(1).setValue(row1_status);
		rowDict.getRow().get(2).setValue(row1_result);
		
		String row2_status = AsyncService.readAsyncContext(RESULT_RESOURCES + "/" + STATUS);
		String row2_result = AsyncService.readAsyncContext(RESULT_RESOURCES + "/" + RESULT);
		if(row2_status.isEmpty())
			row2_status = STATUS_SCHEDULED;
		rowRes.getRow().get(1).setValue(row2_status);
		rowRes.getRow().get(2).setValue(row2_result);
		
		String row3_status = AsyncService.readAsyncContext(RESULT_DATACONFIG + "/" + STATUS);
		String row3_result = AsyncService.readAsyncContext(RESULT_DATACONFIG + "/" + RESULT);
		if(row3_status.isEmpty())
			row3_status = STATUS_SCHEDULED;
		rowConfig.getRow().get(1).setValue(row3_status);
		rowConfig.getRow().get(2).setValue(row3_result);
		
		String row4_status = AsyncService.readAsyncContext(RESULT_WORKFLOW + "/" + STATUS);
		String row4_result = AsyncService.readAsyncContext(RESULT_WORKFLOW + "/" + RESULT);
		if(row4_status.isEmpty())
			row4_status = STATUS_SCHEDULED;
		rowWFConfig.getRow().get(1).setValue(row4_status);
		rowWFConfig.getRow().get(2).setValue(row4_result);
		
		return data;
	}

	private TableRow createRow(String name) {
		TableRow row = new TableRow();
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
		//AsyncService.writeAsyncContext(AsyncService.PROGRESS_SHEETS_IMPORTED, "1");
		AsyncService.writeAsyncContext(AsyncService.PROGRESS_CURRENT_SHEET, RESULT_DICTIONARIES + " " + STATUS_IMPORTING);
		
		// validate and import dictionaries 
		logger.trace("ImportWF: dictionaries requested");
		data = requestToMainServer(data, req_dictionaries);

		if(data.isValid()) {
			// создаем на локальном словарu
			data = createDictionariesOnLocal(data);
		}else {
			AsyncService.writeAsyncContext(RESULT_DICTIONARIES + "/" + STATUS, STATUS_ERROR);
			AsyncService.writeAsyncContext(RESULT_DICTIONARIES + "/" + RESULT, data.getIdentifier());
			AsyncService.writeAsyncContext(AsyncService.PROGRESS_STOP_ERROR, "Error");
			//data.getStatusTable().getRows().get(0).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_ERROR);
			//data.getStatusTable().getRows().get(0).getCellByKey(NAME_COL_IMPORT).setValue(data.getIdentifier());
		}
		return data;
	}

	public ImportWorkflowDTO resources(ImportWorkflowDTO data) throws ObjectNotFoundException {
		//AsyncService.writeAsyncContext(AsyncService.PROGRESS_SHEETS_IMPORTED, "2");
		AsyncService.writeAsyncContext(AsyncService.PROGRESS_CURRENT_SHEET, RESULT_RESOURCES + " " + STATUS_IMPORTING);
		
		logger.trace("ImportWF: resources requested");
		data = requestToMainServer(data, req_resources);
		if(data.isValid()) {
			// создаем на локальном ресурсы
			data = createResourcesOnLocal(data);
		}else {
			AsyncService.writeAsyncContext(RESULT_RESOURCES + "/" + STATUS, STATUS_ERROR);
			AsyncService.writeAsyncContext(RESULT_RESOURCES + "/" + RESULT, data.getIdentifier());
			AsyncService.writeAsyncContext(AsyncService.PROGRESS_STOP_ERROR, "Error");
			//data.getStatusTable().getRows().get(1).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_ERROR);
			//data.getStatusTable().getRows().get(1).getCellByKey(NAME_COL_IMPORT).setValue(data.getIdentifier());
		}
		return data;
	}

	public ImportWorkflowDTO dataConfigs(ImportWorkflowDTO data) {
		//AsyncService.writeAsyncContext(AsyncService.PROGRESS_SHEETS_IMPORTED, "3");
		AsyncService.writeAsyncContext(AsyncService.PROGRESS_CURRENT_SHEET, RESULT_DATACONFIG + " " + STATUS_IMPORTING);
		
		logger.trace("ImportWF: dataConfigs requested");
		data = requestToMainServer(data, req_dataconfigs);
		if(data.isValid()) {
			// создаем на локальном ресурсы
			try {
				data = createDataConfigsOnLocal(data);
			} catch (ObjectNotFoundException e) {
				e.printStackTrace();
			}
		}else {
			AsyncService.writeAsyncContext(RESULT_DATACONFIG + "/" + STATUS, STATUS_ERROR);
			AsyncService.writeAsyncContext(RESULT_DATACONFIG + "/" + RESULT, data.getIdentifier());
			AsyncService.writeAsyncContext(AsyncService.PROGRESS_STOP_ERROR, "Error");
			//data.getStatusTable().getRows().get(2).getCellByKey(NAME_COL_VALIDATION).setValue(STATUS_ERROR);
			//data.getStatusTable().getRows().get(2).getCellByKey(NAME_COL_IMPORT).setValue(data.getIdentifier());
		}
		return data;
	}

	public ImportWorkflowDTO wf(ImportWorkflowDTO data, UserDetailsDTO user) throws ObjectNotFoundException, JsonProcessingException {
		//AsyncService.writeAsyncContext(AsyncService.PROGRESS_SHEETS_IMPORTED, "4");
		AsyncService.writeAsyncContext(AsyncService.PROGRESS_CURRENT_SHEET, RESULT_WORKFLOW + " " + STATUS_IMPORTING);
		
		logger.trace("ImportWF: wf&activities requested");
		data = requestToMainServer(data, req_wf);
		if(data.isValid()) {
			// создаем на локальном 
			data = createWFOnLocal(data, user);
		}else {
			AsyncService.writeAsyncContext(RESULT_WORKFLOW + "/" + STATUS, STATUS_ERROR);
			AsyncService.writeAsyncContext(RESULT_WORKFLOW + "/" + RESULT, data.getIdentifier());
			AsyncService.writeAsyncContext(AsyncService.PROGRESS_STOP_ERROR, "Error");
			//data.getStatusTable().getRows().get(3).getCellByKey(NAME_COL_VALIDATION).setValue(data.getIdentifier());
			//data.getStatusTable().getRows().get(3).getCellByKey(NAME_COL_IMPORT).setValue(data.getIdentifier());
		}
		return data;
	}

	@Transactional
	private ImportWorkflowDTO createDictionariesOnLocal(ImportWorkflowDTO data) throws ObjectNotFoundException {
		int countSkipt = 0;
		int count = 0;
		Map<String, Map<Long, List<DictNodeDTO>>> map = data.getDictsImport();
		if(map != null && map.keySet() != null) {
			logger.trace("ImportWF: dictionaries received-" + data.getDictsImport().keySet().size());
			for(String url:map.keySet()) {
				Concept root = closureServ.loadConceptByIdentifierActive(url);
				if(root != null && root.getID() > 0) {
					countSkipt++;
				}else {// create
					Map<Long, List<DictNodeDTO>> dict = map.get(url);
					buildDict(url, dict, root);
					count++;
				}
			}
		}else {
			logger.trace("ImportWF: dictionaries received-NULL");
		}

		logger.trace("ImportWF: dictionaries created-" + count);
		logger.trace("ImportWF: dictionaries skipt-" + countSkipt);

		data = printResult(RESULT_DICTIONARIES, data, 1, countSkipt, count, data.getDictsImport().keySet().size());

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
	public void buildDict(String url, Map<Long, List<DictNodeDTO>> listdict, Concept root) throws ObjectNotFoundException {
		if(listdict != null) {
			List<DictNodeDTO> dictlist = listdict.get(0l);
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

			recursionCreateDictionary(listdict, nextParId, root.getID());
		}else {// create empty dict
			root = closureServ.loadRoot(url);
			root = literalServ.prefAndDescription(url, "", root);
			root.setActive(true);
			root = closureServ.saveToTree(null, root);
		}
	}

	/**
	 * создание всех данных для ресурсов начинаем со словаря, потом все DataVariableDTO,
	 * потом конфигурация, а потом уже запись ресурса
	 * 
	 * Если есть запись ресурса и запись конфигурации(русть и пустой) - считаем что ОК
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ImportWorkflowDTO createResourcesOnLocal(ImportWorkflowDTO data) throws ObjectNotFoundException{
		int countSkipt = 0;
		int count = 0;
		Map<String, ResourceDTO> resources = data.getResImport();
		String curImportURL = "";
		try {
			// сразу создадим конфигурацию и ее assembly
			Concept dataConf = closureServ.loadRoot(SystemService.DATA_COLLECTIONS_ROOT);
			if(resources != null && resources.keySet().size() > 0) {
				logger.trace("ImportWF: resources received-" + resources.keySet().size());
				for(String url:resources.keySet()) {
					curImportURL = url;
					Concept root = closureServ.loadConceptByIdentifierActive(url);

					DataCollectionDTO config = data.getConfigImport().get(url);
					List<Concept> concepts = literalServ.loadOnlyChilds(dataConf);
					Concept ret = new Concept();
					for(Concept c:concepts) {
						if(c.getIdentifier().equalsIgnoreCase(config.getUrl().getValue()) &&
								c.getActive()) {
							ret = c;
						}
					}

					if(root != null && root.getID() > 0 && resServ.isResourceUrl(url)
							&& ret != null && ret.getID() > 0) {
						countSkipt++;
					}else {
						config.setNodeId(0l);
						config = superService.dataCollectionDefinitionSave(config);
						if(config.isValid()) {
							List<DataVariableDTO> list = data.getVarsImport().get(url);
							for(DataVariableDTO dv:list) {
								dv.setVarNodeId(0);
								dv.setNodeId(config.getNodeId());
								dv = superService.dataCollectionVariableSave(dv, false);

								if(dv.isValid() || !dv.isStrict()) {
									// теперь создаем словарь для этого ресурса
									if(dv.getClazz().getValue().getCode().equals("documents")) {
										String dictURL = dv.getDictUrl().getValue();
										Concept dictRoot = closureServ.loadConceptByIdentifierActive(dictURL);
										if(dictRoot == null) {
											Map<Long, List<DictNodeDTO>> dict = data.getDictsImport().get(dictURL);
											buildDict(dictURL, dict, dictRoot);
										}
									}
								}
							}
							// теперь создаем запись сомого ресурса
							ResourceDTO resDTO = resources.get(url);
							resDTO = superService.resourceDefinitionSave(resDTO);
							if(resDTO.isValid()) {
								count++;
							}else {
								data.setValid(false);
								data.setIdentifier("Error in resources URL: " + url + ". " + resDTO.getIdentifier());

								break;
							}
						}else {
							data.setValid(false);
							data.setIdentifier("Error in config resources URL: " + url + ". " + config.getIdentifier());

							break;
						}
					}
				}
			}else {
				logger.trace("ImportWF: resources received-NULL");
			}
		} catch (ObjectNotFoundException e) {
			data.setValid(false);
			data.setIdentifier("Error in resources URL: " + curImportURL);
			e.printStackTrace();
		}
		logger.trace("ImportWF: resources created-" + count);
		logger.trace("ImportWF: resources skipt-" + countSkipt);

		data = printResult(RESULT_RESOURCES, data, 2, countSkipt, count, resources.keySet().size());
		cleanImportLists(data);
		return data;
	}

	@Transactional
	public ImportWorkflowDTO createDataConfigsOnLocal(ImportWorkflowDTO data) throws ObjectNotFoundException {
		int countSkipt = 0;
		int count = 0;
		Map<String, DataCollectionDTO> configs = data.getConfigImport();
		if(configs != null && configs.keySet().size() > 0) {
			logger.trace("ImportWF: DataConfigs received-" + configs.keySet().size());
			Concept dataConf = closureServ.loadRoot(SystemService.DATA_COLLECTIONS_ROOT);

			for(String url:configs.keySet()) {
				Concept root = closureServ.findConceptInBranchByIdentifier(dataConf, url);

				if(root != null && root.getID() > 0 && assistServ.isDataConfigurationUrl(url)) {
					// такой урл уже есть
					countSkipt++;
				}else {// create
					DataCollectionDTO config = data.getConfigImport().get(url);

					config.setNodeId(0);
					config = superService.dataCollectionDefinitionSave(config);
					if(config.isValid()) {
						List<DataVariableDTO> list = data.getVarsImport().get(url);
						for(DataVariableDTO dv:list) {
							dv.setVarNodeId(0);
							dv.setNodeId(config.getNodeId());
							dv = superService.dataCollectionVariableSave(dv, false);
						}
						count++;
					}else {
						data.setValid(false);
						data.setIdentifier(config.getIdentifier());

						break;
					}
				}
			}
		}else {
			logger.trace("ImportWF: DataConfigs received-NULL");
		}
		logger.trace("ImportWF: DataConfigs created-" + count);
		logger.trace("ImportWF: DataConfigs skipt-" + countSkipt);

		data = printResult(RESULT_DATACONFIG, data, 3, countSkipt, count, configs.keySet().size());
		cleanImportLists(data);
		return data;
	}

	/**создаем запись словаря на локальной машине
	 * если APPLICATION_URL уже существует - следующий шаг(импорт активностей) пропускаем вообще
	 * @throws JsonProcessingException */
	@Transactional
	public ImportWorkflowDTO createWFOnLocal(ImportWorkflowDTO data, UserDetailsDTO user) throws ObjectNotFoundException, JsonProcessingException {
		data.setValid(false);
		DictNodeDTO dict = data.getDict();
		int countSkipt = 0;
		int count = 0;
		if(dict != null) {
			logger.trace("ImportWF: WF received-1");
			//  в начале была проверка на существующие процесы - такого на локале нет
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

			if(procRoot != null) {
				String applURL = null;
				Concept root = new Concept();
				root.setIdentifier((new Date()).toString());
				root = closureServ.saveToTree(procRoot, root);
				root.setIdentifier(root.getID() + "");
				root = closureServ.save(root);
				count++;

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
				logger.trace("ImportWF: WF created-1");

				if(applURL != null) {
					// если по такому урлу уже есть конфигурация - пропускаем 
					Concept confConcept = closureServ.loadConceptByIdentifierActive("configuration." + applURL);
					if(confConcept == null){
						data = createActivitiesConfigsOnLocal(data, applURL, count, user);
						logger.trace("ImportWF: Activities skipt-" + countSkipt);
					}else {
						logger.trace("ImportWF: Activities skipt");
						data.setValid(true);
					}
				}
			}
		}else {
			logger.trace("ImportWF: WF received-NULL");
		}

		data = printResult(RESULT_WORKFLOW, data, 4, countSkipt, count, countSkipt+count);
		cleanImportLists(data);
		return data;
	}

	/**создаем на локале активити
	 * раз мы сюда пришли - то на локале нет конфигурации выбранного процеса 
	 * @throws JsonProcessingException */
	@Transactional
	private ImportWorkflowDTO createActivitiesConfigsOnLocal(ImportWorkflowDTO data, String applURL, int count, UserDetailsDTO user) throws ObjectNotFoundException, JsonProcessingException {
		List<ThingDTO> path = data.getPathImport();
		if(path != null && path.size() > 0) {
			logger.trace("ImportWF: Activities received-" + path.size());
			Concept firstActivity = closureServ.loadRoot("configuration." + applURL);

			int c = 0;
			// 1 step - all valid
			for(ThingDTO dto:path) {
				// нужно заменить выбранные значения в словарях
				dto = replaceValueInDictionaries(data, dto);
				dto.setNodeId(0l);
				dto.setParentId(0l);
				dto = validServ.thing(dto, new ArrayList<AssemblyDTO>(), false);
				if(dto.isValid()) {
					c++;
				}
			}
			// 2 step - all valid - create all
			if(c == path.size()) {
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
				count++;

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
					count++;
				}
				data.setValid(true);
			}
		}
		logger.trace("ImportWF: Activities created-" + count);
		return data;
	}

	/**проверим есть ли словари исполнителей и финализации в БД 
	если нет - создадим
	так же заменим в активностях выбранные значения на значения из текущей БД*/
	private ThingDTO replaceValueInDictionaries(ImportWorkflowDTO data, ThingDTO dto) {
		try {
			Map<String, Map<Long, List<DictNodeDTO>>> dicts = data.getDictsImport();
			for(String urldict:dicts.keySet()) {
				Concept root = closureServ.loadConceptByIdentifierActive(urldict);
				if(root == null) {
					Map<Long, List<DictNodeDTO>> dict = dicts.get(urldict);
					buildDict(urldict, dict, root);
				}
			}

			if(dto != null) {
				Concept execConcept = null;
				Concept finalConcept = null;

				List<String> vals = data.getPathImportDict().get(dto.getNodeId());
				if(vals != null && vals.get(0) != null)
					execConcept = closureServ.loadConceptByIdentifierActive(vals.get(0));
				if(vals != null && vals.get(1) != null)
					finalConcept = closureServ.loadConceptByIdentifierActive(vals.get(1));

				DictionaryDTO dictExec = dto.getDictionaries().get(AssemblyService.ACTIVITY_EXECUTIVES);
				dictExec.getPrevSelected().clear();
				if(dictExec != null && execConcept != null) {
					dictExec.getPrevSelected().add(execConcept.getID());
				}

				DictionaryDTO dictFinal = dto.getDictionaries().get(AssemblyService.ACTIVITY_CONFIG_FINALIZE);
				dictFinal.getPrevSelected().clear();
				if(dictFinal != null) {
					if(finalConcept == null) {// если из главного пришло не понятное значение, для всех активити ставим НО, для последнего Ацепт
						Concept finDict = closureServ.loadConceptByIdentifier(SystemService.DICTIONARY_SYSTEM_FINALIZATION);
						finalConcept = closureServ.findConceptInBranchByIdentifier(finDict, SystemService.FINAL_ACCEPT);
					}

					if(finalConcept != null)
						dictFinal.getPrevSelected().add(finalConcept.getID());
				}
			}
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
		}
		return dto;
	}

	private ImportWorkflowDTO requestToMainServer(ImportWorkflowDTO data, String req) {
		try {
			String url = data.getServerurl() + req;
			ResponseEntity<ImportWorkflowDTO> r = restTemplate.postForEntity(url, data, ImportWorkflowDTO.class);

			if(r != null && r.getBody() != null && r.getStatusCode() == HttpStatus.OK) {
				data = r.getBody();
				//data.setValid(true);
				//data.setIdentifier("");
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
		data.getPathImportDict().clear();
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

	//RESULT_DICTIONARIES
	private ImportWorkflowDTO printResult(String rowKey, ImportWorkflowDTO data, int numRow, int avail, int receiv, int tot) {
		String str = "Available " + avail + "; received new " + receiv + "; total " + tot;
		
		AsyncService.writeAsyncContext(AsyncService.PROGRESS_SHEETS_IMPORTED, numRow + "");
		//AsyncService.writeAsyncContext(AsyncService.PROGRESS_CURRENT_SHEET, rowKey);
		
		if(data.isValid()) {
			AsyncService.writeAsyncContext(rowKey + "/" + STATUS, STATUS_RECEIVED);
			AsyncService.writeAsyncContext(rowKey + "/" + RESULT, "Success. " + str);
		}else {
			AsyncService.writeAsyncContext(rowKey + "/" + STATUS, STATUS_ERROR);
			AsyncService.writeAsyncContext(rowKey + "/" + RESULT, " Error! " + str + ". " + data.getIdentifier());
			AsyncService.writeAsyncContext(AsyncService.PROGRESS_STOP_ERROR, "Error");
		}

		return data;
	}

	/**
	 * Progress bar logic for admin unit import
	 * @param data
	 * @return
	 */
	public AsyncInformDTO calcProgress(AsyncInformDTO data) {
		//names
		data.setTitle(messages.get("processImportWorkflow"));

		String importedSheets = AsyncService.readAsyncContext(AsyncService.PROGRESS_SHEETS_IMPORTED);
		String totalSheets = AsyncService.readAsyncContext(AsyncService.PROGRESS_SHEETS);
		
		String currentSheet = AsyncService.readAsyncContext(AsyncService.PROGRESS_CURRENT_SHEET);
		
		Float importedSh = new Float(0);
		if(!importedSheets.isEmpty()) {
			importedSh = new Float(importedSheets);
		}
		int coef = 25*importedSh.intValue();
		if(coef == 0) {
			coef = 15;
		}
		data.setComplPercent(coef);
		data.setProgressMessage(currentSheet);
				
		String stopError = AsyncService.readAsyncContext(AsyncService.PROGRESS_STOP_ERROR);
		if(!stopError.isEmpty()) {// proces Stop by Error
			data.setCompleted(false);
			data.setCancelled(true);
		}else {
			data.setCancelled(false);
			data.setCompleted(totalSheets.equals(importedSheets));
		}
		return data;
	}
}
