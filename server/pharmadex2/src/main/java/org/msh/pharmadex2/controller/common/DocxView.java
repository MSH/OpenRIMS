package org.msh.pharmadex2.controller.common;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlToken;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableHeader;
import org.msh.pdex2.dto.table.TableQtb;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pharmadex2.dto.FileResourceDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.r2.ResourceService;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.AbstractView;

/**
 * Create a view from MS Word docx file
 * @author alexk
 *
 */
public class DocxView extends AbstractView{
	
	private BoilerService boilerServ;

	private static final Logger logger = LoggerFactory.getLogger(DocxView.class);
	/**
	 * pattern to find parameter definition
	 */
	private static Pattern pat = Pattern.compile("[\\#,\\$]\\{.+?\\}");
	/**
	 * Byte input stream to read the Word document from a file or from BLOB database field
	 */
	private InputStream inputDocument;
	/**
	 * How many parameters has been resolved
	 */
	private int resolved = 0;
	private XWPFDocument doc;
	private List<ParagraphByTableQtb> listByPaint = null;

	class ParagraphByTableQtb{
		private XWPFTableCell cell = null;
		private XWPFParagraph paragraph = null;
		private TableQtb table = null;

		public ParagraphByTableQtb(XWPFTableCell cell, XWPFParagraph paragraph, TableQtb table) {
			super();
			this.cell = cell;
			this.paragraph = paragraph;
			this.table = table;
		}
		public XWPFTableCell getCell() {
			return cell;
		}
		public XWPFParagraph getParagraph() {
			return paragraph;
		}
		public TableQtb getTable() {
			return table;
		}
	}

	public DocxView(InputStream _inputDocument, BoilerService _boilerServ) {
		super();
		setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		this.inputDocument=_inputDocument;
		setBoilerServ(_boilerServ);
	}

	public BoilerService getBoilerServ() {
		return boilerServ;
	}

	public void setBoilerServ(BoilerService boilerServ) {
		this.boilerServ = boilerServ;
	}

	public InputStream getInputDocument() {
		return inputDocument;
	}

	public void setInputDocument(InputStream inputDocument) {
		this.inputDocument = inputDocument;
	}

	public int getResolved() {
		return resolved;
	}

	public void setResolved(int resolved) {
		this.resolved = resolved;
	}

	private void incResolved(int count) {
		this.resolved += count;
	}

	public XWPFDocument getDoc() {
		return doc;
	}

	public void setDoc(XWPFDocument doc) {
		this.doc = doc;
	}

	@Override
	protected boolean generatesDownloadContent() {
		return true;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		resolveDocument(model,true);
		response.setContentType(getContentType());
		ServletOutputStream out = response.getOutputStream();
		getDoc().write(out);
		getDoc().close();
	}


	/**
	 * Create a model using variables from input stream
	 * NOTE - input stream will gone! It is the first step.
	 * @return
	 * @throws IOException 
	 * @throws ObjectNotFoundException 
	 */
	public Map<String, Object> initModel() throws ObjectNotFoundException, IOException {
		Map<String,Object> ret = new LinkedHashMap<String, Object>();
		resolveDocument(ret, false);
		return ret;
	}

	/**
	 * Resolve model parameters in the doc
	 * @param doc
	 * @param model 
	 * @param resolve - true resolve, false - create a model
	 * @throws ObjectNotFoundException 
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */
	public void resolveDocument(Map<String, Object> model, boolean resolve) throws ObjectNotFoundException, IOException{
		setDoc(new XWPFDocument(getInputDocument()));
		getInputDocument().close();
		// заполним документ строковыми значениями
		for (XWPFParagraph par : getDoc().getParagraphs()){
			resolveParagraph(par, null, model, resolve);
		}
		//sometimes, the document's layout may be represent as a table or a set of tables
		resolveTables(getDoc().getTables(), model, resolve);

		if(listByPaint != null) {
			for(ParagraphByTableQtb it:listByPaint) {
				paintTable(it.getCell(), it.getParagraph(), it.getTable());
			}
		}
	}



	/**
	 * Просматриваем все таблицы в документе, включая вложенные 
	 * Заполним все строкове переменные, которые в них найдем
	 * ТАкже запоминаем в список ячейки и параграфы в которые нужно добавить наши таблицы
	 */
	private void resolveTables(List<XWPFTable> listTable, Map<String, Object> model, boolean resolve) throws ObjectNotFoundException {
		for (XWPFTable tbl : listTable) {
			for (XWPFTableRow row : tbl.getRows()) {
				for (XWPFTableCell cell : row.getTableCells()) {
					List<XWPFTable> list = cell.getTables();
					if(list != null && list.size() > 0) {
						resolveTables(list, model, resolve);
					}else {
						for (XWPFParagraph par : cell.getParagraphs()) {
							resolveParagraph(par, cell, model, resolve);
						}
					}
				}
			}
		}
	}

