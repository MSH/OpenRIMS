package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.List;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.ApplicationEventsDTO;
import org.msh.pharmadex2.dto.CheckListDTO;
import org.msh.pharmadex2.dto.ReportConfigDTO;
import org.msh.pharmadex2.dto.ReportDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
				data = siteReport(data, repConf);
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
	 * @return
	 */
	private ReportDTO productReport(UserDetailsDTO user, ReportDTO data, ReportConfigDTO repConf) {
		//get headers
		TableQtb table = data.getTable();
		Headers headers= headersProduct(table.getHeaders());
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
	 * @return
	 */
	private Headers headersProduct(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"pref",
				"prefLabel",
				true,
				true,
				true,
				TableHeader.COLUMN_LINK,
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
	 * @return
	 */
	public ReportDTO siteReport(ReportDTO data, ReportConfigDTO repConf) {
		long nodeId=0;	//Thing node ID
		for(TableRow row : data.getTable().getRows()) {
			if(row.getSelected()) {
				nodeId=row.getDbID();
				break;
			}
		}
		if(nodeId==0) {
			data=reportTable(data,repConf);
		}else {
			//TODO data=loadReportThing(data);
		}
		return data;
	}

	/**
	 * Data table for report may be build
	 * @param data
	 * @param repConf
	 * @return
	 */
	private ReportDTO reportTable(ReportDTO data, ReportConfigDTO repConf) {
		//there are two categories of workflow - products and facilities
		if(repConf.getAddressUrl().length()>0) {
			//address exists, so facility
			if(repConf.isRegistered()) {
				//registered and expired
				data=registeredFacilityTable(data, repConf);
			}else {
				//in process of registration
				//TODO data=inProcessFacilityTable(data, repConf);
			}
		}else {
			//no address - product
			//TODO products
		}
		return data;
	}
	/**
	 * Registered facility
	 * @param data
	 * @param repConf
	 * @return
	 */
	private ReportDTO registeredFacilityTable(ReportDTO data, ReportConfigDTO repConf) {
		TableQtb table = data.getTable();
		Headers headers= headersRegisteredFacility(table.getHeaders());
		if(table.getHeaders().getHeaders().size()!=headers.getHeaders().size()) {
			table.setHeaders(headers);
		}else {
			if(table.getHeaders().getHeaders().get(0).getKey().equals(headers.getHeaders().get(0).getKey())) {
				table.setHeaders(headers);
			}
		}
		jdbcRepo.report_sites(repConf.getDataUrl(), repConf.getDictStageUrl(), repConf.getAddressUrl(), repConf.getOwnerUrl(),
				repConf.getInspectAppUrl(), repConf.getRenewAppUrl(), repConf.getRegisterAppUrl());
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from report_sites", "", "", table.getHeaders());
		TableQtb.tablePage(rows, table);
		table.setSelectable(false);
		return data;
	}

	/**
	 * Report table headers for registered facility 
	 * @param headers
	 * @return
	 */
	private Headers headersRegisteredFacility(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"pref",
				"prefLabel",
				true,
				true,
				true,
				TableHeader.COLUMN_LINK,
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
	 * @return
	 */
	public ThingDTO regTable(ThingDTO data) {
		TableQtb regTable = data.getRegTable();
		if(regTable.getHeaders().getHeaders().size()==0) {
			regTable.setHeaders(headersRegTable(regTable.getHeaders()));
		}
		jdbcRepo.report_register(null);
		String select = "SELECT * from report_register";

		List<TableRow> rows= jdbcRepo.qtbGroupReport(select, "order by registered", "ID='"+data.getNodeId()+"'", regTable.getHeaders());
		TableQtb.tablePage(rows, regTable);
		regTable.setSelectable(false);
		boilerServ.translateRows(regTable);
		return data;
	}

	/**
	 * Records from register
	 * @param headers
	 * @return
	 */
	private Headers headersRegTable(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"registered",
				"registration_date",
				true,
				false,
				false,
				TableHeader.COLUMN_LOCALDATE,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"validto",
				"expiry_date",
				true,
				false,
				false,
				TableHeader.COLUMN_LOCALDATE,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"register",
				"reg_number",
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"varname",
				"register_applicant",
				true,
				false,
				false,
				TableHeader.COLUMN_I18,
				0));
		headers.getHeaders().get(0).setSort(true);
		boilerServ.translateHeaders(headers);
		return headers;
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
		List<Long> dataIds=new ArrayList<Long>();
		dataIds = eventDataIds(data, dataIds);
		if(dataIds.size()==2) {
			data=eventDataLoad(dataIds, data);
		}else {
			data=eventDataCleanUp(data);
		}
		return data;
	}
	/**
	 * Prepare event data
	 * @param dataIds
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ApplicationEventsDTO eventDataLoad(List<Long> dataIds, ApplicationEventsDTO data) throws ObjectNotFoundException {
		if(dataIds.size()==2) {
			if(dataIds.get(0).equals(dataIds.get(1))) {
				data=eventDataLoadActivity(data);
			}else {
				data=eventDataLoadAmendment(dataIds,data);
			}
		}else {
			data=eventDataCleanUp(data);
		}
		return data;
	}
	/**
	 * Load amended and amendment things and compare them
	 * @param dataIds
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ApplicationEventsDTO eventDataLoadAmendment(List<Long> dataIds, ApplicationEventsDTO data) throws ObjectNotFoundException {
		if(dataIds.size()==2) {
			Concept amendmentConcept = closureServ.loadConceptById(dataIds.get(1));
			Concept amdApplRoot = amendServ.amendedApplication(amendmentConcept);
			List<History> hisl = boilerServ.historyAll(amdApplRoot);
			if(hisl.size()>0) {
				//amended
				data.getLeftThing().setNodeId(dataIds.get(0));
				//amendment
				data.getRightThing().setModiUnitId(dataIds.get(0));
				data.getRightThing().setNodeId(dataIds.get(1));
				data.getRightThing().setApplDictNodeId(hisl.get(0).getApplDict().getID());
				data.setChecklist(new CheckListDTO());
			}
		}
		//titles for data
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
		data.setLeftThing(new ThingDTO());
		data.setRightThing(new ThingDTO());
		data.setChecklist(new CheckListDTO());
		return data;
	}
	/**
	 * Load ID's for selected data
	 * @param data
	 * @return
	 */
	private List<Long> eventDataIds(ApplicationEventsDTO data, List<Long> result) {
		if(data.getSelected()>0) {
			jdbcRepo.application_events(data.getAppldataid());
			Headers headers=new Headers();
			headers.setPageSize(Integer.MAX_VALUE);
			headers.getHeaders().add(TableHeader.instanceOf("newdata", TableHeader.COLUMN_LONG));
			List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from application_events", "", "ID="+data.getSelected(), headers);
			if(rows.size()==1) {
				result.add(rows.get(0).getDbID());
				Long newID = (Long) rows.get(0).getRow().get(0).getOriginalValue();
				result.add(newID);
			}
		}
		return result;
	}

}
