package org.msh.pharmadex2.service.r2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.old.User;
import org.msh.pdex2.model.r2.Checklistr2;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.Scheduler;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingDict;
import org.msh.pdex2.model.r2.ThingScheduler;
import org.msh.pdex2.model.r2.ThingThing;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.r2.Checklistr2Repo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.ActivityDTO;
import org.msh.pharmadex2.dto.ActivityHistoryDataDTO;
import org.msh.pharmadex2.dto.ActivitySubmitDTO;
import org.msh.pharmadex2.dto.ActivityToRun;
import org.msh.pharmadex2.dto.ApplicationHistoryDTO;
import org.msh.pharmadex2.dto.ApplicationOrActivityDTO;
import org.msh.pharmadex2.dto.ApplicationsDTO;
import org.msh.pharmadex2.dto.CheckListDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
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
import org.springframework.context.i18n.LocaleContextHolder;
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
	private AccessControlService accessControlServ;
	@Autowired
	private MailService mailService;

	/**
	 * recreate only a table for selected applications
	 * 
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ApplicationsDTO applicatonsTable(ApplicationsDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		if (data.getDictItemId() > 0) {
			Concept dictItem = closureServ.loadConceptById(data.getDictItemId());
			String appUrl = literalServ.readValue("applicationurl", dictItem);
			String dataUrl = literalServ.readValue("dataurl", dictItem);
			data.setTable(createApplicationsTable(appUrl, dataUrl, user.getEmail(), data.getTable()));
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
	private TableQtb createApplicationsTable(String appUrl, String dataUrl, String email, TableQtb table)
			throws ObjectNotFoundException {
		if (table.getHeaders().getHeaders().size() == 0) {
			table.setHeaders(createHeaders(table.getHeaders(), true));
		}
		String select = "select * from activity_data";
		String where = "go is null and applurl='" + appUrl + "' and dataurl='" + dataUrl + "'" + "  and executive='"
				+ email + "' and lang='" + LocaleContextHolder.getLocale().toString().toUpperCase() + "'";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, table.getHeaders());
		TableQtb.tablePage(rows, table);
		table = boilerServ.translateRows(table);
		table.setSelectable(false);
		return table;
	}

	/**
	 * Create headers for application list
	 * 
	 * @param headers
	 * @param present present or scheduled
	 * @return
	 */
	private Headers createHeaders(Headers headers, boolean present) {
		headers.getHeaders()
		.add(TableHeader.instanceOf("come", "scheduled", true, true, true, TableHeader.COLUMN_LOCALDATE, 0));
		if (present) {
			headers.getHeaders()
			.add(TableHeader.instanceOf("days", "days", true, true, true, TableHeader.COLUMN_LONG, 0));
		}

		headers.getHeaders().add(
				TableHeader.instanceOf("activityurl", "activity", true, false, false, TableHeader.COLUMN_I18LINK, 0));

		headers.getHeaders()
		.add(TableHeader.instanceOf("pref", "global_name", true, true, true, TableHeader.COLUMN_STRING, 0));
		headers.getHeaders()
		.add(TableHeader.instanceOf("notes", "description", true, true, true, TableHeader.COLUMN_STRING, 0));

		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		headers = boilerServ.translateHeaders(headers);
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
			table = historyTableRows(user, table, manager);
			table.setSelectable(accServ.isSupervisor(user));
			data.setTable(table);
			return data;
		}
		return data;
	}

	/**
	 * Rows for history table
	 * 
	 * @param user
	 * @param table
	 * @param manager
	 */
	@Transactional
	public TableQtb historyTableRows(UserDetailsDTO user, TableQtb table, boolean manager) {
		String where = "come<(curdate() + Interval 1 day)";
		if (!manager) {
			where = where + " and go is not null";
		}
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from application_history", "", where,
				table.getHeaders());
		TableQtb.tablePage(rows, table);
		boilerServ.translateRows(table);
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
		// current activity?
		if (data.getHistoryId() > 0) {
			History his = boilerServ.historyById(data.getHistoryId());
			if (his.getActivity() != null) {
				data.setActivityNodeId(his.getActivity().getID());
				data.setApplNodeId(his.getApplication().getID());
				// determine owner and application's url
				Concept executor = closureServ.getParent(his.getActivity());
				// set access
				data.setReadOnly(!accServ.sameEmail(executor.getIdentifier(), user.getEmail()));

				// dictionary and data, maybe JSON encoded
				String dictUrl = "";
				if (his.getActivity().getLabel() != null) {
					dictUrl = his.getActivity().getLabel();
					try {
						WorkflowParamDTO wdto = objectMapper.readValue(dictUrl, WorkflowParamDTO.class);
						dictUrl = wdto.getChecklistUrl();
					} catch (JsonProcessingException e) {
						// nothing to do
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
	 * Submit an application to the NMRA from an applicant
	 * 
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public CheckListDTO submit(CheckListDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		if (data.getHistoryId() > 0) {
			// get workflow configuration root
			History curHis = boilerServ.historyById(data.getHistoryId());
			if(!singeltonCondition(curHis)) {
				data.setValid(false);
				data.setIdentifier(messages.get("singletonError"));
				return data;
			}
			Concept applRoot = closureServ.loadParents(curHis.getApplication()).get(0);
			String applUrl = applRoot.getIdentifier();
			Concept configRoot = closureServ.loadRoot("configuration." + applUrl);
			List<Concept> nextActs = loadActivities(configRoot);
			data = (CheckListDTO) validServ.workflowConfig(nextActs, configRoot, data);
			if (data.isValid()) {
				if (nextActs.size() > 0) {
					List<ActivityToRun> toRun = activitiesToRun(data, applUrl, curHis, nextActs);
					//finish the current activity and run others
					if(toRun.size()>0 && data.isValid()) {
						curHis = closeActivity(curHis, false);
						// tracking by an applicant
						activityTrackRun(null, curHis, applUrl, user.getEmail()); 
						// monitoring by the all supervisors as a last resort
						activityMonitoringRun(null, curHis, applUrl); 
						// run activities
						for(ActivityToRun act :toRun ) {
							for (String email : act.getExecutors()) {
								activityCreate(null, act.getConfig(), curHis, email, String.join(",",act.getFallBack()));
							}
						}
					}else {
						if(data.isValid()) {
							data.setValid(false);
							data.setIdentifier(messages.get("badconfiguration") + applUrl);
						}
					}
				} else {
					data.setValid(false);
					data.setIdentifier(messages.get("badconfiguration") + " " + "activities");
				}
			}
			return data;
		} else {
			throw new ObjectNotFoundException("submit. History record id is ZERO", logger);
		}
	}

	/**
	 * It is impossible running more than one modification and/or de-registration against the same object
	 * in addition it is impossible running modification and de-registration if any host application is running
	 * Guest may be running without any condition
	 * Host runs automatically
	 * @param curHis
	 * @return true if condition is 
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean singeltonCondition(History curHis) throws ObjectNotFoundException {
		if(amendmentServ.hasAmendment(curHis.getApplicationData())) {
			Concept applData = amendmentServ.initialApplicationData(curHis.getApplicationData());
			Headers headers = new Headers();
			headers.getHeaders().add(TableHeader.instanceOf("url", TableHeader.COLUMN_STRING));
			// check host
			jdbcRepo.guestPlusHost(applData.getID());
			List<TableRow> rowsHost= jdbcRepo.qtbGroupReport("select * from guestPlusHost", "", "", headers);
			jdbcRepo.dregPlusModi(applData.getID());
			List<TableRow> rowsModi= jdbcRepo.qtbGroupReport("select * from dregPlusModi", "", "", headers);
			return rowsHost.size()==0 && rowsModi.size()==0;
		}else {
			return true;
		}
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
	 */
	private void activityMonitoringRun(Date scheduled, History curHis, String applUrl) throws ObjectNotFoundException {
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
	private void activityTrackRun(Date scheduled, History curHis, String applUrl, String usersEmail)
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
	private Concept adminUnit(Concept actConf, History curHis) throws ObjectNotFoundException {
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
	public void activityCreate(Date scheduled, Concept actConf, History curHis, String email, String notes)
			throws ObjectNotFoundException {
		// cancel all activities with the same configuration and executor
		List<History> allHis = boilerServ.historyAllByApplication(curHis.getApplication());
		if (allHis != null) {
			for (History h : allHis) {
				if (h.getActConfig() != null) {
					if (h.getActConfig().getID() == actConf.getID()) {
						Concept exec = closureServ.getParent(h.getActivity());
						boolean sameExec = accServ.sameEmail(exec.getIdentifier(), email);
						if (h.getActivityData() != null || sameExec) {
							closeActivity(h, true);
						}
					}
				}
			}
		}
		// create an activity
		String actUrlConf = literalServ.readValue("activityurl", actConf);
		Concept activity = createActivityNode("activity." + actUrlConf, email);
		String checkListDict = literalServ.readValue("checklisturl", actConf);
		activity.setLabel(checkListDict);
		activity = closureServ.save(activity);
		// open a history record
		openHistory(scheduled, curHis, actConf, activity, null, notes); // activity data will be created, when the
		// activity will be open
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
	private History openHistory(Date scheduled, History curHis, Concept actConf, Concept activity, Concept activityData,
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
	 * 
	 * @param curHis
	 * @param cancelled
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public History closeActivity(History curHis, boolean cancelled) throws ObjectNotFoundException {
		Date come = curHis.getCome();
		Date go = new Date();
		if (come.after(go)) {
			curHis.setCome(go);
		}
		curHis.setGo(new Date());
		/*if(cancelled) {
			closureServ.removeNode(curHis.getActivityData());
			curHis.setActivityData(null);
		}*/
		curHis.setCancelled(cancelled);
		curHis = boilerServ.saveHistory(curHis);
		return curHis;
	}

	/**
	 * Load all activities in the workflow
	 * 
	 * @param root root activity in the configuration
	 * @return
	 */
	@Transactional
	public List<Concept> loadActivities(Concept root) {
		List<Concept> ret = new ArrayList<Concept>();
		if (root != null) {
			// load all
			List<Concept> all = new ArrayList<Concept>();
			List<Concept> childs = literalServ.loadOnlyChilds(root);
			all.add(root);
			while (childs.size() > 0) {
				if (childs.get(0).getActive()) {
					all.add(childs.get(0));
				}
				childs = literalServ.loadOnlyChilds(childs.get(0));
			}
			ret.addAll(all);
		}
		return ret;
	}

	/**
	 * Application or workflow activity?
	 * 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ApplicationOrActivityDTO applOrAct(ApplicationOrActivityDTO data) throws ObjectNotFoundException {
		History his = boilerServ.historyById(data.getHistoryId());
		if (his.getActivityData() != null) {
			data.setApplication(his.getActivityData().getID() == his.getApplicationData().getID());
		} else {
			data.setApplication(false);
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
	@Transactional
	public ActivityDTO activityLoad(ActivityDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		data.setGuest(accServ.isApplicant(user));
		data.getPath().clear();
		data.getApplication().clear();
		data.getData().clear();
		History curHis = boilerServ.historyById(data.getHistoryId());
		List<History> allHis = boilerServ.historyAllByApplication(curHis.getApplication()); // suppose right sort order
		// by Come

		if (accServ.applicationAllowed(allHis, user)) {
			// application is compiled as a list of data items
			ThingDTO applDTO = createApplication(curHis);
			applDTO = thingServ.path(applDTO);
			data.setApplication(applDTO.getPath());
			// all completed activities
			ThingDTO dto = new ThingDTO();
			for (History his : allHis) {
				if (his.getGo() != null && his.getID() != curHis.getID() && !his.getCancelled()) {
					dto = createActivity(user, his, true);
					ThingDTO dt = createLoadActivityData(data, his, user, true);
					data.getPath().add(dto);
					data.getData().add(dt);
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
				//String dictUrl = curHis.getActivity().getLabel();
				//if (dictUrl != null && dictUrl.toUpperCase().startsWith("DICTIONAR")) {
				data.setHost(validServ.isHostApplication(curHis.getApplDict()));
				dto = createActivity(user, curHis, false);
				ThingDTO dt = createLoadActivityData(data, curHis, user, false);
				data.getPath().add(dto);
				data.getData().add(dt);
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
	private ThingDTO createLoadActivityData(ActivityDTO data, History history, UserDetailsDTO user, boolean readOnly)
			throws ObjectNotFoundException {
		ThingDTO dto = new ThingDTO();
		if (history.getDataUrl() != null && history.getDataUrl().length() > 0) {
			dto.setUrl(history.getDataUrl());
			dto.setActivityId(history.getActivity().getID());
			dto.setHistoryId(history.getID());
			/*Concept root = closureServ.loadRoot(dto.getUrl());
			dto.setParentId(root.getID());*/
			if (history.getActivityData() != null) {
				dto.setNodeId(history.getActivityData().getID());
			}
			dto = thingServ.createContent(dto, user);
			if (dto.getNodeId() > 0) {
				Concept node = closureServ.loadConceptById(dto.getNodeId());
				dto.setStrings(dtoServ.readAllStrings(dto.getStrings(), node));
				dto.setLiterals(dtoServ.readAllLiterals(dto.getLiterals(), node));
				dto.setDates(dtoServ.readAllDates(dto.getDates(), node));
				dto.setNumbers(dtoServ.readAllNumbers(dto.getNumbers(), node));
				dto.setLogical(dtoServ.readAllLogical(dto.getLogical(), node));
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
		// dto = thingServ.createContent(dto);
		return dto;
	}

	/**
	 * Load my activities. Suit for all, except an applicant
	 * 
	 * @param data
	 * @param user
	 * @return
	 */
	public ApplicationsDTO myActivities(ApplicationsDTO data, UserDetailsDTO user) {
		data = presentActivities(data, user);
		data = scheduledActivities(data, user);
		return data;
	}

	/**
	 * Activities to execute now
	 * 
	 * @param data
	 * @param user
	 * @return
	 */
	public ApplicationsDTO presentActivities(ApplicationsDTO data, UserDetailsDTO user) {
		TableQtb table = data.getTable();
		if (table.getHeaders().getHeaders().size() == 0) {
			table.setHeaders(myHeaders(table.getHeaders()));
		}
		jdbcRepo.activities(user.getEmail());
		String select = "select * from _activities";
		String where = "Come<=(curdate() + INTERVAL 2 DAY)";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, table.getHeaders());
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
	private Headers myHeaders(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders()
		.add(TableHeader.instanceOf("Come", "scheduled", true, true, true, TableHeader.COLUMN_LOCALDATE, 0));
		headers.getHeaders()
		.add(TableHeader.instanceOf("pref", "prefLabel", true, true, true, TableHeader.COLUMN_LINK, 0));
		headers.getHeaders()
		.add(TableHeader.instanceOf("applicant", "applicant", true, true, true, TableHeader.COLUMN_STRING, 0));
		headers.getHeaders().add(
				TableHeader.instanceOf("workflow", "prod_app_type", true, true, true, TableHeader.COLUMN_STRING, 0));
		headers.getHeaders()
		.add(TableHeader.instanceOf("activity", "activity", true, true, true, TableHeader.COLUMN_STRING, 0));
		headers = boilerServ.translateHeaders(headers);
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		return headers;
	}

	/**
	 * Create scheduled activities table
	 * 
	 * @param data
	 * @param user
	 * @return
	 */
	private ApplicationsDTO scheduledActivities(ApplicationsDTO data, UserDetailsDTO user) {
		TableQtb table = data.getScheduled();
		if (table.getHeaders().getHeaders().size() == 0) {
			table.setHeaders(myHeaders(table.getHeaders()));
		}
		jdbcRepo.activities(user.getEmail());
		String select = "select * from _activities";
		String where = "Come>curdate()";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, table.getHeaders());
		TableQtb.tablePage(rows, table);
		table = boilerServ.translateRows(table);
		table.setSelectable(false);
		data.setScheduled(table);
		return data;
	}

	/**
	 * Create data for activity submit form Send-submit is not here
	 * 
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ActivitySubmitDTO submitCreateData(UserDetailsDTO user, ActivitySubmitDTO data)
			throws ObjectNotFoundException {
		data.setApplicant(accServ.isApplicant(user));
		if (data.getHistoryId() > 0) {
			// common data preparation logic - cleanup, etc
			if (data.isReload()) { // reload all three tables
				data.getActions().getRows().clear(); // list of possible actions
				data.getExecs().getRows().clear(); // list of all possible executors of the action selected
				data.getNextJob().getRows().clear(); // list of possible next activities of the action selected
				data.setReload(false);
			}
			if (data.isReloadExecs()) { // reload only executors, because a new activity has been selected
				data.getExecs().getRows().clear();
				data.setReloadExecs(false);
			}
			// create lists for choices
			// who am I?
			History his = boilerServ.historyById(data.getHistoryId());
			// for trace and monitoring only re-assign is allowed
			if (!data.isReassign() && his.getActConfig() == null) {
				data.setReassign(true);
			}
			Concept userConc = closureServ.getParent(his.getActivity());
			//
			if (accServ.isMyActivity(his.getActivity(), user) || accServ.isSupervisor(user)) {
				if (data.getActions().getRows().size() == 0) {
					data = createActions(user, data); // list of actions allowed
				}
				// determine the selected activity
				int selected = -1;
				for (TableRow row : data.getActions().getRows()) {
					if (row.getSelected()) {
						Long ls = new Long(row.getDbID());
						selected = ls.intValue();
					}
				}
				// systemDictNode(root, "0", messages.get("continue"));
				// systemDictNode(root, "1", messages.get("route_action"));
				// systemDictNode(root, "2", messages.get("newactivity"));
				// systemDictNode(root, "3", messages.get("cancel"));
				// systemDictNode(root, "4", messages.get("approve"));
				// systemDictNode(root, "5", messages.get("reject"));
				// systemDictNode(root, "6", messages.get("reassign"));
				// systemDictNode(root, "7", messages.get("amendment"))
				data.getScheduled().getRows().clear();
				switch (selected) {
				case 0:
					data = nextJobChoice(his, user, data);
					data.getExecs().getRows().clear();// ika24062022
					data = executorsNextChoice(his, user, data, false);
					break;
				case 1:
					data.getNextJob().getRows().clear();
					data.getExecs().getRows().clear();// ika24062022
					data = executorsThisChoice(his, user, data);
					break;
				case 2:
					data = nextJobChoice(his, user, data);
					data.getExecs().getRows().clear();// ika24062022
					data = executorsNextChoice(his, user, data, false);
					break;
				case 3:
					data.getNextJob().getRows().clear();
					data.getExecs().getRows().clear();
					break;
				case 4:
					data.getNextJob().getRows().clear();
					data.getExecs().getRows().clear();
					data = scheduled(his, data);
					break;
				case 5:
					data.getNextJob().getRows().clear();
					data.getExecs().getRows().clear();
					break;
				case 6:
					data.getNextJob().getRows().clear();
					data.getExecs().getRows().clear();// ika24062022
					data = executorsThisChoice(his, user, data);
					break;
				case 7:
					data.getNextJob().getRows().clear();
					data.getExecs().getRows().clear();
					data = scheduled(his, data);
					break;
				case 8:
					data = nextJobChoice(his, user, data);
					data.getExecs().getRows().clear();// ika24062022
					data = executorsNextChoice(his, user, data, false);
					break;
				default:
					data.getExecs().getRows().clear();
					data.getNextJob().getRows().clear();
				}
				return data;
			} else {
				throw new ObjectNotFoundException("submitCreateData. Access denied. current_user/should_be "
						+ user.getEmail() + "/" + userConc.getIdentifier(), logger);
			}
		} else {
			throw new ObjectNotFoundException("submitCreateData. History ID is ZERO", logger);
		}
	}

	/**
	 * Create list of scheduled runs in the host lifecycle stage Approve has been
	 * selected
	 * 
	 * @param his  - current history record
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ActivitySubmitDTO scheduled(History his, ActivitySubmitDTO data) throws ObjectNotFoundException {
		// any data may contain the scheduler(s)
		TableQtb table = data.getScheduled();
		if (table.getHeaders().getHeaders().size() == 0) {
			table.setHeaders(headersSchedule(table.getHeaders()));
		}
		table.getRows().clear();
		// String nextStage = systemServ.nextStageByApplDict(his,true);
		List<History> allHis = boilerServ.historyAllByApplication(his.getApplication());

		for (History h : allHis) {
			if (!h.getCancelled()) { // don't mind cancelled!
				if (h.getActivityData() != null) {
					Thing th = boilerServ.thingByNode(h.getActivityData());
					for (ThingScheduler ts : th.getSchedulers()) {
						Scheduler sch = boilerServ.schedulerByNode(ts.getConcept());
						String process = sch.getProcessUrl();
						LocalDate sched = boilerServ.localDateFromDate(sch.getScheduled());
						TableRow row = TableRow.instanceOf(ts.getID()); // we need only unique long
						row.getRow().add(TableCell.instanceOf("processes", process));
						row.getRow().add(TableCell.instanceOf("scheduled", sched, LocaleContextHolder.getLocale()));
						table.getRows().add(row);
					}
				}
			}
		}
		table.setSelectable(false);
		boilerServ.translateRows(table);
		return data;
	}

	/**
	 * Headers for scheduled table
	 * 
	 * @param headers
	 * @return
	 */
	private Headers headersSchedule(Headers headers) {
		headers.getHeaders().clear();
		/*	headers.getHeaders().add(TableHeader.instanceOf(
					"stages",
					"stages",
					true,
					false,
					false,
					TableHeader.COLUMN_I18,
					0));*/
		headers.getHeaders()
		.add(TableHeader.instanceOf("processes", "processes", true, false, false, TableHeader.COLUMN_I18, 0));
		headers.getHeaders().add(
				TableHeader.instanceOf("scheduled", "scheduled", true, false, false, TableHeader.COLUMN_LOCALDATE, 0));
		headers = boilerServ.translateHeaders(headers);
		return headers;
	}

	/**
	 * create executor's choice for this activity. To re-assign
	 * 
	 * @param his
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private ActivitySubmitDTO executorsThisChoice(History his, UserDetailsDTO user, ActivitySubmitDTO data)
			throws ObjectNotFoundException {
		Concept actConfig = his.getActConfig();
		if (actConfig == null) {
			// the activity is first
			actConfig = his.getApplConfig();
		}
		data.setExecs(executorsTable(his, actConfig, data.getExecs(), false));
		return data;
	}

	/**
	 * Propose all possible executors for activity selected
	 * 
	 * @param his
	 * @param user
	 * @param data
	 * @param limitToAU limit to the administrative unit
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private ActivitySubmitDTO executorsNextChoice(History his, UserDetailsDTO user, ActivitySubmitDTO data,
			boolean limitToAU) throws ObjectNotFoundException {
		Long nextActConfId = data.nextActivity();
		if (nextActConfId > 0) {
			Concept actConf = closureServ.loadConceptById(nextActConfId);
			data.getExecs().getRows().clear();
			data.setExecs(executorsTable(his, actConf, data.getExecs(), limitToAU));
		} else {
			data.getExecs().getRows().clear();
		}
		return data;
	}

	/**
	 * Propose all possible foreground activities, mark next
	 * 
	 * @param his
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private ActivitySubmitDTO nextJobChoice(History his, UserDetailsDTO user, ActivitySubmitDTO data)
			throws ObjectNotFoundException {
		if (data.getNextJob().getRows().size() == 0) {
			data = nextJobsTable(his, data); // create new table
		}
		return data;
	}

	/**
	 * Create a set of allowed actions using dictionary.system.submit
	 * systemDictNode(root, "0", messages.get("continue")); systemDictNode(root,
	 * "1", messages.get("route_action")); systemDictNode(root, "2",
	 * messages.get("newactivity")); systemDictNode(root, "3",
	 * messages.get("cancel")); systemDictNode(root, "4", messages.get("approve"));
	 * systemDictNode(root, "5", messages.get("reject")); systemDictNode(root, "6",
	 * messages.get("reassign")); systemDictNode(root, "7",
	 * messages.get("amendment"));
	 * 
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ActivitySubmitDTO createActions(UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		History curHis = boilerServ.historyById(data.getHistoryId());
		List<String> allowed = new ArrayList<String>();
		if (data.isSupervisor()) {
			// NMRA supervisor
			data = validServ.actionCancel(curHis, data);
			if (data.isValid()) {
				allowed.add("3"); // cancel
			}
			data = validServ.actionNew(curHis, data);
			if (data.isValid()) {
				allowed.add("2"); // new activity, any activity
			}
			data = validServ.actionReassign(curHis, data);
			if (data.isValid()) {
				allowed.add("6"); // reassign the executor
			}

		} else {
			if (data.isApplicant()) {
				data = validServ.submitNext(curHis, user, data);
				if (data.isValid()) {
					allowed.add("0"); // applicant is restricted to submit next and has not rights to select
					// activity/executor
				}
			} else {
				data = validServ.submitNext(curHis, user, data);
				if (data.isValid()) {
					allowed.add("0"); // next activity
				}
				data = validServ.submitRoute(curHis, user, data);
				if (data.isValid()) {
					allowed.add("1"); // route to another executor
				}
				data = validServ.submitApprove(curHis, user, data, null);
				if (data.isValid()) {
					allowed.add("4");
				}
				data = validServ.submitAmendment(curHis, user, data);
				if (data.isValid()) {
					allowed.add("7"); // implement an amendment
				}
				data = validServ.submitReject(curHis, user, data);
				if (data.isValid()) {
					allowed.add("5");
				}
				data = validServ.submitDeregistration(curHis, user, data);
				if (data.isValid()) {
					allowed.add("8"); // implement an amendment
				}
			}
		}

		DictionaryDTO actDict = systemServ.submitActionDictionary();
		TableQtb dictTable = actDict.getTable();
		List<Concept> items = new ArrayList<Concept>();
		for (TableRow row : dictTable.getRows()) {
			Concept conc = closureServ.loadConceptById(row.getDbID());
			if (allowed.contains(conc.getIdentifier())) {
				items.add(conc);
			}
		}
		data = actionsTable(items, data);
		data.clearErrors();
		return data;
	}

	/**
	 * Table contains all submit actions possible in this case
	 * 
	 * @param items
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ActivitySubmitDTO actionsTable(List<Concept> items, ActivitySubmitDTO data) throws ObjectNotFoundException {
		TableQtb table = data.getActions();
		if (table.getHeaders().getHeaders().size() == 0) {
			table.setHeaders(headersActions(table.getHeaders()));
		}
		for (Concept item : items) {
			TableRow row = new TableRow();
			try {
				long dbID = Long.parseLong(item.getIdentifier());
				row.setDbID(dbID);
				String pref = literalServ.readPrefLabel(item);
				String descr = literalServ.readDescription(item);
				row.getRow().add(TableCell.instanceOf("pref", pref));
				row.getRow().add(TableCell.instanceOf("description", descr));
				// if(dbID==0) {
				// row.setSelected(true);
				// }
				table.getRows().add(row);
				table.setSelectable(!data.isApplicant());
			} catch (NumberFormatException e) {
				throw new ObjectNotFoundException(
						"actionsTable. Invalid action code code/id " + item.getIdentifier() + "/" + item.getID(),
						logger);
			}
		}
		return data;
	}

	/**
	 * Headers for actions table
	 * 
	 * @param headers
	 * @return
	 */
	private Headers headersActions(Headers headers) {
		headers.getHeaders()
		.add(TableHeader.instanceOf("pref", "label_actions", true, false, false, TableHeader.COLUMN_STRING, 0));
		headers.getHeaders().add(
				TableHeader.instanceOf("description", "description", true, false, false, TableHeader.COLUMN_STRING, 0));
		headers.setPageSize(50);
		boilerServ.translateHeaders(headers);
		return headers;
	}

	/**
	 * Table contains all possible executors of the next activity
	 * 
	 * @param actConf
	 * @param his
	 * @param execTable
	 * @param limitToAU limit to admin unit
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public TableQtb executorsTable(History curHis, Concept actConf, TableQtb execTable, boolean limitToAU)
			throws ObjectNotFoundException {
		if (execTable.getRows().size() == 0) {
			Concept role = assmServ.activityExecutorRole(actConf);
			if (!accServ.isApplicantRole(role)) {
				Concept applDict = curHis.getApplDict();
				Concept admUnit = adminUnit(actConf, curHis);
				if (role != null && applDict != null) {
					String where = "";
					if (admUnit != null && limitToAU) {
						where = "auid='" + admUnit.getID() + "'";
					} else {
						where = "";
					}
					String select = "select distinct ID, username, email, orgname, local from executors_select";
					jdbcRepo.executors_select(role.getID(), applDict.getID());
					// real executors
					execTable.setHeaders(headersExecutors(execTable.getHeaders()));
					List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, execTable.getHeaders());
					if (rows.size() == 0) {
						logger.warn("An executor not found - search for office secretaries. Activity config ID is "
								+ actConf.getID());
						role = systemServ.loadRole(SystemService.ROLE_SECRETARY);
						jdbcRepo.executors_select(role.getID(), applDict.getID());
						rows = jdbcRepo.qtbGroupReport(select, "", where, execTable.getHeaders());
					}
					if (rows.size() == 0) {
						logger.warn(
								"an executor not found - send to supervisors in the central office. Activity config ID is "
										+ actConf.getID());
						role = systemServ.loadRole(SystemService.ROLE_ADMIN);
						jdbcRepo.executors_select(role.getID(), applDict.getID());
						where = "auid is null";
						rows = jdbcRepo.qtbGroupReport(select, "", where, execTable.getHeaders());
					}
					execTable.setSelectable(true);
					TableQtb.tablePage(rows, execTable);
				}
			} else {
				Concept parent = closureServ.getParent(curHis.getApplicationData());
				String applicant = parent.getIdentifier();
				execTable.setHeaders(headersExecutors(execTable.getHeaders()));
				List<TableRow> rows = new ArrayList<TableRow>();
				List<TableCell> cells = new ArrayList<TableCell>();
				TableRow row = new TableRow();
				TableCell cell = new TableCell();
				cell.setKey("username");
				cell.setValue("APPLICANT");
				cells.add(cell);
				TableCell cell1 = new TableCell();
				cell1.setKey("orgname");
				cell1.setValue("-----");
				cells.add(cell1);
				TableCell cell2 = new TableCell();
				cell2.setKey("email");
				cell2.setValue(applicant);
				cells.add(cell2);
				row.setRow(cells);
				row.setSelected(true);
				rows.add(row);

				execTable.setSelectable(true);

				TableQtb.tablePage(rows, execTable);
			}
		} else {
			execTable.getRows().clear();
		}
		return execTable;
	}

	/**
	 * Headers for Executor's table
	 * 
	 * @param headers
	 * @return
	 */
	public Headers headersExecutors(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(
				TableHeader.instanceOf("username", "global_name", true, false, false, TableHeader.COLUMN_STRING, 0));
		headers.getHeaders().add(TableHeader.instanceOf("orgname", "organizationauthority", true, false, false,
				TableHeader.COLUMN_STRING, 0));
		headers.getHeaders().add(
				TableHeader.instanceOf("email", "executor_email", true, false, false, TableHeader.COLUMN_STRING, 0));
		headers = boilerServ.translateHeaders(headers);
		headers.setPageSize(Integer.MAX_VALUE);
		return headers;
	}

	/**
	 * Create nextJob table if needed
	 * 
	 * @param his  activity configuration root
	 * @param data table with all foreground activities in this application
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private ActivitySubmitDTO nextJobsTable(History his, ActivitySubmitDTO data) throws ObjectNotFoundException {
		TableQtb table = data.getNextJob();
		if (table.getRows().size() == 0) {
			List<Concept> path = closureServ.loadParents(his.getActConfig());
			table.setHeaders(headersNextJob(table.getHeaders()));
			// jdbcRepo.workflowActivities(his.getApplConfig().getID());
			jdbcRepo.workflowActivities(path.get(0).getID());
			List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from workflow_activities", "", "bg!=1",
					table.getHeaders());
			table.setSelectable(true);
			TableQtb.tablePage(rows, table);
			// mark next activity
			for (int i = 0; i < table.getRows().size(); i++) {
				if (table.getRows().get(i).getDbID() == his.getActConfig().getID()) {
					if (i + 1 < table.getRows().size()) {
						table.getRows().get(i + 1).setSelected(true);
						break;
					}
				}
			}
		}
		return data;
	}

	/**
	 * headers for next
	 * 
	 * @param headers
	 * @return
	 */
	private Headers headersNextJob(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders()
		.add(TableHeader.instanceOf("pref", "activity", true, false, false, TableHeader.COLUMN_STRING, 0));
		headers.getHeaders()
		.add(TableHeader.instanceOf("descr", "description", true, false, false, TableHeader.COLUMN_STRING, 0));
		headers = boilerServ.translateHeaders(headers);
		headers.setPageSize(Integer.MAX_VALUE);
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
	 * Submit an activity in a workflow
	 * The submit action is defined by the code of an activity
	 * This code should be selected by a user from the left upper table
	 * The content of the left upper table is calculated before  
	 * 
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 * @throws JsonProcessingException
	 */
	@Transactional
	public ActivitySubmitDTO submitSend(UserDetailsDTO user, ActivitySubmitDTO data)
			throws ObjectNotFoundException, JsonProcessingException {
		History curHis = boilerServ.historyById(data.getHistoryId());
		if (accServ.isActivityExecutor(curHis.getActivity(), user) || accServ.isSupervisor(user)) {
			int actCode = data.actionSelected();
			if (data.isApplicant()) {
				actCode = 0;
			}
			;
			/*Possible activity codes from SystemService.submitActionDictionary 
			 systemDictNode(root, "0", messages.get("continue"));
			systemDictNode(root, "1", messages.get("route_action"));
			systemDictNode(root, "2", messages.get("newactivity"));
			systemDictNode(root, "3", messages.get("cancel"));
			systemDictNode(root, "4", messages.get("approve"));
			systemDictNode(root, "5", messages.get("reject"));
			systemDictNode(root, "6", messages.get("reassign"));
			systemDictNode(root, "7", messages.get("amendment"));
			systemDictNode(root, "8", messages.get("deregistration"));*/
			switch (actCode) {
			case 0: // NMRA executor continue workflow from the activity selected
				data = validServ.submitNext(curHis, user, data);
				if(data.isValid()) {
					if (accServ.isApplicant(user)) {
						data = submitNext(curHis, user, data);
					} else {
						sendEmailAttention(user, curHis, data);
						data = validServ.submitNextData(curHis, user, data);
						data = submitNext(curHis, user, data);
					}
				}
				return data;
			case 1: // NMRA executor route the activity to other executor
				data = validServ.submitRoute(curHis, user, data);
				data = validServ.submitReAssign(curHis, user, data);
				if(data.isValid()) {
					data = submitReAssign(curHis, user, data);
				}
				return data;
			case 2: // NMRA executor initiate an additional activity for others NMRA executors or
				// for the applicant
				data = checkApplicantExecutor(data, curHis);
				data = validServ.submitAddActivity(curHis, user, data);
				data = validServ.submitAddActivityData(curHis, user, data);
				if(data.isValid()) {
					data = submitAddActivity(curHis, user, data);
				}
				return data;
			case 3:
				data = validServ.actionCancel(curHis, data);
				data = validServ.actionCancelData(curHis, data);
				if(data.isValid()) {
					data = actionCancel(curHis, data);
				}
				return data;
			case 4:
				data = submitSendApprove(data, user, curHis); //validation is inside
				return data;
			case 5:
				data = validServ.submitReject(curHis, user, data);
				data = validServ.submitRejectData(curHis, user, data);
				if(data.isValid()) {
					data = submitReject(curHis, user, data);
				}
				return data;
			case 6:
				data = validServ.actionReassign(curHis, data);
				data = validServ.submitReAssign(curHis, user, data);
				if(data.isValid()) {
					data = submitReAssign(curHis, user, data);
				}
				return data;
			case 7:
				data = validServ.submitAmendment(curHis, user, data);
				if (data.isValid()) {
					sendEmailAttention(user, curHis, data);
					data = isAmended(curHis, user, data);
					data = submitApprove(curHis, user, data);
				}
				return data;
			case 8:
				data = validServ.submitDeregistration(curHis, user, data);
				data = validServ.submitDeregistrationData(curHis, data);
				if (data.isValid()) {
					sendEmailAttention(user, curHis, data);
					data = submitDeregistration(curHis, user, data);
				}
				return data;
			default:
				data.setIdentifier(messages.get("pleaseselectaction"));
				data.setValid(false);
				return data;
			}
		} else {
			throw new ObjectNotFoundException("submitSend. Access denied", logger);
		}
	}

	/**
	 * 10.11.2022 khomenska
	 *  Approve action
	 */
	private ActivitySubmitDTO submitSendApprove(ActivitySubmitDTO data, UserDetailsDTO user, History curHis) throws ObjectNotFoundException{
		Concept applRoot = closureServ.loadParents(curHis.getApplication()).get(0);
		String applUrl = applRoot.getIdentifier();
		Concept configRoot = closureServ.loadRoot("configuration." + applUrl);
		List<Concept> nextActs = loadActivities(configRoot);
		data = validServ.submitApprove(curHis, user, data, nextActs);
		if(data.isValid()) {
			data = submitApprove(curHis, user, data);
			sendEmailAttention(user, curHis, data);
		}
		return data;
	}

	private void sendEmailAttention(UserDetailsDTO user, History curHis, ActivitySubmitDTO data)
			throws ObjectNotFoundException {
		// 23.10.2022
		boolean attentionActivity = validServ.isActivityAttention(curHis.getActConfig());
		if (attentionActivity) {
			String applicantEmail = accessControlServ.applicantEmailByApplication(curHis.getApplication());
			String applName = literalServ.readPrefLabel(curHis.getApplicationData());
			String curActivity = literalServ.readPrefLabel(curHis.getActConfig());
			String nextActivity = "";
			if (data.getNextJob().getRows() != null && data.getNextJob().getRows().size() > 0) {
				for (TableRow r : data.getNextJob().getRows()) {
					if (r.getSelected()) {
						nextActivity = r.getCellByKey("pref").getValue();
						break;
					}
				}
			}

			String attnote = literalServ.readValue("attnote", curHis.getActConfig());
			String res = mailService.createAttentionMail(user, applicantEmail, applName, curActivity, nextActivity, attnote);
			data.getNotes().setValue(res);
			data.getNotes().setMark(true);
			data.getNotes().setReadOnly(true);
			data.getNotes().setTextArea(true);
		}
	}

	/**
	 * Make de-registration things
	 * 
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private ActivitySubmitDTO submitDeregistration(History curHis, UserDetailsDTO user, ActivitySubmitDTO data)
			throws ObjectNotFoundException {
		if (data.isValid()) {
			closeActivity(curHis, false);
			cancelUsersActivities(user, curHis);
			Concept deregData = amendmentServ.amendedConcept(curHis.getApplicationData());
			cancelDataActivities(deregData, true); // cancel all activities, include monitoring
			List<Long> executors = data.executors();
			long actConfId = data.nextActivity();
			if (actConfId > 0) {
				Concept actConf = closureServ.loadConceptById(actConfId);
				if (executors.size() > 0) {
					for (Long execId : executors) {
						Concept userConc = closureServ.loadConceptById(execId);
						activityCreate(null, actConf, curHis, userConc.getIdentifier(), data.getNotes().getValue());
					}
				}
			}
		}
		return data;
	}

	/**
	 * Cancel all activities related to application data
	 * 
	 * @param applicationData - application data
	 * @param all             - remove all activities
	 * @throws ObjectNotFoundException
	 */
	private void cancelDataActivities(Concept applicationData, boolean all) throws ObjectNotFoundException {
		List<History> hisList = boilerServ.historyAll(applicationData);
		for (History his : hisList) {
			if (all) {
				closeActivity(his, true);
			} else {
				if (his.getActConfig() != null) {
					closeActivity(his, true);
				}
			}
		}
	}

	/**
	 * Is this amendment fully implemented?
	 * 
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ActivitySubmitDTO isAmended(History curHis, UserDetailsDTO user, ActivitySubmitDTO data)
			throws ObjectNotFoundException {
		Concept amended = amendmentServ.amendedConcept(curHis.getApplicationData());
		Concept amendment = amendmentServ.amendmentConcept(curHis.getApplicationData(), amended);
		if (!amendmentServ.compareConcepts(amendment, amended)) {
			data.setValid(false);
			data.setIdentifier(messages.get("amendmentisnotimplemented"));
		}
		return data;
	}

	/**
	 * Special case when executor for this activity defined as an applicant
	 * 
	 * @param data
	 * @return applicant executor email or empty string
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private ActivitySubmitDTO checkApplicantExecutor(ActivitySubmitDTO data, History curHis)
			throws ObjectNotFoundException {
		if (data.getExecs().getRows().size() == 0) { // no executors defined, suspect an applicant
			long addAct = data.nextActivity();
			if (addAct > 0) {
				Concept actConfig = closureServ.loadConceptById(addAct);
				Thing thing = new Thing();
				thing = boilerServ.thingByNode(actConfig, thing);
				if (thing.getID() > 0) {
					for (ThingDict td : thing.getDictionaries()) {
						if (td.getVarname().equalsIgnoreCase("executives")) {
							if (td.getConcept().getIdentifier().equalsIgnoreCase("APPLICANT")) {
								data.setApplicantEmail(accServ.applicantEmailByApplication(curHis.getApplication()));
							}
						}
					}
				}
			}
		}
		return data;
	}

	/**
	 * Reject an application and move it to the applicant
	 * 
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 * @throws JsonProcessingException
	 */
	@Transactional
	private ActivitySubmitDTO submitReject(History curHis, UserDetailsDTO user, ActivitySubmitDTO data)
			throws ObjectNotFoundException, JsonProcessingException {
		Concept applicant = closureServ.getParent(curHis.getApplicationData());
		rejectApplication(curHis, applicant.getIdentifier(), data);
		sendEmailAttention(user, curHis, data);
		return data;
	}

	/**
	 * Reject the current application
	 * 
	 * @param curHis
	 * @param applicantEmail
	 * @param data
	 * @throws ObjectNotFoundException
	 * @throws JsonProcessingException
	 */
	@Transactional
	private void rejectApplication(History curHis, String applicantEmail, ActivitySubmitDTO data)
			throws ObjectNotFoundException, JsonProcessingException {
		// cancel all histories, close the current
		List<History> allHist = boilerServ.historyAll(curHis.getApplicationData());
		for (History h : allHist) {
			if (h.getGo() == null) {
				closeActivity(h, h.getID() != curHis.getID());
			}
		}
		// create new application
		ThingDTO tdto = new ThingDTO();
		tdto.setNodeId(curHis.getApplicationData().getID());
		Concept applicant = closureServ.getParent(curHis.getApplication());
		Concept application = closureServ.getParent(applicant);
		tdto.setApplicationUrl(application.getIdentifier());
		tdto.setApplDictNodeId(curHis.getApplDict().getID());
		UserDetailsDTO user = new UserDetailsDTO();
		user.setEmail(applicantEmail);
		tdto = thingServ.loadThing(tdto, user);
		tdto = thingServ.createApplication(tdto, applicantEmail, curHis.getApplicationData());
		// add notes to the history data
		History his = boilerServ.historyById(tdto.getHistoryId());
		his.setPrevNotes(data.getNotes().getValue());
		his = boilerServ.saveHistory(his);
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
	 * Submit to approve
	 * 
	 * @TODO not implemented yet
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ActivitySubmitDTO submitApprove(History curHis, UserDetailsDTO user, ActivitySubmitDTO data)
			throws ObjectNotFoundException {
		if (systemServ.isGuest(curHis)) {
			data = submitGuest(curHis, data);
			return data;
		}
		if (systemServ.isHost(curHis)) {
			data = submitHost(curHis, data);
			return data;
		}
		if (systemServ.isAmend(curHis)) {
			data = submitAmend(curHis, data);
			return data;
		}
		if (systemServ.isDeregistration(curHis)) {
			data.clearErrors();
			cancellActivities(curHis);
			return data;
		}
		data.setIdentifier(messages.get("invalidstage"));
		data.setValid(false);
		return data;
	}

	/**
	 * Submit an amendment
	 * 
	 * @param curHis
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private ActivitySubmitDTO submitAmend(History curHis, ActivitySubmitDTO data) throws ObjectNotFoundException {
		data = submitGuest(curHis, data);
		return data;
	}

	/**
	 * Submit Host application, create another scheduled host application Currently
	 * it is the same as submit guest
	 * 
	 * @param curHis
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private ActivitySubmitDTO submitHost(History curHis, ActivitySubmitDTO data) throws ObjectNotFoundException {
		data = submitGuest(curHis, data);
		return data;
	}

	/**
	 * Submit guest application
	 * 
	 * @param curHis
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ActivitySubmitDTO submitGuest(History curHis, ActivitySubmitDTO data) throws ObjectNotFoundException {
		if (data.isValid()) {
			cancellActivities(curHis);
			data = runScheduledGuestHost(curHis, data);
		}

		return data;
	}

	/**
	 * Run scheduled activities as well as related trace and monitoring ones Close
	 * all scheduled workflows with the same URL and data, but not this yet
	 * 
	 * @param curHis
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private ActivitySubmitDTO runScheduledGuestHost(History curHis, ActivitySubmitDTO data)
			throws ObjectNotFoundException {
		for (TableRow row : data.getScheduled().getRows()) {
			ThingScheduler ts = boilerServ.thingSchedulerById(row.getDbID());
			Scheduler sch = boilerServ.schedulerByNode(ts.getConcept());
			Concept dictConc = systemServ.hostDictNode(sch.getProcessUrl());
			// close previous scheduled
			Concept appldata = amendmentServ.initialApplicationData(curHis.getApplicationData());
			List<History> prevActivities = boilerServ.activities(sch.getProcessUrl(), appldata);
			for (History act : prevActivities) {
				closeActivity(act, true);
			}
			data = createHostApplication(curHis, sch, dictConc, data);
			if (!data.isValid()) {
				return data;
			}
		}
		return data;
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
	private ActivitySubmitDTO createHostApplication(History prevHis, Scheduler sch, Concept dictConc,
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
		//run activities
		Concept configRoot = closureServ.loadRoot("configuration." + applUrl);
		List<Concept> nextActs = loadActivities(configRoot);
		data = (ActivitySubmitDTO) validServ.workflowConfig(nextActs, configRoot, data);
		if (data.isValid()) {
			if (nextActs.size() > 0) {
				List<ActivityToRun> toRun = activitiesToRun(data, applUrl, prevHis, nextActs);
				if(toRun.size()>0 && data.isValid()) {
					History curHis = createHostHistorySample(prevHis, applConc, dictConc, configRoot);
					activityTrackRun(sch.getScheduled(), curHis, applUrl, applicantEmail); // tracking by an applicant
					activityMonitoringRun(sch.getScheduled(), curHis, applUrl); // monitoring by the all supervisors
					// run activities
					for(ActivityToRun act :toRun ) {
						for (String email : act.getExecutors()) {
							activityCreate(sch.getScheduled(), act.getConfig(), curHis, email, String.join(",",act.getFallBack()));
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
			logger.error(applUrl + " " + data.getIdentifier());
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
	 * Cancel all opened activities for the current application, but close the
	 * current
	 * 
	 * @param curHis
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public void cancellActivities(History curHis) throws ObjectNotFoundException {
		List<History> allHis = boilerServ.historyAllByApplication(curHis.getApplication());
		// close activities in the current
		for (History his : allHis) {
			if (his.getID() != curHis.getID() && his.getGo() == null) {
				closeActivity(his, true);
			}
		}
		closeActivity(curHis, false);
	}

	/**
	 * Cancel and activity
	 * 
	 * @param curHis
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private ActivitySubmitDTO actionCancel(History curHis, ActivitySubmitDTO data) throws ObjectNotFoundException {
		closeActivity(curHis, true);
		return data;
	}

	/**
	 * re-route this activity to others executors. Data will be lost!
	 * 
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ActivitySubmitDTO submitReAssign(History curHis, UserDetailsDTO user, ActivitySubmitDTO data)
			throws ObjectNotFoundException {
		if (data.isValid()) {
			// determine activity configuration
			Concept actConfig = curHis.getActConfig();
			if (actConfig == null) {
				actConfig = curHis.getApplConfig();
			}
			List<Long> executors = data.executors();
			// create activities
			for (Long execId : executors) {
				Concept userConc = closureServ.loadConceptById(execId);
				activityCreate(null, actConfig, curHis, userConc.getIdentifier(), data.getNotes().getValue());
			}
			if (curHis.getActConfig() != null) { // monitoring should be left intact!
				closeActivity(curHis, true);
			}
		}
		return data;
	}

	/**
	 * Submit to the next activity selected by user
	 * 
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private ActivitySubmitDTO submitNext(History curHis, UserDetailsDTO user, ActivitySubmitDTO data)
			throws ObjectNotFoundException {
		if (data.isValid()) {
			if (accServ.isApplicant(user)) {
				// restore next activity and executor's list
				data.getNextJob().getRows().clear();
				data.getExecs().getRows().clear();
				data = nextJobChoice(curHis, user, data);
				data = executorsNextChoice(curHis, user, data, true);
				for (TableRow row : data.getExecs().getRows()) {
					row.setSelected(true); // TODO more smart may be needed, because an applicant has no choice
				}
			}
			closeActivity(curHis, false);
			submitAddActivity(curHis, user, data);
		}
		return data;
	}

	/**
	 * Add activities for all executors listed In case next activity is
	 * Finalize.AMEND - implement the amendment
	 * 
	 * @param curHis
	 * @param user
	 * @param data
	 * @throws ObjectNotFoundException
	 */
	public ActivitySubmitDTO submitAddActivity(History curHis, UserDetailsDTO user, ActivitySubmitDTO data)
			throws ObjectNotFoundException {
		if (data.isValid()) {
			cancelUsersActivities(user, curHis);
			List<Long> executors = data.executors();
			long actConfId = data.nextActivity();
			Concept actConf = closureServ.loadConceptById(actConfId);
			if (executors.size() > 0) {
				data = amendmentServ.implement(curHis, actConf, data, user); // is it amendment?
				if (data.isValid()) {
					for (Long execId : executors) {
						// ika => execId=0 !!! APPLICANT
						String identifierUser = "";
						if (execId == 0) {
							identifierUser = data.getExecs().getRows().get(0).getRow().get(2).getValue();
						} else {
							Concept userConc = closureServ.loadConceptById(execId);
							identifierUser = userConc.getIdentifier();
						}
						activityCreate(null, actConf, curHis, identifierUser, data.getNotes().getValue());
					}
				}
			} else {
				if (data.getApplicantEmail().length() > 0) {
					// send to applicant
					activityCreate(null, actConf, curHis, data.getApplicantEmail(), data.getNotes().getValue());
				} else {
					// user sends activity to himself
					data = amendmentServ.implement(curHis, actConf, data, user); // is it amendment?
					if (data.isValid()) {
						activityCreate(null, actConf, curHis, user.getEmail(), data.getNotes().getValue());
					}
				}
			}
		}
		return data;
	}

	/**
	 * Cancel all activities opened for this user, except monitoring!
	 * 
	 * @param user
	 * @param curHis
	 * @throws ObjectNotFoundException
	 */
	private void cancelUsersActivities(UserDetailsDTO user, History curHis) throws ObjectNotFoundException {
		List<History> allHis = boilerServ.historyAllByApplication(curHis.getApplication());
		for (History his : allHis) {
			if (his.getGo() == null || his.getCancelled()) {
				Concept uconc = closureServ.getParent(his.getActivity());
				if (accServ.sameEmail(uconc.getIdentifier(), user.getEmail())) {
					if (his.getActConfig() != null) {
						closeActivity(his, true);
					}
				}
			}
		}
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
