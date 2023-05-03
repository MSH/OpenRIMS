package org.msh.pharmadex2.dto;

import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * It is an internal class to manage legacy data import errors
 * The errors should be collected on the special sheet
 * The message should be displayed on the screen. We don't need to much details in the message
 * Do not use it for data exchange
 * @author alexk
 *
 */
public class LegacyDataErrorsDTO {
	public static final String IMPORT_ERRORS_SHEET = "Import errors";
	private XSSFSheet sheet;
	private String message="";
	private boolean hasAddErrorRow = false;

	public LegacyDataErrorsDTO(XSSFWorkbook workbook) {
		XSSFSheet old = workbook.getSheet(IMPORT_ERRORS_SHEET);
		if(old != null) {
			int index = workbook.getSheetIndex(old);
			workbook.removeSheetAt(index);
		}
		setSheet(workbook.createSheet(IMPORT_ERRORS_SHEET));
	}


	public XSSFSheet getSheet() {
		return sheet;
	}


	public void setSheet(XSSFSheet sheet) {
		this.sheet = sheet;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Is this sheet error sheet
	 * @param sheet
	 * @return
	 */
	public boolean isErrorOrNullSheet(XSSFSheet thisSheet) {
		if(thisSheet!=null) {
			XSSFWorkbook wb = thisSheet.getWorkbook();
			return wb.getSheetIndex(thisSheet) == wb.getSheetIndex(getSheet());
		}else {
			return true;
		}
	}

	/**
	 * Add a row to the errors sheet
	 * @param row
	 * @param maxCellIndex maximum index of cells
	 * @param url 
	 */
	public void add(XSSFRow row, int maxCellIndex, String url) {
		int lastRow = getSheet().getLastRowNum();
		XSSFRow newRow= getSheet().createRow(lastRow+1);
		CellCopyPolicy policy = new CellCopyPolicy();
		policy.setCopyCellValue(true);
		for(int i=0; i<=maxCellIndex; i++) {
			newRow.createCell(i).copyCellFrom(row.getCell(i),policy);
		}
		newRow.createCell(maxCellIndex+1).setCellValue(url);
		hasAddErrorRow = true;
	}

	public void copyHeaderRow(XSSFRow headerRow) {
		XSSFRow newRow = getSheet().createRow(0);
		CellCopyPolicy policy = new CellCopyPolicy();
		policy.setCopyCellValue(true);
		for(int i = 0; i <= headerRow.getLastCellNum(); i++) {
			newRow.createCell(i).copyCellFrom(headerRow.getCell(i), policy);
		}
	}
	
	public boolean hasErrorRows() {
		return hasAddErrorRow;
	}
}
