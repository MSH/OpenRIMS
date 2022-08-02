package org.msh.pharmadex2.service.r2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.controller.r2.ExcelViewMult;
import org.msh.pharmadex2.dto.ApplicationEventsDTO;
import org.msh.pharmadex2.dto.ApplicationHistoryDTO;
import org.msh.pharmadex2.dto.CheckListDTO;
import org.msh.pharmadex2.dto.DataCollectionDTO;
import org.msh.pharmadex2.dto.DataConfigDTO;
import org.msh.pharmadex2.dto.ReportConfigDTO;
import org.msh.pharmadex2.dto.ReportDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Responsible for applications report, data changes history etc
 * @author alexk
 *
 */
@Service
public class ReportService {
	private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private AssemblyService assmServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private AccessControlService accessControl;
	@Autowired
	private AmendmentService amendServ;
	@Autowired
	private Messages mess;
	@Autowired
	private SystemService systemServ;
	@Autowired
	private ApplicationService applServ;
	@Autowired
	private RegisterService registerServ;
	@Autowired
	private DWHService dwhServ;
	@Autowired
	private DictService dictServ;
	
	@Value( "${link.report.datastudio.pharms:\"\"}" )
	public String linkReport;

	/**
	 * Load a report
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ReportDTO load(UserDetailsDTO user, ReportDTO data) throws ObjectNotFoundException {
		data = table(user,data);
		return data;
	}
	/**
	 * Build and load a report
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ReportDTO table(UserDetailsDTO user, ReportDTO data) throws ObjectNotFoundException {
		if(data.getConfig().getNodeId()>0) {
			ReportConfigDTO repConf = assmServ.reportConfig(data.getConfig());
			data.setConfig(repConf);
			if(repConf.getAddressUrl().length()>0) {
				data = siteReport(data, repConf, user);
			}else {
				data = productReport(user, data, repConf);
			}
		}else {
			data = cleanData(data);
		}
		return data;
	}

	/**
	 * Very Simple product report
	 * @param user
	 * @param data
	 * @param repConf
	 * @param user2
	 * @return
	 */
	@Transactional
	private ReportDTO productReport(UserDetailsDTO user, ReportDTO data, ReportConfigDTO repConf) {
		//get headers
		TableQtb table = data.getTable();
		Headers headers= headersProduct(table.getHeaders(), user);
		if(table.getHeaders().getHeaders().size()!=headers.getHeaders().size()) {
			table.setHeaders(headers);
		}else {
			if(table.getHeaders().getHeaders().get(0).getKey().equals(headers.getHeaders().get(0).getKey())) {
				table.setHeaders(headers);
			}
		}
		//get data
		jdbcRepo.productReport(repConf.getDataUrl(), repConf.getDictStageUrl(),repConf.getApplicantUrl(), repConf.getRegisterAppUrl());
		//applicant may see only own or not?
		String where = "";
		if(repConf.isApplicantRestriction()) {
			if(accessControl.isApplicant(user)) {
				where = "email='"+user.getEmail()+"'";
			}
		}
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from report_products", "", where, table.getHeaders());
		TableQtb.tablePage(rows, table);
		return data;
	}
	/**
	 * Create headers for simple product reports
	 * @param headers
	 * @param user 
	 * @return
	 */
	private Headers headersProduct(Headers headers, UserDetailsDTO user) {
		headers.getHeaders().clear();
		int prefHeader=TableHeader.COLUMN_LINK;
		if(user.getGranted().size()==0) {
			prefHeader=TableHeader.COLUMN_STRING;
		}
		headers.getHeaders().add(TableHeader.instanceOf(
				"pref",
				"prefLabel",
				true,
				true,
				true,
				prefHeader,
				40));
		headers.getHeaders().add(TableHeader.instanceOf(
				"applicant",
				"applicant",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				20));
		headers.getHeaders().add(TableHeader.instanceOf(
				"registered",
				"registered",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				20));
		headers.getHeaders().add(TableHeader.instanceOf(
				"register",
				"reg_number",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				20));
		headers.getHeaders().add(TableHeader.instanceOf(
				"validto",
				"valid_to",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				20));
		headers=boilerServ.translateHeaders(headers);
		headers.setPageSize(20);
		return headers;
	}
	/**
	 * Simple site report
	 * @param data
	 * @param repConf
	 * @param user 
	 * @return
	 */
	public ReportDTO siteReport(ReportDTO data, ReportConfigDTO repConf, UserDetailsDTO user) {
		if(repConf.isDeregistered()) {
			data=siteDeregisteredTable(data,repConf,user);
		}else {
			data=siteExistingTable(data, repConf, user);
		}
		return data;
	}

