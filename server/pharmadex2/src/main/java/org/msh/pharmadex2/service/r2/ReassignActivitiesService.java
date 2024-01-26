package org.msh.pharmadex2.service.r2;

import java.util.ArrayList;
import java.util.List;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.History;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.ReassignActivitiesDTO;
import org.msh.pharmadex2.dto.ReassignUserDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**Reassign selected activities of one employee to another
 * 
 * @author ik
 *
 */
@Service
public class ReassignActivitiesService {
	private static final String IDENTIFIER = "Identifier";
	@Autowired
	private Messages messages;
	@Autowired
	private JdbcRepository jdbcRepo;
	@Autowired
	private ValidationService validation;
	@Autowired
	private BoilerService boilerServ;
	@Autowired
	private ClosureService closureServ;
	/**
	 * Load a list of known employees that are suit search criteria 
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public ReassignActivitiesDTO employeeLoad(ReassignActivitiesDTO data)  {
		data=selectedEmployee(data, data.getEmployee(),data.getSelectedEmployee());
		data=selectedEmployee(data, data.getEmployeeother(),data.getSelectedEmployeeOther());
		if(!data.getSelectedEmployee().isEmpty() && !data.getSelectedEmployeeOther().isEmpty()) {
			if(!data.getSelectedEmployee().equals(data.getSelectedEmployeeOther())) {
				//selected employee1 and employee2
				data.getActivities().getRows().clear();
				availableActivities(data,false);
				data=activitiesEmployees(data,data.getCurrentworkfload(),data.getSelectedEmployeeOther());
				data.setAvailableActivities(false);
			}else {
				//emailEmployee1=emailEmployee2
				data=activitiesEmployees(data,data.getCurrentworkfload(),data.getSelectedEmployeeOther());
				data.setAvailableActivities(true);
				return data;
			}
		}else {
			List<TableRow>rows=data.getAvailable().getRows(); 
			for(TableRow row:rows) {
				row.setSelected(false); 
			} 
			data.getAvailable().setRows(rows);
			data.getPrevSelected().clear();
			data.setSelectedOnly(false);
			if(!data.getSelectedEmployee().isEmpty()) {
				//selected only employee1
				data.getAvailable().getRows().clear();
				data.getCurrentworkfload().getRows().clear();
				data=activitiesEmployees(data,data.getActivities(),data.getSelectedEmployee());
				data.setAvailableActivities(true);
			} 
			if(!data.getSelectedEmployeeOther().isEmpty()) {
				//selected only employee2
				data.getActivities().getRows().clear();
				data.getAvailable().getRows().clear();
				data=activitiesEmployees(data,data.getCurrentworkfload(),data.getSelectedEmployeeOther());
				data.setAvailableActivities(true);
			}
		}
		return data;
	}
	/**
	 * Load a list of  available Activities for employee1 and employee2
	 * @param data
	 * @param all, if selectAll=true, else false
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ReassignActivitiesDTO availableActivities(ReassignActivitiesDTO data, boolean all) {
		TableQtb table=data.getAvailable();
		String person=data.getSelectedEmployee();
		String other=data.getSelectedEmployeeOther();
		table.getRows().clear();
		if (table.getHeaders().getHeaders().size() == 0) {
			table.getHeaders().getHeaders().addAll(activitiesToDoHeaders());
		}
		jdbcRepo.todo(person,null);
		String select = "select * from _todo td";
		String where="td.applDictID in (SELECT ud.conceptID	FROM user u	JOIN userdict ud ON ud.useruserId = u.userId WHERE u.Email='"+other+"')";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, table.getHeaders());
		if(!all) {
			rows=setSelectRow(data, rows);
		}else {
			data.getPrevSelected().clear();
			List<Long> selectedIds= new ArrayList<Long>();
			for(TableRow row:rows) {
				selectedIds.add(row.getDbID());
				row.setSelected(true);
			}
			data.setPrevSelected(selectedIds);
			rows=setSelectRow(data, rows);
		}
		TableQtb.tablePage(rows, table);
		table.setSelectable(true);
		data.setAvailable(table);
		return data;
	}
	/**
	 * set the selected flag in the table rows
	 * @param data
	 * @param rows
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private List<TableRow> setSelectRow(ReassignActivitiesDTO data, List<TableRow> rows) {
		List<Long> selectedIds=data.getPrevSelected();
		if(!selectedIds.isEmpty() || selectedIds.size()!=0) {
			if(data.getAvailable().getHeaders().getHeaders().size()==0) {
			}
			for(TableRow row :rows) {
				if(selectedIds.contains(row.getDbID())) {
					row.setSelected(true);
				}else {
					row.setSelected(false);
				}
			}
		}
		return rows;
	}
	/**
	 * Load a list of  activities for employee1 
	 * @param data
	 * @param table - How to load the table
	 * @param person- email employee (1 or 2)
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private ReassignActivitiesDTO activitiesEmployees(ReassignActivitiesDTO data, TableQtb table, String person) {
		table.getRows().clear();
		if (table.getHeaders().getHeaders().size() == 0) {
			table.getHeaders().getHeaders().addAll(activitiesToDoHeaders());
		}
		jdbcRepo.todo(person,null);
		String select = "select * from _todo";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", "", table.getHeaders());
		TableQtb.tablePage(rows, table);
		table.setSelectable(false);
		return data;
	}
	@Transactional
	private ReassignActivitiesDTO selectedEmployee(ReassignActivitiesDTO data, TableQtb table, String person) {
		if(table.getHeaders().getHeaders().isEmpty()) {
			table.setHeaders(headersEmployees(table.getHeaders()));
		}
		List<TableRow> rows = new ArrayList<TableRow>();
		List<TableRow> rowsOnly = new ArrayList<TableRow>();
		String where="Lang='"+LocaleContextHolder.getLocale().toString().toUpperCase()+"'";
		rows = jdbcRepo.qtbGroupReport("SELECT * FROM dwhdepartments dd\r\n" + 
				"join reportsession rs on dd.reportsessionID=rs.ID and rs.Actual", "", where, table.getHeaders());
		if(!person.isEmpty()) {
			for(TableRow row : rows) {
				TableCell cell = row.getCellByKey("Email");
				if(cell!=null) {
					row.setSelected(cell.getValue().equalsIgnoreCase(person));
					if(row.getSelected()) {
						rowsOnly.add(row);
						break;
					}
				}
			}
		}else {
			rowsOnly.addAll(rows);
		}
		TableQtb.tablePage(rowsOnly,table);
		return data;
	}
	/**
	 * Headers for employees table
	 * @param data
	 */
	public Headers headersEmployees(Headers headers) {
		headers.getHeaders().add(TableHeader.instanceOf(
				"FullName",
				messages.get("username"),
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"Department",
				messages.get("departmentbranch"),
				true,
				true,
				true,
				TableHeader.COLUMN_STRING,
				0));
		headers.getHeaders().add(TableHeader.instanceOf(
				"Email",
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
	 * Headers for avialable activities table
	 * @param data
	 */
	private List<TableHeader> activitiesToDoHeaders() {
		List<TableHeader> headers = new ArrayList<TableHeader>();
		headers
		.add(TableHeader.instanceOf("Come", "scheduled", true, true, true, TableHeader.COLUMN_LOCALDATE, 0));
		headers
		.add(TableHeader.instanceOf("pref", "prefLabel", true, true, true, TableHeader.COLUMN_LINK, 0));
		headers
		.add(	TableHeader.instanceOf("workflow", "prod_app_type", true, true, true, TableHeader.COLUMN_STRING, 0));
		headers
		.add(TableHeader.instanceOf("activity", "activity", true, true, true, TableHeader.COLUMN_STRING, 0));
		headers.get(0).setSortValue(TableHeader.SORT_DESC);
		return headers;
	}
	/**
	 * Reassign select activities employee1 to employee2
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	@Transactional
	public ReassignActivitiesDTO employeeReassign(ReassignActivitiesDTO data) throws ObjectNotFoundException{
		List<Long> prevSelect=data.getPrevSelected();
		if(prevSelect.size()>0) {
			String other=data.getSelectedEmployeeOther();
			for(Long rowID : prevSelect) {
				History his=boilerServ.historyById(rowID);
				Concept conAct=his.getActivity();
				Concept conEmail=closureServ.getParent(conAct);
				Concept conRoot=closureServ.getParent(conEmail);
				List<Concept> listCons=closureServ.loadLevel(conRoot);
				Concept conOther= new Concept();
				for(Concept con: listCons) {
					if(con.getIdentifier().equals(other)){
						conOther=con;
					}
				}
				if(conOther.getID()==0) {
					conOther.setIdentifier(other);
					conOther=closureServ.saveToTreeFast(conRoot, conOther);
				}else {
					jdbcRepo.moveSubTree(conAct, conOther);
				}
			}
		}else {
			data.addError("ReassignActivitiesService:"+messages.get("not_availabeactivities"));
			data.setValid(false);
		}
		return data;
	}
	/**
	 * Select all rows in table availableActivities
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ReassignActivitiesDTO selectAll(ReassignActivitiesDTO data) {
		data=availableActivities(data, true);
		return data;
	}
	/**
	 * Deselect all rows in table availableActivities
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ReassignActivitiesDTO deselectAll(ReassignActivitiesDTO data) {
		TableQtb table=data.getAvailable();
		String person=data.getSelectedEmployee();
		String other=data.getSelectedEmployeeOther();
		table.getRows().clear();
		if (table.getHeaders().getHeaders().size() == 0) {
			table.getHeaders().getHeaders().addAll(activitiesToDoHeaders());
		}
		jdbcRepo.todo(person,null);
		String select = "select * from _todo td";
		String where="td.applDictID in (SELECT ud.conceptID	FROM user u	JOIN userdict ud ON ud.useruserId = u.userId WHERE u.Email='"+other+"')";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, table.getHeaders());
		data.getPrevSelected().clear();
		data.setSelectedOnly(false);
		List<Long> selectedIds= new ArrayList<Long>();
		for(TableRow row:rows) {
			row.setSelected(false);
		}
		data.setPrevSelected(selectedIds);
		rows=setSelectRow(data, rows);
		TableQtb.tablePage(rows, table);
		table.setSelectable(true);
		data.setAvailable(table);

		return data;
	}
	/**
	 * Show only select rows in table availableActivities
	 * @param data
	 * @return
	 * @throws ObjectNotFoundException
	 */
	public ReassignActivitiesDTO selectOnly(ReassignActivitiesDTO data) {
		TableQtb table=data.getAvailable();
		String person=data.getSelectedEmployee();
		String other=data.getSelectedEmployeeOther();
		table.getRows().clear();
		if (table.getHeaders().getHeaders().size() == 0) {
			table.getHeaders().getHeaders().addAll(activitiesToDoHeaders());
		}
		jdbcRepo.todo(person,null);
		String select = "select * from _todo td";
		String where="td.applDictID in (SELECT ud.conceptID	FROM user u	JOIN userdict ud ON ud.useruserId = u.userId WHERE u.Email='"+other+"')";
		List<TableRow> rows = jdbcRepo.qtbGroupReport(select, "", where, table.getHeaders());
		List<Long> selectedIds= data.getPrevSelected();
		List<TableRow> rows1= new ArrayList<TableRow>();
		if(selectedIds.size()>0) {
			for(TableRow row:rows) {
				if(selectedIds.contains(row.getDbID())) {
					row.setSelected(true);
					rows1.add(row);
				}else if(data.isSelectedOnly()){
					row.setSelected(false);
				}else {
					row.setSelected(false);
					rows1.add(row);
				}
			}
		}else {
			rows1.addAll(rows);
			data.setSelectedOnly(false);
		}
		TableQtb.tablePage(rows1, table);
		table.setSelectable(true);
		data.setAvailable(table);

		return data;
	}

}
