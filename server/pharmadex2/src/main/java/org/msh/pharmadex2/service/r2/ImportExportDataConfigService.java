package org.msh.pharmadex2.service.r2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.FileResource;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.controller.r2.ExcelViewMult;
import org.msh.pharmadex2.dto.DataCollectionDTO;
import org.msh.pharmadex2.dto.DataConfigDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.FileDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.WorkflowDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.DtoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Export/Import data configuration from Excel File
 * @author alexk
 *
 */
@Service
public class ImportExportDataConfigService {
	private static final Logger logger = LoggerFactory.getLogger(ImportExportDataConfigService.class);
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private Messages mess;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private DictService dictServ;
	@Autowired
	private DtoService dtoServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private ThingService thingServ;


	/**
	 * Export Data Configuration to Excel
	 * @param dto
	 * @return
	 * @throws ObjectNotFoundException
	 * @throws IOException
	 */
	public Resource dataCollectionVariablesExport(DataConfigDTO dto) throws ObjectNotFoundException, IOException {
		Map<String, DataCollectionDTO> data = new LinkedHashMap<String, DataCollectionDTO>();
		Concept node = closureServ.loadConceptById(dto.getNodeId());

		data = dataConfigurations("root", node.getIdentifier(), data);

		Map<String, Map<String, DataCollectionDTO>> model = new LinkedHashMap<String, Map<String, DataCollectionDTO>>();
		model.put("data", data);
		XSSFWorkbook workbook = new XSSFWorkbook();
		ExcelViewMult excel = new ExcelViewMult();
		excel.getProcessor().initWorkbook(workbook);
		if(dto.isRestricted()) {
			TableQtb references = dataCollectionReferences(dto);
			excel.dataCollectionReferences(mess.get("menu_references"),references, workbook);
		}
		excel.workbookForDataConfiguration(model, mess, workbook);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);
		byte[] arr = out.toByteArray();
		workbook.close();
		out.close();
		return new ByteArrayResource(arr);
	}
	/**
	 * Load data collection references into the table
	 * @param dto
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private TableQtb dataCollectionReferences(DataConfigDTO dto) throws ObjectNotFoundException {
		TableQtb ret = new TableQtb();
		Concept conc = closureServ.loadConceptById(dto.getNodeId());
		jdbcRepo.data_url_references(conc.getIdentifier());
		Headers headers = ret.getHeaders();
		headers.getHeaders().add(TableHeader.instanceOf("label", mess.get("menu_references") ,15,TableHeader.COLUMN_STRING));
		headers.getHeaders().add(TableHeader.instanceOf("url", "URL",40, TableHeader.COLUMN_STRING));
		headers.getHeaders().add(TableHeader.instanceOf("count",mess.get("global_quantity"),15, TableHeader.COLUMN_LONG));
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from data_url_references", "", "", headers);
		ret.getRows().addAll(rows);
		return ret;
	}

	/**
	 * Recursive load data configurations in form of <varname, dto> for export to
	 * Excel and other joys
	 * 
	 * @param varName - variable name
	 * @param mainUrl url of the data configuration
	 * @param ret     - result map - variable name, DTO
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Map<String, DataCollectionDTO> dataConfigurations(String varName, String mainUrl,
			Map<String, DataCollectionDTO> ret) throws ObjectNotFoundException {
		// get root by mainUrl
		logger.trace(varName + "---->" + mainUrl);
		Concept root = closureServ.loadRoot("configuration.data");
		//Concept node = closureServ.findConceptInBranchByIdentifier(root, mainUrl);//ik 09122022 add new metod
		Concept node = closureServ.findActivConceptInBranchByIdentifier(root, mainUrl);
		DataCollectionDTO dto = new DataCollectionDTO();
		dto.setNodeId(node.getID());
		try {
			dto.getDescription().setValue(literalServ.readPrefLabel(node));
		} catch (Exception e) {
			dto.getDescription().setValue(mess.get("badconfiguration"));
		}
		dto.getUrl().setValue(mainUrl);
		dto.setVarName(varName);
		// variables
		List<String> excl = new ArrayList<String>();
		//exclude columns below
		excl.add("ID");
		excl.add("conceptID");
		excl.add("Discriminator");
		jdbcRepo.data_config_vars(mainUrl);
		List<TableHeader> uiHeaders = jdbcRepo.headersFromSelect("select * from data_config_vars where false", excl);
		dto.getTable().getHeaders().getHeaders().clear();
		dto.getTable().getHeaders().getHeaders().addAll(uiHeaders);
		dto.getTable().getHeaders().setPageSize(Integer.MAX_VALUE);
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from data_config_vars", "", "",
				dto.getTable().getHeaders());
		dto.getTable().getRows().clear();
		dto.getTable().getRows().addAll(rows);
		// add to the result
		ret.put(mainUrl, dto);
		// things inside
		for (TableRow row : rows) {
			String clazz = row.getCellByKey("clazz").getValue();
			String vn = row.getCellByKey("varname").getValue();
			// Things and persons
			if (clazz.equalsIgnoreCase("things")) {
				String url = row.getCellByKey("url").getValue();
				String auxUrl = row.getCellByKey("AuxDataUrl").getValue();
				ret = dataConfigurations(vn, url, ret);
				if (auxUrl != null) { // persons
					if (auxUrl.length() > 0) {
						ret = dataConfigurations(auxUrl, auxUrl, ret);
					}
				}
			}
			// check dictionaries
			if (clazz.equalsIgnoreCase("dictionaries") || clazz.equalsIgnoreCase("documents")) {
				String url = row.getCellByKey("Url").getValue();
				if (clazz.equalsIgnoreCase("documents")) {
					url = row.getCellByKey("dictUrl").getValue();
				}
				ret = dictConfigurations(vn, url, ret);
			}
			// check resources
			if (clazz.equalsIgnoreCase("resources")) {
				String rUrl = row.getCellByKey("Url").getValue();
				Concept rRoot = closureServ.loadRoot("configuration.resources");
				Concept rLang = closureServ.findConceptInBranchByIdentifier(rRoot,
						LocaleContextHolder.getLocale().toString().toUpperCase());
				Concept rNode = closureServ.findConceptInBranchByIdentifier(rLang, rUrl);
				String dataConfigUrl = rNode.getLabel();
				ret = dataConfigurations(rUrl, dataConfigUrl, ret);
			}
		}

		return ret;
	}
	/**
	 * Upload a dictionary by URL
	 * 
	 * @param vn
	 * @param dictUrl
	 * @param ret
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public Map<String, DataCollectionDTO> dictConfigurations(String vn, String dictUrl,
			Map<String, DataCollectionDTO> ret) throws ObjectNotFoundException {
		if (!ret.keySet().contains(dictUrl)) {
			logger.trace("dict ---->" + vn + "---->" + dictUrl);
			// get a dictionary by url
			Concept root = closureServ.loadRoot(dictUrl);
			boolean system = dictServ.checkSystem(root);
			boolean addresses = dictServ.isAdminUnits(root);
			if (!system && !addresses) {
				TableQtb table = new TableQtb();
				createHeadersDict(table.getHeaders());
				table = dictLevel(1, root.getID(), table);

				DataCollectionDTO dto = new DataCollectionDTO();
				dto.setTable(table);
				dto.getUrl().setValue(dictUrl);
				dto.setVarName(vn);
				ret.put(dictUrl, dto);
			}
		}
		return ret;
	}

	/**
	 * Recursive add rows to the table from the next level of a dictionary
	 * 
	 * @param level
	 * @param table
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private TableQtb dictLevel(int level, long parentID, TableQtb table) throws ObjectNotFoundException {
		// create a table from dictionary literals and the first level
		jdbcRepo.dict_level_ext(parentID);
		TableQtb table1 = jdbcRepo.queryAndPivot("select * from dict_level_ext");
		if (table.getHeaders().getHeaders().size() == 0) {
			table.getHeaders().getHeaders().add(0, TableHeader.instanceOf("level", TableHeader.COLUMN_LONG));
			table.getHeaders().getHeaders().addAll(table1.getHeaders().getHeaders());
		}
		for (TableRow row1 : table1.getRows()) {
			TableCell cell = TableCell.instanceOf("level");
			TableRow row = TableRow.instanceOf(row1.getDbID());
			cell.setOriginalValue(level);
			cell.setValue(level + "");
			row.getRow().add(0, cell);
			row.getRow().addAll(row1.getRow());
			table.getRows().add(row);
			// recursive call for the next level
			table = dictLevel(level + 1, row.getDbID(), table);
		}
		return table;
	}

	/**
	 * Create dictionary table headers
	 * 
	 * @param ret
	 * @param readOnly
	 * @return
	 */
	public Headers createHeadersDict(Headers ret) {
		ret.getHeaders().clear();

		ret.getHeaders().add(TableHeader.instanceOf("level", "level", true, true, true, TableHeader.COLUMN_LONG, 0));
		ret.getHeaders()
		.add(TableHeader.instanceOf("prefLabel", "prefLabel", true, true, true, TableHeader.COLUMN_STRING, 0));
		ret.getHeaders().add(
				TableHeader.instanceOf("description", "description", true, true, true, TableHeader.COLUMN_STRING, 0));
		ret.getHeaders().add(TableHeader.instanceOf("URL", "URL", true, true, true, TableHeader.COLUMN_STRING, 0));
		ret = boilerServ.translateHeaders(ret);
		return ret;
	}
	/**
	 * Export workflow configuration to MS Excel
	 * @param data
	 * @return
	 * @throws IOException 
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Resource workflowExportExcel(WorkflowDTO dto) throws IOException, ObjectNotFoundException {
		//restore literals and dictionaries
		for(ThingDTO tdto : dto.getPath()) {
			Concept node=closureServ.loadConceptById(tdto.getNodeId());
			tdto.setStrings(dtoServ.readAllStrings(tdto.getStrings(),node));
			tdto.setLiterals(dtoServ.readAllLiterals(tdto.getLiterals(), node));
			tdto.setDates(dtoServ.readAllDates(tdto.getDates(),node));
			tdto.setNumbers(dtoServ.readAllNumbers(tdto.getNumbers(),node));
			tdto.setLogical(dtoServ.readAllLogical(tdto.getLogical(), node));
			Set<String> dicts = tdto.getDictionaries().keySet();
			if(dicts!=null) {
				for(String key : dicts) {
					DictionaryDTO ddto = tdto.getDictionaries().get(key);
					ddto = dictServ.createCurrentSelections(ddto);
				}
			}
		}
		ExcelViewMult excel = new ExcelViewMult();
		XSSFWorkbook workbook=excel.workbookForWorkflowConfiguration(dto, mess);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);
		byte[] arr = out.toByteArray();
		workbook.close();
		out.close();
		return new ByteArrayResource(arr);
	}
	/**
	 * Load data import thing
	 * @param user
	 * @param data
	 * @throws ObjectNotFoundException 
	 */
	public ThingDTO dataConfigurationLoadImport(UserDetailsDTO user, ThingDTO data) throws ObjectNotFoundException {
		//load and save only one and only under the root of the tree
		data.setUrl(AssemblyService.SYSTEM_IMPORT_DATA_CONFIGURATION);	//the root of the tree
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
		return data;
	}
	/**
	 * Run data configuration import
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws JsonProcessingException 
	 */
	@Transactional
	public ThingDTO dataConfigurationRunImport(UserDetailsDTO user, ThingDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		Concept parent=closureServ.loadRoot(data.getUrl());
		data.setParentId(parent.getID());
		data= thingServ.thingSaveUnderParent(data, user);
		if(data.isValid()) {
			XSSFWorkbook wb = loadDataConfiguration(data);
			if(wb != null) {
				try {
					wb=importDataConfiguration(wb);
				} catch (Exception e) {
					data.addError(e.getMessage());
					//wb=storeDataConfigurationErrors(wb);
				}
			}
		}
		return data;
	}
	/**
	 * Import data configuration for the primary e-form
	 * It should be on the second sheet
	 * @param wb
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private XSSFWorkbook importDataConfiguration(XSSFWorkbook wb) throws ObjectNotFoundException {
		try {
			XSSFSheet sheet = wb.getSheetAt(1);	//the main page configuration should be on the 
			String url = importUrl(sheet);
			if(url.length()>0) {
			url=adjustDataConfigUrl(url);
			
			}else {
				throw new ObjectNotFoundException(mess.get("error_bmpFile"));
			}
		} catch (Exception e) {
			throw new ObjectNotFoundException(mess.get("error_bmpFile"));
		}
		return wb;
	}
	/**
	 * Recursive adjust data configuration URL
	 * @param url
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private String adjustDataConfigUrl(String url) throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot(AssemblyService.SYSTEM_IMPORT_DATA_CONFIGURATION);
		List<Concept> confs = literalServ.loadOnlyChilds(root);
		url=uniqueDataConfigUrl(confs, url);
		return url;
	}
	private String uniqueDataConfigUrl(List<Concept> confs, String url) {
		//TODO
		return url;
	}
	/**
	 * Data URL should be in cell A1
	 * @param sheet
	 * @return
	 */
	private String importUrl(XSSFSheet sheet) {
		String url = boilerServ.getStringCellValue(sheet.getRow(0), 0);
		if(url == null) {
			return "";
		}
		return url;
	}
	/**
	 * Load workbook
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws  
	 */
	@Transactional
	private XSSFWorkbook loadDataConfiguration(ThingDTO data) throws ObjectNotFoundException {
		XSSFWorkbook ret = null;
		Set<String> keys = data.getDocuments().keySet();
		if(keys.size()==1) {
			FileDTO dto=data.getDocuments().get(keys.iterator().next());
			Set<Long> dictNodeIds = dto.getLinked().keySet();
			for(Long dictNodeId : dictNodeIds) {
				Concept dictNode = closureServ.loadConceptById(dictNodeId);
				if(dictNode.getIdentifier().equalsIgnoreCase(AssemblyService.DATAIMPORT_DATA)) {
					Long fileNodeId = dto.getLinked().get(dictNodeId);
					if(fileNodeId != null) {
						Concept fileNode = closureServ.loadConceptById(fileNodeId);
						FileResource fr = boilerServ.fileResourceByNode(fileNode);
						InputStream inputStream = new ByteArrayInputStream(fr.getFile());
						try {
							ret = new XSSFWorkbook(inputStream);
						} catch (IOException e) {
							data.addError(mess.get("upload_xlsx_data"));
						}
					}else {
						data.addError(mess.get("upload_xlsx_data"));
					}
				}
			}
		}else {
			data.addError(mess.get("upload_xlsx_data"));
		}
		return ret;
	}
}
