package org.msh.pharmadex2.dto;

import java.util.ArrayList;
import java.util.List;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Responsible for amendment data 
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AmendmentDTO extends AllowValidation {
	private long nodeId=0l;
	private String  title="";								//object name
	private String  chapterTitle="";					//chapter name
	//the table
	private TableQtb table = new TableQtb();
	//the application to amend
	private ThingDTO appl = new ThingDTO();
	//the chapter to amend
	private ThingDTO chapter = new ThingDTO();
	//variables to amend
	private TableQtb variables = new TableQtb();
	//description of an amendment
	private FormFieldDTO<String> description = FormFieldDTO.of("");
	//auxiliary chapters that may be in the amendment
	private List<ThingDTO> path = new ArrayList<ThingDTO>();
	private int pathIndex=0;
	
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getChapterTitle() {
		return chapterTitle;
	}
	public void setChapterTitle(String chapterTitle) {
		this.chapterTitle = chapterTitle;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	public ThingDTO getAppl() {
		return appl;
	}
	public void setAppl(ThingDTO appl) {
		this.appl = appl;
	}
	public ThingDTO getChapter() {
		return chapter;
	}
	public void setChapter(ThingDTO chapter) {
		this.chapter = chapter;
	}
	public List<ThingDTO> getPath() {
		return path;
	}
	public void setPath(List<ThingDTO> path) {
		this.path = path;
	}
	public int getPathIndex() {
		return pathIndex;
	}
	public void setPathIndex(int pathIndex) {
		this.pathIndex = pathIndex;
	}
	public TableQtb getVariables() {
		return variables;
	}
	public void setVariables(TableQtb variables) {
		this.variables = variables;
	}
	public FormFieldDTO<String> getDescription() {
		return description;
	}
	public void setDescription(FormFieldDTO<String> description) {
		this.description = description;
	}
	
}
