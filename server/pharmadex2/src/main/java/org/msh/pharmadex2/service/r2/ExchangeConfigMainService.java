package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.msh.pdex2.dto.i18n.Language;
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
import org.msh.pdex2.model.r2.ThingDict;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.r2.ThingRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.DataVariableDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.ExchangeConfigDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * service for load data process configurations USED on main server
 * @author khome
 *
 */
@Service
public class ExchangeConfigMainService {
	private static final Logger logger = LoggerFactory.getLogger(ExchangeConfigMainService.class);
	@Autowired
	private SystemService systemServ;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private Messages messages;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private AssemblyService assemblyServ;
	@Autowired
	private SupervisorService superService;
	@Autowired
	ThingRepo thingRepo;


	/**
	 * on main server load processes dictionary
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ExchangeConfigDTO loadProcesses(ExchangeConfigDTO data) throws ObjectNotFoundException {
		DictionaryDTO leftDict = systemServ.stagesDictionary();
		data.setExistTable(leftDict.getTable());
		data.getExistTable().setSelectable(true);
		if(data.getProcessId() > 0 && data.getExistTable().getRows().size() > 0) {
			for(TableRow r:data.getExistTable().getRows()) {
				if(r.getDbID() == data.getProcessId()) {
					r.setSelected(true);
				}
			}

			Concept c = closureServ.loadConceptById(data.getProcessId());
			if(c != null) {
				DictionaryDTO dto = systemServ.workflowDictionary(c.getIdentifier());
				data.setNotExistTable(dto.getTable());
				data.getNotExistTable().setSelectable(true);

				if(data.getItProcessID() > 0 && data.getNotExistTable().getRows().size() > 0) {
					for(TableRow r:data.getNotExistTable().getRows()) {
						if(r.getDbID() == data.getItProcessID()) {
							r.setSelected(true);
						}
					}
				}
			}
		}

		return data;
	}
	
	/**
	 * on main server load processes dictionary
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ExchangeConfigDTO getUrlProcesses(ExchangeConfigDTO data) throws ObjectNotFoundException {
		if(data.getProcessId() > 0 && data.getItProcessID() > 0) {
			Concept item = closureServ.loadConceptById(data.getItProcessID());
			String url = literalServ.readValue(LiteralService.URL, item);
			data.setUrlSelect(url);
			
			item = closureServ.loadConceptById(data.getProcessId());
			data.setUrlProcess(item.getIdentifier());
		}
		
		return data;
	}

	/**
	 * on main server load dictionaries by checklist
	 * For all activities get field value "Checklist Dictionary URL"
	 * and add field value "Checklist Dictionary URL" from item dictionary
	 */
	public ExchangeConfigDTO loadChecklistDictionaries(ExchangeConfigDTO data) throws ObjectNotFoundException {
		if(data.getItProcessID() > 0) {
			TableQtb table = new TableQtb();
			table.setHeaders(createHeaders(table.getHeaders(), false));

			Set<Concept> list = new HashSet<Concept>();
			Concept dictNode = closureServ.loadConceptById(data.getItProcessID());
			String url = literalServ.readValue(LiteralService.APPLICATION_URL, dictNode);
			if (url.length() > 5) {
				String checklisturl = literalServ.readValue(LiteralService.CHECKLIST_URL, dictNode);
				if(checklisturl != null && checklisturl.length() > 0) {
					Concept c = closureServ.loadRoot(checklisturl);
					list.add(c);
				}

				Concept firstActivity = closureServ.loadRoot("configuration." + url.toLowerCase());
				List<Concept> activities = boilerServ.loadActivities(firstActivity);
				if(activities != null && activities.size() > 0) {
					for(Concept activity:activities) {
						String checkListDictUrl = literalServ.readValue(LiteralService.CHECKLIST_URL, activity);
						if(checkListDictUrl != null && checkListDictUrl.length() > 0) {
							Concept c = closureServ.loadRoot(checkListDictUrl);
							list.add(c);
						}
					}
				}

				if(list.size() > 0) {
					for(Concept c:list) {
						TableRow row = createRow(c, false, true);
						table.getRows().add(row);
					}
				}
				data.setNotExistTable(table);
			} else {
				throw new ObjectNotFoundException(
						"workflowConfiguration. Configuration url is wrong defined. It is " + url, logger);
			}
		}

		return data;
	}

