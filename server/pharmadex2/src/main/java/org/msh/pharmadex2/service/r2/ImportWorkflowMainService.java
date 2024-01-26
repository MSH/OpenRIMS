package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.repository.common.JdbcRepository;
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
import org.msh.pharmadex2.dto.WorkflowDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.DtoService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ImportWorkflowMainService {

	private static final Logger logger = LoggerFactory.getLogger(ImportWorkflowMainService.class);
	@Autowired
	private SystemService systemServ;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private SupervisorService superService;
	@Autowired
	private ThingRepo thingRepo;
	@Autowired
	private ValidationService validServ;
	@Autowired
	private ThingService thingServ;
	@Autowired
	private DtoService dtoServ;
	@Autowired
	private DictService dictServ;

	/**
	 * load data to ProcTable (list dictionaries) and to WfTable
	 * to WfTable load process? when have 1 finalization activity
	 * @param data
	 * @return
	 */
	public ImportWorkflowDTO loadProcesses(ImportWorkflowDTO data) {
		try {
			LocaleContextHolder.setDefaultLocale(Messages.parseLocaleString("EN_US"));
			
			if(data.getProcessIDselect() == 0) {// первая загрузка
				DictionaryDTO procDictionary = systemServ.stagesDictionary();

				data.setProcTable(procDictionary.getTable());
				data.getProcTable().setSelectable(true);
			}else if(data.getProcessIDselect() > 0 && data.getProcTable().getRows().size() > 0) {	
				data.getWfTable().getRows().clear();
				if(data.getWfIDselect() == 0 && data.isSelectedOnly()) {
					// empty right table
				}else {
					// find url by selected process
					String dictURL = "";
					Concept c = closureServ.loadConceptById(data.getProcessIDselect());
					if(c != null) {
						dictURL = c.getIdentifier();
						data.setProcessURL(dictURL);
					}

					//find data by wftable
					TableQtb table = new TableQtb();
					table.setHeaders(createTemplHeaders(table.getHeaders(), true));
					jdbcRepo.processes_finalization(dictURL);

					List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from processes_finalization", "", "", table.getHeaders());
					if(rows != null && rows.size() > 0) {
						if(data.getWfTable().getHeaders().getHeaders().size() == 0) {
							data.getWfTable().setHeaders(createHeaders(data.getWfTable().getHeaders()));
						}

						String search = data.getWfTable().getGeneralSearch();
						for(TableRow r:rows) {
							c = closureServ.loadConceptById(r.getDbID());
							String pref = literalServ.readPrefLabel(c);
							String desc = literalServ.readDescription(c);

							TableRow row = new TableRow();
							row.setDbID(r.getDbID());
							TableCell cell = new TableCell();
							cell.setKey("prefLbl");
							cell.setValue(pref);
							cell.setOriginalValue(r.getCellByKey("URL").getValue());
							row.getRow().add(cell);

							cell = new TableCell();
							cell.setKey("description");
							cell.setValue(desc);
							row.getRow().add(cell);

							if(data.getWfIDselect() == r.getDbID()) {
								row.setSelected(true);
							}

							if(search != null && search.length() > 2) {
								if(pref.toLowerCase().contains(search.toLowerCase())) {
									if(data.isSelectedOnly()) {
										if(data.getWfIDselect() == r.getDbID()) {
											data.getWfTable().getRows().add(row);
										}
									}else {
										data.getWfTable().getRows().add(row);
									}
								}
							}else {
								if(data.isSelectedOnly()) {
									if(data.getWfIDselect() == r.getDbID()) {
										data.getWfTable().getRows().add(row);
									}
								}else {
									data.getWfTable().getRows().add(row);
								}
							}
						}
					}
				}
			}
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
		}
		return data;
	}



	/**
	 * validate select process
	 * @param data
	 * @return
	 * @throws InterruptedException 
	 * @throws ObjectNotFoundException 
	 */
	public ImportWorkflowDTO validateDictionaries(ImportWorkflowDTO data) throws InterruptedException, ObjectNotFoundException {
		TableQtb table = new TableQtb();
		table.setHeaders(createTemplHeaders(table.getHeaders(), false));
		jdbcRepo.importWF_main(data.getWfIDselect(), 1);

		List<TableRow> rows = jdbcRepo.qtbGroupReport("select distinct d.dataurl as url from importwf_main as d order by d.dataurl", "", "", table.getHeaders());
		if(rows != null && rows.size() > 0) {
			data.setValid(true);
		}

		if(data.isValid()) {
			data = importDictionaries(data, rows);
		}
		return data;
	}

	/** получаем таблицу 
	 * ID ресурса | url ресурса | url configuration 
	 * @throws ObjectNotFoundException */
	public ImportWorkflowDTO validateResources(ImportWorkflowDTO data) throws InterruptedException, ObjectNotFoundException {
		// валидируем Resources
		TableQtb table = new TableQtb();
		table.setHeaders(createResourceHeaders(table.getHeaders()));
		jdbcRepo.importWF_main(data.getWfIDselect(), 2);

		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from importwf_main as d group by d.ID, d.dataurl, d.clazz, d.config", "", "", table.getHeaders());
		if(rows != null && rows.size() > 0) {
			int countAll = rows.size();
			int ind = 0;
			for(TableRow row:rows) {
				String url = row.getRow().get(1).getValue();
				if(url.length() > 3) {
					String configURL = row.getRow().get(3).getValue();
					if(configURL != null && configURL.length() > 3) {
						ind++;
					}
				}
			}
			if(countAll == ind) {
				data.setValid(true);
				data = importResources(data, rows);
			}else {
				data.setValid(false);
				data.setIdentifier("ERROR! Empty data configuration URL!");
			}
		}
		return data;
	}

	public ImportWorkflowDTO validateDataConfigs(ImportWorkflowDTO data) throws InterruptedException, ObjectNotFoundException {
		// отбираем урлы датаКонфигураций всех активностей+запись словаря
		TableQtb table = new TableQtb();
		table.setHeaders(createTemplHeaders(table.getHeaders(), true));
		table.getHeaders().getHeaders().add(TableHeader.instanceOf(
				"verif", 
				"verif",
				false,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		jdbcRepo.importWF_activities(data.getWfIDselect());
		String q = "Select actvs.ID as 'ID', actvs.dataurl as 'URL', " + 
				"if(isnull(allconf.url), 0, 1) as 'verif' " + 
				"from importwf_activities as actvs " + 
				"left join data_urls as allconf on allconf.url=actvs.dataurl and allconf.Lang=\"" + LocaleContextHolder.getLocale().toString().toUpperCase() + "\";";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(q, "", "", table.getHeaders());
		if(rows != null && rows.size() > 0) {
			int countAll = rows.size();
			int ind = 0;
			for(TableRow row:rows) {
				String verif = row.getRow().get(2).getValue();
				ind += Integer.valueOf(verif);
			}
			data.setValid(countAll == ind);
		}

		if(data.isValid()) {
			data = importDataConfigs(data, rows);
		}

		return data;
	}

	/** получаем запись словаря, проверяем ее на заполнение 
	 * @throws ObjectNotFoundException */
	public ImportWorkflowDTO validateWF(ImportWorkflowDTO data) throws InterruptedException, ObjectNotFoundException {
		Concept root = systemServ.findProccessByUrl(data.getProcessURL(), data.getWfURL());
		List<String> urls = new ArrayList<String>();
		
		int count = 4;
		
		if(data.getProcessURL().equals(SystemService.DICTIONARY_GUEST_DEREGISTRATION)) {
			count = 4;
		}else if(data.getProcessURL().equals(SystemService.DICTIONARY_GUEST_APPLICATIONS)) {
			count = 4;
		}else if(data.getProcessURL().equals(SystemService.DICTIONARY_HOST_APPLICATIONS)) {
			count = 2;
		}else if(data.getProcessURL().equals(SystemService.DICTIONARY_GUEST_INSPECTIONS)) {
			count = 4;
		}else if(data.getProcessURL().equals(SystemService.DICTIONARY_GUEST_AMENDMENTS)) {
			count = 4;
		}else if(data.getProcessURL().equals(SystemService.DICTIONARY_SHUTDOWN_APPLICATIONS)) {
			count = 2;
		}
		
		if(root != null) {
			String url = literalServ.readValue(LiteralService.URL, root);
			if(url != null && url.length() > 0) urls.add(url);
			
			String applURL = literalServ.readValue(LiteralService.APPLICATION_URL, root);
			if(applURL != null && applURL.length() > 0) urls.add(applURL);
			
			String dataURL = literalServ.readValue(LiteralService.DATA_URL, root);
			if(dataURL != null && dataURL.length() > 0) urls.add(dataURL);
			
			String checkURL = literalServ.readValue(LiteralService.CHECKLIST_URL, root);
			if(checkURL != null && checkURL.length() > 0) urls.add(checkURL);
		}

		if(urls.size() == count) {
			data.setValid(true);
			data = importWFConfigs(data, root, urls);
			
			data = validateActivitiesConfigs(data);
		}else {
			data.setValid(false);
			data.setIdentifier("Empty one of field URL");
		}
		
		return data;
	}
	
	/** получаем все записи активити и проверяем их
	 * проверки все делаем интерфейсніми методами */
	private ImportWorkflowDTO validateActivitiesConfigs(ImportWorkflowDTO data) throws InterruptedException, ObjectNotFoundException {
		WorkflowDTO wfdto = new WorkflowDTO();
		wfdto.setDictNodeId(data.getWfIDselect());
		wfdto = superService.workflowConfiguration(wfdto, null);
		if(wfdto.getPath() != null && wfdto.getPath().size() >= 1) {
			int count = 0;
			
			for(ThingDTO th:wfdto.getPath()) {
				Concept node = closureServ.loadConceptById(th.getNodeId());
				Thing thing = new Thing();
				thing = boilerServ.thingByNode(node,thing);
				if(th.getActivityId() > 0) {
					Concept activity = closureServ.loadConceptById(th.getActivityId());
					th.setActivityName(activity.getLabel());
				}
				th = thingServ.createContent(th, new UserDetailsDTO());
				th.setStrings(dtoServ.readAllStrings(th.getStrings(), node));
				th.setLiterals(dtoServ.readAllLiterals(th.getLiterals(), node));
				th.setDates(dtoServ.readAllDates(th.getDates(), node));
				th.setNumbers(dtoServ.readAllNumbers(th.getNumbers(), node));
				th.setLogical(dtoServ.readAllLogical(th.getLogical(), node));
				
				th = validServ.thing(th, new ArrayList<AssemblyDTO>(), false);
				if(th.isValid()) {
					count++;
				}
			}
			
			if(count == wfdto.getPath().size()) {
				data.setValid(true);
				data = importActivitiesConfigs(data, wfdto);
			}else {
				data.setValid(false);
				data.setIdentifier("Bed activity!");
			}
		}
		
		return data;
	}

	private ImportWorkflowDTO importDictionaries(ImportWorkflowDTO data, List<TableRow> rows) throws ObjectNotFoundException {
		for(TableRow r:rows) {
			String url = r.getRow().get(0).getValue();
			if(!url.toLowerCase().equals(SystemService.DICTIONARY_ADMIN_UNITS.toLowerCase())) {
				Concept root = closureServ.loadConceptByIdentifierActive(url);
				if(root != null && root.getID() > 0) {
					data = buildDict(url, root, data);
					/*DictNodeDTO dictDTO = new DictNodeDTO();
					dictDTO.setUrl(url);
					dictDTO.setParentId(0);
					dictDTO.setNodeId(root.getID());
					dictDTO = dictServ.literalsLoad(dictDTO);
					
					dictDTO.getLiterals().get(LiteralService.PREF_NAME).setValue(literalServ.readPrefLabel(root));
					dictDTO.getLiterals().get(LiteralService.DESCRIPTION).setValue(literalServ.readDescription(root));
					
					Map<Long, List<DictNodeDTO>> dict = new HashMap<Long, List<DictNodeDTO>>();
					List<DictNodeDTO> list = new ArrayList<DictNodeDTO>();
					list.add(dictDTO);
					dict.put(0l, list);
					data.getDictsImport().put(url, dict);
					
					// разбираем словарь по уровням
					Concept levelNode = closureServ.loadConceptById(root.getID());
					List<Concept> child = literalServ.loadOnlyChilds(levelNode);
					if(child != null && child.size() > 0) {
						recursionLoadDictionary(child, url, root.getID(), dict);
					}*/
				}
			}
		}
		data.setValid(true);
		return data;
	}

	private void recursionLoadDictionary(List<Concept> child, String url, long parID, Map<Long, List<DictNodeDTO>> dict) throws ObjectNotFoundException {
		for(Concept c:child) {
			if(c.getActive()) {
				DictNodeDTO dictDTO = new DictNodeDTO();
				dictDTO.setUrl(url);
				dictDTO.setParentId(parID);
				dictDTO.setNodeId(c.getID());
				dictDTO = dictServ.literalsLoad(dictDTO);
				
				dictDTO.getLiterals().get(LiteralService.PREF_NAME).setValue(literalServ.readPrefLabel(c));
				dictDTO.getLiterals().get(LiteralService.DESCRIPTION).setValue(literalServ.readDescription(c));
				
				List<DictNodeDTO> list = dict.get(parID);
				if(list == null) {
					list = new ArrayList<DictNodeDTO>();
				}
				list.add(dictDTO);
				dict.put(parID, list);
			}
		}
	}
	
	private ImportWorkflowDTO buildDict(String url, Concept root, ImportWorkflowDTO data) throws ObjectNotFoundException {
		DictNodeDTO dictDTO = new DictNodeDTO();
		dictDTO.setUrl(url);
		dictDTO.setParentId(0);
		dictDTO.setNodeId(root.getID());
		dictDTO = dictServ.literalsLoad(dictDTO);
		
		dictDTO.getLiterals().get(LiteralService.PREF_NAME).setValue(literalServ.readPrefLabel(root));
		dictDTO.getLiterals().get(LiteralService.DESCRIPTION).setValue(literalServ.readDescription(root));
		
		Map<Long, List<DictNodeDTO>> dict = new HashMap<Long, List<DictNodeDTO>>();
		List<DictNodeDTO> list = new ArrayList<DictNodeDTO>();
		list.add(dictDTO);
		dict.put(0l, list);
		data.getDictsImport().put(url, dict);
		
		// разбираем словарь по уровням
		Concept levelNode = closureServ.loadConceptById(root.getID());
		List<Concept> child = literalServ.loadOnlyChilds(levelNode);
		if(child != null && child.size() > 0) {
			recursionLoadDictionary(child, url, root.getID(), dict);
		}
		
		return data;
	}

	private ImportWorkflowDTO importResources(ImportWorkflowDTO data, List<TableRow> rows) throws ObjectNotFoundException {
		for(TableRow r:rows) {
			String url = r.getRow().get(1).getValue();
			long id = r.getDbID();

			Concept root = closureServ.loadConceptById(id);
			if(root != null) {
				//  для записи ресурса
				ResourceDTO resDTO = new ResourceDTO();
				resDTO.getUrl().setValue(root.getIdentifier());
				resDTO.getConfigUrl().setValue(root.getLabel());
				resDTO.getDescription().setValue(literalServ.readDescription(root));
				data.getResImport().put(url, resDTO);

				// записи конфигурации ресурса
				Concept rootConfig = closureServ.loadConceptByIdentifierActive(root.getLabel());
				DataCollectionDTO config = new DataCollectionDTO();
				config.setNodeId(rootConfig.getID());
				config = superService.dataCollectionDefinitionLoad(config);
				data.getConfigImport().put(url, config);

				// сама конфигурация all assembly_variables
				jdbcRepo.assembly_variables(root.getLabel());
				Headers heads = new Headers();
				heads.getHeaders().add(TableHeader.instanceOf("ID", "ID", false, false, false, TableHeader.COLUMN_STRING, 0));
				heads.getHeaders().add(TableHeader.instanceOf("conceptID", "ID", false, false, false, TableHeader.COLUMN_STRING, 0));
				List<TableRow> vars = jdbcRepo.qtbGroupReport("select * from assembly_variables", "", "", heads);
				if(vars != null && vars.size() > 0) {
					List<DataVariableDTO> list = new ArrayList<DataVariableDTO>();
					for(TableRow v:vars) {// должна быть она запись documents
						DataVariableDTO dvDTO = new DataVariableDTO();
						dvDTO.setNodeId(rootConfig.getID());
						dvDTO.setVarNodeId(Long.parseLong(v.getRow().get(1).getValue()));

						dvDTO = superService.dataCollectionVariableLoad(dvDTO);
						list.add(dvDTO);

						if(dvDTO.getClazz().getValue().equals("documents")) {
							Concept dictRoot = closureServ.loadConceptByIdentifierActive(dvDTO.getDictUrl().getValue());
							if(dictRoot != null && dictRoot.getID() > 0) {
								data = buildDict(dvDTO.getDictUrl().getValue(), dictRoot, data);
								
								
								/*OptionDTO dict = new OptionDTO();
								String prefLbl = literalServ.readValue(LiteralService.PREF_NAME, root, curLang);
								dict.setCode(prefLbl);
								String descr = literalServ.readValue(LiteralService.DESCRIPTION, root, curLang);
								dict.setDescription(descr);
								prefLbl = literalServ.readValue(LiteralService.PREF_NAME, root, lang);
								dict.setOriginalCode(prefLbl);
								descr = literalServ.readValue(LiteralService.DESCRIPTION, root, lang);
								dict.setOriginalDescription(descr);

								Concept levelNode = closureServ.loadConceptById(root.getID());
								List<Concept> child = literalServ.loadOnlyChilds(levelNode);
								if(child != null && child.size() > 0) {
									//recursionLoadDictionary(child, dict, curLang, lang);
								}
								data.getDataImport().put(dvDTO.getDictUrl().getValue(), dict);*/
							}else {
								// если на главном сервере нет словаря с таким урлом - будем создавать пустой на клиенте
								data.getDictsImport().put(dvDTO.getDictUrl().getValue(), null);
								//data.getDataImport().put(dvDTO.getDictUrl().getValue(), null);
							}
						}
					}
					data.getVarsImport().put(url, list);
				}else {
					data.setValid(false);
					data.setIdentifier("ERROR! Empty data configuration!");
				}
			}
		}

		return data;
	}

	private ImportWorkflowDTO importDataConfigs(ImportWorkflowDTO data, List<TableRow> rows) throws ObjectNotFoundException {

		String curLang = LocaleContextHolder.getLocale().toString().toUpperCase();
		String lang = "NE_NP";

		for(TableRow r:rows) {
			String url = r.getRow().get(1).getValue();

			// записи конфигурации
			Concept rootConfig = closureServ.loadConceptByIdentifierActive(url);
			DataCollectionDTO config = new DataCollectionDTO();
			config.setNodeId(rootConfig.getID());
			config = superService.dataCollectionDefinitionLoad(config);
			data.getConfigImport().put(url, config);
			
			// сама конфигурация all assembly_variables
			jdbcRepo.assembly_variables(url);
			Headers heads = new Headers();
			heads.getHeaders().add(TableHeader.instanceOf("ID", "ID", false, false, false, TableHeader.COLUMN_STRING, 0));
			heads.getHeaders().add(TableHeader.instanceOf("conceptID", "ID", false, false, false, TableHeader.COLUMN_STRING, 0));
			List<TableRow> vars = jdbcRepo.qtbGroupReport("select * from assembly_variables", "", "", heads);
			if(vars != null && vars.size() > 0) {
				List<DataVariableDTO> list = new ArrayList<DataVariableDTO>();
				for(TableRow v:vars) {
					DataVariableDTO dvDTO = new DataVariableDTO();
					dvDTO.setNodeId(rootConfig.getID());
					dvDTO.setVarNodeId(Long.parseLong(v.getRow().get(1).getValue()));

					dvDTO = superService.dataCollectionVariableLoad(dvDTO);
					list.add(dvDTO);
				}
				data.getVarsImport().put(url, list);
			}else {
				data.setValid(false);
				data.setIdentifier("ERROR! Empty data configuration!");
			}
		}
		return data;
	}
	
	private ImportWorkflowDTO importWFConfigs(ImportWorkflowDTO data, Concept itDict, List<String> urls) throws ObjectNotFoundException {
		DictNodeDTO dictDTO = new DictNodeDTO();
		dictDTO.setUrl(data.getProcessURL());
		dictDTO.setParentId(0);
		dictDTO.setNodeId(0);
		dictDTO = dictServ.literalsLoad(dictDTO);
		
		String pref = literalServ.readPrefLabel(itDict);
		String descr = literalServ.readDescription(itDict);
		
		dictDTO.getLiterals().get(LiteralService.PREF_NAME).setValue(pref);
		dictDTO.getLiterals().get(LiteralService.DESCRIPTION).setValue(descr);
		if(dictDTO.getLiterals().get(LiteralService.URL) != null)
			dictDTO.getLiterals().get(LiteralService.URL).setValue(urls.get(0));
		if(dictDTO.getLiterals().get(LiteralService.APPLICATION_URL) != null)
			dictDTO.getLiterals().get(LiteralService.APPLICATION_URL).setValue(urls.get(1));
		if(dictDTO.getLiterals().get(LiteralService.DATA_URL) != null)
			dictDTO.getLiterals().get(LiteralService.DATA_URL).setValue(urls.get(2));
		if(dictDTO.getLiterals().get(LiteralService.CHECKLIST_URL) != null)
			dictDTO.getLiterals().get(LiteralService.CHECKLIST_URL).setValue(urls.get(3));
		
		data.setDict(dictDTO);

		return data;
	}
		
	private ImportWorkflowDTO importActivitiesConfigs(ImportWorkflowDTO data, WorkflowDTO wfdto) throws ObjectNotFoundException {
		data.setPathImport(wfdto.getPath());

		
		return data;
	}

	private Headers createHeaders(Headers ret) {
		ret.getHeaders().add(TableHeader.instanceOf(
				"prefLbl", 
				"global_name",
				false,
				false,
				false,
				TableHeader.COLUMN_LINK,
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				"description", 
				"description",
				false,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		ret= boilerServ.translateHeaders(ret);
		return ret;
	}

	private Headers createTemplHeaders(Headers ret, boolean addID) {
		if(addID) {
			ret.getHeaders().add(TableHeader.instanceOf(
					"ID", 
					"ID",
					false,
					false,
					false,
					TableHeader.COLUMN_STRING,
					0));
		}
		ret.getHeaders().add(TableHeader.instanceOf(
				"URL", 
				"URL",
				false,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		ret= boilerServ.translateHeaders(ret);
		return ret;
	}

	private Headers createResourceHeaders(Headers ret) {
		ret.getHeaders().add(TableHeader.instanceOf(
				"ID", 
				"ID",
				false,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				"dataurl", 
				"URL",
				false,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				"clazz", 
				"URL",
				false,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				"config", 
				"URL",
				false,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		ret= boilerServ.translateHeaders(ret);
		return ret;
	}
}
