package org.msh.pharmadex2.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pdex2.services.r2.ClosureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes=Pharmadex2Application.class)
public class ExportFromXLSX {
	@Autowired
	ClosureService closureServ;
	
	
	public XSSFWorkbook getXSSFWorkbook(byte[] bytes) {
		XSSFWorkbook w = null;
		if(bytes.length > 0){			
			try {				
				InputStream inputStream = new ByteArrayInputStream(bytes);
				w = new XSSFWorkbook(inputStream);
			} catch (IOException e) {				
				e.printStackTrace();
			}			
		}
		
		return w;
	}
	//@Test
	public void exportATC() throws IOException, ObjectNotFoundException {
		Path pathFile = Paths.get("src","test","resources", "atc.xlsx");
		byte[] bytes = Files.readAllBytes(pathFile);
		if(bytes.length > 0) {
			XSSFWorkbook workbook = getXSSFWorkbook(bytes);
			if(workbook != null){
				XSSFSheet sheet = workbook.getSheetAt(0);
				Concept root = closureServ.loadRoot("medicinalproductdefinition.productclassification.atc.human");
				for (int i = 1; i <= sheet.getLastRowNum(); i++) {
					XSSFRow row = sheet.getRow(i);
					String identifier=row.getCell(0).getStringCellValue();
					String label=row.getCell(1).getStringCellValue();
					Concept conc = new Concept();
					conc.setActive(true);
					conc.setIdentifier(identifier);
					conc.setLabel(label);
					closureServ.saveToTree(root, conc);
					System.out.println(i);
				}
			}
		}
	}

}
