package org.msh.pharmadex2.service.r2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.ImportLocalesDTO;
import org.msh.pharmadex2.dto.ReassignUserDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.log.EventLogDTO;
import org.msh.pharmadex2.dto.log.ImportLanguageDTO;
import org.msh.pharmadex2.dto.log.ReassignUserLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Logger built on concepts using the common data structure
 * Highly specialized, because we don't need to share it as a separate product
 * loggerURL->responsibilities->records
 * @author alexk
 *
 */
@Service
@Transactional
public class LoggerEventService {
	private static final String LOG_LANGUAGE_REPLACE = "log.language.replace";
	private static final String LOG_USER_REASSIGN = "log.user.reassign";
	private static final Logger logger = LoggerFactory.getLogger(LoggerEventService.class);
	@Autowired
	private ClosureService  closureServ;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private Messages messages;
	@Autowired
	private JdbcRepository jdbcRepo;
	
	/**
	 * Reassign processes, activities and data belongs to emailFrom to emailTo
	 * @param emailExec Supervisor that asks for reassign
	 * @param emailFrom
	 * @param emailTo
	 * @param completed how many reassigned to emailTo
	 * @param total how many left under emailFrom
	 * @param description human readable descriptin
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public void applicantReassignEvent(String emailExec, String emailFrom, String emailTo
			, long completed, long total, String description) throws ObjectNotFoundException {
		Concept email = rootForRecord(LOG_USER_REASSIGN,emailExec);
		//add a record
		Concept record=new Concept();
		record.setIdentifier(LocalDateTime.now().toString());
		ReassignUserLog rec = new ReassignUserLog();
		rec.setDescription(description);
		rec.setEmailFrom(emailFrom);
		rec.setEmailTo(emailTo);
		rec.setLeft(total-completed);
		rec.setReassigned(completed);
		try {
			record.setLabel(objectMapper.writeValueAsString(rec));
			record=closureServ.saveToTree(email, record);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
			throw new ObjectNotFoundException(e.getMessage());
		}
	}
	/**
	 * Get root concept to add a log concept
	 * @param logRoot
	 * @param emailExec
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public Concept rootForRecord(String logRoot, String emailExec) throws ObjectNotFoundException {
		//build or get a tree
		Concept root=closureServ.loadRoot(logRoot);
		Concept email=new Concept();
		email.setIdentifier(emailExec);
		email=closureServ.saveToTree(root, email);
		return email;
	}
	public List<TableHeader> userReassignHeaders() {
		List<TableHeader> ret = new ArrayList<TableHeader>();
		ret.add(TableHeader.instanceOf(
				"eventDate",
				messages.get("global_date"),
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATETIME,
				0));
		ret.add(TableHeader.instanceOf(
				"executor",
				messages.get("employee"),
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		ret.add(TableHeader.instanceOf(
				"emailFrom",
				messages.get("applicant_gmail"),
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		ret.add(TableHeader.instanceOf(
				"emailTo",
				messages.get("reassignTo"),
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		ret.add(TableHeader.instanceOf(
				"quantity",
				messages.get("global_quantity"),
				true,
				true,
				true,
				TableHeader.COLUMN_LONG,
				0));
		ret.add(TableHeader.instanceOf(
				"description",
				messages.get("description"),
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		ret.get(0).setSort(true);
		ret.get(0).setSortValue(TableHeader.SORT_DESC);
		return ret;
	}
	/**
	 * Log import language event
	 * @param logDTO
	 * @param user
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public void importLanguageEvent(ImportLanguageDTO logDTO, UserDetailsDTO user) throws ObjectNotFoundException {
		Concept email = rootForRecord(LOG_LANGUAGE_REPLACE,user.getEmail());
		//add a record
		Concept record=new Concept();
		record.setIdentifier(LocalDateTime.now().toString());
		try {
			record.setLabel(objectMapper.writeValueAsString(logDTO));
			record=closureServ.saveToTree(email, record);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
			throw new ObjectNotFoundException(e.getMessage());
		}
	}
	/**
	 * Previous reassign actions
	 * @param data
	 * @return
	 */
	public ReassignUserDTO userReassignLog(ReassignUserDTO data) {
		if(!data.getEventLog().hasHeaders()) {
			data.getEventLog().getHeaders().getHeaders().clear();
			data.getEventLog().getHeaders().getHeaders().addAll(userReassignHeaders());
		}
		jdbcRepo.userReassignLog(data.getEventLog());
		data.getEventLog().setSelectable(false);
		return data;
	}
	/**
	 * Get import locales log
	 * @param data
	 * @return
	 */
	public EventLogDTO importLocalesLog(EventLogDTO data) {
		data.setTitle(messages.get("importLocales"));
		if(data.getEventLog().getHeaders().getHeaders().isEmpty()) {
			data.getEventLog().getHeaders().getHeaders().addAll(importLocalesHeaders());
		}
		jdbcRepo.importLocalesLog(data.getEventLog());
		data.getEventLog().setSelectable(false);
		return data;
	}
	private List<TableHeader> importLocalesHeaders() {
		List<TableHeader> ret = new ArrayList<TableHeader>();
		ret.add(TableHeader.instanceOf(
				"eventDate",
				messages.get("global_date"),
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATETIME,
				0));
		ret.add(TableHeader.instanceOf(
				"executor",
				messages.get("employee"),
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		ret.add(TableHeader.instanceOf(
				"oldLanguage",
				messages.get("oldLanguage"),
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		ret.add(TableHeader.instanceOf(
				"newLanguage",
				messages.get("newLanguage"),
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		ret.add(TableHeader.instanceOf(
				"total",
				messages.get("total"),
				true,
				true,
				true,
				TableHeader.COLUMN_LONG,
				0));
		ret.add(TableHeader.instanceOf(
				"imported",
				messages.get("imported"),
				true,
				true,
				true,
				TableHeader.COLUMN_LONG,
				0));
		ret.add(TableHeader.instanceOf(
				"updated",
				messages.get("updated"),
				true,
				true,
				true,
				TableHeader.COLUMN_LONG,
				0));
		ret.get(0).setSort(true);
		ret.get(0).setSortValue(TableHeader.SORT_DESC);
		return ret;
	}

}
