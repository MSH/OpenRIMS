package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.Dict2DTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.ExchangeConfigDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * service for updating process configurations from the main server
 * @author khome
 *
 */
@Service
public class ExchangeConfigurationService {
	private static final Logger logger = LoggerFactory.getLogger(ExchangeConfigurationService.class);
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private SystemService systemServ;
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
	private JdbcRepository jdbcRepo;
	@Autowired
	private AssemblyService assemblyServ;

	//public static final String SYSTEM_IMPORT_EXCHANGE_CONFIGURATION="system.import.exchange.configuration";

	private Map<Integer, String> pageHeaders = new HashMap<Integer, String>();
	private static Map<Integer, String> requestToMain = new HashMap<Integer, String>();
	static {
		requestToMain.put(1, "/api/public/exchange/processes/load");
		requestToMain.put(2, "/api/public/exchange/checklists/load");
		requestToMain.put(3, "/api/public/exchange/dictionaries/load");
		requestToMain.put(4, "/api/public/exchange/resources/load");
	}
	private static String req_dictimport = "/api/public/exchange/dictionary/import";
	private static String req_ping = "/api/public/ping";

	public ExchangeConfigDTO load(ExchangeConfigDTO data) {
		buildListHeaders(data);
		if(data.getIdentifier() != null && data.getIdentifier().length() > 0) {
			data.getServerurl().setValue(data.getIdentifier());
			data.setIdentifier("");
		}
		return data;
	}
	
	public ExchangeConfigDTO pingServer(ExchangeConfigDTO data) {
		clearTables(data);
		data.setPingServer(false);
		data.setIdentifier("error server name");
		String url = data.getServerurl().getValue();
		if(url != null && url.startsWith("http")) {

			String s = restTemplate.getForObject(data.getServerurl().getValue() + req_ping, String.class);
			if(s != null && s.equals("OK")) {
				data.setPingServer(true);
				data.setIdentifier("");
			}
		}
		return data;
	}

	private boolean isDevelop(ExchangeConfigDTO data) {
		if(data.getServerurl().getValue().startsWith("http://localhost:")) {
			return true;
		}
		return false;
	}

	private void buildListHeaders(ExchangeConfigDTO data) {
		data.getHeaders().clear();

		pageHeaders = new HashMap<Integer, String>();
		pageHeaders.put(0, "Connect");
		pageHeaders.put(1, messages.get("processes"));
		pageHeaders.put(2, "Checklist Dictionaries");
		pageHeaders.put(3, messages.get("dictionaries"));
		pageHeaders.put(4, messages.get("resources"));

		data.getHeaders().addAll(pageHeaders.values());
		/*data.getHeaders().add("Connect");
		data.getHeaders().add("Checklist Dictionaries");
		data.getHeaders().add(messages.get("dictionaries"));
		data.getHeaders().add(messages.get("resources"));*/

		//data.getHeaders().add(messages.get("processes"));
		//data.getHeaders().add("Data Config URL");
		//data.getHeaders().add("");
		//data.getHeaders().add(messages.get(""));
	}

	public ExchangeConfigDTO loadNext(ExchangeConfigDTO data) throws ObjectNotFoundException {
		if(validData(data)) {
			clearTables(data);

			String url = data.getServerurl() + requestToMain.get(data.getCurrentPage());
			data = restTemplate.postForObject(url, data, ExchangeConfigDTO.class);

			if(data.getCurrentPage() == 2) {
				data = buildCheckListTables(data);
			}else if(data.getCurrentPage() == 3) {
				data = buildDictionariesTables(data);
			}else if(data.getCurrentPage() == 4) {
				data = buildResourcesTables(data);
			}
		}
		return data;
	}

	private boolean validData(ExchangeConfigDTO data) {
		data = pingServer(data);
		if(data.isPingServer()) {
			data.setIdentifier("Select value");
			data.setValid(false);

			if(data.getCurrentPage() == 2) {
				if(data.getWfdto().getSlaveDict().getUrl() != null && data.getWfdto().getSlaveDict().getUrl().length() > 0) {
					List<TableRow> rows = data.getWfdto().getSlaveDict().getTable().getRows();
					if(rows != null && rows.size() > 0) {
						for(TableRow r:rows) {
							if(r.getSelected()) {
								data.setIdentifier("");
								data.setValid(true);

								break;
							}
						}
					}
				}
			}else {
				data.setIdentifier("");
				data.setValid(true);
			}
		}

		return true;
	}


