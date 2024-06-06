package org.msh.pharmadex2.service.r2;

import java.io.IOException;
import java.lang.Thread.State;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.ThreadUtils;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.Pharmadex2Beans;
import org.msh.pharmadex2.dto.AsyncInformDTO;
import org.msh.pharmadex2.dto.ImportWorkflowDTO;
import org.msh.pharmadex2.dto.ReassignUserDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Common service for any async operations initiated by a user
 * Currently:
 * <ul>
 * <li> import admin units
 * <li> DWH update
 * </ul>
 * 
 * @author alexk
 *
 */
@Service
public class AsyncService {
	
	// Processes
	private static final String PROCESS_DWH_UPDATE = "processDwhUpdate";
	public static final String PROCESS_REASSIGN_APPLICANT = "reassignApplicant";
	public static final String PROCESS_IMPORT_ADMIN_UNITS = "processImportAdminUnits";
	private static final String PROCESS_IMPORT_ATC = "processImportATC";
	private static final String PROCESS_IMPORT_LEGACYDATA = "processLegacyData";
	private static final String PROCESS_IMPORT_WF = "processImportWF";
	
	public static final String PROCESS_NAME = "processName";
	//shared multi thread memory
	public static ConcurrentMap<String,String> asyncContext = new ConcurrentHashMap<String, String>();
	// excel import keys. The most processes are import from the Excel
	public static final String PROGRESS_SHEETS_IMPORTED = "sheets_imported";
	public static final String PROGRESS_SHEETS = "sheets";
	public static final String PROGRESS_CURRENT_SHEET = "currentSheet";
	
	public static final String PROGRESS_TOTAL = "total";
	public static final String PROGRESS_TOTAL_REMOVE = "totalremove";
	public static final String PROGRESS_COUNTER = "counter";
	public static final String PROGRESS_COUNTER_REMOVE = "counterremove";
	
	public static final String PROGRESS_STOP_ERROR = "stopError";
	//logger
	private static final Logger logger = LoggerFactory.getLogger(AsyncService.class);

	@Autowired
	private ImportAdmUnitsService adminUnit;
	@Autowired
	private DWHService dwhServ;
	@Autowired
	private ImportATCcodesService importAtc;
	@Autowired
	private ImportBService importLegacyServ;
	@Autowired
	private ImportWorkflowService importwfServ;
	@Autowired
	private Messages messages;
	@Autowired
	private ReassignUserService reassignUserServ;

	/**
	 * Read async context variable by key
	 * @param key
	 * @return empty string is not found
	 */
	public static String readAsyncContext(String key) {
		String ret="";
		String val=asyncContext.get(key);
		if(val!=null) {
			ret=val;
		}
		return ret;
	}
	/**
	 * Initialize the async context
	 */
	public static void initialize() {
		asyncContext=new ConcurrentHashMap<String, String>();
		writeAsyncContext(PROGRESS_SHEETS, "0");
		writeAsyncContext(PROGRESS_SHEETS_IMPORTED, "0");
	}

	/**
	 * Write data to the async context
	 * @param key
	 * @param val
	 */
	public static void writeAsyncContext(String key, String val) {
		asyncContext.put(key, val);
	}

