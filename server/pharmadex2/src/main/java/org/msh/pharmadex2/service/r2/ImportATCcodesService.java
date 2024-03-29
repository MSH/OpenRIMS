package org.msh.pharmadex2.service.r2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.FileResource;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AsyncInformDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class ImportATCcodesService {

	private static final Logger logger = LoggerFactory.getLogger(ImportATCcodesService.class);
	private static AtomicInteger counter = new AtomicInteger(0);//how many imported
	private static AtomicInteger total = new AtomicInteger(0);//how many to import

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
		data = compareRows(data);
		return data;
	}

	/**
	 * sort rows DATAIMPORT table
	 * first row - file by import
	 * second row - file error
	 * @return
	 */
	private ThingDTO compareRows(ThingDTO data){
		try {
			Long dictNodeId = loadDictConcept(false).getID();
			
			if(data.getDocuments().get(AssemblyService.DATAIMPORT_DATA) != null) {
				TableQtb t = data.getDocuments().get(AssemblyService.DATAIMPORT_DATA).getTable();
				List<TableRow> rows = t.getRows();
				List<TableRow> rowsNew = new ArrayList<TableRow>();
				if(rows.get(0).getDbID() == dictNodeId) {
					rowsNew.add(0, rows.get(0));
					rowsNew.add(1, rows.get(1));
				}else if(rows.get(1).getDbID() == dictNodeId) {
					rowsNew.add(0, rows.get(1));
					rowsNew.add(1, rows.get(0));
				}
				data.getDocuments().get(AssemblyService.DATAIMPORT_DATA).getTable().setRows(rowsNew);
			}
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	/**
	 * Really run ATC import
	 * @param data
	 * @param user
	 * @throws ObjectNotFoundException
	 * @throws IOException
	 */
	//NO @Transactional
	public void importRunWorker(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException, IOException{
		data=thingServ.saveUnderParent(data, user);
		setTotal(new AtomicInteger(0));
		setCounter(new AtomicInteger(0));
		XSSFSheet sheet = loadSheet(data);
		if(sheet != null){
			AsyncService.writeAsyncContext(AsyncService.PROGRESS_SHEETS, sheet.getSheetName());
			logger.trace(sheet.getSheetName());
			FileDTO dto = data.getDocuments().get(AssemblyService.DATAIMPORT_DATA);
			//remove old fileErrors
			Long dictNodeErrId = loadDictConcept(true).getID();
			if(dictNodeErrId > 0 && dto.getLinked().get(dictNodeErrId) != null && dto.getLinked().get(dictNodeErrId) > 0) {
				Concept errNode = closureServ.loadConceptById(dto.getLinked().get(dictNodeErrId));
				thingServ.fileRemove(errNode.getID(), user);
			}
			
			Concept rootMain = deactiveCodes();
			LegacyDataErrorsDTO errors = new LegacyDataErrorsDTO(sheet.getWorkbook());
			if(!loadATCcodes(data, user, sheet, rootMain, errors)) {
				createFileError(data, user,sheet);
			}
		}else {
			//TODO error message to AsyncSErvice#context
			logger.trace("Error");
		}
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
	//NO @Transactional
	public boolean loadATCcodes(ThingDTO data, UserDetailsDTO user, XSSFSheet sh, Concept root, LegacyDataErrorsDTO errors) throws ObjectNotFoundException, JsonProcessingException {
		boolean flag = true;
		Set<String> uoms = new HashSet<String>();			//dictionary.who.uom
		Set<String> arouts = new HashSet<String>();		//dictionary.who.adminroute
		//search for the first row
		int firstRow=0;
		setTotal(new AtomicInteger(sh.getLastRowNum()));
		AsyncService.writeAsyncContext(AsyncService.PROGRESS_TOTAL_REMOVE, "");
		AsyncService.writeAsyncContext(AsyncService.PROGRESS_COUNTER_REMOVE, "");
		AsyncService.writeAsyncContext(AsyncService.PROGRESS_TOTAL, getTotal() + "");
		setCounter(new AtomicInteger(0));
		for(int i=0;i<=sh.getLastRowNum();i++) {
			XSSFRow row = sh.getRow(i);
			String identifier = cellAsString(row, COLUMN_CODE);
			if(identifier.equals("A")) {
				firstRow=i;
				logger.trace("Start");
				break;
			}
		}
		if(firstRow<sh.getLastRowNum()) {
			//---------------------------    import
			//get all existing
			Set<String> existing = closureServ.loadLevelIdentifiers(root);
			//import row by row
			for (int i = firstRow; i <= sh.getLastRowNum(); i++) {
				setCounter(new AtomicInteger(i));
				AsyncService.writeAsyncContext(AsyncService.PROGRESS_COUNTER, getCounter() + "");
				if(i>99 && i % 100 == 0) {
					//TODO message 
				}
				XSSFRow row = sh.getRow(i);
				String atcCode = cellAsString(row, COLUMN_CODE);
				String atcText = cellAsString(row,COLUMN_LABEL);
				if(legacyDataService.validString(atcCode) && legacyDataService.validString(atcText)) {
					loadRow(root, row, atcCode, atcText,uoms,arouts, existing);
				}else {
					errors.add(row, row.getLastCellNum(), sh.getSheetName());
					flag = false;
				}
			}
			updateDictionary("dictionary.who.uom", messages.get("dos_unit"),"https://www.whocc.no/atc_ddd_index/", uoms);
			updateDictionary("dictionary.who.adminroute",messages.get("admin_route"),"https://www.whocc.no/atc_ddd_index/", arouts);
			logger.trace("End");
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
	 * @param existing 
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public void loadRow(Concept root, XSSFRow row, String atcCode, String atcText,Set<String> uoms,Set<String> arouts, Set<String> existing) throws ObjectNotFoundException {
		String dddText=dddToString(row,uoms, arouts);
		String identifier=atcCode+"/"+dddText;
		Concept node = new Concept();
		if(!existing.contains(identifier)) {
			existing.add(identifier);
			node.setIdentifier(identifier);
			node.setLabel(atcCode);
			node=closureServ.saveToTreeFast(root, node);
			node=createLiterals(node, atcCode, atcText, dddText);
		}else {
			node=closureServ.findActivConceptInBranchByIdentifier(root, identifier);
			node.setActive(true);
			node=closureServ.save(node);
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
			AsyncService.writeAsyncContext(AsyncService.PROGRESS_TOTAL_REMOVE, codes.size() + "");
			logger.trace(codes.size() + "");
			int i = 0;
			for(Concept c:codes) {
				c.setActive(false);
				closureServ.saveToTreeFast(root, c);
				AsyncService.writeAsyncContext(AsyncService.PROGRESS_COUNTER_REMOVE, i + "");
				i++;
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
	/**
	 * Progress bar logic for admin unit import
	 * 25% на процесс удаления прежних записей (если они есть)
	 * 75% на создание новых если были старые
	 * или 
	 * 100% на создание новых если старых не было
	 * @param data
	 * @return
	 */
	public AsyncInformDTO calcProgress(AsyncInformDTO data) {
		//names
		data.setTitle(messages.get("processImportAtccodes"));
		int per = 100;
		int min = 10;
		data.setComplPercent(min); //minimum value
		
		String totalRem = AsyncService.readAsyncContext(AsyncService.PROGRESS_TOTAL_REMOVE);
		String counterRem = AsyncService.readAsyncContext(AsyncService.PROGRESS_COUNTER_REMOVE);
		if(!totalRem.isEmpty() && !counterRem.isEmpty()) {
			per = 70;
			Float total = new Float(totalRem);
			Float imported = new Float(counterRem);
			Float percentF = ((imported)/total)*30;
			if(percentF > min)
				data.setComplPercent(percentF.intValue());
			data.setProgressMessage("Remove " + counterRem + " " + messages.get("of") + " " + totalRem);
		}
		
		// percents
		String totalS=AsyncService.readAsyncContext(AsyncService.PROGRESS_TOTAL);
		String importedS=AsyncService.readAsyncContext(AsyncService.PROGRESS_COUNTER);
		if(!totalS.isEmpty() && !importedS.isEmpty()) {
			Float total = new Float(totalS);
			Float imported = new Float(importedS);
			Float percentF = ((imported)/total)*per;
			if(percentF > min)
				data.setComplPercent(percentF.intValue());
			data.setProgressMessage("Import " + importedS + " " + messages.get("of") + " " + totalS);
			data.setCompleted(totalS.equals(importedS));
		}
		return data;
	}
}
