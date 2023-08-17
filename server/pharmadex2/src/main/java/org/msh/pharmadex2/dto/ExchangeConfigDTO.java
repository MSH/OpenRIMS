package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	
	private boolean pingServer = false;
	private FormFieldDTO<String> serverurl = FormFieldDTO.of("");
	private int currentPage = 0;
	
	private Dict2DTO wfdto = new Dict2DTO();
	private TableQtb existTable = new TableQtb();
	private TableQtb notExistTable = new TableQtb();
	private DictionaryDTO showDict = new DictionaryDTO();
	private String urlSelect = "";
	private String urlByCopy = "";
	private OptionDTO dictByCopy = new OptionDTO();
	private String curLang = "";
	private String otherLang = "";
	
	private List<String> headers = new ArrayList<String>();
	
	public boolean isPingServer() {
		return pingServer;
	}
	public void setPingServer(boolean pingServer) {
		this.pingServer = pingServer;
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
	public Dict2DTO getWfdto() {
		return wfdto;
	}
	public void setWfdto(Dict2DTO wfdto) {
		this.wfdto = wfdto;
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
	public DictionaryDTO getShowDict() {
		return showDict;
	}
	public void setShowDict(DictionaryDTO showDict) {
		this.showDict = showDict;
	}
	public String getUrlSelect() {
		return urlSelect;
	}
	public void setUrlSelect(String urlSelect) {
		this.urlSelect = urlSelect;
	}
	
}
