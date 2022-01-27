package org.msh.pharmadex2.dto;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Any public organization, i.e. NMRA main office, departments, local offices
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class PublicOrgDTO extends AllowValidation {
	//ID of tthe organization
	private long id =0;
	//node in the tree for this organization
	private DictNodeDTO node = new DictNodeDTO();
	private TableQtb table=new TableQtb();
	private List<Long> selected = new ArrayList<Long>();
	private Long rowId=0l;
	private boolean all=true;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public DictNodeDTO getNode() {
		return node;
	}
	public void setNode(DictNodeDTO node) {
		this.node = node;
	}
	
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	
	public List<Long> getSelected() {
		return selected;
	}
	public void setSelected(List<Long> selected) {
		this.selected = selected;
	}
	
	public Long getRowId() {
		return rowId;
	}
	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}
	
	public boolean isAll() {
		return all;
	}
	public void setAll(boolean all) {
		this.all = all;
	}
	/**
	 * Create DTO from concept node
	 * @param node
	 * @return
	 */
	public static PublicOrgDTO of(DictNodeDTO node) {
		PublicOrgDTO ret = new PublicOrgDTO();
		ret.setNode(node);
		return ret;
	}
	
}
