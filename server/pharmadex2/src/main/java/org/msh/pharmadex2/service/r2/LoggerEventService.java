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
	public void applicantReassign(String emailExec, String emailFrom, String emailTo
			, long completed, long total, String description) throws ObjectNotFoundException {
		//build or get a tree
		Concept root=closureServ.loadRoot(LOG_USER_REASSIGN);
		Concept email=new Concept();
		email.setIdentifier(emailExec);
		email=closureServ.saveToTree(root, email);
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

}
