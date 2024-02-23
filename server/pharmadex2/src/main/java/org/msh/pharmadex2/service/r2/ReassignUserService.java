package org.msh.pharmadex2.service.r2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.AsyncInformDTO;
import org.msh.pharmadex2.dto.ReassignUserDTO;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Reassign GMail for an applicant or tasks for NRA employees
 * This service is stateful and contains 
 * @author alexk
 *
 */
@Service
public class ReassignUserService {
	public static final String ASYNC_INFORM = "asyncInform";
	private static final Logger logger = LoggerFactory.getLogger(ReassignUserService.class);
	private static final String IDENTIFIER = "Identifier";
	@Autowired
	private Messages messages;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private ValidationService validation;
	@Autowired
	private LoggerEventService logEvents;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ClosureService closureServ;

	/**
	 * Load a list of known applicant Gmails that are suit search criteria
	 * An applicant is known, 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ReassignUserDTO applicantSearch(ReassignUserDTO data) throws ObjectNotFoundException {
		data.getReassignTo().clearValidation();
		data.clearErrors();
		if(data.getApplicants().getHeaders().getHeaders().isEmpty()) {
			data.getApplicants().setHeaders(headersApplicants(data.getApplicants().getHeaders()));
		}
		String selected=selectedRowField(data.getApplicants(), IDENTIFIER);
		String searchCond = data.getApplicants().getHeaders().getHeaders().get(0).getGeneralCondition();
		List<TableRow> rows = new ArrayList<TableRow>();
		if(searchCond.length()>=3) {
			rows = jdbcRepo.qtbGroupReport("select * from applicant_emails", "", "", data.getApplicants().getHeaders());
		}
		for(TableRow row : rows) {
			TableCell cell = row.getCellByKey(IDENTIFIER);
			if(cell!=null) {
				row.setSelected(cell.getValue().equalsIgnoreCase(selected));
			}
		}
		TableQtb.tablePage(rows,data.getApplicants());
		return data;
	}
	/**
	 * Headers for applicant's table
	 * @param data
	 */
	public Headers headersApplicants(Headers headers) {
		headers.getHeaders().add(TableHeader.instanceOf(
				IDENTIFIER,
				messages.get("email"),
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		return headers;
	}
	/**
	 * Get details for an applicant
	 * @param data
	 * @return
	 */
	@Transactional
	public ReassignUserDTO applicantDetails(ReassignUserDTO data) {
		String email=selectedRowField(data.getApplicants(),IDENTIFIER);
		if(!email.isEmpty()) {
			jdbcRepo.reassign_details(email);				// build the data for detail tables	
			detailTable("APPLICATION", data.getApplications());
			detailTable("ACTIVITY", data.getActivities());
			detailTable("ACTIVITY_DATA", data.getDataTable());
		}
		return data;
	}



	/**
	 * Build a detail table using result of reassign_details stored procedure and the discriminator
	 * @param discriminator
	 * @param table
	 */
	public void detailTable(String discriminator, TableQtb table) {
		table.setHeaders(headersDetail(table.getHeaders()));
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from reassign_details", "",
				"discriminator='"+discriminator+"'", table.getHeaders());
		TableQtb.tablePage(rows, table);
		table.setSelectable(false);
	}
	/**
	 * Headers for all detail tables are equal
	 * @param headers
	 * @return
	 */
	private Headers headersDetail(Headers headers) {
		if(headers.getHeaders().isEmpty()) {
			headers.getHeaders().add(TableHeader.instanceOf(
					"URL",
					messages.get("URL"),
					true,
					true,
					true,
					TableHeader.COLUMN_STRING,
					0));
			headers.getHeaders().add(TableHeader.instanceOf(
					"quantity",
					messages.get("global_quantity"),
					true,
					true,
					true,
					TableHeader.COLUMN_LONG,
					0));
			headers.getHeaders().get(0).setSort(true);
			headers.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		}
		return headers;
	}
	/**
	 * Take a string value of a cell in the selected row
	 * @param table
	 * @param fieldName
	 * @return empty if not found
	 */
	private String selectedRowField(TableQtb table, String fieldName) {
		String ret="";
		for(TableRow row : table.getRows()) {
			if(row.getSelected()) {
				TableCell cell = row.getCellByKey(fieldName);
				if(cell!=null) {
					ret=cell.getValue();
				}
				break;
			}
		}
		return ret;
	}

	/**
	 * Extract all IDs of concepts to reassign inside data trees
	 * @param emailFrom
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Map<String, List<Long>> toReassign(String emailFrom) throws ObjectNotFoundException {
		Map<String, List<Long>> ret = new HashMap<String, List<Long>>();
		jdbcRepo.to_reassign(emailFrom);
		Headers headers = new Headers();
		headers.getHeaders().add(TableHeader.instanceOf("URL", TableHeader.COLUMN_STRING));
		headers.getHeaders().add(TableHeader.instanceOf("ConceptID", TableHeader.COLUMN_LONG));
		headers.getHeaders().get(0).setSort(true);
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from to_reassign", "","", headers);
		for(TableRow row : rows) {
			String URL = row.getCellByKey("URL").getValue();
			Object conceptIDO = row.getCellByKey("ConceptID").getOriginalValue();
			Long conceptID=0l;
			if(conceptIDO instanceof Long) {
				conceptID=(Long) conceptIDO;
			}else {
				throw new ObjectNotFoundException("toReassign. Something wrong with conceptID in to_reassign. Email is "+emailFrom);
			}
			List<Long> list = ret.get(URL);
			if(list==null) {
				List<Long> l = new ArrayList<Long>();
				l.add(conceptID);
				ret.put(URL, l);
			}else {
				list.add(conceptID);
			}
		}
		return ret;
	}

	/**
	 * Validate emails to reassign
	 * @param data
	 * @return  isValid in the DTO
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ReassignUserDTO validateEmails(ReassignUserDTO data) throws ObjectNotFoundException {
		String emailFrom=selectedRowField(data.getApplicants(), IDENTIFIER);
		String emailTo=data.getReassignTo().getValue();
		//email reassign from should be valid
		if (!validation.eMail(emailFrom)) {
			data.addError(messages.get("valid_email")+" "+emailFrom);
			return data;
		}
		//email reassign from should be an applicant
		if(!isApplicantEmail(emailFrom)) {
			data.addError(messages.get("app_not_verified")+" "+emailFrom);
			return data;
		}
		//email reassign to should be valid
		if (!validation.eMail(emailTo)) {
			data.getReassignTo().invalidate(messages.get("valid_email"));
			data.propagateValidation();
			return data;
		}
		//email reassign to should not belong to any NRA employee
		if(isEmployeeEmail(emailTo)) {
			data.getReassignTo().invalidate(messages.get("employee_not_allowed"));
			data.propagateValidation();
			return data;
		}
		return data;
	}
	/**
	 * Does this email belong employee?
	 * @param email
	 * @return
	 */
	@Transactional
	private boolean isEmployeeEmail(String email) {
		String where = "ConceptID is not null and Email='"+email+"'";
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select Email as 'Identifier' from user", ""
				,where, headersApplicants(new Headers()));
		return !rows.isEmpty();
	}
	/**
	 * Is this email belong to a valid applicant?
	 * Applicant means has something in the database, however does not  belong to employees
	 * @param email
	 * @return
	 */
	@Transactional
	private boolean isApplicantEmail(String email) {
		List<TableRow> rows = jdbcRepo.qtbGroupReport("select * from applicant_emails", ""
				,"Identifier='"+email+"'", headersApplicants(new Headers()));
		return !rows.isEmpty();
	}

