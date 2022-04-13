package org.msh.pharmadex2.service.r2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.common.QueryRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.DtoService;
import org.msh.pharmadex2.service.common.EntityService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class ImportFromExcel {
	private static final Logger logger = LoggerFactory.getLogger(ImportFromExcel.class);
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

	public void importAdmUnit(byte[] bytes) throws JsonParseException, JsonMappingException, IOException, ObjectNotFoundException {
		for(String l:messages.getAllUsedUpperCase()) {
			if(l.equals(messages.getDefaultLocaleName().toUpperCase())) {
				langs[0] = l;
			}else {
				langs[1] = l;
			}
		}
		XSSFWorkbook workbook = getXSSFWorkbook(bytes);
		if(workbook != null){
			int countSheets = workbook.getNumberOfSheets();
			// найдем лист с названием SHEET_NAME_COUNTRY (вдруг не первым разместили)
			XSSFSheet sheetCountry = null;
			int numShCountry = -1;
			for(int i = 0 ; i < countSheets; i++) {
				XSSFSheet sh = workbook.getSheetAt(i);
				if(sh.getSheetName().trim().toLowerCase().equals(SHEET_NAME_COUNTRY)) {
					sheetCountry = sh;
					numShCountry = i;
					break;
				}
			}

			// создадим корень дерева - считав данные с листа SHEET_NAME_COUNTRY
			Concept root = loadSheetContry(sheetCountry);

			for(int i = 0 ; i < countSheets; i++) {
				if(i != numShCountry) {
					loadSheet(i, workbook, root);
				}
			}
		}


	}

	public XSSFWorkbook getXSSFWorkbook(byte[] bytes) {
		XSSFWorkbook w = null;
		if(bytes.length > 0){			
			try {				
				InputStream inputStream = new ByteArrayInputStream(bytes);
				w = new XSSFWorkbook(inputStream);
			} catch (IOException e) {				
				e.printStackTrace();
			}			
		}

		return w;
	}

	public Concept loadSheetContry(XSSFSheet sheetCountry) throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot(SystemService.DICTIONARY_ADMIN_UNITS);

		Map<String, String> data = new HashMap<String, String>();
		XSSFRow row = sheetCountry.getRow(headerNumRow);
		XSSFRow rowData = sheetCountry.getRow(startNumRow);
		for(int i = 0; i < row.getLastCellNum(); i++) {
			XSSFCell cell = row.getCell(i);
			XSSFCell cellData = rowData.getCell(i);

			String hname = cell.getStringCellValue();
			if(hname.equals(NAME_COUNTRY)) {
				data.put(NAME_COUNTRY, cellData.getStringCellValue());
			}else if(hname.equals(NAME_COUNTRY_NATIONAL)) {
				data.put(NAME_COUNTRY_NATIONAL, cellData.getStringCellValue());
			}else if(hname.equals(DESCR_COUNTRY)) {
				data.put(DESCR_COUNTRY, cellData.getStringCellValue());
			}else if(hname.equals(DESCR_COUNTRY_NATIONAL)) {
				data.put(DESCR_COUNTRY_NATIONAL, cellData.getStringCellValue());
			}else if(hname.equals(X_COORDINATE)) {
				data.put(X_COORDINATE, String.valueOf(cellData.getNumericCellValue()));
			}else if(hname.equals(Y_COORDINATE)) {
				data.put(Y_COORDINATE, String.valueOf(cellData.getNumericCellValue()));
			}
		}
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

		return root;
	}

	/**
	 * Считываем лист
	 * 
	 * название Провинции берем из колонок
	 * всеравно проверяем название Провинций, если встречаются разные то разными и записываем
	 * 
	 * @param num
	 * @param wb
	 * @param root
	 * @throws ObjectNotFoundException
	 */
	public void loadSheet(int num, XSSFWorkbook wb, Concept root) throws ObjectNotFoundException {
		XSSFSheet sheet = null;
		sheet = wb.getSheetAt(num);
		boolean isfillColumns = fillInMap(sheet);

		if(isfillColumns) {// мапа с номерами колонок заполнена 
			String[] namesProvince = getNameProvinceAndVerify(sheet);
			if(namesProvince[0].length() > 0) {

				String prevDistrict = "";
				Concept district = null;
				int numDist = 1;

				Concept province = createProvince(num, namesProvince, root);
				for (int i = startNumRow; i <= sheet.getLastRowNum(); i++) {
					XSSFRow row = sheet.getRow(i);
					XSSFCell cellProv = row.getCell(listColumns.get(NAME_PROVINCE));
					// В каждой строке проверяем чтоб провинция не изменилась.Если изменилась - не вносим
					String prov = getCellValue(cellProv);
					if(prov.equals(namesProvince[0])) {
						// теперь район
						XSSFCell cellDistr = row.getCell(listColumns.get(NAME_DISTRICT));
						String distr = getCellValue(cellDistr);
						if(!distr.isEmpty()) {
							boolean createNewDistr = !prevDistrict.equals(distr);
							if(createNewDistr) {// район изменился
								district = createDistrict(row, province, numDist);
								prevDistrict = distr;
								numDist++;
							}
							createCommunity(row, district, createNewDistr);
						}
					}
				}
			}
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
	private Concept createProvince(int numProv, String[] namesProv, Concept root) throws ObjectNotFoundException {
		Concept province = new Concept();

		String identifier = "pr" + numProv;
		province.setIdentifier(identifier);
		province = closureServ.saveToTree(root, province);

		// создаем данные
		Map<String, String> values = new HashMap<String, String>();
		values.put(langs[0], namesProv[0]);
		values.put(langs[1], namesProv[1]);
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

	private Concept createDistrict(XSSFRow row, Concept prov, int num) throws ObjectNotFoundException {
		Concept district = new Concept();

		String identifier = prov.getIdentifier() + "." + num;
		district.setIdentifier(identifier);
		district = closureServ.saveToTree(prov, district);

		// создаем или обновляем данные
		Map<String, String> values = new HashMap<String, String>();
		XSSFCell cell = row.getCell(listColumns.get(NAME_DISTRICT));
		String nameDistr = getCellValue(cell);
		cell = row.getCell(listColumns.get(NAME_DISTRICT_NATIONAL));
		String nameDistrLang = getCellValue(cell);
		values.put(langs[0], nameDistr);
		values.put(langs[1], nameDistrLang);
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

	private Concept createCommunity(XSSFRow row, Concept dist, boolean updateLocationDistrict) throws ObjectNotFoundException {
		Concept community = new Concept();

		XSSFCell cell = row.getCell(listColumns.get(SN));
		if(cell != null) {
			String sn = "";
			if(cell.getCellType().equals(CellType.NUMERIC)) {
				double d = cell.getNumericCellValue();
				Integer num = (int)Math.round(d);
				sn = String.valueOf(num);
			}else if(cell.getCellType().equals(CellType.STRING)) {
				sn = getCellValue(cell);
			}
			if(!sn.isEmpty()) {
				community.setIdentifier(sn);
				community = closureServ.saveToTree(dist, community);

				// создаем или обновляем данные
				Map<String, String> values = new HashMap<String, String>();
				cell = row.getCell(listColumns.get(NAME_COMMUNITY));
				String nameCom = getCellValue(cell);
				cell = row.getCell(listColumns.get(NAME_COMMUNITY_NATIONAL));
				String nameComLang = getCellValue(cell);
				values.put(langs[0], nameCom);
				values.put(langs[1], nameComLang);
				community = literalServ.createUpdateLiteral(LiteralService.PREF_NAME, community, values);

				values = new HashMap<String, String>();
				cell = row.getCell(listColumns.get(WEBSITE));
				String ws = getCellValue(cell);
				cell = row.getCell(listColumns.get(EMAIL));
				String em = getCellValue(cell);
				String descr = ws;
				descr += (!descr.isEmpty()?", ":"") + em;
				values.put(langs[0], descr);
				values.put(langs[1], descr);
				community = literalServ.createUpdateLiteral(LiteralService.DESCRIPTION, community, values);

				values = new HashMap<String, String>();
				cell = row.getCell(listColumns.get(Y_COORDINATE));
				String x = String.valueOf(cell.getNumericCellValue());
				cell = row.getCell(listColumns.get(X_COORDINATE));
				String y = String.valueOf(cell.getNumericCellValue());
				String location = x;
				location += (!location.isEmpty()?";":"") + y;
				values.put(langs[0], location);
				values.put(langs[1], location);
				community = literalServ.createUpdateLiteral(LiteralService.GIS_LOCATION, community, values);

				if(updateLocationDistrict) {
					// установим для dist центром координаты первой palika в списке
					dist = literalServ.createUpdateLiteral(LiteralService.GIS_LOCATION, dist, values);
				}

				values = new HashMap<String, String>();
				values.put(langs[0], ZOOM_COMM);
				values.put(langs[1], ZOOM_COMM);
				community = literalServ.createUpdateLiteral(LiteralService.ZOMM, community, values);

				return community;
			}
		}

		return null;
	}

	/**
	 * проверяем совпадение названия листа и названия провинции в ячейке(проверяем одну ячейку)
	 * Если совпадает, будем считывать данные
	 */
	private String[] getNameProvinceAndVerify(XSSFSheet sheet) {
		String[] names = new String[2];
		names[0] = "";
		names[1] = "";
		String sheetName = sheet.getSheetName().trim();

		XSSFRow row = sheet.getRow(startNumRow);
		XSSFCell cellProv = row.getCell(listColumns.get(NAME_PROVINCE));
		String provName = getCellValue(cellProv);

		if(sheetName.equals(provName)) {
			names[0] = provName;
			names[1] = getCellValue(row.getCell(listColumns.get(NAME_PROVINCE_NATIONAL)));
		}

		return names;
	}

	/**
	 * получаем текстовое значение из ячейки
	 * если null возвращаем пустую строку
	 */
	private String getCellValue(XSSFCell cell) {
		String val = "";
		if(cell != null) {
			val = cell.getStringCellValue();
			if(val != null) {
				val = val.trim();
			}
		}
		return val;
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

}
