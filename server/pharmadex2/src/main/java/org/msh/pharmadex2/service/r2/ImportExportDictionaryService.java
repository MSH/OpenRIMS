package org.msh.pharmadex2.service.r2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.r2.FileResourceRepo;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.DictionaryDTO;
import org.msh.pharmadex2.dto.LegacyDataErrorsDTO;
import org.msh.pharmadex2.dto.ThingDTO;
import org.msh.pharmadex2.dto.auth.UserDetailsDTO;
import org.msh.pharmadex2.service.common.BoilerService;
import org.msh.pharmadex2.service.common.DtoService;
import org.msh.pharmadex2.service.common.EntityService;
import org.msh.pharmadex2.service.common.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImportExportDictionaryService {
	private static final Logger logger = LoggerFactory.getLogger(DictService.class);
	@Autowired
	ClosureService closureServ;
	@Autowired
	DtoService dtoServ;
	@Autowired
	LiteralService literalServ;
	@Autowired
	EntityService entityServ;
	@Autowired
	JdbcRepository jdbcRepo;
	@Autowired
	BoilerService boilerServ;
	@Autowired
	ValidationService validServ;
	@Autowired
	Messages messages;
	@Autowired
	AssemblyService assembServ;
	@Autowired
	ThingService thingServ;
	@Autowired
	DictService dictService;
	@Autowired
	FileResourceRepo fileResourceRepo;

	private XSSFWorkbook wbook = null;
	private XSSFRow headerRow = null;
	private int widthCol = 40*256;
	private String errorFileName = "ErrorImport.xlsx";
	private LegacyDataErrorsDTO errors = null;

	@Transactional
	public ResponseEntity<Resource> exportDictionary(String dictUrl, Long currentId) throws ObjectNotFoundException {
		try {
			String fileName = buildFileName(dictUrl, currentId);

			File file = writeDataToFile(fileName, dictUrl, currentId);
			byte[] bytes = Files.readAllBytes(file.toPath());
			Resource res = new ByteArrayResource(bytes);

			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
					.contentLength(file.length())
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment" + "; filename=\"" + file.getName() +"\"")
					.header("filename", file.getName())
					.body(res);
		} catch (InvalidFormatException | IOException e) {
			e.printStackTrace();
			throw new ObjectNotFoundException("Error export dictionary");
		}
	}

	private File writeDataToFile(String fName, String urldict, Long currentId) throws InvalidFormatException, IOException, ObjectNotFoundException {
		File file = new File(fName);
		wbook = new XSSFWorkbook();
		XSSFSheet sheet = wbook.createSheet(urldict);
		headerRow = sheet.createRow(0);
		XSSFCell cell = headerRow.createCell(0);
		cell.setCellValue(LiteralService.PREF_NAME);
		sheet.setColumnWidth(0, widthCol);
		cell.setCellStyle(headerStyle());

		cell = headerRow.createCell(1);
		cell.setCellValue(LiteralService.DESCRIPTION);
		sheet.setColumnWidth(1, widthCol);
		cell.setCellStyle(headerStyle());

		cell = headerRow.createCell(2);
		cell.setCellValue(LiteralService.URL);
		sheet.setColumnWidth(2, widthCol);
		cell.setCellStyle(headerStyle());

		Concept levelNode = closureServ.loadConceptById(currentId);
		List<Concept> child = literalServ.loadOnlyChilds(levelNode);
		for(int i = 1; i <= child.size(); i++) {
			Concept item = child.get(i - 1);
			if(item.getActive()) {
				XSSFRow row = sheet.createRow(i);
				Map<String, String> literals = literalServ.literals(item);

				Iterator<String> it = literals.keySet().iterator();
				int numcol = 0;
				while(it.hasNext()) {
					String header = it.next();

					numcol = getColumnNumber(header);

					cell = row.createCell(numcol);
					cell.setCellValue(literals.get(header));
					cell.setCellStyle(cellStyle());
				}
			}
		}


		FileOutputStream fos = new FileOutputStream(file);
		wbook.write(fos);
		fos.flush();
		fos.close();

		return file;
	}


	public ThingDTO importLoad(ThingDTO data, UserDetailsDTO user) throws ObjectNotFoundException {
		//load and save only one and only under the root of the tree
		data.setUrl(AssemblyService.SYSTEM_IMPORT_DICTIONARY);
		Concept root = closureServ.loadRoot(data.getUrl());
		data.setParentId(root.getID());
		List<Concept> nodes = closureServ.loadLevel(root);
		if(nodes.size()>0) {
			data.setNodeId(nodes.get(0).getID());
		}
		if(data.getNodeId()==0) {
			data=thingServ.createThing(data, user);
		}else {
			data=thingServ.loadThing(data, user);
		}
		return data;
	}

	public DictionaryDTO importRun(DictionaryDTO data, byte[] fileBytes) throws ObjectNotFoundException, IOException{
		InputStream inputStream = new ByteArrayInputStream(fileBytes);
		wbook = new XSSFWorkbook(inputStream);

		if(wbook != null) {
			errors = null;

			XSSFSheet sheet = wbook.getSheetAt(0);
			headerRow = sheet.getRow(0);
			Map<Integer, String> literalNames = new HashMap<Integer, String>();
			for(int i = headerRow.getFirstCellNum(); i < headerRow.getLastCellNum(); i++) {
				String val = headerRow.getCell(i).getStringCellValue();
				String literalName = validNewLiteral(val);
				if(literalName != null) {
					literalNames.put(i, literalName);
				}
			}

			if(literalNames.containsValue(LiteralService.PREF_NAME) && literalNames.containsValue(LiteralService.DESCRIPTION) &&
					literalNames.containsValue(LiteralService.URL)) {
				Long currentId = 0L;
				if(data.getPath() != null && data.getPath().size() > 0) {
					currentId = data.getPath().get(data.getPath().size() - 1).getId();
				}
				Concept dictItemNode = loadDictSelectItemConcept(data.getUrl(), currentId);

				if(literalNames.keySet() != null && literalNames.keySet().size() > 0) {
					int numColPrefLabel = -1;
					int numColDescription = -1;
					Iterator<Integer> it = literalNames.keySet().iterator();
					while(it.hasNext()) {
						Integer num = it.next();
						if(literalNames.get(num).equals(LiteralService.PREF_NAME)) {
							numColPrefLabel = num;
						}else if(literalNames.get(num).equals(LiteralService.DESCRIPTION)) {
							numColDescription = num;
						}
					}
					
					int startRow = 1;
					int endRow = sheet.getLastRowNum();
					for(int i = startRow; i <= endRow; i++) {
						XSSFRow row = sheet.getRow(i);
						Boolean v = validRow(row, numColPrefLabel, numColDescription);
						if(v != null) {
							if(v) {
								Concept item=itemDict(data, dictItemNode, numColPrefLabel, row);

								for(Integer num:literalNames.keySet()) {
									XSSFCell cell = row.getCell(num);
									String val = "";
									if(cell != null) {
										//val = cell.getStringCellValue().trim();
										val = boilerServ.getStringCellValue(row,num).trim();
										boilerServ.replaceNonPrintCh(val);
									}
									item = literalServ.createUpdateLiteral(literalNames.get(num), val, item);
								}
							}else {// ошибка
								if(errors == null) {
									errors = new LegacyDataErrorsDTO(wbook);
									errors.copyHeaderRow(headerRow);
								}
								errors.add(row, headerRow.getLastCellNum(), "");
							}
						}
					}
				}
				if(errors != null) {
					data.setValid(false);
					data.setIdentifier(messages.get("dataHasErrors"));
					logger.info(messages.get("dataHasErrors"));
				}
			}else {
				logger.error(messages.get("notReqCols"));
				data.setValid(false);
				data.setIdentifier(messages.get("notReqCols"));
			}
		}else {
			logger.error(messages.get("errorWb"));
			data.setValid(false);
			data.setIdentifier(messages.get("errorWb"));
		}
		return data;
	}

	private Concept itemDict(DictionaryDTO data, Concept dictItemNode, int numColPrefLabel, XSSFRow row)
			throws ObjectNotFoundException {
		String val="";
		XSSFCell cell=row.getCell(numColPrefLabel);
		if(cell != null) {
			val = cell.getStringCellValue().trim();
			val=boilerServ.replaceNonPrintCh(val);
		List<TableRow> dictPref=data.getTable().getRows();
		for(TableRow rowD:dictPref) {
			TableCell cellD=rowD.getCellByKey("pref");
			if(cellD!=null) {
				String prefL=cellD.getValue();
				if(prefL.equalsIgnoreCase(val)){
					Concept itemD = closureServ.loadConceptById(rowD.getDbID());
					return itemD;
				}
			}
		}
	}
		Concept item = new Concept();
		item.setIdentifier(item.getID() + "");
		item = closureServ.saveToTree(dictItemNode, item);
		item.setIdentifier(item.getID() + "");
		return item;
	}

	/**
	 * если пустая PrefLbl и description то просто пропускаем строку
	 * если пустая PrefLbl 
	 * @param row
	 * @param numColPrefLbl
	 * @return
	 */
	
	private Boolean validRow(XSSFRow row, int numColPrefLbl, int numColDescription) {
		if(row == null)
			return null;
		XSSFCell cell = null, cellDescr = null;
		if(numColPrefLbl >= 0 && numColDescription >= 0) {
			cell = row.getCell(numColPrefLbl);
			cellDescr = row.getCell(numColDescription);
		}
		String v ="";
		String d="";
		if(cell != null) {
			//String v = cell.getStringCellValue().trim();
			v = boilerServ.getStringCellValue(row,numColPrefLbl).trim();
			//String d = (cellDescr != null)?cellDescr.getStringCellValue().trim():"";
			if(cellDescr!=null) {
				d = boilerServ.getStringCellValue(row,numColDescription).trim();
			}
			if(v != null && v.length() > 0) {// не пустая PrefLbl
				return true;
			}else if(d != null && d.length() > 0) {// пустая PrefLbl и не пустая description
				return false;
			}else {// пустая PrefLbl и пустая description - не ошибка, просто берем следующую строку
				return null;
			}
		}else if(cellDescr != null) {
			//String d = (cellDescr != null)?cellDescr.getStringCellValue().trim():"";
			d = boilerServ.getStringCellValue(row,numColDescription).trim();
			if(d != null && d.length() > 0) {// пустая PrefLbl и не пустая description
				return false;
			}
		}
		
		return null;
	}

	public ResponseEntity<Resource> getFileErrors() throws IOException{
		if(errors != null){
			File fError = new File(errorFileName);
			FileOutputStream fos = new FileOutputStream(fError);
			wbook.write(fos);
			fos.flush();
			fos.close();

			Resource res = new ByteArrayResource(Files.readAllBytes(fError.toPath()));

			ResponseEntity<Resource> resp = ResponseEntity.ok()
					.contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
					.contentLength(fError.length())
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment" + "; filename=\"" + errorFileName +"\"")
					.header("filename", errorFileName)
					.body(res);

			return resp;
		}
		return null;
	}

	/**
	 * validate column name 
	 * create literalName
	 * letters, numbers and "_"
	 * @param value
	 * @return
	 */
	private String validNewLiteral(String value) {
		String literal = null;
		if(value != null && value.length() > 0) {
			value = value.replaceAll(" ", "");
			if(value.matches("\\w+")) {
				literal = value;
			}
		}
		return literal;
	}
	/**
	 * build file name
	 * allPath or Dictionary name
	 * @param curId
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	private String buildFileName(String dictUrl, Long currentId) throws ObjectNotFoundException {
		String fileName = "Dictionary.xlsx";

		Concept dictRoot = loadDictSelectItemConcept(dictUrl, currentId);
		if(dictRoot != null) {
			fileName = "";
			List<String> list = literalServ.loadAllParentPrefLabels(dictRoot);
			if(list != null && list.size() > 0) {
				for(int i = (list.size() - 1); i >= 0; i--) {
					fileName += list.get(i).replaceAll(" ", "") + ".";
				}
				fileName += "xlsx";
			}
		}

		/*Concept dictRoot = closureServ.loadRoot(dictUrl);
		if(dictRoot != null) {
			if(dictRoot.getID() == currentId) {
				fileName += literalServ.readPrefLabel(dictRoot).replaceAll(" ", "") + ".xlsx";
			}else {
				fileName = "";
				Concept c = closureServ.loadConceptById(currentId);
				List<String> list = literalServ.loadAllParentPrefLabels(c);
				if(list != null && list.size() > 0) {
					for(int i = (list.size() - 1); i >= 0; i--) {
						fileName += list.get(i).replaceAll(" ", "") + ".";
					}
					fileName += "xlsx";
				}
			}
		}*/
		return fileName;
	}

	private Concept loadDictSelectItemConcept(String dictUrl, Long currentId) throws ObjectNotFoundException {
		Concept dictItem = closureServ.loadRoot(dictUrl);
		if(dictItem != null) {
			if(currentId > 0 && dictItem.getID() != currentId) {
				dictItem = closureServ.loadConceptById(currentId);
			}
		}
		return dictItem;
	}

	/**
	 * get Column number or create new Column
	 * @param literal
	 * @return
	 */
	private int getColumnNumber(String literal) {
		int col = -1;
		int start = headerRow.getFirstCellNum();
		int end = headerRow.getLastCellNum();
		for(int i = start; i < end; i++) {
			XSSFCell cell = headerRow.getCell(i);
			String h = cell.getStringCellValue();
			if(literal.equals(h)) {
				col = i;
				break;
			}
		}

		if(col == -1) {
			XSSFCell cell = headerRow.createCell(end);
			cell.setCellValue(literal);
			cell.getSheet().setColumnWidth(end, widthCol);
			cell.setCellStyle(headerStyle());

			return end;
		}else {
			return col;
		}
	}

	/**
	 * create style by header cells
	 * @return
	 */
	private XSSFCellStyle headerStyle() {
		XSSFCellStyle style = wbook.createCellStyle();
		style.setWrapText(true);
		XSSFFont font = wbook.createFont();
		font.setBold(true);
		style.setFont(font);

		return style;
	}

	/**
	 * create style by data cells
	 * @return
	 */
	private XSSFCellStyle cellStyle() {
		XSSFCellStyle style = wbook.createCellStyle();
		style.setWrapText(true);

		return style;
	}
}
