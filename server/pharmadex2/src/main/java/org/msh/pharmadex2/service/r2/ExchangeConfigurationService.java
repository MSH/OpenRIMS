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
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingDict;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.r2.ConceptRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.DataVariableDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.ExchangeConfigDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.common.BoilerService;
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
	private SupervisorService superService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private ConceptRepo conceptRepo;

	private Map<Integer, String> pageHeaders = new HashMap<Integer, String>();
	private static Map<Integer, String> requestToMain = new HashMap<Integer, String>();
	static {
		requestToMain.put(1, "/api/public/exchange/processes/load");
		//requestToMain.put(100, "/api/public/exchange/processes/geturl");
		requestToMain.put(2, "/api/public/exchange/checklists/load");
		requestToMain.put(3, "/api/public/exchange/dictionaries/load");
		requestToMain.put(4, "/api/public/exchange/resources/load");
		requestToMain.put(5, "/api/public/exchange/dataconfig/load");
		requestToMain.put(6, "/api/public/exchange/workflows/load");
		
	}
	private static Map<String, String> reqToMain = new HashMap<String, String>();
	static {
		reqToMain.put("processes", "/api/public/exchange/processes/load");
		reqToMain.put("preview", "/api/public/exchange/process/preview");
	}
	private static String req_dictimport = "/api/public/exchange/dictionary/import";
	private static String req_resimport = "/api/public/exchange/resource/import";
	private static String req_dataconfigimport = "/api/public/exchange/dataconfig/import";
	private static String req_workflowsimport = "/api/public/exchange/workflows/importall";
	private static String req_ping = "/api/public/pingbyimport";//;"/api/public/ping"

	public ExchangeConfigDTO load(ExchangeConfigDTO data) {
		buildListHeaders(data);
		data.getServerurl().setValue(data.getIdentifier());
		data.setIdentifier("");
		data.setValid(false);// by red check
		return data;
	}

	public ExchangeConfigDTO pingServer(ExchangeConfigDTO data) throws ObjectNotFoundException{
		data.setValid(false);
		data.setIdentifier(messages.get("error") + " " + messages.get("serverurl"));
		String url = data.getServerurl().getValue();
		if(url != null && url.startsWith("http")) {
			try {
				ResponseEntity<String> r = restTemplate.getForEntity(data.getServerurl().getValue() + req_ping, String.class);
				if(r != null) {
					if(r.getBody().equals("OK")) {
						data.setValid(true);
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
		pageHeaders.put(0, messages.get("connect"));
		pageHeaders.put(1, messages.get("processes"));
		pageHeaders.put(2, messages.get("checklist") + " " + messages.get("dictionaries"));
		pageHeaders.put(3, messages.get("dictionaries"));
		pageHeaders.put(4, messages.get("resources"));
		pageHeaders.put(5, messages.get("configurations"));
		pageHeaders.put(6, messages.get("workflows"));
		pageHeaders.put(7, messages.get("importresult"));

		data.getHeaders().addAll(pageHeaders.values());
	}

	public ExchangeConfigDTO loadNext(ExchangeConfigDTO data) throws ObjectNotFoundException {
		if(validData(data)) {
			clearTables(data);
			int index = data.getCurrentPage();
			try {
				/*if(data.getCurrentPage() == 2 && data.getProcessId() > 0 && data.getItProcessID() > 0
						&& data.getUrlSelect() == null) {
					index = 100;
				}*/

				if(data.getCurrentPage() == 7) {
					return data;
				}

				if(requestToMain.get(data.getCurrentPage()) == null) {
					return data;
				}

				String url = data.getServerurl() + requestToMain.get(index);
				ResponseEntity<ExchangeConfigDTO> r = restTemplate.postForEntity(url, data, ExchangeConfigDTO.class);

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

			if(index == 1) {
				data = buildProccessesTables(data);
			}else if(index == 2) {
				data = buildCheckListTables(data);
			}else if(index == 3) {
				data = buildDictionariesTables(data);
			}else if(index == 4) {
				data = buildResourcesTables(data);
			}else if(index == 5) {
				data = buildDataConfigsTables(data);
			}else if(index == 6) {
				data = buildWorkflowsTables(data);
			}/*else if(index == 100){
				data = validSelectProccess(data);
				if(data.isValid()) {
					data = loadNext(data);
				}
			}*/
		}
		return data;
	}

	private boolean validData(ExchangeConfigDTO data) throws ObjectNotFoundException {
		data = pingServer(data);
		if(data.isValid()) {
			data.setIdentifier("Select value");
			data.setValid(false);

			if(data.getCurrentPage() == 2) {
				//data = validSelectProccess(data);
				//return data.isValid();
				if(data.getProcessId() > 0 && data.getItProcessID() > 0) {
					data.setIdentifier("");
					data.setValid(true);
					return true;
				}
			}else {
				data.setIdentifier("");
				data.setValid(true);
				return true;
			}
		}

		return false;
	}

	private void clearTables(ExchangeConfigDTO data) {
		if(data.getCurrentPage() == 0) {
			data.getExistTable().getRows().clear();
			data.getNotExistTable().getRows().clear();

			//data.getWfdto().getMasterDict().clear();
			//data.getWfdto().getSlaveDict().clear();
		}
		if(data.getCurrentPage() == 1) {
			if(data.getProcessId() == 0) {
				data.getNotExistTable().getRows().clear();
			}
			//data.setImportDone(false);
			//data.getWfdto().getMasterDict().clear();
			//data.getWfdto().getSlaveDict().clear();
		}
		if(data.getCurrentPage() > 2 && data.getCurrentPage() <= 5) {
			data.setNotExistTable(new TableQtb());
			data.setExistTable(new TableQtb());
			//data.setUrlSelect(null);
		}
	}
	
	private ExchangeConfigDTO buildProccessesTables(ExchangeConfigDTO data) throws ObjectNotFoundException {
		if(!isDevelop(data)) {
			// varification all table items
			TableQtb notExistTable = data.getNotExistTable();
			List<TableRow> rows = new ArrayList<TableRow>();
			for(TableRow row:notExistTable.getRows()) {
				Long idMain = row.getDbID();
				String urlProc = data.getProccURLs().get(idMain);
				if(urlProc != null && urlProc.length() > 0) {
					Concept curProc = systemService.findProccessByUrl(data.getUrlProcess(), urlProc);
					if(curProc == null) {
						rows.add(row);
					}
				}
			}

			notExistTable.getRows().clear();
			notExistTable.getRows().addAll(rows);
			data.setNotExistTable(notExistTable);

			data.getNotExistTable().setSelectable(true);
		}
		return data;
	}
	
	public ExchangeConfigDTO previewProcessLoad(ExchangeConfigDTO data) throws ObjectNotFoundException {
		if(data.getItProcessID() > 0) {
			try {
				String url = data.getServerurl() + reqToMain.get("preview");
				ResponseEntity<ExchangeConfigDTO> r = restTemplate.postForEntity(url, data, ExchangeConfigDTO.class);

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
		}else {
			data.setIdentifier("Select value");
			data.setValid(false);
			return data;
		}
		return data;
	}
	
	
	/**
	 * we check whether there are dictionaries from MainServer on the ClientServer, 
	 * and build tables: existing dictionaries and non-existing ones
	 */
	private ExchangeConfigDTO buildCheckListTables(ExchangeConfigDTO data) throws ObjectNotFoundException {
		// varification all table items
		TableQtb notExistTable = data.getNotExistTable();

		TableQtb existTable = new TableQtb();
		existTable.setHeaders(createHeaders(existTable.getHeaders(), true));

		List<TableRow> rows = new ArrayList<TableRow>();
		for(TableRow row:notExistTable.getRows()) {
			String dictUrl = row.getCellByKey("url").getValue();

			Concept root = closureServ.loadConceptByIdentifierActive(dictUrl);
			if(isDevelop(data)) {
				root = closureServ.loadConceptByIdentifierActive(dictUrl + ".dev");
			}
			if(root != null) {// there is a dictionary with this URL
				TableRow r = createRow(root, false, true);
				existTable.getRows().add(r);
			}else{//no
				rows.add(row);
			}
		}

		notExistTable.getRows().clear();
		notExistTable.getRows().addAll(rows);

		data.setExistTable(existTable);
		data.setNotExistTable(notExistTable);

		data.getNotExistTable().setSelectable(true);
		data.getExistTable().setSelectable(false);

		return data;
	}


	private ExchangeConfigDTO buildDictionariesTables(ExchangeConfigDTO data) throws ObjectNotFoundException {
		data = buildCheckListTables(data);
		return data;
	}

	private ExchangeConfigDTO buildResourcesTables(ExchangeConfigDTO data) throws ObjectNotFoundException {
		// varification all table items
		TableQtb notExistTable = data.getNotExistTable();

		TableQtb existTable = new TableQtb();
		existTable.setHeaders(createHeadersResources(existTable.getHeaders(), true));

		List<TableRow> rows = new ArrayList<TableRow>();
		for(TableRow row:notExistTable.getRows()) {
			String dictUrl = row.getCellByKey("url").getValue();

			Concept root = closureServ.loadConceptByIdentifierActive(dictUrl);
			if(isDevelop(data)) {
				root = closureServ.loadConceptByIdentifierActive(dictUrl + ".dev");
			}
			if(root != null) {// there is a dictionary with this URL
				TableRow r = new TableRow();
				r.setDbID(root.getID());
				TableCell cell = new TableCell();
				cell.setKey("url");
				cell.setValue(root.getIdentifier());
				r.getRow().add(cell);

				cell = new TableCell();
				cell.setKey("description");
				cell.setValue(literalServ.readDescription(root));
				r.getRow().add(cell);

				cell = new TableCell();
				cell.setKey("dataurl");
				cell.setValue(root.getLabel());
				r.getRow().add(cell);

				existTable.getRows().add(r);
			}else{//no
				rows.add(row);
			}
		}

		notExistTable.getRows().clear();
		notExistTable.getRows().addAll(rows);

		data.setExistTable(existTable);
		data.setNotExistTable(notExistTable);

		data.getNotExistTable().setSelectable(true);
		data.getExistTable().setSelectable(false);

		return data;
	}

	private ExchangeConfigDTO buildDataConfigsTables(ExchangeConfigDTO data) throws ObjectNotFoundException {
		// varification all table items
		TableQtb notExistTable = data.getNotExistTable();

		TableQtb existTable = new TableQtb();
		existTable.setHeaders(createHeadersShort(existTable.getHeaders(), true));

		List<TableRow> rows = new ArrayList<TableRow>();
		for(TableRow row:notExistTable.getRows()) {
			String dictUrl = row.getCellByKey("url").getValue();

			Concept root = closureServ.loadConceptByIdentifierActive(dictUrl);
			if(isDevelop(data)) {
				root = closureServ.loadConceptByIdentifierActive(dictUrl + ".dev");
			}
			if(root != null) {// there is a dictionary with this URL
				TableRow r = createRow(root, true, true);
				existTable.getRows().add(r);
			}else{//no
				rows.add(row);
			}
		}

		notExistTable.getRows().clear();
		notExistTable.getRows().addAll(rows);

		data.setExistTable(existTable);
		data.setNotExistTable(notExistTable);

		data.getNotExistTable().setSelectable(true);
		data.getExistTable().setSelectable(false);

		return data;
	}

	private ExchangeConfigDTO buildWorkflowsTables(ExchangeConfigDTO data) throws ObjectNotFoundException {
		data.setExistTable(new TableQtb());
		if(data.getRootProcess().length() > 0 && data.getUrlProcess().length() > 0) {
			try {
				data.getExistTable().setHeaders(createHeadersWF(data.getExistTable().getHeaders()));
				Concept process = systemService.findProccessByUrl(data.getRootProcess(), data.getUrlProcess());
				if(process != null) {
					String url = literalServ.readValue(LiteralService.APPLICATION_URL, process);
					if (url.length() > 5) {
						TableRow row = createRow(process, false, false);
						data.getExistTable().getRows().add(row);
						
						Concept firstActivity = closureServ.loadRoot("configuration." + url.toLowerCase());
						List<Concept> activities = boilerServ.loadActivities(firstActivity);
						if(activities != null && activities.size() > 0) {
							for(Concept activity:activities) {
								row = createRow(activity, false, false);
								data.getExistTable().getRows().add(row);
							}
						}
					}
				}
				data.getExistTable().setSelectable(false);
				data.getNotExistTable().setSelectable(false);
				data.setShowImpAll(false);
				// if left table empty OR contain 1 row - item dictionary
				if(data.getExistTable().getRows().size() <= 1) {
					if(data.getExistTable().getRows().size() == 1) {
						if(data.getExistTable().getRows().get(0).getDbID() == process.getID()) {
							data.setShowImpAll(true);
						}
					}else {
						data.setShowImpAll(true);
						data.getNotExistTable().setSelectable(false);
					}
				}
				data.setNodeIdSelect(0l);
			}catch(ObjectNotFoundException e) {
				data.setValid(false);
				data.setIdentifier("Error");
				return data;
			}
		}
		return data;
	}

	
	private ExchangeConfigDTO validSelectProccess(ExchangeConfigDTO data) throws ObjectNotFoundException {
		if(data.getProcessId() > 0 && data.getItProcessID() > 0) {
			String url = data.getProccURLs().get(data.getItProcessID());
			if(url != null && url.length() > 0) {
				Concept curProc = systemService.findProccessByUrl(data.getUrlProcess(), url);
				if(curProc == null) {
					data.setValid(true);
					data.setIdentifier("");
				}else {
					data.setValid(false);
					data.setIdentifier(messages.get("importProcespresent"));
				}
			}else if(url != null && url.length() == 0) {
				data.setValid(false);
				data.setIdentifier("Import proccess has EMPTY URL. Import is not possible");//messages.get("importProcespresent"));
			}else {
				data.setValid(false);
				data.setIdentifier("Process URL ERROR!!!");
			}
		}
		//data.setValid(true);
		return data;
	}
	
	/**
	 * validate selected proccess url on main server
	 * has on current server
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	/*private ExchangeConfigDTO validSelectProccess(ExchangeConfigDTO data) throws ObjectNotFoundException {
		if(data.getUrlProcess().length() > 0 && data.getUrlSelect().length() > 0) {
			// -1- select dictionary url and proccess url are not empty
			Concept curProc = systemService.findProccessByUrl(data.getUrlProcess(), data.getUrlSelect());
			if(curProc != null) {// find select proccess is Present on current server
				data.setValid(false);
				data.setIdentifier(messages.get("importProcespresent"));
				return data;
			}else { // select proccess is not present on current server - load next page
				data.setValid(true);
				data.setIdentifier("");
				data.setUrlSelect(null);
				//data = loadNext(data);
				return data;
			}
		}else if(data.getUrlSelect().trim().isEmpty()) {
			// -2- select proccess url is empty
			data.setValid(false);
			data.setIdentifier("Import proccess has EMPTY URL. Import is not possible");//messages.get("importProcespresent"));
			return data;
		}
		
		return data;
	}*/

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
		pingServer(data);
		if(data.isValid()) {
			data.setDictByCopy(new OptionDTO());
			data.setCurLang(LocaleContextHolder.getLocale().toString().toUpperCase());
			for(Language l:messages.getLanguages().getLangs()) {
				if(!l.getLocaleAsString().equalsIgnoreCase(LocaleContextHolder.getLocale().toString().toUpperCase())) {
					data.setOtherLang(l.getLocaleAsString().toUpperCase());
					break;
				}
			}

			try {
				String url = data.getServerurl() + req_dictimport;
				data = restTemplate.postForObject(url, data, ExchangeConfigDTO.class);
			}catch (RestClientException e) {
				data.setValid(false);
				data.setIdentifier(messages.get("errorloaddata"));
				return data;
			}

			if(data.isValid()) {
				if(data.getUrlByCopy() != null && data.getUrlByCopy().length() > 0) {
					try {
						String curLang = data.getCurLang(); // current lang on clientServer
						String lang = data.getOtherLang();

						OptionDTO rootDTO = data.getDictByCopy();
						Concept root = closureServ.loadRoot(data.getUrlByCopy());
						if(isDevelop(data)) {
							root = closureServ.loadRoot(data.getUrlByCopy() + ".dev");
						}

						Map<String, String> values = new HashMap<String, String>();// one of the values is always filled
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
					}catch(ObjectNotFoundException e) {
						data.setValid(false);
						data.setIdentifier(messages.get("errorloaddata"));
					}
				}
			}
		}
		return data;
	}

	@Transactional
	public DictionaryDTO loadDictionary(ExchangeConfigDTO data) throws ObjectNotFoundException {
		DictionaryDTO dict = new DictionaryDTO();
		if(data.getUrlSelect() != null && data.getUrlSelect().length() > 0) {
			Concept root = closureServ.loadConceptByIdentifier(data.getUrlSelect());
			dict.setUrlId(root.getID());
			dict.setUrl(root.getIdentifier());

			dict = dictServ.createDictionaryFromRoot(dict, root);
		}
		return dict;
	}

	/**
	 * Put right node id and url to the thing definition
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ThingDTO loadResource(ExchangeConfigDTO data) throws ObjectNotFoundException {
		if (data.getNodeIdSelect() > 0) {
			ThingDTO ret = new ThingDTO();
			Concept node = closureServ.loadConceptById(data.getNodeIdSelect());
			ret.setVarName(node.getIdentifier());
			ret.setNodeId(node.getID());
			ret.setUrl(node.getLabel());

			try {
				Thing th = boilerServ.thingByNode(node);
			}catch (ObjectNotFoundException e) {
				ret.setValid(false);
				ret.setIdentifier("Error load Thing by Resource");
			}
			return ret;
		} else {
			throw new ObjectNotFoundException("loadResource. Node ID is ZERO", logger);
		}
	}

	@Transactional
	public TableQtb loadResConfig(ExchangeConfigDTO data) throws ObjectNotFoundException {
		if (data.getNodeIdSelect() > 0) {
			TableQtb table = new TableQtb();
			if (table.getHeaders().getHeaders().size() == 0) {
				table.setHeaders(createHeadersVariables(table.getHeaders()));
			}
			String where = "p.Active=true and p.nodeID='" + data.getNodeIdSelect() + "' and p.lang='"
					+ LocaleContextHolder.getLocale().toString().toUpperCase() + "'";
			String select="select * from(select distinct av.*, c.Label as 'ext' from assm_var av "
					+ "join assembly a on a.ID=av.assemblyID join concept c on c.ID=a.conceptID) p";
			List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, table.getHeaders());
			TableQtb.tablePage(rows, table);
			table.setSelectable(false);

			Concept c = closureServ.loadConceptById(data.getNodeIdSelect());
			table.setGeneralSearch(c.getIdentifier());
			return table;
		} else {
			throw new ObjectNotFoundException("loadResConfig. Node ID is ZERO", logger);
		}
	}

	@Transactional
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

	@Transactional
	public ExchangeConfigDTO importResource(ExchangeConfigDTO  data) throws ObjectNotFoundException {
		pingServer(data);
		if(data.isValid()) {
			data.setCurLang(LocaleContextHolder.getLocale().toString().toUpperCase());
			for(Language l:messages.getLanguages().getLangs()) {
				if(!l.getLocaleAsString().equalsIgnoreCase(LocaleContextHolder.getLocale().toString().toUpperCase())) {
					data.setOtherLang(l.getLocaleAsString().toUpperCase());
					break;
				}
			}

			try {
				String url = data.getServerurl() + req_resimport;
				data = restTemplate.postForObject(url, data, ExchangeConfigDTO.class);
			}catch (RestClientException e) {
				data.setValid(false);
				data.setIdentifier(messages.get("errorloaddata"));
				return data;
			}
			if(data.isValid()) {
				if(data.getDictByCopy().getCode().length() > 0) {
					try {
						String curLang = data.getCurLang(); // current lang on clientServer
						String lang = data.getOtherLang();

						// load main root by all resources
						Concept root = closureServ.loadRoot("configuration.resources");

						OptionDTO rootDTO = data.getDictByCopy();
						String urlRes = rootDTO.getCode();
						if(isDevelop(data)) {
							urlRes += ".dev";
						}

						String urlConfig = rootDTO.getOriginalCode();
						String value = !data.getDictByCopy().getOriginalDescription().isEmpty()?data.getDictByCopy().getOriginalDescription():data.getDictByCopy().getDescription();
						// current Lang
						Concept resDef = resourceDefinitionCreate(root, curLang, urlRes, urlConfig, urlRes, value);
						Thing th = new Thing();
						th = boilerServ.thingByNode(resDef, th);
						if(th.getID() == 0) {
							th.setUrl(resDef.getIdentifier());
							th.setConcept(resDef);
							th = boilerServ.saveThing(th);
						}

						// other Lang
						value = !data.getDictByCopy().getDescription().isEmpty()?data.getDictByCopy().getDescription():data.getDictByCopy().getOriginalDescription();
						resDef = resourceDefinitionCreate(root, lang, urlRes, urlConfig, urlRes, value);
						th = new Thing();
						th = boilerServ.thingByNode(resDef, th);
						if(th.getID() == 0) {
							th.setUrl(resDef.getIdentifier());
							th.setConcept(resDef);
							th = boilerServ.saveThing(th);
						}


						// configuration by Resource
						OptionDTO configDTO = data.getDictByCopy().getOptions().get(0);
						String urlCong = configDTO.getCode();
						if(isDevelop(data)) {
							urlCong += ".dev";
						}

						Concept node = new Concept();
						node.setIdentifier(urlCong);
						Concept rootConfig = closureServ.loadRoot(SystemService.DATA_COLLECTIONS_ROOT);
						node = closureServ.saveToTree(rootConfig, node);

						Map<String, String> values = new HashMap<String, String>();
						String curV = !configDTO.getOriginalDescription().isEmpty()?configDTO.getOriginalDescription():configDTO.getDescription();
						String v = !configDTO.getDescription().isEmpty()?configDTO.getDescription():configDTO.getOriginalDescription();
						values.put(curLang, curV);
						values.put(lang, v);
						node = literalServ.createUpdateLiteral(LiteralService.PREF_NAME, node, values);

						values.put(curLang, "");
						values.put(lang, "");
						node = literalServ.createUpdateLiteral(LiteralService.DESCRIPTION, node, values);

						for(DataVariableDTO dto:data.getVariables()) {
							dto.setNodeId(node.getID());
							dto.setVarNodeId(0);
							dto.setStrict(true);
							dto = superService.dataCollectionVariableSave(dto);
						}
					}catch(ObjectNotFoundException e) {
						data.setValid(false);
						data.setIdentifier(messages.get("errorloaddata"));
					}
				}
			}
		}
		return data;
	}

	@Transactional
	private Concept resourceDefinitionCreate(Concept root, String localeStr, String urlRes, String lblRes, String pref, String desc)
			throws ObjectNotFoundException {
		Concept lang = closureServ.saveToTree(root, localeStr.toUpperCase());
		Concept resDef = closureServ.saveToTree(lang, urlRes);
		resDef.setLabel(lblRes);
		resDef = closureServ.save(resDef);
		resDef = literalServ.prefAndDescription(pref, desc, resDef);
		return resDef;
	}

	@Transactional
	public ExchangeConfigDTO importDataConfig(ExchangeConfigDTO  data) throws ObjectNotFoundException {
		pingServer(data);
		if(data.isValid()) {
			data.setCurLang(LocaleContextHolder.getLocale().toString().toUpperCase());
			for(Language l:messages.getLanguages().getLangs()) {
				if(!l.getLocaleAsString().equalsIgnoreCase(LocaleContextHolder.getLocale().toString().toUpperCase())) {
					data.setOtherLang(l.getLocaleAsString().toUpperCase());
					break;
				}
			}

			try {
				String url = data.getServerurl() + req_dataconfigimport;
				data = restTemplate.postForObject(url, data, ExchangeConfigDTO.class);
			}catch (RestClientException e) {
				data.setValid(false);
				data.setIdentifier(messages.get("errorloaddata"));
				return data;
			}

			if(data.isValid()) {
				if(data.getVariables() != null && data.getVariables().size() > 0) {
					try {
						String curLang = data.getCurLang(); // current lang on clientServer
						String lang = data.getOtherLang();
						Map<String, String> values = new HashMap<String, String>();//one of the values is always filled

						// the configuration record itself with URL and name
						OptionDTO rootDTO = data.getDictByCopy();
						String urlP = rootDTO.getCode();
						if(isDevelop(data)) {
							urlP += ".dev";
						}

						String d = rootDTO.getDescription();

						Concept node = new Concept();
						node.setIdentifier(urlP);
						Concept root = closureServ.loadRoot(SystemService.DATA_COLLECTIONS_ROOT);
						node = closureServ.saveToTree(root, node);

						String curV = !rootDTO.getOriginalDescription().isEmpty()?rootDTO.getOriginalDescription():rootDTO.getDescription();
						String v = !rootDTO.getDescription().isEmpty()?rootDTO.getDescription():rootDTO.getOriginalDescription();
						values.put(curLang, curV);
						values.put(lang, v);
						node = literalServ.createUpdateLiteral(LiteralService.PREF_NAME, node, values);

						values.put(curLang, "");
						values.put(lang, "");
						node = literalServ.createUpdateLiteral(LiteralService.DESCRIPTION, node, values);

						for(DataVariableDTO dto:data.getVariables()) {
							dto.setNodeId(node.getID());
							dto.setVarNodeId(0);
							dto.setStrict(true);
							dto = superService.dataCollectionVariableSave(dto);
						}
					}catch(ObjectNotFoundException e) {
						data.setValid(false);
						data.setIdentifier(messages.get("errorloaddata"));
					}
				}
			}
		}
		return data;
	}

	@Transactional
	public ExchangeConfigDTO importAllWorkflows(ExchangeConfigDTO  data) throws ObjectNotFoundException{
		pingServer(data);
		if(data.isValid()) {
			data.setCurLang(LocaleContextHolder.getLocale().toString().toUpperCase());
			for(Language l:messages.getLanguages().getLangs()) {
				if(!l.getLocaleAsString().equalsIgnoreCase(LocaleContextHolder.getLocale().toString().toUpperCase())) {
					data.setOtherLang(l.getLocaleAsString().toUpperCase());
					break;
				}
			}
			//data.setImportDone(false);

			try {
				String url = data.getServerurl() + req_workflowsimport;
				data = restTemplate.postForObject(url, data, ExchangeConfigDTO.class);
			}catch (RestClientException e) {
				data.setValid(false);
				data.setIdentifier(messages.get("errorloaddata"));
				return data;
			}
			if(data.isValid()) {
				try {
					String curLang = data.getCurLang(); // current lang on clientServer
					String lang = data.getOtherLang();

					TableRow row = null;
					data.getExistTable().getRows().clear();

					//1 dictionary process
					String rootUrl = data.getRootProcess();
					Concept root = null;//closureServ.loadConceptByIdentifier(rootUrl);
					
					List<Concept> ret = new ArrayList<Concept>();
					ret = conceptRepo.findAllByIdentifier(rootUrl);
					if(ret != null && ret.size() > 0) {
						for(Concept c:ret) {
							Concept parent = closureServ.getParent(c);
							if(parent == null) {
								root = c;
								break;
							}
						}
					}
					
					OptionDTO rootDTO = data.getDictByCopy();
					if(root == null) {
						root = closureServ.loadRoot(rootUrl);

						Map<String, String> values = new HashMap<String, String>();// one of the values is always filled
						String curV = "", v = "";
						if(rootDTO.getOriginalCode() != null && rootDTO.getOriginalCode().length() > 0) {
							curV = rootDTO.getOriginalCode();
						}
						if(rootDTO.getCode() != null && rootDTO.getCode().length() > 0) {
							v = rootDTO.getCode();
						}
						values.put(curLang, curV);
						values.put(lang, v);
						root = literalServ.createUpdateLiteral(LiteralService.PREF_NAME, root, values);

						values = new HashMap<String, String>();
						curV = "";
						v = "";
						if(rootDTO.getOriginalDescription() != null && rootDTO.getOriginalDescription().length() > 0) {
							curV = rootDTO.getOriginalDescription();
						}
						if(rootDTO.getDescription() != null && rootDTO.getDescription().length() > 0) {
							v = rootDTO.getDescription();
						}
						values.put(curLang, curV);
						values.put(lang, v);
						root = literalServ.createUpdateLiteral(LiteralService.DESCRIPTION, root, values);
						root.setActive(rootDTO.isActive());

						root = closureServ.saveToTree(null, root);
					}

					//2 select item dictionary ALWAYS first in options list
					OptionDTO itemDictDTO = data.getDictByCopy().getOptions().get(0);
					Concept itemDict = null;
					try{
						itemDict = systemService.findProccessByUrl(root.getIdentifier(), itemDictDTO.getCode());
					}catch(ObjectNotFoundException e) {
						itemDict = null;
					}

					if(itemDict == null) {// not process in DB
						itemDict = new Concept();
						itemDict.setIdentifier("dev");
						itemDict = closureServ.saveToTree(root, itemDict);
						itemDict.setIdentifier(itemDict.getID() + "");
						itemDict = closureServ.save(itemDict);

						List<OptionDTO> literals = itemDictDTO.getOptions();
						for(OptionDTO litDTO:literals) {
							Map<String, String> values = new HashMap<String, String>();
							String curV = "", v = "";
							if(litDTO.getOriginalDescription() != null && litDTO.getOriginalDescription().length() > 0) {
								curV = litDTO.getOriginalDescription();
							}
							if(litDTO.getDescription() != null && litDTO.getDescription().length() > 0) {
								v = litDTO.getDescription();
							}
							values.put(curLang, curV);
							values.put(lang, v);
							itemDict = literalServ.createUpdateLiteral(litDTO.getCode(), itemDict, values);
						}
					}

					row = createRow(itemDict, false, false);
					data.getExistTable().getRows().add(row);

					Concept currentAct = null; 
					//3 list activities they are always in order; i = 0 - itemDictDTO higher
					for(int i = 1; i < data.getDictByCopy().getOptions().size(); i++) {
						OptionDTO option = data.getDictByCopy().getOptions().get(i);
						if(i == 1) {
							Concept firstAct = closureServ.loadConceptByIdentifier(option.getCode());
							Thing th = null;
							if(firstAct == null) {
								firstAct = new Concept();
								firstAct.setIdentifier(option.getCode());
								firstAct = closureServ.saveToTree(null, firstAct);

								try {
									th = boilerServ.thingByNode(firstAct);
								}catch(ObjectNotFoundException e) {
									th = null;
								}
							}

							if(th == null) {
								th = new Thing();
								th.setUrl(AssemblyService.ACTIVITY_CONFIGURATION);
								th.setConcept(firstAct);
								th = boilerServ.saveThing(th);
							}

							List<OptionDTO> literals = option.getOptions();
							for(OptionDTO litDTO:literals) {
								if(litDTO.getCode().startsWith("&&&&")) {// dictionary
									String varname = litDTO.getCode().substring(4);
									ThingDict td = new ThingDict();
									td.setUrl(litDTO.getOriginalCode());
									td.setVarname(varname);

									// we need to get the system dictionary (for now we assume that it exists
									// find the one you need among the values - without taking into account the language for now
									Concept itDict = null;
									Concept dict = closureServ.loadRoot(litDTO.getOriginalCode());
									for(Concept c:literalServ.loadOnlyActiveChilds(dict)) {
										if(c.getIdentifier().equals(litDTO.getDescription())) {
											itDict = c;
											break;
										}
									}
									td.setConcept(itDict);
									th.getDictionaries().add(td);
								}else {// literals
									Map<String, String> values = new HashMap<String, String>();
									String curV = "", v = "";
									if(litDTO.getOriginalDescription() != null && litDTO.getOriginalDescription().length() > 0) {
										curV = litDTO.getOriginalDescription();
									}
									if(litDTO.getDescription() != null && litDTO.getDescription().length() > 0) {
										v = litDTO.getDescription();
									}
									values.put(curLang, curV);
									values.put(lang, v);
									firstAct = literalServ.createUpdateLiteral(litDTO.getCode(), firstAct, values);
									
									if(litDTO.getCode().equals(AssemblyService.CONCURRENTURL) && !curV.isEmpty()) {
										data.getWarnings().add(messages.get("resImp_concurrenturl") + curV);
									}
								}

								th = boilerServ.saveThing(th);
							}
							currentAct = firstAct;
						}else {
							Concept activity = new Concept();
							activity.setIdentifier("dev");
							activity = closureServ.saveToTree(currentAct, activity);
							activity.setIdentifier(activity.getID() + "");
							activity = closureServ.save(activity);

							Thing th = new Thing();
							th.setUrl(AssemblyService.ACTIVITY_CONFIGURATION);
							th.setConcept(activity);
							th = boilerServ.saveThing(th);

							List<OptionDTO> literals = option.getOptions();
							for(OptionDTO litDTO:literals) {
								if(litDTO.getCode().startsWith("&&&&")) {// dictionary
									String varname = litDTO.getCode().substring(4);
									ThingDict td = new ThingDict();
									td.setUrl(litDTO.getOriginalCode());
									td.setVarname(varname);

									// we need to get the system dictionary (for now we assume that it exists
									// find the one you need among the values - without taking into account the language for now
									Concept itDict = null;
									Concept dict = closureServ.loadRoot(litDTO.getOriginalCode());
									for(Concept c:literalServ.loadOnlyActiveChilds(dict)) {
										if(c.getIdentifier().equals(litDTO.getDescription())) {
											itDict = c;
											break;
										}
									}
									td.setConcept(itDict);
									th.getDictionaries().add(td);
								}else {// literals
									Map<String, String> values = new HashMap<String, String>();
									String curV = "", v = "";
									if(litDTO.getOriginalDescription() != null && litDTO.getOriginalDescription().length() > 0) {
										curV = litDTO.getOriginalDescription();
									}
									if(litDTO.getDescription() != null && litDTO.getDescription().length() > 0) {
										v = litDTO.getDescription();
									}
									values.put(curLang, curV);
									values.put(lang, v);
									activity = literalServ.createUpdateLiteral(litDTO.getCode(), activity, values);
									
									if(litDTO.getCode().equals(AssemblyService.CONCURRENTURL) && !curV.isEmpty()) {
										data.getWarnings().add(messages.get("resImp_concurrenturl") + curV);
									}
								}
							}
							th = boilerServ.saveThing(th);

							currentAct = activity;
						}

						row = createRow(currentAct, false, false);
						data.getExistTable().getRows().add(row);
					}
					//data.setImportDone(true);
					data.setIdentifier(messages.get("global_import_short") + " " + messages.get("global.success"));
				}catch(ObjectNotFoundException e) {
					data.setValid(false);
					data.setIdentifier(messages.get("errorloaddata"));
				}
			}
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
