package org.msh.pharmadex2.service.r2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.FileResource;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.common.QueryRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.FileDTO;
import org.msh.pharmadex2.dto.LegacyDataErrorsDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.DtoService;
import org.msh.pharmadex2.service.common.EntityService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
//@Transactional
public class ImportAService {
	private static final Logger logger = LoggerFactory.getLogger(ImportAService.class);
	@Autowired
	ClosureService closureServ;
	@Autowired
	DtoService dtoServ;
	@Autowired
	LiteralService literalServ;
	@Autowired
	EntityService entityServ;
	@Autowired
	JdbcRepository jdbcRepo;
	@Autowired
	BoilerService boilerServ;
	@Autowired
	ValidationService validServ;
	@Autowired
	Messages messages;
	@Autowired
	AssemblyService assembServ;
	@Autowired
	QueryRepository queryRep;
	@Autowired
	private ThingService thingServ;
	@Autowired
	private LegacyDataService legacyDataService;
	@Autowired
	private SystemService systemService;

	private static String SHEET_NAME_COUNTRY = "country";
	private static String NAME_COUNTRY = "name_country_eng";
	private static String NAME_COUNTRY_NATIONAL = "name_country_national";
	private static String DESCR_COUNTRY = "description_country_eng";
	private static String DESCR_COUNTRY_NATIONAL = "description_country_national";

	private static String SN = "SN";
	private static String NAME_PROVINCE = "name_province_eng";
	private static String NAME_PROVINCE_NATIONAL = "name_province_national";
	private static String NAME_DISTRICT = "name_district_eng";
	private static String NAME_DISTRICT_NATIONAL = "name_district_national";
	private static String NAME_COMMUNITY = "name_community_eng";
	private static String NAME_COMMUNITY_NATIONAL = "name_community_national";
	private static String WEBSITE = "website";
	private static String EMAIL = "email";
	private static String X_COORDINATE = "x_coordinate";
	private static String Y_COORDINATE = "y_coordinate";

	private static int headerNumRow = 0;
	private static int startNumRow = 1;
	private int numSheetCountry = -1;
	private int countSheets = 0;

	private static String ZOOM_COUTRY = "8";
	private static String ZOOM_PROV = "13";
	private static String ZOOM_DISTR = "16";
	private static String ZOOM_COMM = "19";

	private static Map<String, Integer> listColumns = new HashMap<String, Integer>();
	static {
		listColumns.put(SN, -1);
		listColumns.put(NAME_PROVINCE, -1);
		listColumns.put(NAME_PROVINCE_NATIONAL, -1);
		listColumns.put(NAME_DISTRICT, -1);
		listColumns.put(NAME_DISTRICT_NATIONAL, -1);
		listColumns.put(NAME_COMMUNITY, -1);
		listColumns.put(NAME_COMMUNITY_NATIONAL, -1);
		listColumns.put(WEBSITE, -1);
		listColumns.put(EMAIL, -1);
		listColumns.put(X_COORDINATE, -1);
		listColumns.put(Y_COORDINATE, -1);
		//listColumns = Collections.unmodifiableMap(listColumns);
	}

	/**Список языков перепишем: первый в списке ОБЯЗАТЕЛЬНО язык по умолчанию (en_US) берем из org.msh.pdex2.i18n.Messages */
	private String[] langs = new String[2];
	private XSSFWorkbook book = null;

	/**
	 * prepare electronic form for import admin units
	 * @param data
	 * @param user 
	 * @throws ObjectNotFoundException 
	 */
	public ThingDTO importAdminunitsLoad(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		//load and save only one and only under the root of the tree
		data.setUrl(AssemblyService.SYSTEM_IMPORT_ADMINUNITS);
		Concept root = closureServ.loadRoot(data.getUrl());
		data.setParentId(root.getID());
		List<Concept> nodes = closureServ.loadLevel(root);
		if(nodes.size()>0) {
			data.setNodeId(nodes.get(0).getID());
		}
		if(data.getNodeId()==0) {
			data=thingServ.createThing(data, user);
			// показываем существующий словарь (если он есть)
		}else {
			root = closureServ.loadConceptById(data.getNodeId());
			String result = literalServ.readValue(AssemblyService.DATAIMPORT_RESULT, root);
			if(!(result.isEmpty() || result.startsWith("End"))) {
				data.setUrl(AssemblyService.SYSTEM_IMPORT_ADMINUNITS_RELOAD);
			}
			data=thingServ.loadThing(data, user);
			data.setValid(true);
			data.setUrl(AssemblyService.SYSTEM_IMPORT_ADMINUNITS);
		}
		return data;
	}
	
