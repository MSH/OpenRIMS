package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * New amendment initializer
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AmendmentNewDTO extends AllowValidation{
	private long dictItemId=0;
	private String permitType;   //type of permit selected for de-registration
	private long dataNodeId=0;
	private TableQtb applications = new TableQtb();
	private TableQtb dataUnits = new TableQtb();
	
	public long getDictItemId() {
		return dictItemId;
	}
	public void setDictItemId(long dictItemId) {
		this.dictItemId = dictItemId;
	}
	
	public String getPermitType() {
		return permitType;
	}
	public void setPermitType(String permitType) {
		this.permitType = permitType;
	}
	public long getDataNodeId() {
		return dataNodeId;
	}
	public void setDataNodeId(long dataNodeId) {
		this.dataNodeId = dataNodeId;
	}
	public TableQtb getApplications() {
		return applications;
	}
	public void setApplications(TableQtb applications) {
		this.applications = applications;
	}
	public TableQtb getDataUnits() {
		return dataUnits;
	}
	public void setDataUnits(TableQtb dataUnits) {
		this.dataUnits = dataUnits;
	}
	
}
