package org.msh.pharmadex2.dto;

import java.util.LinkedHashMap;
import java.util.Map;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
/**
 * Components necessary to maintain the life cycle of an application
 * <ul>
 * 		<li> guest
 * 		<li> renewal
 * 		<li> inspection
 * 		<li> modification
 * 		<li> de-registration
 * </ul>
 * @author alexk
 *
 */
public class ProcessComponentsDTO extends AllowValidation {
	private FormFieldDTO<String> dictURL= FormFieldDTO.of("");		//dictionary URL
	private FormFieldDTO<String> dictName=FormFieldDTO.of("");	//dictionary name
	private FormFieldDTO<String> dictDescr=FormFieldDTO.of("");	//dictionary description
	private long dictNodeID=0;	//application node
	private FormFieldDTO<String> applURL=FormFieldDTO.of("");	//application URL
	private FormFieldDTO<String> applName=FormFieldDTO.of("");	//application name
	private FormFieldDTO<String> applDescr=FormFieldDTO.of("");	//application description
	private TableQtb dataConfigurations = new TableQtb();			//table for data configuration
	private TableQtb resources = new TableQtb();			//table for resources
	private TableQtb dictionaries = new TableQtb();			//table for dictionaries
	public FormFieldDTO<String> getDictURL() {
		return dictURL;
	}
	public void setDictURL(FormFieldDTO<String> dictURL) {
		this.dictURL = dictURL;
	}
	public FormFieldDTO<String> getDictName() {
		return dictName;
	}
	public void setDictName(FormFieldDTO<String> dictName) {
		this.dictName = dictName;
	}
	public FormFieldDTO<String> getDictDescr() {
		return dictDescr;
	}
	public void setDictDescr(FormFieldDTO<String> dictDescr) {
		this.dictDescr = dictDescr;
	}
	public long getDictNodeID() {
		return dictNodeID;
	}
	public void setDictNodeID(long dictNodeID) {
		this.dictNodeID = dictNodeID;
	}
	public FormFieldDTO<String> getApplURL() {
		return applURL;
	}
	public void setApplURL(FormFieldDTO<String> applURL) {
		this.applURL = applURL;
	}
	public FormFieldDTO<String> getApplName() {
		return applName;
	}
	public void setApplName(FormFieldDTO<String> applName) {
		this.applName = applName;
	}
	public FormFieldDTO<String> getApplDescr() {
		return applDescr;
	}
	public void setApplDescr(FormFieldDTO<String> applDescr) {
		this.applDescr = applDescr;
	}
	public TableQtb getDataConfigurations() {
		return dataConfigurations;
	}
	public void setDataConfigurations(TableQtb dataConfigurations) {
		this.dataConfigurations = dataConfigurations;
	}
	public TableQtb getResources() {
		return resources;
	}
	public void setResources(TableQtb resources) {
		this.resources = resources;
	}
	public TableQtb getDictionaries() {
		return dictionaries;
	}
	public void setDictionaries(TableQtb dictionaries) {
		this.dictionaries = dictionaries;
	}
	
}