	public ThingDTO importAdminunitsReload(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		if(data.getNodeId() > 0) {
			Concept root = closureServ.loadConceptById(data.getNodeId());
			String result = literalServ.readValue(AssemblyService.DATAIMPORT_RESULT, root);
			if(!(result.isEmpty() || result.startsWith("End"))) {
				data.setUrl(AssemblyService.SYSTEM_IMPORT_ADMINUNITS_RELOAD);
			}
			
			data=thingServ.loadThing(data, user);
			data.setValid(true);
		}
		data.setUrl(AssemblyService.SYSTEM_IMPORT_ADMINUNITS);
		return data;
	}

	public ThingDTO importAdminunitsVerify(ThingDTO data) throws ObjectNotFoundException, JsonParseException, JsonMappingException, IOException {
		// проверяем файл на наличие обязательных листов, колонок и т.д.
		data.setValid(false);

		FileDTO dto = data.getDocuments().get(AssemblyService.DATAIMPORT_DATA);
		Concept fileNode = null;
		Iterator<Long> it = dto.getLinked().keySet().iterator();
		if(it.hasNext()) {
			Long dictNodeId = it.next();
			fileNode = closureServ.loadConceptById(dto.getLinked().get(dictNodeId));
		}

		if(fileNode != null) {
			FileResource fr = boilerServ.fileResourceByNode(fileNode);
			InputStream inputStream = new ByteArrayInputStream(fr.getFile());
			book = new XSSFWorkbook(inputStream);
			if(book != null){
				countSheets = book.getNumberOfSheets();
				boolean verCou=false;
				boolean verPro=false;
				String pr="";
				for(int i = 0 ; i < countSheets; i++) {//sh
					XSSFSheet sh = book.getSheetAt(i);
					if(sh.getSheetName().trim().toLowerCase().equals(SHEET_NAME_COUNTRY)) {
						XSSFRow r1=sh.getRow(0);
						verCou=verySheetCountry(r1);
					}else {
						XSSFRow r2=sh.getRow(0);
						verPro=verySheetProvince(r2);
						if(!verPro) {
							pr+=" "+sh.getSheetName();
						}
					}
					/*if(sh.getSheetName().trim().toLowerCase().equals(SHEET_NAME_COUNTRY)) {
						data.setValid(true);
						return data;
					}*/
				}//sh
				String err="";
				data.setValid(true);
				if(!verCou) {
					data.setValid(false);
					err=err+" Error sheet country.";}
				if(!pr.equals("")) {
					data.setValid(false);
					err=err+" Error sheets a provinces:"+pr;}
				data.setIdentifier(err);
			}
		}
		return data;
	}

