package org.msh.pharmadex2.service.r2;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.pdex2.dto.i18n.Language;
import org.msh.pdex2.dto.i18n.Languages;
import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.i18n.ResourceBundle;
import org.msh.pdex2.model.i18n.ResourceMessage;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.FileResource;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.i18n.ResourceBundleRepo;
import org.msh.pdex2.repository.i18n.ResourceMessageRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.FileDTO;
import org.msh.pharmadex2.dto.ImportLocalesDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.log.ImportLanguageDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class ImportLocalesService {

	private static final Logger logger = LoggerFactory.getLogger(ImportLocalesService.class);
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private Messages messages;
	@Autowired
	private ThingService thingServ;
	@Autowired
	private ResourceBundleRepo bundleRepo;
	@Autowired
	private ResourceMessageRepo messRepo;
	@Autowired
	private DictService dictServ;
	@Autowired
	private ValidationService validator;
	@Autowired
	private LoggerEventService logEvent;

	/**
	 * prepare electronic form for import admin units
	 * @param data
	 * @param user 
	 * @throws ObjectNotFoundException 
	 */
	public ImportLocalesDTO importLoad(ImportLocalesDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		//load and save only one and only under the root of the tree
		ThingDTO thing = data.getThing();
		thing.setUrl(AssemblyService.SYSTEM_IMPORT_LOCALES);
		Concept root = closureServ.loadRoot(thing.getUrl());
		thing.setParentId(root.getID());
		List<Concept> nodes = closureServ.loadLevel(root);
		if(nodes.size()>0) {
			thing.setNodeId(nodes.get(0).getID());
		}
		if(thing.getNodeId()==0) {
			thing=thingServ.createThing(thing, user);
		}else {
			thing=thingServ.loadThing(thing, user);
		}
		data.setThing(thing);
		data.setTable(loadMessages(data.getTable()));
		return data;
	}

	/**
	 * load table with messages currently defined
	 * @param table
	 * @return
	 */
	public TableQtb loadMessages(TableQtb table) {
		table.setSelectable(false);
		if (table.getHeaders().getHeaders().size() == 0) {
			table.setHeaders(headerMessages(table.getHeaders()));
		}

		String q = "select concat(rm.message_key, \" (\", rb.locale, \")\") as 'message_key', rm.message_value as 'message_value' " + 
				"FROM resource_message as rm " + 
				"join resource_bundle as rb on rb.id=rm.key_bundle";

		List<TableRow> rows = jdbcRepo.qtbGroupReport(q, "", "", table.getHeaders());
		TableQtb.tablePage(rows, table);
		table = boilerServ.translateRows(table);
		//data.setTable(table);
		return table;
	}
	/**
	 * Headers for messages table
	 * @param headers
	 * @return
	 */
	private Headers headerMessages(Headers headers) {
		headers.setPageSize(20);
		headers.getHeaders().add(
				TableHeader.instanceOf("message_key", "res_key", true, true, true, TableHeader.COLUMN_LINK, 40));
		headers.getHeaders().add(
				TableHeader.instanceOf("message_value", "res_value", true, true, true, TableHeader.COLUMN_LINK, 40));
		boilerServ.translateHeaders(headers);
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		return headers;
	}
	/**
	 * headers for the excel table
	 * @param headers
	 * @return
	 */
	private Headers headerDownload(Headers headers) {
		headers.setPageSize(20);
		headers.getHeaders().add(
				TableHeader.instanceOf("message_key", "key", true, true, true, TableHeader.COLUMN_STRING, 40));
		headers.getHeaders().add(
				TableHeader.instanceOf("message_value", "value", true, true, true, TableHeader.COLUMN_STRING, 40));
		headers.getHeaders().add(
				TableHeader.instanceOf("newvalue", "translated", true, true, true, TableHeader.COLUMN_STRING, 40));
		boilerServ.translateHeaders(headers);
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		return headers;
	}
	/**
	 * Download messages on the current language
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public TableQtb downloadTemplate(UserDetailsDTO user) throws ObjectNotFoundException {
		TableQtb table = new TableQtb();
		if (table.getHeaders().getHeaders().size() == 0) {
			table.setHeaders(headerDownload(table.getHeaders()));
		}
		table.getHeaders().setPageSize(Integer.MAX_VALUE);
		ResourceBundle bundle = messages.getCurrentBundle();
		String q = "SELECT\r\n" + 
				"message_key,\r\n" + 
				"message_value,\r\n" + 
				"'' as 'newvalue' \r\n" + 
				"FROM resource_message\r\n" + 
				"where key_bundle = " + bundle.getId();

		List<TableRow> rows = jdbcRepo.qtbGroupReport(q, "", "", table.getHeaders());
		TableQtb.tablePage(rows, table);
		return table;
	}
	/**
	 * Get ID of a bundle with locale selected
	 * @param data
	 * @return name or
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Long selectedLocaleID(ImportLocalesDTO data) throws ObjectNotFoundException {
		// determine language selected
		Set<Long> selected = dictServ.selectedItems(data.getThing().getDictionaries().get("locales"));
		if(selected.size()==1) {
			return selected.iterator().next();
		}else {
			throw new ObjectNotFoundException("ImportVerify. Locales dictionary should be single choice", logger);
		}
	}
	/**
	 * Selected locale name
	 * @param selectLocaleId
	 * @return name or 
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public String selectedLocaleName(Long selectedLocaleDictId) throws ObjectNotFoundException {
		Concept item=closureServ.loadConceptById(selectedLocaleDictId);
		String ret= literalServ.readPrefLabel(item);
		return ret;
	}

	/**
	 * Selected locale human friendly name
	 * @param selectedLocaleDictId
	 * @return name or 
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public String selectedLocaleDisplayName(Long selectedLocaleDictId) throws ObjectNotFoundException {
		Concept item=closureServ.loadConceptById(selectedLocaleDictId);
		String ret= literalServ.readDescription(item);
		return ret;
	}

	/**
	 * Unfortunately it is impossible to validate file uploaded using uniform thing validation algorithm
	 */
	@Transactional
	public ImportLocalesDTO importVerify(ImportLocalesDTO data) throws ObjectNotFoundException, JsonParseException, JsonMappingException, IOException {
		data.clearErrors();
		ThingDTO thing = data.getThing();
		thing = validator.thing(thing, new ArrayList<AssemblyDTO>(), true);
		if(thing.isValid()) {
			Long selectLocaleId=selectedLocaleID(data);
			String selectLocale = selectedLocaleName(selectLocaleId);
			// Do we have a flag for language selected?
			FileDTO dtoFlags = thing.getDocuments().get("flags");
			Map<Long, Long> flags = dtoFlags.getLinked();
			if(flags == null || flags.get(selectLocaleId) == null || flags.get(selectLocaleId) == 0) {
				data.setValid(false);
				data.setIdentifier(messages.get("upload_file"));
				dtoFlags.setValid(false);
				dtoFlags.setIdentifier(messages.get("upload_file") + " " + messages.get("flags") + " " + selectLocale);
			}
			// Do we have a data to import messages?
			FileDTO dto = thing.getDocuments().get("files");
			Map<Long, Long> files = dto.getLinked();
			if(files == null || files.get(selectLocaleId) == null || files.get(selectLocaleId) == 0) {
				data.setValid(false);
				data.setIdentifier(messages.get("upload_file"));
				dto.setValid(false);
				dto.setIdentifier(messages.get("upload_file") + " " + messages.get("files") + " " + selectLocale);
			}
			// this feature works only for two or one language defined
			Languages lang = messages.getLanguages();
			if(lang.getLangs().size()>2) {
				data.addError(messages.get("allowedonlytwolanguages"));
			}
			//en_US bundle must exists
			enUsBundle();
		}
		data.propagateValidation();
		return data;
	}
	/**
	 * Get mandatory English US resource bundle
	 * @return English US resource bundle
	 * @throws ObjectNotFoundException in this bundle is not exists 
	 */
	@Transactional
	public ResourceBundle enUsBundle() throws ObjectNotFoundException {
		Optional<ResourceBundle> bundlo = bundleRepo.findByLocale("en_US");
		if(bundlo.isPresent()) {
			return bundlo.get();
		}else {
			throw new ObjectNotFoundException("",logger);
		}
	}


	/**
	 * Run import
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 * @throws IOException
	 */
	@Transactional
	public ImportLocalesDTO importRun(ImportLocalesDTO data, UserDetailsDTO user) throws ObjectNotFoundException, IOException{
		ImportLanguageDTO logDTO = new ImportLanguageDTO();
		ThingDTO thing = data.getThing();
		Long selectLocaleId=selectedLocaleID(data);
		String selectLocale=selectedLocaleName(selectLocaleId);
		String selectLocaleName=selectedLocaleDisplayName(selectLocaleId);
		logDTO.setNewLanguage(selectLocaleName);
		thing = thingServ.saveUnderParent(thing, user);
		// Flag for the locale
		FileDTO dtoFlags = thing.getDocuments().get("flags");
		Long idFlag = dtoFlags.getLinked().get(selectLocaleId);
		Concept flagFileNode = closureServ.loadConceptById(idFlag);
		FileResource flagFile = boilerServ.fileResourceByNode(flagFileNode);
		String flagSVG = "";
		if(flagFile != null) {
			flagSVG = new String(flagFile.getFile());
		}
		String localeOld = "";
		// remove first not EN_US bundle
		Iterable<ResourceBundle> all = bundleRepo.findAll();
		for(ResourceBundle rb:all) {
			if(!rb.getLocale().equalsIgnoreCase("en_us")) {
				localeOld = rb.getLocale();
				logDTO.setOldLanguages(rb.getDisplayName());
				bundleRepo.delete(rb);
				break;
			}
		}
		// create a new bundle
		ResourceBundle enUs=enUsBundle();
		ResourceBundle newLocale = new ResourceBundle();
		newLocale.setSortOrder(!enUs.getSortOrder());
		newLocale.setDisplayName(selectLocaleName);
		newLocale.setLocale(selectLocale);
		newLocale.setSvgFlag(flagSVG);
		newLocale.setNmraLogo(enUs.getNmraLogo());
		newLocale = bundleRepo.save(newLocale);

		// get file with messages
		FileDTO dtoFiles = thing.getDocuments().get("files");
		Long idFile = dtoFiles.getLinked().get(selectLocaleId);
		Concept fileNode = closureServ.loadConceptById(idFile);
		FileResource file = boilerServ.fileResourceByNode(fileNode);

		if(file != null) {
			InputStream inputStream = new ByteArrayInputStream(file.getFile());
			XSSFWorkbook book = new XSSFWorkbook(inputStream);
			if(book != null){
				XSSFSheet sheet = boilerServ.getSheetAt(book, 0);
				importMessagesFromSheet(sheet, newLocale, logDTO);
			}
		}
		// update messages for removed locale
		if(localeOld.length() > 0) {
			String sqlUpdate = "UPDATE `concept` c SET `Identifier` = '" + selectLocale + "' WHERE upper(c.Identifier)='" + localeOld.toUpperCase() + "'";
			int res = jdbcRepo.update(sqlUpdate);
			logDTO.setUpdated(res);
		}
		logEvent.importLanguageEvent(logDTO, user);
		messages.getMessages().clear();
		messages.loadLanguages();
		return data;
	}

	/**
	 * Import messages
	 * @param sheet
	 * @param newLocale
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ImportLanguageDTO importMessagesFromSheet(XSSFSheet sheet, ResourceBundle newLocale,
			ImportLanguageDTO dto) throws ObjectNotFoundException {
		//prepare data as a map
		int rownum = 1;
		XSSFRow row = boilerServ.getSheetRow(sheet, rownum);
		int numColKey = 0;
		int numColValue = 2;
		Map<String, String> newValues = new HashMap<String, String>();
		ResourceBundle enUS=enUsBundle();
		List<ResourceMessage> enMess = messRepo.findByKeyBundle(new Long(enUS.getId()).intValue());
		while(row != null ) {
			String key = boilerServ.getStringCellValue(row, numColKey);
			String value = boilerServ.getStringCellValue(row, numColValue);
			if(key != null && key.length() > 0) {
				if(value !=null && value.length()>0) {
					newValues.put(key, value);
				}
			}
			rownum++;
			row = boilerServ.getSheetRow(sheet, rownum);
		}
		//insert messages into the bundle. Suppose en_us contains all
		dto.cleanUp();
		if(enMess != null && enMess.size() > 0) {
			for(ResourceMessage rm:enMess) {
				String key = rm.getMessage_key();
				String value = newValues.get(key);
				if(!(value != null && value.trim().length() > 0)) {
					value = rm.getMessage_value();
				}else {
					dto.setImported(dto.getImported()+1);
				}
				ResourceMessage newrm = new ResourceMessage();
				newrm.setMessage_key(key);
				newrm.setMessage_value(value);
				newrm = messRepo.save(newrm);
				newLocale.getMessages().add(newrm);
				dto.setTotalMessages(dto.getTotalMessages()+1);
			}
			bundleRepo.save(newLocale);
		}
		return dto;
	}
	/**
	 * Save it to the thing
	 * @param data
	 * @param user 
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws JsonProcessingException 
	 */
	public ImportLocalesDTO save(ImportLocalesDTO data, UserDetailsDTO user) throws JsonProcessingException, ObjectNotFoundException {
		data.setThing(thingServ.saveUnderParent(data.getThing(), user));
		return data;
	}
	/**
	 * Create table contains lost messages
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public TableQtb messagesLost() throws ObjectNotFoundException {
		TableQtb ret = new TableQtb();
		ret.getHeaders().getHeaders().add(TableHeader.instanceOf("key", messages.get("key"), 30, TableHeader.COLUMN_STRING));
		Languages langs = messages.getLanguages();
		for(Language lang :langs.getLangs()) {
			ret.getHeaders().getHeaders().add(TableHeader.instanceOf(lang.getLocaleAsString(), lang.getLocaleAsString(), 60, TableHeader.COLUMN_STRING));
		}
		Set<String> usedKeys = new LinkedHashSet<String>();
		//read all used by software
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/shablon/allmessages.out");
		try (BufferedReader br
				= new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = br.readLine()) != null) {
				usedKeys.add(line);
			}
		} catch (IOException e) {
			throw new ObjectNotFoundException("messagesLost - file allmessages.out not found", logger);
		}
		//read all variables
		String select ="SELECT distinct Identifier as 'key' \r\n" + 
				"FROM assembly\r\n" + 
				"join concept conc on conc.ID=conceptID\r\n" + 
				"where conc.Active";
		Headers headers = new Headers();
		headers.getHeaders().add(TableHeader.instanceOf("key", TableHeader.COLUMN_STRING));
		List<TableRow> vars = jdbcRepo.qtbGroupReport(select, "", "", headers);
		for(TableRow row : vars) {
			usedKeys.add(row.getRow().get(0).getValue().trim());
		}
		//exclude existing to get lost
		List<String> lost = new ArrayList<String>();
		for(String key : usedKeys) {
			if(messages.get(key).equals(key)) {
				lost.add(key);
			}
		}
		//sorting
		Collections.sort(lost);
		//create table rows
		long id=1;
		for(String key : lost) {
			TableRow row = TableRow.instanceOf(id);
			row.getRow().add(TableCell.instanceOf("key", key));
			for(Language lang :langs.getLangs()) {
				row.getRow().add(TableCell.instanceOf(lang.getLocaleAsString(), ""));
			}
			ret.getRows().add(row);
			id++;
		}
		return ret;
	}
}
