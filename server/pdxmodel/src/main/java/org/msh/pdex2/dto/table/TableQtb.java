package org.msh.pdex2.dto.table;

import java.util.ArrayList;
import java.util.List;

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
	
	/*public void setSelectedRow(long tr) {
		for(TableRow row :this.getRows()){
			if(row.getDbID()==tr){
				row.setSelected(true);
				break;
			}
		}
		
	}*/
	
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
	
}