	private boolean verySheetCountry(XSSFRow r1) {
		boolean ver=false;
		int c=0;
		for (Iterator<Cell> it1 = r1.cellIterator(); it1.hasNext(); ) {//r
			Cell cell = (Cell) it1.next();
			if(cell.getStringCellValue().equalsIgnoreCase(NAME_COUNTRY)) {
				c+=1; 
			}else if(cell.getStringCellValue().equalsIgnoreCase(NAME_COUNTRY_NATIONAL)) {
				c+=1; 
			}else if(cell.getStringCellValue().equalsIgnoreCase(DESCR_COUNTRY)) {
				c+=1;
			}else if(cell.getStringCellValue().equalsIgnoreCase(DESCR_COUNTRY_NATIONAL)) {
				c+=1;
			}else if(cell.getStringCellValue().equalsIgnoreCase(X_COORDINATE)) {
				c+=1;
			}else if(cell.getStringCellValue().equalsIgnoreCase(Y_COORDINATE)) {
				c+=1;
			}
			;
		}//r
		if(c==6) {
			ver=true;
		}
		return ver;
	}
	private boolean verySheetProvince(XSSFRow r1) {
		boolean ver=false;
		int c=0;
		for (Iterator<Cell> it1 = r1.cellIterator(); it1.hasNext(); ) {//r
			Cell cell = (Cell) it1.next();
			if(cell.getStringCellValue().equalsIgnoreCase(SN)) {
				c+=1;
			}else if(cell.getStringCellValue().equalsIgnoreCase(NAME_PROVINCE)) {
				c+=1;
			}else if(cell.getStringCellValue().equalsIgnoreCase(NAME_PROVINCE_NATIONAL)) {
				c+=1;
			}else if(cell.getStringCellValue().equalsIgnoreCase(NAME_DISTRICT)) {
				c+=1;
			}else if(cell.getStringCellValue().equalsIgnoreCase(NAME_DISTRICT_NATIONAL)) {
				c+=1;
			}else if(cell.getStringCellValue().equalsIgnoreCase(NAME_COMMUNITY)) {
				c+=1;
			}else if(cell.getStringCellValue().equalsIgnoreCase(NAME_COMMUNITY_NATIONAL)) {
				c+=1;
			}else if(cell.getStringCellValue().equalsIgnoreCase(X_COORDINATE)) {
				c+=1;
			}else if(cell.getStringCellValue().equalsIgnoreCase(Y_COORDINATE)) {
				c+=1;
			}
			;
		}//r
		if(c==9) {
			ver=true;
		}
		return ver;
	}

	/**
	 * словарь с идентификатором SystemService.DICTIONARY_ADMIN_UNITS перемещает в архив
	 * к идентификатору добавляет текущую дату
	 * 
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private Concept archiveDictionary() throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot(SystemService.DICTIONARY_ADMIN_UNITS);
		LocalDateTime currentDate = LocalDateTime.now();
		String d = currentDate.getYear() + "";
		d += (currentDate.getMonthValue() < 10?"0":"") + currentDate.getMonthValue();
		d += (currentDate.getDayOfMonth() < 10?"0":"") + currentDate.getDayOfMonth();

		String ident = root.getIdentifier() + d;
		root.setIdentifier(ident);
		root = closureServ.saveToTree(null, root);

		systemService.checkAddressDict();
		root = closureServ.loadRoot(SystemService.DICTIONARY_ADMIN_UNITS);

		return root;
	}

	@Async
	public void importAdminunitsRun(ThingDTO data) throws ObjectNotFoundException, IOException{
		String curLoc = LocaleContextHolder.getLocale().toString().toUpperCase();
		String defLoc = messages.getDefaultLocaleName().toUpperCase();
		boolean hasOtherLoc = true;
		for(String l:messages.getAllUsedUpperCase()) {
			if(l.equals(defLoc)) {
				langs[0] = l;
			}else {
				langs[1] = l;
			}
			if(l.equals(curLoc)) {
				hasOtherLoc = false;
			}
		}
		if(hasOtherLoc) {
			LocaleContextHolder.setDefaultLocale(messages.getCurrentLocale());
		}

		
		
		//Concept protocol=protocolConcept(AssemblyService.SYSTEM_IMPORT_ADMINUNITS);
		writeProtocol("Start import");

		Concept rootCountry = archiveDictionary();
		if(book != null){
			LegacyDataErrorsDTO errors= new LegacyDataErrorsDTO(book);

			//TODO надo обязательно считать первым лист SHEET_NAME_COUNTRY
			int sheetIndex = 0;
			XSSFSheet sheet = boilerServ.getSheetAt(book, sheetIndex);

			while(!errors.isErrorOrNullSheet(sheet)) {
				if(sheet.getSheetName().toLowerCase().equals(SHEET_NAME_COUNTRY)) {
					rootCountry = closureServ.loadRoot(SystemService.DICTIONARY_ADMIN_UNITS);

					rootCountry = importAdminunitsCountrySheet(sheet, errors, rootCountry);
				}
				sheetIndex++;
				sheet = boilerServ.getSheetAt(book,sheetIndex);
			}

			if(rootCountry != null) {
				// считываем остальные листы
				sheetIndex = 0;
				sheet = boilerServ.getSheetAt(book, sheetIndex);

				writeProtocol("Start import sheet - count  " + book.getNumberOfSheets());
				while(!errors.isErrorOrNullSheet(sheet)) {
					if(!sheet.getSheetName().toLowerCase().equals(SHEET_NAME_COUNTRY)) {
						importAdminunitsSheet(sheet, errors, rootCountry, sheetIndex);
					}
					sheetIndex++;
					sheet = boilerServ.getSheetAt(book,sheetIndex);
				}
			}else {
				writeProtocol("Not find sheet " + SHEET_NAME_COUNTRY);
			}
		}
		writeProtocol("End import");
	}

	/**public ThingDTO verifstatusImportA(ThingDTO data) throws ObjectNotFoundException {
		boolean stop = false;
		Concept protocol=legacyDataService.protocolConcept(AssemblyService.SYSTEM_IMPORT_ADMINUNITS);
		String val = literalServ.readValue(AssemblyService.DATAIMPORT_RESULT, protocol);
		if(val != null) {
			if(val.startsWith("end")) {
				stop = true;
			}
		}
		return stop;
	}**/

