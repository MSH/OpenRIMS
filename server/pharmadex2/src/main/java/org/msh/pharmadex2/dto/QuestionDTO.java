package org.msh.pharmadex2.dto;


import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A single check list question
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class QuestionDTO extends  AllowValidation{
	private long id=0;							//checklistr2 ID
	private long dictId=0;					//dictionary item ID
	private String question="";
	private int answer=0;					//answer on the question 0,1,2,3 (no answer, yes, no, na)
	private boolean head=false;			//is it header or a question
	private FormFieldDTO<String> comment=FormFieldDTO.of("");
	private String description="";
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getDictId() {
		return dictId;
	}
	public void setDictId(long dictId) {
		this.dictId = dictId;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public int getAnswer() {
		return answer;
	}
	public void setAnswer(int answer) {
		this.answer = answer;
	}
	public boolean isHead() {
		return head;
	}
	public void setHead(boolean head) {
		this.head = head;
	}
	public FormFieldDTO<String> getComment() {
		return comment;
	}
	public void setComment(FormFieldDTO<String> comment) {
		this.comment = comment;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * Create question from OptionDTO derived from the dictionary
	 * @param odto
	 * @return
	 */
	public static QuestionDTO of(OptionDTO odto) {
		QuestionDTO ret = new QuestionDTO();
		ret.setDescription(odto.getDescription());
		ret.setDictId(odto.getId());
		ret.setHead(!odto.isActive());
		ret.setQuestion(odto.getCode());
		return ret;
	}
	@Override
	public String toString() {
		return "QuestionDTO [question=" + question + ", answer=" + answer + ", head=" + head + "]";
	}

}
