package org.msh.pharmadex2.service.r2;

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
import org.msh.pharmadex2.dto.ApplicationHistoryDTO;
import org.msh.pharmadex2.dto.CheckListDTO;
import org.msh.pharmadex2.dto.PublicPermitDTO;
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
 * 
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
	private ApplicationService applServ;
	@Autowired
	private RegisterService registerServ;
	@Autowired
	private ThingService thingServ;

	


	/*
	 * @Value("${link.report.datastudio.pharms:\"\"}") public String linkReport;
	 */


	/**
	 * ASk for all records in all journals
	 * 
	 * @param data
	 * @param user
	 * @return
	 */
	public ApplicationHistoryDTO applicationRegisters(ApplicationHistoryDTO data, UserDetailsDTO user) {
		TableQtb regTable = data.getTable();
		regTable = registerServ.applicationRegistersTable(regTable, data.getNodeId(), false);
		return data;
	}

	/**
	 * Events for an application data given
	 * 
	 * @param user
	 * @param data
	 * @return
	 */
	public ApplicationEventsDTO applicationEvents(UserDetailsDTO user, ApplicationEventsDTO data) {
		jdbcRepo.application_events(data.getAppldataid());
		if (data.getTable().getHeaders().getHeaders().size() == 0) {
			data.getTable().setHeaders(applicationEventsHeaders(data.getTable().getHeaders()));
		}
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from application_events", "", "",
				data.getTable().getHeaders());
		TableQtb.tablePage(rows, data.getTable());
		data.getTable().setSelectable(false);
		return data;
	}

	/**
	 * Headers for aplication events table
	 * 
	 * @param headers
	 * @return
	 */
	private Headers applicationEventsHeaders(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(
				TableHeader.instanceOf("eventdate", "global_date", true, true, true, TableHeader.COLUMN_LOCALDATE, 0));
		headers.getHeaders()
		.add(TableHeader.instanceOf("pref", "prefLabel", true, true, true, TableHeader.COLUMN_LINK, 0));
		headers = boilerServ.translateHeaders(headers);
		return headers;
	}

	/**
	 * Application event data The application event is an information additional to
	 * application information, like
	 * <ul>
	 * <li>inspection or review reports
	 * <li>renewal related data - payments, additional documents, etc
	 * <li>modification (amendments)
	 * <li>suspensions and cancellation events <li
	 * </ul>
	 * 
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ApplicationEventsDTO applicationEventsData(UserDetailsDTO user, ApplicationEventsDTO data)
			throws ObjectNotFoundException {
		data = eventDataIds(data);
		if (data.hasEvent()) {
			data = eventDataLoad(data);
		} else {
			data = eventDataCleanUp(data);
		}
		return data;
	}

	/**
	 * Prepare event data
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private ApplicationEventsDTO eventDataLoad(ApplicationEventsDTO data) throws ObjectNotFoundException {
		if (data.hasEvent()) {
			if (data.hasActivity()) {
				data = eventDataLoadActivity(data); // not used yet
			} else {
				data = eventDataLoadAmendment(data);
			}
		} else {
			data = eventDataCleanUp(data);
		}
		return data;
	}

	/**
	 * Load amended and amendment things and compare them
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private ApplicationEventsDTO eventDataLoadAmendment(ApplicationEventsDTO data) throws ObjectNotFoundException {
		String prefLabel = "";
		data.getLeftThing().setNodeId(data.getOldDataId());
		data.getRightThing().setNodeId(data.getCurrDataId());
		// titles for data
		String title = data.getTitle() + " " + prefLabel;
		data.setTitle(title.trim());
		data.setLeftTitle(mess.get("prev"));
		data.setRigthTitle(mess.get("amendment"));
		return data;
	}

	/**
	 * Load activity data and corresponded checklist Selected should be activity
	 * data ID
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private ApplicationEventsDTO eventDataLoadActivity(ApplicationEventsDTO data) throws ObjectNotFoundException {
		data.setLeftThing(new ThingDTO());
		data.getLeftThing().setNodeId(data.getSelected());
		Concept actData = closureServ.loadConceptById(data.getSelected());
		List<History> hisl = boilerServ.historyByActivitydata(actData);
		if (hisl.size() > 0) {
			data.getChecklist().setHistoryId(hisl.get(0).getID());
		} else {
			data.getChecklist().setHistoryId(0l);
		}
		// titles for data
		data.setLeftTitle(mess.get("auxiliarydata"));
		data.setRigthTitle(mess.get("checklist"));
		return data;
	}

	/**
	 * Cleanup all data
	 * 
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
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ApplicationEventsDTO eventDataIds(ApplicationEventsDTO data) throws ObjectNotFoundException {
		if (data.getSelected() > 0) {
			jdbcRepo.application_events(data.getAppldataid());
			Headers headers = new Headers();
			headers.setPageSize(Integer.MAX_VALUE);
			headers.getHeaders().add(TableHeader.instanceOf("newdata", TableHeader.COLUMN_LONG));
			List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from application_events", "",
					"ID=" + data.getSelected(), headers);
			if (rows.size() == 1) {
				data.setOldDataId(rows.get(0).getDbID());
				Long newID = (Long) rows.get(0).getRow().get(0).getOriginalValue();
				data.setAmdDataId(newID);
			}
			// determine amended data
			Concept amendmentUnit = closureServ.loadConceptById(data.getAmdDataId());
			Concept amedmentAppl = amendServ.amendmentApplicationByAmendmentUnit(amendmentUnit);
			Concept currData = amendServ.amendedConcept(amedmentAppl);
			if (currData != null) {
				data.setCurrDataId(currData.getID());
			} else {
				throw new ObjectNotFoundException(
						"The amended (current) data unit nod found. Application ID/amendment ID=" + data.getAppldataid()
						+ "/" + data.getAmdDataId(),
						logger);
			}
		}
		return data;
	}
	/*
	 * 2022-10-20 we don't need it anymore
	 * 	*//**
	 * Renew report parameters cache - addresses, etc.
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 *//*
				public ReportConfigDTO reportParametersRenew(ReportConfigDTO data) throws ObjectNotFoundException {
				data.setNodeId(data.getReport().getNodeId());
				ReportConfigDTO repConf = assmServ.reportConfig(data);
				if(repConf.getAddressUrl().length()>0) {
					systemServ.storeFullAddress(data.getAddressUrl());
				}
				//to be continue..
				return data;
				}*/

	/**
	 * Load application history by node ID
	 * 
	 * @param data
	 * @param user
	 * @return
	 */
	@Transactional
	public ApplicationHistoryDTO applicationHistory(ApplicationHistoryDTO data, UserDetailsDTO user) {
		if (data.getNodeId() > 0) {
			TableQtb table = data.getTable();
			if (!table.hasHeaders()) {
				table.setHeaders(applServ.historyHeaders(table.getHeaders(), user, false));
			}
			jdbcRepo.application_history(data.getNodeId());
			table = applServ.applicationHistoryTableRows(table, false);
			table.setSelectable(false);
		}
		return data;
	}

	/**
	 * Init thingDTO using history ID
	 * 
	 * @param historyID
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ThingDTO activityDataLoad(Long historyId, UserDetailsDTO user) throws ObjectNotFoundException {
		ThingDTO dto = new ThingDTO();
		History history = boilerServ.historyById(historyId);
		if (history.getActivityData() != null) {
			dto.setNodeId(history.getActivityData().getID());
			String pref = literalServ.readPrefLabel(history.getActConfig());
			dto.setTitle(pref);
		} else {
			throw new ObjectNotFoundException("activityDataLoad. activityDataID is null. history ID=" + historyId,
					logger);
		}
		return dto;
	}

	/**
	 * @throws ObjectNotFoundException 
	 * 
	 */
	@Transactional
	public PublicPermitDTO isReject(UserDetailsDTO user, PublicPermitDTO data) throws ObjectNotFoundException{
		Concept applData = closureServ.loadConceptById(data.getPermitDataID());
		Concept owner=closureServ.getParent(applData);
		data.setReject(false);
		if(!accessControl.isApplicant(user)) {
			String select = "select * from onapproval_app";
			Headers headers = new Headers();
			headers.getHeaders().add(TableHeader.instanceOf("dataModuleId", TableHeader.COLUMN_LONG));
			headers.getHeaders().add(TableHeader.instanceOf("state", TableHeader.COLUMN_STRING));
			String where="dataModuleId="+applData.getID();
			List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, headers);
			if(rows.size()>0 && accessControl.sameEmail(owner.getIdentifier(), user.getEmail())) {
				data.setReject(true);
			}
		}
		return data;
	}
	/*
	 * public String getLinkReport() { return linkReport; }
	 */
	/**
	 * Get public available permit data
	 * @param user 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public PublicPermitDTO permitData(UserDetailsDTO user, PublicPermitDTO data) throws ObjectNotFoundException {
		jdbcRepo.application_history(data.getPermitDataID());
		String select = "select * from application_history where go is not null";
		Headers headers = new Headers();
		TableHeader headerCome = TableHeader.instanceOf("come", TableHeader.COLUMN_LOCALDATE);
		headerCome.setSortValue(TableHeader.SORT_ASC);
		headers.getHeaders().add(headerCome);
		headers.getHeaders().add(TableHeader.instanceOf("go", TableHeader.COLUMN_LOCALDATE));
		headers.getHeaders().add(TableHeader.instanceOf("activityDataID", TableHeader.COLUMN_LONG));
		headers.getHeaders().add(TableHeader.instanceOf("workflow", TableHeader.COLUMN_STRING));
		headers.getHeaders().add(TableHeader.instanceOf("activity", TableHeader.COLUMN_STRING));
		headers.getHeaders().get(1).setSort(true);		//GO
		headers.getHeaders().get(1).setSortValue(TableHeader.SORT_ASC);
		List<TableRow> hisRows = jdbcRepo.qtbGroupReport(select, "", "", headers);
		Concept applData = closureServ.loadConceptById(data.getPermitDataID());
		History his= new History();
		if(his.getID()==0){
			// from the first history record
			List<History> otherHis = boilerServ.historyAllOrderByCome(applData);
			if(!otherHis.isEmpty()) {
				his=otherHis.get(0);
			}
		}
		if(his.getID()==0 && data.getHistoryID()>0) {
			// passed in the parameter
			his = boilerServ.historyById(data.getHistoryID());
		}
		if(his.getID()>0) {
			//the application
			ThingDTO applDTO = applServ.createApplication(his);
			applDTO = thingServ.path(user,applDTO);
			if(!applDTO.getPath().isEmpty()) {
				data.setTitle(applDTO.getApplName());
				data.setDescription(applDTO.getApplDescr());
				data.setApplication(applDTO.getPath());
			}else {
				throw new ObjectNotFoundException("Application is empty. Permti Data ID is "+data.getPermitDataID(),logger);
			}
			//the history
			data.getApplHistory().clear();
			String wf="";
			int i=0;
			for(TableRow hisRow : hisRows) {
				Object actDataID = hisRow.getCellByKey("activityDataID").getOriginalValue();
				ThingDTO dt = new ThingDTO();
				if(!wf.equalsIgnoreCase(hisRow.getCellByKey("workflow").getValue())) {
					wf=hisRow.getCellByKey("workflow").getValue();
					i=i+1;
					dt.setUxIdentifier(String.valueOf(i));
				}else {
					dt.setUxIdentifier(String.valueOf(i));
				}
				String title = hisRow.getCellByKey("go").getValue()
						+ "  "
						+  hisRow.getCellByKey("workflow").getValue()
						+"/"
						+hisRow.getCellByKey("activity").getValue();
				if(actDataID != null && actDataID instanceof Long && (Long)actDataID>0) {
					dt.setNodeId((Long)actDataID);
				}else {
					dt.setNodeId(0L);
				}
				dt.setTitle(title);
				dt.setHistoryId(hisRow.getDbID());
				dt.setReadOnly(true);
				data.getApplHistory().add(dt);
			}
		}else {
			throw new ObjectNotFoundException("History for permit not found. Permti Data ID is "+data.getPermitDataID(),logger);
		}
		data.setGuest(accessControl.isApplicant(user));
		return data;
	}

}
