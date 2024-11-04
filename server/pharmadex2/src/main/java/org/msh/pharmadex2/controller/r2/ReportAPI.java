package org.msh.pharmadex2.controller.r2;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.dto.ApplicationEventsDTO;
import org.msh.pharmadex2.dto.ApplicationHistoryDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.exception.DataNotFoundException;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.r2.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
/**
 * Internal reports - guest and NMRA users
 * @author alexk
 *
 */
@RestController
public class ReportAPI {
	@Autowired
	private UserService userServ;
	@Autowired
	private ReportService reportServ;

	/**
	 * Load events for an application drilled down
	 * @param auth
	 * @param data
	 * @return
	 */
	@PostMapping("/api/*/report/application/events")
	public ApplicationEventsDTO applicationEvents(Authentication auth, @RequestBody ApplicationEventsDTO data) {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		data=reportServ.applicationEvents(user,data);
		return data;
	}
	/**
	 * Load data regarding the event - modification or some else
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException 
	 */
	@PostMapping("/api/*/report/application/events/data")
	public ApplicationEventsDTO applicationEventsData(Authentication auth, @RequestBody ApplicationEventsDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=reportServ.applicationEventsData(user,data);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return data;
	}
	
	/**
	 * Load the history and the list of modification for an application defined by nodeId
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/report/application/history")
	public ApplicationHistoryDTO applicationHistory(Authentication auth, @RequestBody ApplicationHistoryDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		data=reportServ.applicationHistory(data,user);
		return data;
	}
	
	/**
	 * Load all registers related to the application
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/report/application/registers")
	public ApplicationHistoryDTO applicationRegisters(Authentication auth, @RequestBody ApplicationHistoryDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		data=reportServ.applicationRegisters(data,user);
		return data;
	}
	/**
	 * Init, not load, ThingDTO using history ID
	 * @param auth
	 * @param historyID
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/report/activity/data/load")
	public ThingDTO activityDataLoad(Authentication auth, @RequestBody Long historyID) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		ThingDTO thingDto;
		try {
			thingDto = reportServ.activityDataLoad(historyID,user);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
		return thingDto;
	}
}
