package org.msh.pharmadex2.service.r2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.controller.common.DocxView;
import org.msh.pharmadex2.dto.ELAssistantBuildDTO;
import org.msh.pharmadex2.dto.ELAssistantSelectDTO;
import org.msh.pharmadex2.dto.ProcessComponentsDTO;
import org.msh.pharmadex2.dto.ResourceDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.ThingValuesDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ELAssistantService {
	private static final Logger logger = LoggerFactory.getLogger(ELAssistantService.class);
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private Messages messages;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private ProcessComponentsService processComp;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private ResolverService resolverServ;
	@Autowired
	private ThingService thingServ;
	/**
	 * Create/reload a table contains certification workflow URLS
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ELAssistantSelectDTO elAssistantWorkflows(ELAssistantSelectDTO data) throws ObjectNotFoundException {
		if(data.getTable().getHeaders().getHeaders().size()==0) {
			data.getTable().setHeaders(createWorkflowHeaders(data.getTable().getHeaders()));
		}else {
			data=workflowSelection(data);
		}
		String select="SELECT url, prefLabel\r\n" + 
				"FROM application_urls";
		String where = "dict='dictionary.guest.applications' and\r\n" + 
				"Lang='" +LocaleContextHolder.getLocale().toString() +"'";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, data.getTable().getHeaders());
		TableQtb.tablePage(rows, data.getTable());
		data.getTable().setSelectable(true);
		String selectedURL=data.getSelectedURL();
		data.setSelectedURL("");
		for(TableRow row :data.getTable().getRows()) {
			String url=row.getCell("url", data.getTable().getHeaders()).getValue();
			if(url.equalsIgnoreCase(selectedURL)) {
				row.setSelected(true);
				data.setSelectedURL(selectedURL);
				break;
			}
		}
		return data;
	}
	/**
	 * Determine selected workflow URL
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ELAssistantSelectDTO workflowSelection(ELAssistantSelectDTO data) throws ObjectNotFoundException {
		data.setSelectedURL("");
		for(TableRow row :data.getTable().getRows()) {
			if(row.getSelected()) {
				data.setSelectedURL(row.getCell("url", data.getTable().getHeaders()).getValue());
				break;
			}
		}
		return data;
	}
	/**
	 * Header for workflow selection table
	 * @param headers
	 * @return
	 */
	private Headers createWorkflowHeaders(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"url",
				messages.get("URL"),
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"prefLabel",
				messages.get("prefLabel"),
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		return headers;
	}
	/**
	 * Build EL and tables
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ELAssistantBuildDTO elAssistantBuild(ELAssistantBuildDTO data) throws ObjectNotFoundException {
		data=buildEL(data);
		data=sourceBuild(data);
		data=representaionBuild(data);
		return data;
	}
	/**
	 * propose representation part of EL
	 * @param data
	 * @return
	 */
	private ELAssistantBuildDTO representaionBuild(ELAssistantBuildDTO data) {
		if(data.getClazz().size()>0) {
			switch (data.getClazz().get(data.getClazz().size()-1)) {
			case "/":
			case "things":
			case "persons":
				data=thingRepresentation(data);
				break;
			case "literals":
			case "strings":
				data=literalsRepresentation(data);
				break;
			case "addresses":
				data=addressRepresentation(data);
				break;
			case "dates":
				data=dateRepresentation(data);
				break;
			case "dictionaries":
				data=dictRepresentation(data);
				break;
			case "documents":
				data=docRepresentation(data);
				break;
			case "droplist":
				data=dropListRepresentation(data);
				break;
			case "intervals":
				data=intervalRepresentation(data);
				break;
			case "links":
				data=linkRrpresentation(data);
				break;
			case "logical":
				data=logicalRepresentation(data);
				break;
			case "numbers":
				data=numberRepresentation(data);
				break;
			case "registers":
				data=registerRepresentation(data);
				break;
			case "schedulers":
				data=schedulerRepresentation(data);
				break;
			default:
				representationClean(data);
			}
			data=selectRepresentation(data);
		}else {
			representationClean(data);
		}

		return data;
	}

	private ELAssistantBuildDTO schedulerRepresentation(ELAssistantBuildDTO data) {
		data.getRepresentations().getRows().clear();
		data.getRepresentations().setHeaders(representationHeaders());
		data.getRepresentations().getRows().add(rootRow(0l,"date", messages.get("scheduler_date")));
		return data;
	}
	private ELAssistantBuildDTO registerRepresentation(ELAssistantBuildDTO data) {
		data.getRepresentations().getRows().clear();
		data.getRepresentations().setHeaders(representationHeaders());
		data.getRepresentations().getRows().add(rootRow(0l,"literal", messages.get("register_literal")));
		data.getRepresentations().getRows().add(rootRow(1l,"registered", messages.get("register_registered")));
		data.getRepresentations().getRows().add(rootRow(2l,"expired", messages.get("register_expired")));
		data.getRepresentations().getRows().add(rootRow(3l,"registeredBS", messages.get("register_registeredbs")));
		data.getRepresentations().getRows().add(rootRow(4l,"expiredBS", messages.get("register_expiredbs")));
		data.getRepresentations().getRows().add(rootRow(5l,"registeredBS1", messages.get("register_registeredbs1")));
		data.getRepresentations().getRows().add(rootRow(5l,"expiredBS1", messages.get("register_expiredbs1")));
		return data;
	}
	private ELAssistantBuildDTO numberRepresentation(ELAssistantBuildDTO data) {
		data.getRepresentations().getRows().clear();
		data.getRepresentations().setHeaders(representationHeaders());
		data.getRepresentations().getRows().add(rootRow(0l,"number", messages.get("number_number")));
		data.getRepresentations().getRows().add(rootRow(0l,"numberBS", messages.get("number_bs")));
		return data;
	}
	private ELAssistantBuildDTO logicalRepresentation(ELAssistantBuildDTO data) {
		data.getRepresentations().getRows().clear();
		data.getRepresentations().setHeaders(representationHeaders());
		data.getRepresentations().getRows().add(rootRow(0l,"choice", messages.get("logical_choice")));
		return data;
	}
	private ELAssistantBuildDTO linkRrpresentation(ELAssistantBuildDTO data) {
		data.getRepresentations().getRows().clear();
		data.getRepresentations().setHeaders(representationHeaders());
		data.getRepresentations().getRows().add(rootRow(0l,"link_role", messages.get("link_role")));
		return data;
	}
	private ELAssistantBuildDTO intervalRepresentation(ELAssistantBuildDTO data) {
		data.getRepresentations().getRows().clear();
		data.getRepresentations().setHeaders(representationHeaders());
		data.getRepresentations().getRows().add(rootRow(0l,"from", messages.get("interval_from")));
		data.getRepresentations().getRows().add(rootRow(1l,"fromBS", messages.get("interval_frombs")));
		data.getRepresentations().getRows().add(rootRow(2l,"fromBS1", messages.get("interval_frombs1")));
		data.getRepresentations().getRows().add(rootRow(3l,"to", messages.get("interval_to")));
		data.getRepresentations().getRows().add(rootRow(4l,"toBS", messages.get("interval_tobs")));
		data.getRepresentations().getRows().add(rootRow(5l,"toBS1", messages.get("interval_tobs1")));
		return data;
	}
	private ELAssistantBuildDTO dropListRepresentation(ELAssistantBuildDTO data) {
		data.getRepresentations().getRows().clear();
		data.getRepresentations().setHeaders(representationHeaders());
		data.getRepresentations().getRows().add(rootRow(0l,"label", messages.get("droplist_label")));
		data.getRepresentations().getRows().add(rootRow(1l,"description", messages.get("droplist_description")));
		return data;
	}
	private ELAssistantBuildDTO docRepresentation(ELAssistantBuildDTO data) {
		data.getRepresentations().getRows().clear();
		data.getRepresentations().setHeaders(representationHeaders());
		data.getRepresentations().getRows().add(rootRow(0l,"filename", messages.get("doc_filename")));
		data.getRepresentations().getRows().add(rootRow(1l,"preflabel", messages.get("doc_preflabels")));
		data.getRepresentations().getRows().add(rootRow(2l,"description", messages.get("doc_description")));
		data.getRepresentations().getRows().add(rootRow(3l,"choice", messages.get("doc_choice")));
		data.getRepresentations().getRows().add(rootRow(4l,"image", messages.get("doc_image")));
		return data;
	}
	private ELAssistantBuildDTO dictRepresentation(ELAssistantBuildDTO data) {
		data.getRepresentations().getRows().clear();
		data.getRepresentations().setHeaders(representationHeaders());
		data.getRepresentations().getRows().add(rootRow(0l,"choice", messages.get("dict_choice")));
		data.getRepresentations().getRows().add(rootRow(1l,"level1", messages.get("dict_level1")));
		data.getRepresentations().getRows().add(rootRow(2l,"level2", messages.get("dict_level2")));
		data.getRepresentations().getRows().add(rootRow(3l,"level3", messages.get("dict_level3")));
		data.getRepresentations().getRows().add(rootRow(4l,"level4", messages.get("dict_level4")));
		data.getRepresentations().getRows().add(rootRow(5l,"office", messages.get("addr_office")));
		return data;
	}
	private ELAssistantBuildDTO dateRepresentation(ELAssistantBuildDTO data) {
		data.getRepresentations().getRows().clear();
		data.getRepresentations().setHeaders(representationHeaders());
		data.getRepresentations().getRows().add(rootRow(0l,"date", messages.get("date_repr")));
		data.getRepresentations().getRows().add(rootRow(1l,"dateBS", messages.get("date_ne_repr")));
		data.getRepresentations().getRows().add(rootRow(2l,"dateBS1", messages.get("date_ne_repr1")));
		data.getRepresentations().getRows().add(rootRow(3l,"years", messages.get("date_years")));
		data.getRepresentations().getRows().add(rootRow(4l,"yearsBS", messages.get("date_years_ne")));
		data.getRepresentations().getRows().add(rootRow(5l,"yearsBS1", messages.get("date_years_ne1")));
		return data;
	}
	private ELAssistantBuildDTO addressRepresentation(ELAssistantBuildDTO data) {
		data.getRepresentations().getRows().clear();
		data.getRepresentations().setHeaders(representationHeaders());
		data.getRepresentations().getRows().add(rootRow(0l,"gis", messages.get("addr_gis")));
		data.getRepresentations().getRows().add(rootRow(1l,"office", messages.get("addr_office")));
		data.getRepresentations().getRows().add(rootRow(2l,"choice", messages.get("addr_choice")));
		data.getRepresentations().getRows().add(rootRow(3l,"level1", messages.get("addr_level1")));
		data.getRepresentations().getRows().add(rootRow(4l,"level2", messages.get("addr_level2")));
		data.getRepresentations().getRows().add(rootRow(5l,"level3", messages.get("addr_level3")));
		data.getRepresentations().getRows().add(rootRow(5l,"level4", messages.get("addr_level4")));
		return data;
	}
	/**
	 * literals
	 * @param data
	 * @return
	 */
	private ELAssistantBuildDTO literalsRepresentation(ELAssistantBuildDTO data) {
		data.getRepresentations().getRows().clear();
		data.getRepresentations().setHeaders(representationHeaders());
		data.getRepresentations().getRows().add(rootRow(0l,"literal", messages.get("fieldtext")));
		return data;
	}
	public void representationClean(ELAssistantBuildDTO data) {
		data.getRepresentations().getRows().clear();
		data.getRepresentations().setHeaders(new Headers());
		data.setRepresentation("");
	}
	/**
	 * Make selection in the representatipn table
	 * @param data
	 * @return
	 */
	private ELAssistantBuildDTO selectRepresentation(ELAssistantBuildDTO data) {
		for(TableRow row : data.getRepresentations().getRows()) {
			if(row.getCellByKey("key").getValue().equalsIgnoreCase(data.getRepresentation())) {
				row.setSelected(!row.getSelected());
			}else {
				row.setSelected(false);
			}
		}
		return data;
	}
	/**
	 * Data representation for thing are @form and @changes
	 * @param data
	 * @return
	 */
	private ELAssistantBuildDTO thingRepresentation(ELAssistantBuildDTO data) {
		if(!data.getSource().get(0).equalsIgnoreCase("this") && !data.getSource().get(0).equalsIgnoreCase("process")) {
			data.getRepresentations().getRows().clear();
			data.getRepresentations().setHeaders(representationHeaders());
			data.getRepresentations().getRows().add(rootRow(0l,ResolverServiceRender.FORM, messages.get("wholedocument")));
			data.getRepresentations().getRows().add(rootRow(0l,ResolverServiceRender.CHANGES, messages.get("amendmentchanges")));
		}
		return data;
	}
	/**
	 * HEaders for representation table
	 * @return
	 */
	private Headers representationHeaders() {
		Headers ret = new Headers();
		ret.getHeaders().add(TableHeader.instanceOf(
				"key",
				messages.get("prefLabel"),
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				"description",
				messages.get("description"),
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		return ret;
	}
	/**
	 * Build EL expression groundd on the user's choice
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ELAssistantBuildDTO buildEL(ELAssistantBuildDTO data) throws ObjectNotFoundException {
		if(data.getSource().size()==0) {
			data.setEl("");
		}else {
			if(data.getSource().get(0).equalsIgnoreCase("/")) {
				List<String> ell= new ArrayList<String>();
				ell.addAll(data.getSource());
				for(int i=0;i<data.getClazz().size();i++) {
					if(data.getClazz().get(i).equalsIgnoreCase("persons")){
						ell.add(i+1, "0/");
					}
				}
				String el ="${" + String.join("/", ell) + "@" + data.getRepresentation()+ "}";
				el=el.replace("//", "/");	//root
				data.setEl(el);
			}
			if(data.getSource().get(0).equalsIgnoreCase("this")) {
				String varName="";
				if(data.getSource().size()==3) {
					varName=data.getSource().get(2);
					data.setThisPageURL(data.getSource().get(1));
				}
				data.setEl("${this/"+varName+"@"+data.getRepresentation()+"}");
			}
			if(data.getSource().get(0).equalsIgnoreCase("process")) {
				String activityUrl=activityURLByActivityDataURL(data);
				if(data.getSource().size()>2) {
					data.setEl("${process/"+activityUrl+"/"+data.getSource().get(2)+"@"+data.getRepresentation()+"}");
				}else {
					data.setEl("${process/"+activityUrl+"@"+data.getRepresentation()+"}");
				}
			}
		}
		return data;
	}
	/**
	 * Get activity URL by activity data URL and workflow URL
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private String activityURLByActivityDataURL(ELAssistantBuildDTO data) throws ObjectNotFoundException {
		String ret = "";
		if(data.getSource().size()>1) {
			String select="SELECT distinct activityURL\r\n" + 
					"FROM activity_configurations";
			String where="workflowURL='"+data.getWorkflowURL()+"' \r\n" + 
					"and activityDataURL='"+data.getSource().get(1)+"'\r\n" + 
					"and Lang='EN_US'";
			Headers headers = new Headers();
			headers.getHeaders().add(TableHeader.instanceOf("activityURL", TableHeader.COLUMN_STRING));
			List<TableRow> rows= jdbcRepo.qtbGroupReport(select, "", where, headers);
			if(!rows.isEmpty()) {
				ret=rows.get(0).getRow().get(0).getValue();
			}
			if(ret.isEmpty()) {
				throw new ObjectNotFoundException("activityURLByActivityDataURL. activity URL is undefined for "+ data.getSource().get(1) ,logger);
			}
		}
		return ret;
	}
	/**
	 * Data Sources level will build using the EL parsed before
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ELAssistantBuildDTO sourceBuild(ELAssistantBuildDTO data) throws ObjectNotFoundException {
		if(data.getSource().size()==0) {
			data=roots(data);					// /, this, or process
		}else {
			if(data.getClazz().size()>0) {
				String clazz=data.getClazz().get(data.getClazz().size()-1);
				switch (clazz) {
				case "/":
				case "things":
				case "persons":
					data=sourceThings(data);
					break;
				case "this":
					data=sourceAllForms(data);
					break;
				case "process":
					data=sourceActivities(data);
					break;
				default:
					sourceClean(data);
				}
			}else {
				sourceClean(data);
			}
		}
		return data;
	}

	/**
	 * List of activities
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private ELAssistantBuildDTO sourceActivities(ELAssistantBuildDTO data) throws ObjectNotFoundException {
		String select="SELECT prefLabel as 'prefLabel', activityDataURL as 'key', 'things' as 'clazz'\r\n" + 
				"FROM activity_configurations";
		String where="workflowURL='" + data.getWorkflowURL() +"'\r\n" + 
				"and Lang='"+LocaleContextHolder.getLocale().toString().toUpperCase()+"'\r\n" + 
				"and LENGTH(activityDataURL)>0";
		if(data.getSources().getHeaders().getHeaders().size()==0) {
			data.getSources().setHeaders(sourceActivitiesHeaders(data.getSources().getHeaders()));
		}
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, data.getSources().getHeaders());
		TableQtb.tablePage(rows, data.getSources());
		return data;
	}
	/**
	 * Headers for "process" data source 
	 * @param headers
	 * @return
	 */
	private Headers sourceActivitiesHeaders(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"prefLabel",
				messages.get("prefLabel"),
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"key",
				messages.get("url"),
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"clazz",
				messages.get("clazz"),
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		return headers;
	}
	/**
	 * All data forms (pages)
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ELAssistantBuildDTO sourceAllForms(ELAssistantBuildDTO data) throws ObjectNotFoundException {
		Long dictNodeID=dictItemIDByWorkflowUrl(data.getWorkflowURL());
		String applDataUrl=dataURLByWorkflowURL(data.getWorkflowURL());
		if(dictNodeID>0 && !applDataUrl.isEmpty()) {
			if(data.getSources().getHeaders().getHeaders().isEmpty()) {
				data.getSources().setHeaders(sourceThingsHeaders());
			}
			//use workflow validator service to get all configured pages
			ProcessComponentsDTO pdto = new ProcessComponentsDTO();
			pdto.setDictNodeID(dictNodeID);
			List<TableRow> rows=processComp.pagesRows(pdto);
			List<TableRow> srcRows = new ArrayList<TableRow>();
			long dbID=1l;
			for(TableRow row : rows) {
				Object good = row.getCellByKey("good").getOriginalValue();
				if(good instanceof Boolean) {
					if((boolean) good) {		//only configured pages
						srcRows.add(sourceRow(dbID, row.getCellByKey("url").getValue(),"things"));
						dbID++;
					}
				}
			}
			TableQtb.tablePage(srcRows, data.getSources());
		}else {
			sourceClean(data);
		}
		return data;
	}
	/**
	 * Sources table row usint external data
	 * @param dbID
	 * @param value
	 * @param clazz
	 * @return
	 */
	private TableRow sourceRow(long dbID, String value, String clazz) {
		TableRow ret = TableRow.instanceOf(dbID);
		ret.getRow().add(TableCell.instanceOf("key", value));
		ret.getRow().add(TableCell.instanceOf("clazz", clazz));
		return ret;
	}
	/**
	 * Clean up source table
	 * @param data
	 */
	private void sourceClean(ELAssistantBuildDTO data) {
		data.getSources().getRows().clear();
		data.getSources().setHeaders(new Headers());
	}
	/**
	 * Data Source is thing on path
	 * @param data 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ELAssistantBuildDTO sourceThings(ELAssistantBuildDTO data) throws ObjectNotFoundException {
		String dataURL=dataURLRestoreFromPath(data);
		if(dataURL.length()>0) {
			if(data.getSources().getHeaders().getHeaders().isEmpty()) {
				data.getSources().setHeaders(sourceThingsHeaders());
			}
			jdbcRepo.assembly_variables(dataURL);
			String select ="select varname as 'key', Clazz as 'clazz' from assembly_variables";
			String where="Clazz !='heading'";
			if(data.getSource().get(0).equalsIgnoreCase("this")) {
				where= "clazz in (\r\n" + 
						"'literals',\r\n" + 
						"'strings',\r\n" + 
						"'numbers',\r\n" + 
						"'dates',\r\n" + 
						"'dictionaries',\r\n" + 
						"'addresses',\r\n" + 
						"'schedulers',\r\n" + 
						"'registers',\r\n" + 
						"'droplist'\r\n" + 
						")";
			}
			String orderBy="order by `Row`, Col";
			List<TableRow> rows = jdbcRepo.qtbGroupReport(select, orderBy, where, data.getSources().getHeaders());
			TableQtb.tablePage(rows, data.getSources());
			data.getSources().setSelectable(data.getRepresentation().length()==0);
		}else {
			throw new ObjectNotFoundException("sourceThings The workflow "+data.getWorkflowURL()+ " has not data definition", logger);
		}
		return data;
	}

	/**
	 * Restore the data URL using source and clazz lists
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private String dataURLRestoreFromPath(ELAssistantBuildDTO data) throws ObjectNotFoundException {
		String ret=dataURLByWorkflowURL(data.getWorkflowURL());
		if(data.getSource().size()>0) {
			if(data.getSource().get(0).equalsIgnoreCase("/")) {
				for(int i=1;i<data.getSource().size();i++) {
					if(data.getClazz().get(i).equalsIgnoreCase("things")) {
						ret=dataURLByVarName(ret,data.getSource().get(i), data.getClazz().get(i));
					}
					if(data.getClazz().get(i).equalsIgnoreCase("persons")) {
						ret=auxDataUrl(i-1, data);
					}
				}
			}else {
				if(data.getSource().get(0).equalsIgnoreCase("this") && data.getSource().size()>1) {
					return data.getSource().get(1);
				}
				if(data.getSource().get(0).equalsIgnoreCase("process") && data.getSource().size()>1) {
					return data.getSource().get(1);
				}
			}
		}else {
			ret=data.getSource().get(data.getSource().size()-1);
		}
		return ret;
	}
	/**
	 * Get aux data url from the path
	 * @param index
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private String auxDataUrl(int index, ELAssistantBuildDTO data) throws ObjectNotFoundException {
		String ret=dataURLByWorkflowURL(data.getWorkflowURL());
		if(data.getSource().size()>index) {
			for(int i=1;i<=index;i++) {
				String clazz=data.getClazz().get(i);
				if(i==index) {
					clazz="persons";
				}
				ret=dataURLByVarName(ret,data.getSource().get(i), clazz);
			}
		}
		return ret;
	}
	/**
	 * Get data url related to the variable from the configuration. It suits for things and persons
	 * @param configurationURL
	 * @param varName
	 * @param clazz 
	 * @return
	 */
	private String dataURLByVarName(String configurationURL, String varName, String clazz) {
		String ret="";
		String select="select Url as 'dataURL', auxDataUrl as 'auxDataUrl' from assembly_variables";
		String where="varname='"+varName+"'" + "and clazz in ('things','persons')";
		jdbcRepo.assembly_variables(configurationURL);
		Headers headers= new Headers();
		headers.getHeaders().add(TableHeader.instanceOf("dataURL", TableHeader.COLUMN_STRING));
		headers.getHeaders().add(TableHeader.instanceOf("auxDataURL", TableHeader.COLUMN_STRING));
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, headers);
		if(rows.size()==1) {
			if(clazz.equalsIgnoreCase("things")) {
				ret = rows.get(0).getCellByKey("dataURL").getValue();
			}else {
				ret = rows.get(0).getCellByKey("auxDataURL").getValue();
			}
		}
		return ret;
	}
	/**
	 * Data source is electronic form definition
	 * @return
	 */
	private Headers sourceThingsHeaders() {
		Headers ret=new Headers();
		ret.getHeaders().add(TableHeader.instanceOf(
				"key",
				messages.get("prefLabel"),
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				"clazz",
				messages.get("clazz"),
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		return ret;
	}
	/**
	 * Application URL by Workflow URL
	 * @param workflowURL
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private String dataURLByWorkflowURL(String workflowURL) throws ObjectNotFoundException {
		String ret="";
		Long dictItemID = dictItemIDByWorkflowUrl(workflowURL);
		if(dictItemID>0) {
			Concept dictItem = closureServ.loadConceptById(dictItemID);
			ret=literalServ.readValue("dataurl", dictItem);
		}
		return ret;
	}

	/**
	 * Get dictionary item ID by workflow URL
	 * @param workflowURL
	 * @return
	 */
	private Long dictItemIDByWorkflowUrl(String workflowURL) {
		Long ret=0l;
		String select = "SELECT dictItemID\r\n" + 
				"FROM application_urls";
		String where="url='"+workflowURL+"'\r\n" + 
				"and Lang='"+LocaleContextHolder.getLocale().toString().toUpperCase()+"'";
		Headers headers= new Headers();
		headers.getHeaders().add(TableHeader.instanceOf("dictItemID", TableHeader.COLUMN_LONG));
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, headers);
		if(rows.size()==1) {
			Object ido=rows.get(0).getCellByKey("dictItemID").getOriginalValue();
			if(ido instanceof Long) {
				ret= (Long) ido;
			}
		}
		return ret;
	}
	/**
	 * Build sources and representations for the data source roots
	 * @param data
	 * @return
	 */
	private ELAssistantBuildDTO roots(ELAssistantBuildDTO data) {
		data.getSources().getRows().clear();
		data.getSources().setHeaders(createRootHeaders());
		data.getSources().getRows().add(rootRow(0l,"/", messages.get("applicationForm")));
		data.getSources().getRows().add(rootRow(1l,"this", messages.get("currentPage")));
		data.getSources().getRows().add(rootRow(2l,"process", messages.get("processData")));
		data.getSources().setSelectable(true);
		return data;
	}
	/**
	 * Row for root sources
	 * @param rowID
	 * @param key
	 * @param description
	 * @return
	 */
	private TableRow rootRow(long rowID, String key, String description) {
		TableRow row = TableRow.instanceOf(rowID);
		row.getRow().add(TableCell.instanceOf("key", key));	//used to build EL
		row.getRow().add(TableCell.instanceOf("description", description));
		return row;
	}
	/**
	 * Headers for data sources root
	 * @return
	 */
	private Headers createRootHeaders() {
		Headers headers = new Headers();
		headers.getHeaders().add(TableHeader.instanceOf(
				"key",							//the value of the column "key" is used to build EL
				messages.get("clazz"),
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"description",
				messages.get("description"),
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		return headers;
	}
	/**
	 * Create docx with EL expression to test, and, then, resolve it using random history id
	 * @param data
	 * @param user 
	 * @return
	 * @throws IOException 
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Resource test(ELAssistantBuildDTO data, UserDetailsDTO user) throws IOException, ObjectNotFoundException {
		// create a test docx
		XWPFDocument docx = new XWPFDocument();
		// add el
		XWPFParagraph tmpParagraph = docx.createParagraph();
		XWPFRun tmpRun = tmpParagraph.createRun();
		tmpRun.setText(data.getEl());
		// add _ERRORS_
		XWPFParagraph errorsP = docx.createParagraph();
		XWPFRun errorsRun = errorsP.createRun();
		errorsRun.setText("${_ERROR_}");
		ResourceDTO fres = testResourceDTO(data, user);
		if(fres.getHistoryId()>0) {
			byte[] inArr= docxToBytes(docx);
			InputStream inStream = new ByteArrayInputStream(inArr);
			DocxView dx = new DocxView(inStream,boilerServ);
			logger.trace("init model");
			Map<String,Object> model = dx.initModel();
			logger.trace("resolve model");
			model = resolverServ.resolveModel(model,fres, user);
			inStream.reset();
			DocxView px = new DocxView(inStream,boilerServ);
			logger.trace("resolve document");
			px.resolveDocument(model, true);
			//out the result
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			px.getDoc().write(outStream);
			px.getDoc().close();
			return new ByteArrayResource( outStream.toByteArray());
		}else {
			//add error message
			XWPFParagraph errorParagraph = docx.createParagraph();
			XWPFRun errorRun = errorParagraph.createRun();
			errorRun.setText(messages.get("testworkflownotfoundfor") +" "+ data.getWorkflowURL());
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			docx.write(outStream);
			docx.close();
			return new ByteArrayResource( outStream.toByteArray());
		}

	}

	/**
	 * Find the test application and create test ResourceDTO for the future computing
	 * Should be modified
	 * @param data
	 * @param user 
	 * @return
	 */
	private ResourceDTO testResourceDTO(ELAssistantBuildDTO data, UserDetailsDTO user) {
		ResourceDTO ret = new ResourceDTO();
		ret.setData(thisPageRandom(data.getThisPageURL(), user));
		History history = historyWorkflowRandom(data.getWorkflowURL(),user);
		if(history.getID()>0) {
			ret.setFileName("eltest.docx");
			ret.setHistoryId(history.getID());
			ret.setMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			ret.setNodeId(history.getApplicationData().getID());
		}else {
			ret.setFileName("eltest.docx");
			ret.setHistoryId(0l);
			ret.setMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			ret.setNodeId(0l);
		}
		return ret;
	}
	/**
	 * Get random page for "this" data source 
	 * @param thisPageURL
	 * @param user 
	 * @return
	 */
	private ThingValuesDTO thisPageRandom(String thisPageURL, UserDetailsDTO user) {
		ThingValuesDTO ret = new ThingValuesDTO();
		String select="SELECT  distinct p.ID as 'pageID'\r\n" + 
				"FROM concept root\r\n" + 
				"left join closure clo on clo.childID=root.ID and clo.`Level`=1\r\n" + 
				"join closure clo1 on clo1.parentID=root.ID and clo1.`Level`=2\r\n" + 
				"join concept p on p.ID=clo1.childID and p.Identifier <> '_LITERALS_' and p.Active";
		String where="clo.ID is null\r\n" + 
				"and root.Identifier='"+thisPageURL +"'";
		Headers headers = new Headers();
		headers.getHeaders().add(TableHeader.instanceOf("pageID", TableHeader.COLUMN_LONG));
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, headers);
		int size = rows.size();
		if(size>0) {
			Random rand= new Random(LocalTime.now().toNanoOfDay());
			int index=rand.nextInt(size);
			TableRow row = rows.get(index);
			Object ido=row.getRow().get(0).getOriginalValue();
			if(ido instanceof Long) {
				try {
					ThingDTO data = new ThingDTO();
					data.setNodeId((long) ido);
					data.setUrl(thisPageURL);
					data = thingServ.loadThing(data, user);
					ret=thingServ.thingValuesExtract(user, data, ret);
				} catch (ObjectNotFoundException e) {
					//nothing to do
				}
			}
		}
		return ret;
	}
	/**
	 * Select a history record for a random application by the workflow URL
	 * @param workflowURL
	 * @param user 
	 * @return
	 */
	private History historyWorkflowRandom(String workflowURL, UserDetailsDTO user) {
		History ret = new History();
		String select="SELECT distinct h.ID as 'HistoryID'\r\n" + 
				"FROM pv_activities act\r\n" + 
				"join history h on h.activityID=act.ActivityID";
		String where="act.ActivityOutcome='APPROVE'\r\n" + 
				"and act.ActivityCompleted\r\n" + 
				"and act.WorkflowURL='"+workflowURL+"'";
		Headers headers = new Headers();
		headers.getHeaders().add(TableHeader.instanceOf("HistoryID", TableHeader.COLUMN_LONG));
		List<TableRow> rows= jdbcRepo.qtbGroupReport(select,"", where, headers);
		int size=rows.size();
		if(size>0) {
			Random rand= new Random(LocalTime.now().toNanoOfDay());
			int index=rand.nextInt(size);
			TableRow row = rows.get(index);
			Object ido=row.getRow().get(0).getOriginalValue();
			if(ido instanceof Long) {
				try {
					ret = boilerServ.historyById((long) ido);
				} catch (ObjectNotFoundException e) {
					//nothing to do
				}
			}
		}
		return ret;
	}
	/**
	 * write docx to byte array
	 * @param docx
	 * @return
	 * @throws IOException 
	 */
	private byte[] docxToBytes(XWPFDocument docx) throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		docx.write(outStream);
		byte[] outArr = outStream.toByteArray();
		return outArr;
	}

}
