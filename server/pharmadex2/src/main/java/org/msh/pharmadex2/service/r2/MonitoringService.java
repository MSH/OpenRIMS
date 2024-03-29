package org.msh.pharmadex2.service.r2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.buf.StringUtils;
import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.old.User;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.ApplicationsDTO;
import org.msh.pharmadex2.dto.UserAccessDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
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
	private ClosureService closureServ;
	@Autowired
	private DictService dictServ;
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
	public ApplicationsDTO myMonitoring(ApplicationsDTO data, UserDetailsDTO user, String typeTable, String searchStr) throws ObjectNotFoundException {
		if(accServ.isSupervisor(user)) {
			data = supervisor(data, user, typeTable, searchStr);
			return data;
		}
		if(accServ.isModerator(user) || accServ.isSecretary(user)) {
			data = moderator(data, user, typeTable, searchStr);
			return data;
		}
		if(accServ.isApplicant(user)) {
			data = applicant(data, user, typeTable, searchStr);
		}
		return data;
	}

	/**
	 * Load data to Monitoring/Fullsearch page
	 * @param data
	 * @param user
	 * @param searchStr
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ApplicationsDTO monitoringFullSearch(ApplicationsDTO data, UserDetailsDTO user, String searchStr) throws ObjectNotFoundException {
		//if(accServ.isSupervisor(user) || accServ.isSecretary(user) || accServ.isModerator(user) || accServ.isApplicant(user)) {
		if(true) {
			setDateActual(data);
			setTypes(data);
			setStates(data);

			TableQtb table = data.getFullsearch();

			if (table.getHeaders().getHeaders().size() == 0) {
				table.getHeaders().getHeaders().addAll(fullHeaders());
				table.setGeneralSearch(searchStr);
			}

			boolean emptyTable = false;
			if(table.getGeneralSearch().length() <= 2 && table.getOtherSearch().length() <= 2 && 
					data.getProd_app_type().getValue().getId() == 0 && data.getState().getValue().getId() == 0) {
				emptyTable = true;
			}

			String mainWhere = "";
			String search = "";
			String ownerSearch = "";

			if(emptyTable) {
				mainWhere = " false";
				search = "";
				ownerSearch = "";
			}else {
				mainWhere = "";

				if(table.getGeneralSearch().length() > 2) {
					search = table.getGeneralSearch();
				}else {
					search = "";
				}
				if(table.getOtherSearch().length() > 2) {
					ownerSearch = table.getOtherSearch();
				}else {
					ownerSearch = "";
				}
				if(data.getProd_app_type().getValue() != null && data.getProd_app_type().getValue().getId() > 0){
					mainWhere = "m.dictItemID=" + data.getProd_app_type().getValue().getId();
				}
				if(data.getState().getValue() != null && data.getState().getValue().getId() > 0) {
					mainWhere += (mainWhere.isEmpty()?"":" and ") + "m.state like '" + data.getState().getValue().getCode() + "'";
				}
			}

			String select = "";
			if(accServ.isApplicant(user)) {
				jdbcRepo.monitorfullapplicant(user.getEmail(), search, ownerSearch);
				select = "select * from _monitorfullapplicant as m";
			}else {
				jdbcRepo.monitoringFull(user.getEmail(), search, ownerSearch);
				select = "select * from _monitoringfull as m";
			}
			if(accServ.isSupervisor(user) || accServ.isSecretary(user) || accServ.isModerator(user) || accServ.isApplicant(user)) {
			List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", mainWhere, table.getHeaders());
			TableQtb.tablePage(rows, table);
			table = boilerServ.translateRows(table);
			table.setSelectable(true);
			data.setFullsearch(table);
			}
		}
		

		return data;
	}

	private void setDateActual(ApplicationsDTO data) {
		try {
			String select ="SELECT CompletedAt FROM reportsession where Actual='1'";
			Headers ret = new Headers();
			ret.getHeaders().add(TableHeader.instanceOf("CompletedAt","CompletedAt",false,false,false,TableHeader.COLUMN_LOCALDATETIME,0));
			List<TableRow> dateActual = jdbcRepo.qtbGroupReport(select, "", "", ret);
			if(dateActual.size() > 0) {
				LocalDateTime date = (LocalDateTime) dateActual.get(0).getCell("CompletedAt", ret).getOriginalValue();
				data.getDateactual().setValue(date);
				data.getDateactual().setReadOnly(true);
			}
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void setTypes(ApplicationsDTO data) {
		try {
			if(data.getProd_app_type().getValue() == null) {
				Concept root = closureServ.loadRoot("dictionary.guest.applications");
				List<OptionDTO> firstLevel = dictServ.loadLevelAsOptions(root);
				OptionDTO ret = new OptionDTO();

				ret.getOptions().addAll(firstLevel);

				FormFieldDTO<OptionDTO> fld = FormFieldDTO.of(ret);
				data.setProd_app_type(fld);
			}
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void setStates(ApplicationsDTO data) {
		if(data.getState().getValue() == null) {
			OptionDTO ret = new OptionDTO();

			OptionDTO st = new OptionDTO();
			st.setId(1);
			st.setCode(SystemService.STATE_ACTIVE);
			ret.getOptions().add(st);

			st = new OptionDTO();
			st.setId(2);
			st.setCode(SystemService.STATE_ONAPPROVAL);
			ret.getOptions().add(st);

			st = new OptionDTO();
			st.setId(3);
			st.setCode(SystemService.STATE_REVOKED);
			ret.getOptions().add(st);

			st = new OptionDTO();
			st.setId(4);
			st.setCode(SystemService.STATE_LOST);
			ret.getOptions().add(st);

			st = new OptionDTO();
			st.setId(5);
			st.setCode(SystemService.STATE_DEREGISTERED);
			ret.getOptions().add(st);

			FormFieldDTO<OptionDTO> fld = FormFieldDTO.of(ret);
			data.setState(fld);
		}
	}

	/**
	 * Only processing by the executor
	 * * probably dead 18/07/2023 ik
	 * @param data
	 * @param executor
	 * @return
	 */
	private ApplicationsDTO otherExecutors(ApplicationsDTO data, UserDetailsDTO executor, String typeTable, String searchStr) {
		if(typeTable.equals("actual")) {
			if(!data.getTable().hasHeaders()) {
				data.getTable().setHeaders(applicantHeaders(data.getTable().getHeaders()));
				// только когда снова создаем заголовки, только тогда проверяем был ли текст в строке поиска
				if(searchStr != null && !searchStr.equals("null") && searchStr.length() > 2) {
					for(TableHeader th:data.getTable().getHeaders().getHeaders()) {
						th.setGeneralCondition(searchStr);
					}
					data.getTable().setGeneralSearch(searchStr);
				}
			}
			String where = "executor='"+executor.getEmail()+"'" + " and officeID is not null";
			data = loadTableActual(where, data);
		}else if(typeTable.equals("scheduled")) {
			if(!data.getScheduled().hasHeaders()) {
				data.getScheduled().setHeaders(applicantHeaders(data.getScheduled().getHeaders()));
				// только когда снова создаем заголовки, только тогда проверяем был ли текст в строке поиска
				if(searchStr != null && !searchStr.equals("null") && searchStr.length() > 2) {
					for(TableHeader th:data.getScheduled().getHeaders().getHeaders()) {
						th.setGeneralCondition(searchStr);
					}
					data.getScheduled().setGeneralSearch(searchStr);
				}
			}
			String where = "executor='"+executor.getEmail()+"'" + " and officeID is not null";
			data = loadTableScheduler(where, data);
		}else if(typeTable.equals("fullsearch")) {

		}

		return data;
	}

	/**
	 * Activities running for applicant's applications
	 * @param data
	 * @param applicant
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private ApplicationsDTO applicant(ApplicationsDTO data, UserDetailsDTO user, String typeTable, String searchStr) throws ObjectNotFoundException {
		String email=user.getEmail();
		// my access
		UserAccessDTO ua = userServ.nraUserAccess(email);
		// scheduled or current?
		TableQtb table=new TableQtb();
		String interval="scheduled<'"+boilerServ.isoDate(LocalDate.now().plusDays(2)) + "'";
		if(typeTable.equals("scheduled")) {
			interval="scheduled>'"+boilerServ.isoDate(LocalDate.now())+"'";
			table=data.getScheduled();
		}else {
			table=data.getTable();
		}
		//do we need headers?
		if(!table.hasHeaders()) {
			table.setHeaders(supervisorHeaders(table.getHeaders(), !accServ.isApplicant(user)));
			// when creating a header, check if there is text in the search
			if(searchStr != null && !searchStr.equals("null") && searchStr.length() > 2) {
				for(TableHeader th:table.getHeaders().getHeaders()) {
					th.setGeneralCondition(searchStr);
				}
				table.setGeneralSearch(searchStr);
			}
		}
		//ask for activities
		String select = "select distinct " + table.getHeaders().listKeys()
				+ " from new_monitoring";
		String where="Lang='"+ LocaleContextHolder.getLocale().toString().toUpperCase() +"'"
				+ " and "+interval
				+ " and Owner='"+email + "'";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, table.getHeaders());
		TableQtb.tablePage(rows, table);
		table = boilerServ.translateRows(table);
		table.setSelectable(true);
		data.setTable(table);
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
	private ApplicationsDTO moderator(ApplicationsDTO data, UserDetailsDTO user, String typeTable, String searchStr) throws ObjectNotFoundException {
		String email=user.getEmail();
		// my access
		UserAccessDTO ua = userServ.nraUserAccess(email);
		// scheduled or current?
		TableQtb table=new TableQtb();
		String interval="scheduled<'"+boilerServ.isoDate(LocalDate.now().plusDays(2)) + "'";
		if(typeTable.equals("scheduled")) {
			interval="scheduled>'"+boilerServ.isoDate(LocalDate.now())+"'";
			table=data.getScheduled();
		}else {
			table=data.getTable();
		}
		//do we need headers?
		if(!table.hasHeaders()) {
			table.setHeaders(supervisorHeaders(table.getHeaders(), !accServ.isApplicant(user)));
			// when creating a header, check if there is text in the search
			if(searchStr != null && !searchStr.equals("null") && searchStr.length() > 2) {
				for(TableHeader th:table.getHeaders().getHeaders()) {
					th.setGeneralCondition(searchStr);
				}
				table.setGeneralSearch(searchStr);
			}
		}
		//ask for activities
		String select = "select distinct " + table.getHeaders().listKeys()
				+ " from new_monitoring";
		String where="Lang='"+ LocaleContextHolder.getLocale().toString().toUpperCase() +"'"
				+ " and "+interval
				+ " and applDictID in (" + ua.applAccess() + ")"
				+ " and organizationID='"+ua.getOfficeID()+"'";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, table.getHeaders());
		TableQtb.tablePage(rows, table);
		table = boilerServ.translateRows(table);
		table.setSelectable(true);
		data.setTable(table);
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
	private ApplicationsDTO supervisor(ApplicationsDTO data, UserDetailsDTO user, String typeTable, String searchStr) throws ObjectNotFoundException {
		String email=user.getEmail();
		// my access
		UserAccessDTO ua = userServ.nraUserAccess(email);
		// scheduled or current?
		TableQtb table=new TableQtb();
		String interval="scheduled<'"+boilerServ.isoDate(LocalDate.now().plusDays(2)) + "'";
		if(typeTable.equals("scheduled")) {
			interval="scheduled>'"+boilerServ.isoDate(LocalDate.now())+"'";
			table=data.getScheduled();
		}else {
			table=data.getTable();
		}
		//do we need headers?
		if(!table.hasHeaders()) {
			table.setHeaders(supervisorHeaders(table.getHeaders(), !accServ.isApplicant(user)));
			// when creating a header, check if there is text in the search
			if(searchStr != null && !searchStr.equals("null") && searchStr.length() > 2) {
				for(TableHeader th:table.getHeaders().getHeaders()) {
					th.setGeneralCondition(searchStr);
				}
				table.setGeneralSearch(searchStr);
			}
		}
		//ask for activities
		String select = "select distinct " + table.getHeaders().listKeys()
				+ " from new_monitoring";
		String where="Lang='"+ LocaleContextHolder.getLocale().toString().toUpperCase() +"'"
				+ " and "+interval
				+ " and applDictID in (" + ua.applAccess() + ")";
		// supervisor for local office
		if(!ua.isMainOffice()) {
			where=where + " and organizationID='"+ua.getOfficeID()+"'";
		}
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, table.getHeaders());
		TableQtb.tablePage(rows, table);
		table = boilerServ.translateRows(table);
		table.setSelectable(true);
		data.setTable(table);
		return data;
	}


	private void fullSearch_proc(ApplicationsDTO data, String searchStr) throws ObjectNotFoundException {
		if(!data.getFullsearch().hasHeaders()) {
			data.getFullsearch().setHeaders(fullSearchHeaders(data.getFullsearch().getHeaders()));
			// только когда снова создаем заголовки, только тогда проверяем был ли текст в строке поиска
			if(searchStr != null && !searchStr.equals("null") && searchStr.length() > 2) {
				for(TableHeader th:data.getFullsearch().getHeaders().getHeaders()) {
					th.setGeneralCondition(searchStr);
				}
				data.getFullsearch().setGeneralSearch(searchStr);
			}
		}
		String select="SELECT * FROM monitoring_all";
		List<TableRow> fullsearch = jdbcRepo.qtbGroupReport(select,"", "", data.getFullsearch().getHeaders());
		TableQtb.tablePage(fullsearch, data.getFullsearch());
		data.getFullsearch().setSelectable(false);
		select ="SELECT CompletedAt FROM reportsession where Actual='1'";
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
	 * @param where
	 * @param data
	 * @return
	 */
	@Transactional
	private ApplicationsDTO loadTableActual(String where, ApplicationsDTO data) {
		String currWhere = where +" and scheduled<=curdate() + Interval 1 day";
		String select = "select max(ID) as 'ID', scheduled, prefLabel, process, activity, group_concat(distinct executor) as 'executor', office from in_activities";
		String groupby = "group by scheduled,prefLabel, process, activity, office";
		List<TableRow> current = jdbcRepo.qtbGroupReport(select, groupby, currWhere, data.getTable().getHeaders());
		TableQtb.tablePage(current, data.getTable());
		data.getTable().setSelectable(false);
		return data;
	}

	/**
	 * Load current and scheduled tables.
	 * It's assumed that in_activities has been executed and headers have been built
	 * @param where
	 * @param data
	 * @return
	 */
	@Transactional
	private ApplicationsDTO loadTableScheduler(String where, ApplicationsDTO data) {
		String schedWhere= where+ " and scheduled>curdate()";
		String select = "select max(ID) as 'ID', scheduled, prefLabel, process, activity, group_concat(distinct executor) as 'executor', office from in_activities";
		String groupby = "group by scheduled,prefLabel, process, activity, office";
		List<TableRow> sched = jdbcRepo.qtbGroupReport(select, groupby, schedWhere, data.getScheduled().getHeaders());
		TableQtb.tablePage(sched, data.getScheduled());
		data.getScheduled().setSelectable(false);
		return data;
	}

	/**
	 * Load current tables.
	 * It's assumed that in_activities has been executed and headers have been built
	 *  * * probably dead 18/07/2023 ik
	 * @param where String where
	 * @param data
	 * @return
	 */
	@Transactional
	private ApplicationsDTO loadTableActualApplicant(String where , ApplicationsDTO data) {
		String currWhere = where +" and scheduled<=curdate() + Interval 1 day";
		String select = "select max(ID) as 'ID', max(scheduled) as 'scheduled', prefLabel, process, activity from in_activities";
		String groupby = "group by prefLabel, process, activity";
		List<TableRow> current = jdbcRepo.qtbGroupReport(select, groupby, currWhere, data.getTable().getHeaders());
		TableQtb.tablePage(current, data.getTable());
		data.getTable().setSelectable(false);
		return data;
	}

	/**
	 * Load scheduled tables.
	 * It's assumed that in_activities has been executed and headers have been built
	 * @param where String where
	 * @param data
	 * @return
	 */
	@Transactional
	private ApplicationsDTO loadTableSchedulerApplicant(String where , ApplicationsDTO data) {
		String schedWhere = where+ " and scheduled>curdate()";
		String select = "select max(ID) as 'ID', max(scheduled) as 'scheduled', prefLabel, process, activity from in_activities";
		String groupby = "group by prefLabel, process, activity";
		List<TableRow> sched = jdbcRepo.qtbGroupReport(select, groupby, schedWhere, data.getScheduled().getHeaders());
		TableQtb.tablePage(sched, data.getScheduled());
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
	private Headers supervisorHeaders(Headers headers, boolean showExecutor) {
		headers.getHeaders().clear();
		headers.getHeaders().add(TableHeader.instanceOf(
				"ID", 
				"references",
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				15));
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
				"Owner",
				"sender_email",
				false,
				true,
				true,
				TableHeader.COLUMN_STRING,
				20));
		if(showExecutor) {
			headers.getHeaders().add(TableHeader.instanceOf(
					"executor",
					"employees",
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
		}
		headers.setPageSize(20);
		boilerServ.translateHeaders(headers);
		headers.getHeaders().get(1).setSortValue(TableHeader.SORT_ASC);
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
	/**
	 * GEt the latest history ID by the application data ID
	 * @param applID
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Long convinientHistory(Long applID) throws ObjectNotFoundException {
		Concept applData = closureServ.loadConceptById(applID);
		List<History> hlist = boilerServ.historyAllOrderByCome(applData);
		return hlist.get(hlist.size()-1).getID();
	}

	private List<TableHeader> fullHeaders() {
		List<TableHeader> headers = new ArrayList<TableHeader>();
		headers.add(TableHeader.instanceOf(
				"workflow",
				"prod_app_type",
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				20));
		headers.add(TableHeader.instanceOf(
				"pref",
				"prefLabel",
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				40));
		headers.add(TableHeader.instanceOf(
				"owner",
				"owners",
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				20));
		headers.add(TableHeader.instanceOf(
				"state",
				"state",
				true,
				false,
				false,
				TableHeader.COLUMN_STRING,
				20));
		headers = boilerServ.translateHeadersList(headers);
		headers.get(0).setSortValue(TableHeader.SORT_DESC);
		return headers;
	}

}
