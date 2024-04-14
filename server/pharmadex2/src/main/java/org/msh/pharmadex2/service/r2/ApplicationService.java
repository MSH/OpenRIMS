package org.msh.pharmadex2.service.r2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
import java.util.List;
//import java.util.Map;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.old.User;
import org.msh.pdex2.model.r2.Assembly;
//import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Checklistr2;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.Scheduler;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingDict;
//import org.msh.pdex2.model.r2.ThingPerson;
//import org.msh.pdex2.model.r2.ThingScheduler;
import org.msh.pdex2.model.r2.ThingThing;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.r2.Checklistr2Repo;
import org.msh.pdex2.repository.r2.HistoryRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.ActivityDTO;
import org.msh.pharmadex2.dto.ActivityHistoryDataDTO;
import org.msh.pharmadex2.dto.ActivitySubmitDTO;
import org.msh.pharmadex2.dto.ActivityToRun;
import org.msh.pharmadex2.dto.ApplicationHistoryDTO;
import org.msh.pharmadex2.dto.ApplicationOrActivityDTO;
import org.msh.pharmadex2.dto.ApplicationsDTO;
//import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.CheckListDTO;
//import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.QuestionDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.WorkflowParamDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.DtoService;
import org.msh.pharmadex2.service.common.EntityService;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Application related services
 * 
 * @author alexk
 *
 */
@Service
public class ApplicationService {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private DictService dictServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private Checklistr2Repo checklistRepo;
	@Autowired
	private AccessControlService accServ;
	@Autowired
	private DtoService dtoServ;
	@Autowired
	private ValidationService validServ;
	@Autowired
	private EntityService entityServ;
	@Autowired
	private UserService userServ;
	@Autowired
	private ThingService thingServ;
	@Autowired
	private Messages messages;
	@Autowired
	private SystemService systemServ;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private AmendmentService amendmentServ;
	@Autowired
	private PubOrgService pubOrgServ;
	@Autowired
	private AssemblyService assmServ;
	@Autowired
	private HistoryRepo historyRepo;
	//@Autowired
	//private MailService mailService;

