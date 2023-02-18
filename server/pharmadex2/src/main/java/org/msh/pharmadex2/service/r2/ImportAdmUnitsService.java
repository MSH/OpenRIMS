package org.msh.pharmadex2.service.r2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
public class ImportAdmUnitsService {

	private static final Logger logger = LoggerFactory.getLogger(ImportAdmUnitsService.class);
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

	/*private static String marker_COUNTRY = "country";
	private static String marker_ENG = "Eng";
	private static String marker_NATIONAL = "National";
	private static String marker_NAME = "name";
	private static String marker_DESCRIPTION = "description";
	private static String marker_COORDINATE_X = "coordinateX";
	private static String marker_COORDINATE_Y = "coordinateY";*/

	/**Список языков перепишем: первый в списке ОБЯЗАТЕЛЬНО язык по умолчанию (en_US) берем из org.msh.pdex2.i18n.Messages */
	private String[] langs = new String[2];
	private XSSFWorkbook book = null;
	private Concept fileNode = null;
	private FileResource fr = null;
	private int countSheets = 0;
	private static int headerNumRow = 0;

	private static int DEFAULT_ZOOM_COUTRY = 7;

	private static String SHEET_NAME_COUNTRY = "country";
	private static String NAME_COUNTRY = "name_country_eng";
	private static String NAME_COUNTRY_NATIONAL = "name_country_national";
	private static String DESCR_COUNTRY = "description_country_eng";
	private static String DESCR_COUNTRY_NATIONAL = "description_country_national";
	private static String ZOOM_COUNTRY = "zoom_country";
	private static String ZOOM_PROVINCE = "zoom_province";
	private static String ZOOM_DISTRICT = "zoom_district";
	private static String ZOOM_COMMUNITY = "zoom_community";
	private static String ZOOM_COMMUNA = "zoom_communa";
	private static Map<String, Integer> zooms_index = new HashMap<String, Integer>(); 

	private static String SN = "SN";
	private static String NAME_PROVINCE = "name_province_eng";
	private static String NAME_PROVINCE_NATIONAL = "name_province_national";
	private static String NAME_DISTRICT = "name_district_eng";
	private static String NAME_DISTRICT_NATIONAL = "name_district_national";
	private static String NAME_COMMUNITY = "name_community_eng";
	private static String NAME_COMMUNITY_NATIONAL = "name_community_national";
	private static String NAME_COMUNA = "name_communa_eng";
	private static String NAME_COMUNA_NATIONAL = "name_communa_national";
	private static String WEBSITE = "website";
	private static String EMAIL = "email";
	private static String X_COORDINATE = "x_coordinate";
	private static String Y_COORDINATE = "y_coordinate";

