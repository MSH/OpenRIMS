package org.msh.pharmadex2.service.r2;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.IOUtils;
import org.hibernate.Hibernate;
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
import org.msh.pdex2.model.r2.Thing;
import org.msh.pdex2.model.r2.ThingDict;
import org.msh.pdex2.model.r2.User;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.common.UserRepo;
import org.msh.pdex2.repository.r2.ThingRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.ActivityDTO;
import org.msh.pharmadex2.dto.ActivitySubmitDTO;
import org.msh.pharmadex2.dto.AddressDTO;
import org.msh.pharmadex2.dto.AsyncInformDTO;
import org.msh.pharmadex2.dto.CheckListDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.FileDTO;
import org.msh.pharmadex2.dto.IntervalDTO;
import org.msh.pharmadex2.dto.LocationDTO;
import org.msh.pharmadex2.dto.PersonDTO;
import org.msh.pharmadex2.dto.QuestionDTO;
import org.msh.pharmadex2.dto.RegisterDTO;
import org.msh.pharmadex2.dto.RunTestProcessDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.auth.UserRoleDto;
import org.msh.pharmadex2.dto.enums.AssistantEnum;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.DtoService;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;



/**
 * Create test process until some stage
 */
@Service
public class TestProcessService {
	private static final String RUN_MESSAGES = "messages";
	private static final String STAGES = "stages";
	public static final String PROCESS_TEST_TOTAL = "PROCESS_TEST_TOTAL";
	public static final String PROCESS_TEST_COMPLETED = "PROCESS_TEST_COMPLETED";
	public static final String PROCESS_TEST_STAGE = "PROCESS_TEST_STAGE";
	private static final Logger logger = LoggerFactory.getLogger(TestProcessService.class);
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private Messages messages;
	@Autowired
	private ValidationService validator;
	@Autowired
	private ThingService thingServ;
	@Autowired
	private AssemblyService assemblyServ;
	@Autowired
	private RegisterService registerServ;
	@Autowired
	private DictService dictServ;
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private DtoService dtoServ;
	@Autowired
	private ApplicationService applServ;
	@Autowired
	private SubmitService submitServ;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private ThingRepo thingRepo;
	@Autowired
	private UserRepo userRepo;

	@Transactional
	public RunTestProcessDTO load(RunTestProcessDTO data) {
		data.setApplication_info(data.getNode().getLiterals().get("prefLabel"));
		data.getApplication_info().setAssistant(AssistantEnum.NO);
		data.setApplicationurl(data.getNode().getLiterals().get("applicationurl"));
		data.getApplicationurl().setAssistant(AssistantEnum.NO);
		data.setDataurl(data.getNode().getLiterals().get("dataurl"));
		data.getDataurl().setAssistant(AssistantEnum.NO);
		data.getCurrent_applications().setValue(calcApplications(data.getNode().getNodeId()));
		if(!data.getStages().isLoaded()) {
			data=stages(data);
		}
		return data;
	}

	/**
	 * Create or return the table of stages
	 * @param data
	 * @return
	 */
	public RunTestProcessDTO stages(RunTestProcessDTO data) {
		data.setStages(new TableQtb());
		data=stagesHeaders(data);
		data=addStage(1l, messages.get("ApplicantState.NEW_APPLICATION"), data);
		data=addStage(2l, messages.get("submitforapproval"),data);
		data=addStage(3l, messages.get("APPROVE"),data);
		data=addStage(4l, messages.get("activity_decline"),data);
		data=addStage(5l, messages.get("finalize"),data);
		data=addStage(6l, messages.get("ProdAppType.RENEW"),data);
		return data;
	}

	/**
	 * Add a stage to the stages table
	 * @param id
	 * @param value
	 * @param data
	 * @return
	 */
	private RunTestProcessDTO addStage(long id, String value, RunTestProcessDTO data) {
		TableRow row = TableRow.instanceOf(id);
		row.getRow().add(TableCell.instanceOf(STAGES, value));
		row.getRow().add(TableCell.instanceOf(RUN_MESSAGES, ""));
		data.getStages().getRows().add(row);
		return data;
	}