	/**
	 * on main server load all dictionaries from configuration every activity and configuration start application
	 */
	public ExchangeConfigDTO loadDictionaries(ExchangeConfigDTO data) throws ObjectNotFoundException {
		if(data.getItProcessID() > 0) {
			TableQtb table = new TableQtb();
			table.setHeaders(createHeaders(table.getHeaders(), false));

			// dictionaries from activity configurations
			Concept dictNode = closureServ.loadConceptById(data.getItProcessID());
			String url = literalServ.readValue(LiteralService.APPLICATION_URL, dictNode);
			if (url.length() > 5) {
				Set<Concept> list = new HashSet<Concept>();

				String dataurl = literalServ.readValue(LiteralService.DATA_URL, dictNode);
				if(dataurl != null && dataurl.length() > 0) {
					Concept c = closureServ.loadConceptByIdentifier(dataurl);
					loadAssByDictionaries(c, list);
				}
				//TODO add person, addresses, resources
				Concept firstActivity = closureServ.loadRoot("configuration." + url.toLowerCase());
				List<Concept> activities = boilerServ.loadActivities(firstActivity);
				if(activities != null && activities.size() > 0) {
					for(Concept activity:activities) {
						dataurl = literalServ.readValue(LiteralService.DATA_URL, activity);
						if(dataurl != null && dataurl.length() > 0) {
							Concept c = closureServ.loadConceptByIdentifier(dataurl);
							loadAssByDictionaries(c, list);
						}
					}
				}

				if(list.size() > 0) {
					for(Concept c:list) {
						TableRow row = createRow(c, false, true);
						table.getRows().add(row);
					}
				}
				data.setNotExistTable(table);
			} else {
				throw new ObjectNotFoundException(
						"workflowConfiguration. Configuration url is wrong defined. It is " + url, logger);
			}

		}
		return data;
	}

	private void loadAssByDictionaries(Concept root, Set<Concept> list) throws ObjectNotFoundException{
		if(root != null) {
			List<Assembly> assemblies = assemblyServ.loadDataConfiguration(root.getIdentifier());
			if(assemblies != null && assemblies.size() > 0) {
				for(Assembly ass:assemblies) {
					if(ass.getClazz().equals("persons")) {
						String u = ass.getAuxDataUrl();// this is the URL already here
						Concept c = closureServ.loadConceptByIdentifier(u);
						loadAssByDictionaries(c, list);
					}else if(ass.getClazz().equals("resources")){
						String u = ass.getUrl();
						Concept c = closureServ.loadConceptByIdentifier(u);
						if(c != null && c.getLabel() != null && c.getLabel().length() > 0) {
							c = closureServ.loadConceptByIdentifierActive(c.getLabel());
							loadAssByDictionaries(c, list);
						}
					}else if(ass.getClazz().equals("things")) {
						String u = ass.getUrl();
						Concept c = closureServ.loadConceptByIdentifier(u);
						loadAssByDictionaries(c, list);
						
						u = ass.getAuxDataUrl();
						c = closureServ.loadConceptByIdentifierActive(u);
						loadAssByDictionaries(c, list);
					}else if(ass.getClazz().equals("dictionaries") || ass.getClazz().equals("documents")
							 || ass.getClazz().equals("links")) {
						String u = ass.getDictUrl();
						Concept c = closureServ.loadConceptByIdentifier(u);
						if(c != null) list.add(c);
					}else if(ass.getClazz().equals("droplist")) {
						String u = ass.getUrl();
						Concept c = closureServ.loadConceptByIdentifier(u);
						if(c != null) list.add(c);
					}
				}
			}
		}
	}

