package org.msh.pharmadex2.dto.form;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.msh.pdex2.dto.table.TableCell;
import org.msh.pharmadex2.dto.enums.AssistantEnum;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class serves any field in on screen form
 * @author alexk
 *
 * @param <T> class of the field
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class FormFieldDTO<T> extends FieldSuggest{
	private boolean readOnly=false;
	private boolean textArea=false;
	private T value = null;
	private String valueStr="";	//pre-formatted value, currently only for dates
	private int bdScale=2;		//Big decimal scale
	private boolean mark=false;	//mark this field by color
	private String detail=""; // 06122022 khomenska auxiliary field, for example for the calendar type depending on the configuration settings
	private AssistantEnum assistant = AssistantEnum.NO;	// do we need assistance to input data to this field?
	private String description="";								//help or assistant text
	public FormFieldDTO() {
		super();
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isTextArea() {
		return textArea;
	}

	public void setTextArea(boolean textArea) {
		this.textArea = textArea;
	}

	public FormFieldDTO(T value) {
		super();
		this.value = value;
	}
	/**
	 * Value of a field, see for the real type
	 * @return
	 */
	public T getValue() {
		return value;
	}
	/**
	 * Value of a field, see for the real type
	 * @return
	 */
	public void setValue(T value) {
		if(value!=null){
			if(value instanceof BigDecimal) {
				BigDecimal bdVal = ((BigDecimal) value).setScale(this.getBdScale(), BigDecimal.ROUND_HALF_UP);
				this.value=(T) bdVal;
			}else {
				this.value = value;
			}
		}
	}

	public String getValueStr() {
		return valueStr;
	}

	public void setValueStr(String valueStr) {
		this.valueStr = valueStr;
	}

	public int getBdScale() {
		return bdScale;
	}

	public void setBdScale(int bdScale) {
		this.bdScale = bdScale;
	}
	public boolean isMark() {
		return mark;
	}

	public void setMark(boolean mark) {
		this.mark = mark;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public AssistantEnum getAssistant() {
		return assistant;
	}

	public void setAssistant(AssistantEnum assistant) {
		this.assistant = assistant;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return getValue().toString();
	}

	public static FormFieldDTO<String> of(String string) {
		if(string==null) {
			string="";
		}
		return new FormFieldDTO<String>(string);
	}

	/**
	 * Create text field, enhanced
	 * @param string
	 * @param readOnly
	 * @param textArea
	 * @return
	 */
	public static FormFieldDTO<String> of(String string, boolean readOnly, boolean textArea) {
		if(string==null) {
			string="";
		}
		FormFieldDTO<String> ret = new FormFieldDTO<String>(string);
		ret.setReadOnly(readOnly);
		ret.setTextArea(textArea);
		return ret;
	}

	/**
	 * Create text field, enhanced with assistant
	 * @param string
	 * @param readOnly
	 * @param textArea
	 * @return
	 */
	public static FormFieldDTO<String> of(String string, boolean readOnly, boolean textArea, AssistantEnum assistant) {
		if(string==null) {
			string="";
		}
		FormFieldDTO<String> ret = new FormFieldDTO<String>(string);
		ret.setReadOnly(readOnly);
		ret.setTextArea(textArea);
		ret.setAssistant(assistant);
		return ret;
	}

	public static FormFieldDTO<BigDecimal> of(BigDecimal value, int scale) {
		BigDecimal myValue = BigDecimal.ZERO;
		if (value!=null) {
			myValue=BigDecimal.ZERO.add(value);
		}
		myValue = myValue.setScale(scale, BigDecimal.ROUND_HALF_UP);
		FormFieldDTO<BigDecimal> ret = new FormFieldDTO<BigDecimal>(myValue);
		ret.setBdScale(scale);
		return ret;
	}



	public static FormFieldDTO<LocalDate> of(LocalDate dt) {
		FormFieldDTO<LocalDate> ret = new FormFieldDTO<LocalDate>();
		ret.setValue(dt);
		ret.setValueStr(TableCell.localDateToString(dt));
		return ret;
	}

	public static FormFieldDTO<Long> of(Long l) {
		return new FormFieldDTO<Long>(l);
	}

	public static FormFieldDTO<LocalDate> of(Date date) {
		Date dt = new Date();
		if(date instanceof java.sql.Date) {
			dt=new Date(date.getTime());
		}else {
			dt=date;
		}
		LocalDate ld  = dt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		FormFieldDTO<LocalDate> ret =FormFieldDTO.of(ld);
		return ret;
	}

	public static FormFieldDTO<Integer> of(Integer intPar) {
		if(intPar!=null) {
			return new FormFieldDTO<Integer>(intPar);
		}else {
			return null;
		}
	}

	public static FormFieldDTO<OptionDTO> of(OptionDTO optionDTO) {
		FormFieldDTO<OptionDTO> ret = new FormFieldDTO<OptionDTO>(optionDTO);
		return ret;
	}
	
	

}
