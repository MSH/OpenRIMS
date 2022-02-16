package org.msh.pharmadex2.dto;

import java.time.LocalDate;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Legacy data table and selection
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class LegacyDataDTO extends AllowValidation {
	private long selectedNode=0l;
	private String url="";					// the type of the data
	private LocalDate registered;		//when the selected registered
	private LocalDate expired;			//when the selected will be expired
	private TableQtb table = new TableQtb();
	private String varName="";
	private String storageUrl="";	//where to store
	private String altLabel = "altLabel";	//name for alternative lablel, default, altLabel
	public long getSelectedNode() {
		return selectedNode;
	}
	public void setSelectedNode(long selectedNode) {
		this.selectedNode = selectedNode;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public LocalDate getRegistered() {
		return registered;
	}
	public void setRegistered(LocalDate registered) {
		this.registered = registered;
	}
	public LocalDate getExpired() {
		return expired;
	}
	public void setExpired(LocalDate expired) {
		this.expired = expired;
	}
	public String getStorageUrl() {
		return storageUrl;
	}
	public void setStorageUrl(String storageUrl) {
		this.storageUrl = storageUrl;
	}
	public String getAltLabel() {
		return altLabel;
	}
	public void setAltLabel(String altLabel) {
		this.altLabel = altLabel;
	}
	
}