	private void loadAssOnlyResources(String url, Set<String> urls) throws ObjectNotFoundException{
		List<Assembly> assemblies = assemblyServ.loadDataConfiguration(url, "resources");
		if(assemblies != null && assemblies.size() > 0) {
			for(Assembly ass:assemblies) {
				url = ass.getUrl();
				urls.add(url);
				/*Concept resConcept = closureServ.loadConceptByIdentifier(url);
				if(resConcept != null) {
					if(resConcept.getLabel() != null && resConcept.getLabel().length() > 0) {
						Concept config = closureServ.loadConceptByIdentifier(resConcept.getLabel());
						if(config != null) {
							map.put(resConcept, config);
						}
					}
				}*/
			}
		}
		assemblies = assemblyServ.loadDataConfiguration(url, "things");
		if(assemblies != null && assemblies.size() > 0) {
			for(Assembly ass:assemblies) {
				String urlAss = ass.getUrl();
				if(urlAss != null && urlAss.length() > 0) {
					//Concept resConcept = null;
					// resources
					List<Assembly> assRes = assemblyServ.loadDataConfiguration(urlAss, "resources");
					if(assRes != null && assRes.size() > 0) {
						for(Assembly it:assRes) {
							String u = it.getUrl();
							urls.add(u);
							/*
							resConcept = closureServ.loadConceptByIdentifier(url);
							if(resConcept != null) {
								if(resConcept.getLabel() != null && resConcept.getLabel().length() > 0) {
									Concept config = closureServ.loadConceptByIdentifier(resConcept.getLabel());
									if(config != null) {
										map.put(resConcept, config);
									}
								}
							}*/
						}
					}
					loadAssOnlyResources(urlAss, urls);
				}
			}
		}
	}

	/**
	 * by tab DataConfigs
	 * @param url
	 * @param nodeIds
	 * @throws ObjectNotFoundException
	 */
	private void loadAssFull(Concept root, Set<Concept> configs, Set<String> warnins) throws ObjectNotFoundException{
		if(root == null)
			return;
		List<Assembly> assemblies = assemblyServ.loadDataConfiguration(root.getIdentifier());
		if(assemblies != null && assemblies.size() > 0) {
			for(Assembly ass:assemblies) {
				if(ass.getClazz().equals("persons")) {
					String u = ass.getAuxDataUrl();// this is the URL already here
					Concept c = closureServ.loadConceptByIdentifier(u);
					if(c != null) {
						configs.add(c);
						loadAssFull(c, configs, warnins);
					}
				}else if(ass.getClazz().equals("things")) {
					String u = ass.getUrl();
					Concept c = closureServ.loadConceptByIdentifier(u);
					if(c != null) {
						configs.add(c);
						loadAssFull(c, configs, warnins);
					}
					
					u = ass.getAuxDataUrl();
					c = closureServ.loadConceptByIdentifier(u);
					if(c != null) {
						configs.add(c);
						loadAssFull(c, configs, warnins);
					}
				}else if(ass.getClazz().equals("atc")) {
					warnins.add(messages.get("resImp_dictAtc") + SystemService.PRODUCTCLASSIFICATION_ATC_HUMAN);
				}else if(ass.getClazz().equals("legacy")) {
					String u = ass.getDictUrl();
					warnins.add(messages.get("resImp_dictLegacy") + u);
				}else if(ass.getClazz().equals("schedulers")) {
					String u = ass.getAuxDataUrl();
					warnins.add(messages.get("resImp_scheduler") + u);
				}else if(ass.getClazz().equals("addresses")) {
					warnins.add(messages.get("resImp_address") + assemblyServ.adminUnitsDict());
				}
			}
		}
	}

	/**
	 * For all activities get field value "Data Configuration URL", 
	 * from this URL get configuration get all dictionaries and documents
	 * 
	 */
	public ExchangeConfigDTO loadResources(ExchangeConfigDTO data) throws ObjectNotFoundException{
		if(data.getItProcessID() > 0) {
			TableQtb table = new TableQtb();
			table.setHeaders(createHeadersResources(table.getHeaders(), false));

			// take the resources entry and the configuration for it
			Concept dictNode = closureServ.loadConceptById(data.getItProcessID());
			String url = literalServ.readValue(LiteralService.APPLICATION_URL, dictNode);
			if (url.length() > 5) {
				//Map<Concept, Concept> mapRes = new HashMap<Concept, Concept>();
				Map<String, String> mapRes = new HashMap<String, String>();
				Set<String> urlsRes = new HashSet<String>();

				String dataurl = literalServ.readValue(LiteralService.DATA_URL, dictNode);
				if(dataurl != null && dataurl.length() > 0) {
					loadAssOnlyResources(dataurl, urlsRes);
				}

				Concept firstActivity = closureServ.loadRoot("configuration." + url.toLowerCase());
				List<Concept> activities = boilerServ.loadActivities(firstActivity);
				if(activities != null && activities.size() > 0) {
					for(Concept activity:activities) {
						dataurl = literalServ.readValue(LiteralService.DATA_URL, activity);
						if(dataurl != null && dataurl.length() > 0) {
							loadAssOnlyResources(dataurl, urlsRes);
						}
					}
				}

				if(urlsRes.size() > 0) {
					// we need to get children concepts from root = "configuration.resources" resmainserver
					String mainWhere = "lang='" + LocaleContextHolder.getLocale().toString().toUpperCase() + "'";
					String str = "";
					for(String u:urlsRes) {
						str += "'" + u + "',";
					}
					mainWhere += " and url in (" + str.substring(0, str.length() - 1) + ")";
					List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from resmainserver", "", mainWhere, table.getHeaders());
					table.getRows().addAll(rows);
				}
				/*if(mapRes.keySet() != null && mapRes.keySet().size() > 0) {
					for(Concept resCon:mapRes.keySet()) {
						TableRow row = createRow(resCon, true, true);
						
						Concept config = mapRes.get(resCon);
						TableCell cell = new TableCell();
						cell.setKey("dataurl");
						cell.setValue(config.getIdentifier());
						row.getRow().add(cell);
						
						table.getRows().add(row);
					}
				}*/
				data.setNotExistTable(table);
			} else {
				throw new ObjectNotFoundException(
						"ExchangeConfigMainService.loadResources(). Configuration url is wrong defined. It is " + url, logger);
			}
		}
		return data;
	}

