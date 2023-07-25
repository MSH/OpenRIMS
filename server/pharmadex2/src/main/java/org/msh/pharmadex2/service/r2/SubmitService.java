package org.msh.pharmadex2.service.r2;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.model.r2.Scheduler;
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingDict;
import org.msh.pdex2.model.r2.ThingPerson;
import org.msh.pdex2.model.r2.ThingScheduler;
import org.msh.pdex2.model.r2.ThingThing;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.ActivitySubmitDTO;
import org.msh.pharmadex2.dto.ActivityToRun;
import org.msh.pharmadex2.dto.CheckListDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.VerifItemDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * "Submit" actions services
 * 
 * @author alexk
 *
 */
@Service
public class SubmitService {
	private static final Logger logger = LoggerFactory.getLogger(SubmitService.class);
	@Autowired
	private AccessControlService accServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private ValidationService validServ;
	@Autowired
	private AmendmentService amendmentServ;
	@Autowired
	private SystemService systemServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private ApplicationService appServ;
	@Autowired
	private AssemblyService assmServ;
	@Autowired
	private Messages messages;
	@Autowired
	private UserService userServ;
	@Autowired
	private MailService mailService;
	@Autowired
	private ThingService thingServ;
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
			if (data.isReload()) { // reload all  tables
				data.getActions().getRows().clear(); // list of possible actions
				data.getExecs().getRows().clear(); // list of all possible executors of the action selected
				data.getNextJob().getRows().clear(); // list of possible next activities of the action selected
				data.getRunHosts().getRows().clear();//list of all possible host processes to run
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
				//				systemDictNode(root, "0", messages.get("continue"));
				//				systemDictNode(root, "1", messages.get("route_action"));
				//				systemDictNode(root, "2", messages.get("newactivity"));
				//				systemDictNode(root, "3", messages.get("cancel"));
				//				systemDictNode(root, "4", messages.get("approve"));
				//				systemDictNode(root, "5", messages.get("reject"));
				//				systemDictNode(root, "6", messages.get("reassign"));
				//				systemDictNode(root, "7", messages.get("amendment"));
				//				systemDictNode(root, "8", messages.get("deregistration"));
				//				systemDictNode(root, "9", messages.get("revokepermit"));
				//				systemDictNode(root, "10", messages.get("decline"));
				//				systemDictNode(root, "11", messages.get("run"));
				data.getScheduled().getRows().clear();
				data.getRunHosts().getRows().clear();
				switch (selected) {
				case 0:
					//submit(Continue) NMRA || Applicant
					if(accServ.isEmployee(user)) {
						data = nextJobChoice(his, user, data);
						data.getExecs().getRows().clear();// ika24062022
						data.getRunHosts().getRows().clear();
						data = executorsNextChoice(his, user, data, false);
					}
					if(accServ.isApplicant(user)) {
						data.getNextJob().getRows().clear();
						data.getExecs().getRows().clear();// ika24062022
						data.getRunHosts().getRows().clear();
						data = executorsThisChoice(his, user, data);
					}
					break;
				case 2:
					//button&Monitoring (new_action) NMRA || Supervisor
					data = nextJobChoice(his, user, data);
					data.getExecs().getRows().clear();// ika24062022
					data.getRunHosts().getRows().clear();
					data = executorsNextChoice(his, user, data, false);
					break;
				case 3:
					// Monitoring (Cancel working) Supervisor
					data.getNextJob().getRows().clear();
					data.getExecs().getRows().clear();
					data.getRunHosts().getRows().clear();
					break;
				case 4:
					data.getRunHosts().getRows().clear();
					//submit (Approve) NMRA - isDeregistration
					if(systemServ.isDeregistration(his)) {
						data = nextJobChoice(his, user, data);
						data.getExecs().getRows().clear();// ika24062022
						data = executorsNextChoice(his, user, data, false);
						break;
					}else if(systemServ.isShutdown(his)) {
						data.getExecs().getRows().clear();
						data.getNextJob().getRows().clear();
					}else {
						//submit (Approve) NMRA - isGuest, isModification, isHost
						data.getNextJob().getRows().clear();
						data.getExecs().getRows().clear();
						data = scheduled(his, data);
						break;
					}
				case 5:
					//button (reject=returnToApplicant) NMRA - isGuest, isModification, isDeregistration
					data.getNextJob().getRows().clear();
					data.getExecs().getRows().clear();
					data.getRunHosts().getRows().clear();
					break;
				case 6:
					//Button&Monitoring (reassign) NMRA & Supervisor 
					data.getNextJob().getRows().clear();
					data.getExecs().getRows().clear();// ika24062022
					data.getRunHosts().getRows().clear();
					data = executorsThisChoice(his, user, data);
					break;
				case 9:
					//submit&Monitoring (revoke permit) - isHost
					data.getExecs().getRows().clear();
					data.getNextJob().getRows().clear();
					data.getRunHosts().getRows().clear();
					//data = executorsNMRA(his, user, data);
					break;
				case 10:
					data.getNextJob().getRows().clear();
					data.getExecs().getRows().clear();// ika24062022
					data.getRunHosts().getRows().clear();
					//data = executorsThisChoice(his, user, data);
					break;
				case 11:
					data.getNextJob().getRows().clear();
					data.getExecs().getRows().clear();
					data.getScheduled().getRows().clear();
					History curHis = boilerServ.historyById(data.getHistoryId());
					data=validServ.runHosts(curHis, data);
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
	 * Create a set of allowed actions using dictionary.system.submit
	 * systemDictNode(root, "0", messages.get("continue")); systemDictNode(root,
	 * "1", messages.get("route_action")); systemDictNode(root, "2",
	 * messages.get("newactivity")); systemDictNode(root, "3",
	 * messages.get("cancel")); systemDictNode(root, "4", messages.get("approve"));
	 * systemDictNode(root, "5", messages.get("reject")); systemDictNode(root, "6",
	 * messages.get("reassign")); systemDictNode(root, "7",
	 * messages.get("amendment"));systemDictNode(root, "10",
	 * messages.get("decline"));
	 * 
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ActivitySubmitDTO createActions(UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		List<String> allowed = new ArrayList<String>();
		//allowed actions list
		if(data.isMonitoring()) {//monitoring
			allowed.addAll(submitMonitoringAction(user, data));	//actions for processes opened using a monitoring table 
		}else { 
			if(data.isReassign() || data.isReject()) {//button
				allowed.addAll(submitButtonAction(user, data));		//actions that start by buttons (reassign, return to applicant)
			}else {
				allowed.addAll(submitAction(user, data));				//actions in the submit form
			}
		}
		//allowed actions table
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
	 * Collect all submit actions possible
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private List<String> submitAction(UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		History curHis = boilerServ.historyById(data.getHistoryId());
		List<String> allowed = new ArrayList<String>();
		if(validServ.isActivityFinalAction(curHis, SystemService.FINAL_NO)) {	//no special finalization actions, no finalization
			//for all executors continue to the next step is allowed
			data = validServ.submitNext(curHis, user, data);
			if (data.isValid()) {
				allowed.add("0");
			}
			if(accServ.isSupervisor(user) || accServ.isModerator(user)) {
				data = validServ.actionCancel(curHis, data);
				data = validServ.actionNew(curHis, data);
				if (data.isValid()) {
					allowed.add("2"); // new activity, any activity
				}
				if (data.isValid()) {
					allowed.add("3"); // cancel
				}
			}
			if(accServ.isEmployee(user)) {//NMRA
				if(systemServ.isHost(curHis)) {
					if(isRevokeProcessPossible(curHis,amendmentServ.initialApplicationData(curHis.getApplicationData()))) {
						allowed.add("9"); // revoke the permit is allowed for any NMRA user
					}
				}
				/*else {
					if(validServ.submitDecline(curHis, data)) {
						allowed.add("10");	//decline is a regular action
						allowed.add("0");		// next should be allowed too
					}
				}*/
			}
		}else //it is a finalization step. Possible one of outcomes "Approve (4) or  Decline(10)  
			// Revocation may be added if appropriate (Host workflow!!!)
			if(accServ.isEmployee(user)) {	// finalization allowed only for NMRA employees 
				data = validServ.submitApprove(curHis, user, data, null);
				if (data.isValid()){ 
					allowed.add("4"); 
					//workflow Host+Approve and Revoke + or continue if there is a "decline" in the workflow
					if(systemServ.isHost(curHis)) {
						if(isRevokeProcessPossible(curHis,amendmentServ.initialApplicationData(curHis.getApplicationData()))) {
							allowed.add("9"); // revoke the permit
						}
					}
					allowed.add("0");//or continue, to select activity "decline"
				}				//---else {	//for current, decline possible only for guest processes
				if(validServ.submitDecline(curHis, data)) {
					allowed.add("10");
					allowed.add("0"); // next should be allowed too
				}
			}
		return allowed;
	}
	private List<String> submitButtonAction(UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		History curHis = boilerServ.historyById(data.getHistoryId());
		List<String> allowed = new ArrayList<String>();
		if(accServ.isEmployee(user)) {//NMRA
			if(data.isReject()) {
				if(!systemServ.isHost(curHis) || !systemServ.isShutdown(curHis)) {
					//data = validServ.submitReject(curHis, user, data);
					//if (data.isValid()) {
					allowed.add("5");
					//}
				}
			}else {
				data = validServ.actionNew(curHis, data);
				if (data.isValid()) {
					allowed.add("2"); // new activity, any activity
				}
				data = validServ.actionReassign(curHis, data);
				if (data.isValid()) {
					allowed.add("6"); // reassign the executor
				}
			}
		}
		return allowed;
	}
	/**
	 * Actions allowed from the monitoring feature 
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private List<String> submitMonitoringAction(UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		History curHis = boilerServ.historyById(data.getHistoryId());
		List<String> allowed = new ArrayList<String>();
		if(accServ.isSupervisor(user) || accServ.isModerator(user)) {//SUPERVISOR or moderator
			data = validServ.actionReassign(curHis, data);
			if (data.isValid()) {
				allowed.add("6"); // reassign the executor
			}
			data = validServ.actionNew(curHis, data);
			if (data.isValid()) {
				allowed.add("2"); // new activity, any activity
			}
			data = validServ.actionCancel(curHis, data);
			if (data.isValid()) {
				allowed.add("3"); // cancel
			}
			if(isRevokeProcessPossible(curHis,amendmentServ.initialApplicationData(curHis.getApplicationData()))) {
				allowed.add("9"); // revoke the permit
			}
			data=validServ.runHosts(curHis, data);
			if(data.isValid()) {
				allowed.add("11");	//run host process
			}
		}
		return allowed;
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
	 * create executor's choice for this activity. To revokePermit
	 * 
	 * @param his
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	private ActivitySubmitDTO executorsNMRA(History his, UserDetailsDTO user, ActivitySubmitDTO data)
			throws ObjectNotFoundException {
		/*we need to get a concept of process configuration RevokePermit*/
		Concept nodeApplData = his.getApplicationData();
		Thing thing = new Thing();
		thing = boilerServ.thingByNode(nodeApplData, thing);
		String processUrl = thing.getUrl();
		Concept dictConc = systemServ.revokepermitDictNode(processUrl);
		processUrl = literalServ.readValue(LiteralService.APPLICATION_URL, dictConc);

		Concept actConfig = closureServ.loadRoot("configuration." + processUrl);
		if (actConfig == null) {
			return data;
		}
		TableQtb exec=executorsTable(his, actConfig, data.getExecs(), true);

		data.setExecs(exec);
		return data;
	}
	/**
	 * Can we run the revoke permit process for the permit data given? 
	 * @param curHis  the activity
	 * @param applData permit data
	 * @return true, if it is existing
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public boolean isRevokeProcessPossible( History curHis, Concept applData) throws ObjectNotFoundException {
		Concept root=boilerServ.getRootTree(applData);
		Concept conDict=systemServ.revokepermitDictNode(root.getIdentifier());
		if(conDict!=null) {		// the revoke permit process is defined, check the possibility to run it
			jdbcRepo.application_history(applData.getID());	//take all history for the permit data into a temporary table
			TableQtb table=new TableQtb();
			table.getHeaders().setPageSize(Integer.MAX_VALUE);
			table = appServ.applicationHistoryTableRows(table, true);	//take not completed job units
			if(table.getRows().size()>0) {
				List<TableRow> rows =table.getRows();
				for(TableRow row:rows) {
					History h=boilerServ.historyById(row.getDbID());
					if(systemServ.isGuest(h) || systemServ.isDeregistration(h) || systemServ.isShutdown(h)) {
						return false;	//revoke is impossible when the permit is not registered yet, or on deregistration or on revocation
					}
				}
				return true;
			}
			return true;	//there are no not completed job units, it is strange, but revoke is possible
		}
		return false;	//not defined
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
				//table.setSelectable(!data.isApplicant()); delete this condition IK 28.06.2023
			} catch (NumberFormatException e) {
				throw new ObjectNotFoundException(
						"actionsTable. Invalid action code code/id " + item.getIdentifier() + "/" + item.getID(),
						logger);
			}
		}
		return data;
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
				Concept admUnit = appServ.adminUnit(actConf, curHis);
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
			data=(CheckListDTO) submitCondition(curHis,data, user);
			if(data.isValid()) {
				Concept applRoot = closureServ.loadParents(curHis.getApplication()).get(0);
				String applUrl = applRoot.getIdentifier();
				data = (CheckListDTO)validServ.validWorkFlowConfig(data, applUrl);
				if (data.isValid()) {
					Concept configRoot = closureServ.loadRoot("configuration." + applUrl);
					List<Concept> nextActs = boilerServ.loadActivities(configRoot);
					boolean fullValid=checkPagesDefined(curHis.getApplicationData());
					if(fullValid) {
						if (nextActs.size() > 0) {
							List<ActivityToRun> toRun = appServ.activitiesToRun(data, applUrl, curHis, nextActs);
							//finish the current activity and run others
							if(toRun.size()>0 && data.isValid()) {
								Concept userConcept = userServ.userConcept(user);
								if(userConcept != null) {
									curHis.setExecutor(userConcept);
								}
								curHis = appServ.closeActivity(curHis, false);
								// tracking by an applicant
								appServ.activityTrackRun(null, curHis, applUrl, user.getEmail()); 
								// monitoring by the all supervisors as a last resort
								appServ.activityMonitoringRun(null, curHis, applUrl); 
								// run activities
								for(ActivityToRun act :toRun ) {
									for (String email : act.getExecutors()) {
										appServ.activityCreate(null, act.getConfig(), curHis, email, String.join(",",act.getFallBack()));
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
					}else {
						data.setValid(false);
						data.setIdentifier(messages.get("errorApplNotFull"));
					}
				}

			} else {
				throw new ObjectNotFoundException(data.getIdentifier(), logger);
			}
		}
		return data;
	}
	/**
	 * Check submit conditions yet another time just before submitting
	 * @param curHis
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private AllowValidation submitCondition(History curHis, AllowValidation data, UserDetailsDTO user) throws ObjectNotFoundException {
		data.clearErrors();
		Concept applDict=curHis.getApplDict();
		Concept applicationData=curHis.getApplicationData();
		Concept dict = closureServ.getParent(applDict);
		if(dict.getIdentifier().equalsIgnoreCase(SystemService.DICTIONARY_GUEST_APPLICATIONS)
				|| dict.getIdentifier().equalsIgnoreCase(SystemService.DICTIONARY_HOST_APPLICATIONS)) {
			// new permit and hosts are allowed unconditionally
			return data;
		}
		if(dict.getIdentifier().equalsIgnoreCase(SystemService.DICTIONARY_GUEST_AMENDMENTS)
				|| dict.getIdentifier().equalsIgnoreCase(SystemService.DICTIONARY_GUEST_DEREGISTRATION)
				|| dict.getIdentifier().equalsIgnoreCase(SystemService.DICTIONARY_GUEST_INSPECTIONS)) {
			VerifItemDTO dto = new VerifItemDTO();
			dto.setApplDictNodeId(applDict.getID());
			dto.setApplID(applicationData.getID());
			dto=validServ.verifAddDeristrationModificationInspection(dto,curHis);
			if(!dto.isValid()) {
				data.addError(dto.getIdentifier());
			}
			return data;
		}
		return data;
	}
	/**
	 * check "persons" (in general 1:m)
	 * @param page
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private boolean checkPagesPersonDefined(Concept page) throws ObjectNotFoundException {
		Thing pThing = new Thing();
		pThing=boilerServ.thingByNode(page, pThing);
		for(ThingPerson tp : pThing.getPersons()) {
			if (!checkPagesDefined(tp.getConcept())){
				return false;
			}
		}
		return true;
	}
	/**
	 * Have all pages been defined?
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional			// may become public in the future
	private boolean checkPagesDefined(Concept data) throws ObjectNotFoundException {
		boolean ret = true;
		//check persons on the first page
		if(!checkPagesPersonDefined(data)) {
			return false;
		}
		// get thing
		Thing thing = new Thing();
		thing=boilerServ.thingByNode(data, thing);
		if(thing.getID()>0) {
			// get all pages
			Map<String,Concept> pages =new LinkedHashMap<String, Concept>();
			for(ThingThing tt : thing.getThings()) {
				pages.put(tt.getUrl().toUpperCase(),tt.getConcept());
			}
			// get data configuration URL
			Concept owner = closureServ.getParent(data);
			if(owner != null) {
				Concept root=closureServ.getParent(owner);
				if(root != null) {
					List<Assembly> assms =assmServ.loadDataConfiguration(root.getIdentifier());
					for(Assembly assm :assms) {
						if(assm.getClazz().equalsIgnoreCase("things")) {
							String url = assm.getUrl();
							Concept page = pages.get(url.toUpperCase());
							if(page==null) {
								return false;
							}else {
								if(!checkPagesPersonDefined(page)) {
									return false;
								}
							}
						}
					}
				}else {
					ret=false;
				}
			}else {
				ret=false;
			}
		}else {
			ret=false;
		}
		return ret;
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
			List<Long> executors = data.executors();
			long actConfId = data.nextActivity();
			Concept actConf = closureServ.loadConceptById(actConfId);
			if (executors.size() > 0) {
				if(systemServ.isAmend(curHis)){
					// form amendment process, if for the next activity finalization action is  AMEND or ACCEPT,
					// then, implement the amendment
					data = amendmentServ.implement(curHis, actConf, data, user);
				}
				if (data.isValid()) {
					// then implement next actions
					for (Long execId : executors) {
						String identifierUser = "";
						if (execId == 0) {  
							// ika => execId=0 !!! APPLICANT is an executor of the next activity. It is only for backward compatibility
							identifierUser = data.getExecs().getRows().get(0).getRow().get(2).getValue();
						} else {
							//otherwise, the executor of the next activity is NMRA employee
							Concept userConc = closureServ.loadConceptById(execId);
							identifierUser = userConc.getIdentifier();
						}
						//create the next activity
						appServ.activityCreate(null, actConf, curHis, identifierUser, data.getNotes().getValue());
					}
				}
			} else {
				// The executor of the next activity is an applicant
				if (data.getApplicantEmail().length() > 0) {
					appServ.activityCreate(null, actConf, curHis, data.getApplicantEmail(), data.getNotes().getValue());
				} else {
					// applicant sends activity to self
					appServ.activityCreate(null, actConf, curHis, user.getEmail(), data.getNotes().getValue());
				}
			}
		}
		return data;
	}
	/**
	 * Submit an activity in a workflow
	 * The submit action is defined by the code of an activity
	 * This code should be selected by a user from the left upper table
	 * The content of the left upper table is calculated in submitCreateData
	 * 
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 * @throws JsonProcessingException
	 */
	/*Possible activity codes from SystemService.submitActionDictionary 
	 systemDictNode(root, "0", messages.get("continue"));
	systemDictNode(root, "1", messages.get("route_action"));
	systemDictNode(root, "2", messages.get("newactivity"));
	systemDictNode(root, "3", messages.get("cancel"));
	systemDictNode(root, "4", messages.get("approve"));
	systemDictNode(root, "5", messages.get("reject"));
	systemDictNode(root, "6", messages.get("reassign"));
	systemDictNode(root, "7", messages.get("amendment"));
	systemDictNode(root, "8", messages.get("deregistration"));
	systemDictNode(root, "9", messages.get("revokepermit"));*/
	@Transactional
	public ActivitySubmitDTO submitSend(UserDetailsDTO user, ActivitySubmitDTO data)
			throws ObjectNotFoundException, JsonProcessingException {
		History curHis = boilerServ.historyById(data.getHistoryId());
		data.clearErrors();
		if (accServ.isActivityExecutor(curHis.getActivity(), user) || accServ.isSupervisor(user)) {
			int actCode = data.actionSelected();
			if (data.isApplicant()) {
				actCode = 0;						//for applicant only "continue" is allowed
			};
			switch (actCode) {
			case 0:
				//Continue
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
			case 2: 
				//run any activity concurrently the current one
				data = checkApplicantExecutor(data, curHis);
				data = validServ.submitAddActivityData(curHis, user, data);
				if(data.isValid()) {
					data = submitAddActivity(curHis, user, data);
				}
				return data;
			case 3:
				//Cancel working
				data = validServ.actionCancel(curHis, data);
				data = validServ.actionCancelData(curHis, data);
				if(data.isValid()) {
					//data = actionCancel(curHis, data); canceled
					appServ.closeActivity(curHis, true);
				}
				return data;
			case 4:
				data = submitSendApprove(data, user, curHis); //validation is inside
				return data;
			case 5:
				//returnToApplicant
				data = validServ.submitReject(curHis, user, data);
				data = validServ.submitRejectData(curHis, user, data);
				if(data.isValid()) {
					data = submitReject(curHis, user, data);
				}
				return data;
			case 6:
				//reassign
				data = validServ.actionReassign(curHis, data);
				data = validServ.submitReAssign(curHis, user, data);
				if(data.isValid()) {
					data = submitReAssign(curHis, user, data);
				}
				return data;
			case 9:
				data = validServ.submitRevokePermit(curHis, user, data);
				if(data.isValid()) {
					data = submitRevokePermit(curHis, user, data);
				}
				return data;
			case 10:
				//decline
				data = validServ.submitDeclineData(curHis, user, data);
				if (data.isValid()) {
					if (systemServ.isShutdown(curHis)|| systemServ.isAmend(curHis)|| 
							systemServ.isDeregistration(curHis) || systemServ.isGuestInspection(curHis)){
						cancelActivities(curHis);
						sendEmailAttention(user, curHis, data);
					}else if(systemServ.isGuest(curHis)){
						data = submitDeclineGuest(curHis, user, data);
					}else if(systemServ.isHost(curHis)) {
						cancelActivities(curHis);
						Scheduler sch = new Scheduler();
						Concept dictConc= curHis.getApplDict();
						sch.setProcessUrl(literalServ.readValue("applicationurl", dictConc));
						sch.setScheduled(Date.valueOf(LocalDate.now()));
						data = appServ.createHostApplication(curHis, sch, dictConc, data);
					}
				}
				return data;
			case 11:
				data=validServ.submitRunHost(curHis, data);
				if(data.isValid()) {
					data=submitRunHostProcesses(curHis,data);
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
			appServ.closeActivity(curHis, false);
			data = submitAddActivity(curHis, user, data);
			data = runConcurrentProcess(curHis, user, data);
		}
		return data;
	}

	/**
	 * Run a concurrent process if next activity contains the definition of one
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ActivitySubmitDTO runConcurrentProcess(History curHis, UserDetailsDTO user, ActivitySubmitDTO data) throws ObjectNotFoundException {
		long nextActConfId = data.nextActivity();
		if(nextActConfId>0) {
			Concept nextActivity = closureServ.loadConceptById(nextActConfId);
			String processUrl = literalServ.readValue(AssemblyService.CONCURRENTURL, nextActivity);
			if(!processUrl.isEmpty()) {
				Concept dictItem = new Concept();
				try {
					dictItem=systemServ.applDictItemByUrl(SystemService.DICTIONARY_HOST_APPLICATIONS,processUrl);
				} catch (ObjectNotFoundException e) {
					dictItem=systemServ.applDictItemByUrl(SystemService.DICTIONARY_SHUTDOWN_APPLICATIONS,processUrl);
				}
				if(dictItem.getID()>0) {
					if(systemServ.isUniqueProcess(dictItem, curHis.getApplicationData())) {
						Scheduler sch = new Scheduler();
						sch.setProcessUrl(processUrl);
						sch.setScheduled(Date.valueOf(LocalDate.now()));
						data = appServ.createHostApplication(curHis, sch, dictItem, data);
					}
				}
			}
		}
		return data;
	}

	/**
	 * Run hosts processes defined in the choice 11 (run)
	 * @param curHis 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ActivitySubmitDTO submitRunHostProcesses(History curHis, ActivitySubmitDTO data) throws ObjectNotFoundException {
		for(TableRow row :data.getRunHosts().getRows()) {
			if(row.getSelected()) {
				Scheduler sch = new Scheduler();
				Concept dictItem=closureServ.loadConceptById(row.getDbID());
				String processUrl=literalServ.readValue("applicationurl", dictItem);
				sch.setProcessUrl(processUrl);
				sch.setScheduled(Date.valueOf(LocalDate.now()));
				data = appServ.createHostApplication(curHis, sch, dictItem, data);
			}
		}
		return data;
	}


	private void sendEmailAttention(UserDetailsDTO user, History curHis, ActivitySubmitDTO data)
			throws ObjectNotFoundException {
		// 23.10.2022
		boolean attentionActivity = validServ.isActivityAttention(curHis.getActConfig());
		if (attentionActivity) {
			String applicantEmail = accServ.applicantEmailByApplication(curHis.getApplication());
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
			String notes = data.getNotes().getValue();
			if(notes != null || !notes.isEmpty()) {
				notes += " (" + res + ")";
			}else notes = res;
			data.getNotes().setValue(notes);
			data.getNotes().setMark(true);
			data.getNotes().setReadOnly(true);
			data.getNotes().setTextArea(true);
		}
	}
	/**
	 * 10.11.2022 khomenska
	 *  Approve action
	 * @throws JsonProcessingException 
	 */
	private ActivitySubmitDTO submitSendApprove(ActivitySubmitDTO data, UserDetailsDTO user, History curHis) throws ObjectNotFoundException, JsonProcessingException{
		Concept applRoot = closureServ.loadParents(curHis.getApplication()).get(0);
		String applUrl = applRoot.getIdentifier();
		Concept configRoot = closureServ.loadRoot("configuration." + applUrl);
		List<Concept> nextActs = boilerServ.loadActivities(configRoot);
		data = validServ.submitApprove(curHis, user, data, nextActs);
		if(data.isValid()) {
			data = submitApprove(curHis, user, data);
			sendEmailAttention(user, curHis, data);
		}
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
		if (data.isValid() && curHis.getActConfig() != null) { // wrong, monitoring and trace can`t be reassigned
			// determine activity configuration
			Concept actConfig = curHis.getActConfig();
			if (actConfig == null) {
				actConfig = curHis.getApplConfig();
			}
			List<Long> executors = data.executors();

			// create activities, the first will contain the reference to the data of the reassigned one
			boolean moveData=true;
			for (Long execId : executors) {
				Concept userConc = closureServ.loadConceptById(execId);
				History newHis=appServ.activityCreate(null, actConfig, curHis, userConc.getIdentifier(), data.getNotes().getValue());
				if(newHis !=null) {		//not reassign to the same executor
					//the first activity will inherit the data if one
					if(moveData && curHis.getActivityData()!=null) {
						newHis.setActivityData(curHis.getActivityData());
						moveData=false;
					}
					//previous notes may be added too
					if(curHis.getPrevNotes()!=null) {
						String prevNotes=curHis.getPrevNotes();
						if(newHis.getPrevNotes()!=null) {
							prevNotes=prevNotes+"/ "+newHis.getPrevNotes();
						}
						newHis.setPrevNotes(prevNotes);
					}
					newHis= boilerServ.saveHistory(newHis);
					//closeActivity(curHis, true); 24032023 ika
					appServ.stopOneActivity(curHis, true);
				}
			}
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
		//sendEmailAttention(user, curHis, data);
		return data;
	}
	@Transactional
	private ActivitySubmitDTO submitApproveRevoke(History curHis, UserDetailsDTO user, ActivitySubmitDTO data)
			throws ObjectNotFoundException, JsonProcessingException {
		List<History> ret=boilerServ.historyAllOrderByCome(curHis.getApplicationData());
		if(!ret.isEmpty()) {
			if(systemServ.isGuest(ret.get(0))) {
				closeAllHistory(curHis);
				ThingDTO tdto = new ThingDTO();
				tdto.setNodeId(ret.get(0).getApplicationData().getID());
				Concept applicant = closureServ.getParent(ret.get(0).getApplication());
				Concept application = closureServ.getParent(applicant);
				tdto.setApplicationUrl(application.getIdentifier());
				tdto.setApplDictNodeId(ret.get(0).getApplDict().getID());
				UserDetailsDTO userApp = new UserDetailsDTO();
				userApp.setEmail(applicant.getIdentifier());
				tdto = thingServ.loadThing(tdto, userApp);
				tdto = thingServ.createApplication(tdto, applicant.getIdentifier(), curHis.getApplicationData());
				addNotesHistoryData(data, tdto);
			}else {
				throw new ObjectNotFoundException("First history is not guestApplication for appData ID= "+curHis.getApplicationData(),logger);
			}
		}else {
			throw new ObjectNotFoundException("Histories is empty for appData ID= "+curHis.getApplicationData(),logger);
		}
		return data;
	}

	@Transactional
	private ActivitySubmitDTO submitDeclineGuest(History curHis, UserDetailsDTO user, ActivitySubmitDTO data)
			throws ObjectNotFoundException, JsonProcessingException {
		List<History> ret=boilerServ.historyAllOrderByCome(curHis.getApplicationData());
		if(!ret.isEmpty()) {
			History firstRet=ret.get(0);
			if(systemServ.isGuest(firstRet)) {
				cancelActivities(curHis);
				ThingDTO tdto = new ThingDTO();
				tdto.setNodeId(firstRet.getApplicationData().getID());
				Concept applicant = closureServ.getParent(firstRet.getApplication());
				Concept application = closureServ.getParent(applicant);
				tdto.setApplicationUrl(application.getIdentifier());
				tdto.setApplDictNodeId(ret.get(0).getApplDict().getID());
				UserDetailsDTO userApp = new UserDetailsDTO();
				userApp.setEmail(applicant.getIdentifier());
				tdto = thingServ.loadThing(tdto, userApp);
				tdto = thingServ.createApplication(tdto, applicant.getIdentifier(), curHis.getApplicationData());
				addNotesHistoryData(data, tdto);
				sendEmailAttention(user, curHis, data);
			}else {
				throw new ObjectNotFoundException("First history is not guestApplication for appData ID= "+curHis.getApplicationData(),logger);
			}
		}else {
			throw new ObjectNotFoundException("Histories is empty for appData ID= "+curHis.getApplicationData(),logger);
		}
		return data;
	}

	/**
	 * Submit to approve
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 * @throws JsonProcessingException 
	 */
	@Transactional
	public ActivitySubmitDTO submitApprove(History curHis, UserDetailsDTO user, ActivitySubmitDTO data)
			throws ObjectNotFoundException, JsonProcessingException {
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
			data=submitDeregistration(curHis, user, data);
			return data;
		}
		if(systemServ.isShutdown(curHis)) {
			data.clearErrors();
			data=submitApproveRevoke(curHis, user, data);
			return data;
		}
		if(systemServ.isGuestInspection(curHis)) {
			data = submitGuest(curHis, data);
			return data;
		}
		data.setIdentifier(messages.get("invalidstage"));
		data.setValid(false);
		return data;
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
			appServ.closeActivity(curHis, false);
			cancelUsersActivities(user, curHis);
			closeAllHistory(curHis);
			List<Long> executors = data.executors();
			long actConfId = data.nextActivity();
			if (actConfId > 0) {
				Concept actConf = closureServ.loadConceptById(actConfId);
				if (executors.size() > 0) {
					for (Long execId : executors) {
						Concept userConc = closureServ.loadConceptById(execId);
						appServ.activityCreate(null, actConf, curHis, userConc.getIdentifier(), data.getNotes().getValue());
					}
				}
			}
		}
		return data;
	}
	/**
	 * RevokePermit an application and move it to the NMRA users
	 * dictionary.shutdown.applications
	 * @param curHis
	 * @param user
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 * @throws JsonProcessingException
	 */
	@Transactional
	private ActivitySubmitDTO submitRevokePermit(History curHis, UserDetailsDTO user, ActivitySubmitDTO data)
			throws ObjectNotFoundException, JsonProcessingException {
		Concept nodeApplData = amendmentServ.initialApplicationData(curHis.getApplicationData());
		Thing thing = new Thing();
		thing = boilerServ.thingByNode(nodeApplData, thing);
		String processUrl = thing.getUrl();
		Concept dictConc = systemServ.revokepermitDictNode(processUrl);
		if(dictConc!=null) {
			processUrl = literalServ.readValue(LiteralService.APPLICATION_URL, dictConc);
			data = appServ.createRevokePermitApplication(curHis, processUrl, dictConc, data, user);
			return data;
		} else {
			throw new ObjectNotFoundException("submitRevokePermit. revokepermitDictNode("+processUrl+") is NULL", logger);
		}
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
		//closeAllHistory(curHis);
		cancelActivities(curHis);
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
		addNotesHistoryData(data, tdto);
	}
	private void addNotesHistoryData(ActivitySubmitDTO data, ThingDTO tdto) throws ObjectNotFoundException {
		// add notes to the history data
		History his = boilerServ.historyById(tdto.getHistoryId());
		his.setPrevNotes(data.getNotes().getValue());
		his = boilerServ.saveHistory(his);
	}

	private void closeAllHistory(History curHis) throws ObjectNotFoundException {
		// cancel all histories, close the current
		//List<History> allHist = boilerServ.historyAll(curHis.getApplicationData());
		Concept applData=closureServ.loadConceptById(curHis.getApplicationData().getID());
		Concept permitData = amendmentServ.initialApplicationData(applData);
		jdbcRepo.has_activities(null, null, permitData.getID());
		Headers headers = new Headers();
		headers.getHeaders().add(TableHeader.instanceOf("historyID", TableHeader.COLUMN_LONG));
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from has_activities", "","", headers);
		if(!rows.isEmpty()) {
			List<History> allHist= new ArrayList<History>();
			for(TableRow r:rows) {
				Long idHist=(Long)r.getCellByKey("historyID").getOriginalValue();
				History h=boilerServ.historyById(idHist);
				allHist.add(h);
			}
			for (History h : allHist) {
				if (h.getGo() == null) {
					appServ.closeActivity(h, h.getID() != curHis.getID());
				}
			}
		}
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
	private ActivitySubmitDTO submitGuest(History curHis, ActivitySubmitDTO data) throws ObjectNotFoundException {
		if (data.isValid()) {
			cancelActivities(curHis);
			data = runScheduledGuestHost(curHis, data);
		}
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
						appServ.closeActivity(his, true);
					}
				}
			}
		}
	}
	/**
	 * Cancel all activities related to application data
	 * 
	 * @param applicationData - application data
	 * @param all             - remove all activities
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	private void cancelDataActivities(Concept applicationData, boolean all) throws ObjectNotFoundException {
		List<History> hisList = boilerServ.historyAll(applicationData);
		for (History his : hisList) {
			if (all) {
				appServ.closeActivity(his, true);
			} else {
				if (his.getActConfig() != null) {
					appServ.closeActivity(his, true);
				}
			}
		}
	}
	/**
	 * Cancel all opened activities for the current application, but close the
	 * current
	 * 
	 * @param curHis
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public void cancelActivities(History curHis) throws ObjectNotFoundException {
		List<History> allHis = boilerServ.historyAllByApplication(curHis.getApplication());
		// close activities in the current
		for (History his : allHis) {
			if (his.getID() != curHis.getID() && his.getGo() == null) {
				appServ.closeActivity(his, true);
			}
		}
		appServ.closeActivity(curHis, false);
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
			Concept dictConc = systemServ.applDictItemByUrl(SystemService.DICTIONARY_HOST_APPLICATIONS,sch.getProcessUrl());
			// close previous scheduled
			Concept appldata = amendmentServ.initialApplicationData(curHis.getApplicationData());
			List<History> prevActivities = boilerServ.activities(sch.getProcessUrl(), appldata);
			for (History act : prevActivities) {
				appServ.closeActivity(act, true);
			}
			data = appServ.createHostApplication(curHis, sch, dictConc, data);
			if (!data.isValid()) {
				return data;
			}
		}
		return data;
	}
}
