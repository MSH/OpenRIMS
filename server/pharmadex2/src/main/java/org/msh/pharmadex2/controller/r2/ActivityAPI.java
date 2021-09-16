package org.msh.pharmadex2.controller.r2;

import java.io.IOException;
import java.util.Optional;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.dto.ActivityDTO;
import org.msh.pharmadex2.dto.ActivitySubmitDTO;
import org.msh.pharmadex2.dto.ApplicationHistoryDTO;
import org.msh.pharmadex2.dto.ApplicationsDTO;
import org.msh.pharmadex2.dto.CheckListDTO;
import org.msh.pharmadex2.dto.DataConfigDTO;
import org.msh.pharmadex2.dto.FileDTO;
import org.msh.pharmadex2.dto.PersonDTO;
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
	AmendmentService amendServ;

	@PostMapping({ "/api/admin/my/activities", "/api/moderator/my/activities", "/api/screener/my/activities",
		"/api/reviewer/my/activities", "/api/accountant/my/activities", "/api/inspector/my/activities","/api/guest/my/activities" })
	public ApplicationsDTO myActivities(Authentication auth, @RequestBody ApplicationsDTO data)
			throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		data = applServ.myActivities(data, user);
		return data;
	}

	@PostMapping({ "/api/admin/my/monitoring", "/api/moderator/my/monitoring",  "/api/guest/my/monitoring" })
	public ApplicationsDTO myMonitoring(Authentication auth, @RequestBody ApplicationsDTO data)
			throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		data = applServ.myMonitoring(data, user);
		return data;
	}

	/**
	 * Load the activity Creates a path the bread crumb reads
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/guest/activity/load", "/api/admin/activity/load", "/api/screener/activity/load",
		"/api/moderator/activity/load", "/api/accountant/activity/load", "/api/reviewer/activity/load",
	"/api/inspector/activity/load" })
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
	@PostMapping({ "/api/guest/application/checklist/save", "/api/admin/application/checklist/save",
		"/api/moderator/application/checklist/save", "/api/screener/application/checklist/save",
		"/api/reviewer/application/checklist/save", "/api/inspector/application/checklist/save",
	"/api/accountant/application/checklist/save" })
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
	@PostMapping({ "/api/guest/application/history", "/api/admin/application/history",
		"/api/moderator/application/history", "/api/screener/application/history",
		"/api/reviewer/application/history", "/api/accountant/application/history",
	"/api/inspector/application/history" })
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
	@PostMapping({ "/api/guest/thing/load", "/api/moderator/thing/load", "/api/screener/thing/load",
		"/api/reviewer/thing/load", "/api/inspector/thing/load", "/api/accountant/thing/load" })
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
	@PostMapping({ "/api/guest/thing/save","/api/admin/thing/save", "/api/moderator/thing/save", "/api/screener/thing/save", "/api/reviewer/thing/save",
		"/api/inspector/thing/save", "/api/accountant/thing/save" })
	public ThingDTO thingSaveOthers(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		try {
			data = applServ.thingSaveUnderParent(data,user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	@PostMapping({ "/api/admin/thing/validate", "/api/guest/thing/validate", "/api/moderator/thing/validate",
		"/api/screener/thing/validate", "/api/reviewer/thing/validate", "/api/inspector/thing/validate",
	"/api/accountant/thing/validate" })
	public ThingDTO thingValidate(@RequestBody ThingDTO data) throws DataNotFoundException {
		try {
			data = validServ.thing(data);
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
	@PostMapping({ "/api/guest/activity/path", "/api/admin/activity/path", "/api/moderator/activity/path",
		"/api/screener/activity/path", "/api/reviewer/activity/path", "/api/inspector/activity/path",
	"/api/accountant/activity/path" })
	public ThingDTO activityPath(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
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
	@PostMapping({ "/api/guest/activity/auxpath", "/api/admin/activity/auxpath", "/api/moderator/activity/auxpath",
		"/api/screener/activity/auxpath", "/api/reviewer/activity/auxpath", "/api/inspector/activity/auxpath",
	"/api/accountant/activity/auxpath" })
	public ThingDTO activityAuxPath(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
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
	@PostMapping({ "/api/guest/thing/file/load", "/api/admin/thing/file/load", "/api/moderator/thing/file/load",
		"/api/screener/thing/file/load", "/api/reviewer/thing/file/load", "/api/inspector/thing/file/load",
	"/api/accountant/thing/file/load" })
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
	@PostMapping({ "/api/guest/thing/file/save", "/api/admin/thing/file/save", "/api/moderator/thing/file/save",
		"/api/screener/thing/file/save", "/api/reviewer/thing/file/save", "/api/inspector/thing/file/save",
		"/api/accountant/thing/file/save"

	})
	public FileDTO fileSave(Authentication auth, @RequestParam("dto") String jsonDto,
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
	@PostMapping({ "/api/guest/thing/files", "/api/admin/thing/files", "/api/moderator/thing/files",
		"/api/screener/thing/files", "/api/reviewer/thing/files", "/api/inspector/thing/files",
	"/api/accountant/thing/files" })
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
	@RequestMapping(value = { "/api/guest/application/file/download/id={id}",
			"/api/admin/application/file/download/id={id}", "/api/moderator/application/file/download/id={id}",
			"/api/screener/application/file/download/id={id}", "/api/reviewer/application/file/download/id={id}",
			"/api/inspector/application/file/download/id={id}",
	"/api/accountant/application/file/download/id={id}" }, method = RequestMethod.GET)
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
	@PostMapping({ "/api/guest/application/checklist/load", "/api/admin/application/checklist/load",
		"/api/moderator/application/checklist/load", "/api/screener/application/checklist/load",
		"/api/reviewer/application/checklist/load", "/api/inspector/application/checklist/load",
	"/api/accountant/application/checklist/load" })
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
	@PostMapping({ "/api/guest/person/table/load", "/api/admin/person/table/load", "/api/moderator/person/table/load",
		"/api/screener/person/table/load", "/api/reviewer/person/table/load", "/api/inspector/person/table/load",
	"/api/accountant/person/table/load" })
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
	@RequestMapping(value = { "/api/guest/resource/download/param={param}",
			"/api/admin/resource/download/param={param}", "/api/moderator/resource/download/param={param}",
			"/api/screener/resource/download/param={param}", "/api/reviewer/resource/download/param={param}",
			"/api/inspector/resource/download/param={param}",
	"/api/accountant/resource/download/param={param}" }, method = RequestMethod.GET)
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
	@PostMapping({ "/api/admin/thing/values/extract", "/api/guest/thing/values/extract",
		"/api/moderator/thing/values/extract", "/api/screener/thing/values/extract",
		"/api/reviewer/thing/values/extract", "/api/inspector/thing/values/extract",
	"/api/accountant/thing/values/extract" })
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
	@PostMapping({ "/api/admin/activity/submit/create/data", "/api/guest/activity/submit/create/data",
		"/api/moderator/activity/submit/create/data", "/api/screener/activity/submit/create/data",
		"/api/reviewer/activity/submit/create/data", "/api/inspector/activity/submit/create/data",
	"/api/accountant/activity/submit/create/data" })
	public ActivitySubmitDTO activitySubmitCreateData(Authentication auth, @RequestBody ActivitySubmitDTO data)
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
		"/api/guest/activity/submit/send",
		"/api/admin/activity/submit/send",
		"/api/moderator/activity/submit/send",
		"/api/screener/activity/submit/send",
		"/api/reviewer/activity/submit/send",
		"/api/inspector/activity/submit/send",
		"/api/accountant/activity/submit/send"
	})
	public ActivitySubmitDTO activitySubmitSend(Authentication auth, @RequestBody ActivitySubmitDTO data)
			throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=applServ.submitSend(user,data);
		} catch (ObjectNotFoundException e) {
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
		"/api/admin/activity/background/done",
		"/api/guest/activity/background/done",
		"/api/moderator/activity/background/done",
		"/api/screener/activity/background/done",
		"/api/reviewer/activity/background/done",
		"/api/inspector/activity/background/done",
		"/api/accountant/activity/background/done"
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
		"/api/admin/activity/done",
		"/api/guest/activity/done",
		"/api/moderator/activity/done",
		"/api/screener/activity/done",
		"/api/reviewer/activity/done",
		"/api/inspector/activity/done",
		"/api/accountant/activity/done"
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
	@PostMapping({ "/api/admin/activity/history/is/monitoring", "/api/guest/activity/history/is/monitoring",
		"/api/moderator/activity/history/is/monitoring", "/api/screener/activity/history/is/monitoring",
		"/api/reviewer/activity/history/is/monitoring", "/api/inspector/activity/history/is/monitoring",
	"/api/accountant/activity/history/is/monitoring" })
	public ActivityDTO activityHistoryIsMonitorind(Authentication auth, @RequestBody ActivityDTO data)
			throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data = applServ.activityHistoryIsMonitoring(user, data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	/*@RequestMapping(value = { "/api/admin/activity/printpdf/historyid={param}", "/api/guest/activity/printpdf/historyid={param}",
			"/api/moderator/activity/printpdf/historyid={param}", "/api/screener/activity/printpdf/historyid={param}",
			"/api/reviewer/activity/printpdf/historyid={param}", "/api/inspector/activity/printpdf/historyid={param}",
			"/api/accountant/activity/printpdf/historyid={param}"}, method = RequestMethod.GET)
	public ResponseEntity<Resource> printpdf(Authentication auth, @PathVariable(value = "param", required = true) Long historyid)
			throws DataNotFoundException, ObjectNotFoundException, IOException, DocumentException {
		try {
			UserDetailsDTO user = new UserDetailsDTO();
			user = userServ.userData(auth, user);
			return pdfServ.printPDF(user, historyid);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}*/
	
	@RequestMapping(value = { "/api/admin/activity/printprev", "/api/guest/activity/printprev",
			"/api/moderator/activity/printprev", "/api/screener/activity/printprev",
			"/api/reviewer/activity/printprev", "/api/inspector/activity/printprev",
			"/api/accountant/activity/printprev"})
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
	 * Load list of thing's chapters. First one always the main
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ 
		"/api/*/thing/chapters/load",
	})
	public DataConfigDTO thingChaptersLoad(Authentication auth, @RequestBody DataConfigDTO data)
			throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=amendServ.thingChaptersLoad(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
}
