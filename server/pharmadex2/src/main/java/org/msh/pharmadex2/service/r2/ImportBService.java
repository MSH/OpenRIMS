package org.msh.pharmadex2.service.r2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.FileResource;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.FileDTO;
import org.msh.pharmadex2.dto.LegacyDataDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Import legacy data
 * @author alexk
 *
 */
@Service
public class ImportBService {
	private static final Logger logger = LoggerFactory.getLogger(LegacyDataService.class);
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private ThingService thingServ;
	@Autowired
	private LegacyDataService legacyDataServ;
	@Autowired
	Messages messages;
	@Autowired
	private LiteralService literalServ;
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
	
	public ThingDTO importLegacyDataLoad(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		//load and save only one and only under the root of the tree
		data.setUrl(AssemblyService.SYSTEM_IMPORT_LEGACY_DATA);
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
		
		return data;
	}
	
	public ThingDTO importLegacyDataReload(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		if(data.getNodeId() > 0) {
			data=thingServ.loadThing(data, user);
			data.setValid(true);
			String result = data.getLiterals().get(AssemblyService.DATAIMPORT_RESULT).getValue();
			if(!(result.isEmpty() || result.startsWith("End"))) {
				data.getAddresses().clear();
			}
		}
		return data;
	}
	
	public ThingDTO importLegacyDataVerify(ThingDTO data) {
		data.setValid(true);
		return data;
	}
	
	/**
	 * Identificator=0 - file by import
	 * Identificator=1 - file with errors in import
	 * @param data
	 * @param user
	 * @throws ObjectNotFoundException
	 * @throws IOException
	 */
	@Async
	public void importLegacyDataRun(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException, IOException{
		FileDTO dto = data.getDocuments().get(AssemblyService.DATAIMPORT_DATA);
		Concept fileNode = null;
		Long dictNodeId = loadDictConcept(false).getID();
		fileNode = closureServ.loadConceptById(dto.getLinked().get(dictNodeId));
		if(fileNode != null) {
			String curLoc = LocaleContextHolder.getLocale().toString().toUpperCase();
			boolean hasOtherLoc = true;
			for(String l:messages.getAllUsedUpperCase()) {
				if(l.equals(curLoc)) {
					hasOtherLoc = false;
				}
			}
			if(hasOtherLoc) {
				LocaleContextHolder.setDefaultLocale(messages.getCurrentLocale());
			}
			
			FileResource fr = boilerServ.fileResourceByNode(fileNode);
			XSSFWorkbook wb = legacyDataServ.importLegacyData(fr.getFile());
			
			String fnameErr = "Error.xlsx";
			String nfile = fileNode.getLabel();
			if(nfile.endsWith(".xlsx")) {
				fnameErr = nfile.replace(".xlsx", ".xlsxOut.xlsx");
			}
			
			File fError = new File(fnameErr);
			FileOutputStream fos = new FileOutputStream(fError);
			wb.write(fos);
			fos.flush();
			fos.close();
			
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