	/**
	 * Load report for de-registered 
	 * @param data
	 * @param repConf
	 * @return
	 */
	private ReportDTO siteDeregisteredTable(ReportDTO data, ReportConfigDTO repConf, UserDetailsDTO user) {
		TableQtb table = data.getTable();
		if(table.getHeaders().getHeaders().size()==0) {
			Headers headers= headersDeRegisteredSite(table.getHeaders(),user);
			table.setHeaders(headers);
		}
		String where="";
		/*if(accessControl.isApplicant(user)) {
			where="email='"+user.getEmail()+"'";
		}*/
		jdbcRepo.report_deregister(repConf.getAddressUrl(), repConf.getRegisterAppUrl());
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from report_deregister", "", where, table.getHeaders());
		TableQtb.tablePage(rows, table);
		table.setSelectable(false);
		return data;
	}

	/**
	 * Headers for de-registered sites
	 * @param headers
	 * @return
	 */
	private Headers headersDeRegisteredSite(Headers headers, UserDetailsDTO user) {
		headers.getHeaders().clear();
		int prefHeader=TableHeader.COLUMN_LINK;
		if(user.getGranted().size()==0) {
			prefHeader=TableHeader.COLUMN_STRING;
		}
		headers.getHeaders().add(TableHeader.instanceOf(
				"deregistered",
				"deregistration",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				11));
		headers.getHeaders().add(TableHeader.instanceOf(
				"pref",
				"prefLabel",
				true,
				true,
				true,
				prefHeader,
				40));
		headers.getHeaders().add(TableHeader.instanceOf(
				"address",
				"address",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				60));
		headers.getHeaders().add(TableHeader.instanceOf(
				"appl",
				"prod_app_type",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				20));
		headers=boilerServ.translateHeaders(headers);
		headers.getHeaders().get(0).setSort(true);
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		headers.setPageSize(20);
		return headers;
	}
	/**
	 * Registered facility
	 * @param data
	 * @param repConf
	 * @return
	 */
	@Transactional
	private ReportDTO siteExistingTable(ReportDTO data, ReportConfigDTO repConf, UserDetailsDTO user) {
		TableQtb table = data.getTable();
		if(table.getHeaders().getHeaders().size()==0) {
			Headers headers= headersRegisteredSite(table.getHeaders(), user);
			table.setHeaders(headers);
		}
		jdbcRepo.report_sites(repConf.getDataUrl(), repConf.getDictStageUrl(), repConf.getAddressUrl(), repConf.getOwnerUrl(),
				repConf.getInspectAppUrl(), repConf.getRenewAppUrl(), repConf.getRegisterAppUrl());
		String where="";
		/*if(accessControl.isApplicant(user)) {
			where="email='"+user.getEmail()+"'";
		}*/
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from report_sites", "", where, table.getHeaders());
		TableQtb.tablePage(rows, table);
		table.setSelectable(false);
		return data;
	}