	/*private Integer[] column_names = {1, 3, 5, 7};
	private Integer[] column_namesNational = {2, 4, 6, 8};
	private Integer[] column_other = {0, 9, 10, 11, 12};*/
	/*private Integer[] column_names = {1, 3};
	private Integer[] column_namesNational = {2, 4};
	private Integer[] column_other = {0, 5, 6, 7, 8};*/
	private Integer[] column_names = null;
	private Integer[] column_namesNational = null;
	private Integer[] column_other = null;
	private String[] zooms = null;

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
		}else {
			/*root = closureServ.loadConceptById(data.getNodeId());
			String result = literalServ.readValue(AssemblyService.DATAIMPORT_RESULT, root);
			if(!(result.isEmpty() || result.startsWith("End"))) {
				data.setUrl(AssemblyService.SYSTEM_IMPORT_ADMINUNITS_RELOAD);
			}*/
			String sstm = getSystemProtocol();
			if(!(sstm.equals("") || sstm.equals("END"))) {
				data.setUrl(AssemblyService.SYSTEM_IMPORT_ADMINUNITS_RELOAD);
			}
			data=thingServ.loadThing(data, user);
			data.setValid(true);
			data.setUrl(AssemblyService.SYSTEM_IMPORT_ADMINUNITS);
		}
		return data;
	}

	@Transactional
	public ThingDTO importAdminunitsReload(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		if(data.getNodeId() > 0) {
			/*Concept root = closureServ.loadConceptById(data.getNodeId());
			String result = literalServ.readValue(AssemblyService.DATAIMPORT_RESULT, root);
			if(!(result.isEmpty() || result.startsWith("End"))) {
				data.setUrl(AssemblyService.SYSTEM_IMPORT_ADMINUNITS_RELOAD);
			}*/
			String sstm = getSystemProtocol();
			if(!(sstm.equals("") || sstm.equals("END"))) {
				data.setUrl(AssemblyService.SYSTEM_IMPORT_ADMINUNITS_RELOAD);
			}

			data=thingServ.loadThing(data, user);
			data.setValid(true);
		}
		data.setUrl(AssemblyService.SYSTEM_IMPORT_ADMINUNITS);
		return data;
	}

	/**
	 * проверка файла для импорта
	 * проверяем наличие первого листа с названием "country"
	 * проверяем наличие нужніх колонок на листах
	 */
	public ThingDTO importAdminunitsVerify(ThingDTO data) throws ObjectNotFoundException, JsonParseException, JsonMappingException, IOException {
		// проверяем файл на наличие обязательных листов, колонок и т.д.
		data.setValid(false);

		setDefaultValues();
		writeProtocol("Start verify file");

		FileDTO dto = data.getDocuments().get(AssemblyService.DATAIMPORT_DATA);

		Iterator<Long> it = dto.getLinked().keySet().iterator();
		if(it.hasNext()) {
			Long dictNodeId = it.next();
			fileNode = closureServ.loadConceptById(dto.getLinked().get(dictNodeId));
		}

		if(fileNode != null) {
			fr = boilerServ.fileResourceByNode(fileNode);
			InputStream inputStream = new ByteArrayInputStream(fr.getFile());
			book = new XSSFWorkbook(inputStream);
			if(book != null){
				countSheets = book.getNumberOfSheets();
				boolean verCountryNum = true;
				boolean verCou = false;
				boolean verPro = false;
				String pr = "";

				// лист COUNTRY должен біть первім!!!!!
				XSSFSheet sh = book.getSheetAt(0);
				if(sh.getSheetName().trim().toLowerCase().equals(SHEET_NAME_COUNTRY)) {
					XSSFRow r1 = sh.getRow(0);
					verCou = verySheetCountry(r1);

					for(int i = 1; i < countSheets; i++) {//sh
						sh = book.getSheetAt(i);
						XSSFRow r2 = sh.getRow(0);
						verPro = verySheetProvince(r2);
						if(!verPro) {
							pr += " " + sh.getSheetName();
						}
					}
				}else {
					verCountryNum = false;
				}

				String err = "";
				data.setValid(true);
				if(!verCountryNum) {
					data.setValid(false);
					err = err + " Error sheet country. Sheet COUNTRY must be first!";
				}
				if(!verCou) {
					data.setValid(false);
					err=err+" Error sheet country.";
				}
				if(!pr.equals("")) {
					data.setValid(false);
					err=err+" Error sheets a provinces:"+pr;
				}
				if(err == "") {
					writeProtocol("End verify file. OK!");
					// load configuration
					/*if(!loadConfigurationByImport()) {
						writeProtocol("Error load configuration!");
					}*/
				}else {
					writeProtocol(err);
				}
				data.setIdentifier(err);
			}
		}
		return data;
	}

	/**
	 * verification Sheet Country
	 * Sheet must contain 10 columns
	 */
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
			}else if(cell.getStringCellValue().equalsIgnoreCase(ZOOM_COUNTRY)) {
				c+=1;
			}else if(cell.getStringCellValue().equalsIgnoreCase(ZOOM_PROVINCE)) {
				c+=1;
			}else if(cell.getStringCellValue().equalsIgnoreCase(ZOOM_DISTRICT)) {
				c+=1;
			}else if(cell.getStringCellValue().equalsIgnoreCase(ZOOM_COMMUNITY)) {
				c+=1;
			}
		}//r
		if(c == 10) {
			ver = true;
		}
		return ver;
	}

	/**
	 * verification Sheet of any province
	 * Sheet must contain 7 or 9 or 11 columns
	 */
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
			}else if(cell.getStringCellValue().equalsIgnoreCase(NAME_COMUNA)) {
				c+=1;
			}else if(cell.getStringCellValue().equalsIgnoreCase(NAME_COMUNA_NATIONAL)) {
				c+=1;
			}else if(cell.getStringCellValue().equalsIgnoreCase(X_COORDINATE)) {
				c+=1;
			}else if(cell.getStringCellValue().equalsIgnoreCase(Y_COORDINATE)) {
				c+=1;
			}
			;
		}//r
		if(c == 11 || c==9 || c==7) {
			ver = true;
		}
		return ver;
	}

	/**
	 * устанавливаем разные значения по умолчанию
	 * или заполняем какие то масивы перед началом самого импорта
	 */
	public void setDefaultValues() {
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
		
		// ля удобства и понимания последовательности зумов
		zooms_index.put(ZOOM_COUNTRY, 0);
		zooms_index.put(ZOOM_PROVINCE, 1);
		zooms_index.put(ZOOM_DISTRICT, 2);
		zooms_index.put(ZOOM_COMMUNITY, 3);
		zooms_index.put(ZOOM_COMMUNA, 4);
	}

	/**
	 * Заполним масив значений zoom
	 * в карте или считанное значение или по-умолчанию
	 * @param data
	 * @param countZoom
	 */
	private void setDefaultZooms(Map<String, String> data, int countZoom) {
		// заполним значениями масив zooms
		zooms = new String[countZoom];
		Iterator<String> it = data.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			Integer index = zooms_index.get(key);
			if(index != null && index >= 0) {
				zooms[index] = data.get(key);
			}
		}
	}
	
	//ik 27082022
	private int countColRow(XSSFRow r1, String str) {
		int count=0;
		String s1=NAME_PROVINCE.replaceAll("_eng", "");
		String s2=NAME_DISTRICT.replaceAll("_eng", "");
		String s3=NAME_COMMUNITY.replaceAll("_eng", "");
		String s4=NAME_COMUNA.replaceAll("_eng", "");
		for (Iterator<Cell> it1 = r1.cellIterator(); it1.hasNext(); ) {
			Cell cell = (Cell) it1.next();
			if(cell.getStringCellValue().equalsIgnoreCase(s1+str)) {
				count+=1;
			} if(cell.getStringCellValue().equalsIgnoreCase(s2+str)) {
				count+=1;
			}
			if (cell.getStringCellValue().equalsIgnoreCase(s3+str)) {
				count+=1;
			}if(cell.getStringCellValue().equalsIgnoreCase(s4+str)) {
				count+=1;
			}
		}
		return count;
	}

	/**
	 *заполняем рабочие масивы номерами колонок
	 *
	 */
	private void setNumbersRow(XSSFSheet sheet) {
		//column_names = new Integer[4];
		//column_namesNational = new Integer[4];
		column_other = new Integer[5];

		int indexNames = 0, indexNamesNat = 0, indexOther = 0;

		XSSFRow row = sheet.getRow(headerNumRow);
		//ik 27082022
		int col=0;
		col=countColRow(row,"_eng");
		column_names = new Integer[col];
		col=countColRow(row,"_NATIONAL");
		column_namesNational = new Integer[col];
		//ik

		XSSFCell cell = null;
		for(int i = 0; i < row.getLastCellNum(); i++) {
			cell = row.getCell(i);
			if(cell != null) {
				String cellVal = cell.getStringCellValue();
				if(cellVal != null && cellVal.length() > 0) {
					if(cellVal.toLowerCase().startsWith("name_") && !cellVal.toLowerCase().endsWith("_national")) {
						column_names[indexNames] = i;
						indexNames++;
					}else if(cellVal.toLowerCase().startsWith("name_") && cellVal.toLowerCase().endsWith("_national")) {
						column_namesNational[indexNamesNat] = i;
						indexNamesNat++;
					}else {
						column_other[indexOther] = i;
						indexOther++;
					}
				}
			}
		}

		/* сравниваем размеры масивов с zooms и именами, 
		 * чтоб не было ошибок по индексам 
		 * в zooms всегда на 1 больше, так как есть для страны*/
		if(zooms.length < (column_names.length + 1)) {
			String[] copy = new String[column_names.length + 1];
			for(int i = 0; i < copy.length; i++) {
				if(i < zooms.length) {
					copy[i] = zooms[i];
				}else {
					copy[i] = zooms[zooms.length - 1];
				}
			}
			zooms = copy;
		}
		// заполним значениями масив zooms
		/*zooms = new String[column_names.length];
		if(column_names.length >= 2) {
			zooms[0] = "13";
			zooms[1] = "16";
			for(int i = 2; i < column_names.length; i++) {
				zooms[i] = "19";
			}
		}*/
	}

	/**
	 * return true - load configuration ok
	 * return false - error by load configuration
	 */
	/*public boolean loadConfigurationByImport() throws ObjectNotFoundException {
		Concept config = assembServ.loadDataConfigurationsAdmUnits();
		List<Concept> child = literalServ.loadOnlyChilds(config);
		if(child != null && child.size() > 0) {
			/* получим все записи - отсортируем по order
	 * все которые с самым маленьким относятся к стране
	 * 
	 * 
	 */
	/*return true;
		}else {
			return false;
		}
	}*/

	@Async
	public void importAdminunitsRun(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException, IOException{
		setDefaultValues();

		writeProtocol(messages.get("startImport"));
		writeSystemProtocol("START");

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
				sheetIndex++; // 1
				sheet = boilerServ.getSheetAt(book,sheetIndex);
			}

			if(rootCountry != null) {
				sheetIndex = 1;
				// считываем остальные листы
				sheet = boilerServ.getSheetAt(book, sheetIndex);
				setNumbersRow(sheet);

				while(!errors.isErrorOrNullSheet(sheet)) {
					if(!sheet.getSheetName().toLowerCase().equals(SHEET_NAME_COUNTRY)) {
						importAdminunitsSheet(sheet, errors, rootCountry, sheetIndex);
					}
					sheetIndex++;
					sheet = boilerServ.getSheetAt(book, sheetIndex);
				}
			}else {
				writeProtocol(messages.get("notFindSheet") + " " + SHEET_NAME_COUNTRY);
			}

			if(errors.hasErrorRows()) {
				String fnameErr = "Error.xlsx";
				String nfile = fileNode.getLabel();
				if(nfile.endsWith(".xlsx")) {
					fnameErr = nfile.replace(".xlsx", ".xlsxOut.xlsx");
				}

				File fError = new File(fnameErr);
				FileOutputStream fos = new FileOutputStream(fError);
				book.write(fos);
				fos.flush();
				fos.close();

				FileDTO dto = data.getDocuments().get(AssemblyService.DATAIMPORT_DATA);
				FileDTO fdto = new FileDTO();
				fdto.setThingNodeId(dto.getThingNodeId());
				fdto.setThingUrl(dto.getThingUrl());
				fdto.setDictNodeId(loadDictConcept(true).getID());
				fdto.setDictUrl(dto.getDictUrl());
				fdto.setUrl(dto.getUrl());
				fdto.setVarName(dto.getVarName());
				fdto.setFileName(fError.getName());
				fdto.setFileSize(fError.length());
				fdto.setMediaType(fr.getMediatype());
				thingServ.fileSave(fdto, user, Files.readAllBytes(fError.toPath()));
			}
		}
		writeProtocol(messages.get("endImport"));
		writeSystemProtocol("END");
	}

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
	public void importAdminunitsSheet(XSSFSheet sheet, LegacyDataErrorsDTO errors, Concept rootCountry, int sheetIndex) throws ObjectNotFoundException {
		String url = legacyDataService.sheetName(sheet);
		int rownum = 1;
		XSSFRow row = boilerServ.getSheetRow(sheet, rownum);
		writeProtocol(messages.get("startImportSheet") + " " + url);

		Map<Integer, List<ItemConcept>> levelMap = new HashMap<Integer, List<ItemConcept>>();
		for(int i = 0; i < column_names.length - 1; i++) {// для последнего уровня не добавляем (могут быть повторы в полном обьеме)
			levelMap.put(i, new ArrayList<ItemConcept>());
		}

		int threshold = 0;			//when the data will finished?

		/** масив предыдущих значений - для понимания когда создавать новый */
		ItemConcept[] prevMass = new ItemConcept[column_names.length];
		/** масив обьектов второго уровня - первый это первый элемент prevMass и на одном листе он всегда один */
		List<ItemConcept> itemsTwoLevel = new ArrayList<ItemConcept>();

		boolean firstRow = true;
		while(row != null && threshold < 300) {	//see 300 invalid records before finish

			DataRowItem dto = new DataRowItem(row);
			if(dto.validItem()) {
				if(firstRow) {
					List<ItemConcept> its = new ArrayList<ItemConcept>();
					for(int i = 0; i < column_names.length; i++) {
						ItemConcept it = new ItemConcept();
						it.name = dto.names[i];
						it.index = i;
						it.data = dto;
						its.add(it);
						if(i > 0) {
							its.get(i - 1).children.add(it);
						}
						prevMass[i] = it;
						if(i == 1) {
							itemsTwoLevel.add(it);
						}
					}
					firstRow = false;
				}

				List<ItemConcept> its = new ArrayList<ItemConcept>();
				for(int i = 0; i < column_names.length; i++) {
					if(prevMass[i].name.equals(dto.names[i])) {
						its.add(prevMass[i]);
					}else {
						ItemConcept it = new ItemConcept();
						it.name = dto.names[i];
						it.index = i;
						it.data = dto;
						its.add(it);
						if(i > 0) {
							its.get(i - 1).children.add(it);
						}
						prevMass[i] = it;
						if(i == 1) {
							itemsTwoLevel.add(it);
						}
					}
				}
			}else if(legacyDataService.validString(dto.names[0])) {
				errors.add(row, row.getLastCellNum(), url);
				threshold=0;
			}else {
				threshold++;
			}

			rownum++;
			row = boilerServ.getSheetRow(sheet, rownum);
		}

		// теперь создадим Concept
		if(itemsTwoLevel != null && itemsTwoLevel.size() > 0) {
			// для провинции - она одна на одном листе - возьмем ее из prevMass
			Concept province = createChild(prevMass[0], rootCountry, sheetIndex);
			for(int i = 0; i < itemsTwoLevel.size(); i++) {
				ItemConcept item = itemsTwoLevel.get(i);
				Concept concept = createChild(item, province, i + 1);
				List<ItemConcept> children = item.children;
				createRecursionConcept(children, concept);
			}
		}
	}

	private void createRecursionConcept(List<ItemConcept> children, Concept parConcept) throws ObjectNotFoundException {
		if(children != null && children.size() > 0) {
			for(int j = 0; j < children.size(); j++) {
				ItemConcept it = children.get(j);
				Concept c = null;
				if(it.data.SN != null && it.data.SN.length() > 0) {// this is last item, where SN=25 (example)
					c = createLastChild(it, parConcept);
				}else {
					c = createChild(it, parConcept, j + 1);
				}
				if(c != null) {
					if(j == 0) {
						String location = literalServ.readValue(LiteralService.GIS_LOCATION, c);

						Map<String, String> values = new HashMap<String, String>();
						values.put(langs[0], location);
						values.put(langs[1], location);
						parConcept = literalServ.createUpdateLiteral(LiteralService.GIS_LOCATION, parConcept, values);
					}
					List<ItemConcept> child = it.children;
					createRecursionConcept(child, c);
				}
			}
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
		int countZoom = 0;
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
			}else if(headerCellValue.equals(ZOOM_COUNTRY)) {
				value = loadZoomCell(row, i);
				if(value != null && !value.isEmpty()) {
					data.put(ZOOM_COUNTRY, value);
				}else {
					data.put(ZOOM_COUNTRY, String.valueOf(DEFAULT_ZOOM_COUTRY));
				}
				countZoom++;
			}else if(headerCellValue.equals(ZOOM_PROVINCE)) {
				value = loadZoomCell(row, i);
				if(value != null && !value.isEmpty()) {
					data.put(ZOOM_PROVINCE, value);
				}else {
					data.put(ZOOM_PROVINCE, String.valueOf(DEFAULT_ZOOM_COUTRY + 1));
				}
				countZoom++;
			}else if(headerCellValue.equals(ZOOM_DISTRICT)) {
				value = loadZoomCell(row, i);
				if(value != null && !value.isEmpty()) {
					data.put(ZOOM_DISTRICT, value);
				}else {
					data.put(ZOOM_DISTRICT, String.valueOf(DEFAULT_ZOOM_COUTRY + 2));
				}
				countZoom++;
			}else if(headerCellValue.equals(ZOOM_COMMUNITY)) {
				value = loadZoomCell(row, i);
				if(value != null && !value.isEmpty()) {
					data.put(ZOOM_COMMUNITY, value);
				}else {
					data.put(ZOOM_COMMUNITY, String.valueOf(DEFAULT_ZOOM_COUTRY + 3));
				}
				countZoom++;
			}else if(headerCellValue.equals(ZOOM_COMMUNA)) {
				value = loadZoomCell(row, i);
				if(value != null && !value.isEmpty()) {
					data.put(ZOOM_COMMUNA, value);
				}else {
					data.put(ZOOM_COMMUNA, String.valueOf(DEFAULT_ZOOM_COUTRY + 4));
				}
				countZoom++;
			}
		}
		setDefaultZooms(data, countZoom);
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
			String location = buildLocationString(data.get(X_COORDINATE), data.get(Y_COORDINATE));
			values.put(langs[0], location);
			values.put(langs[1], location);
			root = literalServ.createUpdateLiteral(LiteralService.GIS_LOCATION, root, values);

			values = new HashMap<String, String>();
			values.put(langs[0], zooms[0]);
			values.put(langs[1], zooms[0]);
			root = literalServ.createUpdateLiteral(LiteralService.ZOMM, root, values);
			
		}

		if(legacyDataService.validString(data.get(NAME_COUNTRY))) {
			return true;
		}else {
			errors.add(row, rowHdr.getLastCellNum(), root.getIdentifier());
			return false; //waiting for threshold
		}
	}

	@Transactional
	private Concept createChild(ItemConcept item, Concept parentConcept, int nn) throws ObjectNotFoundException {
		Concept concept = new Concept();
		DataRowItem dataRow = item.data;
		String identif = "";
		if(item.index == 0) {
			identif = "pr" + "." + nn;
		}else {
			identif = parentConcept.getIdentifier() + "." + nn;
		}
		concept.setIdentifier(identif);
		concept = closureServ.saveToTree(parentConcept, concept);

		//строки на англ языке уже валидированные - пустых имен точно нет! Пустые на нац языке заполнены англ названиями
		// создаем или обновляем данные
		Map<String, String> values = new HashMap<String, String>();
		values.put(langs[0], dataRow.names[item.index]);
		values.put(langs[1], dataRow.namesNational[item.index]);
		concept = literalServ.createUpdateLiteral(LiteralService.PREF_NAME, concept, values);

		values = new HashMap<String, String>();
		values.put(langs[0], "");
		values.put(langs[1], "");
		concept = literalServ.createUpdateLiteral(LiteralService.DESCRIPTION, concept, values);

		values = new HashMap<String, String>();
		values.put(langs[0], "");
		values.put(langs[1], "");
		concept = literalServ.createUpdateLiteral(LiteralService.GIS_LOCATION, concept, values);

		values = new HashMap<String, String>();
		values.put(langs[0], zooms[item.index + 1]);
		values.put(langs[1], zooms[item.index + 1]);
		concept = literalServ.createUpdateLiteral(LiteralService.ZOMM, concept, values);
		return concept;
	}

	@Transactional
	private Concept createLastChild(ItemConcept item, Concept parentConcept) throws ObjectNotFoundException {
		Concept child = new Concept();
		DataRowItem dataRow = item.data;

		child.setIdentifier(dataRow.SN);
		child = closureServ.saveToTree(parentConcept, child);

		//строки на англ языке уже валидированные - пустых имен точно нет! Пустые на нац языке заполнены англ названиями
		// создаем или обновляем данные
		Map<String, String> values = new HashMap<String, String>();
		values.put(langs[0], dataRow.names[item.index]);
		values.put(langs[1], dataRow.namesNational[item.index]);
		child = literalServ.createUpdateLiteral(LiteralService.PREF_NAME, child, values);

		values = new HashMap<String, String>();
		values.put(langs[0], dataRow.description);
		values.put(langs[1], dataRow.description);
		child = literalServ.createUpdateLiteral(LiteralService.DESCRIPTION, child, values);

		values = new HashMap<String, String>();
		values.put(langs[0], dataRow.coordinates);
		values.put(langs[1], dataRow.coordinates);
		child = literalServ.createUpdateLiteral(LiteralService.GIS_LOCATION, child, values);

		values = new HashMap<String, String>();
		values.put(langs[0], zooms[item.index + 1]);
		values.put(langs[1], zooms[item.index + 1]);
		child = literalServ.createUpdateLiteral(LiteralService.ZOMM, child, values);
		return child;
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
		Map<String, String> values = new HashMap<String, String>();
		values.put(langs[0], val);
		values.put(langs[1], val);
		protocol = literalServ.createUpdateLiteral(AssemblyService.DATAIMPORT_RESULT, protocol, values);
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
	@Transactional
	public Concept archiveDictionary() throws ObjectNotFoundException {
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

	private Concept loadDictConcept(boolean byError) throws ObjectNotFoundException {
		Concept itemDict = null;
		Concept root = closureServ.loadRoot(SystemService.DICTIONARY_SYSTEM_IMPORT_DATA);
		List<Concept> level = literalServ.loadOnlyChilds(root);
		if(level.size() == 2) {
			for(int i = 0; i < level.size(); i++) {
				Concept item = level.get(i);
				if(byError && item.getIdentifier().equals(AssemblyService.DATAIMPORT_DATA_ERROR)) {
					itemDict = item;
					break;
				}else if(!byError && item.getIdentifier().equals(AssemblyService.DATAIMPORT_DATA)) {
					itemDict = item;
					break;
				}
			}

		}

		return itemDict;
	}

	private class ItemConcept {
		String name = "";
		int index = -1;// это по сути номер имени (Province-0, District-1, Community - 2, Comuna -3)
		List<ItemConcept> children = new ArrayList<ItemConcept>();
		DataRowItem data = null;
	}

	/**
	 * Вспомогательный класс, куда считываем все данные со строки, чтоб уже не возвращаться к экселю
	 * @author khome
	 *
	 */
	private class DataRowItem{
		public String SN = "";
		public String[] names;
		public String[] namesNational;
		public String description = "";
		public String coordinates = "";

		public DataRowItem() {
			super();
		}

		public DataRowItem(XSSFRow row) {
			SN = boilerServ.getStringCellValue(row, column_other[0]);
			String website = boilerServ.getStringCellValue(row, column_other[1]);
			String email = boilerServ.getStringCellValue(row, column_other[2]);
			description = (legacyDataService.validString(website)?(website + ", "):"") + (legacyDataService.validString(email)?email:"");
			
			Double coord = boilerServ.getNumberCellValue(row, column_other[3]);
			String x = String.valueOf(coord);
			coord = boilerServ.getNumberCellValue(row, column_other[4]);
			String y = String.valueOf(coord);
			coordinates = buildLocationString(x, y);
			
			names = new String[column_names.length];
			for(int i = 0; i < column_names.length; i++) {
				//if(column_names[i]!=null) {
				names[i] = boilerServ.getStringCellValue(row, column_names[i]);
				//}
			}
			namesNational = new String[column_namesNational.length];
			for(int i = 0; i < column_namesNational.length; i++) {
				//if(column_namesNational[i]!=null) {
				namesNational[i] = boilerServ.getStringCellValue(row, column_namesNational[i]);
				//}
			}
			// названия на нац.языке заполним англ версией, если национальные есть пустые
			for(int i = 0; i < column_namesNational.length; i++) {
				if(namesNational[i] == null || namesNational[i].isEmpty()) {
					namesNational[i] = names[i];
				}
			}
		}

		public boolean validItem() {
			int i = 0;
			for(String n:names) {
				if(legacyDataService.validString(n)) {
					i++;
				}
			}

			if(legacyDataService.validString(SN) && i == names.length) {
				return true;
			}
			return false;
		}
	}

	/**
	 * System Literal 
	 * monitors import status
	 * START, END, ERROR
	 */
	@Transactional
	public void writeSystemProtocol(String val) throws ObjectNotFoundException {
		Concept protocol = protocolConcept(AssemblyService.SYSTEM_IMPORT_ADMINUNITS);
		// write protocol text on two language
		Map<String, String> values = new HashMap<String, String>();
		values.put(langs[0], val);
		values.put(langs[1], val);
		protocol = literalServ.createUpdateLiteral(AssemblyService.DATAIMPORT_SYSTEM_RESULT, protocol, values);
	}

	private String getSystemProtocol() throws ObjectNotFoundException {
		Concept protocol = protocolConcept(AssemblyService.SYSTEM_IMPORT_ADMINUNITS);
		String result = literalServ.readValue(AssemblyService.DATAIMPORT_SYSTEM_RESULT, protocol);
		return result;
	}

	private String loadZoomCell(XSSFRow row, int i) {
		Double d = boilerServ.getNumberCellValue(row, i);
		if(d != null) {
			int zoom = d.intValue();
			if(zoom > 0) {
				return String.valueOf(zoom);
			}
		}
		return null;
	}
	
	private String buildLocationString(String x, String y) {
		String value = "";
		x = x.replace(",", ".");
		y = y.replace(",", ".");
		
		value = (legacyDataService.validString(x)?(x + ","):"") + (legacyDataService.validString(y)?y:"");
		return value;
	}
}