	/**
	 * Import data from the particular data sheet
	 * Import will start from the first row. Zero row assumed for headers
	 * @param sheet
	 * @param errors
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private Concept importAdminunitsCountrySheet(XSSFSheet sheet, LegacyDataErrorsDTO errors, Concept rootCntr) throws ObjectNotFoundException {
		String sheetName = legacyDataService.sheetName(sheet);
		int rownum = 1;
		XSSFRow row = boilerServ.getSheetRow(sheet, rownum);
		XSSFRow rowHdr = boilerServ.getSheetRow(sheet, 0);

		writeProtocol("Create Country " + sheetName);
		int threshold=0;			//when the data will finished?
		while(row !=null && threshold<300) { //see 300 invalid records before finish
			if(importAdminunitsCountrySheetRow(rowHdr, row, errors, rootCntr)) {
				threshold=0;
			}else {
				threshold++;
			}
			rownum++;
			row = boilerServ.getSheetRow(sheet,rownum);
		}
		//writeProtocol("End " + sheetName);

		return rootCntr;
	}

	@Transactional
	private void importAdminunitsSheet(XSSFSheet sheet, LegacyDataErrorsDTO errors, Concept rootCountry, int sheetIndex) throws ObjectNotFoundException {
		String url = legacyDataService.sheetName(sheet);
		int rownum = 1;
		XSSFRow row = boilerServ.getSheetRow(sheet, rownum);
		writeProtocol("Start import sheet " + url);

		Concept province = null;
		String prevDistrict = "";
		Concept district = null;
		int numDist = 1;

		// считываем номера колонок с данными
		boolean isfillColumns = fillInMap(sheet);
		if(isfillColumns) {
			int threshold = 0;			//when the data will finished?
			while(row != null && threshold < 300) {	//see 300 invalid records before finish
				// считаем данные со строки и проверим обязательные поля
				String sn = "";
				Double s = boilerServ.getNumberCellValue(row, listColumns.get(SN));
				if(s != null) {
					sn = String.valueOf(s.intValue());
				}else {
					sn = boilerServ.getStringCellValue(row, listColumns.get(SN));
				}
				String name = boilerServ.getStringCellValue(row, listColumns.get(NAME_PROVINCE));
				String nameLang = boilerServ.getStringCellValue(row, listColumns.get(NAME_PROVINCE_NATIONAL));
				String nameDistr = boilerServ.getStringCellValue(row, listColumns.get(NAME_DISTRICT));
				String nameDistrLang = boilerServ.getStringCellValue(row, listColumns.get(NAME_DISTRICT_NATIONAL));
				String nameCom = boilerServ.getStringCellValue(row, listColumns.get(NAME_COMMUNITY));
				String nameComLang = boilerServ.getStringCellValue(row, listColumns.get(NAME_COMMUNITY_NATIONAL));
				String website = boilerServ.getStringCellValue(row, listColumns.get(WEBSITE));
				String email = boilerServ.getStringCellValue(row, listColumns.get(EMAIL));
				String descr = (legacyDataService.validString(website)?(website + ", "):"") + (legacyDataService.validString(email)?email:"");

				// тут немного напутали
				Double coord = boilerServ.getNumberCellValue(row, listColumns.get(X_COORDINATE));
				String x = String.valueOf(coord);
				coord = boilerServ.getNumberCellValue(row, listColumns.get(Y_COORDINATE));
				String y = String.valueOf(coord);
				String location = (legacyDataService.validString(y)?(y + ";"):"") + (legacyDataService.validString(x)?x:"");

				if(legacyDataService.validString(sn) && legacyDataService.validString(name) && legacyDataService.validString(nameDistr) 
						&& legacyDataService.validString(nameCom) && url.equals(name)) {

					if(province == null) {
						province = createProvince(sheetIndex, name, nameLang, rootCountry);
					}

					boolean createNewDistr = !prevDistrict.equals(nameDistr);
					if(createNewDistr) {//TODO
						district = createDistrict(nameDistr, nameDistrLang, province, numDist);
						prevDistrict = nameDistr;
						numDist++;
					}
					createCommunity(sn, nameCom, nameComLang, descr, location, district, createNewDistr);

					threshold=0;
				}else if(legacyDataService.validString(name)) {
					errors.add(row, row.getLastCellNum(), url);
					threshold=0;
				}else {
					threshold++;
				}
				rownum++;
				row = boilerServ.getSheetRow(sheet, rownum);
			}
		}else {
			XSSFRow rowh = boilerServ.getSheetRow(sheet, 0);
			errors.add(rowh, rowh.getLastCellNum(), url);
		}
	}

	/**
	 * Import a row from the sheet or put this row to the "errors" sheet
	 * @param row the row
	 * @param errors the "errors" sheet
	 * @param root the root concept under which the data should be stored 
	 * @param root 
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private boolean importAdminunitsCountrySheetRow(XSSFRow rowHdr, XSSFRow row, LegacyDataErrorsDTO errors, Concept root) throws ObjectNotFoundException {
		// заполним мапу с данным для root страны
		Map<String, String> data = new HashMap<String, String>();
		for(int i = 0; i < rowHdr.getLastCellNum(); i++) {
			String headerCellValue = boilerServ.getStringCellValue(rowHdr, i);
			String value = "";
			if(headerCellValue.equals(NAME_COUNTRY)) {
				value = boilerServ.getStringCellValue(row, i);
				data.put(NAME_COUNTRY, value);
			}else if(headerCellValue.equals(NAME_COUNTRY_NATIONAL)) {
				value = boilerServ.getStringCellValue(row, i);
				data.put(NAME_COUNTRY_NATIONAL, value);
			}else if(headerCellValue.equals(DESCR_COUNTRY)) {
				value = boilerServ.getStringCellValue(row, i);
				data.put(DESCR_COUNTRY, value);
			}else if(headerCellValue.equals(DESCR_COUNTRY_NATIONAL)) {
				value = boilerServ.getStringCellValue(row, i);
				data.put(DESCR_COUNTRY_NATIONAL, value);
			}else if(headerCellValue.equals(X_COORDINATE)) {
				double d = boilerServ.getNumberCellValue(row, i);
				value = String.valueOf(d);
				data.put(X_COORDINATE, value);
			}else if(headerCellValue.equals(Y_COORDINATE)) {
				double d = boilerServ.getNumberCellValue(row, i);
				value = String.valueOf(d);
				data.put(Y_COORDINATE, value);
			}
		}
		// записываем считанные данные
		if(legacyDataService.validString(data.get(NAME_COUNTRY)) && legacyDataService.validString(data.get(X_COORDINATE)) && legacyDataService.validString(data.get(Y_COORDINATE))) {
			Map<String, String> values = new HashMap<String, String>();
			values.put(langs[0], data.get(NAME_COUNTRY));
			values.put(langs[1], data.get(NAME_COUNTRY_NATIONAL));
			root = literalServ.createUpdateLiteral(LiteralService.PREF_NAME, root, values);

			values = new HashMap<String, String>();
			values.put(langs[0], data.get(DESCR_COUNTRY));
			values.put(langs[1], data.get(DESCR_COUNTRY_NATIONAL));
			root = literalServ.createUpdateLiteral(LiteralService.DESCRIPTION, root, values);

			values = new HashMap<String, String>();
			String location = data.get(Y_COORDINATE);
			location += (!location.isEmpty()?";":"") + data.get(X_COORDINATE);
			values.put(langs[0], location);
			values.put(langs[1], location);
			root = literalServ.createUpdateLiteral(LiteralService.GIS_LOCATION, root, values);

			values = new HashMap<String, String>();
			values.put(langs[0], ZOOM_COUTRY);
			values.put(langs[1], ZOOM_COUTRY);
			root = literalServ.createUpdateLiteral(LiteralService.ZOMM, root, values);
		}

		if(legacyDataService.validString(data.get(NAME_COUNTRY))) {
			errors.add(row, rowHdr.getLastCellNum(), root.getIdentifier());
			return true;
		}else {
			return false; //waiting for threshold
		}

	}

	/**
	 * Создаем новую
	 * Есть полоное перемещение в корзину предыдущего адм.деления
	 * @param numProv
	 * @param nameProvince
	 * @param root
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private Concept createProvince(int numProv, String name, String nameNat, Concept root) throws ObjectNotFoundException {
		Concept province = new Concept();

		String identifier = "pr" + numProv;
		province.setIdentifier(identifier);
		province = closureServ.saveToTree(root, province);

		// создаем данные
		Map<String, String> values = new HashMap<String, String>();
		values.put(langs[0], name);
		values.put(langs[1], nameNat);
		root = literalServ.createUpdateLiteral(LiteralService.PREF_NAME, province, values);

		values = new HashMap<String, String>();
		values.put(langs[0], "");
		values.put(langs[1], "");
		root = literalServ.createUpdateLiteral(LiteralService.DESCRIPTION, province, values);

		values = new HashMap<String, String>();
		values.put(langs[0], "");
		values.put(langs[1], "");
		root = literalServ.createUpdateLiteral(LiteralService.GIS_LOCATION, province, values);

		values = new HashMap<String, String>();
		values.put(langs[0], ZOOM_PROV);
		values.put(langs[1], ZOOM_PROV);
		root = literalServ.createUpdateLiteral(LiteralService.ZOMM, province, values);
		return province;
	}

	@Transactional
	private Concept createDistrict(String n, String nLang, Concept prov, int num) throws ObjectNotFoundException {
		Concept district = new Concept();

		String identifier = prov.getIdentifier() + "." + num;
		district.setIdentifier(identifier);
		district = closureServ.saveToTree(prov, district);

		// создаем или обновляем данные
		Map<String, String> values = new HashMap<String, String>();
		if(n != null && !n.isEmpty() && (nLang == null  || nLang.isEmpty())) {
			values.put(langs[0], n);
			values.put(langs[1], n);
		}else if((n == null || n.isEmpty()) && nLang != null  && !nLang.isEmpty()) {
			values.put(langs[0], nLang);
			values.put(langs[1], nLang);
		}else {
			values.put(langs[0], n);
			values.put(langs[1], nLang);
		}
		district = literalServ.createUpdateLiteral(LiteralService.PREF_NAME, district, values);

		values = new HashMap<String, String>();
		values.put(langs[0], "");
		values.put(langs[1], "");
		district = literalServ.createUpdateLiteral(LiteralService.DESCRIPTION, district, values);

		values = new HashMap<String, String>();
		values.put(langs[0], "");
		values.put(langs[1], "");
		district = literalServ.createUpdateLiteral(LiteralService.GIS_LOCATION, district, values);

		values = new HashMap<String, String>();
		values.put(langs[0], ZOOM_DISTR);
		values.put(langs[1], ZOOM_DISTR);
		district = literalServ.createUpdateLiteral(LiteralService.ZOMM, district, values);

		return district;
	}

	@Transactional
	private void createCommunity(String sn, String n, String nLang, String descr, String loc, Concept dist, boolean updateLocationDistrict) throws ObjectNotFoundException {
		Concept community = new Concept();
		community.setIdentifier(sn);
		community = closureServ.saveToTree(dist, community);

		// создаем или обновляем данные
		Map<String, String> values = new HashMap<String, String>();
		if(n != null && !n.isEmpty() && (nLang == null  || nLang.isEmpty())) {
			values.put(langs[0], n);
			values.put(langs[1], n);
		}else if((n == null || n.isEmpty()) && nLang != null  && !nLang.isEmpty()) {
			values.put(langs[0], nLang);
			values.put(langs[1], nLang);
		}else {
			values.put(langs[0], n);
			values.put(langs[1], nLang);
		}
		community = literalServ.createUpdateLiteral(LiteralService.PREF_NAME, community, values);

		values = new HashMap<String, String>();
		values.put(langs[0], descr);
		values.put(langs[1], descr);
		community = literalServ.createUpdateLiteral(LiteralService.DESCRIPTION, community, values);

		values = new HashMap<String, String>();
		values.put(langs[0], loc);
		values.put(langs[1], loc);
		community = literalServ.createUpdateLiteral(LiteralService.GIS_LOCATION, community, values);

		if(updateLocationDistrict) {
			// установим для dist центром координаты первой palika в списке
			dist = literalServ.createUpdateLiteral(LiteralService.GIS_LOCATION, dist, values);
		}

		values = new HashMap<String, String>();
		values.put(langs[0], ZOOM_COMM);
		values.put(langs[1], ZOOM_COMM);
		community = literalServ.createUpdateLiteral(LiteralService.ZOMM, community, values);
	}

	/**
	 * заполним карту с названиями колонок их номерами для удобства работы
	 * для каждого листа карту заполняем заново
	 * перед началом заполнения сбрасываем старые значения
	 * если хоть одно значение не заполненно, данные считывать не будем
	 * 
	 * @param colname
	 * @param row
	 * @return
	 */
	private boolean fillInMap(XSSFSheet sheet) {
		boolean isfill = false;
		Set<String> keys = listColumns.keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()) {
			listColumns.put(it.next(), -1);
		}