	public ExchangeConfigDTO loadDataConfigs(ExchangeConfigDTO data) throws ObjectNotFoundException{
		if(data.getItProcessID() > 0) {
			TableQtb table = new TableQtb();
			table.setHeaders(createHeadersShort(table.getHeaders(), false));
			
			// take the resources entry and the configuration for it
			Concept dictNode = closureServ.loadConceptById(data.getItProcessID());
			String url = literalServ.readValue(LiteralService.APPLICATION_URL, dictNode);
			if (url.length() > 5) {
				Set<Concept> list = new HashSet<Concept>();
				String dataurl = literalServ.readValue(LiteralService.DATA_URL, dictNode);
				if(dataurl != null && dataurl.length() > 0) {
					Concept c = closureServ.loadConceptByIdentifier(dataurl);
					if(c != null) {
						list.add(c);
						loadAssFull(c, list, data.getWarnings());
					}
					
					Concept firstActivity = closureServ.loadRoot("configuration." + url.toLowerCase());
					List<Concept> activities = boilerServ.loadActivities(firstActivity);
					if(activities != null && activities.size() > 0) {
						for(Concept activity:activities) {
							dataurl = literalServ.readValue(LiteralService.DATA_URL, activity);
							if(dataurl != null && dataurl.length() > 0) {
								c = closureServ.loadConceptByIdentifier(dataurl);
								if(c != null) {
									list.add(c);
									loadAssFull(c, list, data.getWarnings());
								}
							}
						}
					}
					
					if(list.size() > 0) {
						for(Concept cn:list) {
							TableRow row = createRow(cn, true, true);
							table.getRows().add(row);
						}
					}
				}
				data.setNotExistTable(table);
			}else {
				throw new ObjectNotFoundException(
						"ExchangeConfigMainService.loadDataConfigs(). Configuration url is wrong defined. It is " + url, logger);
			}
		}
		return data;
	}
	
	public ExchangeConfigDTO loadWorkflows(ExchangeConfigDTO data) throws ObjectNotFoundException{
		if(data.getItProcessID() > 0) {
			TableQtb table = new TableQtb();
			table.setHeaders(createHeadersWF(table.getHeaders()));
			
			Concept dictNode = closureServ.loadConceptById(data.getItProcessID());
			String url = literalServ.readValue(LiteralService.APPLICATION_URL, dictNode);
			if (url.length() > 5) {
				Concept root = closureServ.getParent(dictNode);
				data.setRootProcess(root.getIdentifier());
				data.setUrlProcess(literalServ.readValue(LiteralService.URL, dictNode));
				
				TableRow row = createRow(dictNode, false, false);
				table.getRows().add(row);
				//Concept root = closureServ.getParent(dictNode);
				Concept firstActivity = closureServ.loadRoot("configuration." + url.toLowerCase());
				List<Concept> activities = boilerServ.loadActivities(firstActivity);
				if(activities != null && activities.size() > 0) {
					for(Concept activity:activities) {
						row = createRow(activity, false, false);
						table.getRows().add(row);
					}
				}
				data.setNotExistTable(table);
			}else {
				throw new ObjectNotFoundException(
						"ExchangeConfigMainService.loadWorkflows(). Configuration url is wrong defined. It is " + url, logger);
			}
		}
		return data;
	}
	/*
	//-- get dictionaries from configurations for resources
// we take resources from activity configurations: resource-configuration for it-dictionary
					String select = "select ID, Url from assm_var ";
					String where = "nodeID in (" + ids + ") and Clazz like 'resources' and lang like '" + LocaleContextHolder.getLocale().toString().toUpperCase() + "'";
					TableQtb tableres = new TableQtb();
					tableres.setHeaders(createHeadersURL(tableres.getHeaders()));
					List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, tableres.getHeaders());
					// now only one
					String urlRes = rows.get(0).getCellByKey("Url").getValue();
					Concept cRes = closureServ.loadConceptByIdentifier(urlRes);
					String urlResConfig = cRes.getLabel();
					Concept configRes = closureServ.loadRoot(urlResConfig);
	 */

