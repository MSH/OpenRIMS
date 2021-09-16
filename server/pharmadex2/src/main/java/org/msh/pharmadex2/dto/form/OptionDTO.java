package org.msh.pharmadex2.dto.form;

import java.util.ArrayList;
import java.util.List;

import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.old.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO for any class derived from com.msh.pdex.Options.java
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class OptionDTO {
	// all possible codes
	private List<OptionDTO> options = new ArrayList<OptionDTO>();
	//data fields
	private long id=0;
	//values will be translated by i18N
	private String code = "";
	private String description="";
	//values will keep as is
	private String originalCode="";
	private String originalDescription="";
	//active or not
	private boolean active=true;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<OptionDTO> getOptions() {
		return options;
	}

	public void setOptions(List<OptionDTO> options) {
		this.options = options;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}



	public String getOriginalCode() {
		return originalCode;
	}


	public void setOriginalCode(String originalCode) {
		this.originalCode = originalCode;
	}


	public String getOriginalDescription() {
		return originalDescription;
	}


	public void setOriginalDescription(String originalDescription) {
		this.originalDescription = originalDescription;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "OptionDTO [options=" + options + ", id=" + id + ", code=" + code + ", description=" + description
				+ ", originalCode=" + originalCode + ", originalDescription=" + originalDescription + "]";
	}


	public Integer getIntId() {
		Long lid = new Long(getId());
		return lid.intValue();
	}
	/**
	 * Create it from anything :)
	 * @param id
	 * @param code
	 * @param description
	 * @return
	 */
	public static OptionDTO of(long id, String code, String description) {
		OptionDTO ret = new OptionDTO();
		ret.setId(id);
		ret.setCode(code);
		ret.setDescription(description);
		return ret;
	}
	
}