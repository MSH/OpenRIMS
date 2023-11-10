package org.msh.pdex2.dto.table;

import java.util.ArrayList;
import java.util.List;

import org.msh.pdex2.exception.ObjectNotFoundException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



/**
 * Selectable rows
 * equal method compare concatenation of dbID and all cells
 * @author Alex Kurasoff
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class TableRow{
	private long dbID=0;
	private boolean selected=false;
	private List<TableCell> row = new ArrayList<TableCell>();


	public String calcUniqueKey() {
		String ret = dbID +"";
		for(TableCell cell: row) {
			ret +=cell.getValue();
		}
		return ret;
	}

	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((calcUniqueKey() == null) ? 0 : calcUniqueKey().hashCode());
		return result;
	}




	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableRow other = (TableRow) obj;
		if (calcUniqueKey() == null) {
			if (other.calcUniqueKey() != null)
				return false;
		} else if (!calcUniqueKey().equals(other.calcUniqueKey()))
			return false;
		return true;
	}




	public long getDbID() {
		return dbID;
	}
	public void setDbID(long dbID) {
		this.dbID = dbID;
	}
	public boolean getSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public List<TableCell> getRow() {
		return row;
	}
	public void setRow(List<TableCell> row) {
		this.row = row;
	}

	/**
	 * Private constructor to avoid wrong creation
	 */
	public TableRow(){
		super();
	}
	/**
	 * Creation method
	 * @param hasSelection is this table has selection?
	 * @param id id of this row. Typically it is database id
	 */
	public static TableRow instanceOf(long id) {
		TableRow ret = new TableRow();
		ret.setDbID(id);
		return ret;
	}
	/**
	 * Get value of table Cell for header with a key given
	 * @param key
	 * @param headers
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public Object getOriginalCellValue(String key, Headers headers) throws ObjectNotFoundException {
		int index = headers.indexOfKey(key);
		if(index>-1){
			return getRow().get(index).getOriginalValue();
		}else{
			throw new ObjectNotFoundException("Can't found header with key "+key);
		}
	}
	
	/**
	 * Get cell value as string
	 * @param key
	 * @param headers
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public String getCellValue(String key, Headers headers) throws ObjectNotFoundException {
		return getCell(key, headers).getValue();
	}
	/**
	 * Cells quantity is equal, string value of each cell equals each other
	 * @param other
	 * @return
	 */
	public boolean equalsCells(TableRow other) {
		if(other != null &&(this.getRow().size() == other.getRow().size())){
			for(int i=0; i<this.getRow().size();i++){
				if(!this.getRow().get(i).getValue().equalsIgnoreCase(other.getRow().get(i).getValue())){
					return false;
				}
			}
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Get a cell by table header key
	 * @param key
	 * @param headers
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public TableCell getCell(String key, Headers headers) throws ObjectNotFoundException {
		int index = headers.indexOfKey(key);
		if(index>-1){
			return getRow().get(index);
		}else{
			throw new ObjectNotFoundException("Can't found header with key "+key);
		}
	}
	@Override
	public String toString() {
		String ret="";
		for(TableCell cell : getRow()) {
			ret=ret+cell.getKey() +"["+cell.getOriginalValue()+"], ";
		}
		return ret;
	}
	/**
	 * Get table cell from table row by the key
	 * @param key
	 * @return null, if not found
	 */
	public TableCell getCellByKey(String key) {
		for(TableCell cell : getRow()) {
			if(cell.getKey().equalsIgnoreCase(key)) {
				return cell;
			}
		}
		return null;
	}

}