	private TableRow createRow(Concept root, boolean isShort, boolean addURL) throws ObjectNotFoundException {
		TableRow row = new TableRow();
		row.setDbID(root.getID());

		TableCell cell = null;
		if(addURL) {
			cell = new TableCell();
			cell.setKey("url");
			cell.setValue(root.getIdentifier());
			row.getRow().add(cell);
		}

		if(isShort) {
			cell = new TableCell();
			cell.setKey("description");
			cell.setValue(literalServ.readPrefLabel(root));
			row.getRow().add(cell);
		}else {
			cell = new TableCell();
			cell.setKey("prefLbl");
			cell.setValue(literalServ.readPrefLabel(root));
			row.getRow().add(cell);
		
			cell = new TableCell();
			cell.setKey("description");
			cell.setValue(literalServ.readDescription(root));
			row.getRow().add(cell);
		}

		return row;
	}

	@Transactional
	public ExchangeConfigDTO importDictionary(ExchangeConfigDTO  data) throws ObjectNotFoundException {
		//if(data.getDictByCopy().getId() > 0) {
		if(data.getNodeIdSelect() > 0) {
			data = varifLangs(data);
			String curLang = data.getCurLang().toUpperCase(); // current lang on clientServer
			String lang = data.getOtherLang().toUpperCase();// other lang on clientServer

			Concept root = closureServ.loadConceptById(data.getNodeIdSelect());
			data.setUrlByCopy(root.getIdentifier());

			//OptionDTO rootDTO = new OptionDTO();
			data.getDictByCopy().setId(root.getID());
			data.getDictByCopy().setActive(true);
			String prefLbl = literalServ.readValue(LiteralService.PREF_NAME, root, curLang);
			data.getDictByCopy().setOriginalCode(prefLbl);
			String descr = literalServ.readValue(LiteralService.DESCRIPTION, root, curLang);
			data.getDictByCopy().setOriginalDescription(descr);

			prefLbl = literalServ.readValue(LiteralService.PREF_NAME, root, lang);
			data.getDictByCopy().setCode(prefLbl);
			descr = literalServ.readValue(LiteralService.DESCRIPTION, root, lang);
			data.getDictByCopy().setDescription(descr);

			Concept levelNode = closureServ.loadConceptById(root.getID());
			List<Concept> child = literalServ.loadOnlyChilds(levelNode);
			if(child != null && child.size() > 0) {
				recursionLoadDictionary(child, data.getDictByCopy(), curLang, lang);
			}

			//data.setDictByCopy(rootDTO);
		}
		return data;
	}

	private void recursionLoadDictionary(List<Concept> child, OptionDTO parentDTO, String curLang, String lang) throws ObjectNotFoundException {
		for(Concept c:child) {
			if(c.getActive()) {
				OptionDTO dto = new OptionDTO();
				dto.setId(c.getID());
				dto.setActive(true);

				String prefLbl = literalServ.readValue(LiteralService.PREF_NAME, c, curLang);
				dto.setOriginalCode(prefLbl);
				String descr = literalServ.readValue(LiteralService.DESCRIPTION, c, curLang);
				dto.setOriginalDescription(descr);

				prefLbl = literalServ.readValue(LiteralService.PREF_NAME, c, lang);
				dto.setCode(prefLbl);
				descr = literalServ.readValue(LiteralService.DESCRIPTION, c, lang);
				dto.setDescription(descr);

				parentDTO.getOptions().add(dto);

				List<Concept> childNext = literalServ.loadOnlyChilds(c);
				if(childNext != null && childNext.size() > 0) {
					recursionLoadDictionary(childNext, dto, curLang, lang);
				}
			}
		}
	}

