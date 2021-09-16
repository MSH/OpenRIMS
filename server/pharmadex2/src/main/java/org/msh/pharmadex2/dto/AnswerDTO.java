package org.msh.pharmadex2.dto;

import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.dto.form.Validator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Answer to a question
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AnswerDTO extends AllowValidation{
	private long id=0;
	private boolean yes=false;
	private boolean no=false;
	private boolean na=false;
	private boolean ask=false;
	private boolean answered=false;
	private String message="";
	private String answerHeader="";
	@Validator(above=3, below=255)
	FormFieldDTO<OptionDTO> expert = new FormFieldDTO<OptionDTO>(new OptionDTO());
	@Validator(above=3, below=255)
	FormFieldDTO<String> eQuestion = new FormFieldDTO<String>("");
	FormFieldDTO<String> notes = new FormFieldDTO<String>("");
	
	
	private boolean valid=true;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isYes() {
		return yes;
	}
	public void setYes(boolean yes) {
		this.yes = yes;
	}
	public boolean isNo() {
		return no;
	}
	public void setNo(boolean no) {
		this.no = no;
	}
	public boolean isNa() {
		return na;
	}
	public void setNa(boolean na) {
		this.na = na;
	}
	
	public boolean isAsk() {
		return ask;
	}

	public void setAsk(boolean ask) {
		this.ask = ask;
	}

	public boolean isAnswered() {
		return answered;
	}

	public void setAnswered(boolean answered) {
		this.answered = answered;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getAnswerHeader() {
		return answerHeader;
	}

	public void setAnswerHeader(String answerHeader) {
		this.answerHeader = answerHeader;
	}

	public FormFieldDTO<OptionDTO> getExpert() {
		return expert;
	}

	public void setExpert(FormFieldDTO<OptionDTO> expert) {
		this.expert = expert;
	}

	public FormFieldDTO<String> geteQuestion() {
		return eQuestion;
	}

	public void seteQuestion(FormFieldDTO<String> eQuestion) {
		this.eQuestion = eQuestion;
	}
	
	public FormFieldDTO<String> getNotes() {
		return notes;
	}

	public void setNotes(FormFieldDTO<String> notes) {
		this.notes = notes;
	}

	@Override
	public boolean isValid() {
		return this.valid;
	}

	@Override
	public void setValid(boolean _valid) {
		this.valid=_valid;
	}

}