	/**
	 * Create headers for the stages table
	 * @param data
	 * @return
	 */
	private RunTestProcessDTO stagesHeaders(RunTestProcessDTO data) {
		data.getStages().getHeaders().getHeaders().clear();
		data.getStages().getHeaders().getHeaders().add(TableHeader.instanceOf(
				STAGES,
				messages.get(STAGES),
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		data.getStages().getHeaders().getHeaders().add(TableHeader.instanceOf(
				RUN_MESSAGES,
				messages.get(RUN_MESSAGES),
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0));
		return data;
	}

	/**
	 * Total quantity of applications 
	 * @param dictNodeID
	 * @return
	 */
	private Long calcApplications(long dictNodeID) {
		Long ret=0l;
		String select="select * from (\r\n"
				+ "SELECT applDictID as 'dictNodeID', count( distinct h.applDataID) as 'quantity'\r\n"
				+ "FROM `history` h\r\n"
				+ "where h.activityDataID= h.applDataID and h.actConfigID is null\r\n"
				+ "group by applDictID\r\n"
				+ ") t";
		String where="dictNodeID='"+dictNodeID+"'";
		Headers headers = new Headers();
		headers.getHeaders().add(TableHeader.instanceOf("quantity", TableHeader.COLUMN_LONG));
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, headers);
		if(rows.size()==1) {
			ret=(long) rows.get(0).getRow().get(0).getIntValue();
		}
		return ret;
	}

	/**
	 * Validate test process parameters
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public RunTestProcessDTO validate(RunTestProcessDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		data=validateInputFields(data);
		data=validateStages(data);
		return data;
	}


	/**
	 * prefLabel, repeat, year, and duration of the NRA review
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private RunTestProcessDTO validateInputFields(RunTestProcessDTO data) throws ObjectNotFoundException {
		data.getPrefLabel().clearValidation();
		String prefLabel=data.getPrefLabel().getValue();
		if(prefLabel==null || prefLabel.length()<3 || prefLabel.length()>120) {
			validator.suggest(data.getPrefLabel(), 3, 120, true);
		}
		data=validateApplicantEmail(data);
		data.getRepeat().clearValidation();
		Long repeat=data.getRepeat().getValue();
		if(repeat<1 || repeat>20) {
			String errorMess = messages.get("valuerangeerror") +" "+ 1+", " + 20;
			validator.suggest(data.getRepeat(), errorMess, true);
		}
		data.getYear().clearValidation();
		int thisYear=LocalDate.now().getYear();
		Long year=data.getYear().getValue();
		if(year<thisYear-2 || year>thisYear) {
			String errorMess = messages.get("valuerangeerror") +" "+ (thisYear-2)+", " + thisYear;
			validator.suggest(data.getYear(), errorMess, true);
		}
		data.getDaysonreview().clearValidation();
		Long reviewDuration=data.getDaysonreview().getValue();
		if(reviewDuration<1 || reviewDuration>60) {
			String errorMess = messages.get("valuerangeerror") +" "+ 1+", " + 60;
			validator.suggest(data.getDaysonreview(), errorMess, true);
		}
		data.propagateValidation();
		return data;
	}

	/**
	 * VAlidate appicant's email
	 * @param data
	 * @return
	 */
	private RunTestProcessDTO validateApplicantEmail(RunTestProcessDTO data) {
		data.getApplicant_email().clearValidation();
		String email=data.getApplicant_email().getValue();
		if(validator.eMail(email)) {
			String select="SELECT distinct u.email as 'email'\r\n"
					+ "FROM `user` u\r\n"
					+ "join `userdict` ud on ud.useruserId=u.userId";
			String where="email like '" + email+"'";
			List<TableRow> rows=jdbcRepo.qtbGroupReport(select, "", where, new Headers());
			if(!rows.isEmpty()) {
				data.getApplicant_email().invalidate(messages.get("email_exists"));
			}
		}else {
			data.getApplicant_email().invalidate(messages.get("invalid_email"));
		}
		return data;
	}

	/**
	 * Create applications and run stages
	 * @param currentUser 
	 * @param data
	 */
	@Transactional
	public void run(UserDetailsDTO currentUser, RunTestProcessDTO data) {
		cleanTestAsyncContext(data);
		AsyncService.writeAsyncContext(PROCESS_TEST_TOTAL, data.getRepeat().getValue()+"");
		AsyncService.writeAsyncContext(PROCESS_TEST_COMPLETED, "0");
		UserDetailsDTO applicant= createApplicant(data.getApplicant_email().getValue());

		for(long i=0;i<data.getRepeat().getValue();i++) {
			ThingDTO applDTO = new ThingDTO();
			try {
				applDTO.setApplDictNodeId(data.getNode().getNodeId());
				applDTO=thingServ.thingDataFromApplDictNode(applDTO);
				applDTO=thingServ.pathRebuild(applicant, applDTO);
				//
				applDTO=runStages(applicant, currentUser, data, applDTO);		//run a set of the selected stages
				//
				if(!applDTO.isValid()) {
					logger.error(applDTO.getIdentifier());
					AsyncService.writeAsyncContext(AsyncService.PROGRESS_STOP_ERROR, applDTO.getIdentifier());
					return;
				}
			} catch (ObjectNotFoundException | IOException e) {
				logger.error(e.getMessage());
				AsyncService.writeAsyncContext(AsyncService.PROGRESS_STOP_ERROR, e.getMessage());
			}
		}
		AsyncService.writeAsyncContext(PROCESS_TEST_COMPLETED, data.getRepeat().getValue()+"");
	}
	/**
	 * Exactly one stage should be selected
	 * @param data 
	 * @return
	 */
	private RunTestProcessDTO validateStages(RunTestProcessDTO data) {
		data.setStagesError("");
		List<TableRow> selected = data.getStages().getSelectedRows();
		if(selected.size()!=1) {
			data.addError(messages.get("error_selection_stages"));
			data.setStagesError(messages.get("error_selection_stages"));
		}
		return data;
	}

	/**
	 * Run application's stages asked by a user
	 * @param applicant 
	 * @param applicant 
	 * @param currentUser 
	 * @param data 
	 * @param appl
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws IOException 
	 */
	@Transactional
	public ThingDTO runStages(UserDetailsDTO applicant, UserDetailsDTO currentUser, RunTestProcessDTO data, ThingDTO applDTO) 
			throws ObjectNotFoundException, IOException {
		ThingDTO mainPageDto=applDTO.getPath().get(0);
		List<TableRow> rows = selectedStages(data.getStages());
		for(TableRow row : rows) {
			switch(stageIndex(row)) {
			case "01":
				applDTO=createApplicationForm(stageIndex(row),applicant,data, applDTO);
				break;
			case "02":
				mainPageDto=submitApplication(stageIndex(row),applicant,data,mainPageDto);
				break;
			case "03":
				data=reviewApplication(stageIndex(row),currentUser,data,mainPageDto, SystemService.FINAL_ACCEPT);
				break;
			case "04":
				data=reviewApplication(stageIndex(row),currentUser,data,mainPageDto, SystemService.FINAL_DECLINE);
				break;
			default:
				data.addError(messages.get("invalidstage ")+ stageIndex(row));
				data.setStagesError(messages.get("invalidstage ")+ stageIndex(row));
			}
		}
		return applDTO;
	}

	/**
	 * Create list of stages to execute until selected one inclusive
	 * stages 3 and 4 are in conflict. The stage 3 is preferable
	 * it is presumed that exactly one stage is selected
	 * @param stages
	 * @return
	 */
	public List<TableRow> selectedStages(TableQtb stages) {
		List<TableRow> ret = new ArrayList<TableRow>();
		//determine stages
		long selected=stages.getSelectedRows().get(0).getDbID();
		List<Long> rowIds=new ArrayList<Long>();
		for(long l=1l;l<=selected;l++) {
			rowIds.add(l);
		}
		if(rowIds.contains(3l) && rowIds.contains(4l)) {
			if(selected==4l) {
				rowIds.remove(3l);	//decline has been selected implicity
			}else {
				rowIds.remove(4l);
			}
		}
		//select stages
		for(TableRow row : stages.getRows()) {
			if(rowIds.contains(row.getDbID())) {
				ret.add(row);
			}
		}
		return ret;
	}

	/**
	 * Pass an application until approval
	 * @param stageIndex
	 * @param currentUser
	 * @param data
	 * @param mainApplPage
	 * @param acceptOrDecline 
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws IOException 
	 */
	@Transactional
	public RunTestProcessDTO reviewApplication(String stageIndex, UserDetailsDTO currentUser, RunTestProcessDTO data,
			ThingDTO mainApplPage, String acceptOrDecline) throws ObjectNotFoundException, IOException {
		History firstActivity=firstActivity(mainApplPage, currentUser);
		List<Long> pathToApprove=pathToFinalActivity(firstActivity, acceptOrDecline);
		data=completeWorkflow(firstActivity, currentUser, pathToApprove, data);
		if(data.isValid()) {
			AsyncService.writeAsyncContext(PROCESS_TEST_STAGE+stageIndex(data.getStages().getRows().get(2)),
					messages.get("success"));
		}else {
			AsyncService.writeAsyncContext(PROCESS_TEST_STAGE+stageIndex(data.getStages().getRows().get(2)),
					messages.get("error"));
		}
		return data;
	}
	/**
	 * complete a workflow until from the first activity to the final activity
	 * Do not run the final activity
	 * @param firstActivity
	 * @param currentUser 
	 * @param pathToApprove
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws IOException 
	 */
	private RunTestProcessDTO completeWorkflow(History firstActivity, UserDetailsDTO currentUser, List<Long> pathToApprove,
			RunTestProcessDTO data) throws ObjectNotFoundException, IOException {
		History activity=completeActivity(currentUser, firstActivity);
		for(Long activityConfigId : pathToApprove) {
			activity =nextActivity(activity,currentUser, activityConfigId);
			activity=completeActivity(currentUser, activity);
		}
		return data;
	}
	/**
	 * Filling activity data and checklist 
	 * @param currentUser
	 * @param firstActivity
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws IOException 
	 */
	@Transactional
	public History completeActivity(UserDetailsDTO currentUser, History activity) throws ObjectNotFoundException, IOException {
		History ret = new History();
		ActivityDTO aDto = new ActivityDTO();
		aDto.setHistoryId(activity.getID());
		aDto=applServ.activityLoad(aDto, currentUser);
		ThingDTO activityData=aDto.getData().getLast();
		activityData=createActivityData(currentUser,activityData);
		activityData.setHistoryId(activity.getID());
		checklist(currentUser, activityData);
		ret = currentHistory(activity);
		return ret;
	}
	/**
	 * Create and save activity data
	 * @param activityData 
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws IOException 
	 */
	private ThingDTO createActivityData(UserDetailsDTO user, ThingDTO activityData) throws ObjectNotFoundException, IOException {
		String activityConfigUrl = activityData.getUrl();
		if(!activityConfigUrl.isEmpty()) {
			List<Assembly> pageAssms=assemblyServ.loadDataConfiguration(activityConfigUrl, user);
			activityData=thingServ.createContent(activityData, user);
			activityData=resolvePrefLabel(RandomStringUtils.randomAlphanumeric(10), activityData);
			activityData=resolveComponents(user,pageAssms,activityData);
			activityData.setStrict(false);
			activityData=thingServ.saveUnderOwner(activityData, user);
		}
		return activityData;
	}

	/**
	 * Complete an activity, start next activity
	 * @param thisActivity
	 * @param currentUser 
	 * @param next activity configuration ID
	 * @return a history record opened for the next activity, ID==0, if none
	 * @throws ObjectNotFoundException 
	 * @throws JsonProcessingException 
	 */
	@Transactional
	public History nextActivity(History thisActivity, UserDetailsDTO currentUser, Long nextActivityCfgId) throws JsonProcessingException, ObjectNotFoundException {
		History ret = new History();
		ActivitySubmitDTO submitDto = new ActivitySubmitDTO();
		submitDto.setHistoryId(thisActivity.getID());
		submitDto=assignContinueAction(submitDto);		//Continue action is 0
		submitDto.setApplicant(false);
		submitDto=assignExecutor(currentUser, submitDto);
		submitDto=assignNextActivity(nextActivityCfgId, submitDto);
		submitDto=submitServ.submitSend(currentUser, submitDto);
		ret = currentHistory(thisActivity);
		return ret;
	}
	/**
	 * Load a current history record for the activity
	 * @param activity
	 * @return
	 */
	private History currentHistory(History activity) {
		History ret;
		List<History> hisList = boilerServ.historyOpenedByApplData(activity.getApplicationData());
		ret=hisList.getLast();
		return ret;
	}

	/**
	 * Assign the next activity using nextActivityCfg
	 * @param nextActivityCfgId
	 * @param submitDto
	 * @return modified submitDto
	 */
	private ActivitySubmitDTO assignNextActivity(Long nextActivityCfgId, ActivitySubmitDTO submitDto) {
		submitDto.getNextJob().getRows().add(TableRow.instanceOf(nextActivityCfgId));
		submitDto.getNextJob().getRows().get(0).setSelected(true);
		return submitDto;
	}

	/**
	 * Assign the current user as an executor of the next activity
	 * @param currentUser
	 * @param submitDto
	 * @return modified submitDto
	 * @throws ObjectNotFoundException 
	 */
	private ActivitySubmitDTO assignExecutor(UserDetailsDTO currentUser, ActivitySubmitDTO submitDto) throws ObjectNotFoundException {
		Optional<User> usero=userRepo.findByEmail(currentUser.getEmail());
		if(usero.isPresent()) {
			submitDto.getExecs().getRows().add(TableRow.instanceOf(usero.get().getConcept().getID()));
			submitDto.getExecs().getRows().get(0).setSelected(true);
		}else {
			throw new ObjectNotFoundException("assignExecutor User not found "+currentUser.getEmail(),logger);
		}
		return submitDto;
	}

	/**
	 * Assign "continue" action as a next action
	 * @param submitDto
	 * @return modifier submitDto
	 */
	private ActivitySubmitDTO assignContinueAction(ActivitySubmitDTO submitDto) {
		submitDto.getActions().getRows().add(TableRow.instanceOf(0l));
		submitDto.getActions().getRows().get(0).setSelected(true);
		return submitDto;
	}

	/**
	 * Return list of Activity Configuration IDs from the first activity to the Approve activity, e.g.
	 * all NO and the final one
	 * @param firstActivity
	 * @param finalActivity Approve or Decline
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<Long> pathToFinalActivity(History firstActivity, String finalActivity) throws ObjectNotFoundException {
		List<Long> ret = new ArrayList<Long>();
		List<Concept> followingActivities= new ArrayList<Concept>();
		followingActivities=followingActivities(firstActivity.getActConfig(), followingActivities);
		//<both COMPANY and ACCEPT are ACCEPT
		//the DECLINE is only one>
		List<String> finalOutcomes = new ArrayList<String>();
		finalOutcomes.add(finalActivity);
		if(finalActivity.equals(SystemService.FINAL_ACCEPT)) {
			finalOutcomes.add(SystemService.FINAL_COMPANY);
		}
		//</>
		//<Collect all NO, search for the finalId>
		Long finalId=0l;
		for(Concept activityConfig : followingActivities) {
			String outcome=activityOutCome(activityConfig);
			if(outcome.equals(SystemService.FINAL_NO)) {
				ret.add(activityConfig.getID());
			}
			if(finalOutcomes.contains(outcome)) {
				finalId=activityConfig.getID();
			}
		}
		//</>
		//<Add final to the last if found, otherwise is error>
		if(finalId>0) {
			ret.add(finalId);
		}else {
			throw new ObjectNotFoundException("pathToFinalActivity. Final activity not found "+ finalActivity, logger);
		}
		//</>
		return ret;
	}

	/**
	 * Get following activities recursively
	 * @param firstActivity
	 * @param followingActivities
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private List<Concept> followingActivities(Concept concept, List<Concept> followingActivities) throws ObjectNotFoundException {
		List<Concept> childs=literalServ.loadOnlyActiveChilds(concept);
		if(!childs.isEmpty()) {
			for(Concept child : childs) {
				if(child.getActive()) {
					if(!validator.isActivityBackground(child)) {
						followingActivities.add(child);
					}
				}
			}
			followingActivities=followingActivities(childs.getLast(),followingActivities);
		}
		return followingActivities;
	}

	/**
	 * Get outcome of the activity
	 * @param activityConfig
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public String activityOutCome(Concept activityConfig) throws ObjectNotFoundException {
		String ret=SystemService.FINAL_NO;
		List<Thing> things =thingRepo.findByConcept(activityConfig);
		for(ThingDict adict :things.get(0).getDictionaries()) {
			if(adict.getUrl().equalsIgnoreCase(SystemService.DICTIONARY_SYSTEM_FINALIZATION)) {
				return adict.getConcept().getIdentifier();
			}
		}
		return ret;
	}

	/**
	 * return the first activity that is assigned to the current user
	 * if any activity is assigned, then the first one will be re-assigned 
	 * @param firstActivities
	 * @param currentUser 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private History firstActivity(ThingDTO applData, UserDetailsDTO currentUser) throws ObjectNotFoundException {
		List<History> firstActivities= firstActivities(applData);
		if(!firstActivities.isEmpty()) {
			History myActivity=activityForUser(firstActivities, currentUser);
			if(myActivity==null) {
				//not found, thus reassign it!
				TableQtb executors =submitServ.executorsTable(firstActivities.get(0), 
						firstActivities.get(0).getActConfig(), new TableQtb(), false);
				Long executorId=0l;
				//can I be the executor?
				for(TableRow row : executors.getRows()) {
					String email=row.getCellByKey("email").getValue();
					if(email.equalsIgnoreCase(currentUser.getEmail())) {
						executorId=row.getDbID();
						break;
					}
				}
				if(executorId>0) {
					ActivitySubmitDTO dto = new ActivitySubmitDTO();
					dto.getExecs().getRows().add(TableRow.instanceOf(executorId));
					dto.getExecs().getRows().get(0).setSelected(true);
					dto.setNotes(FormFieldDTO.of(messages.get("Test")));
					dto=submitServ.submitReAssign(firstActivities.get(0), dto);
					firstActivities= firstActivities(applData);
					myActivity=activityForUser(firstActivities, currentUser);
					if(myActivity != null) {
						return myActivity;
					}else {
						throw new ObjectNotFoundException("firstActivity "+messages.get("checkAccessToActivity")+" "+ currentUser.getEmail(),logger);
					}

				}else {
					throw new ObjectNotFoundException("firstActivity "+messages.get("checkAccessToActivity")+" "+ currentUser.getEmail(),logger);
				}
			}else {
				return myActivity;
			}
		}else {
			throw new ObjectNotFoundException("firstActivity "+messages.get("emptyactivitylist"),logger);
		}
	}
	/**
	 * Search for my activity in the list
	 * @param firstActivities
	 * @param currentUser
	 * @throws ObjectNotFoundException 
	 */
	private History activityForUser(List<History> firstActivities, UserDetailsDTO currentUser) throws ObjectNotFoundException {
		for(History his : firstActivities) {
			if(his.getActConfig()!=null) {
				Concept owner=closureServ.getParent(his.getActivity());
				if(owner.getIdentifier().equalsIgnoreCase(currentUser.getEmail())) {
					return his;
				}
			}
		}
		return null;
	}

	/**
	 * Get list of the currently opened activities
	 * @param mainApplPage 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private List<History> firstActivities(ThingDTO mainApplPage) throws ObjectNotFoundException {
		List<History> ret= new ArrayList<History>();
		Concept applData=closureServ.loadConceptById(mainApplPage.getNodeId());
		List<History> hlist=boilerServ.historyOpenedByApplData(applData);
		ret=hlist.stream()
				.filter(h->h.getActConfig()!=null)
				.filter(h->{
					try {
						return !validator.isActivityBackground(h.getActConfig());
					} catch (ObjectNotFoundException e) {
						return false;
					}
				})
				.toList();
		return ret;
	}

	/**
	 * Submit an application to NRA
	 * @param stageIndex
	 * @param applicant
	 * @param data
	 * @param mainApplPage
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ThingDTO submitApplication(String stageIndex, UserDetailsDTO applicant, RunTestProcessDTO data,
			ThingDTO mainApplPage) throws ObjectNotFoundException {
		CheckListDTO checkListDto=checklist(applicant, mainApplPage);
		if(checkListDto.isValid()) {
			checkListDto=submitServ.submit(checkListDto, applicant);
			if(checkListDto.isValid()) {
				AsyncService.writeAsyncContext(PROCESS_TEST_STAGE+stageIndex(data.getStages().getRows().get(1)),
						messages.get("success"));
			}else {
				throw new ObjectNotFoundException(checkListDto.getIdentifier(), logger);
			}
		}else {
			throw new ObjectNotFoundException(checkListDto.getIdentifier(), logger);

		}
		return mainApplPage;
	}


	/**
	 * Create and fill-up submission checklist
	 * @param user
	 * @param applDTO
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public CheckListDTO checklist(UserDetailsDTO user, ThingDTO applDTO) throws ObjectNotFoundException {
		CheckListDTO dto = new CheckListDTO();
		dto.setHistoryId(applDTO.getHistoryId());
		dto=applServ.checklistLoad(dto, user);
		if(dto.isValid()) {
			for(QuestionDTO question : dto.getQuestions()) {
				question.setAnswer(1);	//Yes
			}
			dto=applServ.checklistSave(dto, user);
			if(!dto.isValid()) {
				applDTO.addError(dto.getIdentifier());
			}
		}else {
			applDTO.addError(dto.getIdentifier());
		}
		return dto;
	}

	/**
	 * Fill out test data into application form and, then, save it  
	 * @param string 
	 * @param applicant 
	 * @param data 
	 * @param appl
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws IOException 
	 */
	private ThingDTO createApplicationForm(String string, UserDetailsDTO applicant,  RunTestProcessDTO data, ThingDTO applDTO) 
			throws ObjectNotFoundException, IOException {
		for(ThingDTO thingDto : applDTO.getPath()) {
			thingDto=thingServ.createContent(thingDto, applicant);
		}
		//Save the first page and create an application
		ThingDTO firstPage=applDTO.getPath().get(0);
		firstPage.setStrict(false);
		firstPage.setParentId(0l);
		firstPage = createApplicationPage(applicant, data.getPrefLabel().getValue(), firstPage);
		//store others pages
		Long parentId= firstPage.getNodeId();
		for(ThingDTO page : applDTO.getPath()) {
			if(page.getNodeId()==0) {
				page.setParentId(parentId);
				page=createApplicationPage(applicant, data.getPrefLabel().getValue(), page);
			}
		}

		AsyncService.writeAsyncContext(PROCESS_TEST_STAGE+stageIndex(data.getStages().getRows().get(0)),
				messages.get("success"));
		return applDTO;
	}

	/**
	 * Create and store a page of the application form
	 * @param user
	 * @param prefLabel
	 * @param page
	 * @return
	 * @throws ObjectNotFoundException
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	private ThingDTO createApplicationPage(UserDetailsDTO user, String prefLabel,ThingDTO page)
			throws ObjectNotFoundException, IOException, JsonProcessingException {
		List<Assembly> pageAssms=assemblyServ.loadDataConfiguration(page.getUrl(), user);
		page=resolvePrefLabel(prefLabel, page);
		page=resolveComponents(user,pageAssms,page);
		page.setStrict(false);
		page=thingServ.saveUnderOwner(page, user);
		return page;
	}

	/**
	 * resolve components on the page
	 * @param user 
	 * @param pageAssms 
	 * @param page
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws IOException 
	 */
	private ThingDTO resolveComponents(UserDetailsDTO user, List<Assembly> pageAssms, ThingDTO page) throws ObjectNotFoundException, IOException {
		for(Assembly assm : pageAssms) {
			switch(assm.getClazz()) {
			case "literals":
			case "strings":
				page=resolveLiteral(assm,page);
				break;
			case "numbers":
				page=resolveNumbers(assm,page);
				break;
			case "registers":
				page=resolveRegisters(assm,page);
				break;
			case "addresses":
				page=resolveAddresses(assm,page);
				break;
			case "documents":
				page=resolveDocuments(user,assm,page);
				break;
			case "dictionaries":
				page=resolveDictionaries(assm,page);
				break;
			case "persons":
				page=resolvePersons(user,assm,page);
				break;
			case "dates":
				page=resolveDates(assm,page);
				break;
			case "intervals":
				page=resolveIntervals(assm,page);
				break;
			}
		}
		return page;
	}

	/**
	 * Anyway interval should be built using min and max 
	 * @param assm
	 * @param page
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ThingDTO resolveIntervals(Assembly assm, ThingDTO page) throws ObjectNotFoundException {
		String varName=assm.getPropertyName().getIdentifier();
		IntervalDTO fld = page.getIntervals().get(varName);
		if(fld!=null) {
			fld.clearErrors();
			fld.getTo().setValue(LocalDate.now().plusMonths(assm.getMax()));
			fld.getFrom().setValue(fld.getTo().getValue().minusMonths(assm.getMin()));

		}
		return page;
	}

	/**
	 * Date, maybe in the defined interval, preferred is today's date
	 * @param assm
	 * @param page
	 * @return
	 */
	public ThingDTO resolveDates(Assembly assm, ThingDTO page) {
		String varName=assm.getPropertyName().getIdentifier();
		FormFieldDTO<LocalDate> fld = page.getDates().get(varName);
		if(fld!=null) {
			fld.clearValidation();
			if(assm.getRequired()) {
				long offset=randomNumberBetweenMinAndMax(assm);
				if(offset==0) {
					offset=assm.getMin();
				}
				fld.setValue(LocalDate.now().plusMonths(offset));
			}else {
				fld.setValue(LocalDate.now());
			}
			fld.setValueStr(TableCell.localDateToString(fld.getValue()));
		}
		return page;
	}

	/**
	 * Recursive persons resolver
	 * @param user
	 * @param assm
	 * @param mainPage
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	private ThingDTO resolvePersons(UserDetailsDTO user, Assembly assm, ThingDTO mainPage) throws ObjectNotFoundException, JsonProcessingException, IOException {
		String varName=assm.getPropertyName().getIdentifier();
		PersonDTO fld = mainPage.getPersons().get(varName);
		if(fld!=null) {
			fld.clearErrors();
			fld=thingServ.createPersonDTO(mainPage, dtoServ.assemblyDto(assm), fld);
			//save main and prepare detail records
			mainPage.setStrict(false);
			mainPage=thingServ.saveUnderOwner(mainPage, user);
			mainPage.setAuxPathVar(varName);
			mainPage=thingServ.auxPathPerson(fld, user, mainPage);
			//create and store the first page of a detail form
			for(ThingDTO thingDto : mainPage.getAuxPath()) {
				thingDto=thingServ.createContent(thingDto, user);
			}
			ThingDTO detailPage=mainPage.getAuxPath().get(0);
			detailPage.setStrict(false);
			detailPage=createApplicationPage(user, varName, detailPage);
			//create and store the rest pages
			Long parentId= detailPage.getNodeId();
			for(ThingDTO pageAux : mainPage.getAuxPath()) {
				if(parentId!=0) {
					pageAux.setParentId(parentId);
					pageAux=createApplicationPage(user, varName, pageAux);
				}
			}

		}
		return mainPage;
	}

	/**
	 * resolve multiply and single choices in the dictionaries
	 * @param assm
	 * @param page
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ThingDTO resolveDictionaries(Assembly assm, ThingDTO page) throws ObjectNotFoundException {
		String varName=assm.getPropertyName().getIdentifier();
		DictionaryDTO fld = page.getDictionaries().get(varName);
		if(fld!=null) {
			fld.clearErrors();
			long selected = selectDictionary(assm.getUrl());
			fld.getPrevSelected().clear();
			fld.getPrevSelected().add(selected);
			fld=dictServ.createDictionary(fld);
		}
		return page;
	}

	/**
	 * Prepare documents. Assumed that the all documents should be uploaded
	 * @param user 
	 * @param assm
	 * @param page
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws IOException 
	 */
	private ThingDTO resolveDocuments(UserDetailsDTO user, Assembly assm, ThingDTO page) throws IOException, ObjectNotFoundException {
		String varName=assm.getPropertyName().getIdentifier();
		FileDTO fld = page.getDocuments().get(varName);
		if(fld!=null) {
			fld.clearErrors();
			fld.setDictUrl(assm.getDictUrl());
			fld.setMaxFileSize(100000l);
			fld.setThingUrl(page.getUrl());
			fld.setUrl(assm.getUrl());
			fld.setVarName(varName);
			for(TableRow row : fld.getTable().getRows()) {
				fld.setDictNodeId(row.getDbID());
				fld=uploadFile(user,fld);
			}

		}
		return page;
	}

	/**
	 * Uploads a file to the database, however does not link to the thing
	 * @param user 
	 * @param dta
	 * @return
	 * @throws IOException 
	 * @throws ObjectNotFoundException 
	 */
	private FileDTO uploadFile(UserDetailsDTO user, FileDTO data) throws IOException, ObjectNotFoundException {
		InputStream fileStream = getClass().getResourceAsStream("/static/img/sample.jpg");
		byte[] fileBytes = IOUtils.toByteArray(fileStream);
		data.setFileSize((long) fileBytes.length);
		data.setFileName("sample.jpg");
		data.setMediaType(MediaType.IMAGE_JPEG_VALUE);
		data = thingServ.fileSaveAfterValidation(data, user, fileBytes);
		return data;
	}

	/**
	 * Resolve addresses
	 * @param assm
	 * @param page
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ThingDTO resolveAddresses(Assembly assm, ThingDTO page) throws ObjectNotFoundException {
		String varName=assm.getPropertyName().getIdentifier();
		AddressDTO fld = page.getAddresses().get(varName);
		if(fld!=null) {
			fld.clearErrors();
			Long adminUnitId = selectDictionary(SystemService.DICTIONARY_ADMIN_UNITS);
			//set GIS data - always a center of the admin unit
			Concept node = closureServ.loadConceptById(adminUnitId);
			String markerStr=literalServ.readValue("gisLocation", node);
			if(markerStr!=null) {
				fld.setMarker(LocationDTO.of(markerStr));
			}
			//set the dictionary
			fld.getDictionary().getPrevSelected().clear();
			fld.getDictionary().getPrevSelected().add(adminUnitId);
			fld.setDictionary(dictServ.createDictionary(fld.getDictionary()));
		}
		return page;
	}

	/**
	 * Select selectedCount an item from the final layer of the dictionary
	 * @param dictUrl
	 * @return selected item ID
	 * @throws ObjectNotFoundException empty dictionary, etc.
	 */
	@Transactional
	public Long selectDictionary(String dictUrl) throws ObjectNotFoundException {
		Long ret = 0l;
		DictionaryDTO dictDTO=new DictionaryDTO();
		dictDTO.setUrl(dictUrl);
		dictDTO=dictServ.createDictionary(dictDTO);
		Long dictNodeId=dictDTO.getUrlId();				//starts from the root, e.g. the first level
		while(dictDTO.getTable().getRows().size()>0) {	
			dictNodeId = selectDictLevel(dictDTO.getTable().getRows());
			if(dictNodeId>0) {
				ret=dictNodeId;
				dictDTO.getPrevSelected().add(dictNodeId);
				dictDTO=dictServ.nextDictionary(dictDTO);
			}
		}
		if(ret>0) {
			return ret;
		}else {
			throw new ObjectNotFoundException("resolveDictionary, empty dictionary "+dictUrl, logger);
		}
	}
	/**
	 * select a level in the dictionary
	 * @param rows
	 * @param selectedCount
	 * @return
	 */
	private Long selectDictLevel(List<TableRow> rows) {
		Long ret = 0l;
		Random random = new Random();
		if(rows.size()>1) {
			int j=random.nextInt(rows.size()-1);
			TableRow row=rows.get(j);
			ret =row.getDbID();
		}else {
			if(rows.size()==1) {
				ret=rows.get(0).getDbID();
			}
		}
		return ret;
	}

	/**
	 * resolve registers
	 * @param assm
	 * @param page
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ThingDTO resolveRegisters(Assembly assm, ThingDTO page) throws ObjectNotFoundException {
		String varName=assm.getPropertyName().getIdentifier();
		RegisterDTO fld = page.getRegisters().get(varName);
		if(fld!=null) {
			fld.clearErrors();
			fld=registerServ.askNewNumber(fld);
		}
		return page;
	}

	/**
	 * resolve numbers
	 * @param assm
	 * @param page
	 * @return
	 */
	@Transactional
	public ThingDTO resolveNumbers(Assembly assm, ThingDTO page) {
		String varName=assm.getPropertyName().getIdentifier();
		long generatedNumber=100l;
		FormFieldDTO<Long> fld = page.getNumbers().get(varName);
		if(fld!=null) {
			fld.clearValidation();
			if(assm.getRequired()){
				generatedNumber = randomNumberBetweenMinAndMax(assm);
			}
		}
		fld.setValue(generatedNumber);
		fld.setValueStr(generatedNumber+"");
		return page;
	}

	private long randomNumberBetweenMinAndMax(Assembly assm) {
		long generatedNumber=100;
		if(assm.getRequired()) {
			generatedNumber=assm.getMin();
			if(assm.getMax()>assm.getMin()) {
				Random random = new Random();
				generatedNumber=random.nextInt((int) (assm.getMax() - assm.getMin())) + assm.getMin();
			}
		}
		return generatedNumber;
	}

	/**
	 * resolve a literal or string
	 * @param assm
	 * @param page
	 * @return
	 */
	private ThingDTO resolveLiteral(Assembly assm, ThingDTO page) {
		String varName=assm.getPropertyName().getIdentifier();
		if(!varName.equalsIgnoreCase("prefLabel")) {
			FormFieldDTO<String> fld = page.getLiterals().get(varName);
			if(fld==null) {
				fld=page.getStrings().get(varName);
			}
			if(fld != null) {
				fld.clearValidation();
				int len=10;
				String generatedString = RandomStringUtils.randomAlphanumeric(10);
				if(varName.toLowerCase().contains("email")) {
					generatedString=RandomStringUtils.randomAlphabetic(5)+"@"
							+RandomStringUtils.randomAlphabetic(8)+".com";
				}
				if(varName.toLowerCase().contains("phone")) {
					len=(int) assm.getMax();
					generatedString = RandomStringUtils.randomNumeric(len);
				}
				if(assm.getRequired()) {
					if(generatedString.length()<assm.getMin()|| generatedString.length()>assm.getMax()) {
						if(len<assm.getMin()) {
							len=(int) assm.getMin();
						}
						if(len>assm.getMax()) {
							len=(int) assm.getMax();
						}
						generatedString = RandomStringUtils.randomAlphanumeric(len);
					}
				}
				fld.setValue(generatedString);
			}
		}
		return page;
	}

	/**
	 * Create and set a prefLabel
	 * @param prefLabel
	 * @param pageDTO
	 * @return
	 */
	private ThingDTO resolvePrefLabel(String prefLabel, ThingDTO firstPage) {
		FormFieldDTO<String> prefLabelDTO=firstPage.getLiterals().get("prefLabel");
		if(prefLabelDTO == null) {
			prefLabelDTO=firstPage.getStrings().get("prefLabel");
		}
		if(prefLabelDTO != null) {
			prefLabelDTO.setValue(prefLabel+"-"+LocalDateTime.now().hashCode());
		}
		return firstPage;
	}

	/**
	 * Create a test applicant
	 * @param value
	 * @return
	 */
	public UserDetailsDTO createApplicant(String eMail) {
		UserDetailsDTO ret = new UserDetailsDTO();
		ret.setActive(true);
		ret.setEmail(eMail);
		ret.setLogin(eMail);
		ret.setName(eMail);
		ret.getAllRoles().add(UserRoleDto.guestUser());
		ret.getGranted().add(UserRoleDto.guestUser());
		return ret;
	}

	/**
	 * the index for a stage
	 * @param row
	 * @return
	 */
	private String stageIndex(TableRow row) {
		String ret = "0"+row.getDbID();
		return StringUtils.right(ret, 2);
	}

	/**
	 * Clean all asynchronious data storage
	 * @param data 
	 */
	private void cleanTestAsyncContext(RunTestProcessDTO data) {
		for(TableRow row : data.getStages().getRows()) {
			int rowId=(int)row.getDbID();
			AsyncService.writeAsyncContext(PROCESS_TEST_STAGE+rowId, "");
		}

	}

	/**
	 * The async progress informer
	 * @param data
	 * @return
	 */
	public AsyncInformDTO calcProgress(AsyncInformDTO data) {
		String totalS=AsyncService.readAsyncContext(PROCESS_TEST_TOTAL);
		String completedS=AsyncService.readAsyncContext(PROCESS_TEST_COMPLETED);
		if(totalS.isEmpty() || completedS.isEmpty()) {
			data.setCompleted(false);
			data.setCancelled(true);
			return data;
		}
		Long total=Long.valueOf(totalS);
		Long completed=Long.valueOf(completedS);
		//percent of completion and progress message
		int pers=(int) ((completed/total)*100);
		if(pers==0) {
			pers=(int) ((total*0.1/total)*100);
		}
		data.setComplPercent(pers);
		data.setProgressMessage(completedS+"/"+totalS);

		String stopError = AsyncService.readAsyncContext(AsyncService.PROGRESS_STOP_ERROR);
		if(!stopError.isEmpty()) {
			data.setCompleted(false);
			data.setCancelled(true);
			data.setComplPercent(100);
			data.setProgressMessage(messages.get("error"));
		}else {
			data.setCancelled(false);
			data.setCompleted(total.equals(completed));
		}
		return data;
	}

	/**
	 * Read run results stored by the asynchronous process
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public RunTestProcessDTO readResults(RunTestProcessDTO data) throws ObjectNotFoundException {
		data.clearErrors();
		String stopError = AsyncService.readAsyncContext(AsyncService.PROGRESS_STOP_ERROR);
		for(TableRow row :data.getStages().getRows()) {
			if(row.getSelected()) {
				String runMess=AsyncService.readAsyncContext(PROCESS_TEST_STAGE+stageIndex(row));
				if(!runMess.isEmpty()) {
					row.getCellByKey(RUN_MESSAGES).setValue(runMess);
					if(runMess.equals(stopError)) {
						row.getCellByKey(RUN_MESSAGES).setStyleClass("text-danger");
					}else {
						row.getCellByKey(RUN_MESSAGES).setStyleClass("text-success");
					}
				}
			}
		}

		if(!stopError.isEmpty()) {
			data.addError(stopError);
		}
		return data;
	}


}