	public ExchangeConfigDTO importResource(ExchangeConfigDTO  data) throws ObjectNotFoundException{
		data = varifLangs(data);
		String curLang = data.getCurLang().toUpperCase(); // current lang on clientServer
		String lang = data.getOtherLang().toUpperCase();// other lang on clientServer

		/*
		 * 1) data of resource item  - data.getDictByCopy()
		 * 2) data of config item - data.getDictByCopy().getOptions()
		 * 3) full items from data of config - data.getVariables()
		 */
		
		data.setDictByCopy(new OptionDTO());
		data.getVariables().clear();
		
		Concept rootRes = closureServ.loadConceptById(data.getNodeIdSelect());
		if(!rootRes.getActive()) {
			data.setIdentifier(messages.get("error_dataintegrity"));
			data.setValid(false);
			return data;
		}
		data.getDictByCopy().setCode(rootRes.getIdentifier());
		data.getDictByCopy().setOriginalCode(rootRes.getLabel());
		String descr = literalServ.readValue(LiteralService.DESCRIPTION, rootRes, curLang);
		data.getDictByCopy().setOriginalDescription(descr);
		descr = literalServ.readValue(LiteralService.DESCRIPTION, rootRes, lang);
		data.getDictByCopy().setDescription(descr);
		
		// configuration by Resource
		Concept root = closureServ.loadConceptByIdentifier(rootRes.getLabel());
		if(!root.getActive()) {
			data.setIdentifier(messages.get("error_dataintegrity"));
			data.setValid(false);
			return data;
		}
		OptionDTO configDTO = new OptionDTO();
		configDTO.setOriginalCode(root.getIdentifier());
		configDTO.setCode(root.getIdentifier());

		String prefLbl = literalServ.readValue(LiteralService.PREF_NAME, root, curLang);
		configDTO.setOriginalDescription(prefLbl);
		prefLbl = literalServ.readValue(LiteralService.PREF_NAME, root, lang);
		configDTO.setDescription(prefLbl);
		data.getDictByCopy().getOptions().add(configDTO);

		// load all variables(only 1 item by resources) ids
		TableQtb table = new TableQtb();
		table.setHeaders(createHeadersVariables(table.getHeaders()));
		String where = "p.Active=true and p.nodeID='" + root.getID() + "' and p.lang='"
				+ LocaleContextHolder.getLocale().toString().toUpperCase() + "'";
		String select="select * from(select distinct av.*, c.Label as 'ext' from assm_var av "
				+ "join assembly a on a.ID=av.assemblyID join concept c on c.ID=a.conceptID) p";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, table.getHeaders());

		List<Long> idVars = new ArrayList<Long>();
		for(TableRow r:rows) {
			idVars.add(r.getDbID());
		}

		for(Long id:idVars) {
			DataVariableDTO dto = new DataVariableDTO();
			dto.setNodeId(root.getID());
			dto.setVarNodeId(id);

			dto = superService.dataCollectionVariableLoad(dto);
			data.getVariables().add(dto);
		}

