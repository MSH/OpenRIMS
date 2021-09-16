package org.msh.pdex2.dto.table;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Headers for a table. ALso acts as filter and sort conditions and responsible for pagination
 * @author Alex Kurasoff
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Headers {
	private List<TableHeader> headers=new ArrayList<>();
	//vertical controls
		//page
	private int page=1; //page number, required
	private int pageSize=10; //joke;
	private int pages=0;
		//show rows
	private boolean selectedOnly=false;
	private boolean hiddenOnly=false;
			
	//horizontal control
	private int fixedLeft = 1000; // from which column we should keep horizontal fixed part
	private int currentLeft=1001; //from which column we should display variable part
	private int columns=1001;	  //how many columns we should display
	
	//useful counters
	private int selectedCount=0;
	private int allCount=0;

	/**
	 * Clone headers, needs for some non standard searches 
	 * @param headers
	 * @return
	 */
	public static Headers clone(Headers headers) {
		Headers clone = new Headers();
		clone.setColumns(headers.getColumns());
		clone.setCurrentLeft(headers.getCurrentLeft());
		clone.setFixedLeft(headers.getFixedLeft());
		clone.setHeaders(headers.getHeaders());
		clone.setHiddenOnly(headers.isHiddenOnly());
		clone.setPage(headers.getPage());
		clone.setPages(headers.getPages());
		clone.setPageSize(headers.getPageSize());
		clone.setSelectedOnly(headers.isSelectedOnly());
		return clone;
	}

	public List<TableHeader> getHeaders() {
		return headers;
	}

	public void setHeaders(List<TableHeader> headers) {
		this.headers.clear();
		this.headers.addAll(headers);
	}
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPages() {
		return pages;
	}
	public void setPages(int pages) {
		if(page>pages){
			setPage(1);
		}
		this.pages = pages;
	}
	
	public boolean isSelectedOnly() {
		return selectedOnly;
	}

	public void setSelectedOnly(boolean selectedOnly) {
		this.selectedOnly = selectedOnly;
	}

	public boolean isHiddenOnly() {
		return hiddenOnly;
	}

	public void setHiddenOnly(boolean hiddenOnly) {
		this.hiddenOnly = hiddenOnly;
	}

	public int getFixedLeft() {
		return fixedLeft;
	}
	/**
	 * Ensure right fixed left and current left
	 * @param fixedLeft
	 */
	public void setFixedLeft(int fixedLeft) {
		this.fixedLeft = fixedLeft;
		setCurrentLeft(fixedLeft+1);
	}

	public int getCurrentLeft() {
		return currentLeft;
	}

	public void setCurrentLeft(int currentLeft) {
		this.currentLeft = currentLeft;
	}

	public int getColumns() {
		return columns;
	}
	/**
	 * Visible columns
	 * @param columns
	 */
	public void setColumns(int columns) {
		this.columns = columns;
	}
	
	

	public int getSelectedCount() {
		return selectedCount;
	}

	public void setSelectedCount(int selectedCount) {
		this.selectedCount = selectedCount;
	}

	public int getAllCount() {
		return allCount;
	}

	public void setAllCount(int allCount) {
		this.allCount = allCount;
	}

	/**
	 * Create where phrase from headers list header and header and header...
	 * require key is table_alias.field
	 * @return List of where conditions that all should be true (and .. and .. and)
	 */
	public List<String> createWhere() {
		List<String> ret = new ArrayList<String>();
		for(TableHeader h : getHeaders()){
			String s = h.createWhere();
			if(s.length()>0){
				ret.add(s);
			}
		}
		return ret;
	}
	
	/**
	 * Create where phrase for Search field
	 * @return
	 */
	public List<String> createGeneralWhere(){
		List<String> ret = new ArrayList<String>();
		for(TableHeader h : getHeaders()){
			String s = h.createGeneralWhere();
			if(s.length()>0){
				ret.add(s);
			}
		}
		return ret;
	}
	/**
	 * Create list of phrases that will be use in OrderBy
	 * require key is table_alias.field
	 * @return
	 */
	public List<String> createOrderBy() {
		List<String> ret = new ArrayList<>();
		for(TableHeader h :getHeaders()){
			String s = h.createOrderBy();
			if(s.length()>0){
				ret.add(s);
			}
		}
		return ret;
	}
	/**
	 * Found index of header with key
	 * @param key
	 * @return
	 */
	public int indexOfKey(String key) {
		int ret=-1;
		for(int i=0; i<getHeaders().size();i++){
			if(getHeaders().get(i).getKey().equals(key)){
				ret=i;
				break;
			}
		}
		return ret;
	}
	/**
	 * Get a header by a key given. Case sensitive!!!
	 * If not found, create string header
	 * @param key
	 * @return
	 */
	public TableHeader getHeaderByKey(String key) {
		TableHeader ret = TableHeader.instanceOf(
				key,
				key,
				false,
				false,
				false,
				TableHeader.COLUMN_STRING,
				0);
		for(TableHeader head : getHeaders()) {
			if(head.getKey().equals(key)) {
				ret=head;
				break;
			}
		}
		return ret;
	}

	
	
}
