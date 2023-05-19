package org.msh.pharmadex2.controller.r2;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.dto.AmendmentNewDTO;
import org.msh.pharmadex2.dto.ApplicationOrActivityDTO;
//import org.msh.pharmadex2.dto.ApplicationSelectDTO;
import org.msh.pharmadex2.dto.ApplicationsDTO;
import org.msh.pharmadex2.dto.CheckListDTO;
import org.msh.pharmadex2.dto.ContentDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.GisLocationDTO;
import org.msh.pharmadex2.dto.HostScheduleDTO;
import org.msh.pharmadex2.dto.LegacyDataDTO;
import org.msh.pharmadex2.dto.PermitsDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.mock.ChoiceDTO;
import org.msh.pharmadex2.exception.DataNotFoundException;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.r2.AmendmentService;
import org.msh.pharmadex2.service.r2.ApplicationService;
import org.msh.pharmadex2.service.r2.ContentService;
import org.msh.pharmadex2.service.r2.DeregistrationService;
import org.msh.pharmadex2.service.r2.DictService;
import org.msh.pharmadex2.service.r2.InspectionService;
import org.msh.pharmadex2.service.r2.LegacyDataService;
import org.msh.pharmadex2.service.r2.SubmitService;
import org.msh.pharmadex2.service.r2.SystemService;
//import org.msh.pharmadex2.service.r2.ThingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//import com.fasterxml.jackson.core.JsonProcessingException;
@RestController
public class GuestAPI {
	@Autowired
	private ContentService contentService;
	@Autowired
	private UserService userServ;
	@Autowired
	private ApplicationService applServ;
	@Autowired
	private DictService dictService;
	@Autowired
	private AmendmentService amendServ;
	@Autowired
	private SystemService systemServ;
	@Autowired
	private LegacyDataService legacyServ;
	@Autowired
	private DeregistrationService deregServ;
	@Autowired
	private InspectionService inspectionServ;
	@Autowired
	private SubmitService submServ;

	/**
	 * Tiles for landing page
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/guest/content")
	public ContentDTO guestContent(@RequestBody ContentDTO data) throws DataNotFoundException {
		try {
			data=contentService.loadContent(data, "guest");
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * List of guest applications
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/applications")
	public DictionaryDTO applications(Authentication auth, @RequestBody DictionaryDTO data) throws DataNotFoundException {
		//UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		try {
			data=systemServ.applicationsDictionary("dictionary.guest.applications",data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	/**
	 * List of guest inspection applications
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/applications/inspections")
	public DictionaryDTO applicationsInspections(Authentication auth, @RequestBody DictionaryDTO data) throws DataNotFoundException {
		//UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		try {
			data=systemServ.applicationsDictionary("dictionary.guest.inspections",data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * Dictionary of all possible amendments
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/*/amendments")
	public DictionaryDTO amendments(Authentication auth, @RequestBody DictionaryDTO data) throws DataNotFoundException {
		//UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=systemServ.amendmentDictionary(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	/**
	 * Get a list of permits for a user and permit type given
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/permits")
	public PermitsDTO permits(Authentication auth, @RequestBody PermitsDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=inspectionServ.permits(user,data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	/**
	 * Dictionary of all possible de-registration
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/*/deregistration")
	public DictionaryDTO deregistration(Authentication auth, @RequestBody DictionaryDTO data) throws DataNotFoundException {
		//UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=systemServ.deregistrationDict(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * List of guest applications
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/guest/applications/table/search={s}")
	public ApplicationsDTO applicatonsTable(Authentication auth, @RequestBody ApplicationsDTO data, @PathVariable(value = "s") String s) throws DataNotFoundException {
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		try {
			data=applServ.applicatonsTable(data, user, s);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	/**
	 * List of amendment applications
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/guest/applications/table/amendments/search={s}")
	public ApplicationsDTO applicatonsTableAmendment(Authentication auth, @RequestBody ApplicationsDTO data, @PathVariable(value = "s") String s) throws DataNotFoundException {
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		try {
			data.setAmendment(true);
			data=applServ.applicatonsTable(data, user, s);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	/**
	 * submit an application after checklist
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/guest/application/submit")
	public CheckListDTO applicationSubmit(Authentication auth, @RequestBody CheckListDTO data) throws DataNotFoundException {
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		try {
			data=submServ.submit(data,user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Will we start an application or work in an activity?
	 * Calculate historyId for the most appropriate history record
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/*/application/or/activity")
	public ApplicationOrActivityDTO applicationOrActivity(Authentication auth,@RequestBody ApplicationOrActivityDTO data) throws DataNotFoundException{
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		try {
			data=applServ.applOrAct(user,data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * По выбранному адресу загружаем список аптек
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/*/application/loadcentermap")
	public GisLocationDTO loadCenterMap(@RequestBody GisLocationDTO data) throws DataNotFoundException{
		try {
			data=dictService.loadCenterMap(data);
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	/**
	 * Create a user's choice from the mock ThingDTO
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/guest/mock/choice")
	public ChoiceDTO mockChoice(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException{
		//UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		DictionaryDTO dict = data.getDictionaries().get("workflows");
		ChoiceDTO ret;
		try {
			ret = dictService.mockChoice(dict);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return ret;
	}
	
	/**
	 * Load applications that are possible to amend
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/amendment/propose/add")
	public AmendmentNewDTO amendmentProposeAdd(Authentication auth, @RequestBody AmendmentNewDTO data) throws DataNotFoundException {
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
			try {
				data=amendServ.proposeAdd(user, data);
			} catch (ObjectNotFoundException e) {
				throw new DataNotFoundException(e);
			}
		return data;
	}
	
	@PostMapping("/api/*/deregistration/propose/add")
	public AmendmentNewDTO deregistrationProposeAdd(Authentication auth, @RequestBody AmendmentNewDTO data) throws DataNotFoundException {
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
			try {
				data=deregServ.proposeAdd(user, data);
			} catch (ObjectNotFoundException e) {
				throw new DataNotFoundException(e);
			}
		return data;
	}
	
	/**
	 * Legacy data reload the table
	 * @param auth
	 * @param data
	 * @return
	 */
	@PostMapping("/api/*/legacy/data")
	public LegacyDataDTO legacyData(Authentication auth, @RequestBody LegacyDataDTO data) {
		data=legacyServ.reloadTable(data);
		return data;
	}
	
	/**
	 * Legacy data reload the table
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/*/host/schedule")
	public HostScheduleDTO hostSchedule(Authentication auth, @RequestBody HostScheduleDTO data) throws DataNotFoundException {
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		try {
			data=inspectionServ.hostSchedule(user,data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
}