		return data;
	}

	@Transactional
	public ExchangeConfigDTO importDataConfig(ExchangeConfigDTO  data) throws ObjectNotFoundException{
		if(data.getNodeIdSelect() > 0) {
			data = varifLangs(data);
			String curLang = data.getCurLang().toUpperCase(); // current lang on clientServer
			String lang = data.getOtherLang().toUpperCase();// other lang on clientServer

			data.getVariables().clear();
			Concept root = closureServ.loadConceptById(data.getNodeIdSelect());
			if(!root.getActive()) {
				data.setIdentifier(messages.get("error_dataintegrity"));
				data.setValid(false);
				return data;
			}
			data.getDictByCopy().setOriginalCode(root.getIdentifier());
			data.getDictByCopy().setCode(root.getIdentifier());

			String prefLbl = literalServ.readValue(LiteralService.PREF_NAME, root, curLang);
			data.getDictByCopy().setOriginalDescription(prefLbl);
			prefLbl = literalServ.readValue(LiteralService.PREF_NAME, root, lang);
			data.getDictByCopy().setDescription(prefLbl);

			// load all variables(only 1 item by resources) ids
			TableQtb table = new TableQtb();
			table.setHeaders(createHeadersVariables(table.getHeaders()));
			String where = "p.Active=true and p.nodeID='" + data.getNodeIdSelect() + "' and p.lang='"
					+ LocaleContextHolder.getLocale().toString().toUpperCase() + "'";
			String select="select * from(select distinct av.*, c.Label as 'ext' from assm_var av "
					+ "join assembly a on a.ID=av.assemblyID join concept c on c.ID=a.conceptID) p";
			List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, table.getHeaders());

			List<Long> idVars = new ArrayList<Long>();
			for(TableRow r:rows) {
				idVars.add(r.getDbID());
			}

			for(Long id:idVars) {
				DataVariableDTO dto = new DataVariableDTO();
				dto.setNodeId(data.getNodeIdSelect());
				dto.setVarNodeId(id);

				dto = superService.dataCollectionVariableLoad(dto);
				//TODO ONLY description field may be on 2 langs
				
				data.getVariables().add(dto);
			}
		}

		return data;
	}
	
	@Transactional
	public ExchangeConfigDTO importAllWorkflows(ExchangeConfigDTO  data) throws ObjectNotFoundException{
		if(data.getItProcessID() > 0) {// copy all activities
			data = varifLangs(data);
			String curLang = data.getCurLang().toUpperCase(); // current lang on clientServer
			String lang = data.getOtherLang().toUpperCase();// other lang on clientServer

			data.getDictByCopy().getOptions().clear();
			
			Concept dictNode = closureServ.loadConceptById(data.getItProcessID());
			String url = literalServ.readValue(LiteralService.APPLICATION_URL, dictNode);
			if (url.length() > 5) {
				//1 dictionary process
				Concept root = closureServ.getParent(dictNode);
				data.setRootProcess(root.getIdentifier());
				String prefLbl = literalServ.readValue(LiteralService.PREF_NAME, root, curLang);
				data.getDictByCopy().setOriginalCode(prefLbl);
				String descr = literalServ.readValue(LiteralService.DESCRIPTION, root, curLang);
				data.getDictByCopy().setOriginalDescription(descr);
				prefLbl = literalServ.readValue(LiteralService.PREF_NAME, root, lang);
				data.getDictByCopy().setCode(prefLbl);
				descr = literalServ.readValue(LiteralService.DESCRIPTION, root, lang);
				data.getDictByCopy().setDescription(descr);
				
				//2 select item dictionary ALWAYS first in options list
				OptionDTO item = new OptionDTO();
				
				Map<String, String> litsCur = literalServ.literals(dictNode, curLang);
				Map<String, String> lits = literalServ.literals(dictNode, lang);
				
				OptionDTO dto = null;
				for(String lit:litsCur.keySet()) {
					String val = litsCur.get(lit);
					if(lit.toLowerCase().equals(LiteralService.URL.toLowerCase())) {
						item.setCode(val);
					}
					String val1 = lits.get(lit);
					dto = new OptionDTO();
					dto.setCode(lit);
					dto.setOriginalDescription((val != null && val.length() > 0)?val:val1);
					dto.setDescription((val1 != null && val1.length() > 0)?val1:val);
					
					item.getOptions().add(dto);
				}
				data.getDictByCopy().getOptions().add(item);
				
				//3 list activities
				Concept firstActivity = closureServ.loadRoot("configuration." + url.toLowerCase());
				List<Concept> activities = boilerServ.loadActivities(firstActivity);
				if(activities != null && activities.size() > 0) {
					for(Concept activity:activities) {
						item = new OptionDTO();
						if(activity.getID() == firstActivity.getID()) {
							item.setCode(activity.getIdentifier());
						}

						litsCur = literalServ.literals(activity, curLang);
						lits = literalServ.literals(activity, lang);
						
						for(String lit:litsCur.keySet()) {
							String val = litsCur.get(lit);
							String val1 = lits.get(lit);
							dto = new OptionDTO();
							dto.setCode(lit);
							dto.setOriginalDescription((val != null && val.length() > 0)?val:val1);
							dto.setDescription((val1 != null && val1.length() > 0)?val1:val);
							
							item.getOptions().add(dto);
						}
						
						Thing th = boilerServ.thingByNode(activity);
						if(th != null && th.getID() > 0) {
							//th = thingRepo.findById(th.getID()).get();
							Set<ThingDict> dictionaries = th.getDictionaries();
							for(ThingDict td:dictionaries) {
								dto = new OptionDTO();
								dto.setCode("&&&&" + td.getVarname());
								dto.setOriginalCode(td.getUrl());
								Concept val = td.getConcept();
								dto.setDescription(val.getIdentifier());
								item.getOptions().add(dto);
							}
						}
						data.getDictByCopy().getOptions().add(item);
					}
				}
			}else {
				throw new ObjectNotFoundException(
						"ExchangeConfigMainService.importWorkflows(). Configuration url is wrong defined. It is " + url, logger);
			}
			
		}

		return data;
	}
	
	private ExchangeConfigDTO varifLangs(ExchangeConfigDTO data) {
		String curLang = data.getCurLang().toUpperCase(); // current lang on clientServer
		String lang = data.getOtherLang().toUpperCase();// other lang on clientServer

		List<Language> langs = messages.getLanguages().getLangs();
		if(curLang.equalsIgnoreCase(langs.get(0).getLocaleAsString()) || curLang.equalsIgnoreCase(langs.get(1).getLocaleAsString())) {
			// the current language of the Client server is on the Main Server

		}else if(lang.equalsIgnoreCase(langs.get(0).getLocaleAsString()) || lang.equalsIgnoreCase(langs.get(1).getLocaleAsString())) {
			//other language of the Client server is on the Main Server

		}else {// none of the client server languages are on the Main Server
			// write the default language from the MainServer
			lang = messages.getDefLocaleFromBundle();
			data.setOtherLang(lang);
		}

		return data;
	}

	private Headers createHeaders(Headers ret, boolean isLink) {
		//ret.getHeaders().clear();
		ret.getHeaders().add(TableHeader.instanceOf(
				"url", 
				"url",
				false,
				false,
				false,
				(isLink?TableHeader.COLUMN_LINK:TableHeader.COLUMN_STRING),
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				"prefLbl", 
				"global_name",
				false,
				false,
				false,
				TableHeader.COLUMN_STRING,
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
	
	private Headers createHeadersWF(Headers ret) {
		ret.getHeaders().add(TableHeader.instanceOf(
				"prefLbl", 
				"global_name",
				false,
				false,
				false,
				TableHeader.COLUMN_STRING,
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

	private Headers createHeadersResources(Headers ret, boolean isLink) {
		ret.getHeaders().add(TableHeader.instanceOf(
				"url", 
				"url",
				false,
				false,
				false,
				(isLink?TableHeader.COLUMN_LINK:TableHeader.COLUMN_STRING),
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				"description", 
				"description",
				false,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				"dataurl", 
				"dataurl",
				false,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		ret= boilerServ.translateHeaders(ret);
		return ret;
	}
	
	private Headers createHeadersShort(Headers ret, boolean isLink) {
		ret.getHeaders().add(TableHeader.instanceOf(
				"url", 
				"url",
				false,
				false,
				false,
				(isLink?TableHeader.COLUMN_LINK:TableHeader.COLUMN_STRING),
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

	
	private Headers createHeadersVariables(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf("row", "row", true, true, true, TableHeader.COLUMN_LONG, 0));
		headers.getHeaders().add(TableHeader.instanceOf("col", "col", true, true, true, TableHeader.COLUMN_LONG, 0));
		headers.getHeaders().add(TableHeader.instanceOf("ord", "order", true, true, true, TableHeader.COLUMN_LONG, 0));
		headers.getHeaders()
		.add(TableHeader.instanceOf("propertyName", "variables", true, true, true, TableHeader.COLUMN_LINK, 0));
		headers.getHeaders()
		.add(TableHeader.instanceOf("ext", "ext", true, true, true, TableHeader.COLUMN_STRING, 0));
		headers.getHeaders()
		.add(TableHeader.instanceOf("clazz", "clazz", true, true, true, TableHeader.COLUMN_STRING, 0));

		headers.getHeaders()
		.add(TableHeader.instanceOf("pref", "description", true, true, true, TableHeader.COLUMN_STRING, 0));

		TableHeader h = headers.getHeaders().get(0);
		h.setSort(true);
		h.setSortValue(TableHeader.SORT_ASC);
		TableHeader h1 = headers.getHeaders().get(1);
		h1.setSort(true);
		h1.setSortValue(TableHeader.SORT_ASC);
		TableHeader h2 = headers.getHeaders().get(2);
		h2.setSort(true);
		h2.setSortValue(TableHeader.SORT_ASC);
		headers = boilerServ.translateHeaders(headers);
		headers.setPageSize(100);
		return headers;
	}


}
