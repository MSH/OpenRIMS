package org.msh.pdex2.dto.table;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Most common table model for QuanTB Collector project
 * provides headers, rows, searchTreshold and general search field
 * @author Alex Kurasoff
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class TableQtb {
	private boolean selectable=true;
	private Headers headers = new Headers();
	private List<TableRow> rows = new ArrayList<>();
	private Set<Long> selections = new LinkedHashSet<Long>();
	//search part
	private int searchTreshold=2; //start search after user will typed searchTreshold characters
	private String generalSearch="";
	private String otherSearch = "";
	private boolean paintBorders=true;		//should we paint borders (only for MS Word yet)
	private boolean paintHeaders=true;	//should we paint headers (only for MS Word yet)
	
	public boolean isSelectable() {
		return selectable;
	}
	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}
	public Headers getHeaders() {
		return headers;
	}
	public void setHeaders(Headers headers) {
		this.headers = headers;
	}
	public List<TableRow> getRows() {
		return rows;
	}
	public void setRows(List<TableRow> rows) {
		this.rows = rows;
	}
	
	public Set<Long> getSelections() {
		return selections;
	}
	public void setSelections(Set<Long> selections) {
		this.selections = selections;
	}
	public int getSearchTreshold() {
		return searchTreshold;
	}
	public void setSearchTreshold(int searchTreshold) {
		this.searchTreshold = searchTreshold;
	}
	public String getGeneralSearch() {
		return generalSearch;
	}
	public void setGeneralSearch(String generalSearch) {
		this.generalSearch = generalSearch;
	}
	
	public String getOtherSearch() {
		return otherSearch;
	}
	public void setOtherSearch(String otherSearch) {
		this.otherSearch = otherSearch;
	}
	public boolean isPaintBorders() {
		return paintBorders;
	}
	public void setPaintBorders(boolean paintBorders) {
		this.paintBorders = paintBorders;
	}
	public boolean isPaintHeaders() {
		return paintHeaders;
	}
	public void setPaintHeaders(boolean paintHeaders) {
		this.paintHeaders = paintHeaders;
	}
	/**
	 * Get selected row
	 * @return null if not found
	 */
	public TableRow getSelectedRow() {
		TableRow ret = null;
		for(TableRow row :this.getRows()){
			if(row.getSelected()){
				ret=row;
				break;
			}
		}
		return ret;
	}
	
	/**
	 * Get selected rows!!!
	 * @return null if not found
	 */
	public List<TableRow> getSelectedRows() {
		List<TableRow> ret = new ArrayList<TableRow>();
		for(TableRow row :this.getRows()){
			if(row.getSelected()){
				ret.add(row);
				
			}
		}
		return ret;
	}
	
	/**
	 * deselect all rows
	 */
	public void unselectAll() {
		for(TableRow row : getRows()){
			row.setSelected(false);
		}
		
	}
	
	/**
	 * Place a current page to a table from all rows
	 * @param rows
	 * @param table
	 */
	public static void tablePage(List<TableRow> rows, TableQtb table) {
		table.getRows().clear();
		table.getHeaders().setPages(TableHeader.calcPages(table.getHeaders().getPageSize(),rows.size()));
		table.getRows().addAll(TableHeader.fetchPage(rows,table.getHeaders().getPage(),
				table.getHeaders().getPageSize()));
		boolean filtered=false;
		for(TableHeader th : table.getHeaders().getHeaders()) {
			filtered = filtered || th.getGeneralCondition().length()>0 || th.getConditionS().length()>0;
		}
		table.getHeaders().setFiltered(filtered);
	}
	
	/**
	 * lace a current page to a table from all rows, but keep selection
	 * @param rows all rows that are typically is has ben gotten by a SQL
	 * @param table
	 * @param selectedOnly - fetch only selected rows
	 */
	public static void tablePageKeepSelection(List<TableRow> rows, TableQtb table, boolean selectedOnly) {
		table.recalcSelections();
		List<TableRow> allRows = new ArrayList<TableRow>();
		if(selectedOnly) {
			for(TableRow row : rows) {
				if(table.getSelections().contains(row.getDbID())) {
					allRows.add(row);
				}
			}
		}else {
			allRows.addAll(rows);
		}
		//
		TableQtb.tablePage(allRows, table);
		//
		for(TableRow row : table.getRows()) {
			if(table.getSelections().contains(row.getDbID())){
				row.setSelected(true);
			}
		}
	}
	/**
	 * Recalculate list of selections using currently on-screen selected
	 */
	private void recalcSelections() {
		for(TableRow row : getRows()) {
			if(getSelections().contains(row.getDbID())) {
				if(!row.getSelected()) {
					getSelections().remove(row.getDbID()); //remove
				}
			}else {
				if(row.getSelected()) {
					getSelections().add(row.getDbID());		//add
				}
			}
		}
		
	}
	/**
	 * Calc pages
	 * @param pageSize
	 * @param totalSize
	 * @return
	 */
	public static int calcPages(int pageSize, int totalSize) {
		int ret = (int)Math.ceil((float)totalSize/pageSize);
		return ret;
	}
	/**
	 * Has this table headers
	 * @return
	 */
	public boolean hasHeaders() {
		return getHeaders().getHeaders().size()>0;
	}

	/**
	 * Is this table loaded (initialized)
	 * @return
	 */
	public boolean isLoaded() {
		return getHeaders().getHeaders().size()>0;
	}
	
	
}