		int ind = 0;
		XSSFRow row = sheet.getRow(headerNumRow);
		XSSFCell cell = null;
		for(int i = 0; i < row.getLastCellNum(); i++) {
			cell = row.getCell(i);
			if(cell != null) {
				String cellVal = cell.getStringCellValue();
				if(cellVal != null && cellVal.length() > 0) {
					keys = listColumns.keySet();
					it = keys.iterator();
					while(it.hasNext()) {
						String val = it.next();
						if(val.equals(cellVal.trim())){
							listColumns.put(val, i);
							ind++;
						}
					}
				}
			}
		}

		if(ind == listColumns.keySet().size()) {
			isfill = true;
		}
		return isfill;
	}

	@Transactional
	public Concept protocolConcept(String protocolUrl) throws ObjectNotFoundException {
		Concept root=closureServ.loadRoot(protocolUrl);
		List<Concept> nodes = closureServ.loadLevel(root);
		if(nodes.size()>0) {
			return nodes.get(0);
		}else {
			Concept node = new Concept();
			node=closureServ.save(node);
			node.setIdentifier(node.getID()+"");
			node = closureServ.saveToTree(root, node);
			return node;
		}
	}

	@Transactional
	public void writeProtocol(String val) throws ObjectNotFoundException {
		val += ". Date " + getCurrentDate();
		Concept protocol=protocolConcept(AssemblyService.SYSTEM_IMPORT_ADMINUNITS);
		protocol = literalServ.createUpdateLiteral(AssemblyService.DATAIMPORT_RESULT, val, protocol);
		logger.info(val);
	}

	private String getCurrentDate() {
		LocalDateTime currentDate = LocalDateTime.now();
		String d = (currentDate.getDayOfMonth() < 10?"0":"") + currentDate.getDayOfMonth();
		d += "/" + (currentDate.getMonthValue() < 10?"0":"") + currentDate.getMonthValue();
		d += "/" + currentDate.getYear();
		
		d += " " + currentDate.getHour() + ":" + currentDate.getMinute() + ":" + currentDate.getSecond();
		return d;
	}
	
}
