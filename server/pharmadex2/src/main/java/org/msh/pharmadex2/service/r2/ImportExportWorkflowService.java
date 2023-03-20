package org.msh.pharmadex2.service.r2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.enums.YesNoNA;
import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.FileResource;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.controller.r2.ExcelViewMult;
import org.msh.pharmadex2.dto.DataCollectionDTO;
import org.msh.pharmadex2.dto.DataVariableDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.FileDTO;
import org.msh.pharmadex2.dto.LayoutCellDTO;
import org.msh.pharmadex2.dto.LayoutRowDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.WorkflowDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.DtoService;
import org.msh.pharmadex2.service.common.EntityService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Export ThingDTO into Excel table
 * Import ThingDTO from the data exported to the Excel table
 * @author alexk
 *
 */
@Service
public class ImportExportWorkflowService {
	private static final Logger logger = LoggerFactory.getLogger(ImportExportDataConfigService.class);
	@Autowired
	private DtoService dtoServ;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private DictService dictServ;
	@Autowired
	private Messages mess;
	@Autowired
	private ThingService thingServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private LiteralService literalServ;;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private ValidationService validServ;
	@Autowired
	private EntityService entityServ;
	/**
	 * Export workflow configuration to MS Excel
	 * @param user 
	 * @param data
	 * @return
	 * @throws IOException 
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Resource workflowExportExcel(UserDetailsDTO user, WorkflowDTO dto) throws IOException, ObjectNotFoundException {
		// load all workflow configuration things
		for(ThingDTO tdto : dto.getPath()) {
			tdto=thingServ.loadThing(tdto, user);
			/*Concept node=closureServ.loadConceptById(tdto.getNodeId());
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
			}*/
		}
		ExcelViewMult excel = new ExcelViewMult();
		XSSFWorkbook workbook=excel.workbookForWorkflowConfiguration(dto, mess, closureServ);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);
		byte[] arr = out.toByteArray();
		workbook.close();
		out.close();
		return new ByteArrayResource(arr);
	}
	/**
	 * Load data import thing Workflow
	 * @param user
	 * @param data
	 * @throws ObjectNotFoundException 
	 */
	public ThingDTO importWorkflow(UserDetailsDTO user, ThingDTO data) throws ObjectNotFoundException {
		//load and save only one and only under the root of the tree
		data.setUrl(AssemblyService.SYSTEM_IMPORT_DATA_WORKFLOW);	//the root of the tree
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
	 * Run data configuration workflow import
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws JsonProcessingException 
	 */
	@Transactional(rollbackFor = ObjectNotFoundException.class)
	public ThingDTO importRun(UserDetailsDTO user, ThingDTO data) throws ObjectNotFoundException{
		data.clearErrors();
		FormFieldDTO<Long> dataIDF=data.getNumbers().get("dataID");
		if(dataIDF!=null && dataIDF.getValue()>0) {
			long dataID=dataIDF.getValue();
			Concept co=closureServ.loadConceptById(dataID);
			List<Concept> listCo=literalServ.loadOnlyChilds(co);
			deActiveAction(listCo);
				 XSSFWorkbook wb = loadDataWorkflow(data); 
				 if(wb != null) { 
					 data=importDataWorkflow(dataID,wb, user, data); 
					 } 
		}else {
			data.addError(mess.get("error_dataID"));
		}
		return data;
	}
	
	public void deActiveAction(List<Concept> listCo) {
		for(Concept co:listCo) {
			co.setActive(false);
			co=closureServ.save(co);
			deActiveAction(literalServ.loadOnlyChilds(co));
		}
	}
	
	/**
	 * Load workbook
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws  
	 */
	@Transactional
	private XSSFWorkbook loadDataWorkflow(ThingDTO data) throws ObjectNotFoundException {
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
	 * Import data configuration for the primary e-form
	 * It should be on the second sheet
	 * Supposed also that the column order in Assembly table is the same as in Assembly table and starts from "Required"
	 * @param data 
	 * @param wb
	 * @param user 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ThingDTO importDataWorkflow(long dataID, XSSFWorkbook wb, UserDetailsDTO user, ThingDTO data) throws ObjectNotFoundException {
		ThingDTO act= new ThingDTO();
		try {
			act.setNodeId(dataID);
			act.setUrl("activity.configuration");
			act=thingServ.createContent(act, user);
		
			XSSFSheet sheet0=wb.getSheetAt(0);
			act = cteateActionWF(user, act, sheet0);
			String error=validThing(act);
			if(!error.isEmpty()) {
				data.setIdentifier(" Sheet "+sheet0.getSheetName()+" - "+error);
				data.setValid(false);
				return data;
			}
			
			 long nodeID=act.getNodeId(); 
			 for(int i=1; i<wb.getNumberOfSheets(); i++) {
			 sheet0=wb.getSheetAt(i); 
			 ThingDTO actNext= new ThingDTO(); 
			 actNext.setUrl("activity.configuration");
			 actNext=thingServ.createContent(actNext, user);
			 actNext.setParentId(nodeID); 
			 actNext =cteateActionWF(user, actNext, sheet0); 
			 error=validThing(actNext);
				if(!error.isEmpty()) {
					data.setIdentifier(" Sheet "+sheet0.getSheetName()+" - "+error);
					data.setValid(false);
					return data;
				}
			 nodeID=actNext.getNodeId(); }
		} catch (Exception e) {
			if(!(e instanceof ObjectNotFoundException)) {
				e.printStackTrace();
			}
			throw new ObjectNotFoundException(mess.get("error_bmpFile") +": "+e.getMessage(), logger);
		}
		return act;
	}
	
	private String validThing (ThingDTO act) {
		String error="";
		
		if(!act.isValid()) {
			for(String key:act.getLiterals().keySet()) {
				FormFieldDTO<String> dto=act.getLiterals().get(key);
				if(dto.isError()) {
					error=error+" "+key+": "+dto.getSuggest();
				}
			}
			for(String key:act.getStrings().keySet()) {
				FormFieldDTO<String> dto=act.getStrings().get(key);
				if(dto.isError()) {
					error=error+" "+key+": "+dto.getSuggest();
				}
			}
			for(String key:act.getLogical().keySet()) {
				FormFieldDTO<OptionDTO> dto=act.getLogical().get(key);
				if(dto.isError()) {
					error=error+" "+key+": "+dto.getSuggest();
				}
			}
			for(String key:act.getDictionaries().keySet()) {
				DictionaryDTO dto=act.getDictionaries().get(key);
				if(!dto.isValid()) {
					error=error+" "+key+": "+dto.getIdentifier();
				}
			}
		}
		
		return error;
	}
	private ThingDTO cteateActionWF(UserDetailsDTO user, ThingDTO act, XSSFSheet sheet0) throws ObjectNotFoundException {
		List<LayoutRowDTO> rows= act.getLayout();
		int rowNo=ExcelViewMult.START_THING_CONTENT_ROW;
		int colNo=1;
		for(LayoutRowDTO row:rows) {
			for(LayoutCellDTO cell : row.getCells()) {
				for(String varName :cell.getVariables()) {
					String str=boilerServ.getStringCellValue(sheet0.getRow(rowNo), colNo);
					//System.out.println(str +" vn="+varName+ " col="+colNo+ " row="+rowNo);
					FormFieldDTO<String> lit= act.getLiterals().get(varName);
					if(act.getLiterals().get(varName)!=null) {
						act.getLiterals().get(varName).setValue(str);
					}else if(act.getStrings().get(varName)!=null){
						act.getStrings().get(varName).setValue(str);
					}else if(act.getLogical().get(varName)!=null){
						YesNoNA.valueOf(str);
						OptionDTO opt = dtoServ.enumToOptionDTO(YesNoNA.valueOf(str), YesNoNA.values());
						act.getLogical().get(varName).setValue(opt);
					}else if(act.getDictionaries().get(varName)!=null) {
						DictionaryDTO dic=act.getDictionaries().get(varName);
						List <TableRow> trs=dic.getTable().getRows();
						for(TableRow tr:trs) {
							List <TableCell> cells=tr.getRow();
							for(TableCell c:cells) {
								tr.setSelected(false);
								if(((String) c.getOriginalValue()).equalsIgnoreCase(str)) {
									tr.setSelected(true);
								}
							}
						}
						dic.getPrevSelected().clear();
						Concept root = closureServ.loadRoot(dic.getUrl());
						List<Concept> listCon=literalServ.loadOnlyChilds(root);
						for(Concept con:listCon) {
							if(con.getIdentifier().equalsIgnoreCase(str)){
								dic.getPrevSelected().add(con.getID());
							}
						}
						
					}
					rowNo++;
				}
				colNo++;
				colNo++;
				rowNo=ExcelViewMult.START_THING_CONTENT_ROW;
			}
			colNo=1;
		}
		act.setStrict(true);
		act=thingServ.thingSaveUnderParent(act, user);
		return act;
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
			dto = dtoServ.assemblyToDto(assm, dto);
			validServ.variable(dto, true, true);
			if(dto.isValid() || !dto.isStrict()) {
				good.add(assm);
			}else {
				throw new ObjectNotFoundException(row.toString(),logger);
			}
		}
		//if all are good, save all
		for(Assembly assm : good) {
			assm=boilerServ.assemblySave(assm);
		}
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
	
}
