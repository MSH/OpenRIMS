package org.msh.pharmadex2.controller.r2;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pharmadex2.controller.common.ExcelView;
import org.msh.pharmadex2.dto.ActivityDTO;
import org.msh.pharmadex2.dto.ActivityHistoryDataDTO;
import org.msh.pharmadex2.dto.ActivitySubmitDTO;
import org.msh.pharmadex2.dto.ApplicationHistoryDTO;
import org.msh.pharmadex2.dto.ApplicationsDTO;
import org.msh.pharmadex2.dto.AssemblyDTO;
import org.msh.pharmadex2.dto.CheckListDTO;
import org.msh.pharmadex2.dto.DataUnitDTO;
import org.msh.pharmadex2.dto.FileDTO;
import org.msh.pharmadex2.dto.LinksDTO;
import org.msh.pharmadex2.dto.PersonDTO;
import org.msh.pharmadex2.dto.PersonSpecialDTO;
import org.msh.pharmadex2.dto.RegisterDTO;
import org.msh.pharmadex2.dto.ResourceDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.ThingValuesDTO;
import org.msh.pharmadex2.dto.TimeLineWokflowDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.exception.DataNotFoundException;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.msh.pharmadex2.service.r2.AccessControlService;
import org.msh.pharmadex2.service.r2.ApplicationService;
import org.msh.pharmadex2.service.r2.AssemblyService;
import org.msh.pharmadex2.service.r2.IrkaServices;
import org.msh.pharmadex2.service.r2.LinkService;
import org.msh.pharmadex2.service.r2.MonitoringService;
import org.msh.pharmadex2.service.r2.ResourceService;
import org.msh.pharmadex2.service.r2.SubmitService;
import org.msh.pharmadex2.service.r2.SupervisorService;
import org.msh.pharmadex2.service.r2.ThingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Common API controls for NMRA users
 * 
 * @author alexk
 *
 */
@RestController
public class ActivityAPI{
	private static final Logger logger = LoggerFactory.getLogger(ActivityAPI.class);
	@Autowired
	private UserService userServ;
	@Autowired
	private ApplicationService applServ;
	@Autowired
	private MonitoringService monitoringServ;
	@Autowired
	private ThingService thingServ;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ValidationService validServ;
	@Autowired
	SupervisorService superVisServ;
	@Autowired
	ResourceService resourceServ;
	@Autowired
	Messages messages;
	@Autowired
	private IrkaServices irkaServ;
	@Autowired
	private LinkService linkServ;

	@Autowired
	private SubmitService submServ;
	@Autowired
	private AccessControlService accServ;
	@Autowired
	private AssemblyService assmServ;

