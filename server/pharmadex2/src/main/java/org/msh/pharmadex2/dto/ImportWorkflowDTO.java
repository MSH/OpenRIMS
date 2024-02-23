package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ImportWorkflowDTO extends AllowValidation{

	/** field by server url */
	private FormFieldDTO<String> serverurl = FormFieldDTO.of("");
	/** признак подключения к серверу */
	private boolean connect = false;
	
	private TableQtb procTable = new TableQtb();
	private TableQtb wfTable = new TableQtb();
	private TableQtb statusTable = new TableQtb();
	
	/**id selected item in left procTable */
	private Long processIDselect = 0l;
	private String processURL = "";
	/**id selected item in right wfTable */
	private Long wfIDselect = 0l;
	private String wfURL = "";
	/** признак успешности проверки всех активностей выбранного процеса
	 * при истине - можно выполнять импорт  */
	private boolean validateWFselect = false;
	
	/**  */
	private boolean selectedOnly = false;
	/** configuration URL-data dictionary */
	private Map<String, List<DataVariableDTO>> varsImport = new HashMap<String, List<DataVariableDTO>>();
	private Map<String, DataCollectionDTO> configImport = new HashMap<String, DataCollectionDTO>();
	private Map<String, ResourceDTO> resImport = new HashMap<String, ResourceDTO>();
	private List<ThingDTO> pathImport = new ArrayList<ThingDTO>();
	/** nodeID ThingDTO - list selected values in dictionaries Execitors, finalization */
	private Map<Long, List<String>> pathImportDict = new HashMap<Long, List<String>>();
	
	/** dictionary URL-data dictionary */
	private Map<String, Map<Long, List<DictNodeDTO>>> dictsImport = new HashMap<String, Map<Long, List<DictNodeDTO>>>();
	private DictNodeDTO dict = new DictNodeDTO();
	
	public FormFieldDTO<String> getServerurl() {
		return serverurl;
	}

	public void setServerurl(FormFieldDTO<String> serverurl) {
		this.serverurl = serverurl;
	}

	public boolean isConnect() {
		return connect;
	}

	public void setConnect(boolean connect) {
		this.connect = connect;
	}

	public TableQtb getProcTable() {
		return procTable;
	}

	public void setProcTable(TableQtb procTable) {
		this.procTable = procTable;
	}

	public TableQtb getWfTable() {
		return wfTable;
	}

	public void setWfTable(TableQtb wfTable) {
		this.wfTable = wfTable;
	}

	public TableQtb getStatusTable() {
		return statusTable;
	}

	public void setStatusTable(TableQtb statusTable) {
		this.statusTable = statusTable;
	}

	public Long getProcessIDselect() {
		return processIDselect;
	}

	public void setProcessIDselect(Long processIDselect) {
		this.processIDselect = processIDselect;
	}

	public Long getWfIDselect() {
		return wfIDselect;
	}

	public void setWfIDselect(Long wfIDselect) {
		this.wfIDselect = wfIDselect;
	}

	public String getWfURL() {
		return wfURL;
	}

	public void setWfURL(String wfURL) {
		this.wfURL = wfURL;
	}

	public boolean isSelectedOnly() {
		return selectedOnly;
	}

	public void setSelectedOnly(boolean selectedOnly) {
		this.selectedOnly = selectedOnly;
	}

	public boolean isValidateWFselect() {
		return validateWFselect;
	}

	public void setValidateWFselect(boolean validateWFselect) {
		this.validateWFselect = validateWFselect;
	}

	public String getProcessURL() {
		return processURL;
	}

	public void setProcessURL(String processURL) {
		this.processURL = processURL;
	}

	public Map<String, List<DataVariableDTO>> getVarsImport() {
		return varsImport;
	}

	public void setVarsImport(Map<String, List<DataVariableDTO>> varsImport) {
		this.varsImport = varsImport;
	}

	public Map<String, DataCollectionDTO> getConfigImport() {
		return configImport;
	}

	public void setConfigImport(Map<String, DataCollectionDTO> configImport) {
		this.configImport = configImport;
	}

	public Map<String, ResourceDTO> getResImport() {
		return resImport;
	}

	public void setResImport(Map<String, ResourceDTO> resImport) {
		this.resImport = resImport;
	}

	public List<ThingDTO> getPathImport() {
		return pathImport;
	}

	public void setPathImport(List<ThingDTO> pathImport) {
		this.pathImport = pathImport;
	}

	public Map<String, Map<Long, List<DictNodeDTO>>> getDictsImport() {
		return dictsImport;
	}

	public void setDictsImport(Map<String, Map<Long, List<DictNodeDTO>>> dictsImport) {
		this.dictsImport = dictsImport;
	}

	public DictNodeDTO getDict() {
		return dict;
	}

	public void setDict(DictNodeDTO dict) {
		this.dict = dict;
	}

	public Map<Long, List<String>> getPathImportDict() {
		return pathImportDict;
	}

	public void setPathImportDict(Map<Long, List<String>> pathImportDict) {
		this.pathImportDict = pathImportDict;
	}
	
}

