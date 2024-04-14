package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Data configuration
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DataConfigDTO extends AllowValidation {
	//selected data config node
	private long nodeId=0;
	//selected data config URL
	private String url="";
	//a page selected last time
	private int pageNo=0;
	//selected variable url node
	private long varNodeId=0;
	//table for data urls
	private TableQtb table=new TableQtb();
	//table for a data url variables
	private TableQtb varTable=new TableQtb();
	//edit restricted because of external refs. For future extension. Currently, always true
	private boolean restricted=true;
	
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public long getVarNodeId() {
		return varNodeId;
	}
	public void setVarNodeId(long varNodeId) {
		this.varNodeId = varNodeId;
	}
	
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	public TableQtb getVarTable() {
		return varTable;
	}
	public void setVarTable(TableQtb varTable) {
		this.varTable = varTable;
	}
	
	public boolean isRestricted() {
		return restricted;
	}
	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}
	@Override
	public String toString() {
		return "DataConfigDTO [nodeId=" + nodeId + ", varNodeId=" + varNodeId + ", table=" + table + ", varTable="
				+ varTable + "]";
	}
	
}
