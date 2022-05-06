package org.msh.pharmadex2.service.r2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.LegacyData;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.r2.LegacyDataRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.LegacyDataDTO;
import org.msh.pharmadex2.dto.LegacyDataErrorsDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Methods related to the legacy data processing
 * @author alexk
 *
 */
@Service
public class LegacyDataService {

	private static final Logger logger = LoggerFactory.getLogger(LegacyDataService.class);
	@Autowired
	private LegacyDataRepo legacyRepo;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private ThingService thingServ;
	@Autowired
	private Messages messages;
	@Autowired
	private SystemService systemService;
	
	private static String SHEET_NAME_COUNTRY = "country";
	/**Список языков перепишем: первый в списке ОБЯЗАТЕЛЬНО язык по умолчанию (en_US) берем из org.msh.pdex2.i18n.Messages */
	private String[] langs = new String[2];

	/**
	 * Create a new legacy data using the assembly
	 * @param assm
	 * @return
	 */
	public LegacyDataDTO create(AssemblyDTO assm) {
		LegacyDataDTO ret = new LegacyDataDTO();
		//url to select and store
		ret.setUrl(assm.getDictUrl());
		ret.setStorageUrl(assm.getUrl());
		ret.setTable(createTable(ret));
		ret.setVarName(assm.getPropertyName());
		ret.setAltLabel(assm.getFileTypes());
		return ret;
	}
	/**
	 * Load a table with the legacy data.
	 * @param data
	 * @return
	 */
	private TableQtb createTable(LegacyDataDTO data) {
		TableQtb table = data.getTable();
		if(table.getHeaders().getHeaders().size()==0) {
			table.setHeaders(createHeaders(table.getHeaders()));
		}
		jdbcRepo.legacy_data(data.getUrl());
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from legacy_data", "", "", table.getHeaders());
		List<TableRow> selected= new ArrayList<TableRow>();
		if(data.getSelectedNode()>0) {
			for(TableRow row : data.getTable().getRows()) {
				if(row.getDbID()==data.getSelectedNode()) {
					row.setSelected(true);
					selected.add(row);
				}else {
					row.setSelected(false);
				}
			}
		}
		//when a row is selected, we will display only this row, otherwise, allow to select
		if(selected.size()==0) {
			selected.addAll(rows);
		}
		TableQtb.tablePage(selected, table);
		return table;
	}
	/**
	 * Header for legacy data table
	 * @param headers
	 * @return
	 */
	private Headers createHeaders(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"prefLabel",
				"prefLabel",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"altLabel",
				"altLabel",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"register",
				"reg_number",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"regdate",
				"registration_date",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"expdate",
				"expiry_date",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				0));
		headers=boilerServ.translateHeaders(headers);
		return headers;
	}
	/**
	 * Import legacy data from xlsx file
	 * Scan all sheets, except the errors sheet
	 * The name of the scanned sheet is URL for legacy data, e.g., legacy.pharmacies or legacy.wholesalers
	 * The legacy data layout on the sheet should from the first row, the zero row assumes for headers, and:
	 * <ul>
	 * <li> prefLabel - mandatory
	 * <li> altLabel, or empty in this case the altLabel=prefLabel
	 * <li> registration number - mandatory
	 * <li> registration start date - mandatory
	 * <li> registration expiration date - mandatory
	 * </ul>
	 * @param xlsx byte array represents xlsx file
	 * @return null if the workbook can't be read, or workbook otherwise. The errors sheet in the workbook may contain errors
	 * @throws IOException 
	 * @throws ObjectNotFoundException 
	 */
	public XSSFWorkbook importLegacyData(byte[] xlsx) throws IOException, ObjectNotFoundException {
		if(xlsx.length > 0){						
			InputStream inputStream = new ByteArrayInputStream(xlsx);
			XSSFWorkbook book=new XSSFWorkbook(inputStream);
			LegacyDataErrorsDTO errors= new LegacyDataErrorsDTO(book);
			int sheetIndex=0;
			XSSFSheet sheet = boilerServ.getSheetAt(book, sheetIndex);
			while(!errors.isErrorOrNullSheet(sheet)) {
				importLegacyDataSheet(sheet, errors);
				sheetIndex++;
				sheet=boilerServ.getSheetAt(book,sheetIndex);
			}
			return book;
		}else {
			return null;
		}
	}
	/**
	 * Import data from the particular data sheet
	 * Import will start from the first row. Zero row assumed for headers
	 * @param sheet
	 * @param errors
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private void importLegacyDataSheet(XSSFSheet sheet, LegacyDataErrorsDTO errors) throws ObjectNotFoundException {
		String url=sheetName(sheet);
		int rownum=1;
		XSSFRow row = boilerServ.getSheetRow(sheet,rownum);
		Concept root=closureServ.loadRoot(url);
		Concept protocol=protocolConcept(AssemblyService.SYSTEM_IMPORT_LEGACY_DATA);
		protocol=literalServ.createUpdateLiteral(AssemblyService.DATAIMPORT_RESULT, "started for "+ url, protocol);
		logger.info("started for "+url);
		int threshold=0;			//when the data will finished?
		while(row !=null && threshold<300) {					//see 300 invalid records before finish
			writeProtocol(rownum, 100, url, protocol);
			if(importLegacyDataRow(row, errors, root)) {
				threshold=0;
			}else {
				threshold++;
			}
			rownum++;
			row = boilerServ.getSheetRow(sheet,rownum);
		}
		protocol=literalServ.createUpdateLiteral(AssemblyService.DATAIMPORT_RESULT, "completed for "+ url, protocol);
		logger.info("completed for "+url);
	}
	/**
	 * Write a record to the protocol, if % of uploaded rows reaches the step
	 * @param rownum
	 * @param lastRow
	 * @param url 
	 * @param i
	 * @param protocol
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public void writeProtocol(int rownum, int step, String url, Concept protocol) throws ObjectNotFoundException {
		if(rownum>step) {
			int modulo = rownum % step;
			if(modulo==0) {
				String val=literalServ.readValue(AssemblyService.SYSTEM_IMPORT_LEGACY_DATA, protocol);
				val=val + " "+ url+"-" + rownum;
				protocol=literalServ.createUpdateLiteral(AssemblyService.DATAIMPORT_RESULT, val.trim(), protocol);
				logger.info(val.trim());
			}
		}
	}
	/**
	 * Concept to protocol data import process
	 * @param systemImportLegacyData
	 * @return
	 * @throws ObjectNotFoundException 
	 */
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
	/**
	 * Import a row from the sheet or put this row to the "errors" sheet
	 * @param row the row
	 * @param errors the "errors" sheet
	 * @param root the root concept under which the data should be stored 
	 * @param root 
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private boolean importLegacyDataRow(XSSFRow row, LegacyDataErrorsDTO errors, Concept root) throws ObjectNotFoundException {
		String prefLabel=boilerServ.getStringCellValue(row,0);
		String altLabel=boilerServ.getStringCellValue(row,1);
		if(!validString(altLabel)) {
			altLabel=prefLabel;
		}
		String regNo=boilerServ.getStringCellValue(row,2);
		Date regDate= boilerServ.getDateCellValue(row,3);
		Date expDate=boilerServ.getDateCellValue(row,4);
		String notes= boilerServ.getStringCellValue(row,5);
		if(!validString(notes)) {
			notes="";
		}
		if(notes.length()>500) {
			notes=notes.substring(0,500);
		}
		if(validString(prefLabel) && validString(altLabel) && validString(regNo) && validDate(regDate) && validDate(expDate)) {	
			Concept node = new Concept();
			node.setIdentifier(regNo);
			node = closureServ.saveToTree(root, node);
			node = literalServ.createUpdateLiteral("prefLabel", prefLabel, node);
			node = literalServ.createUpdateLiteral("altLabel", altLabel, node);
			LegacyData ld = new LegacyData();
			List<LegacyData> ldl= legacyRepo.findByRegisterAndUrl(regNo, root.getIdentifier());
			if(ldl.size()>0) {
				ld=ldl.get(0);
			}
			ld.setConcept(node);
			ld.setExpDate(expDate);
			ld.setRegDate(regDate);
			ld.setRegister(regNo);
			ld.setUrl(root.getIdentifier());
			ld.setNote(notes);
			legacyRepo.save(ld);
			return true;
		}
		if(validString(prefLabel)) {
			errors.add(row,5, root.getIdentifier());
			return true;
		}else {
			return false; //waiting for threshold
		}
	}
	/**
	 * Is this date valid
	 * @param dateVal
	 * @return
	 */
	private boolean validDate(Date dateVal) {
		return (dateVal!=null) && (dateVal instanceof Date);
	}
	/**
	 * Is this string valid?
	 * @param str
	 * @return
	 */
	public boolean validString(String str) {
		return str!=null && str.length()>0;
	}
	/**
	 * Get a name of the sheet
	 * @param sheet
	 * @return
	 */
	public String sheetName(XSSFSheet sheet) {
		XSSFWorkbook wb=sheet.getWorkbook();
		List<String> sheetNames = new ArrayList<String>();
		for (int i=0; i<wb.getNumberOfSheets(); i++) {
			sheetNames.add( wb.getSheetName(i) );
		}
		return sheetNames.get(wb.getSheetIndex(sheet));
	}


	/**
	 * Reload table data
	 * @param data
	 * @return
	 */
	public LegacyDataDTO reloadTable(LegacyDataDTO data) {
		data.setTable(createTable(data));
		if(data.getSelectedNode()>0) {
			for(TableRow row : data.getTable().getRows()) {
				if(row.getDbID()==data.getSelectedNode()) {
					row.setSelected(true);
				}else {
					row.setSelected(false);
					data.setSelectedNode(0);
				}
			}
		}
		return data;
	}
	/**
	 * Load a legacy data by the concept of it
	 * @param concept
	 * @param dto
	 * @return
	 */
	@Transactional
	public LegacyDataDTO load(Concept concept, LegacyDataDTO dto) {
		dto.setSelectedNode(concept.getID());
		dto=reloadTable(dto);
		return dto;
	}

}
