package org.msh.pharmadex2.dto;

import java.time.LocalDateTime;

import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ImportLocalesDTO extends AllowValidation {
	private TableQtb table = new TableQtb();
	private ThingDTO thing= new ThingDTO();
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
