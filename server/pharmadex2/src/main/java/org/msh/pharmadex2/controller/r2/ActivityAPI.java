package org.msh.pharmadex2.controller.r2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pharmadex2.controller.common.ExcelView;
import org.msh.pharmadex2.dto.ActivityDTO;
import org.msh.pharmadex2.dto.ActivityHistoryDataDTO;
import org.msh.pharmadex2.dto.ActivitySubmitDTO;
import org.msh.pharmadex2.dto.ApplicationHistoryDTO;
import org.msh.pharmadex2.dto.ApplicationsDTO;
import org.msh.pharmadex2.dto.CheckListDTO;
import org.msh.pharmadex2.dto.DataUnitDTO;
import org.msh.pharmadex2.dto.FileDTO;
import org.msh.pharmadex2.dto.PersonDTO;
import org.msh.pharmadex2.dto.PersonSpecialDTO;
import org.msh.pharmadex2.dto.ResourceDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.ThingValuesDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.exception.DataNotFoundException;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.msh.pharmadex2.service.r2.AmendmentService;
import org.msh.pharmadex2.service.r2.ApplicationService;
import org.msh.pharmadex2.service.r2.PdfService;
import org.msh.pharmadex2.service.r2.ResourceService;
import org.msh.pharmadex2.service.r2.SupervisorService;
import org.msh.pharmadex2.service.r2.ThingService;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Common API controls for NMRA users
 * 
 * @author alexk
 *
 */
@RestController
public class ActivityAPI {
	@Autowired
	private UserService userServ;
	@Autowired
	private ApplicationService applServ;
	@Autowired
	private AmendmentService amendServ;
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
	PdfService pdfServ;
	@Autowired
	Messages messages;


