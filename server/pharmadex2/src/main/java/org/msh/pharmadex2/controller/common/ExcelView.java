package org.msh.pharmadex2.controller.common;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableRow;
import org.springframework.web.servlet.view.document.AbstractXlsxView;


/**
 * Common excel (xlsx) view. Allows create simple excel sheet based on such model:
 * <ul>
 * <li>sheetname - name of the sheet, string
 * <li>title - title of the sheet, string
 * <li>headers - headers of the table - list of TableHeader
 * <li>rows List<List<TableCell>> rows of the table
 * </ul>
 * @author Alex Kurasoff
 *
 */
public class ExcelView extends AbstractXlsxView{

	public static final String ROWS = "rows";
	public static final String HEADERS = "headers";
	public static final String TITLE = "title";
	public static final String SHEETNAME = "sheetname";

	POIProcessor processor = new POIProcessor(new File("Demands.xlsx"));

	public POIProcessor getProcessor() {
		return processor;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void buildExcelDocument(Map<String, Object> model,
			Workbook workbook,
			HttpServletRequest request,
			HttpServletResponse response)
	{
		//VARIABLES REQUIRED IN MODEL
		String sName = (String)model.get(SHEETNAME);
		String sTitle = (String) model.get(TITLE);
		List<TableHeader> sHeaders = (List<TableHeader>)model.get(HEADERS);
		List<TableRow> sRows = (List<TableRow>)model.get(ROWS);

		//BUILD DOC

		getProcessor().initWorkbook(workbook);
		getProcessor().createSheet(sName,0);

		int row = 0;
		int titleLen = 4;
		if(sHeaders != null && sHeaders.size()>=4){
			titleLen=sHeaders.size()-1;
		}
		row = placeTitle(sTitle, titleLen,row);
		row = placeHeaders(sHeaders,row);
		row = placeRows(sHeaders,sRows,row);
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
