package org.msh.pharmadex2.service.r2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.old.User;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pharmadex2.dto.ApplicationsDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * All monitoring and superving related methods
 * @author alexk
 *
 */
@Service
public class MonitoringService {
	@Autowired
	private AccessControlService accServ;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private UserService userServ;
	

	/**
	 * The same as my activities, however only monitoring activities
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ApplicationsDTO myMonitoring(ApplicationsDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		jdbcRepo.in_activities();
		if(accServ.isSupervisor(user) || accServ.isSecretary(user)) {
			data=supervisor(data,user);
			return data;
		}
		if(accServ.isModerator(user)) {
			data=moderator(data,user);
			return data;
		}
		if(accServ.isApplicant(user)) {
			data=applicant(data,user);
			return data;
		}
		//others NMRA employee
		data=otherExecutors(data,user);
		return data;
	}
	/**
	 * Only processing by the executor
	 * @param data
	 * @param executor
	 * @return
	 */
	private ApplicationsDTO otherExecutors(ApplicationsDTO data, UserDetailsDTO executor) {
		if(!data.getTable().hasHeaders()) {
			data.getTable().setHeaders(applicantHeaders(data.getTable().getHeaders()));
		}
		if(!data.getScheduled().hasHeaders()) {
			data.getScheduled().setHeaders(applicantHeaders(data.getScheduled().getHeaders()));
		}
		String where = "executor='"+executor.getEmail()+"'" + " and officeID is not null";
		data = loadTablesApplicant(where, data);
		return data;
	}

	/**
	 * Only initiated by the applicant
	 * @param data
	 * @param applicant
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ApplicationsDTO applicant(ApplicationsDTO data, UserDetailsDTO applicant) throws ObjectNotFoundException {
		if(!data.getTable().hasHeaders()) {
			data.getTable().setHeaders(applicantHeaders(data.getTable().getHeaders()));
		}
		if(!data.getScheduled().hasHeaders()) {
			data.getScheduled().setHeaders(applicantHeaders(data.getScheduled().getHeaders()));
		}
		
		String where  = "applicant='"+applicant.getEmail()+"'" + " and officeID is not null";
		data = loadTablesApplicant(where, data);
		data.getTable().setSelectable(true);
		//fullsearch
				jdbcRepo.monitoring_all(applicant.getEmail(), null,null);
				fullSearch_proc(data);
		data.getFullsearch().setSelectable(true);
		return data;
	}
	/**
	 * The current and scheduled activities available for a moderator user
	 * @param data
	 * @param user
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ApplicationsDTO moderator(ApplicationsDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		User u = userServ.findByEmail(user.getEmail());
		if(!data.getTable().hasHeaders()) {
			data.getTable().setHeaders(supervisorHeaders(data.getTable().getHeaders()));
		}
		if(!data.getScheduled().hasHeaders()) {
			data.getScheduled().setHeaders(supervisorHeaders(data.getScheduled().getHeaders()));
		}
		List<Long> resp= userServ.responsibilities(u);
		if(accServ.isTerritoryUser(user)) {
			data=moderatorTerritory(u, resp, data);
		}else {
			data=moderatorCountry(u, resp, data);
		}
		//fullsearch
				if(accServ.isTerritoryUser(user)) {
					jdbcRepo.monitoring_all(null, user.getEmail(), user.getEmail());
				}else {
					jdbcRepo.monitoring_all(null, user.getEmail(), null);
				}
				fullSearch_proc(data);
				data.getFullsearch().setSelectable(true);
		return data;
	}
	/**
	 * Moderator for whole country. Limited only by the responsibility
	 * @param moderator 
	 * @param resp
	 * @param data
	 * @return
	 */
	private ApplicationsDTO moderatorCountry(User moderator, List<Long> resp, ApplicationsDTO data) {
		List<String> resps = new ArrayList<String>();
		for(Long r : resp) {
			resps.add(r+"");
		}
		String where = "adict in ("+
				String.join(",",resps)
		+")";
		data=loadTables(where, data);
		return data;
	}

