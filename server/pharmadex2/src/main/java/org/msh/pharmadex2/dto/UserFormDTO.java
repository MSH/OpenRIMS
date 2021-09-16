package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/***
 * Display, edit information regarding a user
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserFormDTO extends AllowValidation{
	//form fields to view/edit data
	private FormFieldDTO<OptionDTO> userRoleFld = FormFieldDTO.of(new OptionDTO());
	private FormFieldDTO<String> userNameFld = FormFieldDTO.of(""); 
	
	public FormFieldDTO<OptionDTO> getUserRoleFld() {
		return userRoleFld;
	}

	public void setUserRoleFld(FormFieldDTO<OptionDTO> userRoleFld) {
		this.userRoleFld = userRoleFld;
	}

	public FormFieldDTO<String> getUserNameFld() {
		return userNameFld;
	}

	public void setUserNameFld(FormFieldDTO<String> userNameFld) {
		this.userNameFld = userNameFld;
	}
}
