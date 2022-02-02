package org.msh.pharmadex2.service.r2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.xssf.usermodel.XSSFName;
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
	private static final String EXPIRATION_DATE = "expiration";
	private static final String REGISTRATION_DATE = "registration";
	private static final String REGISTER = "register";
	private static final String ALTERNATIVE_NAME = "alternative";
	private static final String NAME = "name";

	private static final Logger logger = LoggerFactory.getLogger(LegacyDataService.class);
	@Autowired
	private Messages messages;
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
	 * Add records for which the registration numbers are not in the database yet
	 * @param xlsx byte array represents xlsx file
	 * @param url - URL of the data, e.g., "legacy.pharmacy" or "legacy.medicines"
	 * @return
	 * @throws IOException 
	 * @throws ObjectNotFoundException 
	 */
	public String importData(byte[] xlsx, String url) throws IOException, ObjectNotFoundException {
		String mess="";
		if(xlsx.length > 0){						
			InputStream inputStream = new ByteArrayInputStream(xlsx);
			XSSFWorkbook book=new XSSFWorkbook(inputStream);
			// retrieve the ranges
			Map<String,XSSFName> ranges= new LinkedHashMap<String, XSSFName>();
			XSSFName nameRange =   book.getName(NAME);
			if(nameRange!=null) {
				ranges.put(NAME,nameRange);
			}else {
				mess=NAME;
			}
			XSSFName altNameRange = book.getName(ALTERNATIVE_NAME);
			if(altNameRange!=null) {
				ranges.put(ALTERNATIVE_NAME, altNameRange);
			}else {
				mess=ALTERNATIVE_NAME;
			}
			XSSFName registerRange = book.getName(REGISTER);
			if(registerRange!=null) {
				ranges.put(REGISTER,registerRange);
			}else {
				mess=REGISTER;
			}
			XSSFName regDateRange = book.getName(REGISTRATION_DATE);
			if(regDateRange!=null) {
				ranges.put(REGISTRATION_DATE,regDateRange);
			}else {
				mess=REGISTRATION_DATE;
			}
			XSSFName expDateRange = book.getName(EXPIRATION_DATE);
			if(expDateRange!=null) {
				ranges.put(EXPIRATION_DATE, expDateRange);
			}else {
				mess=EXPIRATION_DATE;
			}
			if(mess.length()==0) {
				mess=storeLegacyData(book,ranges, url);
			}else {
				mess=messages.get("error_bmpFile")+" <"+mess+">";
			}
			book.close();
		}else {
			mess=messages.get("Error.uploadFile");
		}
		return mess;
	}

	/**
	 * Import the legacy data to the database
	 * @param book 
	 * @param ranges
	 * @param url 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private String storeLegacyData(XSSFWorkbook book, Map<String, XSSFName> ranges, String url) throws ObjectNotFoundException {
		String mess="";
		XSSFSheet sheet=book.getSheetAt(0);
		for(int row=1; row<=sheet.getLastRowNum();row++) {
			storeLegacyRow(sheet,ranges,row,url);
		}
		return mess;
	}
	/**
	 * Store a row to the database
	 * @param sheet
	 * @param ranges
	 * @param index
	 * @param url 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private String storeLegacyRow(XSSFSheet sheet, Map<String, XSSFName> ranges, int index, String url) throws ObjectNotFoundException {
		String mess ="";
		Cell cell = fetchCell(sheet, ranges.get(REGISTER), index);
		String register=cell.getStringCellValue();
		List<LegacyData> datl = legacyRepo.findByRegisterAndUrl(register, url);
		if(datl.size()==0) {
			LegacyData ld= new LegacyData();
			//concept
			Concept root = closureServ.loadRoot(url);
			Concept node = new Concept();
			node.setIdentifier(register);
			node=closureServ.saveToTree(root, node);
			Cell nameCell = fetchCell(sheet, ranges.get(NAME), index);
			String name=nameCell.getStringCellValue();
			if(name!=null) {
				Cell altNameCell = fetchCell(sheet, ranges.get(ALTERNATIVE_NAME), index);
				node=literalServ.createUpdateLiteral("prefLabel", nameCell.getStringCellValue(), node);
				node=literalServ.createUpdateLiteral("altLabel", altNameCell.getStringCellValue(), node);
				ld.setConcept(closureServ.loadConceptById(node.getID()));
				//registration data
				Cell regCell = fetchCell(sheet, ranges.get(REGISTER),index);
				ld.setRegister(regCell.getStringCellValue());
				Cell rDateCell = fetchCell(sheet, ranges.get(REGISTRATION_DATE), index);
				ld.setRegDate(rDateCell.getDateCellValue());
				Cell eDateCell = fetchCell(sheet, ranges.get(EXPIRATION_DATE), index);
				ld.setExpDate(eDateCell.getDateCellValue());
				ld.setUrl(url);
				legacyRepo.save(ld);
				
			}else {
				mess=messages.get("valid_namereq");
			}
		}
		// extract the cell contents based on cell type etc.
		return mess;
	}
	/**
	 * Get a cell from the range 
	 * @param sheet
	 * @param xssfName
	 * @param index
	 * @return
	 */
	public Cell fetchCell(XSSFSheet sheet, XSSFName xssfName, int rownum) {
		AreaReference aref = new AreaReference(xssfName.getRefersToFormula(), null);
		short col = aref.getFirstCell().getCol();
		Cell cell = sheet.getRow(rownum).getCell(col);
		return cell;
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