	/**
	 * Really reassign the applicant
	 * @param data
	 * @throws JsonProcessingException 
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public void reassignApplicantWorker(ReassignUserDTO data) throws JsonProcessingException, ObjectNotFoundException {
		logger.trace(messages.get("reassignusers")+"<<<");
		String emailFrom=selectedRowField(data.getApplicants(), IDENTIFIER);
		String emailTo=data.getReassignTo().getValue();
		Map<String, List<Long>> toReassign =toReassign(emailFrom);
		int total=0;
		for(String key : toReassign.keySet()) {
			total=total+toReassign.get(key).size();
		}
		AsyncInformDTO info= initProgressData(emailFrom, emailTo, total);
		try {
			for(String key :toReassign.keySet()) {
				Concept root = closureServ.loadRoot(key);
				Concept emailToC=closureServ.saveToTree(root, emailTo);
				for(Long id : toReassign.get(key)) {
					if(!info.isCancelled()) {
						Concept conc=closureServ.loadConceptById(id);
						jdbcRepo.moveSubTree(conc, emailToC);
						info.setCompl(info.getCompl()+1);
						Float percentF = (info.getCompl() * 100.0f) / info.getComplOf();
						int perc=percentF.intValue();
						if(perc>100) perc=100;
						info.setComplPercent(perc);
						info.setProgressMessage(progressMessage(info));
						AsyncService.writeAsyncContext(ASYNC_INFORM, objectMapper.writeValueAsString(info));
					}else {
						break;
					}
				}
			}
		} catch (Exception e) {
			info.addError(e.getMessage());
		}
		if(info.isCancelled()) {
			info.setCompleted(false);
		}else {
			info.setCompleted(true);
		}
		AsyncService.writeAsyncContext(ASYNC_INFORM, objectMapper.writeValueAsString(info));
		try {
			String mess = info.getProgressMessage();
			if(!info.isValid()) {
				mess=info.getIdentifier();
			}
			logEvents.applicantReassignEvent(data.getExecName(), emailFrom, emailTo
					,info.getCompl(), info.getComplOf(), mess);
		} catch (ObjectNotFoundException e) {
			logger.error("applicantReassignRunAsync "+e.getMessage());
		}
		logger.trace(messages.get("reassignusers")+">>>");
	}	
	/**
	 * Create a progress message
	 * @param info
	 * @return
	 */
	private String progressMessage(AsyncInformDTO info) {
		return messages.get("reassigned") + " "+ info.getCompl() + "/"+info.getComplOf() +" ("+info.getComplPercent()+"%)";
	}
	/**
	 * Initialize progress data object and, then, store it into Async context
	 * @param emailFrom
	 * @param emailTo
	 * @param size
	 * @return
	 * @throws JsonProcessingException 
	 */
	private AsyncInformDTO initProgressData(String emailFrom, String emailTo, int size) throws JsonProcessingException {
		AsyncInformDTO ret = new AsyncInformDTO();
		ret.setProcessName(AsyncService.PROCESS_REASSIGN_APPLICANT);
		ret.setTitle(messages.get("reassignusers")+ " " + emailFrom+"->"+emailTo);
		ret.setStartedAt(LocalDateTime.now());
		ret.setComplOf(size);
		ret.setCompleted(false);
		AsyncService.writeAsyncContext(ASYNC_INFORM, objectMapper.writeValueAsString(ret));
		return ret;
	}
	/**
	 * Get async inform
	 * @return real or new created AsyncInformDTO
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public AsyncInformDTO asyncInform() throws JsonMappingException, JsonProcessingException {
		AsyncInformDTO ret = new AsyncInformDTO();
		String ai = AsyncService.readAsyncContext(ASYNC_INFORM);
		if(!ai.isEmpty()) {
			ret = objectMapper.readValue(ai, AsyncInformDTO.class);
		}
		return ret;
	}

}