	/**
	 * Select all activities by responsibility-office
	 * It is presumed that the in_activities is ready and headers have been built
	 * @param moderator
	 * @param resp 
	 * @param data
	 * @return
	 */
	@Transactional
	private ApplicationsDTO moderatorTerritory(User moderator, List<Long> resp, ApplicationsDTO data) {
		String where = "officeID='"+moderator.getOrganization().getID()+"'";
		List<String> resps = new ArrayList<String>();
		for(Long r : resp) {
			resps.add(r+"");
		}
		where = where + " and adict in ("+
				String.join(",",resps)
		+")";
		data=loadTables(where, data);
		return data;
	}
	/**
	 * The current and scheduled monitoring available to the supervisor user
	 * It is presumed that the in_activities is ready
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ApplicationsDTO supervisor(ApplicationsDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		if(!data.getTable().hasHeaders()) {
			data.getTable().setHeaders(supervisorHeaders(data.getTable().getHeaders()));
		}
		if(!data.getScheduled().hasHeaders()) {
			data.getScheduled().setHeaders(supervisorHeaders(data.getScheduled().getHeaders()));
		}
		
		String where = "supervisor='"+user.getEmail()+"'";
		data = loadTables(where, data);
		data.getTable().setSelectable(true);
		data.getScheduled().setSelectable(true);
		//fullsearch
		if(accServ.isTerritoryUser(user)) {
			jdbcRepo.monitoring_all(null, null, user.getEmail());
		}else {
			jdbcRepo.monitoring_all(null, null, null);
		}
		fullSearch_proc(data);
		data.getFullsearch().setSelectable(true);
		return data;
	}
	
	private void fullSearch_proc(ApplicationsDTO data) throws ObjectNotFoundException {
		if(!data.getFullsearch().hasHeaders()) {
			data.getFullsearch().setHeaders(fullSearchHeaders(data.getFullsearch().getHeaders()));
		}
		String select="SELECT * FROM pdx2.monitoring_all";
		List<TableRow> fullsearch = jdbcRepo.qtbGroupReport(select,"", "", data.getFullsearch().getHeaders());
		TableQtb.tablePage(fullsearch, data.getFullsearch());
		data.getFullsearch().setSelectable(false);
		select ="SELECT CompletedAt FROM pdx2.reportsession where Actual='1'";
		Headers ret = new Headers();
		ret.getHeaders().add(TableHeader.instanceOf("CompletedAt","CompletedAt",false,false,false,TableHeader.COLUMN_LOCALDATETIME,0));
		List<TableRow> dateActual= jdbcRepo.qtbGroupReport(select, "", "", ret);
		if(dateActual.size()>0) {
			LocalDateTime date=(LocalDateTime) dateActual.get(0).getCell("CompletedAt", ret).getOriginalValue();
			data.getDateactual().setValue(date);
			data.getDateactual().setReadOnly(true);
		}
	}
	/**
	 * Load current and scheduled tables.
	 * It's assumed that in_activities has been executed and headers have been built
	 * @param where
	 * @param data
	 * @return
	 */
	@Transactional
	private ApplicationsDTO loadTables(String where, ApplicationsDTO data) {
		String currWhere = where +" and scheduled<=curdate() + Interval 1 day";
		String schedWhere= where+ " and scheduled>curdate()";
		String select = "select max(ID) as 'ID', scheduled, prefLabel, process, activity, group_concat(distinct executor) as 'executor', office from in_activities";
		String groupby = "group by scheduled,prefLabel, process, activity, office";
		List<TableRow> current = jdbcRepo.qtbGroupReport(select, groupby, currWhere, data.getTable().getHeaders());
		List<TableRow> sched = jdbcRepo.qtbGroupReport(select, groupby, schedWhere, data.getScheduled().getHeaders());
		TableQtb.tablePage(current, data.getTable());
		TableQtb.tablePage(sched, data.getScheduled());
		data.getTable().setSelectable(false);
		data.getScheduled().setSelectable(false);
		return data;
	}

