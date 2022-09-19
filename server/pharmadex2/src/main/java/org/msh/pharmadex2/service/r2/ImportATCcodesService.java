package org.msh.pharmadex2.service.r2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

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
public class ImportATCcodesService {

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

	private static int SHEET_NUMBER = 0;
	private static int ROW_START = 1;
	private static int COLUMN_CODE = 0;
	private static int COLUMN_LABEL = 1;
	
	private XSSFWorkbook book = null;
	private Concept fileNode = null;
	private FileResource fr = null;
	
	/**
	 * prepare electronic form for import admin units
	 * @param data
	 * @param user 
	 * @throws ObjectNotFoundException 
	 */
	public ThingDTO importLoad(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		//load and save only one and only under the root of the tree
		data.setUrl(AssemblyService.SYSTEM_IMPORT_ATCCODES);
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
			data.setValid(true);
			data.setUrl(AssemblyService.SYSTEM_IMPORT_ATCCODES);
		}
		return data;
	}
	
	public ThingDTO importReLoad(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		if(data.getNodeId() > 0) {
			String sstm = getSystemProtocol();
			if(!(sstm.equals("") || sstm.equals("END"))) {
				data.setUrl(AssemblyService.SYSTEM_IMPORT_ATCCODES_RELOAD);
			}
			/*Concept root = closureServ.loadConceptById(data.getNodeId());
			String result = literalServ.readValue(AssemblyService.DATAIMPORT_RESULT, root);
			if(!(result.isEmpty() || result.startsWith("End"))) {
				data.setUrl(AssemblyService.SYSTEM_IMPORT_ADMINUNITS_RELOAD);
			}
*/
			data=thingServ.loadThing(data, user);
			data.setValid(true);
		}
		data.setUrl(AssemblyService.SYSTEM_IMPORT_ATCCODES);
		return data;
	}
	
	public ThingDTO importStart(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException, IOException{
		LocaleContextHolder.setDefaultLocale(messages.getCurrentLocale());
		
		writeProtocol(messages.get("startImport"));
		writeSystemProtocol("START");

		data = thingServ.loadThing(data, user);
		return data;
	}
	
	@Async
	public void importRun(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException, IOException{
		LocaleContextHolder.setDefaultLocale(messages.getCurrentLocale());
		
		//writeProtocol(messages.get("startImport"));
		//writeSystemProtocol("START");
		XSSFSheet sheet = loadSheet(data);
		
		if(sheet != null){
			Concept rootMain = deactiveCodes();
		
			LegacyDataErrorsDTO errors = new LegacyDataErrorsDTO(book);
			
			if(!loadATCcodes(sheet, rootMain, errors)) {
				createFileError(data, user);
			}
		}else {
			writeProtocol(messages.get("badFile"));
			writeSystemProtocol("ERROR");
			
			return;
		}
		writeProtocol(messages.get("endImport"));
		writeSystemProtocol("END");
	}
	
	private boolean loadATCcodes(XSSFSheet sh, Concept root, LegacyDataErrorsDTO errors) throws ObjectNotFoundException {
		boolean flag = true;
		for (int i = ROW_START; i <= sh.getLastRowNum(); i++) {
			XSSFRow row = sh.getRow(i);
			String identifier = row.getCell(COLUMN_CODE).getStringCellValue();
			String label = row.getCell(COLUMN_LABEL).getStringCellValue();
			if(legacyDataService.validString(identifier) && legacyDataService.validString(label)) {
				Concept conc = closureServ.loadConceptByIdentifier(identifier);
				if(conc == null) {
					conc = new Concept();
					conc.setIdentifier(identifier);
					conc.setLabel(label);
				}
				conc.setActive(true);
				closureServ.saveToTreeFast(root, conc);
			}else {
				errors.add(row, row.getLastCellNum(), sh.getSheetName());
				flag = false;
			}
		}
		return flag;
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
	
	private XSSFSheet loadSheet(ThingDTO data) throws ObjectNotFoundException, JsonParseException, JsonMappingException, IOException {
		XSSFSheet sheet = null;
		
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
	
	@Transactional
	public void writeProtocol(String val) throws ObjectNotFoundException {
		val += ". Date " + getCurrentDate();
		Concept protocol=protocolConcept(AssemblyService.SYSTEM_IMPORT_ATCCODES);
		protocol = literalServ.createUpdateLiteral(AssemblyService.DATAIMPORT_RESULT, val, protocol);
		logger.info(val);
	}
	
	/**
	 * System Literal 
	 * monitors import status
	 * START, END, ERROR
	 */
	@Transactional
	public void writeSystemProtocol(String val) throws ObjectNotFoundException {
		Concept protocol = protocolConcept(AssemblyService.SYSTEM_IMPORT_ATCCODES);
		protocol = literalServ.createUpdateLiteral(AssemblyService.DATAIMPORT_SYSTEM_RESULT, val, protocol);
	}
	
	private String getSystemProtocol() throws ObjectNotFoundException {
		Concept protocol = protocolConcept(AssemblyService.SYSTEM_IMPORT_ATCCODES);
		String result = literalServ.readValue(AssemblyService.DATAIMPORT_SYSTEM_RESULT, protocol);
		return result;
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

	private String getCurrentDate() {
		LocalDateTime currentDate = LocalDateTime.now();
		String d = (currentDate.getDayOfMonth() < 10?"0":"") + currentDate.getDayOfMonth();
		d += "/" + (currentDate.getMonthValue() < 10?"0":"") + currentDate.getMonthValue();
		d += "/" + currentDate.getYear();

		d += " " + currentDate.getHour() + ":" + currentDate.getMinute() + ":" + currentDate.getSecond();
		return d;
	}
	
	private void createFileError(ThingDTO data, UserDetailsDTO user) throws IOException, ObjectNotFoundException {
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