	/**
	 * recreate only a table for selected applications
	 * 
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ApplicationsDTO applicatonsTable(ApplicationsDTO data, UserDetailsDTO user, String searchStr) throws ObjectNotFoundException {
		if (data.getDictItemId() > 0) {
			Concept dictItem = closureServ.loadConceptById(data.getDictItemId());
			data.setCanAdd(dictItem.getActive());
			String appUrl = literalServ.readValue("applicationurl", dictItem);
			String dataUrl = literalServ.readValue("dataurl", dictItem);
			if(data.isAmendment()) {
				//data=amendmentServ.createAmendmentsTable(dataUrl, user.getEmail(), data, searchStr);
				data.setTable(amendmentServ.createAmendmentsTable(dataUrl, user.getEmail(), data.getTable(), searchStr));
			}else {
				data =createApplicationsTable(appUrl, dataUrl, user.getEmail(), data, searchStr);
			}
		}
		return data;
	}

	/**
	 * Create a table with list of applications
	 * 
	 * @param appUrl
	 * @param email
	 * @param string
	 * @param table
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private ApplicationsDTO createApplicationsTable(String appUrl, String dataUrl, String email, ApplicationsDTO data, String searchStr)
			throws ObjectNotFoundException {
		TableQtb table = data.getTable();
		if(data.isArch()) {
			table=data.getArchive();
		}
		table=headerTableApp(searchStr, table);
		jdbcRepo.applications_applicant(dataUrl,email);
		String select = "select * from applications_applicant";
		//String where=  "category in ('NOTSUBMITTED', 'REVOKED', 'ONAPPROVAL', 'ACTIVE', 'LOST')";
		String where = "category not in ('INACTIVE')";
		if(data.isArch()) {
			where=  "category in ('INACTIVE')";
		}
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, table.getHeaders());
		data.setCount(rows.size());
		//divided into archive and living
		TableQtb.tablePage(rows, table);
		table = boilerServ.translateRows(table);
		table.setSelectable(false);
		if(data.isArch()) {
				data.setArchive(table);
		}else {
				//paint color
				for(TableRow row : table.getRows()) {
					for(TableCell cell :row.getRow()) {
						if(cell.getKey().equalsIgnoreCase("term")) {
							if(cell.getIntValue()<0) {
								cell.setStyleClass("text-danger");
							}
						}
					}
				}
				data.setTable(table);
		}
		return data;
	}

	private TableQtb headerTableApp(String searchStr, TableQtb table) {
		if (table.getHeaders().getHeaders().size() == 0) {
			table.setHeaders(createApplicationsTableHeaders(table.getHeaders()));
			if(searchStr != null && !searchStr.equals("null") && searchStr.length() > 2) {
				for(TableHeader th:table.getHeaders().getHeaders()) {
					th.setGeneralCondition(searchStr);
				}
				table.setGeneralSearch(searchStr);
			}
		}else {
			if(table.getGeneralSearch().equals("onSelDict")) {
				table.setGeneralSearch("");
				for(TableHeader th:table.getHeaders().getHeaders()) {
					th.setGeneralCondition(table.getGeneralSearch());
					if(th.getConditionS().length() > 0) {
						th.setConditionS("");
						th.setFilterActive(false);
					}
				}
			}
		}
		return table;
	}



	/**
	 * Create headers for applicant applications table
	 * @param headers
	 * @return
	 */
	private Headers createApplicationsTableHeaders(Headers headers) {
		headers.getHeaders().add(TableHeader.instanceOf(
				"come",
				"scheduled",
				true, true, true, TableHeader.COLUMN_LOCALDATE, 0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"term",
				"days",
				true, true, true, TableHeader.COLUMN_LONG, 0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"prefLabel",
				"prefLabel",
				true, true, true, TableHeader.COLUMN_LINK, 0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"tcategory",
				"category",
				true, true, true, TableHeader.COLUMN_LINK, 0));
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_DESC);
		headers = boilerServ.translateHeaders(headers);
		headers.setPageSize(20);
		return headers;
	}
	/**
	 * Create application information table
	 * 
	 * @param user
	 * @param data
	 * @param manager - called from the manager
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ApplicationHistoryDTO applicationInformationTable(UserDetailsDTO user, ApplicationHistoryDTO data,
			boolean manager) throws ObjectNotFoundException {
		if (data.getApplDictNodeId() > 0) {
			Concept dictNode = closureServ.loadConceptById(data.getApplDictNodeId());
			data.setApplName(literalServ.readPrefLabel(dictNode));
		}
		if (data.getHistoryId() > 0) {
			History his = boilerServ.historyById(data.getHistoryId());
			data.setCurrentHistoryActive(his.getGo()==null);
			if (his.getPrevNotes() != null) {
				data.setNotes(his.getPrevNotes());
			}
			Concept dictNode = his.getApplDict();
			data.setApplName(literalServ.readPrefLabel(dictNode));
			data.setApplDictNodeId(dictNode.getID());
			TableQtb table = data.getTable();
			if (table.getHeaders().getHeaders().size() == 0) {
				table.setHeaders(historyHeaders(table.getHeaders(), user, manager));
			}
			Concept objectData = boilerServ.initialApplicationNode(his.getApplicationData());
			jdbcRepo.application_history(objectData.getID());
			table = applicationHistoryTableRows( table,  false);
			//table.setSelectable(accServ.isSupervisor(user));
			data.setTable(table);
			//build table events ika30012023
			TableQtb tableEv = data.getTableEv();
			boolean applicant=accServ.isApplicant(user);
			if (tableEv.getHeaders().getHeaders().size() == 0) {
				tableEv.setHeaders(historyHeaders(tableEv.getHeaders(), user, manager));
				//eliminate links for applicants
				if(applicant) {
					for(TableHeader h : tableEv.getHeaders().getHeaders()){
						if(h.getColumnType()==TableHeader.COLUMN_LINK) {
							h.setColumnType(TableHeader.COLUMN_STRING);
						}
					}
				}
			}
			//---Concept objectData = boilerServ.initialApplicationNode(his.getApplicationData());
			//--jdbcRepo.application_history(objectData.getID());
			tableEv = applicationHistoryTableRows(tableEv, true);
			//tableEv.setSelectable(accServ.isSupervisor(user));
			data.setTableEv(tableEv);
			return data;
		}else {
			data.setCurrentHistoryActive(true);		//for new ones
		}
		return data;
	}

	/**
	 * Activities for application history tables
	 * There are two tables in the application history:
	 * <ul>
	 * <li> completed activities
	 * <li> not completed  activities
	 * </ul>
	 * @param table - table to place rows
	 * @param notCompleted true - select only not completed, false - select only completed
	 * @return
	 */
	@Transactional
	public TableQtb applicationHistoryTableRows( TableQtb table,  boolean notCompleted) {
		String where = "";
		if(notCompleted) {
			where = "go is null";
		}else {
			where = "go is not null";
		}
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from application_history", "", where,
				table.getHeaders());
		TableQtb.tablePage(rows, table);
		boilerServ.translateRows(table);
		table.setSelectable(false);
		return table;
	}

	/**
	 * Headers for application data history table usage in ApplicationService and
	 * ReportService
	 * 
	 * @param headers
	 * @param user
	 * @param manager create table for TODO and Monitoring. Otherwise - report
	 * @return
	 */
	public Headers historyHeaders(Headers headers, UserDetailsDTO user, boolean manager) {
		headers.getHeaders().add(
				TableHeader.instanceOf("come", "global_date", true, true, true, TableHeader.COLUMN_LOCALDATETIME, 0));
		headers.getHeaders()
		.add(TableHeader.instanceOf("go", "done", true, true, true, TableHeader.COLUMN_LOCALDATETIME, 0));
		headers.getHeaders().add(TableHeader.instanceOf("days", "days", true, true, true, TableHeader.COLUMN_LONG, 0));
		int colType = TableHeader.COLUMN_STRING;
		if (manager) {
			colType = TableHeader.COLUMN_LINK;
		}
		headers.getHeaders().add(TableHeader.instanceOf("workflow", "workflows", true, true, true, colType, 0));
		headers.getHeaders()
		.add(TableHeader.instanceOf("activity", "activity", true, true, true, TableHeader.COLUMN_STRING, 0));
		if (!accServ.isApplicant(user)) {
			headers.getHeaders().add(
					TableHeader.instanceOf("executive", "persons", true, true, true, TableHeader.COLUMN_STRING, 0));
		}
		headers.getHeaders()
		.add(TableHeader.instanceOf("notes", "description", true, true, true, TableHeader.COLUMN_STRING, 0));
		if (!manager) {
			headers.getHeaders().add(TableHeader.instanceOf("details", "Global_details", true, true, true,
					TableHeader.COLUMN_I18LINK, 0));
		}
		headers = boilerServ.translateHeaders(headers);
		headers.getHeaders().get(0).setSort(true);
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		headers.setPageSize(Integer.MAX_VALUE);
		return headers;
	}

	/**
	 * Create or load a checklist
	 * 
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public CheckListDTO checklistLoad(CheckListDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		data.getQuestions().clear();
		data.setTitle("");
		// current activity?
		if (data.getHistoryId() > 0) {
			History his = boilerServ.historyById(data.getHistoryId());
			if (his.getActivity() != null) {
				if(accServ.checklistAllowed(his.getActConfig(),user)) {
					data.setActivityNodeId(his.getActivity().getID());
					data.setApplNodeId(his.getApplication().getID());
					// determine owner and application's url
					Concept executor = closureServ.getParent(his.getActivity());
					// set access
					data.setReadOnly(!accServ.sameEmail(executor.getIdentifier(), user.getEmail()));

					//determine dict url is one
					String dictUrl = "";
					if(his.getActConfig()!=null) {
						dictUrl=literalServ.readValue("checklisturl", his.getActConfig());
					}
					// dictionary and data, maybe JSON encoded
					if(dictUrl.isEmpty()) {
						if (his.getActivity().getLabel() != null) {
							dictUrl = his.getActivity().getLabel();
							try {
								WorkflowParamDTO wdto = objectMapper.readValue(dictUrl, WorkflowParamDTO.class);
								dictUrl = wdto.getChecklistUrl();
							} catch (JsonProcessingException e) {
								// nothing to do
							}
						}
					}
					Concept dictRoot = closureServ.loadRoot(dictUrl);
					data.setDictUrl(dictUrl);
					data.setTitle(literalServ.readPrefLabel(dictRoot));
					List<Checklistr2> stored = checklistRepo.findAllByActivity(his.getActivity());
					List<Long> dictIds = new ArrayList<Long>();
					for (Checklistr2 chl : stored) {
						dictIds.add(chl.getDictItem().getID());
					}
					List<OptionDTO> plainDictionary = dictServ.loadPlain(dictUrl);
					data.getQuestions().clear();
					for (OptionDTO odto : plainDictionary) {
						int index = dictIds.indexOf(odto.getId());
						if (index > -1) {
							data.getQuestions().add(dtoServ.question(stored.get(index), odto));
						} else {
							data.getQuestions().add(QuestionDTO.of(odto));
						}
					}
				}else {
					//checklist is not allowed
				}
			} else {
				throw new ObjectNotFoundException("checkListLoad.Activity not defined in the history", logger);
			}
		} else {
			throw new ObjectNotFoundException("checkListLoad. History not found. ID is ZERO", logger);
		}

		return data;
	}

	/**
	 * We will save all checklist questions, regardless completion
	 * 
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public CheckListDTO checklistSave(CheckListDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		if (data.getActivityNodeId() > 0) {
			data = validServ.checklist(data);
			Concept activity = closureServ.loadConceptById(data.getActivityNodeId());
			Concept applData = closureServ.loadConceptById(data.getApplNodeId());
			List<Checklistr2> existed = checklistRepo.findAllByActivity(activity);
			List<Checklistr2> tosave = new ArrayList<Checklistr2>();
			for (QuestionDTO qdto : data.getQuestions()) {
				Checklistr2 chl = entityServ.checklist(qdto, activity, applData);
				tosave.add(chl);
			}
			checklistRepo.deleteAll(existed);
			checklistRepo.saveAll(tosave);
		} else {
			throw new ObjectNotFoundException("checklisSave Activity ID is zero", logger);
		}
		return data;
	}
	/**
	 * Collect necessary data about activities to run
	 * @param data to inform about errors
	 * @param applUrl url of an application only to create messages
	 * @param curHis 
	 * @param nextActs list of activity configurations
	 * @return list of activities to run and data with error if one
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public List<ActivityToRun> activitiesToRun(AllowValidation data, String applUrl, History curHis, List<Concept> nextActs) throws ObjectNotFoundException {
		// collect activities to run - all background and first foreground
		List<ActivityToRun> toRun = new ArrayList<ActivityToRun>();
		Concept firstActivity = new Concept();
		for (Concept act : nextActs) {
			if (!validServ.isActivityBackground(act)) {
				if(firstActivity.getID()==0) {
					firstActivity = act;	//latch
					toRun.add(ActivityToRun.of(firstActivity, literalServ.readPrefLabel(act), false));
				}
			}else {
				toRun.add(ActivityToRun.of(act,literalServ.readPrefLabel(act),true));
			}
		}
		//collect executors for activities to run
		for(ActivityToRun act : toRun) {
			act=activityExecutors(act, curHis);
		}
		//ensure that all executors has been collected
		for(ActivityToRun act : toRun) {
			if(act.getExecutors().size()==0) {
				data.setValid(false);
				data.setIdentifier(messages.get("executornotdefined")+" "+act.getPrefLabel());
				return toRun;
			}
		}
		// at least one foreground activity should be
		if(firstActivity.getID() == 0l) {
			data.setValid(false);
			data.setIdentifier(messages.get("badconfiguration")+" "+applUrl);
			return toRun;
		}
		return toRun;
	}
	/**
	 * Collect executors for an activity to run as well as fallback messages
	 * Consider user role in the application and admin unit if needed
	 * @param act - activity To run
	 * @param curHis - current history record
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ActivityToRun activityExecutors(ActivityToRun act, History curHis) throws ObjectNotFoundException {
		Concept role = assmServ.activityExecutorRole(act.getConfig());
		if (role.getIdentifier().toUpperCase().contains("APPLICANT")) {
			// applicant is a special case
			act.getExecutors().add(accServ.applicantEmailByApplication(curHis.getApplication()));
		} else {
			// NMRA staff
			Concept applDict = curHis.getApplDict();
			String where = "";
			Concept admUnit = adminUnit(act.getConfig(), curHis);
			if (role != null && applDict != null) {
				if (admUnit != null) {
					where = "auid='" + admUnit.getID() + "'";
				} else {
					where = "auid is null";
				}
				jdbcRepo.executors_select(role.getID(), applDict.getID());
				act.getExecutors().addAll(executorsEmails(where));
				if (act.getExecutors().size() == 0) {
					String mess = "Executors not found - search for office secretaries.";
					act.getFallBack().add(mess);
					logger.warn(mess);
					role = systemServ.loadRole(SystemService.ROLE_SECRETARY);
					jdbcRepo.executors_select(role.getID(), applDict.getID());
					act.getExecutors().addAll(executorsEmails(where));
				}
				if (act.getExecutors().size() == 0) {
					String mess = "Executors not found - send to supervisors.";
					logger.warn(mess);
					act.getFallBack().add(mess);
					role = systemServ.loadRole(SystemService.ROLE_ADMIN);
					jdbcRepo.executors_select(role.getID(), applDict.getID());
					where = "auid is null";
					act.getExecutors().addAll(executorsEmails(where));
				}
				if (act.getExecutors().size()== 0) {
					String mess = "Executors not found";
					logger.warn(mess);
					act.getFallBack().add(mess);
				}
			}
		}
		return act;
	}

	/**
	 * 
	 * 
	 * /** Run background activity for supervisor to monitoring this application
	 * 
	 * @param scheduled - null neans now
	 * @param curHis
	 * @param applUrl
	 * @throws ObjectNotFoundException
	 * @deprecated 2024-03-05
	 */
	public void activityMonitoringRun(Date scheduled, History curHis, String applUrl) throws ObjectNotFoundException {
		// get all supervisors
		List<String> svEmails = new ArrayList<String>();
		List<User> supers = userServ.loadUsersByRole("ROLE_ADMIN");
		for (User sv : supers) {
			if (sv.getEnabled()) {
				svEmails.add(sv.getEmail());
			}
		}
		if (svEmails.size() > 0) {
			// activity control
			for (String email : svEmails) {
				// activity control
				Concept activity = createActivityNode("activity.monitor", email);
				// data always null
				Concept activityData = null;
				// open a history record
				openHistory(scheduled, curHis, null, activity, activityData, ""); // there is no activity configuration
				// for application itself
			}
		} else {
			throw new ObjectNotFoundException("runMonitoringActivity. Supervisors not found", logger);
		}

	}

	/**
	 * Run tracking activity for the current user (an Applicant)
	 * 
	 * @param scheduled when scheduled, null means now
	 * @param curHis
	 * @param applUrl
	 * @param user
	 * @throws ObjectNotFoundException
	 */
	public void activityTrackRun(Date scheduled, History curHis, String applUrl, String usersEmail)
			throws ObjectNotFoundException {
		// activity control
		Concept activity = createActivityNode("activity.trace", usersEmail);
		// data always null
		Concept activityData = null;
		// open a history record
		openHistory(scheduled, curHis, null, activity, activityData, ""); // there is no activity configuration for
		// application itself

	}

	/**
	 * Get a list of executors eMails, using executors selected by the stored
	 * procedure
	 * 
	 * @param ret   - t
	 * @param where
	 */
	public List<String> executorsEmails(String where) {
		List<String> ret = new ArrayList<String>();
		Headers headers = new Headers();
		headers.getHeaders().add(TableHeader.instanceOf("email", TableHeader.COLUMN_STRING));
		headers.getHeaders().add(TableHeader.instanceOf("local", TableHeader.COLUMN_LONG));
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from executors_select", "", where, headers);
		if (rows.size() > 0) {
			for (TableRow row : rows) {
				TableCell email = row.getRow().get(0);
				TableCell local = row.getRow().get(1);
				if (local.getIntValue() > 0) {
					// executor for the administrative unit
					ret.add(email.getValue());
				}
			}
			if (ret.size() == 0) {
				logger.warn("executors for administrative unit not found, search in the country wide executors");
				for (TableRow row : rows) {
					TableCell email = row.getRow().get(0);
					TableCell local = row.getRow().get(1);
					if (local.getIntValue() == 0) {
						// executor for the country
						ret.add(email.getValue());
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Determine admin unit ID
	 * 
	 * @param actConf
	 * @param curHis
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Concept adminUnit(Concept actConf, History curHis) throws ObjectNotFoundException {
		Concept ret = null;
		if (actConf != null) {
			String addrUrl = literalServ.readValue("addressurl", actConf);
			if (addrUrl.length() > 0) {
				Concept applData=amendmentServ.initialApplicationData(curHis.getApplicationData());
				Thing rootThing = boilerServ.thingByNode(applData);
				/*if (rootThing.getAmendments().iterator().hasNext()) { 2022-11-27 wrong routing detected
					ThingAmendment ta = rootThing.getAmendments().iterator().next();
					rootThing = boilerServ.thingByNode(ta.getConcept());
				}*/
				for (ThingThing tt : rootThing.getThings()) {
					if (tt.getUrl().equalsIgnoreCase(addrUrl)) {
						ret = boilerServ.adminUnitLevel(pubOrgServ.territoryLevel(), tt.getConcept());
					} else {
						Thing addThing = boilerServ.thingByNode(tt.getConcept());
						for (ThingThing tt1 : addThing.getThings()) {
							if (tt1.getUrl().equalsIgnoreCase(addrUrl)) {
								ret = boilerServ.adminUnitLevel(pubOrgServ.territoryLevel(), tt1.getConcept());
							}
						}
					}
					if (ret != null) {
						break;
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Create an activity for a user
	 * 
	 * @param scheduled if null, now
	 * @param actConf   activity configuration concept
	 * @param curHis    current history record
	 * @param email     email on an executor
	 * @param notes     notes from the previous step (empty string if none)
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public History activityCreate(Date scheduled, Concept actConf, History curHis, String email, String notes)
			throws ObjectNotFoundException {
		// search for unfinished activity with the same configuration and executor
		List<History> allHis = boilerServ.historyAllByApplication(curHis.getApplication());
		boolean found=false;
		if (allHis != null) {
			for (History h : allHis) {
				if (h.getActConfig() != null) {
					if (h.getActConfig().getID() == actConf.getID()) {
						Concept exec = closureServ.getParent(h.getActivity());
						if(exec != null) {
							boolean sameExec = accServ.sameEmail(exec.getIdentifier(), email);
							//if (h.getActivityData() != null || sameExec) {
							if (h.getGo() == null && sameExec) {
								found=true;
								break;
							}
						}
					}
				}
			}
		}
		if(!found) {
			// create an activity
			String actUrlConf = literalServ.readValue("activityurl", actConf);
			Concept activity = createActivityNode("activity." + actUrlConf, email);
			String checkListDict = literalServ.readValue("checklisturl", actConf);
			activity.setLabel(checkListDict);
			activity = closureServ.save(activity);
			// open a history record
			return openHistory(scheduled, curHis, actConf, activity, null, notes); // activity data will be created, when the
			// activity will be open
		}else {
			return null;
		}
	}

	/**
	 * Open history record for activity given
	 * 
	 * @param scheduled    null means now
	 * @param curHis       current history record
	 * @param activity     new activity concept
	 * @param activityData new activity data, if one
	 * @param actConf      new activity configuration concept
	 * @param prevNotes    notes, from the previous step
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public History openHistory(Date scheduled, History curHis, Concept actConf, Concept activity, Concept activityData,
			String prevNotes) throws ObjectNotFoundException {
		History his = new History();
		his.setApplDict(curHis.getApplDict());
		his.setApplConfig(curHis.getApplConfig());
		his.setActConfig(actConf);
		his.setActivity(activity);
		his.setActivityData(activityData);
		his.setApplication(curHis.getApplication());
		his.setApplicationData(curHis.getApplicationData());
		if (scheduled == null) {
			his.setCome(new Date());
		} else {
			his.setCome(scheduled);
		}
		his.setDataUrl(literalServ.readValue("dataurl", actConf));
		his.setPrevNotes(prevNotes);
		his = boilerServ.saveHistory(his);
		return his;
	}

	/**
	 * Create activity control (activity)
	 * 
	 * @param actConf
	 * @param eMail
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Concept createActivityNode(String url, String eMail) throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot(url);
		Concept exec = new Concept();
		exec.setIdentifier(eMail);
		exec = closureServ.saveToTree(root, exec);
		Concept activity = new Concept();
		activity = closureServ.save(activity);
		activity.setIdentifier(activity.getID() + "");
		activity = closureServ.saveToTree(exec, activity);
		return activity;
	}

	/**
	 * Find executors. Apply application type and territory restrictions Allows to
	 * assign an applicant
	 * 
	 * @deprecated replaced by "executors_select"
	 * @param actConf
	 * @param curHis
	 * @return list of executor's emails
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public List<String> findExecutors(Concept actConf, History curHis) throws ObjectNotFoundException {
		Thing thing = boilerServ.thingByNode(actConf);
		// Roles
		List<Concept> roleNodes = new ArrayList<Concept>();
		for (ThingDict td : thing.getDictionaries()) {
			if (td.getVarname().equalsIgnoreCase("executives")) {
				if (td.getConcept().getIdentifier().equalsIgnoreCase("APPLICANT")) {
					List<String> ret = new ArrayList<String>();
					ret.add(accServ.applicantEmailByApplication(curHis.getApplication()));
					return ret; // !!!!!!!! applicant is a special case
				}
				roleNodes.add(td.getConcept());
			}
		}
		// Responsibilities
		List<Concept> parents = closureServ.loadParents(curHis.getApplication());
		List<Concept> respNodes = systemServ.guestWorkflows(parents.get(0).getIdentifier());
		// Territory
		String addrUrl = "";
		literalServ.readValue("addressurl", actConf);
		Concept addr = new Concept();
		Thing appThing = boilerServ.thingByNode(curHis.getApplicationData());
		List<Thing> things = new ArrayList<Thing>();
		things.add(appThing);
		if (addrUrl.length() > 0) {
			for (ThingThing tt : appThing.getThings()) {
				if (tt.getUrl().equalsIgnoreCase(addrUrl)) {
					addr = tt.getConcept();
					break;
				}
			}
		}
		// ask user service
		List<String> ret = userServ.findUsers(roleNodes, respNodes, addr);
		return ret;
	}

	/**
	 * Close this history record de-activate the current activity
	 * and possibly extra first NMRA activities
	 * @param curHis
	 * @param cancelled
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public History closeActivity(History curHis, boolean cancelled) throws ObjectNotFoundException {
		List<History> extraActs = extraActivities(curHis);
		for(History h :extraActs) {
			stopOneActivity(h,true);
		}
		return stopOneActivity(curHis, cancelled);
	}
	/**
	 * Extra first time NMRA activities that can't be concurrent
	 * The first time NMRA activity is the first activity or any activity after applicant's activity
	 * @param curHis
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private List<History> extraActivities(History curHis) throws ObjectNotFoundException {
		List<History> ret = new ArrayList<History>();
		if(curHis.getActConfig()!=null) {		//is it initial application, monitoring, or trace?
			if(!validServ.isActivityBackground(curHis.getActConfig())) {
				boolean hasExtra=false;
				Concept parent = closureServ.getParent(curHis.getActConfig());
				if(parent==null) {
					hasExtra=true;			//the first activity
				}else {
					hasExtra=isApplicantActivity(parent);	//the previous activity is applicant's activity
				}
				if(hasExtra) {
					List<History> applHis = boilerServ.historyAllByApplication(curHis.getApplication());
					for(History his : applHis) {
						if(his.getActConfig()!=null && his.getGo()==null) {
							if(his.getActConfig().getID()==curHis.getActConfig().getID()) {
								if(his.getID()!=curHis.getID()) {
									ret.add(his);
								}
							}
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * Is this activity managed by an applicant
	 * @param actConfig
	 * @return
	 */
	private boolean isApplicantActivity(Concept actConfig) {
		try {
			Concept role = assmServ.activityExecutorRole(actConfig);
			return role.getIdentifier().equalsIgnoreCase(SystemService.ROLE_APPLICANT);
		} catch (ObjectNotFoundException e) {
			return false;
		}

	}

	/**
	 * Close or cancel a single activity
	 * Save data configuration into the concept if one
	 * @param curHis
	 * @param cancelled
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public History stopOneActivity(History curHis, boolean cancelled) throws ObjectNotFoundException {
		if(curHis.getGo() == null) {
			Date come = curHis.getCome();
			Date go = new Date();
			if (come.after(go)) {
				curHis.setCome(go);
			}
			curHis.setGo(new Date());
			curHis.setCancelled(cancelled);
			curHis = boilerServ.saveHistory(curHis);
			// save data configuration
			if(curHis.getActivityData()!=null && curHis.getActivityData().getLabel()==null && curHis.getDataUrl()!=null) {
				thingServ.storeConfigurationToNode(curHis.getDataUrl(), curHis.getActivityData());
			}
		}
		return curHis;
	}

	/**
	 * Application or workflow activity?
	 * Determine the history as well
	 * @param user 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ApplicationOrActivityDTO applOrAct(UserDetailsDTO user, ApplicationOrActivityDTO data) throws ObjectNotFoundException {
		History his = new History();
		if(data.getHistoryId()>0) {
			his = boilerServ.historyById(data.getHistoryId());
		}else {
			//get the most appropriate history using dataId
			if(data.getDataId()>0) {
				Concept applData= closureServ.loadConceptById(data.getDataId());
				List<History> allHis = historyRepo.findAllByApplicationDataOrderByCome(applData);
				//search for opened initial application record 
				for(History h : allHis) {
					if(h.getActivityData()!=null && h.getApplicationData()!=null &&
							h.getActivityData().getID()==h.getApplicationData().getID() && h.getActConfig()==null && h.getGo()==null
							&& h.getApplDict().getID()==data.getApplDictNodeId()) {
						data.setHistoryId(h.getID());
						data.setApplication(true);
						return data;
					}
				}

				//search for any opened history record assigned 
				for(History h : allHis) {
					his=h;	//suppose..
					if(h.getGo()==null) {
						Concept worker = closureServ.getParent(h.getActivity());
						if(accServ.sameEmail(user.getEmail(), worker.getIdentifier())){
							data.setHistoryId(h.getID());
							data.setApplication(false);
							return data;
						}
					}
				}

			}else {
				throw new ObjectNotFoundException("applicationOrActivity History and/or data are not defined "+
						data.toString(),logger);
			}
		}
		//the latest one or existing history
		data.setHistoryId(his.getID());
		data.setApplication(false);
		if(his.getActivityData()!=null && his.getApplicationData()!=null &&
				his.getActivityData().getID()==his.getApplicationData().getID() && his.getActConfig()==null && his.getGo()==null) {
			data.setApplication(true);
		}
		return data;
	}

	/**
	 * All finished activities for revise This activity for job
	 * 
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@SuppressWarnings("unused")
	@Transactional
	public ActivityDTO activityLoad(ActivityDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		data.setGuest(accServ.isApplicant(user));
		data.getPath().clear();
		data.getApplication().clear();
		data.getData().clear();
		History curHis = boilerServ.historyById(data.getHistoryId());
		List<History> allHis = boilerServ.historyAllByApplication(curHis.getApplication()); // suppose right sort order
		// by Come

		if (accServ.applicationAllowed(allHis, user) || true) {
			// application is compiled as a list of data items
			ThingDTO applDTO = createApplication(curHis);
			applDTO = thingServ.path(user, applDTO);
			data.setApplication(applDTO.getPath());
			// all completed activities
			ThingDTO dto = new ThingDTO();
			for (History his : allHis) {
				if (his.getGo() != null && his.getID() != curHis.getID() && !his.getCancelled()) {
					dto = createActivity(user, his, true);
					ThingDTO dt = createLoadActivityData(his, true);
					data.getPath().add(dto);
					data.getData().add(dt);
					if(his.getActivity()!=null) {
						Concept exec=closureServ.getParent(his.getActivity());
						data.getExecutors().add(userServ.nameByEmail(exec.getIdentifier()));
					}
					// notes from the previous step
					String psn = "";
					if (his.getPrevNotes() != null) {
						psn = his.getPrevNotes().trim();
					}
					data.getNotes().add(psn);
					data.getCancelled().add(his.getCancelled());
				}
			}
			// the current activity, if checklist is defined
			if (curHis.getActConfig() != null) {
				data.setBackground(validServ.isActivityBackground(curHis.getActConfig()));
				data.setAttention(validServ.isActivityAttention(curHis.getActConfig()));
				if(validServ.isActivityFinalAction(curHis, SystemService.FINAL_ACCEPT)) {
					data.setFinalization(true);
				}else if(validServ.isActivityFinalAction(curHis, SystemService.FINAL_AMEND)) {
					data.setFinalization(true); 
				}else if(validServ.isActivityFinalAction(curHis, SystemService.FINAL_DEREGISTRATION)) {
					data.setFinalization(true);
				}else {
					data.setFinalization(false);
				}
				data.setHost(validServ.isHostApplication(curHis.getApplDict()));
				dto = createActivity(user, curHis, false);
				ThingDTO dt = createLoadActivityData(curHis,false);
				data.getPath().add(dto);
				data.getData().add(dt);
				Concept exec=closureServ.getParent(curHis.getActivity());
				data.getExecutors().add(userServ.nameByEmail(exec.getIdentifier()));
				//2023-03-17 data edit condition 
				boolean writable = accServ.sameEmail(user.getEmail(), exec.getIdentifier()) && curHis.getGo()==null;
				dt.setReadOnly(!writable);
				dto.setReadOnly(!writable);
				//2023-03-17
				// notes from the previous step
				String psn = "";
				if (curHis.getPrevNotes() != null) {
					psn = curHis.getPrevNotes().trim();
				}
				data.getNotes().add(psn);
				data.getCancelled().add(curHis.getCancelled());
				//}
			}
			data = amendmentServ.amended(curHis.getApplicationData(), data);
			return data;
		} else {
			throw new ObjectNotFoundException("activityLoad. Access denied.", logger);
		}

	}

	/**
	 * Create or a data for an activity if one
	 * 
	 * @param data
	 * @param history
	 * @param user
	 * @param readOnly
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ThingDTO createLoadActivityData(History history, boolean readOnly)
			throws ObjectNotFoundException {
		ThingDTO dto = new ThingDTO();
		if (history.getDataUrl() != null && history.getDataUrl().length() > 0) {
			dto.setUrl(history.getDataUrl());
			dto.setActivityId(history.getActivity().getID());
			dto.setHistoryId(history.getID());
			if (history.getActivityData() != null) {
				dto.setNodeId(history.getActivityData().getID());
			}
			dto.setReadOnly(readOnly);
		}
		return dto;
	}

	/**
	 * Create a minimal application ThingDTO from a history
	 * 
	 * @param history
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ThingDTO createApplication(History history) throws ObjectNotFoundException {
		ThingDTO ret = new ThingDTO();
		ret.setApplication(true);
		ret.setActivityId(history.getActivity().getID());
		ret.setApplicationUrl(boilerServ.url(history.getApplication()));
		ret.setNodeId(history.getApplicationData().getID());
		ret.setHistoryId(history.getID());
		ret.setApplDictNodeId(history.getApplDict().getID());
		return ret;
	}

	/**
	 * Create a data for activity listed in the history record
	 * 
	 * @param user
	 * @param his
	 * @param readOnly
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ThingDTO createActivity(UserDetailsDTO user, History his, boolean readOnly) throws ObjectNotFoundException {
		ThingDTO dto = new ThingDTO();
		dto.setReadOnly(readOnly);
		String title = literalServ.readPrefLabel(his.getActConfig());
		if (title.length() == 0) {
			title = literalServ.readPrefLabel(his.getActivityData());
		}
		if (title.length() == 0) {
			title = messages.get("activity.trace");
		}
		dto.setTitle(title);
		Concept exec = closureServ.getParent(his.getApplication());
		Concept appRoot = closureServ.getParent(exec);
		dto.setApplicationUrl(appRoot.getIdentifier());
		dto.setHistoryId(his.getID());
		dto.setNodeId(his.getApplicationData().getID());
		dto.setApplication(true);
		return dto;
	}

	/**
	 * Load activities that are required attention
	 * @param data
	 * @param user
	 * @return
	 */
	public ApplicationsDTO attentionToDo(ApplicationsDTO data, UserDetailsDTO user) {
		TableQtb table = data.getTable();
		if (table.getHeaders().getHeaders().size() == 0) {
			table.getHeaders().getHeaders().addAll(attentionHeaders());
			String search = table.getGeneralSearch();
			if(!search.isEmpty()) {
				for(TableHeader header : table.getHeaders().getHeaders()) {
					header.setGeneralCondition(search);
				}
			}
		}
		jdbcRepo.attention(user.getEmail(), 1);
		String select = "select * from _attention";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", "", table.getHeaders());
		TableQtb.tablePage(rows, table);
		table = boilerServ.translateRows(table);
		table.setSelectable(false);
		data.setTable(table);
		return data;
	}
	/**
	 * Headers for attention table
	 * @param  
	 * @param headers
	 * @return
	 */
	private List<TableHeader> attentionHeaders() {
		List<TableHeader> headers = new ArrayList<TableHeader>();
		headers
		.add(TableHeader.instanceOf("Come", "scheduled", true, true, true, TableHeader.COLUMN_LOCALDATE, 0));
		headers
		.add(TableHeader.instanceOf("Gone", "updated_date", true, true, true, TableHeader.COLUMN_LOCALDATE, 0));
		headers
		.add(TableHeader.instanceOf("permit", "prefLabel", true, true, true, TableHeader.COLUMN_LINK, 0));
		headers
		.add(TableHeader.instanceOf("workflowgroup", "prod_app_type", true, true, true, TableHeader.COLUMN_STRING, 0));
		headers.add(
				TableHeader.instanceOf("workflow", "workflows", true, true, true, TableHeader.COLUMN_STRING, 0));
		headers
		.add(TableHeader.instanceOf("activity", "activity", true, true, true, TableHeader.COLUMN_STRING, 0));
		headers = boilerServ.translateHeadersList(headers);
		headers.get(0).setSortValue(TableHeader.SORT_DESC);
		headers.get(1).setSortValue(TableHeader.SORT_ASC);
		return headers;
	}

	/**
	 * Activities to execute now
	 * 
	 * @param data
	 * @param user
	 * @return
	 */
	public ApplicationsDTO activitiesToDo(ApplicationsDTO data, boolean present, UserDetailsDTO user) {
		TableQtb table = data.getTable();
		if (table.getHeaders().getHeaders().size() == 0) {
			table.getHeaders().getHeaders().addAll(activitiesToDoHeaders());
			String search = table.getGeneralSearch();
			if(!search.isEmpty()) {
				for(TableHeader header : table.getHeaders().getHeaders()) {
					header.setGeneralCondition(search);
				}
			}
		}
		jdbcRepo.todo(user.getEmail(),present);
		String select = "select * from _todo";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", "", table.getHeaders());
		TableQtb.tablePage(rows, table);
		table = boilerServ.translateRows(table);
		table.setSelectable(false);
		data.setTable(table);
		return data;
	}

	/**
	 * Headers for my actual and my scheduled activities
	 * 
	 * @param headers
	 * @return
	 */
	private List<TableHeader> activitiesToDoHeaders() {
		List<TableHeader> headers = new ArrayList<TableHeader>();
		headers
		.add(TableHeader.instanceOf("Come", "scheduled", true, true, true, TableHeader.COLUMN_LOCALDATE, 0));
		headers
		.add(TableHeader.instanceOf("pref", "prefLabel", true, true, true, TableHeader.COLUMN_LINK, 0));
		headers
		.add(TableHeader.instanceOf("applicant", "applicant", true, true, true, TableHeader.COLUMN_STRING, 0));
		headers
		.add(	TableHeader.instanceOf("workflow", "prod_app_type", true, true, true, TableHeader.COLUMN_STRING, 0));
		headers
		.add(TableHeader.instanceOf("activity", "activity", true, true, true, TableHeader.COLUMN_STRING, 0));
		headers = boilerServ.translateHeadersList(headers);
		headers.get(0).setSortValue(TableHeader.SORT_DESC);
		return headers;
	}

	/**
	 * Is selected activity traced or monitoring (not configurable) activity
	 * 
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ActivityDTO activityHistoryIsMonitoring(UserDetailsDTO user, ActivityDTO data)
			throws ObjectNotFoundException {
		if (data.getHistoryId() > 0) {
			data.clearErrors();
			data.setGuest(accServ.isApplicant(user));
			return data;
		} else {
			throw new ObjectNotFoundException("activityHistoryIsMonitoring. History ID is ZERO", logger);
		}
	}


	/**
	 * Cancel all opened activities in this workflow
	 * 
	 * @param curHis
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private void cancelWorkflow(History curHis, ActivitySubmitDTO data) throws ObjectNotFoundException {
		curHis.setPrevNotes(data.getNotes().getValue());
		List<History> allHis = boilerServ.historyAllByApplication(curHis.getApplication());
		for (History his : allHis) {
			if (his.getGo() == null) {
				closeActivity(his, true);
			}
		}
	}

	/**
	 * Create a new host application and the first activity for it
	 * The application URL comes from the scheduler
	 * @param prevHis
	 * @param sch
	 * @param dictConc
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ActivitySubmitDTO createHostApplication(History prevHis, Scheduler sch, Concept dictConc,
			ActivitySubmitDTO data) throws ObjectNotFoundException {
		//create and save the application
		String applUrl = sch.getProcessUrl();
		String applicantEmail = accServ.applicantEmailByApplication(prevHis.getApplication());
		Concept applRoot = closureServ.loadRoot(sch.getProcessUrl());
		Concept owner = new Concept();
		owner.setIdentifier(applicantEmail);
		owner = closureServ.saveToTree(applRoot, owner);
		Concept applConc = new Concept();
		applConc = closureServ.save(applConc);
		applConc.setIdentifier(applConc.getID() + "");
		applConc = closureServ.saveToTree(owner, applConc);
		data = (ActivitySubmitDTO)validServ.validWorkFlowConfig(data, applUrl);			
		if (data.isValid()) {
			Concept configRoot = closureServ.loadRoot("configuration." + applUrl);
			List<Concept> nextActs = boilerServ.loadActivities(configRoot);
			if (nextActs.size() > 0) {
				History curHis = createHostHistorySample(prevHis, applConc, dictConc, configRoot);
				List<ActivityToRun> toRun = activitiesToRun(data, applUrl, curHis, nextActs);
				if(toRun.size()>0 && data.isValid()) {
					activityTrackRun(sch.getScheduled(), curHis, applUrl, applicantEmail); // tracking by an applicant
					activityMonitoringRun(sch.getScheduled(), curHis, applUrl); // monitoring by the all supervisors
					// run activities
					for(ActivityToRun act :toRun ) {
						for (String email : act.getExecutors()) {
							String notes = data.getNotes().getValue();
							if(notes==null) notes="";
							activityCreate(sch.getScheduled(), act.getConfig(), curHis, email, notes+ " /"+ String.join(",",act.getFallBack()));
						}
					}
				}else {
					if(data.isValid()) {
						data.setValid(false);
						data.setIdentifier(messages.get("badconfiguration")+" "+applUrl);
					}
				}
			} else {
				String mess= messages.get("badconfiguration") +" "+applUrl;
				logger.error(mess);
				data.setValid(false);
				data.setIdentifier(mess);
			}
		} else {
			String mess= messages.get("badconfiguration") +" "+applUrl;
			logger.error(mess);
			data.setValid(false);
			data.setIdentifier(mess);
		}
		return data;
	}

	/**
	 * 
	 * @param prevHis
	 * @param sch
	 * @param dictConc
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ActivitySubmitDTO createRevokePermitApplication(History prevHis, String applUrl, Concept dictConc,
			ActivitySubmitDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		Concept applRoot = closureServ.loadRoot(applUrl);
		Concept owner = new Concept();
		owner.setIdentifier(user.getEmail());
		owner = closureServ.saveToTree(applRoot, owner);
		Concept applConc = new Concept();
		applConc = closureServ.save(applConc);
		applConc.setIdentifier(applConc.getID() + "");
		applConc = closureServ.saveToTree(owner, applConc);
		data = (ActivitySubmitDTO)validServ.validWorkFlowConfig(data, applUrl);
		if (data.isValid()) {
			Concept configRoot = closureServ.loadRoot("configuration." + applUrl);
			List<Concept> nextActs = boilerServ.loadActivities(configRoot);
			if (nextActs.size() > 0) {
				Concept actConfig = nextActs.get(0);
				History curHis = createHostHistorySample(prevHis, applConc, dictConc, configRoot);
				activityMonitoringRun(null, curHis, applUrl); 
				activityCreate(null, actConfig, curHis, owner.getIdentifier(), data.getNotes().getValue());
			} else {
				String mess= messages.get("badconfiguration") +" "+applUrl;
				logger.error(mess);
				data.setValid(false);
				data.setIdentifier(mess);
			}
		} else {
			String mess= messages.get("badconfiguration") +" "+applUrl;
			logger.error(mess);
			data.setValid(false);
			data.setIdentifier(mess);
		}
		return data;
	}

	/**
	 * Create a sample for history in host stage It is a sample for the real history
	 * records
	 * 
	 * @param prevHis
	 * @param applConc
	 * @param dictConc
	 * @param configRoot
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public History createHostHistorySample(History prevHis, Concept applConc, Concept applDict, Concept applConfig)
			throws ObjectNotFoundException {
		History his = new History();
		his.setApplConfig(applConfig);
		his.setApplDict(applDict);
		his.setApplication(applConc);
		his.setApplicationData(amendmentServ.initialApplicationData(prevHis.getApplicationData()));
		return his;
	}
	/**
	 * Try to done parallel activity or inform that this activity is the last
	 * (data.done=false)
	 * 
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ActivityDTO activityDone(UserDetailsDTO user, ActivityDTO data) throws ObjectNotFoundException {
		if (data.getHistoryId() > 0) {
			History his = boilerServ.historyById(data.getHistoryId());
			if (accServ.isActivityExecutor(his.getActivity(), user) || accServ.isSupervisor(user)) {
				if (firstExecutor(his)) {
					data.setDone(false); // I'm the first executor, thus I can assign the next one
				} else {
					his = closeActivity(his, false); // I'm not the first executor, thus I can only close my own
					// activity
					data.setDone(true);
				}
				return data;
			} else {
				throw new ObjectNotFoundException("activityBackgroundDone. Access is denied for " + user.getEmail(),
						logger);
			}

		} else {
			throw new ObjectNotFoundException("activityDone. History ID is ZERO", logger);
		}
	}

	/**
	 * Done the background activity
	 * 
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ActivityDTO activityBackgroundDone(UserDetailsDTO user, ActivityDTO data) throws ObjectNotFoundException {
		if (data.getHistoryId() > 0) {
			History curHis = boilerServ.historyById(data.getHistoryId());
			if (accServ.isActivityExecutor(curHis.getActivity(), user) || accServ.isSupervisor(user)) {
				curHis = closeActivity(curHis, false);
			} else {
				throw new ObjectNotFoundException("activityBackgroundDone. Access is denied for " + user.getEmail(),
						logger);
			}
		} else {
			throw new ObjectNotFoundException("activityBackgroundDone. History ID is ZERO", logger);
		}
		return data;
	}

	/**
	 * Am first completed the concurrent activity?
	 * 
	 * @param his
	 * @return
	 */
	@Transactional
	public boolean firstExecutor(History curHis) {
		List<History> allHis = boilerServ.historyAllByApplication(curHis.getApplication());
		boolean hasActivities = false;
		for (History his : allHis) {
			if (his.getGo() == null) {
				hasActivities = true;
				break;
			}
		}
		for (History his : allHis) {
			if (his.getID() != curHis.getID()) { // not this
				if (his.getGo() != null) { // closed
					if (his.getActConfig() != null && curHis.getActConfig() != null) { // safe to check :)
						if (his.getActConfig().getID() == curHis.getActConfig().getID()) { // someone done it
							return hasActivities;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * load application data related to the activity
	 * 
	 * @param data
	 * @param userDto
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ActivityHistoryDataDTO activityHistoryData(ActivityHistoryDataDTO data, UserDetailsDTO userDto)
			throws ObjectNotFoundException {
		if (data.getHistoryId() > 0) {
			History his = boilerServ.historyById(data.getHistoryId());
			// breadcrumb
			Concept wConc = his.getApplDict();
			data.setWorkflow(literalServ.readPrefLabel(wConc));
			Concept actConc = his.getActConfig();
			if (actConc != null) {
				data.setActivity(literalServ.readPrefLabel(actConc));
				Concept dataConc = his.getApplicationData();
				data.setPrefLabel(literalServ.readPrefLabel(dataConc));
			} else {
				data.setActivity("");
				data.setPrefLabel("");
			}
			// dates
			LocalDate come = boilerServ.localDateFromDate(his.getCome());
			data.getGlobal_startdate().setValue(come);
			LocalDate go = null;
			if (his.getGo() != null) {
				go = boilerServ.localDateFromDate(his.getGo());
				data.getCompleteddate().setValue(go);
				data.setCompleted(true);
			} else {
				data.setCompleted(false);
			}
			// activity data
			if (his.getActivityData() != null) {
				data.setActivityDataId(his.getActivityData().getID());
			} else {
				data.setActivityDataId(0l);
			}
			// executor and notes
			if (!accServ.isApplicant(userDto)) {
				if (his.getPrevNotes() != null) {
					data.getNotes().setValue(his.getPrevNotes());
				} else {
					data.getNotes().setValue("");
				}
				// executor
				Concept activity = his.getActivity();
				Concept exec = closureServ.getParent(activity);
				User user = userServ.findByEmail(exec.getIdentifier());
				if (user != null) {
					Concept ucon = user.getConcept();
					data.getExpert().setValue(literalServ.readPrefLabel(ucon));
				} else {
					data.getExpert().setValue("");
				}
			}
		} else {
			throw new ObjectNotFoundException("activityHistoryData. historyId is ZERO", logger);
		}
		return data;
	}
	
}
