package org.msh.pharmadex2.controller.r2;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.dto.ApplicationOrActivityDTO;
import org.msh.pharmadex2.dto.ApplicationSelectDTO;
import org.msh.pharmadex2.dto.ApplicationsDTO;
import org.msh.pharmadex2.dto.CheckListDTO;
import org.msh.pharmadex2.dto.ContentDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.GisLocationDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.mock.ChoiceDTO;
import org.msh.pharmadex2.exception.DataNotFoundException;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.r2.AmendmentService;
import org.msh.pharmadex2.service.r2.ApplicationService;
import org.msh.pharmadex2.service.r2.ContentService;
import org.msh.pharmadex2.service.r2.DictService;
import org.msh.pharmadex2.service.r2.SystemService;
import org.msh.pharmadex2.service.r2.ThingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
@RestController
public class GuestAPI {
	@Autowired
	private ContentService contentService;
	@Autowired
	private UserService userServ;
	@Autowired
	private ThingService thingServ;
	@Autowired
	private ApplicationService applServ;
	@Autowired
	private DictService dictService;
	@Autowired
	private AmendmentService amendServ;
	@Autowired
	private SystemService systemServ;


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
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		try {
			data=systemServ.applicationsDictionary(data);
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
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=systemServ.amendmentDictionary(data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	/**
	 * Dictionary of all possible renewals
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/*/renewal")
	public DictionaryDTO renewal(Authentication auth, @RequestBody DictionaryDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=systemServ.renewalDict(data);
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
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
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
	@PostMapping("/api/guest/applications/table")
	public ApplicationsDTO applicatonsTable(Authentication auth, @RequestBody ApplicationsDTO data) throws DataNotFoundException {
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		try {
			data=applServ.applicatonsTable(data, user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Save an application
	 * 
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping({ "/api/guest/thing/save/application" })
	public ThingDTO thingSaveGuest(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data = thingServ.save(data, user);
		} catch (ObjectNotFoundException | JsonProcessingException e) {
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
			data=applServ.submit(data,user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}

	/**
	 * Will we start an application or work in an activity?
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/guest/application/or/activity")
	public ApplicationOrActivityDTO applicationOrActivity(@RequestBody ApplicationOrActivityDTO data) throws DataNotFoundException{
		try {
			data=applServ.applOrAct(data);
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
		UserDetailsDTO user =userServ.userData(auth, new UserDetailsDTO());
		DictionaryDTO dict = data.getDictionaries().get("workflows");
		ChoiceDTO ret;
		try {
			ret = dictService.mockChoice(dict);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return ret;
	}
	
}