	/**
	 * Report table headers for registered facility 
	 * @param headers
	 * @return
	 */
	private Headers headersRegisteredSite(Headers headers, UserDetailsDTO user) {
		headers.getHeaders().clear();
		int prefHeader=TableHeader.COLUMN_LINK;
		if(user.getGranted().size()==0) {
			prefHeader=TableHeader.COLUMN_STRING;
		}
		headers.getHeaders().add(TableHeader.instanceOf(
				"pref",
				"prefLabel",
				true,
				true,
				true,
				prefHeader,
				40));
		headers.getHeaders().add(TableHeader.instanceOf(
				"address",
				"address",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				60));
		headers.getHeaders().add(TableHeader.instanceOf(
				"regno",
				"reg_number",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				20));
		headers.getHeaders().add(TableHeader.instanceOf(
				"owners",
				"owners",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				30));
		headers.getHeaders().add(TableHeader.instanceOf(
				"regdate",
				"registration_date",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				11));

		headers.getHeaders().add(TableHeader.instanceOf(
				"inspdate",
				"inspectiondate",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				11));
		headers.getHeaders().add(TableHeader.instanceOf(
				"renewvaldate",
				"ProdAppType.RENEW",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				11));
		headers.getHeaders().add(TableHeader.instanceOf(
				"expdate",
				"expiry_date",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				11));
		headers=boilerServ.translateHeaders(headers);
		headers.setPageSize(20);
		return headers;
	}
	/**
	 * Clean table and thing
	 * @param data
	 * @return
	 */
	private ReportDTO cleanData(ReportDTO data) {
		data.getTable().getHeaders().getHeaders().clear();
		data.getTable().getRows().clear();
		data.setThing(new ThingDTO());
		return data;
	}
	/**
	 * Reset report screen to the root state
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ReportDTO resetRoot(UserDetailsDTO user, ReportDTO data) throws ObjectNotFoundException {
		data=cleanData(data);
		return data;
	}

	/**
	 * ASk for all records in all journals
	 * @param data
	 * @param user 
	 * @return
	 */
	public ApplicationHistoryDTO applicationRegisters(ApplicationHistoryDTO data, UserDetailsDTO user) {
		TableQtb regTable = data.getTable();
		regTable=registerServ.applicationRegistersTable(regTable, data.getNodeId(),false);
		return data;
	}


	/**
	 * Load report configuration or table of reports or both (?)
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ReportConfigDTO reportConfigurationLoad(ReportConfigDTO data) throws ObjectNotFoundException {
		data=reportConfiguratuonTable(data);

		data.setEnabledrenewext(dwhServ.enablesBtn());
		return data;
	}
	/**
	 * load report configuration table
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ReportConfigDTO reportConfiguratuonTable(ReportConfigDTO data) throws ObjectNotFoundException {
		TableQtb table = data.getTable();
		if(!table.hasHeaders()) {
			table.setHeaders(headersReportConfig(table.getHeaders()));
		}
		jdbcRepo.report_configurations();
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from report_configurations", "", "", table.getHeaders());
		TableQtb.tablePage(rows, table);
		table.setSelectable(false);
		if(data.getReport().getUrl().length()==0) {
			data.getReport().setUrl("report.configuration");
			Concept conc = closureServ.loadRoot(data.getReport().getUrl());
			data.getReport().setParentId(conc.getID());
		}
		return data;
	}
	/**
	 * Report configuration table headers
	 * @param headers
	 * @return
	 */
	private Headers headersReportConfig(Headers headers) {
		headers.getHeaders().add(TableHeader.instanceOf(
				"prefLabel",
				"prefLabel",
				true,
				true,
				true,
				TableHeader.COLUMN_LINK,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"application",
				"application",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"stage",
				"stages",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));		
		headers=boilerServ.translateHeaders(headers);
		return headers;
	}