	/**
	 * Resolve paragraph data
	 * @param par paragraph
	 * @param model 
	 * @param resolve - resolve, otherwise - create a model
	 * @return quantity of EL
	 * @throws ObjectNotFoundException 
	 */
	private void resolveParagraph(XWPFParagraph par, XWPFTableCell cell, Map<String, Object> model, boolean resolve) throws ObjectNotFoundException{
		int ret = 0;
		XWPFRun prevRun = null;
		// normalize runs
		for(XWPFRun run : par.getRuns()){
			if(run != null){
				if(prevRun==null){
					prevRun=run;
				}else{
					if(compareRuns(prevRun,run)){
						if(run.getText(0)==null) {
							//prevRun.setText(prevRun.getText(0)+"",0);
						}else {
							prevRun.setText(prevRun.getText(0) + run.getText(0),0);
						}
						run.setText("",0);
					}else{
						prevRun = run;
					}
				}
			}
		}
		// resolve
		for(XWPFRun run:par.getRuns()){
			if(run.getText(0) != null){
				Matcher match = pat.matcher(run.getText(0));
				StringBuffer sb = new StringBuffer();
				while(match.find()){
					String toEval = match.group();
					Object repl = getReplacementFromModel(toEval, model,resolve);
					if(repl != null){
						if(repl instanceof String) {
							String replStr = (String) repl;
							match.appendReplacement(sb, replStr); //real evaluation
						}
						if(repl instanceof Long) {
							match.appendReplacement(sb, "");
							run=createImage((Long)repl, run);
						}
						if(repl instanceof TableQtb) { 
							match.appendReplacement(sb, ""); 
							TableQtb table = (TableQtb) repl; 
							if(listByPaint == null)
								listByPaint = new ArrayList<ParagraphByTableQtb>();
							ParagraphByTableQtb it = new ParagraphByTableQtb(cell, par, table);
							listByPaint.add(it);
						}
					}else{
						throw new ObjectNotFoundException("Impossibe to evaluate " + toEval, logger);
					}
					ret++;
				}
				match.appendTail(sb);
				run.setText(sb.toString(),0); //test resolve
			}
		}

		incResolved(ret);
	}
	/**
	 * Load an image from the database and insert it into the run
	 * @param resConceptID
	 * @param run
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	private XWPFRun createImage(Long resConceptID, XWPFRun run) throws ObjectNotFoundException {
		FileResourceDTO res = getBoilerServ().fileByConceptId(resConceptID);
		run=insertPicture(res.getFile(), res.getMime(),run);
		return run;
	}

	/**
	 * Paint a table starting with the run
	 * @param run
	 * @param table
	 */
	private void paintTable(XWPFTableCell cell, XWPFParagraph par, TableQtb table) {
		XWPFTable tableDoc = null;
		XWPFTableRow headerRow = null;
		if(cell == null) {
			tableDoc = par.getDocument().insertNewTbl(par.getCTP().newCursor());
			headerRow = tableDoc.getRow(0);
			headerRow.setRepeatHeader(true);
		}else {
			tableDoc = cell.insertNewTbl(par.getCTP().newCursor());
			headerRow = tableDoc.createRow();
		}
		for(int i = 0; i < table.getHeaders().getHeaders().size(); i++) {
			if (headerRow.getCell(i) == null) {
				headerRow.createCell();
			}
		}

		setTableBorders(tableDoc);

		if(tableDoc != null) {
			CTTblWidth tableWidth = tableDoc.getCTTbl().addNewTblPr().addNewTblW();
			tableWidth.setType(STTblWidth.PCT);
			tableWidth.setW(BigInteger.valueOf(100*50));

			tableDoc = createHeaders(tableDoc, headerRow, table.getHeaders().getHeaders());
			for(TableRow row : table.getRows()) {
				createRow(table.getHeaders().getHeaders(), tableDoc, row);
			}
		}
	}