	public ExchangeConfigDTO loadDictionary(ExchangeConfigDTO data) throws ObjectNotFoundException {
		if(data.getUrlSelect() != null && data.getUrlSelect().length() > 0) {
			DictionaryDTO dict = new DictionaryDTO();
			Concept root = closureServ.loadConceptByIdentifier(data.getUrlSelect());
			dict.setUrlId(root.getID());
			dict.setUrl(root.getIdentifier());

			//dict.setSystem(dictServ.checkSystem(root));//ika
			data.setShowDict(dictServ.createDictionaryFromRoot(dict, root));
		}else {
			data.setShowDict(new DictionaryDTO());
		}
		return data;
	}

	/**
	 * on main server load processes dictionary
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ExchangeConfigDTO loadProcesses(ExchangeConfigDTO data) throws ObjectNotFoundException {
		Dict2DTO dto = data.getWfdto();
		dto = systemServ.stagesWorkflow(dto);

		data.setWfdto(dto);
		return data;
	}

	private void clearTables(ExchangeConfigDTO data) {
		if(data.getCurrentPage() >= 2 || data.getCurrentPage() <= 4) {
			data.getNotExistTable().getRows().clear();
			data.getExistTable().getRows().clear();
			data.setShowDict(new DictionaryDTO());
			data.setUrlSelect("");
			data.setShowDict(new DictionaryDTO());
		}
	}
	/**
	 * we check whether there are dictionaries from MainServer on the ClientServer, 
	 * and build tables: existing dictionaries and non-existing ones
	 */
	private ExchangeConfigDTO buildCheckListTables(ExchangeConfigDTO data) throws ObjectNotFoundException {
		// varification all table items
		TableQtb notExistTable = data.getNotExistTable();
		if(notExistTable.getHeaders().getHeaders().size() == 0) {
			notExistTable.setHeaders(createHeaders(notExistTable.getHeaders(), false));
		}
		
		TableQtb existTable = data.getExistTable();
		if(existTable.getHeaders().getHeaders().size() == 0) {
			existTable.setHeaders(createHeaders(existTable.getHeaders(), true));
		}

		List<TableRow> rows = new ArrayList<TableRow>();
		for(TableRow row:notExistTable.getRows()) {
			String dictUrl = row.getCellByKey("url").getValue();

			Concept root = closureServ.loadConceptByIdentifierActive(dictUrl);
			if(isDevelop(data)) {
				root = closureServ.loadConceptByIdentifierActive(dictUrl + ".dev");
			}
			if(root != null) {// словарь с таким урлом существует
				TableRow r = new TableRow();
				r.setDbID(root.getID());

				TableCell cell = new TableCell();
				cell.setKey("url");
				cell.setValue(root.getIdentifier());
				r.getRow().add(cell);

				cell = new TableCell();
				cell.setKey("prefLbl");
				cell.setValue(literalServ.readPrefLabel(root));
				r.getRow().add(cell);

				cell = new TableCell();
				cell.setKey("description");
				cell.setValue(literalServ.readDescription(root));
				r.getRow().add(cell);

				existTable.getRows().add(r);
			}else{//нет
				rows.add(row);
			}
		}

		notExistTable.getRows().clear();
		notExistTable.getRows().addAll(rows);

		data.getNotExistTable().setSelectable(true);
		data.getExistTable().setSelectable(false);

		return data;
	}


	private ExchangeConfigDTO buildDictionariesTables(ExchangeConfigDTO data) throws ObjectNotFoundException {
		data = buildCheckListTables(data);
		return data;
	}

