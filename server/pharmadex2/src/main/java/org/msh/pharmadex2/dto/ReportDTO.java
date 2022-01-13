package org.msh.pharmadex2.dto;

import org.msh.pdex2.dto.table.TableQtb;
/**
 * Responsible for reports
 * Drill down from  a dictionary to the thing
 */
import org.msh.pharmadex2.dto.form.AllowValidation;/**

 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown=true)
public class ReportDTO extends AllowValidation {
	//configuration
	private ReportConfigDTO config;
	//table for report
	private TableQtb table = new TableQtb();
	//thing for report drill-down
	private ThingDTO thing = new ThingDTO();
	
	public ReportConfigDTO getConfig() {
		return config;
	}
	public void setConfig(ReportConfigDTO config) {
		this.config = config;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	public ThingDTO getThing() {
		return thing;
	}
	public void setThing(ThingDTO thing) {
		this.thing = thing;
	}

}
