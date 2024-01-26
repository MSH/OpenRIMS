package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ExchangeConfigDTO extends AllowValidation {
	
	private FormFieldDTO<String> serverurl = FormFieldDTO.of("");
	private int currentPage = 0;
	
	private Long processId = 0l;
	private String rootProcess = "";
	private String urlProcess = "";
	private Long itProcessID = 0l;
	private String title = "";
	
	private TableQtb existTable = new TableQtb();
	private TableQtb notExistTable = new TableQtb();
	private Map<Long, String> proccURLs = new HashMap<Long, String>();
	private String urlSelect = "";// url selected dictionary by show 
	private Long nodeIdSelect = 0l;
	private String urlByCopy = "";
	private OptionDTO dictByCopy = new OptionDTO();
	private String curLang = "";
	private String otherLang = "";
	private boolean hasProcess = true;//the process from the Main server already exists locally
	
	private List<DataVariableDTO> variables = new ArrayList<DataVariableDTO>();
	
	private List<String> headers = new ArrayList<String>();
	private Set<String> warnings = new HashSet<String>();
	private boolean showImpAll = true;//true - show ImportAll
	
	/**by preview show proccess */
	private ThingDTO previewThing = new ThingDTO();
	private String previewURL = "";
	
	public String getRootProcess() {
		return rootProcess;
	}
	public void setRootProcess(String rootProcess) {
		this.rootProcess = rootProcess;
	}
	
	public String getUrlProcess() {
		return urlProcess;
	}
	public void setUrlProcess(String urlProcess) {
		this.urlProcess = urlProcess;
	}
	public FormFieldDTO<String> getServerurl() {
		return serverurl;
	}
	public void setServerurl(FormFieldDTO<String> serverurl) {
		this.serverurl = serverurl;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public TableQtb getExistTable() {
		return existTable;
	}
	public void setExistTable(TableQtb existTable) {
		this.existTable = existTable;
	}
	public TableQtb getNotExistTable() {
		return notExistTable;
	}
	public void setNotExistTable(TableQtb notExistTable) {
		this.notExistTable = notExistTable;
	}
	public OptionDTO getDictByCopy() {
		return dictByCopy;
	}
	public void setDictByCopy(OptionDTO dictByCopy) {
		this.dictByCopy = dictByCopy;
	}
	public String getUrlByCopy() {
		return urlByCopy;
	}
	public void setUrlByCopy(String urlByCopy) {
		this.urlByCopy = urlByCopy;
	}
	public String getCurLang() {
		return curLang;
	}
	public void setCurLang(String cLang) {
		this.curLang = cLang;
	}
	public String getOtherLang() {
		return otherLang;
	}
	public void setOtherLang(String otherLang) {
		this.otherLang = otherLang;
	}
	public List<String> getHeaders() {
		return headers;
	}
	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}
	public String getUrlSelect() {
		return urlSelect;
	}
	public void setUrlSelect(String urlSelect) {
		this.urlSelect = urlSelect;
	}
	
	public Long getNodeIdSelect() {
		return nodeIdSelect;
	}
	public Map<Long, String> getProccURLs() {
		return proccURLs;
	}
	public void setProccURLs(Map<Long, String> proccURLs) {
		this.proccURLs = proccURLs;
	}
	public void setNodeIdSelect(Long nodeIdSelect) {
		this.nodeIdSelect = nodeIdSelect;
	}
	public List<DataVariableDTO> getVariables() {
		return variables;
	}
	public void setVariables(List<DataVariableDTO> variables) {
		this.variables = variables;
	}
	
	public Long getProcessId() {
		return processId;
	}
	public void setProcessId(Long processId) {
		this.processId = processId;
	}
	public Long getItProcessID() {
		return itProcessID;
	}
	public void setItProcessID(Long itProcessID) {
		this.itProcessID = itProcessID;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	/*public List<String> getWarnings() {
		return warnings;
	}
	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}*/
	
	public boolean isShowImpAll() {
		return showImpAll;
	}
	public Set<String> getWarnings() {
		return warnings;
	}
	public void setWarnings(Set<String> warnings) {
		this.warnings = warnings;
	}
	public void setShowImpAll(boolean showImpAll) {
		this.showImpAll = showImpAll;
	}
	public boolean isHasProcess() {
		return hasProcess;
	}
	public void setHasProcess(boolean hasProcess) {
		this.hasProcess = hasProcess;
	}
	public ThingDTO getPreviewThing() {
		return previewThing;
	}
	public void setPreviewThing(ThingDTO previewThing) {
		this.previewThing = previewThing;
	}
	public String getPreviewURL() {
		return previewURL;
	}
	public void setPreviewURL(String previewURL) {
		this.previewURL = previewURL;
	}
	
}