	/**
	 * Add a new row and fill cells by data
	 * @param headers 
	 * @param tableDoc
	 * @param row
	 * @return a new created row
	 */
	private XWPFTableRow createRow(List<TableHeader> headers, XWPFTable tableDoc, TableRow row) {
		XWPFTableRow ret = tableDoc.createRow();
		int cellIndex=0;
		for(TableCell cell :row.getRow()) {
			if (ret.getCell(cellIndex) == null) 
				ret.createCell();

			switch(headers.get(cellIndex).getColumnType()) {
			case TableHeader.COLUMN_DECIMAL:
			case TableHeader.COLUMN_LONG:
				setTextAndAlignCell(ParagraphAlignment.RIGHT,cell.getValue(),ret.getCell(cellIndex),false);
				break;
			case TableHeader.COLUMN_LOCALDATE:
			case TableHeader.COLUMN_LOCALDATETIME:
				setTextAndAlignCell(ParagraphAlignment.CENTER,cell.getValue(),ret.getCell(cellIndex),false);
				break;
			default:
				ret.getCell(cellIndex).setText(cell.getValue());
			}
			cellIndex++;
		}
		return ret;
	}

	/**
	 * Create headers just after the current run
	 * @param tableDoc
	 * @param headers
	 * @return 
	 */
	private XWPFTable createHeaders(XWPFTable tableDoc, XWPFTableRow headerRow, List<TableHeader> headers) {
		//create header
		//XWPFTableRow headerRow = tableDoc.getRow(0);
		int cellIndex=0;
		for(TableHeader header : headers) {
			XWPFTableCell cell = headerRow.getCell(cellIndex);
			setTextAndAlignCell(ParagraphAlignment.CENTER, header.getDisplayValue(), cell,true);
			if(header.getExcelWidth()>0) {
				CTTblWidth cellWidth = cell.getCTTc().addNewTcPr().addNewTcW();
				cellWidth.setW(BigInteger.valueOf(header.getExcelWidth()*50));
				cellWidth.setType(STTblWidth.PCT);
			}
			cellIndex++;
		}

		return tableDoc;
	}
	/**
	 * Align a value inside a cell
	 * @param text 
	 * @param header
	 * @param cell
	 * @param isHeader - this cell is from header
	 */
	public void setTextAndAlignCell(ParagraphAlignment align, String text, XWPFTableCell cell, boolean isHeader) {
		XWPFParagraph para = cell.getParagraphs().get(0);
		// create a run to contain the content
		XWPFRun rh = para.createRun();
		String[] lines = text.split("<br>");
		rh.setText(lines[0], 0); // set first line into XWPFRun
		for(int i=1;i<lines.length;i++){
			// add break and insert new text
			rh.addBreak();
			rh.setText(lines[i]);
		}
		rh.setBold(isHeader);
		para.setAlignment(align);
	}

	/**
	 * Replace a parameter expression to a parameter value
	 * in case not found, no replace
	 * Case INSENSETIVE!
	 * @param toEval
	 * @param model
	 * @param resolve - true resolve, false - create model
	 * @return resolved string or empty string if !resolve 
	 * @throws ObjectNotFoundException 
	 */
	private Object getReplacementFromModel(String toEval, Map<String, Object> model,boolean resolve) throws ObjectNotFoundException {
		int begIndex = toEval.indexOf("{");
		int endIndex = toEval.indexOf("}");
		String varName = toEval.substring(begIndex + 1, endIndex);
		Object ret = model.get(varName);
		if(ret != null) {
			return ret;
		}else {
			if(resolve) {
				throw new ObjectNotFoundException("Variable not found " + varName, logger);
			}else {
				model.put(varName, new Object());
				return "";
			}
		}
	}

	/**
	 * set all table borders by insert table
	 * @param tableDoc
	 */
	private void setTableBorders(XWPFTable tableDoc) {
		tableDoc.getCTTbl().addNewTblPr().addNewTblBorders().addNewLeft().setVal(
				org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder.SINGLE);
		tableDoc.getCTTbl().getTblPr().getTblBorders().addNewRight().setVal(
				org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder.SINGLE);
		tableDoc.getCTTbl().getTblPr().getTblBorders().addNewTop().setVal(
				org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder.SINGLE);
		tableDoc.getCTTbl().getTblPr().getTblBorders().addNewBottom().setVal(
				org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder.SINGLE);
		tableDoc.getCTTbl().getTblPr().getTblBorders().addNewInsideH().setVal(
				org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder.SINGLE);
		tableDoc.getCTTbl().getTblPr().getTblBorders().addNewInsideV().setVal(
				org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder.SINGLE);
	}