	/**
	 * Is the Data Import Thread running?
	 * @return
	 */
	public static boolean hasDataImportThread() {
		boolean ret=false;
		for (Thread t : ThreadUtils.getAllThreads()) {
			if(t.getName().startsWith(Pharmadex2Beans.DATA_IMPORT)) {
				if(t.isAlive() && t.getState()==State.RUNNABLE) {
					ret=true;
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * Run admin unit import
	 * @param data
	 * @param user
	 */
	@Async("taskExecutorDataImport")
	public void importAdminunitsRun(ThingDTO data, UserDetailsDTO user){
		initialize();
		AsyncService.writeAsyncContext(PROCESS_NAME, PROCESS_IMPORT_ADMIN_UNITS);	//mandatory for all async processes
		adminUnit.importAdminunitsWorker(data, user);
	}
	
	/**
	 * Start async thread to run ATC import
	 * @param data
	 * @param user
	 */
	@Async("taskExecutorDataImport")
	public void importAtccodesRun(ThingDTO data, UserDetailsDTO user) {
		initialize();
		AsyncService.writeAsyncContext(PROCESS_NAME, PROCESS_IMPORT_ATC);
		try {
			importAtc.importRunWorker(data,user);
		} catch (ObjectNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Start async thread to run import Legacy data
	 * @param data
	 * @param user
	 */
	@Async("taskExecutorDataImport")
	public void importLegacyDataRun(ThingDTO data, UserDetailsDTO user) {
		initialize();
		AsyncService.writeAsyncContext(PROCESS_NAME, PROCESS_IMPORT_LEGACYDATA);
		try {
			importLegacyServ.importRunWorker(data,user);
		} catch (ObjectNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Start async thread to run import wf
	 * @param data
	 * @param user
	 */
	@Async("taskExecutorDataImport")
	public void importWFRun(ImportWorkflowDTO data, UserDetailsDTO user) {
		initialize();
		AsyncService.writeAsyncContext(PROCESS_NAME, PROCESS_IMPORT_WF);
		try {
			importwfServ.importRunWorker(data, user);
		} catch (ObjectNotFoundException | JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Upload DWH 
	 */
	@Async("taskExecutorDataImport")
	public void dwhUploadRun() {
		initialize();
		AsyncService.writeAsyncContext(PROCESS_NAME, PROCESS_DWH_UPDATE);	//mandatory for all async processes
		dwhServ.uploadWorker();
	}
	/**
	 * Reassign an applicant
	 * @param data
	 * @return
	 */
	@Async("taskExecutorDataImport")
	public void reassignApplicantRun(ReassignUserDTO data) {
		initialize();
		writeAsyncContext(PROCESS_NAME, PROCESS_REASSIGN_APPLICANT);
		try {
			reassignUserServ.reassignApplicantWorker(data);
		} catch (JsonProcessingException | ObjectNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Inform data import progress 
	 * Uniform for all data import
	 * Initially sets process name and the elapsator
	 * @param data
	 * @return
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public AsyncInformDTO dataImportProgress(AsyncInformDTO data) throws JsonMappingException, JsonProcessingException {
		data.setCompleted(!hasDataImportThread());
		String processName=readAsyncContext(PROCESS_NAME);
		data.setProcessName(processName);
		data.setStartedAt(LocalDateTime.now());
		Duration duration = Duration.between(data.getStartedAt(), LocalDateTime.now());
		data.setDuration(duration.getSeconds());
		String elapsator=messages.get("elapsed") + " " + duration.getSeconds() + " " + messages.get("logoff.soon.2");
		data.setElapsedMessage(elapsator);
		switch(processName) {
		case AsyncService.PROCESS_IMPORT_ADMIN_UNITS:
			data=adminUnit.calcProgress(data);
			break;
		case AsyncService.PROCESS_DWH_UPDATE:
			data=dwhServ.calcProgress(data);
			break;
		case AsyncService.PROCESS_REASSIGN_APPLICANT:
			data=reassignUserServ.asyncInform();
			break;
		case AsyncService.PROCESS_IMPORT_ATC:
			data = importAtc.calcProgress(data);
			break;
		case AsyncService.PROCESS_IMPORT_LEGACYDATA:
			data = importLegacyServ.calcProgress(data);
			break;
		case AsyncService.PROCESS_IMPORT_WF:
			data = importwfServ.calcProgress(data);
			break;
			
			//TODO the rest of processes
		}

		return data;
	}
	/**
	 * Set unified concurrent process error
	 * @param data
	 * @return
	 */
	public AllowValidation concurrentError(AllowValidation data) {
		data.addError(messages.get("errorprocessisrunning"));
		return data;
	}



}
