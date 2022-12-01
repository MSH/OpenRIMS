package org.msh.pharmadex2.controller.r2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.controller.common.ExcelView;
import org.msh.pharmadex2.dto.ApplicationEventsDTO;
import org.msh.pharmadex2.dto.ApplicationHistoryDTO;
import org.msh.pharmadex2.dto.ReportDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.exception.DataNotFoundException;
import org.msh.pharmadex2.service.common.UserService;
import org.msh.pharmadex2.service.r2.PdfService;
import org.msh.pharmadex2.service.r2.ReportService;
import org.msh.pharmadex2.service.r2.ThingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
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
	@Autowired
	private ThingService thingServ;
	@Autowired
	private PdfService pdfServ;
	/**
	 * Reset all data to the initial state
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/report/reset/root")
	public ReportDTO resetRoot(Authentication auth, @RequestBody ReportDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=reportServ.resetRoot(user, data);
			return data;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}
	
	
	/**
	 * Set the path of the dictionary, reset table, reset thing
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/report/application")
	public ReportDTO application(Authentication auth, @RequestBody ReportDTO data) throws DataNotFoundException {
		userServ.userData(auth, new UserDetailsDTO());
		try {
			data.setThing(thingServ.path(data.getThing()));
			return data;
		} catch (ObjectNotFoundException e) {
					throw new DataNotFoundException(e);
		}
	}
	
	/**
	 * Preview print copy of an application
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/report/print/preview")
	public ThingDTO printPreview(Authentication auth, @RequestBody ThingDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data = pdfServ.printprev(user, data);
			return data;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}

	/**
	 * Export report to the Excel
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/report/export/excel")
	public ModelAndView exportExcel(Authentication auth, 
			@RequestBody ReportDTO data,
			HttpServletResponse response
			) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		List<String> pathStr = new ArrayList<String>();
		try {
			data=reportServ.load(user, data);
			data.getTable().getHeaders().setPageSize(Integer.MAX_VALUE);
			Map<String, Object> model = new HashMap<String, Object>();
			//Sheet Name
			model.put(ExcelView.SHEETNAME, data.getConfig().getTitle());
			//Title
			model.put(ExcelView.TITLE, String.join(",", pathStr));
			//Headers List
			model.put(ExcelView.HEADERS, data.getTable().getHeaders().getHeaders());
			//Rows
			model.put(ExcelView.ROWS, data.getTable().getRows());
			response.setHeader( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Applications.xlsx\"");
			response.setHeader("filename", "Applications.xlsx");       
			return new ModelAndView(new ExcelView(), model);
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}
	
	/**
	 * Load a table with reports available for the user
	 * @param auth
	 * @param data
	 * @return
	 */
	@PostMapping("/api/*/report/all")
	public TableQtb reportAll(Authentication auth, @RequestBody TableQtb data) {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		data=reportServ.all(user, data);
		return data;
	}
	/**
	 * load report selected as a table of report items
 	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/report/load")
	public ReportDTO load(Authentication auth, @RequestBody ReportDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=reportServ.load(user, data);
			return data;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}
	
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