	/**
	 * Sometimes one paragraph belong to several runs
	 * @param prev
	 * @param actual
	 * @return
	 */
	public boolean compareRuns(XWPFRun prev, XWPFRun actual){
		return	prev.getCharacterSpacing() == actual.getCharacterSpacing()
				&& prev.getKerning() == actual.getKerning()
				&& prev.getSubscript().equals(actual.getSubscript())
				&& prev.getUnderline().equals(actual.getUnderline())
				&& prev.isBold() == actual.isBold()
				&& prev.isCapitalized() == actual.isCapitalized()
				&& prev.isDoubleStrikeThrough() == actual.isDoubleStrikeThrough()
				&& prev.isEmbossed() == actual.isEmbossed()
				&& prev.isHighlighted() == actual.isHighlighted()
				&& prev.isImprinted() == actual.isImprinted()
				&& prev.isItalic() == actual.isItalic()
				&& prev.isShadowed() == actual.isShadowed()
				&& prev.isSmallCaps() == actual.isSmallCaps()
				&& prev.isStrikeThrough() == actual.isStrikeThrough()
				&& prev.getEmbeddedPictures().size()==0;
	}
	/**
	 * Insert an inline picture to the run
	 * @param picStream
	 * @param mime
	 * @param run 
	 * @return
	 * @throws ObjectNotFoundException 
	 * @throws IOException 
	 */
	public XWPFRun insertPicture(byte[] picture, String mime, XWPFRun run) throws ObjectNotFoundException {
		ByteArrayInputStream istream =new ByteArrayInputStream(picture);
		byte[] im;
		try {
			int pictureType=pictureType(mime);
			BufferedImage img = null;
			if(pictureType!=-1) {
				img = ImageIO.read(istream);
				im=picture;
			}else {
				pictureType=Document.PICTURE_TYPE_PNG;
				Path emptyImagePath = Paths.get("src","main","resources", "static", "img", "notfound.png");
				File emptyImage = emptyImagePath.toFile();
				img=ImageIO.read(emptyImage);
				im=IOUtils.toByteArray(new FileInputStream(emptyImage));
			}
			int height= img.getHeight()*9525;
			int width = img.getWidth()*9525;
			String blipId = run.getDocument().addPictureData(im, pictureType);
			int id=run.getDocument().getNextPicNameNumber(pictureType);
					CTInline inline = run.getCTR().addNewDrawing().addNewInline();
			String picXml = "" +
					"<a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">" +
					"   <a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">" +
					"      <pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">" +
					"         <pic:nvPicPr>" +
					"            <pic:cNvPr id=\"" + id + "\" name=\"Generated\"/>" +
					"            <pic:cNvPicPr/>" +
					"         </pic:nvPicPr>" +
					"         <pic:blipFill>" +
					"            <a:blip r:embed=\"" + blipId + "\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"/>" +
					"            <a:stretch>" +
					"               <a:fillRect/>" +
					"            </a:stretch>" +
					"         </pic:blipFill>" +
					"         <pic:spPr>" +
					"            <a:xfrm>" +
					"               <a:off x=\"0\" y=\"0\"/>" +
					"               <a:ext cx=\"" + width + "\" cy=\"" + height + "\"/>" +
					"            </a:xfrm>" +
					"            <a:prstGeom prst=\"rect\">" +
					"               <a:avLst/>" +
					"            </a:prstGeom>" +
					"         </pic:spPr>" +
					"      </pic:pic>" +
					"   </a:graphicData>" +
					"</a:graphic>";

			XmlToken xmlToken =  XmlToken.Factory.parse(picXml);
			inline.set(xmlToken);
			inline.setDistT(0);
			inline.setDistB(0);
			inline.setDistL(0);
			inline.setDistR(0);
			CTPositiveSize2D extent = inline.addNewExtent();
			extent.setCx(width);
			extent.setCy(height);
			CTNonVisualDrawingProps docPr = inline.addNewDocPr();
			docPr.setId(id);
			docPr.setName("Picture " + id);
			docPr.setDescr("Generated");
		} catch (IOException | InvalidFormatException | XmlException e) {
			throw new ObjectNotFoundException(e, logger);
		}
		return run;
	}
	/**
	 * Picture type by mime string
	 * @param mime
	 * @return -1 - wrong format
	 * @throws ObjectNotFoundException 
	 */
	private int pictureType(String mime) throws ObjectNotFoundException {
		String m = mime.toUpperCase();
		if(m.contains("IMAGE")) {
			if(m.contains("JPG")){
				return Document.PICTURE_TYPE_JPEG;
			}
			if(m.contains("JPEG")){
				return Document.PICTURE_TYPE_JPEG;
			}
			if(m.contains("PNG")){
				return Document.PICTURE_TYPE_PNG;
			}
		}
		return -1;
	}

}