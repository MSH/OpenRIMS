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
import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.repository.common.JdbcRepository;
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
	private ValidationService validServ;
	@Autowired
	private ThingService thingServ;
	@Autowired
	private DtoService dtoServ;
	@Autowired
	private DictService dictServ;
	@Autowired
	private AssemblyService assemblyServ;
	@Autowired
	private Messages messages;

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
	 * получаем список всех словарей из всех конфигураций процесса
	 * (не берутся словари из ресурсов - єто ветка импорта ресурсов)
	 */
	public ImportWorkflowDTO validateDictionaries(ImportWorkflowDTO data){
		LocaleContextHolder.setDefaultLocale(Messages.parseLocaleString("EN_US"));
		try {
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
		} catch (ObjectNotFoundException e) {
			data.setValid(false);
			data.setIdentifier("Error valid Dictionaries in Main Server!");
			e.printStackTrace();
		}
		return data;
	}

	/** получаем список ресурсов по всем конфигурациям процесса
	 * В полученной таблице если в последней колонке есть урл конфигурации - значит ресурс считаем пригодным для импорта
	 * получаем таблицу 
	 * ID ресурса | url ресурса | url configuration 
	 * если есть url configuration - то считаем что данные ОК
	 *  */
	public ImportWorkflowDTO validateResources(ImportWorkflowDTO data){
		LocaleContextHolder.setDefaultLocale(Messages.parseLocaleString("EN_US"));
		try {// валидируем Resources
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
		} catch (ObjectNotFoundException e) {
			data.setValid(false);
			data.setIdentifier("Error valid Resources in Main Server!");
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * получаем урлы датаконфигураций всех активностей+запись словаря
	 * в результирующей таблице в последней колонке есть признак создана такая конфигурация данных или нет
	 * если да-данные пригодня для импорта
	 * @param data
	 * @return
	 */
	public ImportWorkflowDTO validateDataConfigs(ImportWorkflowDTO data) {
		LocaleContextHolder.setDefaultLocale(Messages.parseLocaleString("EN_US"));
		// отбираем урлы датаКонфигураций всех активностей+запись словаря
		//try {
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
				"left join data_urls as allconf on allconf.url=actvs.dataurl and allconf.Lang=\"" 
				+ LocaleContextHolder.getLocale().toString().toUpperCase() + "\" where actvs.dataurl <> \"\";";

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
		}else {
			data.setIdentifier("Not found data configuration urls!");
		}

		return data;
	}

	/** получаем запись словаря, проверяем ее на заполнение 
	 * далее проверяем записи активностей
	 * */
	public ImportWorkflowDTO validateWF(ImportWorkflowDTO data) {
		LocaleContextHolder.setDefaultLocale(Messages.parseLocaleString("EN_US"));
		try {
			Concept root = systemServ.findProccessByUrl(data.getProcessURL(), data.getWfURL());
			Map<String, String> urls = new HashMap<String, String>();
			// create with empty value - always 4 keys in map
			urls.put(LiteralService.URL, "");
			urls.put(LiteralService.APPLICATION_URL, "");
			urls.put(LiteralService.DATA_URL, "");
			urls.put(LiteralService.CHECKLIST_URL, "");

			int count = 4, c = 0;

			if(data.getProcessURL().equals(SystemService.DICTIONARY_HOST_APPLICATIONS) ||
					data.getProcessURL().equals(SystemService.DICTIONARY_SHUTDOWN_APPLICATIONS)) {
				count = 2;
			}

			if(root != null) {
				String url = literalServ.readValue(LiteralService.URL, root);
				if(url != null && url.length() > 0) {
					urls.put(LiteralService.URL, url);
					c++;
				}

				String applURL = literalServ.readValue(LiteralService.APPLICATION_URL, root);
				if(applURL != null && applURL.length() > 0) {
					urls.put(LiteralService.APPLICATION_URL, applURL);
					c++;
				}

				String dataURL = literalServ.readValue(LiteralService.DATA_URL, root);
				if(dataURL != null && dataURL.length() > 0) {
					urls.put(LiteralService.DATA_URL, dataURL);
					c++;
				}

				String checkURL = literalServ.readValue(LiteralService.CHECKLIST_URL, root);
				if(checkURL != null && checkURL.length() > 0) {
					urls.put(LiteralService.CHECKLIST_URL, checkURL);
					c++;
				}
			}

			if(c >= count) {
				data.setValid(true);
				data = importWFConfigs(data, root, urls);

				if(data.isValid()) {
					data = validateActivitiesConfigs(data);
				}
			}else {
				data.setValid(false);
				data.setIdentifier("Empty one of field URL");
			}
		} catch (ObjectNotFoundException e) {
			data.setValid(false);
			data.setIdentifier("Error valid WorkFlow in Main Server!");
			e.printStackTrace();
		}
		return data;
	}

	/** получаем все записи активити и проверяем их
	 * проверки все делаем интерфейсніми методами */
	private ImportWorkflowDTO validateActivitiesConfigs(ImportWorkflowDTO data) {
		LocaleContextHolder.setDefaultLocale(Messages.parseLocaleString("EN_US"));
		try {
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
					data.setIdentifier("Bad activity! " + wfdto.getTitle());
				}
			}
		} catch (ObjectNotFoundException e) {
			data.setValid(false);
			data.setIdentifier("Error valid ActivitiesConfigs in Main Server!");
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * из полученного списка урлов создаем записи словарей DictNodeDTO для импорта
	 * записываем их в карту dictsImport в ImportWorkflowDTO
	 * private Map<String, Map<Long, List<DictNodeDTO>>> dictsImport = new HashMap<String, Map<Long, List<DictNodeDTO>>>();
	 * String - урл словаря
	 * Map<Long, List<DictNodeDTO>> - это карта ОДНОГО словаря, где Long это парент ид - ид родительской записи
	 */
	private ImportWorkflowDTO importDictionaries(ImportWorkflowDTO data, List<TableRow> rows) throws ObjectNotFoundException {
		for(TableRow r:rows) {
			String url = r.getRow().get(0).getValue();
			if(!url.toLowerCase().equals(SystemService.DICTIONARY_ADMIN_UNITS.toLowerCase())) {
				Concept root = closureServ.loadConceptByIdentifierActive(url);
				if(root != null && root.getID() > 0) {
					data = buildDict(url, root, data);
				}
			}
		}
		data.setValid(true);
		return data;
	}

	public ImportWorkflowDTO buildDict(String url, Concept root, ImportWorkflowDTO data) throws ObjectNotFoundException {
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

		recursionLoadDictionary(url, root.getID(), dict);

		return data;
	}

	private void recursionLoadDictionary(String url, long parID, Map<Long, List<DictNodeDTO>> dict) throws ObjectNotFoundException {
		// разбираем словарь по уровням
		Concept levelNode = closureServ.loadConceptById(parID);
		List<Concept> child = literalServ.loadOnlyChilds(levelNode);
		if(child != null && child.size() > 0) {
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

					recursionLoadDictionary(url, c.getID(), dict);
				}
			}
		}
	}

	/**
	 * из полученного списка урлов создаем записи  для импорта
	 * заполняем resImport (урл ресурса - запись ресурса ResourceDTO)
	 * заполняем configImport (урл ресурса - запись конфигурации ресурса DataCollectionDTO)
	 * заполняем varsImport (урс ресурса - список записей Variable из конфигурации List<DataVariableDTO>)
	 * заполняем dictsImport (урс словаря из конфигурации - карта словаря)
	 */
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
				resDTO = validServ.resourceDefinition(resDTO, false);//superService.resourceDefinitionSave(resDTO);
				if(resDTO.isValid()) {
					// записи конфигурации ресурса
					Concept dataConf = closureServ.loadRoot(SystemService.DATA_COLLECTIONS_ROOT);
					Concept rootConfig = closureServ.findConceptInBranchByIdentifier(dataConf, root.getLabel());
					if(rootConfig == null) {
						data.setValid(false);
						data.setIdentifier("Not found Resources configuration! URL - " + root.getLabel());
						return data;
					}
					DataCollectionDTO config = new DataCollectionDTO();
					config.setNodeId(rootConfig.getID());
					config = superService.dataCollectionDefinitionLoad(config);
					config = validServ.dataCollection(config);
					if(config.isValid()) {
						// получим список Assembly для конфигурации - есть условие одна documents и одна heading
						List<Assembly> datas = assemblyServ.loadDataConfiguration(root.getLabel());
						if(!datas.isEmpty()) {
							List<Assembly> doc = new ArrayList<Assembly>();
							List<Assembly> hea = new ArrayList<Assembly>();
							for(Assembly d:datas) {
								if(d.getClazz().equalsIgnoreCase("documents")){
									doc.add(d);
								}else if(d.getClazz().equalsIgnoreCase("heading")) {
									hea.add(d);
								}
								if(doc.size()==0 || doc.size()>1 || doc.size()+hea.size()!=datas.size()) {
									data.setValid(false);
									data.setIdentifier(messages.get("errorConfigDataResource") + " Resource config URL-" + root.getLabel());
									logger.error(messages.get("errorConfigDataResource"));
									break;
								}
							}//
							//  
							if(data.isValid()) {
								List<DataVariableDTO> list = new ArrayList<DataVariableDTO>();
								for(Assembly assm:datas) {
									DataVariableDTO dvDTO = new DataVariableDTO();
									dvDTO.setNodeId(rootConfig.getID());
									dvDTO.setVarNodeId(assm.getPropertyName().getID());

									dvDTO = dtoServ.assembly(assm, rootConfig, assm.getPropertyName(), dvDTO);
									dvDTO = validServ.variable(dvDTO, false, false);
									if(dvDTO.isValid() || !dvDTO.isStrict()) {
										list.add(dvDTO);
										if(dvDTO.getClazz().getValue().getCode().equals("documents")) {
											Concept dictRoot = closureServ.loadConceptByIdentifierActive(dvDTO.getDictUrl().getValue());
											if(dictRoot != null && dictRoot.getID() > 0) {
												data = buildDict(dvDTO.getDictUrl().getValue(), dictRoot, data);
											}else {
												// если на главном сервере нет словаря с таким урлом - будем создавать пустой на клиенте
												data.getDictsImport().put(dvDTO.getDictUrl().getValue(), null);
											}
										}
									}
								}

								if(list.size() > 0) {
									// все проверено, все ок - добавляем на импорт
									data.getResImport().put(url, resDTO);
									data.getConfigImport().put(url, config);
									data.getVarsImport().put(url, list);
								}
							}else {
								// была ошибка - остановка импорта
								break;
							}
						}else {
							data.setValid(false);
							data.setIdentifier("Empty Resources configuration! URL - " + root.getLabel());
							logger.error(messages.get("errorConfigDataResource"));
							break;
						}
					}else {
						data.setValid(false);
						data.setIdentifier("Bad Resources configuration! URL is "
								+config.getUrl().getValue()
								+"variable: "+config.getVarName()
								+ "details : " + config.getIdentifier());
						break;
					}
				}else {
					data.setValid(false);
					data.setIdentifier("Resource " + url + ". " + resDTO.getIdentifier());
					break;
				}
			}
		}

		return data;
	}

	/**
	 * из полученного списка урлов создаем записи для импорта
	 * заполняем configImport (урл ресурса - запись конфигурации ресурса DataCollectionDTO)
	 * заполняем varsImport (урс ресурса - список записей Variable из конфигурации List<DataVariableDTO>)

	 * Получаем список rows урлов дата конфигураций
	 * перебираем их и 1) проверяем и записіваем на импорт конфигурацию по этому урлу
	 * 					2) далее из этой конфигурации выбираем зинки и персоны (рекурсией вглубь)
	 * 					3) по выбранным урлам проверяем и создаем конфигурации
	 */
	private ImportWorkflowDTO importDataConfigs(ImportWorkflowDTO data, List<TableRow> rows) {
		try {
			for(TableRow r:rows) {
				String url = r.getRow().get(1).getValue();
				long id = r.getDbID();
				System.out.println(url + " id " + id);

				// записи конфигурации
				Concept dataConf = closureServ.loadRoot(SystemService.DATA_COLLECTIONS_ROOT);
				Concept rootConfig = closureServ.findConceptInBranchByIdentifier(dataConf, url);
				if(rootConfig == null) {
					data.setValid(false);
					data.setIdentifier("Not found data configuration!(" + url + ")");
					return data;
				}
				DataCollectionDTO config = new DataCollectionDTO();
				config.setNodeId(rootConfig.getID());
				config = superService.dataCollectionDefinitionLoad(config);
				config = validServ.dataCollection(config);
				if(config.isValid()) {
					// получим список Assembly для конфигурации - есть условие одна documents и одна heading
					List<Assembly> datas = assemblyServ.loadDataConfiguration(url);
					List<DataVariableDTO> list = new ArrayList<DataVariableDTO>();
					for(Assembly assm:datas) {
						DataVariableDTO dvDTO = new DataVariableDTO();
						dvDTO.setNodeId(rootConfig.getID());
						dvDTO.setVarNodeId(assm.getPropertyName().getID());

						dvDTO = dtoServ.assembly(assm, rootConfig, assm.getPropertyName(), dvDTO);
						dvDTO = validServ.variable(dvDTO, false, false);
						if(dvDTO.isValid() || !dvDTO.isStrict()) {
							list.add(dvDTO);
						}else {
							data.setValid(false);
							data.setIdentifier("Bad configuration! URL is " +dvDTO.getUrl().getValue()
									+" description: "+ config.getIdentifier() 
									+ " variable is " + assm.getPropertyName()
									+ " details " +assm.getPropertyName().getIdentifier() +"/"+dvDTO.getIdentifier());
							break;
						}
					}
					if(list.size() > 0 && list.size() == datas.size()) {
						// все проверено, все ок - добавляем на импорт
						data.getConfigImport().put(url, config);
						data.getVarsImport().put(url, list);

						// берем следующие страницы в конфигурации (получаем зинки и персоны, из низ урлы и т.д.)
						TableQtb table = new TableQtb();
						table.setHeaders(createTemplHeaders(table.getHeaders(), false));
						jdbcRepo.importWF_main(id, 3);

						List<TableRow> items = jdbcRepo.qtbGroupReport("select d.dataurl as URL from importwf_main as d group by d.dataurl", "", "", table.getHeaders());
						if(items != null && items.size() > 0) {
							for(TableRow tr:items) {
								String urltr = tr.getRow().get(0).getValue();

								// записи конфигурации
								rootConfig = closureServ.findConceptInBranchByIdentifier(dataConf, urltr);
								if(rootConfig == null) {
									data.setValid(false);
									data.setIdentifier("Not found data configuration!(" + url + ")");
									return data;
								}
								System.out.println(urltr + " id " + rootConfig.getID());

								config = new DataCollectionDTO();
								config.setNodeId(rootConfig.getID());
								config = superService.dataCollectionDefinitionLoad(config);
								config = validServ.dataCollection(config);
								if(config.isValid()) {
									// получим список Assembly для конфигурации - есть условие одна documents и одна heading
									datas = assemblyServ.loadDataConfiguration(urltr);
									list = new ArrayList<DataVariableDTO>();
									for(Assembly assm:datas) {
										DataVariableDTO dvDTO = new DataVariableDTO();
										dvDTO.setNodeId(rootConfig.getID());
										dvDTO.setVarNodeId(assm.getPropertyName().getID());

										dvDTO = dtoServ.assembly(assm, rootConfig, assm.getPropertyName(), dvDTO);
										dvDTO = validServ.variable(dvDTO, false, false);
										if(dvDTO.isValid() || !dvDTO.isStrict()) {
											list.add(dvDTO);
										}else {
											data.setValid(false);
											data.setIdentifier("Bad configuration! URL is " +dvDTO.getUrl().getValue()
													+" description: "+ config.getIdentifier() 
													+ " variable is " + assm.getPropertyName()
													+ " details " +assm.getPropertyName().getIdentifier() +"/"+dvDTO.getIdentifier());
											break;
										}
									}
									if(list.size() > 0 && list.size() == datas.size()) {
										// все проверено, все ок - добавляем на импорт
										data.getConfigImport().put(urltr, config);
										data.getVarsImport().put(urltr, list);
									}
								}
							}
						}
					}
				}else {
					data.setValid(false);
					data.setIdentifier("Bad configuration! URL is " 
							+ config.getUrl().getValue()
							+ "details : "+ config.getIdentifier());
					break;
				}
			}
		} catch (ObjectNotFoundException e) {
			data.setValid(false);
			data.setIdentifier("Error valid DataConfigurations in Main Server!");
			e.printStackTrace();
		}
		return data;
	}


	/**
	 * из полученного концепта и списка урлов создаем запись словаря
	 * заполняем dict - DictNodeDTO
	 */
	private ImportWorkflowDTO importWFConfigs(ImportWorkflowDTO data, Concept itDict, Map<String, String> urls) throws ObjectNotFoundException {
		DictNodeDTO dictDTO = new DictNodeDTO();
		dictDTO.setUrl(data.getProcessURL());
		dictDTO.setParentId(0);
		dictDTO.setNodeId(0);
		dictDTO = dictServ.literalsLoad(dictDTO);

		String pref = literalServ.readPrefLabel(itDict);
		String descr = literalServ.readDescription(itDict);

		dictDTO.getLiterals().get(LiteralService.PREF_NAME).setValue(pref);
		dictDTO.getLiterals().get(LiteralService.DESCRIPTION).setValue(descr);

		if(dictDTO.getLiterals().get(LiteralService.URL) != null) {
			dictDTO.getLiterals().get(LiteralService.URL).setValue(urls.get(LiteralService.URL));
		}
		if(dictDTO.getLiterals().get(LiteralService.APPLICATION_URL) != null) {
			dictDTO.getLiterals().get(LiteralService.APPLICATION_URL).setValue(urls.get(LiteralService.APPLICATION_URL));
		}
		if(dictDTO.getLiterals().get(LiteralService.DATA_URL) != null) {
			dictDTO.getLiterals().get(LiteralService.DATA_URL).setValue(urls.get(LiteralService.DATA_URL));
		}
		if(dictDTO.getLiterals().get(LiteralService.CHECKLIST_URL) != null) {
			dictDTO.getLiterals().get(LiteralService.CHECKLIST_URL).setValue(urls.get(LiteralService.CHECKLIST_URL));
		}

		dictDTO = validServ.node(dictDTO, "", false);
		if(dictDTO.isValid()) {
			data.setDict(dictDTO);
		}else {
			data.setValid(false);
			data.setIdentifier(dictDTO.getIdentifier());
		}

		return data;
	}

	/**
	 *  заполняем pathImport - список ThingDTO для импорта
	 */
	private ImportWorkflowDTO importActivitiesConfigs(ImportWorkflowDTO data, WorkflowDTO wfdto) throws ObjectNotFoundException {
		// запишем для импорта 2 словаря из активити(достаточно из первого, они ж одинаковые) - список ролей и списоз финализаций
		List<ThingDTO> path = wfdto.getPath();
		if(path != null && path.size() >= 1) {
			for(String key:path.get(0).getDictionaries().keySet()) {
				String url = path.get(0).getDictionaries().get(key).getUrl();
				Concept dictRoot = closureServ.loadConceptByIdentifierActive(url);
				if(dictRoot != null && dictRoot.getID() > 0) {
					data = buildDict(url, dictRoot, data);
				}
			}
		}
		for(ThingDTO dto:path) {
			Long key = dto.getNodeId();
			List<String> vals = new ArrayList<String>();
			DictionaryDTO exec = dto.getDictionaries().get(AssemblyService.ACTIVITY_EXECUTIVES);
			if(exec != null) {
				if(exec.getPrevSelected() != null) {
					Long select = exec.getPrevSelected().get(0);
					Concept c = closureServ.loadConceptById(select);
					vals.add(c.getIdentifier());
				}else vals.add(null);
			}else vals.add(null);

			DictionaryDTO finaliz = dto.getDictionaries().get(AssemblyService.ACTIVITY_CONFIG_FINALIZE);
			if(finaliz != null) {
				if(finaliz.getPrevSelected() != null) {
					Long select = finaliz.getPrevSelected().get(0);
					Concept c = closureServ.loadConceptById(select);
					vals.add(c.getIdentifier());
				}else vals.add(null);
			}else vals.add(null);
			data.getPathImportDict().put(key, vals);
		}

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