	/**
	 * Load current and scheduled tables.
	 * It's assumed that in_activities has been executed and headers have been built
	 * @param where String where
	 * @param data
	 * @return
	 */
	@Transactional
	private ApplicationsDTO loadTablesApplicant(String where , ApplicationsDTO data) {
		String currWhere = where +" and scheduled<=curdate() + Interval 1 day";
		String schedWhere= where+ " and scheduled>curdate()";
		//String select = "select max(ID) as 'ID', scheduled, prefLabel, process, activity from in_activities";
		String select = "select max(ID) as 'ID', max(scheduled) as 'scheduled', prefLabel, process, activity from in_activities";
		String groupby = "group by prefLabel, process, activity";
		List<TableRow> current = jdbcRepo.qtbGroupReport(select, groupby, currWhere, data.getTable().getHeaders());
		List<TableRow> sched = jdbcRepo.qtbGroupReport(select, groupby, schedWhere, data.getScheduled().getHeaders());
		TableQtb.tablePage(current, data.getTable());
		TableQtb.tablePage(sched, data.getScheduled());
		data.getTable().setSelectable(false);
		data.getScheduled().setSelectable(false);
		return data;
	}
	
	
	private Headers fullSearchHeaders(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"application", 
				"prod_app_type",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				15));
		headers.getHeaders().add(TableHeader.instanceOf(
				"prefLabel",
				"prefLabel",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				30));
		headers.getHeaders().add(TableHeader.instanceOf(
				"owner",
				"owners",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				20));
		headers.getHeaders().add(TableHeader.instanceOf(
				"state",
				"state",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				20));
		headers.getHeaders().add(TableHeader.instanceOf(
				"regNo",
				"reg_number",
				false,
				true,
				true,
				TableHeader.COLUMN_STRING,
				20));
		headers.getHeaders().add(TableHeader.instanceOf(
				"regDate",
				"registration_date",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				30));
		headers.getHeaders().add(TableHeader.instanceOf(
				"expDate",
				"expiry_date",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				30));
		headers.getHeaders().add(TableHeader.instanceOf(
				"address",
				"address",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				30));
		headers.setPageSize(20);
		boilerServ.translateHeaders(headers);
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		return headers;
	}
	
	/**
	 * Headers for the supervisor monitoring tables 
	 * @param headers
	 * @return
	 */
	private Headers supervisorHeaders(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"scheduled", 
				"scheduled",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				15));
		headers.getHeaders().add(TableHeader.instanceOf(
				"prefLabel",
				"prefLabel",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				30));
		headers.getHeaders().add(TableHeader.instanceOf(
				"process",
				"manageapplications",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				20));
		headers.getHeaders().add(TableHeader.instanceOf(
				"activity",
				"activity",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				20));
		headers.getHeaders().add(TableHeader.instanceOf(
				"executor",
				"executor_email",
				false,
				true,
				true,
				TableHeader.COLUMN_STRING,
				20));
		headers.getHeaders().add(TableHeader.instanceOf(
				"office",
				"departmentbranch",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				30));
		headers.setPageSize(20);
		boilerServ.translateHeaders(headers);
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		return headers;
	}

	/**
	 * Headers for the supervisor monitoring tables 
	 * @param headers
	 * @return
	 */
	private Headers applicantHeaders(Headers headers) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"scheduled", 
				"scheduled",
				true,
				true,
				true,
				TableHeader.COLUMN_LOCALDATE,
				15));
		headers.getHeaders().add(TableHeader.instanceOf(
				"prefLabel",
				"prefLabel",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				30));
		headers.getHeaders().add(TableHeader.instanceOf(
				"process",
				"manageapplications",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				20));
		headers.getHeaders().add(TableHeader.instanceOf(
				"activity",
				"activity",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				20));
		headers.setPageSize(20);
		boilerServ.translateHeaders(headers);
		headers.getHeaders().get(0).setSortValue(TableHeader.SORT_ASC);
		return headers;
	}
	
}
