package org.msh.pharmadex2.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.r2.DictService;
import org.msh.pharmadex2.service.r2.LiteralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.i18n.LocaleContextHolder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Import Data for Dictionary admin unit from .xlsx file
 * 
 * province: identifier = "pr" + номер по порядку провинции;
 * district: identifier = identifier Province + "." + номер по порядку внутри провинции
 * municipality: identifier = колонка SN из файла импорта (номер по порядку)
 * 
 * @author dudchenko
 *
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ImportAdminUnitXLSX {
	@Autowired
	DictService dictServ;
	@Autowired
	ClosureService closureServ;
	@Autowired
	BoilerService boilerServ; 
	@Autowired
	JdbcRepository jdbcRepo;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private ObjectMapper objectMapper;

	private static String DICTIONARY_URL_IN_DB = "dictionary.admin.units";
	private static String DICTIONARY_URL = "dictionary.admin.units";
	private static String excelFileName = "Nepal-GIS coordinate.xlsx";
	/** количество листов, на которых данные по провинциям */
	private static int countSheets = 7;
	
	private static int num_col_sn = 0;
	private static Map<String, Integer> colsnumberProvince1 = null;
    static {
    	colsnumberProvince1 = new HashMap<String, Integer>();
    	colsnumberProvince1.put("namedist", 2);
    	colsnumberProvince1.put("namedist_ne", 3);
    	colsnumberProvince1.put("namepalika", 4);
    	colsnumberProvince1.put("namepalika_ne", 5);
    	colsnumberProvince1.put("website", 12);
    	colsnumberProvince1.put("email", 13);
    	colsnumberProvince1.put("coord_x", 19);
    	colsnumberProvince1.put("coord_y", 20);
    	colsnumberProvince1 = Collections.unmodifiableMap(colsnumberProvince1);
    }
    private static Map<String, Integer> colsnumberProvince2 = null;
    static {
    	colsnumberProvince2 = new HashMap<String, Integer>();
    	colsnumberProvince2.put("namedist", 2);
    	colsnumberProvince2.put("namedist_ne", 3);
    	colsnumberProvince2.put("namepalika", 4);
    	colsnumberProvince2.put("namepalika_ne", 5);
    	colsnumberProvince2.put("website", 12);
    	colsnumberProvince2.put("email", 13);
    	colsnumberProvince2.put("coord_x", 20);
    	colsnumberProvince2.put("coord_y", 21);
    	colsnumberProvince2 = Collections.unmodifiableMap(colsnumberProvince2);
    }
    private static Map<String, Integer> colsnumberProvince3 = null;
    static {
    	colsnumberProvince3 = new HashMap<String, Integer>();
    	colsnumberProvince3.put("namedist", 2);
    	colsnumberProvince3.put("namedist_ne", 3);
    	colsnumberProvince3.put("namepalika", 4);
    	colsnumberProvince3.put("namepalika_ne", 5);
    	colsnumberProvince3.put("website", 13);
    	colsnumberProvince3.put("email", 14);
    	colsnumberProvince3.put("coord_x", 21);
    	colsnumberProvince3.put("coord_y", 22);
    	colsnumberProvince3 = Collections.unmodifiableMap(colsnumberProvince3);
    }
    private static Map<String, Integer> colsnumberProvince4 = null;
    static {
    	colsnumberProvince4 = new HashMap<String, Integer>();
    	colsnumberProvince4.put("namedist", 2);
    	colsnumberProvince4.put("namedist_ne", 3);
    	colsnumberProvince4.put("namepalika", 4);
    	colsnumberProvince4.put("namepalika_ne", 5);
    	colsnumberProvince4.put("website", 11);
    	colsnumberProvince4.put("email", 12);
    	colsnumberProvince4.put("coord_x", 19);
    	colsnumberProvince4.put("coord_y", 20);
    	colsnumberProvince4 = Collections.unmodifiableMap(colsnumberProvince4);
    }
    private static Map<String, Integer> colsnumberProvince5 = null;
    static {
    	colsnumberProvince5 = new HashMap<String, Integer>();
    	colsnumberProvince5.put("namedist", 2);
    	colsnumberProvince5.put("namedist_ne", 3);
    	colsnumberProvince5.put("namepalika", 4);
    	colsnumberProvince5.put("namepalika_ne", 5);
    	colsnumberProvince5.put("website", 12);
    	colsnumberProvince5.put("email", 13);
    	colsnumberProvince5.put("coord_x", 20);
    	colsnumberProvince5.put("coord_y", 21);
    	colsnumberProvince5 = Collections.unmodifiableMap(colsnumberProvince5);
    }
    private static Map<String, Integer> colsnumberProvince6 = null;
    static {
    	colsnumberProvince6 = new HashMap<String, Integer>();
    	colsnumberProvince6.put("namedist", 3);
    	colsnumberProvince6.put("namedist_ne", 4);
    	colsnumberProvince6.put("namepalika", 5);
    	colsnumberProvince6.put("namepalika_ne", 6);
    	colsnumberProvince6.put("website", 13);
    	colsnumberProvince6.put("email", 14);
    	colsnumberProvince6.put("coord_x", 21);
    	colsnumberProvince6.put("coord_y", 22);
    	colsnumberProvince6 = Collections.unmodifiableMap(colsnumberProvince6);
    }
    private static Map<String, Integer> colsnumberProvince7 = null;
    static {
    	colsnumberProvince7 = new HashMap<String, Integer>();
    	colsnumberProvince7.put("namedist", 2);
    	colsnumberProvince7.put("namedist_ne", 3);
    	colsnumberProvince7.put("namepalika", 4);
    	colsnumberProvince7.put("namepalika_ne", 5);
    	colsnumberProvince7.put("website", 12);
    	colsnumberProvince7.put("email", 13);
    	colsnumberProvince7.put("coord_x", 20);
    	colsnumberProvince7.put("coord_y", 21);
    	colsnumberProvince7 = Collections.unmodifiableMap(colsnumberProvince7);
    }
    
    private static Map<Integer, Map<String, Integer>> colsnumberFull = null;
    static {
    	colsnumberFull = new HashMap<Integer, Map<String, Integer>>();
    	colsnumberFull.put(1, colsnumberProvince1);
    	colsnumberFull.put(2, colsnumberProvince2);
    	colsnumberFull.put(3, colsnumberProvince3);
    	colsnumberFull.put(4, colsnumberProvince4);
    	colsnumberFull.put(5, colsnumberProvince5);
    	colsnumberFull.put(6, colsnumberProvince6);
    	colsnumberFull.put(7, colsnumberProvince7);
    	colsnumberFull = Collections.unmodifiableMap(colsnumberFull);
    }
	
	private static int num_start_row = 1;
	
	private static String[] langs = new String[] {
			"EN_US", "PT"
	};
	
	private static List<String> namesProvinces = null;
    static {
    	namesProvinces = new ArrayList<String>();
    	namesProvinces.add("Province 1");
    	namesProvinces.add("Province 2");
    	namesProvinces.add("Province 3");//old Bagmati Province
    	namesProvinces.add("Province 4");//old Gandaki Province
    	namesProvinces.add("Province 5");//old Lumbini Province
    	namesProvinces.add("Province 6");//old Karnali Province
    	namesProvinces.add("Province 7");//old Sudurpaschim Province
    	//namesProvinces = Collections.unmodifiableList(namesProvinces);
    }
    
	private static Map<String, String> centersProvinces = null;
    static {
        centersProvinces = new HashMap<String, String>();
        centersProvinces.put(namesProvinces.get(0), "26.451577;87.272012");
        centersProvinces.put(namesProvinces.get(1), "26.725294;85.937035");
        centersProvinces.put(namesProvinces.get(2), "27.703430;85.318418");//old Bagmati Province
        centersProvinces.put(namesProvinces.get(3), "28.208735;83.985706");//old Gandaki Province
        centersProvinces.put(namesProvinces.get(4), "28.057470;82.486182");//old Lumbini Province
        centersProvinces.put(namesProvinces.get(5), "28.576041;81.624855");//old Karnali Province
        centersProvinces.put(namesProvinces.get(6), "28.908744;80.506798");//old Sudurpaschim Province
        //centersProvinces = Collections.unmodifiableMap(centersProvinces);
    }
    
    private Map<String, Long> olddata = new LinkedHashMap<String, Long>();
	
	//@Test
	public void importAdmUnit() throws JsonParseException, JsonMappingException, IOException, ObjectNotFoundException {
		//removeAllRranch();
		changeIdentifireConcepts();
		
		Path pathFile = Paths.get("src","test","resources", excelFileName);
		byte[] bytes = Files.readAllBytes(pathFile);
		
		Locale def = new Locale("en", "US");
		LocaleContextHolder.setDefaultLocale(def);
		LocaleContextHolder.setLocale(def);
		
		if(bytes.length > 0) {
			XSSFWorkbook workbook = getXSSFWorkbook(bytes);
			if(workbook != null){
				Concept root = closureServ.loadRoot(DICTIONARY_URL);
				Map<String, String> values = new HashMap<String, String>();
				values.put(langs[0], "Nepal");
				values.put(langs[1], "नेपाल");
				root = literalServ.createUpdateLiteral(LiteralService.PREF_NAME, root, values);

				values = new HashMap<String, String>();
				values.put(langs[0], "Administrative division of Nepal");
				values.put(langs[1], "Administrative division of Nepal");
				root = literalServ.createUpdateLiteral(LiteralService.DESCRIPTION, root, values);
				
				values = new HashMap<String, String>();
				values.put(langs[0], "28.169245;84.119994");
				values.put(langs[1], "28.169245;84.119994");
				root = literalServ.createUpdateLiteral(LiteralService.GIS_LOCATION, root, values);
				
				values = new HashMap<String, String>();
				values.put(langs[0], "8");
				values.put(langs[1], "8");
				root = literalServ.createUpdateLiteral(LiteralService.ZOMM, root, values);
				
				olddata = loadOldData();
				for(int i = 0 ; i < countSheets; i++) {
					loadSheet(i, workbook, root, colsnumberFull.get(i + 1));
				}
			}
		}
		
	}

	public void loadSheet(int num, XSSFWorkbook wb, Concept root, Map<String, Integer> colsnumber) throws ObjectNotFoundException {
		XSSFSheet sheet = null;
		sheet = wb.getSheetAt(num);
		String nameProvince = namesProvinces.get(num);//sheet.getSheetName().substring(0, 10);
		System.out.println(nameProvince);

		Concept province = createProvince(nameProvince, root);
		
		String prevdistrict = "";
		Concept district = null;
		int numDist = 1;
		for (int i = num_start_row; i <= sheet.getLastRowNum(); i++) {
			XSSFRow row = sheet.getRow(i);
			XSSFCell cell = row.getCell(colsnumber.get("namedist"));
			if(cell != null) {
				String val = cell.getStringCellValue();
				if(!val.trim().isEmpty()) {
					boolean createNewDistr = !prevdistrict.equals(val);
					if(createNewDistr) {// район изменился
						district = createDistrict(row, province, colsnumber, numDist);
						prevdistrict = val;
						numDist++;
					}
					createPalika(row, district, createNewDistr, colsnumber);
				}
			}
		}
		
	}
	
	private Concept createProvince(String nameProvince, Concept root) throws ObjectNotFoundException {
		Concept province = new Concept();
		
		String identifier = "pr" + (namesProvinces.indexOf(nameProvince) + 1);
		Long id = olddata.get(identifier);
		if(id != null && id > 0) {
			province = closureServ.loadConceptById(id);
		}else {
			province.setIdentifier(identifier);
		}
		province = closureServ.saveToTree(root, province);
		
		// создаем или обновляем данные
		Map<String, String> values = new HashMap<String, String>();
		values.put(langs[0], nameProvince);
		values.put(langs[1], nameProvince);
		root = literalServ.createUpdateLiteral(LiteralService.PREF_NAME, province, values);

		values = new HashMap<String, String>();
		values.put(langs[0], "");
		values.put(langs[1], "");
		root = literalServ.createUpdateLiteral(LiteralService.DESCRIPTION, province, values);
		
		values = new HashMap<String, String>();
		String location = centersProvinces.get(nameProvince);
		values.put(langs[0], location);
		values.put(langs[1], location);
		root = literalServ.createUpdateLiteral(LiteralService.GIS_LOCATION, province, values);
		
		values = new HashMap<String, String>();
		values.put(langs[0], "13");
		values.put(langs[1], "13");
		root = literalServ.createUpdateLiteral(LiteralService.ZOMM, province, values);
		return province;
	}
	
	private Concept createDistrict(XSSFRow row, Concept prov, Map<String, Integer> colsnumber, int num) throws ObjectNotFoundException {
		Concept district = new Concept();
		
		String identifier = prov.getIdentifier() + "." + num;
		Long id = olddata.get(identifier);
		if(id != null && id > 0) {
			district = closureServ.loadConceptById(id);
		}else {
			district.setIdentifier(identifier);
		}
		district = closureServ.saveToTree(prov, district);
		
		// создаем или обновляем данные
		Map<String, String> values = new HashMap<String, String>();
		XSSFCell cell = row.getCell(colsnumber.get("namedist"));
		String val = cell.getStringCellValue();
		values.put(langs[0], val.trim());
		cell = row.getCell(colsnumber.get("namedist_ne"));
		val = cell.getStringCellValue();
		values.put(langs[1], val.trim());
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
		values.put(langs[0], "16");
		values.put(langs[1], "16");
		district = literalServ.createUpdateLiteral(LiteralService.ZOMM, district, values);
		
		return district;
	}
	
	private Concept createPalika(XSSFRow row, Concept dist, boolean updateLocationDistrict, Map<String, Integer> colsnumber) throws ObjectNotFoundException {
		Concept palika = new Concept();
		
		XSSFCell cell = row.getCell(num_col_sn);
		double d = cell.getNumericCellValue();
		String identifier = String.valueOf((int)d);
		
		Long id = olddata.get(identifier);
		if(id != null && id > 0) {
			palika = closureServ.loadConceptById(id);
		}else {
			palika.setIdentifier(identifier);
		}
		palika = closureServ.saveToTree(dist, palika);
		
		// создаем или обновляем данные
		Map<String, String> values = new HashMap<String, String>();
		cell = row.getCell(colsnumber.get("namepalika"));
		String val = cell.getStringCellValue();
		values.put(langs[0], val.trim());
		cell = row.getCell(colsnumber.get("namepalika_ne"));
		val = cell.getStringCellValue();
		values.put(langs[1], val.trim());
		palika = literalServ.createUpdateLiteral(LiteralService.PREF_NAME, palika, values);

		values = new HashMap<String, String>();
		cell = row.getCell(colsnumber.get("website"));
		val = cell.getStringCellValue().trim();
		String descr = val;
		cell = row.getCell(colsnumber.get("email"));
		val = cell.getStringCellValue().trim();
		descr += (!descr.isEmpty()?", ":"") + val;
		values.put(langs[0], descr);
		values.put(langs[1], descr);
		palika = literalServ.createUpdateLiteral(LiteralService.DESCRIPTION, palika, values);
		
		values = new HashMap<String, String>();
		cell = row.getCell(colsnumber.get("coord_y"));
		val = String.valueOf(cell.getNumericCellValue());
		String location = val;
		cell = row.getCell(colsnumber.get("coord_x"));
		val = String.valueOf(cell.getNumericCellValue());
		location += (!location.isEmpty()?";":"") + val;
		values.put(langs[0], location);
		values.put(langs[1], location);
		palika = literalServ.createUpdateLiteral(LiteralService.GIS_LOCATION, palika, values);
		
		if(updateLocationDistrict) {
			// установим для dist центром координаты первой palika в списке
			dist = literalServ.createUpdateLiteral(LiteralService.GIS_LOCATION, dist, values);
		}
		
		values = new HashMap<String, String>();
		values.put(langs[0], "19");
		values.put(langs[1], "19");
		palika = literalServ.createUpdateLiteral(LiteralService.ZOMM, palika, values);
		
		return palika;
	}
	
	/**
	 * из БД получаем уже существующие админ юниты
	 * и по ним строим мапу Identifier - Id concept
	 * запоминаем, что б каждый раз не обходить весь словарь (он увеличивается в процесе работы импорта)
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private Map<String, Long> loadOldData() throws ObjectNotFoundException {
		Map<String, Long> map = new HashMap<String, Long>();
		Concept root = closureServ.loadRoot(DICTIONARY_URL_IN_DB);
		loadChilds(root, map);
		
		return map;
	}
	
	private void loadChilds(Concept par, Map<String, Long> map) throws ObjectNotFoundException {
		List<Concept> list = literalServ.loadOnlyChilds(par);
		for(Concept c:list) {
			String iden = c.getIdentifier();
			map.put(iden, c.getID());
			loadChilds(c, map);
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
    
	//@Test
	public void changeIdentifireConcepts() throws ObjectNotFoundException {
		Map<Long, String> map = new LinkedHashMap<Long, String>();
		map.put(new Long(3434), "pr3");//Bagmati Province
		map.put(new Long(3458), "pr3.1");//Bhaktapur
		map.put(new Long(3606), "274");//Bhaktapur Municipality
		map.put(new Long(7357), "275");//Changunarayan Municipality
		map.put(new Long(7371), "277");//Suryabinayak Municipality
		map.put(new Long(7385), "276");//Thimi Municipality
		map.put(new Long(3466), "pr3.6");//Kathmandu
		map.put(new Long(3474), "pr3.5");//Kavrepalanchowk
		map.put(new Long(7411), "pr3.7");//Lalitpur
		map.put(new Long(7425), "pr3.9");//Nuwakot
		map.put(new Long(7439), "pr3.13");//Sindhupalchowk
		map.put(new Long(3442), "pr4");//Gandaki Province
		map.put(new Long(3450), "pr2");//Janakpur Province
		
		Iterator<Long> it = map.keySet().iterator();
		while(it.hasNext()) {
			Long id = it.next();
			Concept c = closureServ.loadConceptById(id);
			if(c != null) {
				c.setIdentifier(map.get(id));
				closureServ.save(c);
			}
		}
	}
	
	private void removeAllRranch() throws ObjectNotFoundException {
		List<Long> list = new ArrayList<Long>();
		list.add(new Long(19033));
		list.add(new Long(19036));
		list.add(new Long(19064));
		list.add(new Long(19067));
		list.add(new Long(19070));
		list.add(new Long(19073));
		list.add(new Long(19076));
		list.add(new Long(19107));
		list.add(new Long(19132));
		list.add(new Long(19135));
		list.add(new Long(19193));
		list.add(new Long(19196));
		list.add(new Long(19247));
		list.add(new Long(19251));
		list.add(new Long(19264));
		list.add(new Long(19267));
		list.add(new Long(19272));
		list.add(new Long(19277));
		list.add(new Long(19282));
		list.add(new Long(19287));
		list.add(new Long(19292));
		list.add(new Long(19293));
		list.add(new Long(19304));
		list.add(new Long(19309));
		list.add(new Long(19314));
		list.add(new Long(19315));
		list.add(new Long(19517));
		list.add(new Long(20112));
		
		list.add(new Long(3426));

		for(Long id:list) {
			Concept root = closureServ.loadConceptById(id);
			closureServ.removeNode(root);
		}
	}
}