	@PostMapping({ "/api/*/my/activities"})
	public ApplicationsDTO myActivities(Authentication auth, @RequestBody ApplicationsDTO data)
			throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		data = applServ.myActivities(data, user);
		return data;
	}

	@PostMapping({ "/api/*/my/monitoring"})
	public ApplicationsDTO myMonitoring(Authentication auth, @RequestBody ApplicationsDTO data)
			throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		data = applServ.myMonitoring(data, user);
		return data;
	}
	
	@PostMapping("/api/*/my/monitoring/actual/excel")
	public ModelAndView myMonitoringActualExcel(Authentication auth, 
			@RequestBody ApplicationsDTO data,
			HttpServletResponse response) {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		data.getTable().getHeaders().setPageSize(Integer.MAX_VALUE);
		data = applServ.myMonitoring(data, user);
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
	public ModelAndView myMonitoringScheduledExcel(Authentication auth, 
			@RequestBody ApplicationsDTO data,
			HttpServletResponse response) {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		data.getScheduled().getHeaders().setPageSize(Integer.MAX_VALUE);
		data = applServ.myMonitoring(data, user);
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
	
	
	/**
	 * Load the activity Creates a path the bread crumb reads
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/activity/load"})
	public ActivityDTO activityLoad(Authentication auth, @RequestBody ActivityDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data = applServ.activityLoad(data, user);
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
	public CheckListDTO checklistSave(Authentication auth, @RequestBody CheckListDTO data)
			throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data = applServ.checklistSave(data, user);
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
	@PostMapping({ "/api/*/application/history"})
	public ApplicationHistoryDTO applicationHistory(Authentication auth, @RequestBody ApplicationHistoryDTO data)
			throws DataNotFoundException {
		try {
			UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
			data = applServ.historyTable(user, data);
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
	public ThingDTO thingLoad(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			if (data.getNodeId() == 0) {
				data = thingServ.createThing(data, user);
			} else {
				data = thingServ.loadThing(data, user);
			}
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
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
	public ThingDTO thingSaveOthers(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		try {
			if(data.getParentId()>0) {
				data = applServ.thingSaveUnderParent(data,user);
			}else {
				data=thingServ.save(data, user);
			}
		} catch (ObjectNotFoundException | JsonProcessingException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping({ "/api/*/thing/validate"})
	public ThingDTO thingValidate(@RequestBody ThingDTO data) throws DataNotFoundException {
		try {
			data = validServ.thing(data,true);
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
	@PostMapping({ "/api/*/activity/path"})
	public ThingDTO path(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		try {
			data = thingServ.path(data);
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
	public ThingDTO auxPath(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		try {
			data = thingServ.auxPath(data);
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
	public FileDTO fileLoad(Authentication auth, @RequestBody FileDTO data) throws DataNotFoundException {
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
	public FileDTO thingFileSave(Authentication auth, @RequestParam("dto") String jsonDto,
			@RequestParam("file") Optional<MultipartFile> file) throws DataNotFoundException {
		FileDTO data = new FileDTO();
		try {
			UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
			data = objectMapper.readValue(jsonDto, FileDTO.class);
			byte[] fileBytes = new byte[0];
			if (file.isPresent()) {
				fileBytes = file.get().getBytes();
				data.setFileSize(file.get().getSize());
				data.setFileName(file.get().getOriginalFilename());
				data.setMediaType(file.get().getContentType());
				data = thingServ.fileSave(data, user, fileBytes);
			}
		} catch (ObjectNotFoundException | IOException e) {
			throw new DataNotFoundException(e);
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
	public FileDTO thingFiles(Authentication auth, @RequestBody FileDTO data) throws DataNotFoundException {
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
	public ResponseEntity<Resource> applicationFileDownload(@PathVariable(value = "id", required = true) Long fileresId)
			throws DataNotFoundException, ObjectNotFoundException {
		try {
			return thingServ.fileDownload(fileresId);
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
	public CheckListDTO checklistLoad(Authentication auth, @RequestBody CheckListDTO data)
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
	public PersonDTO personTableLoad(Authentication auth, @RequestBody PersonDTO data) {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		data = thingServ.personTableLoad(data, user);
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
	@Deprecated
	@RequestMapping(value = { "/api/*/resource/download/param={param}"}, method = RequestMethod.GET)
	public ResponseEntity<Resource> resourceDownload(@PathVariable(value = "param", required = true) String jsonStr)
			throws DataNotFoundException, ObjectNotFoundException {
		try {
			ResourceDTO resDto = objectMapper.readValue(jsonStr, ResourceDTO.class);
			ResourceDTO fres = resourceServ.prepareResourceDownload(resDto);
			Resource res = resourceServ.fileResolve(fres);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(fres.getMediaType()))

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
	public ResponseEntity<Resource> resourceDownloadForm(@RequestBody ResourceDTO data)
			throws DataNotFoundException {
		try {
			ResourceDTO fres = resourceServ.prepareResourceDownload(data);
			Resource res = resourceServ.fileResolve(fres);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(fres.getMediaType()))
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
	public ThingValuesDTO thingValuesExtract(Authentication auth, @RequestBody ThingDTO thing)
			throws DataNotFoundException {
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
	public ActivitySubmitDTO submitCreateData(Authentication auth, @RequestBody ActivitySubmitDTO data)
			throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data = applServ.submitCreateData(user, data);
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
	@PostMapping({ 
		"/api/*/activity/submit/send"
	})
	public ActivitySubmitDTO submitSend(Authentication auth, @RequestBody ActivitySubmitDTO data)
			throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=applServ.submitSend(user,data);
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
	public ActivityDTO activityBackgroundDone(Authentication auth, @RequestBody ActivityDTO data)
			throws DataNotFoundException {
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
	public ActivityDTO activityDone(Authentication auth, @RequestBody ActivityDTO data)
			throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=applServ.activityDone(user,data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Is this activity monitoring, therefore can't be changed of reassigned
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/*/activity/history/is/monitoring" })
	public ActivityDTO historyIsMonitorind(Authentication auth, @RequestBody ActivityDTO data)
			throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data = applServ.activityHistoryIsMonitoring(user, data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	@RequestMapping(value = { "/api/*/activity/printprev"})
	public ThingDTO printprev(Authentication auth, @RequestBody ThingDTO data)
			throws DataNotFoundException, ObjectNotFoundException, IOException {
		try {
			UserDetailsDTO user = new UserDetailsDTO();
			user = userServ.userData(auth, user);
			return pdfServ.printprev(user, data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}
	
	/**
	 * Load histroy data
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/activity/history/data")
	public ActivityHistoryDataDTO historyData(Authentication auth, @RequestBody ActivityHistoryDataDTO data)
			throws DataNotFoundException {
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
	public PersonSpecialDTO personSpecialLoad(Authentication auth, @RequestBody PersonSpecialDTO data)
			throws DataNotFoundException {
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
	public DataUnitDTO amendmentPath(Authentication auth, @RequestBody DataUnitDTO data)
			throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			ThingDTO th = new ThingDTO();
			th.setNodeId(data.getNodeId());
			thingServ.path(th);
			data.getPath().clear();
			data.getPath().addAll(th.getPath());
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
}
