package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import org.msh.pharmadex2.dto.form.AllowValidation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * CheckList component
 * @author khomenska
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CheckListDTO extends AllowValidation{
	private long historyId=0;					//history identifier, mandatory!
	private String dictUrl="";					//dictionary url
	private long activityNodeId=0;			//activity related
	private long applNodeId=0;				//application node id
	private String title = "";					//title to place above
	private boolean readOnly = false;
	private boolean init=false;				//in the INIT activity, only restricted set of actions will be provided
	private List<QuestionDTO> questions = new ArrayList<QuestionDTO>();
	public String getDictUrl() {
		return dictUrl;
	}
	public void setDictUrl(String dictUrl) {
		this.dictUrl = dictUrl;
	}
	public long getHistoryId() {
		return historyId;
	}
	public void setHistoryId(long historyId) {
		this.historyId = historyId;
	}
	public long getActivityNodeId() {
		return activityNodeId;
	}
	public void setActivityNodeId(long activityNodeId) {
		this.activityNodeId = activityNodeId;
	}
	public long getApplNodeId() {
		return applNodeId;
	}
	public void setApplNodeId(long applNodeId) {
		this.applNodeId = applNodeId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public boolean isInit() {
		return init;
	}
	public void setInit(boolean init) {
		this.init = init;
	}
	public List<QuestionDTO> getQuestions() {
		return questions;
	}
	public void setQuestions(List<QuestionDTO> questions) {
		this.questions = questions;
	}
	
}
