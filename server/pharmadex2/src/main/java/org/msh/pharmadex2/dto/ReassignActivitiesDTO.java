package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

public class ReassignActivitiesDTO extends AllowValidation{
	private TableQtb employee = new TableQtb();//table for selecting an employee1 for unloading
	private TableQtb employeeother = new TableQtb();//selecting an employee2 to whom activities are transferred
	private TableQtb activities = new TableQtb();//employee1 activity
	private TableQtb currentworkfload = new TableQtb();//loading employee2
	private String selectedEmployee="";// email employee1
	private String selectedEmployeeOther="";// email employee2
	private boolean availableActivities=true;// flag for show table availableActivities
	private TableQtb available = new TableQtb();// table availableActivities
	private List<Long> prevSelected = new ArrayList<Long>();//array of selected rows in table availableActivities
	private boolean selectedOnly=false;//show only selected rows
	
	public TableQtb getEmployee() {
		return employee;
	}
	public void setEmployee(TableQtb employee) {
		this.employee = employee;
	}
	public TableQtb getEmployeeother() {
		return employeeother;
	}
	public void setEmployeeother(TableQtb employeeother) {
		this.employeeother = employeeother;
	}
	public TableQtb getActivities() {
		return activities;
	}
	public void setActivities(TableQtb activities) {
		this.activities = activities;
	}
	public TableQtb getCurrentworkfload() {
		return currentworkfload;
	}
	public void setCurrentworkfload(TableQtb currentworkfload) {
		this.currentworkfload = currentworkfload;
	}

	public String getSelectedEmployee() {
		return selectedEmployee;
	}
	public void setSelectedEmployee(String selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}
	public String getSelectedEmployeeOther() {
		return selectedEmployeeOther;
	}
	public void setSelectedEmployeeOther(String selectedEmployeeOther) {
		this.selectedEmployeeOther = selectedEmployeeOther;
	}
	public boolean isAvailableActivities() {
		return availableActivities;
	}
	public void setAvailableActivities(boolean availableActivities) {
		this.availableActivities = availableActivities;
	}
	public TableQtb getAvailable() {
		return available;
	}
	public void setAvailable(TableQtb available) {
		this.available = available;
	}
	public List<Long> getPrevSelected() {
		List<Long> tmp = new ArrayList<Long>();
		tmp.addAll(this.prevSelected);
		this.prevSelected.clear();
		for(Long n :tmp) {
			if(n>0) {
				this.prevSelected.add(n);
			}
		}
		return prevSelected;
	}
	public void setPrevSelected(List<Long> prevSelected) {
		this.prevSelected = prevSelected;
	}
	public boolean isSelectedOnly() {
		return selectedOnly;
	}
	public void setSelectedOnly(boolean selectedOnly) {
		this.selectedOnly = selectedOnly;
	}
}