	/**
	 * Create a table with report available for a user given
	 * @param user
	 * @param data
	 * @return
	 */
	@Transactional
	public TableQtb all(UserDetailsDTO user, TableQtb data) {
		if(!data.hasHeaders()) {
			data.setHeaders(headersUserReport(data.getHeaders()));
		}
		String select="";
		if(accessControl.isApplicant(user)) {
			jdbcRepo.report_applicant();
			select="select * from report_applicant";
		}else {
			jdbcRepo.report_user(user.getEmail());
			select="select * from report_user";
		}
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", "", data.getHeaders());
		TableQtb.tablePage(rows, data);
		data.setSelectable(false);
		return data;
	}
	/**
	 * Headers for table of reports available for a user
	 * @param headers
	 * @return
	 */
	private Headers headersUserReport(Headers headers) {
		headers.getHeaders().add(TableHeader.instanceOf(
				"prefLabel",
				"prefLabel",
				true,
				true,
				true,
				TableHeader.COLUMN_LINK,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"description",
				"description",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers=boilerServ.translateHeaders(headers);
		return headers;
	}
	/**
	 * Events for an application data given
	 * @param user
	 * @param data
	 * @return
	 */
	public ApplicationEventsDTO applicationEvents(UserDetailsDTO user, ApplicationEventsDTO data) {
		jdbcRepo.application_events(data.getAppldataid());
		if(data.getTable().getHeaders().getHeaders().size()==0){
			data.getTable().setHeaders(applicationEventsHeaders(data.getTable().getHeaders()));
		}
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from application_events", "","", data.getTable().getHeaders());
		TableQtb.tablePage(rows, data.getTable());
		data.getTable().setSelectable(false);
		return data;
	}
	/**
	 * Headers for aplication events table
	 * @param headers
	 * @return
	 */
	private Headers applicationEventsHeaders(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"eventdate",
				"global_date",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"pref",
				"prefLabel",
				true,
				true,
				true,
				TableHeader.COLUMN_LINK,
				0));
		headers=boilerServ.translateHeaders(headers);
		return headers;
	}
	/**
	 * Application event data
	 * The application event is an information additional to application information, like
	 * <ul>
	 * <li> inspection or review reports
	 * <li> renewal related data - payments, additional documents, etc
	 * <li> modification (amendments)
	 * <li> suspensions and cancellation events
	 * <li
	 * </ul>
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ApplicationEventsDTO applicationEventsData(UserDetailsDTO user, ApplicationEventsDTO data) throws ObjectNotFoundException {
		data = eventDataIds(data);
		if(data.hasEvent()) {
			data=eventDataLoad(data);
		}else {
			data=eventDataCleanUp(data);
		}
		return data;
	}
	/**
	 * Prepare event data
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ApplicationEventsDTO eventDataLoad(ApplicationEventsDTO data) throws ObjectNotFoundException {
		if(data.hasEvent()) {
			if(data.hasActivity()) {
				data=eventDataLoadActivity(data);					//not used yet
			}else {
				data=eventDataLoadAmendment(data);
			}
		}else {
			data=eventDataCleanUp(data);
		}
		return data;
	}
	/**
	 * Load amended and amendment things and compare them
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ApplicationEventsDTO eventDataLoadAmendment(ApplicationEventsDTO data) throws ObjectNotFoundException {
		String prefLabel="";
		data.getLeftThing().setNodeId(data.getOldDataId());
		data.getRightThing().setNodeId(data.getCurrDataId());

		/*


			List<History> hisl = boilerServ.historyAll(amendmentAppRoot);
			if(hisl.size()>0) {
				//amended
				data.getLeftThing().setNodeId(dataIds.get(0));
				//amendment
				data.getRightThing().setModiUnitId(dataIds.get(0));
				data.getRightThing().setNodeId(dataIds.get(1));
				data.getRightThing().setApplDictNodeId(hisl.get(0).getApplDict().getID());
				data.setChecklist(new CheckListDTO());
				Concept amended=closureServ.loadConceptById(dataIds.get(0));
				Concept amendment=closureServ.loadConceptById(dataIds.get(1));
				prefLabel=boilerServ.prefLabelCheck(amendment);
				if(prefLabel.length()==0) {
					prefLabel=boilerServ.prefLabelCheck(amended);
				}
			}
		}*/
		//titles for data
		String title = data.getTitle()+" "+prefLabel;
		data.setTitle(title.trim());
		data.setLeftTitle(mess.get("prev"));
		data.setRigthTitle(mess.get("amendment"));
		return data;
	}
	/**
	 * Load activity data and corresponded checklist
	 * Selected should be activity data ID
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ApplicationEventsDTO eventDataLoadActivity(ApplicationEventsDTO data) throws ObjectNotFoundException {
		data.setLeftThing(new ThingDTO());
		data.getLeftThing().setNodeId(data.getSelected());
		Concept actData=closureServ.loadConceptById(data.getSelected());
		List<History> hisl = boilerServ.historyByActivitydata(actData);
		if(hisl.size()>0) {
			data.getChecklist().setHistoryId(hisl.get(0).getID());
		}else {
			data.getChecklist().setHistoryId(0l);
		}
		//titles for data
		data.setLeftTitle(mess.get("auxiliarydata"));
		data.setRigthTitle(mess.get("checklist"));
		return data;
	}
	/**
	 * Cleanup all data
	 * @param data
	 * @return
	 */
	private ApplicationEventsDTO eventDataCleanUp(ApplicationEventsDTO data) {
		data.setSelected(0);
		data.setOldDataId(0l);
		data.setAmdDataId(0l);
		data.setCurrDataId(0l);
		data.setLeftThing(new ThingDTO());
		data.setRightThing(new ThingDTO());
		data.setChecklist(new CheckListDTO());
		return data;
	}
	/**
	 * Load additional ID's - old data, amendment data, current data
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ApplicationEventsDTO eventDataIds(ApplicationEventsDTO data) throws ObjectNotFoundException {
		if(data.getSelected()>0) {
			jdbcRepo.application_events(data.getAppldataid());
			Headers headers=new Headers();
			headers.setPageSize(Integer.MAX_VALUE);
			headers.getHeaders().add(TableHeader.instanceOf("newdata", TableHeader.COLUMN_LONG));
			List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from application_events", "", "ID="+data.getSelected(), headers);
			if(rows.size()==1) {
				data.setOldDataId(rows.get(0).getDbID());
				Long newID = (Long) rows.get(0).getRow().get(0).getOriginalValue();
				data.setAmdDataId(newID);
			}
			//determine amended data
			Concept amendmentUnit = closureServ.loadConceptById(data.getAmdDataId());
			Concept amedmentAppl = amendServ.amendmentApplicationByAmendmentUnit(amendmentUnit);
			Concept currData = amendServ.amendedConcept(amedmentAppl);
			if(currData != null) {
				data.setCurrDataId(currData.getID());
			}else {
				throw new ObjectNotFoundException("The amended (current) data unit nod found. Application ID/amendment ID="+
						data.getAppldataid()+"/"+data.getAmdDataId(),logger);
			}
		}
		return data;
	}
	/**
	 * Renew report parameters cache - addresses, etc.
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public ReportConfigDTO reportParametersRenew(ReportConfigDTO data) throws ObjectNotFoundException {
		data.setNodeId(data.getReport().getNodeId());
		ReportConfigDTO repConf = assmServ.reportConfig(data);
		if(repConf.getAddressUrl().length()>0) {
			systemServ.storeFullAddress(data.getAddressUrl());
		}
		//to be continue..
		return data;
	}

	/**
	 * Load application history by node ID
	 * @param data
	 * @param user 
	 * @return
	 */
	@Transactional
	public ApplicationHistoryDTO applicationHistory(ApplicationHistoryDTO data, UserDetailsDTO user) {
		if(data.getNodeId()>0) {
			TableQtb table=data.getTable();
			if(!table.hasHeaders()) {
				table.setHeaders(applServ.historyHeaders(table.getHeaders(),user,false));
			}
			jdbcRepo.application_history(data.getNodeId());
			table=applServ.historyTableRows(user, table, false);
			table.setSelectable(false);
		}
		return data;
	}
	/**
	 * Init thingDTO using history ID
	 * @param historyID
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO activityDataLoad(Long historyId, UserDetailsDTO user) throws ObjectNotFoundException {
		ThingDTO dto = new ThingDTO();
		History history = boilerServ.historyById(historyId);
		if(history.getActivityData() != null) {
			dto.setNodeId(history.getActivityData().getID());
			String pref = literalServ.readPrefLabel(history.getActConfig());
			dto.setTitle(pref);
		}else {
			throw new ObjectNotFoundException("activityDataLoad. activityDataID is null. history ID="+historyId,logger);
		}
		return dto;
	}
	
	public Resource excelReport(DataConfigDTO dto) throws ObjectNotFoundException, IOException {
		Map<String, DataCollectionDTO> data = new LinkedHashMap<String, DataCollectionDTO>();
		Concept node = closureServ.loadConceptById(dto.getNodeId());
		
		data = dataConfigurations("root", node.getIdentifier(), data);
		
		Map<String,Map<String,DataCollectionDTO>> model = new LinkedHashMap<String, Map<String,DataCollectionDTO>>();
		model.put("data", data);
		XSSFWorkbook workbook = new XSSFWorkbook();
		ExcelViewMult excel = new ExcelViewMult();
		excel.buildWorkbook(model, workbook);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		workbook.write(out);
		byte[] arr = out.toByteArray();
		workbook.close();
		out.close();
		return new ByteArrayResource(arr);
	}
	
	/**
	 * Recursive load data configurations in form of <varname, dto> for export to Excel and other joys
	 * @param varName - variable name
	 * @param mainUrl url of the data configuration
	 * @param ret - result map - variable name, DTO
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Map<String, DataCollectionDTO> dataConfigurations(String varName, String mainUrl, Map<String, DataCollectionDTO> ret) throws ObjectNotFoundException {
		//get root by mainUrl
		logger.trace(varName+"---->"+mainUrl);
		Concept root=closureServ.loadRoot("configuration.data");
		Concept node=closureServ.findConceptInBranchByIdentifier(root, mainUrl);
		DataCollectionDTO dto = new DataCollectionDTO();
		dto.setNodeId(node.getID());
		dto.getDescription().setValue(literalServ.readPrefLabel(node));
		dto.getUrl().setValue(mainUrl);
		dto.setVarName(varName);
		//variables
		List<String> excl = new ArrayList<String>();
		excl.add("ID");
		excl.add("conceptID");
		excl.add("Discriminator");
		jdbcRepo.data_config_vars(mainUrl);
		List<TableHeader> uiHeaders=jdbcRepo.headersFromSelect("select * from data_config_vars where false", excl);
		dto.getTable().getHeaders().getHeaders().clear();
		dto.getTable().getHeaders().getHeaders().addAll(uiHeaders);
		dto.getTable().getHeaders().setPageSize(Integer.MAX_VALUE);
		List<TableRow> rows=jdbcRepo.qtbGroupReport("select * from data_config_vars", "", "", dto.getTable().getHeaders());
		dto.getTable().getRows().clear();
		dto.getTable().getRows().addAll(rows);
		//add to the result
		ret.put(mainUrl, dto);
		//things inside
		for(TableRow row :rows) {
			String clazz=row.getCellByKey("clazz").getValue();
			String vn=row.getCellByKey("varname").getValue();
			//Things and persons
			if(clazz.equalsIgnoreCase("things")) {
				String url=row.getCellByKey("url").getValue();
				String auxUrl=row.getCellByKey("AuxDataUrl").getValue();
				ret=dataConfigurations(vn, url, ret);
				if(auxUrl!=null) {	//persons
					if(auxUrl.length()>0) {
						ret=dataConfigurations(auxUrl, auxUrl, ret);
					}
				}
			}
			//check dictionaries
			if(clazz.equalsIgnoreCase("dictionaries") || clazz.equalsIgnoreCase("documents")) {
				String url = row.getCellByKey("Url").getValue();
				if(clazz.equalsIgnoreCase("documents")) {
					url=row.getCellByKey("dictUrl").getValue();
				}
				ret=dictConfigurations(vn, url,ret);
			}
			//check resources
			if(clazz.equalsIgnoreCase("resources")) {
				String rUrl=row.getCellByKey("Url").getValue();
				Concept rRoot = closureServ.loadRoot("configuration.resources");
				Concept rLang=closureServ.findConceptInBranchByIdentifier(rRoot, LocaleContextHolder.getLocale().toString().toUpperCase());
				Concept rNode=closureServ.findConceptInBranchByIdentifier(rLang, rUrl);
				String dataConfigUrl=rNode.getLabel();
				ret=dataConfigurations(rUrl, dataConfigUrl, ret);
			}
		}

		return ret;
	}
	/**
	 * Upload a dictionary by URL
	 * @param vn
	 * @param dictUrl
	 * @param ret
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public Map<String, DataCollectionDTO> dictConfigurations(String vn, String dictUrl,
			Map<String, DataCollectionDTO> ret) throws ObjectNotFoundException {
		if(!ret.keySet().contains(dictUrl)) {
			logger.trace("dict ---->"+vn+"---->"+dictUrl);
			//get a dictionary by url
			Concept root = closureServ.loadRoot(dictUrl);
			boolean system=dictServ.checkSystem(root);
			boolean addresses=dictServ.isAdminUnits(root);
			if(!system && !addresses) {
				TableQtb table = new TableQtb();
				createHeadersDict(table.getHeaders());
				table=dictLevel(1, root.getID(), table);

				DataCollectionDTO dto = new DataCollectionDTO();
				dto.setTable(table);
				dto.getUrl().setValue(dictUrl);
				dto.setVarName(vn);
				ret.put(dictUrl,dto);
			}
		}
		return ret;
	}
	/**
	 * Recursive add rows to the table from the next level of a dictionary
	 * @param level
	 * @param table
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private TableQtb dictLevel(int level, long parentID, TableQtb table) throws ObjectNotFoundException {
		//create a table from dictionary literals and the first level
		jdbcRepo.dict_level_ext(parentID);
		TableQtb table1 = jdbcRepo.queryAndPivot("select * from dict_level_ext");
		if(table.getHeaders().getHeaders().size()==0) {
			table.getHeaders().getHeaders().add(0, TableHeader.instanceOf("level", TableHeader.COLUMN_LONG));
			table.getHeaders().getHeaders().addAll(table1.getHeaders().getHeaders());
		}
		for(TableRow row1 : table1.getRows()) {
			TableCell cell = TableCell.instanceOf("level");
			TableRow row = TableRow.instanceOf(row1.getDbID());
			cell.setOriginalValue(level);
			cell.setValue(level+"");
			row.getRow().add(0, cell);
			row.getRow().addAll(row1.getRow());
			table.getRows().add(row);
			//recursive call for the next level
			table=dictLevel(level+1,row.getDbID(), table);
		}
		return table;
	}
	
	public String getLinkReport() {
		return linkReport;
	}
	
	/**
	 * Create dictionary table headers
	 * @param ret 
	 * @param readOnly 
	 * @return
	 */
	public Headers createHeadersDict(Headers ret) {
		ret.getHeaders().clear();

		ret.getHeaders().add(TableHeader.instanceOf(
				"level", 
				"level",
				true,
				true,
				true,
				TableHeader.COLUMN_LONG,
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				"prefLabel", 
				"prefLabel",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				"description", 
				"description",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		ret.getHeaders().add(TableHeader.instanceOf(
				"URL", 
				"URL",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		ret= boilerServ.translateHeaders(ret);
		return ret;
	}

}