	/**
	 * Alerts to do
	 * @param auth
	 * @param uri
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/activity/todo/attention")
	public ApplicationsDTO todoAttention(Authentication auth, UriComponentsBuilder uri,@RequestBody ApplicationsDTO data) throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		data = applServ.attentionToDo(data, user);
		return data;
	}

	/**
	 * Present activities to do
	 * @param auth
	 * @param uri
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/activity/todo/actual")
	public ApplicationsDTO todoActual(Authentication auth, UriComponentsBuilder uri,@RequestBody ApplicationsDTO data) throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		data = applServ.activitiesToDo(data, true, user);
		return data;
	}
	/**
	 * Scheduled activities to do
	 * @param auth
	 * @param uri
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/activity/todo/scheduled")
	public ApplicationsDTO todoScheduled(Authentication auth, UriComponentsBuilder uri,@RequestBody ApplicationsDTO data) throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		data = applServ.activitiesToDo(data, false, user);
		return data;
	}

	/**
	 * t - type - "actual", "scheduled"
	 * @param auth
	 * @param data
	 * @param t
	 * @param s
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/my/monitoring/actual", "/api/*/my/monitoring/scheduled"})
	public ApplicationsDTO myMonitoringActual(Authentication auth, UriComponentsBuilder uri,
			@RequestBody ApplicationsDTO data, HttpServletRequest request)
			throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			String reqUrl=request.getRequestURL().toString().toLowerCase();
			String[] reqUrlArray= reqUrl.split("/");
			data = monitoringServ.myMonitoring(data, user, reqUrlArray[reqUrlArray.length-1], "");
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping({ "/api/*/my/monitoring/fullsearch"})
	public ApplicationsDTO monitoringFull(Authentication auth, UriComponentsBuilder uri,
			@RequestBody ApplicationsDTO data)
					throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data = monitoringServ.monitoringFullSearch(data, user, "");
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Get the latest history ID by the application data ID
	 * @param auth
	 * @param applID
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/my/monitoring/convinient/history"})
	public Long myMonitoring(Authentication auth, UriComponentsBuilder uri, @RequestBody Long applID)
			throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		try {
			return monitoringServ.convinientHistory(applID);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	@PostMapping("/api/*/my/monitoring/application")
	public ApplicationsDTO application(Authentication auth, UriComponentsBuilder uri, @RequestBody ApplicationsDTO data) throws DataNotFoundException {
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		accServ.allowAuthenticated(auth, uri);
		try {
			data.setThing(thingServ.path(user,data.getThing()));
			return data;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	@PostMapping("/api/*/my/monitoring/actual/excel")
	public ModelAndView myMonitoringActualExcel(Authentication auth, UriComponentsBuilder uri, 
			@RequestBody ApplicationsDTO data,
			HttpServletResponse response) throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		data.getTable().getHeaders().setPageSize(Integer.MAX_VALUE);
		try {
			data = monitoringServ.myMonitoring(data, user, "actual", "");
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		Map<String, Object> model = new HashMap<String, Object>();
		//Sheet Name
		model.put(ExcelView.SHEETNAME, messages.get("actual"));
		//Title
		model.put(ExcelView.TITLE, messages.get("monitoring"));
		//Headers List
		model.put(ExcelView.HEADERS, data.getTable().getHeaders().getHeaders());
		//Rows
		model.put(ExcelView.ROWS, data.getTable().getRows());
		response.setHeader( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"monitoring_actual.xlsx\"");
		response.setHeader("filename", "monitoring_actual.xlsx");       
		return new ModelAndView(new ExcelView(), model);
	}

	@PostMapping("/api/*/my/monitoring/scheduled/excel")
	public ModelAndView myMonitoringScheduledExcel(Authentication auth, UriComponentsBuilder uri,
			@RequestBody ApplicationsDTO data,
			HttpServletResponse response) throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		data.getScheduled().getHeaders().setPageSize(Integer.MAX_VALUE);
		try {
			data = monitoringServ.myMonitoring(data, user, "scheduled", "");
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		Map<String, Object> model = new HashMap<String, Object>();
		//Sheet Name
		model.put(ExcelView.SHEETNAME, messages.get("scheduled"));
		//Title
		model.put(ExcelView.TITLE, messages.get("monitoring"));
		//Headers List
		model.put(ExcelView.HEADERS, data.getScheduled().getHeaders().getHeaders());
		//Rows
		model.put(ExcelView.ROWS, data.getScheduled().getRows());
		response.setHeader( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"monitoring_scheduled.xlsx\"");
		response.setHeader("filename", "monitoring_scheduled.xlsx");       
		return new ModelAndView(new ExcelView(), model);
	}

	@PostMapping("/api/*/my/monitoring/fullsearch/excel")
	public ModelAndView myMonitoringFullsearchExcel(Authentication auth, UriComponentsBuilder uri, 
			@RequestBody ApplicationsDTO data,
			HttpServletResponse response) throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		data.getScheduled().getHeaders().setPageSize(Integer.MAX_VALUE);
		try {
			data = monitoringServ.monitoringFullSearch(data, user, "");
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		Map<String, Object> model = new HashMap<String, Object>();
		//Sheet Name
		model.put(ExcelView.SHEETNAME, messages.get("fullsearch"));
		//Title
		model.put(ExcelView.TITLE, messages.get("monitoring"));
		//Headers List
		model.put(ExcelView.HEADERS, data.getFullsearch().getHeaders().getHeaders());
		//Rows
		model.put(ExcelView.ROWS, data.getFullsearch().getRows());
		response.setHeader( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"monitoring_fullsearch.xlsx\"");
		response.setHeader("filename", "monitoring_fullsearch.xlsx");       
		return new ModelAndView(new ExcelView(), model);
	}



	/**
	 * Load the activity Creates a path the bread crumb reads
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/activity/load"})
	public ActivityDTO activityLoad(Authentication auth, UriComponentsBuilder uri, @RequestBody ActivityDTO data) throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data = applServ.activityLoad(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	/**
	 * get wokflow activities to build a timeline for the selected activity
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/activity/timeline/workflow"})
	public TimeLineWokflowDTO timelineWorkflow(Authentication auth, UriComponentsBuilder uri, @RequestBody TimeLineWokflowDTO data) throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data = applServ.timelineWorkflow(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * save a checklist
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/application/checklist/save"})
	public CheckListDTO checklistSave(Authentication auth, UriComponentsBuilder uri, @RequestBody CheckListDTO data)
			throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data = applServ.checklistSave(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Reload history for application.
	 * Also, checks the current history ID
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/application/history"})
	public ApplicationHistoryDTO applicationHistory(Authentication auth, UriComponentsBuilder uri, @RequestBody ApplicationHistoryDTO data)
			throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		try {
			UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
			data = applServ.applicationInformationTable(user, data, true);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Reload history
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/application/manager/history"})
	public ApplicationHistoryDTO applicationManagerHistory(Authentication auth, UriComponentsBuilder uri, @RequestBody ApplicationHistoryDTO data)
			throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		try {
			UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
			data = applServ.applicationInformationTable(user, data, true);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Start new application or load an activity
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/thing/load"})
	public ThingDTO thingLoad(Authentication auth, UriComponentsBuilder uri, @RequestBody ThingDTO data) throws DataNotFoundException {
		if(!data.isReadOnly()) {
			accServ.allowAuthenticated(auth, uri);
		}
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			if (data.getNodeId() == 0) {
				data = thingServ.createThing(data, user);
			} else if(data.getUrl().equals(AssemblyService.ACTIVITY_CONFIGURATION) &&
					data.getTitle().equals(messages.get("newactivity"))){
				data = thingServ.createThing(data, user);
			}else {
				data = thingServ.loadThing(data, user);
			}
		} catch (ObjectNotFoundException e) {
			//e.getMessage();
			throw new DataNotFoundException( "Foo Not Found", e);
			//throw new ResponseStatusException(
			//HttpStatus.NOT_FOUND, "Foo Not Found", e);
		}
		//logger.debug("end thing");
		return data;
	}

	/**
	 * Save a thing data
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/thing/save"})
	public ThingDTO thingSaveOthers(Authentication auth, UriComponentsBuilder uri, @RequestBody ThingDTO data) throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		try {
			if(data.getParentId()>0) {
				data = thingServ.saveUnderParent(data,user);
			}else {
				data=thingServ.saveUnderOwner(data, user);
			}
		} catch (ObjectNotFoundException | JsonProcessingException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping({ "/api/*/thing/validate"})
	public ThingDTO thingValidate(Authentication auth, UriComponentsBuilder uri,@RequestBody ThingDTO data) throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		try {
			List<AssemblyDTO> storedDataConfig = thingServ.loadDataConfigurationFromNode(data.getNodeId());
			data = validServ.thing(data,storedDataConfig,true);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Responsible for right fill-out path until the submit
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/activity/path")
	public ThingDTO path(Authentication auth, UriComponentsBuilder uri, @RequestBody ThingDTO data) throws DataNotFoundException {
		accServ.allowAuthenticated(auth,uri);
		try {
			//logger.debug("Start path");
			UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
			data = thingServ.path(user,data);
			//logger.debug("end path");
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Responsible for sub-path to fill out a multiply complex things like persons,
	 * medicines, etc
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/activity/auxpath"})
	public ThingDTO auxPath(Authentication auth, UriComponentsBuilder uri, @RequestBody ThingDTO data) throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		try {
			UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
			data = thingServ.auxPath(user, data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Load a file linked to a thing
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/thing/file/load"})
	public FileDTO fileLoad(Authentication auth, UriComponentsBuilder uri, @RequestBody FileDTO data) throws DataNotFoundException {
		//accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data = thingServ.fileLoad(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Save an application attachment
	 * 
	 * @param auth
	 * @param contextId
	 * @param response
	 * @param jsonDto
	 * @param file
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/thing/file/save"})
	public FileDTO thingFileSave(Authentication auth, UriComponentsBuilder uri, @RequestParam("dto") String jsonDto,
			@RequestParam("file") MultipartFile file) throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		FileDTO data = new FileDTO();
		try {
			UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
			data = objectMapper.readValue(jsonDto, FileDTO.class);
			byte[] fileBytes = new byte[0];
			//if (file.isPresent()) {
			fileBytes = file.getBytes();
			data.setFileSize(file.getSize());
			data.setFileName(file.getOriginalFilename());
			data.setMediaType(file.getContentType());
			data = thingServ.fileSave(data, user, fileBytes);
			//}
		} catch (ObjectNotFoundException | IOException e) {
			throw new DataNotFoundException();
		}
		return data;
	}

	/**
	 * load a table with files
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/thing/files" })
	public FileDTO thingFiles(Authentication auth, UriComponentsBuilder uri, @RequestBody FileDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data = thingServ.thingFiles(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Upload a file from Documents table
	 * 
	 * @param docId
	 * @return
	 * @throws                         org.msh.pdex.ipermit.exceptions.BadQueryParameterException
	 * @throws DataNotFoundException
	 * @throws ObjectNotFoundException
	 */
	@RequestMapping(value = { "/api/*/application/file/download/id={id}"}, method = RequestMethod.GET)
	public ResponseEntity<Resource> applicationFileDownload(Authentication auth, UriComponentsBuilder uri,@PathVariable(value = "id", required = true) Long fileresId)
			throws DataNotFoundException, ObjectNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			return thingServ.fileDownload(fileresId,user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * load a checklist
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/application/checklist/load"})
	public CheckListDTO checklistLoad(Authentication auth, UriComponentsBuilder uri,@RequestBody CheckListDTO data)
			throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data = applServ.checklistLoad(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * reload person's table
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/person/table/load"})
	public PersonDTO personTableLoad(Authentication auth, UriComponentsBuilder uri, @RequestBody PersonDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data = thingServ.personTableLoad(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * Suspend a person
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/person/suspend"})
	public PersonDTO personSuspend(Authentication auth, UriComponentsBuilder uri, @RequestBody PersonDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		accServ.allowAuthenticated(auth, uri);
		try {
			data = thingServ.personSuspend(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Download a resource Apply variables to a template, if applicable
	 * 
	 * @param jsonStr
	 * @return
	 * @throws DataNotFoundException
	 * @throws ObjectNotFoundException
	 */
	//@Deprecated
	@RequestMapping(value = { "/api/*/resource/download/param={param}"}, method = RequestMethod.GET)
	public ResponseEntity<Resource> resourceDownload(Authentication auth, UriComponentsBuilder uri,@PathVariable(value = "param", required = true) String jsonStr)
			throws DataNotFoundException, ObjectNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		try {
			ResourceDTO resDto = objectMapper.readValue(jsonStr, ResourceDTO.class);
			ResourceDTO fres = resourceServ.prepareResourceDownload(resDto);
			UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
			Resource res = resourceServ.fileResolve(fres,user);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(fres.getMediaType()))
					//.contentLength(fres.getFileSize())
					.header(HttpHeaders.CONTENT_DISPOSITION,
							fres.getContentDisp() + "; filename=\"" + fres.getFileName() + "\"")
					.header("filename", fres.getFileName()).body(res);
		} catch (ObjectNotFoundException | IOException e) {
			throw new DataNotFoundException(e);
		}
	}
	/**
	 * New upload to bypass "get" length limitation
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/resource/download/form")
	public ResponseEntity<Resource> resourceDownloadForm(Authentication auth, UriComponentsBuilder uri,@RequestBody ResourceDTO data)
			throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		try {
			UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
			ResourceDTO fres = resourceServ.prepareResourceDownload(data);
			logger.trace("start "+fres.getFileName());
			Resource res = resourceServ.fileResolve(fres, user);
			logger.trace("finish "+fres.getFileName());
			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(fres.getMediaType()))
					//.contentLength(fres.getFileSize()) 2023-11-13
					.header(HttpHeaders.CONTENT_DISPOSITION, fres.getContentDisp() + "; filename=\"" + fres.getFileName() + "\"")
					.header("filename", fres.getFileName()).body(res);
		} catch (ObjectNotFoundException | IOException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * Responsible for extract values from a thing given
	 * 
	 * @param auth
	 * @param thing
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/thing/values/extract"})
	public ThingValuesDTO thingValuesExtract(Authentication auth, UriComponentsBuilder uri, @RequestBody ThingDTO thing)
			throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		ThingValuesDTO data = new ThingValuesDTO();
		try {
			data = thingServ.thingValuesExtract(user, thing, data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Responsible for creation data for activity submit form
	 * 
	 * @param auth
	 * @param thing
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/activity/submit/create/data"})
	public ActivitySubmitDTO submitCreateData(
			Authentication auth,
			UriComponentsBuilder uri,
			HttpServletRequest request,
			@RequestBody ActivitySubmitDTO data)
					throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			String baseUrl =ServletUriComponentsBuilder.fromRequest(request)
					.replacePath(null)
					.build()
					.toUriString();
			data.setServerUrl(baseUrl);
			data = submServ.submitCreateData(user, data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * Submit form has been filled, sent to the next step
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({"/api/*/activity/submit/send"})
	public ActivitySubmitDTO submitSend(
			Authentication auth,
			UriComponentsBuilder uri,
			HttpServletRequest request,
			@RequestBody ActivitySubmitDTO data)
					throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			String baseUrl =ServletUriComponentsBuilder.fromRequest(request)
					.replacePath(null)
					.build()
					.toUriString();
			data.setServerUrl(baseUrl);
			data=submServ.submitSend(user,data);
			if(!data.isValid()) {
				data.setColorAlert("danger");
			}
		} catch (ObjectNotFoundException | JsonProcessingException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * Finish a background activity
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ 
		"/api/*/activity/background/done"
	})
	public ActivityDTO activityBackgroundDone(Authentication auth, UriComponentsBuilder uri, @RequestBody ActivityDTO data)
			throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=applServ.activityBackgroundDone(user,data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * Done an activity, do not assign the next one
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ 
		"/api/*/activity/done"
	})
	public ActivityDTO activityDone(Authentication auth, UriComponentsBuilder uri, @RequestBody ActivityDTO data)
			throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=applServ.activityDone(user,data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * The monitoring
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/activity/history/is/monitoring" })
	public ActivityDTO historyIsMonitorind(Authentication auth, UriComponentsBuilder uri, @RequestBody ActivityDTO data)
			throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data = applServ.activityHistoryIsMonitoring(user, data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Load histroy data
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/activity/history/data")
	public ActivityHistoryDataDTO historyData(Authentication auth, UriComponentsBuilder uri, @RequestBody ActivityHistoryDataDTO data)
			throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=applServ.activityHistoryData(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * Load the data section for the special person
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/application/person/special/load")
	public PersonSpecialDTO personSpecialLoad(Authentication auth, UriComponentsBuilder uri, @RequestBody PersonSpecialDTO data)
			throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=thingServ.personSpecialLoad(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Data unit's things to display
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/activity/amendment/path")
	public DataUnitDTO amendmentPath(Authentication auth, UriComponentsBuilder uri, @RequestBody DataUnitDTO data)
			throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			ThingDTO th = new ThingDTO();
			th.setNodeId(data.getNodeId());
			thingServ.path(user,th);
			data.getPath().clear();
			data.getPath().addAll(th.getPath());
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Save an application data under the user
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/thing/save/application" })
	public ThingDTO thingSaveGuest(Authentication auth, UriComponentsBuilder uri, @RequestBody ThingDTO data) throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data = thingServ.saveUnderOwner(data, user);
		} catch (ObjectNotFoundException | JsonProcessingException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * Save without validation an application data under the user
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/ting/save/relax" })
	public ThingDTO thingSaveRelax(Authentication auth, UriComponentsBuilder uri, @RequestBody ThingDTO data) throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data.setStrict(false);
			data = thingServ.saveUnderOwner(data, user);
		} catch (ObjectNotFoundException | JsonProcessingException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * Refresh a thing, e.g. after onSelectionChange
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/thing/refresh" })
	public ThingDTO thingRefresh(Authentication auth, UriComponentsBuilder uri, @RequestBody ThingDTO data) throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		//TODO code
		return data;
	}
	/**
	 * @throws ObjectNotFoundException 
	 * 
	 */
	@PostMapping({ "/api/*/register/number/new" })
	public RegisterDTO registerNumberNew(Authentication auth, UriComponentsBuilder uri, @RequestBody RegisterDTO data) throws DataNotFoundException, ObjectNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		accServ.allowAuthenticated(auth, uri);
		//data.getReg_number().setValue("1234");
		data=irkaServ.registerNumberNew(user,data);
		return data;
	}
	/**
	 * Reload objects to link table
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/links/table" })
	public LinksDTO linksTable(Authentication auth, UriComponentsBuilder uri, @RequestBody LinksDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=linkServ.loadObjectsTable(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * Select/de-select a row in the table of objects 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/links/selectrow" })
	public LinksDTO linksSelectRow(Authentication auth, UriComponentsBuilder uri, @RequestBody LinksDTO data) throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=linkServ.selectRow(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping({ "/api/*/links/dictionary/select" })
	public LinksDTO linksDictionarySelect(Authentication auth, UriComponentsBuilder uri, @RequestBody LinksDTO data) throws DataNotFoundException {
		accServ.allowAuthenticated(auth, uri);
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=linkServ.dictionarySelect(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}


}
