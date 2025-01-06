package org.msh.pharmadex2.service.r2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.FileResource;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.controller.r2.ExcelViewMult;
import org.msh.pharmadex2.dto.DataCollectionDTO;
import org.msh.pharmadex2.dto.DataConfigDTO;
import org.msh.pharmadex2.dto.DataVariableDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.FileDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.WorkflowDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.DtoService;
import org.msh.pharmadex2.service.common.EntityService;
import org.msh.pharmadex2.service.common.ValidationService;
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
	@Autowired
	private SupervisorService superServ;
	@Autowired
	private ValidationService validServ;
	@Autowired
	private EntityService entityServ;

	/**
	 * Export Data Configuration to Excel
	 * @param dto
	 * @return
	 * @throws ObjectNotFoundException
	 * @throws IOException
	 */
	public Resource variablesExport(DataConfigDTO dto) throws ObjectNotFoundException, IOException {
		Map<String, DataCollectionDTO> data = new LinkedHashMap<String, DataCollectionDTO>();
		Concept node = closureServ.loadConceptById(dto.getNodeId());
		data = variablesLoad("root", node.getIdentifier(), data);
		//(data_url,sheet_data)
		Map<String, Map<String, DataCollectionDTO>> model = new LinkedHashMap<String, Map<String, DataCollectionDTO>>();
		model.put("data", data);
		XSSFWorkbook workbook = new XSSFWorkbook();
		ExcelViewMult excel = new ExcelViewMult();
		excel.getProcessor().initWorkbook(workbook);
		if(dto.isRestricted()) {
			TableQtb references = referencesLoad(dto);
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
	private TableQtb referencesLoad(DataConfigDTO dto) throws ObjectNotFoundException {
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
	public Map<String, DataCollectionDTO> variablesLoad(String varName, String mainUrl,
			Map<String, DataCollectionDTO> ret) throws ObjectNotFoundException {
		// get root by mainUrl
		logger.trace(varName + "---->" + mainUrl);
		Concept root = closureServ.loadRoot("configuration.data");
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
		// Query for
		jdbcRepo.data_config_vars(mainUrl);
		List<TableHeader> uiHeaders = headers();
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
				ret = variablesLoad(vn, url, ret);
				if (auxUrl != null) { // persons
					if (auxUrl.length() > 0) {
						ret = variablesLoad(auxUrl, auxUrl, ret);
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
				ret = variablesLoad(rUrl, dataConfigUrl, ret);
			}
		}

		return ret;
	}
	/**
	 * Create table headers
	 * @return
	 */
	private List<TableHeader> headers() {
		List<String> excl = new ArrayList<String>();
		//exclude columns below
		excl.add("ID");
		excl.add("conceptID");
		excl.add("Discriminator");
		List<TableHeader> uiHeaders = jdbcRepo.headersFromSelect("select * from data_config_vars where false", excl);
		return uiHeaders;
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
	 * Load data import thing
	 * @param user
	 * @param data
	 * @throws ObjectNotFoundException 
	 */
	public ThingDTO importLoad(UserDetailsDTO user, ThingDTO data) throws ObjectNotFoundException {
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
	@Transactional(rollbackFor = ObjectNotFoundException.class)
	public ThingDTO importRun(UserDetailsDTO user, ThingDTO data) throws ObjectNotFoundException{
		data.clearErrors();
		Concept parent=closureServ.loadRoot(data.getUrl());
		data.setParentId(parent.getID());
		data= thingServ.saveUnderParent(data, user);
		if(data.isValid()) {
			XSSFWorkbook wb = loadDataConfiguration(data);
			if(wb != null) {
				wb=importDataConfiguration(data,wb);
			}
		}
		return data;
	}
	/**
	 * Import data configuration for the primary e-form
	 * It should be on the second sheet
	 * Supposed also that the column order in Assembly table is the same as in Assembly table and starts from "Required"
	 * @param data 
	 * @param wb
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private XSSFWorkbook importDataConfiguration(ThingDTO data, XSSFWorkbook wb) throws ObjectNotFoundException {
		DataCollectionDTO collDto = new DataCollectionDTO();
		try {
			XSSFSheet sheet = wb.getSheetAt(1);									//configuration of the first data page 
			collDto = importDataCollection(sheet,0);	// get URL and description
			collDto=superServ.dataCollectionDefinitionSave(collDto);		// save URL and description
			//read the Excel sheet into TableQtb rows
			TableQtb table = new TableQtb();
			jdbcRepo.data_config_vars(collDto.getUrl().getValue());	//should not be existed :)
			List<TableHeader> headers = headers();
			table.getHeaders().getHeaders().addAll(headers);
			ExcelViewMult excel = new ExcelViewMult();
			table = excel.readRows(sheet, 3, 0, table);
			variablesImport(collDto, table);
		} catch (Exception e) {
			if(!(e instanceof ObjectNotFoundException)) {
				e.printStackTrace();
			}
			throw new ObjectNotFoundException(mess.get("error_bmpFile") +": "+e.getMessage(), logger);
		}
		return wb;
	}
	/**
	 * It is assumed that the data collection metadata (URL and description) is saved
	 * Each variable should be saved as a concept and a record in the assemblies table linked to it
	 * for header's keys please refer to the stored procedure data_config_vars
	 * @param collDto
	 * @param table
	 * @throws ObjectNotFoundException 
	 */
	private void variablesImport(DataCollectionDTO collDto, TableQtb table) throws ObjectNotFoundException {
		List<Assembly> good = new ArrayList<Assembly>();
		for(TableRow row : table.getRows()) {
			Concept concept = saveConcept(collDto.getNodeId(),row);
			Assembly assm=assemblyRender(concept, row);
			DataVariableDTO dto = new DataVariableDTO();
			dto.setNodeId(collDto.getNodeId());
			dto = dtoServ.assemblyToDataVariableDto(assm, dto);
			validServ.variable(dto, true, true);
			if(dto.isValid() || !dto.isStrict()) {
				good.add(assm);
			}else {
				throw new ObjectNotFoundException(mess.get(dto.getIdentifier()) + " "+row.toString(),logger);
			}
		}
		//if all are good, save all
		for(Assembly assm : good) {
			assm=boilerServ.assemblySave(assm);
		}
	}

	/**
	 * Render assembly entity from a table row
	 * It is assumed that headers contains column names exactly as in the database
	 * @param concept concept to which assembly should be linked
	 * @param row data to render
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private Assembly assemblyRender(Concept concept, TableRow row) throws ObjectNotFoundException {
		Assembly assm = new Assembly();
		assm.setPropertyName(concept);
		assm=(Assembly) entityServ.renderFromQtbTable(row, assm);
		return assm;
	}
	/**
	 * Save a concept and literals based on row data
	 * @param dataConfigID 
	 * @param row
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private Concept saveConcept(long dataConfigID, TableRow row) throws ObjectNotFoundException {
		Concept parent=closureServ.loadConceptById(dataConfigID);
		Concept variableConcept = new Concept();
		String varName= row.getCellByKey("varname").getValue();
		String varNameExt=row.getCellByKey("varnameext").getValue();
		String decription=row.getCellByKey("descr").getValue();
		DataVariableDTO dto = new DataVariableDTO();
		dto.getVarName().setValue(varName);
		dto.getVarNameExt().setValue(varNameExt);
		dto=validServ.variableName(dto, true);
		if(dto.isValid()) {
			dto = validServ.variableExtName(dto, true);
		}
		dto.propagateValidation();
		if(dto.isValid()) {
			variableConcept.setIdentifier(varName);
			variableConcept.setActive(true);
			variableConcept.setLabel(varNameExt);
			variableConcept = closureServ.saveToTree(parent, variableConcept);
			variableConcept=literalServ.prefAndDescription(decription, decription, variableConcept);
			return variableConcept;
		}else {
			throw new ObjectNotFoundException("varname=["+varName+"],varNAmeExt=["+varNameExt+"]",logger);
		}
	}
	/**
	 * Import data collection definition - URL and description 
	 * @param sheet
	 * @param i
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private DataCollectionDTO importDataCollection(XSSFSheet sheet, int i) throws ObjectNotFoundException {
		DataCollectionDTO ret= new DataCollectionDTO();
		String url = importUrl(sheet);
		if(url.length()>0) {
			url=adjustDataConfigUrl(url);
			ret.setUrl(FormFieldDTO.of(url));
			String description=boilerServ.getStringCellValue(sheet.getRow(1), 0);
			ret.setDescription(FormFieldDTO.of(description));

		}else {
			throw new ObjectNotFoundException(mess.get("error_bmpFile"));
		}
		return ret;
	}
	/**
	 * Recursive adjust data configuration URL
	 * @param url
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private String adjustDataConfigUrl(String url) throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot(SystemService.DATA_COLLECTIONS_ROOT);
		List<Concept> confs = literalServ.loadOnlyChilds(root);
		url=uniqueDataConfigUrl(confs, url);
		return url;
	}
	private String uniqueDataConfigUrl(List<Concept> confs, String url) {
		for(Concept conf : confs) {
			if(conf.getActive()) {
				if(conf.getIdentifier().equalsIgnoreCase(url)) {
					url=url+".copy";
					url=uniqueDataConfigUrl(confs, url);
					break;
				}
			}
		}
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
							throw new ObjectNotFoundException(mess.get("upload_xlsx_data"),logger);
						}
					}else {
						throw new ObjectNotFoundException(mess.get("upload_xlsx_data"),logger);
					}
				}
			}
		}else {
			throw new ObjectNotFoundException(mess.get("upload_xlsx_data"),logger);
		}
		return ret;
	}
	/**
	 * File name for variables configuration export should be URL of the data configuratuon root
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public String variablesExportFileName(DataConfigDTO data) throws ObjectNotFoundException {
		String ret = "dataconfiguration.xlsx";
		if(data.getNodeId()>0) {
			Concept root = closureServ.loadConceptById(data.getNodeId());
			ret = root.getIdentifier()+".xlsx";
		}
		return ret;
	}



}
