package org.msh.pharmadex2.dto;



import java.time.LocalDate;

import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.springframework.context.i18n.LocaleContextHolder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/**
 * Administrative-Configuration-Date and Number formats
 * @author alexk
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class FormatsDTO extends AllowValidation {
	// format input
	private FormFieldDTO<String> formatDate = FormFieldDTO.of("");
	private TableQtb table = new TableQtb();
	// format samples
	private FormFieldDTO<LocalDate> dateInputSample = FormFieldDTO.of(LocalDate.now());
	private FormFieldDTO<LocalDate> dateDisplaySample = FormFieldDTO.of(LocalDate.now());
	private TableCell dateCell = TableCell.instanceOf("date", LocalDate.now(), LocaleContextHolder.getLocale());
	private String dateEL = "";

	public FormFieldDTO<String> getFormatDate() {
		return formatDate;
	}
	public void setFormatDate(FormFieldDTO<String> formatDate) {
		this.formatDate = formatDate;
	}
	public TableQtb getTable() {
		return table;
	}
	public void setTable(TableQtb table) {
		this.table = table;
	}
	public FormFieldDTO<LocalDate> getDateInputSample() {
		return dateInputSample;
	}
	public void setDateInputSample(FormFieldDTO<LocalDate> dateInputSample) {
		this.dateInputSample = dateInputSample;
	}
	public FormFieldDTO<LocalDate> getDateDisplaySample() {
		return dateDisplaySample;
	}
	public void setDateDisplaySample(FormFieldDTO<LocalDate> dateDisplaySample) {
		this.dateDisplaySample = dateDisplaySample;
	}
	public TableCell getDateCell() {
		return dateCell;
	}
	public void setDateCell(TableCell dateCell) {
		this.dateCell = dateCell;
	}
	public String getDateEL() {
		return dateEL;
	}
	public void setDateEL(String dateEL) {
		this.dateEL = dateEL;
	}
	
}
