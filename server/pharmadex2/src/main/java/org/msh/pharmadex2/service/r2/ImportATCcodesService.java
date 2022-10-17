package org.msh.pharmadex2.service.r2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class ImportATCcodesService {

	private static final Logger logger = LoggerFactory.getLogger(ImportATCcodesService.class);
	private static AtomicBoolean uploadFlag = new AtomicBoolean(false);		//avoid usage of components while import is in process
	private static AtomicInteger counter = new AtomicInteger(0);					//how many imported
	private static AtomicInteger total = new AtomicInteger(0);						//how many to import

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

	private static int SHEET_NUMBER = 0;
	private static int COLUMN_CODE = 0;
	private static int COLUMN_LABEL = 1;
	private static int COLUMN_DDD=2;
	private static int COLUMN_UOM=3;
	private static int COLUMN_ADMINROUTE=4;
	private static int COLUMN_NOTE=5;


	public static AtomicBoolean getUploadFlag() {
		return uploadFlag;
	}
	public static void setUploadFlag(AtomicBoolean uploadFlag) {
		ImportATCcodesService.uploadFlag = uploadFlag;
	}

	public static AtomicInteger getCounter() {
		return counter;
	}
	public static void setCounter(AtomicInteger counter) {
		ImportATCcodesService.counter = counter;
	}
	public static AtomicInteger getTotal() {
		return total;
	}
	public static void setTotal(AtomicInteger total) {
		ImportATCcodesService.total = total;
	}
	/**
	 * ATC import electronic form
	 * @param data
	 * @param user 
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO importLoad(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		//load and save only one and only under the root of the tree
		data.setUrl(AssemblyService.SYSTEM_IMPORT_ATCCODES);	//the root of the tree
		Concept root = closureServ.loadRoot(data.getUrl());
		data.setParentId(root.getID());
		List<Concept> nodes = closureServ.loadLevel(root);
		if(nodes.size()>0) {
			data.setNodeId(nodes.get(0).getID());
		}
		if(data.getNodeId()==0) {
			data=thingServ.createThing(data, user);
		}else {
			data=thingServ.loadThing(data, user);
		}
		data.setReadOnly(getUploadFlag().get());
		return data;
	}

	/**
	 * Really run import asynchronous
	 * @param data
	 * @param user
	 */
	@Async
	public void importRunAsync(ThingDTO data, UserDetailsDTO user) {
		ImportATCcodesService.setUploadFlag(new AtomicBoolean(true));
		try {
			setUploadFlag(new AtomicBoolean(true));
			importRun(data,user);
			setUploadFlag(new AtomicBoolean(false));
		} catch (ObjectNotFoundException | IOException e) {
			setUploadFlag(new AtomicBoolean(false));
			e.printStackTrace();
			try {
				writeProtocol(data, user,e.getMessage());
			} catch (ObjectNotFoundException | JsonProcessingException e1) {
				e1.printStackTrace();
			}
		}
		finally {
			setUploadFlag(new AtomicBoolean(false));
		}
	}

	/**
	 * Write a protocol data
	 * @param data
	 * @param user
	 * @param message
	 * @throws ObjectNotFoundException 
	 * @throws JsonProcessingException 
	 */
	@Transactional
	private void writeProtocol(ThingDTO data, UserDetailsDTO user, String message) throws ObjectNotFoundException, JsonProcessingException {
		data=importLoad(data, user);
		String mess = 
				DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now())
				+" "
				+message 
				+ " "
				+getCounter().get()
				+"/"+getTotal().get();
		data.getLiterals().get(AssemblyService.DATAIMPORT_RESULT).setValue(mess);
		data=thingServ.thingSaveUnderParent(data, user);
		logger.trace(mess);
	}
	/**
	 * Run import
	 * @param data
	 * @param user
	 * @throws ObjectNotFoundException
	 * @throws IOException
	 */
	public void importRun(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException, IOException{
		data=thingServ.thingSaveUnderParent(data, user);
		setTotal(new AtomicInteger(0));
		setCounter(new AtomicInteger(0));
		writeProtocol(data, user, messages.get("btn_startimport"));
		XSSFSheet sheet = loadSheet(data);
		if(sheet != null){
			Concept rootMain = deactiveCodes();
			LegacyDataErrorsDTO errors = new LegacyDataErrorsDTO(sheet.getWorkbook());
			if(!loadATCcodes(data, user, sheet, rootMain, errors)) {
				createFileError(data, user,sheet);
			}
		}else {
			writeProtocol(data, user,messages.get("error"));
			return;
		}
		writeProtocol(data, user, messages.get("completedby"));
		ImportATCcodesService.setUploadFlag(new AtomicBoolean(false));
	}

	/**
	 * Import - re-import ATC codes and DDD 
	 * Write/add dictionaries dictionary.who.uom, dictionary.who.adimroute
	 * @param user 
	 * @param data 
	 * @param sh the sheet
	 * @param root concept with root URL
	 * @param errors
	 * @return
	 * @throws ObjectNotFoundException
	 * @throws JsonProcessingException 
	 */
	@Transactional
	public boolean loadATCcodes(ThingDTO data, UserDetailsDTO user, XSSFSheet sh, Concept root, LegacyDataErrorsDTO errors) throws ObjectNotFoundException, JsonProcessingException {
		boolean flag = true;
		Set<String> uoms = new HashSet<String>();			//dictionary.who.uom
		Set<String> arouts = new HashSet<String>();		//dictionary.who.adminroute
		//search for the first row
		int firstRow=0;
		setTotal(new AtomicInteger(sh.getLastRowNum()));
		setCounter(new AtomicInteger(0));
		for(int i=0;i<=sh.getLastRowNum();i++) {
			XSSFRow row = sh.getRow(i);
			String identifier = cellAsString(row, COLUMN_CODE);
			if(identifier.equals("A")) {
				firstRow=i;
				break;
			}
		}
		writeProtocol(data, user, messages.get("btn_startimport"));
		if(firstRow<sh.getLastRowNum()) {
			//import
			for (int i = firstRow; i <= sh.getLastRowNum(); i++) {
				setCounter(new AtomicInteger(i));
				if(i>99 && i % 100 == 0) {
					writeProtocol(data, user, messages.get("btn_startimport"));
				}
				XSSFRow row = sh.getRow(i);
				String atcCode = cellAsString(row, COLUMN_CODE);
				String atcText = cellAsString(row,COLUMN_LABEL);
				if(legacyDataService.validString(atcCode) && legacyDataService.validString(atcText)) {
					loadRow(root, row, atcCode, atcText,uoms,arouts);
				}else {
					errors.add(row, row.getLastCellNum(), sh.getSheetName());
					flag = false;
				}
			}
			updateDictionary("dictionary.who.uom", messages.get("dos_unit"),"https://www.whocc.no/atc_ddd_index/", uoms);
			updateDictionary("dictionary.who.adminroute",messages.get("admin_route"),"https://www.whocc.no/atc_ddd_index/", arouts);
		}else {
			flag=false;
		}
		return flag;
	}
	/**
	 * Load a row in a single transaction
	 * @param root
	 * @param row
	 * @param atcCode
	 * @param atcText
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public void loadRow(Concept root, XSSFRow row, String atcCode, String atcText,Set<String> uoms,Set<String> arouts) throws ObjectNotFoundException {
		String dddText=dddToString(row,uoms, arouts);
		Concept concept = closureServ.loadConceptByIdentifier(atcCode+"/"+dddText);
		if(concept == null) {
			Concept conc = new Concept();
			conc=closureServ.save(conc);
			conc.setIdentifier(atcCode+"/"+dddText);
			conc.setLabel(atcCode);
			conc=closureServ.saveToTreeFast(root, conc);
			conc=createLiterals(conc, atcCode, atcText, dddText);
		}else {
			concept.setActive(true);
			concept=closureServ.save(concept);
		}
	}

	/**
	 * Create literals under the concept
	 * @param conc
	 * @param atcCode
	 * @param atcText
	 * @param dddText
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept createLiterals(Concept conc, String atcCode, String atcText, String dddText) throws ObjectNotFoundException {
		literalServ.createUpdatePrefLabel(atcText, conc);
		literalServ.createUpdateDescription(dddText, conc);
		literalServ.createUpdateLiteral("atc", atcCode, conc);
		return conc;
	}
	/**
	 * Extract DDD data from the row as a string
	 * Add values for dictionaries
	 * @param row
	 * @return
	 */
	private String dddToString(XSSFRow row, Set<String> uoms, Set<String> arouts) {
		String ret= cellAsString(row,COLUMN_LABEL); 
		String ddd = cellAsString(row, COLUMN_DDD);
		String uom = cellAsString(row, COLUMN_UOM);
		String adminRoute = cellAsString(row, COLUMN_ADMINROUTE);
		String note = cellAsString(row, COLUMN_NOTE);
		if(ddd.length()>0) {
			ret=ret+", "+ddd;
		}
		if(uom.length()>0) {
			uoms.add(uom);
			ret=ret+uom;
		}
		if(adminRoute.length()>0) {
			arouts.add(adminRoute);
			ret=ret+", "+adminRoute;
		}
		if(note.length()>0){
			ret=ret+", "+note;
		}
		return ret;
	}
	/**
	 * The WHO data contains useful abbreviations for Units of Measures and Admin routes
	 * This page https://www.whocc.no/atc_ddd_index/ contains descriptions of them
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private void updateDictionary(String url, String prefLabel, String description, Set<String> values) throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot(url);
		literalServ.prefAndDescription(prefLabel, description, root);
		List<Concept> level=literalServ.loadOnlyChilds(root);
		boolean found=false;
		for(String value : values) {
			found=false;
			for(Concept item : level) {
				if(value.length()>0) {
					if(item.getIdentifier().equals(value)) {
						found=true;
						break;
					}
				}
			}
			if(!found && value.length()>0) {
				//add
				Concept item=new Concept();
				item.setIdentifier(value);
				item=closureServ.saveToTree(root, item);
				//both are mandatory for a dictionary
				literalServ.createUpdateLiteralRealString("prefLabel", value, item);
				literalServ.createUpdateLiteralRealString("description", value, item);
			}
		}
	}

	/**
	 * Cell value in a row as a string
	 * @param row the row
	 * @param col column number
	 * @return String if one, empty string otherwise
	 */
	private String cellAsString(XSSFRow row, int col) {
		String ret="";
		try {
			//string
			ret = row.getCell(col).getStringCellValue();
		} catch (Exception e) {
			try {
				//numeirc
				double numret=row.getCell(col).getNumericCellValue();
				ret=String.valueOf(numret);
			} catch (Exception e1) {
				//date
				try {
					Date retD=row.getCell(col).getDateCellValue();
					ret=DateTimeFormatter.ISO_LOCAL_DATE.toFormat().format(retD);
				} catch (Exception e2) {
					ret="";	//nothing to do
				}
			}
		}
		return ret;
	}

	@Transactional
	public Concept deactiveCodes() throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot(SystemService.PRODUCTCLASSIFICATION_ATC_HUMAN);
		List<Concept> codes = closureServ.loadLevel(root);
		if(codes != null && codes.size() > 0) {
			for(Concept c:codes) {
				c.setActive(false);
				closureServ.saveToTreeFast(root, c);
			}
		}
		return root;
	}
	/**
	 * Load the source data
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private XSSFSheet loadSheet(ThingDTO data) throws ObjectNotFoundException, JsonParseException, JsonMappingException, IOException {
		XSSFSheet sheet = null;
		FileResource fr = null;
		Concept fileNode = null;
		XSSFWorkbook book = null;
		FileDTO dto = data.getDocuments().get(AssemblyService.DATAIMPORT_DATA);
		if(dto != null && dto.getLinked() != null) {
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
					sheet = book.getSheetAt(SHEET_NUMBER);
				}
			}
		}

		return sheet;
	}

	private void createFileError(ThingDTO data, UserDetailsDTO user, XSSFSheet sheet) throws IOException, ObjectNotFoundException {
		String fnameErr = "Error.xlsx";
		File fError = new File(fnameErr);
		FileOutputStream fos = new FileOutputStream(fError);
		sheet.getWorkbook().write(fos);
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
		fdto.setMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		thingServ.fileSave(fdto, user, Files.readAllBytes(fError.toPath()));
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
}
