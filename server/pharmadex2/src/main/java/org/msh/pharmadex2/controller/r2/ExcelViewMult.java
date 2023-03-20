package org.msh.pharmadex2.controller.r2;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.enums.YesNoNA;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.controller.common.POIProcessor;
import org.msh.pharmadex2.dto.DataCollectionDTO;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.LayoutCellDTO;
import org.msh.pharmadex2.dto.LayoutRowDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.WorkflowDTO;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


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
	public static final int START_THING_CONTENT_ROW = 2;
	private static final Logger logger = LoggerFactory.getLogger(ExcelViewMult.class);
	public static final String LABEL_BOOLEAN_TRUE = "+";

	POIProcessor processor = new POIProcessor(new File("report.xlsx"));

	public POIProcessor getProcessor() {
		return processor;
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model,
			//TODO seems as void!
			Workbook workbook,
			HttpServletRequest request,
			HttpServletResponse response)	{

		workbookForDataConfiguration(model, null, workbook);
	}

	/**
	 * Really build the workbook 
	 * @param model
	 * @param mess 
	 * @param workbook
	 */
	public void workbookForDataConfiguration(@SuppressWarnings("rawtypes") Map model, Messages mess, Workbook workbook) {
		Object dato = model.get("data");
		if(dato!=null) {
			if(dato instanceof Map<?,?>) {
				@SuppressWarnings("unchecked")
				Map<String,DataCollectionDTO> data = (Map<String, DataCollectionDTO>) dato;
				for(String key : data.keySet()) {
					placeDataConfigurationSheet(key, mess, data.get(key));
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
	 * @param mess 
	 * @param data - data to place
	 * @return
	 */
	public void placeDataConfigurationSheet(String key, Messages mess, DataCollectionDTO data) {
		getProcessor().createSheet(key,1000);
		int row=0;
		placeTitle(key,10,row);
		row++;
		placeTitle(data.getDescription().getValue(), 10, row);
		row++;
		row = placeHeaders(data.getTable().getHeaders().getHeaders(),row);
		if(data.getTable().getRows().size()>0) {
			row = placeRows(data.getTable().getHeaders().getHeaders(),data.getTable().getRows(),row);
		}else {
			placeTitle(mess.get("badconfiguration"), 10, row);
		}
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
		boolean paint = false;
		for(TableCell cell : row.getRow()){
			paint = false;
			if(cell.getOriginalValue() != null){
				String clazz = cell.getOriginalValue().getClass().getSimpleName();
				if(clazz.contains("String")){
					getProcessor().addLabel(i, rowNo, cell.getValue());
					paint = true;
				}
				if(clazz.contains("Integer") ){
					if(cell.getOriginalValue() != null){
						getProcessor().addInteger(i, rowNo, (Integer) cell.getOriginalValue());
						paint = true;
					}
				}
				if(clazz.contains("Long") ){
					if(cell.getOriginalValue() != null){
						getProcessor().addLong(i, rowNo, (Long)cell.getOriginalValue());
						paint = true;
					}
				}
				if(clazz.contains("Boolean")){
					if((Boolean) cell.getOriginalValue()){
						getProcessor().addLabel(i, rowNo, LABEL_BOOLEAN_TRUE);
						paint = true;
					}
				}
				if(clazz.contains("LocalDate")){
					if(cell.getOriginalValue() != null){
						LocalDate ld = (LocalDate) cell.getOriginalValue();
						getProcessor().addDate(i, rowNo, ld);
						paint = true;
					}
				}
				if(clazz.contains("LocalDateTime")){
					if(cell.getOriginalValue() != null){
						LocalDateTime ldt = (LocalDateTime) cell.getOriginalValue();
						getProcessor().addDateTime(i, rowNo, ldt);
						paint = true;
					}
				}
				if(clazz.contains("BigDecimal")){
					if(cell.getOriginalValue() != null){
						getProcessor().addDecimal(i, rowNo, ((BigDecimal)cell.getOriginalValue()));
						paint = true;
					}
				}
			}
			if(!paint) {
				//создаем пустую ячейку чтоб получились бордеры
				getProcessor().addLabel(i, rowNo, " ");
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
				if(head.getKey().toUpperCase().equals("URL")) {
					getProcessor().addCaption(i, row, head.getDisplayValue(), 32);
				}else
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
	/**
	 * Create workbook contains workflow description
	 * @param dto
	 * @param mess 
	 * @return
	 */
	public XSSFWorkbook workbookForWorkflowConfiguration(WorkflowDTO dto, Messages mess, ClosureService closureServ) {
		getProcessor().initWorkbook(new XSSFWorkbook());
		for(ThingDTO td : dto.getPath()) {
			placeThingSheet(td,dto.getTitle(),mess, closureServ);
		}
		return getProcessor().getWorkbook();
	}
	/**
	 * Place a sheet with thing data.
	 * Title is a name of the sheet
	 * Currently only primitives and dictionaries 
	 * @param td
	 * @param title 
	 * @param mess 
	 * @param objectMapper 
	 * @throws JsonProcessingException 
	 */
	private void placeThingSheet(ThingDTO td, String title, Messages mess, ClosureService closureServ)  {
		getProcessor().createSheet(td.getTitle(),1000);
		getProcessor().addCaption(0, 0, title, 40);
		int rowNo=START_THING_CONTENT_ROW;
		int colNo=0;
		for(LayoutRowDTO row : td.getLayout()) {
			for(LayoutCellDTO cell : row.getCells()) {
				for(String varName :cell.getVariables()) {
					getProcessor().addSubChapter(colNo, rowNo, mess.get(varName), 40);
					Object obj=td.variableByName(varName);
					String val=obj.toString();
					if(obj instanceof FormFieldDTO<?>) {
						FormFieldDTO<?> fld = (FormFieldDTO<?>) obj;
						Object objVal = fld.getValue();
						if(objVal instanceof OptionDTO) {
							//here we do use OptionDTO only for logical
							OptionDTO odto= (OptionDTO) objVal;
							Long lid =  odto.getId()-1;
							val=YesNoNA.values()[lid.intValue()].toString();
						}
						
					}
					if(obj instanceof DictionaryDTO) {
						DictionaryDTO ddto=(DictionaryDTO) obj;
						if(ddto.getPrevSelected().size()==1) {
							try {
								Concept con=closureServ.loadConceptById(ddto.getPrevSelected().get(0));
							val=con.getIdentifier();
							} catch (ObjectNotFoundException e) {
								
							}
							
						}
					}
					getProcessor().addLabel(colNo+1, rowNo, val, 60);
					rowNo++;				//order
				}
				colNo++;				//columns for label and value
				colNo++;			
				rowNo=2;
			}
			rowNo++;					//rows
			colNo=0;
		}
	}
	/**
	 * Create a sheet and place data collection references on it
	 * @param title the sheet name
	 * @param references
	 * @param workbook
	 */
	public void dataCollectionReferences(String title, TableQtb references, XSSFWorkbook workbook) {
		getProcessor().createSheet(title,1000);
		int row=0;
		placeHeaders(references.getHeaders().getHeaders(), row);
		row++;
		placeRows(references.getHeaders().getHeaders(), references.getRows(),row);
	}

	/**
	 * Read rows from the sheet to the table from the rowNo, i.e., reverse placeRows
	 * @param sheet
	 * @param rowNo start from this row
	 * @param colNo start from this column
	 * @param table
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public TableQtb readRows(XSSFSheet sheet, int rowNo, int colNo, TableQtb table) throws ObjectNotFoundException {
		table.getRows().clear();
		TableRow tr = readRow(sheet,rowNo, colNo, table);
		while(tr != null) {
			table.getRows().add(tr);
			rowNo++;
			 tr = readRow(sheet,rowNo, colNo, table);
		}
		return table;
	}
	/**
	 * Read a row while column in colNo is not empty
	 * @param sheet
	 * @param rowNo row number in Excel sheet from 0
	 * @param colNo column number in Excel sheet from 0. This column must contain a value
	 * @param table with headers to import into rows
	 * @return null if row is empty or doesn't exist
	 * @throws ObjectNotFoundException 
	 */
	private TableRow readRow(XSSFSheet sheet, int rowNo, int colNo, TableQtb table) throws ObjectNotFoundException {
		XSSFRow row = sheet.getRow(rowNo);
		TableRow tr = TableRow.instanceOf(rowNo);
		int cellnum=colNo;
		if(row != null) {
			XSSFCell cell = row.getCell(colNo);
			if(cell.getRawValue() != null) {
				for(TableHeader header : table.getHeaders().getHeaders()) {
					TableCell tc =readCell(header, row.getCell(cellnum));
					if(tc!=null) {
						tr.getRow().add(tc);
					}else {
						throw new ObjectNotFoundException("unsupported cell type for export "+header.getColumnType(),logger);
					}
					cellnum++;
				}
			}
			return tr;
		}else {
			return null;
		}
	}
	/**
	 * Read a cell from Excel Row to a cell in TableQtb row
	 * @param header
	 * @param cell
	 * @return
	 */
	private TableCell readCell(TableHeader th, XSSFCell cell) {
		TableCell ret = null;
		switch(th.getColumnType()) {
		case TableHeader.COLUMN_LONG:
			ret = TableCell.instanceOf(th.getKey(), getCellValueLong(cell), LocaleContextHolder.getLocale());
			break;
		case TableHeader.COLUMN_STRING:
		case TableHeader.COLUMN_I18:
		case TableHeader.COLUMN_I18LINK:
		case TableHeader.COLUMN_LINK:
			ret=TableCell.instanceOf(th.getKey(), getCellValueString(cell));
			break;
		case TableHeader.COLUMN_BOOLEAN_CHECKBOX:
		case TableHeader.COLUMN_BOOLEAN_RADIO:
			ret=TableCell.instanceOf(th.getKey(), getCellValueBool(cell));
			break;
		case TableHeader.COLUMN_DECIMAL:
			ret=TableCell.instanceOf(th.getKey(), getCellValueBigDecimal(cell),LocaleContextHolder.getLocale());
			break;
		case TableHeader.COLUMN_LOCALDATE:
			ret=TableCell.instanceOf(th.getKey(), getCellValueLocalDate(cell),LocaleContextHolder.getLocale());
			break;
		case TableHeader.COLUMN_LOCALDATETIME:
			ret=TableCell.instanceOf(th.getKey(), getCellValueLocalDateTime(cell),LocaleContextHolder.getLocale());
			break;
		default:
		}
		return ret;
	}


	/**
	 * Get cell vsalue as a LocalDateTime
	 * @param cell
	 * @return LocalDateTime.now() if cell is null
	 */
	private LocalDateTime getCellValueLocalDateTime(XSSFCell cell) {
		LocalDateTime ret = LocalDateTime.now();
		if(cell != null) {
			Date dt = cell.getDateCellValue();
			ret = BoilerService.dateAsLocalDateTime(dt);
		}
		return ret;
	}

	/**
	 * GEt cell value as a local date
	 * @param cell
	 * @return now if cell is null
	 */
	private LocalDate getCellValueLocalDate(XSSFCell cell) {
		LocalDate ret = LocalDate.now();
		if(cell != null) {
			try {
				Date dt = cell.getDateCellValue();
				ret = BoilerService.dateToLocalDate(dt);
			} catch (Exception e) {
				//nothing to do
			}
		}
		return ret;
	}

	/**
	 * Get a big decimal value from the cell
	 * @param cell
	 * @return
	 */
	private BigDecimal getCellValueBigDecimal(XSSFCell cell) {
		BigDecimal ret=BigDecimal.ZERO;
		if(cell != null) {
			Double dv = getCellValueDouble(cell);
			ret= BigDecimal.valueOf(dv);
		}
		return ret;
	}

	/**
	 * Get boolean value in the cell
	 * @param row
	 * @param col
	 * @return
	 */
	private boolean getCellValueBool(XSSFCell cell) {
		if(cell != null) {
			String valStr=getCellValueString(cell);
			if(valStr==null) {
				return false;
			}
			if(valStr.equalsIgnoreCase(ExcelViewMult.LABEL_BOOLEAN_TRUE)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * GEt cell value as a long number
	 * @param row
	 * @param col
	 * @return
	 */
	private Long getCellValueLong(XSSFCell cell) {
		Double dRet=0.0;
		if(cell != null) {
			dRet=getCellValueDouble(cell);
		}
		return dRet.longValue();
	}

	/**
	 * Get cell value as a string. No exceptions
	 * @param row
	 * @param col
	 * @return
	 */
	public String getCellValueString(XSSFCell cell) {
		String ret="";
		if(cell != null) {
			try {
				ret=cell.getStringCellValue();
			} catch (Exception e) {
				double regNod = cell.getNumericCellValue();
				DecimalFormat df = new DecimalFormat("#");
				df.setMaximumFractionDigits(0);
				df.setMinimumFractionDigits(0);
				ret=df.format(regNod);
			}
		}
		ret=replaceNonPrintCh(ret);
		return ret.trim();
	}

	/**
	 * Get cell value as a string. No exceptions
	 * Non numeric values will be 0.0
	 * @param row
	 * @param col
	 * @return
	 */
	public Double getCellValueDouble(XSSFCell cell) {
		Double ret = 0.0;
		if(cell!=null) {
			try {
				ret = cell.getNumericCellValue();
			} catch (Exception e) {
				//nothing to do
			}
		}
		return ret;
	}

	/**
	 *replace non-printable characters
	 */
	public String replaceNonPrintCh(String val) {
		// erases all the ASCII control characters
		val = val.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
		// removes non-printable characters from Unicode
		val = val.replaceAll("\\p{C}", "");
		return val;
	}

}
