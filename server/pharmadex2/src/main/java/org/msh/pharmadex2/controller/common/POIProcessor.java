package org.msh.pharmadex2.controller.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * This class contains POI specific things for Excel views
 * 
 * @author Alexey Kurasov
 *
 */
public class POIProcessor {

	private File excelFile = null;
	private XSSFWorkbook workbook = null;
	private XSSFSheet currentSheet= null;
	private XSSFCellStyle generalStyle;
	private XSSFCellStyle subChapterStyle;
	private XSSFCellStyle captionStyle;
	private XSSFCellStyle labelStyle;
	private XSSFCellStyle labelCenterStyle;
	private XSSFCellStyle dateStyle;
	private XSSFCellStyle dateMYStyle;
	private XSSFCellStyle intStyle;
	private XSSFCellStyle totalStyle;
	private XSSFCellStyle decimalStyle;
	private XSSFCellStyle decimalTotalStyle;
	private XSSFCellStyle decimalSubChapterStyle;
	private Font captionFont=null;
	private Font dataFont=null;

	protected final String[] ALPHA = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
			"AA","AB","AC","AD","AE","AF","AG","AH","AI","AJ","AK","AL","AM","AN","AO","AP","AQ","AR","AS","AT","AU","AV","AW","AX","AY","AZ",
			"BA","BB","BC","BD","BE","BF","BG","BH","BI","BJ","BK","BL","BM","BN","BO","BP","BQ","BR","BS","BT","BU","BV","BW","BX","BY","BZ",
			"CA","CB","CC","CD","CE","CF","CG","CH","CI","CJ","CK","CL","CM","CN","CO","CP","CQ","CR","CS","CT","CU","CV","CW","CX","CY","CZ",
			"DA","DB","DC","DD","DE","DF","DG","DH","DI","DJ","DK","DL","DM","DN","DO","DP","DQ","DR","DS","DT","DU","DV","DW","DX","DY","DZ",
			"EA","EB","EC","ED","EE","EF","EG","EH","EI","EJ","EK","EL","EM","EN","EO","EP","EQ","ER","ES","ET","EU","EV","EW","EX","EY","EZ"};

	/**
	 * Only valid constructor
	 * @param excelFile
	 */
	public POIProcessor(File excelFile) {
		this.excelFile = excelFile;
	}



	public XSSFWorkbook getWorkbook() {
		if(workbook == null){
			workbook = new XSSFWorkbook();
			initStyles();
		}
		return workbook;
	}



	private void initStyles() {
		setCaptionFont(workbook.createFont());
		setDataFont(workbook.createFont());
		setGeneralStyle(workbook.createCellStyle());
		setSubChapterStyle(workbook.createCellStyle());
		setCaptionStyle(workbook.createCellStyle());
		setLabelStyle(workbook.createCellStyle());
		setLabelCenterStyle(workbook.createCellStyle());
		setDateStyle(workbook.createCellStyle());
		setDateMYStyle(workbook.createCellStyle());
		setIntStyle(workbook.createCellStyle());
		setTotalStyle(workbook.createCellStyle());
		setDecimalStyle(workbook.createCellStyle());
		setDecimalTotalStyle(workbook.createCellStyle());
		setDecimalSubChapterStyle(workbook.createCellStyle());
	}
	/**
	 * Workbook is provided
	 * @param workbook
	 */
	public void initWorkbook(Workbook workbook) {
		this.workbook = (XSSFWorkbook) workbook;
		initStyles();
	}
	
	
	public Font getCaptionFont() {
		return captionFont;
	}



	public void setCaptionFont(Font _caption) {
		this.captionFont = _caption;
		assignGeneralFont(captionFont);
		captionFont.setBold(true);
	}



	public Font getDataFont() {
		return dataFont;
	}



	public void setDataFont(Font _data) {
		this.dataFont = _data;
		assignGeneralFont(dataFont);
	}



	private void assignGeneralFont(Font font) {
		font.setFontName("Times");
		font.setFontHeightInPoints((short) 10);
	}



	public XSSFCellStyle getIntStyle() {
		return intStyle;
	}



	public void setIntStyle(XSSFCellStyle _intStyle) {
		this.intStyle = _intStyle;
		setCommonStyle(intStyle);
		CreationHelper createHelper = getWorkbook().getCreationHelper();
		intStyle.setDataFormat(createHelper.createDataFormat().getFormat(BuiltinFormats.getBuiltinFormat(3)));
		intStyle.setAlignment(HorizontalAlignment.RIGHT);
	}



	public XSSFCellStyle getTotalStyle() {
		return totalStyle;
	}



	public void setTotalStyle(XSSFCellStyle _totalStyle) {
		this.totalStyle = _totalStyle;
		setCommonStyle(totalStyle);
		CreationHelper createHelper = getWorkbook().getCreationHelper();
		totalStyle.setDataFormat(createHelper.createDataFormat().getFormat(BuiltinFormats.getBuiltinFormat(3)));
		totalStyle.setAlignment(HorizontalAlignment.RIGHT);
		totalStyle.setFont(getCaptionFont());
	}



	public XSSFCellStyle getDecimalStyle() {
		return decimalStyle;
	}



	public void setDecimalStyle(XSSFCellStyle _decimalStyle) {
		this.decimalStyle = _decimalStyle;
		setCommonStyle(decimalStyle);
		CreationHelper createHelper = getWorkbook().getCreationHelper();
		decimalStyle.setDataFormat(createHelper.createDataFormat().getFormat(BuiltinFormats.getBuiltinFormat(4)));
		decimalStyle.setAlignment(HorizontalAlignment.RIGHT);
	}




	public XSSFCellStyle getDecimalTotalStyle() {
		return decimalTotalStyle;
	}



	public void setDecimalTotalStyle(XSSFCellStyle _decimalTotalStyle) {
		this.decimalTotalStyle = _decimalTotalStyle;
		setCommonStyle(decimalStyle);
		CreationHelper createHelper = getWorkbook().getCreationHelper();
		decimalTotalStyle.setDataFormat(createHelper.createDataFormat().getFormat(BuiltinFormats.getBuiltinFormat(4)));
		decimalTotalStyle.setAlignment(HorizontalAlignment.RIGHT);
		decimalTotalStyle.setFont(getCaptionFont());
	}



	public XSSFCellStyle getDecimalSubChapterStyle() {
		return decimalSubChapterStyle;
	}



	public void setDecimalSubChapterStyle(XSSFCellStyle _decimalSubChapterStyle) {
		this.decimalSubChapterStyle = _decimalSubChapterStyle;
		setCommonStyle(decimalSubChapterStyle);
		CreationHelper createHelper = getWorkbook().getCreationHelper();
		decimalSubChapterStyle.setDataFormat(createHelper.createDataFormat().getFormat(BuiltinFormats.getBuiltinFormat(4)));
		decimalSubChapterStyle.setAlignment(HorizontalAlignment.RIGHT);
		decimalSubChapterStyle.setFont(getCaptionFont());
		colorizeIt(decimalSubChapterStyle);
	}



	public XSSFCellStyle getGeneralStyle() {
		return generalStyle;
	}



	public void setGeneralStyle(XSSFCellStyle _generalStyle) {
		this.generalStyle = _generalStyle;
		setCommonStyle(generalStyle);
	}



	public XSSFCellStyle getSubChapterStyle() {
		return subChapterStyle;
	}



	public void setSubChapterStyle(XSSFCellStyle _subChapter) {
		subChapterStyle = _subChapter;
		colorizeIt(subChapterStyle);
		setCommonStyle(subChapterStyle);
		subChapterStyle.setWrapText(true);
		subChapterStyle.setFont(getCaptionFont());
	}


	public XSSFCellStyle getCaptionStyle() {
		return captionStyle;
	}



	public void setCaptionStyle(XSSFCellStyle _captionStyle) {
		this.captionStyle = _captionStyle;
		colorizeIt(captionStyle);
		setCommonStyle(captionStyle);
		captionStyle.setWrapText(true);
		captionStyle.setFont(getCaptionFont());
		captionStyle.setAlignment(HorizontalAlignment.CENTER);
	}



	/**
	 * Add uniform background color to the style
	 * @param style
	 */
	private void colorizeIt(XSSFCellStyle style) {
		//XSSFColor myColor = new XSSFColor(new java.awt.Color(255,255,204));
		 IndexedColorMap colorMap = workbook.getStylesSource().getIndexedColors();
		XSSFColor myColor = new XSSFColor(new java.awt.Color(255,255,204),colorMap);
		style.setFillForegroundColor(myColor);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	}


	/**
	 * common part for any style
	 * @param style
	 */
	private void setCommonStyle(XSSFCellStyle style) {
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFont(getDataFont());
	}



	public XSSFCellStyle getLabelStyle() {
		return labelStyle;
	}



	public void setLabelStyle(XSSFCellStyle _label) {
		this.labelStyle = _label;
		setCommonStyle(labelStyle);
		labelStyle.setWrapText(true);
		labelStyle.setFont(getDataFont());
	}



	public XSSFCellStyle getLabelCenterStyle() {
		return labelCenterStyle;
	}



	public void setLabelCenterStyle(XSSFCellStyle _labelCenterStyle) {
		this.labelCenterStyle = _labelCenterStyle;
		setCommonStyle(labelCenterStyle);
		labelCenterStyle.setWrapText(true);
		labelCenterStyle.setFont(getDataFont());
		labelCenterStyle.setAlignment(HorizontalAlignment.CENTER);
	}



	public XSSFCellStyle getDateStyle() {
		return dateStyle;
	}



	public void setDateStyle(XSSFCellStyle _date) {
		this.dateStyle = _date;
		setCommonStyle(dateStyle);
		CreationHelper createHelper = getWorkbook().getCreationHelper();
		dateStyle.setDataFormat(createHelper.createDataFormat().getFormat(BuiltinFormats.getBuiltinFormat(0xe)));
		dateStyle.setAlignment(HorizontalAlignment.RIGHT);

	}



	public XSSFCellStyle getDateMYStyle() {
		return dateMYStyle;
	}



	public void setDateMYStyle(XSSFCellStyle _dateMYStyle) {
		this.dateMYStyle = _dateMYStyle;
		setCommonStyle(dateMYStyle);
		CreationHelper createHelper = getWorkbook().getCreationHelper();
		dateMYStyle.setDataFormat(createHelper.createDataFormat().getFormat("mmm-yyyy"));
		dateMYStyle.setAlignment(HorizontalAlignment.CENTER);
	}



	public XSSFSheet getCurrentSheet() {
		return currentSheet;
	}



	public void setCurrentSheet(XSSFSheet currentSheet) {
		this.currentSheet = currentSheet;
	}



	public File getExcelFile() {
		return excelFile;
	}



	public void setExcelFile(File excelFile) {
		this.excelFile = excelFile;
	}



	/**
	 * Create a worksheet with name and position given and set it as the current sheet
	 * to build sequence of sheets pass a big position, f.e. 100
	 * @param name
	 * @param position
	 */
	public void createSheet(String name, int position) {
		if(name.length()>30){
			name = name.substring(0, 29);
		}
		if(getWorkbook().getSheet(name) != null){
			name = "("+getWorkbook().getNumberOfSheets()+")"+name;
			if(name.length()>30){
				name = name.substring(0, 29);
			}
		}
		setCurrentSheet(getWorkbook().createSheet(name));
		int maxPos = getWorkbook().getNumberOfSheets()-1;
		if(position>maxPos){
			position = maxPos;
			if(position<0){
				position=0;
			}
		}
		getWorkbook().setSheetOrder(name, position);
	}


	/**
	 * set column width for the current sheet
	 * @param col
	 * @param width
	 */
	public void setColumnView(int col, int width) {
		getCurrentSheet().setColumnWidth(col, width*256);

	}


	/**
	 * add a sub chapter styled message to the cell given
	 * @param col
	 * @param row
	 * @param message
	 */
	public void addSubChapter(int col, int row, String message) {
		XSSFRow sRow = getRow(row);
		XSSFCell sCell = getCell(sRow, col);
		sCell.setCellType(CellType.STRING);
		sCell.setCellStyle(getSubChapterStyle());
		sCell.setCellValue(message);
	}
	
	/**
	 * add a sub chapter styled message to the cell given, set cell width, zero means default
	 * @param col
	 * @param row
	 * @param message
	 * @param colWidth - cell width in characters
	 */
	public void addSubChapter(int col, int row, String message, int colWidth) {
		XSSFRow sRow = getRow(row);
		XSSFCell sCell = getCell(sRow, col);
		sCell.setCellType(CellType.STRING);
		sCell.setCellStyle(getSubChapterStyle());
		sCell.setCellValue(message);
		if(colWidth>0) {
			setColumnView(col, colWidth);
		}
	}

	/**
	 * Add a label styled message to row and col given
	 * @param col
	 * @param row
	 * @param message
	 */
	public void addLabel(int col, int row, String message) {
		XSSFRow sRow = getRow(row);
		XSSFCell sCell = getCell(sRow, col);
		sCell.setCellStyle(getLabelStyle());
		sCell.setCellValue(message);

	}
	/**
	 * Add a label styled message to row and col given
	 * @param col
	 * @param row
	 * @param message
	 * @param colWidth - width in chars, 0 means deafult
	 */
	public void addLabel(int col, int row, String message, int colWidth) {
		XSSFRow sRow = getRow(row);
		XSSFCell sCell = getCell(sRow, col);
		sCell.setCellStyle(getLabelStyle());
		sCell.setCellValue(message);
		if(colWidth>0) {
			setColumnView(col, colWidth);
		}
	}
	/**
	 * Add a date to row and column given
	 * @param row
	 * @param col
	 * @param date
	 */
	public void addDate(int col, int row, LocalDate ld) {
		XSSFRow sRow = getRow(row);
		XSSFCell sCell = getCell(sRow, col);
		sCell.setCellStyle(getDateStyle());
		if(ld != null){
			Date dt = Date.from(ld.atStartOfDay()
				      .atZone(ZoneId.systemDefault())
				      .toInstant());
			sCell.setCellValue(dt);
		}
	}
	/**
	 * Add date time to row, col given
	 * @param col
	 * @param row
	 * @param ldt
	 */
	public void addDateTime(int col, int row, LocalDateTime ldt) {
		XSSFRow sRow = getRow(row);
		XSSFCell sCell = getCell(sRow, col);
		sCell.setCellStyle(getDateStyle());
		if(ldt != null){
			Date dt = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
			sCell.setCellValue(dt);
		}
	}

	/**
	 * Add integer value to row, col
	 * @param col
	 * @param row
	 * @param value
	 */
	public void addInteger(int col, int row, Integer value) {
		XSSFRow sRow = getRow(row);
		XSSFCell sCell = getCell(sRow, col);
		sCell.setCellStyle(getIntStyle());
		sCell.setCellValue(value);
	}
	
	/**
	 * Add long value to row, col
	 * @param col
	 * @param row
	 * @param value
	 */
	public void addLong(int col, int row, Long value) {
		XSSFRow sRow = getRow(row);
		XSSFCell sCell = getCell(sRow, col);
		sCell.setCellStyle(getIntStyle());
		sCell.setCellValue(value);
	}

	/**
	 * Add centered label value to col, row given
	 * @param col
	 * @param row
	 * @param value
	 */
	public void addCenterLabel(int col, int row, String value) {
		XSSFRow sRow = getRow(row);
		XSSFCell sCell = getCell(sRow, col);
		sCell.setCellStyle(getLabelCenterStyle());
		sCell.setCellValue(value);
	}

	/**
	 * Add caption to col, row and, possible to change column width
	 * @param col
	 * @param row
	 * @param value
	 * @param colWidth if zero - no width change
	 */
	public void addCaption(int col, int row, String value, int colWidth) {
		XSSFRow sRow = getRow(row);
		XSSFCell sCell = getCell(sRow, col);
		sCell.setCellStyle(getCaptionStyle());
		sCell.setCellValue(value);
		if(colWidth>0){
			setColumnView(col, colWidth);
		}
	}

	/**
	 * Add date in mmm-dd format to col, row given
	 * @param col
	 * @param row
	 * @param date
	 */
	public void addDateMY(int col, int row, Date date) {
		XSSFRow sRow = getRow(row);
		XSSFCell sCell = getCell(sRow, col);
		sCell.setCellStyle(getDateMYStyle());
		sCell.setCellValue(date);
	}

	/**
	 * Add total quantity to col, row given
	 * @param col
	 * @param row
	 * @param value
	 */
	public void addTotal(int col, int row, int value) {
		XSSFRow sRow = getRow(row);
		XSSFCell sCell = getCell(sRow, col);
		sCell.setCellStyle(getTotalStyle());
		sCell.setCellValue(value);
	}

	/**
	 * Place value as decimal number to col, row given
	 * @param col
	 * @param row
	 * @param value
	 */
	public void addDecimal(int col, int row, BigDecimal value) {
		XSSFRow sRow = getRow(row);
		XSSFCell sCell = getCell(sRow, col);
		sCell.setCellStyle(getDecimalStyle());
		sCell.setCellValue(value.doubleValue());
	}

	/**
	 * Place value as decimal number with total style to col, row given
	 * @param col
	 * @param row
	 * @param value
	 */
	public void addDecimalTotal(int col, int row, BigDecimal value) {
		XSSFRow sRow = getRow(row);
		XSSFCell sCell = getCell(sRow, col);
		sCell.setCellStyle(getDecimalTotalStyle());
		sCell.setCellValue(value.doubleValue());
	}

	/**
	 * Place value as decimal subchapter to col, row given
	 * @param col
	 * @param row
	 * @param value
	 */
	public void addDecimalSubChapter(int col, int row, BigDecimal value){
		XSSFRow sRow = getRow(row);
		XSSFCell sCell = getCell(sRow, col);
		sCell.setCellStyle(getDecimalSubChapterStyle());
		sCell.setCellValue(value.doubleValue());
	}

	/**
	 * Generate formulas for sum of numbers in columns col from fromRow to toRow
	 * Also collects sum of sums in grandTotal map
	 * @param col zero based
	 * @param fromRow zero based
	 * @param toRow zero based
	 * @param grandTotal to collect formulas
	 * @param isDecimal should be decimal numbers
	 */
	public void addSumFormula(int col, int fromRow, int toRow,
			Map<Integer, String> grandTotal, boolean isDecimal) {
		String formula = "SUM(" + ALPHA[col] + (fromRow + 1) + ":" + ALPHA[col] + (toRow + 1) + ")";
		int formulaRow = toRow + 1;
		addFormula(col, formulaRow, formula, false, isDecimal);
		String s = grandTotal.get(col);
		if (s == null) {
			s = "";
		}
		grandTotal.put(col, s + ALPHA[col] + (formulaRow + 1) + "+");
	}

	/**
	 * Place a formula to col, row given<br>
	 * <b>Integers only!</b>
	 * @param col
	 * @param row
	 * @param formula
	 * @param isFormatTotal if true - format this cell as total
	 * @param isDecimal 
	 */
	public void addFormula(int col, int row, String formula, boolean isFormatTotal, boolean isDecimal) {
		XSSFRow sRow = getRow(row);
		XSSFCell sCell = getCell(sRow, col);
		if(isFormatTotal){
			if(isDecimal){
				sCell.setCellStyle(getDecimalTotalStyle());
			}else{
				sCell.setCellStyle(getTotalStyle());
			}
		}else{
			if(isDecimal){
				sCell.setCellStyle(getDecimalStyle());
			}else{
				sCell.setCellStyle(getIntStyle());
			}
		}
		sCell.setCellFormula(formula);
	}



	/**
	 * get existing or create the new cell in given row
	 * @param sRow
	 * @param col 
	 * @return
	 */
	private XSSFCell getCell(XSSFRow sRow, int col) {
		XSSFCell res = sRow.getCell(col);
		if(res==null){
			res = sRow.createCell(col);
		}
		return res;
	}



	/**
	 * Get existing or create new row for the current sheet
	 * @param row zero based row number
	 * @return
	 */
	private XSSFRow getRow(int row) {
		XSSFRow res = getCurrentSheet().getRow(row);
		if (res == null){
			res = getCurrentSheet().createRow(row);
		}
		return res;
	}


	/**
	 * merge cells in range given
	 * @param colLeft
	 * @param rowLeft
	 * @param colRight
	 * @param rowRight
	 */
	public void mergeCells(int colLeft, int rowLeft, int colRight, int rowRight) {
		// all merged cells should be assigned to style of the very first cell
		XSSFCellStyle style = getCell(getRow(rowLeft), colLeft).getCellStyle();
		for(int row=rowLeft; row<=rowRight; row++){
			XSSFRow myRow = getRow(row);
			for(int col=colLeft; col<=colRight; col++){
				XSSFCell cell = getCell(myRow, col);
				cell.setCellStyle(style);
			}
		}
		getCurrentSheet().addMergedRegion(new CellRangeAddress(
				rowLeft, //first row (0-based)
				rowRight, //last row  (0-based)
				colLeft, //first column (0-based)
				colRight  //last column  (0-based)
				));

	}



	public void save() throws IOException {
		FileOutputStream fileOut = new FileOutputStream(getExcelFile());
		getWorkbook().write(fileOut);
		fileOut.close();
	}


	/**
	 * Set height of the row given
	 * @param row
	 * @param height
	 */
	public void setRowView(int row, int height) {
		XSSFRow theRow = getRow(row);
		theRow.setHeight((short) height);
	}


	/**
	 * Mark part of a cell bold
	 * @param row
	 * @param col
	 * @param mark substring until make bold if not found, no part of string will be bold
	 */
	public void makeBoldLabel(int row, int col, String mark) {
		XSSFRow sRow = getRow(row);
		XSSFCell cell = getCell(sRow, col);
		Font font = getWorkbook().createFont();
		font.setBold(true);
		RichTextString rts = cell.getRichStringCellValue();
		String value = cell.getStringCellValue();
		int index = value.indexOf(mark);
		if(index>0){
			rts.applyFont(0, index, font);
		}

	}


	/**
	 * Enlarge font + 2 points
	 * @param col
	 * @param row
	 */
	public void enlargeFont(int col, int row) {
		XSSFRow sRow = getRow(row);
		XSSFCell cell = getCell(sRow, col);
		XSSFFont font = cell.getCellStyle().getFont();
		XSSFFont newFont = getWorkbook().createFont();
		newFont.setFontHeightInPoints((short) (font.getFontHeightInPoints() + 2));
		newFont.setBold(true);
		newFont.setFontName(font.getFontName());
		RichTextString rts = cell.getRichStringCellValue();
		rts.applyFont(newFont);
		sRow.setHeight((short) (sRow.getHeight()+40));
	}

}
