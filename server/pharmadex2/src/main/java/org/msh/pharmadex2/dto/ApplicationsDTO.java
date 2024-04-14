package org.msh.pharmadex2.dto;

import java.time.LocalDateTime;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Table of applications accessible in a url given
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationsDTO extends AllowValidation {
	private String url="";
	private String title="";
	private String description = "";
	private long dictItemId=0;
	private TableQtb table = new TableQtb();
	private TableQtb scheduled = new TableQtb();
	private TableQtb fullsearch = new TableQtb();
	private TableQtb archive = new TableQtb();
	private ThingDTO thing= new ThingDTO();
	private FormFieldDTO<LocalDateTime> dateactual=new FormFieldDTO(LocalDateTime.now()) ;
	private boolean amendment=false;
	private FormFieldDTO<OptionDTO> prod_app_type = new FormFieldDTO<OptionDTO>();
	private FormFieldDTO<OptionDTO> state = new FormFieldDTO<OptionDTO>();
	private boolean canAdd = false;
	private boolean arch = false;
	private int count=0;
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getDictItemId() {
		return dictItemId;
	}

	public void setDictItemId(long dictItemId) {
		this.dictItemId = dictItemId;
	}

	public TableQtb getTable() {
		return table;
	}

	public void setTable(TableQtb table) {
		this.table = table;
	}

	public TableQtb getScheduled() {
		return scheduled;
	}

	public void setScheduled(TableQtb scheduled) {
		this.scheduled = scheduled;
	}

	public TableQtb getFullsearch() {
		return fullsearch;
	}

	public void setFullsearch(TableQtb fullsearch) {
		this.fullsearch = fullsearch;
	}

	public ThingDTO getThing() {
		return thing;
	}

	public void setThing(ThingDTO thing) {
		this.thing = thing;
	}

	public FormFieldDTO<LocalDateTime> getDateactual() {
		return dateactual;
	}

	public void setDateactual(FormFieldDTO<LocalDateTime> dateactual) {
		this.dateactual = dateactual;
	}

	public boolean isAmendment() {
		return amendment;
	}

	public void setAmendment(boolean amendment) {
		this.amendment = amendment;
	}

	public FormFieldDTO<OptionDTO> getProd_app_type() {
		return prod_app_type;
	}

	public void setProd_app_type(FormFieldDTO<OptionDTO> prod_app_type) {
		this.prod_app_type = prod_app_type;
	}

	public FormFieldDTO<OptionDTO> getState() {
		return state;
	}

	public void setState(FormFieldDTO<OptionDTO> state) {
		this.state = state;
	}

	public boolean isCanAdd() {
		return canAdd;
	}

	public void setCanAdd(boolean canAdd) {
		this.canAdd = canAdd;
	}

	public TableQtb getArchive() {
		return archive;
	}

	public void setArchive(TableQtb archive) {
		this.archive = archive;
	}

	public boolean isArch() {
		return arch;
	}

	public void setArch(boolean arch) {
		this.arch = arch;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
