package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
/**
 * A list of permits 
 * First usage is Application Dashboard 
 * @author alexk
 *
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class PermitsDTO extends AllowValidation {
	private long dictItemId=0;		// item in any "guest" dictionary
	private String permitType="";	// the permit type name, e.g. Individual owner pharmacies
	private TableQtb table= new TableQtb();	//table to show/select permits
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
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	
}
