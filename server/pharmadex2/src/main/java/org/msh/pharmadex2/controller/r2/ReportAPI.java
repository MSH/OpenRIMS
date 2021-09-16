package org.msh.pharmadex2.controller.r2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pharmadex2.controller.common.ExcelView;
import org.msh.pharmadex2.dto.ReportDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
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
	@Autowired
	private Messages messages;
	
	/**
	 * Load any configured report
	 * @param auth
	 * @param data
	 * @return
	 * @throws DataNotFoundException
	 */
	@PostMapping("/api/*/report/load")
	public ReportDTO futurePharma(Authentication auth, @RequestBody ReportDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=reportServ.load(user, data);
			return data;
		} catch (ObjectNotFoundException e) {
			throw new DataNotFoundException(e);
		}
	}
	/**
	 * Reset all data to the initial state
	 * The first element of the breadcrumb has been pressed
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
	@PostMapping("/api/*/report/reset/path")
	public ReportDTO resetPath(Authentication auth, @RequestBody ReportDTO data) throws DataNotFoundException {
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data=reportServ.resetPath(user, data);
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
		UserDetailsDTO user = userServ.userData(auth, new UserDetailsDTO());
		try {
			data.setThing(thingServ.path(data.getThing()));
			data.setThing(reportServ.regTable(data.getThing()));
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
			data = reportServ.regTable(data);
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
		for(OptionDTO opt : data.getDict().getPath()) {
			pathStr.add(opt.getCode());
		}
		try {
			data.getTable().getHeaders().setPageSize(Integer.MAX_VALUE);
			data=reportServ.load(user, data);
			Map<String, Object> model = new HashMap<String, Object>();
			//Sheet Name
			model.put(ExcelView.SHEETNAME, data.getDict().getHome());
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
	
}