	private ExchangeConfigDTO buildResourcesTables(ExchangeConfigDTO data) throws ObjectNotFoundException {
		data = buildCheckListTables(data);
		data.getNotExistTable().setSelectable(false);
		return data;
	}
	/**
	 * on main server load dictionaries by checklist
	 * For all activities get field value "Checklist Dictionary URL"
	 * and add field value "Checklist Dictionary URL" from item dictionary
	 */
	public ExchangeConfigDTO loadChecklistDictionaries(ExchangeConfigDTO data) throws ObjectNotFoundException {
		if(data.getWfdto().getSlaveDict().getPrevSelected() != null && data.getWfdto().getSlaveDict().getPrevSelected().size() > 0) {
			Long dictNodeId = data.getWfdto().getSlaveDict().getPrevSelected().get(0);
			TableQtb table = data.getNotExistTable();
			if(table.getHeaders().getHeaders().size() == 0) {
				table.setHeaders(createHeaders(table.getHeaders(), false));
			}

			Concept dictNode = closureServ.loadConceptById(dictNodeId);
			String url = literalServ.readValue(LiteralService.APPLICATION_URL, dictNode);
			if (url.length() > 5) {
				String checklisturl = literalServ.readValue(LiteralService.CHECKLIST_URL, dictNode);
				if(checklisturl != null && checklisturl.length() > 0) {
					TableRow row = createRow(checklisturl);
					table.getRows().add(row);
				}

				Concept firstActivity = closureServ.loadRoot("configuration." + url.toLowerCase());
				List<Concept> activities = boilerServ.loadActivities(firstActivity);
				if(activities != null && activities.size() > 0) {
					for(Concept activity:activities) {
						String checkListDictUrl = literalServ.readValue(LiteralService.CHECKLIST_URL, activity);
						if(checkListDictUrl != null && checkListDictUrl.length() > 0) {
							TableRow row = createRow(checkListDictUrl);
							table.getRows().add(row);
						}
					}
					data.setNotExistTable(table);
				}
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
		if(data.getWfdto().getSlaveDict().getPrevSelected() != null && data.getWfdto().getSlaveDict().getPrevSelected().size() > 0) {
			Long dictNodeId = data.getWfdto().getSlaveDict().getPrevSelected().get(0);
			TableQtb table = data.getNotExistTable();
			if(table.getHeaders().getHeaders().size() == 0) {
				table.setHeaders(createHeaders(table.getHeaders(), false));
			}

			// словари из конфигураций активностей
			Concept dictNode = closureServ.loadConceptById(dictNodeId);
			String url = literalServ.readValue(LiteralService.APPLICATION_URL, dictNode);
			if (url.length() > 5) {
				List<Long> nodeIds = new ArrayList<Long>();

				String dataurl = literalServ.readValue(LiteralService.DATA_URL, dictNode);
				if(dataurl != null && dataurl.length() > 0) {
					Concept c = closureServ.loadConceptByIdentifier(dataurl);
					if(c != null) {
						nodeIds.add(c.getID());
					}
					loadAssembliesRecursion(dataurl, nodeIds);
				}
				//TODO add person, addresses, resources
				Concept firstActivity = closureServ.loadRoot("configuration." + url.toLowerCase());
				List<Concept> activities = boilerServ.loadActivities(firstActivity);
				if(activities != null && activities.size() > 0) {
					for(Concept activity:activities) {
						dataurl = literalServ.readValue(LiteralService.DATA_URL, activity);
						if(dataurl != null && dataurl.length() > 0) {
							Concept c = closureServ.loadConceptByIdentifier(dataurl);
							if(c != null) {
								nodeIds.add(c.getID());
							}
							loadAssembliesRecursion(dataurl, nodeIds);
						}
					}
				}

				if(nodeIds.size() > 0) {
					String ids = "";
					for(Long id:nodeIds) {
						ids += id + ",";
					}
					ids = ids.substring(0, ids.length() - 1);

					// теперь получим все словари
					jdbcRepo.loadDictionaryExchange();
					String select = "select distinct tbl.ID, tbl.url, tbl.prefLbl, tbl.description  " + 
							"from loadDictionaryExchange as tbl";
					String where = "tbl.nodeID in (" + ids + ")";
					List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, table.getHeaders());
					//TableQtb.tablePage(rows, table);

					table.getRows().clear();
					table.getHeaders().setPages(1);
					table.getRows().addAll(rows);

					data.setNotExistTable(table);
				}
			} else {
				throw new ObjectNotFoundException(
						"workflowConfiguration. Configuration url is wrong defined. It is " + url, logger);
			}

		}
		return data;
	}

	private void loadAssembliesRecursion(String url, List<Long> nodeIds) throws ObjectNotFoundException{
		List<Assembly> assemblies = assemblyServ.loadDataConfiguration(url, "things");
		if(assemblies != null && assemblies.size() > 0) {
			for(Assembly ass:assemblies) {
				String urlAss = ass.getUrl();
				if(urlAss != null && urlAss.length() > 0) {
					Concept c = closureServ.loadConceptByIdentifier(urlAss);
					if(c != null) nodeIds.add(c.getID());
					// resources
					List<Assembly> assRes = assemblyServ.loadDataConfiguration(urlAss, "resources");
					if(assRes != null && assRes.size() > 0) {
						for(Assembly it:assRes) {
							String u = it.getUrl();
							c = closureServ.loadConceptByIdentifier(u);
							if(c != null && c.getLabel() != null && c.getLabel().length() > 0) {
								c = closureServ.loadConceptByIdentifier(c.getLabel());
								if(c != null) nodeIds.add(c.getID());
							}
						}
					}

					// persons
					List<Assembly> assPer = assemblyServ.loadDataConfiguration(urlAss, "persons");
					if(assPer != null && assPer.size() > 0) {
						for(Assembly it:assPer) {
							String u = it.getAuxDataUrl();
							c = closureServ.loadConceptByIdentifier(u);
							if(c != null) nodeIds.add(c.getID());
							loadAssembliesRecursion(u, nodeIds);
						}
					}
					// addresses
					//List<Assembly> assAddr = assemblyServ.loadDataConfiguration(urlAss, "addresses");

					loadAssembliesRecursion(urlAss, nodeIds);
				}
			}
		}
	}

	private void loadAssembliesRecursionResources(String url, List<Concept> list) throws ObjectNotFoundException{
		List<Assembly> assemblies = assemblyServ.loadDataConfiguration(url, "things");
		if(assemblies != null && assemblies.size() > 0) {
			for(Assembly ass:assemblies) {
				String urlAss = ass.getUrl();
				if(urlAss != null && urlAss.length() > 0) {
					Concept c = null;//closureServ.loadConceptByIdentifier(urlAss);
					// resources
					List<Assembly> assRes = assemblyServ.loadDataConfiguration(urlAss, "resources");
					if(assRes != null && assRes.size() > 0) {
						for(Assembly it:assRes) {
							String u = it.getUrl();

							c = closureServ.loadConceptByIdentifier(u);
							if(c != null) {
								list.add(c);
								if(c.getLabel() != null && c.getLabel().length() > 0) {
									c = closureServ.loadConceptByIdentifier(c.getLabel());
									if(c != null) list.add(c);
								}
							}
						}
					}
					loadAssembliesRecursionResources(urlAss, list);
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
		Long dictNodeId = data.getWfdto().getSlaveDict().getPrevSelected().get(0);
		TableQtb table = data.getNotExistTable();
		if(table.getHeaders().getHeaders().size() == 0) {
			table.setHeaders(createHeaders(table.getHeaders(), false));
		}

		// берем запись resources и конфигурацию к ней
		Concept dictNode = closureServ.loadConceptById(dictNodeId);
		String url = literalServ.readValue(LiteralService.APPLICATION_URL, dictNode);
		if (url.length() > 5) {
			List<Concept> list = new ArrayList<Concept>();

			String dataurl = literalServ.readValue(LiteralService.DATA_URL, dictNode);
			if(dataurl != null && dataurl.length() > 0) {
				Concept c = null;//closureServ.loadConceptByIdentifier(dataurl);

				List<Assembly> assemblies = assemblyServ.loadDataConfiguration(dataurl, "resources");
				if(assemblies != null && assemblies.size() > 0) {
					for(Assembly ass:assemblies) {
						url = ass.getUrl();

						c = closureServ.loadConceptByIdentifier(url);
						if(c != null) {
							list.add(c);
							if(c.getLabel() != null && c.getLabel().length() > 0) {
								c = closureServ.loadConceptByIdentifier(c.getLabel());
								if(c != null) list.add(c);
							}
						}
					}
				}

				loadAssembliesRecursionResources(dataurl, list);
			}
			//TODO add person, addresses, resources
			Concept firstActivity = closureServ.loadRoot("configuration." + url.toLowerCase());
			List<Concept> activities = boilerServ.loadActivities(firstActivity);
			if(activities != null && activities.size() > 0) {
				for(Concept activity:activities) {
					dataurl = literalServ.readValue(LiteralService.DATA_URL, activity);
					if(dataurl != null && dataurl.length() > 0) {
						Concept c = null;//closureServ.loadConceptByIdentifier(dataurl);

						List<Assembly> assemblies = assemblyServ.loadDataConfiguration(dataurl, "resources");
						if(assemblies != null && assemblies.size() > 0) {
							for(Assembly ass:assemblies) {
								url = ass.getUrl();

								c = closureServ.loadConceptByIdentifier(url);
								if(c != null) {
									list.add(c);
									if(c.getLabel() != null && c.getLabel().length() > 0) {
										c = closureServ.loadConceptByIdentifier(c.getLabel());
										if(c != null) list.add(c);
									}
								}
							}
						}

						loadAssembliesRecursionResources(dataurl, list);
					}
				}
			}

			if(list.size() > 0) {
				for(Concept c:list) {
					TableRow row = new TableRow();
					row.setDbID(c.getID());

					TableCell cell = new TableCell();
					cell.setKey("url");
					cell.setValue(c.getIdentifier());
					row.getRow().add(cell);

					cell = new TableCell();
					cell.setKey("prefLbl");
					cell.setValue(literalServ.readPrefLabel(c));
					row.getRow().add(cell);

					cell = new TableCell();
					cell.setKey("description");
					cell.setValue(literalServ.readDescription(c));
					row.getRow().add(cell);

					table.getRows().add(row);
				}

				data.setNotExistTable(table);
			}
		} else {
			throw new ObjectNotFoundException(
					"workflowConfiguration. Configuration url is wrong defined. It is " + url, logger);
		}
		return data;
	}

	/*
	 //-- получим словари из конфигураций для ресурсов
					// из конфигураций активностей берем ресурсы: ресурс-конфигурация на него-словарь
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

	private TableRow createRow(String urlDict) throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot(urlDict);

		TableRow row = new TableRow();
		row.setDbID(root.getID());

		TableCell cell = new TableCell();
		cell.setKey("url");
		cell.setValue(urlDict);
		row.getRow().add(cell);

		cell = new TableCell();
		cell.setKey("prefLbl");
		cell.setValue(literalServ.readPrefLabel(root));
		row.getRow().add(cell);

		cell = new TableCell();
		cell.setKey("description");
		cell.setValue(literalServ.readDescription(root));
		row.getRow().add(cell);

		return row;
	}

	public ExchangeConfigDTO importDictionary(ExchangeConfigDTO  data) throws ObjectNotFoundException {
		pingServer(data);
		if(data.isPingServer()) {
			data.setCurLang(LocaleContextHolder.getLocale().toString().toUpperCase());
			for(Language l:messages.getLanguages().getLangs()) {
				if(!l.getLocaleAsString().equalsIgnoreCase(LocaleContextHolder.getLocale().toString().toUpperCase())) {
					data.setOtherLang(l.getLocaleAsString().toUpperCase());
					break;
				}
			}

			String url = data.getServerurl() + req_dictimport;
			data = restTemplate.postForObject(url, data, ExchangeConfigDTO.class);

			if(data.getUrlByCopy() != null && data.getUrlByCopy().length() > 0) {
				String curLang = data.getCurLang(); // current lang on clientServer
				String lang = data.getOtherLang();

				OptionDTO rootDTO = data.getDictByCopy();
				Concept root = closureServ.loadRoot(data.getUrlByCopy());
				if(isDevelop(data)) {
					root = closureServ.loadRoot(data.getUrlByCopy() + ".dev");
				}

				Map<String, String> values = new HashMap<String, String>();// одно из значений всегда заполнено
				String curV = !rootDTO.getOriginalCode().isEmpty()?rootDTO.getOriginalCode():rootDTO.getCode();
				String v = !rootDTO.getCode().isEmpty()?rootDTO.getCode():rootDTO.getOriginalCode();
				values.put(curLang, curV);
				values.put(lang, v);
				root = literalServ.createUpdateLiteral(LiteralService.PREF_NAME, root, values);

				values = new HashMap<String, String>();
				curV = !rootDTO.getOriginalDescription().isEmpty()?rootDTO.getOriginalDescription():rootDTO.getDescription();
				v = !rootDTO.getDescription().isEmpty()?rootDTO.getDescription():rootDTO.getOriginalDescription();
				values.put(curLang, curV);
				values.put(lang, v);
				root = literalServ.createUpdateLiteral(LiteralService.DESCRIPTION, root, values);
				root.setActive(rootDTO.isActive());

				root = closureServ.saveToTree(null, root);

				recursionCreateDictionary(rootDTO.getOptions(), root, curLang, lang);
			}
		}
		return data;
	}

	public void recursionCreateDictionary(List<OptionDTO> list, Concept parent, String locale, String lang) throws ObjectNotFoundException {
		if(list != null && list.size() > 0) {
			for(OptionDTO dto:list) {
				Concept concept = new Concept();
				concept.setActive(dto.isActive());
				concept.setIdentifier("ident");
				concept = closureServ.saveToTree(parent, concept);
				concept.setIdentifier(concept.getID() + "");
				concept = closureServ.save(concept);

				Map<String, String> values = new HashMap<String, String>();
				String curV = !dto.getOriginalCode().isEmpty()?dto.getOriginalCode():dto.getCode();
				String v = !dto.getCode().isEmpty()?dto.getCode():dto.getOriginalCode();
				values.put(locale, curV);
				values.put(lang, v);
				concept = literalServ.createUpdateLiteral(LiteralService.PREF_NAME, concept, values);

				values = new HashMap<String, String>();
				curV = !dto.getOriginalDescription().isEmpty()?dto.getOriginalDescription():dto.getDescription();
				v = !dto.getDescription().isEmpty()?dto.getDescription():dto.getOriginalDescription();
				values.put(locale, curV);
				values.put(lang, v);
				concept = literalServ.createUpdateLiteral(LiteralService.DESCRIPTION, concept, values);

				recursionCreateDictionary(dto.getOptions(), concept, locale, lang);
			}
		}
	}

	public ExchangeConfigDTO importDictionaryMainServer(ExchangeConfigDTO  data) throws ObjectNotFoundException {
		if(data.getDictByCopy().getId() > 0) {
			String curLang = data.getCurLang().toUpperCase(); // current lang on clientServer
			String lang = data.getOtherLang().toUpperCase();// other lang on clientServer

			List<Language> langs = messages.getLanguages().getLangs();
			if(curLang.equalsIgnoreCase(langs.get(0).getLocaleAsString()) || curLang.equalsIgnoreCase(langs.get(1).getLocaleAsString())) {
				// текущий язык сервераКлиента есть на ГлавномСервере

			}else if(lang.equalsIgnoreCase(langs.get(0).getLocaleAsString()) || lang.equalsIgnoreCase(langs.get(1).getLocaleAsString())) {
				// другой язык сервераКлиента есть на ГлавномСервере

			}else {// ниодного из языков сервераКлиента нет на ГлавномСервере
				// пишем язык по умолчанию с ГлавномСервере
				lang = messages.getDefLocaleFromBundle();
				data.setOtherLang(lang);
			}

			Concept root = closureServ.loadConceptById(data.getDictByCopy().getId());
			data.setUrlByCopy(root.getIdentifier());

			OptionDTO rootDTO = new OptionDTO();
			rootDTO.setId(root.getID());
			rootDTO.setActive(true);
			String prefLbl = literalServ.readValue(LiteralService.PREF_NAME, root, curLang);
			rootDTO.setOriginalCode(prefLbl);
			String descr = literalServ.readValue(LiteralService.DESCRIPTION, root, curLang);
			rootDTO.setOriginalDescription(descr);

			prefLbl = literalServ.readValue(LiteralService.PREF_NAME, root, lang);
			rootDTO.setCode(prefLbl);
			descr = literalServ.readValue(LiteralService.DESCRIPTION, root, lang);
			rootDTO.setDescription(descr);

			Concept levelNode = closureServ.loadConceptById(root.getID());
			List<Concept> child = literalServ.loadOnlyChilds(levelNode);
			if(child != null && child.size() > 0) {
				recursionLoadDictionary(child, rootDTO, curLang, lang);
			}

			data.setDictByCopy(rootDTO);
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
}
