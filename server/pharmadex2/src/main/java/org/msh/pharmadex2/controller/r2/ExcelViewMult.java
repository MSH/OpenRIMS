package org.msh.pharmadex2.controller.r2;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pharmadex2.controller.common.POIProcessor;
import org.msh.pharmadex2.dto.DataCollectionDTO;
import org.msh.pharmadex2.exception.DataNotFoundException;
import org.springframework.web.servlet.view.document.AbstractXlsxView;


/**
 * Allows multi sheets Excel workbook creation
 * It is a special view in Spring Boot MVC approach.
 * The model is a single parameter "data" that should contain Map<String,DataCollectionDTO>
 * A key in the map is a name of a sheet. The DataCollectionDTO contains data for the sheet:
 * <ul>
 * <li> description - description of the data
 * <li> url - url of the sheet data, if applicable
 * <li> table a table to place on the sheet
 * </ul>
 * It will be a good idea to implement Map using LinkedHAshMap
 * The semantic is depends on the service that creates the model.
 * The known service is ReportService to report data configurations for a Supervisor 
 * @author Alex Kurasoff
 *
 */
public class ExcelViewMult extends AbstractXlsxView{

	POIProcessor processor = new POIProcessor(new File("report.xlsx"));

	public POIProcessor getProcessor() {
		return processor;
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model,
			Workbook workbook,
			HttpServletRequest request,
			HttpServletResponse response)	{
		
		buildWorkbook(model, workbook);
	}
	
	/**
	 * Really build the workbook 
	 * @param model
	 * @param workbook
	 */
	public void buildWorkbook(@SuppressWarnings("rawtypes") Map model, Workbook workbook) {
		//VARIABLES REQUIRED IN MODEL
		getProcessor().initWorkbook(workbook);
		Object dato = model.get("data");
		if(dato!=null) {
			if(dato instanceof Map<?,?>) {
				@SuppressWarnings("unchecked")
				Map<String,DataCollectionDTO> data = (Map<String, DataCollectionDTO>) dato;
				for(String key : data.keySet()) {
					placeSheet(key, data.get(key));
				}
			}
		}else {
			placeError("Model should contain data");
		}
	}
	/**
	 * Create workbook with error data
	 * @param message
	 * @return
	 */
	public void placeError(String message) {
		getProcessor().createSheet("Error",1000);
		int row=0;
		placeTitle(message, 10, row);
	}

	/**
	 * Add a sheet to a workbook given
	 * @param key - sheet name
	 * @param data - data to place
	 * @return
	 */
	public void placeSheet(String key, DataCollectionDTO data) {
		getProcessor().createSheet(key,1000);
		int row=0;
		placeTitle(data.getDescription().getValue(), 10, row);
		row++;
		if(data.getUrl().getValue().length()>0) {
			placeTitle(data.getDescription().getValue(), 10, row);
			row++;
		}
		row = placeHeaders(data.getTable().getHeaders().getHeaders(),row);
		row = placeRows(data.getTable().getHeaders().getHeaders(),data.getTable().getRows(),row);
	}

	/**
	 * Rows of the table
	 * @param sHeaders 
	 * @param sRows
	 * @param rowNo
	 * @return
	 */
	private int placeRows(List<TableHeader> sHeaders, List<TableRow> sRows, int rowNo) {
		if(sRows != null && sRows.size()>0){
			for(TableRow row : sRows){
				placeRow(sHeaders,row,rowNo);
				rowNo++;
			}
			return ++rowNo;
		}else{
			return rowNo;
		}
	}
	/**
	 * Place a row
	 * @param sHeaders 
	 * @param row
	 * @param rowNo
	 */
	private void placeRow(List<TableHeader> sHeaders, TableRow row, int rowNo) {
		int i=0;
		for(TableCell cell : row.getRow()){
			if(cell.getOriginalValue() != null){
				String clazz = cell.getOriginalValue().getClass().getSimpleName();
				if(clazz.contains("String")){
					getProcessor().addLabel(i, rowNo, cell.getValue());
				}
				if(clazz.contains("Integer") ){
					if(cell.getOriginalValue() != null){
						getProcessor().addInteger(i, rowNo, (Integer) cell.getOriginalValue());
					}
				}
				if(clazz.contains("Long") ){
					if(cell.getOriginalValue() != null){
						getProcessor().addLong(i, rowNo, (Long)cell.getOriginalValue());
					}
				}
				if(clazz.contains("Boolean")){
					if((Boolean) cell.getOriginalValue()){
						getProcessor().addLabel(i, rowNo, "+");
					}
				}
				if(clazz.contains("LocalDate")){
					if(cell.getOriginalValue() != null){
						LocalDate ld = (LocalDate) cell.getOriginalValue();
						getProcessor().addDate(i, rowNo, ld);
					}
				}
				if(clazz.contains("LocalDateTime")){
					if(cell.getOriginalValue() != null){
						LocalDateTime ldt = (LocalDateTime) cell.getOriginalValue();
						getProcessor().addDateTime(i, rowNo, ldt);
					}
				}
				if(clazz.contains("BigDecimal")){
					if(cell.getOriginalValue() != null){
						getProcessor().addDecimal(i, rowNo, ((BigDecimal)cell.getOriginalValue()));
					}
				}
			}
			i++;
		}
	}
	/**
	 * Headers of the table
	 * @param sHeaders
	 * @param row
	 * @return
	 */
	private int placeHeaders(List<TableHeader> sHeaders, int row) {
		if(sHeaders != null && sHeaders.size()>0){
			int i=0;
			for(TableHeader head : sHeaders){
				getProcessor().addCaption(i, row, head.getDisplayValue(), head.getExcelWidth());
				i++;
			}
			return ++row;
		}else{
			return row;
		}
	}
	/**
	 * Place a title on a sheet
	 * @param sTitle
	 * @param size - title width
	 * @param row 
	 * @return
	 */
	private int placeTitle(String sTitle, int size, int row) {
		if(sTitle != null && sTitle.length()>0){
			getProcessor().mergeCells(0,row,size,row);
			getProcessor().addSubChapter(0, row, sTitle);
			return 1;
		}else{
			return 0;
		}
	}

}
